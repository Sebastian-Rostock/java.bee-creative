package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;

/**
 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit einem
 * gegebenen Stapelrahmen einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion berechnete
 * Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf Stapelrahmen und Funktion
 * aufgelöst.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMResult extends BaseValue {

	/**
	 * Diese Methode den Ergebniswert des {@link FEMFunction#invoke(FEMFrame) Aufrufs} der gegebenen Funktion mit dem gegebenen Stapelrahmen mit
	 * {@code call-by-reference}-Semantik zurück.<br>
	 * Der gelieferte Ergebniswert verzögert die Auswertung der Funktion mis zum ersten Lesen seines {@link #type() Datentyp} bzw. seiner {@link #data()
	 * Nutzdaten}.
	 * 
	 * @param frame Stapelrahmen.
	 * @param function Funktion.
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist.
	 */
	public static final FEMResult from(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		return new FEMResult(frame, function);
	}

	{}

	/**
	 * Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame)
	 */
	FEMValue _value_;

	/**
	 * Dieses Feld speichert die Stapelrahmen zur Auswertung der Funktion oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame)
	 */
	FEMFrame _frame_;

	/**
	 * Dieses Feld speichert die Funktion oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame)
	 */
	FEMFunction _function_;

	/**
	 * Dieser Konstruktor initialisiert Stapelrahmen und Funktion.
	 * 
	 * @param frame Stapelrahmen.
	 * @param function Funktion.
	 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist.
	 */
	public FEMResult(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		if (frame == null) throw new NullPointerException("frame = null");
		if (function == null) throw new NullPointerException("function = null");
		this._frame_ = frame;
		this._function_ = function;
	}

	{}

	/**
	 * Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #function() Funktion} mit den {@link #frame() Stapelrahmen}
	 * zurück.
	 * 
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
	 */
	public final synchronized FEMValue value() throws NullPointerException {
		FEMValue result = this._value_;
		if (result != null) return result;
		result = this._function_.invoke(this._frame_);
		if (result == null) throw new NullPointerException("this.function().invoke(this.frame()) = null");
		this._value_ = result;
		this._frame_ = null;
		this._function_ = null;
		return result;
	}

	/**
	 * Diese Methode gibt die Stapelrahmen oder {@code null} zurück.<br>
	 * Der erste Aufruf von {@link #value()} setzt die Stapelrahmen auf {@code null}.
	 * 
	 * @return Stapelrahmen oder {@code null}.
	 */
	public final FEMFrame frame() {
		return this._frame_;
	}

	/**
	 * Diese Methode gibt die Funktion oder {@code null} zurück.<br>
	 * Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
	 * 
	 * @return Funktion oder {@code null}.
	 */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FEMType<?> type() {
		return this.value().type();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object data() {
		return this.value().data();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		if (this._value_ != null) {
			target.putValue(this._value_);
		} else {
			target.putHandler(this._function_).put(this._frame_);
		}
	}

}