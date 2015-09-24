package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Scripts.ScriptTracer;
import bee.creative.function.Scripts.ScriptTracerHelper;
import bee.creative.function.Scripts.ScriptTracerInput;
import bee.creative.function.Values.VirtualValue;

/**
 * Diese Klasse implementiert Hilfsklassen und Hilfsmethoden zur Erzeugung von Funktionen.
 * 
 * @see Value
 * @see Scope
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Functions {

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
			target.put(this.getClass().getName());
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
			final String name = this.name;
			if ((name.indexOf('<') >= 0) || (name.indexOf('>') >= 0)) {
				target.put("<").put(this.name.replaceAll("<", "<<").replaceAll(">", ">>")).put(">");
			} else {
				target.put(this.name);
			}
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link ScriptTracer}. Die genaue
	 * Beschreibung der Verarbeitung kann bei der Methode {@link #execute(Scope)} nachgelesen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ScriptTracer
	 */
	public static final class TraceFunction extends BaseFunction implements ScriptTracerInput {

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
		 * Diese Methode gibt den {@link ScriptTracer} zurück, dessen Zustand in {@link #execute(Scope)} modifiziert wird.
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
		 * <p>
		 * Hierbei werden dem {@link #tracer()} zuerst der gegebene Ausführungskontext sowie der {@link #function() aufzurufenden Funktion} bekannt gegeben und die
		 * Methode {@link ScriptTracerHelper#onExecute(ScriptTracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
		 * Anschließend wird die {@link ScriptTracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link ScriptTracer#getScope() aktuellen
		 * Ausführungskontext} ausgewertet und das Ergebnis im {@link #tracer()} {@link ScriptTracer#useResult(Value) gespeichert}.<br>
		 * Abschließend werden dann {@link ScriptTracerHelper#onReturn(ScriptTracer) tracer().helper().onReturn(tracer())} aufgerufen und der
		 * {@link ScriptTracer#getResult() aktuelle Ergebniswert} zurück gegeben.<br>
		 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link ScriptTracer#useException(RuntimeException) gespeichert}, wird
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link ScriptTracer#getException() altuelle
		 * Ausnahme} des {@link #tracer()} ausgelöst.<br>
		 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link ScriptTracer#clear() bereinigt}.<br>
		 * Der verwendete {@link ScriptTracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt.
		 */
		@Override
		public Value execute(final Scope scope) {
			final ScriptTracer tracer = this.tracer;
			try {
				final ScriptTracerHelper helper = tracer.getHelper();
				helper.onExecute(tracer.useScope(scope).useFunction(this.function));
				try {
					helper.onReturn(tracer.useResult(tracer.getFunction().execute(tracer.getScope())));
					return tracer.getResult();
				} catch (final RuntimeException exception) {
					helper.onThrow(tracer.useException(exception));
					throw tracer.getException();
				}
			} finally {
				tracer.clear();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.tracer.equals(tracer)) return this;
			return tracer.trace(this.function);
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
	 * Diese Klasse implementiert eine Funktion, welche immer den gleichen gegebenen Ergebniswert liefert.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction extends BaseFunction implements ScriptTracerInput {

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
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.value instanceof ScriptTracerInput) return ((ScriptTracerInput)this.value).toTrace(tracer);
			return this;
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
	 * Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link VirtualValue}.
	 * 
	 * @see VirtualValue
	 * @see #execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VirtualFunction extends BaseFunction {

		/**
		 * Diese Methode gibt die gegebene Funktion als {@link VirtualFunction} zurück. Wenn diese bereits eine {@link VirtualFunction} ist, wird sie unverändert
		 * zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return {@link VirtualFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public static VirtualFunction valueOf(final Function function) throws NullPointerException {
			if (function instanceof VirtualFunction) return (VirtualFunction)function;
			return new VirtualFunction(function);
		}

		{}

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert die auszuwertende Funktion, die in {@link #execute(Scope)} zur Erzeugung eines {@link VirtualValue} genutzt wird.
		 * 
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public VirtualFunction(final Function function) throws NullPointerException {
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
		 * Der Ergebniswert entspricht {@code new LazyValue(scope, this.function())}.
		 * 
		 * @see #function()
		 * @see VirtualValue#VirtualValue(Scope, Function)
		 */
		@Override
		public VirtualValue execute(final Scope scope) {
			return new VirtualValue(scope, this.function);
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
	 * Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte des Ausführungskontexts entspricht.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParamFunction extends BaseFunction {

		/**
		 * Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0} bis {@code 9}.
		 */
		static final ParamFunction[] INSTANCES = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		{}

		/**
		 * Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Ausführungskontexts als Ergebniswert liefert.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist.
		 */
		public static ParamFunction valueOf(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
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
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist.
		 */
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
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
	 * Diese Klasse definiert eine Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen berechnet.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class InvokeFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert {@code true}, wenn die Verkettung aktiviert ist.
		 */
		final boolean direct;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final Function function;

		/**
		 * Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen.
		 */
		final Function[] params;

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verketung und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
		 *        wenn die aufzurufende Funktion mit dem Ausführungskontext zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
		 *        Parameterfunktionen ausgewertet werden soll.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist.
		 */
		public InvokeFunction(final Function function, final boolean direct, final Function... params) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (params == null) throw new NullPointerException("params = null");
			this.direct = direct;
			this.function = function;
			this.params = params;
		}

		{}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #function() aufzurufende Funktion} direkt mit den Ergebnissen der {@link #params()
		 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die {@link #function() aufzurufende Funktion} mit dem in {@link #execute(Scope)} gegebenen
		 * Ausführungskontext zu einer Funktion ausgewertet, welche dann mit den Ergebnissen der {@link #params() Parameterfunktionen} aufgerufen wird.
		 * 
		 * @return Verkettung.
		 * @see #execute(Scope)
		 */
		public boolean direct() {
			return this.direct;
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

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht
		 * {@code (this.direct() ? this.function() : this.function().execute(scope).valueTo(FunctionValue.TYPE, scope.context())).execute(Scope#invokeScope(scope, this.params()))}.
		 * 
		 * @see #direct()
		 * @see #params()
		 * @see #function()
		 * @see Scope#invokeScope(Scope, Function...)
		 */
		@Override
		public Value execute(Scope scope) {
			final Function function;
			if (this.direct) {
				function = this.function;
			} else {
				final Value value = this.function.execute(scope);
				function = scope.context().dataOf(value, Values.FUNCTION_TYPE);
			}
			scope = Scope.invokeScope(scope, this.params);
			final Value result = function.execute(scope);
			return result;
		}

		/**
		 * Diese Methode gibt eine zu dieser Funktion gleichwertige {@link InvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
		 * {@link #params()} in eine {@link VirtualFunction} konvertiert wurde.
		 * 
		 * @see VirtualFunction#valueOf(Function)
		 * @return neue {@link InvokeFunction} Funktion mit Parameterfunktionen, die {@link VirtualFunction} sind.
		 */
		public InvokeFunction toLazy() {
			final Function[] functions = this.params.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = VirtualFunction.valueOf(functions[i]);
			}
			return new InvokeFunction(VirtualFunction.valueOf(this.function), this.direct, functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			final Function[] params = this.params;
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = tracer.trace(params[i]);
			}
			return new InvokeFunction(tracer.trace(this.function), this.direct, params);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.function).putParams(Arrays.asList(this.params));
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, welche die zusätzliche Parameterwerte eines Ausführungskontexts an eine gegebene Funktion bindet und diese
	 * gebundene Funktion anschließend als {@link Values#functionValue(Function)} liefert.
	 * 
	 * @see #execute(Scope)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den gebundenen Ausführungskontext, dessen zusätzliche Parameterwerte genutzt werden.
		 */
		final Scope scope;

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final Function function;

		/**
		 * Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #execute(Scope)} die die zusätzliche Parameterwerte des Ausführungskontext gebunden
		 * werden.
		 * 
		 * @see #execute(Scope)
		 * @param function zu bindende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public ClosureFunction(final Function function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.scope = null;
			this.function = function;
		}

		/**
		 * Dieser Konstruktor initialisiert den Ausführungskontext sowie die gebundene Funktion und sollte nur von {@link ClosureFunction#execute(Scope)} genutzt
		 * werden. Die {@link #execute(Scope)}-Methode delegiert die zugesicherten Parameterwerte des ihr übergebenen Ausführungskontext zusammen mit den
		 * zusätzlichen Parameterwerten des gebundenen Ausführungskontext an die gegebene Funktion und liefert deren Ergebniswert.
		 * 
		 * @see #execute(Scope)
		 * @param scope Ausführungskontext mit den zusätzlichen Parameterwerten.
		 * @param function gebundene Funktion.
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
		 * Wenn diese Funktion über {@link #ClosureFunction(Function)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code new FunctionValue(new ClosureFunction(scope, this.function()))}. Damit wird der gegebene Ausführungskontext an die Funktion {@link #function()}
		 * gebunden und als {@link Values#functionValue(Function)} zurück gegeben.
		 * <p>
		 * Wenn sie dagegen über {@link #ClosureFunction(Scope, Function)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code this.function().execute(Scope.valueScope(this.scope(), scope.toArray(), false))}. Damit werden die gebundene Funktion mit den zugesicherten
		 * Parameterwerten des gegebenen sowie den zusätzlichen Parameterwerten des gebundenen Ausführungskontexts ausgewertet und der so ermittelte Ergebniswert
		 * geliefert.
		 */
		@Override
		public Value execute(final Scope scope) {
			final Scope scope2 = this.scope;
			if (scope2 == null) return Values.functionValue(new ClosureFunction(scope, this.function));
			return this.function.execute(Scope.arrayScope(scope2, scope.array(), false));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.scope == null) return new ClosureFunction(tracer.trace(this.function));
			return new ClosureFunction(this.scope, tracer.trace(this.function));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putHandler(this.function);
		}

	}

	{}

	/**
	 * Dieses Feld speichert eine Funktion mit der Signatur {@code (method: Function, params: Array): Value}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameterwerte des Ausführungskontexts gegeben Funktion mit
	 * den im zweiten Parameterwert gegebenen Parameterwertliste.
	 */
	public static final Function CALL_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			if (scope.size() != 2) throw new IllegalArgumentException("scope.size() != 2");
			final Context context = scope.context();
			final Function method = context.dataOf(scope.get(0), Values.FUNCTION_TYPE);
			final Scope params = Scope.arrayScope(scope, context.dataOf(scope.get(1), Values.ARRAY_TYPE));
			return method.execute(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("CALL_FUNCTION");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion mit der Signatur {@code (params1: Value, ..., paramN: Value, method: Function): Value}, deren Ergebniswert via
	 * {@code method(params1, ..., paramsN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameterwert des Ausführungskontexts gegeben Funktion mit den
	 * davor liegenden Parameterwerten.
	 */
	public static final Function APPLY_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			final int index = scope.size() - 1;
			if (index < 0) throw new IllegalArgumentException("scope.size() < 1");
			final Context context = scope.context();
			final Function method = context.dataOf(scope.get(index), Values.FUNCTION_TYPE);
			final Scope params = Scope.arrayScope(scope, scope.array().section(0, index));
			return method.execute(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("APPLY_FUNCTION");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte eines gegebenen Ausführungskontexts {@code scope} entspricht, d.h.
	 * {@code Array.valueOf(scope.toArray().value())}.
	 * 
	 * @see Array#valueOf(Value...)
	 * @see Scope#array()
	 */
	public static final Function ARRAY_COPY_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			return Values.arrayValue(Array.valueOf(scope.array().value()));
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
	 * @see Scope#array()
	 */
	public static final Function ARRAY_VIEW_FUNCTION = new BaseFunction() {

		@Override
		public Value execute(final Scope scope) {
			return Values.arrayValue(scope.array());
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

}
