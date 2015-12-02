package bee.creative.fem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import bee.creative.fem.Scripts.ScriptFormatter;
import bee.creative.fem.Scripts.ScriptFormatterInput;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert eine unmodifizierbare Liste von Werten sowie Methoden zur Erzeugung solcher Wertlisten aus nativen Arrays und {@link Iterable}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMArray implements Items<FEMValue>, Iterable<FEMValue>, ScriptFormatterInput {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste.
	 * 
	 * @see FEMArray#collect(Collector)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 * 
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(FEMValue value);

	}

	{}

	/**
	 * Dieses Feld speichert eine leere Wertliste.
	 */
	public static final FEMArray EMPTY = new FEMArray() {

		@Override
		public FEMValue get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int length() {
			return 0;
		}

	};

	{}

	/**
	 * Diese Methode konvertiert das gegebene native Array in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere änderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden. Die Elemente des
	 * kopierten Arrays werden üver {@link Values#valueOf(Object)} bei jedem Zugriff via {@link #get(int)} in Werte überführt.
	 * 
	 * @see java.lang.reflect.Array#get(Object, int)
	 * @see java.lang.reflect.Array#getLength(Object)
	 * @see java.lang.reflect.Array#newInstance(Class, int)
	 * @param data nativee Array.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code data} {@link Class#isArray() kein Array ist}.
	 */
	public static FEMArray from(Object data) throws NullPointerException, IllegalArgumentException {
		if (data == null) throw new NullPointerException("data = null");
		final int length = java.lang.reflect.Array.getLength(data);
		if (length == 0) return FEMArray.EMPTY;
		final Object values = java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), length);
		System.arraycopy(data, 0, values, 0, length);
		data = null;
		return new FEMArray() {

			@Override
			public FEMValue get(final int index) throws IndexOutOfBoundsException {
				if (index < 0) throw new IndexOutOfBoundsException("index < 0");
				if (index >= length) throw new IndexOutOfBoundsException("index >= length");
				final Object value = java.lang.reflect.Array.get(values, index);
				return Values.valueOf(value);
			}

			@Override
			public int length() {
				return length;
			}

		};
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere änderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden.
	 * 
	 * @param data Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMArray valueOf(FEMValue... data) throws NullPointerException {
		if (data.length == 0) return FEMArray.EMPTY;
		final FEMValue[] values = data = data.clone();
		return new FEMArray() {

			@Override
			public FEMValue get(final int index) throws IndexOutOfBoundsException {
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
	 * @see #valueOf(Collection)
	 * @param data Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMArray valueOf(final Iterable<? extends FEMValue> data) throws NullPointerException {
		final ArrayList<FEMValue> result = new ArrayList<>();
		Iterables.appendAll(result, data);
		return FEMArray.valueOf(result);
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Collection#toArray(Object[])
	 * @see #valueOf(FEMValue...)
	 * @param data Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMArray valueOf(final Collection<? extends FEMValue> data) throws NullPointerException {
		if (data.size() == 0) return FEMArray.EMPTY;
		return FEMArray.valueOf(data.toArray(new FEMValue[data.size()]));
	}

	{}

	/**
	 * Diese Methode fügt alle Werte im gegebenen Abschnitt geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig abgebrochen, wenn
	 * {@link Collector#push(FEMValue)} {@code false} liefert.
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
	public FEMValue[] value() {
		final int length = this.length();
		final FEMValue[] values = new FEMValue[length];
		final Collector collector = new Collector() {

			int index = 0;

			@Override
			public boolean push(final FEMValue value) {
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
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public boolean collect(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target = null");
		return this.collect(target, 0, this.length());
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 * 
	 * @param array Wertliste.
	 * @return {@link FEMArray}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public FEMArray concat(final FEMArray array) throws NullPointerException {
		if (array.length() == 0) return this;
		if (this.length() == 0) return array;
		return new FEMArray() {

			@Override
			protected boolean collect(final Collector target, final int offset, final int length) {
				final int offset2 = offset - FEMArray.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return array.collect(target, offset2, length);
				else if (length2 <= 0) return FEMArray.this.collect(target, offset, length);
				else {
					if (!FEMArray.this.collect(target, offset, -offset2)) return false;
					return array.collect(target, 0, length2);
				}
			}

			@Override
			public FEMValue get(final int index) throws IndexOutOfBoundsException {
				final int index2 = index - FEMArray.this.length();
				return index2 < 0 ? FEMArray.this.get(index) : array.get(index2);
			}

			@Override
			public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
				final int offset2 = offset - FEMArray.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return array.section(offset2, length);
				if (length2 <= 0) return super.section(offset, length);
				return super.section(offset, -offset2).concat(array.section(0, length2));
			}

			@Override
			public int length() {
				return FEMArray.this.length() + array.length();
			}

		};
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte.
	 */
	public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length())) return this;
		if ((offset < 0) || (length < 0) || ((offset + length) > this.length())) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		return new FEMArray() {

			@Override
			protected boolean collect(final Collector target, final int offset2, final int length2) {
				return FEMArray.this.collect(target, offset + offset2, length2);
			}

			@Override
			public FEMValue get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return FEMArray.this.get(index + offset);
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public FEMArray section(final int offset2, final int length2) throws IllegalArgumentException {
				return FEMArray.this.section(offset + offset2, length2);
			}

		};
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 */
	@Override
	public abstract FEMValue get(final int index) throws IndexOutOfBoundsException;

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
		if (!(object instanceof FEMArray)) return false;
		final FEMArray data = (FEMArray)object;
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
	public Iterator<FEMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putArray(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Scripts.scriptFormatter().formatData((Object)this);
	}

}
