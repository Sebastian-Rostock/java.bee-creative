package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.BooleanValue;
import bee.creative.function.Values.FunctionValue;
import bee.creative.function.Values.NullValue;
import bee.creative.function.Values.NumberValue;
import bee.creative.function.Values.ObjectValue;
import bee.creative.function.Values.StringValue;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;
import bee.creative.util.Strings;

/**
 * Diese Klasse implementiert {@link Type}{@code s} für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} und {@link Boolean}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Types {

	/**
	 * Diese Schnittstelle definiert Methoden zur Konvertierung des Datensatzes eines gegebenen {@link Value}{@code s} in einen bestimmten Datentyp bzw. {@link Value}. Verwendung finden diese Methoden in {@link AbstractType2#dataOf(Value)} und {@link AbstractType2#valueOf(Value)}, wofür sie via {@link AbstractType2#setHandler(ValueHandler)} registriert werden können.
	 * 
	 * @see AbstractType2#dataOf(Value)
	 * @see AbstractType2#valueOf(Value)
	 * @see AbstractType2#setHandler(ValueHandler)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 * @param <GValue> Typ des {@link Value}{@code s}.
	 */
	public static interface ValueHandler<GData, GValue> {

		/**
		 * Diese Methode gibt den in {@code GData} konvertierten Datensatz des gegebenen {@link Value}{@code s} zurück.
		 * 
		 * @see Type#dataOf(Value)
		 * @param value {@link Value}.
		 * @return konvertierter Datensatz.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in {@code GData} konvertiert werden kann.
		 */
		public GData dataOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

		/**
		 * Diese Methode gibt den in {@code GValue} konvertierten Datensatz des gegebenen {@link Value}{@code s} zurück.
		 * 
		 * @see Type#valueOf(Value)
		 * @param value {@link Value}.
		 * @return {@link Value} mit konvertiertem Datensatz.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in {@code GData} konvertiert werden kann.
		 */
		public GValue valueOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Type}, dem zur Vollständigkeit nur noch die Methoden {@link #dataOf(Value)} und {@link #valueOf(Value)} fehlen. {@link Object#hashCode() Streuwert} und {@link Object#equals(Object) Äquivalenz} beruhen auf dem {@link Type#id() Identifikator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 * @param <GValue> Typ des {@link Value}{@code s}.
	 */
	public static abstract class AbstractType<GData, GValue extends Value> implements Type<GData>, Converter<Value, GData> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean is(final Type<?> type) {
			return (type == this) || ((type != null) && (type.id() == this.id()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract GValue valueOf(Value value) throws NullPointerException, ClassCastException, IllegalArgumentException;

		/**
		 * Diese Methode gibt {@code this.dataOf(input)} zurück und realisiert die {@link Converter}-Schnittstelle.
		 * 
		 * @see Type#dataOf(Value)
		 * @see Converter#convert(Object)
		 * @param input Value.
		 * @return {@code this.dataOf(input)}.
		 */
		@Override
		public GData convert(final Value input) {
			return this.dataOf(input);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #id()
		 */
		@Override
		public int hashCode() {
			return this.id();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #id()
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Type<?>)) return false;
			final Type<?> data = (Type<?>)object;
			return this.id() == data.id();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this.getClass().getSimpleName(), this.id());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Type}, dessen {@link #dataOf(Value)}- und {@link #valueOf(Value)}-Methoden durch einen via {@link #setHandler(ValueHandler)} registrierten {@link ValueHandler} beeinflusst werden können.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 * @param <GValue> Typ des {@link Value}{@code s}.
	 */
	public static abstract class AbstractType2<GData, GValue extends Value> extends AbstractType<GData, GValue> {

		/**
		 * Dieses Feld speichert den {@link ValueHandler} zur Anpassung von {@link #dataOf(Value)} und {@link #valueOf(Value)}.
		 */
		ValueHandler<? extends GData, ? extends GValue> handler;

		/**
		 * Diese Methode gibt den {@link ValueHandler} zur Anpassung von {@link #dataOf(Value)} und {@link #valueOf(Value)} zurück.
		 * 
		 * @return {@link ValueHandler} oder {@code null}.
		 */
		public ValueHandler<? extends GData, ? extends GValue> getHandler() {
			return this.handler;
		}

		/**
		 * Diese Methode setzt den {@link ValueHandler} zur Anpassung von {@link #dataOf(Value)} und {@link #valueOf(Value)}.
		 * 
		 * @param handler {@link ValueHandler} oder {@code null}.
		 */
		public void setHandler(final ValueHandler<? extends GData, ? extends GValue> handler) {
			this.handler = handler;
		}

		/**
		 * Diese Methode wird von {@link #dataOf(Value)} mit dem {@link Value} sowie dessen Datensatz aufgerufen und gibt den in den generischen Datentyp dieses {@link Type}{@code s} konvertierten Datensatz zurück.
		 * 
		 * @see Type#dataOf(Value)
		 * @param value {@link Value}.
		 * @param data Datensatz.
		 * @return konvertierter Datensatz.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in den generische Datentyp dieses {@link Type}{@code s} konvertiert werden kann.
		 */
		protected abstract GData dataOf(Value value, Object data) throws NullPointerException, ClassCastException, IllegalArgumentException;

		/**
		 * Diese Methode wird von {@link #valueOf(Value)} mit dem {@link Value} sowie dessen Datensatz aufgerufen und gibt den in den generischen Datentyp dieses {@link Type}{@code s} konvertierten Datensatz als {@link Value} zurück.
		 * 
		 * @see Type#valueOf(Value)
		 * @param value {@link Value}.
		 * @param data Datensatz.
		 * @return {@link Value} mit konvertiertem Datensatz.
		 * @throws NullPointerException Wenn der gegebene {@link Value} {@code null} ist.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in den generische Datentyp dieses {@link Type}{@code s} konvertiert werden kann.
		 */
		protected abstract GValue valueOf(Value value, Object data) throws NullPointerException, ClassCastException, IllegalArgumentException;

		/**
		 * {@inheritDoc} Wenn der via {@link #setHandler(ValueHandler)} registrierte {@link ValueHandler} sowie das Ergebnis seiner {@link ValueHandler#dataOf(Value) Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben. Anderenfalls gelten die folgenden Konvertierungsregeln:
		 * 
		 * @see ValueHandler#dataOf(Value)
		 */
		@Override
		public GData dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final ValueHandler<? extends GData, ? extends GValue> handler = this.handler;
			if(handler != null){
				final GData result = handler.dataOf(value);
				if(result != null) return result;
			}
			return this.dataOf(value, value.data());
		}

		/**
		 * {@inheritDoc} Wenn der via {@link #setHandler(ValueHandler)} registrierte {@link ValueHandler} sowie das Ergebnis seiner {@link ValueHandler#valueOf(Value) Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben. Anderenfalls gelten die folgenden Konvertierungsregeln:
		 * 
		 * @see ValueHandler#valueOf(Value)
		 */
		@Override
		public GValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final ValueHandler<? extends GData, ? extends GValue> handler = this.handler;
			if(handler != null){
				final GValue result = handler.valueOf(value);
				if(result != null) return result;
			}
			return this.valueOf(value, value.data());
		}

	}

	/**
	 * Diese Klasse implementiert den {@code null}-{@link Type}.
	 * 
	 * @see NullValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullType extends AbstractType<Object, NullValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return NullType.ID;
		}

		/**
		 * {@inheritDoc} <br>
		 * <ul>
		 * <li>Der Rückgabewert ist immer {@code null}.</li>
		 * </ul>
		 */
		@Override
		public Object dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(value == null) throw new NullPointerException();
			return null;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Der Rückgabewert ist immer {@link NullValue#INSTANCE}.</li>
		 * </ul>
		 * 
		 * @see NullType#dataOf(Value)
		 */
		@Override
		public NullValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(value == null) throw new NullPointerException();
			return NullValue.INSTANCE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link Value}{@code []}.
	 * 
	 * @see ArrayValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayType extends AbstractType2<Value[], ArrayValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 1;

		/**
		 * Dieses Feld speichert das leere {@link Value}{@code []}.
		 */
		static final Value[] NULL_DATA = {};

		/**
		 * Dieses Feld speichert das leere {@link Value}{@code []} als {@link Value}.
		 */
		static final ArrayValue NULL_VALUE = new ArrayValue(0, ArrayType.NULL_DATA);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Value[] dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return ArrayType.NULL_DATA;
			if(value.type().id() == ArrayType.ID) return (Value[])data;
			return new Value[]{value};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ArrayValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return ArrayType.NULL_VALUE;
			if(value.type().id() == ArrayType.ID) return (ArrayValue)value;
			return new ArrayValue(0, new Value[]{value});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return ArrayType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein Array mit dem gegebenen {@link Value} zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public Value[] dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein Array mit dem gegebenen {@link Value} als {@link ArrayValue} zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public ArrayValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link Object Objekte}.
	 * 
	 * @see ObjectValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectType extends AbstractType2<Object, ObjectValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 2;

		/**
		 * Dieses Feld speichert ein {@link Object}.
		 */
		static final Object NULL_DATA = new Object();

		/**
		 * Dieses Feld speichert ein {@link Object} als {@link Value}.
		 */
		static final ObjectValue NULL_VALUE = new ObjectValue(ObjectType.NULL_DATA);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return ObjectType.NULL_DATA;
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ObjectValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return ObjectType.NULL_VALUE;
			if(value.type().id() == ObjectType.ID) return (ObjectValue)value;
			return ObjectValue.valueOf(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return ObjectType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein {@link Object} zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz des {@link Value}{@code s} zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public Object dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein {@link Object} als {@link Value} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz des {@link Value}{@code s} als {@link ObjectValue} zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public ObjectValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link Function Funktionen}.
	 * 
	 * @see FunctionValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionType extends AbstractType2<Function, FunctionValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 3;

		/**
		 * Dieses Feld speichert die leere {@link Function}.
		 */
		static final Function NULL_DATA = new ValueFunction(NullValue.INSTANCE);

		/**
		 * Dieses Feld speichert die leere {@link Function} als {@link Value}.
		 */
		static final FunctionValue NULL_VALUE = new FunctionValue(FunctionType.NULL_DATA);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Function dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return FunctionType.NULL_DATA;
			if(value.type().id() == FunctionType.ID) return (Function)data;
			return ValueFunction.valueOf(value);

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FunctionValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return FunctionType.NULL_VALUE;
			if(value.type().id() == FunctionType.ID) return (FunctionValue)value;
			return FunctionValue.valueOf(ValueFunction.valueOf(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return FunctionType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der {@link Value} vom Typ {@link FunctionType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der {@link Value} via {@link ValueFunction#valueOf(Value)} umgewandelt und zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Value)
		 */
		@Override
		public Function dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der {@link Value} vom Typ {@link FunctionType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der {@link Value} via {@link ValueFunction#valueOf(Value)} und {@link FunctionValue#valueOf(Function)} umgewandelt und zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Value)
		 * @see FunctionValue#valueOf(Function)
		 */
		@Override
		public FunctionValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link String Zeichenketten}.
	 * 
	 * @see StringValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringType extends AbstractType2<String, StringValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 4;

		/**
		 * Dieses Feld speichert {@code ""}.
		 */
		static final String NULL_DATA = "";

		/**
		 * Dieses Feld speichert {@code ""} als {@link Value}.
		 */
		static final StringValue NULL_VALUE = new StringValue(StringType.NULL_DATA);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return StringType.NULL_DATA;
			switch(value.type().id()){
				case ArrayType.ID:
					return Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data)));
				case StringType.ID:
					return (String)data;
			}
			return String.valueOf(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected StringValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return StringType.NULL_VALUE;
			switch(value.type().id()){
				case ArrayType.ID:
					return StringValue.valueOf(Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data))));
				case StringType.ID:
					return (StringValue)value;
			}
			return StringValue.valueOf(String.valueOf(data));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return StringType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code ""} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, werden die mit dieser Methode in {@link String Zeichenketten} umgewandelten Elemente des Datensatzes mit dem Trennzeichen {@code ", "} verkettet und zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz via {@link String#valueOf(Object)} umgewandelt und zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see String#valueOf(Object)
		 */
		@Override
		public String dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code "null"} als {@link StringValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, werden die mit dieser Methode in {@link String Zeichenketten} umgewandelten Elemente des Datensatzes mit dem Trennzeichen {@code ", "} verkettet und als {@link StringValue} zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz via {@link String#valueOf(Object)} umgewandelt und als {@link StringValue} zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see String#valueOf(Object)
		 */
		@Override
		public StringValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link Number Zahlenwerte}.
	 * 
	 * @see NumberValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberType extends AbstractType2<Number, NumberValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 5;

		/**
		 * Dieses Feld speichert {@code NaN} als {@link Double}.
		 */
		static final Number NULL_DATA = Double.valueOf(Double.NaN);

		/**
		 * Dieses Feld speichert {@code NaN} als {@link Value}.
		 */
		static final NumberValue NULL_VALUE = new NumberValue(NumberType.NULL_DATA);

		/**
		 * Dieses Feld speichert {@code 1} als {@link Integer}.
		 */
		static final Number TRUE_DATA = Integer.valueOf(1);

		/**
		 * Dieses Feld speichert {@code 1} als {@link Value}.
		 */
		static final NumberValue TRUE_VALUE = new NumberValue(NumberType.TRUE_DATA);

		/**
		 * Dieses Feld speichert {@code 0} als {@link Integer}.
		 */
		static final Number FALSE_DATA = Integer.valueOf(0);

		/**
		 * Dieses Feld speichert {@code 0} als {@link Value}.
		 */
		static final NumberValue FALSE_VALUE = new NumberValue(NumberType.FALSE_DATA);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Number dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return NumberType.NULL_DATA;
			switch(value.type().id()){
				case StringType.ID:
					return Double.valueOf((String)data);
				case NumberType.ID:
					return (Number)data;
				case BooleanType.ID:
					return (((Boolean)data).booleanValue() ? NumberType.TRUE_DATA : NumberType.FALSE_DATA);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected NumberValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return NumberType.NULL_VALUE;
			switch(value.type().id()){
				case StringType.ID:
					return NumberValue.valueOf(Double.valueOf((String)data));
				case NumberType.ID:
					return (NumberValue)value;
				case BooleanType.ID:
					return (((Boolean)data).booleanValue() ? NumberType.TRUE_VALUE : NumberType.FALSE_VALUE);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return NumberType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@code NaN} als {@link Double} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird dieser via {@link Double#valueOf(String)} umgewandelt und zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird {@code 0} als {@link Integer} nur bei dem Datensatz {@link Boolean#FALSE} zurück gegeben; Anderenfalls ist der Rückgabewert {@code 1} als {@link Integer}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see Double#valueOf(String)
		 */
		@Override
		public Number dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@code NaN} als {@link Double} in inemem {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird sein Datensatz via {@link Double#valueOf(String)} umgewandelt und als {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird {@code 0} als {@link Integer} in einem {@link NumberValue} nur bei dem Datensatz {@link Boolean#FALSE} zurück gegeben; Anderenfalls ist der Rückgabewert {@code 1} als {@link Integer} in einem {@link NumberValue}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see Double#valueOf(String)
		 */
		@Override
		public NumberValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Type} für {@link Boolean Wahrheitswerte}.
	 * 
	 * @see BooleanValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanType extends AbstractType2<Boolean, BooleanValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 6;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Boolean dataOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return Boolean.FALSE;
			switch(value.type().id()){
				case ArrayType.ID:
				case ObjectType.ID:
				case FunctionType.ID:
					return Boolean.TRUE;
				case StringType.ID:
					return Boolean.valueOf(((String)data).length() != 0);
				case NumberType.ID:
					return Boolean.valueOf(((Number)data).intValue() != 0);
				case BooleanType.ID:
					return (Boolean)data;
			}
			throw new IllegalArgumentException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected BooleanValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(data == null) return BooleanValue.FALSE;
			switch(value.type().id()){
				case ArrayType.ID:
				case ObjectType.ID:
				case FunctionType.ID:
					return BooleanValue.TRUE;
				case StringType.ID:
					return BooleanValue.valueOf(((String)data).length() != 0);
				case NumberType.ID:
					return BooleanValue.valueOf(((Number)data).intValue() != 0);
				case BooleanType.ID:
					return (BooleanValue)value;
			}
			throw new IllegalArgumentException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return BooleanType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@link Boolean#FALSE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link Boolean#TRUE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird {@link Boolean#FALSE} nur bei einem leeren {@link String} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird {@link Boolean#FALSE} nur bei einer {@code 0}- bzw. {@code NaN}-{@link Number} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public Boolean dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.dataOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@link BooleanValue#FALSE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link BooleanValue#TRUE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird {@link BooleanValue#FALSE} nur bei einem leeren {@link String} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird {@link BooleanValue#FALSE} nur bei einer {@code 0}- bzw. {@code NaN}-{@link Number} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public BooleanValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Types() {
	}

}
