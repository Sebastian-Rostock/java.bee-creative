package bee.creative.ber;

import java.util.Arrays;
import bee.creative.lang.Strings;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine Menge von Kanten eines bidirectional-entity-relation Speichers.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BEREdges implements Iterable2<BEREdge> {

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
		return this.customPop(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		return this.customPop(sourceRef, relationRef, targetRef);
	}

	public boolean popAll(BEREdge... edges) {
		return this.popAll(Arrays.asList(edges));
	}

	public boolean popAll(Iterable<BEREdge> edges) {
		return this.defaultPopAll(edges);
	}

	public void clear() {
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
	}

	/** Diese Methode liefert die Anzahl der als {@code source} vorkommenden Entitäten.
	 *
	 * @return {@code source}-Anzahl. */
	public int getSourceCount() {
		return REFMAP.size(this.sourceMap);
	}

	// anzahl der als source bei target und rel vorkommenden knoten
	public int getSourceCount(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(sourceVal)) return 1;
		return REFSET.size(sourceVal);
	}

	// erster als source bei target und rel vorkommender knoten
	public int getSourceRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(sourceVal)) return BEREdges.asRef(sourceVal);
		return REFSET.getRef(sourceVal);
	}

	// als source vorkommende knoten
	public int[] getSourceRefs() {
		return REFMAP.toArray(this.sourceMap);
	}

	// als source bei target und rel vorkommende knoten
	public int[] getSourceRefs(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return BEREdges.EMPTY_REFS;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return BEREdges.EMPTY_REFS;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return BEREdges.EMPTY_REFS;
		var sourceVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(sourceVal)) return new int[]{BEREdges.asRef(sourceVal)};
		return REFSET.toArray(sourceVal);
	}

	public int getSourceRelationCount(int sourceRef) {
		if (sourceRef == 0) return 0;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.size(relationMap);
	}

	public int[] getSourceRelationRefs(int sourceRef) {
		if (sourceRef == 0) return BEREdges.EMPTY_REFS;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return BEREdges.EMPTY_REFS;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.toArray(relationMap);
	}

	public int getTargetCount() {
		return REFMAP.size(this.targetMap);
	}

	public int getTargetCount(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(targetVal)) return 1;
		return REFSET.size(targetVal);
	}

	// erster als target bei source und rel vorkommender knoten
	public int getTargetRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(targetVal)) return BEREdges.asRef(targetVal);
		return REFSET.getRef(targetVal);
	}

	public int[] getTargetRefs() {
		return REFMAP.toArray(this.targetMap);
	}

	public int[] getTargetRefs(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return BEREdges.EMPTY_REFS;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return BEREdges.EMPTY_REFS;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return BEREdges.EMPTY_REFS;
		var targetVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(targetVal)) return new int[]{BEREdges.asRef(targetVal)};
		return REFSET.toArray(targetVal);
	}

	// anzahl der als rel bei target vorkommenden knoten
	public int getTargetRelationCount(int targetRef) {
		if (targetRef == 0) return 0;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.size(relationMap);
	}

	// als rel bei target vorkommenden knoten
	public int[] getTargetRelationRefs(int targetRef) {
		if (targetRef == 0) return BEREdges.EMPTY_REFS;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return BEREdges.EMPTY_REFS;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.toArray(relationMap);
	}

	public boolean isSourceRef(int sourceRef) {
		if (sourceRef == 0) return false;
		return REFMAP.getIdx(this.sourceMap, sourceRef) != 0;
	}

	public boolean isSourceRef(int targetRef, int relationRef, int sourceRef) {
		if ((targetRef == 0) || (relationRef == 0) || (sourceRef == 0)) return false;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var sourceVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(sourceVal)) return BEREdges.asRef(sourceVal) == sourceRef;
		return REFSET.getIdx(sourceVal, sourceRef) != 0;
	}

	public boolean isSourceRelationRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	public boolean isTargetRef(int targetRef) {
		if (targetRef == 0) return false;
		return REFMAP.getIdx(this.targetMap, targetRef) != 0;
	}

	public boolean isTargetRef(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var targetVal = BEREdges.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BEREdges.isRef(targetVal)) return BEREdges.asRef(targetVal) == targetRef;
		return REFSET.getIdx(targetVal, targetRef) != 0;
	}

	public boolean isTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	public boolean isReadonly() {
		return false;
	}

	@Override
	public Iterator2<BEREdge> iterator() {
		var sourceRefs = this.getSourceRefs();
		return Iterators.concatAll(Iterators.concatAll(Iterators.fromCount(sourceRefs.length).translate(sourceIdx -> {
			var sourceRef = sourceRefs[sourceIdx];
			var relationRefs = this.getSourceRelationRefs(sourceRef);
			return Iterators.fromCount(relationRefs.length).translate(relationIdx -> {
				var relationRef = relationRefs[relationIdx];
				var targetRefs = this.getTargetRefs(sourceRef, relationRef);
				return Iterators.fromCount(targetRefs.length).translate(targetIdx -> {
					var targetRef = targetRefs[targetIdx];
					return new BEREdge(sourceRef, relationRef, targetRef);
				});
			});
		})));
	}

	@Override
	public String toString() {
		var res = new StringBuilder("{ ");
		Strings.join(res, ", ", this);
		return res.append(" }").toString();
	}

	static final int[] EMPTY_REFS = new int[0];

	static boolean isRef(int[] val) {
		return val.length == 1;
	}

	static int[] toRef(int ref) {
		return new int[]{ref};
	}

	static int asRef(int[] val) {
		return val[0];
	}

	static int[] asRefVal(Object val) {
		return (int[])val;
	}

	static Object[] asRefMap(Object val) {
		return (Object[])val;
	}

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@code source}-Referenzen auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@code relation}-Referenzen auf {@code target}-Referenzen. Letztere sind dabei als {@link Integer} oder gemäß {@link REFSET} abgebildet. */
	Object[] sourceMap = REFMAP.EMPTY;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@code target}-Referenzen auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@code relation}-Referenzen auf {@code source}-Referenzen. Letztere sind dabei als {@link Integer} oder gemäß {@link REFSET} abgebildet. */
	Object[] targetMap = REFMAP.EMPTY;

	boolean customPut(int sourceRef, int relationRef, int targetRef) {
		return this.defaultPut(sourceRef, relationRef, targetRef);
	}

	boolean customPop(int sourceRef, int relationRef, int targetRef) {
		return this.defaultPop(sourceRef, relationRef, targetRef);
	}

	final boolean defaultPut(int sourceRef, int relationRef, int targetRef) {
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
		var sourceRelationTargetVal = BEREdges.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, BEREdges.toRef(targetRef));
		} else if (BEREdges.isRef(sourceRelationTargetVal)) {
			var targetRef2 = BEREdges.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return false;
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(sourceRelationTargetVal);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
			var sourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
			REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (sourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false;
		}

		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);
		if (targetRelationIdx == 0) return false;
		var targetRelationSourceVal = BEREdges.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		if (targetRelationSourceVal == null) {
			REFMAP.setVal(targetRelationMap, targetRelationIdx, BEREdges.toRef(sourceRef));
		} else if (BEREdges.isRef(targetRelationSourceVal)) {
			var sourceRef2 = BEREdges.asRef(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.from(sourceRef, sourceRef2));
		} else {
			var targetRelationSourceSet = REFSET.grow(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet);
			REFSET.putRef(targetRelationSourceSet, sourceRef);
		}

		return true;
	}

	final boolean defaultPutAll(Iterable<BEREdge> edges) {
		var res = false;
		for (var edge: edges) {
			res = this.put(edge) | res;
		}
		return res;
	}

	final boolean defaultPop(int sourceRef, int relationRef, int targetRef) {
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
			var nextTargetRelationSourceSet = nextTargetRelationSourceVal;
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

}