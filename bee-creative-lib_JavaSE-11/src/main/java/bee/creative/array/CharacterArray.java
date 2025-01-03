package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code char}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface CharacterArray extends Array<char[], Character> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	char get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code char}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see CharacterArray#getAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, CharacterArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, char value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code char}-Arrays an die gegebene Position.
	 *
	 * @see CharacterArray#setAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, CharacterArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(char value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code char}-Arrays am Ende ein.
	 *
	 * @see CharacterArray#addAll(ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist. */
	default void addAll(char[] values) throws NullPointerException {
		this.addAll(CharacterArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, char value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code char}-Arrays an der gegebenen Position ein.
	 *
	 * @see CharacterArray#addAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), CharacterArraySection.from(values));
	}

	@Override
	CharacterArraySection section();

}
