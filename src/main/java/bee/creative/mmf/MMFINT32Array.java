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
		super(byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray newSection(final int offset, final int length) {
		return new MMFINT32Array(this.byteBuffer, this.byteOffset + (offset << 2), length << 2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(int index) {
		index <<= 2;
		if ((index < 0) || (index >= this.byteLength)) return 0;
		return this.byteBuffer.getInt(this.byteOffset + index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mode() {
		return 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return this.byteLength >> 2;
	}

}