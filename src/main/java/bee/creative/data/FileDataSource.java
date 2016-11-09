package bee.creative.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/** Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einem {@link RandomAccessFile}.
 *
 * @see RandomAccessFile
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	final RandomAccessFile _data_;

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
		if (file == null) throw new NullPointerException("file = null");
		this._data_ = file;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final RandomAccessFile data() {
		return this._data_;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		this._data_.readFully(array, offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this._data_.seek(index);
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this._data_.getFilePointer();
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this._data_.length();
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		this._data_.close();
	}

}