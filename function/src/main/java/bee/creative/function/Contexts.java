package bee.creative.function;

import java.math.BigDecimal;
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
		 * {@inheritDoc} Hierfür wird auf dem {@link Type#id() Identifikator} des gegebenen Datentyps eine Fallunterscheidung durchgeführt und eine der
		 * spezialisierten Konvertierunsmethoden aufgerufen. Bei einem unbekannten Datentyp wird eine {@link IllegalArgumentException} ausgelöst.
		 * 
		 * @see #castToNullValue(Value)
		 * @see #castToArrayValue(Value)
		 * @see #castToObjectValue(Value)
		 * @see #castToFunctionValue(Value)
		 * @see #castToStringValue(Value)
		 * @see #castToNumberValue(Value)
		 * @see #castToBooleanValue(Value)
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public <GValue> GValue cast(final Value value, final Type<GValue> type) throws NullPointerException, ClassCastException, IllegalArgumentException {
			switch (type.id()) {
				case NullType.ID:
					return (GValue)this.castToNullValue(value);
				case ArrayType.ID:
					return (GValue)this.castToArrayValue(value);
				case ObjectType.ID:
					return (GValue)this.castToObjectValue(value);
				case FunctionType.ID:
					return (GValue)this.castToFunctionValue(value);
				case StringType.ID:
					return (GValue)this.castToStringValue(value);
				case NumberType.ID:
					return (GValue)this.castToNumberValue(value);
				case BooleanType.ID:
					return (GValue)this.castToBooleanValue(value);
			}
			throw new IllegalArgumentException();
		}

		/**
		 * Diese Methode gibt {@link NullValue#INSTANCE} zurück.
		 * 
		 * @param value Wert.
		 * @return {@link NullValue#INSTANCE}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		protected NullValue castToNullValue(final Value value) throws NullPointerException {
			if (value == null) throw new NullPointerException();
			return NullValue.INSTANCE;
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link ArrayValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird ein leeres Array zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein {@link ArrayValue} zurück gegeben, das den gegebenen Wert als einziges Element enthält.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		protected ArrayValue castToArrayValue(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case NullType.ID:
					return ArrayValue.valueOf(Array.EMPTY);
				case ArrayType.ID:
					return (ArrayValue)value;
				default:
					return ArrayValue.valueOf(Array.valueOf(value));
			}
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link ObjectValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn die Nutzdaten des Werts {@code null} sind, wird ein {@link NullPointerException} ausgelöst.</li>
		 * <li>Wenn der Wert ein {@link ObjectValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird ein neuer {@link ObjectValue} mit den Nutzdaten des gegebenen Werts zurück gegeben.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return {@link ObjectValue}.
		 * @throws NullPointerException Wenn der gegebene Wert oder dessen Nutzdaten {@code null} sind.
		 */
		protected ObjectValue castToObjectValue(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case ObjectType.ID:
					return (ObjectValue)value;
				default:
					return ObjectValue.valueOf(value.data());
			}
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link FunctionValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link FunctionValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>In allen anderen Fällen wird der Wert via {@link ValueFunction#valueOf(Value)} und {@link FunctionValue#valueOf(Function)} umgewandelt und zurück
		 * gegeben.</li>
		 * </ul>
		 * 
		 * @see ValueFunction#valueOf(Value)
		 * @see FunctionValue#valueOf(Function)
		 * @param value Wert.
		 * @return {@link FunctionValue}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		protected FunctionValue castToFunctionValue(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case FunctionType.ID:
					return (FunctionValue)value;
				default:
					return FunctionValue.valueOf(ValueFunction.valueOf(value));
			}
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link StringValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@code "null"} als {@link StringValue} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, werden die mit dieser Methode in {@link String Zeichenketten} umgewandelten Elemente des {@link Array}s mit
		 * dem Trennzeichen {@code ", "} verkettet und als {@link StringValue} zurück gegeben.</li>
		 * <li>In allen anderen Fällen derden die Nutzdaten des Werts via {@link String#valueOf(Object)} in eine Zeichenkette umgewandelt und als
		 * {@link StringValue} zurück gegeben.</li>
		 * </ul>
		 * 
		 * @see String#valueOf(Object)
		 * @param value Wert.
		 * @return {@link StringValue}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		protected StringValue castToStringValue(final Value value) throws NullPointerException {
			switch (value.type().id()) {
				case NullType.ID:
					return StringValue.valueOf("");
				case StringType.ID:
					return (StringValue)value;
				case ArrayType.ID:
					return StringValue.valueOf(Strings.join(", ", Iterables.convertedIterable(StringValue.TYPE, ((ArrayValue)value).data)));
				default:
					return StringValue.valueOf(String.valueOf(value.data()));
			}
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link NumberValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@code NaN} als {@link NumberValue} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, werden seine Nutzdaten via {@link BigDecimal#BigDecimal(String)} interpretiert und als {@link NumberValue}
		 * zurück gegeben. Hierbei kann eine {@link IllegalArgumentException} ausgelöst werden.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird bei {@code false} eine {@code 0} bzw. bei {@code true} eine {@code 1} als {@link NumberValue} zurück
		 * gegeben.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return {@link NumberValue}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
		 */
		protected NumberValue castToNumberValue(final Value value) throws NullPointerException, IllegalArgumentException {
			switch (value.type().id()) {
				case NullType.ID:
					return NumberValue.valueOf(Double.NaN);
				case StringType.ID:
					return NumberValue.valueOf(new BigDecimal(((StringValue)value).data));
				case NumberType.ID:
					return (NumberValue)value;
				case BooleanType.ID:
					return NumberValue.valueOf(((BooleanValue)value).data.booleanValue() ? 1 : 0);
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wert in einen {@link BooleanValue} und gibt diesen zurück.
		 * <ul>
		 * <li>Wenn der Wert ein {@link NullValue} ist, wird {@link BooleanValue#FALSE} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link BooleanValue} ist, wird er unverändert zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link ArrayValue} ist, wird {@link BooleanValue#FALSE} nur bei einem leeren {@link Array} als Nutzdaten zurück gegeben;
		 * Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der Wert ein {@link ObjectValue} oder {@link FunctionValue} ist, wird {@link BooleanValue#TRUE} zurück gegeben.</li>
		 * <li>Wenn der Wert ein {@link StringValue} ist, wird {@link BooleanValue#FALSE} nur bei einem leeren {@link String} als Nutzdaten zurück gegeben;
		 * Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>Wenn der Wert ein {@link NumberValue} ist, wird {@link BooleanValue#FALSE} nur bei {@code 0} bzw. {@code NaN} als Nutzdaten zurück gegeben;
		 * Anderenfalls ist der Rückgabewert {@link BooleanValue#TRUE}.</li>
		 * <li>In allen anderen Fällen wird eine {@link IllegalArgumentException} ausgelöst.</li>
		 * </ul>
		 * 
		 * @param value Wert.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Wert nicht konvertiert werden kann.
		 */
		protected BooleanValue castToBooleanValue(final Value value) throws NullPointerException, IllegalArgumentException {
			switch (value.type().id()) {
				case NullType.ID:
					return BooleanValue.FALSE;
				case ArrayType.ID:
					return BooleanValue.valueOf(((ArrayValue)value).data.length() != 0);
				case ObjectType.ID:
				case FunctionType.ID:
					return BooleanValue.TRUE;
				case StringType.ID:
					return BooleanValue.valueOf(((StringValue)value).data.length() != 0);
				case NumberType.ID:
					return BooleanValue.valueOf(((NumberValue)value).data.intValue() != 0);
				case BooleanType.ID:
					return (BooleanValue)value;
				default:
					throw new IllegalArgumentException();
			}
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
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
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static void setDefaultContext(final Context value) throws NullPointerException {
		if (value == null) throw new NullPointerException();
		Contexts.defaultContext = value;
	}

}
