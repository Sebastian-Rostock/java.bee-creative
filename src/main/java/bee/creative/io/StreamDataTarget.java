package bee.creative.io;

import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link OutputStream}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class StreamDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final OutputStream data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public StreamDataTarget(final OutputStream data) throws NullPointerException {
		this.data = data;
	}

	@Override
	public Object data() {
		return this.data;
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		this.data.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		this.data.close();
	}

}