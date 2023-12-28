package bee.creative.qs.h2;

class H2QTSetOrder extends H2QTSet {

	@Override
	public H2QTSet order() {
		return this;
	}

	H2QTSetOrder(H2QTSet that, H2QTSetNames names) {
		super(that.owner, names, new H2QQ());
		this.table.push("SELECT * FROM (").push(that).push(") ORDER BY C0").push(1, names.size(), (q, i) -> q.push(", C").push(i));
	}

}