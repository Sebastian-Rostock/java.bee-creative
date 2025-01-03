package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code float}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FloatArray extends Array<float[], Float> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	float get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code float}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see FloatArray#getAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, FloatArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, float value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code float}-Arrays an die gegebene Position.
	 *
	 * @see FloatArray#setAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, FloatArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(float value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays am Ende ein.
	 *
	 * @see FloatArray#addAll(ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code float}-Array {@code null} ist. */
	default void addAll(float[] values) throws NullPointerException {
		this.addAll(FloatArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, float value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code float}-Arrays an der gegebenen Position ein.
	 *
	 * @see FloatArray#addAll(int, ArraySection)
	 * @see FloatArraySection#from(float[])
	 * @param index Position.
	 * @param values {@code float}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code float}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, float[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), FloatArraySection.from(values));
	}

	@Override
	FloatArraySection section();

}
