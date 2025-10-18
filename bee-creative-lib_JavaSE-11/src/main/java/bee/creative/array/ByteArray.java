package bee.creative.array;

import java.io.IOException;
import java.io.OutputStream;

/** Diese Schnittstelle definiert ein modifizierbares {@code byte}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface ByteArray extends Array<byte[], Byte> {

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	byte get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code byte}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see ByteArray#getAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	default void getAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.getAll(index, ByteArraySection.from(values));
	}

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	void set(int index, byte value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code byte}-Arrays an die gegebene Position.
	 *
	 * @see ByteArray#setAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	default void setAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.setAll(index, ByteArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	default void add(byte value) {
		this.add(this.size(), value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays am Ende ein.
	 *
	 * @see ByteArray#addAll(ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist. */
	default void addAll(byte[] values) throws NullPointerException {
		this.addAll(ByteArraySection.from(values));
	}

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void add(int index, byte value) throws IndexOutOfBoundsException {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays an der gegebenen Position ein.
	 *
	 * @see ByteArray#addAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	default void addAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException {
		this.addAll(this.size(), ByteArraySection.from(values));
	}

	@Override
	ByteArraySection section();

}
