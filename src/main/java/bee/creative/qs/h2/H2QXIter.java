package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import bee.creative.util.Iterators.BaseIterator;

/** Diese Klasse implementiert den {@link Iterator} für die Nachfahren von {@link H2QXSet}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
abstract class H2QXIter<GItem> extends BaseIterator<GItem> {

	final H2QXSet<GItem, ?> owner;

	final ResultSet item;

	boolean next;

	H2QXIter(final H2QXSet<GItem, ?> owner) {
		this.owner = owner;
		try {
			final Statement stmt = owner.owner.conn.createStatement();
			this.item = stmt.executeQuery(owner.select);
			this.next = this.item.next();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.item.getStatement().close();
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
	public GItem next() {
		try {
			final GItem item = this.next(this.item);
			this.next = this.item.next();
			return item;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	protected abstract GItem next(ResultSet next2) throws SQLException;

}