package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/** Diese Klasse implementiert den {@link DataReader} zu einem {@link ByteBuffer}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BufferDataReader extends DataReader {

	/** Dieser Konstruktor initialisiert die {@link #wrappedSource()}. */
	public BufferDataReader(ByteBuffer wrappedSource) throws NullPointerException {
		this.wrappedSource = notNull(wrappedSource);
	}

	@Override
	public ByteBuffer wrappedSource() {
		return this.wrappedSource;
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		try {
			this.wrappedSource.get(b, off, len);
		} catch (BufferUnderflowException | IndexOutOfBoundsException cause) {
			throw new EOFException();
		}
	}

	@Override
	public void seek(long index) throws IOException {
		if ((index < 0) || (index > this.length())) throw new IOException();
		try {
			this.wrappedSource.position((int)index);
		} catch (IllegalArgumentException cause) {
			throw new IOException(cause);
		}
	}

	@Override
	public long index() throws IOException {
		return this.wrappedSource.position();
	}

	@Override
	public long length() throws IOException {
		return this.wrappedSource.limit();
	}

	@Override
	public void close() throws IOException {
	}

	private final ByteBuffer wrappedSource;

}