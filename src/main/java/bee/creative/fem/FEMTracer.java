package bee.creative.fem;

import bee.creative.util.Objects;

/** Diese Klasse implementiert ein Überwachungsobjekt zur Verwaltung der Zustandsdaten bei der Verfolgung und Überwachung der Verarbeitung von Funktionen. Dieses
 * Objekt wird dazu das Argument für die Methoden des {@link Listener} genutzt, welcher auf die Ereignisse der Überwachung reagieren kann.
 * 
 * @see Listener
 * @see FEMFunction#withTracer(FEMTracer)
 * @see FEMFunction#withoutTracer()
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
			public void onThrow(final FEMTracer event) {
			}

			@Override
			public void onReturn(final FEMTracer event) {
			}

			@Override
			public void onExecute(final FEMTracer event) {
			}

			@Override
			public String toString() {
				return "EMPTY";
			}

		};

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
		 * {@link FEMTracer#getException()} kann hierbei angepasst werden.
		 * 
		 * @see FEMTracer#useException(RuntimeException)
		 * @param event {@link FEMTracer}. */
		public void onThrow(FEMTracer event);

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
		 * {@link FEMTracer#getResult()} kann hierbei angepasst werden.
		 * 
		 * @see FEMTracer#useResult(FEMValue)
		 * @param event {@link FEMTracer}. */
		public void onReturn(FEMTracer event);

		/** Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link FEMTracer#getFrame()} und {@link FEMTracer#getFunction()} können hierbei
		 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Stapelrahmen durchzuführen.
		 * 
		 * @see FEMTracer#useFrame(FEMFrame)
		 * @see FEMTracer#useFunction(FEMFunction)
		 * @param event {@link FEMTracer}. */
		public void onExecute(FEMTracer event);

	}

	/** Dieses Feld speichert den {@link Listener}. */
	Listener _listener_ = Listener.EMPTY;

	/** Dieses Feld speichert den Stapelrahmen der Funktion. Dieser kann in der Methode {@link Listener#onExecute(FEMTracer)} für den Aufruf angepasst werden. */
	FEMFrame _frame_;

	/** Dieses Feld speichert die Function, die nach {@link Listener#onExecute(FEMTracer)} aufgerufen wird bzw. vor {@link Listener#onThrow(FEMTracer)} oder
	 * {@link Listener#onReturn(FEMTracer)} aufgerufen wurde. Diese kann in der Methode {@link Listener#onExecute(FEMTracer)} für den Aufruf angepasst werden. */
	FEMFunction _function_;

	/** Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode {@link Listener#onReturn(FEMTracer)}
	 * angepasst werden. */
	FEMValue _result_;

	/** Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode {@link Listener#onThrow(FEMTracer)}
	 * angepasst werden. */
	RuntimeException _exception_;

	{}

	/** Diese Methode gibt den {@link Listener} zurück.
	 * 
	 * @return {@link Listener}. */
	public final Listener getListener() {
		return this._listener_;
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
	public final FEMTracer useListener(final Listener value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this._listener_ = value;
		return this;
	}

	/** Diese Methode setzt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Die {@link #getException()
	 * aktuelle Ausnahme} wird damit auf {@code null} gesetzt.
	 * 
	 * @param value Ergebniswert.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final FEMTracer useResult(final FEMValue value) throws NullPointerException {
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
	public final FEMTracer useFrame(final FEMFrame value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this._frame_ = value;
		return this;
	}

	/** Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet.
	 * 
	 * @param value Funktion.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final FEMTracer useFunction(final FEMFunction value) throws NullPointerException {
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
	public final FEMTracer useException(final RuntimeException value) throws NullPointerException {
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
	public final FEMTracer clear() {
		this._frame_ = null;
		this._function_ = null;
		this._result_ = null;
		this._exception_ = null;
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toFormatString(true, true, this, "listener", this._listener_, "frame", this._frame_, "function", this._function_, "result", this._result_,
			"exception", this._exception_);
	}

}