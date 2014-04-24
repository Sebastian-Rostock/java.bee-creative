package bee.creative.function;

import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Scopes.ValueScope;
import bee.creative.function.Types.NullType;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.FunctionValue;
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

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht dem der Funktion, die als erster Parameterwerte des gegebenen Ausführungskontexts gegeben ist und mit den Werten im
		 * {@link ArrayValue} des zweiten Parameterwertes aufgerufen wird.
		 */
		@Override
		public Value execute(final Scope scope) {
			if(scope.size() != 2) throw new IllegalArgumentException();
			return scope.get(0).valueTo(FunctionValue.TYPE).data().execute(new ValueScope(scope, scope.get(1).valueTo(ArrayValue.TYPE).data()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return CallFunction.class.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof CallFunction);
		}

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
	 * entspricht {@code function(params1, ..., paramsN)} entspricht.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ApplyFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ApplyFunction}.
		 */
		public static final ApplyFunction INSTANCE = new ApplyFunction();

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
			final int size = scope.size() - 1;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = scope.get(i);
			}
			final Function function = scope.get(size).valueTo(FunctionValue.TYPE).data();
			return function.execute(new ValueScope(scope, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return ApplyFunction.class.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof ApplyFunction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
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

		/**
		 * Diese Methode gibt die gegebenen Funktion als {@link TraceFunction} mit dem gegebenen {@link TraceHandler} oder unverändert zurück. Sie sollte zur
		 * rekursiven Weiterverfolgung in {@link TraceHandler#onExecute(TraceEvent)} aufgerufen und zur Modifikation von {@link TraceEvent#function} verwendet
		 * werden.
		 * <p>
		 * Wenn die Funktion eine {@link CompositeFunction} ist, wird eine {@link CompositeFunction} zurück gegeben, deren Parameterfunktionen in eine
		 * {@link TraceFunction} umgewandelt wurden. Wenn die Funktion eine {@link ValueFunction} ist und ihr Ergebniswert ein {@link FunctionValue} ist, wird diese
		 * ebenfalls in eine {@link TraceFunction} umgewandelt und als {@link ValueFunction} zurück gegeben. Andernfalls wird die gegebene Funktion zurück gegeben.
		 * 
		 * @param handler {@link TraceHandler}.
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static final Function trace(final TraceHandler handler, final Function function) throws NullPointerException {
			if((handler == null) || (function == null)) throw new NullPointerException();
			final Object clazz = function.getClass();
			if(clazz == CompositeFunction.class){
				final CompositeFunction function2 = (CompositeFunction)function;
				final Function[] functions2 = function2.functions();
				for(int i = 0, size = functions2.length; i < size; i++){
					functions2[i] = new TraceFunction(handler, functions2[i]);
				}
				return new CompositeFunction(new TraceFunction(handler, function2.function), function2.chained, functions2);
			}else if(clazz == ValueFunction.class){
				final Value value = function.execute(null);
				if(value.getClass() == FunctionValue.class) return new ValueFunction(new FunctionValue(new TraceFunction(handler, (Function)value.data())));
			}
			return function;
		}

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
			if((handler == null) || (function == null)) throw new NullPointerException();
			this.handler = handler;
			this.function = function;
		}

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
			try{
				event.result = event.function.execute(event.scope);
				event.exception = null;
				handler.onReturn(event);
				return event.result;
			}catch(final RuntimeException exception){
				event.result = null;
				event.exception = exception;
				handler.onThrow(event);
				throw event.exception;
			}
		}

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
			if(object == this) return true;
			if(!(object instanceof TraceFunction)) return false;
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
	 * Diese Klasse implementiert eine Funktion, deren Ergebniswert dem {@link ArrayValue} der Parameterwerte des Ausführungskontexts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ArrayFunction#execute(Scope)
	 */
	public static final class ArrayFunction implements Function {

		/**
		 * Dieses Feld speichert die {@link ArrayFunction}.
		 */
		public static final ArrayFunction INSTANCE = new ArrayFunction();

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht dem {@link ArrayValue} der Parameterwerte des gegebenen Ausführungskontexts.
		 * 
		 * @see Scope#get(int)
		 * @see Scope#size()
		 */
		@Override
		public ArrayValue execute(final Scope scope) {
			final int size = scope.size();
			final Value[] array = new Value[size];
			for(int i = 0; i < size; i++){
				array[i] = scope.get(i);
			}
			return new ArrayValue(0, array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof ArrayFunction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
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
		public static final ValueFunction NULL_FUNCTION = new ValueFunction(NullValue.INSTANCE);

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück.
		 * 
		 * @see Values#valueOf(Object)
		 * @param data Nutzdaten des Ergebniswerts.
		 * @return {@link ValueFunction}.
		 */
		public static Function valueOf(final Object data) {
			if(data == null) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(Values.valueOf(data));
		}

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück.
		 * 
		 * @param value Ergebniswert.
		 * @return {@link ValueFunction}.
		 */
		public static Function valueOf(final Value value) {
			if((value == null) || (value.type().id() == NullType.ID)) return ValueFunction.NULL_FUNCTION;
			return new ValueFunction(value);
		}

		/**
		 * Dieses Feld speichert den Ergebniswert.
		 */
		private final Value value;

		/**
		 * Dieser Konstruktor initialisiert den Ergebniswert.
		 * 
		 * @param value Ergebniswert.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if(value == null) throw new NullPointerException();
			this.value = value;
		}

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
			if(object == this) return true;
			if(!(object instanceof ValueFunction)) return false;
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
		static final Function[] INSTANCES = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		/**
		 * Diese Methode erzeugt eine eine projizierende Funktion, deren Ergebniswert dem {@code index}-ten Parameterwert des Ausführungskontexts entspricht, und
		 * gibt diese zurück.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index negativ ist.
		 */
		public static Function valueOf(final int index) throws IndexOutOfBoundsException {
			if(index < ParamFunction.INSTANCES.length) return ParamFunction.INSTANCES[index];
			return new ParamFunction(index);
		}

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
			if(index < 0) throw new IndexOutOfBoundsException();
			this.index = index;
		}

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
			if(object == this) return true;
			if(!(object instanceof ParamFunction)) return false;
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
			if((function == null) || (functions == null)) throw new NullPointerException();
			this.chained = chained;
			this.function = function;
			this.functions = functions;
		}

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
		 * {@code (this.chained() ? this.function().execute(scope).valueTo(FunctionValue.TYPE).data() : this.function()).execute(new CompositeScope(scope, this.functions()))}.
		 * 
		 * @see #chained()
		 * @see #function()
		 * @see #functions()
		 * @see CompositeScope
		 */
		@Override
		public Value execute(final Scope scope) {
			return (this.chained ? this.function.execute(scope).valueTo(FunctionValue.TYPE).data() : this.function)
				.execute(new CompositeScope(scope, this.functions));
		}

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
			if(object == this) return true;
			if(!(object instanceof CompositeFunction)) return false;
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
