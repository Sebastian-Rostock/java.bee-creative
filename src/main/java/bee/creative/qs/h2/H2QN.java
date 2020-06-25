package bee.creative.qs.h2;

import bee.creative.lang.Objects;
import bee.creative.qs.QN;

/** Diese Klasse implementiert ein {@link QN} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QN implements QN {

	final H2QS owner;

	final int key;

	H2QN(final H2QS owner, final int key) {
		this.owner = owner;
		this.key = key;
	}

	@Override
	public long key() {
		return this.key;
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
	public String value() {
		return this.owner.valueImpl(this);
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public int hashCode() {
		return this.key;
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
		return value == null ? Integer.toString(this.key) : Objects.toString(value);
	}

}
