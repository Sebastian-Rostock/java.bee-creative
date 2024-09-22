package bee.creative.ber;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import bee.creative.lang.Strings;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine Menge von Kanten eines bidirectional-entity-relation Speichers.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERState implements Iterable2<BEREdge> {

	public static BERState from(IntBuffer buffer) {
		var p = new int[buffer.remaining()];
		buffer.get(p);

		return null;

	}

	public static BERState from(ByteBuffer buffer) {
		return BERState.from(buffer.asIntBuffer());
	}

	public static BERState from(int[] array) {

		return null;
	}

	// putEdges bzw popEdges
	public static BERState from(BERState oldStete, BERState newState) {

		return null;
	}

	public int getRootRef() {
		return this.rootRef;
	}

	public int getNextRef() {
		return this.nextRef;
	}

	// als source vorkommende knoten
	public int[] getSourceRefs() {
		this.restore();
		return REFMAP.toArray(this.sourceMap);
	}

	/** Diese Methode liefert die Anzahl der als {@code source} vorkommenden Entitäten.
	 *
	 * @return {@code source}-Anzahl. */
	public int getSourceCount() {
		this.restore();
		return REFMAP.size(this.sourceMap);
	}

	public int[] getSourceRelationRefs(int sourceRef) {
		if (sourceRef == 0) return BERState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return BERState.EMPTY_REFS;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.toArray(relationMap);
	}

	public int getSourceRelationCount(int sourceRef) {
		if (sourceRef == 0) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.size(relationMap);
	}

	// erster als target bei source und rel vorkommender knoten
	public int getSourceRelationTargetRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(targetVal)) return BERState.asRef(targetVal);
		return REFSET.getRef(targetVal);
	}

	public int[] getSourceRelationTargetRefs(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return BERState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return BERState.EMPTY_REFS;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return BERState.EMPTY_REFS;
		var targetVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(targetVal)) return new int[]{BERState.asRef(targetVal)};
		return REFSET.toArray(targetVal);
	}

	public int getSourceRelationTargetCount(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(targetVal)) return 1;
		return REFSET.size(targetVal);
	}

	public int[] getTargetRefs() {
		this.restore();
		return REFMAP.toArray(this.targetMap);
	}

	public int getTargetCount() {
		this.restore();
		return REFMAP.size(this.targetMap);
	}

	// als rel bei target vorkommenden knoten
	public int[] getTargetRelationRefs(int targetRef) {
		if (targetRef == 0) return BERState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return BERState.EMPTY_REFS;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.toArray(relationMap);
	}

	// anzahl der als rel bei target vorkommenden knoten
	public int getTargetRelationCount(int targetRef) {
		if (targetRef == 0) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.size(relationMap);
	}

	// erster als source bei target und rel vorkommender knoten
	public int getTargetRelationSourceRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(sourceVal)) return BERState.asRef(sourceVal);
		return REFSET.getRef(sourceVal);
	}

	// als source bei target und rel vorkommende knoten
	public int[] getTargetRelationSourceRefs(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return BERState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return BERState.EMPTY_REFS;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return BERState.EMPTY_REFS;
		var sourceVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(sourceVal)) return new int[]{BERState.asRef(sourceVal)};
		return REFSET.toArray(sourceVal);
	}

	// anzahl der als source bei target und rel vorkommenden knoten
	public int getTargetRelationSourceCount(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(sourceVal)) return 1;
		return REFSET.size(sourceVal);
	}

	public boolean isSourceRef(int sourceRef) {
		if (sourceRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.sourceMap, sourceRef) != 0;
	}

	public boolean isSourceRelationRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	public boolean isSourceRelationTargetRef(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var targetVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(targetVal)) return BERState.asRef(targetVal) == targetRef;
		return REFSET.getIdx(targetVal, targetRef) != 0;
	}

	public boolean isTargetRef(int targetRef) {
		if (targetRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.targetMap, targetRef) != 0;
	}

	public boolean isTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	public boolean isTargetRelationSourceRef(int targetRef, int relationRef, int sourceRef) {
		if ((targetRef == 0) || (relationRef == 0) || (sourceRef == 0)) return false;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = BERState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var sourceVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (BERState.isRef(sourceVal)) return BERState.asRef(sourceVal) == sourceRef;
		return REFSET.getIdx(sourceVal, sourceRef) != 0;
	}

	public void forEach(BERTask task) {
		if (this.storage != null) {
			BERState.forEach(task, this.storage);
		} else {
			BERState.forEach(task, this.sourceMap);
		}
	}

	@Override
	public Iterator2<BEREdge> iterator() { // TODO eigene iter
		var sourceRefs = this.getSourceRefs();
		return Iterators.concatAll(Iterators.concatAll(Iterators.fromCount(sourceRefs.length).translate(sourceIdx -> {
			var sourceRef = sourceRefs[sourceIdx];
			var relationRefs = this.getSourceRelationRefs(sourceRef);
			return Iterators.fromCount(relationRefs.length).translate(relationIdx -> {
				var relationRef = relationRefs[relationIdx];
				var targetRefs = this.getSourceRelationTargetRefs(sourceRef, relationRef);
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

	static void forEach(BERTask task, int[] storage) {
		// TODO
	}

	static void forEach(BERTask task, Object[] sourceMap) {
		var sourceKeys = REFMAP.getKeys(sourceMap);
		for (var sourceIdx = sourceMap.length; 0 < sourceIdx; sourceIdx--) {
			var relationMap = BERState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
			if (relationMap != null) {
				var sourceRef = REFSET.getRef(sourceKeys, sourceIdx);
				var relationKeys = REFMAP.getKeys(relationMap);
				for (var relationIdx = relationMap.length; 0 < relationIdx; relationIdx--) {
					var targetVal = BERState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
					if (targetVal != null) {
						var relationRef = REFSET.getRef(relationKeys, relationIdx);
						if (BERState.isRef(targetVal)) {
							task.run(sourceRef, relationRef, BERState.asRef(targetVal));
						} else {
							REFSET.toArray(targetVal);
							for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
								var targetRef = targetVal[targetIdx];
								if (targetRef != 0) {
									task.run(sourceRef, relationRef, targetRef);
								}
							}
						}
					}
				}
			}
		}
	}

	static Object[] restorePut(Object[] sourceMap, int sourceRef, int relationRef, int targetRef) {

		sourceMap = REFMAP.grow(sourceMap);

		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceIdx == 0) throw new IllegalStateException();

		var sourceRelationMap = BERState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		sourceRelationMap = sourceRelationMap == null ? REFMAP.make() : REFMAP.grow(sourceRelationMap);
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) throw new IllegalStateException();

		var sourceRelationTargetVal = BERState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, BERState.toRef(targetRef));
		} else if (BERState.isRef(sourceRelationTargetVal)) {
			var targetRef2 = BERState.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return sourceMap;
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(sourceRelationTargetVal);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
			var targetIdx = REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (targetIdx == 0) throw new IllegalStateException();
		}

		return sourceMap;
	}

	int rootRef;

	int nextRef;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@code source}-Referenzen auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@code relation}-Referenzen auf {@code target}-Referenzen. Letztere sind dabei als {@link Integer} oder gemäß {@link REFSET} abgebildet. */
	Object[] sourceMap = REFMAP.EMPTY;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@code target}-Referenzen auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@code relation}-Referenzen auf {@code source}-Referenzen. Letztere sind dabei als {@link Integer} oder gemäß {@link REFSET} abgebildet. */
	Object[] targetMap = REFMAP.EMPTY;

	/** Dieses Feld speichert alle {@link BEREdge Kanten} als kompaktes {@code int}-Array der Struktur
	 * {@code (hashCode, rootRef, nextRef, sourceCount, (sourceRef, targetRefCount, targetSetCount, (relationRef, targetRef)[targetRefCount], (relationRef, targetCount, targetRef[targetCount])[targetSetCount])[sourceCount])}. */
	int[] storage;

	/** Diese Methode übernimmt alle Merkmale des gegebenen {@link BERState}. */
	void setAll(BERState source) {
		this.rootRef = source.rootRef;
		this.nextRef = source.nextRef;
		this.sourceMap = source.sourceMap;
		this.targetMap = source.targetMap;
		this.storage = source.storage;
	}

	/** Diese Methode ersetzt {@link #sourceMap} und {@link #targetMap} nur dann mit den in {@link #storage} hinterlegten {@link BEREdge}, wenn {@link #storage}
	 * nicht {@code null} ist. Anschließend wird {@link #storage} auf {@code null} gesetzt. */
	void restore() {
		if (this.storage == null) return;
		try {
			this.sourceMap = REFMAP.EMPTY;
			this.targetMap = REFMAP.EMPTY;
			BERState.forEach((BERTask)(sourceRef, relationRef, targetRef) -> {
				this.sourceMap = BERState.restorePut(this.sourceMap, sourceRef, relationRef, targetRef);
				this.targetMap = BERState.restorePut(this.targetMap, targetRef, relationRef, sourceRef);
			}, this.storage);
			this.storage = null;
		} finally {
			if (this.storage != null) {
				this.sourceMap = REFMAP.EMPTY;
				this.targetMap = REFMAP.EMPTY;
			}
		}
	}

	final boolean putDirect(int sourceRef, int relationRef, int targetRef, BERStore berStore) {
		var sourceMap = berStore.sourceMap = REFMAP.grow(berStore.sourceMap);

		var targetMap = berStore.targetMap = REFMAP.grow(berStore.targetMap);

		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceIdx == 0) return false;

		var sourceRelationMap = BERState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		sourceRelationMap = sourceRelationMap == null ? REFMAP.make() : REFMAP.grow(sourceRelationMap);
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		var targetIdx = REFMAP.putRef(targetMap, targetRef);
		if (targetIdx == 0) return false;
		var targetRelationMap = BERState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		targetRelationMap = targetRelationMap == null ? REFMAP.make() : REFMAP.grow(targetRelationMap);
		REFMAP.setVal(targetMap, targetIdx, targetRelationMap);

		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) return false;
		var sourceRelationTargetVal = BERState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, BERState.toRef(targetRef));
		} else if (BERState.isRef(sourceRelationTargetVal)) {
			var targetRef2 = BERState.asRef(sourceRelationTargetVal);
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
		var targetRelationSourceVal = BERState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		if (targetRelationSourceVal == null) {
			REFMAP.setVal(targetRelationMap, targetRelationIdx, BERState.toRef(sourceRef));
		} else if (BERState.isRef(targetRelationSourceVal)) {
			var sourceRef2 = BERState.asRef(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.from(sourceRef, sourceRef2));
		} else {
			var targetRelationSourceSet = REFSET.grow(targetRelationSourceVal);
			REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet);
			REFSET.putRef(targetRelationSourceSet, sourceRef);
		}

		return true;
	}

	final boolean popDirect(int sourceRef, int relationRef, int targetRef, BERStore berStore) {
		var nextSourceMap = berStore.sourceMap;
		var nextSourceIdx = REFMAP.getIdx(nextSourceMap, sourceRef);
		if (nextSourceIdx == 0) return false;

		var nextTargetMap = berStore.targetMap;
		var nextTargetIdx = REFMAP.getIdx(nextTargetMap, targetRef);
		if (nextTargetIdx == 0) return false;

		var nextSourceRelationMap = BERState.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceIdx));

		var nextTargetRelationMap = BERState.asRefMap(REFMAP.getVal(nextTargetMap, nextTargetIdx));

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
				berStore.sourceMap = REFMAP.pack(nextSourceMap);
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
					berStore.sourceMap = REFMAP.pack(nextSourceMap);
				} else {
					REFMAP.setVal(nextSourceMap, nextSourceIdx, REFMAP.pack(nextSourceRelationMap));
				}
			} else {
				REFMAP.setVal(nextSourceRelationMap, nextSourceRelationIdx, REFSET.pack(nextSourceRelationTargetSet));
			}
		}

		if (BERState.isRef(nextTargetRelationSourceVal)) {
			REFMAP.setVal(nextTargetRelationMap, REFMAP.popRef(nextTargetRelationMap, relationRef), null);
			if (REFMAP.size(nextTargetRelationMap) == 0) {
				REFMAP.setVal(nextTargetMap, REFMAP.popRef(nextTargetMap, targetRef), null);
				berStore.targetMap = REFMAP.pack(nextTargetMap);
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
					berStore.targetMap = REFMAP.pack(nextTargetMap);
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