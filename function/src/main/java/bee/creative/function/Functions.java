package bee.creative.function;

import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Scopes.ValueScope;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.FunctionValue;
import bee.creative.function.Values.LazyValue;
import bee.creative.function.Values.NullValue;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsklassen und Hilfsmethoden zur Erzeugung von Funktionen.
 * 
 * @see Value
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Functions {

	/**
	 * Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link LazyValue} zu einer gegebenen,
	 * auszuwertenden Funktion ist.
	 * 
	 * @see LazyValue
	 * @see LazyFunction#execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LazyFunction implements Function {

		/**
		 * Diese Methode konvertiert die gegebene Funktion in eine {@link LazyFunction} und gibt diese zurück.
		 * 
		 * @param value auszuwertende Funktion.
		 * @return {@link LazyFunction}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static LazyFunction valueOf(final Function value) throws NullPointerException {
			return new LazyFunction(value);
		}

		/**
		 * Diese Methode überführt die gegebenen, komponierte Funktion in eine neue komponierte Funktion, bei der jede Parameterfunktion in eine
		 * {@link LazyFunction} konvertiert wurde, und gibt diese zurück.
		 * 
		 * @param function komponierte Funktion.
		 * @return neue komponierte Funktion mit {@link LazyFunction}s als Parameterfunktionen.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static CompositeFunction applyTo(final CompositeFunction function) throws NullPointerException {
			final Function[] functions = function.functions.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = LazyFunction.valueOf(functions[i]);
			}
			return CompositeFunction.valueOf(function.function, function.chained, functions);
		}

		{}

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert die auszuwertende Funktion.
		 * 
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public LazyFunction(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException();
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code LazyValue.valueOf(scope, this.function())}.
		 * 
		 * @see #function()
		 * @see LazyValue#valueOf(Scope, Function)
		 */
		@Override
		public LazyValue execute(final Scope scope) {
			return LazyValue.valueOf(scope, this.function);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof LazyFunction)) return false;
			final LazyFunction data = (LazyFunction)object;
			return Objects.equals(this.function, data.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.function);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion mit der Signatur {@code (function: FunctionValue, params: ArrayValue): Value}. Der Ergebniswert entspricht
	 * {@code function(params[0], params[1], ...)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CallFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link CallFunction}.
		 */
		public static final CallFunction INSTANCE = new CallFunction();

		{}

		/**
		 * Dieser Konstruktor ist versteckt.
		 */
		CallFunction() {
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht dem Ergebnis der Funktion, die als erster Parameterwerte ({@link FunctionValue}) des gegebenen Ausführungskontexts gegeben
		 * ist und mit den Werten im {@link ArrayValue} des zweiten Parameterwertes aufgerufen wird.
		 */
		@Override
		public Value execute(final Scope scope) {
			if (scope.size() != 2) throw new IllegalArgumentException();
			final Context context = scope.context();
			return context.cast(scope.get(0), FunctionValue.TYPE).data().execute(new ValueScope(scope, context.cast(scope.get(1), ArrayValue.TYPE).data()));
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion mit der Signatur {@code (params1: Value, ..., paramN: Value, function: FunctionValue): Value}. Der Ergebniswert
	 * entspricht {@code function(params1, ..., paramsN)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ApplyFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ApplyFunction}.
		 */
		public static final ApplyFunction INSTANCE = new ApplyFunction();

		{}

		/**
		 * Dieser Konstruktor ist versteckt.
		 */
		ApplyFunction() {
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht dem der Funktion, die als letzter Parameterwert des gegebenen Ausführungskontexts gegeben ist und mit den davor liegenden
		 * Parameterwerten aufgerufen wird.
		 * 
		 * @see Scope#get(int)
		 * @see Scope#size()
		 */
		@Override
		public Value execute(final Scope scope) {
			final int index = scope.size() - 1;
			final Context context = scope.context();
			return context.cast(scope.get(index), FunctionValue.TYPE).data().execute(new ValueScope(scope, Array.valueOf(scope).section(0, index)));
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, deren Ergebniswert einem {@link ArrayValue} mit den Parameterwerten des Ausführungskontexts entspricht.
	 * 
	 * @see ArrayFunction#execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class ArrayFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ArrayFunction}, die eine Kopie der Parameterwerte liefert.
		 * 
		 * @see Array#valueOf(Value...)
		 */
		public static final ArrayFunction COPY = new ArrayFunction() {

			@Override
			public boolean mode() {
				return true;
			}

		};

		/**
		 * Dieses Feld speichert die {@link ArrayFunction}, die eine Sicht auf die Parameterwerte liefert.
		 * 
		 * @see Array#valueOf(Scope)
		 */
		public static final ArrayFunction VIEW = new ArrayFunction() {

			@Override
			public boolean mode() {
				return false;
			}

		};

		{}

		/**
		 * Diese Methode gibt eine {@link ArrayFunction} zurück, die entwerder eine {@link Array#valueOf(Scope) Sicht} auf oder eine {@link Array#valueOf(Value...)
		 * Kopie} der Parameterwerte des ihr übergebenen Ausführungskontexts liefert.
		 * 
		 * @see #COPY
		 * @see #VIEW
		 * @param mode {@code true}, wenn die {@link ArrayFunction} statt einer Sicht eine Kopie der Parameterwerte liefern soll.
		 * @return {@link ArrayFunction}.
		 */
		public static ArrayFunction valueOf(final boolean mode) {
			return mode ? ArrayFunction.COPY : ArrayFunction.VIEW;
		}

		{}

		/**
		 * Dieser Konstruktor ist versteckt.
		 */
		ArrayFunction() {
		}

		{}

		/**
		 * Diese Methode gibt den Modus.
		 * 
		 * @return {@code true}, wenn {@link #execute(Scope)} statt einer {@link Array#valueOf(Scope) Sicht} eine {@link Array#valueOf(Value...) Kopie} der
		 *         Parameterwerte liefert.
		 */
		public abstract boolean mode();

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht dem {@link ArrayValue} der Parameterwerte des gegebenen Ausführungskontexts. Das {@link Array} wird hierbei entweder als
		 * {@link Array#valueOf(Scope) Sicht} oder als {@link Array#valueOf(Value...) Kopie} bereit gestellt. <br>
		 * Wenn {@link #mode()} {@code true} ist, entspricht der Ergebniswert {@code ArrayValue.valueOf(Array.valueOf(scope))}. Andernfalls entspricht er
		 * {@code ArrayValue.valueOf(Array.valueOf(Array.valueOf(scope).toArray()))}.
		 * 
		 * @see Array#valueOf(Scope)
		 * @see Array#valueOf(Value...)
		 */
		@Override
		public ArrayValue execute(final Scope scope) {
			if (this.mode()) return ArrayValue.valueOf(Array.valueOf(Array.valueOf(scope).value()));
			return ArrayValue.valueOf(Array.valueOf(scope));
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(ArrayFunction.class.getSimpleName(), this.mode());
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link TraceHandler}s und
	 * {@link TraceEvent}s. Die genaue Beschreibung der Verarbeitung kann bei der Methode {@link #execute(Scope)} nachgelesen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see TraceEvent
	 * @see TraceHandler
	 */
	public static final class TraceFunction implements Function {

		/**
		 * Diese Klasse implementiert das Argument für die Methoden des {@link TraceHandler}s.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @see TraceHandler
		 * @see TraceFunction
		 */
		public static final class TraceEvent {

			/**
			 * Dieses Feld speichert den Ausführungskontext der Funktion. Dieser kann in der Methode {@link TraceHandler#onExecute(TraceEvent)} für den Aufruf
			 * angepasst werden.
			 */
			public Scope scope;

			/**
			 * Dieses Feld speichert die Function, die nach {@link TraceHandler#onExecute(TraceEvent)} aufgerufen wird bzw. vor
			 * {@link TraceHandler#onThrow(TraceEvent)} oder {@link TraceHandler#onReturn(TraceEvent)} aufgerufen wurde. Diese kann in der Methode
			 * {@link TraceHandler#onExecute(TraceEvent)} für den Aufruf angepasst werden.
			 */
			public Function function;

			/**
			 * Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode {@link TraceHandler#onReturn(TraceEvent)}
			 * angepasst werden.
			 */
			public Value result;

			/**
			 * Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode
			 * {@link TraceHandler#onThrow(TraceEvent)} angepasst werden.
			 */
			public RuntimeException exception;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "function", this.function, "result", this.result, "exception",
					this.exception});
			}

		}

		/**
		 * Diese Schnittstelle definiert die Methoden zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen.
		 * 
		 * @see TraceEvent
		 * @see TraceFunction
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static interface TraceHandler {

			/**
			 * Diese Methode wird nach dem Verlassen der {@link Function#execute(Scope) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
			 * {@link TraceEvent#exception} kann hierbei angepasst werden.
			 * 
			 * @see TraceEvent#exception
			 * @param event {@link TraceEvent}.
			 */
			public void onThrow(TraceEvent event);

			/**
			 * Diese Methode wird nach dem Verlassen der {@link Function#execute(Scope) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
			 * {@link TraceEvent#result} kann hierbei angepasst werden.
			 * 
			 * @see TraceEvent#result
			 * @param event {@link TraceEvent}.
			 */
			public void onReturn(TraceEvent event);

			/**
			 * Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link TraceEvent#scope} und {@link TraceEvent#function} können hierbei
			 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Ausführungskontext durchzuführen.
			 * 
			 * @see TraceEvent#scope
			 * @see TraceEvent#function
			 * @param event {@link TraceEvent}.
			 */
			public void onExecute(TraceEvent event);

		}

		{}

		/**
		 * Diese Methode gibt die gegebenen Funktion als {@link TraceFunction} mit dem gegebenen {@link TraceHandler} oder unverändert zurück. Sie sollte zur
		 * rekursiven Weiterverfolgung in {@link TraceHandler#onExecute(TraceEvent)} aufgerufen und zur Modifikation von {@link TraceEvent#function} verwendet
		 * werden.
		 * <p>
		 * Wenn die Funktion eine {@link CompositeFunction} ist, wird eine {@link CompositeFunction} zurück gegeben, deren Parameterfunktionen in eine
		 * {@link TraceFunction} umgewandelt wurden. Wenn die Funktion eine {@link ValueFunction} ist und ihr Ergebniswert ein {@link FunctionValue} ist, wird diese
		 * ebenfalls in eine {@link TraceFunction} umgewandelt und als {@link ValueFunction} zurück gegeben. Wenn die Funktion eine {@link ClosureFunction} ist,
		 * wird deren Funktion ebenfalls in eine {@link TraceFunction} umgewandelt. Andernfalls wird die gegebene Funktion zurück gegeben.
		 * 
		 * @param handler {@link TraceHandler}.
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static Function trace(final TraceHandler handler, final Function function) throws NullPointerException {
			if ((handler == null) || (function == null)) throw new NullPointerException();
			final Object clazz = function.getClass();
			if (clazz == CompositeFunction.class) {
				final CompositeFunction function2 = (CompositeFunction)function;
				final Function[] functions2 = function2.functions();
				for (int i = 0, size = functions2.length; i < size; i++) {
					functions2[i] = new TraceFunction(handler, functions2[i]);
				}
				return CompositeFunction.valueOf(new TraceFunction(handler, function2.function), function2.chained, functions2);
			} else if (clazz == ValueFunction.class) {
				final ValueFunction function2 = (ValueFunction)function;
				final Value value = function2.value;
				if (value.getClass() == FunctionValue.class) return ValueFunction.valueOf(FunctionValue.valueOf(new TraceFunction(handler, (Function)value.data())));
			} else if (clazz == ClosureFunction.class) {
				final ClosureFunction function2 = (ClosureFunction)function;
				if (function2.scope == null) return ClosureFunction.valueOf(new TraceFunction(handler, function2.function));
				return new ClosureFunction(function2.scope, new TraceFunction(handler, function2.function));
			}
			return function;
		}

		{}

		/**
		 * Dieses Feld speichert den {@link TraceHandler}.
		 */
		final TraceHandler handler;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert Funktion und {@link TraceHandler}.
		 * 
		 * @param handler {@link TraceHandler}.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TraceFunction(final TraceHandler handler, final Function function) throws NullPointerException {
			if ((handler == null) || (function == null)) throw new NullPointerException();
			this.handler = handler;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den {@link TraceHandler} zurück.
		 * 
		 * @return {@link TraceHandler}.
		 */
		public TraceHandler handler() {
			return this.handler;
		}

		/**
		 * Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Hierbei werden zuerst ein {@link TraceEvent} mit dem gegebenen Ausführungskontext sowie der {@link #function() aufzurufenden Funktion} erzeugt und die
		 * Methode {@link TraceHandler#onExecute(TraceEvent)} aufgerufen. Anschließend werden die Funktion {@link TraceEvent#function} mit dem Ausführungskontext
		 * {@link TraceEvent#scope} ausgewertet und das Ergebnis auf {@link TraceEvent#result} respeichert. Abschließend werden dann
		 * {@link TraceHandler#onReturn(TraceEvent)} aufgerufen und der Ergebniswert von {@link TraceEvent#result} zurück gegeben. Wenn eine
		 * {@link RuntimeException} auftritt, werden diese auf {@link TraceEvent#exception} gespeichert, {@link TraceHandler#onThrow(TraceEvent)} aufgerufen und die
		 * {@link RuntimeException} von {@link TraceEvent#exception} ausgelöst.
		 */
		@Override
		public Value execute(final Scope scope) {
			final TraceEvent event = new TraceEvent();
			final TraceHandler handler = this.handler;
			event.scope = scope;
			event.function = this.function;
			handler.onExecute(event);
			try {
				event.result = event.function.execute(event.scope);
				event.exception = null;
				handler.onReturn(event);
				return event.result;
			} catch (final RuntimeException exception) {
				event.result = null;
				event.exception = exception;
				handler.onThrow(event);
				throw event.exception;
			}
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.handler, this.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof TraceFunction)) return false;
			final TraceFunction data = (TraceFunction)object;
			return Objects.equals(this.handler, data.handler) && Objects.equals(this.function, data.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.function.toString();
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion mit konstantem Ergebniswert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ValueFunction#execute(Scope)
	 */
	public static final class ValueFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ValueFunction}, die immer {@link NullValue#INSTANCE} liefert.
		 */
		public static final ValueFunction NULL_FUNCTION = ValueFunction.valueOf(NullValue.INSTANCE);

		{}

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück. Die Eingabe {@code null} wird hierbei zu {@link #NULL_FUNCTION}.
		 * 
		 * @see Values#valueOf(Object)
		 * @param data Nutzdaten des Ergebniswerts oder {@code null}.
		 * @return {@link ValueFunction}.
		 */
		public static ValueFunction valueOf(final Object data) {
			if (data == null) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(Values.valueOf(data));
		}

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück. Die Eingabe {@code null} wird hierbei zu {@link #NULL_FUNCTION}.
		 * 
		 * @param value Ergebniswert oder {@code null}.
		 * @return {@link ValueFunction}.
		 */
		public static ValueFunction valueOf(final Value value) {
			if ((value == null) || (value == NullValue.INSTANCE)) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(value);
		}

		{}

		/**
		 * Dieses Feld speichert den Ergebniswert.
		 */
		final Value value;

		/**
		 * Dieser Konstruktor initialisiert den Ergebniswert.
		 * 
		 * @param value Ergebniswert.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if (value == null) throw new NullPointerException();
			this.value = value;
		}

		{}

		/**
		 * Diese Methode gibt den Ergebniswert zurück.
		 * 
		 * @return Ergebniswert.
		 */
		public Value value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code this.value()}.
		 * 
		 * @see #value()
		 */
		@Override
		public Value execute(final Scope scope) {
			return this.value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ValueFunction)) return false;
			final ValueFunction data = (ValueFunction)object;
			return Objects.equals(this.value, data.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Ausführungskontexts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ParamFunction#execute(Scope)
	 */
	public static final class ParamFunction implements Function {

		/**
		 * Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0} bis {@code 9}.
		 */
		static final ParamFunction[] INSTANCES = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		{}

		/**
		 * Diese Methode erzeugt eine eine projizierende Funktion, deren Ergebniswert dem {@code index}-ten Parameterwert des Ausführungskontexts entspricht, und
		 * gibt diese zurück.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public static ParamFunction valueOf(final int index) throws IndexOutOfBoundsException {
			if (index < ParamFunction.INSTANCES.length) return ParamFunction.INSTANCES[index];
			return new ParamFunction(index);
		}

		{}

		/**
		 * Dieses Feld speichert den Index des Parameterwerts.
		 */
		final int index;

		/**
		 * Dieser Konstruktor initialisiert den Index des Parameterwerts.
		 * 
		 * @param index Index des Parameterwerts.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException();
			this.index = index;
		}

		{}

		/**
		 * Diese Methode gibt den Index des Parameterwerts zurück.
		 * 
		 * @return Index des Parameterwerts.
		 * @see #execute(Scope)
		 */
		public int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code scope.get(this.index())}.
		 * 
		 * @see #index()
		 */
		@Override
		public Value execute(final Scope scope) {
			return scope.get(this.index);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ParamFunction)) return false;
			final ParamFunction data = (ParamFunction)object;
			return this.index == data.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.index);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, die einen Ausführungskontext an eine gegebene Funktion binden kann.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction implements Function {

		/**
		 * Diese Methode konvertiert die gegebene Funktion in eine {@link ClosureFunction} und gibt diese zurück. Die {@link #execute(Scope)}-Methode der erzeugten
		 * {@link ClosureFunction} {@link ClosureFunction#ClosureFunction(Scope, Function) bindet} den ihr übergebenen Ausführungskontext an die gegebene Funktion
		 * und gibt die so erzeugte Funktion als {@link FunctionValue} zurück.
		 * 
		 * @see ClosureFunction#ClosureFunction(Function)
		 * @param function auszuwertende Funktion.
		 * @return {@link ClosureFunction}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ClosureFunction valueOf(final Function function) throws NullPointerException {
			return new ClosureFunction(function);
		}

		{}

		/**
		 * Dieses Feld speichert den gebundenen Ausführungskontext, dessen zusätzliche Parameterwerte genutzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert die Funktion. Die {@link #execute(Scope)}-Methode bindet den ihr übergebenen Ausführungskontext an die gegebene Funktion
		 * und gibt die so erzeugte Funktion als {@link FunctionValue} zurück.
		 * 
		 * @see #execute(Scope)
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ClosureFunction(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException();
			this.scope = null;
			this.function = function;
		}

		/**
		 * Dieser Konstruktor initialisiert den gebundenen Ausführungskontext und die Funktion. Die {@link #execute(Scope)}-Methode delegiert die Parameterwerte des
		 * ihr übergebenen Ausführungskontext zusammen mit den Parameterwerten des gebundenen Ausführungskontext als zusätzliche Parameterwerte an die gegebene
		 * Funktion und gibt deren Ergebniswert zurück.
		 * 
		 * @see #execute(Scope)
		 * @param scope gebundener Ausführungskontext.
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ClosureFunction(final Scope scope, final Function function) throws NullPointerException {
			if ((scope == null) || (function == null)) throw new NullPointerException();
			this.scope = scope;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den gebundene Ausführungskontext oder {@code null} zurück. Der Ausführungskontext ist {@code null}, wenn diese {@link ClosureFunction}
		 * über dem Konstruktor {@link #ClosureFunction(Function)} erzeugt wurde.
		 * 
		 * @see #ClosureFunction(Function)
		 * @see #ClosureFunction(Scope, Function)
		 * @see #execute(Scope)
		 * @return gebundener Ausführungskontext oder {@code null}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn diese Funktion über {@link #ClosureFunction(Function)} erzeugt wurde, entspricht der Ergebniswert
		 * {@code new FunctionValue(new ClosureFunction(scope, this.function()))}. Wenn sie dagegen über {@link #ClosureFunction(Scope, Function)} erzeugt wurde,
		 * entspricht der Ergebniswert {@code this.function().execute(new ValueScope(this.scope(), Array.valueOf(scope), false))}.
		 */
		@Override
		public Value execute(final Scope scope) {
			final Scope scope2 = this.scope;
			if (scope2 == null) return FunctionValue.valueOf(new ClosureFunction(scope, this.function));
			return this.function.execute(new ValueScope(scope2, Array.valueOf(scope), false));
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.scope, this.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ClosureFunction)) return false;
			final ClosureFunction data = (ClosureFunction)object;
			return Objects.equals(this.scope, data.scope) && Objects.equals(this.function, data.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (this.scope == null) return Objects.toStringCall(this, this.function);
			return Objects.toStringCall(this, this.scope, this.function);
		}

	}

	/**
	 * Diese Klasse definiert eine komponierte Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen
	 * berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see CompositeFunction#execute(Scope)
	 */
	public static final class CompositeFunction implements Function {

		/**
		 * Diese Methode erzeugt eine Funktion, die den Aufruf der gegebenen Funktion mit den Ergebniswerten der gegebenen Parameterfunktionen berechnet, und gibt
		 * diese zurück.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @return {@link CompositeFunction}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static CompositeFunction valueOf(final Function function, final Function... functions) throws NullPointerException {
			return new CompositeFunction(function, functions);
		}

		/**
		 * Diese Methode erzeugt eine Funktion, die den Aufruf der gegebenen Funktion mit den Ergebniswerten der gegebenen Parameterfunktionen berechnet, und gibt
		 * diese zurück.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param chained Verketung.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @return {@link CompositeFunction}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static CompositeFunction valueOf(final Function function, final boolean chained, final Function... functions) throws NullPointerException {
			return new CompositeFunction(function, chained, functions);
		}

		{}

		/**
		 * Dieses Feld speichert die Verkettung.
		 */
		final boolean chained;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final Function function;

		/**
		 * Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen.
		 */
		final Function[] functions;

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeFunction(final Function function, final Function... functions) throws NullPointerException {
			this(function, false, functions);
		}

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verketung und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param chained Verketung.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public CompositeFunction(final Function function, final boolean chained, final Function... functions) throws NullPointerException {
			if ((function == null) || (functions == null)) throw new NullPointerException();
			this.chained = chained;
			this.function = function;
			this.functions = functions;
		}

		{}

		/**
		 * Diese Methode gibt die Verkettung zurück.
		 * 
		 * @return Verkettung.
		 * @see #execute(Scope)
		 */
		public boolean chained() {
			return this.chained;
		}

		/**
		 * Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 * @see #execute(Scope)
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #execute(Scope)
		 */
		public Function[] functions() {
			return this.functions.clone();
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn die {@link #chained() Verkettung} deaktiviert ist, ergibt sich dieser Ergebniswert aus dem Aufruf der {@link #function() aufzurufende Funktion} mit
		 * den Parameterwerten, die sich aus der Auswertung der {@link #functions() Parameterfunktionen} mit dem gegebenen Ausführungskontext ergeben. Ist die
		 * {@link #chained() Verkettung} dagegen aktiviert, wird statt der {@link #function() aufzurufende Funktion} die Funktion verwendet, die bei der Auswertung
		 * der {@link #function() aufzurufenden Funktion} mit dem gegebenen Ausführungskontext ermittelt wurde.<br>
		 * <p>
		 * Der Ergebniswert entspricht
		 * {@code (this.chained() ? this.function().execute(scope).valueTo(FunctionValue.TYPE, scope.context()).data() : this.function()).execute(new CompositeScope(scope, this.functions()))}.
		 * 
		 * @see #chained()
		 * @see #function()
		 * @see #functions()
		 * @see CompositeScope
		 */
		@Override
		public Value execute(final Scope scope) {
			return (this.chained ? scope.context().cast(this.function.execute(scope), FunctionValue.TYPE).data() : this.function).execute(new CompositeScope(scope,
				this.functions));
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hashEx(this.function, this.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof CompositeFunction)) return false;
			final CompositeFunction data = (CompositeFunction)object;
			return Objects.equals(this.function, data.function) && Objects.equals(this.functions, data.functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(true, true, this, new Object[]{"function", this.function, "functions", this.functions});
		}

	}

}
