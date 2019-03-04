package bee.creative.log;

@SuppressWarnings ("javadoc")
class ScopedEnterNode extends ScopedEntryNode {

	ScopedEnterNode(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	public int indent() {
		return +1;
	}

}