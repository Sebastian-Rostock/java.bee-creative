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
 * @param <E> Typ der Elemente. */
public class HashSet<E> extends AbstractHashSet<E> implements Serializable, Cloneable {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt des gegebenen {@link Set}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(Set<? extends E> source) {
		this(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet} mit dem Inhalt des gegebenen {@link Iterable}.
	 *
	 * @param source gegebene Elemente. */
	public HashSet(Iterable<? extends E> source) {
		this.addAll(source);
	}

	@Override
	public long emu() {
		return super.emu() + EMU.fromArray(this.items);
	}

	@Override
	public HashSet<E> clone() {
		var result = (HashSet<E>)super.clone();
		if (this.capacityImpl() == 0) return result;
		result.items = this.items.clone();
		return result;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected E customGetKey(int entryIndex) {
		return (E)this.items[entryIndex];
	}

	@Override
	protected void customSetKey(int entryIndex, E item) {
		this.items[entryIndex] = item;
	}

	@Override
	protected void customClear() {
		Arrays.fill(this.items, null);
	}

	@Override
	protected void customClearKey(int entryIndex) {
		this.items[entryIndex] = null;
	}

	@Override
	protected HashAllocator customAllocator(int capacity) {
		Object[] items2;
		if (capacity == 0) {
			items2 = EMPTY_OBJECTS;
		} else {
			items2 = new Object[capacity];
		}
		return new HashAllocator() {

			@Override
			public void copy(int sourceIndex, int targetIndex) {
				items2[targetIndex] = HashSet.this.items[sourceIndex];
			}

			@Override
			public void apply() {
				HashSet.this.items = items2;
			}

		};
	}

	/** Dieses Feld bildet vom Index eines Elements auf dessen Wert ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	transient Object[] items = EMPTY_OBJECTS;

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 1947961515821394540L;

	@SuppressWarnings ({"unchecked"})
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		var count = stream.readInt();
		this.allocateImpl(count);
		for (var i = 0; i < count; i++) {
			this.putKeyImpl((E)stream.readObject());
		}
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.countImpl());
		for (var item: this) {
			stream.writeObject(item);
		}
	}

}
