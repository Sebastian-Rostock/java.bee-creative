package bee.creative.fem;

import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert einen Dezimalbruch.
 * 
 * @author Sebastian Rostock 2015.
 */
public final class FEMDecimal implements Comparable<FEMDecimal> {

	/**
	 * Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalbruch.
	 */
	public static final FEMDecimal valueOf(final double value) {
		return new FEMDecimal(value);
	}

	/**
	 * Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalbruch.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMDecimal valueOf(final Number value) throws NullPointerException {
		return new FEMDecimal(value.doubleValue());
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung des Dezimalbruchs.
	 */
	final double value;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung des Dezimalbruchs.
	 * 
	 * @param value interne Darstellung des Dezimalbruchs.
	 */
	public FEMDecimal(final double value) {
		this.value = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung des Dezimalbruchs zurück. Diese ist ein {@code double}.
	 * 
	 * @return interne Darstellung des Dezimalbruchs.
	 */
	public double value() {
		return this.value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Dezimalbruch gleich dem gegebenen ist.
	 * 
	 * @param value Dezimalbruch.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public boolean equals(final FEMDecimal value) throws NullPointerException {
		return this.value == value.value;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn diesr Dezimalbruch gleiner, gleich oder größer als der gegebene
	 * Dezimalbruch ist.
	 * 
	 * @param value Dezimalbruch.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public int compare(final FEMDecimal value) throws NullPointerException {
		return Comparators.compare(this.value, value.value);
	}

	public Number tNumber() {
		return Double.valueOf(this.value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final long value = Double.doubleToLongBits(this.value);
		return (int)(value ^ (value >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDecimal)) return false;
		return this.equals((FEMDecimal)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final FEMDecimal value) {
		return this.compare(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatDecimal(this);
	}

}
