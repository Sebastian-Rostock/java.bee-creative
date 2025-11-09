package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Integer}-Schlüsseln, beliebigen Wertobjekten und geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ der Werte. */
public class HashMapIO<V> extends AbstractHashMap<Integer, V> implements Serializable, Cloneable {

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
	public HashMapIO(Map<? extends Integer, ? extends V> source) {
		this(source.size());
		this.putAll(source);
	}

	@Override
	public V put(Integer key, V value) {
		return super.put(notNull(key), value);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapIO<V> clone() {
		var result = (HashMapIO<V>)super.clone();
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
	protected V customGetValue(int entryIndex) {
		return (V)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Integer key) {
		this.keys[entryIndex] = key;
	}

	protected void customSetKeyInt(int entryIndex, int key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected void customSetValue(int entryIndex, V value) {
		this.values[entryIndex] = value;
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
		var keys2 = capacity == 0 ? AbstractHashData.EMPTY_INTS : new int[capacity];
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
	transient int[] keys = AbstractHashData.EMPTY_INTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	private static final long serialVersionUID = -5082256600102090233L;

	@SuppressWarnings ("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			var key = stream.readInt();
			var value = stream.readObject();
			this.putValueImpl(key, (V)value);
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var entry: this.newEntriesImpl()) {
			stream.writeInt(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

}
