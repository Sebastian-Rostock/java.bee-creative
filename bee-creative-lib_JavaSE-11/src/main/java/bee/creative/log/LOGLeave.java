package bee.creative.log;

class LOGLeave extends LOGEntry {

	@Override
	public int indent() {
		return -1;
	}

	LOGLeave(Object text, Object[] args) {
		super(text, args);
	}

}