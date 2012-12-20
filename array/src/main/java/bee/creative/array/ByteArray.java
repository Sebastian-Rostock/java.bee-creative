package bee.creative.array;

import java.util.List;

/**
 * Diese Schnittstelle definiert ein modifizierbares {@code byte}-{@link Array}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ByteArray extends Array<byte[], Byte> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Byte> values();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteArraySection section();

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Position.
	 * @return {@code index}-ter Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public byte get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode füllt das gegebene {@code byte}-Array mit den Werten ab der gegebenen Position.
	 * 
	 * @see ByteArray#get(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.length > size()}).
	 */
	public void get(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, Array<byte[], Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int index, ArraySection<byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode setzt den {@code index}-ten Wert.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size()}).
	 */
	public void set(int index, byte value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode kopiert die Werte des gegebenen {@code byte}-Arrays an die gegebene Position.
	 * 
	 * @see ByteArray#set(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void set(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, Array<byte[], Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int index, ArraySection<byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt den gegebenen Werte am Ende ein.
	 * 
	 * @param value Wert.
	 */
	public void add(byte value);

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays am Ende ein.
	 * 
	 * @see ByteArray#add(ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 */
	public void add(byte[] values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Array<byte[], Byte> values) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(ArraySection<byte[]> values) throws NullPointerException;

	/**
	 * Diese Methode fügt den gegebenen Wert an der gegebenen Position ein.
	 * 
	 * @param index Position.
	 * @param value Wert.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, byte value) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die Werte des gegebenen {@code byte}-Arrays an der gegebenen Position ein.
	 * 
	 * @see ByteArray#add(int, ArraySection)
	 * @see ByteArraySection#from(byte[])
	 * @param index Position.
	 * @param values {@code byte}-Array.
	 * @throws NullPointerException Wenn da gegebene {@code byte}-Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, byte[] values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, Array<byte[], Byte> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, ArraySection<byte[]> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(int index, int count);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(int index, int count);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteArray subArray(int fromIndex, int toIndex);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] toArray();

}
