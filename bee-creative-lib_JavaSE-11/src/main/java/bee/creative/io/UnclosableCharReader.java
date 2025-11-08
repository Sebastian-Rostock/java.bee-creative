package bee.creative.io;

import java.io.IOException;
import java.io.Reader;

/** Diese Klasse implementiert einen {@link CharReader} mit deaktiviertem {@link #close()}.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class UnclosableCharReader extends CharReader {

	/** Dieser Konstruktor initialisiert den {@link Reader}. */
	public UnclosableCharReader(Reader reader) {
		super(reader);
	}

	/** Diese Methode tut nichts. */
	@Override
	public void close() throws IOException {
	}

	@Override
	public UnclosableCharReader asUnclosableReader() {
		return this;
	}

}
