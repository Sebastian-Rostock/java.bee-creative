package bee.creative.log;

class LOGLeave extends LOGEntry {

	@Override
	public int indent() {
		return -1;
	}

	LOGLeave(LOGBuilder owner, Object text, Object[] args) {
		super(owner, text, args);
	}

}