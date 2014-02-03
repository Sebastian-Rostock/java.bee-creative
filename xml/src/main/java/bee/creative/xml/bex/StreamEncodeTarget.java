package bee.creative.xml.bex;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Diese Klasse implementiert ein {@link EncodeTarget}, das siene Schnittstelle an einen {@link OutputStream} delegiert.
 * 
 * @see FileOutputStream
 * @see ByteArrayOutputStream
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class StreamEncodeTarget implements EncodeTarget {

	/**
	 * Dieses Feld speichert den {@link OutputStream}.
	 */
	protected final OutputStream stream;

	/**
	 * Dieser Konstruktor initialisiert den {@link OutputStream}.
	 * 
	 * @param stream {@link OutputStream}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public StreamEncodeTarget(final OutputStream stream) throws NullPointerException {
		if(stream == null) throw new NullPointerException();
		this.stream = stream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		this.stream.write(array, offset, length);
	}

}
