package bee.creative.mmf;

import java.nio.ByteBuffer;

/**
 * Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT32} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
class MMFINT32Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT32Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength >> 2, byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray __section(final int offset, final int length) {
		return new MMFINT32Array(this.__byteBuffer, this.__byteOffset + (offset << 2), length << 2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int __get(int index) {
		return this.__byteBuffer.getInt(this.__byteOffset + (index << 2));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mode() {
		return 4;
	}

}