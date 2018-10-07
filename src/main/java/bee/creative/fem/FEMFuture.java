package bee.creative.fem;

import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Wert, der als Ergebniswert einer Funktion mit <em>return-by-reference</em>-Semantik sowie als Parameterwert eines Aufrufs
 * mit <em>call-by-reference</em>-Semantik eingesetzt werden kann. Der Wert kapselt dazu eine gegebene {@link #function() Funktion} sowie einen gegebenen
 * {@link #frame() Stapelrahmen} und wertet diese Funktion erst dann mit diesem Stapelrahmen einmalig aus, wenn auf {@link #type() Datentyp} oder {@link #data()
 * Nutzdaten} {@link #result(boolean) zugegriffen} wird, d.h. bei einem Aufruf von {@link #result(boolean)}. Der von der Funktion berechnete Ergebniswert wird
 * zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf Stapelrahmen und Funktion aufgelöst.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMFuture extends FEMValue {

	/** Diese Methode gibt den Ergebniswert des {@link FEMFunction#invoke(FEMFrame) Aufrufs} der gegebenen Funktion mit dem gegebenen Stapelrahmen als Wert mit
	 * <em>call-by-reference</em>-Semantik zurück. Der gelieferte Ergebniswert verzögert die Auswertung der Funktion bis zum ersten {@link #result(boolean)
	 * Zugriff} auf seinen {@link #type() Datentyp} bzw. seine {@link #data() Nutzdaten}.
	 *
	 * @param frame Stapelrahmen.
	 * @param function Funktion.
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist. */
	public static FEMFuture from(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
		return new FEMFuture(Objects.notNull(frame), Objects.notNull(function));
	}

	/** Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
	 *
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMValue result;

	/** Dieses Feld speichert die Stapelrahmen zur Auswertung der Funktion oder {@code null}.
	 *
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMFrame frame;

	/** Dieses Feld speichert die Funktion oder {@code null}.
	 *
	 * @see FEMFunction#invoke(FEMFrame) */
	FEMFunction function;

	/** Dieser Konstruktor initialisiert Stapelrahmen und Funktion.
	 *
	 * @param frame Stapelrahmen.
	 * @param function Funktion. */
	FEMFuture(final FEMFrame frame, final FEMFunction function) {
		this.frame = frame;
		this.function = function;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #result() Ergebniswert} noch nicht ausgewertet wurde.
	 *
	 * @return Auswertungsstatus. */
	public final synchronized boolean ready() {
		return this.result != null;
	}

	/** Diese Methode gibt die Stapelrahmen oder {@code null} zurück. Der erste Aufruf von {@link #result(boolean)} setzt die Stapelrahmen auf {@code null}.
	 *
	 * @return Stapelrahmen oder {@code null}. */
	public final synchronized FEMFrame frame() {
		return this.frame;
	}

	/** Diese Methode gibt die Funktion oder {@code null} zurück. Der erste Aufruf von {@link #result(boolean)} setzt die Funktion auf {@code null}.
	 *
	 * @return Funktion oder {@code null}. */
	public final synchronized FEMFunction function() {
		return this.function;
	}

	/** Diese Methode gibt {@code this.result().data()} zurück. */
	@Override
	public final Object data() {
		return this.result(false).data();
	}

	/** Diese Methode gibt {@code this.result().type()} zurück. */
	@Override
	public final FEMType<?> type() {
		return this.result(false).type();
	}

	/** Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #function() Funktion} mit den {@link #frame() Stapelrahmen}
	 * zurück. Dieser Ergebniswert wird nur beim ersten Aufruf dieser Methode ermittelt und zur Wiederverwendung zwischengespeichert. Dabei werden die Verweise
	 * auf Stapelrahmen und Funktion aufgelöst.
	 *
	 * @see FEMFuture
	 * @see #frame()
	 * @see #function()
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist. */
	@Override
	public final synchronized FEMValue result(final boolean recursive) throws NullPointerException {
		FEMValue result = this.result;
		if (result != null) {
			if (!recursive) return result;
			final FEMValue result2 = result.result(true);
			if (result == result2) return result;
			this.result = result2;
			return result;
		} else {
			result = this.function.invoke(this.frame);
			if (result == null) throw new NullPointerException("function().invoke(frame()) = null");
			result = result.result(recursive);
			this.result = result;
			this.frame = null;
			this.function = null;
			return result;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.result().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMValue)) return false;
		return this.equals((FEMValue)object);
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized String toString() {
		if (this.result != null) return this.result.toString();
		return this.function.toString() + this.frame.toString();
	}

}