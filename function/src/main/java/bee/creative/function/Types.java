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
	 * Diese Klasse implementiert einen abstrakten {@link Type}. {@link Object#hashCode() Streuwert} und {@link Object#equals(Object) Äquivalenz} beruhen auf dem {@link Type#id() Identifikator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static abstract class AbstractType<GData> implements Type<GData>, Converter<Value, GData> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean is(final Type<?> type) {
			return (type == this) || ((type != null) && (type.id() == this.id()));
		}

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
		 */
		@Override
		public int hashCode() {
			return this.id();
		}

		/**
		 * {@inheritDoc}
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
	 * Diese Klasse implementiert den {@code null}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullType extends AbstractType<Object> {

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
	 * Diese Klasse implementiert den {@link Value}{@code []}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayType extends AbstractType<Value[]> {

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
		public int id() {
			return ArrayType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType} und sein Datensatz ein Array sind, wird der Datensatz via {@link ArrayValue#valueOf(Object)} in einen neuen {@link ArrayValue} umgewandelt und dessen Datensatz zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein Array mit dem gegebenen {@link Value} zurück gegeben.</li>
		 * </ul>
		 */
		@Override
		public Value[] dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return ArrayType.NULL_DATA;
			switch(value.type().id()){
				case ArrayType.ID:
					return (Value[])data;
				case ObjectType.ID:
					final ArrayValue array = ArrayValue.valueOf(data);
					if(array != null) return array.data();
				default:
					return new Value[]{value};
			}
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, wird der {@link ArrayValue} unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType} und sein Datensatz ein Array sind, wird der Datensatz via {@link ArrayValue#valueOf(Object)} in einen neuen {@link ArrayValue} umgewandelt und zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein Array mit dem gegebenen {@link Value} zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see ArrayType#dataOf(Value)
		 * @see ArrayValue#valueOf(Value...)
		 */
		@Override
		public ArrayValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return ArrayType.NULL_VALUE;
			switch(value.type().id()){
				case ArrayType.ID:
					return (ArrayValue)value;
				case ObjectType.ID:
					final ArrayValue array = ArrayValue.valueOf(data);
					if(array != null) return array;
				default:
					return new ArrayValue(0, new Value[]{value});
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Object}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectType extends AbstractType<Object> {

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
			final Object data = value.data();
			if(data == null) return ObjectType.NULL_DATA;
			return value.data();
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird ein {@link Object} als {@link Value} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType} ist, wird der {@link ObjectValue} unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Datensatz des {@link Value}{@code s} als {@link ObjectValue} zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see ObjectType#dataOf(Value)
		 * @see ObjectValue#valueOf(Object)
		 */
		@Override
		public ObjectValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return ObjectType.NULL_VALUE;
			if(value.type().id() == ObjectType.ID) return (ObjectValue)value;
			return new ObjectValue(data);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Function}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionType extends AbstractType<Function> {

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
		public int id() {
			return FunctionType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der {@link Value} vom Typ {@link FunctionType} und sein Datensatz nicht {@code null} sind, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der {@link Value} via {@link ValueFunction#valueOf(Value)} umgewandelt und zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Value)
		 */
		@Override
		public Function dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return FunctionType.NULL_DATA;
			if(value.type().id() == FunctionType.ID) return (Function)data;
			return ValueFunction.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der {@link Value} vom Typ {@link FunctionType} und sein Datensatz nicht {@code null} sind, wird der {@link Value} unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der {@link Value} via {@link ValueFunction#valueOf(Value)} und {@link FunctionValue#valueOf(Function)} umgewandelt und zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see FunctionType#dataOf(Value)
		 * @see FunctionValue#valueOf(Function)
		 */
		@Override
		public FunctionValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return FunctionType.NULL_VALUE;
			if(value.type().id() == FunctionType.ID) return (FunctionValue)value;
			return FunctionValue.valueOf(ValueFunction.valueOf(value));
		}

	}

	/**
	 * Diese Klasse implementiert den {@link String}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringType extends AbstractType<String> {

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
		public int id() {
			return StringType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code ""} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType}, {@link FunctionType}, {@link NumberType} oder {@link BooleanType} ist, wird sein Datensatz dieser via {@link String#valueOf(Object)} umgewandelt und zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, werden die Elemente via {@code Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data)))} umgewandelt und als {@link StringValue} zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public String dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return StringType.NULL_DATA;
			switch(value.type().id()){
				case ArrayType.ID:
					return Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data)));
				case StringType.ID:
					return (String)data;
				case ObjectType.ID:
				case FunctionType.ID:
				case NumberType.ID:
				case BooleanType.ID:
					return String.valueOf(data);
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code "null"} als {@link StringValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType}, {@link FunctionType}, {@link NumberType} oder {@link BooleanType} ist, wird sein Datensatz dieser via {@link String#valueOf(Object)} umgewandelt und als {@link StringValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird der {@link StringValue} unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, werden die Elemente via {@code Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])value.data())))} umgewandelt und als {@link StringValue} zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see StringType#dataOf(Value)
		 * @see StringValue#valueOf(String)
		 */
		@Override
		public StringValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return StringType.NULL_VALUE;
			switch(value.type().id()){
				case ArrayType.ID:
					return StringValue.valueOf(Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data))));
				case StringType.ID:
					return (StringValue)value;
				case ObjectType.ID:
				case FunctionType.ID:
				case NumberType.ID:
				case BooleanType.ID:
					return StringValue.valueOf(String.valueOf(data));
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberType extends AbstractType<Number> {

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
		public int id() {
			return NumberType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@code NaN} als {@link Double} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird dieser via {@link Double#valueOf(String)} umgewandelt und zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird {@code 0} als {@link Integer} nur bei dem Datensatz {@link Boolean#FALSE} zurück gegeben; Anderenfalls ist der Rückgabewert {@code 1} als {@link Integer}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public Number dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return NumberType.NULL_DATA;
			switch(value.type().id()){
				case StringType.ID:
					return Double.valueOf((String)data);
				case NumberType.ID:
					return (Number)data;
				case BooleanType.ID:
					return (((Boolean)data).booleanValue() ? NumberType.TRUE_DATA : NumberType.FALSE_DATA);
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@code NaN} als {@link Double} in inemem {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird sein Datensatz via {@link Double#valueOf(String)} umgewandelt und als {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird der {@link NumberValue} unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird {@code 0} als {@link Integer} in einem {@link NumberValue} nur bei dem Datensatz {@link Boolean#FALSE} zurück gegeben; Anderenfalls ist der Rückgabewert {@code 1} als {@link Integer} in einem {@link NumberValue}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see NumberType#dataOf(Value)
		 * @see NumberValue#valueOf(Number)
		 */
		@Override
		public NumberValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return NumberType.NULL_VALUE;
			switch(value.type().id()){
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

	}

	/**
	 * Diese Klasse implementiert den {@link Boolean}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanType extends AbstractType<Boolean> {

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
		public int id() {
			return BooleanType.ID;
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@link Boolean#FALSE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link Boolean#TRUE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird {@link Boolean#FALSE} nur bei einem leeren {@link String} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird {@link Boolean#FALSE} nur bei einer {@code 0}- bzw. {@code NaN}-{@link Number} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public Boolean dataOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
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
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} sind, wird {@link BooleanValue#FALSE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link BooleanValue#TRUE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird {@link BooleanValue#FALSE} nur bei einem leeren {@link String} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird {@link BooleanValue#FALSE} nur bei einer {@code 0}- bzw. {@code NaN}-{@link Number} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird der {@link BooleanValue} unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see BooleanType#dataOf(Value)
		 * @see BooleanValue#valueOf(Boolean)
		 */
		@Override
		public BooleanValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			final Object data = value.data();
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
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Types() {
	}

}
