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
 * Diese Klasse implementiert {@link Value}{@code s} für {@code null}, {@link Value}{@code []}, {@link Object}, {@link Function}, {@link String}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} und {@link Boolean}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Value Wert}.
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
		 * @see Objects#equals(Object, Object)
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
			return Objects.toStringCall(this.getClass().getSimpleName(), this.type(), this.data());
		}

	}

	/**
	 * Diese Klasse implementiert den leeren {@link Value}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends AbstractValue<Object> {

		/**
		 * Dieses Feld speichert den {@link NullValue} für {@code null}.
		 */
		public static final Value INSTANCE = new NullValue();

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
		 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
		 */
		NullValue() {
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
			return NullType.INSTANCE;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Wert} mit {@link Value Wertlisten} als Datensatz.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends AbstractValue<Value[]> {

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Zahlenliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Diese Methode konvertiert die gegebene Wahrheitswertliste in einen {@link Value Wert} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final Object... data) throws NullPointerException {
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = ObjectValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
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
		 * Dieses Feld speichert die Datensätze.
		 */
		final Value[] data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz ohne Prüfung.
		 * 
		 * @param IGNORE IGNORTIERT.
		 * @param data Datensatz.
		 */
		ArrayValue(final int IGNORE, final Value[] data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist oder enthält.
		 */
		public ArrayValue(final Value... data) throws NullPointerException {
			if(Arrays.asList(data).contains(null)) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayType type() {
			return ArrayType.INSTANCE;
		}

		/**
		 * {@inheritDoc} Die zurück gegebene {@link Value Wertliste} sollte nicht verändert werden.
		 */
		@Override
		public Value[] data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Value Wert} mit beliebigen {@link Object Objekten} als Datensatz.
	 * 
	 * @see ObjectType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends AbstractValue<Object> {

		/**
		 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link Value} und gibt diesen zurück. Abhängig vom Datentyp des gegebenen Datensatzes wird hierfür ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue}, {@link StringValue}, {@link NumberValue} oder {@link BooleanValue} verwendet. Wenn die Eingabe {@code null} ist, wird {@link NullValue#INSTANCE} zurück gegeben.
		 * 
		 * @param data Datensatz oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Object data) {
			if(data == null) return NullValue.INSTANCE;
			if(data instanceof Value) return (Value)data;
			if(data instanceof String) return StringValue.valueOf((String)data);
			if(data instanceof Number) return NumberValue.valueOf((Number)data);
			if(data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
			if(data instanceof Function) return FunctionValue.valueOf((Function)data);
			final Class<?> clazz = data.getClass();
			if(!clazz.isArray()) return new ObjectValue(data);
			if(clazz == int[].class) return ArrayValue.valueOf((int[])data);
			if(clazz == long[].class) return ArrayValue.valueOf((long[])data);
			if(clazz == byte[].class) return ArrayValue.valueOf((byte[])data);
			if(clazz == char[].class) return ArrayValue.valueOf((char[])data);
			if(clazz == short[].class) return ArrayValue.valueOf((short[])data);
			if(clazz == float[].class) return ArrayValue.valueOf((float[])data);
			if(clazz == double[].class) return ArrayValue.valueOf((double[])data);
			if(clazz == boolean[].class) return ArrayValue.valueOf((boolean[])data);
			return ArrayValue.valueOf((Object[])data);
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final Object data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectType type() {
			return ObjectType.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link Function Funktion}.
	 * 
	 * @see FunctionType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends AbstractValue<Function> {

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
		 * Dieses Feld speichert den Datensatz.
		 */
		final Function data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FunctionType type() {
			return FunctionType.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Function data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value} für {@link String Zeichenketten}.
	 * 
	 * @see StringType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends AbstractValue<String> {

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
		 * Dieses Feld speichert den Datensatz.
		 */
		final String data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public StringType type() {
			return StringType.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Value}.
	 * 
	 * @see NumberType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends AbstractValue<Number> {

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
		 * Dieses Feld speichert den Datensatz.
		 */
		final Number data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NumberType type() {
			return NumberType.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Number data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Wert} für {@link Boolean Wahrheitswerte}.
	 * 
	 * @see BooleanType#ID
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends AbstractValue<Boolean> {

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
		 * Diese Methode konvertiert den gegebenen {@link Boolean} in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data {@link Boolean}.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static BooleanValue valueOf(final Boolean data) throws NullPointerException {
			return BooleanValue.valueOf(data.booleanValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final Boolean data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BooleanType type() {
			return BooleanType.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean data() {
			return this.data;
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Values() {
	}

}
