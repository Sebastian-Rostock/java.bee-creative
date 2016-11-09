package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code double}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DoubleArray extends Array<double[], Double> {

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
	public List<Double> values();

	/** {@inheritDoc} */
	@Override
	public DoubleArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public double get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code double}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see DoubleArray#get(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void get(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, Array<double[], Double> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void get(int index, ArraySection<double[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, double value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code double}-Arrays an die gegebene Position.
	 *
	 * @see DoubleArray#set(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void set(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, Array<double[], Double> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void set(int index, ArraySection<double[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(double value);

	/** Diese Methode fügt die Werte des gegebenen {@code double}-Arrays am Ende ein.
	 *
	 * @see DoubleArray#add(ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist. */
	public void add(double[] values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(Array<double[], Double> values) throws NullPointerException;

	/** {@inheritDoc} */
	@Override
	public void add(ArraySection<double[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, double value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code double}-Arrays an der gegebenen Position ein.
	 *
	 * @see DoubleArray#add(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, Array<double[], Double> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void add(int index, ArraySection<double[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** {@inheritDoc} */
	@Override
	public void insert(int index, int count);

	/** {@inheritDoc} */
	@Override
	public void remove(int index, int count);

	/** {@inheritDoc} */
	@Override
	public DoubleArray subArray(int fromIndex, int toIndex);

	/** {@inheritDoc} */
	@Override
	public double[] toArray();

}
