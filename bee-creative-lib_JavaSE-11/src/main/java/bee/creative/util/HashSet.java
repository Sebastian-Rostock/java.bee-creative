package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein auf {@link AbstractHashSet} aufbauendes {@link Set} mit beliebigen Elementen und geringem {@link AbstractHashData
 * Speicherverbrauch}. Das {@link #contains(Object) Finden} von Elementen benötigt ca. 45 % der Rechenzeit, die ein {@link java.util.HashSet} benötigen würde.
 * {@link #add(Object) Einfügen} und {@link #remove(Object) Entfernen} von Elementen liegen dazu bei ca. 60 % bzw. 85 % der Rechenzeit.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet<GItem> extends AbstractHashSet<GItem> implements Serializable, Cloneable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 1947961515821394540L;

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Consumer) HashSet.from(hasher, Getters.neutral(), null)}. */
	public static <GItem> HashSet<GItem> from(final Hasher hasher) throws NullPointerException {
		return HashSet.from(hasher, Getters.<GItem>neutralGetter(), null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Hasher, Getter, Consumer) HashSet.from(hasher, installItem, null)}. */
	public static <GItem> HashSet<GItem> from(final Hasher hasher, final Getter<? super GItem, ? extends GItem> installItem) throws NullPointerException {
		return HashSet.from(hasher, installItem, null);
	}

	/** Diese Methode liefert ein neues {@link HashSet}, welches Streuwert, Äquivalenz, Installation und Wiederverwendung von Elementen an die gegebenen Methoden
	 * delegiert.
	 *
	 * @param hasher Methoden zur Berechnung von {@link #customHash(Object) Streuwert} und {@link #customEqualsKey(int, Object) Äquivalenz} der Elemente.
	 * @param installItem Methode zur {@link #customInstallKey(Object) Installation} des Elements.
	 * @param reuseItem Methode zur Anzeige der {@link #customReuseEntry(int) Wiederverwendung} des Elements oder {@code null}. */
	public static <GItem> HashSet<GItem> from(final Hasher hasher, final Getter<? super GItem, ? extends GItem> installItem,
		final Consumer<? super GItem> reuseItem) throws NullPointerException {
		Objects.notNull(hasher);
		Objects.notNull(installItem);
		return new HashSet<>() {

			private static final long serialVersionUID = -1363112074783475978L;

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

	/** Dieses Feld bildet vom Index eines Elements auf dessen Wert ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] items = AbstractHashData.EMPTY_OBJECTS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Set<? extends GItem> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt des gegebenen {@link Iterable}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(final Iterable<? extends GItem> source) {
		this.addAll(source);
	}

	@SuppressWarnings ({"unchecked"})
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			this.putKeyImpl((GItem)stream.readObject());
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final GItem item: this) {
			stream.writeObject(item);
		}
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GItem customGetKey(final int entryIndex) {
		return (GItem)this.items[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final GItem item) {
		this.items[entryIndex] = item;
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.items, null);
	}

	@Override
	protected void customClearKey(final int entryIndex) {
		this.items[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] items2;
		if (capacity == 0) {
			items2 = AbstractHashData.EMPTY_OBJECTS;
		} else {
			items2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				items2[targetIndex] = HashSet.this.items[sourceIndex];
			}

			@Override
			public void apply() {
				HashSet.this.items = items2;
			}

		};
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.items);
	}

	@Override
	public HashSet<GItem> clone() {
		final HashSet<GItem> result = (HashSet<GItem>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.items = this.items.clone();
		return result;
	}

}
