package bee.creative.fem;

/**
 * Diese Klasse implementiert einen Wahrheitswert.<br>
 * Intern wird der Wahrheitswert als {@code boolean} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMBoolean implements Comparable<FEMBoolean> {

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

	/**
	 * Diese Methode gibt einen neuen Wahrheitswert mit dem gegebenen Wert zurück.
	 * 
	 * @see #from(boolean)
	 * @see Boolean#booleanValue()
	 * @param value Wert.
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMBoolean from(final Boolean value) throws NullPointerException {
		return FEMBoolean.from(value.booleanValue());
	}

	/**
	 * Diese Methode gibt einen neuen Wahrheitswert mit dem in der gegebenen Zeichenkette kodierten Wert zurück.
	 * 
	 * @param value Zeichenkette ({@code "true"} oder {@code "false"}).
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist.
	 */
	public static final FEMBoolean from(final String value) throws NullPointerException, IllegalArgumentException {
		if (value.equals("true")) return FEMBoolean.TRUE;
		if (value.equals("false")) return FEMBoolean.FALSE;
		throw new IllegalArgumentException();
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung des Wahrheitswerts.
	 */
	final boolean __value;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung des Wahrheitswerts.
	 * 
	 * @param value interne Darstellung des Wahrheitswerts.
	 */
	public FEMBoolean(final boolean value) {
		this.__value = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung des Wahrheitswerts zurück. Diese ist ein {@code boolean}.
	 * 
	 * @return interne Darstellung des Wahrheitswerts.
	 */
	public final boolean value() {
		return this.__value;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Wahrheitswert gleich dem gegebenen ist.
	 * 
	 * @param that Wahrheitswert.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMBoolean that) throws NullPointerException {
		return this.__value == that.__value;
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
		return Boolean.compare(this.__value, that.__value);
	}

	/**
	 * Diese Methode gibt diesen Wahrheitswert als {@link Boolean} zurück.
	 * 
	 * @return {@link Boolean}.
	 */
	public final Boolean toBoolean() {
		return Boolean.valueOf(this.__value);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.__value ? 1231 : 1237;
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
	 * Diese Methode gibt die Textdarstellung dieses Wahrheitswert zurück.<br>
	 * Für die Wahrheitswerte {@code true} und {@code false} sind die Textdarstellungen {@code "true"} und {@code "false"}.
	 * 
	 * @return Textdarstellung.
	 */
	@Override
	public final String toString() {
		return this.__value ? "true" : "false";
	}

}
