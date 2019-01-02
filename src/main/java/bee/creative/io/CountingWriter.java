package bee.creative.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/** Diese Klasse implementiert erweitert einen {@link FilterWriter} um den {@link #getWriteCount() Zähler der geschriebenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingWriter extends FilterWriter {

	private long writeCount;

	/** Dieser Konstruktor initialisiert den {@link Writer}. */
	public CountingWriter(final Writer writer) {
		super(writer);
	}

	/** Diese Methode setzt die Anzahl der geschriebenen Zeichen. */
	protected void setWriteCount(final long value) {
		this.writeCount = value;
	}

	/** Diese Methode gibt die Anzahl der geschriebenen Zeichen zurück. */
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
	public void write(final String source, final int offset, final int length) throws IOException {
		super.write(source, offset, length);
		this.setWriteCount(this.writeCount + length);
	}

	/** {@inheritDoc} */
	@Override
	public void write(final char[] source, final int offset, final int length) throws IOException {
		super.write(source, offset, length);
		this.setWriteCount(this.writeCount + length);
	}

}
