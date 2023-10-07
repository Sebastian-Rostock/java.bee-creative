package bee.creative.qs.h2;

import java.util.Arrays;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.lang.Strings;
import bee.creative.qs.QN;
import bee.creative.qs.QT;
import bee.creative.util.Iterables;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert ein {@link QT} mit Bezug zu einer Datenbank.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class H2QT implements QT, UseToString {

	/** Dieses Feld speichert den Graphspeicher mit {@link H2QS#conn Datenbankverbindung}. */
	public final H2QS owner;

	/** Dieses Feld speichert die Kennungen der Hyperknoten. */
	public final long[] keys;

	@Override
	public H2QN get(int index) throws IndexOutOfBoundsException {
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
	public Iterator2<QN> iterator() {
		return Iterators.fromArray(this, 0, this.size());
	}

	@Override
	public int hashCode() {
		var result = Objects.hashInit();
		for (var key: this.keys) {
			result = Objects.hashPush(result, Objects.hash(key));
		}
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof H2QT)) return false;
		var that = (H2QT)object;
		if (!Arrays.equals(this.keys, that.keys) || (this.owner != that.owner)) return false;
		return true;
	}

	@Override
	public List<QN> toList() {
		return Iterables.toList(this);
	}

	@Override
	public QN[] toArray() {
		var size = this.size();
		var res = new QN[size];
		for (var i = 0; i < size; i++) {
			res[i] = this.get(i);
		}
		return res;
	}

	@Override
	public String toString() {
		return "(" + Strings.join(" ", this) + ")";
	}

	H2QT(H2QS owner, long... keys) {
		this.owner = owner;
		this.keys = keys;
	}

}
