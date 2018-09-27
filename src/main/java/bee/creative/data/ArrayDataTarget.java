package bee.creative.data;

import java.io.IOException;
import bee.creative.array.CompactByteArray;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link CompactByteArray}.
 *
 * @see CompactByteArray
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	final CompactByteArray data;

	/** Dieses Feld speichert die Schreibeposition. */
	int index;

	/** Dieser Konstruktor initialisiert die Nutzdaten mit 128 Byte Größe. */
	public ArrayDataTarget() {
		this(128);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten mit der gegebenen Größe.
	 *
	 * @see CompactByteArray#CompactByteArray(int)
	 * @param size Größe. */
	public ArrayDataTarget(final int size) {
		this(new CompactByteArray(size));
		this.data.setAlignment(0);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die Nutzdaten {@code null} sind. */
	public ArrayDataTarget(final CompactByteArray data) throws NullPointerException {
		this.data = Objects.notNull(data);
	}

	/** {@inheritDoc} */
	@Override
	public CompactByteArray data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		if ((offset < 0) || ((offset + length) > array.length)) throw new IndexOutOfBoundsException();
		final CompactByteArray data = this.data;
		final int size = data.size(), index = this.index, index2 = index + length;
		data.insert(size, Math.max(index2 - size, 0));
		System.arraycopy(array, offset, data.array(), data.startIndex() + index, length);
		this.index = index2;
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.index = (int)index;
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.index;
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.data.size();
	}

	/** {@inheritDoc} */
	@Override
	public void allocate(final long value) throws IOException {
		final int size = this.data.size();
		final int count = (int)value - size;
		if (count < 0) {
			this.data.remove(size - count, count);
			this.index = Math.min(this.index, size - count);
		} else if (count > 0) {
			this.data.insert(size, count);
		}
	}

}