package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import bee.creative.qs.QISet;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Iterator2;

/** Diese Klasse implementiert ein {@link QISet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge. */
public abstract class H2QISet<GI> implements QISet<GI> {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public long size() {
		try (ResultSet rset = new H2QQ().push("SELECT COUNT(*) FROM (").push(this).push(")").select(this.owner)) {
			return rset.next() ? rset.getLong(1) : 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public GI first() {
		try (final ResultSet rset = new H2QQ().push("SELECT TOP 1 * FROM (").push(this).push(")").select(this.owner)) {
			return rset.next() ? this.customItem(rset) : null;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean isEmpty() {
		try (final ResultSet rset = new H2QQ().push("SELECT TOP 1 * FROM (").push(this).push(")").select(this.owner)) {
			return !rset.next();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public Iterator2<GI> iterator() {
		return new Iter();
	}

	@Override
	public Set<GI> toSet() {
		return this.iterator().toSet();
	}

	@Override
	public List<GI> toList() {
		return this.iterator().toList();
	}

	@Override
	public String toString() {
		return this.toList().toString();
	}

	/** Dieses Feld speichert die Anfrage zur Ermittlung der Einträge dieser Menge. */
	protected final H2QQ table;

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. */
	protected H2QISet(final H2QS owner, final H2QQ table) {
		this.owner = owner;
		this.table = table != null ? table : new H2QQ(owner);
	}

	/** Diese Methode liefert den Eintrag zum gegebenen {@link ResultSet}. */
	protected abstract GI customItem(final ResultSet next) throws SQLException;

	private class Iter extends AbstractIterator<GI> {

		public Iter() {
			try {
				this.item = H2QISet.this.table.select(H2QISet.this.owner);
				this.next = this.item.next();
			} catch (final SQLException cause) {
				throw new IllegalStateException(cause);
			}
		}

		@Override
		public boolean hasNext() {
			if (this.next) return true;
			try {
				this.item.getStatement().close();
			} catch (final SQLException ignore) {}
			return false;
		}

		@Override
		public GI next() {
			try {
				final GI item = H2QISet.this.customItem(this.item);
				this.next = this.item.next();
				return item;
			} catch (final SQLException cause) {
				throw new IllegalStateException(cause);
			}
		}

		@Override
		protected void finalize() throws Throwable {
			this.item.getStatement().close();
		}

		private boolean next;

		private final ResultSet item;

	}

}
