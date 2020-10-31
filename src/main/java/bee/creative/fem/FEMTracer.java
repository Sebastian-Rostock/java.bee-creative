package bee.creative.fem;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein Überwachungsobjekt zur Verwaltung der Zustandsdaten bei der Verfolgung und Überwachung der Verarbeitung von Funktionen.
 * Dieses Objekt wird dazu das Argument für die Methoden des {@link Listener} genutzt, welcher auf die Ereignisse der Überwachung reagieren kann.
 *
 * @see Listener
 * @see FEMFunction#trace(FEMTracer)
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMTracer {

	/** Diese Schnittstelle definiert die Überwachungsmethoden zur Verfolgung der Verarbeitung von Funktionen.
	 *
	 * @see FEMTracer
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface Listener {

		/** Dieses Feld speichert die leeren Überwachungsmethoden, die den {@link FEMTracer} nicht modifizieren. */
		public static final Listener EMPTY = new Listener() {

			@Override
			public void onThrow(final FEMTracer tracer) {
			}

			@Override
			public void onReturn(final FEMTracer tracer) {
			}

			@Override
			public void onInvoke(final FEMTracer tracer) {
			}

			@Override
			public String toString() {
				return "EMPTY";
			}

		};

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion über {@code throw} aufgerufen. Das Feld
		 * {@link FEMTracer#getException()} kann hierbei angepasst werden.
		 *
		 * @see FEMTracer#useException(RuntimeException)
		 * @param tracer {@link FEMTracer}. */
		public void onThrow(FEMTracer tracer);

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion über {@code return} aufgerufen. Das
		 * Feld {@link FEMTracer#getResult()} kann hierbei angepasst werden.
		 *
		 * @see FEMTracer#useResult(FEMValue)
		 * @param tracer {@link FEMTracer}. */
		public void onReturn(FEMTracer tracer);

		/** Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link FEMTracer#getFrame()} und {@link FEMTracer#getFunction()} können hierbei
		 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Stapelrahmen durchzuführen.
		 *
		 * @see FEMTracer#useFrame(FEMFrame)
		 * @see FEMTracer#useFunction(FEMFunction)
		 * @param tracer {@link FEMTracer}. */
		public void onInvoke(FEMTracer tracer);

	}

	/** Dieses Feld speichert den {@link Listener}. */
	Listener listener = Listener.EMPTY;

	/** Dieses Feld speichert den Stapelrahmen der Funktion. Dieser kann in der Methode {@link Listener#onInvoke(FEMTracer)} für den Aufruf angepasst werden. */
	FEMFrame frame;

	/** Dieses Feld speichert die Function, die nach {@link Listener#onInvoke(FEMTracer)} aufgerufen wird bzw. vor {@link Listener#onThrow(FEMTracer)} oder
	 * {@link Listener#onReturn(FEMTracer)} aufgerufen wurde. Diese kann in der Methode {@link Listener#onInvoke(FEMTracer)} für den Aufruf angepasst werden. */
	FEMFunction function;

	/** Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode {@link Listener#onReturn(FEMTracer)}
	 * angepasst werden. */
	FEMValue result;

	/** Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode {@link Listener#onThrow(FEMTracer)}
	 * angepasst werden. */
	RuntimeException exception;

	/** Diese Methode gibt die Überwachungsmethoden zurück.
	 *
	 * @return Überwachungsmethoden. */
	public Listener getListener() {
		return this.listener;
	}

	/** Diese Methode gibt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} zurück.
	 *
	 * @return Ergebniswert oder {@code null}. */
	public FEMValue getResult() {
		return this.result;
	}

	/** Diese Methode gibt den aktuellen Stapelrahmen zurück, der zur Auswertung der {@link #getFunction() aktuellen Funktion} verwendet wird.
	 *
	 * @return Stapelrahmen oder {@code null}. */
	public FEMFrame getFrame() {
		return this.frame;
	}

	/** Diese Methode gibt die aktuelle Funktion zurück, die mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet wird.
	 *
	 * @return Funktion oder {@code null}. */
	public FEMFunction getFunction() {
		return this.function;
	}

	/** Diese Methode gibt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} zurück.
	 *
	 * @return Ausnahme oder {@code null}. */
	public RuntimeException getException() {
		return this.exception;
	}

	/** Diese Methode setzt die Überwachungsmethoden und gibt {@code this} zurück.
	 *
	 * @param value Überwachungsmethoden.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMTracer useListener(final Listener value) throws NullPointerException {
		this.listener = Objects.notNull(value);
		return this;
	}

	/** Diese Methode setzt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Die {@link #getException()
	 * aktuelle Ausnahme} wird damit auf {@code null} gesetzt.
	 *
	 * @param value Ergebniswert.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMTracer useResult(final FEMValue value) throws NullPointerException {
		this.result = Objects.notNull(value);
		this.exception = null;
		return this;
	}

	/** Diese Methode setzt den aktuellen Stapelrahmen und gibt {@code this} zurück. Dieser wird zur Auswertung der {@link #getFunction() aktuellen Funktion}
	 * verwendet.
	 *
	 * @param value Stapelrahmen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMTracer useFrame(final FEMFrame value) throws NullPointerException {
		this.frame = Objects.notNull(value);
		return this;
	}

	/** Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet.
	 *
	 * @param value Funktion.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMTracer useFunction(final FEMFunction value) throws NullPointerException {
		this.function = Objects.notNull(value);
		return this;
	}

	/** Diese Methode setzt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Der {@link #getResult() aktuelle
	 * Ergebniswert} wird damit auf {@code null} gesetzt.
	 *
	 * @param value Ausnahme.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public FEMTracer useException(final RuntimeException value) throws NullPointerException {
		this.exception = Objects.notNull(value);
		this.result = null;
		return this;
	}

	/** Diese Methode setzt alle aktuellen Einstellungen auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #getFrame()
	 * @see #getFunction()
	 * @see #getResult()
	 * @see #getException()
	 * @return {@code this}. */
	public FEMTracer clear() {
		this.frame = null;
		this.function = null;
		this.result = null;
		this.exception = null;
		return this;
	}

	@Override
	public String toString() {
		return Objects.toStringCall(true, true, this, "listener", this.listener, "frame", this.frame, "function", this.function, "result", this.result,
			"exception", this.exception);
	}

}