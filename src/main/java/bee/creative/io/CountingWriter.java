package bee.creative.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/** Diese Klasse implementiert erweitert einen {@link FilterWriter} um den {@link #writeCount() Zähler der geschriebenen Zeichen}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class CountingWriter extends FilterWriter {

	protected long writeCount;

	/** Dieser Konstruktor initialisiert den {@link Writer}. */
	public CountingWriter(final Writer writer) {
		super(writer);
	}

	/** Diese Methode gibt die Anzahl der geschriebenen Zeichen zurück. */
	public long writeCount() {
		return this.writeCount;
	}

	@Override
	public void write(final int c) throws IOException {
		super.write(c);
		this.writeCount++;
	}

	@Override
	public void write(final String string, final int offset, final int length) throws IOException {
		super.write(string, offset, length);
		this.writeCount += length;
	}

	@Override
	public void write(final char[] chars, final int offset, final int length) throws IOException {
		super.write(chars, offset, length);
		this.writeCount += length;
	}

}
