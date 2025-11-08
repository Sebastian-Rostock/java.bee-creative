package bee.creative.io;

import java.io.IOException;
import java.io.Reader;

/** Diese Klasse erweitert einen {@link CharReader} um den {@link #getReadCount() Zähler der gelesenen bzw. ausgelassenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CountingCharReader extends CharReader {

	/** Dieser Konstruktor initialisiert den {@link Reader}. */
	public CountingCharReader(Reader reader) {
		super(reader);
	}

	/** Diese Methode gibt die Anzahl der gelesenen bzw. ausgelassenen Zeichen zurück. */
	public long getReadCount() {
		return this.readCount;
	}

	@Override
	public int read() throws IOException {
		var result = super.read();
		if (result < 0) return result;
		this.addReadCount(1);
		return result;
	}

	@Override
	public int read(char[] target, int offset, int length) throws IOException {
		var result = super.read(target, offset, length);
		if (result < 0) return result;
		this.addReadCount(result);
		return result;
	}

	@Override
	public long skip(long count) throws IOException {
		var result = super.skip(count);
		this.addReadCount(result);
		return result;
	}

	/** Diese Methode erhöht die Anzahl der gelesenen bzw. ausgelassenen Zeichen. */
	protected synchronized void addReadCount(long value) {
		this.readCount += value;
	}

	/** Diese Methode setzt die Anzahl der gelesenen bzw. ausgelassenen Zeichen. */
	protected synchronized void setReadCount(long value) {
		this.readCount = value;
	}

	@Override
	protected Reader wrappedSource() {
		return this;
	}

	private long readCount;

}
