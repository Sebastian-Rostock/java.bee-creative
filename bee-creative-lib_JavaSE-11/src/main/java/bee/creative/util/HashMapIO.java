package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Integer}-Schlüsseln, beliebigen Wertobjekten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Werte. */
public class HashMapIO<GValue> extends AbstractHashMap<Integer, GValue> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapIO() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapIO(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapIO} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapIO(Map<? extends Integer, ? extends GValue> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public GValue put(Integer key, GValue value) {
		return super.put(Objects.notNull(key), value);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapIO<GValue> clone() {
		var result = (HashMapIO<GValue>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

	@Override
	protected Integer customGetKey(int entryIndex) {
		return this.keys[entryIndex];
	}

	protected int customGetKeyInt(int entryIndex) {
		return this.keys[entryIndex];
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customGetValue(int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Integer key) {
		this.keys[entryIndex] = key;
	}

	protected void customSetKeyInt(int entryIndex, int key) {
		this.keys[entryIndex] = key;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customSetValue(int entryIndex, GValue value) {
		var values = this.values;
		var result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return (key instanceof Integer) && (((Integer)key).intValue() == this.keys[entryIndex]);
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
		var keys2 = capacity == 0 ? AbstractHashData.EMPTY_INTEGERS : new int[capacity];
		var values2 = capacity == 0 ? AbstractHashData.EMPTY_OBJECTS : new Object[capacity];
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				keys2[targetIndex] = HashMapIO.this.keys[sourceIndex];
				values2[targetIndex] = HashMapIO.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapIO.this.keys = keys2;
				HashMapIO.this.values = values2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient int[] keys = AbstractHashData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	@SuppressWarnings ("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			var key = stream.readInt();
			var value = stream.readObject();
			this.putImpl(key, (GValue)value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var entry: this.newEntriesImpl()) {
			stream.writeInt(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

	private static final long serialVersionUID = -5082256600102090233L;

}
