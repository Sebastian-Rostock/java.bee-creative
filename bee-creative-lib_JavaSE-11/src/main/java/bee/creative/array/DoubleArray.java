package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code double}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DoubleArray extends Array<double[], Double> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	double get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code double}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see DoubleArray#getAll(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, DoubleArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, double value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code double}-Arrays an die gegebene Position.
	 *
	 * @see DoubleArray#setAll(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, DoubleArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(double value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code double}-Arrays am Ende ein.
	 *
	 * @see DoubleArray#addAll(ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code double}-Array {@code null} ist. */
	default void addAll(double[] values) throws NullPointerException {
		this.addAll(DoubleArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, double value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code double}-Arrays an der gegebenen Position ein.
	 *
	 * @see DoubleArray#addAll(int, ArraySection)
	 * @see DoubleArraySection#from(double[])
	 * @param index Position.
	 * @param values {@code double}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code double}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, double[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), DoubleArraySection.from(values));
	}

	@Override
	DoubleArraySection section();

}
