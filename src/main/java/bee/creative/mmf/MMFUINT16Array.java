package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
	protected int customGet(final int index) {
		return super.customGet(index) & 65535;
	}

	/** {@inheritDoc} */
	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFUINT16Array(this.byteBuffer, this.byteOffset + (offset << 1), length << 1);
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFUINT16Array(this.byteBuffer.duplicate().order(order), this.byteOffset, this.byteLength);
	}

}