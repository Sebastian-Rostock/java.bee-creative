package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Entries;
import bee.creative.util.Setter;

public abstract class H2QDBag<GI> extends H2QISet<GI> {

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}, der die
	 * {@link Object#toString() Textdarstellung} eines Elements dieser Datensammlung darstellt.
	 *
	 * @param items Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	public void items(final Setter<QN, GI> items) {
		try (final ResultSet rset = this.table.select(this.owner)) {
			while (rset.next()) {
				items.set(this.node(rset), this.item(rset));
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

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Die Tabelle muss mit der Spalte
	 * {@code N BIGINT NOT NULL PRIMARY KEY} beginnen. Der {@code index} gibt den Namen der automatisch erzeugten Tabelle zur Erfassung der indizierten
	 * {@link QS#nodes() Hperknoten mit Textwert}. */
	protected H2QDBag(final H2QS owner, final H2QQ table, final String index) {
		super(owner, Objects.notNull(table));
		if (index == null) return;
		new H2QQ().push("CREATE TABLE IF NOT EXISTS ").push(index).push(" (N BIGINT NOT NULL, PRIMARY KEY (N));").update(owner);
		this.table.push(new Cache(index));
	}

	protected QN node(final ResultSet next) throws SQLException {
		return this.owner.newNode(next.getLong(1));
	}

	protected void putItems(final PutItemSet putItemSet) throws SQLException {
		System.err.println(putItemSet.toList());
	}

	protected void popItems(final PopItemSet popItemSet) throws SQLException {
		System.err.println(popItemSet.toList());
	}

	protected class Cache {

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
					final var popItemSet = new PopItemSet(owner, this.index);
					new H2QQ().push("DELETE FROM ").push(this.index).push(" WHERE N IN (").push(popItemSet).push(")").update(owner);
					that.popItems(popItemSet);
				}
				if (putValueChanged) {
					final var putItemSet = new PutItemSet(owner, this.index);
					new H2QQ().push("MERGE INTO ").push(this.index).push(" SELECT N FROM (").push(putItemSet).push(")").update(owner);
					that.putItems(putItemSet);
				}
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
	 * {@link H2QDBag#putItems(PutItemSet)}. Die {@link Entry#getKey() Schüssel} jedes Eintrags nennt die {@link H2QN#key Kennung} des {@link H2QE Hyperknoten},
	 * dessen {@link H2QN#value() Textwert} im {@link Entry#getValue() Werte} des Eintrags angegeben ist. */
	protected static class PutItemSet extends H2QISet<Entry<Long, String>> {

		public void collectAll(final Setter<Long, String> setter) throws SQLException {
			try (var rset = this.table.select(this.owner)) {
				while (rset.next()) {
					setter.set((Long)rset.getObject(1), rset.getString(2));
				}
			}
		}

		@Override
		protected Entry<Long, String> item(final ResultSet next) throws SQLException {
			return Entries.from((Long)next.getObject(1), next.getString(2));
		}

		PutItemSet(final H2QS owner, final String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table)
				.push(" (N BIGINT NOT NULL, V VARCHAR(1G) NOT NULL, PRIMARY KEY (N)) AS (SELECT N, V FROM QN where N NOT IN (SELECT N FROM ").push(index).push("))")
				.update(owner);
		}

	}

	/** Diese Klasse implementiert eine temporäre Kopie der Menge der indizierten {@link H2QS#nodes() Hyperknoten mit Textwert} für
	 * {@link H2QDBag#popItems(PopItemSet)}. Jeder Eintrag nennt die {@link H2QN#key Kennung} eines gelöschten {@link H2QE Hyperknoten}. */
	protected static class PopItemSet extends H2QISet<Long> {

		@Override
		protected Long item(final ResultSet next) throws SQLException {
			return next.getLong(1);
		}

		PopItemSet(final H2QS owner, final String index) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL, PRIMARY KEY (N)) AS (SELECT N FROM ").push(index)
				.push(" WHERE N NOT IN (SELECT N FROM QN))").update(owner);
		}

	}

}
