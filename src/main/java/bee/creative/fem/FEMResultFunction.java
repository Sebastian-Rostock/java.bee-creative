package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link FEMResult} ist.
 * 
 * @see FEMResult
 * @see #invoke(FEMFrame)
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMResultFunction extends FEMBaseFunction {

	/** Diese Methode gibt die gegebene Funktion als {@link FEMResultFunction} zurück.<br>
	 * Wenn diese bereits eine {@link FEMResultFunction} ist, wird sie unverändert zurück gegeben.
	 * 
	 * @param function Funktion.
	 * @return {@link FEMResultFunction}.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public static FEMResultFunction from(final FEMFunction function) throws NullPointerException {
		if (function instanceof FEMResultFunction) return (FEMResultFunction)function;
		return new FEMResultFunction(function);
	}

	{}

	/** Dieses Feld speichert die auszuwertende Funktion. */
	final FEMFunction _function_;

	/** Dieser Konstruktor initialisiert die auszuwertende Funktion, die in {@link #invoke(FEMFrame)} zur Erzeugung eines {@link FEMResult} genutzt wird.
	 * 
	 * @param function auszuwertende Funktion.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public FEMResultFunction(final FEMFunction function) throws NullPointerException {
		if (function == null) throw new NullPointerException("function = null");
		this._function_ = function;
	}

	{}

	/** Diese Methode gibt die auszuwertende Funktion zurück.
	 * 
	 * @return auszuwertende Funktion. */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc}
	 * <p>
	 * Der Ergebniswert entspricht {@code FEMResult.from(frame, this.function())}.
	 * 
	 * @see #function()
	 * @see FEMResult#from(FEMFrame, FEMFunction) */
	@Override
	public final FEMResult invoke(final FEMFrame frame) {
		return FEMResult.from(frame, this._function_);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putFunction(this._function_);
	}

}