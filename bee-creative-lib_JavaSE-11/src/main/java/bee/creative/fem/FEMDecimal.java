package bee.creative.fem;

import bee.creative.lang.Integers;

/** Diese Klasse implementiert einen unveränderlichen Dezimalbruch. Intern wird der Dezimalbruch als {@code double} dargestellt.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMDecimal implements FEMValue , Comparable<FEMDecimal> {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 7;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMDecimal> TYPE = FEMType.from(FEMDecimal.ID);

	/** Dieses Feld speichert den Dezimalbruch {@code NaN}. */
	public static final FEMDecimal EMPTY = new FEMDecimal(Double.NaN);

	/** Dieses Feld speichert die größte negativen Dezimalbruch. */
	public static final FEMDecimal MINIMUM = new FEMDecimal(Double.MIN_VALUE);

	/** Dieses Feld speichert die größte positiven Dezimalbruch. */
	public static final FEMDecimal MAXIMUM = new FEMDecimal(Double.MAX_VALUE);

	/** Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 *
	 * @param value Wert.
	 * @return Dezimalbruch. */
	public static FEMDecimal from(final double value) {
		return new FEMDecimal(value);
	}

	/** Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 *
	 * @param value Wert.
	 * @return Dezimalbruch.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMDecimal from(final Number value) throws NullPointerException {
		return FEMDecimal.from(value.doubleValue());
	}

	/** Diese Methode gibt einen neuen Dezimalbruch mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @see Double#parseDouble(String)
	 * @param value Zeichenkette.
	 * @return Dezimalbruch.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMDecimal from(final String value) throws NullPointerException, IllegalArgumentException {
		try {
			return FEMDecimal.from(Double.parseDouble(value));
		} catch (final NumberFormatException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Dieses Feld speichert die interne Darstellung des Dezimalbruchs. */
	final double value;

	/** Dieser Konstruktor initialisiert die interne Darstellung des Dezimalbruchs.
	 *
	 * @param value interne Darstellung des Dezimalbruchs. */
	public FEMDecimal(final double value) {
		this.value = value;
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMDecimal data() {
		return this;
	}

	@Override
	public FEMType<FEMDecimal> type() {
		return FEMDecimal.TYPE;
	}

	/** Diese Methode gibt die interne Darstellung des Dezimalbruchs zurück.
	 *
	 * @return interne Darstellung des Dezimalbruchs. */
	public double value() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final long value = Double.doubleToLongBits(this.value);
		return Integers.toIntL(value) ^ Integers.toIntH(value);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDecimal)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMDecimal)) return false;
		}
		final FEMDecimal that = (FEMDecimal)object;
		return (this.value == that.value) || (Double.isNaN(this.value) && Double.isNaN(that.value));
	}

	@Override
	public int compareTo(final FEMDecimal value) {
		return this.compareTo(value, 0);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn dieser Dezimalbruch kleiner, gleich oder größer als der gegebene Dezimalbruch ist.
	 * Wenn die Dezimalbrüche nicht vergleichbar sind, wird {@code undefined} geliefert.
	 *
	 * @param that Dezimalbruch.
	 * @param undefined Rückgabewert für nicht vergleichbare Dezimalbrüche.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public int compareTo(final FEMDecimal that, final int undefined) throws NullPointerException {
		if (this.value < that.value) return -1;
		if (this.value > that.value) return +1;
		if (this.value == that.value) return 0;
		return undefined;
	}

	/** Diese Methode gibt die Textdarstellung dieses Dezimalbruchs zurück.
	 *
	 * @see Double#toString(double)
	 * @return Textdarstellung. */
	@Override
	public String toString() {
		return Double.toString(this.value);
	}

	/** Diese Methode gibt diesen Dezimalbruch als {@link Double} zurück.
	 *
	 * @return {@link Double}. */
	public Double toNumber() {
		return Double.valueOf(this.value);
	}

}
