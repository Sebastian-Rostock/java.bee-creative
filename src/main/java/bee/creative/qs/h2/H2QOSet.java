package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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
	protected final H2QS owner;

	/** Dieses Feld speichert den Namen der {@code TABLE} bzw. des {@code VIEW} mit den Einträgen dieser Menge. */
	protected final String name;

	/** Dieses Feld speichert {@code true}, wenn {@link #name} für einen {@code VIEW} bzw. {@code false}, wenn er für eine temporäre {@code TABLE} steht. */
	protected final boolean view;

	/** @param select Anfrage des {@code VIEW} oder {@code null}. */
	H2QOSet(final H2QS owner, final String select) {
		this.owner = owner;
		this.name = "QT" + owner.newQK(owner.createTempKey);
		this.view = select != null;
		if (!this.view) return;
		owner.exec("create view " + this.name + " as " + select);
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.view) {
			this.owner.exec("drop view if exists " + this.name + " cascade");
		} else {
			this.owner.exec("drop table if exists " + this.name + " cascade");
		}
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
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public boolean hasAny() {
		try (final ResultSet rset = this.owner.exec.executeQuery("select top 1 1 from " + this.name)) {
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
