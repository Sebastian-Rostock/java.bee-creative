package bee.creative.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link RandomAccessFile}.
 * 
 * @see RandomAccessFile
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	final RandomAccessFile __data;

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

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile}.
	 * 
	 * @param file {@link RandomAccessFile}.
	 * @throws NullPointerException Wenn das {@link RandomAccessFile} {@code null} ist. */
	public FileDataTarget(final RandomAccessFile file) throws NullPointerException {
		if (file == null) throw new NullPointerException("file = null");
		this.__data = file;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final RandomAccessFile data() {
		return this.__data;
	}

	/** {@inheritDoc} */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		this.__data.write(array, offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.__data.seek(index);
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.__data.getFilePointer();
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.__data.length();
	}

	/** {@inheritDoc} */
	@Override
	public void allocate(final long value) throws IOException {
		this.__data.setLength(value);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		this.__data.close();
	}

}