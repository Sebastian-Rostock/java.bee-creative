package bee.creative.fem;

import java.util.Arrays;
import bee.creative.fem.Scripts.ScriptFormatter;
import bee.creative.fem.Scripts.ScriptFormatterInput;
import bee.creative.fem.Scripts.ScriptTracer;
import bee.creative.fem.Scripts.ScriptTracerHelper;
import bee.creative.fem.Scripts.ScriptTracerInput;
import bee.creative.fem.Values.VirtualValue;

/**
 * Diese Klasse implementiert Hilfsklassen und Hilfsmethoden zur Erzeugung von Funktionen.
 * 
 * @see FEMValue
 * @see FEMScope
 * @see FEMFunction
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Functions {

	/**
	 * Diese Klasse implementiert eine abstakte Funktion als {@link ScriptFormatterInput}.<br>
	 * Die {@link #toString() Textdarstellung} der Funktion wird über {@link ScriptFormatter#formatFunction(FEMFunction...)} und damit via
	 * {@link #toScript(ScriptFormatter)} ermittelt.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseFunction implements FEMFunction, ScriptFormatterInput {

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
	 * Diese Klasse implementiert einen benannten Platzhalter einer Funktione, dessen {@link #invoke(FEMScope)}-Methoden an eine gegebene Funktion delegiert.
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
		FEMFunction function;

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
		 * Diese Methode setzt die in {@link #invoke(FEMScope)} aufzurufende Funktion.
		 * 
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public void set(final FEMFunction function) throws NullPointerException {
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
		 * Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMScope)} aufgerufen wird. Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht
		 * aufgerufen wurde.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public FEMFunction function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMValue invoke(final FEMScope scope) {
			return this.function.invoke(scope);
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
	 * Beschreibung der Verarbeitung kann bei der Methode {@link #invoke(FEMScope)} nachgelesen werden.
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
		final FEMFunction function;

		/**
		 * Dieser Konstruktor initialisiert Funktion und {@link ScriptTracer}.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public TraceFunction(final ScriptTracer tracer, final FEMFunction function) throws NullPointerException {
			if (tracer == null) throw new NullPointerException("tracer = null");
			if (function == null) throw new NullPointerException("function = null");
			this.tracer = tracer;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den {@link ScriptTracer} zurück, dessen Zustand in {@link #invoke(FEMScope)} modifiziert wird.
		 * 
		 * @return {@link ScriptTracer}.
		 */
		public ScriptTracer tracer() {
			return this.tracer;
		}

		/**
		 * Diese Methode gibt die aufzurufende {@link FEMFunction} zurück.
		 * 
		 * @return aufzurufende {@link FEMFunction}.
		 */
		public FEMFunction function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Hierbei werden dem {@link #tracer()} zuerst der gegebene Ausführungskontext sowie der {@link #function() aufzurufenden Funktion} bekannt gegeben und die
		 * Methode {@link ScriptTracerHelper#onExecute(ScriptTracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
		 * Anschließend wird die {@link ScriptTracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link ScriptTracer#getScope() aktuellen
		 * Ausführungskontext} ausgewertet und das Ergebnis im {@link #tracer()} {@link ScriptTracer#useResult(FEMValue) gespeichert}.<br>
		 * Abschließend werden dann {@link ScriptTracerHelper#onReturn(ScriptTracer) tracer().helper().onReturn(tracer())} aufgerufen und der
		 * {@link ScriptTracer#getResult() aktuelle Ergebniswert} zurück gegeben.<br>
		 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link ScriptTracer#useException(RuntimeException) gespeichert}, wird
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link ScriptTracer#getException() altuelle
		 * Ausnahme} des {@link #tracer()} ausgelöst.<br>
		 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link ScriptTracer#clear() bereinigt}.<br>
		 * Der verwendete {@link ScriptTracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt.
		 */
		@Override
		public FEMValue invoke(final FEMScope scope) {
			final ScriptTracer tracer = this.tracer;
			try {
				final ScriptTracerHelper helper = tracer.getHelper();
				helper.onExecute(tracer.useScope(scope).useFunction(this.function));
				try {
					helper.onReturn(tracer.useResult(tracer.getFunction().invoke(tracer.getScope())));
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
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
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
	 * @see #invoke(FEMScope)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den Ergebniswert.
		 */
		final FEMValue value;

		/**
		 * Dieser Konstruktor initialisiert den Ergebniswert.
		 * 
		 * @param value Ergebniswert.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ValueFunction(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.value = value;
		}

		{}

		/**
		 * Diese Methode gibt den Ergebniswert zurück.
		 * 
		 * @return Ergebniswert.
		 */
		public FEMValue value() {
			return this.value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMValue invoke(final FEMScope scope) {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
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
	 * @see #invoke(FEMScope)
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
		public static VirtualFunction valueOf(final FEMFunction function) throws NullPointerException {
			if (function instanceof VirtualFunction) return (VirtualFunction)function;
			return new VirtualFunction(function);
		}

		{}

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final FEMFunction function;

		/**
		 * Dieser Konstruktor initialisiert die auszuwertende Funktion, die in {@link #invoke(FEMScope)} zur Erzeugung eines {@link VirtualValue} genutzt wird.
		 * 
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public VirtualFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public FEMFunction function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code new LazyValue(scope, this.function())}.
		 * 
		 * @see #function()
		 * @see VirtualValue#VirtualValue(FEMScope, FEMFunction)
		 */
		@Override
		public VirtualValue invoke(final FEMScope scope) {
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
	 * @see #invoke(FEMScope)
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
		 * @see #invoke(FEMScope)
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
		public FEMValue invoke(final FEMScope scope) {
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
	 * @see #invoke(FEMScope)
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
		final FEMFunction function;

		/**
		 * Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen.
		 */
		final FEMFunction[] params;

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
		public InvokeFunction(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (params == null) throw new NullPointerException("params = null");
			this.direct = direct;
			this.function = function;
			this.params = params;
		}

		{}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #function() aufzurufende Funktion} direkt mit den Ergebnissen der {@link #params()
		 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die {@link #function() aufzurufende Funktion} mit dem in {@link #invoke(FEMScope)} gegebenen
		 * Ausführungskontext zu einer Funktion ausgewertet, welche dann mit den Ergebnissen der {@link #params() Parameterfunktionen} aufgerufen wird.
		 * 
		 * @return Verkettung.
		 * @see #invoke(FEMScope)
		 */
		public boolean direct() {
			return this.direct;
		}

		/**
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #invoke(FEMScope)
		 */
		public FEMFunction[] params() {
			return this.params.clone();
		}

		/**
		 * Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 * @see #invoke(FEMScope)
		 */
		public FEMFunction function() {
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
		 * @see FEMScope#invokeScope(FEMScope, FEMFunction...)
		 */
		@Override
		public FEMValue invoke(FEMScope scope) {
			final FEMFunction function;
			if (this.direct) {
				function = this.function;
			} else {
				final FEMValue value = this.function.invoke(scope);
				function = scope.context().dataOf(value, Values.FUNCTION_TYPE);
			}
			scope = FEMScope.invokeScope(scope, this.params);
			final FEMValue result = function.invoke(scope);
			return result;
		}

		/**
		 * Diese Methode gibt eine zu dieser Funktion gleichwertige {@link InvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
		 * {@link #params()} in eine {@link VirtualFunction} konvertiert wurde.
		 * 
		 * @see VirtualFunction#valueOf(FEMFunction)
		 * @return neue {@link InvokeFunction} Funktion mit Parameterfunktionen, die {@link VirtualFunction} sind.
		 */
		public InvokeFunction toLazy() {
			final FEMFunction[] functions = this.params.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = VirtualFunction.valueOf(functions[i]);
			}
			return new InvokeFunction(VirtualFunction.valueOf(this.function), this.direct, functions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this.params;
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
	 * gebundene Funktion anschließend als {@link Values#functionValue(FEMFunction)} liefert.
	 * 
	 * @see #invoke(FEMScope)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den gebundenen Ausführungskontext, dessen zusätzliche Parameterwerte genutzt werden.
		 */
		final FEMScope scope;

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final FEMFunction function;

		/**
		 * Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #invoke(FEMScope)} die die zusätzliche Parameterwerte des Ausführungskontext gebunden
		 * werden.
		 * 
		 * @see #invoke(FEMScope)
		 * @param function zu bindende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public ClosureFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.scope = null;
			this.function = function;
		}

		/**
		 * Dieser Konstruktor initialisiert den Ausführungskontext sowie die gebundene Funktion und sollte nur von {@link ClosureFunction#invoke(FEMScope)} genutzt
		 * werden. Die {@link #invoke(FEMScope)}-Methode delegiert die zugesicherten Parameterwerte des ihr übergebenen Ausführungskontext zusammen mit den
		 * zusätzlichen Parameterwerten des gebundenen Ausführungskontext an die gegebene Funktion und liefert deren Ergebniswert.
		 * 
		 * @see #invoke(FEMScope)
		 * @param scope Ausführungskontext mit den zusätzlichen Parameterwerten.
		 * @param function gebundene Funktion.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public ClosureFunction(final FEMScope scope, final FEMFunction function) throws NullPointerException {
			if (scope == null) throw new NullPointerException("scope = null");
			if (function == null) throw new NullPointerException("function = null");
			this.scope = scope;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den gebundene Ausführungskontext oder {@code null} zurück. Der Ausführungskontext ist {@code null}, wenn diese {@link ClosureFunction}
		 * über dem Konstruktor {@link #ClosureFunction(FEMFunction)} erzeugt wurde.
		 * 
		 * @see #ClosureFunction(FEMFunction)
		 * @see #ClosureFunction(FEMScope, FEMFunction)
		 * @see #invoke(FEMScope)
		 * @return gebundener Ausführungskontext oder {@code null}.
		 */
		public FEMScope scope() {
			return this.scope;
		}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public FEMFunction function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn diese Funktion über {@link #ClosureFunction(FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code new FunctionValue(new ClosureFunction(scope, this.function()))}. Damit wird der gegebene Ausführungskontext an die Funktion {@link #function()}
		 * gebunden und als {@link Values#functionValue(FEMFunction)} zurück gegeben.
		 * <p>
		 * Wenn sie dagegen über {@link #ClosureFunction(FEMScope, FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code this.function().execute(Scope.valueScope(this.scope(), scope.toArray(), false))}. Damit werden die gebundene Funktion mit den zugesicherten
		 * Parameterwerten des gegebenen sowie den zusätzlichen Parameterwerten des gebundenen Ausführungskontexts ausgewertet und der so ermittelte Ergebniswert
		 * geliefert.
		 */
		@Override
		public FEMValue invoke(final FEMScope scope) {
			final FEMScope scope2 = this.scope;
			if (scope2 == null) return Values.functionValue(new ClosureFunction(scope, this.function));
			return this.function.invoke(FEMScope.arrayScope(scope2, scope.array(), false));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
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
	public static final FEMFunction CALL_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMScope scope) {
			if (scope.size() != 2) throw new IllegalArgumentException("scope.size() != 2");
			final Context context = scope.context();
			final FEMFunction method = context.dataOf(scope.get(0), Values.FUNCTION_TYPE);
			final FEMScope params = FEMScope.arrayScope(scope, context.dataOf(scope.get(1), Values.ARRAY_TYPE));
			return method.invoke(params);
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
	public static final FEMFunction APPLY_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMScope scope) {
			final int index = scope.size() - 1;
			if (index < 0) throw new IllegalArgumentException("scope.size() < 1");
			final Context context = scope.context();
			final FEMFunction method = context.dataOf(scope.get(index), Values.FUNCTION_TYPE);
			final FEMScope params = FEMScope.arrayScope(scope, scope.array().section(0, index));
			return method.invoke(params);
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
	 * @see FEMArray#valueOf(FEMValue...)
	 * @see FEMScope#array()
	 */
	public static final FEMFunction ARRAY_COPY_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMScope scope) {
			return Values.arrayValue(FEMArray.valueOf(scope.array().value()));
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
	 * @see FEMScope#array()
	 */
	public static final FEMFunction ARRAY_VIEW_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMScope scope) {
			return Values.arrayValue(scope.array());
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

}
