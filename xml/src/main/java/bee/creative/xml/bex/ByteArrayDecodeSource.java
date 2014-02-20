package bee.creative.xml.bex;

import java.io.IOException;

/**
 * Diese Klasse implementiert eine {@link DecodeSource}, die ihre Schnittstelle an ein {@code byte}-Array delegiert.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class ByteArrayDecodeSource implements DecodeSource {

	/**
	 * Dieses Feld speichert das {@code byte}-Array.
	 */
	protected final byte[] data;

	/**
	 * Dieses Feld speichert die Leseposition.
	 */
	protected int index;

	/**
	 * Dieser Konstruktor initialisiert das {@code byte}-Array.
	 * 
	 * @param data {@code byte}-Array.
	 * @throws NullPointerException Wenn das {@code byte}-Array {@code null} ist.
	 */
	public ByteArrayDecodeSource(final byte[] data) throws NullPointerException {
		this.data = data;
		this.index = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void read(final byte[] array, final int offset, final int length) throws IOException {
		final int index = this.index;
		System.arraycopy(this.data, index, array, offset, length);
		this.index = index + length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void seek(final long index) throws IOException {
		this.index = (int)index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long index() throws IOException {
		return this.index;
	}

}
