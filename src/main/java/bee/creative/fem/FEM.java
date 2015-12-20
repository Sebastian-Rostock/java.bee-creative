package bee.creative.fem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Converter;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

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
		public final int hashCode() {
			return Objects.hash(this.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object object) {
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
	 * Stapelrahmen einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion berechnete Ergebniswert
	 * wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf Stapelrahmen und Funktion aufgelöst.
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
		 * Dieses Feld speichert die Stapelrahmen zur Auswertung der Funktion oder {@code null}.
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
		 * Dieser Konstruktor initialisiert Stapelrahmen und Funktion.
		 * 
		 * @param frame Stapelrahmen.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist.
		 */
		public VirtualValue(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
			if (frame == null) throw new NullPointerException("frame = null");
			if (function == null) throw new NullPointerException("function = null");
			this.__frame = frame;
			this.__function = function;
		}

		{}

		/**
		 * Diese Methode gibt das Ergebnis der {@link FEMFunction#invoke(FEMFrame) Auswertung} der {@link #function() Funktion} mit den {@link #frame()
		 * Stapelrahmen} zurück.
		 * 
		 * @see FEMFunction#invoke(FEMFrame)
		 * @return Ergebniswert.
		 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
		 */
		public final synchronized FEMValue value() throws NullPointerException {
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
		 * Diese Methode gibt die Stapelrahmen oder {@code null} zurück.<br>
		 * Der erste Aufruf von {@link #value()} setzt die Stapelrahmen auf {@code null}.
		 * 
		 * @return Stapelrahmen oder {@code null}.
		 */
		public final FEMFrame frame() {
			return this.__frame;
		}

		/**
		 * Diese Methode gibt die Funktion oder {@code null} zurück.<br>
		 * Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public final FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMType<?> type() {
			return this.value().type();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Object data() {
			return this.value().data();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final synchronized void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			if (this.__value != null) {
				target.putValue(this.__value);
			} else {
				target.putHandler(this.__function).put(this.__frame);
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMVoid}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class VoidValue extends DataValue<FEMVoid> {

		VoidValue() {
			super(FEMVoid.INSTANCE);
		}

		{}

		@Override
		public final FEMType<FEMVoid> type() {
			return FEM.VOID_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMArray}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class ArrayValue extends DataValue<FEMArray> {

		ArrayValue(final FEMArray data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMArray> type() {
			return FEM.ARRAY_TYPE;
		}

		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putArray(this.__data);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMString}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class StringValue extends DataValue<FEMString> {

		StringValue(final FEMString data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMString> type() {
			return FEM.STRING_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMBinary}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class BinaryValue extends DataValue<FEMBinary> {

		BinaryValue(final FEMBinary data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMBinary> type() {
			return FEM.BINARY_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMInteger}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class IntegerValue extends DataValue<FEMInteger> {

		IntegerValue(final FEMInteger data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMInteger> type() {
			return FEM.INTEGER_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMDecimal}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class DecimalValue extends DataValue<FEMDecimal> {

		DecimalValue(final FEMDecimal data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMDecimal> type() {
			return FEM.DECIMAL_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMBoolean}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class BooleanValue extends DataValue<FEMBoolean> {

		BooleanValue(final FEMBoolean data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMBoolean> type() {
			return FEM.BOOLEAN_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMDuration}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class DurationValue extends DataValue<FEMDuration> {

		DurationValue(final FEMDuration data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMDuration> type() {
			return FEM.DURATION_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMDatetime}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class DatetimeValue extends DataValue<FEMDatetime> {

		DatetimeValue(final FEMDatetime data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMDatetime> type() {
			return FEM.DATETIME_TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link FEMValue} zu {@link FEMFunction}.
	 */
	@SuppressWarnings ("javadoc")
	public static final class FunctionValue extends TracerValue<FEMFunction> {

		FunctionValue(final FEMFunction data) throws NullPointerException {
			super(data);
		}

		{}

		@Override
		public final FEMType<FEMFunction> type() {
			return FEM.FUNCTION_TYPE;
		}

		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			return new ValueFunction(FEM.functionValue(tracer.trace(this.__data)));
		}

		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__data);
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
		public final String toString() {
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
		public final void set(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this.__function = function;
		}

		/**
		 * Diese Methode gibt den Namen.
		 * 
		 * @return Name.
		 */
		public final String name() {
			return this.__name;
		}

		/**
		 * Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird.<br>
		 * Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht aufgerufen wurde.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public final FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this.__function.invoke(frame);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(FEM.formatValue(this.__name));
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
		public final ScriptTracer tracer() {
			return this.__tracer;
		}

		/**
		 * Diese Methode gibt die aufzurufende {@link FEMFunction} zurück.
		 * 
		 * @return aufzurufende {@link FEMFunction}.
		 */
		public final FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Hierbei werden dem {@link #tracer()} zuerst der gegebene Stapelrahmen sowie der {@link #function() aufzurufenden Funktion} bekannt gegeben und die
		 * Methode {@link ScriptTracerHelper#onExecute(ScriptTracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
		 * Anschließend wird die {@link ScriptTracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link ScriptTracer#getFrame() aktuellen
		 * Stapelrahmen} ausgewertet und das Ergebnis im {@link #tracer()} {@link ScriptTracer#useResult(FEMValue) gespeichert}.<br>
		 * Abschließend werden dann {@link ScriptTracerHelper#onReturn(ScriptTracer) tracer().helper().onReturn(tracer())} aufgerufen und der
		 * {@link ScriptTracer#getResult() aktuelle Ergebniswert} zurück gegeben.<br>
		 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link ScriptTracer#useException(RuntimeException) gespeichert}, wird
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link ScriptTracer#getException() altuelle
		 * Ausnahme} des {@link #tracer()} ausgelöst.<br>
		 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link ScriptTracer#clear() bereinigt}.<br>
		 * Der verwendete {@link ScriptTracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt.
		 */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			final ScriptTracer tracer = this.__tracer;
			try {
				final ScriptTracerHelper helper = tracer.getHelper();
				helper.onExecute(tracer.useFrame(frame).useFunction(this.__function));
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.__tracer.equals(tracer)) return this;
			return tracer.trace(this.__function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
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
		public final FEMValue value() {
			return this.__value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this.__value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.__value instanceof ScriptTracerInput) return ((ScriptTracerInput)this.__value).toTrace(tracer);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
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
		public static final VirtualFunction from(final FEMFunction function) throws NullPointerException {
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
		public final FEMFunction function() {
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
		public final VirtualValue invoke(final FEMFrame frame) {
			return new VirtualValue(frame, this.__function);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__function);
		}

	}

	/**
	 * Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte der Stapelrahmen entspricht.
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
		 * Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Stapelrahmens als Ergebniswert liefert.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist.
		 */
		public static final ParamFunction from(final int index) throws IndexOutOfBoundsException {
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
		public final int index() {
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
		public final FEMValue invoke(final FEMFrame frame) {
			return frame.get(this.__index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$").put(new Integer(this.__index + 1));
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
		 *        wenn die aufzurufende Funktion mit den Stapelrahmen zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
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
		 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die aufzurufende Funktion mit den in {@link #invoke(FEMFrame)} gegebenen Stapelrahmen zu einer
		 * Funktion ausgewertet, welche dann mit den Ergebnissen der Parameterfunktionen aufgerufen wird.
		 * 
		 * @return Verkettung.
		 * @see #invoke(FEMFrame)
		 */
		public final boolean direct() {
			return this.__direct;
		}

		/**
		 * Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #invoke(FEMFrame)
		 */
		public final FEMFunction[] params() {
			return this.__params.clone();
		}

		/**
		 * Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 * @see #invoke(FEMFrame)
		 */
		public final FEMFunction function() {
			return this.__function;
		}

		/**
		 * Diese Methode gibt eine zu dieser Funktion gleichwertige {@link InvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
		 * {@link #params()} in eine {@link VirtualFunction} konvertiert wurde.
		 * 
		 * @see VirtualFunction#from(FEMFunction)
		 * @return neue {@link InvokeFunction} Funktion mit Parameterfunktionen, die {@link VirtualFunction} sind.
		 */
		public final InvokeFunction toLazy() {
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
		 * {@code (this.direct() ? this.function() : frame.context().dataOf(this.function().invoke(frame), FUNCTION_TYPE)).invoke(frame.newFrame(this.params()))}.
		 * 
		 * @see #direct()
		 * @see #params()
		 * @see #function()
		 * @see FEMFrame#newFrame(FEMFunction...)
		 */
		@Override
		public final FEMValue invoke(FEMFrame frame) {
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
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
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
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.__function).putParams(Arrays.asList(this.__params));
		}

	}

	/**
	 * Diese Klasse implementiert eine Funktion, welche die zusätzlichen Parameterwerte von Stapelrahmen an eine gegebene Funktion bindet und diese gebundene
	 * Funktion anschließend als {@link FEM#functionValue(FEMFunction)} liefert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ClosureFunction extends BaseFunction implements ScriptTracerInput {

		/**
		 * Diese Methode gibt die gegebene Funktion als {@link ClosureFunction} zurück.<br>
		 * Wenn diese bereits eine {@link ClosureFunction} ist, wird sie unverändert zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return {@link ClosureFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 */
		public static final ClosureFunction from(final FEMFunction function) throws NullPointerException {
			if (function instanceof ClosureFunction) return (ClosureFunction)function;
			return new ClosureFunction(function);
		}

		{}

		/**
		 * Dieses Feld speichert die gebundenen Stapelrahmen, deren zusätzliche Parameterwerte genutzt werden.
		 */
		final FEMFrame __frame;

		/**
		 * Dieses Feld speichert die auszuwertende Funktion.
		 */
		final FEMFunction __function;

		/**
		 * Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #invoke(FEMFrame)} die die zusätzlichen Parameterwerte der Stapelrahmen gebunden
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
		 * Dieser Konstruktor initialisiert die Stapelrahmen sowie die gebundene Funktion und sollte nur von {@link ClosureFunction#invoke(FEMFrame)} genutzt
		 * werden.<br>
		 * Die {@link #invoke(FEMFrame)}-Methode delegiert die zugesicherten Parameterwerte der ihr übergebenen Stapelrahmen zusammen mit den zusätzlichen
		 * Parameterwerten der gebundenen Stapelrahmen an die gegebene Funktion und liefert deren Ergebniswert.
		 * 
		 * @see #invoke(FEMFrame)
		 * @param frame Stapelrahmen mit den zusätzlichen Parameterwerten.
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
		 * Diese Methode gibt die gebundene Stapelrahmen oder {@code null} zurück.<br>
		 * Die Stapelrahmen sind {@code null}, wenn diese {@link ClosureFunction} über dem Konstruktor {@link #ClosureFunction(FEMFunction)} erzeugt wurde.
		 * 
		 * @see #ClosureFunction(FEMFunction)
		 * @see #ClosureFunction(FEMFrame, FEMFunction)
		 * @see #invoke(FEMFrame)
		 * @return gebundene Stapelrahmen oder {@code null}.
		 */
		public final FEMFrame frame() {
			return this.__frame;
		}

		/**
		 * Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion.
		 */
		public final FEMFunction function() {
			return this.__function;
		}

		{}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Wenn diese Funktion über {@link #ClosureFunction(FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code functionValue(new ClosureFunction(frame, this.function()))}. Damit werden die gegebenen Stapelrahmen an die Funktion {@link #function()} gebunden
		 * und als {@link FEM#functionValue(FEMFunction)} zurück gegeben.
		 * <p>
		 * Wenn sie dagegen über {@link #ClosureFunction(FEMFrame, FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code this.function().invoke(this.frame().withParams(frame.params()))}. Damit werden die gebundene Funktion mit den zugesicherten Parameterwerten der
		 * gegebenen sowie den zusätzlichen Parameterwerten der gebundenen Stapelrahmen ausgewertet und der so ermittelte Ergebniswert geliefert.
		 */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			if (this.__frame == null) return FEM.functionValue(new ClosureFunction(frame, this.__function));
			return this.__function.invoke(this.__frame.withParams(frame.params()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this.__frame == null) return ClosureFunction.from(tracer.trace(this.__function));
			return new ClosureFunction(this.__frame, tracer.trace(this.__function));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putHandler(this.__function);
		}

	}

	/**
	 * Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
	 * {@link ScriptCompiler} in Werte und Funktionen überführt werden.
	 * <p>
	 * Die Erzeugung von {@link Range Bereichen} erfolgt gemäß dieser Regeln:
	 * <ul>
	 * <li>Die Zeichen {@code '/'}, {@code '\''} und {@code '\"'} erzeugen je einen Bereich, der das entsprechende Zeichen als Bereichstyp verwendet, mit dem
	 * Zeichen beginnt und endet sowie das Zeichen zwischen dem ersten und letzten nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der
	 * Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.</li>
	 * <li>Das Zeichen <code>'&lt;'</code> erzeugen einen Bereich, der mit dem Zeichen <code>'&gt;'</code> endet und beide Zeichen zwischen dem ersten und letzten
	 * jeweils nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der Bereich an der Stelle des Fehlers und hat den Bereichstyp
	 * {@code '?'}. Andernfalls hat er den Bereichstyp {@code '!'}.</li>
	 * <li>Jedes der Zeichen {@code '$'}, {@code ';'}, {@code ':'}, {@code '('}, {@code ')'}, <code>'{'</code> und <code>'}'</code> erzeugt eine eigene Bereich,
	 * der das entsprechende Zeichen als Bereichstyp verwendet.</li>
	 * <li>Sequenzen aus Zeichen kleiner gleich dem Leerzeichen werden zu Bereichen mit dem Bereichstyp {@code '_'}.</li>
	 * <li>Alle restlichen Zeichenfolgen werden zu Bereichen mit dem Bereichstyp {@code '.'}.</li>
	 * </ul>
	 * <p>
	 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
	 * 
	 * @see #parseRanges()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ScriptParser extends Parser {

		/**
		 * Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}.
		 */
		int __value;

		/**
		 * Dieses Feld speichert die bisher ermittelten Bereiche.
		 */
		final List<Range> __ranges = new ArrayList<>();

		@SuppressWarnings ("javadoc")
		boolean __active;

		{}

		/**
		 * Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #__stop()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		synchronized final void __start() throws IllegalStateException {
			this.__check();
			this.__active = true;
			this.__value = -1;
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		final void __stop() {
			this.__active = false;
		}

		@SuppressWarnings ("javadoc")
		final void __check() throws IllegalStateException {
			if (this.__active) throw new IllegalStateException();
		}

		/**
		 * Diese Methode fügt eine neue Bereich mit den gegebenen Parametern hinzu, der bei {@link #index()} endet.
		 * 
		 * @param type Typ des Bereichs.
		 * @param start Start des Bereichs.
		 */
		final void __put(final int type, final int start) {
			this.__ranges.add(new Range((char)type, start, this.index() - start));
		}

		/**
		 * Diese Methode beginnt das parsen eines Wertbereichs mit dem Bereichstyp {@code '.'}, welches mit {@link #__closeValue()} beendet werden muss.
		 */
		final void __openValue() {
			if (this.__value >= 0) return;
			this.__value = this.index();
		}

		/**
		 * Diese Methode beendet das einlesen des Wertbereichs mit dem Bereichstyp {@code '.'}.
		 */
		final void __closeValue() {
			final int start = this.__value;
			if (start < 0) return;
			this.__value = -1;
			if (this.index() <= start) return;
			this.__put('.', start);
		}

		/**
		 * Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher
		 * das Zeichen als Typ verwendet.
		 * 
		 * @param type Zeichen als Bereichstyp.
		 */
		final void __parseMask(final int type) {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						this.__put(type, start);
						return;
					}
				}
			}
			this.__put('?', start);
		}

		/**
		 * Diese Methode parst einen Bereich, der mit dem Zeichen <code>'&lt;'</code> beginnt, mit dem Zeichen <code>'&gt;'</code> ende und in dem diese Zeichen nur
		 * paarweise vorkommen dürfen. ein solcher Bereich geparst werden konnte, ist dessen Bereichstyp {@code '!'}. Wenn eine dieser Regeln verletzt wird, ist der
		 * Bereichstyp {@code '?'}.
		 */
		final void __parseName() {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '>') {
					if (this.skip() != '>') {
						this.__put('!', start);
						return;
					}
				} else if (symbol == '<') {
					if (this.skip() != '<') {
						break;
					}
				}
			}
			this.__put('?', start);
		}

		/**
		 * Diese Methode überspringt alle Zeichen, die kleiner oder gleich dem eerzeichen sind.
		 */
		final void __parseSpace() {
			final int start = this.index();
			for (int symbol = this.skip(); (symbol >= 0) && (symbol <= ' '); symbol = this.skip()) {}
			this.__put('_', start);
		}

		/**
		 * Diese Methode erzeugt zum gegebenen Zeichen einen Bereich der Länge 1 und navigiert zum nächsten Zeichen.
		 * 
		 * @see #skip()
		 * @see #__put(int, int)
		 * @param type Zeichen als Bereichstyp.
		 */
		final void __parseSymbol(final int type) {
			final int start = this.index();
			this.skip();
			this.__put(type, start);
		}

		/**
		 * Diese Methode parst die {@link #source() Eingabe}.
		 */
		final void __parseSource() {
			for (int symbol; true;) {
				switch (symbol = this.symbol()) {
					case -1: {
						this.__closeValue();
						return;
					}
					case '\'':
					case '\"':
					case '/': {
						this.__closeValue();
						this.__parseMask(symbol);
						break;
					}
					case '<': {
						this.__closeValue();
						this.__parseName();
						break;
					}
					case '$':
					case ':':
					case ';':
					case '(':
					case ')':
					case '[':
					case ']':
					case '{':
					case '}': {
						this.__closeValue();
						this.__parseSymbol(symbol);
						break;
					}
					default: {
						if (symbol <= ' ') {
							this.__closeValue();
							this.__parseSpace();
						} else {
							this.__openValue();
							this.skip();
						}
					}
				}
			}
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #source() Eingabe} keines der in {@link #__parseSource()} erkannten Zeichen enthält,
		 * d.h. wenn das Parsen der Eingabe via {@link #parseRanges()} genau einen Bereich mit dem Typ {@code '.'} ergibt, welcher über {@link #__openValue()} und
		 * {@link #__closeValue()} entstand.
		 * 
		 * @return {@code true}, wenn die Eingabe nur einen Wert enthält.
		 */
		final boolean __checkSource() {
			if (this.isParsed()) return false;
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				switch (symbol) {
					case '\'':
					case '\"':
					case '/':
					case '<':
					case '>':
					case '$':
					case ':':
					case ';':
					case '(':
					case ')':
					case '[':
					case ']':
					case '{':
					case '}':
						return false;
					default: {
						if (symbol <= ' ') return false;
					}
				}
			}
			return true;
		}

		/**
		 * Diese Methode gibt die in Anführungszeichen eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #__parseMask(int)
		 * @param type Anführungszeichen.
		 * @return Eingabe mit Maskierung.
		 */
		final String __encodeMask(final int type) {
			this.take(type);
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					this.take(symbol);
				}
				this.take(symbol);
			}
			this.take(type);
			return this.target();
		}

		/**
		 * Diese Methode gibt die in spitze Klammern eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #__parseName()
		 * @return Eingabe mit Maskierung.
		 */
		final String __encodeValue() {
			this.take('<');
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				if ((symbol == '<') || (symbol == '>')) {
					this.take(symbol);
				}
				this.take(symbol);
			}
			this.take('>');
			return this.target();
		}

		/**
		 * Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.
		 * 
		 * @param type Anführungszeichen.
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		final String __decodeMask(final int type) throws IllegalArgumentException {
			if (this.symbol() != type) throw new IllegalArgumentException();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						if (this.isParsed()) return this.target();
						throw new IllegalArgumentException();
					}
				}
				this.take(symbol);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.
		 * 
		 * @see #__parseName()
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		final String __decodeValue() throws IllegalArgumentException {
			if (this.symbol() != '<') throw new IllegalArgumentException();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '<') {
					if (this.skip() != '<') throw new IllegalArgumentException();
				} else if (symbol == '>') {
					if (this.skip() != '>') {
						if (this.isParsed()) return this.target();
						throw new IllegalArgumentException();
					}
				}
				this.take(symbol);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
		 * 
		 * @param value Eingabe.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		public synchronized final ScriptParser useSource(final String value) throws NullPointerException, IllegalStateException {
			this.__check();
			super.source(value);
			return this;
		}

		/**
		 * Diese Methode parst die {@link #source() Eingabe} in einen aufbereiteten Quelltext und gibt diesen zurück.
		 * 
		 * @see FEMScript
		 * @see #parseRanges()
		 * @return aufbereiteter Quelltext.
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		public final FEMScript parseScript() throws IllegalStateException {
			return new FEMScript(this.source(), this.parseRanges());
		}

		/**
		 * Diese Methode parst die {@link #source() Eingabe} und gibt die Liste der ermittelten Bereiche zurück.
		 * 
		 * @see Range
		 * @return Bereiche.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		public final Range[] parseRanges() throws IllegalStateException {
			this.__start();
			try {
				this.__ranges.clear();
				this.__parseSource();
				final Range[] result = this.__ranges.toArray(new Range[this.__ranges.size()]);
				return result;
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit einem der Anführungszeichen {@code '\''} oder {@code '\"'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatString()
		 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		public final String parseString() throws IllegalStateException, IllegalArgumentException {
			this.__start();
			try {
				if (this.symbol() == '\'') return this.__decodeMask('\'');
				if (this.symbol() == '\"') return this.__decodeMask('\"');
				throw new IllegalArgumentException();
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Schrägstrichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit {@code '/'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatComment()
		 * @return Eingabe ohne Maskierung mit {@code '/'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		public final String parseComment() throws IllegalStateException, IllegalArgumentException {
			this.__start();
			try {
				if (this.symbol() == '/') return this.__decodeMask('/');
				throw new IllegalArgumentException();
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit <code>'&lt;'</code> bzw. <code>'&gt;'</code> und enthält diese Zeichen nur gedoppelt. Wenn die Eingabe nicht
		 * derart beginnt, wird sie unverändert zurück gegeben.
		 * 
		 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		public final String parseValue() throws IllegalStateException, IllegalArgumentException {
			this.__start();
			try {
				if (this.symbol() == '<') return this.__decodeValue();
				return this.source();
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die in spitze Klammern eingeschlossenen und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.<br>
		 * Wenn die Eingabe keine von diesem Parser besonders behandelten Zeichen enthält, wird sie unverändert zurück gegeben.
		 * 
		 * @see #parseValue()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		public final String formatValue() throws IllegalStateException {
			this.__start();
			try {
				if (this.__checkSource()) return this.source();
				this.reset();
				return this.__encodeValue();
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die in {@code '\''} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseString()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		public final String formatString() throws IllegalStateException {
			this.__start();
			try {
				return this.__encodeMask('\'');
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode gibt die in {@code '/'} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseComment()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 */
		public final String formatComment() throws IllegalStateException {
			this.__start();
			try {
				return this.__encodeMask('/');
			} finally {
				this.__stop();
			}
		}

	}

	/**
	 * Diese Klasse implementiert einen Kompiler für {@link FEMValue Werte} und {@link FEMFunction Funktionen}.
	 * <p>
	 * Die Bereichestypen eines Quelltexts haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) sind bedeutungslos, dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link FEMArray}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link FEMArray} als Funktion bzw. Parameterwert
	 * kompiliert wird.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über
	 * {@link ScriptCompilerHelper#compileName(ScriptCompiler, String)} aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog
	 * zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu genau einer Funktion kompiliert werden.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.valueOf(0)}). Andernfalls steht der Bereich für {@link FEM#PARAMS_VIEW_FUNCTION}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileParam(ScriptCompiler, String)} in Werte überführt. Funktionen werden hierbei
	 * als {@link FunctionValue} angegeben.</li>
	 * </ul>
	 * <p>
	 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
	 * 
	 * @see #compileValue()
	 * @see #compileFunction()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ScriptCompiler extends Parser {

		/**
		 * Dieses Feld speichert die Kompilationsmethoden.
		 */
		ScriptCompilerHelper __helper = ScriptCompilerHelper.DEFAULT;

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		FEMScript __script = FEMScript.EMPTY;

		/**
		 * Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter.
		 */
		final Map<String, ProxyFunction> __proxies = Collections.synchronizedMap(new LinkedHashMap<String, ProxyFunction>());

		/**
		 * Dieses Feld speichert die Parameternamen.
		 */
		final List<String> __params = Collections.synchronizedList(new LinkedList<String>());

		/**
		 * Dieses Feld speichert die Zulässigkeit von Wertlisten.
		 */
		boolean __arrayEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit von Funktionszeigern.
		 */
		boolean __handlerEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Bindung des Stapelrahmens.
		 */
		boolean __closureEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen.
		 */
		boolean __chainingEnabled = true;

		ScriptFormatter __formatter;

		@SuppressWarnings ("javadoc")
		boolean __active;

		{}

		/**
		 * Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #__stop()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		final synchronized void __start() throws IllegalStateException {
			this.__check();
			this.__active = true;
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		final void __stop() {
			this.__active = false;
		}

		@SuppressWarnings ("javadoc")
		final void __check() throws IllegalStateException {
			if (this.__active) throw new IllegalStateException();
		}

		/**
		 * Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den Typ des ersten bedeutsamen Bereichs oder {@code -1}
		 * zurück. Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereichstyp.
		 */
		final int __compileType() {
			int symbol = this.symbol();
			while ((symbol == '_') || (symbol == '/')) {
				symbol = this.skip();
			}
			return symbol;
		}

		/**
		 * Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
		 * 
		 * @param string Zeichenkette.
		 * @return Zahl.
		 */
		final int __compileIndex(final String string) {
			if ((string == null) || string.isEmpty()) return -1;
			final char symbol = string.charAt(0);
			if ((symbol < '0') || (symbol > '9')) return -1;
			try {
				return Integer.parseInt(string);
			} catch (final NumberFormatException e) {
				return -1;
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
		 * 
		 * @see ArrayValue
		 * @return Wertliste als {@link FEMValue}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final FEMValue __compileArrayAsValue() throws ScriptException {
			if (!this.__arrayEnabled) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			final List<FEMValue> result = new ArrayList<>();
			this.skip();
			if (this.__compileType() == ']') {
				this.skip();
				return FEM.__emptyArray;
			}
			while (true) {
				final FEMValue value = this.__compileParamAsValue();
				result.add(value);
				switch (this.__compileType()) {
					case ';': {
						this.skip();
						this.__compileType();
						break;
					}
					case ']': {
						this.skip();
						return FEM.arrayValue(FEMArray.from(result));
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMFunction} und gibt diesen zurück.
		 * 
		 * @see ArrayValue
		 * @see ValueFunction
		 * @see InvokeFunction
		 * @see FEM#PARAMS_VIEW_FUNCTION
		 * @return Wertliste als {@link FEMFunction}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final FEMFunction __compileArrayAsFunction() throws ScriptException {
			if (!this.__arrayEnabled) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			this.skip();
			if (this.__compileType() == ']') {
				this.skip();
				return new ValueFunction(FEM.__emptyArray);
			}
			final List<FEMFunction> list = new ArrayList<>();
			boolean value = true;
			while (true) {
				final FEMFunction item = this.__compileParamAsFunction();
				list.add(item);
				value = value && (item instanceof ValueFunction);
				switch (this.__compileType()) {
					case ';': {
						this.skip();
						this.__compileType();
						break;
					}
					case ']': {
						this.skip();
						final int size = list.size();
						if (!value) {
							final FEMFunction result = new InvokeFunction(FEM.PARAMS_VIEW_FUNCTION, true, list.toArray(new FEMFunction[size]));
							return result;
						}
						final FEMValue[] values = new FEMValue[size];
						for (int i = 0; i < size; i++) {
							values[i] = list.get(i).invoke(FEMFrame.EMPTY);
						}
						return new ValueFunction(FEM.arrayValue(values));
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/**
		 * Diese Methode kompiliert via {@code this.helper().compileParam(this, this.section())} den beim aktuellen Bereich beginnende Parameter und gibt diesen
		 * zurück.
		 * 
		 * @see ScriptCompilerHelper#compileParam(ScriptCompiler, String)
		 * @return Parameter.
		 * @throws ScriptException Wenn {@link #section()} ungültig ist.
		 */
		final FEMFunction __compileParam() throws ScriptException {
			try {
				final FEMFunction result = this.__helper.compileParam(this, this.section());
				if (result == null) throw new ScriptException().useSender(this).useHint(" Parameter erwartet.");
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this);
			}
		}

		/**
		 * Diese Methode kompiliert denF beim aktuellen Bereich beginnende Wert und gibt diese zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final FEMValue __compileParamAsValue() throws ScriptException {
			switch (this.__compileType()) {
				case -1:
				case '$':
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}': {
					throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
				}
				case '[': {
					return this.__compileArrayAsValue();
				}
				case '{': {
					if (this.__closureEnabled) throw new ScriptException().useSender(this).useHint(" Ungebundene Funktion unzulässig.");
					final FEMFunction retult = this.__compileFrame();
					return FEM.functionValue(retult);
				}
				default: {
					final FEMFunction param = this.__compileParam();
					if (!(param instanceof ValueFunction)) throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
					final FEMValue result = param.invoke(FEMFrame.EMPTY);
					return result;
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final FEMFunction __compileParamAsFunction() throws ScriptException {
			FEMFunction result;
			boolean indirect = false;
			switch (this.__compileType()) {
				case -1:
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}': {
					throw new ScriptException().useSender(this).useHint(" Wert oder Funktion erwartet.");
				}
				case '$': {
					this.skip();
					final String name = this.__compileName();
					if (name == null) return FEM.PARAMS_VIEW_FUNCTION;
					int index = this.__compileIndex(name);
					if (index < 0) {
						index = this.__params.indexOf(name);
						if (index < 0) throw new ScriptException().useSender(this).useHint(" Parametername «%s» ist unbekannt.", name);
					} else if (index > 0) {
						index--;
					} else throw new ScriptException().useSender(this).useHint(" Parameterindex «%s» ist unzulässig.", index);
					return ParamFunction.from(index);
				}
				case '{': {
					result = this.__compileFrame();
					if (this.__compileType() != '(') {
						if (this.__closureEnabled) return ClosureFunction.from(result);
						return new ValueFunction(FEM.functionValue(result));
					}
					if (!this.__chainingEnabled) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
					break;
				}
				case '[': {
					return this.__compileArrayAsFunction();
				}
				default: {
					result = this.__compileParam();
					if (this.__compileType() != '(') {
						if (this.__handlerEnabled) return new ValueFunction(FEM.functionValue(result));
						throw new ScriptException().useSender(this).useHint(" Funktionsverweise sind nicht zulässig.");
					}
				}
			}
			do {
				if (indirect && !this.__chainingEnabled) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
				this.skip(); // '('
				final List<FEMFunction> list = new ArrayList<>();
				while (true) {
					if (this.__compileType() == ')') {
						this.skip();
						result = new InvokeFunction(result, !indirect, list.toArray(new FEMFunction[list.size()]));
						break;
					}
					final FEMFunction item = this.__compileParamAsFunction();
					list.add(item);
					switch (this.__compileType()) {
						default:
							throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «)» erwartet.");
						case ';':
							this.skip();
						case ')':
					}
				}
				indirect = true;
			} while (this.__compileType() == '(');
			return result;
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
		 * 
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final ProxyFunction __compileProxy() throws ScriptException {
			final String name = this.__compileName();
			if ((name == null) || (this.__compileIndex(name) >= 0)) throw new ScriptException().useSender(this).useHint(" Funktionsname erwartet.");
			final ProxyFunction result = this.proxy(name);
			if (this.__compileType() != '{') throw new ScriptException().useSender(this).useHint(" Parametrisierter Funktionsaufruf erwartet.");
			final FEMFunction target = this.__compileFrame();
			result.set(target);
			return result;
		}

		/**
		 * Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
		 * Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0} ist.
		 * 
		 * @return Funktions- oder Parametername oder {@code null}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final String __compileName() throws ScriptException {
			try {
				switch (this.__compileType()) {
					case '$':
					case '(':
					case '[':
					case '{': {
						throw new IllegalStateException();
					}
					case -1:
					case ':':
					case ';':
					case ')':
					case '}':
					case ']': {
						return null;
					}
				}
				final String result = this.__helper.compileName(this, this.section());
				if (result.isEmpty()) throw new IllegalArgumentException();
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this).useHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich (<code>'{'</code>) beginnende, parametrisierte Funktion in einen {@link FunctionValue} und gibt
		 * diesen zurück.
		 * 
		 * @return Funktion als {@link FunctionValue}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		final FEMFunction __compileFrame() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this.__compileType() < 0) throw new ScriptException().useSender(this);
				final String name = this.__compileName();
				if (name != null) {
					if (this.__compileIndex(name) >= 0) throw new ScriptException().useSender(this).useHint(" Parametername erwartet.");
					this.__params.add(count++, name);
				}
				switch (this.__compileType()) {
					case ';': {
						if (name == null) throw new ScriptException().useSender(this).useHint(" Parametername oder Zeichen «:» erwartet.");
						this.skip();
						break;
					}
					case ':': {
						this.skip();
						final FEMFunction result = this.__compileParamAsFunction();
						if (this.__compileType() != '}') throw new ScriptException().useSender(this).useHint(" Zeichen «}» erwartet.");
						this.skip();
						this.__params.subList(0, count).clear();
						return result;
					}
					default: {
						throw new ScriptException().useSender(this);
					}
				}
			}
		}

		void __format() {
			while (true) {
				this.__formatSequence(false);
				if (this.symbol() < 0) return;
				this.__formatter.put(this.section()).putBreakSpace();
				this.skip();
			}
		}

		void __formatArray() {
			this.__formatter.put("[").putBreakInc();
			this.skip();
			this.__formatSequence(false);
			if (this.symbol() == ']') {
				this.__formatter.putBreakDec().put("]");
				this.skip();
			}
		}

		void __formatParam() {
			this.__formatter.put("(").putBreakInc();
			this.skip();
			this.__formatSequence(false);
			if (this.symbol() == ')') {
				this.__formatter.putBreakDec().put(")");
				this.skip();
			}
		}

		final void __formatFrame() {
			this.__formatter.put("{");// .putBreakInc();
			this.skip();
			this.__formatSequence(true);
			if (this.symbol() == ':') {
				this.__formatter.put(": ");
				this.skip();
				this.__formatSequence(false);
			}
			if (this.symbol() == '}') {
				this.__formatter// .putBreakDec()
					.put("}");
				this.skip();
			}
		}

		/**
		 * Diese Methode gibt das zurück. <br>
		 * die Sequenz endet an jeder Klammer sowie dem Doppelpunkt.
		 */
		void __formatSequence(final boolean space) {
			int count = 0;
			while (true) {
				switch (this.symbol()) {
					case '_': {
						this.skip();
						break;
					}
					case '/': {
						this.__formatter.put(this.section());
						if (space) {
							this.__formatter.put(" ");
						} else {
							this.__formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case ';': {
						this.__formatter.put(";");
						if (space) {
							this.__formatter.put(" ");
						} else {
							this.__formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case '(': {
						this.__formatParam();
						break;
					}
					case '[': {
						this.__formatArray();
						break;
					}
					case '{': {
						this.__formatFrame();
						break;
					}
					default: {
						this.__formatter.put(this.section());
						this.skip();
						break;
					}
					case ':':
					case ']':
					case '}':
					case ')':
					case -1: {
						if (count < 2) return;
						this.__formatter.putIndent();
						return;
					}
				}
			}
		}

		/**
		 * Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name des Platzhalters.
		 * @return Platzhalterfunktion.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 */
		public final ProxyFunction proxy(final String name) throws NullPointerException {
			synchronized (this.__proxies) {
				ProxyFunction result = this.__proxies.get(name);
				if (result != null) return result;
				this.__proxies.put(name, result = new ProxyFunction(name));
				return result;
			}
		}

		/**
		 * Diese Methode gibt den aktuellen Bereich zurück.
		 * 
		 * @return aktueller Bereich.
		 */
		public final Range range() {
			return this.isParsed() ? Range.EMPTY : this.__script.get(this.index());
		}

		/**
		 * Diese Methode gibt den zu kompilierenden Quelltext zurück.
		 * 
		 * @return Quelltext.
		 */
		public final FEMScript script() {
			return this.__script;
		}

		/**
		 * Diese Methode gibt die genutzten Kompilationsmethoden zurück.
		 * 
		 * @return Kompilationsmethoden.
		 */
		public final ScriptCompilerHelper helper() {
			return this.__helper;
		}

		/**
		 * Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.
		 * 
		 * @return Abbildung von Namen auf Platzhalter.
		 */
		public final Map<String, ProxyFunction> proxies() {
			return this.__proxies;
		}

		/**
		 * Diese Methode gibt die Liste der aktellen Parameternamen zurück.
		 * 
		 * @return Parameternamen.
		 */
		public final List<String> params() {
			return Collections.unmodifiableList(this.__params);
		}

		/**
		 * Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
		 * 
		 * @see Range#extract(String)
		 * @return Aktuelle Zeichenkette.
		 */
		public final String section() {
			return this.range().extract(this.__script.source());
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Wertlisten.
		 */
		public final boolean isArrayEnabled() {
			return this.__arrayEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link ScriptCompilerHelper#compileParam(ScriptCompiler, String)} als {@link FunctionValue}
		 * gelieferten Funktionen als Funktionszeiger zu {@link ValueFunction}s kompiliert werden dürfen (z.B {@code SORT(array; compFun)}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Funktionszeigern.
		 */
		public final boolean isHandlerEnabled() {
			return this.__handlerEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link ClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Stapelrahmens.
		 */
		public final boolean isClosureEnabled() {
			return this.__closureEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
		 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
		 * 
		 * @see #compileFunction()
		 * @see InvokeFunction#direct()
		 * @see InvokeFunction#invoke(FEMFrame)
		 * @return Zulässigkeit der Verkettung von Funktionen.
		 */
		public final boolean isChainingEnabled() {
			return this.__chainingEnabled;
		}

		public final ScriptFormatter formatter() {
			return this.__formatter;
		}

		/**
		 * Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
		 * 
		 * @param value Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useScript(final FEMScript value) throws NullPointerException, IllegalStateException {
			this.__check();
			this.source(value.types());
			this.__script = value;
			return this;
		}

		/**
		 * Diese Methode setzt die zu nutzenden Kompilationsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Kompilationsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useHelper(final ScriptCompilerHelper value) throws NullPointerException, IllegalStateException {
			if (value == null) throw new NullPointerException("value = null");
			this.__check();
			this.__helper = value;
			return this;
		}

		/**
		 * Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useParams(final String... value) throws NullPointerException, IllegalStateException {
			return this.useParams(Arrays.asList(value));
		}

		/**
		 * Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useParams(final List<String> value) throws NullPointerException, IllegalStateException {
			if (value.contains(null)) throw new NullPointerException("value.contains(null)");
			this.__check();
			this.__params.clear();
			this.__params.addAll(value);
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Wertlisten.
		 * 
		 * @see #isArrayEnabled()
		 * @param value Zulässigkeit von Wertlisten.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useArrayEnabled(final boolean value) throws IllegalStateException {
			this.__check();
			this.__arrayEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Funktionszeigern.
		 * 
		 * @see #isHandlerEnabled()
		 * @param value Zulässigkeit von Funktionszeigern.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useHandlerEnabled(final boolean value) throws IllegalStateException {
			this.__check();
			this.__handlerEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Bindung des Stapelrahmens.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Stapelrahmens.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useClosureEnabled(final boolean value) throws IllegalStateException {
			this.__check();
			this.__closureEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
		 * 
		 * @see #isChainingEnabled()
		 * @param value Zulässigkeit der Verkettung von Funktionen.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public synchronized ScriptCompiler useChainingEnabled(final boolean value) throws IllegalStateException {
			this.__check();
			this.__chainingEnabled = value;
			return this;
		}

		public synchronized ScriptCompiler useFormatter(final ScriptFormatter value) throws NullPointerException, IllegalStateException {
			this.__check();
			if (value == null) throw new NullPointerException("value = null");
			this.__formatter = value;
			return this;
		}

		public final void formatScript() throws IllegalStateException {
			this.__start();
			try {
				this.__format();
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Wert oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMValue compileValue() throws ScriptException, IllegalStateException {
			this.__start();
			try {
				if (this.__compileType() < 0) return null;
				final FEMValue result = this.__compileParamAsValue();
				if (this.__compileType() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück.<br>
		 * Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert sein. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Wertliste
		 * geliefert.
		 * 
		 * @return Werte.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMValue[] compileValues() throws ScriptException, IllegalStateException {
			this.__start();
			try {
				if (this.__compileType() < 0) return new FEMValue[0];
				final List<FEMValue> result = new ArrayList<FEMValue>();
				while (true) {
					result.add(this.__compileParamAsValue());
					switch (this.__compileType()) {
						case -1: {
							return result.toArray(new FEMValue[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück.<br>
		 * Die Parameterfunktion müssen durch Bereiche vom Typ {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem
		 * {@link ScriptCompilerHelper#compileName(ScriptCompiler, String) Namen} und ist sonst durch eine parametrisierte Funktion gegeben. Wenn der Quelltext nur
		 * Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ProxyFunction[] compileProxies() throws ScriptException, IllegalStateException {
			this.__start();
			try {
				final List<ProxyFunction> result = new ArrayList<ProxyFunction>();
				if (this.__compileType() < 0) return new ProxyFunction[0];
				while (true) {
					result.add(this.__compileProxy());
					switch (this.__compileType()) {
						case -1: {
							return result.toArray(new ProxyFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Funktion oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMFunction compileFunction() throws ScriptException, IllegalStateException {
			this.__start();
			try {
				if (this.__compileType() < 0) return null;
				final FEMFunction result = this.__compileParamAsFunction();
				if (this.__compileType() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.__stop();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
		 * separiert sein.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMFunction[] compileFunctions() throws ScriptException, IllegalStateException {
			this.__start();
			try {
				if (this.__compileType() < 0) return new FEMFunction[0];
				final List<FEMFunction> result = new ArrayList<FEMFunction>();
				while (true) {
					result.add(this.__compileParamAsFunction());
					switch (this.__compileType()) {
						case -1: {
							return result.toArray(new FEMFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.__stop();
			}
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.__helper, this.__params, this.__script, this.__proxies);
		}

	}

	/**
	 * Diese Schnittstelle definiert Kompilationsmethoden, die von einem {@link ScriptCompiler Kompiler} zur Übersetzung von Quelltexten in Werte, Funktionen und
	 * Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptCompilerHelper {

		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden immer {@code null} liefern.
		 */
		static ScriptCompilerHelper DEFAULT = new ScriptCompilerHelper() {

			// TODO
			{}

			@Override
			public String compileName(final ScriptCompiler compiler, final String section) throws ScriptException {
				return section;
			}

			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler, String section) throws ScriptException {
				switch (compiler.range().type()) {
					case '"':
					case '\'': {
						return new ValueFunction(FEM.stringValue(FEM.parseString(section)));
					}
					case '?': {
						return compiler.proxy(FEM.parseValue(section));
					}
					default: {
						section = FEM.parseValue(section);
						if (section.equalsIgnoreCase("NULL")) return new ValueFunction(FEM.__void);
						if (section.equalsIgnoreCase("TRUE")) return new ValueFunction(FEM.__true);
						if (section.equalsIgnoreCase("FALSE")) return new ValueFunction(FEM.__false);
						try {
							return new ValueFunction(FEM.decimalValue(new BigDecimal(section)));
						} catch (final NumberFormatException cause) {
							return compiler.proxy(section);
						}
					}
				}
			}

			@Override
			public String toString() {
				return "DEFAULT";
			}

		};

		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
		 * 
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @param section aktueller Bereich des Quelltexts ({@link ScriptCompiler#section()}).
		 * @return Funktions- bzw. Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
		 */
		public String compileName(ScriptCompiler compiler, String section) throws ScriptException;

		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebene Parameter als Funktion zurück. Ein Parameter kann hierbei
		 * für eine Funktion stehen. Konstante Parameterwerte sollten als {@link ValueFunction} oder {@link ProxyFunction} geliefert werden.
		 * 
		 * @see ScriptCompiler#proxy(String)
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @param section aktueller Bereich des Quelltexts ({@link ScriptCompiler#section()}).
		 * @return Parameter als {@link FEMFunction}, Parameterwert als {@link ValueFunction} oder Platzhalter als {@link ProxyFunction}.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält.
		 */
		public FEMFunction compileParam(ScriptCompiler compiler, String section) throws ScriptException;

	}

	/**
	 * Diese Klasse implementiert einen Formatter, der Werten und Funktionen in eine Zeichenkette überführen kann. Dieser realisiert damit die entgegen gesetzte
	 * Operation zur Kombination von {@link ScriptParser} und {@link ScriptCompiler}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptFormatter {

		/**
		 * Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
		 * 
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		private static final class Mark {

			/**
			 * Dieses Feld speichert das Objekt, dass in {@link #__items} vor jeder Markierung eingefügt wird.
			 */
			public static final Mark DEFAULT = new ScriptFormatter.Mark(0, false, false, false);

			{}

			/**
			 * Dieses Feld speichert die Eigenschaften dieser Markierung.
			 */
			int __data;

			/**
			 * Dieser Konstruktor initialisiert die Markierung.
			 * 
			 * @param level Einrücktiefe ({@link #level()}).
			 * @param last Endmarkierung ({@link #isLast()}).
			 * @param space Leerzeichen ({@link #isSpace()}).
			 * @param enabled Aktivierung ({@link #isEnabled()}).
			 */
			public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
				this.__data = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
			}

			{}

			/**
			 * Diese Methode gibt die Tiefe der Einrückung zurück.
			 * 
			 * @return Tiefe der Einrückung.
			 */
			public int level() {
				return this.__data >> 3;
			}

			/**
			 * Diese Methode aktiviert die Einrückung.
			 */
			public void enable() {
				this.__data |= 2;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
			 * 
			 * @return {@code true} bei einer Endmarkierung.
			 */
			public boolean isLast() {
				return (this.__data & 1) != 0;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
			 * 
			 * @return {@code true} bei einem bedingten Leerzeichen.
			 */
			public boolean isSpace() {
				return (this.__data & 4) != 0;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
			 * 
			 * @return Aktivierung.
			 */
			public boolean isEnabled() {
				return (this.__data & 2) != 0;
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
			}

		}

		{}

		/**
		 * Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen.
		 */
		final List<Object> __items = new ArrayList<Object>();

		/**
		 * Dieses Feld speichert den Puffer für {@link #format()}.
		 */
		final StringBuilder __string = new StringBuilder();

		/**
		 * Dieses Feld speichert den Stack der Hierarchieebenen.
		 */
		final LinkedList<Boolean> __indents = new LinkedList<Boolean>();

		/**
		 * Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "    "}.
		 */
		String __indent = "\t";

		/**
		 * Dieses Feld speichert die Formatierungsmethoden.
		 */
		ScriptFormatterHelper __helper = ScriptFormatterHelper.DEFAULT;

		{}

		/**
		 * Diese Methode beginnt das Parsen und sollte nur in Verbindung mit {@link #stopFormatting()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		protected final void startFormatting() throws IllegalStateException {
			this.checkIdling();
			this.__indents.addLast(Boolean.FALSE);
		}

		/**
		 * Diese Methode beendet das Parsen und sollte nur in Verbindung mit {@link #startFormatting()} verwendet werden.
		 */
		protected final void stopFormatting() {
			this.__items.clear();
			this.__string.setLength(0);
			this.__indents.clear();
		}

		/**
		 * Diese Methode prüft den Parsestatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		protected final void checkIdling() throws IllegalStateException {
			if (this.__indents.size() != 0) throw new IllegalStateException();
		}

		/**
		 * Diese Methode prüft den Parsestatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		protected final void checkFormatting() throws IllegalStateException {
			if (this.__indents.size() == 0) throw new IllegalStateException();
		}

		/**
		 * Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
		 * 
		 * @param object Markierung.
		 * @return {@code this}.
		 */
		private ScriptFormatter putMark(final Mark object) {
			this.__items.add(object);
			return this;
		}

		/**
		 * Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
		 * angefügt.
		 * 
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakInc() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.__indents;
			indents.addLast(Boolean.FALSE);
			return this.putMark(Mark.DEFAULT).putMark(new Mark(indents.size(), false, false, false));
		}

		/**
		 * Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
		 * passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde oder aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakDec() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.__indents;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this.putMark(Mark.DEFAULT).putMark(new Mark(value, true, false, indents.removeLast().booleanValue()));
		}

		/**
		 * Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
		 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakSpace() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.__indents;
			return this.putMark(Mark.DEFAULT).putMark(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
		}

		/**
		 * Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
		 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
		 * 
		 * @see #putBreakSpace()
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putIndent() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.__indents;
			if (this.__indents.getLast().booleanValue()) return this;
			final int value = indents.size();
			for (int i = 0; i < value; i++) {
				indents.set(i, Boolean.TRUE);
			}
			final List<Object> items = this.__items;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == Mark.DEFAULT) {
					final Mark token = (Mark)items.get(i + 1);
					if (token.level() <= value) {
						if (token.isEnabled()) return this;
						token.enable();
					} // else if (token.level() < value) return this;
					i--;
				}
			}
			return this;
		}

		/**
		 * Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * seine {@link Object#toString() Textdarstellung} angefügt.
		 * 
		 * @see Object#toString()
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @param part Objekt.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann.
		 */
		public ScriptFormatter put(final Object part) throws IllegalStateException, IllegalArgumentException {
			if (part == null) throw new NullPointerException("part = null");
			this.checkFormatting();
			if (part instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)part).toScript(this);
			} else {
				this.__items.add(part.toString());
			}
			return this;
		}

		/**
		 * Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird es
		 * über {@link ScriptFormatterHelper#formatData(ScriptFormatter, Object)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatData(ScriptFormatter, Object)
		 * @param data Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann.
		 */
		public ScriptFormatter putData(final Object data) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (data == null) throw new NullPointerException("function = null");
			this.checkFormatting();
			if (data instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)data).toScript(this);
			} else {
				this.__helper.formatData(this, data);
			}
			return this;
		}

		/**
		 * Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
		 * Wenn die Liste leer ist, wird {@code "[]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
		 * separiert über {@link #putValue(FEMValue)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor
		 * der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putValue(FEMValue)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param array Wertliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann.
		 */
		public ScriptFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (array == null) throw new NullPointerException("array = null");
			this.checkFormatting();
			final Iterator<? extends FEMValue> iter = array.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("[").putBreakInc().putValue(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace().putValue(item);
					} while (iter.hasNext());
					this.putBreakDec().put("]");
				} else {
					this.put("[").putBreakInc().putValue(item).putBreakDec().put("]");
				}
			} else {
				this.put("[]");
			}
			return this;
		}

		/**
		 * Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.<br>
		 * Wenn der Wert ein {@link ScriptFormatterInput} ist, wird er über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird er
		 * über {@link ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann.
		 */
		public ScriptFormatter putValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value == null) throw new NullPointerException("value = null");
			this.checkFormatting();
			if (value instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)value).toScript(this);
			} else {
				this.__helper.formatValue(this, value);
			}
			return this;
		}

		/**
		 * Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Stapelrahmens an und gibt {@code this} zurück.<br>
		 * Wenn diese Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die nummerierten Parameterwerte in {@code "("} und {@code ")"} eingeschlossen,
		 * sowie mit {@code ";"} separiert über {@link #putValue(FEMValue)} angefügt. Vor jedem Parameterwert wird dessen logische Position {@code i} als
		 * {@code "$i: "} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor der schließenden Klammer
		 * {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param params Stapelrahmen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann.
		 */
		public ScriptFormatter putFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this.checkFormatting();
			final Iterator<? extends FEMValue> iter = params.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().put("$1: ").putValue(item);
					int index = 2;
					do {
						item = iter.next();
						this.put(";").putBreakSpace().put("$").put(index).put(": ").putValue(item);
						index++;
					} while (iter.hasNext());
					this.putBreakDec().put(")");
				} else {
					this.put("(").putBreakInc().put("$1: ").putValue(item).putBreakDec().put(")");
				}
			} else {
				this.put("()");
			}
			return this;
		}

		/**
		 * Diese Methode fügt den Quelltext der Liste der gegebenen Parameterfunktionen an und gibt {@code this} zurück.<br>
		 * Wenn die Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die Parameterfunktionen in {@code "("} und {@code ")"} eingeschlossen sowie mit
		 * {@code ";"} separiert über {@link #putFunction(FEMFunction)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue
		 * Hierarchieebene, die vor der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes
		 * Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Funktionsliste mehr als ein Element enthält.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param params Funktionsliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann.
		 */
		public ScriptFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this.checkFormatting();
			final Iterator<? extends FEMFunction> iter = params.iterator();
			if (iter.hasNext()) {
				FEMFunction item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().putFunction(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace().putFunction(item);
					} while (iter.hasNext());
					this.putBreakDec().put(")");
				} else {
					this.put("(").putBreakInc().putFunction(item).putBreakDec().put(")");
				}
			} else {
				this.put("()");
			}
			return this;
		}

		/**
		 * Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.<br>
		 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(FEMFunction)} angefügt.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param function parametrisierte Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public ScriptFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			return this.put("{: ").putFunction(function).put("}");
		}

		/**
		 * Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
		 * Wenn die Funktion ein {@link ScriptFormatterInput} ist, wird sie über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * sie über {@link ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)
		 * @param function Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public ScriptFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			this.checkFormatting();
			if (function instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)function).toScript(this);
			} else {
				this.__helper.formatFunction(this, function);
			}
			return this;
		}

		/**
		 * Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
		 * 
		 * @return Zeichenkette zur Einrückung oder {@code null}.
		 */
		public String getIndent() {
			return this.__indent;
		}

		/**
		 * Diese Methode gibt die genutzten Formatierungsmethoden zurück.
		 * 
		 * @return Formatierungsmethoden.
		 */
		public ScriptFormatterHelper getHelper() {
			return this.__helper;
		}

		/**
		 * Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück. Wenn diese {@code null} ist, wird nicht
		 * eingerückt.
		 * 
		 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		public ScriptFormatter useIndent(final String indent) throws IllegalStateException {
			this.checkIdling();
			this.__indent = indent;
			return this;
		}

		/**
		 * Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		public ScriptFormatter useHelper(final ScriptFormatterHelper helper) throws NullPointerException, IllegalStateException {
			if (helper == null) throw new NullPointerException("helper = null");
			this.checkIdling();
			this.__helper = helper;
			return this;
		}

		/**
		 * Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
		 * 
		 * @see #put(Object)
		 * @return Quelltext.
		 */
		private String format() {
			final String indent = this.__indent;
			final List<Object> items = this.__items;
			final StringBuilder string = this.__string;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == Mark.DEFAULT) {
					final Mark token = (Mark)items.get(i++);
					if (token.isEnabled() && (indent != null)) {
						string.append('\n');
						for (int count = token.level() - (token.isLast() ? 2 : 1); count > 0; count--) {
							string.append(indent);
						}
					} else if (token.isSpace()) {
						string.append(' ');
					}
				} else {
					string.append(item);
				}
			}
			return string.toString();
		}

		/**
		 * Diese Methode formatiert die gegebenen Elemente in einen Quelltext und gibt diesen zurück.<br>
		 * Die Elemente werden über den gegebenen {@link Converter} angefügt und mit {@code ';'} separiert. In der Methode {@link Converter#convert(Object)} sollten
		 * hierfür {@link #putData(Object)}, {@link #putValue(FEMValue)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
		 * 
		 * @see #formatData(Iterable)
		 * @see #formatValue(Iterable)
		 * @see #formatFunction(Iterable)
		 * @param <GItem> Typ der Elemente.
		 * @param items Elemente.
		 * @param formatter {@link Converter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann.
		 */
		private <GItem> String format(final Iterable<? extends GItem> items, final Converter<GItem, ?> formatter) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			this.startFormatting();
			try {
				final Iterator<? extends GItem> iter = items.iterator();
				if (!iter.hasNext()) return "";
				GItem item = iter.next();
				if (iter.hasNext()) {
					this.putIndent();
					formatter.convert(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace();
						formatter.convert(item);
					} while (iter.hasNext());
				} else {
					formatter.convert(item);
				}
				return this.format();
			} finally {
				this.stopFormatting();
			}
		}

		/**
		 * Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatData(Arrays.asList(datas))}.
		 * 
		 * @see #formatData(Iterable)
		 * @param datas Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code datas} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatData(final Object... datas) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (datas == null) throw new NullPointerException("datas = null");
			return this.formatData(Arrays.asList(datas));
		}

		/**
		 * Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putData(Object)
		 * @param datas Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code datas} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann.
		 */
		public String formatData(final Iterable<?> datas) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (datas == null) throw new NullPointerException("values = null");
			return this.format(datas, new Converter<Object, Object>() {

				@Override
				public Object convert(final Object input) {
					return ScriptFormatter.this.putData(input);
				}

			});
		}

		/**
		 * Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatValue(Arrays.asList(values))}.
		 * 
		 * @see #formatValue(Iterable)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatValue(final FEMValue... values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (values == null) throw new NullPointerException("values = null");
			return this.formatValue(Arrays.asList(values));
		}

		/**
		 * Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Die Werte werden über {@link #putValue(FEMValue)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putValue(FEMValue)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatValue(final Iterable<? extends FEMValue> values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (values == null) throw new NullPointerException("values = null");
			return this.format(values, new Converter<FEMValue, Object>() {

				@Override
				public Object convert(final FEMValue input) {
					return ScriptFormatter.this.putValue(input);
				}

			});
		}

		/**
		 * Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatFunction(Arrays.asList(functions))}.
		 * 
		 * @see #formatFunction(Iterable)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann.
		 */
		public String formatFunction(final FEMFunction... functions) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (functions == null) throw new NullPointerException("functions = null");
			return this.formatFunction(Arrays.asList(functions));
		}

		/**
		 * Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann.
		 */
		public String formatFunction(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (functions == null) throw new NullPointerException("functions = null");
			return this.format(functions, new Converter<FEMFunction, Object>() {

				@Override
				public Object convert(final FEMFunction input) {
					return ScriptFormatter.this.putFunction(input);
				}

			});
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.__helper, this.__indent, this.__items);
		}

	}

	/**
	 * Diese Schnittstelle definiert ein Objekt, welches sich selbst in seine Quelltextdarstellung überführen und diese an einen {@link ScriptFormatter} anfügen
	 * kann.
	 * 
	 * @see ScriptFormatter#put(Object)
	 * @see ScriptFormatter#putValue(FEMValue)
	 * @see ScriptFormatter#putFunction(FEMFunction)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptFormatterInput {

		/**
		 * Diese Methode formatiert dieses Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.<br>
		 * Sie wird vom {@link ScriptFormatter} im Rahmen folgender Methoden aufgerufen:
		 * <ul>
		 * <li>{@link ScriptFormatter#put(Object)}</li>
		 * <li>{@link ScriptFormatter#putData(Object)}</li>
		 * <li>{@link ScriptFormatter#putValue(FEMValue)}</li>
		 * <li>{@link ScriptFormatter#putFunction(FEMFunction)}</li>
		 * </ul>
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann.
		 */
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException;

	}

	/**
	 * Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link ScriptFormatter#putValue(FEMValue)} und
	 * {@link ScriptFormatter#putFunction(FEMFunction)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public interface ScriptFormatterHelper {

		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden ihre Eingeben über {@link String#valueOf(Object)} formatieren.<br>
		 * {@link FEMScript Aufbereitete Quelltexte} wirden in {@link #formatData(ScriptFormatter, Object)} analog zur Interpretation des {@link ScriptCompiler}
		 * formatiert.
		 */
		static ScriptFormatterHelper DEFAULT = new ScriptFormatterHelper() {

			@Override
			public void formatData(final ScriptFormatter target, final Object data) throws IllegalArgumentException {
				if (data instanceof FEMScript) {
					FEM.scriptCompiler().useScript((FEMScript)data).useFormatter(target).formatScript();
				} else {
					target.put(String.valueOf(data));
				}
			}

			@Override
			public void formatValue(final ScriptFormatter target, final FEMValue value) throws IllegalArgumentException {
				target.put(String.valueOf(value));
			}

			@Override
			public void formatFunction(final ScriptFormatter target, final FEMFunction function) throws IllegalArgumentException {
				target.put(String.valueOf(function));
			}

			@Override
			public String toString() {
				return "DEFAULT";
			}

		};

		/**
		 * Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param data Objekt.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann.
		 */
		public void formatData(ScriptFormatter target, Object data) throws IllegalArgumentException;

		/**
		 * Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann.
		 */
		public void formatValue(ScriptFormatter target, FEMValue value) throws IllegalArgumentException;

		/**
		 * Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public void formatFunction(ScriptFormatter target, FEMFunction function) throws IllegalArgumentException;

	}

	/**
	 * Diese Klasse implementiert die {@link IllegalArgumentException}, die bei Syntaxfehlern von einem {@link ScriptParser} oder {@link ScriptCompiler} ausgelöst
	 * wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptException extends IllegalArgumentException {

		/**
		 * Dieses Feld speichert die Serial-Version-UID.
		 */
		private static final long serialVersionUID = -918623847189389909L;

		{}

		/**
		 * Dieses Feld speichert den Hinweis zum erwarteten Inhalt des Bereichs.
		 */
		String __hint = "";

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		FEMScript __script = FEMScript.EMPTY;

		/**
		 * Dieses Feld speichert den Bereich, in dem der Syntaxfehler entdeckt wurde.
		 */
		Range __range = Range.EMPTY;

		/**
		 * Dieser Konstruktor initialisiert die {@link ScriptException} ohne Ursache.
		 */
		public ScriptException() {
			super();
		}

		/**
		 * Dieser Konstruktor initialisiert die {@link ScriptException} mit Ursache.
		 * 
		 * @param cause Urssache.
		 */
		public ScriptException(final Throwable cause) {
			super(cause);
		}

		{}

		/**
		 * Diese Methode gibt den Hinweis zum erwarteten Inhalt des Bereichs zurück.
		 * 
		 * @see #getRange()
		 * @return Hinweis oder {@code null}.
		 */
		public String getHint() {
			return this.__hint;
		}

		/**
		 * Diese Methode gibt den Bereich zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Bereich.
		 */
		public Range getRange() {
			return this.__range;
		}

		/**
		 * Diese Methode gibt Quelltext zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Quelltext.
		 */
		public FEMScript getScript() {
			return this.__script;
		}

		/**
		 * Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #getHint()
		 * @param hint Hinweis.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code hint} {@code null} ist.
		 */
		public ScriptException useHint(final String hint) throws NullPointerException {
			this.__hint = hint.toString();
			return this;
		}

		/**
		 * Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #useHint(String)
		 * @see String#format(String, Object...)
		 * @param format Hinweis.
		 * @param args Formatargumente.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code format} bzw. {@code args} {@code null} ist.
		 */
		public ScriptException useHint(final String format, final Object... args) throws NullPointerException {
			return this.useHint(String.format(format, args));
		}

		/**
		 * Diese Methode setzt den Bereich und gibt {@code this} zurück.
		 * 
		 * @see #getRange()
		 * @param range Bereich.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code range} {@code null} ist.
		 */
		public ScriptException useRange(final Range range) throws NullPointerException {
			if (range == null) throw new NullPointerException("range = null");
			this.__range = range;
			return this;
		}

		/**
		 * Diese Methode setzt den Quelltext und gibt {@code this} zurück.
		 * 
		 * @see #getScript()
		 * @param script Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code script} {@code null} ist.
		 */
		public ScriptException useScript(final FEMScript script) throws NullPointerException {
			if (script == null) throw new NullPointerException("script = null");
			this.__script = script;
			return this;
		}

		/**
		 * Diese Methode setzt Quelltext sowie Bereich und gibt {@code this} zurück.
		 * 
		 * @see #useScript(FEMScript)
		 * @see #useRange(Range)
		 * @param sender {@link ScriptCompiler}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code sender} {@code null} ist.
		 */
		public ScriptException useSender(final ScriptCompiler sender) throws NullPointerException {
			if (sender == null) throw new NullPointerException("sender = null");
			return this.useRange(sender.range()).useScript(sender.script());
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMessage() {
			return (this.__range == Range.EMPTY //
				? "Unerwartetes Ende der Zeichenkette." //
				: "Unerwartete Zeichenkette «" + this.__range.extract(this.__script.__source) + "» an Position " + this.__range.__start + ".") //
				+ this.__hint;
		}
	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Zustandsdaten einer {@link TraceFunction} zur Verfolgung und Überwachung der Verarbeitung von
	 * Funktionen. Dieses Objekt wird dazu das Argument für die Methoden des {@link ScriptTracerHelper} genutzt, welcher auf die Ereignisse der Überwachung
	 * reagieren kann.
	 * 
	 * @see TraceFunction
	 * @see ScriptTracerHelper
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ScriptTracer {

		/**
		 * Dieses Feld speichert den {@link ScriptTracerHelper}.
		 */
		ScriptTracerHelper __helper = ScriptTracerHelper.DEFAULT;

		/**
		 * Dieses Feld speichert den Stapelrahmen der Funktion. Dieser kann in der Methode {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf
		 * angepasst werden.
		 */
		FEMFrame __frame;

		/**
		 * Dieses Feld speichert die Function, die nach {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen wird bzw. vor
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} oder {@link ScriptTracerHelper#onReturn(ScriptTracer)} aufgerufen wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf angepasst werden.
		 */
		FEMFunction __function;

		/**
		 * Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode
		 * {@link ScriptTracerHelper#onReturn(ScriptTracer)} angepasst werden.
		 */
		FEMValue __result;

		/**
		 * Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} angepasst werden.
		 */
		RuntimeException __exception;

		{}

		/**
		 * Diese Methode gibt den {@link ScriptTracerHelper} zurück.
		 * 
		 * @return {@link ScriptTracerHelper}.
		 */
		public ScriptTracerHelper getHelper() {
			return this.__helper;
		}

		/**
		 * Diese Methode gibt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ergebniswert oder {@code null}.
		 */
		public FEMValue getResult() {
			return this.__result;
		}

		/**
		 * Diese Methode gibt den aktuellen Stapelrahmen zurück, der zur Auswertung der {@link #getFunction() aktuellen Funktion} verwendet wird.
		 * 
		 * @return Stapelrahmen oder {@code null}.
		 */
		public FEMFrame getFrame() {
			return this.__frame;
		}

		/**
		 * Diese Methode gibt die aktuelle Funktion zurück, die mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet wird.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public FEMFunction getFunction() {
			return this.__function;
		}

		/**
		 * Diese Methode gibt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ausnahme oder {@code null}.
		 */
		public RuntimeException getException() {
			return this.__exception;
		}

		/**
		 * Diese Methode setzt die Überwachungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Überwachungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useHelper(final ScriptTracerHelper value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__helper = value;
			return this;
		}

		/**
		 * Diese Methode setzt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Die {@link #getException()
		 * aktuelle Ausnahme} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ergebniswert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useResult(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__result = value;
			this.__exception = null;
			return this;
		}

		/**
		 * Diese Methode setzt den aktuellen Stapelrahmen und gibt {@code this} zurück. Dieser wird zur Auswertung der {@link #getFunction() aktuellen Funktion}
		 * verwendet.
		 * 
		 * @param value Stapelrahmen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useFrame(final FEMFrame value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__frame = value;
			return this;
		}

		/**
		 * Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet.
		 * 
		 * @param value Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useFunction(final FEMFunction value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__function = value;
			return this;
		}

		/**
		 * Diese Methode setzt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Der {@link #getResult() aktuelle
		 * Ergebniswert} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ausnahme.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useException(final RuntimeException value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.__result = null;
			this.__exception = value;
			return this;
		}

		/**
		 * Diese Methode setzt alle aktuellen Einstellungen auf {@code null} und gibt {@code this} zurück.
		 * 
		 * @see #getFrame()
		 * @see #getFunction()
		 * @see #getResult()
		 * @see #getException()
		 * @return {@code this}.
		 */
		public ScriptTracer clear() {
			this.__frame = null;
			this.__function = null;
			this.__result = null;
			this.__exception = null;
			return this;
		}

		/**
		 * Diese Methode gibt die gegebenen Funktion als {@link TraceFunction} oder unverändert zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation von
		 * {@link ScriptTracer#__function} verwendet werden.
		 * <p>
		 * Wenn die Funktion ein {@link ScriptTracerInput} ist, wird das Ergebnis von {@link ScriptTracerInput#toTrace(ScriptTracer)} zurück gegeben. Andernfalls
		 * wird die gegebene Funktion zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public FEMFunction trace(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (function instanceof ScriptTracerInput) return ((ScriptTracerInput)function).toTrace(this);
			return function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toFormatString(true, true, this, "helper", this.__helper, "frame", this.__frame, "function", this.__function, "result", this.__result,
				"exception", this.__exception);
		}

	}

	/**
	 * Diese Schnittstelle definiert ein Objekt, welches sich selbst in eine {@link TraceFunction} überführen kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptTracerInput {

		/**
		 * Diese Methode gibt dieses Objekt als als {@link TraceFunction} mit dem gegebenen {@link ScriptTracer} zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation der
		 * {@link ScriptTracer#getFunction() aktuellen Funktion} des {@link ScriptTracer} verwendet werden.<br>
		 * Wenn dieses Objekt ein Wert ist, muss er sich in einer {@link ValueFunction} liefern.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException;

	}

	/**
	 * Diese Schnittstelle definiert die Überwachungsmethoden zur Verfolgung der Verarbeitung von Funktionen.
	 * 
	 * @see ScriptTracer
	 * @see TraceFunction
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptTracerHelper {

		/**
		 * Dieses Feld speichert den {@code default}-{@link ScriptTracerHelper}, dessen Methoden den {@link ScriptTracer} nicht modifizieren.
		 */
		public static final ScriptTracerHelper DEFAULT = new ScriptTracerHelper() {

			@Override
			public void onThrow(final ScriptTracer event) {
			}

			@Override
			public void onReturn(final ScriptTracer event) {
			}

			@Override
			public void onExecute(final ScriptTracer event) {
			}

			@Override
			public String toString() {
				return "DEFAULT";
			}

		};

		/**
		 * Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
		 * {@link ScriptTracer#__exception} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#__exception
		 * @param event {@link ScriptTracer}.
		 */
		public void onThrow(ScriptTracer event);

		/**
		 * Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
		 * {@link ScriptTracer#__result} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#__result
		 * @param event {@link ScriptTracer}.
		 */
		public void onReturn(ScriptTracer event);

		/**
		 * Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link ScriptTracer#__frame} und {@link ScriptTracer#__function} können hierbei
		 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Stapelrahmen durchzuführen.
		 * 
		 * @see ScriptTracer#__frame
		 * @see ScriptTracer#__function
		 * @param event {@link ScriptTracer}.
		 */
		public void onExecute(ScriptTracer event);

	}

	{}

	/**
	 * Dieses Feld speichert den Identifikator von {@link #VOID_TYPE}.
	 */
	public static final int VOID_ID = 0;

	/**
	 * Dieses Feld speichert den Datentyp von {@link VoidValue}.
	 */
	public static final FEMType<FEMVoid> VOID_TYPE = FEMType.from(FEM.VOID_ID, "VOID");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #ARRAY_TYPE}.
	 */
	public static final int ARRAY_ID = 1;

	/**
	 * Dieses Feld speichert den Datentyp von {@link ArrayValue}.
	 */
	public static final FEMType<FEMArray> ARRAY_TYPE = FEMType.from(FEM.ARRAY_ID, "ARRAY");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #STRING_TYPE}.
	 */
	public static final int STRING_ID = 4;

	/**
	 * Dieses Feld speichert den Datentyp von {@link StringValue}.
	 */
	public static final FEMType<FEMString> STRING_TYPE = FEMType.from(FEM.STRING_ID, "STRING");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #BINARY_TYPE}.
	 */
	public static final int BINARY_ID = 5;

	/**
	 * Dieses Feld speichert den Datentyp von {@link BinaryValue}.
	 */
	public static final FEMType<FEMBinary> BINARY_TYPE = FEMType.from(FEM.BINARY_ID, "BINARY");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #INTEGER_TYPE}.
	 */
	public static final int INTEGER_ID = 6;

	/**
	 * Dieses Feld speichert den Datentyp von {@link IntegerValue}.
	 */
	public static final FEMType<FEMInteger> INTEGER_TYPE = FEMType.from(FEM.INTEGER_ID, "INTEGER");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DECIMAL_TYPE}.
	 */
	public static final int DECIMAL_ID = 7;

	/**
	 * Dieses Feld speichert den Datentyp von {@link DecimalValue}.
	 */
	public static final FEMType<FEMDecimal> DECIMAL_TYPE = FEMType.from(FEM.DECIMAL_ID, "DECIMAL");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #BOOLEAN_TYPE}.
	 */
	public static final int BOOLEAN_ID = 3;

	/**
	 * Dieses Feld speichert den Datentyp von {@link BooleanValue}.
	 */
	public static final FEMType<FEMBoolean> BOOLEAN_TYPE = FEMType.from(FEM.BOOLEAN_ID, "BOOLEAN");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DURATION_TYPE}.
	 */
	public static final int DURATION_ID = 8;

	/**
	 * Dieses Feld speichert den Datentyp von {@link DurationValue}.
	 */
	public static final FEMType<FEMDuration> DURATION_TYPE = FEMType.from(FEM.DURATION_ID, "DURATION");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #DATETIME_TYPE}.
	 */
	public static final int DATETIME_ID = 9;

	/**
	 * Dieses Feld speichert den Datentyp von {@link DatetimeValue}.
	 */
	public static final FEMType<FEMDatetime> DATETIME_TYPE = FEMType.from(FEM.DATETIME_ID, "DATETIME");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #FUNCTION_TYPE}.
	 */
	public static final int FUNCTION_ID = 2;

	/**
	 * Dieses Feld speichert den Datentyp von {@link FunctionValue}.
	 */
	public static final FEMType<FEMFunction> FUNCTION_TYPE = FEMType.from(FEM.FUNCTION_ID, "FUNCTION");

	/**
	 * Dieses Feld speichert eine Funktion mit der Signatur {@code (method: Function, params: Array): Value}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameterwerte des Stapelrahmens gegeben Funktion mit den im
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
	 * {@code method(params1, ..., paramsN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameterwert des Stapelrahmens gegeben Funktion mit den davor
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
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte eines gegebenen Stapelrahmens {@code frame} entspricht, d.h.
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
	 * Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte eines gegebenen Stapelrahmens {@code frame} entspricht, d.h.
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

	@SuppressWarnings ("javadoc")
	static final VoidValue __void = new VoidValue();

	@SuppressWarnings ("javadoc")
	static final BooleanValue __true = new BooleanValue(FEMBoolean.TRUE);

	@SuppressWarnings ("javadoc")
	static final BooleanValue __false = new BooleanValue(FEMBoolean.FALSE);

	@SuppressWarnings ("javadoc")
	static final ArrayValue __emptyArray = new ArrayValue(FEMArray.EMPTY);

	@SuppressWarnings ("javadoc")
	static final StringValue __emptyString = new StringValue(FEMString.EMPTY);

	@SuppressWarnings ("javadoc")
	static final BinaryValue __emptyBinary = new BinaryValue(FEMBinary.EMPTY);

	{}

	/**
	 * Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().arrayOf(data)}.
	 * 
	 * @see FEMContext#arrayOf(Object)
	 * @param data Wertliste, natives Array, {@link Iterable} oder {@link Collection}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt bzw. eines der Elemente nicht umgewandelt werden kann.
	 */
	public static final FEMArray arrayOf(final Object data) throws IllegalArgumentException {
		return FEMContext.__default.arrayOf(data);
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().valueOf(data)}.
	 * 
	 * @see FEMContext#valueOf(Object)
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann.
	 */
	public static final FEMValue valueOf(final Object data) throws IllegalArgumentException {
		return FEMContext.__default.valueOf(data);
	}

	/**
	 * Diese Methode gibt den {@link FEMVoid#INSTANCE Leerwert} als {@link FEMValue} zurück.
	 * 
	 * @return {@link FEMVoid#INSTANCE}.
	 */
	public static final VoidValue voidValue() {
		return FEM.__void;
	}

	/**
	 * Diese Methode gibt {@code true} als {@link FEMValue} zurück.
	 * 
	 * @return {@code true}.
	 */
	public static final BooleanValue trueValue() {
		return FEM.__true;
	}

	/**
	 * Diese Methode gibt {@code false} als {@link FEMValue} zurück.
	 * 
	 * @return {@code false}.
	 */
	public static final BooleanValue falseValue() {
		return FEM.__false;
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final ArrayValue arrayValue(final FEMArray data) throws NullPointerException {
		if (data.__length == 0) return FEM.__emptyArray;
		return new ArrayValue(data);
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final ArrayValue arrayValue(final FEMValue... data) throws NullPointerException {
		if (data.length == 0) return FEM.__emptyArray;
		return FEM.arrayValue(FEMArray.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link FEMValue} zurück.
	 * 
	 * @param data Wertliste.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final ArrayValue arrayValue(final Iterable<? extends FEMValue> data) throws NullPointerException {
		return FEM.arrayValue(FEMArray.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link FEMValue} zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final StringValue stringValue(final char[] data) throws NullPointerException {
		if (data.length == 0) return FEM.__emptyString;
		return FEM.stringValue(FEMString.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link FEMValue} zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final StringValue stringValue(final String data) throws NullPointerException {
		if (data.length() == 0) return FEM.__emptyString;
		return FEM.stringValue(FEMString.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link FEMValue} zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final StringValue stringValue(final FEMString data) throws NullPointerException {
		if (data.__length == 0) return FEM.__emptyString;
		return new StringValue(data);
	}

	/**
	 * Diese Methode gibt die gegebene Bytefolge als {@link FEMValue} zurück.
	 * 
	 * @param data Bytefolge.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final BinaryValue binaryValue(final byte[] data) throws NullPointerException {
		if (data.length == 0) return FEM.__emptyBinary;
		return FEM.binaryValue(FEMBinary.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Bytefolge als {@link FEMValue} zurück.
	 * 
	 * @param data Bytefolge.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final BinaryValue binaryValue(final FEMBinary data) throws NullPointerException {
		if (data.__length == 0) return FEM.__emptyBinary;
		return new BinaryValue(data);
	}

	/**
	 * Diese Methode gibt die gegebene Dezimalzahl als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalzahl.
	 * @return Wert.
	 */
	public static final IntegerValue integerValue(final long data) {
		return FEM.integerValue(FEMInteger.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Dezimalzahl als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalzahl.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final IntegerValue integerValue(final Number data) throws NullPointerException {
		return FEM.integerValue(FEMInteger.from(data));
	}

	/**
	 * Diese Methode gibt die gegebene Dezimalzahl als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalzahl.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final IntegerValue integerValue(final FEMInteger data) throws NullPointerException {
		return new IntegerValue(data);
	}

	/**
	 * Diese Methode gibt den gegebenen Dezimalbruch als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalbruch.
	 * @return Wert.
	 */
	public static final DecimalValue decimalValue(final double data) {
		return FEM.decimalValue(FEMDecimal.from(data));
	}

	/**
	 * Diese Methode gibt den gegebenen Dezimalbruch als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalbruch.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final DecimalValue decimalValue(final Number data) throws NullPointerException {
		return FEM.decimalValue(FEMDecimal.from(data));
	}

	/**
	 * Diese Methode gibt den gegebenen Dezimalbruch als {@link FEMValue} zurück.
	 * 
	 * @param data Dezimalbruch.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final DecimalValue decimalValue(final FEMDecimal data) throws NullPointerException {
		return new DecimalValue(data);
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link FEMValue} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return Wert.
	 */
	public static final BooleanValue booleanValue(final boolean data) {
		return (data ? FEM.__true : FEM.__false);
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link FEMValue} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final BooleanValue booleanValue(final Boolean data) throws NullPointerException {
		return FEM.booleanValue(data.booleanValue());
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link FEMValue} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final BooleanValue booleanValue(final FEMBoolean data) throws NullPointerException {
		return FEM.booleanValue(data.__value);
	}

	/**
	 * Diese Methode gibt die gegebenen Zeitspanne als {@link FEMValue} zurück.
	 * 
	 * @param data Zeitspanne.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final DurationValue durationValue(final FEMDuration data) throws NullPointerException {
		return new DurationValue(data);
	}

	/**
	 * Diese Methode gibt die gegebenen Zeitangabe als {@link FEMValue} zurück.
	 * 
	 * @param data Zeitangabe.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final DatetimeValue datetimeValue(final Calendar data) throws NullPointerException {
		return FEM.datetimeValue(FEMDatetime.from(data));
	}

	/**
	 * Diese Methode gibt die gegebenen Zeitangabe als {@link FEMValue} zurück.
	 * 
	 * @param data Zeitangabe.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final DatetimeValue datetimeValue(final FEMDatetime data) throws NullPointerException {
		return new DatetimeValue(data);
	}

	/**
	 * Diese Methode gibt die gegebene Funktion als {@link FEMValue} zurück.
	 * 
	 * @param data Funktion.
	 * @return Wert.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static final FunctionValue functionValue(final FEMFunction data) throws NullPointerException {
		return new FunctionValue(data);
	}

	{}

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

	public static void main(final String[] args) throws Exception {
		final FEMScript script = FEM.scriptParser().useSource("lala{ /r/   A /24/ ; /234/  B   ; /asdasd/ /234/   C:and(or(1;false;/er/<a123<<;:DS<<E>>R>); 'dada''sdasd')}").parseScript();
		System.out.println(script);
		System.out.println(FEM.scriptFormatter().formatData((Object)script));
		// System.out.println(FEM.scriptCompiler().useScript(script).compileProxies()[0].function());

	}

	public static String parseValue(final String source) {
		return FEM.scriptParser().useSource(source).parseValue();
	}

	public static String parseString(final String source) {
		return FEM.scriptParser().useSource(source).parseString();
	}

	public static String parseComment(final String source) {
		return FEM.scriptParser().useSource(source).parseComment();
	}

	public static String formatValue(final String source) {
		return FEM.scriptParser().useSource(source).formatValue();
	}

	public static String formatString(final String source) {
		return FEM.scriptParser().useSource(source).formatString();
	}

	public static String formatComment(final String source) {
		return FEM.scriptParser().useSource(source).formatComment();
	}

}
