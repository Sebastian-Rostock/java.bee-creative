package bee.creative.function;

import java.util.ArrayList;
import java.util.Collection;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Scripts.ScriptTracer;
import bee.creative.function.Scripts.ScriptTracerInput;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert grundlegende Werte für {@code null}, {@link Array Wertlisten}, {@link Object Objekte}, {@link Function Funktionen}, {@link String
 * Zeichenketten}, {@link Number Zahlen} und {@link Boolean Wahrheitswerte}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten Wert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseValue implements Value, ScriptFormatterInput {

		/**
		 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextfrei konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht
		 * 
		 * @see Context#DEFAULT
		 * @see Context#dataOf(Value, Type)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} bzw. {@code Context.DEFAUL} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
		 */
		public final <GData> GData data(final Type<GData> type) throws NullPointerException, IllegalArgumentException {
			return Context.DEFAULT.dataOf(this, type);
		}

		/**
		 * Diese Methode gibt die in den gegebenen Datentyp ({@code GData}) kontextsensitiv konvertierten {@link #data() Nutzdaten} dieses Werts zurück.<br>
		 * Der Rückgabewert entspricht {@code context.dataOf(this, type)}.
		 * 
		 * @see Context#dataOf(Value, Type)
		 * @param <GData> Typ der gelieferten Nutzdaten, in welchen die Nutzdaten dieses Werts konvertiert werden.
		 * @param type Datentyp.
		 * @param context Kontext.
		 * @return Nutzdaten.
		 * @throws NullPointerException Wenn {@code type} bzw. {@code context} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn die Nutzdaten dieses Werts nicht konvertiert werden können.
		 */

		public final <GData> GData data(final Type<GData> type, final Context context) throws NullPointerException, ClassCastException, IllegalArgumentException {
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
			if (!(object instanceof Value)) return false;
			final Value data = (Value)object;
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
			return Scripts.scriptFormatter().formatValue(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Wert, dem zur Vollständigkeit nur noch der {@link #type() Datentyp} fehlt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class DataValue<GData> extends BaseValue {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		protected GData data;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@code null}.
		 */
		public DataValue() {
			this.data = null;
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public DataValue(final GData data) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			this.data = data;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GData data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract Type<GData> type();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putData(this.data);
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
	 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit einem
	 * gegebenen Ausführungskontext einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion
	 * berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf
	 * Ausführungskontext und Funktion aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VirtualValue extends BaseValue {

		/**
		 * Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Value value;

		/**
		 * Dieses Feld speichert den Ausführungskontext zum Aufruf der Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Scope scope;

		/**
		 * Dieses Feld speichert die Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Function function;

		/**
		 * Dieser Konstruktor initialisiert Ausführungskontext und Funktion.
		 * 
		 * @param scope Ausführungskontext.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public VirtualValue(final Scope scope, final Function function) throws NullPointerException {
			if (scope == null) throw new NullPointerException("scope = null");
			if (function == null) throw new NullPointerException("function = null");
			this.scope = scope;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den Ergebniswert der Ausführung der Funktion mit dem Ausführungskontext zurück.
		 * 
		 * @see Function#execute(Scope)
		 * @return Ergebniswert.
		 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
		 */
		public synchronized Value value() throws NullPointerException {
			Value result = this.value;
			if (result != null) return result;
			result = this.function.execute(this.scope);
			if (result == null) throw new NullPointerException("this.function().execute(this.scope()) = null");
			this.value = result;
			this.scope = null;
			this.function = null;
			return result;
		}

		/**
		 * Diese Methode gibt den Ausführungskontext oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt den Ausführungskontext auf {@code null}.
		 * 
		 * @return Ausführungskontext oder {@code null}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * Diese Methode gibt die Funktion oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
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
		public Type<?> type() {
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
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			if (this.value != null) {
				target.putValue(this.value);
			} else {
				target.putHandler(this.function).putScope(this.scope.array());
			}
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link Value} zu {@code null}.
	 */
	public static final Value NULL = new DataValue<Object>() {

		@Override
		public Type<Object> type() {
			return Values.NULL_TYPE;
		}

	};

	/**
	 * Dieses Feld speichert den {@link Value} zu {@link Boolean#TRUE}.
	 */
	public static final DataValue<Boolean> TRUE = new DataValue<Boolean>(Boolean.TRUE) {

		@Override
		public Type<Boolean> type() {
			return Values.BOOLEAN_TYPE;
		}

	};

	/**
	 * Dieses Feld speichert den {@link Value} zu {@link Boolean#FALSE}.
	 */
	public static final DataValue<Boolean> FALSE = new DataValue<Boolean>(Boolean.FALSE) {

		@Override
		public Type<Boolean> type() {
			return Values.BOOLEAN_TYPE;
		}

	};

	/**
	 * Dieses Feld speichert den Identifikator von {@link #NULL_TYPE}.
	 */
	public static final int NULL_ID = 0;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #NULL}.
	 */
	public static final Type<Object> NULL_TYPE = Type.simpleType(Values.NULL_ID, "null");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #ARRAY_TYPE}.
	 */
	public static final int ARRAY_ID = 1;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #arrayValue(Array)}.
	 */
	public static final Type<Array> ARRAY_TYPE = Type.simpleType(Values.ARRAY_ID, "ARRAY");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #OBJECT_TYPE}.
	 */
	public static final int OBJECT_ID = 2;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #objectValue(Object)}.
	 */
	public static final Type<Object> OBJECT_TYPE = Type.simpleType(Values.OBJECT_ID, "OBJECT");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #FUNCTION_TYPE}.
	 */
	public static final int FUNCTION_ID = 3;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #functionValue(Function)}.
	 */
	public static final Type<Function> FUNCTION_TYPE = Type.simpleType(Values.FUNCTION_ID, "FUNCTION");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #STRING_TYPE}.
	 */
	public static final int STRING_ID = 4;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #stringValue(String)}.
	 */
	public static final Type<String> STRING_TYPE = Type.simpleType(Values.STRING_ID, "STRING");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #NUMBER_TYPE}.
	 */
	public static final int NUMBER_ID = 5;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #numberValue(Number)}.
	 */
	public static final Type<Number> NUMBER_TYPE = Type.simpleType(Values.NUMBER_ID, "NUMBER");

	/**
	 * Dieses Feld speichert den Identifikator von {@link #BOOLEAN_TYPE}.
	 */
	public static final int BOOLEAN_ID = 6;

	/**
	 * Dieses Feld speichert den Datentyp von {@link #booleanValue(boolean)}.
	 */
	public static final Type<Boolean> BOOLEAN_TYPE = Type.simpleType(Values.BOOLEAN_ID, "BOOLEAN");

	{}

	/**
	 * Diese Methode ist eine Abkürzung für {@code Context.DEFAULT.valueOf(data)}.
	 * 
	 * @see Context#valueOf(Object)
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann.
	 */
	public static Value valueOf(final Object data) throws IllegalArgumentException {
		return Context.DEFAULT.valueOf(data);
	}

	/**
	 * Diese Methode gibt den gegebenen Wert oder {@link Values#NULL} zurück.<br>
	 * Wenn die Eingabe {@code null} ist, wird {@link Values#NULL} geliefert.
	 * 
	 * @param value Wert oder {@code null}.
	 * @return Wert oder {@link Values#NULL}.
	 */
	public static Value nullValue(final Value value) {
		if (value == null) return Values.NULL;
		return value;
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link Value} zurück.
	 * 
	 * @param data Wertliste.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @return {@link Array}-{@link Value}.
	 */
	public static DataValue<Array> arrayValue(final Array data) throws NullPointerException {
		return new DataValue<Array>(data) {

			@Override
			public Type<Array> type() {
				return Values.ARRAY_TYPE;
			}

			@Override
			public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
				target.putArray(this.data);
			}

		};
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link Value} zurück.
	 * 
	 * @see #arrayValue(Collection)
	 * @param data Wertliste.
	 * @return {@link Array}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Array> arrayValue(final Iterable<?> data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		final Collection<Object> result = new ArrayList<>();
		Iterables.appendAll(result, data);
		return Values.arrayValue(result);
	}

	/**
	 * Diese Methode gibt die gegebene Wertliste als {@link Value} zurück.
	 * 
	 * @see #arrayValue(Array)
	 * @see Array#from(Object)
	 * @see Collection#toArray()
	 * @param data Wertliste.
	 * @return {@link Array}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Array> arrayValue(final Collection<?> data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		if (data.size() == 0) return Values.arrayValue(Array.EMPTY);
		return Values.arrayValue(Array.from(data.toArray()));
	}

	/**
	 * Diese Methode gibt das gegebene Objekt als {@link Value} zurück.
	 * 
	 * @param data Objekt.
	 * @return {@link Object}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Object> objectValue(final Object data) throws NullPointerException {
		return new DataValue<Object>(data) {

			@Override
			public Type<Object> type() {
				return Values.OBJECT_TYPE;
			}

		};
	}

	/**
	 * Diese Methode gibt die gegebene Funktion als {@link Value} zurück.
	 * 
	 * @param data Funktion.
	 * @return {@link Function}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Function> functionValue(final Function data) throws NullPointerException {
		return new TracerValue<Function>(data) {

			@Override
			public Type<Function> type() {
				return Values.FUNCTION_TYPE;
			}

			@Override
			public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
				return new ValueFunction(Values.functionValue(tracer.trace(this.data)));
			}

			@Override
			public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
				target.putFunction(this.data);
			}

		};
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link Value} zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return {@link String}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<String> stringValue(final String data) throws NullPointerException {
		return new DataValue<String>(data) {

			@Override
			public Type<String> type() {
				return Values.STRING_TYPE;
			}

			@Override
			public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
				target.put("'").put(this.data.replaceAll("'", "''")).put("'");
			}

		};
	}

	/**
	 * Diese Methode gibt den gegebene Zahlenwert als {@link Value} zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Number}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Number> numberValue(final Number data) throws NullPointerException {
		return new DataValue<Number>(data) {

			@Override
			public Type<Number> type() {
				return Values.NUMBER_TYPE;
			}

		};
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link Value} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Boolean}-{@link Value}.
	 */
	public static DataValue<Boolean> booleanValue(final boolean data) {
		return (data ? Values.TRUE : Values.FALSE);
	}

	/**
	 * Diese Methode gibt den gegebenen Wahrheitswert als {@link Value} zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Boolean}-{@link Value}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static DataValue<Boolean> booleanValue(final Boolean data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		return Values.booleanValue(data.booleanValue());
	}

}
