package bee.creative.util;

import java.util.Map;

/** Diese Klasse erweitert eine {@link HashMap} um einen Streuwertpuffer, analog zu {@link java.util.HashMap}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap2<GKey, GValue> extends HashMap<GKey, GValue> {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = -8419791227943208230L;

	/** Dieses Feld speichert den initialwert für {@link #hashes}. */
	static final int[] EMPTY_HASHES = {};

	{}

	/** Diese Methode gibt eine neue {@link HashMap2} zurück, welche Streuwert und Äquivalenz der Schlüssel über den gegebenen {@link Hasher} ermittelt. .
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param hasher Methoden zum Abgleich der Schlüssel.
	 * @return An {@link Hasher} gebundene {@link HashMap2}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher) {
		Objects.assertNotNull(hasher);
		return new HashMap2<GKey, GValue>() {

			private static final long serialVersionUID = -1399170754272459026L;

			@Override
			protected int customHash(final Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object key) {
				return hasher.equals(this.keys[entryIndex], key);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
				return this.customEqualsKey(entryIndex, key);
			}

		};
	}

	{}

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = HashMap2.EMPTY_HASHES;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap2(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} als Kopie der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap2(final Map<? extends GKey, ? extends GValue> source) {
		this.allocateImpl(source.size());
		this.putAll(source);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return (this.hashes[entryIndex] == keyHash) && this.customEqualsKey(entryIndex, key);
	}

	/** {@inheritDoc} */
	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] keys2;
		final Object[] values2;
		final int[] hashes2;
		if (capacity == 0) {
			keys2 = HashMap.EMPTY_OBJECTS;
			values2 = HashMap.EMPTY_OBJECTS;
			hashes2 = HashMap2.EMPTY_HASHES;
		} else {
			keys2 = new Object[capacity];
			values2 = new Object[capacity];
			hashes2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				keys2[targetIndex] = HashMap2.this.keys[sourceIndex];
				values2[targetIndex] = HashMap2.this.values[sourceIndex];
				hashes2[targetIndex] = HashMap2.this.hashes[sourceIndex];
			}

			@Override
			public void apply() {
				HashMap2.this.keys = keys2;
				HashMap2.this.values = values2;
				HashMap2.this.hashes = hashes2;
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			final HashMap2<?, ?> result = (HashMap2<?, ?>)super.clone();
			if (this.capacityImpl() == 0) {
				result.hashes = HashMap2.EMPTY_HASHES;
			} else {
				result.hashes = this.hashes.clone();
			}
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
