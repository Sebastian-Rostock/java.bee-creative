package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Functions.NullFunction;
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
		 * Dieses Feld speichert den {@link NullType}.
		 */
		public static final NullType INSTANCE = new NullType();

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		NullType() {
		}

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
		public Object dataOf(final Value value) throws IllegalArgumentException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see NullType#dataOf(Value)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
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
		 * Dieses Feld speichert den {@link ArrayType}.
		 */
		public static final ArrayType INSTANCE = new ArrayType();

		/**
		 * Dieses Feld speichert das leere {@link Value}{@code []}.
		 */
		static final Value[] NULL_DATA = {};

		/**
		 * Dieses Feld speichert das leere {@link Value}{@code []} als {@link Value}.
		 */
		static final Value NULL_VALUE = ArrayValue.valueOf(ArrayType.NULL_DATA);

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		ArrayType() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return ArrayType.ID;
		}

		/**
		 * {@inheritDoc} TODO
		 */
		@Override
		public Value[] dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return ArrayType.NULL_DATA;
			final Object data = value.data();
			if(data == null) return ArrayType.NULL_DATA;
			if(value.type().id() == ArrayType.ID) return (Value[])data;
			return new Value[]{value};
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see ArrayType#dataOf(Value)
		 * @see ArrayValue#valueOf(Value...)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return ArrayType.NULL_VALUE;
			final Object data = value.data();
			if(data == null) return ArrayType.NULL_VALUE;
			if(value.type().id() == ArrayType.ID) return value;
			return ArrayValue.valueOf(value);
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
		 * Dieses Feld speichert den {@link ObjectType}.
		 */
		public static final ObjectType INSTANCE = new ObjectType();

		/**
		 * Dieses Feld speichert ein {@link Object}.
		 */
		static final Object NULL_DATA = new Object();

		/**
		 * Dieses Feld speichert ein {@link Object} als {@link Value}.
		 */
		static final Value NULL_VALUE = ObjectValue.valueOf(ObjectType.NULL_DATA);

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		ObjectType() {
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
		 */
		@Override
		public Object dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return ObjectType.NULL_DATA;
			final Object data = value.data();
			if(data == null) return ObjectType.NULL_DATA;
			return value.data();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see ObjectType#dataOf(Value)
		 * @see ObjectValue#valueOf(Object)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return ObjectType.NULL_VALUE;
			final Object data = value.data();
			if(data == null) return ObjectType.NULL_VALUE;
			if(value.type().id() == ObjectType.ID) return value;
			return ObjectValue.valueOf(data);
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
		 * Dieses Feld speichert den {@link FunctionType}.
		 */
		public static final FunctionType INSTANCE = new FunctionType();

		/**
		 * Dieses Feld speichert ein {@link Object} als {@link Value}.
		 */
		static final Value NULL_VALUE = FunctionValue.valueOf(NullFunction.INSTANCE);

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		FunctionType() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return FunctionType.ID;
		}

		/**
		 * {@inheritDoc} TODO
		 * 
		 * @see ValueFunction#valueOf(Value)
		 */
		@Override
		public Function dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return NullFunction.INSTANCE;
			final Object data = value.data();
			if(data == null) return NullFunction.INSTANCE;
			if(value.type().id() == FunctionType.ID) return (Function)data;
			return ValueFunction.valueOf(value);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see FunctionType#dataOf(Value)
		 * @see FunctionValue#valueOf(Function)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return FunctionType.NULL_VALUE;
			final Object data = value.data();
			if(data == null) return FunctionType.NULL_VALUE;
			if(value.type().id() == FunctionType.ID) return value;
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
		 * Dieses Feld speichert den {@link StringType}.
		 */
		public static final StringType INSTANCE = new StringType();

		/**
		 * Dieses Feld speichert {@code ""} als {@link Double}.
		 */
		static final String NULL_DATA = "";

		/**
		 * Dieses Feld speichert {@code ""} als {@link Value}.
		 */
		static final Value NULL_VALUE = StringValue.valueOf(StringType.NULL_DATA);

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		StringType() {
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
		 * <li>Wenn der {@link Value} oder sein Datensatz {@code null} ist, wird {@code ""} zurück gegeben.</li>
		 * <li>Wenn der Datensatz des {@link Value}{@code s} {@code null} ist, wird {@code "null"} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ObjectType}, {@link FunctionType}, {@link NumberType} oder {@link BooleanType} ist, wird dieser via {@link String#valueOf(Object)} umgewandelt und zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType} ist, .</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public String dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return StringType.NULL_DATA;
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
		 * 
		 * @see StringType#dataOf(Value)
		 * @see StringValue#valueOf(String)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return StringType.NULL_VALUE;
			final Object data = value.data();
			if(data == null) return StringType.NULL_VALUE;
			switch(value.type().id()){
				case ArrayType.ID:
					return StringValue.valueOf(Strings.join(", ", Iterables.convertedIterable(this, Arrays.asList((Value[])data))));
				case StringType.ID:
					return value;
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
		 * Dieses Feld speichert den {@link NumberType}.
		 */
		public static final NumberType INSTANCE = new NumberType();

		/**
		 * Dieses Feld speichert {@code NaN} als {@link Double}.
		 */
		static final Number NULL_DATA = Double.valueOf(Double.NaN);

		/**
		 * Dieses Feld speichert {@code NaN} als {@link Value}.
		 */
		static final Value NULL_VALUE = NumberValue.valueOf(NumberType.NULL_DATA);

		/**
		 * Dieses Feld speichert {@code 1} als {@link Integer}.
		 */
		static final Number TRUE_DATA = Integer.valueOf(1);

		/**
		 * Dieses Feld speichert {@code 1} als {@link Value}.
		 */
		static final Value TRUE_VALUE = NumberValue.valueOf(NumberType.TRUE_DATA);

		/**
		 * Dieses Feld speichert {@code 0} als {@link Integer}.
		 */
		static final Number FALSE_DATA = Integer.valueOf(0);

		/**
		 * Dieses Feld speichert {@code 0} als {@link Value}.
		 */
		static final Value FALSE_VALUE = NumberValue.valueOf(NumberType.FALSE_DATA);

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		NumberType() {
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
		 * <li>Wenn der {@link Value} oder sein Datensatz {@code null} sind, wird {@code NaN} als {@link Double} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird dieser via {@link Double#valueOf(String)} umgewandelt und zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird {@code 0} als {@link Integer} nur nur bei dem Datensatz {@link Boolean#FALSE} zurück gegeben; Anderenfalls ist der Rückgabewert {@code 1} als {@link Integer}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public Number dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return NumberType.NULL_DATA;
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
		 * 
		 * @see NumberType#dataOf(Value)
		 * @see NumberValue#valueOf(Number)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return NumberType.NULL_VALUE;
			final Object data = value.data();
			if(data == null) return NumberType.NULL_VALUE;
			switch(value.type().id()){
				case StringType.ID:
					return NumberValue.valueOf(Double.valueOf((String)data));
				case NumberType.ID:
					return value;
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
		 * Dieses Feld speichert den {@link BooleanType}.
		 */
		public static final BooleanType INSTANCE = new BooleanType();

		/**
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		BooleanType() {
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
		 * <li>Wenn der {@link Value} oder sein Datensatz {@code null} sind, wird {@link Boolean#FALSE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link ArrayType}, {@link ObjectType} oder {@link FunctionType} ist, wird {@link Boolean#TRUE} zurück gegeben.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link StringType} ist, wird {@link Boolean#FALSE} nur bei einem leeren {@link String} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link NumberType} ist, wird {@link Boolean#FALSE} nur bei einer {@code 0}- bzw. {@code NaN}-{@link Number} als Datensatz zurück gegeben; Anderenfalls ist der Rückgabewert {@link Boolean#TRUE}.</li>
		 * <li>Wenn der {@link Value} vom Typ {@link BooleanType} ist, wird sein Datensatz unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 */
		@Override
		public Boolean dataOf(final Value value) throws IllegalArgumentException {
			if(value == null) return Boolean.FALSE;
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
		 * 
		 * @see BooleanType#dataOf(Value)
		 * @see BooleanValue#valueOf(Boolean)
		 */
		@Override
		public Value valueOf(final Value value) throws ClassCastException, IllegalArgumentException {
			if(value == null) return BooleanValue.FALSE;
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
					return value;
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
