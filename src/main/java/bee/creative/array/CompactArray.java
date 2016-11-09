package bee.creative.array;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

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
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
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

		{}

		/** {@inheritDoc} */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this._owner_.remove(fromIndex, toIndex - fromIndex);
		}

		/** {@inheritDoc} */
		@Override
		public GValue set(final int index, final GValue value) {
			if (value == null) throw new NullPointerException("value = null");
			final CompactArray<?, GValue> owner = this._owner_;
			final GValue entry = owner._value_(index);
			owner._value_(index, value);
			return entry;
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final GValue value) {
			if (value == null) throw new NullPointerException("value = null");
			final CompactArray<?, GValue> owner = this._owner_;
			owner.insert(index, 1);
			owner._value_(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public GValue remove(final int index) {
			final CompactArray<?, GValue> owner = this._owner_;
			final GValue entry = owner._value_(index);
			owner.remove(index, 1);
			return entry;
		}

	}

	/** Diese Klasse implementiert eine {@link List} als unmodifizierbare Sicht auf die Werte eines {@link CompactArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Array#values()
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
	 *        oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
	 *        {@link Boolean}). */
	public static class UnmodifiableValues<GArray, GValue> extends AbstractList<GValue> implements RandomAccess {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactArray<GArray, GValue> _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public UnmodifiableValues(final CompactArray<GArray, GValue> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/** Diese Methode gibt ein neues Array mit allen Werten des intern genutzten {@link CompactArray}s zurück.
		 *
		 * @see List#toArray()
		 * @return neues Array. */
		public GArray array() {
			return this._owner_.toArray();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public GValue get(final int index) {
			return this._owner_._value_(index);
		}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this._owner_._size_;
		}

		/** {@inheritDoc} */
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

		/** {@inheritDoc} */
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

	/** Diese Klasse implementiert ein abstraktes {@link Array} als modifizierbare Sicht auf einen Teil eines {@link CompactArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des {@link CompactArray}s.
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
	 *        oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ({@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
	 *        {@link Boolean}). */
	protected static abstract class CompactSubArray<GOwner extends CompactArray<GArray, GValue>, GArray, GValue> implements Array<GArray, GValue> {

		/** Dieses Feld speichert den Besitzer. */
		protected final GOwner _owner_;

		/** Dieses Feld speichert den Index des ersten Werts im Teil-{@link Array}. */
		protected int _startIndex_;

		/** Dieses Feld speichert den Index des ersten Werts nach dem Teil-{@link Array} */
		protected int _finalIndex_;

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactSubArray(final GOwner owner, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException {
			if (owner == null) throw new NullPointerException("owner = null");
			if (startIndex > finalIndex) throw new IndexOutOfBoundsException("startIndex > finalIndex");
			this._startIndex_ = owner._exclusiveIndex_(startIndex);
			this._finalIndex_ = owner._exclusiveIndex_(finalIndex);
			this._owner_ = owner;
		}

		{}

		/** Diese Methode gibt den gegebenen Index als Index des internen Arrays des Besitzers zurück.
		 *
		 * @param index Index.
		 * @return Index + {@code startIndex}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist ({@code index < 0} oder {@code index > size()}). */
		protected final int _ownerIndex_(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int delta = this._startIndex_;
			if (index > (this._finalIndex_ - delta)) throw new IndexOutOfBoundsException("index > size");
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
		protected final Array<GArray, GValue> _ownerSubArray_(final int startIndex, final int finalIndex) {
			if (startIndex < 0) throw new IndexOutOfBoundsException("startIndex < 0");
			if (startIndex > finalIndex) throw new IllegalArgumentException("startIndex > finalIndex");
			final int delta = this._startIndex_;
			final int ownerFinalIndex = delta + finalIndex;
			if (ownerFinalIndex > this._finalIndex_) throw new IndexOutOfBoundsException("finalIndex > size()");
			return this._owner_.subArray(delta + startIndex, ownerFinalIndex);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this._finalIndex_ - this._startIndex_;
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			this.remove(0, this._finalIndex_ - this._startIndex_);
		}

		/** {@inheritDoc} */
		@Override
		public boolean isEmpty() {
			return this._startIndex_ == this._finalIndex_;
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			this._owner_.get(this._ownerIndex_(index), values);
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			this._owner_.get(this._ownerIndex_(index), values);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			this._owner_.set(this._ownerIndex_(index), values);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			this._owner_.set(this._ownerIndex_(index), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final Array<GArray, GValue> values) throws NullPointerException {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final ArraySection<GArray> values) throws NullPointerException {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException {
			if (values == null) throw new NullPointerException("values = null");
			this.add(index, values.section());
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
			if (values == null) throw new NullPointerException("values = null");
			this.insert(index, values.size());
			this.set(index, values);
		}

		/** {@inheritDoc} */
		@Override
		public void insert(final int index, final int count) {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int startIndex = this._startIndex_;
			final int finalIndex = this._finalIndex_;
			final int ownerIndex = startIndex + index;
			if (ownerIndex > finalIndex) throw new IndexOutOfBoundsException("index > size()");
			final GOwner owner = this._owner_;
			int offset = -owner._from_;
			owner.insert(ownerIndex, count);
			offset += owner._from_;
			this._startIndex_ = startIndex + offset;
			this._finalIndex_ = finalIndex + offset + count;
		}

		/** {@inheritDoc} */
		@Override
		public void remove(final int index, final int count) {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int startIndex = this._startIndex_;
			final int finalIndex = this._finalIndex_;
			final int ownerIndex = startIndex + index;
			if ((ownerIndex + count) > finalIndex) throw new IndexOutOfBoundsException("index + count > size()");
			final GOwner owner = this._owner_;
			int offset = -owner._from_;
			owner.insert(ownerIndex, count);
			offset += owner._from_;
			this._startIndex_ = startIndex + offset;
			this._finalIndex_ = (finalIndex + offset) - count;
		}

		/** {@inheritDoc} */
		@Override
		public List<GValue> values() {
			return this._owner_.values().subList(this._startIndex_, this._finalIndex_);
		}

		/** {@inheritDoc} */
		@Override
		public GArray toArray() {
			final GOwner owner = this._owner_;
			final int fromIndex = this._startIndex_;
			final int size = this._finalIndex_ - fromIndex;
			final GArray array = owner._allocArray_(size);
			System.arraycopy(owner._array_(), fromIndex, array, 0, size);
			return array;
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return this.section().hashCode();
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Array<?, ?>)) return false;
			final Array<?, ?> data = (Array<?, ?>)object;
			return this.section().equals(data.section());
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this.section().toString();
		}

	}

	{}

	/** Dieses Feld speichert die relative Ausrichtungsposition. */
	private float _align_ = 0.5f;

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
		this._array_(this._allocArray_(capacity));
		this.allocate(capacity);
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
	public CompactArray(final ArraySection<GArray> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		ArraySection.validate(section);
		this._array_(section.array());
		this._from_ = section.startIndex();
		this._size_ = section.size();
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Wert als Objekt zurück.
	 *
	 * @see CompactArray#values()
	 * @param index Index.
	 * @return {@code index}-ter Wert als Objekt. */
	protected abstract GValue _value_(int index);

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @see CompactArray#values()
	 * @param index Index.
	 * @param value Wert als Objekt. */
	protected abstract void _value_(int index, GValue value);

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index >= size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size}). */
	protected final int _inclusiveIndex_(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index >= this._size_) throw new IndexOutOfBoundsException("index >= size()");
		return this._from_ + index;
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index > size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size}). */
	protected final int _exclusiveIndex_(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		if (index > this._size_) throw new IndexOutOfBoundsException("index > size");
		return this._from_ + index;
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
		return this._from_;
	}

	/** Diese Methode gibt den Index des ersten Werts nach dem Abschnitt des internen Arrays zurück.
	 *
	 * @see ArraySection#finalIndex()
	 * @return Index des ersten Werts nach dem Abschnitt. */
	public int finalIndex() {
		return this._from_ + this._size_;
	}

	/** Diese Methode gibt die relative Ausrichtungsposition der Elemente im Array zurück. Bei der relativen Ausrichtungsposition {@code 0} werden die Elemente am
	 * Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition
	 * {@code 1} gilt das gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays
	 * beschleunigt wird.
	 *
	 * @return relative Ausrichtungsposition ({@code 0..1}). */
	public float getAlignment() {
		return this._align_;
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
		this._align_ = alignment;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int _calcAlign_(final int space) {
		return (int)(space * this._align_);
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		if (values == null) throw new NullPointerException("values = null");
		this.get(index, values.section());
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		if (values == null) throw new NullPointerException("values = null");
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if ((index + valuesSize) > this._size_) throw new IndexOutOfBoundsException("index + values.size() > size");
		if (valuesSize == 0) return;
		System.arraycopy(this._array_(), index + this._from_, values.array(), values.startIndex(), valuesSize);
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		if (values == null) throw new NullPointerException("values = null");
		this.set(index, values.section());
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		if (values == null) throw new NullPointerException("values = null");
		if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if ((index + valuesSize) > this._size_) throw new IndexOutOfBoundsException("index + values.size() > size");
		if (valuesSize == 0) return;
		System.arraycopy(values.array(), values.startIndex(), this._array_(), index + this._from_, valuesSize);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final Array<GArray, GValue> values) throws NullPointerException {
		this.add(this._size_, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final ArraySection<GArray> values) throws NullPointerException {
		this.add(this._size_, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final Array<GArray, GValue> values) {
		if (values == null) throw new NullPointerException("values = null");
		this.add(index, values.section());
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException {
		if (values == null) throw new NullPointerException("values = null");
		final int valuesSize = values.size();
		if (valuesSize == 0) return;
		this.insert(index, valuesSize);
		System.arraycopy(values.array(), values.startIndex(), this._array_(), this._from_ + index, valuesSize);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this._size_;
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.remove(0, this._size_);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this._size_ == 0;
	}

	/** {@inheritDoc} */
	@Override
	public void insert(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this._insert_(this._exclusiveIndex_(index), count);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this._remove_(this._exclusiveIndex_(index), count);
	}

	/** {@inheritDoc} */
	@Override
	public Values<GArray, GValue> values() {
		return new Values<GArray, GValue>(this);
	}

	/** {@inheritDoc} */
	@Override
	public GArray toArray() {
		final int size = this._size_;
		final GArray array = this._allocArray_(size);
		System.arraycopy(this._array_(), this._from_, array, 0, size);
		return array;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.section().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Array<?, ?>)) return false;
		final Array<?, ?> data = (Array<?, ?>)object;
		return this.section().equals(data.section());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.section().toString();
	}

}