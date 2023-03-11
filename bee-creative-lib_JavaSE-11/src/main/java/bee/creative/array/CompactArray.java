package bee.creative.array;

import java.util.List;
import java.util.RandomAccess;
import bee.creative.lang.Objects;
import bee.creative.util.AbstractList2;

/** Diese Klasse implementiert ein abstraktes {@link Array} auf Basis einer {@link ArrayData}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
 *        oder {@code boolean[]}).
 * @param <GValue> Typ der Werte ({@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
 *        {@link Boolean}). */
public abstract class CompactArray<GArray, GValue> extends ArrayData<GArray> implements Array<GArray, GValue> {

	/** Diese Klasse implementiert eine {@link List} als modifizierbare Sicht auf die Werte eines {@link CompactArray}.
	 *
	 * @see Array#values()
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
	 *        oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
	 *        {@link Boolean}). */
	public static class Values<GArray, GValue> extends UnmodifiableValues<GArray, GValue> {

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public Values(final CompactArray<GArray, GValue> owner) throws NullPointerException {
			super(owner);
		}

		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.owner.remove(fromIndex, toIndex - fromIndex);
		}

		@Override
		public GValue set(final int index, final GValue value) {
			Objects.notNull(value);
			final CompactArray<?, GValue> owner = this.owner;
			final GValue entry = owner.customGet(index);
			owner.customSet(index, value);
			return entry;
		}

		@Override
		public void add(final int index, final GValue value) {
			Objects.notNull(value);
			final CompactArray<?, GValue> owner = this.owner;
			owner.insert(index, 1);
			owner.customSet(index, value);
		}

