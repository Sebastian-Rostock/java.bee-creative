package bee.creative.log;

class ScopedLeaveNode extends ScopedEntryNode {

	ScopedLeaveNode(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	public int indent() {
		return -1;
	}

}