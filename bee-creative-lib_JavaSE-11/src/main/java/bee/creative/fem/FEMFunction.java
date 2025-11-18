package bee.creative.fem;

import static bee.creative.lang.Objects.notNull;
import bee.creative.lang.Objects;
import bee.creative.util.Iterables;

/** Diese Schnittstelle definiert eine Funktion, deren {@link #invoke(FEMFrame) Berechnungsmethode} mit einem {@link FEMFrame Stapelrahmen} aufgerufen werden
 * kann, um einen Ergebniswert zu ermitteln. Aus den {@link FEMFrame#params() Parameterwerten} sowie dem {@link FEMFrame#context() Kontextobjekt} der
 * Stapelrahmens können hierbei Informationen für die Berechnungen extrahiert werden. Der Zustand des Kontextobjekts kann auch modifiziert werden.
 * <p>
 * Als {@link #toString() Textdarstellung} wird der {@link Class#getSimpleName() Klassenname} verwendet.
 *
 * @see FEMValue
 * @see FEMFrame
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FEMFunction {

	/** Dieses Feld speichert die leere Parameterliste. */
	FEMFunction[] PARAMS = new FEMFunction[0];

	/** Diese Methode führt Berechnungen mit dem gegebenen Stapelrahmen durch und gibt den ermittelten Ergebniswert zurück.
	 *
	 * @param frame Stapelrahmen.u
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist. */
	FEMValue invoke(FEMFrame frame) throws NullPointerException;

	/** Diese Methode gibt eine komponierte Funktion zurück, welche diese Funktion mit den gegebenen Parameterfunktionen aufruft.
	 * <p>
	 * Der Ergebniswert der gelieferten Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht grundsätzlich:
	 * <pre>this.invoke(frame.newFrame(params)</pre> Wenn diese Funktion bereits komponiert ist, wird zur Ermittlung des Ergebniswert der gelieferten Funktion
	 * zuerst der Ergebniswert dieser Funktion in einen {@link FEMHandler Funktionszeiger} {@link FEMHandler#toFunction() umwandelt}, deren
	 * {@link FEMHandler#value() referenzierte} Funktion schließlich mit den gegebenen Parameterfunktionen aufgerufen werden. Der Ergebniswert der gelieferten
	 * Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht dann:
	 * <pre>this.invoke(frame).toFunction().invoke(frame.newFrame(params)</pre>
	 *
	 * @see FEMComposite
	 * @see FEMFrame#withParams(FEMFunction[])
	 * @param params Parameterfunktionen.
	 * @return komponierte Funktion.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	default FEMFunction compose(FEMFunction... params) throws NullPointerException {
		return FEMComposite.from(false, this, params.clone());
	}

	/** Diese Methode ist eine Abkürzung für {@link #compose(FEMFunction...) this.compose(Iterables.toArray(FEMFunction.PARAMS, params))}.
	 *
	 * @see Iterables#toArray(Iterable, Object[]) */
	default FEMFunction compose(Iterable<? extends FEMFunction> params) throws NullPointerException {
		return this.compose(Iterables.toArray(params, FEMFunction.PARAMS));
	}

	/** Diese Methode gibt diese Funktion mit Verfolgung bzw. Überwachung der Verarbeitung durch das gegebene {@link FEMTracer Überwachungsobjekt} zurück.
	 * <p>
	 * Sie sollte zur rekursiven Weiterverfolgung in {@link FEMTracer.Listener#onInvoke(FEMTracer)} aufgerufen sowie zur Modifikation der
	 * {@link FEMTracer#getFunction() aktuellen Funktion} des {@link FEMTracer Überwachungsobjekts} verwendet werden. Wenn diese Funktion ein Wert ist, sollte
	 * dieser sich unverändert liefern.
	 * <p>
	 * Der {@link #invoke(FEMFrame) Aufruf} der gelieferten Funktion startet folgenden Ablauf: Zuerst werden dem Überwachungsobjekt der gegebene Stapelrahmen
	 * sowie die {@link FEMTracer#getFunction() aufzurufenden Funktion} bekannt gegeben und die Methode {@link FEMTracer.Listener#onInvoke(FEMTracer)
	 * tracer.getListener().onExecute(tracer)} aufgerufen. Anschließend wird die {@link FEMTracer#getFunction() aktuelle Funktion} des Überwachungsobjekts mit
	 * seinem {@link FEMTracer#getFrame() aktuellen Stapelrahmen} ausgewertet und das Ergebnis im Überwachungsobjekt {@link FEMTracer#useResult(FEMValue)
	 * gespeichert}. Abschließend wird {@link FEMTracer.Listener#onReturn(FEMTracer) tracer.getListener().onReturn(tracer)} aufgerufen und der
	 * {@link FEMTracer#getResult() aktuelle Ergebniswert} geliefert. Wenn eine {@link RuntimeException} auftritt, wird diese im Überwachungsobjekt
	 * {@link FEMTracer#useException(RuntimeException) gespeichert}, {@link FEMTracer.Listener#onThrow(FEMTracer) tracer.getListener().onThrow(tracer)} aufgerufen
	 * und die {@link FEMTracer#getException() aktuelle Ausnahme} des Überwachungsobjekts ausgelöst. In jedem Fall wird der Zustand des Überwachungsobjekts beim
	 * Verlassen dieser Methode {@link FEMTracer#clear() bereinigt}. Der verwendete {@link FEMTracer.Listener} wird nur einmalig zu Beginn der Auswertung
	 * ermittelt.
	 *
	 * @see TraceFunction
	 * @param tracer {@link FEMTracer}.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code tracer} {@code null} ist. */
	default FEMFunction trace(FEMTracer tracer) throws NullPointerException {
		return new TraceFunction(tracer, this);
	}

	/** Diese Methode gibt diese Funktion als {@link FEMValue Wert} zurück. Ein Wert liefert dabei stets sich selbst. Jede andere Funktion liefert einen
	 * {@link FEMHandler Funktionszeiger} auf sich selbst.
	 *
	 * @return Wert. */
	default FEMValue toValue() {
		return FEMHandler.from(this);
	}

	/** Diese Methode gibt diese Funktion mit {@code return-by-reference}-Semantik zurück. Der Ergebniswert der gelieferten Funktion wird über
	 * {@link #toFuture(FEMFrame)} ermittelt. Ein {@link FEMValue Wert} liefert dabei stets sich selbst.
	 *
	 * @see FutureFunction
	 * @return {@link FEMFuture}-Funktion. */
	default FEMFunction toFuture() {
		return new FutureFunction(this);
	}

	/** Diese Methode gibt das Ergebnis dieser Funktion als {@link FEMFuture} zurück, wenn der Ergebniswert dieser Funktion vom gegebenen Stapelrahmen abhängt.
	 * Andernfalls wird der Ergebniswert direkt geliefert. Ein Wert liefert dabei stets sich selbst.
	 *
	 * @param frame Stapelrahmen.
	 * @return Ergebniswert ggf. als {@link FEMFuture}.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist. */
	default FEMValue toFuture(FEMFrame frame) throws NullPointerException {
		return new FEMFuture(frame, this);
	}

	public static abstract class BaseFunction implements FEMFunction {

		@Override
		public String toString() {
			return FEMDomain.DEFAULT.printScript(this);
		}

	}

	public static final class TraceFunction extends BaseFunction {

		public FEMTracer tracer() {
			return this.tracer;
		}

		public FEMFunction target() {
			return this.target;
		}

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				var listener = this.tracer.getListener();
				listener.onInvoke(this.tracer.useFrame(frame).useFunction(this.target));
				try {
					listener.onReturn(this.tracer.useResult(this.tracer.getFunction().invoke(this.tracer.getFrame())));
					return this.tracer.getResult();
				} catch (RuntimeException exception) {
					listener.onThrow(this.tracer.useException(exception));
					throw this.tracer.getException();
				}
			} finally {
				this.tracer.clear();
			}
		}

		@Override
		public FEMFunction trace(FEMTracer tracer) throws NullPointerException {
			return this.target.trace(tracer);
		}

		@Override
		public int hashCode() {
			return Objects.hashPush(Objects.hash(this.target), 1237);
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof TraceFunction)) return false;
			var that = (TraceFunction)object;
			return this.target.equals(that.target);
		}

		final FEMTracer tracer;

		final FEMFunction target;

		TraceFunction(FEMTracer tracer, FEMFunction target) throws NullPointerException {
			this.tracer = notNull(tracer);
			this.target = target;
		}

	}

	public static final class FutureFunction extends BaseFunction {

		public FEMFunction target() {
			return this.target;
		}

		@Override
		public FEMValue invoke(FEMFrame frame) {
			return this.toFuture(frame);
		}

		@Override
		public FEMFunction trace(FEMTracer tracer) throws NullPointerException {
			return this.target.trace(tracer).toFuture();
		}

		@Override
		public int hashCode() {
			return Objects.hashPush(Objects.hash(this.target), 1271);
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof FutureFunction)) return false;
			var that = (FutureFunction)object;
			return this.target.equals(that.target);
		}

		@Override
		public FEMFunction toFuture() {
			return this;
		}

		final FEMFunction target;

		FutureFunction(FEMFunction function) {
			this.target = function;
		}

	}

}