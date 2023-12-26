package bee.creative.qs.h2;

class H2QTSetOrder extends H2QTSet {

	@Override
	public H2QTSet order() {
		return this;
	}

	H2QTSetOrder(H2QTSet that) {
		super(that.owner, that.names, new H2QQ());
		var size = names().size();
		var qry = this.table.push("SELECT * FROM (").push(that).push(") ORDER BY C0");
		for (var i = 1; i < size; i++) {
			qry.push(", C").push(i);
		}
	}

}