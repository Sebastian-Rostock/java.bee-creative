package bee.creative.fem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 * <p>
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von aufbereiteten Quelltexten.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class FEMScript implements Items<FEMScript.Range>, Iterable<FEMScript.Range> {

	/**
	 * Diese Klasse implementiert ein Objekt, dass einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Range)}
	 * erfolgt gemäß ihrer Startposition.
	 * 
	 * @see FEMScript
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class Range implements Comparable<FEMScript.Range> {

		/**
		 * Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind.
		 */
		public static final FEMScript.Range EMPTY = new Range((char)0, 0, 0);

		{}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Range#start() Startposition} ist, größer der {@link Range#end() Endposition} ist bzw. innerhalb der oder gleich den Grenzen des Bereichs liegt.
		 * 
		 * @see Range#end()
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen.
		 */
		public static final Comparable<FEMScript.Range> contains(final int index) {
			return new Comparable<FEMScript.Range>() {

				@Override
				public int compareTo(final FEMScript.Range value) {
					final int start = value.__start;
					return index < start ? -1 : index > (value.__length + start) ? +1 : 0;
				}

			};
		}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#end() Endposition}en mit der gegebenen Position vergleicht. Der
		 * Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position kleiner,
		 * gleich bzw. größer der {@link Range#end() Endposition} eines gegebenen Bereichs ist.
		 * 
		 * @see Range#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für das Ende von {@link Range}s.
		 */
		public static final Comparable<FEMScript.Range> endingAt(final int index) {
			return new Comparable<FEMScript.Range>() {

				@Override
				public int compareTo(final FEMScript.Range value) {
					return Comparators.compare(index, value.__start + value.__length);
				}

			};
		}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#start() Startposition}en mit der gegebenen Position vergleicht.
		 * Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position
		 * kleiner, gleich bzw. größer der {@link Range#start() Startposition} eines gegebenen Bereichs ist.
		 * 
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen.
		 */
		public static final Comparable<FEMScript.Range> startingAt(final int index) {
			return new Comparable<FEMScript.Range>() {

				@Override
				public int compareTo(final FEMScript.Range value) {
					return Comparators.compare(index, value.__start);
				}

			};
		}

		{}

		/**
		 * Dieses Feld speichert den Typ des Bereichs.
		 */
		final char __type;

		/**
		 * Dieses Feld speichert die Startposition.
		 */
		final int __start;

		/**
		 * Dieses Feld speichert die Länge.
		 */
		final int __length;

		/**
		 * Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn die Startposition oder die Länge negativ sind.
		 */
		public Range(final char type, final int start, final int length) throws IllegalArgumentException {
			if (start < 0) throw new IllegalArgumentException("start < 0");
			if (length < 0) throw new IllegalArgumentException("length < 0");
			this.__type = type;
			this.__start = start;
			this.__length = length;
		}

		{}

		/**
		 * Diese Methode gibt den Typ des Bereichs zurück.
		 * 
		 * @see FEMScript
		 * @return Bereichstyp.
		 */
		public final char type() {
			return this.__type;
		}

		/**
		 * Diese Methode gibt die Position zurück, vord der die {@link Range} endet.
		 * 
		 * @return Endposition.
		 */
		public final int end() {
			return this.__start + this.__length;
		}

		/**
		 * Diese Methode gibt die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public final int start() {
			return this.__start;
		}

		/**
		 * Diese Methode gibt die Länge des die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public final int length() {
			return this.__length;
		}

		/**
		 * Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.
		 * 
		 * @param source Zeichenkette.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 */
		public final String extract(final String source) throws NullPointerException {
			final int start = this.__start;
			return source.substring(start, start + this.__length);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int compareTo(final FEMScript.Range value) {
			return Comparators.compare(this.__start, value.__start);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			return this.__type ^ this.__start ^ this.__length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FEMScript.Range)) return false;
			final FEMScript.Range that = (FEMScript.Range)object;
			return (this.__start == that.__start) && (this.__length == that.__length) && (this.__type == that.__type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String toString() {
			return "'" + this.__type + "'@" + this.__start + "/" + this.__length;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den leeren Quelltext ohne Bereiche.
	 */
	public static final FEMScript EMPTY = new FEMScript("", new FEMScript.Range[0]);

	{}

	/**
	 * Dieses Feld speichert die Zeichenkette.
	 */
	final String __source;

	/**
	 * Dieses Feld speichert die Bereiche.
	 */
	final FEMScript.Range[] __ranges;

	/**
	 * Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen.
	 */
	public FEMScript(final String source, final FEMScript.Range[] ranges) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		final int length = source.length();
		for (final FEMScript.Range range: ranges) {
			final int start = range.__start;
			if (start < offset) throw new IllegalArgumentException("ranges overlapping");
			offset = start + range.__length;
		}
		if (offset > length) throw new IllegalArgumentException("ranges exceeding");
		this.__source = source;
		this.__ranges = ranges.clone();
	}

	/**
	 * Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen.
	 */
	public FEMScript(final String source, final Collection<? extends FEMScript.Range> ranges) throws NullPointerException, IllegalArgumentException {
		this(source, ranges.toArray(new FEMScript.Range[ranges.size()]));
	}

	{}

	/**
	 * Diese Methode gibt die Zeichenkette zurück.
	 * 
	 * @return Zeichenkette.
	 */
	public final String source() {
		return this.__source;
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Range#type() Typen} der {@link #ranges() Bereiche} das Zeichenkette zurück.
	 * 
	 * @see Range#type()
	 * @see #ranges()
	 * @return Bereichstypen als Zeichenkette.
	 */
	public final char[] types() {
		final int length = this.__ranges.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = this.__ranges[i].__type;
		}
		return types;
	}

	/**
	 * Diese Methode gibt eine Koppie der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Bereiche.
	 */
	public final FEMScript.Range[] ranges() {
		return this.__ranges.clone();
	}

	/**
	 * Diese Methode gibt die Anzahl der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #ranges()
	 * @see #iterator()
	 * @return Anzahl der Bereiche.
	 */
	public final int length() {
		return this.__ranges.length;
	}

	/**
	 * Diese Methode gibt diesen Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette}, der nicht in
	 * einem der {@link #ranges() Bereiche} enthalten ist.
	 * 
	 * @return normalisierter Quelltext.
	 */
	public final FEMScript normalize() {
		final List<FEMScript.Range> normalRanges = new ArrayList<>(this.__ranges.length);
		final StringBuilder normalSource = new StringBuilder();
		int start = 0;
		for (final FEMScript.Range range: this.__ranges) {
			final int length = range.__length;
			normalSource.append(range.extract(this.__source));
			normalRanges.add(new Range(range.__type, start, length));
			start += length;
		}
		return new FEMScript(normalSource.toString(), normalRanges);
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Bereich zurück.
	 */
	@Override
	public final FEMScript.Range get(final int index) throws IndexOutOfBoundsException {
		return this.__ranges[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<FEMScript.Range> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(this.__source) ^ Objects.hash((Object[])this.__ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMScript)) return false;
		final FEMScript that = (FEMScript)object;
		return this.__source.equals(that.__source) && Objects.equals(this.__ranges, that.__ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return this.source();
	}

}