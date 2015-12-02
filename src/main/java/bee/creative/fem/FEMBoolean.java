package bee.creative.fem;

/**
 * Diese Klasse implementiert einen Wahrheitswert.
 * 
 * @author Sebastian Rostock 2015.
 */
public class FEMBoolean implements Comparable<FEMBoolean> {

	/**
	 * Dieses Feld speichert den Wahrheitswert {@code true}.
	 */
	public static final FEMBoolean TRUE = new FEMBoolean(true);

	/**
	 * Dieses Feld speichert den Wahrheitswert {@code false}.
	 */
	public static final FEMBoolean FALSE = new FEMBoolean(false);

	{}

	/**
	 * Diese Methode gibt einen neuen Wahrheitswert mit dem gegebenen Wert zurück.
	 * 
	 * @param value Wert.
	 * @return Wahrheitswert.
	 */
	public static final FEMBoolean from(final boolean value) {
		return value ? FEMBoolean.TRUE : FEMBoolean.FALSE;
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung des Wahrheitswerts.
	 */
	final boolean value;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung des Wahrheitswerts.
	 * 
	 * @param value interne Darstellung des Wahrheitswerts.
	 */
	public FEMBoolean(final boolean value) {
		this.value = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung des Wahrheitswerts zurück. Diese ist ein {@code boolean}.
	 * 
	 * @return interne Darstellung des Wahrheitswerts.
	 */
	public final boolean value() {
		return this.value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Wahrheitswert gleich dem gegebenen ist.
	 * 
	 * @param that Wahrheitswert.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMBoolean that) throws NullPointerException {
		if (that == null) throw new NullPointerException("that = null");
		return this.value == that.value;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn dieser Wahrheitswert gleiner, gleich oder größer als der
	 * gegebene Wahrheitswert ist.
	 * 
	 * @param that Wahrheitswert.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMBoolean that) throws NullPointerException {
		if (that == null) throw new NullPointerException("that = null");
		return Boolean.compare(this.value, that.value);
	}

	/**
	 * Diese Methode gibt diesen Wahrheitswert als {@link Boolean} zurück.
	 * 
	 * @return {@link Boolean}.
	 */
	public final Boolean toBoolean() {
		return Boolean.valueOf(this.value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.value ? 1231 : 1237;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBoolean)) return false;
		return this.equals((FEMBoolean)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final FEMBoolean value) {
		return this.compare(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatBoolean(this);
	}

}
