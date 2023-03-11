package bee.creative.array;

import java.util.List;

/** Diese Schnittstelle definiert ein modifizierbares {@code byte}-{@link Array}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface ByteArray extends Array<byte[], Byte> {

	@Override
	public int size();

	@Override
	public void clear();

	@Override
	public boolean isEmpty();

	@Override
	public List<Byte> values();

	@Override
	public ByteArraySection section();

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public byte get(int index) throws IndexOutOfBoundsException;

	/** Diese Methode füllt das gegebene {@code byte}-Array mit den Werten ab der gegebenen Position.
	 *
	 * @see ByteArray#getAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}). */
	public void getAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, Array<? super byte[], ? super Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void getAll(int index, ArraySection<? super byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode setzt den {@code index}-ten Wert.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}). */
	public void set(int index, byte value) throws IndexOutOfBoundsException;

	/** Diese Methode kopiert die Werte des gegebenen {@code byte}-Arrays an die gegebene Position.
	 *
	 * @see ByteArray#setAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public void setAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, Array<? extends byte[], ? extends Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void setAll(int index, ArraySection<? extends byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/** Diese Methode fügt den gegebenen Werte am Ende ein.
	 *
	 * @param value Wert. */
	public void add(byte value);

	/** Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays am Ende ein.
	 *
	 * @see ByteArray#addAll(ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist. */
	public void addAll(byte[] values) throws NullPointerException;

	@Override
	public void addAll(Array<? extends byte[], ? extends Byte> values) throws NullPointerException;

	@Override
	public void addAll(ArraySection<? extends byte[]> values) throws NullPointerException;

	/** Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 *
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void add(int index, byte value) throws IndexOutOfBoundsException;

	/** Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays an der gegebenen Position ein.
	 *
	 * @see ByteArray#addAll(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}). */
	public void addAll(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, Array<? extends byte[], ? extends Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void addAll(int index, ArraySection<? extends byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	@Override
	public void insert(int index, int count);

	@Override
	public void remove(int index, int count);

	@Override
	public ByteArray subArray(int fromIndex, int toIndex);

	@Override
	public byte[] toArray();

}
