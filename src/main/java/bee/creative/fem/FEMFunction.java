package bee.creative.fem;

import java.util.Collection;
import java.util.List;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Funktion, deren {@link #invoke(FEMFrame) Berechnungsmethode} mit einem {@link FEMFrame Stapelrahmen} aufgerufen
 * werden kann, um einen Ergebniswert zu ermitteln.<br>
 * Aus den {@link FEMFrame#params() Parameterwerten} sowie dem {@link FEMFrame#context() Kontextobjekt} der Stapelrahmens können hierbei Informationen für die
 * Berechnungen extrahiert werden. Der Zustand des Kontextobjekts kann auch modifiziert werden.
 * <p>
 * Als {@link #toString() Textdarstellung} wird der {@link Class#getSimpleName() Klassenname} verwendet.
 *
 * @see FEMValue
 * @see FEMFrame
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMFunction {

	@SuppressWarnings ("javadoc")
	public static final class TraceFunction extends FEMFunction {

		final FEMTracer tracer;

		final FEMFunction function;

		TraceFunction(final FEMTracer tracer, final FEMFunction function) throws NullPointerException {
			this.tracer = Objects.assertNotNull(tracer);
			this.function = function;
		}

		{}

		public final FEMTracer tracer() {
			return this.tracer;
		}

		public final FEMFunction function() {
			return this.function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			final FEMTracer tracer = this.tracer;
			try {
				final FEMTracer.Listener helper = tracer.getListener();
				helper.onExecute(tracer.useFrame(frame).useFunction(this.function));
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
			return this.function.withTracer(tracer);
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this.function.withoutTracer();
		}

		@Override
		public final int hashCode() {
			return this.function.hashCode() ^ 123456789;
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof TraceFunction)) return false;
			final TraceFunction data = (TraceFunction)object;
			return this.function.equals(data.function);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class FrameFunction extends FEMFunction {

		final FEMFrame frame;

		final FEMFunction function;

		FrameFunction(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
			this.frame = Objects.assertNotNull(frame);
			this.function = function;
		}

		{}

		public final FEMFrame frame() {
			return this.frame;
		}

		public final FEMFunction function() {
			return this.function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this.function.invoke(this.frame.withParams(frame.params()));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this.function.withTracer(tracer).toClosure(this.frame);
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this.function.withoutTracer().toClosure(this.frame);
		}

		@Override
		public final int hashCode() {
			return this.frame.hashCode() ^ this.function.hashCode();
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FrameFunction)) return false;
			final FrameFunction data = (FrameFunction)object;
			return this.frame.equals(data.frame) && this.function.equals(data.function);
		}

		@Override
		public final FEMFunction toClosure(final FEMFrame frame) throws NullPointerException {
			return this.function.toClosure(frame);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class FutureFunction extends FEMFunction {

		final FEMFunction function;

		FutureFunction(final FEMFunction function) {
			this.function = function;
		}

		{}

		public final FEMFunction function() {
			return this.function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this.toFuture(frame);
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this.function.withTracer(tracer).toFuture();
		}

		@Override
		public final FEMFunction toClosure(final FEMFrame params) {
			return this.function.toClosure(params).toFuture();
		}

		@Override
		public final FEMFunction withoutTracer() {
			return this.function.withoutTracer().toFuture();
		}

		@Override
		public final int hashCode() {
			return this.function.hashCode() ^ 456789123;
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FutureFunction)) return false;
			final FutureFunction data = (FutureFunction)object;
			return this.function.equals(data.function);
		}

		@Override
		public final FEMFunction toFuture() {
			return this;
		}

		@Override
		public final FEMValue toFuture(final FEMFrame frame) throws NullPointerException {
			return this.function.toFuture(frame);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class ConcatFunction extends FEMFunction {

		final FEMFunction[] params;

		final FEMFunction function;

		ConcatFunction(final FEMFunction function, final FEMFunction... params) {
			this.params = params;
			this.function = function;
		}

		{}

		public final FEMFunction[] params() {
			return this.params.clone();
		}

		public final FEMFunction function() {
			return this.function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this.function.invoke(frame).toFunction().invoke(frame.newFrame(this.params));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this.params.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withTracer(tracer);
			}
			return new ConcatFunction(this.function.withTracer(tracer), params);
		}

		@Override
		public final FEMFunction withoutTracer() {
			final FEMFunction[] params = this.params.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withoutTracer();
			}
			return new ConcatFunction(this.function.withoutTracer(), params);
		}

		@Override
		public final int hashCode() {
			return this.function.hashCode() ^ Objects.hash((Object[])this.params);
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ConcatFunction)) return false;
			final ConcatFunction data = (ConcatFunction)object;
			return this.function.equals(data.function) && Objects.equals(this.params, data.params);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class ClosureFunction extends FEMFunction {

		final FEMFunction function;

		ClosureFunction(final FEMFunction function) throws NullPointerException {
			this.function = function;
		}

		{}

		public final FEMFunction function() {
			return this.function;
		}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this.function.toClosure(frame).toValue();
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			return this.function.withTracer(tracer).toClosure();
		}

		/** {@inheritDoc} */
		@Override
		public FEMFunction withoutTracer() {
			return this.function.withoutTracer().toClosure();
		}

		@Override
		public final int hashCode() {
			return this.function.hashCode() ^ 789123456;
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ClosureFunction)) return false;
			final ClosureFunction data = (ClosureFunction)object;
			return this.function.equals(data.function);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class CompositeFunction extends FEMFunction {

		final FEMFunction[] params;

		final FEMFunction function;

		CompositeFunction(final FEMFunction function, final FEMFunction... params) {
			this.params = params;
			this.function = function;
		}

		{}

		public final FEMFunction[] params() {
			return this.params.clone();
		}

		public final FEMFunction function() {
			return this.function;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			return this.function.invoke(frame.newFrame(this.params));
		}

		@Override
		public final FEMFunction withTracer(final FEMTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this.params.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withTracer(tracer);
			}
			return new CompositeFunction(this.function.withTracer(tracer), params);
		}

		@Override
		public final FEMFunction withoutTracer() {
			final FEMFunction[] params = this.params.clone();
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = params[i].withoutTracer();
			}
			return new CompositeFunction(this.function.withoutTracer(), params);
		}

		@Override
		public final int hashCode() {
			return this.function.hashCode() ^ Objects.hash((Object[])this.params);
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof CompositeFunction)) return false;
			final CompositeFunction data = (CompositeFunction)object;
			return this.function.equals(data.function) && Objects.equals(this.params, data.params);
		}

		@Override
		public final String toString() {
			return FEMDomain.NORMAL.formatFunction(this);
		}

	}

	{}

	/** Diese Methode führt Berechnungen mit dem gegebenen Stapelrahmen durch und gibt den ermittelten Ergebniswert zurück.
	 *
	 * @param frame Stapelrahmen.
	 * @return Ergebniswert.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist. */
	public abstract FEMValue invoke(FEMFrame frame) throws NullPointerException;

	/** Diese Methode gibt eine verkette Funktion zurück, welche den Ergebniswert dieser Funktion in einen {@link FEMHandler Funktionszeiger}
	 * {@link FEMHandler#from(FEMValue, FEMContext) umwandelt}, die davon {@link FEMHandler#value() referenzierte} Funktion mit den gegebenen Parameterfunktionen
	 * aufruft und deren Ergebniswert liefert. Der Ergebniswert der gelieferten Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht
	 * {@code this.invoke(frame).toFunction().invoke(frame.newFrame(this.params())}.
	 *
	 * @see ConcatFunction
	 * @param params Parameterfunktionen.
	 * @return verkette Funktion.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFunction concat(final FEMFunction... params) throws NullPointerException {
		return new ConcatFunction(this, params.clone());
	}

	/** Diese Methode ist eine Abkürzung für {@code this.concat(params.toArray(new FEMFunction[params.size()]))}.
	 *
	 * @see #concat(FEMFunction...)
	 * @see Collection#toArray(Object[]) */
	@SuppressWarnings ("javadoc")
	public final FEMFunction concat(final List<? extends FEMFunction> params) throws NullPointerException {
		return this.concat(params.toArray(new FEMFunction[params.size()]));
	}

	/** Diese Methode ist eine Abkürzung für {@code this.concat(Iterables.toList(params))}.
	 *
	 * @see #concat(List)
	 * @see Iterables#toList(Iterable) */
	@SuppressWarnings ("javadoc")
	public final FEMFunction concat(final Iterable<? extends FEMFunction> params) throws NullPointerException {
		return this.concat(Iterables.toList(params));
	}

	/** Diese Methode gibt eine komponierte Funktion zurück, welche diese Funktion mit den gegebenen Parameterfunktionen aufruft. Der Ergebniswert der gelieferten
	 * Funktion zu einem gegebenen {@link FEMFrame Stapelrahmen} {@code frame} entspricht {@code this.invoke(frame.newFrame(params)}.
	 *
	 * @see CompositeFunction
	 * @see FEMFrame#withParams(FEMFunction[])
	 * @param params Parameterfunktionen.
	 * @return komponierte Funktion.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public final FEMFunction compose(final FEMFunction... params) throws NullPointerException {
		return new CompositeFunction(this, params.clone());
	}

	/** Diese Methode ist eine Abkürzung für {@code this.compose(params.toArray(new FEMFunction[params.size()]))}.
	 *
	 * @see #compose(FEMFunction...)
	 * @see Collection#toArray(Object[]) */
	@SuppressWarnings ("javadoc")
	public final FEMFunction compose(final List<? extends FEMFunction> params) throws NullPointerException {
		return this.compose(params.toArray(new FEMFunction[params.size()]));
	}

	/** Diese Methode ist eine Abkürzung für {@code this.compose(Iterables.toList(params))}.
	 *
	 * @see #compose(List)
	 * @see Iterables#toList(Iterable) */
	@SuppressWarnings ("javadoc")
	public final FEMFunction compose(final Iterable<? extends FEMFunction> params) throws NullPointerException {
		return this.compose(Iterables.toList(params));
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
	 * @see TraceFunction
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

	/** Diese Methode gibt eine diese Funktion repräsentierenden {@link FEMValue Wert} zurück.<br>
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
	 * @see FutureFunction
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
		return new FEMFuture(Objects.assertNotNull(frame), this);
	}

	/** Diese Methode gibt eine Parameterfunktion zurück, welche bei der {@link #invoke(FEMFrame) Auswertung} mit einem {@link FEMFrame Stapelrahmen}
	 * {@code frame} einen Funktionszeiger auf diese Funktion liefert, welcher dieser Funktion mit Bindung an den Stapelrahmen entspricht, d.h.
	 * {@code this.toClosure(frame).toValue()}.
	 *
	 * @see ClosureFunction
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
	 * @see FrameFunction
	 * @param params {@link FEMFrame Stapelrahmen} mit den zugesicherten Parameterwerten.
	 * @return {@link FEMFunction} mit gebundenem {@link FEMFrame}.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
	public FEMFunction toClosure(final FEMFrame params) throws NullPointerException {
		return new FrameFunction(params, this);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}