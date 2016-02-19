package bee.creative.mmf;

import java.nio.ByteBuffer;

/** Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT8} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class MMFINT8Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT8Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength, byteBuffer, byteOffset, byteLength);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int _get_(final int index) {
		return this._byteBuffer_.get(this._byteOffset_ + index);
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray _section_(final int offset, final int length) {
		return new MMFINT8Array(this._byteBuffer_, this._byteOffset_ + offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public int mode() {
		return 1;
	}

}