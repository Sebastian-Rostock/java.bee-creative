package bee.creative.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Diese Klasse implementiert erweitert einen {@link FilterInputStream} um den {@link #readCount() Zähler der gelesenen bzw. ausgelassenen Bytes}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingInputStream extends FilterInputStream {

	protected long readCount;

	/** Dieser Konstruktor initialisiert den {@link InputStream}. */
	public CountingInputStream(final InputStream inputStream) {
		super(inputStream);
	}

	/** Diese Methode gibt die Anzahl der gelesenen bzw. ausgelassenen Bytes zurück. */
	public long readCount() {
		return this.readCount;
	}

	/** {@inheritDoc} */
	@Override
	public int read() throws IOException {
		final int result = super.read();
		if (result < 0) return result;
		this.readCount++;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public int read(final byte[] target, final int offset, final int length) throws IOException {
		final int result = super.read(target, offset, length);
		if (result < 0) return result;
		this.readCount += result;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public long skip(final long count) throws IOException {
		final long result = super.skip(count);
		this.readCount += result;
		return result;
	}

}
