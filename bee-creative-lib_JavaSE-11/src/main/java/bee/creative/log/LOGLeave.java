package bee.creative.log;

class LOGLeave extends LOGEntry {

	LOGLeave(final Object text, final Object[] args) {
		super(text, args);
	}

	@Override
	public int indent() {
		return -1;
	}

}