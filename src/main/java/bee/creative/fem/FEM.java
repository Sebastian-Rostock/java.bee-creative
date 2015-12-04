package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import bee.creative.fem.Script.ScriptCompiler;
import bee.creative.fem.Script.ScriptFormatter;
import bee.creative.fem.Script.ScriptFormatterInput;
import bee.creative.fem.Script.ScriptParser;
import bee.creative.fem.Script.ScriptTracer;
import bee.creative.fem.Script.ScriptTracerHelper;
import bee.creative.fem.Script.ScriptTracerInput;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * FEM - Function Evaluation Model
 * <p>
 * * Diese Klasse implementiert grundlegende Werte für {@code null}, {@link FEMArray Wertlisten}, {@link Object Objekte}, {@link FEMFunction Funktionen},
 * {@link String Zeichenketten}, {@link Number Zahlen} und {@link Boolean Wahrheitswerte}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class FEM {

	/**
	 * Diese Klasse implementiert einen abstrakten Wert als {@link ScriptFormatterInput}.<br>
	 * Die {@link #toString() Textdarstellung} des Werts wird über {@link ScriptFormatter#formatValue(FEMValue...)} und damit via
	 * {@link #toScript(ScriptFormatter)} ermittelt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseValue implements FEMValue, ScriptFormatterInput {

		/**
		 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextfrei konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht
		 * 
		 * @see FEMContext#DEFAULT()
		 * @see FEMContext#dataOf(FEMValue, FEMType)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
		 */
		public final <GData> GData data(final FEMType<GData> type) throws NullPointerException, IllegalArgumentException {
			return FEMContext.__default.dataOf(this, type);
		}

		/**
		 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht {@code context.dataOf(this, type)}.
		 * 
		 * @see FEMContext#dataOf(FEMValue, FEMType)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @param context Kontext.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
		 */

		public final <GData> GData data(final FEMType<GData> type, final FEMContext context) throws NullPointerException, ClassCastException,
			IllegalArgumentException {
			return context.dataOf(this, type);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FEMValue)) return false;
			final FEMValue data = (FEMValue)object;
			return Objects.equals(this.type(), data.type()) && Objects.equals(this.data(), data.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putData(this.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return FEM.scriptFormatter().formatValue(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Wert mit Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class DataValue<GData> extends BaseValue {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		protected GData __data;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@code null}.
		 */
		public DataValue() {
			this.__data = null;
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public DataValue(final GData data) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			this.__data = data;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GData data() {
			return this.__data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract FEMType<GData> type();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putData(this.__data);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DataValue} als {@link ScriptTracerInput}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class TracerValue<GData> extends DataValue<GData> implements ScriptTracerInput {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@code null}.
		 */
		public TracerValue() {
			super();
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public TracerValue(final GData data) throws NullPointerException {
			super(data);
		}

	}

	/**
	 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit gegebenen
	 * Rahmendaten einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion berechnete Ergebniswert
	 * wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf Rahmendaten und Funktion aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VirtualValue extends BaseValue {

		/**
		 * Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
		 * 
		 * @see FEMFunction#invoke(FEMFrame)
		 */
		FEMValue __value;

		/**
		 * Dieses Feld speichert die Rahmendaten zur Auswertung der Funktion oder {@code null}.
		 * 
		 * @see FEMFunction#invoke(FEMFrame)
		 */
		FEMFrame __frame;

		/**
		 * Dieses Feld speichert die Funktion oder {@code null}.
		 * 
		 * @see FEMFunction#invoke(FEMFrame)
		 */
		FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert Rahmendaten und Funktion.
		 * 
		 * @param scope Rahmendaten.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public VirtualValue(final FEMFrame scope, final FEMFunction function) throws NullPointerException {
			if (scope == null) throw new NullPointerException("scope = null");
			if (function == null) throw new NullPointerException("function = null");
			this.__frame = scope;
			this.__function = function;
		}

		{}

		/**
		 * Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #function() Funktion} mit den {@link #frame() Rahmendaten}
		 * zurück.
		 * 
		 * @see FEMFunction#invoke(FEMFrame)
		 * @return Ergebniswert.
		 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
		 */
		public synchronized FEMValue value() throws NullPointerException {
			FEMValue result = this.__value;
			if (result != null) return result;
			result = this.__function.invoke(this.__frame);
			if (result == null) throw new NullPointerException("this.function().invoke(this.frame()) = null");
			this.__value = result;
			this.__frame = null;
			this.__function = null;
			return result;
		}

		/**
		 * Diese Methode gibt die Rahmendaten oder {@code null} zurück.<br>
		 * Der erste Aufruf von {@link #value()} setzt die Rahmendaten auf {@code null}.
		 * 
		 * @return Rahmendaten oder {@code null}.
		 */
		public FEMFrame frame() {
			return this.__frame;
		}

		/**
		 * Diese Methode gibt die Funktion oder {@code null} zurück.<br>
		 * Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMType<?> type() {
			return this.value().type();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.value().data();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			if (this.__value != null) {
				target.putValue(this.__value);
			} else {
				target.putHandler(this.__function).put(this.__frame);
			}
		}

	}

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
			return FEM.scriptFormatter().formatFunction(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen benannten Platzhalter einer Funktione, dessen {@link #invoke(FEMFrame)}-Methoden an eine gegebene Funktion delegiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ProxyFunction extends BaseFunction {

		/**
		 * Dieses Feld speichert den Namen.
		 */
		final String __name;

		/**
		 * Dieses Feld speichert die Funktion.
		 */
		FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 */
		public ProxyFunction(final String name) throws NullPointerException {
			if (name == null) throw new NullPointerException("name = null");
			this.__name = name;
		}

		{}

		/**
		 * Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
		 * 
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public void set(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.__function = function;
		}

		/**
		 * Diese Methode gibt den Namen.
		 * 
		 * @return Name.
		 */
		public String name() {
			return this.__name;
		}

		/**
		 * Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird.<br>
		 * Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht aufgerufen wurde.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return this.__function.invoke(frame);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(FEM.formatName(this.__name));
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link ScriptTracer}.<br>
	 * Die genaue Beschreibung der Verarbeitung kann bei der Methode {@link #invoke(FEMFrame)} nachgelesen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ScriptTracer
	 */
	public static final class TraceFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den {@link ScriptTracer}.
		 */
		final ScriptTracer __tracer;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert Funktion und {@link ScriptTracer}.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code tracer} bzw. {@code function} {@code null} ist.
		 */
		public TraceFunction(final ScriptTracer tracer, final FEMFunction function) throws NullPointerException {
			if (tracer == null) throw new NullPointerException("tracer = null");
			if (function == null) throw new NullPointerException("function = null");
			this.__tracer = tracer;
			this.__function = function;
		}

		{}

		/**
		 * Diese Methode gibt den {@link ScriptTracer} zurück, dessen Zustand in {@link #invoke(FEMFrame)} modifiziert wird.
		 * 
		 * @return {@link ScriptTracer}.
		 */
		public ScriptTracer tracer() {
			return this.__tracer;
		}

		/**
		 * Diese Methode gibt die aufzurufende {@link FEMFunction} zurück.
		 * 
		 * @return aufzurufende {@link FEMFunction}.
		 */
		public FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Hierbei werden dem {@link #tracer()} zuerst der gegebene Rahmendaten sowie der {@link #function() aufzurufenden Funktion} bekannt gegeben und die Methode
		 * {@link ScriptTracerHelper#onExecute(ScriptTracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
		 * Anschließend wird die {@link ScriptTracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link ScriptTracer#getScope() aktuellen
		 * Rahmendaten} ausgewertet und das Ergebnis im {@link #tracer()} {@link ScriptTracer#useResult(FEMValue) gespeichert}.<br>
		 * Abschließend werden dann {@link ScriptTracerHelper#onReturn(ScriptTracer) tracer().helper().onReturn(tracer())} aufgerufen und der
		 * {@link ScriptTracer#getResult() aktuelle Ergebniswert} zurück gegeben.<br>
		 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link ScriptTracer#useException(RuntimeException) gespeichert}, wird
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link ScriptTracer#getException() altuelle
		 * Ausnahme} des {@link #tracer()} ausgelöst.<br>
		 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link ScriptTracer#clear() bereinigt}.<br>
		 * Der verwendete {@link ScriptTracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt.
		 */
		@Override
		public FEMValue invoke(final FEMFrame frame) {
			final ScriptTracer tracer = this.__tracer;
			try {
				final ScriptTracerHelper helper = tracer.getHelper();
				helper.onExecute(tracer.useScope(frame).useFunction(this.__function));
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
			if (this.__tracer.equals(tracer)) return this;
			return tracer.trace(this.__function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__function);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, welche immer den gleichen gegebenen Ergebniswert liefert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ValueFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den Ergebniswert.
		 */
		final FEMValue __value;

		/**
		 * Dieser Konstruktor initialisiert den Ergebniswert.
		 * 
		 * @param value Ergebniswert.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ValueFunction(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__value = value;
		}

		{}

		/**
		 * Diese Methode gibt den Ergebniswert zurück.
		 * 
		 * @return Ergebniswert.
		 */
		public FEMValue value() {
			return this.__value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return this.__value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.__value instanceof ScriptTracerInput) return ((ScriptTracerInput)this.__value).toTrace(tracer);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putValue(this.__value);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link VirtualValue} ist.
	 * 
	 * @see VirtualValue
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VirtualFunction extends BaseFunction {

		/**
		 * Diese Methode gibt die gegebene Funktion als {@link VirtualFunction} zurück.<br>
		 * Wenn diese bereits eine {@link VirtualFunction} ist, wird sie unverändert zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return {@link VirtualFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public static VirtualFunction from(final FEMFunction function) throws NullPointerException {
			if (function instanceof VirtualFunction) return (VirtualFunction)function;
			return new VirtualFunction(function);
		}

		{}

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert die auszuwertende Funktion, die in {@link #invoke(FEMFrame)} zur Erzeugung eines {@link VirtualValue} genutzt wird.
		 * 
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public VirtualFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.__function = function;
		}

		{}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code new LazyValue(frame, this.function())}.
		 * 
		 * @see #function()
		 * @see VirtualValue#VirtualValue(FEMFrame, FEMFunction)
		 */
		@Override
		public VirtualValue invoke(final FEMFrame frame) {
			return new VirtualValue(frame, this.__function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__function);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte der Rahmendaten entspricht.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParamFunction extends BaseFunction {

		/**
		 * Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0..9}.
		 */
		static final ParamFunction[] __cache = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		{}

		/**
		 * Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Rahmendatens als Ergebniswert liefert.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist.
		 */
		public static ParamFunction from(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			if (index < ParamFunction.__cache.length) return ParamFunction.__cache[index];
			return new ParamFunction(index);
		}

		{}

		/**
		 * Dieses Feld speichert den Index des Parameterwerts.
		 */
		final int __index;

		/**
		 * Dieser Konstruktor initialisiert den Index des Parameterwerts.
		 * 
		 * @param index Index des Parameterwerts.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist.
		 */
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			this.__index = index;
		}

		{}

		/**
		 * Diese Methode gibt den Index des Parameterwerts zurück.
		 * 
		 * @return Index des Parameterwerts.
		 * @see #invoke(FEMFrame)
		 */
		public int index() {
			return this.__index;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code frame.get(this.index())}.
		 * 
		 * @see #index()
		 */
		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return frame.get(this.__index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$").put(this.__index + 1);
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen berechnet.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class InvokeFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert {@code true}, wenn die Verkettung aktiviert ist.
		 */
		final boolean __direct;

		/**
		 * Dieses Feld speichert die aufzurufende Funktion.
		 */
		final FEMFunction __function;

		/**
		 * Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen.
		 */
		final FEMFunction[] __params;

		/**
		 * Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verketung und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
		 *        wenn die aufzurufende Funktion mit den Rahmendaten zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
		 *        Parameterfunktionen ausgewertet werden soll.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist.
		 */
		public InvokeFunction(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (params == null) throw new NullPointerException("params = null");
			this.__direct = direct;
			this.__function = function;
			this.__params = params;
		}

		{}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #function() aufzurufende Funktion} direkt mit den Ergebnissen der {@link #params()
		 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die aufzurufende Funktion mit den in {@link #invoke(FEMFrame)} gegebenen Rahmendaten zu einer
		 * Funktion ausgewertet, welche dann mit den Ergebnissen der Parameterfunktionen aufgerufen wird.
		 * 
		 * @return Verkettung.
		 * @see #invoke(FEMFrame)
		 */
		public boolean direct() {
			return this.__direct;
		}

		/**
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #invoke(FEMFrame)
		 */
		public FEMFunction[] params() {
			return this.__params.clone();
		}

		/**
		 * Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 * @see #invoke(FEMFrame)
		 */
		public FEMFunction function() {
			return this.__function;
		}

		/**
		 * Diese Methode gibt eine zu dieser Funktion gleichwertige {@link InvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
		 * {@link #params()} in eine {@link VirtualFunction} konvertiert wurde.
		 * 
		 * @see VirtualFunction#from(FEMFunction)
		 * @return neue {@link InvokeFunction} Funktion mit Parameterfunktionen, die {@link VirtualFunction} sind.
		 */
		public InvokeFunction toLazy() {
			final FEMFunction[] functions = this.__params.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = VirtualFunction.from(functions[i]);
			}
			return new InvokeFunction(VirtualFunction.from(this.__function), this.__direct, functions);
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht
		 * {@code (this.direct() ? this.function() : frame.context().dataOf(this.function().invoke(frame), FEM.FUNCTION_TYPE)).invoke(frame.newFrame(this.params()))}.
		 * 
		 * @see #direct()
		 * @see #params()
		 * @see #function()
		 * @see FEMFrame#newFrame(FEMFunction...)
		 */
		@Override
		public FEMValue invoke(FEMFrame frame) {
			final FEMFunction function;
			if (this.__direct) {
				function = this.__function;
			} else {
				final FEMValue value = this.__function.invoke(frame);
				if (value == null) throw new NullPointerException("this.function().invoke(frame) = null");
				function = frame.__context.dataOf(value, FEM.FUNCTION_TYPE);
			}
			frame = frame.newFrame(this.__params);
			final FEMValue result = function.invoke(frame);
			if (result == null) throw new NullPointerException("function.invoke(frame.newFrame(this.params()) = null");
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this.__params;
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = tracer.trace(params[i]);
			}
			return new InvokeFunction(tracer.trace(this.__function), this.__direct, params);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__function).putParams(Arrays.asList(this.__params));
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, welche die zusätzlichen Parameterwerte von Rahmendaten an eine gegebene Funktion bindet und diese gebundene
	 * Funktion anschließend als {@link FEM#functionValue(FEMFunction)} liefert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert die gebundenen Rahmendaten, deren zusätzliche Parameterwerte genutzt werden.
		 */
		final FEMFrame __frame;

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #invoke(FEMFrame)} die die zusätzlichen Parameterwerte der Rahmendaten gebunden
		 * werden.
		 * 
		 * @see #invoke(FEMFrame)
		 * @param function zu bindende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public ClosureFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.__frame = null;
			this.__function = function;
		}

		/**
		 * Dieser Konstruktor initialisiert die Rahmendaten sowie die gebundene Funktion und sollte nur von {@link ClosureFunction#invoke(FEMFrame)} genutzt werden.<br>
		 * Die {@link #invoke(FEMFrame)}-Methode delegiert die zugesicherten Parameterwerte der ihr übergebenen Rahmendaten zusammen mit den zusätzlichen
		 * Parameterwerten der gebundenen Rahmendaten an die gegebene Funktion und liefert deren Ergebniswert.
		 * 
		 * @see #invoke(FEMFrame)
		 * @param frame Rahmendaten mit den zusätzlichen Parameterwerten.
		 * @param function gebundene Funktion.
		 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist.
		 */
		public ClosureFunction(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
			if (frame == null) throw new NullPointerException("frame = null");
			if (function == null) throw new NullPointerException("function = null");
			this.__frame = frame;
			this.__function = function;
		}

		{}

		/**
		 * Diese Methode gibt die gebundene Rahmendaten oder {@code null} zurück.<br>
		 * Die Rahmendaten sind {@code null}, wenn diese {@link ClosureFunction} über dem Konstruktor {@link #ClosureFunction(FEMFunction)} erzeugt wurde.
		 * 
		 * @see #ClosureFunction(FEMFunction)
		 * @see #ClosureFunction(FEMFrame, FEMFunction)
		 * @see #invoke(FEMFrame)
		 * @return gebundene Rahmendaten oder {@code null}.
		 */
		public FEMFrame frame() {
			return this.__frame;
		}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn diese Funktion über {@link #ClosureFunction(FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code FEM.functionValue(new ClosureFunction(frame, this.function()))}. Damit werden die gegebenen Rahmendaten an die Funktion {@link #function()}
		 * gebunden und als {@link FEM#functionValue(FEMFunction)} zurück gegeben.
		 * <p>
		 * Wenn sie dagegen über {@link #ClosureFunction(FEMFrame, FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code this.function().invoke(this.frame().withParams(frame.params()))}. Damit werden die gebundene Funktion mit den zugesicherten Parameterwerten der
		 * gegebenen sowie den zusätzlichen Parameterwerten der gebundenen Rahmendaten ausgewertet und der so ermittelte Ergebniswert geliefert.
		 */
		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (this.__frame == null) return FEM.functionValue(new ClosureFunction(frame, this.__function));
			return this.__function.invoke(this.__frame.withParams(frame.params()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.__frame == null) return new ClosureFunction(tracer.trace(this.__function));
			return new ClosureFunction(this.__frame, tracer.trace(this.__function));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putHandler(this.__function);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class VoidValue extends DataValue<FEMVoid> {

		VoidValue() {
			super(FEMVoid.INSTANCE);
		}

		{}

		@Override
		public FEMType<FEMVoid> type() {
			return FEM.VOID_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ArrayValue extends DataValue<FEMArray> {

		ArrayValue(final FEMArray data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMArray> type() {
			return FEM.ARRAY_TYPE;
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putArray(this.__data);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class IntegerValue extends DataValue<FEMInteger> {

		IntegerValue(final FEMInteger data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMInteger> type() {
			return FEM.INTEGER_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class DecimalValue extends DataValue<FEMDecimal> {

		DecimalValue(final FEMDecimal data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMDecimal> type() {
			return FEM.DECIMAL_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class BooleanValue extends DataValue<FEMBoolean> {

		BooleanValue(final FEMBoolean data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMBoolean> type() {
			return FEM.BOOLEAN_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class DurationValue extends DataValue<FEMDuration> {

		DurationValue(final FEMDuration data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMDuration> type() {
			return FEM.DURATION_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class DatetimeValue extends DataValue<FEMDatetime> {

		DatetimeValue(final FEMDatetime data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMDatetime> type() {
			return FEM.DATETIME_TYPE;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class FunctionValue extends TracerValue<FEMFunction> {

		FunctionValue(final FEMFunction data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public FEMType<FEMFunction> type() {
			return FEM.FUNCTION_TYPE;
		}

		@Override
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			return new FEM.ValueFunction(FEM.functionValue(tracer.trace(this.__data)));
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__data);
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link FEMValue} zu {@code FEMVoid#INSTANCE}.
	 */
	static final VoidValue __void = new VoidValue();

	/**
	 * Dieses Feld speichert den {@link FEMValue} zu {@link FEMBoolean#TRUE}.
	 */
	static final BooleanValue __true = new BooleanValue(FEMBoolean.TRUE);

	/**
	 * Dieses Feld speichert den {@link FEMValue} zu {@link FEMBoolean#FALSE}.
	 */
	static final BooleanValue __false = new BooleanValue(FEMBoolean.FALSE);

	/**
	 * Dieses Feld speichert den Identifikator von {@link #VOID_TYPE}.
	 */
	public static final int VOID_ID = 0;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #__void}.
	 */
	public static final FEMType<FEMVoid> VOID_TYPE = FEMType.from(FEM.VOID_ID, "VOID");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #ARRAY_TYPE}.
	 */
	public static final int ARRAY_ID = 1;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#arrayValue(FEMArray)}.
	 */
	public static final FEMType<FEMArray> ARRAY_TYPE = FEMType.from(FEM.ARRAY_ID, "ARRAY");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #STRING_TYPE}.
	 */
	public static final int STRING_ID = 4;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#stringValue(String)}.
	 */
	public static final FEMType<String> STRING_TYPE = FEMType.from(FEM.STRING_ID, "STRING");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #INTEGER_TYPE}.
	 */
	public static final int INTEGER_ID = 5;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#numberValue(Number)}.
	 */
	public static final FEMType<FEMInteger> INTEGER_TYPE = FEMType.from(FEM.INTEGER_ID, "INTEGER");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DECIMAL_TYPE}.
	 */
	public static final int DECIMAL_ID = 5;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#numberValue(Number)}.
	 */
	public static final FEMType<FEMDecimal> DECIMAL_TYPE = FEMType.from(FEM.DECIMAL_ID, "DECIMAL");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #BOOLEAN_TYPE}.
	 */
	public static final int BOOLEAN_ID = 6;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#booleanValue(boolean)}.
	 */
	public static final FEMType<FEMBoolean> BOOLEAN_TYPE = FEMType.from(FEM.BOOLEAN_ID, "BOOLEAN");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DURATION_TYPE}.
	 */
	public static final int DURATION_ID = 6;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#DURATIONValue(boolean)}.
	 */
	public static final FEMType<FEMDuration> DURATION_TYPE = FEMType.from(FEM.DURATION_ID, "DURATION");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DATETIME_TYPE}.
	 */
	public static final int DATETIME_ID = 6;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#DATETIMEValue(boolean)}.
	 */
	public static final FEMType<FEMDatetime> DATETIME_TYPE = FEMType.from(FEM.DATETIME_ID, "DATETIME");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #FUNCTION_TYPE}.
	 */
	public static final int FUNCTION_ID = 3;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FEM#functionValue(FEMFunction)}.
	 */
	public static final FEMType<FEMFunction> FUNCTION_TYPE = FEMType.from(FEM.FUNCTION_ID, "FUNCTION");

	/**
	 * Dieses Feld speichert eine Funktion mit der Signatur {@code (method: Function, params: Array): Value}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameterwerte des Rahmendatens gegeben Funktion mit den im
	 * zweiten Parameterwert gegebenen Parameterwertliste.
	 */
	public static final FEMFunction CALL_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 2) throw new IllegalArgumentException("frame.size() != 2");
			final FEMContext context = frame.__context;
			final FEMFunction method = context.dataOf(frame.get(0), FEM.FUNCTION_TYPE);
			final FEMFrame params = frame.withParams(context.dataOf(frame.get(1), FEM.ARRAY_TYPE));
			return method.invoke(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("CALL_FUNCTION");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion mit der Signatur {@code (params1: Value, ..., paramN: Value, method: Function): Value}, deren Ergebniswert via
	 * {@code method(params1, ..., paramsN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameterwert des Rahmendatens gegeben Funktion mit den davor
	 * liegenden Parameterwerten.
	 */
	public static final FEMFunction INVOKE_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			final int index = frame.size() - 1;
			if (index < 0) throw new IllegalArgumentException("frame.size() < 1");
			final FEMContext context = frame.__context;
			final FEMFunction method = context.dataOf(frame.get(index), FEM.FUNCTION_TYPE);
			final FEMFrame params = frame.withParams(frame.params().section(0, index));
			return method.invoke(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("APPLY_FUNCTION");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte eines gegebenen Rahmendatens {@code frame} entspricht, d.h.
	 * {@code Array.valueOf(frame.toArray().value())}.
	 * 
	 * @see FEMArray#from(FEMValue...)
	 * @see FEMFrame#params()
	 */
	public static final FEMFunction PARAMS_COPY_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return FEM.arrayValue(FEMArray.from(frame.params().value()));
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	/**
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte eines gegebenen Rahmendatens {@code frame} entspricht, d.h.
	 * {@code frame#toArray()}.
	 * 
	 * @see FEMFrame#params()
	 */
	public static final FEMFunction PARAMS_VIEW_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return FEM.arrayValue(frame.params());
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	{}

	public static FEMValue voidValue() {
		return FEM.__void;
	}

	public static DataValue<FEMBoolean> trueValue() {
		return FEM.__true;
	}

	public static DataValue<FEMBoolean> falseValue() {
		return FEM.__false;
	}

	/**
	 * Diese Methode gibt den gegebenen Wert oder {@link #voidValue()} zurück.<br>
	 * Wenn die Eingabe {@code null} ist, wird {@link #voidValue()} geliefert.
	 * 
	 * @param value Wert oder {@code null}.
	 * @return Wert oder {@link #voidValue()}.
	 */
	public static FEMValue voidValue(final FEMValue value) {
		if (value == null) return FEM.__void;
		return value;
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMArray> arrayValue(final FEMArray data) throws NullPointerException {
		return new ArrayValue(data);
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMArray> arrayValue(final Iterable<?> data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		final Collection<Object> result = new ArrayList<>();
		Iterables.appendAll(result, data);
		return FEM.arrayValue(result);
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMArray> arrayValue(final Collection<?> data) throws NullPointerException {
		return FEM.arrayValue(FEM.arrayFrom(data.toArray()));
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link FEMValue} zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<String> stringValue(final String data) throws NullPointerException {
		return new DataValue<String>(data) {

			@Override
			public FEMType<String> type() {
				return FEM.STRING_TYPE;
			}

			@Override
			public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
				target.put("'").put(this.__data.replaceAll("'", "''")).put("'");
			}

		};
	}

	/**
	 * Diese Methode gibt die gegebene Dezimalzahl als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalzahl.
	 * @return Wert.
	 */
	public static DataValue<FEMInteger> integerValue(final long data) {
		return new IntegerValue(FEMInteger.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Dezimalzahl als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalzahl.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMInteger> integerValue(final Number data) throws NullPointerException {
		return new IntegerValue(FEMInteger.from(data));
	}

	/**
	 * Diese Methode gibt den gegebenen Dezimalbruch als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalbruch.
	 * @return Wert.
	 */
	public static DataValue<FEMDecimal> decimalValue(final double data) {
		return new DecimalValue(FEMDecimal.from(data));
	}

	/**
	 * Diese Methode gibt den gegebenen Dezimalbruch als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalbruch.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMDecimal> decimalValue(final Number data) throws NullPointerException {
		return new DecimalValue(FEMDecimal.from(data));
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link FEMValue} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return Wert.
	 */
	public static DataValue<FEMBoolean> booleanValue(final boolean data) {
		return (data ? FEM.__true : FEM.__false);
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link FEMValue} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMBoolean> booleanValue(final Boolean data) throws NullPointerException {
		return FEM.booleanValue(data.booleanValue());
	}

	public static DataValue<FEMBoolean> booleanValue(final FEMBoolean data) throws NullPointerException {
		return FEM.booleanValue(data.__value);
	}

	/**
	 * Diese Methode gibt die gegebene Funktion als {@link FEMValue} zurück.
	 * 
	 * @param data Funktion.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<FEMFunction> functionValue(final FEMFunction data) throws NullPointerException {
		return new FunctionValue(data);
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().valueOf(data)}.
	 * 
	 * @see FEMContext#valueOf(Object)
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann.
	 */
	public static FEMValue valueOf(final Object data) throws IllegalArgumentException {
		return FEMContext.__default.valueOf(data);
	}

	/**
	 * Diese Methode konvertiert das gegebene native Array in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere änderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden. Die Elemente des
	 * kopierten Arrays werden üver {@link valueOf} bei jedem Zugriff via {@link #get(int)} in Werte überführt.
	 * 
	 * @see java.lang.reflect.Array#get(Object, int)
	 * @see java.lang.reflect.Array#getLength(Object)
	 * @see java.lang.reflect.Array#newInstance(Class, int)
	 * @param data nativee Array.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code data} {@link Class#isArray() kein Array ist}.
	 */
	public static FEMArray arrayFrom(Object data) throws NullPointerException, IllegalArgumentException {
		if (data == null) throw new NullPointerException("data = null");
		final int length = java.lang.reflect.Array.getLength(data);
		if (length == 0) return FEMArray.EMPTY;
		final Object values = java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), length);
		System.arraycopy(data, 0, values, 0, length);
		data = null;
		return new FEMArray() {

			@Override
			public FEMValue get(final int index) throws IndexOutOfBoundsException {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index >= this.__length) throw new IndexOutOfBoundsException("index >= length");
				final Object value = java.lang.reflect.Array.get(values, index);
				return FEM.valueOf(value);
			}

			@Override
			public int length() {
				return this.__length;
			}

		};
	}

	{}

	static final char toHex(final int value) {
		final int value2 = value - 10;
		return (char)(value2 < 0 ? ('0' + value) : ('A' + value2));
	}

	public static String formatName(final String source) {
		if ((source.indexOf('<') >= 0) || (source.indexOf('>') >= 0)) return "<" + source.replaceAll("<", "<<").replaceAll(">", ">>") + ">";
		return source;
	}

	public static String formatVoid(final FEMVoid source) throws NullPointerException {
		if (source == null) throw new NullPointerException("source = null");
		return "null";
	}

	public static String formatArray(final FEMArray source) throws NullPointerException {
		return FEM.scriptFormatter().formatData((Object)source);
	}

	public static String formatScope(final FEMFrame source) throws NullPointerException {
		return new ScriptFormatter().formatData((Object)source);
	}

	public static String formatBinary(final FEMBinary source) throws NullPointerException {
		final StringBuilder target = new StringBuilder();
		target.append("0x");
		for (int i = 0, length = source.__length; i < length; i++) {
			final int value = source.__get(i);
			target.append(FEM.toHex((value >> 4) & 0xF)).append(FEM.toHex((value >> 0) & 0xF));
		}
		return target.toString();
	}

	public static String formatInteger(final FEMInteger source) throws NullPointerException {
		return Long.toString(source.value());
	}

	public static String formatDecimal(final FEMDecimal source) throws NullPointerException {
		return Double.toString(source.value());
	}

	public static String formatBoolean(final FEMBoolean source) throws NullPointerException {
		return Boolean.toString(source.value());
	}

	public static String formatDuration(final FEMDuration source) throws NullPointerException {
		final StringBuilder target = new StringBuilder();

		final int sing = source.signValue();
		if (sing < 0) {
			target.append('-');
		}
		if (sing != 0) {
			target.append('P');
			final int years = source.yearsValue(), months = source.monthsValue();
			if (years != 0) {
				target.append(years).append('Y');
			}
			if (months != 0) {
				target.append(months).append('M');
			}
			final int days = source.daysValue();
			if (days != 0) {
				target.append(days).append('D');
			}
			final int hours = source.hoursValue(), minutes = source.minutesValue(), seconds = source.secondsValue(), milliseconds = source.millisecondsValue();
			if ((hours | minutes | seconds | milliseconds) != 0) {
				target.append('T');
			}
			if (hours != 0) {
				target.append(hours).append('H');
			}
			if (minutes != 0) {
				target.append(minutes).append('M');
			}
			if (milliseconds != 0) {
				target.append(String.format("%d.%03dS", seconds, milliseconds));
			} else if (seconds != 0) {
				target.append(seconds).append('S');
			}
		} else {
			target.append("P0M");
		}
		return target.toString();
	}

	public static String formatDatetime(final FEMDatetime source) throws NullPointerException {
		final StringBuilder target = new StringBuilder();
		final boolean hasDate = source.hasDate();
		if (hasDate) {
			target.append(String.format("%04d-%02d-%02d", source.yearValue(), source.monthValue(), source.dateValue()));
		}
		if (source.hasTime()) {
			if (hasDate) {
				target.append('T');
			}
			target.append(String.format("%02d:%02d:%02d", source.hourValue(), source.minuteValue(), source.secondValue()));
			final int millisecond = source.millisecondValue();
			if (millisecond != 0) {
				target.append(String.format(".%03d", millisecond));
			}
		}
		if (source.hasZone()) {
			final int zone = source.zoneValue();
			if (zone == 0) {
				target.append('Z');
			} else {
				final int zoneAbs = Math.abs(zone);
				target.append(zone < 0 ? '-' : '+').append(String.format("%02d:%02d", zoneAbs / 60, zoneAbs % 60));
			}
		}
		return target.toString();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptParser} und gibt diesen zurück.
	 * 
	 * @see ScriptParser
	 * @return {@link ScriptParser}.
	 */
	public static ScriptParser scriptParser() {
		return new ScriptParser();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptCompiler} und gibt diesen zurück.
	 * 
	 * @see ScriptCompiler
	 * @return {@link ScriptCompiler}.
	 */
	public static ScriptCompiler scriptCompiler() {
		return new ScriptCompiler();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptFormatter} und gibt diesen zurück.
	 * 
	 * @see ScriptFormatter
	 * @return {@link ScriptFormatter}.
	 */
	public static ScriptFormatter scriptFormatter() {
		return new ScriptFormatter();
	}

}
