package bee.creative.str;

import java.util.Arrays;

/** Diese Klasse implementiert einen bidirectional-entity-relation Speicher.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class STRStore extends STRState {

	public void setRootRef(int rootRef) {
		this.backup();
		this.rootRef = rootRef;
	}

	public void setNextRef(int nextRef) {
		this.backup();
		this.nextRef = nextRef;
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

	/** Diese Methode gibt das zurück. ersetzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der
	 * einzigartigen referenzen kopiert diese an den beginn von sourceRefs
	 *
	 * @param targetRef
	 * @param relationRef
	 * @param sourceRefs
	 * @return */
	int setSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		// TODO
		return 0;
	}

	/** ergänzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der ergänzten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int putSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		// TODO
		return 0;
	}

	/** entfernt von den als source von target und rel vorkommenden referenzen die > 0 gegebenen liefert die anzahl der entfernten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int popSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		// TODO
		return 0;
	}

	public boolean put(STREdge edge) {
		this.backup();
		return this.insert(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean put(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.backup();
		return this.insert(sourceRef, relationRef, targetRef);
	}

	public boolean putAll(STREdge... edges) {
		return this.putAll(Arrays.asList(edges));
	}

	public boolean putAll(Iterable<STREdge> edges) {
		this.backup();
		var res = false;
		for (var edge: edges) {
			res = this.insert(edge.sourceRef, edge.relationRef, edge.targetRef) | res;
		}
		return res;
	}

	public boolean pop(STREdge edge) {
		this.backup();
		return this.delete(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.backup();
		return this.delete(sourceRef, relationRef, targetRef);
	}

	public boolean popAll(STREdge... edges) {
		return this.popAll(Arrays.asList(edges));
	}

	public boolean popAll(Iterable<STREdge> edges) {
		this.backup();
		var res = false;
		for (var edge: edges) {
			res = this.pop(edge) | res;
		}
		return res;
	}

	public void clear() {
		this.backup();
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
	}

	public void replace(STRState state) {
		if (state instanceof STRStore) {
			state = new STRState(state.toInts()); // TODO bessere deep copy
		} else {
			state.restore();
		}
		this.clear();
		this.nextRef = state.nextRef;
		this.rootRef = state.rootRef;
		this.sourceMap = state.sourceMap; // hier deep copy einsetzen
		this.targetMap = state.targetMap; // hier deep copy einsetzen
	}

	public void insertAll(STRState state) {
		this.backup();
		state.forEach(this::insert);
		this.nextRef += state.nextRef;
		this.rootRef += state.rootRef;
	}

	public void deleteAll(STRState state) {
		this.backup();
		state.forEach(this::delete);
		this.nextRef -= state.nextRef;
		this.rootRef -= state.rootRef;
	}

	public STRUpdate commit() {
		return new STRUpdate(this, true);
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	public STRUpdate rollback() {
		return new STRUpdate(this, false);
	}

	STRState backup;

	private boolean insert(int sourceRef, int relationRef, int targetRef) {

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
		var sourceRelationMap = STRState.asRefMap(REFMAP.getVal(sourceMap, nextSourceIdx));
		Object[] backupSourceRelationMap = null;

		if (sourceRelationMap == null) {
			// NEW sourceRelationMap
			REFMAP.setVal(sourceMap, nextSourceIdx, REFMAP.EMPTY);
			sourceRelationMap = REFMAP.make();
		} else {
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			backupSourceRelationMap = backupSourceIdx != 0 ? STRState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx)) : null;
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
		var targetRelationMap = STRState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		Object[] backupTargetRelationMap = null;

		if (targetRelationMap == null) {
			// NEW targetRelationMap
			REFMAP.setVal(targetMap, targetIdx, REFMAP.EMPTY);
			targetRelationMap = REFMAP.make();
		} else {
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			backupTargetRelationMap = backupTargetIdx != 0 ? STRState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx)) : null;
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
		var sourceRelationTargetSet = STRState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		var sourceRelationTargetSet2 = sourceRelationTargetSet;

		if (sourceRelationTargetSet == null) {
			// NEW sourceRelationTargetVal
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.EMPTY);
			sourceRelationTargetSet = STRState.toRef(targetRef);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else if (STRState.isRef(sourceRelationTargetSet)) {
			// ADD sourceRelationTargetVal
			var targetRef2 = STRState.asRef(sourceRelationTargetSet);
			if (targetRef == targetRef2) return false; // OLD
			sourceRelationTargetSet = REFSET.from(targetRef, targetRef2);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else {
			var backupSourceRelationIdx = backupSourceRelationMap != null ? REFMAP.getIdx(backupSourceRelationMap, relationRef) : 0;
			var backupSourceRelationTargetVal =
				backupSourceRelationIdx != 0 ? STRState.asRefVal(REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx)) : null;
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
			if (STRState.isRef(sourceRelationTargetSet)) {
				REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			} else if (STRState.isRef(sourceRelationTargetSet2)) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
			} else {
				REFSET.popRef(sourceRelationTargetSet, targetRef);
			}
			throw new IllegalStateException();
		}

		var targetRelationSourceVal = STRState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		var targetRelationSourceSet2 = targetRelationSourceVal;

		try {
			if (targetRelationSourceVal == null) {
				REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.EMPTY);
				targetRelationSourceVal = STRState.toRef(sourceRef);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else if (STRState.isRef(targetRelationSourceVal)) {
				// ADD targetRelationSourceVal
				var sourceRef2 = STRState.asRef(targetRelationSourceVal);
				targetRelationSourceVal = REFSET.from(sourceRef, sourceRef2);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else {
				var backupTargetRelationIdx = backupTargetRelationMap != null ? REFMAP.getIdx(backupTargetRelationMap, relationRef) : 0;
				var backupTargetRelationSourceVal =
					backupTargetRelationIdx != 0 ? STRState.asRefVal(REFMAP.getVal(backupTargetRelationMap, backupTargetRelationIdx)) : null;
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
				if (STRState.isRef(sourceRelationTargetSet)) {
					REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
				} else if (STRState.isRef(sourceRelationTargetSet2)) {
					REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
				} else {
					REFSET.popRef(sourceRelationTargetSet, targetRef);
				}
				if (STRState.isRef(targetRelationSourceVal)) {
					REFMAP.setVal(targetRelationMap, REFMAP.popRef(targetRelationMap, relationRef), null);
				} else if (STRState.isRef(targetRelationSourceSet2)) {
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet2);
				}
				throw new IllegalStateException();
			}
		}

		return true;
	}

	private boolean delete(int sourceRef, int relationRef, int targetRef) {
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
		var prevSourceRelationMap = prevSourceIdx != 0 ? STRState.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceIdx)) : null;
		var nextSourceRelationMap = STRState.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));
		if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.copy(nextSourceRelationMap);
			REFMAP.setVal(nextSourceMap, nextSourceIdx, nextSourceRelationMap);
		}

		var prevTargetIdx = REFMAP.getIdx(prevTargetMap, targetRef);
		var prevTargetRelationMap = prevTargetIdx != 0 ? STRState.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetIdx)) : null;
		var nextTargetRelationMap = STRState.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));
		if (nextTargetRelationMap == prevTargetRelationMap) {
			nextTargetRelationMap = REFMAP.copy(nextTargetRelationMap);
			REFMAP.setVal(nextTargetMap, nextTargetIdx, nextTargetRelationMap);
		}

		var nextSourceRelationIdx = REFMAP.getIdx(nextSourceRelationMap, relationRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationTargetVal = STRState.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationIdx));

		var nextTargetRelationIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationIdx == 0) return false; // IllegalState
		var nextTargetRelationSourceVal = STRState.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationIdx));

		if (STRState.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = STRState.asRef(nextSourceRelationTargetVal);
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

		if (STRState.isRef(nextTargetRelationSourceVal)) {
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

	private void backup() {
		if (this.backup != null) return;
		var backup = new STRState(this);
		this.sourceMap = REFMAP.copy(this.sourceMap);
		this.targetMap = REFMAP.copy(this.targetMap);
		this.backup = backup;
	}

}
