package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code float}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FloatArray extends Array<float[], Float> {

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
	public List<Float> values();

	/** {@inheritDoc} */
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
	 * @see FloatArray#get(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void get(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, Array<float[], Float> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, ArraySection<float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, float value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code float}-Arrays an die gegebene Position.
	 *
	 * @see FloatArray#set(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void set(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, Array<float[], Float> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, ArraySection<float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(float value);

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays am Ende ein.
	 *
	 * @see FloatArray#add(ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist. */
	public void add(float[] values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(Array<float[], Float> values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(ArraySection<float[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, float value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays an der gegebenen Position ein.
	 *
	 * @see FloatArray#add(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, Array<float[], Float> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, ArraySection<float[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void insert(int index, int count);

	/** {@inheritDoc} */
	@Override
	public void remove(int index, int count);

	/** {@inheritDoc} */
	@Override
	public FloatArray subArray(int fromIndex, int toIndex);

	/** {@inheritDoc} */
	@Override
	public float[] toArray();

}
