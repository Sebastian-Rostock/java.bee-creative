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

	private static final long serialVersionUID = -8419791227943208230L;

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), Getters.empty(), null)}. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher) throws NullPointerException {
		return HashMap2.from(hasher, Getters.<GKey>neutral(), Getters.<GKey, GValue>empty(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), installValue, null)}. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher, final Getter<? super GKey, ? extends GValue> installValue)
		throws NullPointerException {
		return HashMap2.from(hasher, Getters.<GKey>neutral(), installValue, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), installAndReuseValue,
	 * installAndReuseValue)}. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher, final Field<? super GKey, GValue> installAndReuseValue)
		throws NullPointerException {
		return HashMap2.from(hasher, Getters.<GKey>neutral(), installAndReuseValue, installAndReuseValue);
	}

	/** Diese Methode liefert eine neue {@link HashMap2}, welche Streuwert, Äquivalenz, Installation und Wiederverwendung von Schlüsseln, Werten bzw. Einträgen an
	 * die gegebenen Methoden delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Schlüssel.
	 * @param installKey Methode zur {@link #customInstallKey(Object) Installation} des Schlüssels.
	 * @param installValue Methode zur {@link #customInstallValue(Object) Installation} des Werts.
	 * @param reuseEntry Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Eintrags oder {@code null}. */
	public static <GKey, GValue> HashMap2<GKey, GValue> from(final Hasher hasher, final Getter<? super GKey, ? extends GKey> installKey,
		final Getter<? super GKey, ? extends GValue> installValue, final Setter<? super GKey, ? super GValue> reuseEntry) throws NullPointerException {
		Objects.notNull(hasher);
		Objects.notNull(installKey);
		Objects.notNull(installValue);
		return new HashMap2<GKey, GValue>() {

			private static final long serialVersionUID = 2518756984792880994L;

			@Override
			protected int customHash(final Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object key) {
				return hasher.equals(this.customGetKey(entryIndex), key);
			}

			@Override
			protected GKey customInstallKey(final GKey key) {
				return installKey.get(key);
			}

			@Override
			protected GValue customInstallValue(final GKey key) {
				return installValue.get(key);
			}

			@Override
			protected void customReuseEntry(final int entryIndex) {
				if (reuseEntry == null) return;
				reuseEntry.set(this.customGetKey(entryIndex), this.customGetValue(entryIndex));
			}

		};
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
		this.customSetKey(entryIndex, key);
		this.hashes[entryIndex] = keyHash;
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
		return (this.customHashKey(entryIndex) == keyHash) && this.customEqualsKey(entryIndex, key);
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
