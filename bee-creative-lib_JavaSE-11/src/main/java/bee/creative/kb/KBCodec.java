package bee.creative.kb;

import java.io.IOException;
import bee.creative.fem.FEMString;
import bee.creative.kb.KBState.ValueStrMap;

class KBCodec {

	public static byte[] persistState(KBState source) throws IOException {
		return ZIPDOS.deflate(target -> KBCodec.persistState(target, source));
	}

	public static void persistState(ZIPDOS target, KBState source) throws IOException {
		synchronized (source) {
			target.writeInt(KBCodec.STATE_MAGIC);
			KBCodec.___persistStateRefs(target, source);
			KBCodec.persistStateEdges(target, source);
			KBCodec.persistStateValues(target, source);
		}
	}

	public void persistBuffer(ZIPDOS target, KBBuffer source) throws IOException {
		var that = new KBBuffer();
		synchronized (this) {
			that.reset(source.getBackup());
			that.undoHistory.setAll(source.undoHistory);
			that.redoHistory.setAll(source.redoHistory);
		}
		target.writeInt(KBCodec.BUFFER_MAGIC);
		KBCodec.___persistStateRefs(target, that);
		KBCodec.persistStateEdges(target, that);
		KBCodec.persistStateValues(target, that);
		KBCodec.persistHistoryItems(target, that.undoHistory);
		KBCodec.persistHistoryItems(target, that.redoHistory);
	}

