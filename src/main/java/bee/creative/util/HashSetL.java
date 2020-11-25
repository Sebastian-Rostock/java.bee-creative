package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein auf {@link AbstractHashSet} aufbauendes {@link Set} mit {@link Long} Elementen und geringem {@link AbstractHashData
 * Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashSetL extends AbstractHashSet<Long> implements Serializable, Cloneable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 4228392317821609623L;

	/** Dieses Feld bildet vom Index eines Elements auf dessen Wert ab. */
	transient long[] items = AbstractHashData.EMPTY_LONGS;

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSetL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSetL(final int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetL} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetL(final Set<? extends Long> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetL} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetL(final Collection<? extends Long> source) {
		this.addAll(source);
	}

	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		this.allocateImpl(count);
		for (int i = 0; i < count; i++) {
			this.putKeyImpl(stream.readLong());
		}
	}

	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (final Long item: this) {
			stream.writeLong(item);
		}
	}

	@Override
	protected Long customGetKey(final int entryIndex) {
		return this.items[entryIndex];
	}

	@Override
	protected void customSetKey(final int entryIndex, final Long item, final int itemHash) {
		this.items[entryIndex] = item;
	}

	@Override
	protected int customHash(final Object item) {
		return Objects.hash(item);
	}

	@Override
	protected int customHashKey(final int entryIndex) {
		return Objects.hash(this.items[entryIndex]);
	}

	@Override
	protected boolean customEqualsKey(final int entryIndex, final Object item) {
		return (item instanceof Long) && (((Long)item).intValue() == this.items[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(final int capacity) {
		final long[] items2;
		if (capacity == 0) {
			items2 = AbstractHashData.EMPTY_LONGS;
		} else {
			items2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(final int sourceIndex, final int targetIndex) {
				items2[targetIndex] = HashSetL.this.items[sourceIndex];
			}

			@Override
			public void apply() {
				HashSetL.this.items = items2;
			}

		};
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.items);
	}

	@Override
	public HashSetL clone() throws CloneNotSupportedException {
		try {
			final HashSetL result = (HashSetL)super.clone();
			if (this.capacityImpl() == 0) return result;
			result.items = this.items.clone();
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
