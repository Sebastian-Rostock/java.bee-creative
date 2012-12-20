package bee.creative.array;

import java.util.List;

/**
 * Diese Schnittstelle definiert ein modifizierbares {@code char}-{@link Array}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface CharacterArray extends Array<char[], Character> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Character> values();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArraySection section();

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public char get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode füllt das gegebene {@code char}-Array mit den Werten ab der gegebenen Position.
	 * 
	 * @see CharacterArray#get(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}).
	 */
	public void get(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, Array<char[], Character> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, ArraySection<char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode setzt den {@code index}-ten Wert.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public void set(int index, char value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode kopiert die Werte des gegebenen {@code char}-Arrays an die gegebene Position.
	 * 
	 * @see CharacterArray#set(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void set(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, Array<char[], Character> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, ArraySection<char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt den gegebenen Werte am Ende ein.
	 * 
	 * @param value Wert.
	 */
	public void add(char value);

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code char}-Arrays am Ende ein.
	 * 
	 * @see CharacterArray#add(ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code char}-Array {@code null} ist.
	 */
	public void add(char[] values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Array<char[], Character> values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(ArraySection<char[]> values) throws NullPointerException;

	/**
	 * Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, char value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code char}-Arrays an der gegebenen Position ein.
	 * 
	 * @see CharacterArray#add(int, ArraySection)
	 * @see CharacterArraySection#from(char[])
	 * @param index Position.
	 * @param values {@code char}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code char}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, char[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, Array<char[], Character> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, ArraySection<char[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(int index, int count);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(int index, int count);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArray subArray(int fromIndex, int toIndex);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char[] toArray();

}
