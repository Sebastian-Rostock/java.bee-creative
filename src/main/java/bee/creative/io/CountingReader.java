package bee.creative.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/** Diese Klasse implementiert erweitert einen {@link FilterReader} um den {@link #readCount() Zähler der gelesenen bzw. ausgelassenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingReader extends FilterReader {

	protected long readCount;

	/** Dieser Konstruktor initialisiert den {@link Reader}. */
	public CountingReader(final Reader reader) {
		super(reader);
	}

	/** Diese Methode gibt die Anzahl der gelesenen bzw. ausgelassenen Zeichen zurück. */
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
	public int read(final char[] chars, final int offset, final int length) throws IOException {
		final int result = super.read(chars, offset, length);
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
