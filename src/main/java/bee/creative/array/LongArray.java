package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code long}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface LongArray extends Array<long[], Long> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Long> values();

	@Override
	public LongArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public long get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code long}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see LongArray#getAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super long[], ? super Long> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super long[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, long value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code long}-Arrays an die gegebene Position.
	 *
	 * @see LongArray#setAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends long[], ? extends Long> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends long[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(long value);

	/** Diese Methode fügt die Werte des gegebenen {@code long}-Arrays am Ende ein.
	 *
	 * @see LongArray#addAll(ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist. */
	public void addAll(long[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends long[], ? extends Long> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends long[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, long value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code long}-Arrays an der gegebenen Position ein.
	 *
	 * @see LongArray#addAll(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends long[], ? extends Long> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends long[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public LongArray subArray(int fromIndex, int toIndex);

	@Override
	public long[] toArray();

}
