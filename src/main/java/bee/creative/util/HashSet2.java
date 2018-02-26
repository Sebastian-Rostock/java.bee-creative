package bee.creative.util;

import java.util.Collection;
import java.util.Set;

/** Diese Klasse erweitert ein {@link HashSet} um einen Streuwertpuffer, analog zu {@link java.util.HashSet}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public class HashSet2<GItem> extends HashSet<GItem> {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = -6978391927144580624L;

	/** Dieses Feld speichert den initialwert für {@link #hashes}. */
	static final int[] EMPTY_HASHES = {};

	{}

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab. */
	transient int[] hashes = HashSet2.EMPTY_HASHES;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet2() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet2(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashSet} als Kopie der gegebenen {@link Set}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(final Set<? extends GItem> source) {
		this.allocateImpl(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert die {@link HashSet} als Kopie der gegebenen {@link Set}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet2(final Collection<? extends GItem> source) {
		this.allocateImpl(source.size());
		this.addAll(source);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected void customSetKey(final int entryIndex, final GItem item, final int itemHash) {
		this.items[entryIndex] = item;
		this.hashes[entryIndex] = itemHash;
	}

	/** {@inheritDoc} */
	@Override
	protected int customHashKey(final int entryIndex) {
		return this.hashes[entryIndex];
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item, final int itemHash) {
		return (this.hashes[entryIndex] == itemHash) && this.customEqualsKey(entryIndex, item);
	}

	/** {@inheritDoc} */
	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final Object[] items2;
		final int[] hashes2;
		if (capacity == 0) {
			items2 = HashSet.EMPTY_OBJECTS;
			hashes2 = HashSet2.EMPTY_HASHES;
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

	/** {@inheritDoc} */
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			final HashSet2<?> result = (HashSet2<?>)super.clone();
			if (this.capacityImpl() == 0) {
				result.hashes = HashSet2.EMPTY_HASHES;
			} else {
				result.hashes = this.hashes.clone();
			}
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
