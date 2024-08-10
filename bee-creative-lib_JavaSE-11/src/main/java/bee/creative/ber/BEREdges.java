package bee.creative.ber;

class BEREdges {

	/** Diese Methode liefert die Anzahl der als {@code source} vorkommenden Entitäten.
	 *
	 * @return {@code source}-Anzahl. */
	public int getSourceCount() {
		return REFSET.size(this.sourceSet);
	}

	// anzahl der als source bei target und rel vorkommenden knoten
	public int getSourceCount(int targetRef, int relationRef) {
		var targetIdx = REFSET.getIdx(this.targetSet, targetRef);

		return 0;
	}

	// erster als source bei target und rel vorkommender knoten
	public int getSourceRef(int targetRef, int relationRef) {
		return 0;
	}

	// als source vorkommende knoten
	public int[] getSourceRefs() {
		return null;
	}

	// als source bei target und rel vorkommende knoten
	public int[] getSourceRefs(int targetRef, int relationRef) {
		return null;
	}

	public int getSourceRelationCount(int sourceRef) {
		return 0;
	}

	public int[] getSourceRelationRefs(int sourceRef) {
		return null;
	}

	public int getTargetCount() {
		return 0;
	}

	public int[] getTargetRefs() {
		return null;
	}

	public int getTargetCount(int sourceRef, int relationRef) {
		return 0;
	}

	public int[] getTargetRefs(int sourceRef, int relationRef) {
		return null;
	}

	// anzahl der als rel bei target vorkommenden knoten
	public int getTargetRelationCount(int targetRef) {
		return 0;
	}

	// als rel bei target vorkommenden knoten
	public int[] getTargetRelationRefs(int targetRef) {
		return null;
	}

	public boolean isSourceRef(int sourceRef) {
		return false;
	}

	public boolean isSourceRef(int targetRef, int relationRef, int sourceRef) {
		return false;
	}

	public boolean isSourceRelationRef(int sourceRef, int relationRef) {
		return false;
	}

	public boolean isTargetRef(int targetRef) {
		return false;
	}

	public boolean isTargetRef(int sourceRef, int relationRef, int targetRef) {
		return false;
	}

	public boolean isTargetRelationRef(int targetRef, int relationRef) {
		return false;
	}

	int[] sourceSet;

	// speichert die RELMAP je source
	Object[][] sourceRelationSet;

	int[] targetSet;
}