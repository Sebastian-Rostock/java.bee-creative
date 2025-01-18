package bee.creative.log;

class LOGEnter extends LOGEntry {

	@Override
	public int indent() {
		return +1;
	}

	LOGEnter(LOGBuilder owner, Object text, Object[] args) {
		super(owner, text, args);
	}

}