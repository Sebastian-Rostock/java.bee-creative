package bee.creative.util;

import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Hashers.naturalHasher;
import java.util.Map;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse erweitert eine {@link HashMap} um einen Streuwertpuffer mit geringem {@link AbstractHashData Speicherverbrauch}, analog zu
 * {@link java.util.HashMap}. Der Speicerverbrauch liegt bei ca. 56 % (20 Byte je {@link #capacity() reservierten} Eintrag) von dem, den eine
 * {@link java.util.HashMap} benötigen würde.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte. */
public class HashMap2<K, V> extends HashMap<K, V> {

	/** Diese Methode ist eine Abkürzung für {@link #hashMapFrom(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), installValue, null)}. */
	public static <K, V> HashMap2<K, V> hashMapFrom(Getter<? super K, ? extends V> installValue) throws NullPointerException {
		return hashMapFrom(naturalHasher(), neutralGetter(), installValue, null);
	}

	public static <K, V> HashMap2<K, V> hashMapFrom(Getter<? super K, ? extends V> installValue, Setter<? super K, ? super V> reuseEntry)
		throws NullPointerException {
		return hashMapFrom(naturalHasher(), neutralGetter(), installValue, reuseEntry);
	}

	public static <K, V> HashMap2<K, V> hashMapFrom(Getter<? super K, ? extends K> installKey, Getter<? super K, ? extends V> installValue)
		throws NullPointerException {
		return hashMapFrom(naturalHasher(), installKey, installValue, null);
	}

	public static <K, V> HashMap2<K, V> hashMapFrom(Getter<? super K, ? extends K> installKey, Getter<? super K, ? extends V> installValue,
		Setter<? super K, ? super V> reuseEntry) throws NullPointerException {
		return hashMapFrom(naturalHasher(), installKey, installValue, reuseEntry);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashMapFrom(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), Getters.empty(),
	 * null)}. */
	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher) throws NullPointerException {
		return HashMap2.hashMapFrom(hasher, neutralGetter(), emptyGetter(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashMapFrom(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), installValue, null)}. */
	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher, Getter<? super K, ? extends V> installValue) throws NullPointerException {
		return HashMap2.hashMapFrom(hasher, neutralGetter(), installValue, null);
	}

	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher, Getter<? super K, ? extends V> installValue, Setter<? super K, ? super V> reuseEntry)
		throws NullPointerException {
		return hashMapFrom(hasher, neutralGetter(), installValue, reuseEntry);
	}

	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher, Getter<? super K, ? extends K> installKey, Getter<? super K, ? extends V> installValue)
		throws NullPointerException {
		return hashMapFrom(hasher, installKey, installValue, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashMapFrom(Hasher, Getter, Getter, Setter) HashMap2.from(hasher, Getters.neutral(), installAndReuseValue,
	 * installAndReuseValue)}. */
	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher, Field<? super K, V> installAndReuseValue) throws NullPointerException {
		return HashMap2.hashMapFrom(hasher, neutralGetter(), installAndReuseValue, installAndReuseValue);
	}

	/** Diese Methode liefert eine neue {@link HashMap2}, welche Streuwert, Äquivalenz, Installation und Wiederverwendung von Schlüsseln, Werten bzw. Einträgen an
	 * die gegebenen Methoden delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Schlüssel.
	 * @param installKey Methode zur {@link #customInstallKey(Object) Installation} des Schlüssels.
	 * @param installValue Methode zur {@link #customInstallValue(Object) Installation} des Werts.
	 * @param reuseEntry Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Eintrags oder {@code null}. */
	public static <K, V> HashMap2<K, V> hashMapFrom(Hasher hasher, Getter<? super K, ? extends K> installKey, Getter<? super K, ? extends V> installValue,
		Setter<? super K, ? super V> reuseEntry) throws NullPointerException {
		Objects.notNull(hasher);
		Objects.notNull(installKey);
		Objects.notNull(installValue);
		return new HashMap2<>() {

			private static final long serialVersionUID = 2518756984792880994L;

			@Override
			protected int customHash(Object key) {
				return hasher.hash(key);
			}

			@Override
			protected boolean customEqualsKey(int entryIndex, Object key) {
				return hasher.equals(this.customGetKey(entryIndex), key);
			}

			@Override
			protected K customInstallKey(K key) {
				return installKey.get(key);
			}

			@Override
			protected V customInstallValue(K key) {
				return installValue.get(key);
			}

			@Override
			protected void customReuseEntry(int entryIndex) {
				if (reuseEntry == null) return;
				reuseEntry.set(this.customGetKey(entryIndex), this.customGetValue(entryIndex));
			}

		};
	}

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashMap2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashMap2(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap2} mit dem Inhalt der gegebenen {@link Map}.
	 *
	 * @param source gegebene Einträge. */
	public HashMap2(Map<? extends K, ? extends V> source) {
		this.allocateImpl(source.size());
		this.putAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.hashes);
	}

	@Override
	public HashMap2<K, V> clone() {
		var result = (HashMap2<K, V>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.hashes = this.hashes.clone();
		return result;
	}

	@Override
	protected void customSetKey(int entryIndex, K key, int keyHash) {
		this.customSetKey(entryIndex, key);
		this.hashes[entryIndex] = keyHash;
	}

	@Override
	protected int customHashKey(int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key, int keyHash) {
		return (this.customHashKey(entryIndex) == keyHash) && this.customEqualsKey(entryIndex, key);
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		Object[] keys2;
		Object[] values2;
		int[] hashes2;
		if (capacity == 0) {
			keys2 = AbstractHashData.EMPTY_OBJECTS;
			values2 = AbstractHashData.EMPTY_OBJECTS;
			hashes2 = AbstractHashData.EMPTY_INTS;
		} else {
			keys2 = new Object[capacity];
			values2 = new Object[capacity];
			hashes2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
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

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = AbstractHashData.EMPTY_INTS;

	private static final long serialVersionUID = -8419791227943208230L;

}
