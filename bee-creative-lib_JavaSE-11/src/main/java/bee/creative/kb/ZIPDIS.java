package bee.creative.kb;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ZIPDIS extends DataInputStream {

	public ZIPDIS(byte[] bytes) throws IOException {
		this(new ByteArrayInputStream(bytes));
	}

	public ZIPDIS(ByteArrayInputStream source) throws IOException {
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
