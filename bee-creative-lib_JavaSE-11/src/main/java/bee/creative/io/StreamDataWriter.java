package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert den {@link DataWriter} zu einem {@link OutputStream}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class StreamDataWriter extends DataWriter {

	/** Dieser Konstruktor initialisiert {@link #wrappedTarget()}. */
	public StreamDataWriter(OutputStream wrappedTarget) throws NullPointerException {
		this.wrappedTarget = notNull(wrappedTarget);
	}

	@Override
	public OutputStream wrappedTarget() {
		return this.wrappedTarget;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.wrappedTarget.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		this.wrappedTarget.close();
	}

	private final OutputStream wrappedTarget;

}