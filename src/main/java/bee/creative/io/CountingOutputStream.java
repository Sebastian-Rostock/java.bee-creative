package bee.creative.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert erweitert einen {@link FilterOutputStream} um den {@link #writeCount() Zähler der geschriebenen Bytes}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingOutputStream extends FilterOutputStream {

	protected long writeCount;

	/** Dieser Konstruktor initialisiert den {@link OutputStream}. */
	public CountingOutputStream(final OutputStream outputStream) {
		super(outputStream);
	}

	{}

	/** Diese Methode gibt die Anzahl der geschriebenen Bytes zurück. */
	public long writeCount() {
		return this.writeCount;
	}

	{}

	@Override
	public void write(int c) throws IOException {
		super.write(c);
		this.writeCount++;
	}

	@Override
	public void write(byte[] chars, int offset, int length) throws IOException {
		super.write(chars, offset, length);
		this.writeCount += length;
	}

}
