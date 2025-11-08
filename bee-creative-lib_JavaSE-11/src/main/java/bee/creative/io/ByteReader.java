package bee.creative.io;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.zip.GZIPInputStream;

/** Diese Klasse erweitert einen {@link FilterInputStream} um {@code as...}-Methoden zur Umwandlung in andere {@link ByteReader}.
 * 
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ByteReader extends FilterInputStream {

	/** Dieser Konstruktor initialisiert den {@link InputStream}. */
	public ByteReader(InputStream wrappedSource) {
		super(wrappedSource);
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link InputStreamReader} als {@link CharReader}. */
	public CharReader asCharReader() throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link InputStreamReader} mit dem gegebenen {@link Charset} als
	 * {@link CharReader}. */
	public CharReader asCharReader(Charset charset) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charset));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link InputStreamReader} mit dem gegebenen Zeichensatz als {@link CharReader}. */
	public CharReader asCharReader(String charsetName) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charsetName));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link InputStreamReader} mit dem gegebenen {@link CharsetDecoder} als
	 * {@link CharReader}. */
	public CharReader asCharReader(CharsetDecoder charsetDecoder) throws IOException {
		return new CharReader(new InputStreamReader(this.wrappedSource(), charsetDecoder));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link DataReader}. */
	public DataReader asDataReader() throws IOException {
		return new StreamDataReader(this.wrappedSource());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link GZIPInputStream} als {@link ByteReader}. */
	public ByteReader asGzipReader() throws IOException {
		return new ByteReader(new GZIPInputStream(this.wrappedSource()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link GZIPInputStream} mit der gegebenen Puffergröße als {@link ByteReader}. */
	public ByteReader asGzipReader(int bufferSize) throws IOException {
		return new ByteReader(new GZIPInputStream(this.wrappedSource(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link DZIPInputStream}. */
	public DZIPInputStream asDzipReader() throws IOException {
		return new DZIPInputStream(this.wrappedSource());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link DZIPInputStream} mit der gegebenen {@link ByteOrder}. */
	public DZIPInputStream asDzipReader(ByteOrder order) throws IOException {
		return new DZIPInputStream(this.wrappedSource(), order);
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link BufferedInputStream} als {@link ByteReader}. */
	public ByteReader asBufferedReader() throws IOException {
		return new ByteReader(new BufferedInputStream(this.wrappedSource()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link BufferedInputStream} mit der gegebenen Puffergröße als
	 * {@link ByteReader}. */
	public ByteReader asBufferedReader(int bufferSize) throws IOException {
		return new ByteReader(new BufferedInputStream(this.wrappedSource(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link CountingByteReader}. */
	public CountingByteReader asCountingReader() throws IOException {
		return new CountingByteReader(this.wrappedSource());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteReader} aufsetzenden {@link UnclosableByteReader}. */
	public UnclosableByteReader asUnclosableReader() throws IOException {
		return new UnclosableByteReader(this.wrappedSource());
	}

	/** Diese Methode liefert den {@link InputStream}, der in den {@code as...}-Methoden verpackt wird. */
	protected InputStream wrappedSource() {
		return this.in;
	}

}
