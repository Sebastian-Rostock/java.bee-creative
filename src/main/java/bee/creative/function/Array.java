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
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste.
	 * 
	 * @see Array#collect(Collector)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 * 
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(Value value);

	}

	{}

	/**
	 * Dieses Feld speichert eine leere Wertliste.
	 */
	public static final Array EMPTY = new Array() {

		@Override
		public Value get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int length() {
			return 0;
		}

	};

	{}

	/**
	 * Diese Methode konvertiert das gegebene Objekt in eine Wertliste und gibt diese oder {@code null} zurück. Wenn das gegebene Objekt {@link Class#isArray()
	 * kein} oder ein unpassendes Array ist, wird {@code null} zurück gegeben.
	 * 
	 * @see Array#valueOf(int[])
	 * @see Array#valueOf(long[])
	 * @see Array#valueOf(byte[])
	 * @see Array#valueOf(short[])
	 * @see Array#valueOf(float[])
	 * @see Array#valueOf(double[])
	 * @see Array#valueOf(boolean[])
	 * @see Array#valueOf(Value[])
	 * @see Array#valueOf(Object[])
	 * @param data Objekt.
	 * @return Wertliste oder {@code null}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array from(final Object data) throws NullPointerException {
		final Class<?> clazz = data.getClass();
		if (!clazz.isArray()) return null;
		if (clazz == int[].class) return Array.valueOf((int[])data);
		if (clazz == long[].class) return Array.valueOf((long[])data);
		if (clazz == byte[].class) return Array.valueOf((byte[])data);
		if (clazz == char[].class) return null;
		if (clazz == short[].class) return Array.valueOf((short[])data);
		if (clazz == float[].class) return Array.valueOf((float[])data);
		if (clazz == double[].class) return Array.valueOf((double[])data);
		if (clazz == boolean[].class) return Array.valueOf((boolean[])data);
		if (clazz == Value[].class) return Array.valueOf((Value[])data);
		return Array.valueOf((Object[])data);
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Byte#valueOf(byte)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(byte[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final byte[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Byte.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Short#valueOf(short)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(short[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final short[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Short.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Integer#valueOf(int)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(int[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final int[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Integer.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Long#valueOf(long)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(long[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final long[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Long.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Float#valueOf(float)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(float[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final float[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Float.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Zahlenliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Double#valueOf(double)
	 * @see NumberValue#valueOf(Number)
	 * @param data Zahlenliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(double[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final double[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return NumberValue.valueOf(Double.valueOf(values[index]));
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebene Wahrheitswertliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see BooleanValue#valueOf(boolean)
	 * @param data Wahrheitswertliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(boolean[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final boolean[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return BooleanValue.valueOf(values[index]);
			}

			@Override
			public int length() {
				return values.length;
			}

		};

	}

	/**
	 * Diese Methode konvertiert die gegebene Objektliste in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Values#valueOf(Object)
	 * @param data Objektliste.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(Object[] data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final Object[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return Values.valueOf(values[index]);
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode gibt eine Wertliste als Sicht auf die Parameterwerte des gegebenen Ausführungskontexts zurück.
	 * 
	 * @see Scope#get(int)
	 * @see Scope#size()
	 * @param scope Ausführungskontext.
	 * @return Wertliste der Parameterwerte.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(final Scope scope) throws NullPointerException {
		if (scope.size() == 0) return Array.EMPTY;
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= scope.size())) throw new IndexOutOfBoundsException();
				return scope.get(index);
			}

			@Override
			public int length() {
				return scope.size();
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @param data Werte.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(Value... data) throws NullPointerException {
		if (data.length == 0) return Array.EMPTY;
		final Value[] values = data = data.clone();
		return new Array() {

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				return values[index];
			}

			@Override
			public int length() {
				return values.length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see #valueOf(Value...)
	 * @param data Werte.
	 * @return {@link Array}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static Array valueOf(final Collection<? extends Value> data) throws NullPointerException {
		if (data.size() == 0) return Array.EMPTY;
		return Array.valueOf(data.toArray(new Value[data.size()]));
	}

	{}

	/**
	 * Diese Methode fügt alle Werte im gegebenen Abschnitt geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig abgebrochen, wenn
	 * {@link Collector#push(Value)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 */
	protected boolean collect(final Collector target, int offset, int length) {
		for (length += offset; offset < length; offset++) {
			if (!target.push(this.get(offset))) return false;
		}
		return true;
	}

	/**
	 * Diese Methode konvertiert diese Wertliste in ein Array und gibt diese zurück.
	 * 
	 * @return Array mit den Werten dieser Wertliste.
	 */
	public Value[] value() {
		final int length = this.length();
		final Value[] values = new Value[length];
		final Collector collector = new Collector() {

			int index = 0;

			@Override
			public boolean push(final Value value) {
				values[this.index++] = value;
				return true;
			}

		};
		this.collect(collector, 0, length);
		return values;
	}

	/**
	 * Diese Methode gibt die Länge dieser Wertliste zurück.
	 * 
	 * @return Länge.
	 */
	public abstract int length();

	/**
	 * Diese Methode fügt alle Werte der Wertliste geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(Value)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public boolean collect(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException();
		return this.collect(target, 0, this.length());
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 * 
	 * @param array Wertliste.
	 * @return {@link Array}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public Array concat(final Array array) throws NullPointerException {
		if (array.length() == 0) return this;
		if (this.length() == 0) return array;
		return new Array() {

			@Override
			protected boolean collect(final Collector target, final int offset, final int length) {
				final int offset2 = offset - Array.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return array.collect(target, offset2, length);
				else if (length2 <= 0) return Array.this.collect(target, offset, length);
				else {
					if (!Array.this.collect(target, offset, -offset2)) return false;
					return array.collect(target, 0, length2);
				}
			}

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				final int index2 = index - Array.this.length();
				return index2 < 0 ? Array.this.get(index) : array.get(index2);
			}

			@Override
			public Array section(final int offset, final int length) throws IllegalArgumentException {
				final int offset2 = offset - Array.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return array.section(offset2, length);
				if (length2 <= 0) return super.section(offset, length);
				return super.section(offset, -offset2).concat(array.section(0, length2));
			}

			@Override
			public int length() {
				return Array.this.length() + array.length();
			}

		};
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link Array}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte.
	 */
	public Array section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length())) return this;
		if ((offset < 0) || (length < 0) || ((offset + length) > this.length())) throw new IllegalArgumentException();
		if (length == 0) return Array.EMPTY;
		return new Array() {

			@Override
			protected boolean collect(final Collector target, final int offset2, final int length2) {
				return Array.this.collect(target, offset + offset2, length2);
			}

			@Override
			public Value get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return Array.this.get(index + offset);
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public Array section(final int offset2, final int length2) throws IllegalArgumentException {
				return Array.this.section(offset + offset2, length2);
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

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 */
	@Override
	public abstract Value get(final int index) throws IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 0x811C9DC5;
		for (int i = 0, length = this.length(); i < length; i++) {
			hash = (hash * 0x01000193) ^ Objects.hash(this.get(i));
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Array)) return false;
		final Array data = (Array)object;
		final int length = this.length();
		if (data.length() != length) return false;
		for (int i = 0; i < length; i++) {
			if (!Objects.equals(this.get(i), data.get(i))) return false;
		}
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
