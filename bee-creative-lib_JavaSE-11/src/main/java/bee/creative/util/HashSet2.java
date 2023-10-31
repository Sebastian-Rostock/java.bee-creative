package bee.creative.util;

import java.util.Set;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse erweitert ein {@link HashSet} um einen Streuwertpuffer mit geringem {@link AbstractHashData Speicherverbrauch}, analog zu
 * {@link java.util.HashSet}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet2<GItem> extends HashSet<GItem> {

	private static final long serialVersionUID = -6978391927144580624L;

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Consumer) HashSet2.from(hasher, Getters.neutral(), null)}. */
	public static <GItem> HashSet2<GItem> from(final Hasher hasher) throws NullPointerException {
		return HashSet2.from(hasher, Getters.<GItem>neutral(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Consumer) HashSet2.from(hasher, installItem, null)}. */
	public static <GItem> HashSet2<GItem> from(final Hasher hasher, final Getter<? super GItem, ? extends GItem> installItem) throws NullPointerException {
		return HashSet2.from(hasher, installItem, null);
	}

	/** Diese Methode liefert ein neues {@link HashSet2}, welches Streuwert, Äquivalenz, Installation und Wiederverwendung von Elementen an die gegebenen Methoden
	 * delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Elemente.
	 * @param installItem Methode zur {@link #customInstallKey(Object) Installation} des Elements.
	 * @param reuseItem Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Elements oder {@code null}. */
	public static <GItem> HashSet2<GItem> from(final Hasher hasher, final Getter<? super GItem, ? extends GItem> installItem,
		final Consumer<? super GItem> reuseItem) throws NullPointerException {
		Objects.notNull(hasher);
		Objects.notNull(installItem);
		return new HashSet2<>() {

			private static final long serialVersionUID = 2607996617303285033L;

			@Override
			protected int customHash(final Object item) {
				return hasher.hash(item);
			}

			@Override
			protected boolean customEqualsKey(final int entryIndex, final Object item) {
				return hasher.equals(this.customGetKey(entryIndex), item);
			}

			@Override
			protected GItem customInstallKey(final GItem key) {
				return installItem.get(key);
			}

			@Override
			protected void customReuseEntry(final int entryIndex) {
				if (reuseItem == null) return;
				reuseItem.set(this.customGetKey(entryIndex));
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = AbstractHashData.EMPTY_INTEGERS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet2(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet2} mit dem Inhalt der gegebenen {@link Set}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(final Set<? extends GItem> source) {
		this.allocateImpl(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet2} mit dem Inhalt des gegebenen {@link Iterable}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(final Iterable<? extends GItem> source) {
		this.addAll(source);
	}

	@Override
	protected void customSetKey(final int entryIndex, final GItem item, final int itemHash) {
		this.customSetKey(entryIndex, item);
		this.hashes[entryIndex] = itemHash;
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item, final int itemHash) {
		return (this.customHashKey(entryIndex) == itemHash) && this.customEqualsKey(entryIndex, item);
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] items2;
		final int[] hashes2;
		if (capacity == 0) {
			items2 = AbstractHashData.EMPTY_OBJECTS;
			hashes2 = AbstractHashData.EMPTY_INTEGERS;
		} else {
			items2 = new Object[capacity];
			hashes2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
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

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.hashes);
	}

	@Override
	public HashSet2<GItem> clone() {
		final HashSet2<GItem> result = (HashSet2<GItem>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.hashes = this.hashes.clone();
		return result;
	}

}
