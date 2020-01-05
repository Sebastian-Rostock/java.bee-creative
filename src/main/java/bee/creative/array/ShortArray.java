package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code short}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface ShortArray extends Array<short[], Short> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Short> values();

	@Override
	public ShortArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public short get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code short}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see ShortArray#getAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super short[], ? super Short> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super short[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, short value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code short}-Arrays an die gegebene Position.
	 *
	 * @see ShortArray#setAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends short[], ? extends Short> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends short[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(short value);

	/** Diese Methode fügt die Werte des gegebenen {@code short}-Arrays am Ende ein.
	 *
	 * @see ShortArray#addAll(ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code short}-Array {@code null} ist. */
	public void addAll(short[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends short[], ? extends Short> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends short[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, short value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code short}-Arrays an der gegebenen Position ein.
	 *
	 * @see ShortArray#addAll(int, ArraySection)
	 * @see ShortArraySection#from(short[])
	 * @param index Position.
	 * @param values {@code short}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code short}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, short[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends short[], ? extends Short> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends short[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public ShortArray subArray(int fromIndex, int toIndex);

	@Override
	public short[] toArray();

}
