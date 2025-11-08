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

/** Diese Klasse erweitert einen {@link FilterOutputStream} um {@code as...}-Methoden zur Umwandlung in andere {@link ByteWriter}.
 * 
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ByteWriter extends FilterOutputStream {

	/** Dieser Konstruktor initialisiert den {@link OutputStream}. */
	public ByteWriter(OutputStream wrappedTarget) {
		super(wrappedTarget);
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link OutputStreamWriter} als {@link CharWriter}. */
	public CharWriter asCharWriter() throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link OutputStreamWriter} mit dem gegebenen {@link Charset} als
	 * {@link CharWriter}. */
	public CharWriter asCharWriter(Charset charset) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charset));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link OutputStreamWriter} mit dem gegebenen Zeichensatz als {@link CharWriter}. */
	public CharWriter asCharWriter(String charsetName) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charsetName));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link OutputStreamWriter} mit dem gegebenen {@link CharsetEncoder} als
	 * {@link CharWriter}. */
	public CharWriter asCharWriter(CharsetEncoder charsetEncoder) throws IOException {
		return new CharWriter(new OutputStreamWriter(this.wrappedTarget(), charsetEncoder));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link StreamDataWriter}. */
	public StreamDataWriter asDataWriter() throws IOException {
		return new StreamDataWriter(this.wrappedTarget());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link GZIPOutputStream} als {@link ByteWriter}. */
	public ByteWriter asGzipWriter() throws IOException {
		return new ByteWriter(new GZIPOutputStream(this.wrappedTarget()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link GZIPOutputStream} mit der gegebenen Puffergröße als {@link ByteWriter}. */
	public ByteWriter asGzipWriter(int bufferSize) throws IOException {
		return new ByteWriter(new GZIPOutputStream(this.wrappedTarget(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link DZIPOutputStream}. */
	public DZIPOutputStream asDzipWriter() throws IOException {
		return new DZIPOutputStream(this.wrappedTarget());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link DZIPOutputStream} mit der gegebenen Kompressionsstufe. */
	public DZIPOutputStream asDzipWriter(int level) throws IOException {
		return new DZIPOutputStream(this.wrappedTarget(), level);
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link DZIPOutputStream} mit der gegebenen Kompressionsstufe und der gegebenen
	 * {@link ByteOrder}. */
	public DZIPOutputStream asDzipWriter(int level, ByteOrder order) throws IOException {
		return new DZIPOutputStream(this.wrappedTarget(), level, order);
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link BufferedOutputStream} als {@link ByteWriter}. */
	public ByteWriter asBufferedWriter() {
		return new ByteWriter(new BufferedOutputStream(this.wrappedTarget()));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link BufferedOutputStream} mit der gegebenen Puffergröße als
	 * {@link ByteWriter}. */
	public ByteWriter asBufferedWriter(int bufferSize) {
		return new ByteWriter(new BufferedOutputStream(this.wrappedTarget(), bufferSize));
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link CountingByteWriter}. */
	public CountingByteWriter asCountingWriter() {
		return new CountingByteWriter(this.wrappedTarget());
	}

	/** Diese Methode liefert einen auf diesem {@link ByteWriter} aufsetzenden {@link UnclosableByteWriter}. */
	public UnclosableByteWriter asUnclosableWriter() {
		return new UnclosableByteWriter(this.wrappedTarget());
	}

	/** Diese Methode liefert den {@link OutputStream}, der in den {@code as...}-Methoden verpackt wird. */
	protected OutputStream wrappedTarget() {
		return this.out;
	}

}
