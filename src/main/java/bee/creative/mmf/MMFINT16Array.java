package bee.creative.mmf;

import java.nio.ByteBuffer;

/**
 * Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
class MMFINT16Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength >> 1, byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray __section(final int offset, final int length) {
		return new MMFINT16Array(this.__byteBuffer, this.__byteOffset + (offset << 1), length << 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int __get(int index) {
		return this.__byteBuffer.getShort(this.__byteOffset + (index << 1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mode() {
		return 2;
	}

}