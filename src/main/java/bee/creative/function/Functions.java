package bee.creative.function;

import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Scripts.ScriptTracer;
import bee.creative.function.Scripts.ScriptTracerHelper;
import bee.creative.function.Scripts.ScriptTracerInput;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.FunctionValue;
import bee.creative.function.Values.ReturnValue;

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
	 * Diese Klasse implementiert eine abstakte Funktion als {@link ScriptFormatterInput}.<br>
	 * Die {@link #toString() Textdarstellung} der Funktion wird über {@link ScriptFormatter#formatFunction(Function...)} und damit via
	 * {@link #toScript(ScriptFormatter)} ermittelt.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseFunction implements Function, ScriptFormatterInput {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.toString());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Scripts.scriptFormatter().formatFunction(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen benannten Platzhalter einer Funktione, dessen {@link #execute(Scope)}-Methoden an eine gegebene Funktion delegiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ProxyFunction extends BaseFunction {

		/**
		 * Dieses Feld speichert den Namen.
		 */
		final String name;

		/**
		 * Dieses Feld speichert die Funktion.
		 */
		Function function;

		/**
		 * Dieser Konstruktor initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 */
		public ProxyFunction(final String name) throws NullPointerException {
			if (name == null) throw new NullPointerException("name = null");
			this.name = name;
		}

		{}

		/**
		 * Diese Methode setzt die in {@link #execute(Scope)} aufzurufende Funktion.
		 * 
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public void set(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.function = function;
		}

		/**
		 * Diese Methode gibt den Namen.
		 * 
		 * @return Name.
		 */
		public String name() {
			return this.name;
		}

		/**
		 * Diese Methode gibt die Funktion zurück, die in {@link #execute(Scope)} aufgerufen wird. Diese ist {@code null}, wenn {@link #set(Function)} noch nicht
		 * aufgerufen wurde.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public Function function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return this.function.execute(scope);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.name);
		}

	}

	{}

	{}

	{}

	/**
	 * Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link ReturnValue} zu einer gegebenen,
	 * auszuwertenden Funktion ist.
	 * 
	 * @see ReturnValue
	 * @see LazyFunction#execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LazyFunction extends BaseFunction {

		/**
		 * Diese Methode konvertiert die gegebene Funktion in eine {@link LazyFunction} und gibt diese zurück.
		 * 
		 * @param value auszuwertende Funktion.
		 * @return {@link LazyFunction}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public static LazyFunction valueOf(final Function value) throws NullPointerException {
			return new LazyFunction(value);
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
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public LazyFunction(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
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

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code ReturnValue.valueOf(scope, this.function())}.
		 * 
		 * @see #function()
		 * @see ReturnValue#valueOf(Scope, Function)
		 */
		@Override
		public ReturnValue execute(final Scope scope) {
			return new ReturnValue(scope, this.function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.function);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link ScriptTracerHelper}s und
	 * {@link ScriptTracer}s. Die genaue Beschreibung der Verarbeitung kann bei der Methode {@link #execute(Scope)} nachgelesen werden.
	 * <p>
	 * Hierbei werden zuerst ein {@link ScriptTracer} mit dem gegebenen Ausführungskontext sowie der {@link #function() aufzurufenden Funktion} erzeugt und die
	 * Methode {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen. Anschließend werden die Funktion {@link ScriptTracer#function} mit dem
	 * Ausführungskontext {@link ScriptTracer#scope} ausgewertet und das Ergebnis auf {@link ScriptTracer#result} respeichert. Abschließend werden dann
	 * {@link ScriptTracerHelper#onReturn(ScriptTracer)} aufgerufen und der Ergebniswert von {@link ScriptTracer#result} zurück gegeben. Wenn eine
	 * {@link RuntimeException} auftritt, werden diese auf {@link ScriptTracer#exception} gespeichert, {@link ScriptTracerHelper#onThrow(ScriptTracer)} aufgerufen
	 * und die {@link RuntimeException} von {@link ScriptTracer#exception} ausgelöst.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ScriptTracer
	 */
	public static final class TraceFunction extends BaseFunction {

		/**
		 * Dieses Feld speichert den {@link ScriptTracer}.
		 */
		final ScriptTracer tracer;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert Funktion und {@link ScriptTracer}.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public TraceFunction(final ScriptTracer tracer, final Function function) throws NullPointerException {
			if (tracer == null) throw new NullPointerException("tracer = null");
			if (function == null) throw new NullPointerException("function = null");
			this.tracer = tracer;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den {@link ScriptTracer} zurück.
		 * 
		 * @return {@link ScriptTracer}.
		 */
		public ScriptTracer tracer() {
			return this.tracer;
		}

		/**
		 * Diese Methode gibt die aufzurufende {@link Function} zurück.
		 * 
		 * @return aufzurufende {@link Function}.
		 */
		public Function function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			final ScriptTracer tracer = this.tracer;
			final ScriptTracerHelper helper = tracer.helper;
			tracer.scope = scope;
			tracer.function = this.function;
			helper.onExecute(tracer);
			try {
				tracer.result = tracer.function.execute(tracer.scope);
				tracer.exception = null;
				helper.onReturn(tracer);
				return tracer.result;
			} catch (final RuntimeException exception) {
				tracer.result = null;
				tracer.exception = exception;
				helper.onThrow(tracer);
				throw tracer.exception;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.function);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion mit konstantem Ergebniswert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ValueFunction#execute(Scope)
	 */
	public static final class ValueFunction extends BaseFunction {

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück.
		 * 
		 * @see #valueOf(Value)
		 * @param data Ergebniswert.
		 * @return {@link ValueFunction}.
		 */
		public static ValueFunction valueOf(final Value data) {
			return new ValueFunction(data);
		}

		/**
		 * Diese Methode erzeugt eine Funktion mit konstantem Ergebniswert und gibt diese zurück.
		 * 
		 * @see Values#valueOf(Object)
		 * @param data Nutzdaten des Ergebniswerts oder {@code null}.
		 * @return {@link ValueFunction}.
		 */
		public static ValueFunction valueOf(final Object data) {
			return ValueFunction.valueOf(Values.valueOf(data));
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
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ValueFunction(final Value value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
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

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value execute(final Scope scope) {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putValue(this.value);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Ausführungskontexts entspricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ParamFunction#execute(Scope)
	 */
	public static final class ParamFunction extends BaseFunction {

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

		{}

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
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$").put(this.index + 1);
		}

	}

	/**
	 * Diese Klasse definiert eine komponierte Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen
	 * berechnet.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see InvokeFunction#execute(Scope)
	 */
	public static final class InvokeFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Diese Methode erzeugt eine Funktion, die den Aufruf der gegebenen Funktion mit den Ergebniswerten der gegebenen Parameterfunktionen berechnet, und gibt
		 * diese zurück.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @return {@link InvokeFunction}.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code functions} {@code null} ist.
		 */
		public static InvokeFunction valueOf(final Function function, final Function... functions) throws NullPointerException {
			return new InvokeFunction(function, functions);
		}

		/**
		 * Diese Methode erzeugt eine Funktion, die den Aufruf der gegebenen Funktion mit den Ergebniswerten der gegebenen Parameterfunktionen berechnet, und gibt
		 * diese zurück.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param chained Verketung.
		 * @param functions Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @return {@link InvokeFunction}.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code functions} {@code null} ist.
		 */
		public static InvokeFunction valueOf(final Function function, final boolean chained, final Function... functions) throws NullPointerException {
			return new InvokeFunction(function, chained, functions);
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
		final Function[] params;

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code functions} {@code null} ist.
		 */
		public InvokeFunction(final Function function, final Function... params) throws NullPointerException {
			this(function, false, params);
		}

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verketung und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param chained Verketung.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code functions} {@code null} ist.
		 */
		public InvokeFunction(final Function function, final boolean chained, final Function... params) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (params == null) throw new NullPointerException("params = null");
			this.chained = chained;
			this.function = function;
			this.params = params;
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
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #execute(Scope)
		 */
		public Function[] params() {
			return this.params.clone();
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
		 * Diese Methode überführt die gegebenen, komponierte Funktion in eine neue komponierte Funktion, bei der jede Parameterfunktion in eine
		 * {@link LazyFunction} konvertiert wurde, und gibt diese zurück.
		 * 
		 * @param function komponierte Funktion.
		 * @return neue komponierte Funktion mit {@link LazyFunction}s als Parameterfunktionen.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public static InvokeFunction applyTo(final InvokeFunction function) throws NullPointerException {
			final Function[] functions = function.params.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = LazyFunction.valueOf(functions[i]);
			}
			return InvokeFunction.valueOf(function.function, function.chained, functions);
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn die {@link #chained() Verkettung} deaktiviert ist, ergibt sich dieser Ergebniswert aus dem Aufruf der {@link #function() aufzurufende Funktion} mit
		 * den Parameterwerten, die sich aus der Auswertung der {@link #params() Parameterfunktionen} mit dem gegebenen Ausführungskontext ergeben. Ist die
		 * {@link #chained() Verkettung} dagegen aktiviert, wird statt der {@link #function() aufzurufende Funktion} die Funktion verwendet, die bei der Auswertung
		 * der {@link #function() aufzurufenden Funktion} mit dem gegebenen Ausführungskontext ermittelt wurde.<br>
		 * <p>
		 * Der Ergebniswert entspricht
		 * {@code (this.chained() ? this.function().execute(scope).valueTo(FunctionValue.TYPE, scope.context()).data() : this.function()).execute(new CompositeScope(scope, this.functions()))}.
		 * 
		 * @see #chained()
		 * @see #function()
		 * @see #params()
		 * @see CompositeScope
		 */
		@Override
		public Value execute(final Scope scope) {
			return (this.chained ? scope.context().cast(this.function.execute(scope), FunctionType.TYPE).data() : this.function).execute(Scope.invokeScope(scope,
				this.params));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			final Function[] params = this.params;
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = tracer.traceFunction(params[i]);
			}
			return new InvokeFunction(tracer.traceFunction(this.function), this.chained, params);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.function).putParams(this.params);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, die einen Ausführungskontext an eine gegebene Funktion binden kann.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction extends BaseFunction {

		/**
		 * Diese Methode konvertiert die gegebene Funktion in eine {@link ClosureFunction} und gibt diese zurück. Die {@link #execute(Scope)}-Methode der erzeugten
		 * {@link ClosureFunction} {@link ClosureFunction#ClosureFunction(Scope, Function) bindet} den ihr übergebenen Ausführungskontext an die gegebene Funktion
		 * und gibt die so erzeugte Funktion als {@link FunctionValue} zurück.
		 * 
		 * @see ClosureFunction#ClosureFunction(Function)
		 * @param function auszuwertende Funktion.
		 * @return {@link ClosureFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
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
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public ClosureFunction(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
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
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public ClosureFunction(final Scope scope, final Function function) throws NullPointerException {
			if (scope == null) throw new NullPointerException("scope = null");
			if (function == null) throw new NullPointerException("function = null");
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

		{}

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
			return this.function.execute(Scope.valueScope(scope2, scope.toArray(), false));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putScope(this.function);
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link CallFunction}.
	 * <p>
	 * Diese Klasse implementiert eine Funktion mit der Signatur {@code (function: FunctionValue, params: ArrayValue): Value}. Der Ergebniswert entspricht
	 * {@code function(params[0], params[1], ...)}.
	 * <p>
	 * Der Ergebniswert entspricht dem Ergebnis der Funktion, die als erster Parameterwerte ({@link FunctionValue}) des gegebenen Ausführungskontexts gegeben ist
	 * und mit den Werten im {@link ArrayValue} des zweiten Parameterwertes aufgerufen wird.
	 */
	public static final Function CALL_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			if (scope.size() != 2) throw new IllegalArgumentException("scope.size() !=2");
			final Context context = scope.context();
			return context.cast(scope.get(0), FunctionType.TYPE).data().execute(Scope.valueScope(scope, context.cast(scope.get(1), ArrayType.TYPE).data()));
		}

		@Override
		public String toString() {
			return "CALL_FUNCTION";
		}

	};

	/**
	 * Dieses Feld speichert die {@link ApplyFunction}.
	 * <p>
	 * Diese Klasse implementiert eine Funktion mit der Signatur {@code (params1: Value, ..., paramN: Value, function: FunctionValue): Value}. Der Ergebniswert
	 * entspricht {@code function(params1, ..., paramsN)}.
	 * <p>
	 * Der Ergebniswert entspricht dem der Funktion, die als letzter Parameterwert des gegebenen Ausführungskontexts gegeben ist und mit den davor liegenden
	 * Parameterwerten aufgerufen wird.
	 */
	public static final Function APPLY_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			final int index = scope.size() - 1;
			final Context context = scope.context();
			return context.cast(scope.get(index), FunctionType.TYPE).data().execute(Scope.valueScope(scope, scope.toArray().section(0, index)));
		}

		@Override
		public String toString() {
			return "APPLY_FUNCTION";
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte eines gegebenen Ausführungskontexts {@code scope} entspricht, d.h.
	 * {@code Array.valueOf(scope#toArray().value())}.
	 * 
	 * @see Array#valueOf(Value...)
	 * @see Scope#toArray()
	 */
	public static final Function ARRAY_COPY_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			return new ArrayValue(Array.valueOf(scope.toArray().value()));
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte eines gegebenen Ausführungskontexts {@code scope} entspricht,
	 * d.h. {@code scope#toArray()}.
	 * 
	 * @see Scope#toArray()
	 */
	public static final Function ARRAY_VIEW_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			return new ArrayValue(scope.toArray());
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

}
