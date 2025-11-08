package bee.creative.io;

import java.io.IOException;
import java.io.Writer;

/** Diese Klasse implementiert einen {@link CharWriter} mit deaktiviertem {@link #close()}.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class UnclosableCharWriter extends CharWriter {

	/** Dieser Konstruktor initialisiert den {@link Writer}. */
	public UnclosableCharWriter(Writer writer) {
		super(writer);
	}

	/** Diese Methode tut nichts. */
	@Override
	public void close() throws IOException {
	}

	@Override
	public UnclosableCharWriter asUnclosableWriter() {
		return this;
	}

}
