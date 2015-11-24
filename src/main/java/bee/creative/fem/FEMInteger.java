package bee.creative.fem;

import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert eine Dezimalzahl.
 * 
 * @author Sebastian Rostock 2015.
 */
public final class FEMInteger implements Comparable<FEMInteger> {

	/**
	 * Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalzahl.
	 */
	public static final FEMInteger valueOf(final long value) {
		return new FEMInteger(value);
	}

	/**
	 * Diese Methode gibt eine neue Dezimalzahl mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMInteger valueOf(final Number value) throws NullPointerException {
		return new FEMInteger(value.longValue());
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung der Dezimalzahl.
	 */
	final long value;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung der Dezimalzahl.
	 * 
	 * @param value interne Darstellung der Dezimalzahl.
	 */
	public FEMInteger(final long value) {
		this.value = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung der Dezimalzahl zurück. Diese ist ein {@code long}.
	 * 
	 * @return interne Darstellung der Dezimalzahl.
	 */
	public long value() {
		return this.value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Dezimalzahl gleich der gegebenen ist.
	 * 
	 * @param value Dezimalzahl.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public boolean equals(final FEMInteger value) throws NullPointerException {
		return this.value == value.value;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn diese Dezimalzahl gleiner, gleich oder größer als die gegebene
	 * Dezimalzahl ist.
	 * 
	 * @param value Dezimalzahl.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public int compare(final FEMInteger value) throws NullPointerException {
		return Comparators.compare(this.value, value.value);
	}

	public Number asNumber() {
		return Long.valueOf(this.value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final long value = this.value;
		return (int)(value ^ (value >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMInteger)) return false;
		return this.equals((FEMInteger)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final FEMInteger value) {
		return this.compare(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatInteger(this);
	}

}
