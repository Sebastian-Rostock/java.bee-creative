package bee.creative.kb;

import java.util.Arrays;
import bee.creative.fem.FEMString;
import bee.creative.util.HashMapIO;
import bee.creative.util.HashMapOI;

/** Diese Klasse implementiert einen Hyperkantenpuffer als veränderbare {@link KBState Hyperkantenmenge}.
 * <p>
 * Der Hyperkantenpuffer macht vor der ersten Änderung grundsätzlich eine Sicherungskopie der aktuellen Hyperkantenmenge und Referenzen. Durch den Aufruf von
 * {@link #commit()} bzw. {@link #rollback()} können dann alle bis dahin gemachten Änderungen angenommen bzw. verworfen werden. In beiden Fällen wird ein
 * {@link KBUpdate Änderungsprotokoll} bereitgestellt.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBBuffer extends KBState {

	public void setNextRef(int nextRef) {
		this.backup();
		this.nextRef = nextRef;
	}

	public void setRootRef(int rootRef) {
		this.backup();
		this.rootRef = rootRef;
	}

	public int newNextRef() {
		this.backup();
		var nextRef = this.getNextRef();
		while (this.containsSourceRef(nextRef) || this.containsTargetRef(nextRef)) {
			nextRef++;
		}
		this.nextRef = nextRef + 1;
		return nextRef;
	}

	public boolean putEdge(KBEdge edge) {
		return (edge != null) && this.putEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public boolean putEdge(int sourceRef, int relationRef, int targetRef) {
		this.backup();
		return this.insertEdge(sourceRef, targetRef, relationRef);
	}

	int TODO_putValue(FEMString value) {
		// TODO
		return 0;
	}

	public boolean putAllEdges(KBEdge... edges) {
		return this.putAllEdges(Arrays.asList(edges));
	}

	public boolean putAllEdges(Iterable<KBEdge> edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEach(edge -> res[0] = this.insertEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | res[0]);
		return res[0];
	}

	public boolean putAllEdges(KBState edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEachEdge((sourceRef, targetRef, relationRef) -> res[0] = this.insertEdge(sourceRef, targetRef, relationRef) | res[0]);
		return res[0];
	}

	public boolean popEdge(KBEdge edge) {
		return (edge != null) && this.popEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public boolean popEdge(int sourceRef, int relationRef, int targetRef) {
		this.backup();
		return this.deleteEdge(sourceRef, targetRef, relationRef);
	}

	public boolean popAllEdges(KBEdge... edges) {
		return this.popAllEdges(Arrays.asList(edges));
	}

	public boolean popAllEdges(Iterable<KBEdge> edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEach(edge -> res[0] = this.deleteEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | res[0]);
		return res[0];
	}

	public boolean popAllEdges(KBState edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEachEdge((sourceRef, targetRef, relationRef) -> res[0] = this.deleteEdge(sourceRef, targetRef, relationRef) | res[0]);
		return res[0];
	}

	boolean popValue(int ref) {
		this.backup();
		return this.deleteValue(ref);
	}

	boolean popValue(FEMString value) {
		this.backup();
		return this.deleteValue(value);
	}

	boolean popAllValues(FEMString... values) {
		return this.popAllValues(Arrays.asList(values));
	}

	boolean popAllValues(Iterable<FEMString> values) {
		this.backup();
		var res = new boolean[1];
		values.forEach((valueStr) -> res[0] = this.deleteValue(valueStr) | res[0]);
		return res[0];
	}

	boolean popAllValues(KBState values) {
		this.backup();
		var res = new boolean[1];
		values.forEachValue((valueRef, valueStr) -> res[0] = this.deleteValue(valueRef, valueStr) | res[0]);
		return res[0];
	}

	/** Diese Methode entfernt alle {@link #edges() Hyperkanten} und {@link #values() Textwerte}. */
	public void clear() {
		this.backup();
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
		this.valueRefMap = new HashMapOI<>();
		this.valueStrMap = new HashMapIO<>();
	}

	/** Diese Methode ergänzt alle {@link KBEdge Hyperkanten} der gegebenen {@link KBState Hyperkantenmenge} {@code putState} und erhöht die Referenzen
	 * {@link #getNextRef()} und {@link #getRootRef()} um die jeweiligen Zählerstände des {@code putState}. */
	public void insertAll(KBState inserts) {
		this.backup();
		inserts.forEachEdge(this::insertEdge);
		inserts.forEachValue(this::insertValue);
		this.nextRef += inserts.nextRef;
		this.rootRef += inserts.rootRef;
	}

	/** Diese Methode entfernt alle {@link KBEdge Hyperkanten} der gegebenen {@link KBState Hyperkantenmenge} {@code popState} und verringert die Referenzen
	 * {@link #getNextRef()} und {@link #getRootRef()} um die jeweiligen Zählerstände des {@code popState}. */
	public void deleteAll(KBState deletes) {
		this.backup();
		deletes.forEachEdge(this::deleteEdge);
		deletes.forEachValue(this::deleteValue);
		this.nextRef -= deletes.nextRef;
		this.rootRef -= deletes.rootRef;
	}

	/** Diese Methode ersetzt alle {@link KBEdge Hyperkanten} soeie die Referenzen {@link #getNextRef()} und {@link #getRootRef()} mit denen der gegebenen
	 * {@link KBState Hyperkantenmenge}. */
	public void replaceAll(KBState state) {
		state = KBState.from(state);
		this.backup();
		this.nextRef = state.nextRef;
		this.rootRef = state.rootRef;
		this.sourceMap = state.sourceMap;
		this.targetMap = state.targetMap;
		this.valueRefMap = state.valueRefMap;
		this.valueStrMap = state.valueStrMap;
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
//			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.EMPTY);
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
//				REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.EMPTY);
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

	private boolean deleteEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		// TODO prüfen
		var nextSourceMap = this.sourceMap;
		var nextSourceIdx = REFMAP.getIdx(nextSourceMap, sourceRef);
		if (nextSourceIdx == 0) return false;

		var nextTargetMap = this.targetMap;
		var nextTargetIdx = REFMAP.getIdx(nextTargetMap, targetRef);
		if (nextTargetIdx == 0) return false;

		var prevSourceMap = this.backup.sourceMap;
		var prevTargetMap = this.backup.targetMap;

		var prevSourceIdx = REFMAP.getIdx(prevSourceMap, sourceRef);
		var prevSourceRelationMap = prevSourceIdx != 0 ? KBState.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceIdx)) : null;
		var nextSourceRelationMap = KBState.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));
		if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.copy(nextSourceRelationMap);
			REFMAP.setVal(nextSourceMap, nextSourceIdx, nextSourceRelationMap);
		}

		var prevTargetIdx = REFMAP.getIdx(prevTargetMap, targetRef);
		var prevTargetRelationMap = prevTargetIdx != 0 ? KBState.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetIdx)) : null;
		var nextTargetRelationMap = KBState.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));
		if (nextTargetRelationMap == prevTargetRelationMap) {
			nextTargetRelationMap = REFMAP.copy(nextTargetRelationMap);
			REFMAP.setVal(nextTargetMap, nextTargetIdx, nextTargetRelationMap);
		}

		var nextSourceRelationIdx = REFMAP.getIdx(nextSourceRelationMap, relationRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationTargetVal = KBState.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationIdx));

		var nextTargetRelationIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationIdx == 0) return false; // IllegalState
		var nextTargetRelationSourceVal = KBState.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationIdx));

		if (KBState.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = KBState.asRef(nextSourceRelationTargetVal);
			if (targetRef != targetRef2) return false;
			REFMAP.setVal(nextSourceRelationMap, REFMAP.popRef(nextSourceRelationMap, relationRef), null);
			if (REFMAP.size(nextSourceRelationMap) == 0) {
				REFMAP.setVal(nextSourceMap, REFMAP.popRef(nextSourceMap, sourceRef), null);
				this.sourceMap = REFMAP.pack(nextSourceMap);
			} else {
				REFMAP.setVal(nextSourceMap, nextSourceIdx, REFMAP.pack(nextSourceRelationMap));
			}
		} else {
			var nextSourceRelationTargetSet = nextSourceRelationTargetVal;
			var prevSourceRelationIdx = prevSourceRelationMap != null ? REFMAP.getIdx(prevSourceRelationMap, relationRef) : 0;
			var prevSourceRelationTargetVal = prevSourceRelationIdx != 0 ? REFMAP.getVal(prevSourceRelationMap, prevSourceRelationIdx) : null;
			if (nextSourceRelationTargetSet == prevSourceRelationTargetVal) {
				nextSourceRelationTargetSet = REFSET.copy(nextSourceRelationTargetVal);
			}
			var nextSourceRelationTargetIdx = REFSET.popRef(nextSourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetIdx == 0) return false;
			if (REFSET.size(nextSourceRelationTargetSet) == 0) {
				REFMAP.setVal(nextSourceRelationMap, nextSourceRelationIdx, null);
				if (REFMAP.size(nextSourceRelationMap) == 0) {
					REFMAP.setVal(nextSourceMap, REFMAP.popRef(nextSourceMap, sourceRef), null);
					this.sourceMap = REFMAP.pack(nextSourceMap);
				} else {
					REFMAP.setVal(nextSourceMap, nextSourceIdx, REFMAP.pack(nextSourceRelationMap));
				}
			} else {
				REFMAP.setVal(nextSourceRelationMap, nextSourceRelationIdx, REFSET.pack(nextSourceRelationTargetSet));
			}
		}

		if (KBState.isRef(nextTargetRelationSourceVal)) {
			// var sourceRef2 = BEREdges.asRef(nextTargetRelationSourceVal);
			// if (sourceRef != sourceRef2) return false;
			REFMAP.setVal(nextTargetRelationMap, REFMAP.popRef(nextTargetRelationMap, relationRef), null);
			if (REFMAP.size(nextTargetRelationMap) == 0) {
				REFMAP.setVal(nextTargetMap, REFMAP.popRef(nextTargetMap, targetRef), null);
				this.targetMap = REFMAP.pack(nextTargetMap);
			} else {
				REFMAP.setVal(nextTargetMap, nextTargetIdx, REFMAP.pack(nextTargetRelationMap));
			}
		} else {
			var nextTargetRelationSourceSet = nextTargetRelationSourceVal;
			var prevTargetRelationIdx = prevTargetRelationMap != null ? REFMAP.getIdx(prevTargetRelationMap, relationRef) : 0;
			var prevTargetRelationSourceVal = prevTargetRelationIdx != 0 ? REFMAP.getVal(prevTargetRelationMap, prevTargetRelationIdx) : null;
			if (nextTargetRelationSourceSet == prevTargetRelationSourceVal) {
				nextTargetRelationSourceSet = REFSET.copy(nextTargetRelationSourceVal);
			}
			REFSET.popRef(nextTargetRelationSourceSet, sourceRef);
			if (REFSET.size(nextTargetRelationSourceSet) == 0) {
				REFMAP.setVal(nextTargetRelationMap, nextTargetRelationIdx, null);
				if (REFMAP.size(nextTargetRelationMap) == 0) {
					REFMAP.setVal(nextTargetMap, REFMAP.popRef(nextTargetMap, targetRef), null);
					this.targetMap = REFMAP.pack(nextTargetMap);
				} else {
					REFMAP.setVal(nextTargetMap, nextTargetIdx, REFMAP.pack(nextTargetRelationMap));
				}
			} else {
				REFMAP.setVal(nextTargetRelationMap, nextTargetRelationIdx, REFSET.pack(nextTargetRelationSourceSet));
			}
		}

		return true;
	}

	private boolean deleteValue(int valueRef) {
		var valueStr = this.valueStrMap.remove(valueRef);
		if (valueStr == null) return false;
		this.valueRefMap.remove(valueStr);
		return true;
	}

	private boolean deleteValue(FEMString valueStr) {
		var valueRef = this.valueRefMap.remove(valueStr);
		if (valueRef == null) return false;
		this.valueStrMap.remove(valueRef);
		return true;
	}

	private boolean deleteValue(int valueRef, FEMString valueStr) {
		// TODO
		return false;
	}

	private void backup() {
		if (this.backup != null) return;
		var backup = new KBState(this);
		this.sourceMap = REFMAP.copy(this.sourceMap);
		this.targetMap = REFMAP.copy(this.targetMap);
		this.valueStrMap = this.valueStrMap.clone();
		this.valueRefMap = this.valueRefMap.clone();
		this.backup = backup;
	}

	private void insertValue(int valueRef, FEMString valueStr) {
		// TODO
	}

}
