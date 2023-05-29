package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
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

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Die erste Spalte der Tabelle muss
	 * {@code (N BIGINT NOT NULL PRIMARY KEY)} sein. */
	protected H2QDBag(final H2QS owner, final H2QQ table, final boolean isMain) {
		super(owner, Objects.notNull(table));
		if (!isMain) return;

	}

	protected QN node(final ResultSet next) throws SQLException {
		return this.owner.newNode(next.getLong(1));
	}

	protected void insertItems() throws SQLException {
		this.insertItems(new H2QQ().push("select N, V from QV where N NOT IN (SELECT N FROM (").push(this).push("))"));
	}

	protected void insertItems(final H2QQ newItems) throws SQLException {

	}

	protected void deleteItems() {
		this.deleteItems(new H2QQ().push("SELECT N FROM (").push(this).push(") where N NOT IN (select N from QV)"));
	}

	protected void deleteItems(final H2QQ push) {
	}

	private class Updater {
	
		Object insertValueVersion;
	
		Object deleteValueVersion;
	
		void updateValueVersion() {
			final Object insertVV = H2QDBag.this.owner.putValueMark, deleteVV = H2QDBag.this.owner.popValueMark;
			final boolean insertVVC = this.insertValueVersion != insertVV, deleteVVC = this.deleteValueVersion != deleteVV;
			if (!insertVVC && !deleteVVC) return;
			this.insertValueVersion = insertVV;
			this.deleteValueVersion = deleteVV;
			try {
				if (insertVVC) {
					H2QDBag.this.insertItems();
				}
				if (deleteVVC) {
					H2QDBag.this.deleteItems();
				}
			} catch (final Exception cause) {
				throw new IllegalStateException(cause);
			}
		}
	
		@Override
		public String toString() {
			this.updateValueVersion();
	
			return "";
		}
	
	}

}
