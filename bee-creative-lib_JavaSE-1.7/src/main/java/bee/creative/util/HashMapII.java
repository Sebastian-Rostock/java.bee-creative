package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine auf {@link AbstractHashMap} aufbauende {@link Map} mit {@link Integer}-Schlüsseln und -Werten sowie geringem
 * {@link AbstractHashData Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashMapII extends AbstractHashMap<Integer, Integer> implements Serializable, Cloneable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = -5580543670395051911L;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. */
	transient int[] keys = AbstractHashData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient int[] values = AbstractHashData.EMPTY_INTEGERS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapII() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapII(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapII} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapII(final Map<? extends Integer, ? extends Integer> source) {
		this(source.size());
		this.putAll(source);
	}

	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final int key = stream.readInt();
			final int value = stream.readInt();
			this.putImpl(key, value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<Integer, Integer> entry: this.newEntriesImpl()) {
			stream.writeInt(entry.getKey());
			stream.writeInt(entry.getValue());
		}
	}

	@Override
	protected Integer customGetKey(final int entryIndex) {
		return this.keys[entryIndex];
	}

	@Override
	protected Integer customGetValue(final int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final Integer key) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected Integer customSetValue(final int entryIndex, final Integer value) {
		final int[] values = this.values;
		final Integer result = values[entryIndex];
		values[entryIndex] = value;
		return result;
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return (key instanceof Integer) && (((Integer)key).intValue() == this.keys[entryIndex]);
	}

	@Override
	protected boolean customEqualsValue(final int entryIndex, final Object value) {
		return (value instanceof Integer) && (((Integer)value).intValue() == this.values[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final int[] keys2;
		final int[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_INTEGERS;
			values2 = AbstractHashData.EMPTY_INTEGERS;
		} else {
			keys2 = new int[capacity];
			values2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				keys2[targetIndex] = HashMapII.this.keys[sourceIndex];
				values2[targetIndex] = HashMapII.this.values[sourceIndex];
			}

			@Override
			public void apply() {
				HashMapII.this.keys = keys2;
				HashMapII.this.values = values2;
			}

		};
	}

	@Override
	public Integer put(final Integer key, final Integer value) {
		return super.put(Objects.notNull(key), Objects.notNull(value));
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(final Integer key, final int value) {
		final int count = this.countImpl(), index = this.putIndexImpl(key), start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapII clone() {
		final HashMapII result = (HashMapII)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.keys = this.keys.clone();
		result.values = this.values.clone();
		return result;
	}

}