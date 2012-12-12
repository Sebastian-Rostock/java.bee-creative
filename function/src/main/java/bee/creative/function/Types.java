package bee.creative.function;

import bee.creative.function.Functions.ValueFunction;
import bee.creative.util.Converter;
import bee.creative.util.Objects;

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
		public boolean is(final Type<?> type) throws NullPointerException {
			return (type == this) || (type.id() == this.id());
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
	public static final class VoidType extends AbstractType<Object> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 0;

		/**
		 * Dieses Feld speichert den {@link VoidType}.
		 */
		public static final VoidType TYPE = new VoidType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return VoidType.ID;
		}

		/**
		 * {@inheritDoc} <br>
		 * Der Rückgabewert ist immer {@code null}.
		 */
		@Override
		public Object dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			if(value == null) throw new NullPointerException();
			return null;
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
		public static final ArrayType TYPE = new ArrayType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return ArrayType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value[] dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case ArrayType.ID:
					return (Value[])data;
				default:
					return new Value[]{value};
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
		 * Dieses Feld speichert den {@link ObjectType}.
		 */
		public static final ObjectType TYPE = new ObjectType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return ObjectType.ID;
		}

		/**
		 * {@inheritDoc} <br>
		 * Der Rückgabewert ist immer {@code value.data()}.
		 */
		@Override
		public Object dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			return value.data();
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
		public static final FunctionType TYPE = new FunctionType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return FunctionType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case FunctionType.ID:
					return (Function)data;
				default:
					return new ValueFunction(value);
			}
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
		public static final StringType TYPE = new StringType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return StringType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case StringType.ID:
				case IntegerType.ID:
				case LongType.ID:
				case FloatType.ID:
				case DoubleType.ID:
				case BooleanType.ID:
					return data.toString();
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Integer}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IntegerType extends AbstractType<Integer> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 5;

		/**
		 * Dieses Feld speichert den {@link IntegerType}.
		 */
		public static final IntegerType TYPE = new IntegerType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return IntegerType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Integer dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case IntegerType.ID:
					return (Integer)data;
				case LongType.ID:
				case FloatType.ID:
				case DoubleType.ID:
					return Integer.valueOf(((Number)data).intValue());
				case StringType.ID:
					return Integer.valueOf((String)data);
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Long}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LongType extends AbstractType<Long> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 6;

		/**
		 * Dieses Feld speichert den {@link LongType}.
		 */
		public static final LongType TYPE = new LongType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return LongType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Long dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case LongType.ID:
					return (Long)data;
				case IntegerType.ID:
				case FloatType.ID:
				case DoubleType.ID:
					return Long.valueOf(((Number)data).longValue());
				case StringType.ID:
					return Long.valueOf((String)data);
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Float}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FloatType extends AbstractType<Float> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 7;

		/**
		 * Dieses Feld speichert den {@link FloatType}.
		 */
		public static final FloatType TYPE = new FloatType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return FloatType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Float dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case FloatType.ID:
					return (Float)data;
				case LongType.ID:
				case IntegerType.ID:
				case DoubleType.ID:
					return Float.valueOf(((Number)data).floatValue());
				case StringType.ID:
					return Float.valueOf((String)data);
				default:
					throw new IllegalArgumentException();
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Double}-{@link Type}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DoubleType extends AbstractType<Double> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 8;

		/**
		 * Dieses Feld speichert den {@link DoubleType}.
		 */
		public static final DoubleType TYPE = new DoubleType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return IntegerType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Double dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case DoubleType.ID:
					return (Double)data;
				case LongType.ID:
				case IntegerType.ID:
				case FloatType.ID:
					return Double.valueOf(((Number)data).doubleValue());
				case StringType.ID:
					return Double.valueOf((String)data);
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
		public static final int ID = 9;

		/**
		 * Dieses Feld speichert den {@link BooleanType}.
		 */
		public static final BooleanType TYPE = new BooleanType();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return BooleanType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean dataOf(final Value value) throws NullPointerException, IllegalArgumentException {
			final Object data = value.data();
			if(data == null) return null;
			switch(value.type().id()){
				case BooleanType.ID:
					return (Boolean)data;
				case StringType.ID:
					return Boolean.valueOf((String)data);
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
