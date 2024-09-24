package bee.creative.ber;

import java.util.Arrays;

/** Diese Klasse implementiert einen bidirectional-entity-relation Speicher.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERStore extends BERState {

	public void clear() {
		this.backup();
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
	}

	public void setRootRef(int rootRef) {
		this.backup();
		this.rootRef = rootRef;
	}

	public void setNextRef(int nextRef) {
		this.backup();
		this.nextRef = nextRef;
	}

	/** Diese Methode gibt das zurück. übernimmt die gegebenen refs zur wiederverwendung. weitere werden nach dem größten automatisch ergänzt. duplikate und refs
	 * <=0 nicht zulässig. leere liste nicht zulässig. */
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

		return 0;
	}

	/** ergänzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der ergänzten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int putSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	/** entfernt von den als source von target und rel vorkommenden referenzen die > 0 gegebenen liefert die anzahl der entfernten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int popSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	public BERUpdate setState(BERState state) {
		this.clear();
		this.setNextRef(state.nextRef);
		this.setRootRef(state.rootRef);
		this.sourceMap = state.sourceMap;
		this.targetMap = state.targetMap;
		return null;
	}

	public boolean put(BEREdge edge) {
		this.backup();
		return this.insert(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean put(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.backup();
		return this.insert(sourceRef, relationRef, targetRef);
	}

	public boolean putAll(BEREdge... edges) {
		return this.putAll(Arrays.asList(edges));
	}

	public boolean putAll(Iterable<BEREdge> edges) {
		this.backup();
		var res = false;
		for (var edge: edges) {
			res = this.insert(edge.sourceRef, edge.relationRef, edge.targetRef) | res;
		}
		return res;
	}

	public boolean pop(BEREdge edge) {
		this.backup();
		return this.delete(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.backup();
		return this.delete(sourceRef, relationRef, targetRef);
	}

	public boolean popAll(BEREdge... edges) {
		return this.popAll(Arrays.asList(edges));
	}

	public boolean popAll(Iterable<BEREdge> edges) {
		this.backup();
		var res = false;
		for (var edge: edges) {
			res = this.pop(edge) | res;
		}
		return res;
	}

	public boolean popAll(BERState edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEach((sourceRef, relationRef, targetRef) -> res[0] = this.delete(sourceRef, relationRef, targetRef) | res[0]);
		return res[0];
	}

	public BERUpdate commit() {
		return new BERUpdate(this, true);
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	public BERUpdate rollback() {
		return new BERUpdate(this, false);
	}

	BERState backup;

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
		var sourceRelationMap = BERState.asRefMap(REFMAP.getVal(sourceMap, nextSourceIdx));
		Object[] backupSourceRelationMap = null;

		if (sourceRelationMap == null) {
			// NEW sourceRelationMap
			REFMAP.setVal(sourceMap, nextSourceIdx, REFMAP.EMPTY);
			sourceRelationMap = REFMAP.make();
		} else {
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			backupSourceRelationMap = backupSourceIdx != 0 ? BERState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx)) : null;
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
		var targetRelationMap = BERState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		Object[] backupTargetRelationMap = null;

		if (targetRelationMap == null) {
			// NEW targetRelationMap
			REFMAP.setVal(targetMap, targetIdx, REFMAP.EMPTY);
			targetRelationMap = REFMAP.make();
		} else {
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			backupTargetRelationMap = backupTargetIdx != 0 ? BERState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx)) : null;
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
		var sourceRelationTargetVal = BERState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		var backupRelationTargetVal = sourceRelationTargetVal;

		if (sourceRelationTargetVal == null) {
			// NEW sourceRelationTargetVal
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.EMPTY);
			sourceRelationTargetVal = BERState.toRef(targetRef);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetVal);
		} else if (BERState.isRef(sourceRelationTargetVal)) {
			// ADD sourceRelationTargetVal
			var targetRef2 = BERState.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return false; // OLD
			sourceRelationTargetVal = REFSET.from(targetRef, targetRef2);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetVal);
		} else {
			var backupSourceRelationIdx = backupSourceRelationMap != null ? REFMAP.getIdx(backupSourceRelationMap, relationRef) : 0;
			var backupSourceRelationTargetVal =
				backupSourceRelationIdx != 0 ? BERState.asRefVal(REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx)) : null;
			if (sourceRelationTargetVal == backupSourceRelationTargetVal) {
				// COW sourceRelationTargetVal
				sourceRelationTargetVal = REFSET.grow(backupSourceRelationTargetVal);
				if (sourceRelationTargetVal == backupSourceRelationTargetVal) {
					sourceRelationTargetVal = REFSET.copy(backupSourceRelationTargetVal);
				}
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetVal);
				REFSET.putRef(sourceRelationTargetVal, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetVal);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetVal)) return false; // OLD
			} else {
				// OWN sourceRelationTargetVal
				sourceRelationTargetVal = REFSET.grow(sourceRelationTargetVal);
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetVal);
				REFSET.putRef(sourceRelationTargetVal, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetVal);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetVal)) return false; // OLD
			}
		}

		// PUT targetRelationMap
		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);

		// MAX targetRelationMap
		if (targetRelationIdx == 0) {
			if (BERState.isRef(sourceRelationTargetVal)) {
				REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			} else if (BERState.isRef(backupRelationTargetVal)) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, backupRelationTargetVal);
			} else {
				REFSET.popRef(sourceRelationTargetVal, targetRef);
			}
			throw new IllegalStateException();
		}

		var targetRelationSourceVal = BERState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		
		if (targetRelationSourceVal == null) {
			REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.EMPTY);
			targetRelationSourceVal = BERState.toRef(sourceRef);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
		} else if (BERState.isRef(targetRelationSourceVal)) {
			// ADD targetRelationSourceVal
			var sourceRef2 = BERState.asRef(targetRelationSourceVal);
			targetRelationSourceVal = REFSET.from(sourceRef, sourceRef2);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
		} else {
			var backupTargetRelationIdx = backupTargetRelationMap != null ? REFMAP.getIdx(backupTargetRelationMap, relationRef) : 0;
			var backupTargetRelationSourceVal =
				backupTargetRelationIdx != 0 ? BERState.asRefVal(REFMAP.getVal(backupTargetRelationMap, backupTargetRelationIdx)) : null;
			if (targetRelationSourceVal == backupTargetRelationSourceVal) {
				// COW targetRelationSourceVal
				targetRelationSourceVal = REFSET.grow(backupTargetRelationSourceVal);
				if (targetRelationSourceVal == backupTargetRelationSourceVal) {
					targetRelationSourceVal = REFSET.copy(backupTargetRelationSourceVal);
				}
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				REFSET.putRef(targetRelationSourceVal, sourceRef);
			} else {
				// OWN targetRelationSourceVal
				targetRelationSourceVal = REFSET.grow(targetRelationSourceVal);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				REFSET.putRef(targetRelationSourceVal, sourceRef);
			}
		}

		return true;
	}

	private boolean delete(int sourceRef, int relationRef, int targetRef) {

		var nextSourceMap = this.sourceMap;
		var nextSourceIdx = REFMAP.getIdx(nextSourceMap, sourceRef);
		if (nextSourceIdx == 0) return false;

		var nextTargetMap = this.targetMap;
		var nextTargetIdx = REFMAP.getIdx(nextTargetMap, targetRef);
		if (nextTargetIdx == 0) return false;

		var prevSourceMap = this.backup.sourceMap;
		var prevTargetMap = this.backup.targetMap;

		var prevSourceIdx = REFMAP.getIdx(prevSourceMap, sourceRef);
		var prevSourceRelationMap = prevSourceIdx != 0 ? BERState.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceIdx)) : null;
		var nextSourceRelationMap = BERState.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));
		if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.copy(nextSourceRelationMap);
			REFMAP.setVal(nextSourceMap, nextSourceIdx, nextSourceRelationMap);
		}

		var prevTargetIdx = REFMAP.getIdx(prevTargetMap, targetRef);
		var prevTargetRelationMap = prevTargetIdx != 0 ? BERState.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetIdx)) : null;
		var nextTargetRelationMap = BERState.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));
		if (nextTargetRelationMap == prevTargetRelationMap) {
			nextTargetRelationMap = REFMAP.copy(nextTargetRelationMap);
			REFMAP.setVal(nextTargetMap, nextTargetIdx, nextTargetRelationMap);
		}

		var nextSourceRelationIdx = REFMAP.getIdx(nextSourceRelationMap, relationRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationTargetVal = BERState.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationIdx));

		var nextTargetRelationIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationIdx == 0) return false; // IllegalState
		var nextTargetRelationSourceVal = BERState.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationIdx));

		if (BERState.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = BERState.asRef(nextSourceRelationTargetVal);
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

		if (BERState.isRef(nextTargetRelationSourceVal)) {
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
		this.backup = new BERState(this);
		this.sourceMap = REFMAP.copy(this.sourceMap);
		this.targetMap = REFMAP.copy(this.targetMap);
	}

}
