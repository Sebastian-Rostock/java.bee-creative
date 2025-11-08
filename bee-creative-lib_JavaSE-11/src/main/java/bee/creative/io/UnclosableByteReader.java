package bee.creative.io;

import java.io.IOException;
import java.io.InputStream;

/** Diese Klasse implementiert einen {@link ByteReader} mit deaktiviertem {@link #close()}.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class UnclosableByteReader extends ByteReader {

	/** Dieser Konstruktor initialisiert den {@link InputStream}. */
	public UnclosableByteReader(InputStream inputStream) {
		super(inputStream);
	}

	/** Diese Methode tut nichts. */
	@Override
	public void close() throws IOException {
	}

	@Override
	public UnclosableByteReader asUnclosableReader() throws IOException {
		return this;
	}

}
