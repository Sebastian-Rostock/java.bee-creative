package bee.creative.hash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein auf {@link HashData} aufbauendes {@link Set}.<br>
 * <b>Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Elemente erfolgt nicht wie in {@link Set}
 * beschrieben über {@link Object#hashCode()} bzw. {@link Object#equals(Object)}, sondern über {@link #customHash(Object)} bzw.
 * {@link #customEquals(Object, Object)}.</b>
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet<GItem> extends HashData<GItem, GItem> implements Set<GItem> {

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit {@link #HashSet(int, boolean) Streuwertpuffer} und der Kapazität {@code 0}. */
	public HashSet() {
		super(false, true);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit {@link #HashSet(int, boolean) Streuwertpuffer} und der gegebenen Kapazität.
	 * 
	 * @param capacity Kapazität. */
	public HashSet(final int capacity) {
		super(false, true);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit der gegebenen Kapazität.
	 * 
	 * @param capacity Kapazität.
	 * @param withHashes {@code true}, wenn die Streuwerte der Elemente gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Elements schnell ermittelt werden kann. */
	public HashSet(final int capacity, final boolean withHashes) {
		super(false, withHashes);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit Kapazität {@code 0}.
	 * 
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashSet(final boolean withHashes) {
		super(false, withHashes);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.count;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.count == 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final Object o) {
		return this.getKey(o);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> iterator() {
		return this.getKeysIterator();
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(final GItem e) {
		return this.putKey(e);
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object o) {
		return this.popKey(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return Iterators.containsAll(this, c.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends GItem> c) {
		return Iterators.appendAll(this, c.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(final Collection<?> c) {
		return Iterators.retainAll(this.iterator(), c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.getKeys().removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearEntries();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int result = 0;
		for (final Object item: this) {
			result += Objects.hash(item);
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Set<?>)) return false;
		final Set<?> that = (Set<?>)object;
		if (that.size() != this.size()) return false;
		return this.containsAll(that);
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.getKeys().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(final T[] a) {
		return this.getKeys().toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getKeys().toString();
	}

}
