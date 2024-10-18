package bee.creative.kb;

import java.util.Arrays;
import bee.creative.fem.FEMString;
import bee.creative.util.HashMapOI;

/** Diese Klasse implementiert einen Wissenspuffer als veränderlichen {@link KBState Wissensstand}. Der Wissenspuffer ist nicht <em>thread-safe</em> und macht
 * vor der ersten Änderung grundsätzlich eine Sicherungskopie des aktuellen Wissensstands. Durch den Aufruf von {@link #commit()} bzw. {@link #rollback()}
 * können dann alle bis dahin gemachten Änderungen angenommen bzw. verworfen werden. In beiden Fällen wird ein {@link KBUpdate Änderungsprotokoll}
 * bereitgestellt.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBBuffer extends KBState {

	public boolean putEdge(KBEdge edge) {
		return (edge != null) && this.putEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public boolean putEdge(int sourceRef, int relationRef, int targetRef) {
		this.backupEdges();
		return this.insertEdge(sourceRef, targetRef, relationRef);
	}

	public int putValue(FEMString valueStr) {
		this.backupValues();
		return this.insertValueStr(valueStr);
	}

	public HashMapOI<FEMString> putAllValue(FEMString... values) {
		return this.putAllValue(Arrays.asList(values));
	}

	public HashMapOI<FEMString> putAllValue(Iterable<FEMString> values) {
		this.backupValues();
		var result = new HashMapOI<FEMString>();
		values.forEach(valueStr -> result.put(valueStr, this.insertValueStr(valueStr)));
		return result;
	}

	public boolean putAllValue(KBValues values) {
		this.backupValues();
		var result = this.getValueCount();
		values.forEach(this::insertValue);
		return result != this.getValueCount();
	}

	public boolean putAllEdges(KBEdge... edges) {
		return this.putAllEdges(Arrays.asList(edges));
	}

	public boolean putAllEdges(Iterable<KBEdge> edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach(edge -> result[0] = this.insertEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | result[0]);
		return result[0];
	}

	public boolean putAllEdges(KBEdges edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach((sourceRef, targetRef, relationRef) -> result[0] = this.insertEdge(sourceRef, targetRef, relationRef) | result[0]);
		return result[0];
	}

	public boolean popEdge(KBEdge edge) {
		return (edge != null) && this.popEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public boolean popEdge(int sourceRef, int relationRef, int targetRef) {
		this.backupEdges();
		return this.deleteEdge(sourceRef, targetRef, relationRef);
	}

	public boolean popAllEdges(KBEdge... edges) {
		return this.popAllEdges(Arrays.asList(edges));
	}

	public boolean popAllEdges(Iterable<KBEdge> edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach(edge -> result[0] = this.deleteEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | result[0]);
		return result[0];
	}

	public boolean popAllEdges(KBEdges edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach((sourceRef, targetRef, relationRef) -> result[0] = this.deleteEdge(sourceRef, targetRef, relationRef) | result[0]);
		return result[0];
	}

	public boolean popValue(FEMString valueStr) {
		this.backupValues();
		var result = this.deleteValueStr(valueStr);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return result;
	}

	public boolean popValueRef(int valueRef) {
		this.backupValues();
		var result = this.deleteValueRef(valueRef);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return result;
	}

	public boolean popAllValues(FEMString... valueStrs) {
		return this.popAllValues(Arrays.asList(valueStrs));
	}

	public boolean popAllValues(Iterable<FEMString> valueStrs) {
		this.backupValues();
		var result = new boolean[1];
		valueStrs.forEach((valueStr) -> result[0] = this.deleteValueStr(valueStr) | result[0]);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return result[0];
	}

	public boolean popAllValues(KBValues values) {
		this.backupValues();
		var result = this.getValueCount();
		values.forEach(this::deleteValue);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return result != this.getValueCount();
	}

	public void setIndexRef(int indexRef) {
		this.backup();
		this.indexRef = indexRef;
	}

	public int getNextInternalRef() {
		this.backup();
		return this.nextInternalRef();
	}

	public int getNextExternalRef() {
		this.backup();
		return this.nextExternalRef();
	}

	public void setCurrentInternalRef(int currentInternalRef) {
		this.backup();
		this.currentInternalRef = currentInternalRef;
	}

	public void setCurrentExternalRef(int currentExternalRef) {
		this.backup();
		this.currentExternalRef = currentExternalRef;
	}

	/** Diese Methode entfernt alle {@link #edges() Hyperkanten} und {@link #values() Textwerte}. */
	public void clear() {
		this.backupEdges();
		this.reset();
	}

	/** Diese Methode ergänzt alle {@link KBEdge Hyperkanten} der gegebenen {@link KBState Hyperkantenmenge} {@code putState} und erhöht die Referenzen
	 * {@link #getCurrentInternalRef()} und {@link #getIndexRef()} um die jeweiligen Zählerstände des {@code putState}. */
	public void insertAll(KBState inserts) {
		this.backupEdges();
		this.backupValues();
		this.indexRef += inserts.indexRef;
		this.currentInternalRef += inserts.currentInternalRef;
		this.currentExternalRef += inserts.currentExternalRef;
		inserts.forEachEdge(this::insertEdge);
		inserts.forEachValue(this::insertValue);
	}

	/** Diese Methode entfernt alle {@link KBEdge Hyperkanten} der gegebenen {@link KBState Hyperkantenmenge} {@code popState} und verringert die Referenzen
	 * {@link #getCurrentInternalRef()} und {@link #getIndexRef()} um die jeweiligen Zählerstände des {@code popState}. */
	public void deleteAll(KBState deletes) {
		this.backupEdges();
		this.backupValues();
		this.indexRef -= deletes.indexRef;
		this.currentExternalRef -= deletes.currentExternalRef;
		this.currentInternalRef -= deletes.currentInternalRef;
		deletes.forEachEdge(this::deleteEdge);
		deletes.forEachValue(this::deleteValue);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
	}

	/** Diese Methode ersetzt alle {@link KBEdge Hyperkanten} soeie die Referenzen {@link #getCurrentInternalRef()} und {@link #getIndexRef()} mit denen der
	 * gegebenen {@link KBState Hyperkantenmenge}. */
	public void replaceAll(KBState state) {
		this.backup();
		this.reset(KBState.from(state));
	}

	/** Diese Methode übernimmt alle Anderungen seit dem letzten {@link #commit()}, {@link #rollback()} bzw. der erzeugung dieses Hyperkantenpuffers und liefert
	 * den zugehörigen {@link KBUpdate Änderungsbericht}. */
	public KBUpdate commit() {
		return new KBUpdate(this, true);
	}

	/** Diese Methode verwirft alle Anderungen seit dem letzten {@link #commit()}, {@link #rollback()} bzw. der erzeugung dieses Hyperkantenpuffers und liefert
	 * den zugehörigen {@link KBUpdate Änderungsbericht}. */
	public KBUpdate rollback() {
		return new KBUpdate(this, false);
	}

	KBState backup;

	boolean backupEdges;

	boolean backupValues;

	private void backup() {
		var backup = this.backup;
		if (backup != null) return;
		this.backup = new KBState(this);
		this.backupEdges = false;
		this.backupValues = false;
	}

	private void backupEdges() {
		if (this.backupEdges) return;
		this.backup();
		this.sourceMap = REFMAP.copy(this.sourceMap);
		this.targetMap = REFMAP.copy(this.targetMap);
		this.backupEdges = true;
	}

	private void backupValues() {
		if (this.backupValues) return;
		this.backup();
		this.valueStrMap = (ValueStrMap)this.valueStrMap.clone();
		this.valueRefMap = (ValueRefMap)this.valueRefMap.clone();
		this.backupValues = true;
	}

	private int nextExternalRef() {
		var result = this.nextExternalRef(Math.min(-1, this.currentExternalRef));
		return this.currentExternalRef = result < 0 ? this.nextExternalRef(-1) : result;
	}

	private int nextExternalRef(int result) {
		while (this.valueStrMap.containsKey(result)) {
			result--;
		}
		return result;
	}

	private int nextInternalRef() {
		var result = this.nextInternalRef(Math.max(1, this.currentInternalRef));
		return this.currentInternalRef = result < 0 ? this.nextInternalRef(1) : result;
	}

	private int nextInternalRef(int result) {
		while ((REFMAP.getIdx(this.sourceMap, result) != 0) || (REFMAP.getIdx(this.targetMap, result) != 0)) {
			result++;
		}
		return result;
	}

	private boolean insertEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		var sourceMap = this.sourceMap;
		var targetMap = this.targetMap;

		sourceMap = REFMAP.grow(sourceMap);
		targetMap = REFMAP.grow(targetMap);

		this.sourceMap = sourceMap;
		this.targetMap = targetMap;

		// PUT sourceMap
		var nextSourceIdx = REFMAP.putRef(sourceMap, sourceRef);

		// MAX sourceMap
		if (nextSourceIdx == 0) throw new IllegalStateException();

		// GET sourceRelationMap
		var sourceRelationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, nextSourceIdx));
		Object[] backupSourceRelationMap = null;

		if (sourceRelationMap == null) {
			// NEW sourceRelationMap
			REFMAP.setVal(sourceMap, nextSourceIdx, REFMAP.EMPTY);
			sourceRelationMap = REFMAP.create();
		} else {
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			backupSourceRelationMap = backupSourceIdx != 0 ? KBState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx)) : null;
			if (sourceRelationMap == backupSourceRelationMap) {
				// COW sourceRelationMap
				sourceRelationMap = REFMAP.grow(backupSourceRelationMap);
				if (sourceRelationMap == backupSourceRelationMap) {
					sourceRelationMap = REFMAP.copy(backupSourceRelationMap);
				}
			} else {
				// OWN sourceRelationMap
				sourceRelationMap = REFMAP.grow(sourceRelationMap);
			}
		}
		REFMAP.setVal(sourceMap, nextSourceIdx, sourceRelationMap);

		// PUT targetMap
		var targetIdx = REFMAP.putRef(targetMap, targetRef);

		// MAX targetMap
		if (targetIdx == 0) throw new IllegalStateException();

		// GET targetRelationMap
		var targetRelationMap = KBState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		Object[] backupTargetRelationMap = null;

		if (targetRelationMap == null) {
			// NEW targetRelationMap
			REFMAP.setVal(targetMap, targetIdx, REFMAP.EMPTY);
			targetRelationMap = REFMAP.create();
		} else {
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			backupTargetRelationMap = backupTargetIdx != 0 ? KBState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx)) : null;
			if (targetRelationMap == backupTargetRelationMap) {
				// COW targetRelationMap
				targetRelationMap = REFMAP.grow(backupTargetRelationMap);
				if (targetRelationMap == backupTargetRelationMap) {
					targetRelationMap = REFMAP.copy(backupTargetRelationMap);
				}
			} else {
				// OWN targetRelationMap
				targetRelationMap = REFMAP.grow(targetRelationMap);
			}
		}
		REFMAP.setVal(targetMap, targetIdx, targetRelationMap);

		// PUT sourceRelationMap
		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);

		// MAX sourceRelationMap
		if (sourceRelationIdx == 0) throw new IllegalStateException();

		// GET sourceRelationTargetVal
		var sourceRelationTargetSet = KBState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		var sourceRelationTargetSet2 = sourceRelationTargetSet;

		if (sourceRelationTargetSet == null) {
			// NEW sourceRelationTargetVal
			// REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.EMPTY);
			sourceRelationTargetSet = KBState.toRef(targetRef);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else if (KBState.isRef(sourceRelationTargetSet)) {
			// ADD sourceRelationTargetVal
			var targetRef2 = KBState.asRef(sourceRelationTargetSet);
			if (targetRef == targetRef2) return false; // OLD
			sourceRelationTargetSet = REFSET.from(targetRef, targetRef2);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else {
			var backupSourceRelationIdx = backupSourceRelationMap != null ? REFMAP.getIdx(backupSourceRelationMap, relationRef) : 0;
			var backupSourceRelationTargetVal =
				backupSourceRelationIdx != 0 ? KBState.asRefVal(REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx)) : null;
			if (sourceRelationTargetSet == backupSourceRelationTargetVal) {
				// COW sourceRelationTargetVal
				sourceRelationTargetSet = REFSET.grow(backupSourceRelationTargetVal);
				if (sourceRelationTargetSet == backupSourceRelationTargetVal) {
					sourceRelationTargetSet = REFSET.copy(backupSourceRelationTargetVal);
				}
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
				REFSET.putRef(sourceRelationTargetSet, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false; // OLD
			} else {
				// OWN sourceRelationTargetVal
				sourceRelationTargetSet = REFSET.grow(sourceRelationTargetSet);
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
				REFSET.putRef(sourceRelationTargetSet, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false; // OLD
			}
		}

		// PUT targetRelationMap
		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);

		// MAX targetRelationMap
		if (targetRelationIdx == 0) {
			if (KBState.isRef(sourceRelationTargetSet)) {
				REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			} else if (KBState.isRef(sourceRelationTargetSet2)) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
			} else {
				REFSET.popRef(sourceRelationTargetSet, targetRef);
			}
			throw new IllegalStateException();
		}

		var targetRelationSourceVal = KBState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		var targetRelationSourceSet2 = targetRelationSourceVal;

		try {
			if (targetRelationSourceVal == null) {
				// REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.EMPTY);
				targetRelationSourceVal = KBState.toRef(sourceRef);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else if (KBState.isRef(targetRelationSourceVal)) {
				// ADD targetRelationSourceVal
				var sourceRef2 = KBState.asRef(targetRelationSourceVal);
				targetRelationSourceVal = REFSET.from(sourceRef, sourceRef2);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else {
				var backupTargetRelationIdx = backupTargetRelationMap != null ? REFMAP.getIdx(backupTargetRelationMap, relationRef) : 0;
				var backupTargetRelationSourceVal =
					backupTargetRelationIdx != 0 ? KBState.asRefVal(REFMAP.getVal(backupTargetRelationMap, backupTargetRelationIdx)) : null;
				if (targetRelationSourceVal == backupTargetRelationSourceVal) {
					// COW targetRelationSourceVal
					targetRelationSourceVal = REFSET.grow(backupTargetRelationSourceVal);
					if (targetRelationSourceVal == backupTargetRelationSourceVal) {
						targetRelationSourceVal = REFSET.copy(backupTargetRelationSourceVal);
					}
					REFSET.putRef(targetRelationSourceVal, sourceRef);
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				} else {
					// OWN targetRelationSourceVal
					targetRelationSourceVal = REFSET.grow(targetRelationSourceVal);
					REFSET.putRef(targetRelationSourceVal, sourceRef);
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				}
			}
			targetRelationIdx = 0;
		} finally {
			if (targetRelationIdx != 0) {
				if (KBState.isRef(sourceRelationTargetSet)) {
					REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
				} else if (KBState.isRef(sourceRelationTargetSet2)) {
					REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
				} else {
					REFSET.popRef(sourceRelationTargetSet, targetRef);
				}
				if (KBState.isRef(targetRelationSourceVal)) {
					REFMAP.setVal(targetRelationMap, REFMAP.popRef(targetRelationMap, relationRef), null);
				} else if (KBState.isRef(targetRelationSourceSet2)) {
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet2);
				}
				throw new IllegalStateException();
			}
		}

		return true;
	}

	private void insertValue(int valueRef, FEMString valueStr) {

		var newValueRef = valueRef;
		var newValueStr = valueStr.data();

		var oldValueRef = this.valueRefMap.put(newValueStr, newValueRef);
		if (oldValueRef != null) {
			if (oldValueRef.intValue() == valueRef) return;

			this.valueRefMap.put(valueStr, oldValueRef);
			throw new IllegalArgumentException();
		}

		var oldValueStr = this.valueStrMap.put(newValueRef, newValueStr);
		if (oldValueStr != null) {
			this.valueStrMap.put(newValueRef, oldValueStr);
			this.valueRefMap.remove(newValueStr);
			throw new IllegalArgumentException();
		}
	}

	private int insertValueStr(FEMString valueStr) {
		var valueRef = this.valueRefMap.get(valueStr.data());
		if (valueRef != null) return valueRef;
		valueRef = this.nextExternalRef();
		this.valueRefMap.put(valueStr, valueRef);
		this.valueStrMap.put(valueRef, valueStr);
		return valueRef;
	}

	private boolean deleteEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		// TODO prüfen
		var sourceMap = this.sourceMap;
		var sourceIdx = REFMAP.getIdx(sourceMap, sourceRef);
		if (sourceIdx == 0) return false;

		var targetMap = this.targetMap;
		var targetIdx = REFMAP.getIdx(targetMap, targetRef);
		if (targetIdx == 0) return false;

		var sourceRelationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		var backupSourceRelationMap = (Object[])null;
		{
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			if (backupSourceIdx != 0) {
				backupSourceRelationMap = KBState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx));
				if (sourceRelationMap == backupSourceRelationMap) {
					sourceRelationMap = REFMAP.copy(sourceRelationMap);
					REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);
				}
			}
		}

		var targetRelationMap = KBState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		var backupTargetRelationMap = (Object[])null;
		{
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			if (backupTargetIdx != 0) {
				backupTargetRelationMap = KBState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx));
				if (targetRelationMap == backupTargetRelationMap) {
					targetRelationMap = REFMAP.copy(targetRelationMap);
					REFMAP.setVal(targetMap, targetIdx, targetRelationMap);
				}
			}
		}

		var sourceRelationIdx = REFMAP.getIdx(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) return false;
		var sourceRelationTargetVal = KBState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));

		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);
		if (targetRelationIdx == 0) return false; // IllegalState
		var targetRelationSourceVal = KBState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));

		if (KBState.isRef(sourceRelationTargetVal)) {
			var targetRef2 = KBState.asRef(sourceRelationTargetVal);
			if (targetRef != targetRef2) return false;
			REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			if (REFMAP.size(sourceRelationMap) == 0) {
				REFMAP.setVal(sourceMap, REFMAP.popRef(sourceMap, sourceRef), null);
				this.sourceMap = REFMAP.pack(sourceMap);
			} else {
				REFMAP.setVal(sourceMap, sourceIdx, REFMAP.pack(sourceRelationMap));
			}
		} else {
			var sourceRelationTargetSet = sourceRelationTargetVal;
			if (backupSourceRelationMap != null) {
				var backupSourceRelationIdx = REFMAP.getIdx(backupSourceRelationMap, relationRef);
				if (backupSourceRelationIdx != 0) {
					var backupSourceRelationTargetVal = REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx);
					if (sourceRelationTargetSet == backupSourceRelationTargetVal) {
						sourceRelationTargetSet = REFSET.copy(sourceRelationTargetVal);
					}
				}
			}
			var nextSourceRelationTargetIdx = REFSET.popRef(sourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetIdx == 0) return false;
			if (REFSET.size(sourceRelationTargetSet) == 0) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, null);
				if (REFMAP.size(sourceRelationMap) == 0) {
					REFMAP.setVal(sourceMap, REFMAP.popRef(sourceMap, sourceRef), null);
					this.sourceMap = REFMAP.pack(sourceMap);
				} else {
					REFMAP.setVal(sourceMap, sourceIdx, REFMAP.pack(sourceRelationMap));
				}
			} else {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.pack(sourceRelationTargetSet));
			}
		}

		if (KBState.isRef(targetRelationSourceVal)) {
			// var sourceRef2 = BEREdges.asRef(nextTargetRelationSourceVal);
			// if (sourceRef != sourceRef2) return false;
			REFMAP.setVal(targetRelationMap, REFMAP.popRef(targetRelationMap, relationRef), null);
			if (REFMAP.size(targetRelationMap) == 0) {
				REFMAP.setVal(targetMap, REFMAP.popRef(targetMap, targetRef), null);
				this.targetMap = REFMAP.pack(targetMap);
			} else {
				REFMAP.setVal(targetMap, targetIdx, REFMAP.pack(targetRelationMap));
			}
		} else {
			var nextTargetRelationSourceSet = targetRelationSourceVal;
			var prevTargetRelationIdx = backupTargetRelationMap != null ? REFMAP.getIdx(backupTargetRelationMap, relationRef) : 0;
			var prevTargetRelationSourceVal = prevTargetRelationIdx != 0 ? REFMAP.getVal(backupTargetRelationMap, prevTargetRelationIdx) : null;
			if (nextTargetRelationSourceSet == prevTargetRelationSourceVal) {
				nextTargetRelationSourceSet = REFSET.copy(targetRelationSourceVal);
			}
			REFSET.popRef(nextTargetRelationSourceSet, sourceRef);
			if (REFSET.size(nextTargetRelationSourceSet) == 0) {
				REFMAP.setVal(targetRelationMap, targetRelationIdx, null);
				if (REFMAP.size(targetRelationMap) == 0) {
					REFMAP.setVal(targetMap, REFMAP.popRef(targetMap, targetRef), null);
					this.targetMap = REFMAP.pack(targetMap);
				} else {
					REFMAP.setVal(targetMap, targetIdx, REFMAP.pack(targetRelationMap));
				}
			} else {
				REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.pack(nextTargetRelationSourceSet));
			}
		}

		return true;
	}

	private void deleteValue(int valueRef, FEMString valueStr) {
		var valueStr2 = this.valueStrMap.remove(valueRef);
		var valueRef2 = this.valueRefMap.remove(valueStr.data());
		if (valueStr2 == null) {
			if (valueRef2 == null) return;
			this.valueRefMap.put(valueStr, valueRef2);
			throw new IllegalArgumentException();
		}
		if (valueRef2 == null) {
			this.valueStrMap.put(valueRef, valueStr2);
			throw new IllegalArgumentException();
		}
		if (valueRef == valueRef2.intValue()) return;
		this.valueRefMap.put(valueStr, valueRef2);
		this.valueStrMap.put(valueRef, valueStr2);
		throw new IllegalArgumentException();
	}

	private boolean deleteValueRef(int valueRef) {
		var valueStr = this.valueStrMap.remove(valueRef);
		if (valueStr == null) return false;
		this.valueRefMap.remove(valueStr);
		return true;
	}

	private boolean deleteValueStr(FEMString valueStr) {
		var valueRef = this.valueRefMap.remove(valueStr);
		if (valueRef == null) return false;
		this.valueStrMap.remove(valueRef);
		return true;
	}
}
