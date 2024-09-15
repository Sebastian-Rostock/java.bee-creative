package bee.creative.ber;

final class BEREdge {

	public static BEREdge from(int sourceRef, int relationRef, int targetRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return null;
		return new BEREdge(sourceRef, relationRef, targetRef);
	}

	public int getSourceRef() {
		return sourceRef;
	}

	public int getRelationRef() {
		return relationRef;
	}

	public int getTargetRef() {
		return targetRef;
	}

	@Override
	public String toString() {
		return "(" + this.sourceRef + ", " + this.relationRef + ", " + this.targetRef + ")";
	}

	final int sourceRef;

	final int relationRef;

	final int targetRef;

	BEREdge(int sourceRef, int relationRef, int targetRef) {
		this.sourceRef = sourceRef;
		this.relationRef = relationRef;
		this.targetRef = targetRef;
	}

}
