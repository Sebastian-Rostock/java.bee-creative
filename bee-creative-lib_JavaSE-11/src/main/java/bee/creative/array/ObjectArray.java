package bee.creative.array;

/** Diese Schnittstelle definiert ein modifizierbares {@code GValue}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente. */
public interface ObjectArray<GValue> extends Array<GValue[], GValue> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	GValue get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code GValue}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see ObjectArray#getAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, ObjectArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, GValue value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code GValue}-Arrays an die gegebene Position.
	 *
	 * @see ObjectArray#setAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, ObjectArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(GValue value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays am Ende ein.
	 *
	 * @see ObjectArray#addAll(ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code GValue}-Array {@code null} ist. */
	default void addAll(GValue[] values) throws NullPointerException {
		this.addAll(ObjectArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, GValue value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code GValue}-Arrays an der gegebenen Position ein.
	 *
	 * @see ObjectArray#addAll(int, ArraySection)
	 * @see ObjectArraySection#from(java.util.Comparator, Object...)
	 * @param index Position.
	 * @param values {@code GValue}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code GValue}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, GValue[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), ObjectArraySection.from(values));
	}

	@Override
	ObjectArraySection<GValue> section();

}
