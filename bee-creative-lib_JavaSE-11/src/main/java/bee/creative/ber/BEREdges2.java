package bee.creative.ber;

public class BEREdges2 extends BEREdges {

	public boolean put(BEREdge edge) {
		return this.put(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean put(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		var sourceMap = this.sourceMap = REFMAP.grow(this.sourceMap);

		var targetMap = this.targetMap = REFMAP.grow(this.targetMap);

		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var sourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		if (sourceRelationMap == null) {
			sourceRelationMap = REFMAP.make();
		} else {
			sourceRelationMap = REFMAP.grow(sourceRelationMap);
		}
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		var targetIdx = REFMAP.putRef(targetMap, targetRef);
		if (targetIdx == 0) return false;
		var targetRelationMap = BEREdges.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		if (targetRelationMap == null) {
			targetRelationMap = REFMAP.make();
		} else {
			targetRelationMap = REFMAP.grow(targetRelationMap);
		}
		REFMAP.setVal(targetMap, targetIdx, targetRelationMap);

		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) return false;
		var sourceRelationTargetVal = REFMAP.getVal(sourceRelationMap, sourceRelationIdx);
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, targetRef);
		} else if (BEREdges.isRef(sourceRelationTargetVal)) {
			var targetRef2 = BEREdges.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return false;
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(BEREdges.asRefSet(sourceRelationTargetVal));
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
			var sourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
			REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (sourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false;
		}

		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);
		if (targetRelationIdx == 0) return false;
		var targetRelationSourceVal = REFMAP.getVal(targetRelationMap, targetRelationIdx);
		if (targetRelationSourceVal == null) {
			REFMAP.setVal(targetRelationMap, targetRelationIdx, sourceRef);
		} else if (BEREdges.isRef(targetRelationSourceVal)) {
			var sourceRef2 = BEREdges.asRef(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.from(sourceRef, sourceRef2));
		} else {
			var targetRelationSourceSet = REFSET.grow(BEREdges.asRefSet(targetRelationSourceVal));
			REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet);
			REFSET.putRef(targetRelationSourceSet, sourceRef);
		}

		return true;
	}
	
	public boolean pop(BEREdge edge) {
		return this.pop(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		var nextSourceMap = this.sourceMap;
		var nextSourceIdx = REFMAP.getIdx(nextSourceMap, sourceRef);
		if (nextSourceIdx == 0) return false;

		var nextTargetMap = this.targetMap;
		var nextTargetIdx = REFMAP.getIdx(nextTargetMap, targetRef);
		if (nextTargetIdx == 0) return false;

		var nextSourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));

		var nextTargetRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));

		var nextSourceRelationIdx = REFMAP.getIdx(nextSourceRelationMap, relationRef);
		if (nextSourceRelationIdx == 0) return false;
		var nextSourceRelationTargetVal = REFMAP.getVal(nextSourceRelationMap, nextSourceRelationIdx);

		var nextTargetRelationIdx = REFMAP.putRef(nextTargetRelationMap, relationRef);
		if (nextTargetRelationIdx == 0) return false; // IllegalState
		var nextTargetRelationSourceVal = REFMAP.getVal(nextTargetRelationMap, nextTargetRelationIdx);

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
			var nextSourceRelationTargetSet = BEREdges.asRefSet(nextSourceRelationTargetVal);
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
			REFMAP.setVal(nextTargetRelationMap, REFMAP.popRef(nextTargetRelationMap, relationRef), null);
			if (REFMAP.size(nextTargetRelationMap) == 0) {
				REFMAP.setVal(nextTargetMap, REFMAP.popRef(nextTargetMap, targetRef), null);
				this.targetMap = REFMAP.pack(nextTargetMap);
			} else {
				REFMAP.setVal(nextTargetMap, nextTargetIdx, REFMAP.pack(nextTargetRelationMap));
			}
		} else {
			var nextTargetRelationSourceSet = BEREdges.asRefSet(nextTargetRelationSourceVal);
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
	
}
