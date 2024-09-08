package bee.creative.ber;

public class BEREdges2 extends BEREdges {

	public boolean put(BEREdge edge) {
		return this.put(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean put(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		var sourceMap = this.sourceMap = REFMAP.grow(this.sourceMap);

		var targetMap = this.targetMap = REFMAP.grow(this.targetMap);

		var sourceRelationIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceRelationIdx == 0) return false;
		var sourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(sourceMap, sourceRelationIdx));
		if (sourceRelationMap == null) {
			sourceRelationMap = REFMAP.make();
		} else {
			sourceRelationMap = REFMAP.grow(sourceRelationMap);
		}
		REFMAP.setVal(sourceMap, sourceRelationIdx, sourceRelationMap);

		var targetRelationIdx = REFMAP.putRef(targetMap, targetRef);
		if (targetRelationIdx == 0) return false;
		var targetRelationMap = BEREdges.asRefMap(REFMAP.getVal(targetMap, targetRelationIdx));
		if (targetRelationMap == null) {
			targetRelationMap = REFMAP.make();
		} else {
			targetRelationMap = REFMAP.grow(targetRelationMap);
		}
		REFMAP.setVal(targetMap, targetRelationIdx, targetRelationMap);

		var sourceRelationTargetIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationTargetIdx == 0) return false;
		var sourceRelationTargetVal = REFMAP.getVal(sourceRelationMap, sourceRelationTargetIdx);
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationTargetIdx, targetRef);
		} else if (BEREdges.isRef(sourceRelationTargetVal)) {
			var targetRef2 = BEREdges.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return false;
			REFMAP.setVal(sourceRelationMap, sourceRelationTargetIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(BEREdges.asRefSet(sourceRelationTargetVal));
			REFMAP.setVal(sourceRelationMap, sourceRelationTargetIdx, sourceRelationTargetSet);
			var sourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
			REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (sourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false;
		}

		var targetRelationSourceIdx = REFMAP.putRef(targetRelationMap, relationRef);
		if (targetRelationSourceIdx == 0) return false;
		var targetRelationSourceVal = REFMAP.getVal(targetRelationMap, targetRelationSourceIdx);
		if (targetRelationSourceVal == null) {
			REFMAP.setVal(targetRelationMap, targetRelationSourceIdx, sourceRef);
		} else if (BEREdges.isRef(targetRelationSourceVal)) {
			var sourceRef2 = BEREdges.asRef(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationSourceIdx, REFSET.from(sourceRef, sourceRef2));
		} else {
			var targetRelationSourceSet = REFSET.grow(BEREdges.asRefSet(targetRelationSourceVal));
			REFMAP.setVal(targetRelationMap, targetRelationSourceIdx, targetRelationSourceSet);
			REFSET.putRef(targetRelationSourceSet, sourceRef);
		}

		return true;
	}

}
