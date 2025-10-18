package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/** Diese Klasse implementiert den {@link DataWriter} zu einem {@link ByteBuffer}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BufferDataWriter extends DataWriter {

	/** Dieser Konstruktor initialisiert das {@link #wrappedTarget()}. **/
	public BufferDataWriter(final ByteBuffer wrappedTarget) throws NullPointerException {
		this.wrappedTarget = notNull(wrappedTarget);
	}

	@Override
	public Object wrappedTarget() {
		return this.wrappedTarget;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			this.wrappedTarget.put(b, off, len);
		} catch (BufferUnderflowException | IndexOutOfBoundsException cause) {
			throw new EOFException();
		}
	}

	@Override
	public void seek(long index) throws IOException {
		if ((index < 0) || (index > this.length())) throw new IOException();
		try {
			this.wrappedTarget.position((int)index);
		} catch (IllegalArgumentException cause) {
			throw new IOException(cause);
		}
	}

	@Override
	public long index() throws IOException {
		return this.wrappedTarget.position();
	}

	@Override
	public long length() throws IOException {
		return this.wrappedTarget.limit();
	}

	@Override
	public void close() throws IOException {
	}

	private final ByteBuffer wrappedTarget;

}