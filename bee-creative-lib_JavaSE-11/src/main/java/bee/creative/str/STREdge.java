package bee.creative.str;

final class STREdge {

	public static STREdge from(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return null;
		return new STREdge(sourceRef, relationRef, targetRef);
	}

	public int getSourceRef() {
		return this.sourceRef;
	}

	public int getRelationRef() {
		return this.relationRef;
	}

	public int getTargetRef() {
		return this.targetRef;
	}

	@Override
	public String toString() {
		return "(" + this.sourceRef + ", " + this.relationRef + ", " + this.targetRef + ")";
	}

	final int sourceRef;

	final int relationRef;

	final int targetRef;

	STREdge(int sourceRef, int relationRef, int targetRef) {
		this.sourceRef = sourceRef;
		this.relationRef = relationRef;
		this.targetRef = targetRef;
	}

}
