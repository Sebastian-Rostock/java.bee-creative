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
 * Diese Klasse implementiert Datentypen für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Number} und
 * {@link Boolean}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Types {

	/**
	 * Diese Klasse implementiert einen abstrakten Datentyp, dem zur Vollständigkeit nur noch die Methode {@link #valueOf(Value)} fehlt. {@link Object#hashCode()
	 * Streuwert} und {@link Object#equals(Object) Äquivalenz} beruhen auf dem {@link Type#id() Identifikator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts, in welchen ein gegebener Wert via {@link #valueOf(Value)} konvertiert werden kann.
	 */
	public static abstract class AbstractType<GValue extends Value> implements Type<GValue> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean is(final Type<?> type) {
			return (type == this) || ((type != null) && (type.id() == this.id()));
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
			return Objects.toStringCall(this, this.id());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Type}, dessen {@link #valueOf(Value)}-Methode durch einen via {@link #setConverter(Converter)}
	 * registrierten {@link Converter} beeinflusst werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts, in welchen ein gegebener Wert via {@link #valueOf(Value)} konvertiert werden kann.
	 */
	public static abstract class AbstractType2<GValue extends Value> extends AbstractType<GValue> {

		/**
		 * Dieses Feld speichert den {@link Converter} zur Anpassung von {@link #valueOf(Value)}.
		 */
		Converter<? super Value, ? extends GValue> converter;

		/**
		 * Diese Methode gibt den {@link Converter} zur Anpassung von {@link #valueOf(Value)} zurück.
		 * 
		 * @return {@link Converter} oder {@code null}.
		 */
		public Converter<? super Value, ? extends GValue> getConverter() {
			return this.converter;
		}

		/**
		 * Diese Methode setzt den {@link Converter} zur Anpassung von {@link #valueOf(Value)}.
		 * 
		 * @param converter {@link Converter} oder {@code null}.
		 */
		public void setConverter(final Converter<? super Value, ? extends GValue> converter) {
			this.converter = converter;
		}

		/**
		 * Diese Methode wird von {@link #valueOf(Value)} mit dem Wert sowie dessen Nutzdaten aufgerufen und gibt den in diesen Datentyp konvertierten Wert zurück.
		 * 
		 * @see Type#valueOf(Value)
		 * @see Value#data()
		 * @param value Wert.
		 * @param data Nutzdaten.
		 * @return konvertierter Wert.
		 * @throws ClassCastException Wenn bei der Konvertierung ein unzulässiger {@code cast} vorkommt.
		 * @throws IllegalArgumentException Wenn der Datensatz des gegebenen {@link Value}{@code s} nicht in den generische Datentyp dieses {@link Type}{@code s}
		 *         konvertiert werden kann.
		 */
		protected abstract GValue valueOf(Value value, Object data) throws ClassCastException, IllegalArgumentException;

		/**
		 * {@inheritDoc} Wenn der via {@link #setConverter(Converter)} registrierte {@link Converter} sowie das Ergebnis seiner {@link Converter#convert(Object)
		 * Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben. Anderenfalls gelten die Konvertierungsregeln dieses Datentyps.
		 * 
		 * @see Converter#convert(Object)
		 */
		@Override
		public GValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if(value == null) throw new NullPointerException();
			final Converter<? super Value, ? extends GValue> converter = this.converter;
			if(converter != null){
				final GValue result = converter.convert(value);
				if(result != null) return result;
			}
			return this.valueOf(value, value.data());
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@code null}.
	 * 
	 * @see NullValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullType extends AbstractType<NullValue> {

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
		 * {@inheritDoc}
		 * <ul>
		 * <li>Der Rückgabewert ist immer {@link NullValue#INSTANCE}.</li>
		 * </ul>
		 */
		@Override
		public NullValue valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			return NullValue.INSTANCE;
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Value}{@code []}.
	 * 
	 * @see ArrayValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayType extends AbstractType2<ArrayValue> implements Converter<Value, Value[]> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 1;

		/**
		 * Dieses Feld speichert den leeren {@link ArrayValue}.
		 */
		public static final ArrayValue NULL_VALUE = new ArrayValue(0, new Value[0]);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ArrayValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				case NullType.ID:
					return ArrayType.NULL_VALUE;
				case ArrayType.ID:
					return (ArrayValue)value;
				default:
					return new ArrayValue(0, new Value[]{value});
			}
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
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein {@link ArrayValue} zurück gegeben, das den gegebenen Wert als einziges Element enthält.</li>
		 * </ul>
		 */
		@Override
		public ArrayValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public Value[] convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Object}.
	 * 
	 * @see ObjectValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectType extends AbstractType2<ObjectValue> implements Converter<Value, Object> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 2;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ObjectValue valueOf(final Value value, final Object data) throws ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				default:
					if(data != null) return ObjectValue.valueOf(data);
				case NullType.ID:
					throw new IllegalArgumentException();
				case ObjectType.ID:
					return (ObjectValue)value;
			}
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
		 * <li>Wenn der Wert ein {@link NullValue} ist oder seine Nutzdaten {@code null} sind, wird ein {@link IllegalArgumentException} ausgelöst.</li>
		 * <li>Wenn der Wert ein {@link ObjectValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein neuer {@link ObjectValue} mit den Nutzdaten des gegebenen Werts zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public ObjectValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public Object convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Function}.
	 * 
	 * @see FunctionValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionType extends AbstractType2<FunctionValue> implements Converter<Value, Function> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 3;

		/**
		 * Dieses Feld speichert den leeren {@link FunctionValue}.
		 */
		public static final FunctionValue NULL_VALUE = new FunctionValue(ValueFunction.NULL_FUNCTION);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FunctionValue valueOf(final Value value, final Object data) throws ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				case NullType.ID:
					return FunctionType.NULL_VALUE;
				case FunctionType.ID:
					return (FunctionValue)value;
				default:
					return FunctionValue.valueOf(ValueFunction.valueOf(value));
			}
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
		 * <li>Wenn der Wert ein {@link FunctionValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Wert via {@link ValueFunction#valueOf(Value)} und {@link FunctionValue#valueOf(Function)} umgewandelt und zurück
		 * gegeben.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Value)
		 * @see FunctionValue#valueOf(Function)
		 */
		@Override
		public FunctionValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public Function convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link String}.
	 * 
	 * @see StringValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringType extends AbstractType2<StringValue> implements Converter<Value, String> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 4;

		/**
		 * Dieses Feld speichert {@code ""} als {@link StringValue}.
		 */
		public static final StringValue NULL_VALUE = new StringValue("");

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected StringValue valueOf(final Value value, final Object data) throws ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				case NullType.ID:
					return StringType.NULL_VALUE;
				case StringType.ID:
					return (StringValue)value;
				case ArrayType.ID:
					return StringValue.valueOf(Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data))));
				default:
					return StringValue.valueOf(String.valueOf(data));
			}
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
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code "null"} als {@link StringValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, werden die mit dieser Methode in {@link String Zeichenketten} umgewandelten Elemente des
		 * Datensatzes mit dem Trennzeichen {@code ", "} verkettet und als {@link StringValue} zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz via {@link String#valueOf(Object)} umgewandelt und als {@link StringValue} zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see String#valueOf(Object)
		 */
		@Override
		public StringValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public String convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Number}.
	 * 
	 * @see NumberValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberType extends AbstractType2<NumberValue> implements Converter<Value, Number> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 5;

		/**
		 * Dieses Feld speichert {@code NaN} als {@link NumberValue}.
		 */
		public static final NumberValue NULL_VALUE = new NumberValue(Double.valueOf(Double.NaN));

		/**
		 * Dieses Feld speichert {@code 1} als {@link NumberValue}.
		 */
		public static final NumberValue TRUE_VALUE = new NumberValue(Integer.valueOf(1));

		/**
		 * Dieses Feld speichert {@code 0} als {@link NumberValue}.
		 */
		public static final NumberValue FALSE_VALUE = new NumberValue(Integer.valueOf(0));

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected NumberValue valueOf(final Value value, final Object data) throws ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				case NullType.ID:
					return NumberType.NULL_VALUE;
				case StringType.ID:
					return NumberValue.valueOf(Double.valueOf((String)data));
				case NumberType.ID:
					return (NumberValue)value;
				case BooleanType.ID:
					return (((Boolean)data).booleanValue() ? NumberType.TRUE_VALUE : NumberType.FALSE_VALUE);
				default:
					throw new IllegalArgumentException();
			}
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
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@code NaN} als {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, werden seine Nutzdaten via {@link Double#valueOf(String)} interpretiert und als {@link NumberValue} zurück
		 * gegeben.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird bei {@code false} eine {@code 0} bzw. bei {@code true} eine {@code 1} als {@link NumberValue} zurück
		 * gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see Double#valueOf(String)
		 */
		@Override
		public NumberValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public Number convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Boolean}.
	 * 
	 * @see BooleanValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanType extends AbstractType2<BooleanValue> implements Converter<Value, Boolean> {

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
		protected BooleanValue valueOf(final Value value, final Object data) throws NullPointerException, ClassCastException, IllegalArgumentException {
			switch(value.type().id()){
				case NullType.ID:
					return BooleanValue.FALSE;
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
				default:
					throw new IllegalArgumentException();
			}
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
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@link BooleanValue#FALSE} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link BooleanValue#TRUE} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, wird {@link BooleanValue#FALSE} nur bei {@code ""} als Nutzdaten zurück gegeben; Anderenfalls ist der
		 * Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird {@link BooleanValue#FALSE} nur bei {@code 0} bzw. {@code NaN} als Nutzdaten zurück gegeben;
		 * Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public BooleanValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return super.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #valueOf(Value)
		 */
		@Override
		public Boolean convert(final Value input) {
			return this.valueOf(input).data();
		}

	}

}
