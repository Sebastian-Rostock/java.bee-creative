package bee.creative._dev_.qhs;

import bee.creative.fem.FEMString;

/** Diese Klasse implementiert den Quad-Node.
 * 
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
final class QN {

	final FEMString V;

	public QN(final byte[] value) {
		this(FEMString.from(value, true, false));
	}

	public QN(final char[] value) {
		this(FEMString.from(value));
	}

	public QN(final String value) {
		this(FEMString.from(value));
	}

	public QN(final FEMString value) {
		this.V = value.compact();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.V.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof QN)) return false;
		final QN that = (QN)object;
		return equalsImpl(that);
	}

	@SuppressWarnings ("javadoc")
	boolean equalsImpl(final QN that) {
		return this.V.equals(that.V);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.V.toString();
	}

}
