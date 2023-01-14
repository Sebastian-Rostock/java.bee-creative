package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code char}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface CharacterArray extends Array<char[], Character> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Character> values();

	@Override
	public CharacterArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public char get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code char}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see CharacterArray#getAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void get(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super char[], ? super Character> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, char value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code char}-Arrays an die gegebene Position.
	 *
	 * @see CharacterArray#setAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void set(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends char[], ? extends Character> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(char value);

	/** Diese Methode fügt die Werte des gegebenen {@code char}-Arrays am Ende ein.
	 *
	 * @see CharacterArray#addAll(ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist. */
	public void add(char[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends char[], ? extends Character> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends char[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, char value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code char}-Arrays an der gegebenen Position ein.
	 *
	 * @see CharacterArray#addAll(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends char[], ? extends Character> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public CharacterArray subArray(int fromIndex, int toIndex);

	@Override
	public char[] toArray();

}