	static void persistHistoryItems(ZIPDOS target, KBBuffer.History history) throws IOException {
		var limit = history.getLimit();
		target.writeInt(limit);
		if (limit == 0) return;
		var size = history.size;
		target.writeInt(size);
		for (var i = 0; i < size; i++) {
			var item = history.getItem(i);
			target.writeStrings(item.info);
			target.writeBinaries(item.insertData, item.deleteData);
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
		KBCodec.___restoreStateRefs(source, result);
		KBCodec.restoreStateEdges(source, result);
		KBCodec.restoreStateValues(source, result);
		return result;
	}

	static final int STATE_MAGIC = 0xCBFF0501;

	static final int BUFFER_MAGIC = 0xCBFF0B01;

	static final int MAGIC_EDGE_PAGE_SRT = 0xCBEF00E1;

	static final int MAGIC_EDGE_PAGE_STR = 0xCBEF00E2;

	static final int MAGIC_EDGE_PAGE_RST = 0xCBEF00E3;

	static final int MAGIC_EDGE_PAGE_RTS = 0xCBEF00E4;

	static final int MAGIC_EDGE_PAGE_TRS = 0xCBEF00E5;

	static final int MAGIC_EDGE_PAGE_TSR = 0xCBEF00E6;

	/** Diese Methode persistiert die Referenzen des {@link KBState} in folgender Struktur: {@code (indexRef: int, internalRef: int, externalRef: int)} */
	static void ___persistStateRefs(ZIPDOS target, KBState source) throws IOException {
		target.writeInt(source.indexRef, source.internalRef, source.externalRef);
	}

	static void persistStateEdges(ZIPDOS target, KBState source) throws IOException {
		var EDGE_LIMIT = 128 * 1024;
		var cursor = new int[]{EDGE_LIMIT};
		var buffer = new KBEdge[EDGE_LIMIT];
		try {
			source.forEachEdge((sourceRef, targetRef, relationRef) -> {
				try {
					var edge = new KBEdge(sourceRef, targetRef, relationRef);
					buffer[--cursor[0]] = edge;
					if (cursor[0] != 0) return;
					KBCodec.persistEdgesPage(target, buffer, cursor[0]);
					cursor[0] = EDGE_LIMIT;
				} catch (IOException cause) {
					throw new IllegalStateException(cause);
				}
			});
		} catch (IllegalStateException cause) {
			if (cause.getCause() instanceof IOException) throw (IOException)cause.getCause();
			throw cause;
		}
		if (cursor[0] != EDGE_LIMIT) KBCodec.persistEdgesPage(target, buffer, cursor[0]);
		KBCodec.persistEdgesPage(target, buffer, EDGE_LIMIT);
	}

	static void persistStateValues(ZIPDOS target, KBState source) throws IOException {
		source.forEachValue((valueRef, valueStr) -> {

		});

		KBCodec.persistValueMap(target, source.valueStrMap);
	}

	static void restoreState(ZIPDIS source, KBState target, KBEdgesTask edgeTask, KBValuesTask valueTask) throws IOException {
		KBCodec.___restoreStateRefs(source, target);
		KBCodec.restoreEdges(source, edgeTask);
		KBCodec.___restoreValues(source, valueTask);
	}

	static void ___restoreStateRefs(ZIPDIS source, KBState target) throws IOException {
		var refs = source.readInt(3);
		target.indexRef = refs[0];
		target.internalRef = refs[1];
		target.externalRef = refs[2];
	}

	static void restoreStateEdges(ZIPDIS source, KBState result) throws IOException {
		KBCodec.restoreEdges(source, result::insertEdgeNow);
	}

	static void restoreStateValues(ZIPDIS source, KBState target) throws IOException {
		target.valueRefMap.allocate(65536);
		target.valueStrMap.allocate(65536);
		KBCodec.___restoreValues(source, (valueRef, valueStr) -> {
			target.valueRefMap.put(valueStr, valueRef);
			target.valueStrMap.put(valueRef, valueStr);
		});
		target.valueRefMap.pack();
	}

	static void restoreEdges(ZIPDIS source, KBEdgesTask task) throws IOException {
		while (true) {
			var i = source.readInt(1)[0];
			switch (i) {
				case MAGIC_EDGE_PAGE_SRT:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(sourceRef, targetRef, relationRef))) return;
				break;
				case MAGIC_EDGE_PAGE_STR:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(sourceRef, relationRef, targetRef))) return;
				break;
				case MAGIC_EDGE_PAGE_RST:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(relationRef, targetRef, sourceRef))) return;
				break;
				case MAGIC_EDGE_PAGE_RTS:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(relationRef, sourceRef, targetRef))) return;
				break;
				case MAGIC_EDGE_PAGE_TRS:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(targetRef, sourceRef, relationRef))) return;
				break;
				case MAGIC_EDGE_PAGE_TSR:
					if (!KBCodec.restoreEdgesMap(source, (sourceRef, targetRef, relationRef) -> task.run(targetRef, relationRef, sourceRef))) return;
				break;
				default:
					System.out.println(i);
					throw new IOException();
			}
		}
	}

	static boolean restoreEdgesMap(ZIPDIS source, KBEdgesTask task) throws IOException {
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

	static void ___restoreValues(ZIPDIS source, KBValuesTask task) throws IOException {
		while (___restoreValuesPage(source, task)) {}
	}

	static boolean ___restoreValuesPage(ZIPDIS source, KBValuesTask task) throws IOException {
		var count = source.readInt(1)[0];
		if (count == 0) return false;
		var refArray = source.readInt(count);
		var strArray = source.readStrings(count);
		for (var i = 0; i < count; i++) {
			task.run(refArray[i], strArray[i]);
		}
		return true;
	}

	/** Diese Methode persistiert die Kanen in folgender Struktur:
	 * {@code (count: int, sourceCount: int, (sourceRef: int, targetRefCount: int, targetSetCount: int, (targetRef: int, relationRef: int)[targetRefCount], (relationRef: int, targetCount: int, targetRef: int[targetCount])[targetSetCount])[sourceCount])} */
	static void persistEdgesMap(ZIPDOS target, Object[] sourceMap) throws IOException {
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
	}

	static void persistEdgesPage(ZIPDOS target, KBEdge[] source, int offset) throws IOException {
		var length = source.length;
		// Referenzen Zählen
		var sourceSet = REFSET.EMPTY;
		var targetSet = REFSET.EMPTY;
		var relationSet = REFSET.EMPTY;
		for (var index = offset; index < length; index++) {
			var edge = source[index];
			sourceSet = REFSET.growAndPutRef(sourceSet, edge.sourceRef);
			targetSet = REFSET.growAndPutRef(targetSet, edge.targetRef);
			relationSet = REFSET.growAndPutRef(relationSet, edge.relationRef);
		}
		var sourceCount = REFSET.size(sourceSet);
		var targetCount = REFSET.size(targetSet);
		var relationCount = REFSET.size(relationSet);
		// Rollen der Referenzen tauschen
		var resultMap = REFMAP.EMPTY;
		if (relationCount <= sourceCount) { // * R * S *
			if (relationCount <= targetCount) { // R * S *
				if (sourceCount <= targetCount) { // RST
					target.writeInt(KBCodec.MAGIC_EDGE_PAGE_RST);
					for (var index = offset; index < length; index++) {
						var edge = source[index];
						resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.relationRef, edge.sourceRef, edge.targetRef);
					}
				} else { // RTS
					target.writeInt(KBCodec.MAGIC_EDGE_PAGE_RTS);
					for (var index = offset; index < length; index++) {
						var edge = source[index];
						resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.relationRef, edge.targetRef, edge.sourceRef);
					}
				}
			} else { // TRS
				target.writeInt(KBCodec.MAGIC_EDGE_PAGE_TRS);
				for (var index = offset; index < length; index++) {
					var edge = source[index];
					resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.targetRef, edge.relationRef, edge.sourceRef);
				}
			}
		} else { // * S * R *
			if (sourceCount <= targetCount) { // S * R *
				if (relationCount <= targetCount) { // SRT
					target.writeInt(KBCodec.MAGIC_EDGE_PAGE_SRT);
					for (var index = offset; index < length; index++) {
						var edge = source[index];
						resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.sourceRef, edge.relationRef, edge.targetRef);
					}
				} else { // STR
					target.writeInt(KBCodec.MAGIC_EDGE_PAGE_STR);
					for (var index = offset; index < length; index++) {
						var edge = source[index];
						resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.sourceRef, edge.targetRef, edge.relationRef);
					}
				}
			} else { // TSR
				target.writeInt(KBCodec.MAGIC_EDGE_PAGE_TSR);
				for (var index = offset; index < length; index++) {
					var edge = source[index];
					resultMap = KBState.insertEdgeIntoMapSRT(resultMap, edge.targetRef, edge.sourceRef, edge.relationRef);
				}
			}
		}
		KBCodec.persistEdgesMap(target, resultMap);
	}

	/** Diese Methode persistiert die Textwete in folgender Struktur: (valueCount: int, valueRef: int[valueCount], valueHash: int[valueCount], valueSize:
	 * int[valueCount], valueLength: int[valueCount], valueString: byte[valueSize][valueCount])</pre> */
	static void persistValueMap(ZIPDOS result, ValueStrMap valueMap) throws IOException {

		var valueCount = valueMap.size();
		result.writeInt(valueCount);

		// TODO je 1024 werte blockweise

		result.writeInt(valueCount);

		var refArray = new int[valueCount];
		var strArray = new FEMString[valueCount];
		var iter = valueMap.fastIterator();
		for (var i = 0; iter.hasNext(); i++) {
			var entry = iter.next();
			refArray[i] = entry.getKey();
			strArray[i] = entry.getValue();
		}
		result.writeInt(refArray);
		result.writeStrings(strArray);
	}

	
	/** Diese Methode persistiert die Textwete in folgender Struktur: (valueCount: int, valueRef: int[valueCount], valueHash: int[valueCount], valueSize:
	 * int[valueCount], valueLength: int[valueCount], valueString: byte[valueSize][valueCount])</pre> */
	static void persistValuesPage(ZIPDOS result, ValueStrMap valueMap) throws IOException {

		var valueCount = valueMap.size();
		result.writeInt(valueCount);

		// TODO je 1024 werte blockweise

		result.writeInt(valueCount);

		var refArray = new int[valueCount];
		var strArray = new FEMString[valueCount];
		var iter = valueMap.fastIterator();
		for (var i = 0; iter.hasNext(); i++) {
			var entry = iter.next();
			refArray[i] = entry.getKey();
			strArray[i] = entry.getValue();
		}
		result.writeInt(refArray);
		result.writeStrings(strArray);
	}
	
}
