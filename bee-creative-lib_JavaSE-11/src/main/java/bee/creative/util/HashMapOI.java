package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit beliebigen Schlüsselobjekten, {@link Integer}-Werten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel. */
public class HashMapOI<GKey> extends AbstractHashMap<GKey, Integer> implements Serializable, Cloneable {

	private static final long serialVersionUID = -467621651047396939L;

	/** Diese Methode gibt eine neue {@link HashMapOI} zurück, welche Streuwert und Äquivalenz der Schlüssel über den gegebenen {@link Hasher} ermittelt.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param hasher Methoden zum Abgleich der Schlüssel.
	 * @return An {@link Hasher} gebundene {@link HashMapOI}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GKey> HashMapOI<GKey> from(final Hasher hasher) throws NullPointerException {
		Objects.notNull(hasher);
		return new HashMapOI<>() {

			private static final long serialVersionUID = 6526317910119486910L;

			@Override
			protected int customHash(final Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object key) {
				return hasher.equals(this.customGetKey(entryIndex), key);
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] keys = AbstractHashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient int[] values = AbstractHashData.EMPTY_INTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapOI() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapOI(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapOI} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapOI(final Map<? extends GKey, ? extends Integer> source) {
		this(source.size());
		this.putAll(source);
	}

	@SuppressWarnings ("unchecked")
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final Object key = stream.readObject();
			final int value = stream.readInt();
			this.putImpl((GKey)key, value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<GKey, Integer> entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeInt(entry.getValue());
		}
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GKey customGetKey(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	@Override
	protected Integer customGetValue(final int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final GKey key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected Integer customSetValue(final int entryIndex, final Integer value) {
		final int[] values = this.values;
		final int result = values[entryIndex];
		values[entryIndex] = value;
		return result;
	}

	@Override
	protected boolean customEqualsValue(final int entryIndex, final Object value) {
		return (value instanceof Integer) && (((Integer)value).intValue() == this.values[entryIndex]);
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.keys, null);
	}

	@Override
	protected void customClearKey(final int entryIndex) {
		this.keys[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] keys2;
		final int[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_INTS;
		} else {
			keys2 = new Object[capacity];
			values2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				keys2[targetIndex] = HashMapOI.this.keys[sourceIndex];
				values2[targetIndex] = HashMapOI.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapOI.this.keys = keys2;
				HashMapOI.this.values = values2;
			}

		};
	}

	@Override
	public Integer put(final GKey key, final Integer value) {
		final int value2 = value.intValue(), count = this.countImpl(), index = this.putIndexImpl(key);
		final Integer result = (count != this.countImpl()) ? null : this.values[index];
		this.values[index] = value2;
		return result;
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(final GKey key, final int value) {
		final int count = this.countImpl(), index = this.putIndexImpl(key), start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapOI<GKey> clone() {
		final HashMapOI<GKey> result = (HashMapOI<GKey>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

}
