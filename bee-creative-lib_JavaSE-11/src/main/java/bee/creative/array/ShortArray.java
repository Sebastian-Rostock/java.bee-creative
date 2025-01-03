package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code short}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface ShortArray extends Array<short[], Short> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	short get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code short}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see ShortArray#getAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, ShortArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, short value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code short}-Arrays an die gegebene Position.
	 *
	 * @see ShortArray#setAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, ShortArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(short value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code short}-Arrays am Ende ein.
	 *
	 * @see ShortArray#addAll(ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist. */
	default void addAll(short[] values) throws NullPointerException {
		this.addAll(ShortArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, short value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code short}-Arrays an der gegebenen Position ein.
	 *
	 * @see ShortArray#addAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), ShortArraySection.from(values));
	}

	@Override
	ShortArraySection section();

}
