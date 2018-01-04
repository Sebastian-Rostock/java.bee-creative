package bee.creative.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit {@link #HashMap(boolean) Streuwertpuffer} und mit Kapazität {@code 0}. */
	public HashMap() {
		this(true);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit Kapazität {@code 0}.
	 *
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashMap(final boolean withHashes) {
		super(true, withHashes);
	}

	{}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param entry Eintrag.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insert(final Entry<? extends GKey, ? extends GValue> entry) {
		return this.insertImpl(entry);
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insert(final GKey key, final GValue value) {
		this.putImpl(key, value);
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param entry Eintrag.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertImpl(final Entry<? extends GKey, ? extends GValue> entry) {
		this.putImpl(entry.getKey(), entry.getValue());
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung des Schlüssels zum gegebenen Wert des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertKeyFromImpl(final Getter<? super GValue, ? extends GKey> key, final GValue value) {
		this.putImpl(key.get(value), value);
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung des Werts zum gegebenen Schlüssel des Eintrags.
	 * @param key Schlüssel des Eintrags.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertValueFromImpl(final Getter<? super GKey, ? extends GValue> value, final GKey key) {
		this.putImpl(key, value.get(key));
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param entries Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final Map<? extends GKey, ? extends GValue> entries) {
		return this.insertAll(entries.entrySet());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param entries Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final Iterator<? extends Entry<? extends GKey, ? extends GValue>> entries) {
		return this.insertAllImpl(entries);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param entries Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
		return this.insertAllImpl(entries.iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final GKey[] keys, final GValue[] values) {
		return this.insertAllImpl(Arrays.asList(keys).iterator(), Arrays.asList(values).iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final Iterator<? extends GKey> keys, final Iterator<? extends GValue> values) {
		return this.insertAllImpl(keys, values);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAll(final Iterable<? extends GKey> keys, final Iterable<? extends GValue> values) {
		return this.insertAllImpl(keys.iterator(), values.iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung des Schlüssels zum gegebenen Wert des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertKeyFrom(final Getter<? super GValue, ? extends GKey> key, final GValue value) {
		return this.insertKeyFromImpl(key, value);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, @SuppressWarnings ("unchecked") final GValue... values) {
		return this.insertAllKeysFromImpl(key, Arrays.asList(values).iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, final Iterator<? extends GValue> values) {
		return this.insertAllKeysFromImpl(key, values);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, final Iterable<? extends GValue> values) {
		return this.insertAllKeysFromImpl(key, values.iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung des Werts zum gegebenen Schlüssel des Eintrags.
	 * @param key Schlüssel des Eintrags.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertValueFrom(final Getter<? super GKey, ? extends GValue> value, final GKey key) {
		return this.insertValueFromImpl(value, key);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, @SuppressWarnings ("unchecked") final GKey... keys) {
		return this.insertAllValuesFromImpl(value, Arrays.asList(keys).iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, final Iterator<? extends GKey> keys) {
		return this.insertAllValuesFromImpl(value, keys);
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> insertAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, final Iterable<? extends GKey> keys) {
		return this.insertAllValuesFromImpl(value, keys.iterator());
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param entries Einträge.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertAllImpl(final Iterator<? extends Entry<? extends GKey, ? extends GValue>> entries) {
		while (entries.hasNext()) {
			this.insertImpl(entries.next());
		}
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertAllImpl(final Iterator<? extends GKey> keys, final Iterator<? extends GValue> values) {
		while (keys.hasNext() && values.hasNext()) {
			this.insert(keys.next(), values.next());
		}
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
	 * @param values Werte der Einträge.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertAllKeysFromImpl(final Getter<? super GValue, ? extends GKey> key, final Iterator<? extends GValue> values) {
		while (values.hasNext()) {
			this.insertKeyFromImpl(key, values.next());
		}
		return this;
	}

	/** Diese Methode {@link #put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
	 *
	 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> insertAllValuesFromImpl(final Getter<? super GKey, ? extends GValue> value, final Iterator<? extends GKey> keys) {
		while (keys.hasNext()) {
			this.insertValueFromImpl(value, keys.next());
		}
		return this;
	}

	/** Diese Methode {@link #remove(Object) entfern den Eintrag mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
	 *
	 * @param key Schlüssel.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> delete(final Object key) {
		this.popImpl(key);
		return this;
	}

	/** Diese Methode {@link #remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> deleteAll(final Object... keys) {
		return this.deleteAllImpl(Arrays.asList(keys).iterator());
	}

	/** Diese Methode {@link #remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> deleteAll(final Iterator<?> keys) {
		return this.deleteAllImpl(keys);
	}

	/** Diese Methode {@link #remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
	 *
	 * @param keys Schlüssel der Einträge.
	 * @return {@code this}. */
	public HashMap<GKey, GValue> deleteAll(final Iterable<?> keys) {
		return this.deleteAllImpl(keys.iterator());
	}

	/** Diese Methode {@link #popKeyImpl(Object) entfern die Einträge mit den gegebenen Schlüsseln} und gibt {@code this} zurück.
	 *
	 * @param Keys Schlüssel.
	 * @return {@code this}. */
	protected final HashMap<GKey, GValue> deleteAllImpl(final Iterator<?> Keys) {
		while (Keys.hasNext()) {
			this.popImpl(Keys.next());
		}
		return this;
	}

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
		if (value == null) {
			for (final Iterator<GValue> iterator = this.newValuesIteratorImpl(); iterator.hasNext();) {
				if (iterator.next() == null) return true;
			}
		} else {
			for (final Iterator<GValue> iterator = this.newValuesIteratorImpl(); iterator.hasNext();) {
				if (value.equals(iterator.next())) return true;
			}
		}
		return false;
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
		this.insertAll(map);
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
		return this.newMappingImpl().hashCode();
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