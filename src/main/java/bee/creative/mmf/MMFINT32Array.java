package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Diese Klasse implementiert ein {@link MMFArray}, welches einen gegebenen Speicherbereich als Folge von {@code INT32} Zahlen interpretiert.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class MMFINT32Array extends MMFArray {

	@SuppressWarnings ("javadoc")
	public MMFINT32Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteLength >> 2, byteBuffer, byteOffset, byteLength);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int customGet(final int index) {
		return this.byteBuffer.getInt(this.byteOffset + (index << 2));
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFINT32Array(this.byteBuffer, this.byteOffset + (offset << 2), length << 2);
	}

	/** {@inheritDoc} */
	@Override
	public int mode() {
		return 4;
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFINT32Array(this.byteBuffer.duplicate().order(order), this.byteOffset, this.byteLength);
	}

}