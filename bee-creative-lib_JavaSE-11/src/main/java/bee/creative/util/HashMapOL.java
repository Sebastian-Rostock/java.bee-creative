package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit beliebigen Schlüsselobjekten, {@link Long}-Werten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ der Schlüssel. */
public class HashMapOL<K> extends AbstractHashMap<K, Long> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapOL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapOL(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapOL} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapOL(Map<? extends K, ? extends Long> source) {
		this(source.size());
		this.putAll(source);
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(K key, long value) {
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
	public HashMapOL<K> clone() {
		var result = (HashMapOL<K>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected K customGetKey(int entryIndex) {
		return (K)this.keys[entryIndex];
	}

	@Override
	protected Long customGetValue(int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, K key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected void customSetValue(int entryIndex, Long value) {
		this.values[entryIndex] = value;
	}

	@Override
	protected boolean customEqualsValue(int entryIndex, Object value) {
		return (value instanceof Long) && (((Long)value).longValue() == this.values[entryIndex]);
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.keys, null);
	}

	@Override
	protected void customClearKey(int entryIndex) {
		this.keys[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		Object[] keys2;
		long[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_LONGS;
		} else {
			keys2 = new Object[capacity];
			values2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				keys2[targetIndex] = HashMapOL.this.keys[sourceIndex];
				values2[targetIndex] = HashMapOL.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapOL.this.keys = keys2;
				HashMapOL.this.values = values2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] keys = AbstractHashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient long[] values = AbstractHashData.EMPTY_LONGS;

	private static final long serialVersionUID = -3537880648284024766L;

	@SuppressWarnings ("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			var key = stream.readObject();
			var value = stream.readLong();
			this.putValueImpl((K)key, value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeLong(entry.getValue());
		}
	}

}
