package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.Hashers.naturalHasher;
import java.util.Set;
import bee.creative.emu.EMU;

/** Diese Klasse erweitert ein {@link HashSet} um einen Streuwertpuffer mit geringem {@link AbstractHashData Speicherverbrauch}, analog zu
 * {@link java.util.HashSet}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente. */
public class HashSet2<E> extends HashSet<E> {

	/** Diese Methode ist eine Abkürzung für {@link #hashSetFrom(Hasher, Getter, Consumer) hashSetFrom(naturalHasher(), installItem, null)}. */
	public static <E> HashSet2<E> hashSetFrom(Getter<? super E, ? extends E> installItem) throws NullPointerException {
		return hashSetFrom(naturalHasher(), installItem, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashSetFrom(Hasher, Getter, Consumer) hashSetFrom(naturalHasher(), installItem, reuseItem)}. */
	public static <E> HashSet2<E> hashSetFrom(Getter<? super E, ? extends E> installItem, Consumer<? super E> reuseItem) throws NullPointerException {
		return hashSetFrom(naturalHasher(), installItem, reuseItem);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashSetFrom(Hasher, Getter, Consumer) hashSetFrom(hasher, neutralGetter(), null)}. */
	public static <E> HashSet2<E> hashSetFrom(Hasher hasher) throws NullPointerException {
		return hashSetFrom(hasher, neutralGetter(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #hashSetFrom(Hasher, Getter, Consumer) hashSetFrom(hasher, installItem, null)}. */
	public static <E> HashSet2<E> hashSetFrom(Hasher hasher, Getter<? super E, ? extends E> installItem) throws NullPointerException {
		return hashSetFrom(hasher, installItem, null);
	}

	/** Diese Methode liefert ein neues {@link HashSet2}, welches Streuwert, Äquivalenz, Installation und Wiederverwendung von Elementen an die gegebenen Methoden
	 * delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Elemente.
	 * @param installItem Methode zur {@link #customInstallKey(Object) Installation} des Elements.
	 * @param reuseItem Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Elements oder {@code null}. */
	public static <E> HashSet2<E> hashSetFrom(Hasher hasher, Getter<? super E, ? extends E> installItem, Consumer<? super E> reuseItem)
		throws NullPointerException {
		notNull(hasher);
		notNull(installItem);
		return new HashSet2<>() {

			@Override
			protected int customHash(Object item) {
				return hasher.hash(item);
			}

			@Override
			protected boolean customEqualsKey(int entryIndex, Object item) {
				return hasher.equals(this.customGetKey(entryIndex), item);
			}

			@Override
			protected E customInstallKey(E key) {
				return installItem.get(key);
			}

			@Override
			protected void customReuseEntry(int entryIndex) {
				if (reuseItem == null) return;
				reuseItem.set(this.customGetKey(entryIndex));
			}

			private static final long serialVersionUID = 2607996617303285033L;

		};
	}

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet2(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet2} mit dem Inhalt der gegebenen {@link Set}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(Set<? extends E> source) {
		this.allocateImpl(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet2} mit dem Inhalt des gegebenen {@link Iterable}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(Iterable<? extends E> source) {
		this.addAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.hashes);
	}

	@Override
	public HashSet2<E> clone() {
		var result = (HashSet2<E>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.hashes = this.hashes.clone();
		return result;
	}

	@Override
	protected void customSetKey(int entryIndex, E item, int itemHash) {
		this.customSetKey(entryIndex, item);
		this.hashes[entryIndex] = itemHash;
	}

	@Override
	protected int customHashKey(int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object item, int itemHash) {
		return (this.customHashKey(entryIndex) == itemHash) && this.customEqualsKey(entryIndex, item);
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		Object[] items2;
		int[] hashes2;
		if (capacity == 0) {
			items2 = EMPTY_OBJECTS;
			hashes2 = EMPTY_INTS;
		} else {
			items2 = new Object[capacity];
			hashes2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				items2[targetIndex] = HashSet2.this.items[sourceIndex];
				hashes2[targetIndex] = HashSet2.this.hashes[sourceIndex];
			}

			@Override
			public void apply() {
				HashSet2.this.items = items2;
				HashSet2.this.hashes = hashes2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = EMPTY_INTS;

	private static final long serialVersionUID = -6978391927144580624L;

}
