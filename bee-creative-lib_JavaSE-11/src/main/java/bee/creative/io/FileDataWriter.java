package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.RandomAccessFile;

/** Diese Klasse implementiert den {@link DataWriter} zu einem {@link RandomAccessFile}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataWriter extends DataWriter {

	/** Dieser Konstruktor initialisiert das {@link #wrappedTarget()}. */
	public FileDataWriter(RandomAccessFile wrappedTarget) throws NullPointerException {
		this.wrappedTarget = notNull(wrappedTarget);
	}

	@Override
	public RandomAccessFile wrappedTarget() {
		return this.wrappedTarget;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.wrappedTarget.write(b, off, len);
	}

	@Override
	public void seek(long index) throws IOException {
		this.wrappedTarget.seek(index);
	}

	@Override
	public long index() throws IOException {
		return this.wrappedTarget.getFilePointer();
	}

	@Override
	public long length() throws IOException {
		return this.wrappedTarget.length();
	}

	@Override
	public void allocate(long value) throws IOException {
		this.wrappedTarget.setLength(value);
	}

	@Override
	public void close() throws IOException {
		this.wrappedTarget.close();
	}

	private final RandomAccessFile wrappedTarget;

}