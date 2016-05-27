package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEMTraceFunction.Tracer;
import bee.creative.fem.FEMTraceFunction.TracerInput;

/** Diese Klasse implementiert eine Funktion, welche die zusätzlichen Parameterwerte von Stapelrahmen an eine gegebene Funktion bindet und diese gebundene
 * Funktion anschließend als {@link FEMHandler} liefert.
 * 
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMClosureFunction extends FEMBaseFunction implements TracerInput {

	/** Diese Methode gibt die gegebene Funktion als {@link FEMClosureFunction} zurück.<br>
	 * Wenn diese bereits eine {@link FEMClosureFunction} ist, wird sie unverändert zurück gegeben.
	 * 
	 * @param function Funktion.
	 * @return {@link FEMClosureFunction}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public static FEMClosureFunction from(final FEMFunction function) throws NullPointerException {
		if (function instanceof FEMClosureFunction) return (FEMClosureFunction)function;
		return new FEMClosureFunction(function);
	}

	{}

	/** Dieses Feld speichert die gebundenen Stapelrahmen, deren zusätzliche Parameterwerte genutzt werden. */
	final FEMFrame _frame_;

	/** Dieses Feld speichert die auszuwertende Funktion. */
	final FEMFunction _function_;

	/** Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #invoke(FEMFrame)} die die zusätzlichen Parameterwerte der Stapelrahmen gebunden
	 * werden.
	 * 
	 * @see #invoke(FEMFrame)
	 * @param function zu bindende Funktion.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public FEMClosureFunction(final FEMFunction function) throws NullPointerException {
		if (function == null) throw new NullPointerException("function = null");
		this._frame_ = null;
		this._function_ = function;
	}

	/** Dieser Konstruktor initialisiert die Stapelrahmen sowie die gebundene Funktion und sollte nur von {@link FEMClosureFunction#invoke(FEMFrame)} genutzt
	 * werden.<br>
	 * Die {@link #invoke(FEMFrame)}-Methode delegiert die zugesicherten Parameterwerte der ihr übergebenen Stapelrahmen zusammen mit den zusätzlichen
	 * Parameterwerten der gebundenen Stapelrahmen an die gegebene Funktion und liefert deren Ergebniswert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @param frame Stapelrahmen mit den zusätzlichen Parameterwerten.
	 * @param function gebundene Funktion.
	 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist. */
	public FEMClosureFunction(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		if (frame == null) throw new NullPointerException("frame = null");
		if (function == null) throw new NullPointerException("function = null");
		this._frame_ = frame;
		this._function_ = function;
	}

	{}

	/** Diese Methode gibt die gebundene Stapelrahmen oder {@code null} zurück.<br>
	 * Die Stapelrahmen sind {@code null}, wenn diese {@link FEMClosureFunction} über dem Konstruktor {@link #FEMClosureFunction(FEMFunction)} erzeugt wurde.
	 * 
	 * @see #FEMClosureFunction(FEMFunction)
	 * @see #FEMClosureFunction(FEMFrame, FEMFunction)
	 * @see #invoke(FEMFrame)
	 * @return gebundene Stapelrahmen oder {@code null}. */
	public final FEMFrame frame() {
		return this._frame_;
	}

	/** Diese Methode gibt die auszuwertende Funktion zurück.
	 * 
	 * @return auszuwertende Funktion. */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc}
	 * <p>
	 * Wenn diese Funktion über {@link #FEMClosureFunction(FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
	 * {@code functionValue(new ClosureFunction(frame, this.function()))}. Damit werden die gegebenen Stapelrahmen an die Funktion {@link #function()} gebunden und
	 * als {@link FEMHandler} zurück gegeben.
	 * <p>
	 * Wenn sie dagegen über {@link #FEMClosureFunction(FEMFrame, FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
	 * {@code this.function().invoke(this.frame().withParams(frame.params()))}. Damit werden die gebundene Funktion mit den zugesicherten Parameterwerten der
	 * gegebenen sowie den zusätzlichen Parameterwerten der gebundenen Stapelrahmen ausgewertet und der so ermittelte Ergebniswert geliefert. */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		if (this._frame_ == null) return FEMHandler.from(new FEMClosureFunction(frame, this._function_));
		return this._function_.invoke(this._frame_.withParams(frame.params()));
	}

	/** {@inheritDoc} */
	@Override
	public final FEMFunction toTrace(final Tracer tracer) throws NullPointerException {
		if (this._frame_ == null) return FEMClosureFunction.from(tracer.trace(this._function_));
		return new FEMClosureFunction(this._frame_, tracer.trace(this._function_));
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putHandler(this._function_);
	}

}