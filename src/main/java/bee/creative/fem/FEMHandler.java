package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEM.ScriptTracer;
import bee.creative.fem.FEM.ScriptTracerInput;

/** Diese Klasse implementiert einen Funktionszeiger, d.h. eine als {@link FEMValue} verpackte Funktion.<br>
 * Intern wird der Wahrheitswert als {@link FEMFunction} dargestellt.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMHandler extends BaseValue implements ScriptTracerInput {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 2;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMHandler> TYPE = FEMType.from(FEMHandler.ID, "HANDLER");

	{}

	/** Diese Methode gibt die gegebene Funktion als Funktionszeiger zurück.
	 * 
	 * @param data Funktion.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public static FEMHandler from(final FEMFunction data) throws NullPointerException {
		return new FEMHandler(data);
	}

	/** Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMMethod.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMHandler from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMHandler.TYPE);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMMethod.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMHandler from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMHandler.TYPE);
	}

	{}

	/** Dieses Feld speichert die Nutzdaten. */
	final FEMFunction _value_;

	/** Dieser Konstruktor initialisiert die Funktion.
	 * 
	 * @param value Funktion.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMHandler(final FEMFunction value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this._value_ = value;
	}

	{}

	/** Diese Methode gibt die Funktion zurück.
	 * 
	 * @return Funktion. */
	public final FEMFunction value() {
		return this._value_;
	}

	/** Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert. */
	public final int hash() {
		return this._value_.hashCode();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieser Funktionszeiger gleich der gegebenen ist.
	 * 
	 * @param that Methode.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMHandler that) throws NullPointerException {
		return this._value_.equals(that._value_);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMHandler data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMHandler> type() {
		return FEMHandler.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMHandler result(boolean recursive) {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMHandler)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMHandler)) return false;
		}
		return this.equals((FEMHandler)object);
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
		return FEMHandler.from(tracer.trace(this._value_));
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putFunction(this._value_);
	}

}