package bee.creative.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Get;
import bee.creative.util.Comparators;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Script implements Get<Script.Range>, Iterable<Script.Range> {

	/**
	 * Diese Klasse implementiert ein Objekt, dass einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Range)}
	 * erfolgt gemäß ihrer Startposition.
	 * 
	 * @see Script
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class Range implements Comparable<Range> {

		/**
		 * Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind.
		 */
		public static final Range EMPTY = new Range((char)0, 0, 0);

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
		public static final Comparable<Range> contains(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					final int start = value.start;
					return index < start ? -1 : index > (value.length + start) ? +1 : 0;
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
		public static final Comparable<Range> endingAt(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.start + value.length);
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
		public static final Comparable<Range> startingAt(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.start);
				}

			};
		}

		{}

		/**
		 * Dieses Feld speichert den Typ des Bereichs.
		 */
		final char type;

		/**
		 * Dieses Feld speichert die Startposition.
		 */
		final int start;

		/**
		 * Dieses Feld speichert die Länge.
		 */
		final int length;

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
			this.type = type;
			this.start = start;
			this.length = length;
		}

		{}

		/**
		 * Diese Methode gibt den Typ des Bereichs zurück.
		 * 
		 * @see Script
		 * @return Bereichstyp.
		 */
		public char type() {
			return this.type;
		}

		/**
		 * Diese Methode gibt die Position zurück, vord der die {@link Range} endet.
		 * 
		 * @return Endposition.
		 */
		public int end() {
			return this.start + this.length;
		}

		/**
		 * Diese Methode gibt die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public int start() {
			return this.start;
		}

		/**
		 * Diese Methode gibt die Länge des die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public int length() {
			return this.length;
		}

		/**
		 * Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.
		 * 
		 * @param source Zeichenkette.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 */
		public String extract(final String source) throws NullPointerException {
			final int start = this.start;
			return source.substring(start, start + this.length);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final Range value) {
			return Comparators.compare(this.start, value.start);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.type ^ this.start ^ this.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Range)) return false;
			final Range data = (Range)object;
			return (this.start == data.start) && (this.length == data.length) && (this.type == data.type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "'" + this.type + "'@" + this.start + "/" + this.length;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den leeren Quelltext ohne Bereiche.
	 */
	public static final Script EMPTY = new Script("", new Range[0]);

	{}

	/**
	 * Dieses Feld speichert die Zeichenkette.
	 */
	final String source;

	/**
	 * Dieses Feld speichert die Bereiche.
	 */
	final Range[] ranges;

	/**
	 * Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen.
	 */
	public Script(final String source, final Range[] ranges) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		final int length = source.length();
		for (final Range range: ranges) {
			final int start = range.start;
			if (start < offset) throw new IllegalArgumentException("ranges overlapping");
			offset = start + range.length;
		}
		if (offset > length) throw new IllegalArgumentException("ranges exceeding");
		this.source = source;
		this.ranges = ranges.clone();
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
	public Script(final String source, final Collection<? extends Range> ranges) throws NullPointerException, IllegalArgumentException {
		this(source, ranges.toArray(new Range[ranges.size()]));
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Bereich zurück.
	 */
	@Override
	public Range get(final int index) throws IndexOutOfBoundsException {
		return this.ranges[index];
	}

	/**
	 * Diese Methode gibt die Zeichenkette zurück.
	 * 
	 * @return Zeichenkette.
	 */
	public String source() {
		return this.source;
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Range#type() Typen} der {@link #ranges() Bereiche} das Zeichenkette zurück.
	 * 
	 * @see Range#type()
	 * @see #ranges()
	 * @return Bereichstypen als Zeichenkette.
	 */
	public char[] types() {
		final Range[] ranges = this.ranges;
		final int length = ranges.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = ranges[i].type;
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
	public Range[] ranges() {
		return this.ranges.clone();
	}

	/**
	 * Diese Methode gibt die Anzahl der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #ranges()
	 * @see #iterator()
	 * @return Anzahl der Bereiche.
	 */
	public int length() {
		return this.ranges.length;
	}

	/**
	 * Diese Methode gibt diesen Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette}, der nicht in
	 * einem der {@link #ranges() Bereiche} enthalten ist.
	 * 
	 * @return normalisierter Quelltext.
	 */
	public Script normalize() {
		final List<Range> normalRanges = new ArrayList<Range>(this.ranges.length);
		final StringBuilder normalSource = new StringBuilder();
		final String source = this.source;
		int start = 0;
		for (final Range range: this.ranges) {
			final int length = range.length;
			normalSource.append(range.extract(source));
			normalRanges.add(new Range(range.type, start, length));
			start += length;
		}
		return new Script(normalSource.toString(), normalRanges);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Range> iterator() {
		return new GetIterator<Range>(this, this.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.source) ^ Objects.hash((Object[])this.ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Script)) return false;
		final Script data = (Script)object;
		return Objects.equals(this.source, data.source) && Objects.equals(this.ranges, data.ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.source();
	}

}
