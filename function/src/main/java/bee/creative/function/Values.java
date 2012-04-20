package bee.creative.function;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
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
	static abstract class BaseValue implements Value {

		/**
		 * Dieses Feld speichert die Standard-{@link Value Werteliste}.
		 */
		static final Value[] ARRAY_DATA = new Value[0];

		/**
		 * Dieses Feld speichert das Standard-{@link Object Objekt}.
		 */
		static final Object OBJECT_DATA = null;

		/**
		 * Dieses Feld speichert die Standard-{@link String Zeichenkette}.
		 */
		static final String STRING_DATA = "";

		/**
		 * Dieses Feld speichert den Standard-{@link Number Zahlenwert}.
		 */
		static final Number NUMBER_DATA = Double.valueOf(Double.NaN);

		/**
		 * Dieses Feld speichert den {@link Number Zahlenwert} {@code 1}.
		 */
		static final Number NUMBER_TRUE = Integer.valueOf(1);

		/**
		 * Dieses Feld speichert den {@link Number Zahlenwert} {@code 0}.
		 */
		static final Number NUMBER_FALSE = Integer.valueOf(0);

		/**
		 * Dieses Feld speichert den Standard-{@link Boolean Wahrheitswert}.
		 */
		static final Boolean BOOLEAN_DATA = Boolean.FALSE;

		/**
		 * Dieses Feld speichert den {@link Boolean Wahrheitswert} {@code true}.
		 */
		static final Boolean BOOLEAN_TRUE = Boolean.TRUE;

		/**
		 * Dieses Feld speichert den {@link Boolean Wahrheitswert} {@code false}.
		 */
		static final Boolean BOOLEAN_FALSE = Boolean.FALSE;

		/**
		 * Diese Methode konvertiert die gegebene Zeichenkette in einen Zahlenwert und gibt diesen zurück.
		 * 
		 * @param data Zeichenkette.
		 * @return Zahlenwert.
		 */
		static Number number(final String data) {
			try{
				return Double.valueOf(data);
			}catch(final RuntimeException e){
				return BaseValue.NUMBER_DATA;
			}
		}

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
			return BaseValue.OBJECT_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] arrayData() {
			return BaseValue.ARRAY_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String stringData() {
			return BaseValue.STRING_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return BaseValue.NUMBER_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return BaseValue.BOOLEAN_DATA;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.data().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			final Value data = (Value)object;
			switch(data.type()){
				default:
					return Objects.equals(this.data(), data.data());
				case Value.TYPE_ARRAY:
					return Arrays.equals(this.arrayData(), data.arrayData());
				case Value.TYPE_STRING:
					return Objects.equals(this.stringData(), data.stringData());
				case Value.TYPE_NUMBER:
					return Objects.equals(this.numberData(), data.numberData());
				case Value.TYPE_BOOLEAN:
					return Objects.equals(this.booleanData(), data.booleanData());
			}
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit {@link Value Wertlisten} als Datensatz.
	 * 
	 * @see Value#TYPE_ARRAY
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends BaseValue {

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
			return ((this.data.length != 0) ? this.data[0].stringData() : BaseValue.STRING_DATA);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return ((this.data.length != 0) ? this.data[0].numberData() : BaseValue.NUMBER_DATA);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return ((this.data.length != 0) ? BaseValue.BOOLEAN_TRUE : BaseValue.BOOLEAN_FALSE);
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
	public static final class ObjectValue extends BaseValue {

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
		public String stringData() {
			return this.data.toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return BaseValue.number(this.stringData());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return BaseValue.BOOLEAN_TRUE;
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
	public static final class StringValue extends BaseValue {

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
			return BaseValue.number(this.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean booleanData() {
			return ((this.data.length() == 0) ? BaseValue.BOOLEAN_FALSE : BaseValue.BOOLEAN_TRUE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.data().hashCode();
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
	static abstract class BooleanValue extends BaseValue {

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
		public String stringData() {
			return this.booleanData().toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number numberData() {
			return (this.booleanData().booleanValue() ? BaseValue.NUMBER_TRUE : BaseValue.NUMBER_FALSE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.booleanData().hashCode();
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
	public static final class NumberValue extends BaseValue {

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
		public String stringData() {
			return this.data.toString();
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
			return ((this.data.intValue() == 0) ? BaseValue.BOOLEAN_FALSE : BaseValue.BOOLEAN_TRUE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.data.hashCode();
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
	 * Dieses Feld speichert den Datentypidentifikator {@code org.w3c.dom.Text}.
	 */
	static final Value VOID_VALUE = new BaseValue() {

		@Override
		public int hashCode() {
			return 0;
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
	public static final Value value(final Value value) {
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
	public static final Value value(final Object data) {
		if(data == null) return Values.voidValue();
		if(data instanceof Value) return (Value)data;
		if(data instanceof String) return Values.stringValue((String)data);
		if(data instanceof Number) return Values.numberValue((Number)data);
		if(data instanceof Boolean) return Values.booleanValue((Boolean)data);
		if(data.getClass().isArray()) return Values.array(data);
		if(data instanceof List<?>) return Values.arrayValue((List<?>)data);
		return new ObjectValue(data);
	}

	/**
	 * Diese Methode gibt den leeren {@link Value Wert} zurück.
	 * 
	 * @return {@code void}-{@link Value Wert}.
	 */
	public static final Value voidValue() {
		return Values.VOID_VALUE;
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final byte... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final short... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final int... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final long... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final float... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Zahlenliste {@code null} ist.
	 */
	public static final Value arrayValue(final double... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Objektliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Objektliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Objektliste {@code null} ist.
	 */
	public static final Value arrayValue(final Object... data) throws NullPointerException {
		return Values.array(data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Objektliste in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Objektliste.
	 * @return {@link Value Wert}.
	 * @throws NullPointerException Wenn die gegebene Objektliste {@code null} ist.
	 */
	public static final Value arrayValue(final List<?> data) throws NullPointerException {
		return Values.array(data.toArray());
	}

	/**
	 * Diese Methode konvertiert die gegebene Zeichenkette in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zeichenkette.
	 * @return {@link Value Wert}.
	 */
	public static final Value stringValue(final String data) {
		if(data == null) return Values.voidValue();
		return new StringValue(data);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static final Value numberValue(final int data) {
		return Values.numberValue(Integer.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static final Value numberValue(final long data) {
		return Values.numberValue(Long.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static final Value numberValue(final float data) {
		return Values.numberValue(Float.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static final Value numberValue(final double data) {
		return Values.numberValue(Double.valueOf(data));
	}

	/**
	 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Zahlenwert.
	 * @return {@link Value Wert}.
	 */
	public static final Value numberValue(final Number data) {
		if(data == null) return Values.voidValue();
		return new NumberValue(data);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Value Wert}.
	 */
	public static final Value booleanValue(final boolean data) {
		return (data ? Values.TRUE_VALUE : Values.FALSE_VALUE);
	}

	/**
	 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value Wert} und gibt diesen zurück.
	 * 
	 * @param data Wahrheitswert.
	 * @return {@link Value Wert}.
	 */
	public static final Value booleanValue(final Boolean data) {
		if(data == null) return Values.voidValue();
		return Values.booleanValue(data.booleanValue());
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Values() {
	}

}
