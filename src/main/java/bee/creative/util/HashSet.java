package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
 * Die nachfolgende Tabelle zeigt den Vergleich der genäherten Speicherbelegung (32 Bit). Die relativen Rechenzeiten wurden für {@link Random#nextInt(int)
 * zufällig} gemischte {@link Integer} ermittelt.
 * <p>
 * <table border="1" cellspacing="0" cellpadding="4">
 * <tr>
 * <th rowspan="2">Klasse</th>
 * <th colspan="4">Speicherverbrauch</th>
 * <th colspan="3">Rechenzeit ({@link #size()} = 4K)</th>
 * <th colspan="3">Rechenzeit ({@link #size()} = 4M)</th>
 * </tr>
 * <tr>
 * <th>{@link #size()} = 0</th>
 * <th>{@link #size()} = 1..500M</th>
 * <th>{@link #size()} = 500M..1G</th>
 * <th>{@link #size()} = 1G..2G</th>
 * <th>{@link #add(Object) add}</th>
 * <th>{@link #contains(Object) contains}</th>
 * <th>{@link #remove(Object) remove}</th>
 * <th>{@link #add(Object) add}</th>
 * <th>{@link #contains(Object) contains}</th>
 * <th>{@link #remove(Object) remove}</th>
 * </tr>
 * <tr>
 * <td>{@link java.util.HashSet}<br>
 * Java 1.7</td>
 * <td>88..136</td>
 * <td colspan="2">{@link #size()} * 36 + 80..128 Byte (= 100 %)</td>
 * <td>{@link #size()} * 34..36 + 80..128 Byte (~ 95..100 %)</td>
 * <td>(= 100 %)</td>
 * <td>(~ 50 %)</td>
 * <td>(~ 67 %)</td>
 * <td>(= 100 %)</td>
 * <td>(~ 98 %)</td>
 * <td>(~ 78 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet}<br>
 * mit Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>{@link #size()} * 16 + 104 Byte (~ 44 %)</td>
 * <td colspan="2">{@link #size()} * 13..16 + 104 Byte (~ 36..44 %)</td>
 * <td>(~ 57 %)</td>
 * <td>(~ 48 %)</td>
 * <td>(~ 57 %)</td>
 * <td>(~ 100 %)</td>
 * <td>(~ 100 %)</td>
 * <td>(~ 69 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashSet}<br>
 * ohne Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>{@link #size()} * 12 + 88 Byte (~33%)</td>
 * <td colspan="2">{@link #size()} * 9..12 + 88 Byte (~25..33%)</td>
 * <td>(~ 55 %)</td>
 * <td>(~ 43 %)</td>
 * <td>(~ 55 %)</td>
 * <td>(~ 95 %)</td>
 * <td>(~ 93 %)</td>
 * <td>(~ 69 %)</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet<GItem> extends HashData<GItem, GItem> implements Set<GItem>, Serializable {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = 1670340423693309390L;

	{}

	/** Diese Methode gibt ein {@link HashSet} zurück, welches zur Ermittlung von {@link #customHash(Object) Streuwerte} und {@link #customEquals(Object, Object)
	 * Äquivalenz} der Elemente den gegebenne {@link Hasher} einsetzt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Elemente.
	 * @return {@link HashSet}. */
	public static <GItem> HashSet<GItem> from(final Hasher<? super Object> hasher) {
		return new HashSet<GItem>() {

			private static final long serialVersionUID = 5244025979236120957L;

			@Override
			protected int customHash(final Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEquals(final Object thisKey, final Object thatKey) {
				return hasher.equals(thisKey, thatKey);
			}

		};
	}

	/** Diese Methode gibt ein {@link HashSet} zurück, welches nur die vom gegebenen {@link Filter} akzeptierten Elemente zulässt und zur Ermittlung von
	 * {@link #customHash(Object) Streuwerte} und {@link #customEquals(Object, Object) Äquivalenz} der Elemente den gegebenne {@link Hasher} einsetzt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter} zur Erkennugn der akzeptierten Elemente, welche als {@code GKey} interpretiert werden können.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Elemente.
	 * @return {@link HashSet}. */
	public static <GItem> HashSet<GItem> from(final Filter<Object> filter, final Hasher<? super GItem> hasher) {
		return new HashSet<GItem>() {

			private static final long serialVersionUID = 1333366283086289645L;

			@Override
			@SuppressWarnings ("unchecked")
			protected int customHash(final Object key) {
				if (!filter.accept(key)) return 0;
				return hasher.hash((GItem)key);
			}

			@Override
			@SuppressWarnings ("unchecked")
			protected boolean customEquals(final Object thisKey, final Object thatKey) {
				if (!filter.accept(thatKey)) return false;
				return hasher.equals((GItem)thisKey, (GItem)thatKey);
			}

		};
	}

	{}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashSet(true, 0)}. */
	public HashSet() {
		this(true, 0);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashSet(withHashes, 0)}. */
	@SuppressWarnings ("javadoc")
	public HashSet(final boolean withHashes) {
		this(withHashes, 0);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashSet(true, capacity)}. */
	@SuppressWarnings ("javadoc")
	public HashSet(final int capacity) {
		this(true, capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit Kapazität {@code 0}.
	 *
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann.
	 * @param capacity Kapazität. */
	public HashSet(final boolean withHashes, final int capacity) {
		super(false, withHashes);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashSet(true, source)}. */
	@SuppressWarnings ("javadoc")
	public HashSet(final Set<? extends GItem> source) {
		this(true, source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} als Kopie des gegebenen {@link Set}.
	 *
	 * @see #HashSet(boolean, int)
	 * @param withHashes Aktivierung des Streuwertpuffers.
	 * @param source gegebene Elemente. */
	public HashSet(final boolean withHashes, final Set<? extends GItem> source) {
		this(withHashes, source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashSet(true, source)}. */
	@SuppressWarnings ("javadoc")
	public HashSet(final Collection<? extends GItem> source) {
		this(true, source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit den gegebenen Elementen.
	 *
	 * @see #HashSet(boolean, int)
	 * @param withHashes Aktivierung des Streuwertpuffers.
	 * @param source gegebene Elemente. */
	public HashSet(final boolean withHashes, final Collection<? extends GItem> source) {
		this(withHashes);
		this.addAll(source);
	}

	{}

	@SuppressWarnings ({"unchecked", "javadoc"})
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		this.nexts = stream.readBoolean() ? HashData.EMPTY_INTEGERS : null;
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			this.putIndexImpl((GItem)stream.readObject());
		}
	}

	@SuppressWarnings ("javadoc")
	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeBoolean(this.nexts != null);
		stream.writeInt(this.count);
		for (final GItem item: this) {
			stream.writeObject(item);
		}
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann, und gibt {@code this} zurück.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public HashSet<GItem> allocate(final int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
		return this;
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public HashSet<GItem> compact() {
		this.allocateImpl(this.countImpl());
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.countImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.countImpl() == 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final Object o) {
		return this.hasKeyImpl(o);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> iterator() {
		return this.newKeysIteratorImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(final GItem e) {
		return this.putKeyImpl(e);
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object o) {
		return this.popKeyImpl(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return Iterators.containsAll(this, c.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends GItem> c) {
		return Iterators.addAll(this, c.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(final Collection<?> c) {
		return Iterators.retainAll(this.iterator(), c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.newKeysImpl().removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearImpl();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.newKeysImpl().hashCode();
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
		return this.newKeysImpl().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(final T[] a) {
		return this.newKeysImpl().toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.newKeysImpl().toString();
	}

}