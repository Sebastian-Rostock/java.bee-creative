package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code int}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface IntegerArray extends Array<int[], Integer> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Integer> values();

	@Override
	public IntegerArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public int get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code int}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see IntegerArray#getAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super int[], ? super Integer> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super int[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, int value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code int}-Arrays an die gegebene Position.
	 *
	 * @see IntegerArray#setAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends int[], ? extends Integer> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends int[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(int value);

	/** Diese Methode fügt die Werte des gegebenen {@code int}-Arrays am Ende ein.
	 *
	 * @see IntegerArray#addAll(ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code int}-Array {@code null} ist. */
	public void addAll(int[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends int[], ? extends Integer> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends int[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, int value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code int}-Arrays an der gegebenen Position ein.
	 *
	 * @see IntegerArray#addAll(int, ArraySection)
	 * @see IntegerArraySection#from(int[])
	 * @param index Position.
	 * @param values {@code int}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code int}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, int[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends int[], ? extends Integer> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends int[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public IntegerArray subArray(int fromIndex, int toIndex);

	@Override
	public int[] toArray();

}
