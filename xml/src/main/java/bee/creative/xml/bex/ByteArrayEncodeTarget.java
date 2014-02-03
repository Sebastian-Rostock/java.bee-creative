package bee.creative.xml.bex;

import java.io.ByteArrayOutputStream;

/**
 * Diese Klasse implementiert ein {@link StreamEncodeTarget} mit internem {@link ByteArrayOutputStream}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class ByteArrayEncodeTarget extends StreamEncodeTarget {

	/**
	 * Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream}.
	 * 
	 * @see ByteArrayOutputStream#ByteArrayOutputStream()
	 */
	public ByteArrayEncodeTarget() {
		super(new ByteArrayOutputStream());
	}

	/**
	 * Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream}.
	 * 
	 * @see ByteArrayOutputStream#ByteArrayOutputStream(int)
	 * @param size Puffergröße.
	 */
	public ByteArrayEncodeTarget(final int size) {
		super(new ByteArrayOutputStream(size));
	}

	/**
	 * Diese Methode gibt eine Kopie des internen {@code byte}-Arrays zurück.
	 * 
	 * @see ByteArrayOutputStream#toByteArray()
	 * @return {@code byte}-Array.
	 */
	public byte[] toByteArray() {
		return ((ByteArrayOutputStream)this.stream).toByteArray();
	}

}
