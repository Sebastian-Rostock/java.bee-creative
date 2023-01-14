package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code float}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FloatArray extends Array<float[], Float> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Float> values();

	@Override
	public FloatArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public float get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code float}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see FloatArray#getAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super float[], ? super Float> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, float value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code float}-Arrays an die gegebene Position.
	 *
	 * @see FloatArray#setAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends float[], ? extends Float> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(float value);

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays am Ende ein.
	 *
	 * @see FloatArray#addAll(ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist. */
	public void addAll(float[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends float[], ? extends Float> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends float[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, float value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays an der gegebenen Position ein.
	 *
	 * @see FloatArray#addAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends float[], ? extends Float> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public FloatArray subArray(int fromIndex, int toIndex);

	@Override
	public float[] toArray();

}
