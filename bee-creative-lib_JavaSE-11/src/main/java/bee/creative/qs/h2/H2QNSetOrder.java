package bee.creative.qs.h2;

class H2QNSetOrder extends H2QNSet {

	@Override
	public H2QNSet order() {
		return this;
	}

	H2QNSetOrder(H2QNSet that) {
		super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY N"));
	}

}