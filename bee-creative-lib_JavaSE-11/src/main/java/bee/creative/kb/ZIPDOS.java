package bee.creative.kb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class ZIPDOS extends DataOutputStream {

	public ZIPDOS() throws IOException {
		this(524288);
	}

	public ZIPDOS(int size) throws IOException {
		this(new ByteArrayOutputStream(size));
	}

	public ZIPDOS(ByteArrayOutputStream target) throws IOException {
		this(target, Deflater.BEST_SPEED);
	}

	public ZIPDOS(ByteArrayOutputStream target, int level) throws IOException {
		super(new DeflaterOutputStream(target, new Deflater(level, true), 524288, false));
		this.target = target;
	}

	public void getBytes(OutputStream result) throws IOException {
		this.target.writeTo(result);
	}

	public byte[] getBytes() {
		return this.target.toByteArray();
	}

	private ByteArrayOutputStream target;

}
