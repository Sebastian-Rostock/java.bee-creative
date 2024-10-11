package bee.creative.str;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class BINWRITER extends DataOutputStream {

	public BINWRITER() throws IOException {
		this(512 * 1024);
	}

	public BINWRITER(int size) throws IOException {
		this(new ByteArrayOutputStream(size));
	}

	public BINWRITER(ByteArrayOutputStream target) throws IOException {
		super(new DeflaterOutputStream(target, new Deflater(Deflater.BEST_SPEED, true), 512 * 1024, false));
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