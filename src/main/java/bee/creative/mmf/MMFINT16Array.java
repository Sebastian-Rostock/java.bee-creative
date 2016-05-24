package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class MMFINT16Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength >> 1, byteBuffer, byteOffset, byteLength);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int _get_(final int index) {
		return this._byteBuffer_.getShort(this._byteOffset_ + (index << 1));
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray _section_(final int offset, final int length) {
		return new MMFINT16Array(this._byteBuffer_, this._byteOffset_ + (offset << 1), length << 1);
	}

	/** {@inheritDoc} */
	@Override
	public int mode() {
		return 2;
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFINT16Array(this._byteBuffer_.duplicate().order(order), this._byteOffset_, this._byteLength_);
	}

}