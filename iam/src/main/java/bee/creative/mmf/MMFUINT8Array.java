package bee.creative.mmf;

import java.nio.ByteBuffer;

/**
 * Diese Klasse implementiert ein {@link MMFArray}, welches eine Datei bzw. einen gegebenen Speicherbereich als Folge von {@code UINT8} Zahlen interpretiert.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
class MMFUINT8Array extends MMFINT8Array {

	@SuppressWarnings ("javadoc")
	public MMFUINT8Array(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		super(byteBuffer, byteOffset, byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray newSection(final int offset, final int length) {
		return new MMFUINT8Array(this.byteBuffer, this.byteOffset + offset, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(final int index) {
		return super.get(index) & 255;
	}

}