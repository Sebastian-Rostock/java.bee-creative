package bee.creative.fem;

import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;

/** Diese Klasse implementiert einen Wert, der als Ergebniswert einer Funktion mit <em>return-by-reference</em>-Semantik sowie als Parameterwert eines Aufrufs
 * mit <em>call-by-reference</em>-Semantik eingesetzt werden kann.<br>
 * Der Wert kapselt dazu eine gegebene {@link #function() Funktion} sowie einen gegebenen {@link #frame() Stapelrahmen} und wertet diese Funktion erst dann mit
 * dem diesem Stapelrahmen einmalig aus, wenn auf {@link #type() Datentyp} oder {@link #data() Nutzdaten} {@link #result(boolean) zugegriffen} wird.<br>
 * Der von der Funktion berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert.<br>
 * Nach der einmaligen Auswertung der Funktion werden die Verweise auf Stapelrahmen und Funktion aufgelöst.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMResult extends BaseValue {

	/** Diese Methode gibt den Ergebniswert des {@link FEMFunction#invoke(FEMFrame) Aufrufs} der gegebenen Funktion mit dem gegebenen Stapelrahmen als Wert mit
	 * <em>call-by-reference</em>-Semantik zurück.<br>
	 * Der gelieferte Ergebniswert verzögert die Auswertung der Funktion bis zum ersten {@link #result(boolean) Zugriff} auf seinen {@link #type() Datentyp} bzw.
	 * seine {@link #data() Nutzdaten}.
	 * 
	 * @param frame Stapelrahmen.
	 * @param function Funktion.
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist. */
	public static FEMResult from(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		if (frame == null) throw new NullPointerException("frame = null");
		if (function == null) throw new NullPointerException("function = null");
		return new FEMResult(frame, function);
	}

	{}

	/** Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMValue _result_;

	/** Dieses Feld speichert die Stapelrahmen zur Auswertung der Funktion oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMFrame _frame_;

	/** Dieses Feld speichert die Funktion oder {@code null}.
	 * 
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMFunction _function_;

	/** Dieser Konstruktor initialisiert Stapelrahmen und Funktion.
	 * 
	 * @param frame Stapelrahmen.
	 * @param function Funktion. */
	FEMResult(final FEMFrame frame, final FEMFunction function) {
		this._frame_ = frame;
		this._function_ = function;
	}

	{}

	/** Diese Methode gibt die Stapelrahmen oder {@code null} zurück.<br>
	 * Der erste Aufruf von {@link #result(boolean)} setzt die Stapelrahmen auf {@code null}.
	 * 
	 * @return Stapelrahmen oder {@code null}. */
	public final synchronized FEMFrame frame() {
		return this._frame_;
	}

	/** Diese Methode gibt die Funktion oder {@code null} zurück.<br>
	 * Der erste Aufruf von {@link #result(boolean)} setzt die Funktion auf {@code null}.
	 * 
	 * @return Funktion oder {@code null}. */
	public final synchronized FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final Object data() {
		return this.result(false).data();
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<?> type() {
		return this.result(false).type();
	}

	/** Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #function() Funktion} mit den {@link #frame() Stapelrahmen}
	 * zurück.<br>
	 * Dieser Ergebniwert wird nur beim ersten Aufruf dieser Methode ermittelt und zur Wiederverwendung zwischengespeichert. Dabei werden die Verweise auf
	 * Stapelrahmen und Funktion aufgelöst.
	 * 
	 * @see FEMResult
	 * @see #frame()
	 * @see #function()
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist. */
	@Override
	public final synchronized FEMValue result(final boolean recursive) throws NullPointerException {
		FEMValue result = this._result_;
		if (result != null) {
			if (!recursive) return result;
			final FEMValue result2 = result.result(true);
			if (result == result2) return result;
			this._result_ = result2;
			return result;
		} else {
			result = this._function_.invoke(this._frame_);
			if (result == null) throw new NullPointerException("function().invoke(frame()) = null");
			result = result.result(recursive);
			this._result_ = result;
			this._frame_ = null;
			this._function_ = null;
			return result;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		if (this._result_ != null) {
			target.putValue(this._result_);
		} else {
			target.putHandler(this._function_).put(this._frame_);
		}
	}

}