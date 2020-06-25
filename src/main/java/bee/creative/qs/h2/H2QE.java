package bee.creative.qs.h2;

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
		return new H2QN(this.owner, this.context);
	}

	@Override
	public H2QN predicate() {
		return new H2QN(this.owner, this.predicate);
	}

	@Override
	public H2QN subject() {
		return new H2QN(this.owner, this.subject);
	}

	@Override
	public H2QN object() {
		return new H2QN(this.owner, this.object);
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public boolean put() {
		return this.owner.putImpl(this);
	}

	@Override
	public boolean pop() {
		return this.owner.popImpl(this);
	}

	@Override
	public boolean state() {
		return this.owner.stateImpl(this);
	}

	@Override
	public H2QE withContext(final QN context) throws NullPointerException, IllegalArgumentException {
		return new H2QE(this.owner, this.owner.asQN(context).key, this.predicate, this.subject, this.object);
	}

	@Override
	public H2QE withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException {
		return new H2QE(this.owner, this.context, this.owner.asQN(predicate).key, this.subject, this.object);
	}

	@Override
	public H2QE withSubject(final QN subject) throws NullPointerException, IllegalArgumentException {
		return new H2QE(this.owner, this.context, this.predicate, this.owner.asQN(subject).key, this.object);
	}

	@Override
	public H2QE withObject(final QN object) throws NullPointerException, IllegalArgumentException {
		return new H2QE(this.owner, this.context, this.predicate, this.subject, this.owner.asQN(object).key);
	}

	@Override
	public int hashCode() {
		return this.context ^ this.predicate ^ this.subject ^ this.object;
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
