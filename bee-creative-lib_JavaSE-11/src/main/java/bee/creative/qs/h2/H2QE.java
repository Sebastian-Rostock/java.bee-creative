package bee.creative.qs.h2;

import java.sql.SQLException;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;

/** Diese Klasse implementiert ein {@link QE} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QE implements QE {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	/** Dieses Feld speichert die Kennung des Kontextknoten. */
	public final long context;

	/** Dieses Feld speichert die Kennung des Prädikatknoten. */
	public final long predicate;

	/** Dieses Feld speichert die Kennung des Subjektknoten. */
	public final long subject;

	/** Dieses Feld speichert die Kennung des Objektknoten. */
	public final long object;

	@Override
	public H2QN context() {
		return this.owner.newNode(this.context);
	}

	@Override
	public H2QN predicate() {
		return this.owner.newNode(this.predicate);
	}

	@Override
	public H2QN subject() {
		return this.owner.newNode(this.subject);
	}

	@Override
	public H2QN object() {
		return this.owner.newNode(this.object);
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public boolean put() {
		try {
			var stmt = this.owner.putQE;
			stmt.setLong(1, this.context);
			stmt.setLong(2, this.predicate);
			stmt.setLong(3, this.subject);
			stmt.setLong(4, this.object);
			return stmt.executeUpdate() != 0;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean pop() {
		try {
			var stmt = this.owner.popQE;
			stmt.setLong(1, this.context);
			stmt.setLong(2, this.predicate);
			stmt.setLong(3, this.subject);
			stmt.setLong(4, this.object);
			return stmt.executeUpdate() != 0;
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean state() {
		try {
			var stmt = this.owner.getQE;
			stmt.setLong(1, this.context);
			stmt.setLong(2, this.predicate);
			stmt.setLong(3, this.subject);
			stmt.setLong(4, this.object);
			try (var rset = stmt.executeQuery()) {
				return rset.next();
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QE withContext(QN context) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.owner.asQN(context).key, this.predicate, this.subject, this.object);
	}

	@Override
	public H2QE withPredicate(QN predicate) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.owner.asQN(predicate).key, this.subject, this.object);
	}

	@Override
	public H2QE withSubject(QN subject) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.predicate, this.owner.asQN(subject).key, this.object);
	}

	@Override
	public H2QE withObject(QN object) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.predicate, this.subject, this.owner.asQN(object).key);
	}

	@Override
	public int hashCode() {
		var result = Objects.hashInit();
		result = Objects.hashPush(result, Objects.hash(this.context));
		result = Objects.hashPush(result, Objects.hash(this.predicate));
		result = Objects.hashPush(result, Objects.hash(this.subject));
		result = Objects.hashPush(result, Objects.hash(this.object));
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QE)) return false;
		var that = (H2QE)object;
		if ((this.object != that.object) || (this.subject != that.subject) || (this.predicate != that.predicate) || (this.context != that.context)) return false;
		if (this.owner != that.owner) return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + this.context() + " " + this.predicate() + " " + this.subject() + " " + this.object() + ")";
	}

	H2QE(H2QS owner, long context, long predicate, long subject, long object) {
		this.owner = owner;
		this.context = context;
		this.predicate = predicate;
		this.subject = subject;
		this.object = object;
	}

}
