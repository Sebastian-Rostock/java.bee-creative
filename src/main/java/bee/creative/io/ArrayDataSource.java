package bee.creative.io;

import java.io.EOFException;
import java.io.IOException;
import bee.creative.array.ByteArray;
import bee.creative.array.ByteArraySection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einer {@link ByteArraySection}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final ByteArraySection data;

	/** Dieses Feld speichert die Leseposition. */
	protected int index;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayDataSource(final byte... data) throws NullPointerException {
		this.data = ByteArraySection.from(data);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayDataSource(final ByteArray data) throws NullPointerException {
		this.data = data.section();
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayDataSource(final ByteArraySection data) throws NullPointerException {
		this.data = Objects.notNull(data);
	}

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