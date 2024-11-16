package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Wert, der als Ergebniswert einer Funktion mit <em>return-by-reference</em>-Semantik sowie als Parameterwert eines Aufrufs
 * mit <em>call-by-reference</em>-Semantik eingesetzt werden kann. Der Wert kapselt dazu eine gegebene {@link #target() Funktion} sowie einen gegebenen
 * {@link #frame() Stapelrahmen} und wertet diese Funktion erst dann mit diesem Stapelrahmen einmalig aus, wenn auf {@link #type() Datentyp} oder {@link #data()
 * Nutzdaten} {@link #result(boolean) zugegriffen} wird, d.h. bei einem Aufruf von {@link #result(boolean)}, {@link #hashCode()}, {@link #equals(Object)} und
 * {@link #toFunction()}. Der von der Funktion berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der
 * Funktion werden die Verweise auf Stapelrahmen und Funktion aufgelöst.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMFuture implements FEMValue {

	public FEMFuture(FEMFrame frame, FEMFunction function) throws NullPointerException {
		this.frame = Objects.notNull(frame);
		this.target = Objects.notNull(function);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #result() Ergebniswert} ausgewertet wurde, d.h. wenn {@link #target()} einen
	 * {@link FEMValue} liefert.
	 *
	 * @return Auswertungsstatus. */
	public synchronized boolean ready() {
		return this.target instanceof FEMValue;
	}

	/** Diese Methode gibt die Stapelrahmen zurück. Der erste Aufruf von {@link #result(boolean)} setzt die Stapelrahmen auf {@link FEMFrame#EMPTY leeren
	 * Stapelrahmen}.
	 *
	 * @return Stapelrahmen . */
	public synchronized FEMFrame frame() {
		return this.frame;
	}

	/** Diese Methode gibt die Funktion zurück. Der erste Aufruf von {@link #result(boolean)} setzt die Funktion auf den Ergebniswert.
	 *
	 * @return Funktion. */
	public synchronized FEMFunction target() {
		return this.target;
	}

	/** Diese Methode gibt {@code this.result().data()} zurück. */
	@Override
	public Object data() {
		return this.result().data();
	}

	/** Diese Methode gibt {@code this.result().type()} zurück. */
	@Override
	public FEMType<?> type() {
		return this.result().type();
	}

	/** Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #target() Funktion} mit den {@link #frame() Stapelrahmen}
	 * zurück. Dieser Ergebniswert wird nur beim ersten Aufruf dieser Methode ermittelt und zur Wiederverwendung zwischengespeichert. Dabei werden die Verweise
	 * auf Stapelrahmen und Funktion aufgelöst.
	 *
	 * @see #frame()
	 * @see #target()
	 * @see FEMFunction#invoke(FEMFrame)
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist. */
	@Override
	public synchronized FEMValue result(boolean deep) throws NullPointerException {
		var result = this.target.invoke(this.frame).result(deep);
		this.target = result;
		this.frame = FEMFrame.EMPTY;
		return result;
	}

	@Override
	public int hashCode() {
		return this.result().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMValue)) return false;
		return this.result().equals(object);
	}

	@Override
	public synchronized String toString() {
		if (this.target instanceof FEMValue) return this.target.toString();
		return this.target.toString() + this.frame.toString();
	}

	@Override
	public FEMFunction toFunction() {
		return this.result().toFunction();
	}

	private FEMFrame frame;

	private FEMFunction target;

}