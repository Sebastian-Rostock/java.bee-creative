package bee.creative.log;

class ScopedEnterNode extends ScopedEntryNode {

	ScopedEnterNode(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	public int indent() {
		return +1;
	}

}