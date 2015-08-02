package bee.creative.function;

import bee.creative.function.Scopes.CompositeScope;
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
 * Diese Klasse implementiert {@link Value}s für {@code null}-, {@link Value}{@code []}-, {@link Object}-, {@link Function}-, {@link String}-, {@link Number}-
 * und {@link Boolean}-Nutzdaten.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten Wert, dem zur Vollständigkeit nur noch der {@link #type() Datentyp} fehlt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class BaseValue<GData> implements Value {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		protected GData data;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit {@code null}.
		 */
		public BaseValue() {
			this.data = null;
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public BaseValue(final GData data) throws NullPointerException {
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
		 * 
		 * @see Objects#hash(Object)
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.data());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Objects#equals(Object, Object)
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
		public String toString() {
			return String.valueOf(this.data());
		}

	}

	/**
	 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit einem
	 * gegebenen Ausführungskontext einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion
	 * berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf
	 * Ausführungskontext und Funktion aufgelöst.
	 * 
	 * @see CompositeScope
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LazyValue implements Value {

		/**
		 * Diese Methode konvertiert den gegebenen Funktionsaufruf in einen {@link LazyValue} und gibt diesen zurück.
		 * 
		 * @param scope Ausführungskontext.
		 * @param function auszuwertende Funktion.
		 * @return {@link LazyValue}.
		 * @throws NullPointerException Wenn {@code scope} bzw. {@code function} {@code null} ist.
		 */
		public static LazyValue valueOf(final Scope scope, final Function function) throws NullPointerException {
			return new LazyValue(scope, function);
		}

		{}

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
		public LazyValue(final Scope scope, final Function function) throws NullPointerException {
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
		public <GValue> GValue valueTo(final Type<GValue> type) throws NullPointerException, IllegalArgumentException {
			return this.value().valueTo(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> GValue valueTo(final Type<GValue> type, final Context context) throws NullPointerException, ClassCastException, IllegalArgumentException {
			return this.value().valueTo(type, context);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.value().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Value)) return false;
			return this.value().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (this.value != null) return Objects.toFormatString(true, true, this, "value", this.value);
			return Objects.toFormatString(true, true, this, "scope", this.scope, "function", this.function);
		}

	}

	/**
	 * Diese Klasse implementiert den Wert zu {@code null}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends BaseValue<Object> {

		/**
		 * Dieses Feld speichert den {@link NullType}.
		 */
		public static final NullType TYPE = new NullType();

		/**
		 * Dieses Feld speichert den {@link NullValue}.
		 */
		public static final NullValue INSTANCE = new NullValue();

		{}

		/**
		 * Diese Methode gibt den gegebenen Wert oder {@link NullValue#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullValue#INSTANCE} zurück
		 * gegeben.
		 * 
		 * @param value Wert oder {@code null}.
		 * @return Wert oder {@link NullValue#INSTANCE}.
		 */
		public static Value valueOf(final Value value) {
			if (value == null) return NullValue.INSTANCE;
			return value;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NullType type() {
			return NullValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit Wertlisten als Nutzdaten.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends BaseValue<Array> {

		/**
		 * Dieses Feld speichert den {@link ArrayType}.
		 */
		public static final ArrayType TYPE = new ArrayType();

		{}

		/**
		 * Diese Methode konvertiert die gegebene Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static ArrayValue valueOf(final Array data) throws NullPointerException {
			return new ArrayValue(data);
		}

		{}

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
			return ArrayValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit beliebigen Objekten als Nutzdaten.
	 * 
	 * @see ObjectType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends BaseValue<Object> {

		/**
		 * Dieses Feld speichert den {@link ObjectType}.
		 */
		public static final ObjectType TYPE = new ObjectType();

		{}

		/**
		 * Diese Methode konvertiert das gegebene Object in einen {@link ObjectValue} und gibt diesen zurück.
		 * 
		 * @param data Object.
		 * @return {@link ObjectValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static ObjectValue valueOf(final Object data) throws NullPointerException {
			return new ObjectValue(data);
		}

		{}

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
			return ObjectValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für Funktionen als Nutzdaten.
	 * 
	 * @see FunctionType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends BaseValue<Function> {

		/**
		 * Dieses Feld speichert den {@link FunctionType}.
		 */
		public static final FunctionType TYPE = new FunctionType();

		{}

		/**
		 * Diese Methode konvertiert die gegebene Funktion in einen {@link FunctionValue} und gibt diesen zurück.
		 * 
		 * @param data Funktion.
		 * @return {@link FunctionValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static FunctionValue valueOf(final Function data) throws NullPointerException {
			return new FunctionValue(data);
		}

		{}

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
			return FunctionValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link String} als Nutzdaten.
	 * 
	 * @see StringType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends BaseValue<String> {

		/**
		 * Dieses Feld speichert den {@link StringType}.
		 */
		public static final StringType TYPE = new StringType();

		{}

		/**
		 * Diese Methode konvertiert den gegebenen Test in einen {@link StringValue} und gibt diesen zurück.
		 * 
		 * @param data Text.
		 * @return {@link StringValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static StringValue valueOf(final String data) throws NullPointerException {
			return new StringValue(data);
		}

		{}

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
			return StringValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Number} als Nutzdaten.
	 * 
	 * @see NumberType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends BaseValue<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberType}.
		 */
		public static final NumberType TYPE = new NumberType();

		{}

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link NumberValue} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 */
		public static NumberValue valueOf(final Number data) throws NullPointerException {
			return new NumberValue(data);
		}

		{}

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
			return NumberValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Boolean} als Nutzdaten.
	 * 
	 * @see BooleanType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends BaseValue<Boolean> {

		/**
		 * Dieses Feld speichert den {@link BooleanType}.
		 */
		public static final BooleanType TYPE = new BooleanType();

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
			return BooleanValue.TYPE;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link Converter} zur Anpassung von {@link Values#valueOf(Object)}.
	 */
	static Converter<? super Object, ? extends Value> defaultConverter;

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

	/**
	 * Diese Methode konvertiert das gegebene Objekt in einen {@link Value} und gibt diesen zurück. Abhängig vom Datentyp des gegebenen Objekts kann hierfür
	 * automatisch ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue}, {@link StringValue}, {@link NumberValue} oder {@link BooleanValue}
	 * verwendet werden.
	 * <ul>
	 * <li>Wenn der via {@link #setDefaultConverter(Converter)} registrierte {@link Converter} sowie das Ergebnis seiner {@link Converter#convert(Object)
	 * Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben.</li>
	 * <li>Wenn das Objekt {@code null} ist, wird {@link NullValue#INSTANCE} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link Value} ist, wird dieser unverändert zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link Array} ist, wird dieses als {@link ArrayValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link String} ist, wird dieser als {@link StringValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt eine {@link Number} ist, wird dieser als {@link NumberValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link Boolean} ist, wird dieser als {@link BooleanValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt eine {@link Function} ist, wird dieser als {@link FunctionValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein Array ist, wird dieses als {@link ArrayValue} zurück gegeben.</li>
	 * <li>In allen anderen Fällen wird der Datensatz als {@link ObjectValue} zurück gegeben.</li>
	 * </ul>
	 * 
	 * @see Converter#convert(Object)
	 * @see Array#from(Object)
	 * @see ArrayValue#valueOf(Array)
	 * @see StringValue#valueOf(String)
	 * @see NumberValue#valueOf(Number)
	 * @see BooleanValue#valueOf(Boolean)
	 * @see FunctionValue#valueOf(Function)
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
		if (data == null) return NullValue.INSTANCE;
		if (data instanceof Value) return (Value)data;
		if (data instanceof Array) return ArrayValue.valueOf((Array)data);
		if (data instanceof String) return StringValue.valueOf((String)data);
		if (data instanceof Number) return NumberValue.valueOf((Number)data);
		if (data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
		if (data instanceof Function) return FunctionValue.valueOf((Function)data);
		final Array array = Array.from(data);
		if (array != null) return ArrayValue.valueOf(array);
		return ObjectValue.valueOf(data);
	}

}
