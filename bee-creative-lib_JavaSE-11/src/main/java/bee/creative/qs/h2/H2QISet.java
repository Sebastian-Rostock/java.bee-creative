package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	/** Dieses Feld speichert die Anfrage zur Ermittlung der Einträgen dieser Menge, inklusive des ggf. vorhandenen Namen der {@code TABLE}. */
	public final H2QQ table;

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public long size() {
		try (ResultSet rset = new H2QQ().push("select count(*) from (").push(this).push(")").select(this.owner)) {
			return rset.next() ? rset.getLong(1) : 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public GI first() {
		try (final ResultSet rset = new H2QQ().push("select top 1 1 from (").push(this).push(")").select(this.owner)) {
			return rset.next() ? this.item(rset) : null;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean isEmpty() {
		try (final ResultSet rset = new H2QQ().push("select top 1 1 from (").push(this).push(")").select(this.owner)) {
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

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage (oder {@code null}). */
	protected H2QISet(final H2QS owner, final H2QQ table) {
		this.owner = owner;
		this.table = table != null ? table : new H2QQ(owner);
	}

	/** Diese Methode liefert das Objekt zum gegebenen {@link ResultSet}. */
	protected abstract GI item(final ResultSet next) throws SQLException;

	private class Iter extends AbstractIterator<GI> {

		public Iter() {
			try {
				final Statement stmt = H2QISet.this.owner.conn.createStatement();
				this.item = stmt.executeQuery(H2QISet.this.table.toString());
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
				final GI item = item(this.item);
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

		private final ResultSet item;

		private boolean next;

	}

}
