package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code GValue}-{@link Array}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der {@link Object}s. */
public interface ObjectArray<GValue> extends Array<GValue[], GValue> {

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
	public List<GValue> values();

	/** {@inheritDoc} */
	@Override
	public ObjectArraySection<GValue> section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public GValue get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code GValue}-Array mit den Werten ab der gegebenen Position.
	 * 
	 * @see ObjectArray#get(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void get(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, Array<GValue[], GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, ArraySection<GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, GValue value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code GValue}-Arrays an die gegebene Position.
	 * 
	 * @see ObjectArray#set(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void set(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, Array<GValue[], GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, ArraySection<GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 * 
	 * @param value Wert. */
	public void add(GValue value);

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays am Ende ein.
	 * 
	 * @see ObjectArray#add(ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist. */
	public void add(GValue[] values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(Array<GValue[], GValue> values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(ArraySection<GValue[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, GValue value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays an der gegebenen Position ein.
	 * 
	 * @see ObjectArray#add(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, Array<GValue[], GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, ArraySection<GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void insert(int index, int count);

	/** {@inheritDoc} */
	@Override
	public void remove(int index, int count);

	/** {@inheritDoc} */
	@Override
	public ObjectArray<GValue> subArray(int fromIndex, int toIndex);

	/** {@inheritDoc} */
	@Override
	public GValue[] toArray();

}
