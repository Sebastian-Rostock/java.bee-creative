package bee.creative.qs.h2;

class H2QNSetMain extends H2QNSet {

	@Override
	public H2QVSet values() {
		return this.owner.values();
	}

	H2QNSetMain(H2QS owner) {
		super(owner, new H2QQ().push("SELECT N FROM QN"));
	}

}