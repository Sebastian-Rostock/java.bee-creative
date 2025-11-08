package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit beliebigen Schlüsselobjekten, {@link Long}-Werten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel. */
public class HashMapOL<GKey> extends AbstractHashMap<GKey, Long> implements Serializable, Cloneable {

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
	public HashMapOL(Map<? extends GKey, ? extends Long> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public Long put(GKey key, Long value) {
		long value2 = value.longValue();
		int count = this.countImpl(), index = this.putIndexImpl(key);
		Long result = (count != this.countImpl()) ? null : this.values[index];
		this.values[index] = value2;
		return result;
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(GKey key, long value) {
		int count = this.countImpl(), index = this.putIndexImpl(key);
		long start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapOL<GKey> clone() {
		HashMapOL<GKey> result = (HashMapOL<GKey>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GKey customGetKey(int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	@Override
	protected Long customGetValue(int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, GKey key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected Long customSetValue(int entryIndex, Long value) {
		long[] values = this.values;
		long result = values[entryIndex];
		values[entryIndex] = value;
		return result;
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
		int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			Object key = stream.readObject();
			long value = stream.readLong();
			this.putImpl((GKey)key, value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (Entry<GKey, Long> entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeLong(entry.getValue());
		}
	}

}
