package bee.creative.function;

import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.BooleanType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Types.NullType;
import bee.creative.function.Types.NumberType;
import bee.creative.function.Types.ObjectType;
import bee.creative.function.Types.StringType;
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
 * Diese Klasse implementiert die Verwaltung des {@link #getDefaultContext() default}-{@link Context}s.
 * 
 * @see #getDefaultContext()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Contexts {

	/**
	 * Diese Klasse implementiert den {@link Context} mit den allgemeinen Regeln zur Umwandlung von Werten in andere Datentypen.
	 * 
	 * @see #cast(Value, Type)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DefaultContext implements Context {

		/**
		 * Dieses Feld speichert den {@link DefaultContext}.
		 */
		public static final DefaultContext INSTANCE = new DefaultContext();

		{}

		/**
		 * Dieses Feld speichert den {@link Converter} zu {@link #castToString(Value)}.
		 */
		protected final Converter<Value, String> castToString = new Converter<Value, String>() {

			@Override
			public String convert(final Value input) {
				return DefaultContext.this.castToString(input);
			}

		};

		/**
		 * {@inheritDoc}
		 * <ul>
		 * <li>Wenn der {@link Value#type() Datentyp} des gegebenen Werts gleich dem gegebenen Datentyp ist, wird der Wert unverändert geliefert.</li>
		 * <li>Andernfalls wird auf dem {@link Type#id() Identifikator} des gegebenen Datentyps eine Fallunterscheidung durchgeführt und eine der spezialisierten
		 * Konvertierunsmethoden aufgerufen.<br>
		 * Bei einem unbekannten Datentyp wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @see #castToArray(Value)
		 * @see #castToObject(Value)
		 * @see #castToFunction(Value)
		 * @see #castToString(Value)
		 * @see #castToNumber(Value)
		 * @see #castToBoolean(Value)
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public <GValue> GValue cast(final Value value, final Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			if (type == value.type()) return (GValue)value;
			switch (type.id()) {
				case ArrayType.ID:
					return (GValue)new ArrayValue(this.castToArray(value));
				case ObjectType.ID:
					return (GValue)new ObjectValue(this.castToObject(value));
				case FunctionType.ID:
					return (GValue)new FunctionValue(this.castToFunction(value));
				case StringType.ID:
					return (GValue)new StringValue(this.castToString(value));
				case NumberType.ID:
					return (GValue)new NumberValue(this.castToNumber(value));
				case BooleanType.ID:
					return (GValue)BooleanValue.valueOf(this.castToBoolean(value));
			}
			throw new IllegalArgumentException();
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in eine Wertliste und gibt diese zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird die leere Wertliste geliefert.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, wird dessen Wertliste unverändert geliefert.</li>
		 * <li>In allen anderen Fällen wird eine Wertliste geliefert, die den gegebenen Wert als einziges Element enthält.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return Wertliste.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		protected Array castToArray(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case NullType.ID:
					return Array.EMPTY;
				case ArrayType.ID:
					return (Array)value.data();
			}
			return Array.valueOf(value);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert ein Objekt und gibt diesen zurück.
		 * <ul>
		 * <li>Es werden immer die Nutzdaten des des gegebenen Werts unverändert geliefert.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return Objekt.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 */
		protected Object castToObject(final Value value) throws NullPointerException {
			final Object data = value.data();
			if (data == null) throw new NullPointerException("data = null");
			return data;
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in eine Funktion und gibt diese zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link FunctionValue} ist, wird deren Funktion unverändert geliefert.</li>
		 * <li>Andernfalls wird die via {@link ValueFunction#valueOf(Object)} aus dem Wert erzeugte Funktion geliefert.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Object)
		 * @param value Wert.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		protected Function castToFunction(final Value value) throws NullPointerException {
			if (value.type().id() == FunctionType.ID) return (Function)value.data();
			return ValueFunction.valueOf(value);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in eine Zeichenkette und gibt diese zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird eine leere Zeichenkette geliefert.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, wird dessen Zeichenkette unverändert geliefert.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, wird eine Zeichenkette geliefert, die aus der das Trennzeichen {@code ", "} verwendenden Verkettung der mit
		 * dieser Methode umgewandelten Werte der Wertliste ersteht.</li>
		 * <li>In allen anderen Fällen wird eine Zeichenkette geliefert, die aus den Nutzdaten des Werts via {@link String#valueOf(Object)} entsteht.</li>
		 * </ul>
		 * 
		 * @see String#valueOf(Object)
		 * @param value Wert.
		 * @return Zeichenkette.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		protected String castToString(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case NullType.ID:
					return "";
				case StringType.ID:
					return (String)value.data();
				case ArrayType.ID:
					return Strings.join(", ", Iterables.convertedIterable(this.castToString, (Array)value.data()));
			}
			return String.valueOf(value.data());
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen Zahlenwert und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@link Double#NaN} geliefert.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird dessen Zahlenwert unverändert geliefert.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, wird ein Zahlenwert geliefert, der via {@link Double#valueOf(String)} interpretiert wird.<br>
		 * Hierbei kann eine {@link IllegalArgumentException} ausgelöst werden.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird bei {@code false} eine {@code 0} bzw. bei {@code true} eine {@code 1} als {@link Integer} geliefert.
		 * </li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return Zahlenwert.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
		 */
		protected Number castToNumber(final Value value) throws NullPointerException, IllegalArgumentException {
			switch (value.type().id()) {
				case NullType.ID:
					return Double.valueOf(Double.NaN);
				case StringType.ID:
					return Double.valueOf((String)value.data());
				case NumberType.ID:
					return (Number)value.data();
				case BooleanType.ID:
					return ((Boolean)value.data()).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen Wahrheitswert und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@link Boolean#FALSE} geliefert.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird dessen Wahrheitswert unverändert geliefert.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} oder ein {@link StringValue} ist, wird {@link Boolean#FALSE} nur bei einer leeren Wertliste bzw. Zeichenkette
		 * geliefert. Anderenfalls wird {@link Boolean#TRUE} geliefert.</li>
		 * <li>Wenn der Wert ein {@link ObjectValue} oder ein {@link FunctionValue} ist, wird {@link Boolean#TRUE} geliefert.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird {@link Boolean#FALSE} nur bei {@code 0} bzw. {@code NaN} geliefert. Anderenfalls wird
		 * {@link Boolean#TRUE} geliefert.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return Wahrheitswert.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
		 */
		protected Boolean castToBoolean(final Value value) throws NullPointerException, IllegalArgumentException {
			switch (value.type().id()) {
				case NullType.ID:
					return Boolean.FALSE;
				case ArrayType.ID:
					return Boolean.valueOf(((Array)value.data()).length() != 0);
				case ObjectType.ID:
				case FunctionType.ID:
					return Boolean.TRUE;
				case StringType.ID:
					return Boolean.valueOf(((String)value.data()).length() != 0);
				case NumberType.ID:
					return Boolean.valueOf(((Number)value.data()).intValue() != 0);
				case BooleanType.ID:
					return (Boolean)value.data();
			}
			throw new IllegalArgumentException();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this);
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link Context}.
	 */
	static Context defaultContext = DefaultContext.INSTANCE;

	/**
	 * Diese Methode gibt den {@code default}-{@link Context} zurück, der in den Methoden {@link Value#valueTo(Type)} und {@link Type#valueOf(Value)} zur
	 * kontextfreien Umwandlung von Werten verwendet wird.
	 * 
	 * @return {@code default}-{@link Context}.
	 */
	public static Context getDefaultContext() {
		return Contexts.defaultContext;
	}

	/**
	 * Diese Methode setzt den {@code default}-{@link Context}.
	 * 
	 * @see #getDefaultContext()
	 * @param value neuer {@code default}-{@link Context}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static void setDefaultContext(final Context value) throws NullPointerException {
		if (value == null) throw new NullPointerException();
		Contexts.defaultContext = value;
	}

}
