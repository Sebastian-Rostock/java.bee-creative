package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code GValue}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente. */
public interface ObjectArray<GValue> extends Array<GValue[], GValue> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<GValue> values();

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
	 * @see ObjectArray#getAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super GValue[], ? super GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, GValue value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code GValue}-Arrays an die gegebene Position.
	 *
	 * @see ObjectArray#setAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends GValue[], ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(GValue value);

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays am Ende ein.
	 *
	 * @see ObjectArray#addAll(ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist. */
	public void addAll(GValue[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends GValue[], ? extends GValue> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends GValue[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, GValue value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays an der gegebenen Position ein.
	 *
	 * @see ObjectArray#addAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends GValue[], ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends GValue[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public ObjectArray<GValue> subArray(int fromIndex, int toIndex);

	@Override
	public GValue[] toArray();

}
