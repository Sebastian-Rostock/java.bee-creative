package bee.creative.fem;

import bee.creative.util.Objects;

/** Diese Klasse implementiert eine Variable als veränderlichen Verweis auf einen Wert.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMVariable extends FEMValue {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = -2;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMVariable> TYPE = FEMType.from(FEMVariable.ID);

	/** Diese Methode gibt eine neue Variable mit dem gegebenen Initialwert zurück.
	 *
	 * @param value Initialwert.
	 * @return {@link FEMVariable}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMVariable from(final FEMValue value) throws NullPointerException {
		return new FEMVariable(value);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMVariable.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMVariable from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMVariable.TYPE);
	}

	/** Dieses Feld speichert den Wert. */
	FEMValue value;

	/** Dieser Konstruktor initialisiert den Wert dieser Variablen.
	 *
	 * @param value initialier Wert, der z.B. als {@link FEMFuture} gegeben sein kann.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMVariable(final FEMValue value) throws NullPointerException {
		this.update(value);
	}

	/** Diese Methode gibt den aktuellen Wert der Variable zurück, der über {@link #update(FEMValue)} geändert werden kann.
	 *
	 * @see #update(FEMValue)
	 * @return aktueller Wert. */
	public final synchronized FEMValue value() {
		return this.value;
	}

	/** Diese Methode setzt den aktuellen Wert der Variable, der über {@link #value()} gelesen werden kann.
	 *
	 * @param value aktuellen Wert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final synchronized void update(final FEMValue value) throws NullPointerException {
		this.value = Objects.assertNotNull(value);
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMVariable data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMVariable> type() {
		return FEMVariable.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.value);
	}

}