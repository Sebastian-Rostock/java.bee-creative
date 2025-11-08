package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import bee.creative.emu.EMU;

/** Diese Klasse implementiert ein auf {@link AbstractHashSet} aufbauendes {@link Set} mit {@link Integer} Elementen und geringem {@link AbstractHashData
 * Speicherverbrauch}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class HashSetI extends AbstractHashSet<Integer> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSetI() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSetI(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetI} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetI(Set<? extends Integer> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSetI} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Elemente. */
	public HashSetI(Collection<? extends Integer> source) {
		this.addAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.items);
	}

	@Override
	public HashSetI clone() {
		var result = (HashSetI)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.items = this.items.clone();
		return result;
	}

	@Override
	protected Integer customGetKey(int entryIndex) {
		return this.items[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, Integer item) {
		this.items[entryIndex] = item;
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object item) {
		return (item instanceof Integer) && (((Integer)item).intValue() == this.items[entryIndex]);
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		int[] items2;
		if (capacity == 0) {
			items2 = EMPTY_INTS;
		} else {
			items2 = new int[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				items2[targetIndex] = HashSetI.this.items[sourceIndex];
			}

			@Override
			public void apply() {
				HashSetI.this.items = items2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Elements auf dessen Wert ab. */
	transient int[] items = EMPTY_INTS;

	private static final long serialVersionUID = 6862948924620051022L;

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			this.putKeyImpl(stream.readInt());
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var item: this) {
			stream.writeInt(item);
		}
	}

}
