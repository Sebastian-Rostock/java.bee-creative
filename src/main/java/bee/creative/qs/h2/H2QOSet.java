package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.qs.QOSet;
import bee.creative.qs.QXSet;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QXSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public abstract class H2QOSet<GI, GISet extends Iterable<GI>> implements QOSet<GI, GISet> {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	/** Dieses Feld speichert den Namen der {@code TABLE} bzw. des {@code VIEW} mit den Einträgen dieser Menge. */
	public final String name;

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QOSet(final H2QS owner, final String select) {
		this.owner = owner;
		this.name = "QT" + owner.newKey(owner.createTemp);
		this.owner.insertQOSet(this.name, select);
	}

	@Override
	protected void finalize() throws Throwable {
		this.owner.deleteQOSet(this.name);
	}

	/** Diese Methode liefert das Objekt zum gegebenen {@link ResultSet}. */
	protected abstract GI next(final ResultSet next) throws SQLException;

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public long size() {
		try (ResultSet rset = this.owner.exec.executeQuery("select count(*) from " + this.name)) {
			return rset.next() ? rset.getLong(1) : 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public GI first() {
		try (final ResultSet rset = this.owner.exec.executeQuery("select top 1 1 from " + this.name)) {
			return rset.next() ? this.next(rset) : null;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean isEmpty() {
		try (final ResultSet rset = this.owner.exec.executeQuery("select top 1 1 from " + this.name)) {
			return !rset.next();
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public Iterator<GI> iterator() {
		return new H2QOIter<>(this);
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
