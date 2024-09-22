package bee.creative.ber;

/** Diese Klasse implementiert den Codec zum Speichern einer {@link BERState Kantenmenge} oder eines {@link BERState Speicherzustands} in ein int-Array.
 * <p>
 * (sourceCount, (sourceRef, targetRefCount, targetSetCount, (relationRef, targetRef)[targetRefCount], (relationRef, targetCount,
 * targetRef[targetCount])[targetSetCount])[sourceCount])
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERCodec {

	public static int[] persistState(BERState src) {
		return BERCodec.persist(src.rootRef, src.nextRef, src);
	}

	public static int[] persistEdges(BERState src) {
		return BERCodec.persist(0, 0, src);
	}

	public static BERState restoreState(int[] src) {
		BERState res = new BERState();

		return null;
	}

	public static BERState restoreEdges(int[] src) {
		BERState res = new BERState();
		
		
		return null;
	}

	private static int[] persist(int rootRef, int nextRef, BERState src) {

		var sourceMap = src.sourceMap;
		// rootRef, nextRef, sourceCount, (sourceRef, targetRefCount, targetSetCount, (relationRef, targetRef)[targetRefCount], (relationRef, targetCount,
		// targetRef[targetCount])[targetSetCount])[sourceCount]
		var sourceSize = 3;
		var sourceCount = 0;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = (Object[])sourceMap[sourceIdx];
			if (relationMap != null) {
				var relationSize = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = BERState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (BERState.isRef(targetVal)) {
							relationSize += /*relationRef*/ 1 + /*targetRef*/ 1;
						} else {
							var c = REFSET.size(targetVal);
							if (c == 1) {
								relationSize += /*relationRef*/ 1 + /*targetRef*/ 1;
							} else if (c > 0) {
								relationSize += /*relationRef*/ 1 + /*targetCount*/ 1 + /*targetRef*/ c;
							}
						}
					}
				}
				if (relationSize != 0) {
					sourceSize += /*sourceRef, targetRefCount, targetSetCount*/ 3 + relationSize;
					sourceCount++;
				}
			}
		}

		var res = new int[sourceSize];

		res[0] = rootRef;
		res[1] = nextRef;
		res[2] = sourceCount;

		System.out.println(sourceSize*4);
		
		return res;
	}

}
