package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

/**
 * Diese Klasse implementiert eine {@link IAMArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT16/short} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
class MMFINT16Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT16Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray newSection(final int offset, final int length) {
		return new MMFINT16Array(this.byteBuffer, this.byteOffset + (offset << 1), length << 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(int index) {
		index <<= 1;
		if ((index < 0) || (index >= this.byteLength)) return 0;
		return this.byteBuffer.getShort(this.byteOffset + index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return this.byteLength >> 1;
	}

}