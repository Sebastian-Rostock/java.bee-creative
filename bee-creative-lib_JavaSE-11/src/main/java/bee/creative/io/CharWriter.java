package bee.creative.io;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.Writer;

public class CharWriter extends FilterWriter {

	public CharWriter(Writer wrappedTarget) {
		super(wrappedTarget);
	}

	public CharWriter asBufferedWriter() {
		return new CharWriter(new BufferedWriter(this.wrappedTarget()));
	}

	public CharWriter asBufferedWriter(int bufferSize) {
		return new CharWriter(new BufferedWriter(this.wrappedTarget(), bufferSize));
	}

	public CountingCharWriter asCountingWriter() {
		return new CountingCharWriter(this.wrappedTarget());
	}

	protected Writer wrappedTarget() {
		return this.out;
	}

}
