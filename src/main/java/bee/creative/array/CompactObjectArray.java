package bee.creative.array;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert ein {@link ObjectArray} als {@link CompactArray}. Das {@link List#add(int, Object) Einfügen} von Elementen an beliebigen
 * Positionen in die über {@link #values()} bereit gestellte {@link List}-Sicht benötigt im Durchschnitt ca. 45 % der Rechenzeit, die ein
 * {@link java.util.ArrayList} benötigen würde. Das {@link List#remove(int) Entfernen} von Elementen an beliebigen Positionen liegt dazu bei ca. 37 % der
 * Rechenzeit.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente. */
public class CompactObjectArray<GValue> extends CompactArray<GValue[], GValue> implements ObjectArray<GValue>, Comparator<GValue> {

	/** Diese Klasse implementiert ein {@link ObjectArray} als modifizierbare Sicht auf einen Teil eines {@link CompactObjectArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente. */
	protected static class CompactObjectSubArray<GValue> extends CompactSubArray<CompactObjectArray<GValue>, GValue[], GValue> implements ObjectArray<GValue> {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactObjectSubArray(final CompactObjectArray<GValue> owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/** {@inheritDoc} */
		@Override
		public ObjectArraySection<GValue> section() {
			return new CompactObjectSubArraySection<>(this);
		}

		/** {@inheritDoc} */
		@Override
		public GValue get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/** {@inheritDoc} */
		@Override
		public void getAll(final int index, final GValue[] values) {
			this.getAll(index, ObjectArraySection.from(this.owner, values));
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final GValue value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/** {@inheritDoc} */
		@Override
		public void setAll(final int index, final GValue[] values) {
			this.setAll(index, ObjectArraySection.from(this.owner, values));
		}

		/** {@inheritDoc} */
		@Override
		public void add(final GValue value) {
			this.add(this.size(), value);
		}

		/** {@inheritDoc} */
		@Override
		public void addAll(final GValue[] values) {
			this.addAll(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final GValue value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public void addAll(final int index, final GValue[] values) {
			this.addAll(this.size(), ObjectArraySection.from(this.owner, values));
		}

		/** {@inheritDoc} */
		@Override
		public ObjectArray<GValue> subArray(final int fromIndex, final int toIndex) {
			return (ObjectArray<GValue>)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link ObjectArraySection} eines {@link CompactObjectArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente. */
	protected static class CompactObjectArraySection<GValue> extends ObjectArraySection<GValue> {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactObjectArray<GValue> owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactObjectArraySection(final CompactObjectArray<GValue> owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		/** {@inheritDoc} */
		@Override
		protected int customCompare(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this.owner);
		}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.owner.size;
		}

		/** {@inheritDoc} */
		@Override
		public GValue[] array() {
			return this.owner.array;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this.owner.from;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			final CompactObjectArray<GValue> owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link ObjectArraySection} eines {@link CompactObjectArray.CompactObjectSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente. */
	protected static class CompactObjectSubArraySection<GValue> extends ObjectArraySection<GValue> {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactObjectSubArray<GValue> owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactObjectSubArraySection(final CompactObjectSubArray<GValue> owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		/** {@inheritDoc} */
		@Override
		protected int customCompare(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this.owner.owner);
		}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.owner.size();
		}

		/** {@inheritDoc} */
		@Override
		public GValue[] array() {
			return this.owner.owner.array;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this.owner.startIndex;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			return this.owner.finalIndex;
		}

	}

	/** Dieses Feld speichert das {@code GValue}-Array. */
	protected GValue[] array;

	/** Dieses Feld speichert die Klasse der Elemente zur Erzeugung des {@link #array} in {@link #customNewArray(int)} über
	 * {@link Array#newInstance(Class, int)}. */
	protected final Class<? extends GValue> valueClass;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @param valueClass Klasse der Elemente für {@link #customNewArray(int)}.
	 * @throws NullPointerException Wenn {@code valueClass} {@code null} ist. */
	public CompactObjectArray(final Class<? extends GValue> valueClass) throws NullPointerException {
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
	public CompactObjectArray(final Class<? extends GValue> valueClass, final int capacity) throws NullPointerException, IllegalArgumentException {
		this(ObjectArraySection.from((GValue[])Array.newInstance(valueClass, capacity), 0, 0));
		this.customSetCapacity(capacity);
	}

	/** Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 * 
	 * @param section {@link ArraySection}.
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @throws NullPointerException Wenn {@code valueClass}, {@code section} bzw. {@code section.array()} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	@SuppressWarnings ("unchecked")
	public CompactObjectArray(final ArraySection<? extends GValue[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
		this.valueClass = (Class<? extends GValue>)section.array().getClass().getComponentType();
	}

	/** {@inheritDoc} */
	@Override
	protected GValue[] customGetArray() {
		return this.array;
	}

	/** {@inheritDoc} */
	@Override
	protected void customSetArray(final GValue[] array) {
		this.array = array;
	}

	/** {@inheritDoc} */
	@Override
	protected GValue customGet(final int index) {
		return this.get(index);
	}

	/** {@inheritDoc} */
	@Override
	protected void customSet(final int index, final GValue value) {
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings ("unchecked")
	protected GValue[] customNewArray(final int length) {
		return (GValue[])Array.newInstance(this.valueClass, length);
	}

	/** {@inheritDoc} */
	@Override
	protected void customClearArray(final int startIndex, final int finalIndex) {
		final Object[] array = this.array;
		for (int i = startIndex, size = finalIndex; i < size; array[i++] = null) {}
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/** {@inheritDoc} */
	@Override
	public void getAll(final int index, final GValue[] values) {
		this.getAll(index, ObjectArraySection.from(this, values));
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final GValue value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void setAll(final int index, final GValue[] values) {
		this.setAll(index, ObjectArraySection.from(this, values));
	}

	/** {@inheritDoc} */
	@Override
	public void add(final GValue value) {
		this.add(this.size, value);
	}

	/** {@inheritDoc} */
	@Override
	public void addAll(final GValue[] values) {
		this.addAll(this.size, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final GValue value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public void addAll(final int index, final GValue[] values) {
		this.addAll(this.size, ObjectArraySection.from(this, values));
	}

	/** {@inheritDoc} */
	@Override
	public GValue[] array() {
		return this.array;
	}

	/** {@inheritDoc} */
	@Override
	public ObjectArraySection<GValue> section() {
		return new CompactObjectArraySection<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public ObjectArray<GValue> subArray(final int startIndex, final int finalIndex) {
		return new CompactObjectSubArray<>(this, startIndex, finalIndex);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings ({"unchecked", "rawtypes"})
	public int compare(final GValue o1, final GValue o2) {
		return Comparators.compare((Comparable)o1, (Comparable)o2);
	}

}
