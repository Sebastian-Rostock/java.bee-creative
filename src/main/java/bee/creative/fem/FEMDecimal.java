package bee.creative.fem;

/**
 * Diese Klasse implementiert einen Dezimalbruch.<br>
 * Intern wird der Dezimalbruch als {@code double} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class FEMDecimal implements Comparable<FEMDecimal> {

	/**
	 * Dieses Feld speichert den Dezimalbruch {@code NaN}.
	 */
	public static final FEMDecimal EMPTY = new FEMDecimal(Double.NaN);

	{}

	/**
	 * Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Dezimalbruch.
	 */
	public static final FEMDecimal from(final double value) {
		return new FEMDecimal(value);
	}

	/**
	 * Diese Methode gibt einen neuen Dezimalbruch mit dem gegebenen Wert zurück.
	 * 
	 * @param number Wert.
	 * @return Dezimalbruch.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMDecimal from(final Number number) throws NullPointerException {
		if (number == null) throw new NullPointerException("number = null");
		return new FEMDecimal(number.doubleValue());
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
	public final double value() {
		return this.value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Dezimalbruch gleich dem gegebenen ist.
	 * 
	 * @param that Dezimalbruch.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMDecimal that) throws NullPointerException {
		if (that == null) throw new NullPointerException("that = null");
		return (this.value == that.value) || (Double.isNaN(this.value) && Double.isNaN(that.value));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn diesr Dezimalbruch gleiner, gleich oder größer als der gegebene
	 * Dezimalbruch ist. Wenn die Dezimalbrüche nicht vergleichbar sind, wird {@code undefined} geliefert.
	 * 
	 * @param that Dezimalbruch.
	 * @param undefined Rückgabewert für nicht vergleichbare Dezimalbrüche.
	 * @return Vergleichswert oder {@code undefined}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMDecimal that, final int undefined) throws NullPointerException {
		if (this.equals(that)) return 0;
		if (this.value < that.value) return -1;
		if (this.value > that.value) return +1;
		return undefined;
	}

	/**
	 * Diese Methode gibt diesen Dezimalbruch als {@link Number} zurück.
	 * 
	 * @return {@link Number}.
	 */
	public final Number toNumber() {
		return Double.valueOf(this.value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final long value = Double.doubleToLongBits(this.value);
		return (int)(value >>> 0) ^ (int)(value >>> 32);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMDecimal)) return false;
		return this.equals((FEMDecimal)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final FEMDecimal value) {
		return this.compare(value, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatDecimal(this);
	}

}
