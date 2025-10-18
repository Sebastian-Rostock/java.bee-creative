package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.RandomAccessFile;

/** Diese Klasse implementiert den {@link DataReader} zu einem {@link RandomAccessFile}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataReader extends DataReader {

	/** Dieser Konstruktor initialisiert die {@link #wrappedSource()}. */
	public FileDataReader(RandomAccessFile wrappedSource) throws NullPointerException {
		this.wrappedSource = notNull(wrappedSource);
	}

	@Override
	public RandomAccessFile wrappedSource() {
		return this.wrappedSource;
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		this.wrappedSource.readFully(b, off, len);
	}

	@Override
	public void seek(long index) throws IOException {
		this.wrappedSource.seek(index);
	}

	@Override
	public long index() throws IOException {
		return this.wrappedSource.getFilePointer();
	}

	@Override
	public long length() throws IOException {
		return this.wrappedSource.length();
	}

	@Override
	public void close() throws IOException {
		this.wrappedSource.close();
	}

	private final RandomAccessFile wrappedSource;

}