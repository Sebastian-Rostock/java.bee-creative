package bee.creative.xml.bex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Diese Klasse implementiert ein {@link EncodeTarget}, das siene Schnittstelle an ein {@link RandomAccessFile} delegiert.
 * 
 * @see RandomAccessFile
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class FileEncodeTarget implements EncodeTarget {

	/**
	 * Dieses Feld speichert das {@link RandomAccessFile}.
	 */
	protected final RandomAccessFile file;

	/**
	 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen {@link File} im Modus {@code "rw"}.
	 * 
	 * @see RandomAccessFile#RandomAccessFile(File, String)
	 * @param file {@link File}.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
	 */
	public FileEncodeTarget(final File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "rw"));
	}

	/**
	 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen Dateinamen im Modus {@code "rw"}.
	 * 
	 * @see RandomAccessFile#RandomAccessFile(String, String)
	 * @param name Dateiname.
	 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
	 */
	public FileEncodeTarget(final String name) throws FileNotFoundException {
		this(new RandomAccessFile(name, "rw"));
	}

	/**
	 * Dieser Konstruktor initialisiert das {@link RandomAccessFile}.
	 * 
	 * @param file {@link RandomAccessFile}.
	 * @throws NullPointerException Wenn das {@link RandomAccessFile} {@code null} ist.
	 */
	public FileEncodeTarget(final RandomAccessFile file) throws NullPointerException {
		if(file == null) throw new NullPointerException();
		this.file = file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		this.file.write(array, offset, length);
	}

}
