package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** Diese Klasse implementiert den {@link DataReader} zu einem {@link InputStream}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class StreamDataReader extends DataReader {

	/** Dieser Konstruktor initialisiert die {@link #wrappedSource()}. */
	public StreamDataReader(InputStream wrappedSource) throws NullPointerException {
		this.wrappedSource = notNull(wrappedSource);
	}

	@Override
	public Object wrappedSource() {
		return this.wrappedSource;
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		while (len != 0) {
			var cnt = this.wrappedSource.read(b, off, len);
			if (cnt < 0) throw new EOFException();
			off += cnt;
			len -= cnt;
		}
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return (int)this.wrappedSource.skip(n);
	}

	@Override
	public void close() throws IOException {
	}

	private final InputStream wrappedSource;

}