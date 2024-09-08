package bee.creative.ber;

import bee.creative.lang.Strings;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine Menge von Kanten eines bidirectional-entity-relation Speichers.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BEREdges implements Iterable2<BEREdge> {

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
		var sourceVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(sourceVal)) return 1;
		return REFSET.size(BEREdges.asRefSet(sourceVal));
	}

	// erster als source bei target und rel vorkommender knoten
	public int getSourceRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(sourceVal)) return BEREdges.asRef(sourceVal);
		return REFSET.getRef(BEREdges.asRefSet(sourceVal));
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
		var sourceVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(sourceVal)) return new int[]{BEREdges.asRef(sourceVal)};
		return REFSET.toArray(BEREdges.asRefSet(sourceVal));
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
		var targetVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(targetVal)) return 1;
		return REFSET.size(BEREdges.asRefSet(targetVal));
	}

	// erster als target bei source und rel vorkommender knoten
	public int getTargetRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(targetVal)) return BEREdges.asRef(targetVal);
		return REFSET.getRef(BEREdges.asRefSet(targetVal));
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
		var targetVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(targetVal)) return new int[]{BEREdges.asRef(targetVal)};
		return REFSET.toArray(BEREdges.asRefSet(targetVal));
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
		var sourceVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(sourceVal)) return BEREdges.asRef(sourceVal) == sourceRef;
		return REFSET.getIdx(BEREdges.asRefSet(sourceVal), sourceRef) != 0;
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
		var targetVal = REFMAP.getVal(relationMap, relationIdx);
		if (BEREdges.isRef(targetVal)) return BEREdges.asRef(targetVal) == targetRef;
		return REFSET.getIdx(BEREdges.asRefSet(targetVal), targetRef) != 0;
	}

	public boolean isTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = BEREdges.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
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

	static boolean isRef(Object val) {
		return val instanceof Integer;
	}

	static int asRef(Object val) {
		return ((Integer)val);
	}

	static int[] asRefSet(Object val) {
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

}