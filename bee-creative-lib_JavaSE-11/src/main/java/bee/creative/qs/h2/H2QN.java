package bee.creative.qs.h2;

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
			var stmt1 = this.owner.popQN;
			stmt1.setLong(1, this.key);
			var stmt2 = this.owner.popQV;
			stmt2.setLong(1, this.key);
			return (stmt1.executeUpdate() != 0) | this.owner.markPopValue(stmt2.executeUpdate() != 0);
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean state() {
		return (this.value() != null) || !this.owner.edges.havingNode(this).isEmpty();
	}

	@Override
	public String value() {
		try {
			var stmt = this.owner.getQV;
			stmt.setLong(1, this.key);
			try (var rset = stmt.executeQuery()) {
				return rset.next() ? rset.getString(1) : null;
			}
		} catch (SQLException cause) {
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
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QN)) return false;
		var that = (H2QN)object;
		return (this.key == that.key) && (this.owner == that.owner);
	}

	@Override
	public String toString() {
		var value = this.value();
		return value == null ? Long.toString(this.key) : Objects.toString(value);
	}

	H2QN(H2QS owner, long key) {
		this.owner = owner;
		this.key = key;
	}

}
