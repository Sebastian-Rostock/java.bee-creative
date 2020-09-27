package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import bee.creative.qs.QXSet;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QXSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public abstract class H2QXSet<GI, GISet extends Iterable<GI>> implements QXSet<GI, GISet> {

	final H2QS owner;

	/** Dieses Feld speichert die SQL-Anfrage zur Ermittlung der Tabelle. */
	protected final String select;

	H2QXSet(final H2QS owner, final String select) {
		this.owner = owner;
		this.select = select;
	}

	@Override
	public long size() {
		try (final Statement stmt = this.owner.conn.createStatement(); final ResultSet rset = stmt.executeQuery(H2QQ.selectSize(this))) {
			return rset.next() ? rset.getLong(1) : 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public boolean hasAny() {
		try (final Statement stmt = this.owner.conn.createStatement(); final ResultSet rset = stmt.executeQuery(H2QQ.selectAny(this))) {
			return rset.next();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public Set<GI> toSet() {
		final Set<GI> result = new HashSet<>();
		Iterables.addAll(result, this);
		return result;
	}

	@Override
	public List<GI> toList() {
		final ArrayList<GI> result = new ArrayList<>();
		Iterables.addAll(result, this.order());
		result.trimToSize();
		return result;
	}

	@Override
	public String toString() {
		return this.toList().toString();
	}

}
