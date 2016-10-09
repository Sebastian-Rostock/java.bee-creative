package bee.creative.fem;

/** Diese Klasse implementiert einen veränderlichen Verweis auf einen Wert.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMPointer extends FEMValue {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 11;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMPointer> TYPE = FEMType.from(FEMPointer.ID);

	{}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMPointer.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMPointer from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMPointer.TYPE);
	}

	{}

	/** Dieses Feld speichert den Wert. */
	FEMValue _value_;

	/** Dieser Konstruktor initialisiert den Wert.
	 * 
	 * @param value initialier Wert, der z.B. als {@link FEMFuture} gegeben sein kann.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMPointer(final FEMValue value) throws NullPointerException {
		this.set(value);
	}

	{}

	/** Diese Methode gibt den aktuellen Wert das zurück, der über {@link #set(FEMValue)} geändert werden kann.
	 * 
	 * @see #set(FEMValue)
	 * @return aktueller Wert. */
	public final synchronized FEMValue get() {
		return this._value_;
	}

	/** Diese Methode setzt den aktuellen Wert.
	 * 
	 * @param value aktuellen Wert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final synchronized void set(final FEMValue value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this._value_ = value;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMPointer> type() {
		return FEMPointer.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMPointer data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized FEMValue result(final boolean recursive) {
		final FEMValue oldValue = this._value_;
		final FEMValue newValue = oldValue.result(recursive);
		if (oldValue == newValue) return newValue;
		this._value_ = newValue;
		return newValue;
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
		this.get().toScript(target);
	}

}