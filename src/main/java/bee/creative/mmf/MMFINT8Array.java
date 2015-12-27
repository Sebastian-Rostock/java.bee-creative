package bee.creative.mmf;

import java.nio.ByteBuffer;

/**
 * Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT8} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
class MMFINT8Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT8Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength, byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray __section(final int offset, final int length) {
		return new MMFINT8Array(this.__byteBuffer, this.__byteOffset + offset, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int __get(final int index) {
		return this.__byteBuffer.get(this.__byteOffset + index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mode() {
		return 1;
	}

}