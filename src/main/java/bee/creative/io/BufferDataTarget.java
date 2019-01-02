package bee.creative.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public  class BufferDataTarget extends BaseDataTarget {

	protected final ByteBuffer data;

	public BufferDataTarget(ByteBuffer data) {
		this.data = data.slice().order(ByteOrder.BIG_ENDIAN);
	}

	@Override
	public Object data() {
		return data;
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		data.put(b, off, len);
	}

	@Override
	public void seek(final long index) throws IOException {
		try {
			data.position((int)index);
		} catch (final IllegalArgumentException cause) {
			throw new IOException(cause);
		}
	}

	@Override
	public long index() throws IOException {
		return data.position();
	}

	@Override
	public long length() throws IOException {
		return data.limit();
	}
}