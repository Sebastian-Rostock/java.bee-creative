package bee.creative.kb;

import java.io.IOException;
import bee.creative.fem.FEMString;
import bee.creative.kb.KBBuffer.History;

class KBCodec {

	public static byte[] persistState(KBState source) throws IOException {
		return ZIPDOS.deflate(target -> KBCodec.persistState(target, source));
	}

	public static void persistState(ZIPDOS target, KBState source) throws IOException {
		synchronized (source) {
			target.writeInt(KBCodec.STATE_MAGIC);
			KBCodec.persistRefs(target, source);
			KBCodec.persistEdges(target, source);
			KBCodec.persistValues(target, source);
		}
	}

	public static void persistBuffer(ZIPDOS target, KBBuffer source) throws IOException {
		var that = new KBBuffer();
		synchronized (source) {
			that.reset(source.getBackup());
			that.undoHistory.reset(source.undoHistory, true);
			that.redoHistory.reset(source.redoHistory, true);
		}
		target.writeInt(KBCodec.BUFFER_MAGIC);
		KBCodec.persistRefs(target, that);
		KBCodec.persistEdges(target, that);
		KBCodec.persistValues(target, that);
		KBCodec.persistHistory(target, that.undoHistory);
		KBCodec.persistHistory(target, that.redoHistory);
	}

	public static KBBuffer restoreBuffer(ZIPDIS source) throws IOException {
		var header = source.readInt(1);
		if (header[0] != KBCodec.BUFFER_MAGIC) throw new IOException();
		var result = new KBBuffer();
		KBCodec.restoreRefs(source, result);
		KBCodec.restoreEdges(source, result);
		KBCodec.restoreValues(source, result);
		KBCodec.restoreHistory(source, result.undoHistory);
		KBCodec.restoreHistory(source, result.redoHistory);
		return result;
	}

	public static void restoreBuffer(ZIPDIS source, KBBuffer target) throws IOException {
		var result = restoreBuffer(source);
		synchronized (target) {
			if (target.hasBackup()) throw new IllegalStateException();
			target.reset(result);
			target.undoHistory.reset(result.undoHistory, false);
			target.redoHistory.reset(result.redoHistory, false);
		}
	}

	public static KBState restoreState(byte[] source) throws IOException {
		return ZIPDIS.inflate(source, KBCodec::restoreState);
	}

	/** Diese Methode liefert einen neuen {@link KBState Wissensstand} zur gegebenen {@link #persistState(ZIPDOS, KBState) Wissensabschrift}. */
	public static KBState restoreState(ZIPDIS source) throws IOException {
		var header = source.readInt(1);
		if (header[0] != KBCodec.STATE_MAGIC) throw new IOException();
		var result = new KBState();
		KBCodec.restoreRefs(source, result);
		KBCodec.restoreEdges(source, result);
		KBCodec.restoreValues(source, result);
		return result;
	}

	static void restoreState(ZIPDIS source, KBState target, KBEdgesTask edgeTask, KBValuesTask valueTask) throws IOException {
		KBCodec.restoreRefs(source, target);
		KBCodec.restoreEdges(source, edgeTask);
		KBCodec.restoreValues(source, valueTask);
	}

	private static final int STATE_MAGIC = 0xCBFF5001;

	private static final int BUFFER_MAGIC = 0xCBFFB001;

	private static void persistRefs(ZIPDOS target, KBState source) throws IOException {
		target.writeInt(source.indexRef, source.internalRef, source.externalRef);
	}

