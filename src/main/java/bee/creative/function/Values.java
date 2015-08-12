package bee.creative.function;

import bee.creative.function.Scripts.ScriptFormatter;
import bee.creative.function.Scripts.ScriptFormatterInput;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.BooleanType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Types.NullType;
import bee.creative.function.Types.NumberType;
import bee.creative.function.Types.ObjectType;
import bee.creative.function.Types.StringType;
import bee.creative.util.Converter;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert grundlegende Werte für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Number} und
 * {@link Boolean}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten Wert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BaseValue implements Value, ScriptFormatterInput {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final <GValue> GValue valueTo(final Type<GValue> type) throws NullPointerException, IllegalArgumentException {
			return Contexts.defaultContext.cast(this, type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final <GValue> GValue valueTo(final Type<GValue> type, final Context context) throws NullPointerException, ClassCastException,
			IllegalArgumentException {
			return context.cast(this, type);
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
			if (object == this) return true;
			if (!(object instanceof Value)) return false;
			final Value data = (Value)object;
			return Objects.equals(this.type(), data.type()) && Objects.equals(this.data(), data.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Scripts.scriptFormatter().formatValue(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Wert, dem zur Vollständigkeit nur noch der {@link #type() Datentyp} fehlt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class DataValue<GData> extends BaseValue {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		protected GData data;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@code null}.
		 */
		public DataValue() {
			this.data = null;
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public DataValue(final GData data) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			this.data = data;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GData data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.put(this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit einem
	 * gegebenen Ausführungskontext einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion
	 * berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf
	 * Ausführungskontext und Funktion aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ReturnValue extends BaseValue {

		// /**
		// * Diese Methode konvertiert den gegebenen Funktionsaufruf in einen {@link ReturnValue} und gibt diesen zurück.
		// *
		// * @param scope Ausführungskontext.
		// * @param function auszuwertende Funktion.
		// * @return {@link ReturnValue}.
		// * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		// */
		// public static ReturnValue valueOf(final Scope scope, final Function function) throws NullPointerException {
		// return new ReturnValue(scope, function);
		// }
		//
		// {}

		/**
		 * Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Value value;

		/**
		 * Dieses Feld speichert den Ausführungskontext zum Aufruf der Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Scope scope;

		/**
		 * Dieses Feld speichert die Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Function function;

		/**
		 * Dieser Konstruktor initialisiert Ausführungskontext und Funktion.
		 * 
		 * @param scope Ausführungskontext.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public ReturnValue(final Scope scope, final Function function) throws NullPointerException {
			if (scope == null) throw new NullPointerException("scope = null");
			if (function == null) throw new NullPointerException("function = null");
			this.scope = scope;
			this.function = function;
		}

		{}

		/**
		 * Diese Methode gibt den Ergebniswert der Ausführung der Funktion mit dem Ausführungskontext zurück.
		 * 
		 * @see Function#execute(Scope)
		 * @return Ergebniswert.
		 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
		 */
		public Value value() throws NullPointerException {
			Value result = this.value;
			if (result != null) return result;
			result = this.function.execute(this.scope);
			if (result == null) throw new NullPointerException("this.function().execute(this.scope()) = null");
			this.value = result;
			this.scope = null;
			this.function = null;
			return result;
		}

		/**
		 * Diese Methode gibt den Ausführungskontext oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt den Ausführungskontext auf {@code null}.
		 * 
		 * @return Ausführungskontext oder {@code null}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * Diese Methode gibt die Funktion oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public Function function() {
			return this.function;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
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
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			if (this.value != null) {
				target.putValue(this.value);
			} else {
				target.putScope(this.function).putParams(this.scope);
			}
		}

	}

	/**
	 * Diese Klasse implementiert den Wert zu {@code null}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends DataValue<Object> {

		/**
		 * Dieses Feld speichert den {@link NullValue}.
		 */
		public static final NullValue NULL = new NullValue();

		{}

		/**
		 * Diese Methode gibt den gegebenen Wert oder {@link NullValue#NULL} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullValue#NULL} geliefert.
		 * 
		 * @param value Wert oder {@code null}.
		 * @return Wert oder {@link NullValue#NULL}.
		 */
		public static Value valueOf(final Value value) {
			if (value == null) return NullValue.NULL;
			return value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NullType type() {
			return NullType.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit Wertlisten als Nutzdaten.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends DataValue<Array> {

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public ArrayValue(final Array data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayType type() {
			return ArrayType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putArray(this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit beliebigen Objekten als Nutzdaten.
	 * 
	 * @see ObjectType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends DataValue<Object> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectType type() {
			return ObjectType.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für Funktionen als Nutzdaten.
	 * 
	 * @see FunctionType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends DataValue<Function> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FunctionType type() {
			return FunctionType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
			target.putFunction(this.data);
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link String} als Nutzdaten.
	 * 
	 * @see StringType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends DataValue<String> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public StringType type() {
			return StringType.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Number} als Nutzdaten.
	 * 
	 * @see NumberType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends DataValue<Number> {

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NumberType type() {
			return NumberType.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Boolean} als Nutzdaten.
	 * 
	 * @see BooleanType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends DataValue<Boolean> {

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#TRUE}.
		 */
		public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE);

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#FALSE}.
		 */
		public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE);

		{}

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 */
		public static BooleanValue valueOf(final boolean data) {
			return (data ? BooleanValue.TRUE : BooleanValue.FALSE);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static BooleanValue valueOf(final Boolean data) throws NullPointerException {
			return BooleanValue.valueOf(data.booleanValue());
		}

		{}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			super(data);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BooleanType type() {
			return BooleanType.TYPE;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link Converter} zur Anpassung von {@link Values#valueOf(Object)}.
	 */
	static Converter<? super Object, ? extends Value> defaultConverter;

	{}

	/**
	 * Diese Methode konvertiert das gegebene Objekt in einen {@link Value Wert} und gibt diesen zurück.<br>
	 * Abhängig vom Datentyp des gegebenen Objekts kann hierfür automatisch ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue},
	 * {@link StringValue}, {@link NumberValue} oder {@link BooleanValue} verwendet werden.
	 * <ul>
	 * <li>Wenn der via {@link #setDefaultConverter(Converter)} registrierte {@link Converter} sowie das Ergebnis seiner {@link Converter#convert(Object)
	 * Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis geliefert.</li>
	 * <li>Wenn das Objekt {@code null} ist, wird {@link NullValue#NULL} geliefert.</li>
	 * <li>Wenn das Objekt ein {@link Value} ist, wird dieser unverändert geliefert.</li>
	 * <li>Wenn das Objekt ein {@link Array} ist, wird dieses als {@link ArrayValue} geliefert.</li>
	 * <li>Wenn das Objekt ein {@link String} ist, wird dieser als {@link StringValue} geliefert.</li>
	 * <li>Wenn das Objekt eine {@link Number} ist, wird dieser als {@link NumberValue} geliefert.</li>
	 * <li>Wenn das Objekt ein {@link Boolean} ist, wird dieser als {@link BooleanValue} geliefert.</li>
	 * <li>Wenn das Objekt eine {@link Function} ist, wird dieser als {@link FunctionValue} geliefert.</li>
	 * <li>Wenn das Objekt ein Array ist, wird dieses als {@link ArrayValue} geliefert.</li>
	 * <li>In allen anderen Fällen wird der Datensatz als {@link ObjectValue} geliefert.</li>
	 * </ul>
	 * 
	 * @see Converter#convert(Object)
	 * @see Array#from(Object)
	 * @see ArrayValue#ArrayValue(Array)
	 * @see StringValue#StringValue(String)
	 * @see NumberValue#NumberValue(Number)
	 * @see BooleanValue#valueOf(Boolean)
	 * @see FunctionValue#FunctionValue(Function)
	 * @see ObjectValue#valueOf(Object)
	 * @param data Datensatz oder {@code null}.
	 * @return {@link Value}.
	 */
	public static Value valueOf(final Object data) {
		final Converter<? super Object, ? extends Value> converter = Values.defaultConverter;
		if (converter != null) {
			final Value value = converter.convert(data);
			if (value != null) return value;
		}
		if (data == null) return NullValue.NULL;
		if (data instanceof Value) return (Value)data;
		if (data instanceof Array) return new ArrayValue((Array)data);
		if (data instanceof String) return new StringValue((String)data);
		if (data instanceof Number) return new NumberValue((Number)data);
		if (data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
		if (data instanceof Function) return new FunctionValue((Function)data);
		final Array array = Array.from(data);
		if (array != null) return new ArrayValue(array);
		return new ObjectValue(data);
	}

	/**
	 * Diese Methode gibt den {@code default}-{@link Converter} zur Anpassung von {@link Values#valueOf(Object)} zurück.
	 * 
	 * @return {@code default}-{@link Converter} oder {@code null}.
	 */
	public static Converter<? super Object, ? extends Value> getDefaultConverter() {
		return Values.defaultConverter;
	}

	/**
	 * Diese Methode setzt den {@code default}-{@link Converter} zur Anpassung von {@link Values#valueOf(Object)}.
	 * 
	 * @param value {@code default}-{@link Converter} oder {@code null}.
	 */
	public static void setDefaultConverter(final Converter<? super Object, ? extends Value> value) {
		Values.defaultConverter = value;
	}

}
