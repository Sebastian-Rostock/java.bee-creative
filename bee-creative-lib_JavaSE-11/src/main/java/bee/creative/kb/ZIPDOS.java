package bee.creative.kb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/** Diese Klasse implementiert einen {@link DataOutputStream}, der einen gegebenen {@link ByteArrayOutputStream} über einen {@link Deflater} bestückt.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ZIPDOS extends DataOutputStream {

	/** Diese Methode liefert die aus der gegebene Bytefolge {@code bytes} gepackte Bytefolge. */
	public static byte[] deflate(byte[] bytes) throws IOException {
		try (var stream = new ZIPDOS()) {
			stream.write(bytes);
			stream.flush();
			return stream.getBytes();
		}
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit einer Kapazität von 512 KB und den {@link Deflater} mt
	 * {@link Deflater#DEFAULT_COMPRESSION}. */
	public ZIPDOS() throws IOException {
		this(524288);
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit der gegebenen Kapazität und den {@link Deflater} mt
	 * {@link Deflater#DEFAULT_COMPRESSION}. */
	public ZIPDOS(int size) throws IOException {
		this(new ByteArrayOutputStream(size));
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit dem gegebenen und den {@link Deflater} mt {@link Deflater#DEFAULT_COMPRESSION}. */
	public ZIPDOS(ByteArrayOutputStream target) throws IOException {
		this(target, Deflater.DEFAULT_COMPRESSION);
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit dem gegebenen und den {@link Deflater} mt der gegebenen Kompressionsstufe
	 * ({@link Deflater#DEFAULT_COMPRESSION}, {@link Deflater#NO_COMPRESSION}..{@link Deflater#BEST_COMPRESSION}). */
	public ZIPDOS(ByteArrayOutputStream target, int level) throws IOException {
		super(new DeflaterOutputStream(target, new Deflater(level, true), 524288, true));
		this.target = target;
	}

	/** Diese Methode delegiert an {@link ByteArrayOutputStream#writeTo(OutputStream) writeTo(result)} des im Konstruktor gegebenen
	 * {@link ByteArrayOutputStream}. */
	public void getBytes(OutputStream result) throws IOException {
		this.target.writeTo(result);
	}

	/** Diese Methode delegiert an {@link ByteArrayOutputStream#toByteArray() toByteArray()} des im Konstruktor gegebenen {@link ByteArrayOutputStream}. */
	public byte[] getBytes() {
		return this.target.toByteArray();
	}

	private ByteArrayOutputStream target;

}
