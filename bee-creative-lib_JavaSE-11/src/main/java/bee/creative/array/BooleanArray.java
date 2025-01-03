package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code boolean}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface BooleanArray extends Array<boolean[], Boolean> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	boolean get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code boolean}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see BooleanArray#getAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, BooleanArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, boolean value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code boolean}-Arrays an die gegebene Position.
	 *
	 * @see BooleanArray#setAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, BooleanArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(boolean value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code boolean}-Arrays am Ende ein.
	 *
	 * @see BooleanArray#addAll(ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist. */
	default void addAll(boolean[] values) throws NullPointerException {
		this.addAll(BooleanArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, boolean value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code boolean}-Arrays an der gegebenen Position ein.
	 *
	 * @see BooleanArray#addAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), BooleanArraySection.from(values));
	}

	@Override
	BooleanArraySection section();

}
