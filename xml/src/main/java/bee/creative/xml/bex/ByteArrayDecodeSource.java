package bee.creative.xml.bex;

import java.io.IOException;

public final class ByteArrayDecodeSource implements DecodeSource {

	final byte[] data;

	int index;

	public ByteArrayDecodeSource(final byte[] data) {
		this.data = data;
		this.index = 0;
	}

	@Override
	public void read(final byte[] array, final int offset, final int length) throws IOException {
		final int index = this.index;
		System.arraycopy(this.data, index, this.data, offset, length);
		this.index = index + length;
	}

	@Override
	public void seek(final long index) throws IOException {
		this.index = (int)index;
	}

	@Override
	public long index() throws IOException {
		return this.index;
	}

}
