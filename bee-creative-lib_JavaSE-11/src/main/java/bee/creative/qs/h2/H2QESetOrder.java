package bee.creative.qs.h2;

class H2QESetOrder extends H2QESet {

	@Override
	public H2QESetOrder order() {
		return this;
	}

	H2QESetOrder(H2QESet that) {
		super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY C, P, S, O"));
	}

}