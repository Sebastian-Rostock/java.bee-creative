package bee.creative.array;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Diese Klasse implementiert ein abstraktes {@link Array} auf Basis einer {@link ArrayData}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]},
 *        {@code long[]}, {@code float[]}, {@code double[]} oder {@code boolean[]}).
 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long},
 *        {@link Float}, {@link Double} oder {@link Boolean}).
 */
public abstract class CompactArray<GArray, GValue> extends ArrayData<GArray> implements Array<GArray, GValue> {

	/**
	 * Diese Klasse implementiert eine {@link List} als modifizierbare Sicht auf die Werte.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Array#values()
	 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long},
	 *        {@link Float}, {@link Double} oder {@link Boolean}).
	 */
	protected static class Values<GValue> extends AbstractList<GValue> implements RandomAccess {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactArray<?, GValue> owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public Values(final CompactArray<?, GValue> owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.owner.remove(fromIndex, toIndex - fromIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final int index) {
			return this.owner.getValue(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue set(final int index, final GValue value) {
			if(value == null) throw new NullPointerException();
			final CompactArray<?, GValue> owner = this.owner;
			final GValue entry = owner.getValue(index);
			owner.setValue(index, value);
			return entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue value) {
			if(value == null) throw new NullPointerException();
			final CompactArray<?, GValue> owner = this.owner;
			owner.insert(index, 1);
			owner.setValue(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue remove(final int index) {
			final CompactArray<?, GValue> owner = this.owner;
			final GValue entry = owner.getValue(index);
			owner.remove(index, 1);
			return entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Array} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des {@link CompactArray}s.
	 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]},
	 *        {@code long[]}, {@code float[]}, {@code double[]} oder {@code boolean[]}).
	 * @param <GValue> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long},
	 *        {@link Float}, {@link Double} oder {@link Boolean}).
	 */
	protected static abstract class CompactSubArray<GOwner extends CompactArray<GArray, GValue>, GArray, GValue>
		implements Array<GArray, GValue> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final GOwner owner;

		/**
		 * Dieses Feld speichert den Index des ersten Werts im Teil-{@link Array}.
		 */
		protected int startIndex;

		/**
		 * Dieses Feld speichert den Index des ersten Werts nach dem Teil-{@link Array}
		 */
		protected int finalIndex;

		/**
		 * Dieser Konstrukteur initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder
		 *         {@code finalIndex > owner.size()} oder {@code startIndex > finalIndex}).
		 */
		public CompactSubArray(final GOwner owner, final int startIndex, final int finalIndex) throws NullPointerException,
			IndexOutOfBoundsException {
			if(owner == null) throw new NullPointerException("owner is null");
			if(startIndex > finalIndex) throw new IndexOutOfBoundsException("startIndex > finalIndex");
			this.startIndex = owner.exclusiveIndex(startIndex);
			this.finalIndex = owner.exclusiveIndex(finalIndex);
			this.owner = owner;
		}

		/**
		 * Diese Methode gibt den gegebenen Index als Index des internen Arrays des Besitzers zurück.
		 * 
		 * @param index Index.
		 * @return Index + {@code startIndex}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist ({@code index < 0} oder
		 *         {@code index > size()}).
		 */
		protected final int ownerIndex(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index < 0");
			final int delta = this.startIndex;
			if(index > (this.finalIndex - delta)) throw new IndexOutOfBoundsException("index > size");
			return index + delta;
		}

