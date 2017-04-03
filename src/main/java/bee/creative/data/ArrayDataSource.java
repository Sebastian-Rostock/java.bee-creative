package bee.creative.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.array.ByteArraySection;
import bee.creative.util.Bytes;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einer {@link ByteArraySection}. Die Dekidierung der Zahlen erfolgt via {@link Bytes} und
 * damit in {@link ByteOrder#BIG_ENDIAN}.
 *
 * @see ByteArraySection
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	final ByteArraySection data;

	/** Dieses Feld speichert die Leseposition. */
	int index;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public ArrayDataSource(final byte... data) throws NullPointerException {
		this.data = ByteArraySection.from(data);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public ArrayDataSource(final ByteArraySection data) throws NullPointerException {
		this.data = Objects.assertNotNull(data);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public ByteArraySection data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		final ByteArraySection data = this.data;
		final int index = this.index, index2 = index + length;
		if (index2 > data.size()) throw new EOFException();
		System.arraycopy(data.array(), data.startIndex() + index, array, offset, length);
		this.index = index2;
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.index = (int)index;
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.index;
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.data.size();
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
	}

}