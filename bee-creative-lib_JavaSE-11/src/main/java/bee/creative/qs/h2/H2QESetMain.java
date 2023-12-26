package bee.creative.qs.h2;

class H2QESetMain extends H2QESet {

	@Override
	public H2QESetMain index() {
		return this;
	}

	H2QESetMain(H2QS owner) {
		super(owner, new H2QQ().push("SELECT * FROM QE"));
	}

}