		@Override
		public GValue remove(final int index) {
			final CompactArray<?, GValue> owner = this.owner;
			final GValue entry = owner.customGet(index);
			owner.remove(index, 1);
			return entry;
		}

	}

	/** Diese Klasse implementiert eine {@link List} als unmodifizierbare Sicht auf die Werte eines {@link CompactArray}.
	 *
	 * @see Array#values()
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
	 *        oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
	 *        {@link Boolean}). */
	public static class UnmodifiableValues<GArray, GValue> extends AbstractList2<GValue> implements RandomAccess {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactArray<GArray, GValue> owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public UnmodifiableValues(final CompactArray<GArray, GValue> owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		/** Diese Methode gibt ein neues Array mit allen Werten des intern genutzten {@link CompactArray} zurück.
		 *
		 * @see List#toArray()
		 * @return neues Array. */
		public GArray array() {
			return this.owner.toArray();
		}

		@Override
		public GValue get(final int index) {
			return this.owner.customGet(index);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public int indexOf(final Object item) {
			if (item == null) {
				for (int index = 0, count = this.size(); index < count; index++) {
					if (this.get(index) == null) return index;
				}
			} else {
				for (int index = 0, count = this.size(); index < count; index++) {
					if (item.equals(this.get(index))) return index;
				}
			}
			return -1;
		}

		@Override
		public int lastIndexOf(final Object item) {
			if (item == null) {
				for (int index = this.size() - 1; 0 <= index; index--) {
					if (this.get(index) == null) return index;
				}
			} else {
				for (int index = this.size() - 1; 0 <= index; index--) {
					if (item.equals(this.get(index))) return index;
				}
			}
			return -1;
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link Array} als modifizierbare Sicht auf einen Teil eines {@link CompactArray}.
	 *
	 * @param <GOwner> Typ des {@link CompactArray}.
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
	 *        oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ({@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
	 *        {@link Boolean}). */
	protected static abstract class CompactSubArray<GOwner extends CompactArray<GArray, GValue>, GArray, GValue> implements Array<GArray, GValue> {

		/** Dieses Feld speichert den Besitzer. */
		protected final GOwner owner;

		/** Dieses Feld speichert den Index des ersten Werts im Teil-{@link Array}. */
		protected int startIndex;

		/** Dieses Feld speichert den Index des ersten Werts nach dem Teil-{@link Array} */
		protected int finalIndex;

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactSubArray(final GOwner owner, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException {
			if (startIndex > finalIndex) throw new IndexOutOfBoundsException("startIndex > finalIndex");
			this.startIndex = owner.exclusiveIndex(startIndex);
			this.finalIndex = owner.exclusiveIndex(finalIndex);
			this.owner = owner;
		}

		/** Diese Methode gibt den gegebenen Index als Index des internen Arrays des Besitzers zurück.
		 *
		 * @param index Index.
		 * @return Index + {@code startIndex}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist ({@code index < 0} oder {@code index > size()}). */
		protected final int ownerIndex(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int delta = this.startIndex;
			if (index > (this.finalIndex - delta)) throw new IndexOutOfBoundsException("index > size");
			return index + delta;
		}

		/** Diese Methode implementiert {@link #subArray(int, int)} als Delegation an den Besitzer.
		 *
		 * @see CompactArray#subArray(int, int)
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @return modifizierbare Teil-{@link Array}-Sicht.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > size()} oder
		 *         {@code startIndex > finalIndex}). */
		protected final Array<GArray, GValue> ownerSubArray(final int startIndex, final int finalIndex) {
			if (startIndex < 0) throw new IndexOutOfBoundsException("startIndex < 0");
			if (startIndex > finalIndex) throw new IllegalArgumentException("startIndex > finalIndex");
			final int delta = this.startIndex;
			final int ownerFinalIndex = delta + finalIndex;
			if (ownerFinalIndex > this.finalIndex) throw new IndexOutOfBoundsException("finalIndex > size()");
			return this.owner.subArray(delta + startIndex, ownerFinalIndex);
		}

		@Override
		public int size() {
			return this.finalIndex - this.startIndex;
		}

		@Override
		public void clear() {
			this.remove(0, this.finalIndex - this.startIndex);
		}

		@Override
		public boolean isEmpty() {
			return this.startIndex == this.finalIndex;
		}

		@Override
		public void getAll(final int index, final Array<? super GArray, ? super GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			this.owner.getAll(this.ownerIndex(index), values);
		}

		@Override
		public void getAll(final int index, final ArraySection<? super GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			this.owner.getAll(this.ownerIndex(index), values);
		}

		@Override
		public void setAll(final int index, final Array<? extends GArray, ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			this.owner.setAll(this.ownerIndex(index), values);
		}

		@Override
		public void setAll(final int index, final ArraySection<? extends GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			this.owner.setAll(this.ownerIndex(index), values);
		}

		@Override
		public void addAll(final Array<? extends GArray, ? extends GValue> values) throws NullPointerException {
			this.addAll(this.size(), values);
		}

		@Override
		public void addAll(final ArraySection<? extends GArray> values) throws NullPointerException {
			this.addAll(this.size(), values);
		}

		@Override
		public void addAll(final int index, final Array<? extends GArray, ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			this.addAll(index, values.section());
		}

		@Override
		public void addAll(final int index, final ArraySection<? extends GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			this.insert(index, values.size());
			this.setAll(index, values);
		}

		@Override
		public void insert(final int index, final int count) {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int startIndex = this.startIndex;
			final int finalIndex = this.finalIndex;
			final int ownerIndex = startIndex + index;
			if (ownerIndex > finalIndex) throw new IndexOutOfBoundsException("index > size()");
			final GOwner owner = this.owner;
			int offset = -owner.from;
			owner.insert(ownerIndex, count);
			offset += owner.from;
			this.startIndex = startIndex + offset;
			this.finalIndex = finalIndex + offset + count;
		}

		@Override
		public void remove(final int index, final int count) {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int startIndex = this.startIndex;
			final int finalIndex = this.finalIndex;
			final int ownerIndex = startIndex + index;
			if ((ownerIndex + count) > finalIndex) throw new IndexOutOfBoundsException("index + count > size()");
			final GOwner owner = this.owner;
			int offset = -owner.from;
			owner.insert(ownerIndex, count);
			offset += owner.from;
			this.startIndex = startIndex + offset;
			this.finalIndex = (finalIndex + offset) - count;
		}

		@Override
		public List<GValue> values() {
			return this.owner.values().subList(this.startIndex, this.finalIndex);
		}

		@Override
		public GArray toArray() {
			final GOwner owner = this.owner;
			final int fromIndex = this.startIndex;
			final int size = this.finalIndex - fromIndex;
			final GArray array = owner.customNewArray(size);
			System.arraycopy(owner.customGetArray(), fromIndex, array, 0, size);
			return array;
		}

		@Override
		public int hashCode() {
			return this.section().hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Array<?, ?>)) return false;
			final Array<?, ?> data = (Array<?, ?>)object;
			return this.section().equals(data.section());
		}

		@Override
		public String toString() {
			return this.section().toString();
		}

	}

	/** Dieses Feld speichert die relative Ausrichtungsposition. */
	private float align = 0.5f;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactArray() {
		this(0);
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactArray(final int capacity) throws IllegalArgumentException {
		if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
		this.customSetArray(this.customNewArray(capacity));
		this.customSetCapacity(capacity);
	}

	/** Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 *
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	public CompactArray(final ArraySection<? extends GArray> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		ArraySection.validate(section);
		this.customSetArray(section.array());
		this.from = section.startIndex();
		this.size = section.size();
	}

	/** Diese Methode gibt den {@code index}-ten Wert als Objekt zurück.
	 *
	 * @see CompactArray#values()
	 * @param index Index.
	 * @return {@code index}-ter Wert als Objekt. */
	protected abstract GValue customGet(int index);

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @see CompactArray#values()
	 * @param index Index.
	 * @param value Wert als Objekt. */
	protected abstract void customSet(int index, GValue value);

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index >= size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size}). */
	protected final int inclusiveIndex(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= this.size) throw new IndexOutOfBoundsException("index >= size()");
		return this.from + index;
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index > size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size}). */
	protected final int exclusiveIndex(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index > this.size) throw new IndexOutOfBoundsException("index > size");
		return this.from + index;
	}

	/** Diese Methode gibt das interne Array zurück.
	 *
	 * @see ArraySection#array()
	 * @return Array. */
	public abstract GArray array();

	/** Diese Methode gibt den Index des ersten Werts im Abschnitt des internen Arrays zurück.
	 *
	 * @see ArraySection#startIndex()
	 * @return Index des ersten Werts im Abschnitt. */
	public int startIndex() {
		return this.from;
	}

	/** Diese Methode gibt den Index des ersten Werts nach dem Abschnitt des internen Arrays zurück.
	 *
	 * @see ArraySection#finalIndex()
	 * @return Index des ersten Werts nach dem Abschnitt. */
	public int finalIndex() {
		return this.from + this.size;
	}

	/** Diese Methode gibt die relative Ausrichtungsposition der Elemente im Array zurück. Bei der relativen Ausrichtungsposition {@code 0} werden die Elemente am
	 * Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition
	 * {@code 1} gilt das gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays
	 * beschleunigt wird.
	 *
	 * @return relative Ausrichtungsposition ({@code 0..1}). */
	public float getAlignment() {
		return this.align;
	}

	/** Diese Methode setzt die relative Ausrichtungsposition der Elemente im Array. Bei der relativen Ausrichtungsposition {@code 0} werden die Elemente am
	 * Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition
	 * {@code 1} gilt das gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays
	 * beschleunigt wird.
	 *
	 * @param alignment relative Ausrichtungsposition ({@code 0..1}).
	 * @throws IllegalArgumentException Wenn die gegebene relative Ausrichtungsposition kleiner {@code 0}, größer {@code 1} ist oder {@link Float#NaN}. */
	public void setAlignment(final float alignment) throws IllegalArgumentException {
		if (!(alignment >= 0f)) throw new IllegalArgumentException("alignment < 0");
		if (!(alignment <= 1f)) throw new IllegalArgumentException("alignment > 1");
		this.align = alignment;
	}

	@Override
	protected int customNewFrom(final int space) {
		return (int)(space * this.align);
	}

	@Override
	public void getAll(final int index, final Array<? super GArray, ? super GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, values.section());
	}

	@Override
	public void getAll(final int index, final ArraySection<? super GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if ((index + valuesSize) > this.size) throw new IndexOutOfBoundsException("index + values.size() > size");
		if (valuesSize == 0) return;
		System.arraycopy(this.customGetArray(), index + this.from, values.array(), values.startIndex(), valuesSize);
	}

	@Override
	public void setAll(final int index, final Array<? extends GArray, ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, values.section());
	}

	@Override
	public void setAll(final int index, final ArraySection<? extends GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if ((index + valuesSize) > this.size) throw new IndexOutOfBoundsException("index + values.size() > size");
		if (valuesSize == 0) return;
		System.arraycopy(values.array(), values.startIndex(), this.customGetArray(), index + this.from, valuesSize);
	}

	@Override
	public void addAll(final Array<? extends GArray, ? extends GValue> values) throws NullPointerException {
		this.addAll(this.size, values);
	}

	@Override
	public void addAll(final ArraySection<? extends GArray> values) throws NullPointerException {
		this.addAll(this.size, values);
	}

	@Override
	public void addAll(final int index, final Array<? extends GArray, ? extends GValue> values) {
		this.addAll(index, values.section());
	}

	@Override
	public void addAll(final int index, final ArraySection<? extends GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		final int valuesSize = values.size();
		if (valuesSize == 0) return;
		this.insert(index, valuesSize);
		System.arraycopy(values.array(), values.startIndex(), this.customGetArray(), this.from + index, valuesSize);
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void clear() {
		this.remove(0, this.size);
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	@Override
	public void insert(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.customInsert(this.exclusiveIndex(index), count);
	}

	@Override
	public void remove(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.customRemove(this.exclusiveIndex(index), count);
	}

	@Override
	public Values<GArray, GValue> values() {
		return new Values<>(this);
	}

	@Override
	public GArray toArray() {
		final int size = this.size;
		final GArray array = this.customNewArray(size);
		System.arraycopy(this.customGetArray(), this.from, array, 0, size);
		return array;
	}

	@Override
	public int hashCode() {
		return this.section().hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Array<?, ?>)) return false;
		final Array<?, ?> data = (Array<?, ?>)object;
		return this.section().equals(data.section());
	}

	@Override
	public String toString() {
		return this.section().toString();
	}

}