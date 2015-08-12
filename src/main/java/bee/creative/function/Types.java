package bee.creative.function;

import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.BooleanValue;
import bee.creative.function.Values.DataValue;
import bee.creative.function.Values.FunctionValue;
import bee.creative.function.Values.NullValue;
import bee.creative.function.Values.NumberValue;
import bee.creative.function.Values.ObjectValue;
import bee.creative.function.Values.StringValue;
import bee.creative.util.Converter;

/**
 * Diese Klasse implementiert Datentypen für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Number} und
 * {@link Boolean}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Types {

	/**
	 * Diese Klasse implementiert einen abstrakten Datentyp, dem zur Vollständigkeit nur noch die Methode {@link #id()} fehlt. {@link Object#hashCode() Streuwert}
	 * und {@link Object#equals(Object) Äquivalenz} beruhen auf dem {@link Type#id() Identifikator}. Des Weiteren ist Der Datentyp kann als {@link Converter}
	 * verwendet werden, welcher einen beliebigen Wert zuerst in einen {@code GValue} umwandelt und anschließend dessen Nutzdaten als {@code GData} liefert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten des Werts.
	 * @param <GValue> Typ des Werts, in welchen ein gegebener Wert via {@link #valueOf(Value)} oder {@link #valueOf(Value, Context)} konvertiert werden kann.
	 */
	public static abstract class BaseType<GData, GValue extends DataValue<GData>> implements Type<GValue>, Converter<Value, GData> {

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
		public final GValue valueOf(final Value value) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return this.valueOf(value, Contexts.defaultContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GValue valueOf(final Value value, final Context context) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return context.cast(value, this);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert über {@link #valueOf(Value)} in den Wert dieses Datentyps und gibt dessen Nutzdaten zurück.
		 */
		@Override
		public GData convert(final Value input) {
			return this.valueOf(input).data();
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
			if (object == this) return true;
			if (!(object instanceof Type<?>)) return false;
			final Type<?> data = (Type<?>)object;
			return this.id() == data.id();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getClass().getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@code null}.
	 * 
	 * @see NullValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullType extends BaseType<Object, NullValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 0;
		/**
		 * Dieses Feld speichert den {@link NullType}.
		 */
		public static final NullType TYPE = new NullType();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return NullType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "null";
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Value}{@code []}.
	 * 
	 * @see ArrayValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayType extends BaseType<Array, ArrayValue> {

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

		{}

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
		public String toString() {
			return Array.class.getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Object}.
	 * 
	 * @see ObjectValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectType extends BaseType<Object, ObjectValue> {

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

		{}

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
		public String toString() {
			return Object.class.getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Function}.
	 * 
	 * @see FunctionValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionType extends BaseType<Function, FunctionValue> {

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

		{}

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
		public String toString() {
			return Function.class.getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link String}.
	 * 
	 * @see StringValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringType extends BaseType<String, StringValue> {

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

		{}

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
		public String toString() {
			return String.class.getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Number}.
	 * 
	 * @see NumberValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberType extends BaseType<Number, NumberValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 5;
		/**
		 * Dieses Feld speichert den {@link NumberType}.
		 */
		public static final NumberType TYPE = new NumberType();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return NumberType.ID;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Number.class.getSimpleName();
		}

	}

	/**
	 * Diese Klasse implementiert den Datentyp für {@link Boolean}.
	 * 
	 * @see BooleanValue
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanType extends BaseType<Boolean, BooleanValue> {

		/**
		 * Dieses Feld speichert den Identifikator.
		 * 
		 * @see Type#id()
		 */
		public static final int ID = 6;
		/**
		 * Dieses Feld speichert den {@link BooleanType}.
		 */
		public static final BooleanType TYPE = new BooleanType();

		{}

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
		public String toString() {
			return Boolean.class.getSimpleName();
		}

	}

}
