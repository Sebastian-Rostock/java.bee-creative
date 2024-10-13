package bee.creative.kb;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/** Diese Klasse implementiert einen {@link DataInputStream}, der einen gegebenen {@link InputStream} über einen {@link Inflater} entpackt.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ZIPDIS extends DataInputStream {

	/** Dieser Konstruktor initialisiert den {@link InputStream} über {@link ByteArrayInputStream#ByteArrayInputStream(byte[]) new
	 * ByteArrayInputStream(bytes)}. */
	public ZIPDIS(byte[] bytes) throws IOException {
		this(new ByteArrayInputStream(bytes));
	}

	/** Dieser Konstruktor initialisiert den gegebenen {@link InputStream}. */
	public ZIPDIS(InputStream source) throws IOException {
		super(new InflaterInputStream(source, new Inflater(true), 524288));
	}

	/** Diese Methode ist eine Abkürzung für {@link #transferTo(OutputStream) this.transferTo(result)}. */
	public void getBytes(OutputStream result) throws IOException {
		this.transferTo(result);
	}

	/** Diese Methode ist eine Abkürzung für {@link #readAllBytes() this.readAllBytes()}. */
	public byte[] getBytes() throws IOException {
		return this.readAllBytes();
	}

}
