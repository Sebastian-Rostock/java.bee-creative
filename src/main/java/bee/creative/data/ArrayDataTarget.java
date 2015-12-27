package bee.creative.data;

import java.io.IOException;
import bee.creative.array.CompactByteArray;

/**
 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link CompactByteArray}.
 * 
 * @see CompactByteArray
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class ArrayDataTarget extends BaseDataTarget {

	/**
	 * Dieses Feld speichert die Nutzdaten.
	 */
	final CompactByteArray __data;

	/**
	 * Dieses Feld speichert die Schreibeposition.
	 */
	int __index;

	/**
	 * Dieser Konstruktor initialisiert die Nutzdaten mit 128 Byte Größe.
	 */
	public ArrayDataTarget() {
		this(128);
	}

	/**
	 * Dieser Konstruktor initialisiert die Nutzdaten mit der gegebenen Größe.
	 * 
	 * @see CompactByteArray#CompactByteArray(int)
	 * @param size Größe.
	 */
	public ArrayDataTarget(final int size) {
		this(new CompactByteArray(size));
		this.__data.setAlignment(0);
	}

	/**
	 * Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die Nutzdaten {@code null} sind.
	 */
	public ArrayDataTarget(final CompactByteArray data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		this.__data = data;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompactByteArray data() {
		return this.__data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		if ((offset < 0) || ((offset + length) > array.length)) throw new IndexOutOfBoundsException();
		final CompactByteArray data = this.__data;
		final int size = data.size(), index = this.__index, index2 = index + length;
		data.insert(size, Math.max(index2 - size, 0));
		System.arraycopy(array, offset, data.array(), data.startIndex() + index, length);
		this.__index = index2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void seek(final long index) throws IOException {
		this.__index = (int)index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long index() throws IOException {
		return this.__index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() throws IOException {
		return this.__data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void allocate(final long value) throws IOException {
		final int size = this.__data.size();
		final int count = (int)value - size;
		if (count < 0) {
			this.__data.remove(size - count, count);
			this.__index = Math.min(this.__index, size - count);
		} else if (count > 0) {
			this.__data.insert(size, count);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
	}

}