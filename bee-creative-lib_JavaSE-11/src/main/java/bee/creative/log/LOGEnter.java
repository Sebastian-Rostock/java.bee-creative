package bee.creative.log;

class LOGEnter extends LOGEntry {

	@Override
	public int indent() {
		return +1;
	}

	LOGEnter(Object text, Object[] args) {
		super(text, args);
	}

}