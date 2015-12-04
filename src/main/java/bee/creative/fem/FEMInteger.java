package bee.creative.fem;

import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert eine Dezimalzahl.<br>
 * Intern wird die Dezimalzahl als {@code long} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMInteger implements Comparable<FEMInteger> {

	/**
	 * Dieses Feld speichert die Dezimalzahl {@code 0}.
	 */
	public static final FEMInteger EMPTY = new FEMInteger(0);

	{}

	/**
	 * Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalzahl.
	 */
	public static final FEMInteger from(final long value) {
		return new FEMInteger(value);
	}

	/**
	 * Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param number Wert.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMInteger from(final Number number) throws NullPointerException {
		if (number == null) throw new NullPointerException("number = null");
		return new FEMInteger(number.longValue());
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung der Dezimalzahl.
	 */
	final long __value;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung der Dezimalzahl.
	 * 
	 * @param value interne Darstellung der Dezimalzahl.
	 */
	public FEMInteger(final long value) {
		this.__value = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung der Dezimalzahl zurück. Diese ist ein {@code long}.
	 * 
	 * @return interne Darstellung der Dezimalzahl.
	 */
	public final long value() {
		return this.__value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Dezimalzahl gleich der gegebenen ist.
	 * 
	 * @param that Dezimalzahl.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEMInteger that) throws NullPointerException {
		if (that == null) throw new NullPointerException("that = null");
		return this.__value == that.__value;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn diese Dezimalzahl gleiner, gleich oder größer als die gegebene
	 * Dezimalzahl ist.
	 * 
	 * @param that Dezimalzahl.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMInteger that) throws NullPointerException {
		if (that == null) throw new NullPointerException("that = null");
		return Comparators.compare(this.__value, that.__value);
	}

	/**
	 * Diese Methode gibt diese Dezimalzahl als {@link Number} zurück.
	 * 
	 * @return {@link Number}.
	 */
	public final Number toNumber() {
		return Long.valueOf(this.__value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final long value = this.__value;
		return (int)(value >>> 0) ^ (int)(value >>> 32);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMInteger)) return false;
		return this.equals((FEMInteger)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final FEMInteger value) {
		return this.compare(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return FEM.formatInteger(this);
	}

}
