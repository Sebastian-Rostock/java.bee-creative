package bee.creative.ber;

class BEREdge {

	public int sourceRef;

	public int relationRef;

	public int targetRef;

	public BEREdge(int sourceRef, int relationRef, int targetRef) {
		this.sourceRef = sourceRef;
		this.relationRef = relationRef;
		this.targetRef = targetRef;
	}

	@Override
	public String toString() {
		return "(" + this.sourceRef + ", " + this.relationRef + ", " + this.targetRef + ")";
	}

}
