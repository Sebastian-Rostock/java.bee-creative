package bee.creative.ber;

class BERState extends BEREdges {

	public int getRootRef() {
		return this.rootRef;
	}

	public int getNextRef() {
		return this.nextRef;
	}

	int rootRef;

	int nextRef;
}
