package bee.creative.fem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Natives;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

/** FEM - Function Evaluation Model
 * <p>
 * Diese Klasse implementiert grundlegende {@link FEMValue Werte} und {@link FEMFunction Funktionen} sowie {@link ScriptParser Parser}, {@link ScriptFormatter
 * Formatter} und {@link ScriptCompiler Compiler} für {@link FEMScript Queltexte}.
 * 
 * @see ScriptParser
 * @see ScriptFormatter
 * @see ScriptCompiler
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEM {

	/** Diese Klasse implementiert einen abstrakten Wert als {@link FEMFunction} und {@link ScriptFormatterInput}.<br>
	 * Die {@link #invoke(FEMFrame)}-Methode liefert {@code this}, sodass instanzen dieser Klassen das Einpacken in eine {@link ValueFunction} nicht benötigen.<br>
	 * Die {@link #toString() Textdarstellung} des Werts wird über {@link #toScript(ScriptFormatter)} ermittelt. Diese Methode delegiert selbst an
	 * {@link #toString()}, sodass mindestens eine dieser Methoden überschrieben werden muss.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BaseValue implements FEMValue, FEMFunction, ScriptFormatterInput {

		/** Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextfrei konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht {@code FEMContext.DEFAULT().dataFrom(this, type)}.
		 * 
		 * @see FEMContext#DEFAULT()
		 * @see FEMContext#dataFrom(FEMValue, FEMType)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können. */
		public final <GData> GData data(final FEMType<GData> type) throws NullPointerException, IllegalArgumentException {
			return FEMContext._default_.dataFrom(this, type);
		}

		/** Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht {@code context.dataFrom(this, type)}.
		 * 
		 * @see FEMContext#dataFrom(FEMValue, FEMType)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @param context Kontext.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können. */

		public final <GData> GData data(final FEMType<GData> type, final FEMContext context) throws NullPointerException, ClassCastException,
			IllegalArgumentException {
			return context.dataFrom(this, type);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return Objects.hash(this.data());
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FEMValue)) return false;
			final FEMValue that = (FEMValue)object;
			return Objects.equals(this.type(), that.type()) && Objects.equals(this.data(), that.data());
		}

		/** {@inheritDoc} */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.toString());
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return FEM.scriptFormatter().formatValue(this);
		}

	}

	/** Diese Klasse implementiert eine abstakte Funktion als {@link ScriptFormatterInput}.<br>
	 * Die {@link #toString() Textdarstellung} der Funktion wird über {@link #toScript(ScriptFormatter)} ermittelt.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BaseFunction implements FEMFunction, ScriptFormatterInput {

		/** Diese Methode gibt diese Funktion als Wert ({@link FEMHandler}) zurück.
		 * 
		 * @return {@code this} als Wert. */
		public final FEMHandler toValue() {
			return new FEMHandler(this);
		}

		/** Diese Methode gibt eine neue {@link InvokeFunction} zurück, welche diese Funktion mit den gegebenen Parameterfunktionen aufruft.
		 * 
		 * @see InvokeFunction
		 * @see FEMFrame#withParams(FEMFunction[])
		 * @param params Parameterfunktionen.
		 * @return {@link InvokeFunction}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist. */
		public final InvokeFunction withParams(final FEMFunction... params) throws NullPointerException {
			return InvokeFunction.from(this, true, params);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.getClass().getName());
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return FEM.scriptFormatter().formatFunction(this);
		}

	}

	/** Diese Klasse implementiert den benannten Platzhalter einer Funktione, dessen {@link #invoke(FEMFrame)}-Methoden an eine {@link #set(FEMFunction) gegebene
	 * Funktion} delegiert.
	 * 
	 * @see ScriptCompiler#proxy(String)
	 * @see ScriptCompiler#proxies()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ProxyFunction extends BaseFunction {

		/** Diese Methode gibt eine neue {@link ProxyFunction} mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name.
		 * @return {@link ProxyFunction}.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
		public static final ProxyFunction from(final String name) throws NullPointerException {
			return new ProxyFunction(name);
		}

		{}

		/** Dieses Feld speichert den Namen. */
		final String _name_;

		/** Dieses Feld speichert die Funktion. */
		FEMFunction _function_;

		/** Dieser Konstruktor initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
		public ProxyFunction(final String name) throws NullPointerException {
			if (name == null) throw new NullPointerException("name = null");
			this._name_ = name;
		}

		{}

		/** Diese Methode setzt die in {@link #invoke(FEMFrame)} aufzurufende Funktion.
		 * 
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
		public final void set(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this._function_ = function;
		}

		/** Diese Methode gibt den Namen.
		 * 
		 * @return Name. */
		public final String name() {
			return this._name_;
		}

		/** Diese Methode gibt die Funktion zurück, die in {@link #invoke(FEMFrame)} aufgerufen wird.<br>
		 * Diese ist {@code null}, wenn {@link #set(FEMFunction)} noch nicht aufgerufen wurde.
		 * 
		 * @return Funktion oder {@code null}. */
		public final FEMFunction function() {
			return this._function_;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this._function_.invoke(frame);
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(FEM.formatValue(this._name_));
		}

	}

	/** Diese Klasse implementiert eine Funktion zur Verfolgung bzw. Überwachung der Verarbeitung von Funktionen mit Hilfe eines {@link ScriptTracer}.<br>
	 * Die genaue Beschreibung der Verarbeitung kann bei der Methode {@link #invoke(FEMFrame)} nachgelesen werden.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see ScriptTracer */
	public static final class TraceFunction extends BaseFunction implements ScriptTracerInput {

		/** Diese Methode gibt eine neue {@link TraceFunction} mit den gegebenen Parametern zurück.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @param function Funktion.
		 * @return {@link TraceFunction}.
		 * @throws NullPointerException Wenn {@code tracer} bzw. {@code function} {@code null} ist. */
		public static final TraceFunction from(final ScriptTracer tracer, final FEMFunction function) throws NullPointerException {
			return new TraceFunction(tracer, function);
		}

		{}

		/** Dieses Feld speichert den {@link ScriptTracer}. */
		final ScriptTracer _tracer_;

		/** Dieses Feld speichert die aufzurufende Funktion. */
		final FEMFunction _function_;

		/** Dieser Konstruktor initialisiert Funktion und {@link ScriptTracer}.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code tracer} bzw. {@code function} {@code null} ist. */
		public TraceFunction(final ScriptTracer tracer, final FEMFunction function) throws NullPointerException {
			if (tracer == null) throw new NullPointerException("tracer = null");
			if (function == null) throw new NullPointerException("function = null");
			this._tracer_ = tracer;
			this._function_ = function;
		}

		{}

		/** Diese Methode gibt den {@link ScriptTracer} zurück, dessen Zustand in {@link #invoke(FEMFrame)} modifiziert wird.
		 * 
		 * @return {@link ScriptTracer}. */
		public final ScriptTracer tracer() {
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
		 * {@link ScriptTracerHelper#onExecute(ScriptTracer) tracer().helper().onExecute(tracer())} aufgerufen.<br>
		 * Anschließend wird die {@link ScriptTracer#getFunction() aktuelle Funktion} des {@link #tracer()} mit seinem {@link ScriptTracer#getFrame() aktuellen
		 * Stapelrahmen} ausgewertet und das Ergebnis im {@link #tracer()} {@link ScriptTracer#useResult(FEMValue) gespeichert}.<br>
		 * Abschließend werden dann {@link ScriptTracerHelper#onReturn(ScriptTracer) tracer().helper().onReturn(tracer())} aufgerufen und der
		 * {@link ScriptTracer#getResult() aktuelle Ergebniswert} zurück gegeben.<br>
		 * Wenn eine {@link RuntimeException} auftritt, wird diese im {@link #tracer()} {@link ScriptTracer#useException(RuntimeException) gespeichert}, wird
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer) tracer().helper().onThrow(tracer())} aufgerufen und die {@link ScriptTracer#getException() altuelle
		 * Ausnahme} des {@link #tracer()} ausgelöst.<br>
		 * In jedem Fall wird der Zustand des {@link #tracer()} beim Verlassen dieser Methode {@link ScriptTracer#clear() bereinigt}.<br>
		 * Der verwendete {@link ScriptTracerHelper} wird nur einmalig zu Beginn der Auswertung über den {@link #tracer()} ermittelt. */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			final ScriptTracer tracer = this._tracer_;
			try {
				final ScriptTracerHelper helper = tracer.getHelper();
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
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this._tracer_.equals(tracer)) return this;
			return tracer.trace(this._function_);
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_);
		}

	}

	/** Diese Klasse implementiert eine Funktion, welche stats den gleichen {@link #value() gegebenen Ergebniswert} liefert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ValueFunction extends BaseFunction implements ScriptTracerInput {

		/** Diese Methode gibt den gegebenen Wert als {@link ValueFunction} zurück.<br>
		 * Wenn dieser ein {@link BaseValue} ist, wird der unverändert zurück gegeben.
		 * 
		 * @param value Wert.
		 * @return {@link FEMFunction}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public static final FEMFunction from(final FEMValue value) throws NullPointerException {
			if (value instanceof BaseValue) return (FEMFunction)value;
			return new ValueFunction(value);
		}

		{}

		/** Dieses Feld speichert den Ergebniswert. */
		final FEMValue _value_;

		/** Dieser Konstruktor initialisiert den Ergebniswert.
		 * 
		 * @param value Ergebniswert.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public ValueFunction(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._value_ = value;
		}

		{}

		/** Diese Methode gibt den Ergebniswert zurück.
		 * 
		 * @return Ergebniswert. */
		public final FEMValue value() {
			return this._value_;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return this._value_;
		}

		/** {@inheritDoc} */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this._value_ instanceof ScriptTracerInput) return ((ScriptTracerInput)this._value_).toTrace(tracer);
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putValue(this._value_);
		}

	}

	/** Diese Klasse implementiert eine Funktion mit {@code call-by-reference}-Semantik, deren Ergebniswert ein {@link FEMResult} ist.
	 * 
	 * @see FEMResult
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class VirtualFunction extends BaseFunction {

		/** Diese Methode gibt die gegebene Funktion als {@link VirtualFunction} zurück.<br>
		 * Wenn diese bereits eine {@link VirtualFunction} ist, wird sie unverändert zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return {@link VirtualFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
		public static final VirtualFunction from(final FEMFunction function) throws NullPointerException {
			if (function instanceof VirtualFunction) return (VirtualFunction)function;
			return new VirtualFunction(function);
		}

		{}

		/** Dieses Feld speichert die auszuwertende Funktion. */
		final FEMFunction _function_;

		/** Dieser Konstruktor initialisiert die auszuwertende Funktion, die in {@link #invoke(FEMFrame)} zur Erzeugung eines {@link FEMResult} genutzt wird.
		 * 
		 * @param function auszuwertende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
		public VirtualFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this._function_ = function;
		}

		{}

		/** Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion. */
		public final FEMFunction function() {
			return this._function_;
		}

		{}

		/** {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code FEMResult.from(frame, this.function())}.
		 * 
		 * @see #function()
		 * @see FEMResult#from(FEMFrame, FEMFunction) */
		@Override
		public final FEMResult invoke(final FEMFrame frame) {
			return FEMResult.from(frame, this._function_);
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_);
		}

	}

	/** Diese Klasse stellt {@link FEMFunction Funktionen} zum Lesen und Schreiben von {@link Field nativen Datenfeldern} sowie zum Aufrufen von {@link Method
	 * nativen Methoden} und {@link Constructor nativen Konstruktoren} bereit.
	 * 
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class NativeFunction extends BaseFunction {

		@SuppressWarnings ("javadoc")
		static abstract class FromField extends NativeFunction {

			@Override
			public abstract Field member();

			@Override
			public boolean isField() {
				return true;
			}

			@Override
			public String toString() {
				return Natives.formatField(this.member());
			}

		}

		@SuppressWarnings ("javadoc")
		static abstract class FromMethod extends NativeFunction {

			@Override
			public abstract Method member();

			@Override
			public boolean isMethod() {
				return true;
			}

			@Override
			public String toString() {
				return Natives.formatMethod(this.member());
			}

		}

		@SuppressWarnings ("javadoc")
		static abstract class FromConstructor extends NativeFunction {

			@Override
			public abstract Constructor<?> member();

			@Override
			public boolean isStatic() {
				return true;
			}

			@Override
			public boolean isConstructor() {
				return true;
			}

			@Override
			public String toString() {
				return Natives.formatConstructor(this.member());
			}

		}

		{}

		/** Diese Methode gibt die native Funktion zur gegebenen Eingabe zurück.<br>
		 * Die Eingabe kann hierbei eine Funktion kodieren, die eine Klasse liefert, an eine Methode bzw. einen Konstruktor delegiert oder ein Datenfeld liest bzw.
		 * schreibt.
		 * <p>
		 * <h4>{@code "CLASS_PATH.class"}</h4> Dieser Pfad ergibt {@link FEMNative#from(Object) FEMNative.from(CLASS_PATH.class)}.
		 * <p>
		 * <h4>{@code "CLASS_PATH.FIELD_NAME"}</h4> Dieser Pfad ergibt {@link #fromField(Field) fromField(CLASS_PATH.class.getDeclaredField("FIELD_NAME"))}.
		 * <p>
		 * <h4>{@code "CLASS_PATH.new(TYPE_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #fromConstructor(Constructor)
		 * fromConstructor(CLASS_PATH.class.getDeclaredConstructor(TYPE_1.class, ..., TYPE_N.class))}.
		 * <p>
		 * <h4>{@code "CLASS_PATH.METHOD_NAME(TYPE1_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #fromMethod(Method)
		 * fromMethod(CLASS_PATH.class.getDeclaredMethod("METHOD_NAME", TYPE_1.class, ..., TYPE_N.class))}.
		 * <p>
		 * 
		 * @see #fromField(Field)
		 * @see #fromMethod(Method)
		 * @see #fromConstructor(Constructor)
		 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktord oder eines Datenfelds.
		 * @return {@link NativeFunction}.
		 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
		 * @throws IllegalArgumentException Wenn {@link Natives#parseMethod(String)} oder {@link Natives#parseConstructor(String)} eine entsprechende Ausnahme
		 *         auslöst.
		 * @throws ReflectiveOperationException Wenn {@link Natives#parseField(String)}, {@link Natives#parseMethod(String)},
		 *         {@link Natives#parseConstructor(String)} oder {@link Natives#parseClass(String)} eine entsprechende Ausnahme auslöst. */
		public static final FEMFunction from(final String memberPath) throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
			if (memberPath.endsWith(".class")) return FEMNative.from(Natives.parseClass(memberPath.substring(0, memberPath.length() - 6)));
			if (memberPath.contains(".new(")) return NativeFunction.fromConstructor(Natives.parseConstructor(memberPath));
			if (memberPath.contains("(")) return FEMNative.from(Natives.parseMethod(memberPath));
			return NativeFunction.fromField(Natives.parseField(memberPath));
		}

		/** Diese Methode gibt eine Funktion zurück, mit welcher der Wert des gegebenen Datenfelds gelesen sowie geschrieben werden kann.<br>
		 * Wenn das gegebene Datenfeld {@code static} ist, muss die gelieferte Funktion es zum Lesen ohne Parameter und zum Schreiben mit dem Wert als Parameter
		 * aufgerufen werden. Andernfalls muss die gelieferte Funktion es zum Lesen mit dem Objekt und zum Schreiben mit dem Objekt und dem Wert als Parameter
		 * aufgerufen werden. Die gelieferte Funktion liefert beim Schreiben stets {@link FEMNative#NULL}.
		 * 
		 * @param field Datenfeld.
		 * @return Funktion zum gegebenen Datenfeld.
		 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
		public static final NativeFunction fromField(final Field field) throws NullPointerException {
			return Modifier.isStatic(field.getModifiers()) ? NativeFunction._fromStaticField_(field) : NativeFunction._fromObjectField_(field);
		}

		@SuppressWarnings ("javadoc")
		static final NativeFunction _fromStaticField_(final Field staticField) {
			return new FromField() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						switch (frame.size()) {
							case 0:
								final Object getValue = staticField.get(null);
								return FEMNative.from(getValue);
							case 1:
								final Object setValue = frame.get(0).data();
								staticField.set(null, setValue);
								return FEMNative.NULL;
						}
						throw new IllegalArgumentException();
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Field member() {
					return staticField;
				}

				@Override
				public boolean isStatic() {
					return true;
				}

			};
		}

		@SuppressWarnings ("javadoc")
		static final NativeFunction _fromObjectField_(final Field objectField) {
			return new FromField() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						switch (frame.size()) {
							case 1:
								final Object getInput = frame.get(0).data();
								final Object getValue = objectField.get(getInput);
								return FEMNative.from(getValue);
							case 2:
								final Object setInput = frame.get(0).data();
								final Object setValue = frame.get(1).data();
								objectField.set(setInput, setValue);
								return FEMNative.NULL;
						}
						throw new IllegalArgumentException();
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Field member() {
					return objectField;
				}

				@Override
				public boolean isStatic() {
					return false;
				}

			};
		}

		/** Diese Methode gibt eine Funktion zurück, die an die gegebene Methode delegiert.
		 * 
		 * @param method Methode.
		 * @return Funktion zur gegebenen Methode.
		 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
		public static final NativeFunction fromMethod(final Method method) throws NullPointerException {
			return Modifier.isStatic(method.getModifiers()) ? NativeFunction._fromStaticMethod_(method) : NativeFunction._fromObjectMethod_(method);
		}

		/** Diese Methode gibt eine Funktion zurück, die an den gegebenen Konstruktor delegiert.
		 * 
		 * @param constructor Konstruktor.
		 * @return Funktion zum gegebenen Konstruktor.
		 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
		public static final NativeFunction fromConstructor(final Constructor<?> constructor) throws NullPointerException {
			if (constructor == null) throw new NullPointerException("constructor = null");
			return NativeFunction._fromConstructor_(constructor);
		}

		@SuppressWarnings ("javadoc")
		static final NativeFunction _fromConstructor_(final Constructor<?> staticConstructor) {
			return new FromConstructor() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						final Object[] params = NativeFunction._paramsFrom_(frame, false);
						final Object result = staticConstructor.newInstance(params);
						return FEMNative.from(result);
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Constructor<?> member() {
					return staticConstructor;
				}

				@Override
				public boolean isStatic() {
					return true;
				}

			};
		}

		@SuppressWarnings ("javadoc")
		static final NativeFunction _fromStaticMethod_(final Method staticMethod) {
			return new FromMethod() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						final Object[] params = NativeFunction._paramsFrom_(frame, false);
						final Object result = staticMethod.invoke(null, params);
						return FEMNative.from(result);
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Method member() {
					return staticMethod;
				}

				@Override
				public boolean isStatic() {
					return true;
				}

			};
		}

		@SuppressWarnings ("javadoc")
		static final NativeFunction _fromObjectMethod_(final Method objectMethod) {
			return new FromMethod() {

				@Override
				public FEMValue invoke(final FEMFrame frame) {
					try {
						final Object[] params = NativeFunction._paramsFrom_(frame, true);
						final Object input = frame.get(0).data();
						final Object result = objectMethod.invoke(input, params);
						return FEMNative.from(result);
					} catch (final Exception cause) {
						throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
					}
				}

				@Override
				public Method member() {
					return objectMethod;
				}

				@Override
				public boolean isStatic() {
					return false;
				}

			};
		}

		@SuppressWarnings ("javadoc")
		static final Object[] _paramsFrom_(final FEMFrame frame, final boolean skipFirst) {
			final int offset = skipFirst ? 1 : 0, length = frame.size() - offset;
			final Object[] result = new Object[length];
			for (int i = 0; i < length; i++) {
				result[i] = frame.get(i + offset).data();
			}
			return result;
		}

		{}

		/** Diese Methode gibt den {@link Member} zurück, auf den sich die Methode {@link #invoke(FEMFrame)} bezieht.
		 * 
		 * @return {@link Member}. */
		public abstract Member member();

		/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} {@code static} ist.
		 * 
		 * @return Bezugskennzeichnung. */
		public abstract boolean isStatic();

		/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Field} ist.
		 * 
		 * @return Feldkennzeichnung. */
		public boolean isField() {
			return false;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} eine {@link Method} ist.
		 * 
		 * @return Methodenkennzeichung. */
		public boolean isMethod() {
			return false;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Constructor} ist.
		 * 
		 * @return Konstructorkennzeichung. */
		public boolean isConstructor() {
			return false;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.toString());
		}

	}

	/** Diese Klasse implementiert eine projizierende Funktion, deren Ergebniswert einem der Parameterwerte der Stapelrahmen entspricht.
	 * 
	 * @see #index()
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ParamFunction extends BaseFunction {

		/** Dieses Feld speichert die projezierenden Funktionen für die Indizes {@code 0..9}. */
		static final ParamFunction[] _cache_ = {new ParamFunction(0), new ParamFunction(1), new ParamFunction(2), new ParamFunction(3), new ParamFunction(4),
			new ParamFunction(5), new ParamFunction(6), new ParamFunction(7), new ParamFunction(8), new ParamFunction(9)};

		{}

		/** Diese Methode gibt eine Funktion zurück, welche den {@code index}-ten Parameterwert des Stapelrahmens als Ergebniswert liefert.
		 * 
		 * @param index Index des Parameterwerts.
		 * @return {@link ParamFunction}.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist. */
		public static final ParamFunction from(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			if (index < ParamFunction._cache_.length) return ParamFunction._cache_[index];
			return new ParamFunction(index);
		}

		{}

		/** Dieses Feld speichert den Index des Parameterwerts. */
		final int _index_;

		/** Dieser Konstruktor initialisiert den Index des Parameterwerts.
		 * 
		 * @param index Index des Parameterwerts.
		 * @throws IndexOutOfBoundsException Wenn {@code index < 0} ist. */
		public ParamFunction(final int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException("index < 0");
			this._index_ = index;
		}

		{}

		/** Diese Methode gibt den Index des Parameterwerts zurück.
		 * 
		 * @return Index des Parameterwerts.
		 * @see #invoke(FEMFrame) */
		public final int index() {
			return this._index_;
		}

		{}

		/** {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht {@code frame.get(this.index())}.
		 * 
		 * @see #index() */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			return frame.get(this._index_);
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$").put(new Integer(this._index_ + 1));
		}

	}

	/** Diese Klasse implementiert eine Funktion, die den Aufruf einer gegebenen Funktion mit den Ergebniswerten mehrerer gegebener Parameterfunktionen berechnet.
	 * 
	 * @see #invoke(FEMFrame)
	 * @see FEMFrame#withParams(FEMFunction[])
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class InvokeFunction extends BaseFunction implements ScriptTracerInput {

		/** Diese Methode gibt eine neue {@link InvokeFunction} mit den gegebenen Parametern zurück.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
		 *        wenn die aufzurufende Funktion mit den Stapelrahmen zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
		 *        Parameterfunktionen ausgewertet werden soll.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @return {@link InvokeFunction}.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist. */
		public static final InvokeFunction from(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
			return new InvokeFunction(function, direct, params);
		}

		{}

		/** Dieses Feld speichert {@code true}, wenn die Verkettung aktiviert ist. */
		final boolean _direct_;

		/** Dieses Feld speichert die aufzurufende Funktion. */
		final FEMFunction _function_;

		/** Dieses Feld speichert die Parameterfunktionen, deren Ergebniswerte als Parameterwerte verwendet werden sollen. */
		final FEMFunction[] _params_;

		/** Dieser Konstruktor initialisiert die aufzurufende Funktion, die Verketung und die Parameterfunktionen.
		 * 
		 * @param function aufzurufende Funktion.
		 * @param direct {@code true}, wenn die aufzurufende Funktion direkt mit den Ergebnissen der Parameterfunktionen ausgewertet werden soll, und {@code false},
		 *        wenn die aufzurufende Funktion mit den Stapelrahmen zu einer Funktion ausgewertet werden soll, welche dann mit den Ergebnissen der
		 *        Parameterfunktionen ausgewertet werden soll.
		 * @param params Parameterfunktionen, deren Ergebniswerte als Parameterwerte beim Aufruf der Funktion verwendet werden sollen.
		 * @throws NullPointerException Wenn {@code function} bzw. {@code params} {@code null} ist. */
		public InvokeFunction(final FEMFunction function, final boolean direct, final FEMFunction... params) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (params == null) throw new NullPointerException("params = null");
			this._direct_ = direct;
			this._function_ = function;
			this._params_ = params;
		}

		{}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #function() aufzurufende Funktion} direkt mit den Ergebnissen der {@link #params()
		 * Parameterfunktionen} aufgerufen wird. Andernfalls wird die aufzurufende Funktion mit den in {@link #invoke(FEMFrame)} gegebenen Stapelrahmen zu einer
		 * Funktion ausgewertet, welche dann mit den Ergebnissen der Parameterfunktionen aufgerufen wird.
		 * 
		 * @return Verkettung.
		 * @see #invoke(FEMFrame) */
		public final boolean direct() {
			return this._direct_;
		}

		/** Diese Methode gibt eine Kopie der Parameterfunktionen zurück.
		 * 
		 * @return Kopie der Parameterfunktionen.
		 * @see #invoke(FEMFrame) */
		public final FEMFunction[] params() {
			return this._params_.clone();
		}

		/** Diese Methode gibt die aufzurufende Funktion zurück.
		 * 
		 * @return aufzurufende Funktion.
		 * @see #invoke(FEMFrame) */
		public final FEMFunction function() {
			return this._function_;
		}

		/** Diese Methode gibt eine zu dieser Funktion gleichwertige {@link InvokeFunction} zurück, bei welcher {@link #function()} und jede Parameterfunktion in
		 * {@link #params()} in eine {@link VirtualFunction} konvertiert wurde.
		 * 
		 * @see VirtualFunction#from(FEMFunction)
		 * @return neue {@link InvokeFunction} Funktion mit Parameterfunktionen, die {@link VirtualFunction} sind. */
		public final InvokeFunction toVirtual() {
			final FEMFunction[] functions = this._params_.clone();
			for (int i = 0, size = functions.length; i < size; i++) {
				functions[i] = VirtualFunction.from(functions[i]);
			}
			return InvokeFunction.from(VirtualFunction.from(this._function_), this._direct_, functions);
		}

		{}

		/** {@inheritDoc}
		 * <p>
		 * Der Ergebniswert entspricht
		 * {@code (this.direct() ? this.function() : frame.context().dataFrom(this.function().invoke(frame), FUNCTION_TYPE)).invoke(frame.newFrame(this.params()))}.
		 * 
		 * @see #direct()
		 * @see #params()
		 * @see #function()
		 * @see FEMFrame#newFrame(FEMFunction...) */
		@Override
		public final FEMValue invoke(FEMFrame frame) {
			final FEMFunction function;
			if (this._direct_) {
				function = this._function_;
			} else {
				final FEMValue value = this._function_.invoke(frame);
				if (value == null) throw new NullPointerException("this.function().invoke(frame) = null");
				function = FEMHandler.from(value, frame._context_).value();
			}
			frame = frame.newFrame(this._params_);
			final FEMValue result = function.invoke(frame);
			if (result == null) throw new NullPointerException("function.invoke(frame.newFrame(this.params()) = null");
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			final FEMFunction[] params = this._params_;
			for (int i = 0, size = params.length; i < size; i++) {
				params[i] = tracer.trace(params[i]);
			}
			return InvokeFunction.from(tracer.trace(this._function_), this._direct_, params);
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this._function_).putParams(Arrays.asList(this._params_));
		}

	}

	/** Diese Klasse implementiert eine Funktion, welche die zusätzlichen Parameterwerte von Stapelrahmen an eine gegebene Funktion bindet und diese gebundene
	 * Funktion anschließend als {@link FEMHandler} liefert.
	 * 
	 * @see #invoke(FEMFrame)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ClosureFunction extends BaseFunction implements ScriptTracerInput {

		/** Diese Methode gibt die gegebene Funktion als {@link ClosureFunction} zurück.<br>
		 * Wenn diese bereits eine {@link ClosureFunction} ist, wird sie unverändert zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return {@link ClosureFunction}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
		public static final ClosureFunction from(final FEMFunction function) throws NullPointerException {
			if (function instanceof ClosureFunction) return (ClosureFunction)function;
			return new ClosureFunction(function);
		}

		{}

		/** Dieses Feld speichert die gebundenen Stapelrahmen, deren zusätzliche Parameterwerte genutzt werden. */
		final FEMFrame _frame_;

		/** Dieses Feld speichert die auszuwertende Funktion. */
		final FEMFunction _function_;

		/** Dieser Konstruktor initialisiert die Funktion, an welchen in {@link #invoke(FEMFrame)} die die zusätzlichen Parameterwerte der Stapelrahmen gebunden
		 * werden.
		 * 
		 * @see #invoke(FEMFrame)
		 * @param function zu bindende Funktion.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist. */
		public ClosureFunction(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			this._frame_ = null;
			this._function_ = function;
		}

		/** Dieser Konstruktor initialisiert die Stapelrahmen sowie die gebundene Funktion und sollte nur von {@link ClosureFunction#invoke(FEMFrame)} genutzt
		 * werden.<br>
		 * Die {@link #invoke(FEMFrame)}-Methode delegiert die zugesicherten Parameterwerte der ihr übergebenen Stapelrahmen zusammen mit den zusätzlichen
		 * Parameterwerten der gebundenen Stapelrahmen an die gegebene Funktion und liefert deren Ergebniswert.
		 * 
		 * @see #invoke(FEMFrame)
		 * @param frame Stapelrahmen mit den zusätzlichen Parameterwerten.
		 * @param function gebundene Funktion.
		 * @throws NullPointerException Wenn {@code frame} bzw. {@code function} {@code null} ist. */
		public ClosureFunction(final FEMFrame frame, final FEMFunction function) throws NullPointerException {
			if (frame == null) throw new NullPointerException("frame = null");
			if (function == null) throw new NullPointerException("function = null");
			this._frame_ = frame;
			this._function_ = function;
		}

		{}

		/** Diese Methode gibt die gebundene Stapelrahmen oder {@code null} zurück.<br>
		 * Die Stapelrahmen sind {@code null}, wenn diese {@link ClosureFunction} über dem Konstruktor {@link #ClosureFunction(FEMFunction)} erzeugt wurde.
		 * 
		 * @see #ClosureFunction(FEMFunction)
		 * @see #ClosureFunction(FEMFrame, FEMFunction)
		 * @see #invoke(FEMFrame)
		 * @return gebundene Stapelrahmen oder {@code null}. */
		public final FEMFrame frame() {
			return this._frame_;
		}

		/** Diese Methode gibt die auszuwertende Funktion zurück.
		 * 
		 * @return auszuwertende Funktion. */
		public final FEMFunction function() {
			return this._function_;
		}

		{}

		/** {@inheritDoc}
		 * <p>
		 * Wenn diese Funktion über {@link #ClosureFunction(FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code functionValue(new ClosureFunction(frame, this.function()))}. Damit werden die gegebenen Stapelrahmen an die Funktion {@link #function()} gebunden
		 * und als {@link FEMHandler} zurück gegeben.
		 * <p>
		 * Wenn sie dagegen über {@link #ClosureFunction(FEMFrame, FEMFunction)} erzeugt wurde, entspricht der Ergebniswert:<br>
		 * {@code this.function().invoke(this.frame().withParams(frame.params()))}. Damit werden die gebundene Funktion mit den zugesicherten Parameterwerten der
		 * gegebenen sowie den zusätzlichen Parameterwerten der gebundenen Stapelrahmen ausgewertet und der so ermittelte Ergebniswert geliefert. */
		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			if (this._frame_ == null) return FEMHandler.from(new ClosureFunction(frame, this._function_));
			return this._function_.invoke(this._frame_.withParams(frame.params()));
		}

		/** {@inheritDoc} */
		@Override
		public final FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException {
			if (this._frame_ == null) return ClosureFunction.from(tracer.trace(this._function_));
			return new ClosureFunction(this._frame_, tracer.trace(this._function_));
		}

		/** {@inheritDoc} */
		@Override
		public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putHandler(this._function_);
		}

	}

	/** Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
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
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptParser extends Parser {

		@SuppressWarnings ("javadoc")
		boolean _active_;

		/** Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}. */
		int _value_;

		/** Dieses Feld speichert die bisher ermittelten Bereiche. */
		final List<Range> _ranges_ = new ArrayList<>();

		{}

		/** Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		synchronized final void _start_() throws IllegalStateException {
			this._check_();
			this._active_ = true;
			this._value_ = -1;
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._active_ = false;
		}

		@SuppressWarnings ("javadoc")
		final void _check_() throws IllegalStateException {
			if (this._active_) throw new IllegalStateException();
		}

		/** Diese Methode fügt eine neue Bereich mit den gegebenen Parametern hinzu, der bei {@link #index()} endet.
		 * 
		 * @param type Typ des Bereichs.
		 * @param start Start des Bereichs. */
		final void _put_(final int type, final int start) {
			this._ranges_.add(new Range((char)type, start, this.index() - start));
		}

		/** Diese Methode beginnt das parsen eines Wertbereichs mit dem Bereichstyp {@code '.'}, welches mit {@link #_closeValue_()} beendet werden muss. */
		final void _openValue_() {
			if (this._value_ >= 0) return;
			this._value_ = this.index();
		}

		/** Diese Methode beendet das einlesen des Wertbereichs mit dem Bereichstyp {@code '.'}. */
		final void _closeValue_() {
			final int start = this._value_;
			if (start < 0) return;
			this._value_ = -1;
			if (this.index() <= start) return;
			this._put_('.', start);
		}

		/** Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher
		 * das Zeichen als Typ verwendet.
		 * 
		 * @param type Zeichen als Bereichstyp. */
		final void _parseMask_(final int type) {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						this._put_(type, start);
						return;
					}
				}
			}
			this._put_('?', start);
		}

		/** Diese Methode parst einen Bereich, der mit dem Zeichen <code>'&lt;'</code> beginnt, mit dem Zeichen <code>'&gt;'</code> ende und in dem diese Zeichen nur
		 * paarweise vorkommen dürfen. ein solcher Bereich geparst werden konnte, ist dessen Bereichstyp {@code '!'}. Wenn eine dieser Regeln verletzt wird, ist der
		 * Bereichstyp {@code '?'}. */
		final void _parseName_() {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '>') {
					if (this.skip() != '>') {
						this._put_('!', start);
						return;
					}
				} else if (symbol == '<') {
					if (this.skip() != '<') {
						break;
					}
				}
			}
			this._put_('?', start);
		}

		/** Diese Methode überspringt alle Zeichen, die kleiner oder gleich dem eerzeichen sind. */
		final void _parseSpace_() {
			final int start = this.index();
			for (int symbol = this.skip(); (symbol >= 0) && (symbol <= ' '); symbol = this.skip()) {}
			this._put_('_', start);
		}

		/** Diese Methode erzeugt zum gegebenen Zeichen einen Bereich der Länge 1 und navigiert zum nächsten Zeichen.
		 * 
		 * @see #skip()
		 * @see #_put_(int, int)
		 * @param type Zeichen als Bereichstyp. */
		final void _parseSymbol_(final int type) {
			final int start = this.index();
			this.skip();
			this._put_(type, start);
		}

		/** Diese Methode parst die {@link #source() Eingabe}. */
		final void _parseSource_() {
			for (int symbol; true;) {
				switch (symbol = this.symbol()) {
					case -1: {
						this._closeValue_();
						return;
					}
					case '\'':
					case '\"':
					case '/': {
						this._closeValue_();
						this._parseMask_(symbol);
						break;
					}
					case '<': {
						this._closeValue_();
						this._parseName_();
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
						this._closeValue_();
						this._parseSymbol_(symbol);
						break;
					}
					default: {
						if (symbol <= ' ') {
							this._closeValue_();
							this._parseSpace_();
						} else {
							this._openValue_();
							this.skip();
						}
					}
				}
			}
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #source() Eingabe} keines der in {@link #_parseSource_()} erkannten Zeichen enthält,
		 * d.h. wenn das Parsen der Eingabe via {@link #parseRanges()} genau einen Bereich mit dem Typ {@code '.'} ergibt, welcher über {@link #_openValue_()} und
		 * {@link #_closeValue_()} entstand.
		 * 
		 * @return {@code true}, wenn die Eingabe nur einen Wert enthält. */
		final boolean _checkSource_() {
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

		/** Diese Methode gibt die in Anführungszeichen eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #_parseMask_(int)
		 * @param type Anführungszeichen.
		 * @return Eingabe mit Maskierung. */
		final String _encodeMask_(final int type) {
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

		/** Diese Methode gibt die in spitze Klammern eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #_parseName_()
		 * @return Eingabe mit Maskierung. */
		final String _encodeValue_() {
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

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.
		 * 
		 * @param type Anführungszeichen.
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		final String _decodeMask_(final int type) throws IllegalArgumentException {
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

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.
		 * 
		 * @see #_parseName_()
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		final String _decodeValue_() throws IllegalArgumentException {
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

		/** Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
		 * 
		 * @param value Eingabe.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptParser useSource(final String value) throws NullPointerException, IllegalStateException {
			this._check_();
			super.source(value);
			return this;
		}

		/** Diese Methode parst die {@link #source() Eingabe} in einen aufbereiteten Quelltext und gibt diesen zurück.
		 * 
		 * @see FEMScript
		 * @see #parseRanges()
		 * @return aufbereiteter Quelltext.
		 * @throws IllegalStateException Wenn aktuell geparst wird. */
		public final FEMScript parseScript() throws IllegalStateException {
			return new FEMScript(this.source(), this.parseRanges());
		}

		/** Diese Methode parst die {@link #source() Eingabe} und gibt die Liste der ermittelten Bereiche zurück.
		 * 
		 * @see Range
		 * @return Bereiche.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final Range[] parseRanges() throws IllegalStateException {
			this._start_();
			try {
				this._ranges_.clear();
				this._parseSource_();
				final Range[] result = this._ranges_.toArray(new Range[this._ranges_.size()]);
				return result;
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit einem der Anführungszeichen {@code '\''} oder {@code '\"'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatString()
		 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseString() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '\'') return this._decodeMask_('\'');
				if (this.symbol() == '\"') return this._decodeMask_('\"');
				throw new IllegalArgumentException();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Schrägstrichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit {@code '/'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatComment()
		 * @return Eingabe ohne Maskierung mit {@code '/'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseComment() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '/') return this._decodeMask_('/');
				throw new IllegalArgumentException();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit <code>'&lt;'</code> bzw. <code>'&gt;'</code> und enthält diese Zeichen nur gedoppelt. Wenn die Eingabe nicht
		 * derart beginnt, wird sie unverändert zurück gegeben.
		 * 
		 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseValue() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '<') return this._decodeValue_();
				return this.source();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in spitze Klammern eingeschlossenen und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.<br>
		 * Wenn die Eingabe keine von diesem Parser besonders behandelten Zeichen enthält, wird sie unverändert zurück gegeben.
		 * 
		 * @see #parseValue()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatValue() throws IllegalStateException {
			this._start_();
			try {
				if (this._checkSource_()) return this.source();
				this.reset();
				return this._encodeValue_();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in {@code '\''} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseString()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatString() throws IllegalStateException {
			this._start_();
			try {
				return this._encodeMask_('\'');
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in {@code '/'} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseComment()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatComment() throws IllegalStateException {
			this._start_();
			try {
				return this._encodeMask_('/');
			} finally {
				this._stop_();
			}
		}

	}

	/** Diese Klasse implementiert einen Kompiler, der {@link FEMScript aufbereitete Quelltexte} in {@link FEMValue Werte} sowie {@link FEMFunction Funktionen}
	 * überführen und diese im Rahmen eines {@link ScriptFormatter} auch formatieren kann.
	 * <p>
	 * Die Bereichestypen der Quelltexte haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) sind bedeutungslos, dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link FEMArray}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link FEMArray} als Funktion bzw. Parameterwert
	 * kompiliert wird.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über {@link ScriptCompilerHelper#compileName(ScriptCompiler)}
	 * aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu
	 * genau einer Funktion kompiliert werden.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.from(0)}). Andernfalls steht der Bereich für {@link FEM#PARAMS_VIEW_FUNCTION}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileParam(ScriptCompiler)} in Parameterfunktionen überführt.</li>
	 * </ul>
	 * <p>
	 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
	 * 
	 * @see #formatScript(ScriptFormatter)
	 * @see #compileValue()
	 * @see #compileFunction()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptCompiler extends Parser {

		@SuppressWarnings ("javadoc")
		boolean _active_;

		/** Dieses Feld speichert die Kompilationsmethoden. */
		ScriptCompilerHelper _helper_ = ScriptCompilerHelper.DEFAULT;

		/** Dieses Feld speichert den Quelltext. */
		FEMScript _script_ = FEMScript.EMPTY;

		/** Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter. */
		final Map<String, ProxyFunction> _proxies_ = Collections.synchronizedMap(new LinkedHashMap<String, ProxyFunction>());

		/** Dieses Feld speichert die Parameternamen. */
		final List<String> _params_ = Collections.synchronizedList(new LinkedList<String>());

		/** Dieses Feld speichert die Zulässigkeit von Wertlisten. */
		boolean _arrayEnabled_ = true;

		/** Dieses Feld speichert die Zulässigkeit von Funktionszeigern. */
		boolean _handlerEnabled_ = true;

		/** Dieses Feld speichert die Zulässigkeit der Bindung des Stapelrahmens. */
		boolean _closureEnabled_ = true;

		/** Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen. */
		boolean _chainingEnabled_ = true;

		/** Dieses Feld speichert den Formatierer. */
		ScriptFormatter _formatter_;

		{}

		/** Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		final synchronized void _start_() throws IllegalStateException {
			this._check_();
			this._active_ = true;
			this._proxies_.clear();
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._active_ = false;
		}

		@SuppressWarnings ("javadoc")
		final void _check_() throws IllegalStateException {
			if (this._active_) throw new IllegalStateException();
		}

		/** Diese Methode formatiert den aktuellen Quelltext als Sequenz von Werten und Stoppzeichen. */
		final void _format_() {
			final ScriptFormatter formatter = this._formatter_;
			while (true) {
				this._formatSequence_(false);
				if (this.symbol() < 0) return;
				formatter.put(this.section()).putBreakSpace();
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Wertliste. */
		final void _formatArray_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("[").putBreakInc();
			this.skip();
			this._formatSequence_(false);
			if (this.symbol() == ']') {
				formatter.putBreakDec().put("]");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Parameterliste. */
		final void _formatParam_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("(").putBreakInc();
			this.skip();
			this._formatSequence_(false);
			if (this.symbol() == ')') {
				formatter.putBreakDec().put(")");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle parametrisierte Funktion. */
		final void _formatFrame_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("{");
			this.skip();
			this._formatSequence_(true);
			if (this.symbol() == ':') {
				formatter.put(": ");
				this.skip();
				this._formatSequence_(false);
			}
			if (this.symbol() == '}') {
				formatter.put("}");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Wertsequenz, die bei einer schließenden Klammer oder Doppelpunkt endet.
		 * 
		 * @param space {@code true}, wenn hinter Kommentaren und Semikola ein Leerzeichen statt eines bedingten Umbruchs eingefügt werden soll. */
		final void _formatSequence_(final boolean space) {
			final ScriptFormatter formatter = this._formatter_;
			int count = 0;
			while (true) {
				switch (this.symbol()) {
					case '_': {
						this.skip();
						break;
					}
					case '/': {
						formatter.put(this.section());
						if (space) {
							formatter.put(" ");
						} else {
							formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case ';': {
						formatter.put(";");
						if (space) {
							formatter.put(" ");
						} else {
							formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case '(': {
						this._formatParam_();
						break;
					}
					case '[': {
						this._formatArray_();
						break;
					}
					case '{': {
						this._formatFrame_();
						break;
					}
					default: {
						formatter.put(this.section());
						this.skip();
						break;
					}
					case ':':
					case ']':
					case '}':
					case ')':
					case -1: {
						if (count < 2) return;
						formatter.putIndent();
						return;
					}
				}
			}
		}

		/** Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den Typ des ersten bedeutsamen Bereichs oder {@code -1}
		 * zurück. Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereichstyp. */
		final int _compileType_() {
			int symbol = this.symbol();
			while ((symbol == '_') || (symbol == '/')) {
				symbol = this.skip();
			}
			return symbol;
		}

		/** Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
		 * 
		 * @param string Zeichenkette.
		 * @return Zahl. */
		final int _compileIndex_(final String string) {
			if ((string == null) || string.isEmpty()) return -1;
			final char symbol = string.charAt(0);
			if ((symbol < '0') || (symbol > '9')) return -1;
			try {
				return Integer.parseInt(string);
			} catch (final NumberFormatException e) {
				return -1;
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link FEMValue}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMValue _compileArrayAsValue_() throws ScriptException {
			if (!this._arrayEnabled_) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			final List<FEMValue> result = new ArrayList<>();
			this.skip();
			if (this._compileType_() == ']') {
				this.skip();
				return FEMArray.EMPTY;
			}
			while (true) {
				final FEMValue value = this._compileParamAsValue_();
				result.add(value);
				switch (this._compileType_()) {
					case ';': {
						this.skip();
						this._compileType_();
						break;
					}
					case ']': {
						this.skip();
						return FEMArray.from(result);
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMFunction} und gibt diesen zurück.
		 * 
		 * @see ValueFunction
		 * @see InvokeFunction
		 * @see FEM#PARAMS_VIEW_FUNCTION
		 * @return Wertliste als {@link FEMFunction}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileArrayAsFunction_() throws ScriptException {
			if (!this._arrayEnabled_) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			this.skip();
			if (this._compileType_() == ']') {
				this.skip();
				return FEMArray.EMPTY;
			}
			final List<FEMFunction> list = new ArrayList<>();
			boolean value = true;
			while (true) {
				final FEMFunction item = this._compileParamAsFunction_();
				list.add(item);
				value = value && (this._functionToValue(item) != null);
				switch (this._compileType_()) {
					case ';': {
						this.skip();
						this._compileType_();
						break;
					}
					case ']': {
						this.skip();
						final int size = list.size();
						if (!value) {
							final FEMFunction result = FEM.PARAMS_VIEW_FUNCTION.withParams(list.toArray(new FEMFunction[size]));
							return result;
						}
						final FEMValue[] values = new FEMValue[size];
						for (int i = 0; i < size; i++) {
							values[i] = list.get(i).invoke(FEMFrame.EMPTY);
						}
						return FEMArray.from(values);
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/** Diese Methode kompiliert via {@code this.helper().compileParam(this, this.section())} die beim aktuellen Bereich beginnende Parameterfunktion und gibt
		 * diese zurück.
		 * 
		 * @see ScriptCompilerHelper#compileParam(ScriptCompiler)
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileParam_() throws ScriptException {
			try {
				final FEMFunction result = this._helper_.compileParam(this);
				if (result == null) throw new ScriptException().useSender(this).useHint(" Parameter erwartet.");
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this);
			}
		}

		/** Diese Methode kompiliert denF beim aktuellen Bereich beginnende Wert und gibt diese zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMValue _compileParamAsValue_() throws ScriptException {
			switch (this._compileType_()) {
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
					return this._compileArrayAsValue_();
				}
				case '{': {
					if (this._closureEnabled_) throw new ScriptException().useSender(this).useHint(" Ungebundene Funktion unzulässig.");
					final FEMFunction retult = this._compileFrame_();
					return FEMHandler.from(retult);
				}
				default: {
					final FEMFunction param = this._compileParam_();
					final FEMValue result = this._functionToValue(param);
					if (result == null) throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
					return result;
				}
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileParamAsFunction_() throws ScriptException {
			FEMFunction result;
			boolean indirect = false;
			switch (this._compileType_()) {
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
					final String name = this._compileName_();
					if (name == null) return FEM.PARAMS_VIEW_FUNCTION;
					int index = this._compileIndex_(name);
					if (index < 0) {
						index = this._params_.indexOf(name);
						if (index < 0) throw new ScriptException().useSender(this).useHint(" Parametername «%s» ist unbekannt.", name);
					} else if (index > 0) {
						index--;
					} else throw new ScriptException().useSender(this).useHint(" Parameterindex «%s» ist unzulässig.", index);
					return ParamFunction.from(index);
				}
				case '{': {
					result = this._compileFrame_();
					if (this._compileType_() != '(') {
						if (this._closureEnabled_) return ClosureFunction.from(result);
						return FEMHandler.from(result);
					}
					if (!this._chainingEnabled_) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
					break;
				}
				case '[': {
					return this._compileArrayAsFunction_();
				}
				default: {
					result = this._compileParam_();
					if (this._compileType_() != '(') {
						if (this._handlerEnabled_) return result;
						if (this._functionToValue(result) != null) return result;
						throw new ScriptException().useSender(this).useHint(" Funktionsverweise sind nicht zulässig.");
					}
				}
			}
			do {
				if (indirect && !this._chainingEnabled_) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
				this.skip(); // '('
				final List<FEMFunction> list = new ArrayList<>();
				while (true) {
					if (this._compileType_() == ')') {
						this.skip();
						result = InvokeFunction.from(result, !indirect, list.toArray(new FEMFunction[list.size()]));
						break;
					}
					final FEMFunction item = this._compileParamAsFunction_();
					list.add(item);
					switch (this._compileType_()) {
						default:
							throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «)» erwartet.");
						case ';':
							this.skip();
						case ')':
					}
				}
				indirect = true;
			} while (this._compileType_() == '(');
			return result;
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
		 * 
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final ProxyFunction _compileProxy_() throws ScriptException {
			final String name = this._compileName_();
			if ((name == null) || (this._compileIndex_(name) >= 0)) throw new ScriptException().useSender(this).useHint(" Funktionsname erwartet.");
			final ProxyFunction result = this.proxy(name);
			switch (this._compileType_()) {
				case '{':
					result.set(this._compileFrame_());
				break;
				case ':':
					this.skip();
					result.set(this._compileParamAsFunction_());
				break;
				default:
					throw new ScriptException().useSender(this).useHint(" Parametrisierter Funktionsaufruf erwartet.");
			}
			return result;
		}

		/** Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
		 * Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0} ist.
		 * 
		 * @return Funktions- oder Parametername oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final String _compileName_() throws ScriptException {
			try {
				switch (this._compileType_()) {
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
				final String result = this._helper_.compileName(this);
				if (result.isEmpty()) throw new IllegalArgumentException();
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this).useHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich (<code>'{'</code>) beginnende, parametrisierte Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileFrame_() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this._compileType_() < 0) throw new ScriptException().useSender(this);
				final String name = this._compileName_();
				if (name != null) {
					if (this._compileIndex_(name) >= 0) throw new ScriptException().useSender(this).useHint(" Parametername erwartet.");
					this._params_.add(count++, name);
				}
				switch (this._compileType_()) {
					case ';': {
						if (name == null) throw new ScriptException().useSender(this).useHint(" Parametername oder Zeichen «:» erwartet.");
						this.skip();
						break;
					}
					case ':': {
						this.skip();
						final FEMFunction result = this._compileParamAsFunction_();
						if (this._compileType_() != '}') throw new ScriptException().useSender(this).useHint(" Zeichen «}» erwartet.");
						this.skip();
						this._params_.subList(0, count).clear();
						return result;
					}
					default: {
						throw new ScriptException().useSender(this);
					}
				}
			}
		}

		/** Diese Methode gibt den Ergebniswert der gegebenen Funktion zurück, sofer diese ein {@link BaseValue} oder eine {@link ValueFunction} ist.<br>
		 * Andernfalls wird {@code null} geliefert.
		 * 
		 * @param function Funktion.
		 * @return Ergebniswert oder {@code null}. */
		final FEMValue _functionToValue(final FEMFunction function) {
			if ((function instanceof BaseValue) || (function instanceof ValueFunction)) return function.invoke(FEMFrame.EMPTY);
			return null;
		}

		/** Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name des Platzhalters.
		 * @return Platzhalterfunktion.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
		public final ProxyFunction proxy(final String name) throws NullPointerException {
			synchronized (this._proxies_) {
				ProxyFunction result = this._proxies_.get(name);
				if (result != null) return result;
				this._proxies_.put(name, result = new ProxyFunction(name));
				return result;
			}
		}

		/** Diese Methode gibt den aktuellen Bereich zurück.
		 * 
		 * @return aktueller Bereich. */
		public final Range range() {
			return this.isParsed() ? Range.EMPTY : this._script_.get(this.index());
		}

		/** Diese Methode gibt den zu kompilierenden Quelltext zurück.
		 * 
		 * @return Quelltext. */
		public final FEMScript script() {
			return this._script_;
		}

		/** Diese Methode gibt die genutzten Kompilationsmethoden zurück.
		 * 
		 * @return Kompilationsmethoden. */
		public final ScriptCompilerHelper helper() {
			return this._helper_;
		}

		/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.<br>
		 * Die gelieferte Abbildung wird vor jeder Kompilation geleert.
		 * 
		 * @return Abbildung von Namen auf Platzhalter. */
		public final Map<String, ProxyFunction> proxies() {
			return this._proxies_;
		}

		/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
		 * 
		 * @return Parameternamen. */
		public final List<String> params() {
			return Collections.unmodifiableList(this._params_);
		}

		/** Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
		 * 
		 * @see Range#extract(String)
		 * @return Aktuelle Zeichenkette. */
		public final String section() {
			return this.range().extract(this._script_.source());
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Wertlisten. */
		public final boolean isArrayEnabled() {
			return this._arrayEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link ScriptCompilerHelper#compileParam(ScriptCompiler)} gelieferten Funktionen als
		 * Funktionszeiger kompiliert werden dürfen (z.B {@code SORT(array; compFun)}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Funktionszeigern. */
		public final boolean isHandlerEnabled() {
			return this._handlerEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link ClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Stapelrahmens. */
		public final boolean isClosureEnabled() {
			return this._closureEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
		 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
		 * 
		 * @see #compileFunction()
		 * @see InvokeFunction#direct()
		 * @see InvokeFunction#invoke(FEMFrame)
		 * @return Zulässigkeit der Verkettung von Funktionen. */
		public final boolean isChainingEnabled() {
			return this._chainingEnabled_;
		}

		/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
		 * 
		 * @param value Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useScript(final FEMScript value) throws NullPointerException, IllegalStateException {
			this._check_();
			this.source(value.types());
			this._script_ = value;
			return this;
		}

		/** Diese Methode setzt die zu nutzenden Kompilationsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Kompilationsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useHelper(final ScriptCompilerHelper value) throws NullPointerException, IllegalStateException {
			if (value == null) throw new NullPointerException("value = null");
			this._check_();
			this._helper_ = value;
			return this;
		}

		/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final ScriptCompiler useParams(final String... value) throws NullPointerException, IllegalStateException {
			return this.useParams(Arrays.asList(value));
		}

		/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useParams(final List<String> value) throws NullPointerException, IllegalStateException {
			if (value.contains(null)) throw new NullPointerException("value.contains(null)");
			this._check_();
			this._params_.clear();
			this._params_.addAll(value);
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit von Wertlisten.
		 * 
		 * @see #isArrayEnabled()
		 * @param value Zulässigkeit von Wertlisten.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useArrayEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._arrayEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit von Funktionszeigern.
		 * 
		 * @see #isHandlerEnabled()
		 * @param value Zulässigkeit von Funktionszeigern.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useHandlerEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._handlerEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit der Bindung des Stapelrahmens.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Stapelrahmens.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useClosureEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._closureEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
		 * 
		 * @see #isChainingEnabled()
		 * @param value Zulässigkeit der Verkettung von Funktionen.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useChainingEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._chainingEnabled_ = value;
			return this;
		}

		/** Diese Methode formatiert den Quelltext im Rahmen des gegebenen Formatierers.
		 * 
		 * @param target Formatierer.
		 * @throws NullPointerException Wenn {@code target} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final void formatScript(final ScriptFormatter target) throws NullPointerException, IllegalStateException {
			this._start_();
			if (target == null) throw new NullPointerException("target = null");
			this._formatter_ = target;
			try {
				this._format_();
			} finally {
				this._formatter_ = null;
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Wert oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMValue compileValue() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return null;
				final FEMValue result = this._compileParamAsValue_();
				if (this._compileType_() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück.<br>
		 * Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert sein. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Wertliste
		 * geliefert.
		 * 
		 * @return Werte.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMValue[] compileValues() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return new FEMValue[0];
				final List<FEMValue> result = new ArrayList<FEMValue>();
				while (true) {
					result.add(this._compileParamAsValue_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new FEMValue[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück.<br>
		 * Die Parameterfunktion müssen durch Bereiche vom Typ {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem
		 * {@link ScriptCompilerHelper#compileName(ScriptCompiler) Namen} und endet dann entweder mit einer in geschweifte Klammern eingeschlossenen parametrisierten
		 * Funktion oder mit einer nach einem Duppelpunkt angegebenen Parameterfunktion. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere
		 * Funktionsliste geliefert. Nach dem Aufruf dieser Methode ist Abbildung {@link #proxies()} entsprechend bestückt.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final ProxyFunction[] compileProxies() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				final List<ProxyFunction> result = new ArrayList<ProxyFunction>();
				if (this._compileType_() < 0) return new ProxyFunction[0];
				while (true) {
					result.add(this._compileProxy_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new ProxyFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Funktion oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMFunction compileFunction() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return null;
				final FEMFunction result = this._compileParamAsFunction_();
				if (this._compileType_() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
		 * separiert sein.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMFunction[] compileFunctions() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return new FEMFunction[0];
				final List<FEMFunction> result = new ArrayList<FEMFunction>();
				while (true) {
					result.add(this._compileParamAsFunction_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new FEMFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this._helper_, this._params_, this._script_, this._proxies_);
		}

	}

	/** Diese Schnittstelle definiert Kompilationsmethoden, die von einem {@link ScriptCompiler Kompiler} zur Übersetzung von Quelltexten in Werte, Funktionen und
	 * Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptCompilerHelper {

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, der in {@link #compileParam(ScriptCompiler)} sofern möglich den Typ {@link FEMNative} mit
		 * Nutzdaten {@code null}, {@code true}, {@code false}, {@link String} und {@link Character} sowie {@link NativeFunction} nutzt und andernfalls einen
		 * {@link ScriptCompiler#proxy(String)} liefert. */
		static ScriptCompilerHelper NATIVE = new ScriptCompilerHelper() {

			@Override
			public String compileName(final ScriptCompiler compiler) throws ScriptException {
				return compiler.section();
			}

			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler) throws ScriptException {
				String section = compiler.section();
				switch (compiler.symbol()) {
					case '"':
						return FEMNative.from(FEM.parseString(section));
					case '\'':
						return FEMNative.from(new Character(FEM.parseString(section).charAt(0)));
					case '!':
						section = FEM.parseValue(section);
					default: {
						if (section.equals("null")) return FEMNative.NULL;
						if (section.equals("true")) return FEMNative.TRUE;
						if (section.equals("false")) return FEMNative.FALSE;
						try {
							return FEMNative.from(new BigDecimal(section));
						} catch (final NumberFormatException cause) {}
						try {
							return NativeFunction.from(FEM.parseValue(section));
						} catch (final Exception cause) {}
						return compiler.proxy(section);
					}
				}
			}

			@Override
			public String toString() {
				return "NATIVE";
			}

		};

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, der in {@link #compileParam(ScriptCompiler)} sofern möglich die Typen {@link FEMVoid},
		 * {@link FEMBoolean}, {@link FEMString} und {@link FEMDecimal} nutzt und andernfalls einen {@link ScriptCompiler#proxy(String)} liefert. */
		static ScriptCompilerHelper DEFAULT = new ScriptCompilerHelper() {

			@Override
			public String compileName(final ScriptCompiler compiler) throws ScriptException {
				return compiler.section();
			}

			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler) throws ScriptException {
				String section = compiler.section();
				switch (compiler.symbol()) {
					case '"':
					case '\'': {
						return FEMString.from(FEM.parseString(section));
					}
					case '!': {
						section = FEM.parseValue(section);
					}
					default: {
						if (section.equalsIgnoreCase("null")) return FEMVoid.INSTANCE;
						if (section.equalsIgnoreCase("true")) return FEMBoolean.TRUE;
						if (section.equalsIgnoreCase("false")) return FEMBoolean.FALSE;
						try {
							return FEMDecimal.from(new BigDecimal(section));
						} catch (final NumberFormatException cause) {}
						return compiler.proxy(section);
					}
				}
			}

			@Override
			public String toString() {
				return "DEFAULT";
			}

		};

		/** Diese Methode gibt den im {@link ScriptCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
		 * 
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Funktions- bzw. Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen). */
		public String compileName(ScriptCompiler compiler) throws ScriptException;

		/** Diese Methode gibt den im {@link ScriptCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebene Parameter als Funktion zurück.<br>
		 * Der Wert des Parameters entspricht hierbei dem Ergebniswert der gelieferten Funktion.<br>
		 * Konstante Parameterwerte können als {@link BaseValue}, {@link ValueFunction} oder {@link ProxyFunction} geliefert werden. Funktion als Parameterwert
		 * können als {@link FEMHandler} geliefert werden.
		 * 
		 * @see ScriptCompiler#proxy(String)
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält. */
		public FEMFunction compileParam(ScriptCompiler compiler) throws ScriptException;

	}

	/** Diese Klasse implementiert einen Formatierer, der Daten, Werten und Funktionen in eine Zeichenkette überführen kann.<br>
	 * Er realisiert damit die entgegengesetzte Operation zur Kombination von {@link ScriptParser} und {@link ScriptCompiler}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptFormatter {

		/** Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
		 * 
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
		static final class Mark {

			/** Dieses Feld speichert das Objekt, dass in {@link #_items_} vor jeder Markierung eingefügt wird. */
			static final Mark EMPTY = new ScriptFormatter.Mark(0, false, false, false);

			{}

			/** Dieses Feld speichert die Eigenschaften dieser Markierung. */
			int _data_;

			/** Dieser Konstruktor initialisiert die Markierung.
			 * 
			 * @param level Einrücktiefe ({@link #level()}).
			 * @param last Endmarkierung ({@link #isLast()}).
			 * @param space Leerzeichen ({@link #isSpace()}).
			 * @param enabled Aktivierung ({@link #isEnabled()}). */
			public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
				this._data_ = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
			}

			{}

			/** Diese Methode gibt die Tiefe der Einrückung zurück.
			 * 
			 * @return Tiefe der Einrückung. */
			public final int level() {
				return this._data_ >> 3;
			}

			/** Diese Methode aktiviert die Einrückung. */
			public final void enable() {
				this._data_ |= 2;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
			 * 
			 * @return {@code true} bei einer Endmarkierung. */
			public final boolean isLast() {
				return (this._data_ & 1) != 0;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
			 * 
			 * @return {@code true} bei einem bedingten Leerzeichen. */
			public final boolean isSpace() {
				return (this._data_ & 4) != 0;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
			 * 
			 * @return Aktivierung. */
			public final boolean isEnabled() {
				return (this._data_ & 2) != 0;
			}

			{}

			/** {@inheritDoc} */
			@Override
			public final String toString() {
				return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
			}

		}

		{}

		/** Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen. */
		final List<Object> _items_ = new ArrayList<Object>();

		/** Dieses Feld speichert den Puffer für {@link #_format_()}. */
		final StringBuilder _string_ = new StringBuilder();

		/** Dieses Feld speichert den Stack der Hierarchieebenen. */
		final LinkedList<Boolean> _indents_ = new LinkedList<Boolean>();

		/** Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "  "}. */
		String _indent_;

		/** Dieses Feld speichert die Formatierungsmethoden. */
		ScriptFormatterHelper _helper_ = ScriptFormatterHelper.EMPTY;

		{}

		/** Diese Methode beginnt das Parsen und sollte nur in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		synchronized final void _start_() throws IllegalStateException {
			this._check_(true);
			this._indents_.addLast(Boolean.FALSE);
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._items_.clear();
			this._string_.setLength(0);
			this._indents_.clear();
		}

		@SuppressWarnings ("javadoc")
		final void _check_(final boolean idling) throws IllegalStateException {
			if (this._indents_.isEmpty() != idling) throw new IllegalStateException();
		}

		/** Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
		 * 
		 * @param object Markierung.
		 * @return {@code this}. */
		final ScriptFormatter _putMark_(final Mark object) {
			this._items_.add(object);
			return this;
		}

		/** Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
		 * angefügt.
		 * 
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakInc() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			indents.addLast(Boolean.FALSE);
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, false, false));
		}

		/** Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
		 * passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde oder aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakDec() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(value, true, false, indents.removeLast().booleanValue()));
		}

		/** Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
		 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakSpace() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
		}

		/** Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
		 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
		 * 
		 * @see #putBreakSpace()
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putIndent() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			if (this._indents_.getLast().booleanValue()) return this;
			final int value = indents.size();
			for (int i = 0; i < value; i++) {
				indents.set(i, Boolean.TRUE);
			}
			final List<Object> items = this._items_;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == Mark.EMPTY) {
					final Mark token = (Mark)items.get(i + 1);
					if (token.level() <= value) {
						if (token.isEnabled()) return this;
						token.enable();
					} // ALTERNATIV: else if (token.level() < value) return this;
					i--;
				}
			}
			return this;
		}

		/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * seine {@link Object#toString() Textdarstellung} angefügt.
		 * 
		 * @see Object#toString()
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @param part Objekt.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann. */
		public final ScriptFormatter put(final Object part) throws IllegalStateException, IllegalArgumentException {
			if (part == null) throw new NullPointerException("part = null");
			this._check_(false);
			if (part instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)part).toScript(this);
			} else {
				this._items_.add(part.toString());
			}
			return this;
		}

		/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird es
		 * über {@link ScriptFormatterHelper#formatData(ScriptFormatter, Object)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatData(ScriptFormatter, Object)
		 * @param data Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
		public final ScriptFormatter putData(final Object data) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (data == null) throw new NullPointerException("data = null");
			this._check_(false);
			if (data instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)data).toScript(this);
			} else {
				this._helper_.formatData(this, data);
			}
			return this;
		}

		/** Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
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
		 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
		public final ScriptFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (array == null) throw new NullPointerException("array = null");
			this._check_(false);
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

		/** Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.<br>
		 * Wenn der Wert ein {@link ScriptFormatterInput} ist, wird er über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird er
		 * über {@link ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
		public final ScriptFormatter putValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value == null) throw new NullPointerException("value = null");
			this._check_(false);
			if (value instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)value).toScript(this);
			} else {
				this._helper_.formatValue(this, value);
			}
			return this;
		}

		/** Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Stapelrahmens an und gibt {@code this} zurück.<br>
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
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
		public final ScriptFormatter putFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this._check_(false);
			final Iterator<? extends FEMValue> iter = params.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().put("$1: ").putValue(item);
					int index = 2;
					do {
						item = iter.next();
						this.put(";").putBreakSpace().put("$").put(new Integer(index)).put(": ").putValue(item);
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

		/** Diese Methode fügt den Quelltext der Liste der gegebenen Parameterfunktionen an und gibt {@code this} zurück.<br>
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
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
		public final ScriptFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this._check_(false);
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

		/** Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.<br>
		 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(FEMFunction)} angefügt.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param function parametrisierte Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public final ScriptFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			return this.put("{: ").putFunction(function).put("}");
		}

		/** Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
		 * Wenn die Funktion ein {@link ScriptFormatterInput} ist, wird sie über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * sie über {@link ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)
		 * @param function Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public final ScriptFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			this._check_(false);
			if (function instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)function).toScript(this);
			} else {
				this._helper_.formatFunction(this, function);
			}
			return this;
		}

		/** Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
		 * 
		 * @return Zeichenkette zur Einrückung oder {@code null}. */
		public final String getIndent() {
			return this._indent_;
		}

		/** Diese Methode gibt die genutzten Formatierungsmethoden zurück.
		 * 
		 * @return Formatierungsmethoden. */
		public final ScriptFormatterHelper getHelper() {
			return this._helper_;
		}

		/** Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.<br>
		 * Wenn diese {@code null} ist, wird nicht eingerückt.
		 * 
		 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		public synchronized final ScriptFormatter useIndent(final String indent) throws IllegalStateException {
			this._check_(true);
			this._indent_ = indent;
			return this;
		}

		/** Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		public synchronized final ScriptFormatter useHelper(final ScriptFormatterHelper helper) throws NullPointerException, IllegalStateException {
			if (helper == null) throw new NullPointerException("helper = null");
			this._check_(true);
			this._helper_ = helper;
			return this;
		}

		/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
		 * 
		 * @see #put(Object)
		 * @return Quelltext. */
		final String _format_() {
			final String indent = this._indent_;
			final List<Object> items = this._items_;
			final StringBuilder string = this._string_;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == Mark.EMPTY) {
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

		/** Diese Methode formatiert die gegebenen Elemente in einen Quelltext und gibt diesen zurück.<br>
		 * Die Elemente werden über den gegebenen {@link Converter} angefügt und mit {@code ';'} separiert. In der Methode {@link Converter#convert(Object)} sollten
		 * hierfür {@link #putData(Object)}, {@link #putValue(FEMValue)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
		 * 
		 * @see #formatDatas(Iterable)
		 * @see #formatValues(Iterable)
		 * @see #formatFunctions(Iterable)
		 * @param <GItem> Typ der Elemente.
		 * @param items Elemente.
		 * @param formatter {@link Converter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
		final <GItem> String _format_(final Iterable<? extends GItem> items, final Converter<GItem, ?> formatter) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			this._start_();
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
				return this._format_();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatDatas(Iterables.itemIterable(object))}.
		 * 
		 * @see #formatDatas(Iterable)
		 * @param object Objekt.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code object} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn das Object nicht formatiert werden kann. */
		public final String formatData(final Object object) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatDatas(Iterables.itemIterable(object));
		}

		/** Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putData(Object)
		 * @param objects Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code objects} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann. */
		public final String formatDatas(final Iterable<?> objects) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this._format_(objects, new Converter<Object, Object>() {

				@Override
				public Object convert(final Object input) {
					return ScriptFormatter.this.putData(input);
				}

			});
		}

		/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatValue(Iterables.itemIterable(value))}.
		 * 
		 * @see #formatValues(Iterable)
		 * @param value Wert.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann. */
		public final String formatValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatValues(Iterables.itemIterable(value));
		}

		/** Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Die Werte werden über {@link #putValue(FEMValue)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putValue(FEMValue)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann. */
		public final String formatValues(final Iterable<? extends FEMValue> values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this._format_(values, new Converter<FEMValue, Object>() {

				@Override
				public Object convert(final FEMValue input) {
					return ScriptFormatter.this.putValue(input);
				}

			});
		}

		/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatFunction(Iterables.itemIterable(function))}.
		 * 
		 * @see #formatFunctions(Iterable)
		 * @param function Funktion.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann. */
		public final String formatFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatFunctions(Iterables.itemIterable(function));
		}

		/** Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann. */
		public final String formatFunctions(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			return this._format_(functions, new Converter<FEMFunction, Object>() {

				@Override
				public Object convert(final FEMFunction input) {
					return ScriptFormatter.this.putFunction(input);
				}

			});
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toInvokeString(this, this._helper_, this._indent_, this._items_);
		}

	}

	/** Diese Schnittstelle definiert ein Objekt, welches sich selbst in seine Quelltextdarstellung überführen und diese an einen {@link ScriptFormatter} anfügen
	 * kann.
	 * 
	 * @see ScriptFormatter#put(Object)
	 * @see ScriptFormatter#putValue(FEMValue)
	 * @see ScriptFormatter#putFunction(FEMFunction)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptFormatterInput {

		/** Diese Methode formatiert dieses Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.<br>
		 * Sie wird vom {@link ScriptFormatter} im Rahmen folgender Methoden aufgerufen:
		 * <ul>
		 * <li>{@link ScriptFormatter#put(Object)}</li>
		 * <li>{@link ScriptFormatter#putData(Object)}</li>
		 * <li>{@link ScriptFormatter#putValue(FEMValue)}</li>
		 * <li>{@link ScriptFormatter#putFunction(FEMFunction)}</li>
		 * </ul>
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann. */
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException;

	}

	/** Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link ScriptFormatter#putValue(FEMValue)} und
	 * {@link ScriptFormatter#putFunction(FEMFunction)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptFormatterHelper {

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden ihre Eingeben über {@link String#valueOf(Object)} formatieren.<br>
		 * {@link FEMScript Aufbereitete Quelltexte} werden in {@link #formatData(ScriptFormatter, Object)} analog zur Interpretation des {@link ScriptCompiler}
		 * formatiert. */
		static ScriptFormatterHelper EMPTY = new ScriptFormatterHelper() {

			@Override
			public void formatData(final ScriptFormatter target, final Object data) throws IllegalArgumentException {
				if (data instanceof FEMScript) {
					FEM.scriptCompiler().useScript((FEMScript)data).formatScript(target);
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
				return "EMPTY";
			}

		};

		/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param data Objekt.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
		public void formatData(ScriptFormatter target, Object data) throws IllegalArgumentException;

		/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
		public void formatValue(ScriptFormatter target, FEMValue value) throws IllegalArgumentException;

		/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public void formatFunction(ScriptFormatter target, FEMFunction function) throws IllegalArgumentException;

	}

	/** Diese Klasse implementiert die {@link IllegalArgumentException}, die bei Syntaxfehlern von einem {@link ScriptParser} oder {@link ScriptCompiler} ausgelöst
	 * wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptException extends IllegalArgumentException {

		/** Dieses Feld speichert die Serial-Version-UID. */
		private static final long serialVersionUID = -918623847189389909L;

		{}

		/** Dieses Feld speichert den Hinweis zum erwarteten Inhalt des Bereichs. */
		String _hint_ = "";

		/** Dieses Feld speichert den Quelltext. */
		FEMScript _script_ = FEMScript.EMPTY;

		/** Dieses Feld speichert den Bereich, in dem der Syntaxfehler entdeckt wurde. */
		Range _range_ = Range.EMPTY;

		/** Dieser Konstruktor initialisiert die {@link ScriptException} ohne Ursache. */
		public ScriptException() {
			super();
		}

		/** Dieser Konstruktor initialisiert die {@link ScriptException} mit Ursache.
		 * 
		 * @param cause Urssache. */
		public ScriptException(final Throwable cause) {
			super(cause);
		}

		{}

		/** Diese Methode gibt den Hinweis zum erwarteten Inhalt des Bereichs zurück.
		 * 
		 * @see #getRange()
		 * @return Hinweis oder {@code null}. */
		public final String getHint() {
			return this._hint_;
		}

		/** Diese Methode gibt den Bereich zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Bereich. */
		public final Range getRange() {
			return this._range_;
		}

		/** Diese Methode gibt Quelltext zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Quelltext. */
		public final FEMScript getScript() {
			return this._script_;
		}

		/** Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #getHint()
		 * @param hint Hinweis.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code hint} {@code null} ist. */
		public final ScriptException useHint(final String hint) throws NullPointerException {
			this._hint_ = hint.toString();
			return this;
		}

		/** Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #useHint(String)
		 * @see String#format(String, Object...)
		 * @param format Hinweis.
		 * @param args Formatargumente.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code format} bzw. {@code args} {@code null} ist. */
		public final ScriptException useHint(final String format, final Object... args) throws NullPointerException {
			return this.useHint(String.format(format, args));
		}

		/** Diese Methode setzt den Bereich und gibt {@code this} zurück.
		 * 
		 * @see #getRange()
		 * @param range Bereich.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code range} {@code null} ist. */
		public final ScriptException useRange(final Range range) throws NullPointerException {
			if (range == null) throw new NullPointerException("range = null");
			this._range_ = range;
			return this;
		}

		/** Diese Methode setzt den Quelltext und gibt {@code this} zurück.
		 * 
		 * @see #getScript()
		 * @param script Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code script} {@code null} ist. */
		public final ScriptException useScript(final FEMScript script) throws NullPointerException {
			if (script == null) throw new NullPointerException("script = null");
			this._script_ = script;
			return this;
		}

		/** Diese Methode setzt Quelltext sowie Bereich und gibt {@code this} zurück.
		 * 
		 * @see #useScript(FEMScript)
		 * @see #useRange(Range)
		 * @param sender {@link ScriptCompiler}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code sender} {@code null} ist. */
		public final ScriptException useSender(final ScriptCompiler sender) throws NullPointerException {
			return this.useRange(sender.range()).useScript(sender.script());
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String getMessage() {
			return (this._range_ == Range.EMPTY //
				? "Unerwartetes Ende der Zeichenkette." //
				: "Unerwartete Zeichenkette «" + this._range_.extract(this._script_.__source) + "» an Position " + this._range_.__start + ".") //
				+ this._hint_;
		}
	}

	/** Diese Klasse implementiert ein Objekt zur Verwaltung der Zustandsdaten einer {@link TraceFunction} zur Verfolgung und Überwachung der Verarbeitung von
	 * Funktionen. Dieses Objekt wird dazu das Argument für die Methoden des {@link ScriptTracerHelper} genutzt, welcher auf die Ereignisse der Überwachung
	 * reagieren kann.
	 * 
	 * @see TraceFunction
	 * @see ScriptTracerHelper
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptTracer {

		/** Dieses Feld speichert den {@link ScriptTracerHelper}. */
		ScriptTracerHelper _helper_ = ScriptTracerHelper.EMPTY;

		/** Dieses Feld speichert den Stapelrahmen der Funktion. Dieser kann in der Methode {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf
		 * angepasst werden. */
		FEMFrame _frame_;

		/** Dieses Feld speichert die Function, die nach {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen wird bzw. vor
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} oder {@link ScriptTracerHelper#onReturn(ScriptTracer)} aufgerufen wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf angepasst werden. */
		FEMFunction _function_;

		/** Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode
		 * {@link ScriptTracerHelper#onReturn(ScriptTracer)} angepasst werden. */
		FEMValue _result_;

		/** Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} angepasst werden. */
		RuntimeException _exception_;

		{}

		/** Diese Methode gibt den {@link ScriptTracerHelper} zurück.
		 * 
		 * @return {@link ScriptTracerHelper}. */
		public final ScriptTracerHelper getHelper() {
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
		public final ScriptTracer useHelper(final ScriptTracerHelper value) throws NullPointerException {
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
		public final ScriptTracer useResult(final FEMValue value) throws NullPointerException {
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
		public final ScriptTracer useFrame(final FEMFrame value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this._frame_ = value;
			return this;
		}

		/** Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getFrame() aktuellen Stapelrahmen} ausgewertet.
		 * 
		 * @param value Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final ScriptTracer useFunction(final FEMFunction value) throws NullPointerException {
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
		public final ScriptTracer useException(final RuntimeException value) throws NullPointerException {
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
		public final ScriptTracer clear() {
			this._frame_ = null;
			this._function_ = null;
			this._result_ = null;
			this._exception_ = null;
			return this;
		}

		/** Diese Methode gibt die gegebenen Funktion als {@link TraceFunction} oder unverändert zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation von
		 * {@link ScriptTracer#_function_} verwendet werden.
		 * <p>
		 * Wenn die Funktion ein {@link ScriptTracerInput} ist, wird das Ergebnis von {@link ScriptTracerInput#toTrace(ScriptTracer)} zurück gegeben. Andernfalls
		 * wird die gegebene Funktion zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist. */
		public final FEMFunction trace(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (function instanceof ScriptTracerInput) return ((ScriptTracerInput)function).toTrace(this);
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

	/** Diese Schnittstelle definiert ein Objekt, welches sich selbst in eine {@link TraceFunction} überführen kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptTracerInput {

		/** Diese Methode gibt dieses Objekt als als {@link TraceFunction} mit dem gegebenen {@link ScriptTracer} zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation der
		 * {@link ScriptTracer#getFunction() aktuellen Funktion} des {@link ScriptTracer} verwendet werden.<br>
		 * Wenn dieses Objekt ein Wert ist, muss er sich in einer {@link ValueFunction} liefern.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist. */
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException;

	}

	/** Diese Schnittstelle definiert die Überwachungsmethoden zur Verfolgung der Verarbeitung von Funktionen.
	 * 
	 * @see ScriptTracer
	 * @see TraceFunction
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptTracerHelper {

		/** Dieses Feld speichert den {@code default}-{@link ScriptTracerHelper}, dessen Methoden den {@link ScriptTracer} nicht modifizieren. */
		public static final ScriptTracerHelper EMPTY = new ScriptTracerHelper() {

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
				return "EMPTY";
			}

		};

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
		 * {@link ScriptTracer#getException()} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#useException(RuntimeException)
		 * @param event {@link ScriptTracer}. */
		public void onThrow(ScriptTracer event);

		/** Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
		 * {@link ScriptTracer#getResult()} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#useResult(FEMValue)
		 * @param event {@link ScriptTracer}. */
		public void onReturn(ScriptTracer event);

		/** Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link ScriptTracer#getFrame()} und {@link ScriptTracer#getFunction()} können
		 * hierbei angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Stapelrahmen durchzuführen.
		 * 
		 * @see ScriptTracer#useFrame(FEMFrame)
		 * @see ScriptTracer#useFunction(FEMFunction)
		 * @param event {@link ScriptTracer}. */
		public void onExecute(ScriptTracer event);

	}

	{}

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: Function, params: Array): Value}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameterwerte des Stapelrahmens gegeben Funktion mit den im
	 * zweiten Parameterwert gegebenen Parameterwertliste. */
	public static final BaseFunction CALL_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			if (frame.size() != 2) throw new IllegalArgumentException("frame.size() != 2");
			final FEMContext context = frame._context_;
			final FEMFunction method = FEMHandler.from(frame.get(0), context).value();
			final FEMFrame params = frame.withParams(FEMArray.from(frame.get(1), context));
			return method.invoke(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("CALL_FUNCTION");
		}

	};

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (params1: Value, ..., paramN: Value, method: Function): Value}, deren Ergebniswert via
	 * {@code method(params1, ..., paramsN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameterwert des Stapelrahmens gegeben Funktion mit den davor
	 * liegenden Parameterwerten. */
	public static final BaseFunction INVOKE_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			final int index = frame.size() - 1;
			if (index < 0) throw new IllegalArgumentException("frame.size() < 1");
			final FEMContext context = frame._context_;
			final FEMFunction method = FEMHandler.from(frame.get(index), context).value();
			final FEMFrame params = frame.withParams(frame.params().section(0, index));
			return method.invoke(params);
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("APPLY_FUNCTION");
		}

	};

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Kopie der Parameterwerte eines gegebenen Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code FEMArray.from(frame.params().value())}.
	 * 
	 * @see FEMArray#from(FEMValue...)
	 * @see FEMFrame#params() */
	public static final BaseFunction PARAMS_COPY_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return FEMArray.from(frame.params().value());
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	/** Dieses Feld speichert eine Funktion, deren Ergebniswert einer Sicht auf die Parameterwerte eines gegebenen Stapelrahmens {@code frame} entspricht, d.h.
	 * {@code frame#params()}.
	 * 
	 * @see FEMFrame#params() */
	public static final BaseFunction PARAMS_VIEW_FUNCTION = new BaseFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			return frame.params();
		}

		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put("$");
		}

	};

	{}

	/** Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().arrayFrom(data)}.
	 * 
	 * @see FEMContext#arrayFrom(Object)
	 * @param data Wertliste, natives Array, {@link Iterable} oder {@link Collection}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt bzw. eines der Elemente nicht umgewandelt werden kann. */
	public static final FEMArray arrayFrom(final Object data) throws IllegalArgumentException {
		return FEMContext._default_.arrayFrom(data);
	}

	/** Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().valueFrom(data)}.
	 * 
	 * @see FEMContext#valueFrom(Object)
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann. */
	public static final FEMValue valueFrom(final Object data) throws IllegalArgumentException {
		return FEMContext._default_.valueFrom(data);
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptParser} und gibt diesen zurück.
	 * 
	 * @see ScriptParser
	 * @return {@link ScriptParser}. */
	public static final ScriptParser scriptParser() {
		return new ScriptParser();
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptCompiler} und gibt diesen zurück.
	 * 
	 * @see ScriptCompiler
	 * @return {@link ScriptCompiler}. */
	public static final ScriptCompiler scriptCompiler() {
		return new ScriptCompiler();
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptFormatter} und gibt diesen zurück.<br>
	 * Der gelieferte {@link ScriptFormatter} nutzt {@link ScriptFormatterHelper#EMPTY} und keine {@link ScriptFormatter#useIndent(String) Einrückung}.
	 * 
	 * @see ScriptFormatter
	 * @return {@link ScriptFormatter}. */
	public static final ScriptFormatter scriptFormatter() {
		return new ScriptFormatter();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseValue()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseValue()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static final String parseValue(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseString()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseString()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static final String parseString(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseString();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseComment()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseComment()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '/'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static final String parseComment(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseComment();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatValue()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatValue()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static final String formatValue(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatString()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatString()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static final String formatString(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatString();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatComment()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatComment()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static final String formatComment(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatComment();
	}

}
