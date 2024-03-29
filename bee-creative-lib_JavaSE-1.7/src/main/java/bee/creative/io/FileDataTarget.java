package bee.creative.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link RandomAccessFile}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final RandomAccessFile data;

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen {@link File} im Modus {@code "rw"}.
	 *
	 * @see RandomAccessFile#RandomAccessFile(File, String)
	 * @param file {@link File}.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist. */
	public FileDataTarget(final File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "rw"));
	}

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen Dateinamen im Modus {@code "rw"}.
	 *
	 * @see RandomAccessFile#RandomAccessFile(String, String)
	 * @param name Dateiname.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist. */
	public FileDataTarget(final String name) throws FileNotFoundException {
		this(new RandomAccessFile(name, "rw"));
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public FileDataTarget(final RandomAccessFile data) throws NullPointerException {
		this.data = Objects.notNull(data);
	}

	@Override
	public RandomAccessFile data() {
		return this.data;
	}

	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		this.data.write(array, offset, length);
	}

	@Override
	public void seek(final long index) throws IOException {
		this.data.seek(index);
	}

	@Override
	public long index() throws IOException {
		return this.data.getFilePointer();
	}

	@Override
	public long length() throws IOException {
		return this.data.length();
	}

	@Override
	public void allocate(final long value) throws IOException {
		this.data.setLength(value);
	}

	@Override
	public void close() throws IOException {
		this.data.close();
	}

}