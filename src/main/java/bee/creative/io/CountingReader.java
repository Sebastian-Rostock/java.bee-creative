package bee.creative.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/** Diese Klasse implementiert erweitert einen {@link FilterReader} um den {@link #getReadCount() Zähler der gelesenen bzw. ausgelassenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CountingReader extends FilterReader {

	private long readCount;

	/** Dieser Konstruktor initialisiert den {@link Reader}. */
	public CountingReader(final Reader reader) {
		super(reader);
	}

	/** Diese Methode setzt die Anzahl der gelesenen bzw. ausgelassenen Zeichen. */
	protected void setReadCount(final long value) {
		this.readCount = value;
	}

	/** Diese Methode gibt die Anzahl der gelesenen bzw. ausgelassenen Zeichen zurück. */
	public long getReadCount() {
		return this.readCount;
	}

	@Override
	public int read() throws IOException {
		final int result = super.read();
		if (result < 0) return result;
		this.setReadCount(this.readCount + 1);
		return result;
	}

	@Override
	public int read(final char[] target, final int offset, final int length) throws IOException {
		final int result = super.read(target, offset, length);
		if (result < 0) return result;
		this.setReadCount(this.readCount + result);
		return result;
	}

	@Override
	public long skip(final long count) throws IOException {
		final long result = super.skip(count);
		this.setReadCount(this.readCount + result);
		return result;
	}

}
