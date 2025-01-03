package bee.creative.array;

import bee.creative.lang.Objects;
import bee.creative.util.List2;

/** Diese Klasse implementiert ein abstraktes {@link Array} auf Basis einer {@link ArrayData}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
 *        oder {@code boolean[]}).
 * @param <GValue> Typ der Werte ({@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
 *        {@link Boolean}). */
public abstract class CompactArray<GArray, GValue> extends ArrayData<GArray> implements Array<GArray, GValue> {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactArray() {
		this(0, 0.5f);
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactArray(int capacity, float alignmen) throws IllegalArgumentException {
		if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
		this.customSetArray(this.customNewArray(capacity));
		this.setAlignment(alignmen);
		this.customSetCapacity(capacity);
	}

	/** Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 *
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	public CompactArray(final ArraySection<? extends GArray, ? extends GValue> section)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		this.customSetArray(section.array());
		this.from = section.offset();
		this.size = section.length();
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
	public void setAlignment(float alignment) throws IllegalArgumentException {
		if (!(alignment >= 0f)) throw new IllegalArgumentException("alignment < 0");
		if (!(alignment <= 1f)) throw new IllegalArgumentException("alignment > 1");
		this.align = alignment;
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public void insert(int index, int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.customInsert(this.exclusiveIndex(index), count);
	}

	@Override
	public void remove(int index, int count) throws IndexOutOfBoundsException, IllegalArgumentException {
		this.customRemove(this.exclusiveIndex(index), count);
	}

	@Override
	public List2<GValue> values() {
		return new Values<>(this);
	}

	@Override
	public GArray toArray() {
		var size = this.size;
		var array = this.customNewArray(size);
		System.arraycopy(this.customGetArray(), this.from, array, 0, size);
		return array;
	}

	@Override
	public int hashCode() {
		return this.section().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Array<?, ?>)) return false;
		var that = (Array<?, ?>)object;
		return this.section().equals(that.section());
	}

	@Override
	public String toString() {
		return this.section().toString();
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index >= size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size}). */
	protected int inclusiveIndex(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException(index);
		return this.from + index;
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index > size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size}). */
	protected int exclusiveIndex(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > this.size)) throw new IndexOutOfBoundsException(index);
		return this.from + index;
	}

	@Override
	protected int customNewFrom(int space) {
		return (int)(space * this.align);
	}

	/** Dieses Feld speichert die relative Ausrichtungsposition. */
	private float align = 0.5f;

	static class Values<GValue> extends ArraySection.Values<GValue> {

		@Override
		public void add(int index, GValue value) {
			Objects.notNull(value);
			this.array.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public GValue remove(int index) {
			var result = this.get(index);
			this.array.remove(index, 1);
			return result;
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			this.array.remove(fromIndex, toIndex - fromIndex);
		}

		Values(CompactArray<?, GValue> array) {
			super(array.section());
			this.array = array;
		}

		final CompactArray<?, GValue> array;

	}

}