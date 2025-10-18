package bee.creative.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Diese Klasse implementiert erweitert einen {@link FilterInputStream} um den {@link #getReadCount() Zähler der gelesenen bzw. ausgelassenen Bytes}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CountingByteReader extends ByteReader {

	/** Dieser Konstruktor initialisiert den {@link InputStream}. */
	public CountingByteReader(InputStream inputStream) {
		super(inputStream);
	}

	/** Diese Methode gibt die Anzahl der gelesenen bzw. ausgelassenen Bytes zurück. */
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
	public int read(byte[] target, int offset, int length) throws IOException {
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

	/** Diese Methode erhöht die Anzahl der gelesenen bzw. ausgelassenen Bytes. */
	protected synchronized void addReadCount(long value) {
		this.readCount += value;
	}

	/** Diese Methode setzt die Anzahl der gelesenen bzw. ausgelassenen Bytes. */
	protected synchronized void setReadCount(long value) {
		this.readCount = value;
	}

	@Override
	protected InputStream wrappedSource() {
		return this;
	}

	private long readCount;

}
