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

	/** Diese Klasse implementiert {@link HashMapOL#from(Hasher)} */
	@SuppressWarnings ("javadoc")
	public static final class HasherHashMap<GKey> extends HashMapOL<GKey> {

		private static final long serialVersionUID = 5915519385854194907L;

		public final Hasher hasher;

		public HasherHashMap(final Hasher hasher) {
			this.hasher = Objects.notNull(hasher);
		}

		@Override
		protected int customHash(final Object key) {
			return this.hasher.hash(key);
		}

		@Override
		protected int customHashKey(final int entryIndex) {
			return this.hasher.hash(this.keys[entryIndex]);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key) {
			return this.hasher.equals(this.keys[entryIndex], key);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
			return this.hasher.equals(this.keys[entryIndex], key);
		}

	}

	/** Diese Methode gibt eine neue {@link HashMapOL} zurück, welche Streuwert und Äquivalenz der Schlüssel über den gegebenen {@link Hasher} ermittelt.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param hasher Methoden zum Abgleich der Schlüssel.
	 * @return An {@link Hasher} gebundene {@link HashMapOL}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GKey> HashMapOL<GKey> from(final Hasher hasher) throws NullPointerException {
		return new HasherHashMap<>(hasher);
	}

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = -3537880648284024766L;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] keys = AbstractHashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. */
	transient long[] values = AbstractHashData.EMPTY_LONGS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMapOL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMapOL(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMapOL} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMapOL(final Map<? extends GKey, ? extends Long> source) {
		this(source.size());
		this.putAll(source);
	}

	@SuppressWarnings ("unchecked")
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			final Object key = stream.readObject();
			final long value = stream.readLong();
			this.putImpl((GKey)key, value);
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Entry<GKey, Long> entry: this.newEntriesImpl()) {
			stream.writeObject(entry.getKey());
			stream.writeLong(entry.getValue());
		}
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GKey customGetKey(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	@Override
	protected Long customGetValue(final int entryIndex) {
		return this.values[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final GKey key, final int keyHash) {
		this.keys[entryIndex] = key;
	}

	@Override
	protected Long customSetValue(final int entryIndex, final Long value) {
		final long[] values = this.values;
		final long result = values[entryIndex];
		values[entryIndex] = value;
		return result;
	}

	@Override
	protected int customHash(final Object key) {
		return Objects.hash(key);
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return Objects.hash(this.keys[entryIndex]);
	}

	@Override
	protected int customHashValue(final int entryIndex) {
		return Objects.hash(this.values[entryIndex]);
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key) {
		return Objects.equals(this.keys[entryIndex], key);
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return Objects.equals(this.keys[entryIndex], key);
	}

	@Override
	protected boolean customEqualsValue(final int entryIndex, final Object value) {
		return Objects.equals(this.values[entryIndex], value);
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
		final long[] values2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_LONGS;
		} else {
			keys2 = new Object[capacity];
			values2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
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

	@Override
	public Long put(final GKey key, final Long value) {
		final long value2 = value.longValue();
		final int count = this.countImpl(), index = this.putIndexImpl(key);
		final Long result = (count != this.countImpl()) ? null : this.values[index];
		this.values[index] = value2;
		return result;
	}

	/** Diese Methode erhöht den zum gegebenen Schlüssel hinterlegten Wert um das gegebene Inkrement. Wenn noch kein Wert hinterlegt ist, wird das Inkrement
	 * hinterlegt.
	 *
	 * @param key Schlüssel.
	 * @param value Inklement */
	public void add(final GKey key, final long value) {
		final int count = this.countImpl(), index = this.putIndexImpl(key);
		final long start = (count != this.countImpl()) ? 0 : this.values[index];
		this.values[index] = start + value;
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.keys) + EMU.fromArray(this.values);
	}

	@Override
	public HashMapOL<GKey> clone() throws CloneNotSupportedException {
		try {
			final HashMapOL<GKey> result = (HashMapOL<GKey>)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.keys = this.keys.clone();
			result.values = this.values.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
