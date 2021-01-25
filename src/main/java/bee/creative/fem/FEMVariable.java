package bee.creative.fem;

import bee.creative.lang.Objects;
import bee.creative.util.Property;

/** Diese Klasse implementiert eine Variable als veränderlichen Verweis auf einen Wert.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMVariable extends FEMValue implements Property<FEMValue> {

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

	/** Dieses Feld speichert den Wert. */
	FEMValue value;

	/** Dieser Konstruktor initialisiert den Wert dieser Variablen.
	 *
	 * @param value initialier Wert, der z.B. als {@link FEMFuture} gegeben sein kann.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMVariable(final FEMValue value) throws NullPointerException {
		this.set(value);
	}

	/** Diese Methode gibt den aktuellen Wert der Variable zurück, der über {@link #set(FEMValue)} geändert werden kann.
	 *
	 * @see #set(FEMValue)
	 * @return aktueller Wert. */
	@Override
	public synchronized FEMValue get() {
		return this.value;
	}

	/** Diese Methode setzt den aktuellen Wert der Variable, der über {@link #get()} gelesen werden kann.
	 *
	 * @param value aktuellen Wert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	@Override
	public synchronized void set(final FEMValue value) throws NullPointerException {
		this.value = Objects.notNull(value);
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public FEMVariable data() {
		return this;
	}

	@Override
	public FEMType<FEMVariable> type() {
		return FEMVariable.TYPE;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.value);
	}

}