package bee.creative.io;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.Writer;

/** Diese Klasse erweitert einen {@link FilterWriter} um {@code as...}-Methoden zur Umwandlung in andere {@link CharWriter}.
 * 
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CharWriter extends FilterWriter {

	/** Dieser Konstruktor initialisiert den {@link Writer}. */
	public CharWriter(Writer wrappedTarget) {
		super(wrappedTarget);
	}

	/** Diese Methode liefert einen auf diesem {@link CharWriter} aufsetzenden {@link BufferedWriter} als {@link CharWriter}. */
	public CharWriter asBufferedWriter() {
		return new CharWriter(new BufferedWriter(this.wrappedTarget()));
	}

	/** Diese Methode liefert einen auf diesem {@link CharWriter} aufsetzenden {@link BufferedWriter} mit der gegebenen Puffergröße als {@link CharWriter}. */
	public CharWriter asBufferedWriter(int bufferSize) {
		return new CharWriter(new BufferedWriter(this.wrappedTarget(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link CharWriter} aufsetzenden {@link CountingCharWriter}. */
	public CountingCharWriter asCountingWriter() {
		return new CountingCharWriter(this.wrappedTarget());
	}

	/** Diese Methode liefert einen auf diesem {@link CharWriter} aufsetzenden {@link UnclosableCharWriter}. */
	public UnclosableCharWriter asUnclosableWriter() {
		return new UnclosableCharWriter(this.wrappedTarget());
	}

	/** Diese Methode liefert den {@link Writer}, der in den {@code as...}-Methoden verpackt wird. */
	protected Writer wrappedTarget() {
		return this.out;
	}

}
