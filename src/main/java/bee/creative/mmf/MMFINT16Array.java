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
	protected int customGet(final int index) {
		return this.byteBuffer.getShort(this.byteOffset + (index << 1));
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFINT16Array(this.byteBuffer, this.byteOffset + (offset << 1), length << 1);
	}

	/** {@inheritDoc} */
	@Override
	public int mode() {
		return 2;
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFINT16Array(this.byteBuffer.duplicate().order(order), this.byteOffset, this.byteLength);
	}

}