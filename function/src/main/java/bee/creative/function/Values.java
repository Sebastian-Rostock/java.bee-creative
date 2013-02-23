package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.BooleanType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Types.NullType;
import bee.creative.function.Types.NumberType;
import bee.creative.function.Types.ObjectType;
import bee.creative.function.Types.StringType;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert {@link Value}{@code s} für {@code null}-, {@link Value}{@code []}-, {@link Object}-, {@link Function}-, {@link String}-, {@link Integer}-, {@link Long}-, {@link Float}-, {@link Double}- und {@link Boolean}-Datensätze.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Schnittstelle definiert eine Methode zur Konvertierung eines gegebenen Datensatzes in einen {@link Value}. Verwendung findet diese Methode in {@link Values#valueOf(Object)}, wofür sie via {@link Values#setHandler(DataHandler)} registriert werden kann.
	 * 
	 * @see Values#valueOf(Object)
	 * @see Values#setHandler(DataHandler)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface DataHandler {

		/**
		 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Datensatz oder {@code null}.
		 * @return {@link Value}.
		 */
		public Value valueOf(final Object data);

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Value}, dem zur Vollständigkeit nur noch ein {@link #data() Datensatz} sowie ein {@link #type() Datentyp} fehlen.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static abstract class AbstractValue<GData> implements Value {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract GData data();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract Type<GData> type();

		/**
		 * {@inheritDoc}
		 */
		@Override
		@SuppressWarnings ("unchecked")
		public <GData> GData dataAs(final Type<GData> type) throws NullPointerException, ClassCastException {
			if(this.type().is(type)) return (GData)this.data();
			throw new ClassCastException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GData2> GData2 dataTo(final Type<GData2> type) throws NullPointerException, IllegalArgumentException {
			return type.dataOf(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value valueTo(final Type<?> type) throws NullPointerException, IllegalArgumentException {
			return type.valueOf(this);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Objects#hashEx(Object)
		 */
		@Override
		public int hashCode() {
			return Objects.hashEx(this.data());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Objects#equalsEx(Object, Object)
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			final Value data = (Value)object;
			return Objects.equals(this.type(), data.type()) && Objects.equalsEx(this.data(), data.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Value} mit {@link #data() Datensatz}, dem zur Vollständigkeit nur noch ein {@link #type() Datentyp} fehlt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static abstract class AbstractValue2<GData> extends AbstractValue<GData> {

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final GData data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public AbstractValue2(final GData data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@code null}-{@link Value}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends AbstractValue<Object> {

		/**
		 * Dieses Feld speichert den {@link NullType}.
		 */
		public static final NullType TYPE = new NullType();

		/**
		 * Dieses Feld speichert den {@link NullValue} für {@code null}.
		 */
		public static final NullValue INSTANCE = new NullValue();

		/**
		 * Diese Methode gibt den gegebenen {@link Value} oder {@link NullValue#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullValue#INSTANCE} zurück gegeben.
		 * 
		 * @param value {@link Value} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Value value) {
			if(value == null) return NullValue.INSTANCE;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NullType type() {
			return NullValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} mit {@link Value}{@code []} als Datensatz. Der {@link #data() Datensatz} sollte als konstant betrachtet und nicht verändert werden.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends AbstractValue2<Value[]> {

		/**
		 * Dieses Feld speichert den {@link ArrayType}.
		 */
		public static final ArrayType TYPE = new ArrayType();

		/**
		 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link ArrayValue} und gibt diesen oder {@code null} zurück. Wenn der Datensatz kein Array ist, wird {@code null} zurück gegeben.
		 * 
		 * @see ArrayValue#valueOf(int[])
		 * @see ArrayValue#valueOf(long[])
		 * @see ArrayValue#valueOf(byte[])
		 * @see ArrayValue#valueOf(char[])
		 * @see ArrayValue#valueOf(short[])
		 * @see ArrayValue#valueOf(float[])
		 * @see ArrayValue#valueOf(double[])
		 * @see ArrayValue#valueOf(boolean[])
		 * @see ArrayValue#valueOf(Value[])
		 * @see ArrayValue#valueOf(Object[])
		 * @param data Datensatz.
		 * @return {@link ArrayValue} oder {@code null}.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public static ArrayValue valueOf(final Object data) throws NullPointerException {
			final Class<?> clazz = data.getClass();
			if(!clazz.isArray()) return null;
			if(clazz == int[].class) return ArrayValue.valueOf((int[])data);
			if(clazz == long[].class) return ArrayValue.valueOf((long[])data);
			if(clazz == byte[].class) return ArrayValue.valueOf((byte[])data);
			if(clazz == char[].class) return ArrayValue.valueOf((char[])data);
			if(clazz == short[].class) return ArrayValue.valueOf((short[])data);
			if(clazz == float[].class) return ArrayValue.valueOf((float[])data);
			if(clazz == double[].class) return ArrayValue.valueOf((double[])data);
			if(clazz == boolean[].class) return ArrayValue.valueOf((boolean[])data);
			if(clazz == Value[].class) return ArrayValue.valueOf((Value[])data);
			return ArrayValue.valueOf((Object[])data);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final byte[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final short[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final char[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final int[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final long[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final float[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final double[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NumberValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Wahrheitswertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final boolean[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = BooleanValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Objektliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Objektliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final Object[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = Values.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final Value[] data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = NullValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz ohne Prüfung.
		 * 
		 * @param IGNORE IGNORTIERT.
		 * @param data Datensatz.
		 */
		ArrayValue(final int IGNORE, final Value[] data) {
			super(data);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist oder enthält.
		 */
		public ArrayValue(final Value... data) throws NullPointerException {
			super(data);
			if(Arrays.asList(data).contains(null)) throw new NullPointerException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayType type() {
			return ArrayValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value} mit beliebigen {@link Object Objekten} als Datensatz.
	 * 
	 * @see ObjectType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends AbstractValue2<Object> {

		/**
		 * Dieses Feld speichert den {@link ObjectType}.
		 */
		public static final ObjectType TYPE = new ObjectType();

		/**
		 * Diese Methode konvertiert das gegebene {@link Object} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link Object}.
		 * @return {@link ObjectValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ObjectValue valueOf(final Object data) throws NullPointerException {
			return new ObjectValue(data);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectType type() {
			return ObjectValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link Function Funktion}.
	 * 
	 * @see FunctionType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends AbstractValue2<Function> {

		/**
		 * Dieses Feld speichert den {@link FunctionType}.
		 */
		public static final FunctionType TYPE = new FunctionType();

		/**
		 * Diese Methode konvertiert die gegebene {@link Function} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link Function}.
		 * @return {@link FunctionValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static FunctionValue valueOf(final Function data) throws NullPointerException {
			return new FunctionValue(data);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FunctionType type() {
			return FunctionValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link String Zeichenketten}.
	 * 
	 * @see StringType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends AbstractValue2<String> {

		/**
		 * Dieses Feld speichert den {@link StringType}.
		 */
		public static final StringType TYPE = new StringType();

		/**
		 * Diese Methode konvertiert den gegebenen {@link String} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link String}.
		 * @return {@link StringValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static StringValue valueOf(final String data) throws NullPointerException {
			return new StringValue(data);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public StringType type() {
			return StringValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link Number Zahlenwerte}.
	 * 
	 * @see NumberType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends AbstractValue2<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberType}.
		 */
		public static final NumberType TYPE = new NumberType();

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 */
		public static NumberValue valueOf(final int data) {
			return new NumberValue(Integer.valueOf(data));
		}

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 */
		public static NumberValue valueOf(final long data) {
			return new NumberValue(Long.valueOf(data));
		}

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 */
		public static NumberValue valueOf(final float data) {
			return new NumberValue(Float.valueOf(data));
		}

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 */
		public static NumberValue valueOf(final double data) {
			return new NumberValue(Double.valueOf(data));
		}

		/**
		 * Diese Methode konvertiert die gegebene {@link Number} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link Number}.
		 * @return {@link NumberValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static NumberValue valueOf(final Number data) throws NullPointerException {
			return new NumberValue(data);
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NumberType type() {
			return NumberValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link Boolean Wahrheitswerte}.
	 * 
	 * @see BooleanType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends AbstractValue2<Boolean> {

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

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 */
		public static BooleanValue valueOf(final boolean data) {
			return (data ? BooleanValue.TRUE : BooleanValue.FALSE);
		}

		/**
		 * Diese Methode konvertiert den gegebenen {@link Boolean Wahrheitswert} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link Boolean}.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static BooleanValue valueOf(final Boolean data) throws NullPointerException {
			return BooleanValue.valueOf(data.booleanValue());
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BooleanType type() {
			return BooleanValue.TYPE;
		}

	}

	/**
	 * Dieses Feld speichert den {@link DataHandler} zur Anpassung von {@link Values#valueOf(Object)}.
	 */
	static DataHandler handler;

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Values() {
	}

	/**
	 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link Value} und gibt diesen zurück. Abhängig vom Datentyp des gegebenen Datensatzes kann hierfür ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue}, {@link StringValue}, {@link NumberValue} oder {@link BooleanValue} verwendet.
	 * <ul>
	 * <li>Wenn der Datensatz {@code null} ist, wird {@link NullValue#INSTANCE} zurück gegeben.</li>
	 * <li>Wenn der Datensatz ein {@link Value} ist, wird dieser unverändert zurück gegeben.</li>
	 * <li>Wenn der via {@link #setHandler(DataHandler)} registrierte {@link DataHandler} sowie das Ergebnis seiner {@link DataHandler#valueOf(Object) Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben.</li>
	 * <li>Wenn der Datensatz ein {@link String} ist, wird dieser als {@link StringValue} zurück gegeben.</li>
	 * <li>Wenn der Datensatz eine {@link Number} ist, wird dieser als {@link NumberValue} zurück gegeben.</li>
	 * <li>Wenn der Datensatz ein {@link Boolean} ist, wird dieser als {@link BooleanValue} zurück gegeben.</li>
	 * <li>Wenn der Datensatz eine {@link Function} ist, wird dieser als {@link FunctionValue} zurück gegeben.</li>
	 * <li>Wenn der Datensatz ein Array ist, wird dieser als {@link ArrayValue} zurück gegeben.</li>
	 * <li>In allen anderen Fällen wird der Datensatz als {@link ObjectValue} zurück gegeben.</li>
	 * </ul>
	 * 
	 * @see DataHandler#valueOf(Object)
	 * @see StringValue#valueOf(String)
	 * @see NumberValue#valueOf(Number)
	 * @see BooleanValue#valueOf(Boolean)
	 * @see FunctionValue#valueOf(Function)
	 * @see ArrayValue#valueOf(Object)
	 * @see ObjectValue#valueOf(Object)
	 * @param data Datensatz oder {@code null}.
	 * @return {@link Value}.
	 */
	public static Value valueOf(final Object data) {
		if(data == null) return NullValue.INSTANCE;
		if(data instanceof Value) return (Value)data;
		final DataHandler handler = Values.handler;
		if(handler != null){
			final Value value = handler.valueOf(data);
			if(value != null) return value;
		}
		if(data instanceof String) return StringValue.valueOf((String)data);
		if(data instanceof Number) return NumberValue.valueOf((Number)data);
		if(data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
		if(data instanceof Function) return FunctionValue.valueOf((Function)data);
		final Value value = ArrayValue.valueOf(data);
		if(value != null) return value;
		return ObjectValue.valueOf(data);
	}

	/**
	 * Diese Methode gibt den {@link DataHandler} zur Anpassung von {@link Values#valueOf(Object)} zurück.
	 * 
	 * @return {@link DataHandler} oder {@code null}.
	 */
	public static DataHandler getHandler() {
		return Values.handler;
	}

	/**
	 * Diese Methode setzt den {@link DataHandler} zur Anpassung von {@link Values#valueOf(Object)}.
	 * 
	 * @param handler {@link DataHandler} oder {@code null}.
	 */
	public static void setHandler(final DataHandler handler) {
		Values.handler = handler;
	}

}
