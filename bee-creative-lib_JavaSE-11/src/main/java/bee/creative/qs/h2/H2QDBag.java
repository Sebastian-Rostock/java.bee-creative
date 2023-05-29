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
	 * {@code N BIGINT NOT NULL PRIMARY KEY} beginnen. Der Cache besteht nur aus dieser Spalte, gibt die Menge der geprüften {@link QS#nodes() Hperknoten mit
	 * Textwert} an und ist nur bei der Hauptwertesammlung anzugeben. */
	protected H2QDBag(final H2QS owner, final H2QQ table, final String cache) {
		super(owner, Objects.notNull(table));
		if (cache == null) return;
		this.table.push(new Cache(cache));
	}

	protected QN node(final ResultSet next) throws SQLException {
		return this.owner.newNode(next.getLong(1));
	}

	protected void putItems(final PutItemSet putItemSet) throws SQLException {
	}

	protected void popItems(final PopItemSet popItemSet) throws SQLException {
	}

	protected class Cache {

		@Override
		public String toString() {
			final Object putValueMark = H2QDBag.this.owner.putValueMark;
			final Object popValueMark = H2QDBag.this.owner.popValueMark;
			final boolean putValueChanged = this.putValueMark != putValueMark;
			final boolean popValueChanged = this.popValueMark != popValueMark;
			if (!putValueChanged && !popValueChanged) return "";
			this.putValueMark = putValueMark;
			this.popValueMark = popValueMark;
			try {
				if (popValueChanged) {
					H2QDBag.this.popItems(this.popItemSet);
				}
				if (putValueChanged) {
					H2QDBag.this.putItems(this.putItemSet);
				}
			} catch (final Exception cause) {
				throw new IllegalStateException(cause);
			}
			return "";
		}

		final String cache;

		final PutItemSet putItemSet;

		final PopItemSet popItemSet;

		Object putValueMark;

		Object popValueMark;

		Cache(final String cache) {
			this.cache = cache;
			this.putItemSet = new PutItemSet(H2QDBag.this.owner, cache);
			this.popItemSet = new PopItemSet(H2QDBag.this.owner, cache);
		}

	}

	protected static class PutItemSet extends H2QISet<Entry<Long, String>> {

		@Override
		protected Entry<Long, String> item(final ResultSet next) throws SQLException {
			return Entries.from(next.getLong(1), next.getString(2));
		}

		PutItemSet(final H2QS owner, final String cache) {
			super(owner, new H2QQ().push("SELECT N, V FROM QN where N NOT IN (SELECT N FROM (").push(cache).push("))"));
		}

	}

	protected static class PopItemSet extends H2QISet<Long> {

		@Override
		protected Long item(final ResultSet next) throws SQLException {
			return next.getLong(1);
		}

		PopItemSet(final H2QS owner, final String cache) {
			super(owner, new H2QQ().push("SELECT N FROM (").push(cache).push(") where N NOT IN (select N from QN)"));
		}

	}

}
