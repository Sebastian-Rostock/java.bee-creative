package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/** Diese Klasse implementiert ein auf {@link HashData} aufbauendes {@link Set}.
 * <p>
 * <b>Achtung:<br>
 * Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Elemente erfolgt nicht wie in {@link Set}
 * beschrieben über Methoden der Elemente, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEquals(Object, Object)} dieses
 * {@link HashSet}.</b>
 * <p>
 * Die nachfolgende Tabelle zeigt den Vergleich der genäherten Speicherbelegung (32 Bit). Die relativen Rechenzeiten wurden zu 2<sup>18</sup>
 * {@link Random#nextInt(int) zufälligen} {@link Integer} ermittelt.
 * <p>
 * <table border="1" cellspacing="0" cellpadding="4">
 * <tr>
 * <th>CLASS</th>
 * <th>SIZE = 0</th>
 * <th>SIZE = 2<sup>1..29</sup></th>
 * <th>{@link #add(Object) add}-TIME</th>
 * <th>{@link #contains(Object) contains}-TIME</th>
 * <th>{@link #remove(Object) remove}-TIME</th>
 * </tr>
 * <tr>
 * <td>{@link java.util.HashSet}</td>
 * <td>88..136 Byte</td>
 * <td>SIZE x 36 + 80..128 Byte</td>
 * <td>100%</td>
 * <td>100%</td>
 * <td>100%</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet} mit Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>SIZE x 16 + 104 Byte</td>
 * <td>70%</td>
 * <td>50%</td>
 * <td>50%</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet} ohne Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>SIZE x 12 + 88 Byte</td>
 * <td>60%</td>
 * <td>40%</td>
 * <td>45%</td>
 * </tr>
 * </table>
 * </p>
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

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit Kapazität {@code 0}.
	 *
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashSet(final boolean withHashes) {
		super(false, withHashes);
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

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit den gegebenen Elemente.
	 *
	 * @param source Einträge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public HashSet(final Collection<? extends GItem> source) throws NullPointerException {
		this(source.size());
		this.addAll(source);
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
		return this.getKeys().hashCode();
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
