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

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = -5082256600102090233L;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient int[] keys = AbstractHashData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] values = AbstractHashData.EMPTY_OBJECTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapIO() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapIO(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapIO} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapIO(final Map<? extends Integer, ? extends GValue> source) {
		this(source.size());
		this.putAll(source);
	}

	@SuppressWarnings ("unchecked")
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final int key = stream.readInt();
			final Object value = stream.readObject();
			this.putImpl(key, (GValue)value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<Integer, GValue> entry: this.newEntriesImpl()) {
			stream.writeInt(entry.getKey());
			stream.writeObject(entry.getValue());
		}
	}

	@Override
	protected Integer customGetKey(final int entryIndex) {
		return this.keys[entryIndex];
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customGetValue(final int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final Integer key) {
		this.keys[entryIndex] = key;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue customSetValue(final int entryIndex, final GValue value) {
		final Object[] values = this.values;
		final Object result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return (key instanceof Integer) && (((Integer)key).intValue() == this.keys[entryIndex]);
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.values, null);
	}

	@Override
	protected void customClearValue(final int entryIndex) {
		this.values[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final int[] keys2;
		final Object[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_INTEGERS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			keys2 = new int[capacity];
			values2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
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

	@Override
	public GValue put(final Integer key, final GValue value) {
		return super.put(Objects.notNull(key), value);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapIO<GValue> clone() {
		final HashMapIO<GValue> result = (HashMapIO<GValue>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

}
