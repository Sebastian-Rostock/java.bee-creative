package bee.creative.ber;

/** Diese Klasse implementiert einen bidirectional-entity-relation Speicher.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERStore extends BERState {

	public BERStore(Object owner) {
		this.owner = owner;
	}

	public Object getOwner() {
		return this.owner;
	}

	@Override
	public void clear() {
		if (this.prevSourceMap == null) {
			this.prevSourceMap = this.sourceMap;
			this.prevTargetMap = this.targetMap;
		}
		super.clear();
	}

	@Override
	public void setRootRef(int rootRef) {
		if (this.prevRootRef == null) {
			this.prevRootRef = this.rootRef;
		}
		this.rootRef = rootRef;
	}

	@Override
	public void setNextRef(int nextRef) {
		if (this.prevNextRef == null) {
			this.prevNextRef = this.nextRef;
		}
		this.nextRef = nextRef;
	}

	int newEntityRef() {
		var nextRef = this.nextRef;
		while (this.isSourceRef(nextRef) || this.isTargetRef(nextRef)) {
			nextRef++;
		}
		this.setNewEntityRef(nextRef + 1);
		return nextRef;
	}

	/** Diese Methode gibt das zurück. wenn gegebener nodeRef als source oder target vorkommt, in cow-nodeRefGen eintragen und aus source/target maps entfernen
	 *
	 * @param entityRef */
	void popEntityRef(int entityRef) {

	}

	/** Diese Methode gibt das zurück. übernimmt die gegebenen refs zur wiederverwendung. weitere werden nach dem größten automatisch ergänzt. duplikate und refs
	 * <=0 nicht zulässig. leere liste nicht zulässig. */
	void setNewEntityRef(int entityRef) {

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

	public BERUpdate commit() {
		return new BERUpdate(this, true);
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	public BERUpdate rollback() {
		return new BERUpdate(this, false);
	}

	public BERUpdate setState(BERState2 state) {
		this.clear();
		this.setNextRef(state.nextRef);
		this.setRootRef(state.rootRef);
		this.sourceMap = state.sourceMap;
		this.targetMap = state.targetMap;
		return null;
	}

	@Override
	boolean customPut(int sourceRef, int relationRef, int targetRef) {

		var prevSourceMap = this.prevSourceMap;
		var prevTargetMap = this.prevTargetMap;
		var nextSourceMap = this.sourceMap;
		var nextTargetMap = this.targetMap;

		if (prevSourceMap == null) {
			nextSourceMap = REFMAP.grow(prevSourceMap = nextSourceMap);
			nextTargetMap = REFMAP.grow(prevTargetMap = nextTargetMap);
			if (prevSourceMap == nextSourceMap) {
				nextSourceMap = REFMAP.copy(nextSourceMap);
			}
			if (prevTargetMap == nextTargetMap) {
				nextTargetMap = REFMAP.copy(nextTargetMap);
			}
			this.prevSourceMap = prevSourceMap;
			this.prevTargetMap = prevTargetMap;
		} else {
			nextSourceMap = REFMAP.grow(nextSourceMap);
			nextTargetMap = REFMAP.grow(nextTargetMap);
		}
		this.sourceMap = nextSourceMap;
		this.targetMap = nextTargetMap;

		var prevSourceRelationIdx = REFMAP.getIdx(prevSourceMap, sourceRef);
		var prevSourceRelationMap = prevSourceRelationIdx != 0 ? BEREdges.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceRelationIdx)) : null;
		var nextSourceRelationIdx = REFMAP.putRef(nextSourceMap, sourceRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceRelationIdx));
		if (nextSourceRelationMap == null) {
			nextSourceRelationMap = REFMAP.make();
		} else if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.grow(prevSourceRelationMap);
			if (nextSourceRelationMap == prevSourceRelationMap) {
				nextSourceRelationMap = REFMAP.copy(prevSourceRelationMap);
			}
		} else {
			nextSourceRelationMap = REFMAP.grow(nextSourceRelationMap);
		}
		REFMAP.setVal(nextSourceMap, nextSourceRelationIdx, nextSourceRelationMap);

		var prevTargetRelationIdx = REFMAP.getIdx(prevTargetMap, targetRef);
		var prevTargetRelationMap = prevTargetRelationIdx != 0 ? BEREdges.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetRelationIdx)) : null;
		var nextTargetRelationIdx = REFMAP.putRef(nextTargetMap, targetRef);
		if (nextTargetRelationIdx == 0) return false;
		var nextTargetRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetRelationIdx));
		if (nextTargetRelationMap == null) {
			nextTargetRelationMap = REFMAP.make();
		} else if (nextTargetRelationMap == prevTargetRelationMap) {
			nextTargetRelationMap = REFMAP.grow(prevTargetRelationMap);
			if (nextTargetRelationMap == prevTargetRelationMap) {
				nextTargetRelationMap = REFMAP.copy(prevTargetRelationMap);
			}
		} else {
			nextTargetRelationMap = REFMAP.grow(nextTargetRelationMap);
		}
		REFMAP.setVal(nextTargetMap, nextTargetRelationIdx, nextTargetRelationMap);

		var prevSourceRelationTargetIdx = prevSourceRelationMap != null ? REFMAP.getIdx(prevSourceRelationMap, relationRef) : 0;
		var prevSourceRelationTargetVal =
			prevSourceRelationTargetIdx != 0 ? BEREdges.asRefVal(REFMAP.getVal(prevSourceRelationMap, prevSourceRelationTargetIdx)) : null;
		var nextSourceRelationTargetIdx = REFMAP.putRef(nextSourceRelationMap, relationRef);
		if (nextSourceRelationTargetIdx == 0) return false;
		var nextSourceRelationTargetVal = BEREdges.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationTargetIdx));
		if (nextSourceRelationTargetVal == null) {
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, BEREdges.toRef(targetRef));
		} else if (BEREdges.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = BEREdges.asRef(nextSourceRelationTargetVal);
			if (targetRef == targetRef2) return false;
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, REFSET.from(targetRef, targetRef2));
		} else if (nextSourceRelationTargetVal == prevSourceRelationTargetVal) {
			var prevSourceRelationTargetSet = prevSourceRelationTargetVal;
			var nextSourceRelationTargetSet = REFSET.grow(prevSourceRelationTargetSet);
			if (nextSourceRelationTargetSet == prevSourceRelationTargetSet) {
				nextSourceRelationTargetSet = REFSET.copy(prevSourceRelationTargetSet);
			}
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, nextSourceRelationTargetSet);
			var nextSourceRelationTargetCount = REFSET.size(nextSourceRelationTargetSet);
			REFSET.putRef(nextSourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetCount == REFSET.size(nextSourceRelationTargetSet)) return false;
		} else {
			var nextSourceRelationTargetSet = REFSET.grow(nextSourceRelationTargetVal);
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, nextSourceRelationTargetSet);
			var nextSourceRelationTargetCount = REFSET.size(nextSourceRelationTargetSet);
			REFSET.putRef(nextSourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetCount == REFSET.size(nextSourceRelationTargetSet)) return false;
		}

		var prevTargetRelationSourceIdx = prevTargetRelationMap != null ? REFMAP.getIdx(prevTargetRelationMap, relationRef) : 0;
		var prevTargetRelationSourceVal =
			prevTargetRelationSourceIdx != 0 ? BEREdges.asRefVal(REFMAP.getVal(prevTargetRelationMap, prevTargetRelationSourceIdx)) : null;
		var nextTargetRelationSourceIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationSourceIdx == 0) return false;
		var nextTargetRelationSourceVal = BEREdges.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationSourceIdx));
		if (nextTargetRelationSourceVal == null) {
			REFMAP.setVal(nextTargetRelationMap, nextTargetRelationSourceIdx, BEREdges.toRef(sourceRef));
		} else if (BEREdges.isRef(nextTargetRelationSourceVal)) {
			var sourceRef2 = BEREdges.asRef(nextTargetRelationSourceVal);
			// if (sourceRef == sourceRef2) {
			// return false;
			// }
			REFMAP.setVal(nextTargetRelationMap, nextTargetRelationSourceIdx, REFSET.from(sourceRef, sourceRef2));
		} else if (nextTargetRelationSourceVal == prevTargetRelationSourceVal) {
			var prevTargetRelationSourceSet = prevTargetRelationSourceVal;
			var nextTargetRelationSourceSet = REFSET.grow(prevTargetRelationSourceSet);
			if (nextTargetRelationSourceSet == prevTargetRelationSourceSet) {
				nextTargetRelationSourceSet = REFSET.copy(prevTargetRelationSourceSet);
			}
			REFMAP.setVal(nextTargetRelationMap, nextTargetRelationSourceIdx, nextTargetRelationSourceSet);
			// var nextTargetRelationSourceCount = REFSET.size(nextTargetRelationSourceSet);
			REFSET.putRef(nextTargetRelationSourceSet, sourceRef);
			// if (nextTargetRelationSourceCount == REFSET.size(nextTargetRelationSourceSet)) {
			// return false;
			// }
		} else {
			var nextTargetRelationSourceSet = REFSET.grow(nextTargetRelationSourceVal);
			REFMAP.setVal(nextTargetRelationMap, nextTargetRelationSourceIdx, nextTargetRelationSourceSet);
			// var nextTargetRelationSourceCount = REFSET.size(nextTargetRelationSourceSet);
			REFSET.putRef(nextTargetRelationSourceSet, sourceRef);
			// if (nextTargetRelationSourceCount == REFSET.size(nextTargetRelationSourceSet)) {
			// return false;
			// }
		}

		return true;
	}

	@Override
	boolean customPop(int sourceRef, int relationRef, int targetRef) {

		var nextSourceMap = this.sourceMap;
		var nextSourceIdx = REFMAP.getIdx(nextSourceMap, sourceRef);
		if (nextSourceIdx == 0) return false;

		var nextTargetMap = this.targetMap;
		var nextTargetIdx = REFMAP.getIdx(nextTargetMap, targetRef);
		if (nextTargetIdx == 0) return false;

		var prevSourceMap = this.prevSourceMap;
		var prevTargetMap = this.prevTargetMap;
		if (prevSourceMap == null) {
			nextSourceMap = REFMAP.copy(prevSourceMap = nextSourceMap);
			nextTargetMap = REFMAP.copy(prevTargetMap = nextTargetMap);
			this.prevSourceMap = prevSourceMap;
			this.prevTargetMap = prevTargetMap;
		}

		var prevSourceIdx = REFMAP.getIdx(prevSourceMap, sourceRef);
		var prevSourceRelationMap = prevSourceIdx != 0 ? BEREdges.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceIdx)) : null;
		var nextSourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));
		if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.copy(nextSourceRelationMap);
			REFMAP.setVal(nextSourceMap, nextSourceIdx, nextSourceRelationMap);
		}

		var prevTargetIdx = REFMAP.getIdx(prevTargetMap, targetRef);
		var prevTargetRelationMap = prevTargetIdx != 0 ? BEREdges.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetIdx)) : null;
		var nextTargetRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));
		if (nextTargetRelationMap == prevTargetRelationMap) {
			nextTargetRelationMap = REFMAP.copy(nextTargetRelationMap);
			REFMAP.setVal(nextTargetMap, nextTargetIdx, nextTargetRelationMap);
		}

		var nextSourceRelationIdx = REFMAP.getIdx(nextSourceRelationMap, relationRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationTargetVal = BEREdges.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationIdx));

		var nextTargetRelationIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationIdx == 0) return false; // IllegalState
		var nextTargetRelationSourceVal = BEREdges.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationIdx));

		if (BEREdges.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = BEREdges.asRef(nextSourceRelationTargetVal);
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

		if (BEREdges.isRef(nextTargetRelationSourceVal)) {
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

	Object owner;

	Integer prevRootRef;

	Integer prevNextRef;

	Object[] prevSourceMap;

	Object[] prevTargetMap;

}
