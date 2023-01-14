package bee.creative.log;

class LOGEnter extends LOGEntry {

	LOGEnter(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	public int indent() {
		return +1;
	}

}