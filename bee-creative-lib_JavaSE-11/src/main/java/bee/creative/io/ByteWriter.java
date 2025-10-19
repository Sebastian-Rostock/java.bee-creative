package bee.creative.io;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.zip.GZIPOutputStream;

public class ByteWriter extends FilterOutputStream {

	public ByteWriter(OutputStream wrappedTarget) {
		super(wrappedTarget);
	}

	public ByteWriter asGzipWriter() throws IOException {
		return new ByteWriter(new GZIPOutputStream(this.wrappedTarget()));
	}

	public ByteWriter asGzipWriter(int bufferSize) throws IOException {
		return new ByteWriter(new GZIPOutputStream(this.wrappedTarget(), bufferSize));
	}

	public DZIPOutputStream asDzipWriter() throws IOException {
		return new DZIPOutputStream(this.wrappedTarget());
	}

	public DZIPOutputStream asDzipWriter(int level) throws IOException {
		return new DZIPOutputStream(this.wrappedTarget(), level);
	}

	public DZIPOutputStream asDzipWriter(int level, ByteOrder order) throws IOException {
		return new DZIPOutputStream(this.wrappedTarget(), level, order);
	}

	public CharWriter asCharWriter() throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget()));
	}

	public CharWriter asCharWriter(Charset charset) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charset));
	}

	public CharWriter asCharWriter(String charsetName) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charsetName));
	}

	public CharWriter asCharWriter(CharsetEncoder charsetEncoder) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charsetEncoder));
	}

	public ByteWriter asBufferedWriter() {
		return new ByteWriter(new BufferedOutputStream(this.wrappedTarget()));
	}

	public ByteWriter asBufferedWriter(int bufferSize) {
		return new ByteWriter(new BufferedOutputStream(this.wrappedTarget(), bufferSize));
	}

	public CountingByteWriter asCountingWriter() {
		return new CountingByteWriter(this.wrappedTarget());
	}

	protected OutputStream wrappedTarget() {
		return this.out;
	}

}
