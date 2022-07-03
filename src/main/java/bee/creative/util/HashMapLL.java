package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Long}-Schlüsseln und -Werten sowie geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashMapLL extends AbstractHashMap<Long, Long> implements Serializable, Cloneable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = -5580543670395051911L;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient long[] keys = AbstractHashData.EMPTY_LONGS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient long[] values = AbstractHashData.EMPTY_LONGS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapLL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapLL(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapLL} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapLL(final Map<? extends Long, ? extends Long> source) {
		this(source.size());
		this.putAll(source);
	}

	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final long key = stream.readInt();
			final long value = stream.readInt();
			this.putImpl(key, value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<Long, Long> entry: this.newEntriesImpl()) {
			stream.writeLong(entry.getKey());
			stream.writeLong(entry.getValue());
		}
	}

	@Override
	protected Long customGetKey(final int entryIndex) {
		return this.keys[entryIndex];
	}

	@Override
	protected Long customGetValue(final int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final Long key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected Long customSetValue(final int entryIndex, final Long value) {
		final long[] values = this.values;
		final Long result = values[entryIndex];
		values[entryIndex] = value;
		return result;
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return (key instanceof Long) && (((Long)key).longValue() == this.keys[entryIndex]);
	}

	@Override
	protected boolean customEqualsValue(final int entryIndex, final Object value) {
		return (value instanceof Long) && (((Long)value).longValue() == this.values[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final long[] keys2;
		final long[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_LONGS;
			values2 = AbstractHashData.EMPTY_LONGS;
		} else {
			keys2 = new long[capacity];
			values2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				keys2[targetIndex] = HashMapLL.this.keys[sourceIndex];
				values2[targetIndex] = HashMapLL.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapLL.this.keys = keys2;
				HashMapLL.this.values = values2;
			}

		};
	}

	@Override
	public Long put(final Long key, final Long value) {
		return super.put(Objects.notNull(key), Objects.notNull(value));
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(final Long key, final long value) {
		final int count = this.countImpl(), index = this.putIndexImpl(key);
		final long start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapLL clone() {
		final HashMapLL result = (HashMapLL)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

}