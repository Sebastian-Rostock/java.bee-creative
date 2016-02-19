package bee.creative.mmf;

import java.nio.ByteBuffer;

/** Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class MMFUINT16Array extends MMFINT16Array {

	@SuppressWarnings ("javadoc")
	public MMFUINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteBuffer, byteOffset, byteLength);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int _get_(final int index) {
		return super._get_(index) & 65535;
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray _section_(final int offset, final int length) {
		return new MMFUINT16Array(this._byteBuffer_, this._byteOffset_ + (offset << 1), length << 1);
	}

}