package bee.creative.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Diese Klasse implementiert eine auf {@link HashData} aufbauende {@link Map}.
 * <p>
 * <b>Achtung:<br>
 * Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Schlüssel erfolgt nicht wie in {@link Map}
 * beschrieben über Methoden der Schlüssel, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEquals(Object, Object)} dieser
 * {@link HashMap}.</b>
 * <p>
 * Die nachfolgende Tabelle zeigt den Vergleich der genäherten Speicherbelegung (32 Bit). Die relativen Rechenzeiten wurden zu 2<sup>18</sup>
 * {@link Random#nextInt(int) zufälligen} {@link Integer} ermittelt.
 * <p>
 * <table border="1" cellspacing="0" cellpadding="4">
 * <tr>
 * <th rowspan="2">Klasse</th>
 * <th colspan="4">Speicherverbrauch</th>
 * <th colspan="4">Rechenzeit ({@link #size()} = 4K)</th>
 * <th colspan="4">Rechenzeit ({@link #size()} = 4M)</th>
 * </tr>
 * <tr>
 * <th>{@link #size()} = 0</th>
 * <th>{@link #size()} = 1..500M</th>
 * <th>{@link #size()} = 500M..1G</th>
 * <th>{@link #size()} = 1G..2G</th>
 * <th>{@link #put(Object, Object) put}</th>
 * <th>{@link #get(Object) get}</th>
 * <th>{@link #remove(Object) remove}</th>
 * <th>{@link java.util.Map.Entry#setValue(Object) setValue}<sup>1</sup></th>
 * <th>{@link #put(Object, Object) put}</th>
 * <th>{@link #get(Object) get}</th>
 * <th>{@link #remove(Object) remove}</th>
 * <th>{@link java.util.Map.Entry#setValue(Object) setValue}<sup>1</sup></th>
 * </tr>
 * <tr>
 * <td>{@link java.util.HashMap}</td>
 * <td>72..120 Byte</td>
 * <td colspan="2">{@link #size()} * 36 + 64..112 Byte (= 100 %)</td>
 * <td>{@link #size()} * 34..36 + 64..112 Byte (~ 94..100 %)</td>
 * <td>(= 100 %)</td>
 * <td>(~ 46 %)</td>
 * <td>(~ 50 %)</td>
 * <td>(~ 27 %)</td>
 * <td>(= 100 %)</td>
 * <td>(~ 92 %)</td>
 * <td>(~ 92 %)</td>
 * <td>(~ 7 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMap}<br>
 * mit Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>{@link #size()} * 20 + 120 Byte (~ 56 %)</td>
 * <td colspan="2">{@link #size()} * 17..20 + 120 Byte (~ 47..56 %)</td>
 * <td>(~ 75 %)</td>
 * <td>(~ 58 %)</td>
 * <td>(~ 48 %)</td>
 * <td>(~ 54 %)</td>
 * <td>(~ 76 %)</td>
 * <td>(~ 72 %)</td>
 * <td>(~ 78 %)</td>
 * <td>(~ 53 %)</td>
 * </tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMap}<br>
 * ohne Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>{@link #size()} * 16 + 104 Byte (~ 44 %)</td>
 * <td colspan="2">{@link #size()} * 13..16 + 104 Byte (~ 36..44 %)</td>
 * <td>(~ 67 %)</td>
 * <td>(~ 58 %)</td>
 * <td>(~ 57 %)</td>
 * <td>(~ 61 %)</td>
 * <td>(~ 94 %)</td>
 * <td>(~ 83 %)</td>
 * <td>(~ 95 %)</td>
 * <td>(~ 53 %)</td>
 * </tr>
 * </table>
 * </p>
 * <sup>1</sup> Iteration über die {@link #entrySet() Menge der Einträge} und {@link java.util.Map.Entry#setValue(Object) Setzen des Werts} jedes
 * {@link java.util.Map.Entry Eintrags}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap<GKey, GValue> extends HashData<GKey, GValue> implements Map<GKey, GValue>, Serializable {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = -255533287357545135L;

	{}

	/** Diese Methode gibt eine {@link HashMap} zurück, welche zur Ermittlung von {@link #customHash(Object) Streuwerte} und {@link #customEquals(Object, Object)
	 * Äquivalenz} der Schlüssel den gegebenne {@link Hasher} einsetzt.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Schlüssel.
	 * @return {@link HashMap}. */
	public static <GKey, GValue> HashMap<GKey, GValue> from(final Hasher<? super Object> hasher) {
		return new HashMap<GKey, GValue>() {

			private static final long serialVersionUID = -3247304797947783260L;

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

	/** Diese Methode gibt eine {@link HashMap} zurück, welche nur die vom gegebenen {@link Filter} akzeptierten Schlüssel zulässt und zur Ermittlung von
	 * {@link #customHash(Object) Streuwerte} und {@link #customEquals(Object, Object) Äquivalenz} der Schlüssel den gegebenne {@link Hasher} einsetzt.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param filter {@link Filter} zur Erkennugn der akzeptierten Schlüssel, welche als {@code GKey} interpretiert werden können.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Schlüssel.
	 * @return {@link HashMap}. */
	public static <GKey, GValue> HashMap<GKey, GValue> from(final Filter<Object> filter, final Hasher<? super GKey> hasher) {
		return new HashMap<GKey, GValue>() {

			private static final long serialVersionUID = -583615691658086667L;

			@Override
			@SuppressWarnings ("unchecked")
			protected int customHash(final Object key) {
				if (!filter.accept(key)) return 0;
				return hasher.hash((GKey)key);
			}

			@Override
			@SuppressWarnings ("unchecked")
			protected boolean customEquals(final Object thisKey, final Object thatKey) {
				if (!filter.accept(thatKey)) return false;
				return hasher.equals((GKey)thisKey, (GKey)thatKey);
			}

		};
	}

	{}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashMap(true, 0)}. */
	public HashMap() {
		this(true, 0);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashMap(true, capacity)}. */
	@SuppressWarnings ("javadoc")
	public HashMap(final int capacity) {
		this(true, 0);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashMap(withHashes, 0)}. */
	@SuppressWarnings ("javadoc")
	public HashMap(final boolean withHashes) {
		this(withHashes, 0);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit der gegebenen Kapazität.
	 *
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann.
	 * @param capacity Kapazität. */
	public HashMap(final boolean withHashes, final int capacity) {
		super(true, withHashes);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@code new HashMap(true, source)}. */
	@SuppressWarnings ("javadoc")
	public HashMap(final Map<? extends GKey, ? extends GValue> source) {
		this(true, source);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} als Kopie der gegebenen {@link Map}.
	 *
	 * @see #HashMap(boolean, int)
	 * @param withHashes Aktivierung des Streuwertpuffers.
	 * @param source gegebene Einträge. */
	public HashMap(final boolean withHashes, final Map<? extends GKey, ? extends GValue> source) {
		this(withHashes, source.size());
		this.putAll(source);
	}

	{}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann, und gibt {@code this} zurück.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public HashMap<GKey, GValue> allocate(final int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
		return this;
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public HashMap<GKey, GValue> compact() {
		return this.allocate(this.countImpl());
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
	public boolean containsKey(final Object key) {
		return this.hasKeyImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		return this.hasValueImpl(value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final Object key) {
		return this.getImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public GValue put(final GKey key, final GValue value) {
		return this.putImpl(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue remove(final Object key) {
		return this.popImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> map) {
		for (final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()) {
			this.putImpl(entry.getKey(), entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Set<GKey> keySet() {
		return this.newKeysImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<GValue> values() {
		return this.newValuesImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return this.newEntriesImpl();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.newEntriesImpl().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		final Map<?, ?> that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (final Entry<?, ?> entry: that.entrySet()) {
			final int entryIndex = this.getIndexImpl(entry.getKey());
			if (entryIndex < 0) return false;
			if (!Objects.equals(this.getValueImpl(entryIndex), entry.getValue())) return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.newMappingImpl().toString();
	}

}