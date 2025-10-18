package bee.creative.io;

import static bee.creative.lang.Objects.notNull;
import java.io.IOException;
import bee.creative.array.ByteArray;
import bee.creative.array.ByteArraySection;

/** Diese Klasse implementiert den {@link DataWriter} zu einem {@link ByteArray}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataWriter extends DataWriter {

	/** Dieser Konstruktor initialisiert das {@link #wrappedTarget()}. **/
	public ArrayDataWriter(ByteArray wrappedTarget) throws NullPointerException {
		this.wrappedTarget = notNull(wrappedTarget);
	}

	@Override
	public ByteArray wrappedTarget() {
		return this.wrappedTarget;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.wrappedTarget.addAll(this.index, ByteArraySection.from(b, off, len));
		this.index += len;
	}

	@Override
	public void seek(long index) throws IOException {
		this.index = (int)index;
	}

	@Override
	public long index() throws IOException {
		return this.index;
	}

	@Override
	public long length() throws IOException {
		return this.wrappedTarget.size();
	}

	@Override
	public void allocate(long value) throws IOException {
		var size = this.wrappedTarget.size();
		var count = (int)value - size;
		if (count < 0) {
			this.wrappedTarget.remove(size - count, count);
			this.index = Math.min(this.index, size - count);
		} else if (count > 0) {
			this.wrappedTarget.insert(size, count);
		}
	}

	@Override
	public void close() throws IOException {
	}

	private final ByteArray wrappedTarget;

	private int index;

}