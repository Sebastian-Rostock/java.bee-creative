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
		while (this.isSourceRef(nextRef) || this.isTargetRef(nextRef)) {
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
		var prevSourceRelationMap = prevSourceRelationIdx != 0 ? BERState.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceRelationIdx)) : null;
		var nextSourceRelationIdx = REFMAP.putRef(nextSourceMap, sourceRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationMap = BERState.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceRelationIdx));
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
		var prevTargetRelationMap = prevTargetRelationIdx != 0 ? BERState.asRefMap(REFMAP.getVal(prevTargetMap, prevTargetRelationIdx)) : null;
		var nextTargetRelationIdx = REFMAP.putRef(nextTargetMap, targetRef);
		if (nextTargetRelationIdx == 0) return false;
		var nextTargetRelationMap = BERState.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetRelationIdx));
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
			prevSourceRelationTargetIdx != 0 ? BERState.asRefVal(REFMAP.getVal(prevSourceRelationMap, prevSourceRelationTargetIdx)) : null;
		var nextSourceRelationTargetIdx = REFMAP.putRef(nextSourceRelationMap, relationRef);
		if (nextSourceRelationTargetIdx == 0) return false;
		var nextSourceRelationTargetVal = BERState.asRefVal(REFMAP.getVal(nextSourceRelationMap, nextSourceRelationTargetIdx));
		if (nextSourceRelationTargetVal == null) {
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, BERState.toRef(targetRef));
		} else if (BERState.isRef(nextSourceRelationTargetVal)) {
			var targetRef2 = BERState.asRef(nextSourceRelationTargetVal);
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
			prevTargetRelationSourceIdx != 0 ? BERState.asRefVal(REFMAP.getVal(prevTargetRelationMap, prevTargetRelationSourceIdx)) : null;
		var nextTargetRelationSourceIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationSourceIdx == 0) return false;
		var nextTargetRelationSourceVal = BERState.asRefVal(REFMAP.getVal(nextTargetRelationMap, nextTargetRelationSourceIdx));
		if (nextTargetRelationSourceVal == null) {
			REFMAP.setVal(nextTargetRelationMap, nextTargetRelationSourceIdx, BERState.toRef(sourceRef));
		} else if (BERState.isRef(nextTargetRelationSourceVal)) {
			var sourceRef2 = BERState.asRef(nextTargetRelationSourceVal);
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

	public boolean put(BEREdge edge) {
		return this.customPut(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean put(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		return this.customPut(sourceRef, relationRef, targetRef);
	}

	public boolean putAll(BEREdge... edges) {
		return this.putAll(Arrays.asList(edges));
	}

	public boolean putAll(Iterable<BEREdge> edges) {
		return this.defaultPutAll(edges);
	}

	public boolean pop(BEREdge edge) {
		this.backup();
		return this.customPop(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.backup();
		return this.customPop(sourceRef, relationRef, targetRef);
	}

	public boolean popAll(BEREdge... edges) {
		return this.popAll(Arrays.asList(edges));
	}

	public boolean popAll(Iterable<BEREdge> edges) {
		return this.defaultPopAll(edges);
	}

	public boolean popAll(BERState edges) {
		this.backup();
		var res = new boolean[1];
		edges.forEach((sourceRef, relationRef, targetRef) -> res[0] = this.customPop(sourceRef, relationRef, targetRef) | res[0]);
		return res[0];
	}

	public BERUpdate commit() {
		return new BERUpdate(this, true);
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	public BERUpdate rollback() {
		return new BERUpdate(this, false);
	}

	final boolean defaultPutAll(Iterable<BEREdge> edges) {
		var res = false;
		for (var edge: edges) {
			res = this.put(edge) | res;
		}
		return res;
	}

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

	final boolean defaultPopAll(Iterable<BEREdge> edges) {
		var res = false;
		for (var edge: edges) {
			res = this.pop(edge) | res;
		}
		return res;
	}

	Object owner;

	BERState backup;

	Object[] prevSourceMap;

	Object[] prevTargetMap;

	void backup() {
		if (this.backup != null) return;
		this.backup = new BERState();
		this.backup.setAll(this);
	}

}
