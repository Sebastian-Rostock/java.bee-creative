package bee.creative.fem;

import bee.creative.fem.FEM.ScriptCompiler;
import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert den benannten Platzhalter einer Funktione, dessen {@link #invoke(FEMFrame)}-Methoden an eine {@link #set(FEMFunction) gegebene
 * Funktion} delegiert.
 * 
 * @see ScriptCompiler#proxy(String)
 * @see ScriptCompiler#proxies()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMProxyFunction extends FEMBaseFunction {

	/** Diese Methode gibt eine neue {@link FEMProxyFunction} mit dem gegebenen Namen zurück.
	 * 
	 * @param name Name.
	 * @return {@link FEMProxyFunction}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public static FEMProxyFunction from(final String name) throws NullPointerException {
		return new FEMProxyFunction(name);
	}

	{}

	/** Dieses Feld speichert den Namen. */
	final String _name_;

	/** Dieses Feld speichert die Funktion. */
	FEMFunction _function_;

	/** Dieser Konstruktor initialisiert den Namen.
	 * 
	 * @param name Name.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public FEMProxyFunction(final String name) throws NullPointerException {
		if (name == null) throw new NullPointerException("name = null");
		this._name_ = name;
	}

	{}

	/** Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
	 * 
	 * @param function Funktion.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
	public final void set(final FEMFunction function) throws NullPointerException {
		if (function == null) throw new NullPointerException("function = null");
		this._function_ = function;
	}

	/** Diese Methode gibt den Namen.
	 * 
	 * @return Name. */
	public final String name() {
		return this._name_;
	}

	/** Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird.<br>
	 * Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht aufgerufen wurde.
	 * 
	 * @return Funktion oder {@code null}. */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		return this._function_.invoke(frame);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(FEM.formatValue(this._name_));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this._name_ + ":" + this._function_;
	}

}