	private static void persistEdges(ZIPDOS target, KBState source) throws IOException {
		var LIMIT = 1024 * 1024;
		var cursor = new int[]{LIMIT};
		var sourceMap = new Object[][]{REFMAP.EMPTY};
		try {
			source.forEachEdge((sourceRef, targetRef, relationRef) -> {
				try {
					sourceMap[0] = KBState.insertEdgeIntoMapSRT(sourceMap[0], sourceRef, relationRef, targetRef);
					if (--cursor[0] != 0) return;
					KBCodec.persistEdgesBlock(target, sourceMap[0]);
					cursor[0] = LIMIT;
					sourceMap[0] = REFMAP.EMPTY;
				} catch (IOException cause) {
					throw new IllegalStateException(cause);
				}
			});
		} catch (IllegalStateException cause) {
			if (cause.getCause() instanceof IOException) throw (IOException)cause.getCause();
			throw cause;
		}
		if (KBCodec.persistEdgesBlock(target, sourceMap[0])) {
			KBCodec.persistEdgesBlock(target, REFMAP.EMPTY);
		}
	}

	/** Diese Methode persistiert die Kanen in folgender Struktur:
	 * {@code (count: int, sourceCount: int, (sourceRef: int, targetRefCount: int, targetSetCount: int, (targetRef: int, relationRef: int)[targetRefCount], (relationRef: int, targetCount: int, targetRef: int[targetCount])[targetSetCount])[sourceCount])}
	 *
	 * @return */
	private static boolean persistEdgesBlock(ZIPDOS target, Object[] sourceMap) throws IOException {
		var count = 1;
		var sourceCount = 0;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = KBState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var relationSize = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = KBState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (KBState.isRef(targetVal)) {
							relationSize += /*relationRef, targetRef*/ 2;
						} else {
							var targetCount = REFSET.size(targetVal);
							if (targetCount == 1) {
								relationSize += /*relationRef, targetRef*/ 2;
							} else if (targetCount > 0) {
								relationSize += /*relationRef, targetCount*/ 2 + /*targetRef*/ targetCount;
							}
						}
					}
				}
				if (relationSize != 0) {
					count += /*sourceRef, targetRefCount, targetRefSetCount*/ 3 + relationSize;
					sourceCount++;
				}
			}
		}
		var index = 0;
		var array = new int[count + 1];
		array[index++] = count;
		array[index++] = sourceCount;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = KBState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var targetRefCount = 0;
				var targetSetCount = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = KBState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (KBState.isRef(targetVal)) {
							targetRefCount++;
						} else {
							var targetCount = REFSET.size(targetVal);
							if (targetCount == 1) {
								targetRefCount++;
							} else if (targetCount > 0) {
								targetSetCount++;
							}
						}
					}
				}
				if ((targetRefCount != 0) || (targetSetCount != 0)) {
					array[index++] = REFSET.getRef(REFMAP.getKeys(sourceMap), sourceIdx);
					array[index++] = targetRefCount;
					array[index++] = targetSetCount;
					if (targetRefCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = KBState.asRefVal(relationMap[relationIdx]);
							if ((targetVal != null) && (KBState.isRef(targetVal) || (REFSET.size(targetVal) == 1))) {
								array[index++] = KBState.asRef(targetVal);
								array[index++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
							}
						}
					}
					if (targetSetCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = KBState.asRefVal(relationMap[relationIdx]);
							if (targetVal != null) {
								if (!KBState.isRef(targetVal)) {
									var targetCount = REFSET.size(targetVal);
									if (targetCount > 1) {
										array[index++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
										array[index++] = targetCount;
										for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
											var targetRef = targetVal[targetIdx];
											if (targetRef != 0) {
												array[index++] = targetRef;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		target.writeInt(array);
		return sourceCount != 0;
	}

	private static void persistValues(ZIPDOS target, KBState source) throws IOException {
		var LIMIT = 256;
		var cursor = new int[]{LIMIT};
		var buffer = new KBValue[LIMIT];
		try {
			source.forEachValue((valueRef, valueStr) -> {
				try {
					buffer[--cursor[0]] = new KBValue(valueRef, valueStr);
					if (cursor[0] != 0) return;
					KBCodec.persistValuesBlock(target, buffer, cursor[0]);
					cursor[0] = LIMIT;
				} catch (IOException cause) {
					throw new IllegalStateException(cause);
				}
			});
		} catch (IllegalStateException cause) {
			if (cause.getCause() instanceof IOException) throw (IOException)cause.getCause();
			throw cause;
		}
		if (KBCodec.persistValuesBlock(target, buffer, cursor[0])) {
			KBCodec.persistValuesBlock(target, buffer, LIMIT);
		}
	}

	/** Diese Methode persistiert die Textwete ab der gegebenen Position bis zum Listenende und liefert nur dann {@code true}, wenn dies nicht {@code 0} sind. */
	private static boolean persistValuesBlock(ZIPDOS result, KBValue[] source, int offset) throws IOException {
		var count = source.length - offset;
		result.writeInt(count);
		if (count == 0) return false;
		var refArray = new int[count];
		var strArray = new FEMString[count];
		for (var i = 0; i < count; i++) {
			var entry = source[i + offset];
			refArray[i] = entry.getKey();
			strArray[i] = entry.getValue();
		}
		result.writeInt(refArray);
		result.writeStrings(strArray);
		return true;
	}

	static void persistHistory(ZIPDOS target, History source) throws IOException {
		var limit = source.getLimit();
		target.writeInt(limit);
		if (limit == 0) return;
		var size = source.size();
		target.writeInt(size);
		for (var i = 0; i < size; i++) {
			var item = source.getItem(i);
			target.writeStrings(item.info);
			target.writeBinaries(item.insertData, item.deleteData);
		}
	}

	private static void restoreRefs(ZIPDIS source, KBState target) throws IOException {
		var refs = source.readInt(3);
		target.indexRef = refs[0];
		target.internalRef = refs[1];
		target.externalRef = refs[2];
	}

	private static void restoreEdges(ZIPDIS source, KBState result) throws IOException {
		KBCodec.restoreEdges(source, result::insertEdgeNow);
	}

	private static void restoreEdges(ZIPDIS source, KBEdgesTask task) throws IOException {
		while (KBCodec.restoreEdgesBlock(source, task)) {}
	}

	private static boolean restoreEdgesBlock(ZIPDIS source, KBEdgesTask task) throws IOException {
		var count = source.readInt(1)[0];
		var index = 0;
		var array = source.readInt(count);
		var sourceRefCount = array[index++];
		if (sourceRefCount == 0) return false;
		while (0 < sourceRefCount--) {
			var sourceRef = array[index++];
			var targetRefCount = array[index++];
			var targetSetCount = array[index++];
			while (0 < targetRefCount--) {
				var targetRef = array[index++];
				var relationRef = array[index++];
				task.run(sourceRef, targetRef, relationRef);
			}
			while (0 < targetSetCount--) {
				var relationRef = array[index++];
				var targetCount = array[index++];
				while (0 < targetCount--) {
					var targetRef = array[index++];
					task.run(sourceRef, targetRef, relationRef);
				}
			}
		}
		return true;
	}

	private static void restoreValues(ZIPDIS source, KBState target) throws IOException {
		target.valueRefMap.allocate(65536);
		target.valueStrMap.allocate(65536);
		KBCodec.restoreValues(source, (valueRef, valueStr) -> {
			target.valueRefMap.put(valueStr, valueRef);
			target.valueStrMap.put(valueRef, valueStr);
		});
		target.valueRefMap.pack();
		target.valueStrMap.pack();
	}

	private static void restoreValues(ZIPDIS source, KBValuesTask task) throws IOException {
		while (KBCodec.restoreValuesBlock(source, task)) {}
	}

	private static boolean restoreValuesBlock(ZIPDIS source, KBValuesTask task) throws IOException {
		var count = source.readInt(1)[0];
		if (count == 0) return false;
		var refArray = source.readInt(count);
		var strArray = source.readStrings(count);
		for (var i = 0; i < count; i++) {
			task.run(refArray[i], strArray[i]);
		}
		return true;
	}

	static void restoreHistory(ZIPDIS source, History target) throws IOException {
		// TODO
	}

}
