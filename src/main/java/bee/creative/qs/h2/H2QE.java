package bee.creative.qs.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;

/** Diese Klasse implementiert ein {@link QE} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QE implements QE {

	final H2QS owner;

	final int context;

	final int predicate;

	final int subject;

	final int object;

	H2QE(final H2QS owner, final int context, final int predicate, final int subject, final int object) {
		this.owner = owner;
		this.context = context;
		this.predicate = predicate;
		this.subject = subject;
		this.object = object;
	}

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
			final PreparedStatement stmt = this.owner.insertSaveEdge;
			stmt.setInt(1, this.context);
			stmt.setInt(2, this.predicate);
			stmt.setInt(3, this.subject);
			stmt.setInt(4, this.object);
			return stmt.executeUpdate() != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean pop() {
		try {
			final PreparedStatement stmt = this.owner.deleteSaveEdge;
			stmt.setInt(1, this.context);
			stmt.setInt(2, this.predicate);
			stmt.setInt(3, this.subject);
			stmt.setInt(4, this.object);
			return stmt.executeUpdate() != 0;
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public boolean state() {
		try {
			final PreparedStatement stmt = this.owner.selectSaveEdge;
			stmt.setInt(1, this.context);
			stmt.setInt(2, this.predicate);
			stmt.setInt(3, this.subject);
			stmt.setInt(4, this.object);
			try (final ResultSet rset = stmt.executeQuery()) {
				return rset.next();
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QE withContext(final QN context) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.owner.asQN(context).key, this.predicate, this.subject, this.object);
	}

	@Override
	public H2QE withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.owner.asQN(predicate).key, this.subject, this.object);
	}

	@Override
	public H2QE withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.predicate, this.owner.asQN(subject).key, this.object);
	}

	@Override
	public H2QE withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		return this.owner.newEdge(this.context, this.predicate, this.subject, this.owner.asQN(object).key);
	}

	@Override
	public int hashCode() {
		int result = Objects.hashInit();
		result = Objects.hashPush(result, this.context);
		result = Objects.hashPush(result, this.predicate);
		result = Objects.hashPush(result, this.subject);
		result = Objects.hashPush(result, this.object);
		return result;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QE)) return false;
		final H2QE that = (H2QE)object;
		if (this.object != that.object) return false;
		if (this.subject != that.subject) return false;
		if (this.predicate != that.predicate) return false;
		if (this.context != that.context) return false;
		if (this.owner != that.owner) return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + this.context() + " " + this.predicate() + " " + this.subject() + " " + this.object() + ")";
	}

}
