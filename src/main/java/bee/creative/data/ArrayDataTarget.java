package bee.creative.data;

import java.io.IOException;
import bee.creative.array.CompactByteArray;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link CompactByteArray}.
 * 
 * @see CompactByteArray
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ArrayDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	final CompactByteArray _data_;

	/** Dieses Feld speichert die Schreibeposition. */
	int _index_;

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
		this._data_.setAlignment(0);
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die Nutzdaten {@code null} sind. */
	public ArrayDataTarget(final CompactByteArray data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		this._data_ = data;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public CompactByteArray data() {
		return this._data_;
	}

	/** {@inheritDoc} */
	@Override
	public void write(final byte[] array, final int offset, final int length) throws IOException {
		if ((offset < 0) || ((offset + length) > array.length)) throw new IndexOutOfBoundsException();
		final CompactByteArray data = this._data_;
		final int size = data.size(), index = this._index_, index2 = index + length;
		data.insert(size, Math.max(index2 - size, 0));
		System.arraycopy(array, offset, data.array(), data.startIndex() + index, length);
		this._index_ = index2;
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this._index_ = (int)index;
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this._index_;
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this._data_.size();
	}

	/** {@inheritDoc} */
	@Override
	public void allocate(final long value) throws IOException {
		final int size = this._data_.size();
		final int count = (int)value - size;
		if (count < 0) {
			this._data_.remove(size - count, count);
			this._index_ = Math.min(this._index_, size - count);
		} else if (count > 0) {
			this._data_.insert(size, count);
		}
	}

}