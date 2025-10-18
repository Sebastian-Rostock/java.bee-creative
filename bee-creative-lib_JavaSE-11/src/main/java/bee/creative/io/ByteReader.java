package bee.creative.io;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.zip.GZIPInputStream;

public class ByteReader extends FilterInputStream {

	public ByteReader(InputStream wrappedSource) {
		super(wrappedSource);
	}

	public DataReader asDataReader() throws IOException {
		return new StreamDataReader(this.wrappedSource());
	}

	public ByteReader asGzipReader() throws IOException {
		return new ByteReader(new GZIPInputStream(this.wrappedSource()));
	}

	public ByteReader asGzipReader(int bufferSize) throws IOException {
		return new ByteReader(new GZIPInputStream(this.wrappedSource(), bufferSize));
	}

	public CharReader asCharReader() throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource()));
	}

	public CharReader asCharReader(Charset charset) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charset));
	}

	public CharReader asCharReader(String charsetName) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charsetName));
	}

	public CharReader asCharReader(CharsetDecoder charsetDecoder) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charsetDecoder));
	}

	public ByteReader asBufferedReader() throws IOException {
		return new ByteReader(new BufferedInputStream(this.wrappedSource()));
	}

	public ByteReader asBufferedReader(int bufferSize) throws IOException {
		return new ByteReader(new BufferedInputStream(this.wrappedSource(), bufferSize));
	}

	public CountingByteReader asCountingReader() throws IOException {
		return new CountingByteReader(this.wrappedSource());
	}

	protected InputStream wrappedSource() {
		return this.in;
	}

}
