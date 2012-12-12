package bee.creative.function;

import java.util.Arrays;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.BooleanType;
import bee.creative.function.Types.DoubleType;
import bee.creative.function.Types.FloatType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Types.IntegerType;
import bee.creative.function.Types.LongType;
import bee.creative.function.Types.ObjectType;
import bee.creative.function.Types.StringType;
import bee.creative.function.Types.VoidType;
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
	 */
	public static abstract class AbstractValue implements Value {

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
		public <GData> GData dataTo(final Type<GData> type) throws NullPointerException, IllegalArgumentException {
			return type.dataOf(this);
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
	 * @see VoidType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VoidValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den {@link VoidValue} für {@code null}.
		 */
		public static final Value NULL = new VoidValue();

		/**
		 * Diese Methode gibt den gegebenen {@link Value} oder den {@link VoidValue#NULL} zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param value {@link Value} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Value value) {
			if(value == null) return VoidValue.NULL;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public VoidType type() {
			return VoidType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return null;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Wert} mit {@link Value Wertlisten} als Datensatz.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final byte... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = IntegerValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final short... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = IntegerValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final char... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = IntegerValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final int... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = IntegerValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final long... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = LongValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final float... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = FloatValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Zahlenliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Zahlenliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final double... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = DoubleValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Wahrheitswertliste in einen {@link Value Wert} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Wahrheitswertliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final boolean... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = BooleanValue.valueOf(data[i]);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Diese Methode konvertiert die gegebene Objektliste in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Objektliste oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Object... data) {
			if(data == null) return VoidValue.NULL;
			final int size = data.length;
			final Value[] values = new Value[size];
			for(int i = 0; i < size; i++){
				values[i] = ObjectValue.valueOf(data);
			}
			return new ArrayValue(0, values);
		}

		/**
		 * Dieses Feld speichert die Datensätze.
		 */
		final Value[] data;

		/**
		 * Dieser Konstrukteur initialisiert die Datensätze.
		 * 
		 * @param IGNORED IGNORIERT.
		 * @param data Datensätze.
		 */
		ArrayValue(int IGNORED, final Value... data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert die Datensätze.
		 * 
		 * @param data Datensätze.
		 * @throws NullPointerException Wenn einer der Datensätze {@code null} ist.
		 */
		public ArrayValue(final Value... data) throws NullPointerException {
			if((data == null) || Arrays.asList(data).contains(null)) throw new NullPointerException();
			this.data = data.clone();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayType type() {
			return ArrayType.TYPE;
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
	public static final class ObjectValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen Datensatz in einen {@link Value} und gibt diesen zurück. Abhängig vom Datentyp des gegebenen Datensatzes wird hierfür ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue}, {@link StringValue}, {@link IntegerValue}, {@link LongValue}, {@link FloatValue}, {@link DoubleValue} oder {@link BooleanValue} verwendet. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data Datensatz.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Object data) {
			if(data == null) return VoidValue.NULL;
			if(data instanceof Value) return (Value)data;
			if(data instanceof Long) return LongValue.valueOf((Long)data);
			if(data instanceof Float) return FloatValue.valueOf((Float)data);
			if(data instanceof Double) return DoubleValue.valueOf((Double)data);
			if(data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
			if(data instanceof String) return StringValue.valueOf((String)data);
			if(data instanceof Number) return IntegerValue.valueOf((Number)data);
			if(data instanceof Function) return FunctionValue.valueOf((Function)data);
			if(data.getClass().isArray()) return ArrayValue.valueOf((Object[])data);
			return new ObjectValue(data);
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
			return ObjectType.TYPE;
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
	public static final class FunctionValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert die gegebene {@link Function} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Function} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Function data) {
			if(data == null) return VoidValue.NULL;
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
			return FunctionType.TYPE;
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
	public static final class StringValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen {@link String} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link String} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final String data) {
			if(data == null) return VoidValue.NULL;
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
			return StringType.TYPE;
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
	 * Diese Klasse implementiert den {@link Integer}-{@link Value}.
	 * 
	 * @see IntegerType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IntegerValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link Value}.
		 */
		public static IntegerValue valueOf(final int data) {
			return new IntegerValue(data);
		}

		/**
		 * Diese Methode konvertiert die gegebene {@link Number} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Number} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Number data) {
			if(data == null) return VoidValue.NULL;
			return new IntegerValue(data.intValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final int data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public IntegerValue(final int data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public IntegerValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data.intValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IntegerType type() {
			return IntegerType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Integer data() {
			return Integer.valueOf(this.data);
		}

		/**
		 * Diese Methode gibt den Datensatz als {@code int} zurück.
		 * 
		 * @return Datensatz als {@code int}.
		 */
		public int value() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Long}-{@link Value}.
	 * 
	 * @see LongType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LongValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link Value}.
		 */
		public static LongValue valueOf(final long data) {
			return new LongValue(data);
		}

		/**
		 * Diese Methode konvertiert die gegebene {@link Number} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Number} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Number data) {
			if(data == null) return VoidValue.NULL;
			return new LongValue(data.longValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final int data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public LongValue(final int data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public LongValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data.intValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LongType type() {
			return LongType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Long data() {
			return Long.valueOf(this.data);
		}

		/**
		 * Diese Methode gibt den Datensatz als {@code long} zurück.
		 * 
		 * @return Datensatz als {@code long}.
		 */
		public long value() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Float}-{@link Value}.
	 * 
	 * @see FloatType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FloatValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link Value}.
		 */
		public static FloatValue valueOf(final float data) {
			return new FloatValue(data);
		}

		/**
		 * Diese Methode konvertiert die gegebene {@link Number} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Number} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Number data) {
			if(data == null) return VoidValue.NULL;
			return new FloatValue(data.floatValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final float data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public FloatValue(final float data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public FloatValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data.floatValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FloatType type() {
			return FloatType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Float data() {
			return Float.valueOf(this.data);
		}

		/**
		 * Diese Methode gibt den Datensatz als {@code float} zurück.
		 * 
		 * @return Datensatz als {@code float}.
		 */
		public float value() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Double}-{@link Value}.
	 * 
	 * @see DoubleType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DoubleValue extends AbstractValue {

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link Value}.
		 */
		public static DoubleValue valueOf(final double data) {
			return new DoubleValue(data);
		}

		/**
		 * Diese Methode konvertiert die gegebene {@link Number} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Number} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Number data) {
			if(data == null) return VoidValue.NULL;
			return new DoubleValue(data.doubleValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final double data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public DoubleValue(final double data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public DoubleValue(final Number data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data.doubleValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DoubleType type() {
			return DoubleType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Double data() {
			return Double.valueOf(this.data);
		}

		/**
		 * Diese Methode gibt den Datensatz als {@code double} zurück.
		 * 
		 * @return Datensatz als {@code double}.
		 */
		public double value() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Wert} für {@link Boolean Wahrheitswerte}.
	 * 
	 * @see BooleanType#ID
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#TRUE}.
		 */
		public static final BooleanValue TRUE = new BooleanValue(true);

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#FALSE}.
		 */
		public static final BooleanValue FALSE = new BooleanValue(false);

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link Value}.
		 */
		public static BooleanValue valueOf(final boolean data) {
			return (data ? BooleanValue.TRUE : BooleanValue.FALSE);
		}

		/**
		 * Diese Methode konvertiert den gegebenen {@link Boolean} in einen {@link Value} und gibt diesen zurück. Wenn die Eingabe {@code null} ist, wird {@link VoidValue#NULL} zurück gegeben.
		 * 
		 * @param data {@link Boolean} oder {@code null}.
		 * @return {@link Value}.
		 */
		public static Value valueOf(final Boolean data) {
			if(data == null) return VoidValue.NULL;
			return BooleanValue.valueOf(data.booleanValue());
		}

		/**
		 * Dieses Feld speichert den Datensatz.
		 */
		final boolean data;

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 */
		public BooleanValue(final boolean data) {
			this.data = data;
		}

		/**
		 * Dieser Konstrukteur initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data.booleanValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BooleanType type() {
			return BooleanType.TYPE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean data() {
			return Boolean.valueOf(this.data);
		}

		/**
		 * Diese Methode gibt den Datensatz als {@code boolean} zurück.
		 * 
		 * @return Datensatz als {@code boolean}.
		 */
		public boolean value() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Value Ergebniswert} einer {@link Function Funktion} mit {@code call-by-reference}-Semantik, welcher eine gegebene {@link Function Funktion} erst dann mit einem gegebenen {@link Scope Ausführungskontext} einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Datensatz} gelesen werden.
	 * <p>
	 * Der von der {@link Function Funktion} berechnete {@link Value Ergebniswert} wird zur schnellen Wiederverwendung gepuffert. Nach der einmaligen Auswertung der {@link Function Funktion} werden die Verweise auf {@link Scope Ausführungskontext} und {@link Function Funktion} aufgelöst.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ReturnValue implements Value {

		/**
		 * Dieses Feld speichert das von der {@link Function Funktion} berechnete Ergebnis oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Value value;

		/**
		 * Dieses Feld speichert den {@link Scope Ausführungskontext} zum Aufruf der {@link Function Funktion} oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Scope scope;

		/**
		 * Dieses Feld speichert die {@link Function Funktion} oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Function function;

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}. {@link Scope Ausführungskontext} und {@link Function Funktion} werden nicht geprüft.
		 * 
		 * @param scope {@link Scope Ausführungskontext}.
		 * @param function {@link Function Funktion}.
		 */
		ReturnValue(final Function function, final Scope scope) {
			this.scope = scope;
			this.function = function;
		}

		/**
		 * Dieser Konstrukteur initialisiert {@link Scope Ausführungskontext} und {@link Function Funktion}.
		 * 
		 * @param scope {@link Scope Ausführungskontext}.
		 * @param function {@link Function Funktion}.
		 * @throws NullPointerException Wenn der gegebene {@link Scope Ausführungskontext} bzw. die gegebene {@link Function Funktion} {@code null} ist.
		 */
		public ReturnValue(final Scope scope, final Function function) throws NullPointerException {
			if((scope == null) || (function == null)) throw new NullPointerException();
			this.scope = scope;
			this.function = function;
		}

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
		public <GData> GData dataAs(final Type<GData> type) throws NullPointerException, ClassCastException {
			return this.value.dataAs(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GData> GData dataTo(final Type<GData> type) throws NullPointerException, IllegalArgumentException {
			return this.value().dataTo(type);
		}

		/**
		 * Diese Methode gibt den {@link Value Ergebniswert} der Ausführung der {@link Function Funktion} mit dem {@link Scope Ausführungskontext} zurück.
		 * 
		 * @see Function#execute(Scope)
		 * @return {@link Value Ergebniswert}.
		 * @throws NullPointerException Wenn der berechnete {@link Value Ergebniswert} {@code null} ist.
		 */
		public Value value() throws NullPointerException {
			Value value = this.value;
			if(value != null) return value;
			value = this.function.execute(this.scope);
			if(value == null) throw new NullPointerException();
			this.value = value;
			this.scope = null;
			this.function = null;
			return value;
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
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			return this.value().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, this.getClass().getSimpleName(), "value", this.value, "scope", this.scope, "function", this.function);
		}

	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Values() {
	}

}
