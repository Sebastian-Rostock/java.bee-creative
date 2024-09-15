package bee.creative.ber;

class BERState2 extends BERState {

	@Override
	public boolean put(int sourceRef, int relationRef, int targetRef) {
		return false;
	}

	@Override
	public boolean putAll(BEREdges edges) {
		return false;
	}

	@Override
	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		return false;
	}

	@Override
	public boolean popAll(BEREdges edges) {
		return false;
	}

	@Override
	public void setRootRef(int rootRef) {
	}

	@Override
	public void setNextRef(int nextRef) {
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

}
