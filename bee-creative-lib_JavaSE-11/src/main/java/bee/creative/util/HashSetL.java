package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert ein auf {@link AbstractHashSet} aufbauendes {@link Set} mit {@link Long} Elementen und geringem {@link AbstractHashData
 * Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashSetL extends AbstractHashSet<Long> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSetL() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSetL(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetL} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetL(Set<? extends Long> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetL} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetL(Collection<? extends Long> source) {
		this.addAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.items);
	}

	@Override
	public HashSetL clone() {
		var result = (HashSetL)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.items = this.items.clone();
		return result;
	}

	@Override
	protected Long customGetKey(int entryIndex) {
		return this.items[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Long item) {
		this.items[entryIndex] = item;
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object item) {
		return (item instanceof Long) && (((Long)item).longValue() == this.items[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		long[] items2;
		if (capacity == 0) {
			items2 = EMPTY_LONGS;
		} else {
			items2 = new long[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				items2[targetIndex] = HashSetL.this.items[sourceIndex];
			}

			@Override
			public void apply() {
				HashSetL.this.items = items2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Elements auf dessen Wert ab. */
	transient long[] items = EMPTY_LONGS;

	private static final long serialVersionUID = 4228392317821609623L;

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			this.putKeyImpl(stream.readLong());
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var item: this) {
			stream.writeLong(item);
		}
	}

}
