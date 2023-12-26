package bee.creative.qs.h2;

class H2QVSetOrder extends H2QVSet {

	@Override
	public H2QVSet order() {
		return this;
	}

	H2QVSetOrder(H2QVSet that) {
		super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY V"));
	}

}