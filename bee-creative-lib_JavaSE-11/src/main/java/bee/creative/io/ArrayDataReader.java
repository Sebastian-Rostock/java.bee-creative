package bee.creative.io;

import java.io.EOFException;
import java.io.IOException;
import bee.creative.array.ByteArray;
import bee.creative.array.ByteArraySection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert den {@link DataReader} zu einer {@link ByteArray}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataReader extends DataReader {

	/** Dieser Konstruktor initialisiert die {@link #wrappedSource()}. */
	public ArrayDataReader(ByteArray wrappedSource) throws NullPointerException {
		this.wrappedSource = Objects.notNull(wrappedSource);
	}

	@Override
	public ByteArray wrappedSource() {
		return this.wrappedSource;
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		if ((this.index + len) > this.length()) throw new EOFException();
		this.wrappedSource.getAll(this.index, ByteArraySection.from(b, off, len));
		this.index += len;
	}

	@Override
	public void seek(long index) throws IOException {
		if ((index < 0) || (this.index > this.length())) throw new IOException();
		this.index = (int)index;
	}

	@Override
	public long index() throws IOException {
		return this.index;
	}

	@Override
	public long length() throws IOException {
		return this.wrappedSource.size();
	}

	@Override
	public void close() throws IOException {
	}

	private final ByteArray wrappedSource;

	private int index;

}