package bee.creative.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.array.ByteArraySection;
import bee.creative.util.Bytes;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einer {@link ByteArraySection}. Die Dekidierung der Zahlen erfolgt via {@link Bytes} und
 * damit in {@link ByteOrder#BIG_ENDIAN}.
 * 
 * @see ByteArraySection
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	final ByteArraySection __data;

	/** Dieses Feld speichert die Leseposition. */
	int __index;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public ArrayDataSource(final byte... data) throws NullPointerException {
		this.__data = ByteArraySection.from(data);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public ArrayDataSource(final ByteArraySection data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		this.__data = data;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public ByteArraySection data() {
		return this.__data;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		final ByteArraySection data = this.__data;
		final int index = this.__index, index2 = index + length;
		if (index2 > data.size()) throw new EOFException();
		System.arraycopy(data.array(), data.startIndex() + index, array, offset, length);
		this.__index = index2;
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.__index = (int)index;
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.__index;
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.__data.size();
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
	}

}