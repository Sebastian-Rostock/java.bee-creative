package bee.creative.io;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.Reader;

/** Diese Klasse erweitert einen {@link FilterReader} um {@code as...}-Methoden zur Umwandlung in andere {@link CharReader}.
 * 
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CharReader extends FilterReader {

	/** Dieser Konstruktor initialisiert den {@link Reader}. */
	public CharReader(Reader wrappedSource) {
		super(wrappedSource);
	}

	/** Diese Methode liefert einen auf diesem {@link CharReader} aufsetzenden {@link BufferedReader} als {@link CharReader}. */
	public CharReader asBufferedReader() {
		return new CharReader(new BufferedReader(this.wrappedSource()));
	}

	/** Diese Methode liefert einen auf diesem {@link CharReader} aufsetzenden {@link BufferedReader} mit der gegebenen Puffergröße als {@link CharReader}. */
	public CharReader asBufferedReader(int bufferSize) {
		return new CharReader(new BufferedReader(this.wrappedSource(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link CharReader} aufsetzenden {@link CountingCharReader}. */
	public CountingCharReader asCountingReader() {
		return new CountingCharReader(this.wrappedSource());
	}

	/** Diese Methode liefert einen auf diesem {@link CharReader} aufsetzenden {@link UnclosableCharReader}. */
	public UnclosableCharReader asUnclosableReader() {
		return new UnclosableCharReader(this.wrappedSource());
	}

	/** Diese Methode liefert den {@link Reader}, der in den {@code as...}-Methoden verpackt wird. */
	protected Reader wrappedSource() {
		return this.in;
	}

}
