package bee.creative.qs.h2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.lang.Strings;
import bee.creative.qs.QN;
import bee.creative.qs.QT;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert ein {@link QT} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QT implements QT, UseToString {

	final H2QS owner;

	final int[] keys;

	H2QT(final H2QS owner, final int... keys) {
		this.owner = owner;
		this.keys = keys;
	}

	@Override
	public H2QN get(final int index) throws IndexOutOfBoundsException {
		return this.owner.newNode(this.keys[index]);
	}

	@Override
	public int size() {
		return this.keys.length;
	}

	@Override
	public H2QS owner() {
		return this.owner;
	}

	@Override
	public Iterator<QN> iterator() {
		return Iterators.fromArray(this, 0, this.size());
	}

	@Override
	public int hashCode() {
		int result = Objects.hashInit();
		for (final int node: this.keys) {
			result = Objects.hashPush(result, node);
		}
		return result;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QT)) return false;
		final H2QT that = (H2QT)object;
		if (!Arrays.equals(this.keys, that.keys)) return false;
		if (this.owner != that.owner) return false;
		return true;
	}

	@Override
	public List<QN> toList() {
		final List<QN> result = new ArrayList<>(this.size());
		Iterables.addAll(result, this);
		return result;
	}

	@Override
	public QN[] toArray() {
		final int size = this.size();
		final QN[] res = new QN[size];
		for (int i = 0; i < size; i++) {
			res[i] = this.get(i);
		}
		return res;
	}

	@Override
	public String toString() {
		return "(" + Strings.join(" ", this) + ")";
	}

}
