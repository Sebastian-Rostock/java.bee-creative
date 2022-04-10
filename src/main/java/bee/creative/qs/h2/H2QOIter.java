package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import bee.creative.qs.QO;
import bee.creative.util.AbstractIterator;

/** Diese Klasse implementiert den {@link Iterator} für {@link H2QOSet}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Elemente. */
class H2QOIter<GI, GISet extends H2QOSet<GI, ?>> extends AbstractIterator<GI> implements QO {

	final GISet owner;

	final ResultSet item;

	boolean next;

	H2QOIter(final GISet owner) {
		this.owner = owner;
		try {
			final Statement stmt = owner.owner.conn.createStatement();
			this.item = stmt.executeQuery("select * from " + owner.name);
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
	public H2QS owner() {
		return this.owner.owner;
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
			final GI item = this.owner.next(this.item);
			this.next = this.item.next();
			return item;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

}