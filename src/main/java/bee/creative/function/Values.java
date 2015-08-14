package bee.creative.function;

import javax.lang.model.type.NullType;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Scripts.ScriptTracer;
import bee.creative.function.Scripts.ScriptTracerInput;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert grundlegende Werte für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Number} und
 * {@link Boolean}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten Wert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseValue implements Value, ScriptFormatterInput {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final <GValue> GValue valueTo(final Type<GValue> type) throws NullPointerException, IllegalArgumentException {
			return Context.DEFAULT.cast(this, type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final <GValue> GValue valueTo(final Type<GValue> type, final Context context) throws NullPointerException, ClassCastException,
			IllegalArgumentException {
			return context.cast(this, type);
		}

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
			target.put(this.data());
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
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.data);
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
	public static final class LazyValue extends BaseValue {

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
		public LazyValue(final Scope scope, final Function function) throws NullPointerException {
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
		public Value value() throws NullPointerException {
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
				target.putScope(this.function).putParams(this.scope);
			}
		}

	}

	/**
	 * Diese Klasse implementiert den Wert zu {@code null}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends DataValue<Object> {

		/**
		 * Dieses Feld speichert den {@link NullValue}.
		 */
		public static final NullValue INSTANCE = new NullValue();

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 0;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<NullValue> TYPE = Type.simpleType(NullValue.TYPE_ID, "null");

		{}

		/**
		 * Diese Methode gibt den gegebenen Wert oder {@link NullValue#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullValue#INSTANCE}
		 * geliefert.
		 * 
		 * @param value Wert oder {@code null}.
		 * @return Wert oder {@link NullValue#INSTANCE}.
		 */
		public static Value valueOf(final Value value) {
			if (value == null) return NullValue.INSTANCE;
			return value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return NullValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit Wertlisten als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends DataValue<Array> {

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 1;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<ArrayValue> TYPE = Type.simpleType(ArrayValue.TYPE_ID, "ARRAY");

		{}

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public ArrayValue(final Array data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return ArrayValue.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putArray(this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit beliebigen Objekten als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends DataValue<Object> {

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 2;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<ObjectValue> TYPE = Type.simpleType(ObjectValue.TYPE_ID, "OBJECT");

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return ObjectValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für Funktionen als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends DataValue<Function> implements ScriptTracerInput {

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 3;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<FunctionValue> TYPE = Type.simpleType(FunctionValue.TYPE_ID, "FUNCTION");

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return FunctionValue.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function toTrace(final ScriptTracer tracer) throws NullPointerException {
			return new ValueFunction(new FunctionValue(tracer.trace(this.data)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link String} als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends DataValue<String> {

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 4;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<StringValue> TYPE = Type.simpleType(StringValue.TYPE_ID, "STRING");

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return StringValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Number} als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends DataValue<Number> {

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 5;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<NumberValue> TYPE = Type.simpleType(NumberValue.TYPE_ID, "NUMBER");

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return NumberValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Boolean} als Nutzdaten.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends DataValue<Boolean> {

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#TRUE}.
		 */
		public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE);

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#FALSE}.
		 */
		public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE);

		/**
		 * Dieses Feld speichert den Identifikator des Datentyps.
		 */
		public static final int TYPE_ID = 6;

		/**
		 * Dieses Feld speichert den Datentyp.
		 */
		public static final Type<BooleanValue> TYPE = Type.simpleType(BooleanValue.TYPE_ID, "BOOLEAN");

		{}

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 */
		public static BooleanValue valueOf(final boolean data) {
			return (data ? BooleanValue.TRUE : BooleanValue.FALSE);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static BooleanValue valueOf(final Boolean data) throws NullPointerException {
			return BooleanValue.valueOf(data.booleanValue());
		}

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return BooleanValue.TYPE;
		}

	}

}
