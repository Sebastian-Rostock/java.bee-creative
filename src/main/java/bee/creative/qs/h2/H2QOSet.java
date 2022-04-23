package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import bee.creative.qs.QOSet;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QOSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public abstract class H2QOSet<GI, GISet extends Iterable<GI>> implements QOSet<GI, GISet> {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	/** Dieses Feld speichert die Anfrage zur Ermittlung der Einträgen dieser Menge, inklusive des ggf. vorhandenen Namen der {@code TABLE}. */
	public final H2QQ table;

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage (oder {@code null}). */
	protected H2QOSet(final H2QS owner, final H2QQ table) {
		this.owner = owner;
		this.table = table != null ? table : new H2QQ(owner);
	}

	/** Diese Methode liefert das Objekt zum gegebenen {@link ResultSet}. */
	protected abstract GI next(final ResultSet next) throws SQLException;

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
			return rset.next() ? this.next(rset) : null;
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
	public Iterator<GI> iterator() {
		return new H2QOIter<>(this);
	}

	@Override
	public HashSet2<GI> toSet() {
		final HashSet2<GI> result = new HashSet2<>(100);
		Iterables.addAll(result, this);
		return result;
	}

	@Override
	public ArrayList<GI> toList() {
		final ArrayList<GI> result = new ArrayList<>(100);
		Iterables.addAll(result, this.order());
		return result;
	}

	@Override
	public String toString() {
		return this.toList().toString();
	}

}
