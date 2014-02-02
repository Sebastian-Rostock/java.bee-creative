package bee.creative.xml.bex;

import java.io.IOException;
import java.io.OutputStream;

public final class StreamEncodeTarget implements EncodeTarget {

	OutputStream stream;

	public StreamEncodeTarget(OutputStream stream) {
		this.stream = stream;
	}

	@Override
	public void write(byte[] array, int offset, int length) throws IOException {
		stream.write(array, offset, length);
	}

}
