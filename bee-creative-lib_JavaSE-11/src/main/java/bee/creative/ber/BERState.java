package bee.creative.ber;

class BERState extends BEREdges {

	public int getRootRef() {
		return this.rootRef;
	}

	public void setRootRef(int rootRef) {
		this.rootRef = rootRef;
	}

	public int getNextRef() {
		return this.nextRef;
	}

	public void setNextRef(int nextRef) {
		this.nextRef = nextRef;
	}

	int rootRef;

	int nextRef;

}
