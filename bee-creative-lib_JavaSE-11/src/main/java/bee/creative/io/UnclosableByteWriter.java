package bee.creative.io;

import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert einen {@link ByteWriter} mit deaktiviertem {@link #close()}.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class UnclosableByteWriter extends ByteWriter {

	/** Dieser Konstruktor initialisiert den {@link OutputStream}. */
	public UnclosableByteWriter(final OutputStream outputStream) {
		super(outputStream);
	}

	/** Diese Methode tut nichts. */
	@Override
	public void close() throws IOException {
	}

	@Override
	public UnclosableByteWriter asUnclosableWriter() {
		return this;
	}

}
