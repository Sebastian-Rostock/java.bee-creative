package bee.creative.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert erweitert einen {@link FilterOutputStream} um den {@link #getWriteCount() Zähler der geschriebenen Bytes}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingOutputStream extends FilterOutputStream {

	private long writeCount;

	/** Dieser Konstruktor initialisiert den {@link OutputStream}. */
	public CountingOutputStream(final OutputStream outputStream) {
		super(outputStream);
	}

	/** Diese Methode setzt die Anzahl der geschriebenen Bytes. */
	protected void setWriteCount(final long value) {
		this.writeCount = value;
	}

	/** Diese Methode gibt die Anzahl der geschriebenen Bytes zurück. */
	public long getWriteCount() {
		return this.writeCount;
	}

	/** {@inheritDoc} */
	@Override
	public void write(final int value) throws IOException {
		super.write(value);
		this.setWriteCount(this.writeCount + 1);
	}

	/** {@inheritDoc} */
	@Override
	public void write(final byte[] source, final int offset, final int length) throws IOException {
		super.write(source, offset, length);
		this.setWriteCount(this.writeCount + length);
	}

}
