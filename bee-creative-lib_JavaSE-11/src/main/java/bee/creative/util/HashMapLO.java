package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Long}-Schlüsseln, beliebigen Wertobjekten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ der Werte. */
public class HashMapLO<V> extends AbstractHashMap<Long, V> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapLO() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapLO(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapLO} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapLO(Map<? extends Long, ? extends V> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public V put(Long key, V value) {
		return super.put(notNull(key), value);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapLO<V> clone() {
		var result = (HashMapLO<V>)super.clone();
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
	@SuppressWarnings ("unchecked")
	protected V customGetValue(int entryIndex) {
		return (V)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Long key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected void customSetValue(int entryIndex, V value) {
		this.values[entryIndex] = value;
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return (key instanceof Long) && (((Long)key).longValue() == this.keys[entryIndex]);
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.values, null);
	}

	@Override
	protected void customClearValue(int entryIndex) {
		this.values[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		long[] keys2;
		Object[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_LONGS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			keys2 = new long[capacity];
			values2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				keys2[targetIndex] = HashMapLO.this.keys[sourceIndex];
				values2[targetIndex] = HashMapLO.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapLO.this.keys = keys2;
				HashMapLO.this.values = values2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient long[] keys = AbstractHashData.EMPTY_LONGS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	private static final long serialVersionUID = -6864886543365066180L;

	@SuppressWarnings ("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			var key = stream.readLong();
			var value = stream.readObject();
			this.putValueImpl(key, (V)value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var entry: this.newEntriesImpl()) {
			stream.writeLong(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

}
