package bee.creative.io;

import java.io.IOException;
import java.io.Writer;

/** Diese Klasse erweitert einen {@link CharWriter} um den {@link #getWriteCount() Zähler der geschriebenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CountingCharWriter extends CharWriter {

	/** Dieser Konstruktor initialisiert den {@link Writer}. */
	public CountingCharWriter(Writer writer) {
		super(writer);
	}

	/** Diese Methode gibt die Anzahl der geschriebenen Zeichen zurück. */
	public long getWriteCount() {
		return this.writeCount;
	}

	@Override
	public void write(int value) throws IOException {
		super.write(value);
		this.addWriteCount(1);
	}

	@Override
	public void write(String source, int offset, int length) throws IOException {
		super.write(source, offset, length);
		this.addWriteCount(length);
	}

	@Override
	public void write(char[] source, int offset, int length) throws IOException {
		super.write(source, offset, length);
		this.addWriteCount(length);
	}

	/** Diese Methode erhöht die Anzahl der geschriebenen Zeichen. */
	protected synchronized void addWriteCount(long value) {
		this.writeCount += value;
	}

	/** Diese Methode setzt die Anzahl der geschriebenen Zeichen. */
	protected synchronized void setWriteCount(long value) {
		this.writeCount = value;
	}

	@Override
	protected Writer wrappedTarget() {
		return this;
	}

	private long writeCount;

}
