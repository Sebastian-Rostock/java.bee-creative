package bee.creative.io;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.Reader;

public class CharReader extends FilterReader {

	public CharReader(Reader wrappedSource) {
		super(wrappedSource);
	}

	public CharReader asBufferedReader() {
		return new CharReader(new BufferedReader(this.wrappedSource()));
	}

	public CharReader asBufferedReader(int bufferSize) {
		return new CharReader(new BufferedReader(this.wrappedSource(), bufferSize));
	}

	public CountingCharReader asCountingReader() {
		return new CountingCharReader(this.wrappedSource());
	}

	protected Reader wrappedSource() {
		return this.in;
	}

}
