package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEMTraceFunction.Tracer;
import bee.creative.fem.FEMTraceFunction.TracerInput;

/** Diese Klasse implementiert eine Funktion, welche stats den gleichen {@link #value() gegebenen Ergebniswert} liefert.
 * 
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMValueFunction extends FEMBaseFunction implements TracerInput {

	/** Diese Methode gibt den gegebenen Wert als {@link FEMValueFunction} zurück, sofern er kein {@link FEMBaseValue} ist. Andernfalls wird der unverändert zurück
	 * gegeben.
	 * 
	 * @param value Wert.
	 * @return {@link FEMFunction}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMFunction from(final FEMValue value) throws NullPointerException {
		if (value instanceof FEMBaseValue) return (FEMFunction)value;
		return new FEMValueFunction(value);
	}

	{}

	/** Dieses Feld speichert den Ergebniswert. */
	final FEMValue _value_;

	/** Dieser Konstruktor initialisiert den Ergebniswert.
	 * 
	 * @param value Ergebniswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMValueFunction(final FEMValue value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this._value_ = value;
	}

	{}

	/** Diese Methode gibt den Ergebniswert zurück.
	 * 
	 * @return Ergebniswert. */
	public final FEMValue value() {
		return this._value_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this._value_;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toTrace(final Tracer tracer) throws NullPointerException {
		if (this._value_ instanceof TracerInput) return ((TracerInput)this._value_).toTrace(tracer);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putValue(this._value_);
	}

}