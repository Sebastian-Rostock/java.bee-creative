package bee.creative.function;

import java.lang.reflect.Array;
import java.util.Arrays;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von {@link Value Werten}.
 * 
 * @see Value
 * @see Scopes
 * @see Functions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Value Wert}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractValue implements Value {

		/**
		 * Dieses Feld speichert das leere {@link Value}-Array.
		 */
		static final Value[] ARRAY_DATA = new Value[0];

		/**
		 * Dieses Feld speichert {@code Double.valueOf(Double.NaN)}.
		 */
		static final Double NUMBER_DATA = Double.valueOf(Double.NaN);

		/**
		 * Dieses Feld speichert den {@code Integer.valueOf(1)}.
		 */
		static final Number NUMBER_TRUE = Integer.valueOf(1);

		/**
		 * Dieses Feld speichert den {@code Integer.valueOf(0)}.
		 */
		static final Number NUMBER_FALSE = Integer.valueOf(0);

		/**
		 * Diese Methode konvertiert den gegebene {@link String} in einen {@link Double} und gibt ihn oder
		 * {@link #NUMBER_DATA} zurück.
		 * 
		 * @param data {@link String}.
		 * @return {@link Double} oder {@link #NUMBER_DATA}.
		 */
		static Number number(final String data) {
			try{
				return Double.valueOf(data);
			}catch(final RuntimeException e){
				return AbstractValue.NUMBER_DATA;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] arrayData() {
			return new Value[]{this};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String stringData() {
			return this.data().toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function functionData() {
			return Functions.valueFunction(this);
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
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			final Value data = (Value)object;
			return (this.type() == data.type()) && Objects.equals(this.data(), data.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link Value Wertlisten} als Datensatz.
	 * 
	 * @see Value#TYPE_ARRAY
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends AbstractValue {

		/**
		 * Dieses Feld speichert die Datensätze.
		 */
		final Value[] data;

		/**
		 * Dieser Konstrukteur initialisiert die Datensätze.
		 * 
		 * @param data Datensätze.
		 * @throws NullPointerException Wenn einer der Datensätze {@code null} ist.
		 */
		public ArrayValue(final Value... data) throws NullPointerException {
			if(data == null) throw new NullPointerException("data is null");
			if(Arrays.asList(data).contains(null)) throw new NullPointerException("data contains null");
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_ARRAY;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] arrayData() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String stringData() {
			return ((this.data.length != 0) ? this.data[0].stringData() : "");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return ((this.data.length != 0) ? this.data[0].numberData() : AbstractValue.NUMBER_DATA);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.valueOf(this.data.length != 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Arrays.hashCode(this.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("arrayValue", (Object)this.data);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit beliebigen {@link Object Objekten} als Datensatz.
	 * 
	 * @see Value#TYPE_OBJECT
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final Object data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			if(data == null) throw new NullPointerException("data is null");
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_OBJECT;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return AbstractValue.number(this.stringData());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.TRUE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("objectValue", this.data);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link String Zeichenketten} als Datensatz.
	 * 
	 * @see Value#TYPE_STRING
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final String data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			if(data == null) throw new NullPointerException("data is null");
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_STRING;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String stringData() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return AbstractValue.number(this.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.valueOf(this.data.length() != 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("stringValue", this.data);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link Boolean Wahrheitswert} als Datensatz.
	 * 
	 * @see Value#TYPE_BOOLEAN
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BooleanValue extends AbstractValue {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_BOOLEAN;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.booleanData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return (this.booleanData().booleanValue() ? AbstractValue.NUMBER_TRUE : AbstractValue.NUMBER_FALSE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("booleanValue", this.booleanData());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link Number Zahlenwert} als Datensatz.
	 * 
	 * @see Value#TYPE_NUMBER
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final Number data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException("data is null");
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_NUMBER;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.valueOf(this.data.intValue() != 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("numberValue", this.data);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link Function Funktion} als Datensatz.
	 * 
	 * @see Value#TYPE_FUNCTION
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final Function data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			if(data == null) throw new NullPointerException("data is null");
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_FUNCTION;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return AbstractValue.NUMBER_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.TRUE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function functionData() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("numberValue", this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Ergebniswert} einer {@link Function Funktion} mit
	 * {@code call-by-reference}-Semantik, welcher eine gegebene {@link Function Funktion} erst dann mit einem gegebenen
	 * {@link Scope Ausführungskontext} einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Datensatz}
	 * gelesen werden.
	 * <p>
	 * Der von der {@link Function Funktion} berechnete {@link Value Ergebniswert} wird zur schnellen Wiederverwendung
	 * gepuffert. Nach der einmaligen Auswertung der {@link Function Funktion} werden die Verweise auf {@link Scope
	 * Ausführungskontext} und {@link Function Funktion} aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ReturnValue extends AbstractValue {

		/**
		 * Dieses Feld speichert das von der {@link Function Funktion} berechnete Ergebnis oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Value value;

		/**
		 * Dieses Feld speichert den {@link Scope Ausführungskontext} zum Aufruf der {@link Function Funktion} oder
		 * {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Scope scope;

		/**
		 * Dieses Feld speichert die {@link Function Funktion} oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Function function;

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}. {@link Scope
		 * Ausführungskontext} und {@link Function Funktion} werden nicht geprüft.
		 * 
		 * @param scope {@link Scope Ausführungskontext}.
		 * @param function {@link Function Funktion}.
		 */
		ReturnValue(final Function function, final Scope scope) {
			this.scope = scope;
			this.function = function;
		}

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}.
		 * 
		 * @param scope {@link Scope Ausführungskontext}.
		 * @param function {@link Function Funktion}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} bzw. die gegebene
		 *         {@link Function Funktion} {@code null} ist.
		 */
		public ReturnValue(final Scope scope, final Function function) throws NullPointerException {
			if(scope == null) throw new NullPointerException("scope is null");
			if(function == null) throw new NullPointerException("function is null");
			this.scope = scope;
			this.function = function;
		}

		/**
		 * Diese Methode gibt den {@link Value Ergebniswert} der Ausführung der {@link Function Funktion} mit dem
		 * {@link Scope Ausführungskontext} zurück.
		 * 
		 * @see Function#execute(Scope)
		 * @return {@link Value Ergebniswert}.
		 * @throws NullPointerException Wenn der berechnete {@link Value Ergebniswert} {@code null} ist.
		 */
		public Value value() throws NullPointerException {
			Value value = this.value;
			if(value != null) return value;
			value = this.function.execute(this.scope);
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
			this.scope = null;
			this.function = null;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
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
		public Value[] arrayData() {
			return this.value().arrayData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String stringData() {
			return this.value().stringData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return this.value().numberData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return this.value().booleanData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function functionData() {
			return this.value().functionData();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.value().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "resultValue", "value", this.value, "scope", this.scope, "function",
				this.function);
		}

	}

	/**
	 * Dieses Feld speichert den {@code null}-{@link Value Wert}.
	 */
	static final Value VOID_VALUE = new AbstractValue() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return Value.TYPE_VOID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return null;
		}

		@Override
		public Value[] arrayData() {
			return AbstractValue.ARRAY_DATA;
		}

		@Override
		public String stringData() {
			return "";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return AbstractValue.NUMBER_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return Boolean.FALSE;
		}

		@Override
		public Function functionData() {
			return Functions.VOID_FUNCTION;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidValue");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Value Wert} mit dem Datensatz {@link Boolean#TRUE}.
	 */
	static final Value TRUE_VALUE = new BooleanValue() {

		@Override
		public Boolean booleanData() {
			return Boolean.TRUE;
		}

	};

	/**
	 * Dieses Feld speichert den {@link Value Wert} mit dem Datensatz {@link Boolean#FALSE}.
	 */
	static final Value FALSE_VALUE = new BooleanValue() {

		@Override
		public Boolean booleanData() {
			return Boolean.FALSE;
		}

	};

	/**
	 * Diese Methode konvertiert die gegebenen {@link Array Objekte} in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Objekte.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebenen {@link Array Objekte} {@code null} sind.
	 */
	static final Value array(final Object data) throws NullPointerException {
		if(data == null) throw new NullPointerException("data is null");
		final int size = Array.getLength(data);
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = Values.value(Array.get(data, i));
		}
		return new ArrayValue(values);
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Value Wert} oder den {@link Values#voidValue() void}-{@link Value Wert}
	 * zurück.
	 * 
	 * @param value {@link Value Wert} oder {@code null}.
	 * @return {@link Value Wert} oder den {@link Values#voidValue() void}-{@link Value Wert}.
	 */
	public static Value value(final Value value) {
		if(value == null) return Values.voidValue();
		return value;
	}

	/**
	 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link Value Wert} und gibt diesen zurück. Abhängig vom
	 * Datentyp des gegebenen Datensatzes wird hierfür eine entsprechende {@link Value Wert}-Implementation gewählt.
	 * 
	 * @param data Datensatz.
	 * @return {@link Value Wert}.
	 */
	public static Value value(final Object data) {
		if(data == null) return Values.voidValue();
		if(data instanceof Value) return (Value)data;
		if(data instanceof String) return Values.stringValue((String)data);
		if(data instanceof Number) return Values.numberValue((Number)data);
		if(data instanceof Boolean) return Values.booleanValue((Boolean)data);
		if(data instanceof Function) return Values.functionValue((Function)data);
		if(data.getClass().isArray()) return Values.array(data);
		return new ObjectValue(data);
	}

	/**
	 * Diese Methode gibt den leeren {@link Value Wert} zurück.
	 * 
	 * @return {@code void}-{@link Value Wert}.
	 */
	public static Value voidValue() {
		return Values.VOID_VALUE;
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final byte... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final short... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final char... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final int... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final long... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final float... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static Value arrayValue(final double... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Objektliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Objektliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Objektliste {@code null} ist.
	 */
	public static Value arrayValue(final Object... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zeichenkette in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return {@link Value Wert}.
	 */
	public static Value stringValue(final String data) {
		if(data == null) return Values.voidValue();
		return new StringValue(data);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static Value numberValue(final int data) {
		return Values.numberValue(Integer.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static Value numberValue(final long data) {
		return Values.numberValue(Long.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static Value numberValue(final float data) {
		return Values.numberValue(Float.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static Value numberValue(final double data) {
		return Values.numberValue(Double.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static Value numberValue(final Number data) {
		if(data == null) return Values.voidValue();
		return new NumberValue(data);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Value Wert}.
	 */
	public static Value booleanValue(final boolean data) {
		return (data ? Values.TRUE_VALUE : Values.FALSE_VALUE);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Value Wert}.
	 */
	public static Value booleanValue(final Boolean data) {
		if(data == null) return Values.voidValue();
		return Values.booleanValue(data.booleanValue());
	}

	/**
	 * Diese Methode konvertiert die gegebene {@link Function Funktion} in einen {@link Value Wert} und gibt diesen
	 * zurück.
	 * 
	 * @param data {@link Function}.
	 * @return {@link Value Wert}.
	 */
	public static Value functionValue(final Function data) {
		if(data == null) return Values.voidValue();
		return new FunctionValue(data);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Values() {
	}

}
