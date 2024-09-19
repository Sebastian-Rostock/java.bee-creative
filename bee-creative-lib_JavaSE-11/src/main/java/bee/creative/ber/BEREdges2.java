package bee.creative.ber;

class BEREdges2 extends BEREdges {

	@Override
	public boolean put(int sourceRef, int relationRef, int targetRef) {
		return false;
	}

	@Override
	public boolean putAll(Iterable<BEREdge> edges) {
		return false;
	}

	@Override
	public boolean pop(int sourceRef, int relationRef, int targetRef) {
		return false;
	}

	@Override
	public boolean popAll(Iterable<BEREdge> edges) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

}
