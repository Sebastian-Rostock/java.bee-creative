package bee.creative.fem;

import java.util.Arrays;

/** Diese Klasse implementiert eine abstrakte Funktion, deren {@link #invoke(FEMFrame) Berechnungsmethode} mit einem {@link FEMFrame Stapelrahmen} aufgerufen
 * werden kann, um einen Ergebniswert zu ermitteln.<br>
 * Aus den {@link FEMFrame#params() Parameterwerten} sowie dem {@link FEMFrame#context() Kontextobjekt} der Stapelrahmens können hierbei Informationen für die
 * Berechnungen extrahiert werden. Der Zustand des Kontextobjekts kann auch modifiziert werden.
 * <p>
 * Die {@link #toString() Textdarstellung} der Funktion wird über {@link #toScript(FEMFormatter)} ermittelt.
 *
 * @see FEMValue
 * @see FEMFrame
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMFunction {

	@SuppressWarnings ("javadoc")
	static final class TraceFunction extends FEMFunction {

		final FEMTracer _tracer_;

		final FEMFunction _function_;

		TraceFunction(final FEMTracer tracer, final FEMFunction function) throws NullPointerException {
			if (tracer == null) throw new NullPointerException("tracer = null");
			this._tracer_ = tracer;
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			final FEMTracer tracer = this._tracer_;
			try {
				final FEMTracer.Listener helper = tracer.getListener();
				helper.onExecute(tracer.useFrame(frame).useFunction(this._function_));
				try {
					helper.onReturn(tracer.useResult(tracer.getFunction().invoke(tracer.getFrame())));
					return tracer.getResult();
				} catch (final RuntimeException exception) {
					helper.onThrow(tracer.useException(exception));
					throw tracer.getException();
				}
			} finally {
				tracer.clear();
			}
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this._function_.withTracer(tracer);
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this._function_.withoutTracer();
		}

		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class FrameFunction extends FEMFunction {

		final FEMFrame _frame_;

		final FEMFunction _function_;

		FrameFunction(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
			if (frame == null) throw new NullPointerException("frame = null");
			this._frame_ = frame;
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this._function_.invoke(this._frame_.withParams(frame.params()));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this._function_.withTracer(tracer).toClosure(this._frame_);
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this._function_.withoutTracer().toClosure(this._frame_);
		}

		@Override
		public final FEMFunction toClosure(final FEMFrame frame) throws NullPointerException {
			return this._function_.toClosure(frame);
		}

		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class FutureFunction extends FEMFunction {

		final FEMFunction _function_;

		FutureFunction(final FEMFunction function) {
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this.toFuture(frame);
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this._function_.withTracer(tracer).toFuture();
		}

		@Override
		public final FEMFunction toClosure(final FEMFrame params) {
			return this._function_.toClosure(params).toFuture();
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this._function_.withoutTracer().toFuture();
		}

		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_);
		}

		@Override
		public final FEMFunction toFuture() {
			return this;
		}

		@Override
		public final FEMValue toFuture(final FEMFrame frame) throws NullPointerException {
			return this._function_.toFuture(frame);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatFunction extends FEMFunction {

		final FEMFunction[] _params_;

		final FEMFunction _function_;

		ConcatFunction(final FEMFunction function, final FEMFunction... params) {
			this._params_ = params;
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return FEMHandler.from(this._function_.invoke(frame), frame._context_).value().invoke(frame.newFrame(this._params_));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this._params_.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withTracer(tracer);
			}
			return new ConcatFunction(this._function_.withTracer(tracer), params);
		}

		@Override
		public final FEMFunction withoutTracer() {
			final FEMFunction[] params = this._params_.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withoutTracer();
			}
			return new ConcatFunction(this._function_.withoutTracer(), params);
		}

		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_).putParams(Arrays.asList(this._params_));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ClosureFunction extends FEMFunction {

		{}

		final FEMFunction _function_;

		ClosureFunction(final FEMFunction function) throws NullPointerException {
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this._function_.toClosure(frame).toValue();
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this._function_.withTracer(tracer).toClosure();
		}

		/** {@inheritDoc} */
		@Override
		public FEMFunction withoutTracer() {
			return this._function_.withoutTracer().toClosure();
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putHandler(this._function_);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CompositeFunction extends FEMFunction {

		final FEMFunction[] _params_;

		final FEMFunction _function_;

		CompositeFunction(final FEMFunction function, final FEMFunction... params) {
			this._params_ = params;
			this._function_ = function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this._function_.invoke(frame.newFrame(this._params_));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this._params_.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withTracer(tracer);
			}
			return new CompositeFunction(this._function_.withTracer(tracer), params);
		}

		@Override
		public final FEMFunction withoutTracer() {
			final FEMFunction[] params = this._params_.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withoutTracer();
			}
			return new CompositeFunction(this._function_.withoutTracer(), params);
		}

		@Override
		public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_).putParams(Arrays.asList(this._params_));
		}

	}

	{}

	/** Diese Methode führt Berechnungen mit dem gegebenen Stapelrahmen durch und gibt den ermittelten Ergebniswert zurück.
	 *
	 * @param frame Stapelrahmen.
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist. */
	public abstract FEMValue invoke(FEMFrame frame) throws NullPointerException;

	/** Diese Methode gibt eine verkette Funktion zurück, welche den Ergebniswert dieser Funktion mit den gegebenen Parameterfunktionen aufruft. Der Ergebniswert
	 * der gelieferten Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht
	 * {@code FEMHandler.from(this.invoke(frame), frame.context()).value().invoke(frame.newFrame(this.params())}.
	 *
	 * @param params Parameterfunktionen.
	 * @return verkette Funktion.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFunction concat(final FEMFunction... params) throws NullPointerException {
		return new ConcatFunction(this, params.clone());
	}

	/** Diese Methode gibt eine komponierte Funktion zurück, welche diese Funktion mit den gegebenen Parameterfunktionen aufruft. Der Ergebniswert der gelieferten
	 * Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht {@code this.invoke(frame.newFrame(params)}.
	 *
	 * @see FEMFrame#withParams(FEMFunction[])
	 * @param params Parameterfunktionen.
	 * @return komponierte Funktion.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		return new CompositeFunction(this, params.clone());
	}

	/** Diese Methode gibt diese Funktion mit Verfolgung bzw. Überwachung der Verarbeitung durch das gegebene {@link FEMTracer Überwachungsobjekt} zurück.
	 * <p>
	 * Sie sollte zur rekursiven Weiterverfolgung in {@link FEMTracer.Listener#onExecute(FEMTracer)} aufgerufen sowie zur Modifikation der
	 * {@link FEMTracer#getFunction() aktuellen Funktion} des {@link FEMTracer Überwachungsobjekts} verwendet werden. Wenn diese Funktion ein Wert ist, sollte
	 * dieser sich unverändert liefern.
	 * <p>
	 * Der {@link #invoke(FEMFrame) Aufruf} der gelieferten Funktion startet folgenden Ablauf:<br>
	 * Zuerst werden dem Überwachungsobjekt der gegebene Stapelrahmen sowie die {@link FEMTracer#getFunction() aufzurufenden Funktion} bekannt gegeben und die
	 * Methode {@link FEMTracer.Listener#onExecute(FEMTracer) tracer.getListener().onExecute(tracer)} aufgerufen.<br>
	 * Anschließend wird die {@link FEMTracer#getFunction() aktuelle Funktion} des Überwachungsobjekts mit seinem {@link FEMTracer#getFrame() aktuellen
	 * Stapelrahmen} ausgewertet und das Ergebnis im Überwachungsobjekt {@link FEMTracer#useResult(FEMValue) gespeichert}.<br>
	 * Abschließend wird {@link FEMTracer.Listener#onReturn(FEMTracer) tracer.getListener().onReturn(tracer)} aufgerufen und der {@link FEMTracer#getResult()
	 * aktuelle Ergebniswert} geliefert.<br>
	 * Wenn eine {@link RuntimeException} auftritt, wird diese im Überwachungsobjekt {@link FEMTracer#useException(RuntimeException) gespeichert},
	 * {@link FEMTracer.Listener#onThrow(FEMTracer) tracer.getListener().onThrow(tracer)} aufgerufen und die {@link FEMTracer#getException() aktuelle Ausnahme}
	 * des Überwachungsobjekts ausgelöst.<br>
	 * In jedem Fall wird der Zustand des Überwachungsobjekts beim Verlassen dieser Methode {@link FEMTracer#clear() bereinigt}.<br>
	 * Der verwendete {@link FEMTracer.Listener} wird nur einmalig zu Beginn der Auswertung ermittelt.
	 *
	 * @see #withoutTracer()
	 * @param tracer {@link FEMTracer}.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code tracer} {@code null} ist. */
	public FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
		return new TraceFunction(tracer, this);
	}

	/** Diese Methode gibt diese Funktion ohne das über {@link #withTracer(FEMTracer)} gebundene {@link FEMTracer Überwachungsobjekt} zurück.
	 *
	 * @see #withTracer(FEMTracer)
	 * @return Funktion ohne {@link FEMTracer Überwachungsobjekt}. */
	public FEMFunction withoutTracer() {
		return this;
	}

	/** Diese Methode gibt einen Funktionszeiger auf diese Funktion als {@link FEMValue Wert} zurück.<br>
	 * Ein Wert liefert hierbei sich selbst. Jede andere Funktion liefert einen {@link FEMHandler}, sodass die über {@code this.toValue().toFunction()} ermittelte
	 * Funktion gleich zu dieser ist.
	 *
	 * @see FEMHandler
	 * @return Funktionszeiger. */
	public FEMValue toValue() {
		return new FEMHandler(this);
	}

	/** Diese Methode gibt diese Funktion mit {@code return-by-reference}-Semantik zurück. Der Ergebniswert der gelieferten Funktion wird über
	 * {@link #toFuture(FEMFrame)} ermittelt.
	 *
	 * @return {@link FEMFuture}-Funktion. */
	public FEMFunction toFuture() {
		return new FutureFunction(this);
	}

	/** Diese Methode gibt das Ergebnis dieser Funktion als {@link FEMFuture} zurück, wenn der Ergebniswert dieser Funktion vom gegebenen Stapelrahmen abhängt.
	 * Andernfalls wird der Ergebniswert direkt geliefert.
	 *
	 * @param frame Stapelrahmen.
	 * @return Ergebniswert ggf. als {@link FEMFuture}.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist. */
	public FEMValue toFuture(final FEMFrame frame) throws NullPointerException {
		if (frame == null) throw new NullPointerException("frame = null");
		return new FEMFuture(frame, this);
	}

	/** Diese Methode gibt eine Parameterfunktion zurück, welche bei der {@link #invoke(FEMFrame) Auswertung} mit einem {@link FEMFrame Stapelrahmen}
	 * {@code frame} einen Funktionszeiger auf diese Funktion liefert, welcher dieser Funktion mit Bindung an den Stapelrahmen entspricht, d.h.
	 * {@code this.toClosure(frame).toHandler()}.
	 *
	 * @return Funktionszeiger mit Stapalrahmenbindung. */
	public final FEMFunction toClosure() {
		return new ClosureFunction(this);
	}

	/** Diese Methode gibt diese Funktion an den gegebenen {@link FEMFrame Stapelrahmen} gebunden zurück.
	 * <p>
	 * Die zugesicherten Parameterwerte sowie das Kontextobjekt für den {@link #invoke(FEMFrame) Aufruf} der gelieferten Funktion entsprechen hierbei denen des in
	 * der Methode {@link #invoke(FEMFrame)} übergeben Stapelrahmen {@code frame}. Die zusätzlichen Parameterwerte stammen dagegen aus dem gegebenen Stapelrahmen
	 * {@code params}, d.h. {@code this.invoke(params.withParams(frame.params()))}.
	 *
	 * @param params {@link FEMFrame Stapelrahmen} mit den zugesicherten Parameterwerten.
	 * @return {@link FEMFunction} mit gebundenem {@link FEMFrame}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public FEMFunction toClosure(final FEMFrame params) throws NullPointerException {
		return new FrameFunction(params, this);
	}

	{}

	/** Diese Methode formatiert diese Funktion in einen Quelltext und fügt diesen an den gegebenen {@link FEMFormatter} an.<br>
	 * Sie kann von einer {@link FEMDomain} im Rahmen der Methoden {@link FEMDomain#formatData(FEMFormatter, Object)} bzw.
	 * {@link FEMDomain#formatFunction(FEMFormatter, FEMFunction)} eingesetzt werden.
	 *
	 * @param target {@link FEMFormatter}.
	 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann. */
	public void toScript(final FEMFormatter target) throws IllegalArgumentException {
		target.put(this.getClass().getName());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return new FEMFormatter().formatFunction(this);
	}

}