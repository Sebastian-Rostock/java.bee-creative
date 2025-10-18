package bee.creative.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Diese Klasse implementiert erweitert einen {@link FilterOutputStream} um den {@link #getWriteCount() Zähler der geschriebenen Bytes}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CountingByteWriter extends ByteWriter {

	/** Dieser Konstruktor initialisiert den {@link OutputStream}. */
	public CountingByteWriter(final OutputStream outputStream) {
		super(outputStream);
	}

	/** Diese Methode gibt die Anzahl der geschriebenen Bytes zurück. */
	public long getWriteCount() {
		return this.writeCount;
	}

	@Override
	public void write(int value) throws IOException {
		super.write(value);
		this.addWriteCount(1);
	}

	@Override
	public void write(byte[] source, int offset, int length) throws IOException {
		super.write(source, offset, length);
		this.addWriteCount(length);
	}

	/** Diese Methode erhöht die Anzahl der geschriebenen Bytes. */
	protected synchronized void addWriteCount(long value) {
		this.writeCount += value;
	}

	/** Diese Methode setzt die Anzahl der geschriebenen Bytes. */
	protected synchronized void setWriteCount(long value) {
		this.writeCount = value;
	}

	@Override
	protected OutputStream wrappedTarget() {
		return this;
	}

	private long writeCount;

}
