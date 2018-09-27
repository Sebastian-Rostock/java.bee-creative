package bee.creative.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einem {@link RandomAccessFile}.
 *
 * @see RandomAccessFile
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	final RandomAccessFile data;

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen {@link File} im Modus {@code "r"}.
	 *
	 * @see RandomAccessFile#RandomAccessFile(File, String)
	 * @param file {@link File}.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist. */
	public FileDataSource(final File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "r"));
	}

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen Dateinamen im Modus {@code "r"}.
	 *
	 * @see RandomAccessFile#RandomAccessFile(String, String)
	 * @param name Dateiname.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist. */
	public FileDataSource(final String name) throws FileNotFoundException {
		this(new RandomAccessFile(name, "r"));
	}

	/** Dieser Konstruktor initialisiert das {@link RandomAccessFile}.
	 *
	 * @param file {@link RandomAccessFile}.
	 * @throws NullPointerException Wenn das {@link RandomAccessFile} {@code null} ist. */
	public FileDataSource(final RandomAccessFile file) throws NullPointerException {
		this.data = Objects.notNull(file);
	}

	/** {@inheritDoc} */
	@Override
	public final RandomAccessFile data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		this.data.readFully(array, offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.data.seek(index);
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.data.getFilePointer();
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.data.length();
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		this.data.close();
	}

}