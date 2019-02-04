package bee.creative.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einem {@link InputStream}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class StreamDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final InputStream data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public StreamDataSource(final InputStream data) throws NullPointerException {
		this.data = data;
	}

	/** {@inheritDoc} */
	@Override
	public Object data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] b, int off, int len) throws IOException {
		while (len != 0) {
			final int cnt = this.data.read(b, off, len);
			if (cnt < 0) throw new EOFException();
			off += cnt;
			len -= cnt;
		}
	}

	/** {@inheritDoc} */
	@Override
	public int skipBytes(final int n) throws IOException {
		return (int)this.data.skip(n);
	}

}