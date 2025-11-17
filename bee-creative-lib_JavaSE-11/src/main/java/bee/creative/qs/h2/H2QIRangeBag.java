package bee.creative.qs.h2;

import bee.creative.qs.QS;

public abstract class H2QIRangeBag<E, T> extends H2QIBag<E, T> {

	public T havingItemsEQ(E item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemEQ(table, item)));
	}

	public T havingItemsLT(E item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemLT(table, item)));
	}

	public T havingItemsLE(E item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE (").push(table -> this.customHavingItemLT(table, item)).push(") OR (")
			.push(table -> this.customHavingItemEQ(table, item)).push(")"));
	}

	public T havingItemsGT(E item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemGT(table, item)));
	}

	public T havingItemsGE(E item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE (").push(table -> this.customHavingItemGT(table, item)).push(") OR (")
			.push(table -> this.customHavingItemEQ(table, item)).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Die Tabelle muss mit der Spalte
	 * {@code N BIGINT NOT NULL PRIMARY KEY} beginnen. Der {@code index} gibt den Namen der automatisch erzeugten Tabelle zur Erfassung der indizierten
	 * {@link QS#nodes() Hperknoten mit Textwert} an. */
	protected H2QIRangeBag(H2QS owner, H2QQ table, String index) {
		super(owner, table, index);
	}

	protected abstract void customHavingItemEQ(H2QQ table, E item) throws NullPointerException, IllegalArgumentException;

	protected abstract void customHavingItemLT(H2QQ table, E item) throws NullPointerException, IllegalArgumentException;

	protected abstract void customHavingItemGT(H2QQ table, E item) throws NullPointerException, IllegalArgumentException;

}
