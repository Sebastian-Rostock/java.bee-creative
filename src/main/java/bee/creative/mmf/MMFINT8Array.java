package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
	protected int customGet(final int index) {
		return this.byteBuffer.get(this.byteOffset + index);
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFINT8Array(this.byteBuffer, this.byteOffset + offset, length);
	}

	/** {@inheritDoc} */
	@Override
	public int mode() {
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFINT8Array(this.byteBuffer.duplicate().order(order), this.byteOffset, this.byteLength);
	}

}