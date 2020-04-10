package bee.creative.util;

import java.util.Collection;
import java.util.Set;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse erweitert ein {@link HashSet} um einen Streuwertpuffer mit geringem {@link AbstractHashData Speicherverbrauch}, analog zu
 * {@link java.util.HashSet}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet2<GItem> extends HashSet<GItem> {

	/** Diese Klasse implementiert {@link HashSet2#from(Hasher)} */
	@SuppressWarnings ("javadoc")
	public static final class HasherHashSet2<GItem> extends HashSet2<GItem> {

		public static final long serialVersionUID = 5185785086449129611L;

		public final Hasher hasher;

		public HasherHashSet2(final Hasher hasher) {
			this.hasher = Objects.notNull(hasher);
		}

		@Override
		protected int customHash(final Object item) {
			return this.hasher.hash(item);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item) {
			return this.hasher.equals(this.items[entryIndex], item);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item, final int itemHash) {
			return (this.hashes[entryIndex] == itemHash) && this.hasher.equals(this.items[entryIndex], item);
		}

	}

	private static final long serialVersionUID = -6978391927144580624L;

	/** Diese Methode gibt ein neues {@link HashSet2} zurück, welche Streuwert und Äquivalenz der Elemente über den gegebenen {@link Hasher} ermittelt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param hasher Methoden zum Abgleich der Elemente.
	 * @return An {@link Hasher} gebundenes {@link HashSet2}.
	 * @throws NullPointerException Wenn {@code hasher} {@code null} ist. */
	public static <GItem> HashSet2<GItem> from(final Hasher hasher) throws NullPointerException {
		return new HasherHashSet2<>(hasher);
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

	/** Dieser Konstruktor initialisiert das {@link HashSet2} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(final Collection<? extends GItem> source) {
		this.addAll(source);
	}

	@Override
	protected void customSetKey(final int entryIndex, final GItem item, final int itemHash) {
		this.items[entryIndex] = item;
		this.hashes[entryIndex] = itemHash;
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item, final int itemHash) {
		return (this.hashes[entryIndex] == itemHash) && Objects.equals(this.items[entryIndex], item);
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
	public HashSet2<GItem> clone() throws CloneNotSupportedException {
		try {
			final HashSet2<GItem> result = (HashSet2<GItem>)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.hashes = this.hashes.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
