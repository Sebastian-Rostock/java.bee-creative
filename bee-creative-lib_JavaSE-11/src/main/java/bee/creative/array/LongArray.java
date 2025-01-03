package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code long}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface LongArray extends Array<long[], Long> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	long get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code long}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see LongArray#getAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, LongArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, long value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code long}-Arrays an die gegebene Position.
	 *
	 * @see LongArray#setAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, LongArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(long value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code long}-Arrays am Ende ein.
	 *
	 * @see LongArray#addAll(ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist. */
	default void addAll(long[] values) throws NullPointerException {
		this.addAll(LongArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, long value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code long}-Arrays an der gegebenen Position ein.
	 *
	 * @see LongArray#addAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), LongArraySection.from(values));
	}

	@Override
	LongArraySection section();

}
