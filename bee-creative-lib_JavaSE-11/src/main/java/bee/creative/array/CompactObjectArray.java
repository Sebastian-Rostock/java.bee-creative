package bee.creative.array;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert ein {@link ObjectArray} als {@link CompactArray}. Das {@link List#add(int, Object) Einfügen} von Elementen an beliebigen
 * Positionen in die über {@link #values()} bereit gestellte {@link List}-Sicht benötigt im Durchschnitt ca. 45 % der Rechenzeit, die ein
 * {@link java.util.ArrayList} benötigen würde. Das {@link List#remove(int) Entfernen} von Elementen an beliebigen Positionen liegt dazu bei ca. 37 % der
 * Rechenzeit.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente. */
public class CompactObjectArray<GValue> extends CompactArray<GValue[], GValue> implements ObjectArray<GValue>, Comparator<GValue> {

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @param valueClass Klasse der Elemente für {@link #customNewArray(int)}.
	 * @throws NullPointerException Wenn {@code valueClass} {@code null} ist. */
	public CompactObjectArray(Class<? extends GValue> valueClass) throws NullPointerException {
		this(valueClass, 0);
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param valueClass Klasse der Elemente für {@link #customNewArray(int)}.
	 * @param capacity Kapazität.
	 * @throws NullPointerException Wenn {@code valueClass} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	@SuppressWarnings ("unchecked")
	public CompactObjectArray(Class<? extends GValue> valueClass, int capacity) throws NullPointerException, IllegalArgumentException {
		this(ObjectArraySection.from((GValue[])Array.newInstance(valueClass, capacity), 0, 0));
		this.customSetCapacity(capacity);
	}

	@SuppressWarnings ("unchecked")
	public CompactObjectArray(ObjectArraySection<GValue> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
		this.valueClass = (Class<? extends GValue>)this.array.getClass().getComponentType();
	}

	/** Dieses Feld speichert das {@code GValue}-Array. */
	protected GValue[] array;

	/** Dieses Feld speichert die Klasse der Elemente zur Erzeugung des {@link #array} in {@link #customNewArray(int)} über
	 * {@link Array#newInstance(Class, int)}. */
	protected final Class<? extends GValue> valueClass;

	@Override
	protected GValue[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(GValue[] array) {
		this.array = array;
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	@SuppressWarnings ("unchecked")
	protected GValue[] customNewArray(int length) {
		return (GValue[])Array.newInstance(this.valueClass, length);
	}

	@Override
	protected void customClearArray(int startIndex, int finalIndex) {
		var array = this.array;
		while (startIndex < finalIndex) {
			array[startIndex++] = null;
		}
	}

	@Override
	public GValue get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, GValue value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public ObjectArraySection<GValue> section() {
		return new Section<>(this);
	}

	@Override
	@SuppressWarnings ({"unchecked", "rawtypes"})
	public int compare(GValue o1, GValue o2) {
		return Comparators.compare((Comparable)o1, (Comparable)o2);
	}

	static class Section<GValue> extends ObjectArraySection<GValue> {

		@Override
		public GValue[] array() {
			return this.owner.array;
		}

		@Override
		public int offset() {
			return this.owner.from;
		}

		@Override
		public int length() {
			return this.owner.size;
		}

		@Override
		protected int customCompare(GValue[] array1, GValue[] array2, int index1, int index2) {
			return Comparators.compare(array1[index1], array2[index2], this.owner);
		}

		final CompactObjectArray<GValue> owner;

		Section(CompactObjectArray<GValue> owner) {
			this.owner = owner;
		}

	}

}
