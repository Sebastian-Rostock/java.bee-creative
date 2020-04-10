package bee.creative.util;

import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse erweitert eine {@link HashMap} um einen Streuwertpuffer mit geringem {@link AbstractHashData Speicherverbrauch}, analog zu
 * {@link java.util.HashMap}. Der Speicerverbrauch liegt bei ca. 56 % (20 Byte je {@link #capacity() reservierten} Eintrag) von dem, den eine
 * {@link java.util.HashMap} benötigen würde.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap2<GKey, GValue> extends HashMap<GKey, GValue> {

	/** Diese Klasse implementiert {@link HashMap2#from(Hasher)} */
	@SuppressWarnings ("javadoc")
	public static class HasherHashMap2<GKey, GValue> extends HashMap2<GKey, GValue> {

		private static final long serialVersionUID = -1399170754272459026L;

		public final Hasher hasher;

		public HasherHashMap2(final Hasher hasher) {
			this.hasher = Objects.notNull(hasher);
		}

		@Override
		protected int customHash(final Object key) {
			return this.hasher.hash(key);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key) {
			return this.hasher.equals(this.keys[entryIndex], key);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
			return (this.hashes[entryIndex] == keyHash) && this.hasher.equals(this.keys[entryIndex], key);
		}

	}

	private static final long serialVersionUID = -8419791227943208230L;

	/** Diese Methode gibt eine neue {@link HashMap2} zurück, welche Streuwert und Äquivalenz der Schlüssel über den gegebenen {@link Hasher} ermittelt.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param hasher Methoden zum Abgleich der Schlüssel.
	 * @return An {@link Hasher} gebundene {@link HashMap2}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher) throws NullPointerException {
		return new HasherHashMap2<>(hasher);
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = AbstractHashData.EMPTY_INTEGERS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap2(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap2} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap2(final Map<? extends GKey, ? extends GValue> source) {
		this.allocateImpl(source.size());
		this.putAll(source);
	}

	@Override
	protected void customSetKey(final int entryIndex, final GKey key, final int keyHash) {
		this.keys[entryIndex] = key;
		this.hashes[entryIndex] = keyHash;
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return (this.hashes[entryIndex] == keyHash) && Objects.equals(this.keys[entryIndex], key);
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] keys2;
		final Object[] values2;
		final int[] hashes2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
			hashes2 = AbstractHashData.EMPTY_INTEGERS;
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

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.hashes);
	}

	@Override
	public HashMap2<GKey, GValue> clone() throws CloneNotSupportedException {
		try {
			final HashMap2<GKey, GValue> result = (HashMap2<GKey, GValue>)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.hashes = this.hashes.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
