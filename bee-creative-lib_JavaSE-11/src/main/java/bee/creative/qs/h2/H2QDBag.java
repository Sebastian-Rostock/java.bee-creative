package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Entries;
import bee.creative.util.Setter;

public abstract class H2QDBag<GI, GIBag> extends H2QISet<GI> {

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}, der die
	 * {@link Object#toString() Textdarstellung} eines Elements dieser Datensammlung darstellt.
	 *
	 * @param items Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	public void items(final Setter<? super H2QN, ? super GI> items) {
		try (final ResultSet rset = this.table.select(this.owner)) {
			while (rset.next()) {
				items.set(this.owner.newNode(rset.getLong(1)), this.customItem(rset));
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	public H2QNSet nodes() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT N FROM (").push(this).push(")"));
	}

	public H2QVSet values() {
		return this.nodes().values();
	}

	public GIBag havingItemsEQ(final GI item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemEQ(table, item)));
	}

	public GIBag havingItemsLT(final GI item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemLT(table, item)));
	}

	public GIBag havingItemsLE(final GI item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE (").push(table -> this.customHavingItemLT(table, item)).push(") OR (")
			.push(table -> this.customHavingItemEQ(table, item)).push(")"));
	}

	public GIBag havingItemsGT(final GI item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE ").push(table -> this.customHavingItemGT(table, item)));
	}

	public GIBag havingItemsGE(final GI item) throws NullPointerException, IllegalArgumentException {
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE (").push(table -> this.customHavingItemGT(table, item)).push(") OR (")
			.push(table -> this.customHavingItemEQ(table, item)).push(")"));
	}

	public GIBag havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(nodes);
		return this.customHaving(new H2QQ().push("SELECT * FROM (").push(this).push(") WHERE N IN (SELECT N FROM (").push(that).push("))"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Die Tabelle muss mit der Spalte
	 * {@code N BIGINT NOT NULL PRIMARY KEY} beginnen. Der {@code index} gibt den Namen der automatisch erzeugten Tabelle zur Erfassung der indizierten
	 * {@link QS#nodes() Hperknoten mit Textwert} an. */
	protected H2QDBag(final H2QS owner, final H2QQ table, final String index) {
		super(owner, Objects.notNull(table));
		if (index == null) return;
		synchronized (owner.cacheMap) {
			this.table.push(owner.cacheMap.install(index, index2 -> {
				new H2QQ().push("CREATE TABLE IF NOT EXISTS ").push(index2).push(" (N BIGINT NOT NULL, PRIMARY KEY (N));").update(owner);
				this.customSetup();
				return new Cache(index2);
			}));
		}
	}

	protected abstract void customSetup();

	protected abstract void customInsert(final InsertSet insertSet) throws SQLException;

	protected abstract void customDelete(final DeleteSet deleteSet) throws SQLException;

	protected abstract GIBag customHaving(H2QQ table) throws NullPointerException, IllegalArgumentException;

	protected abstract void customHavingItemEQ(final H2QQ table, final GI item) throws NullPointerException, IllegalArgumentException;

	protected abstract void customHavingItemLT(final H2QQ table, final GI item) throws NullPointerException, IllegalArgumentException;

	protected abstract void customHavingItemGT(final H2QQ table, final GI item) throws NullPointerException, IllegalArgumentException;

	protected final class Cache {

		@Override
		public String toString() {
			final var that = H2QDBag.this;
			final var owner = that.owner;
			final var putValueMark = owner.putValueMark;
			final var popValueMark = owner.popValueMark;
			final var putValueChanged = this.putValueMark != putValueMark;
			final var popValueChanged = this.popValueMark != popValueMark;
			if (!putValueChanged && !popValueChanged) return "";
			this.putValueMark = putValueMark;
			this.popValueMark = popValueMark;
			try {
				if (popValueChanged) {
					final var popItemSet = new DeleteSet(owner, this.index);
					new H2QQ().push("DELETE FROM ").push(this.index).push(" WHERE N IN (").push(popItemSet).push(")").update(owner);
					that.customDelete(popItemSet);
				}
				if (putValueChanged) {
					final var putItemSet = new InsertSet(owner, this.index);
					new H2QQ().push("MERGE INTO ").push(this.index).push(" SELECT N FROM (").push(putItemSet).push(")").update(owner);
					that.customInsert(putItemSet);
				}
			} catch (final NullPointerException | IllegalStateException | IllegalArgumentException cause) {
				throw cause;
			} catch (final Exception cause) {
				throw new IllegalStateException(cause);
			}
			return "";
		}

		final String index;

		Object putValueMark;

		Object popValueMark;

		Cache(final String index) {
			this.index = index;
		}

	}

	/** Diese Klasse implementiert eine temporäre Kopie der Menge der noch nicht indizierten {@link H2QS#nodes() Hyperknoten mit Textwert} für
	 * {@link H2QDBag#customInsert(InsertSet)}. Die {@link Entry#getKey() Schüssel} jedes Eintrags nennt die {@link H2QN#key Kennung} des {@link H2QE
	 * Hyperknoten}, dessen {@link H2QN#value() Textwert} im {@link Entry#getValue() Werte} des Eintrags angegeben ist. */
	protected static final class InsertSet extends H2QISet<Entry<Long, String>> {

		public void collectAll(final Setter<Long, String> setter) throws SQLException {
			try (var rset = this.table.select(this.owner)) {
				while (rset.next()) {
					setter.set((Long)rset.getObject(1), rset.getString(2));
				}
			}
		}

		@Override
		protected Entry<Long, String> customItem(final ResultSet next) throws SQLException {
			return Entries.from((Long)next.getObject(1), next.getString(2));
		}

		InsertSet(final H2QS owner, final String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table)
				.push(" (N BIGINT NOT NULL, V VARCHAR(1G) NOT NULL, PRIMARY KEY (N)) AS (SELECT N, V FROM QN where N NOT IN (SELECT N FROM ").push(index).push("))")
				.update(owner);
		}

	}

	/** Diese Klasse implementiert eine temporäre Kopie der Menge der indizierten {@link H2QS#nodes() Hyperknoten mit Textwert} für
	 * {@link H2QDBag#customDelete(DeleteSet)}. Jeder Eintrag nennt die {@link H2QN#key Kennung} eines gelöschten {@link H2QE Hyperknoten}. */
	protected static final class DeleteSet extends H2QISet<Long> {

		@Override
		protected Long customItem(final ResultSet next) throws SQLException {
			return next.getLong(1);
		}

		DeleteSet(final H2QS owner, final String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL, PRIMARY KEY (N)) AS (SELECT N FROM ").push(index)
				.push(" WHERE N NOT IN (SELECT N FROM QN))").update(owner);
		}

	}

}
