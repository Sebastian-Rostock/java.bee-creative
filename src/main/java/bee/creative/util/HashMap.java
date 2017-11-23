package bee.creative.util;

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
 * <th>CLASS</th>
 * <th>SIZE = 0</th>
 * <th>SIZE = 2<sup>1..29</sup></th>
 * <th>{@link #put(Object, Object) put}-TIME</th>
 * <th>{@link #get(Object) get}-TIME</th>
 * <th>{@link #remove(Object) pop}-TIME</th>
 * <th>{@link java.util.Map.Entry#setValue(Object) set}-TIME</th>
 * </tr>
 * <td>{@link java.util.HashMap}</td>
 * <td>72..120 Byte</td>
 * <td>SIZE x 36 + 64..112 Byte</td>
 * <td>100%</td>
 * <td>100%</td>
 * <td>100%</td>
 * <td>100%</td>
 * </tr>
 * <tr>
 * <tr>
 * <td>{@link bee.creative.util.HashMap} mit Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>SIZE x 20 + 120 Byte</td>
 * <td>75%</td>
 * <td>60%</td>
 * <td>55%</td>
 * <td>275%</td>
 * </tr>
 * <td>{@link bee.creative.util.HashMap} ohne Streuwertpuffer</td>
 * <td>40 Byte</td>
 * <td>SIZE x 16 + 104 Byte</td>
 * <td>65%</td>
 * <td>50%</td>
 * <td>50%</td>
 * <td>300%</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap<GKey, GValue> extends HashData<GKey, GValue> implements Map<GKey, GValue> {

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit {@link #HashMap(int, boolean) Streuwertpuffer} und der Kapazität {@code 0}. */
	public HashMap() {
		super(true, true);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit {@link #HashMap(int, boolean) Streuwertpuffer} und der gegebenen Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap(final int capacity) {
		super(true, true);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit Kapazität {@code 0}.
	 *
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashMap(final boolean withHashes) {
		super(true, withHashes);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit der gegebenen Kapazität.
	 *
	 * @param capacity Kapazität.
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashMap(final int capacity, final boolean withHashes) {
		super(true, withHashes);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit den gegebenen Einträgen.
	 *
	 * @param source Einträge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public HashMap(final Map<? extends GKey, ? extends GValue> source) throws NullPointerException {
		this(source.size());
		this.putAll(source);
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
		return this.getKey(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		if (value == null) {
			for (final Iterator<GValue> iterator = this.getValuesIterator(); iterator.hasNext();) {
				if (iterator.next() == null) return true;
			}
		} else {
			for (final Iterator<GValue> iterator = this.getValuesIterator(); iterator.hasNext();) {
				if (value.equals(iterator.next())) return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final Object key) {
		return this.getValue(key);
	}

	/** {@inheritDoc} */
	@Override
	public GValue put(final GKey key, final GValue value) {
		return this.putValue(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue remove(final Object key) {
		return this.popValue(key);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> map) {
		for (final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()) {
			this.putValue(entry.getKey(), entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearEntries();
	}

	/** {@inheritDoc} */
	@Override
	public Set<GKey> keySet() {
		return this.getKeys();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<GValue> values() {
		return this.getValues();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return this.getEntries();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.getMapping().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		final Map<?, ?> that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (final Iterator<Entry<GKey, GValue>> entries = this.getEntriesIterator(); entries.hasNext();) {
			final Entry<?, ?> entry = entries.next();
			final Object key = entry.getKey(), value = entry.getValue();
			if (value == null) {
				if (!((that.get(key) == null) && that.containsKey(key))) return false;
			} else {
				if (!value.equals(that.get(key))) return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getMapping().toString();
	}

}