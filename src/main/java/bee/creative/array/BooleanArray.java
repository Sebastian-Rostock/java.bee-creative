package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code boolean}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface BooleanArray extends Array<boolean[], Boolean> {

	/** {@inheritDoc} */
	@Override
	public int size();

	/** {@inheritDoc} */
	@Override
	public void clear();

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty();

	/** {@inheritDoc} */
	@Override
	public List<Boolean> values();

	/** {@inheritDoc} */
	@Override
	public BooleanArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public boolean get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code boolean}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see BooleanArray#getAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void getAll(int index, Array<? super boolean[], ? super Boolean> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void getAll(int index, ArraySection<? super boolean[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, boolean value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code boolean}-Arrays an die gegebene Position.
	 *
	 * @see BooleanArray#setAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void setAll(int index, Array<? extends boolean[], ? extends Boolean> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void setAll(int index, ArraySection<? extends boolean[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(boolean value);

	/** Diese Methode fügt die Werte des gegebenen {@code boolean}-Arrays am Ende ein.
	 *
	 * @see BooleanArray#addAll(ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code boolean}-Array {@code null} ist. */
	public void add(boolean[] values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void addAll(Array<? extends boolean[], ? extends Boolean> values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void addAll(ArraySection<? extends boolean[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, boolean value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code boolean}-Arrays an der gegebenen Position ein.
	 *
	 * @see BooleanArray#addAll(int, ArraySection)
	 * @see BooleanArraySection#from(boolean[])
	 * @param index Position.
	 * @param values {@code boolean}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code boolean}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, boolean[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void addAll(int index, Array<? extends boolean[], ? extends Boolean> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void addAll(int index, ArraySection<? extends boolean[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void insert(int index, int count);

	/** {@inheritDoc} */
	@Override
	public void remove(int index, int count);

	/** {@inheritDoc} */
	@Override
	public BooleanArray subArray(int fromIndex, int toIndex);

	/** {@inheritDoc} */
	@Override
	public boolean[] toArray();

}
