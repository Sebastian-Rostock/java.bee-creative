package bee.creative.function;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.BooleanValue;
import bee.creative.function.Values.NumberValue;
import bee.creative.util.Comparables.Get;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert eine unmodifizierbare Liste von Werten sowie Methoden zur Erzeugung solcher Wertlisten aus primitiven Arrays, {@link Collection}s
 * und {@link Scope}s.
 * 
 * @see ArrayValue
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class Array implements Get<Value>, Iterable<Value> {

	/**
	 * Diese Klasse implementiert eine Wertliste als Sicht auf die Parameterwerte eines Ausführungskontexts.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ScopeArray extends Array {

		/**
		 * Dieses Feld speichert den Ausführungskontext.
		 */
		final Scope scope;

		/**
		 * Dieser Konstruktor initialisiert den Ausführungskontext.
		 * 
		 * @param scope Ausführungskontext.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ScopeArray(final Scope scope) throws NullPointerException {
			if(scope == null) throw new NullPointerException();
			this.scope = scope;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			if((index < 0) || (index >= this.length())) throw new IndexOutOfBoundsException();
			return this.scope.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.scope.size();
		}

	}

	/**
	 * Diese Klasse implementiert eine Wertliste mit einem Array von Werten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ValueArray extends Array {

		/**
		 * Dieses Feld speichert die Werte.
		 */
		final Value[] values;

		/**
		 * Dieser Konstruktor initialisiert die Werte.
		 * 
		 * @param values Werte.
		 */
		public ValueArray(final Value[] values) {
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			return this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.values.length;
		}

	}

	/**
	 * Dieses Feld speichert eine leere Wertliste.
	 */
	public static final Array EMPTY_ARRAY = new Array() {

		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int length() {
			return 0;
		}

	};

	/**
	 * Diese Methode konvertiert das gegebene Objekt in eine Wertliste und gibt diese oder {@code null} zurück. Wenn das gegebene Objekt {@link Class#isArray()
	 * kein Array} ist, wird {@code null} zurück gegeben.
	 * 
	 * @see Array#valueOf(int[])
	 * @see Array#valueOf(long[])
	 * @see Array#valueOf(byte[])
	 * @see Array#valueOf(char[])
	 * @see Array#valueOf(short[])
	 * @see Array#valueOf(float[])
	 * @see Array#valueOf(double[])
	 * @see Array#valueOf(boolean[])
	 * @see Array#valueOf(Value[])
	 * @see Array#valueOf(Object[])
	 * @param data Objekt.
	 * @return Wertliste oder {@code null}.
	 * @throws NullPointerException Wenn das Objekt {@code null} ist.
	 */
	public static Array from(final Object data) throws NullPointerException {
		final Class<?> clazz = data.getClass();
		if(!clazz.isArray()) return null;
		if(clazz == int[].class) return Array.valueOf((int[])data);
		if(clazz == long[].class) return Array.valueOf((long[])data);
		if(clazz == byte[].class) return Array.valueOf((byte[])data);
		if(clazz == char[].class) return Array.valueOf((char[])data);
		if(clazz == short[].class) return Array.valueOf((short[])data);
		if(clazz == float[].class) return Array.valueOf((float[])data);
		if(clazz == double[].class) return Array.valueOf((double[])data);
		if(clazz == boolean[].class) return Array.valueOf((boolean[])data);
		if(clazz == Value[].class) return Array.valueOf((Value[])data);
		return Array.valueOf((Object[])data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final byte[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Byte.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final short[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Short.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final char[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Integer.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final int[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Integer.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final long[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Long.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final float[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Float.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final double[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = NumberValue.valueOf(Double.valueOf(data[i]));
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Wahrheitswertliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see BooleanValue#valueOf(boolean)
	 * @param data Wahrheitswertliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final boolean[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = BooleanValue.valueOf(data[i]);
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode konvertiert die gegebene Objektliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Values#valueOf(Object)
	 * @param data Objektliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final Object[] data) throws NullPointerException {
		final int size = data.length;
		if(size == 0) return Array.EMPTY_ARRAY;
		final Value[] values = new Value[size];
		for(int i = 0; i < size; i++){
			values[i] = Values.valueOf(data[i]);
		}
		return new ValueArray(values);
	}

	/**
	 * Diese Methode gibt eine Wertliste als Sicht auf die Parameterwerte des gegebenen Ausführungskontexts zurück.
	 * 
	 * @see Scope#get(int)
	 * @see Scope#size()
	 * @param scope Ausführungskontext.
	 * @return Wertliste der Parameterwerte.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final Scope scope) throws NullPointerException {
		if(scope.size() == 0) return Array.EMPTY_ARRAY;
		return new ScopeArray(scope);
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @param data Werte.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final Value... data) throws NullPointerException {
		if(data.length == 0) return Array.EMPTY_ARRAY;
		return new ValueArray(data.clone());
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see #valueOf(Value...)
	 * @param data Werte.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Array valueOf(final Collection<? extends Value> data) throws NullPointerException {
		if(data.size() == 0) return Array.EMPTY_ARRAY;
		return Array.valueOf(data.toArray(new Value[data.size()]));
	}

	/**
	 * Diese Methode konvertiert diese Wertliste in ein Array und gibt diese zurück.
	 * 
	 * @return Array mit den Werten dieser Wertliste.
	 */
	public Value[] value() {
		final int length = this.length();
		final Value[] array = new Value[length];
		for(int i = 0; i < length; i++){
			array[i] = this.get(i);
		}
		return array;
	}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 */
	@Override
	public abstract Value get(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge dieser Wertliste zurück.
	 * 
	 * @return Länge.
	 */
	public abstract int length();

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 * 
	 * @param start Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link Array}-Sicht auf einen Abschnitt dieses Arrays.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieses Arrays liegt oder eine negative Länge hätte.
	 */
	public Array section(final int start, final int length) throws IllegalArgumentException {
		if((start == 0) && (length == this.length())) return this;
		if((start < 0) || (length < 0) || ((start + length) > this.length())) throw new IllegalArgumentException();
		if(length == 0) return Array.EMPTY_ARRAY;
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				if((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return Array.this.get(index + start);
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public Array section(final int start2, final int length2) throws IllegalArgumentException {
				return Array.this.section(start + start2, length2);
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Value> iterator() {
		return new GetIterator<Value>(this, this.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 0x811C9DC5;
		for(int i = 0, length = this.length(); i < length; i++){
			hash = (hash * 0x01000193) ^ Objects.hash(this.get(i));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof Array)) return false;
		final Array data = (Array)object;
		final int length = this.length();
		if(data.length() != length) return false;
		for(int i = 0; i < length; i++)
			if(!Objects.equals(this.get(i), data.get(i))) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toString(this);
	}

}
