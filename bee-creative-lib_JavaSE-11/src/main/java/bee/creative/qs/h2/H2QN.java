package bee.creative.qs.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;

/** Diese Klasse implementiert ein {@link QN} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QN implements QN {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	/** Dieses Feld speichert die Kennung dieses Hyperknoten. */
	public final long key;

	@Override
	public boolean pop() {
		try {
			final PreparedStatement stmt1 = this.owner.popQE_N, stmt2 = this.owner.popQV_N;
			stmt1.setLong(1, this.key);
			stmt2.setLong(1, this.key);
			return (stmt1.executeUpdate() != 0) | this.owner.markPopValue(stmt2.executeUpdate() != 0);
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean state() {
		return this.value() != null;
	}

	@Override
	public String value() {
		try {
			final PreparedStatement stmt = this.owner.getQV_N;
			stmt.setLong(1, this.key);
			try (final ResultSet rset = stmt.executeQuery()) {
				return rset.next() ? rset.getString(1) : null;
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QN)) return false;
		final H2QN that = (H2QN)object;
		return (this.key == that.key) && (this.owner == that.owner);
	}

	@Override
	public String toString() {
		final String value = this.value();
		return value == null ? Long.toString(this.key) : Objects.toString(value);
	}

	H2QN(final H2QS owner, final long key) {
		this.owner = owner;
		this.key = key;
	}

}
