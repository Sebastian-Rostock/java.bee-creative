package bee.creative.qs.h2;

class H2QVSetMain extends H2QVSet {

	@Override
	public H2QNSet nodes() {
		return this.owner.nodes();
	}

	H2QVSetMain(H2QS owner) {
		super(owner, new H2QQ().push("SELECT V FROM QN"));
	}

}