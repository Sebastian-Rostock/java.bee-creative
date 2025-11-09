package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Long}-Schlüsseln und -Werten sowie geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashMapLL extends AbstractHashMap<Long, Long> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapLL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapLL(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapLL} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapLL(Map<? extends Long, ? extends Long> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public Long put(Long key, Long value) {
		return super.put(notNull(key), notNull(value));
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(Long key, long value) {
		var count = this.countImpl();
		var index = this.putIndexImpl(key);
		var start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapLL clone() {
		var result = (HashMapLL)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

	@Override
	protected Long customGetKey(int entryIndex) {
		return this.keys[entryIndex];
	}

	@Override
	protected Long customGetValue(int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Long key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected void customSetValue(int entryIndex, Long value) {
		this.values[entryIndex] = value;
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return (key instanceof Long) && (((Long)key).longValue() == this.keys[entryIndex]);
	}

	@Override
	protected boolean customEqualsValue(int entryIndex, Object value) {
		return (value instanceof Long) && (((Long)value).longValue() == this.values[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		long[] keys2;
		long[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_LONGS;
			values2 = AbstractHashData.EMPTY_LONGS;
		} else {
			keys2 = new long[capacity];
			values2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
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

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient long[] keys = AbstractHashData.EMPTY_LONGS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient long[] values = AbstractHashData.EMPTY_LONGS;

	private static final long serialVersionUID = -5580543670395051911L;

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			long key = stream.readInt();
			long value = stream.readInt();
			this.putValueImpl(key, value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (Entry<Long, Long> entry: this.newEntriesImpl()) {
			stream.writeLong(entry.getKey());
			stream.writeLong(entry.getValue());
		}
	}

}