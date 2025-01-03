package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code int}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface IntegerArray extends Array<int[], Integer> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	int get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code int}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see IntegerArray#getAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, IntegerArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, int value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code int}-Arrays an die gegebene Position.
	 *
	 * @see IntegerArray#setAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, IntegerArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(int value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code int}-Arrays am Ende ein.
	 *
	 * @see IntegerArray#addAll(ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist. */
	default void addAll(int[] values) throws NullPointerException {
		this.addAll(IntegerArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, int value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code int}-Arrays an der gegebenen Position ein.
	 *
	 * @see IntegerArray#addAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), IntegerArraySection.from(values));
	}

	@Override
	IntegerArraySection section();

}
