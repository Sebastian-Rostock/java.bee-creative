package bee.creative.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link ByteBuffer}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BufferDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final ByteBuffer data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public BufferDataTarget(final ByteBuffer data) throws NullPointerException {
		this.data = Objects.notNull(data);
	}

	@Override
	public Object data() {
		return this.data;
	}

	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		try {
			this.data.put(array, offset, length);
		} catch (final BufferUnderflowException e) {
			throw new EOFException();
		} catch (final IndexOutOfBoundsException e) {
			throw new EOFException();
		}
	}

	@Override
	public void seek(final long index) throws IOException {
		if ((index < 0) || (index > this.length())) throw new IOException();
		try {
			this.data.position((int)index);
		} catch (final IllegalArgumentException cause) {
			throw new IOException(cause);
		}
	}

	@Override
	public long index() throws IOException {
		return this.data.position();
	}

	@Override
	public long length() throws IOException {
		return this.data.limit();
	}

}