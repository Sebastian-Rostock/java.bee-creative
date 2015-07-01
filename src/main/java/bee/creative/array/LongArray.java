package bee.creative.array;

import java.util.List;

/**
 * Diese Schnittstelle definiert ein modifizierbares {@code long}-{@link Array}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface LongArray extends Array<long[], Long> {

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
	public List<Long> values();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LongArraySection section();

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public long get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode füllt das gegebene {@code long}-Array mit den Werten ab der gegebenen Position.
	 * 
	 * @see LongArray#get(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}).
	 */
	public void get(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, Array<long[], Long> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, ArraySection<long[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode setzt den {@code index}-ten Wert.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public void set(int index, long value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode kopiert die Werte des gegebenen {@code long}-Arrays an die gegebene Position.
	 * 
	 * @see LongArray#set(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void set(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, Array<long[], Long> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, ArraySection<long[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt den gegebenen Werte am Ende ein.
	 * 
	 * @param value Wert.
	 */
	public void add(long value);

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code long}-Arrays am Ende ein.
	 * 
	 * @see LongArray#add(ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code long}-Array {@code null} ist.
	 */
	public void add(long[] values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Array<long[], Long> values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(ArraySection<long[]> values) throws NullPointerException;

	/**
	 * Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, long value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code long}-Arrays an der gegebenen Position ein.
	 * 
	 * @see LongArray#add(int, ArraySection)
	 * @see LongArraySection#from(long[])
	 * @param index Position.
	 * @param values {@code long}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code long}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, long[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, Array<long[], Long> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, ArraySection<long[]> values) throws NullPointerException, IndexOutOfBoundsException;

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
	public LongArray subArray(int fromIndex, int toIndex);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long[] toArray();

}
