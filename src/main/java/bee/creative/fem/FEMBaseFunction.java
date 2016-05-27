package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.fem.FEM.ScriptFormatterInput;

/** Diese Klasse implementiert eine abstakte Funktion als {@link ScriptFormatterInput}.<br>
 * Die {@link #toString() Textdarstellung} der Funktion wird über {@link #toScript(ScriptFormatter)} ermittelt.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMBaseFunction implements FEMFunction, ScriptFormatterInput {

	/** Diese Methode gibt diese Funktion als Wert ({@link FEMHandler}) zurück.
	 * 
	 * @return {@code this} als Wert. */
	public final FEMHandler toValue() {
		return new FEMHandler(this);
	}

	/** Diese Methode gibt eine neue {@link FEMInvokeFunction} zurück, welche diese Funktion mit den gegebenen Parameterfunktionen aufruft.
	 * 
	 * @see FEMInvokeFunction
	 * @see FEMFrame#withParams(FEMFunction[])
	 * @param params Parameterfunktionen.
	 * @return {@link FEMInvokeFunction}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMInvokeFunction withParams(final FEMFunction... params) throws NullPointerException {
		return FEMInvokeFunction.from(this, true, params);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(this.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return FEM.scriptFormatter().formatFunction(this);
	}

}