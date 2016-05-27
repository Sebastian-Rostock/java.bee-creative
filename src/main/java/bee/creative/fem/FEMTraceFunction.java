package bee.creative.fem;

import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link Tracer}.<br>
 * Die genaue Beschreibung der Verarbeitung kann bei der Methode {@link #invoke(FEMFrame)} nachgelesen werden.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see Tracer */
public abstract class FEMTraceFunction extends FEMBaseFunction {

	/** Diese Klasse implementiert ein Objekt zur Verwaltung der Zustandsdaten einer {@link FEMTraceFunction} zur Verfolgung und Überwachung der Verarbeitung von
	 * Funktionen. Dieses Objekt wird dazu das Argument für die Methoden des {@link TracerHelper} genutzt, welcher auf die Ereignisse der Überwachung reagieren
	 * kann.
	 * 
	 * @see FEMTraceFunction
	 * @see TracerHelper
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Tracer {

		/** Dieses Feld speichert den {@link TracerHelper}. */
		TracerHelper _helper_ = TracerHelper.EMPTY;

		/** Dieses Feld speichert den Stapelrahmen der Funktion. Dieser kann in der Methode {@link TracerHelper#onExecute(Tracer)} für den Aufruf angepasst werden. */
		FEMFrame _frame_;

		/** Dieses Feld speichert die Function, die nach {@link TracerHelper#onExecute(Tracer)} aufgerufen wird bzw. vor {@link TracerHelper#onThrow(Tracer)} oder
		 * {@link TracerHelper#onReturn(Tracer)} aufgerufen wurde. Diese kann in der Methode {@link TracerHelper#onExecute(Tracer)} für den Aufruf angepasst werden. */
		FEMFunction _function_;

		/** Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode {@link TracerHelper#onReturn(Tracer)}
		 * angepasst werden. */
		FEMValue _result_;

		/** Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode {@link TracerHelper#onThrow(Tracer)}
		 * angepasst werden. */
		RuntimeException _exception_;

		{}

		/** Diese Methode gibt den {@link TracerHelper} zurück.
		 * 
		 * @return {@link TracerHelper}. */
		public final TracerHelper getHelper() {
			return this._helper_;
		}

		/** Diese Methode gibt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ergebniswert oder {@code null}. */
		public final FEMValue getResult() {
			return this._result_;
		}

		/** Diese Methode gibt den aktuellen Stapelrahmen zurück, der zur Auswertung der {@link #getFunction() aktuellen Funktion} verwendet wird.
		 * 
		 * @return Stapelrahmen oder {@code null}. */
		public final FEMFrame getFrame() {
			return this._frame_;
		}

		/** Diese Methode gibt die aktuelle Funktion zurück, die mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet wird.
		 * 
		 * @return Funktion oder {@code null}. */
		public final FEMFunction getFunction() {
			return this._function_;
		}

		/** Diese Methode gibt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ausnahme oder {@code null}. */
		public final RuntimeException getException() {
			return this._exception_;
		}

		/** Diese Methode setzt die Überwachungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Überwachungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final Tracer useHelper(final TracerHelper value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._helper_ = value;
			return this;
		}

		/** Diese Methode setzt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Die {@link #getException()
		 * aktuelle Ausnahme} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ergebniswert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final Tracer useResult(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._result_ = value;
			this._exception_ = null;
			return this;
		}

		/** Diese Methode setzt den aktuellen Stapelrahmen und gibt {@code this} zurück. Dieser wird zur Auswertung der {@link #getFunction() aktuellen Funktion}
		 * verwendet.
		 * 
		 * @param value Stapelrahmen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final Tracer useFrame(final FEMFrame value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._frame_ = value;
			return this;
		}

		/** Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet.
		 * 
		 * @param value Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final Tracer useFunction(final FEMFunction value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._function_ = value;
			return this;
		}

		/** Diese Methode setzt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Der {@link #getResult() aktuelle
		 * Ergebniswert} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ausnahme.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final Tracer useException(final RuntimeException value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._result_ = null;
			this._exception_ = value;
			return this;
		}

		/** Diese Methode setzt alle aktuellen Einstellungen auf {@code null} und gibt {@code this} zurück.
		 * 
		 * @see #getFrame()
		 * @see #getFunction()
		 * @see #getResult()
		 * @see #getException()
		 * @return {@code this}. */
		public final Tracer clear() {
			this._frame_ = null;
			this._function_ = null;
			this._result_ = null;
			this._exception_ = null;
			return this;
		}

		/** Diese Methode gibt die gegebenen Funktion als {@link FEMTraceFunction} oder unverändert zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link TracerHelper#onExecute(Tracer)} aufgerufen und zur Modifikation von {@link Tracer#_function_}
		 * verwendet werden.
		 * <p>
		 * Wenn die Funktion ein {@link TracerInput} ist, wird das Ergebnis von {@link TracerInput#toTrace(Tracer)} zurück gegeben. Andernfalls wird die gegebene
		 * Funktion zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist. */
		public final FEMFunction trace(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (function instanceof TracerInput) return ((TracerInput)function).toTrace(this);
			return function;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toFormatString(true, true, this, "helper", this._helper_, "frame", this._frame_, "function", this._function_, "result", this._result_,
				"exception", this._exception_);
		}

	}

	/** Diese Schnittstelle definiert ein Objekt, welches sich selbst in eine {@link FEMTraceFunction} überführen kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface TracerInput {

		/** Diese Methode gibt dieses Objekt als {@link FEMTraceFunction} mit dem gegebenen {@link Tracer} zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link TracerHelper#onExecute(Tracer)} aufgerufen und zur Modifikation der {@link Tracer#getFunction()
		 * aktuellen Funktion} des {@link Tracer} verwendet werden.<br>
		 * Wenn dieses Objekt ein Wert ist, sollte dieser sich in einer {@link FEMValueFunction} liefern.
		 * 
		 * @param tracer {@link Tracer}.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist. */
		public FEMFunction toTrace(final Tracer tracer) throws NullPointerException;

	}

	/** Diese Schnittstelle definiert die Überwachungsmethoden zur Verfolgung der Verarbeitung von Funktionen.
	 * 
	 * @see Tracer
	 * @see FEMTraceFunction
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface TracerHelper {

		/** Dieses Feld speichert den {@code default}-{@link TracerHelper}, dessen Methoden den {@link Tracer} nicht modifizieren. */
		public static final TracerHelper EMPTY = new TracerHelper() {

			@Override
			public void onThrow(final Tracer event) {
			}

			@Override
			public void onReturn(final Tracer event) {
			}

			@Override
			public void onExecute(final Tracer event) {
			}

			@Override
			public String toString() {
				return "EMPTY";
			}

		};

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
		 * {@link Tracer#getException()} kann hierbei angepasst werden.
		 * 
		 * @see Tracer#useException(RuntimeException)
		 * @param event {@link Tracer}. */
		public void onThrow(Tracer event);

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
		 * {@link Tracer#getResult()} kann hierbei angepasst werden.
		 * 
		 * @see Tracer#useResult(FEMValue)
		 * @param event {@link Tracer}. */
		public void onReturn(Tracer event);

		/** Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link Tracer#getFrame()} und {@link Tracer#getFunction()} können hierbei
		 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Stapelrahmen durchzuführen.
		 * 
		 * @see Tracer#useFrame(FEMFrame)
		 * @see Tracer#useFunction(FEMFunction)
		 * @param event {@link Tracer}. */
		public void onExecute(Tracer event);

	}

	@SuppressWarnings ("javadoc")
	static final class TraceFunction extends FEMTraceFunction implements TracerInput {

		public TraceFunction(final Tracer tracer, final FEMFunction function) throws NullPointerException {
			super(tracer, function);
		}

		{}

		@Override
		public final FEMFunction toTrace(final Tracer tracer) throws NullPointerException {
			if (this._tracer_.equals(tracer)) return this;
			return tracer.trace(this._function_);
		}

	}

	/** Diese Methode gibt eine neue {@link FEMTraceFunction} mit den gegebenen Parametern zurück.
	 * 
	 * @param tracer {@link Tracer}.
	 * @param function Funktion.
	 * @return {@link FEMTraceFunction}.
	 * @throws NullPointerException Wenn {@code tracer} bzw. {@code function} {@code null} ist. */
	public static FEMTraceFunction from(final Tracer tracer, final FEMFunction function) throws NullPointerException {
		return new TraceFunction(tracer, function);
	}

	{}

	/** Dieses Feld speichert den {@link Tracer}. */
	final Tracer _tracer_;

	/** Dieses Feld speichert die aufzurufende Funktion. */
	final FEMFunction _function_;

	/** Dieser Konstruktor initialisiert Funktion und {@link Tracer}.
	 * 
	 * @param tracer {@link Tracer}.
	 * @param function Funktion.
	 * @throws NullPointerException Wenn {@code tracer} bzw. {@code function} {@code null} ist. */
	FEMTraceFunction(final Tracer tracer, final FEMFunction function) throws NullPointerException {
		if (tracer == null) throw new NullPointerException("tracer = null");
		if (function == null) throw new NullPointerException("function = null");
		this._tracer_ = tracer;
		this._function_ = function;
	}

	{}

	/** Diese Methode gibt den {@link Tracer} zurück, dessen Zustand in {@link #invoke(FEMFrame)} modifiziert wird.
	 * 
	 * @return {@link Tracer}. */
	public final Tracer tracer() {
		return this._tracer_;
	}

	/** Diese Methode gibt die aufzurufende {@link FEMFunction} zurück.
	 * 
	 * @return aufzurufende {@link FEMFunction}. */
	public final FEMFunction function() {
		return this._function_;
	}

	{}

	/** {@inheritDoc}
	 * <p>
	 * Hierbei werden dem {@link #tracer()} zuerst der gegebene Stapelrahmen sowie der {@link #function() aufzurufenden Funktion} bekannt gegeben und die Methode
	 * {@link TracerHelper#onExecute(Tracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
	 * Anschließend wird die {@link Tracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link Tracer#getFrame() aktuellen Stapelrahmen}
	 * ausgewertet und das Ergebnis im {@link #tracer()} {@link Tracer#useResult(FEMValue) gespeichert}.<br>
	 * Abschließend werden dann {@link TracerHelper#onReturn(Tracer) tracer().helper().onReturn(tracer())} aufgerufen und der {@link Tracer#getResult() aktuelle
	 * Ergebniswert} zurück gegeben.<br>
	 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link Tracer#useException(RuntimeException) gespeichert}, wird
	 * {@link TracerHelper#onThrow(Tracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link Tracer#getException() altuelle Ausnahme} des
	 * {@link #tracer()} ausgelöst.<br>
	 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link Tracer#clear() bereinigt}.<br>
	 * Der verwendete {@link TracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt. */
	@Override
	public final FEMValue invoke(final FEMFrame frame) {
		final Tracer tracer = this._tracer_;
		try {
			final TracerHelper helper = tracer.getHelper();
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

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putFunction(this._function_);
	}

}