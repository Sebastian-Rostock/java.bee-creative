package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;

/**
 * Diese Klasse implementiert einen Wahrheitswert.<br>
 * Intern wird der Wahrheitswert als {@code boolean} dargestellt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMBoolean extends BaseValue implements Comparable<FEMBoolean> {

	/**
	 * Dieses Feld speichert den Identifikator von {@link #TYPE}.
	 */
	public static final int ID = 3;

	/**
	 * Dieses Feld speichert den {@link #type() Datentyp}.
	 */
	public static final FEMType<FEMBoolean> TYPE = FEMType.from(FEMBoolean.ID, "BOOLEAN");

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

	/**
	 * Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMBoolean.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMBoolean from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMBoolean.TYPE);
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMBoolean.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Wahrheitswert.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 */
	public static final FEMBoolean from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMBoolean.TYPE);
	}

	{}

	/**
	 * Dieses Feld speichert die interne Darstellung des Wahrheitswerts.
	 */
	final boolean _value_;

	/**
	 * Dieser Konstruktor initialisiert die interne Darstellung des Wahrheitswerts.
	 * 
	 * @param value interne Darstellung des Wahrheitswerts.
	 */
	public FEMBoolean(final boolean value) {
		this._value_ = value;
	}

	{}

	/**
	 * Diese Methode gibt die interne Darstellung des Wahrheitswerts zurück. Diese ist ein {@code boolean}.
	 * 
	 * @return interne Darstellung des Wahrheitswerts.
	 */
	public final boolean value() {
		return this._value_;
	}

	/**
	 * Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert.
	 */
	public final int hash() {
		return this._value_ ? 1231 : 1237;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn dieser Wahrheitswert gleich dem gegebenen ist.
	 * 
	 * @param that Wahrheitswert.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMBoolean that) throws NullPointerException {
		return this._value_ == that._value_;
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
		return Boolean.compare(this._value_, that._value_);
	}

	/**
	 * Diese Methode gibt diesen Wahrheitswert als {@link Boolean} zurück.
	 * 
	 * @return {@link Boolean}.
	 */
	public final Boolean toBoolean() {
		return Boolean.valueOf(this._value_);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FEMBoolean data() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FEMType<FEMBoolean> type() {
		return FEMBoolean.TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBoolean)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMBoolean)) return false;
		}
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
		return this._value_ ? "true" : "false";
	}

}