		/**
		 * Diese Methode implementiert {@link #subArray(int, int)} als Delegation an den Besitzer.
		 * 
		 * @see CompactArray#subArray(int, int)
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @return modifizierbare Teil-{@link Array}-Sicht.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder
		 *         {@code finalIndex > size()} oder {@code startIndex > finalIndex}).
		 */
		protected final Array<GArray, GValue> ownerSubArray(final int startIndex, final int finalIndex) {
			if(startIndex < 0) throw new IndexOutOfBoundsException();
			if(startIndex > finalIndex) throw new IllegalArgumentException();
			final int delta = this.startIndex;
			final int ownerFinalIndex = delta + finalIndex;
			if(ownerFinalIndex > this.finalIndex) throw new IndexOutOfBoundsException();
			return this.owner.subArray(delta + startIndex, ownerFinalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.finalIndex - this.startIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.remove(0, this.finalIndex - this.startIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.startIndex == this.finalIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final Array<GArray, GValue> values) throws NullPointerException,
			IndexOutOfBoundsException {
			this.owner.get(this.ownerIndex(index), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final ArraySection<GArray> values) throws NullPointerException,
			IndexOutOfBoundsException {
			this.owner.get(this.ownerIndex(index), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final Array<GArray, GValue> values) throws NullPointerException,
			IndexOutOfBoundsException {
			this.owner.set(this.ownerIndex(index), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final ArraySection<GArray> values) throws NullPointerException,
			IndexOutOfBoundsException {
			this.owner.set(this.ownerIndex(index), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final Array<GArray, GValue> values) throws NullPointerException {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final ArraySection<GArray> values) throws NullPointerException {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final Array<GArray, GValue> values) throws NullPointerException,
			IndexOutOfBoundsException {
			if(values == null) throw new NullPointerException("values is null");
			this.add(index, values.section());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final ArraySection<GArray> values) throws NullPointerException,
			IndexOutOfBoundsException {
			if(values == null) throw new NullPointerException("values is null");
			this.insert(index, values.size());
			this.set(index, values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void insert(final int index, final int count) {
			if(index < 0) throw new IndexOutOfBoundsException();
			final int startIndex = this.startIndex;
			final int finalIndex = this.finalIndex;
			final int ownerIndex = startIndex + index;
			if(ownerIndex > finalIndex) throw new IndexOutOfBoundsException();
			final GOwner owner = this.owner;
			int offset = -owner.from;
			owner.insert(ownerIndex, count);
			offset += owner.from;
			this.startIndex = startIndex + offset;
			this.finalIndex = finalIndex + offset + count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove(final int index, final int count) {
			if(index < 0) throw new IndexOutOfBoundsException();
			final int startIndex = this.startIndex;
			final int finalIndex = this.finalIndex;
			final int ownerIndex = startIndex + index;
			if((ownerIndex + count) > finalIndex) throw new IndexOutOfBoundsException();
			final GOwner owner = this.owner;
			int offset = -owner.from;
			owner.insert(ownerIndex, count);
			offset += owner.from;
			this.startIndex = startIndex + offset;
			this.finalIndex = (finalIndex + offset) - count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> values() {
			return this.owner.values().subList(this.startIndex, this.finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GArray toArray() {
			final GOwner owner = this.owner;
			final int fromIndex = this.startIndex;
			final int size = this.finalIndex - fromIndex;
			final GArray array = owner.newArray(size);
			System.arraycopy(owner.getArray(), fromIndex, array, 0, size);
			return array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.section().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Array<?, ?>)) return false;
			final Array<?, ?> data = (Array<?, ?>)object;
			return this.section().equals(data.section());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.section().toString();
		}

	}

	/**
	 * Dieses Feld speichert die relative Ausrichtungsposition.
	 */
	private float alignment = 0.5f;

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 */
	public CompactArray() {
		this(0);
	}

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactArray(final int capacity) throws IllegalArgumentException {
		if(capacity < 0) throw new IllegalArgumentException("capacity < 0");
		this.setArray(this.newArray(capacity));
		this.allocate(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als
	 * internes Array wird das der gegebenen {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird
	 * {@code 0.5} verwendet.
	 * 
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder
	 *         {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}.
	 */
	public CompactArray(final ArraySection<GArray> section) throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException {
		ArraySection.validate(section);
		this.setArray(section.array());
		this.from = section.startIndex();
		this.size = section.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int defaultAlignment(final int space) {
		return (int)(space * this.alignment);
	}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert als Objekt zurück.
	 * 
	 * @see CompactArray#values()
	 * @param index Index.
	 * @return {@code index}-ter Wert als Objekt.
	 */
	protected abstract GValue getValue(int index);

	/**
	 * Diese Methode setzt den {@code index}-ten Wert.
	 * 
	 * @see CompactArray#values()
	 * @param index Index.
	 * @param value Wert als Objekt.
	 */
	protected abstract void setValue(int index, GValue value);

	/**
	 * Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder
	 * {@code index >= size}) wird eine {@link IndexOutOfBoundsException} ausgelöst.
	 * 
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder
	 *         {@code index >= size}).
	 */
	protected final int inclusiveIndex(final int index) throws IndexOutOfBoundsException {
		if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
		return this.from + index;
	}

	/**
	 * Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder
	 * {@code index > size}) wird eine {@link IndexOutOfBoundsException} ausgelöst.
	 * 
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder
	 *         {@code index > size}).
	 */
	protected final int exclusiveIndex(final int index) throws IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("index < 0");
		if(index > this.size) throw new IndexOutOfBoundsException("index > size");
		return this.from + index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final Array<GArray, GValue> values) throws NullPointerException,
		IndexOutOfBoundsException {
		if(values == null) throw new NullPointerException("values is null");
		this.get(index, values.section());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final ArraySection<GArray> values) throws NullPointerException,
		IndexOutOfBoundsException {
		if(values == null) throw new NullPointerException("values == null");
		if(index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if((index + valuesSize) > this.size) throw new IndexOutOfBoundsException("index + values.size() > size");
		if(valuesSize == 0) return;
		System.arraycopy(this.getArray(), index + this.from, values.array(), values.startIndex(), valuesSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final Array<GArray, GValue> values) throws NullPointerException,
		IndexOutOfBoundsException {
		if(values == null) throw new NullPointerException("values is null");
		this.set(index, values.section());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final ArraySection<GArray> values) throws NullPointerException,
		IndexOutOfBoundsException {
		if(values == null) throw new NullPointerException("values == null");
		if(index < 0) throw new IndexOutOfBoundsException("index < 0");
		final int valuesSize = values.size();
		if((index + valuesSize) > this.size) throw new IndexOutOfBoundsException("index + values.size() > size");
		if(valuesSize == 0) return;
		System.arraycopy(values.array(), values.startIndex(), this.getArray(), index + this.from, valuesSize);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Array<GArray, GValue> values) throws NullPointerException {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final ArraySection<GArray> values) throws NullPointerException {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final Array<GArray, GValue> values) {
		if(values == null) throw new NullPointerException("values is null");
		this.add(index, values.section());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final ArraySection<GArray> values) throws NullPointerException,
		IndexOutOfBoundsException {
		if(values == null) throw new NullPointerException("values == null");
		final int valuesSize = values.size();
		if(valuesSize == 0) return;
		this.insert(index, valuesSize);
		System.arraycopy(values.array(), values.startIndex(), this.getArray(), this.from + index, valuesSize);
	}

	/**
	 * Diese Methode gibt die relative Ausrichtungsposition der Elemente im Array zurück. Bei der relativen
	 * Ausrichtungsposition {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen
	 * von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das
	 * gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen
	 * am Anfang des Arrays beschleunigt wird.
	 * 
	 * @return relative Ausrichtungsposition ({@code 0..1}).
	 */
	public float getAlignment() {
		return this.alignment;
	}

	/**
	 * Diese Methode setzt die relative Ausrichtungsposition der Elemente im Array. Bei der relativen Ausrichtungsposition
	 * {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende
	 * des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das gegenteil, da hier die
	 * Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays
	 * beschleunigt wird.
	 * 
	 * @param alignment relative Ausrichtungsposition ({@code 0..1}).
	 * @throws IllegalArgumentException Wenn die gegebene relative Ausrichtungsposition kleiner {@code 0}, größer
	 *         {@code 1} ist oder {@link Float#NaN}.
	 */
	public void setAlignment(final float alignment) throws IllegalArgumentException {
		if(!((alignment >= 0f) && (alignment <= 1f))) throw new IllegalArgumentException();
		this.alignment = alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.remove(0, this.size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.defaultInsert(this.exclusiveIndex(index), count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.defaultRemove(this.exclusiveIndex(index), count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GValue> values() {
		return new Values<GValue>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GArray toArray() {
		final int size = this.size;
		final GArray array = this.newArray(size);
		System.arraycopy(this.getArray(), this.from, array, 0, size);
		return array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.section().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof Array<?, ?>)) return false;
		final Array<?, ?> data = (Array<?, ?>)object;
		return this.section().equals(data.section());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.section().toString();
	}

}