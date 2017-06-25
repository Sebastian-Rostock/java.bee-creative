package bee.creative.fem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMScript implements Items<Range>, Iterable<Range> {

	/** Diese Klasse implementiert ein Objekt, das einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Range)}
	 * erfolgt gemäß ihrer Startposition.
	 *
	 * @see FEMScript
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Range implements Comparable<Range> {

		/** Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind. */
		public static final Range EMPTY = new Range((char)0, 0, 0);

		{}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Range#start() Startposition} ist, größer der {@link Range#end() Endposition} ist bzw. innerhalb der oder gleich den Grenzen des Bereichs liegt.
		 *
		 * @see Range#end()
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Range> contains(final int index) {
			return new Comparable<Range>() {

				@Override
				public int compareTo(final Range value) {
					final int start = value.offset;
					return index < start ? -1 : index > (value.length + start) ? +1 : 0;
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#end() Endposition}en mit der gegebenen Position vergleicht.
		 * Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position
		 * kleiner, gleich bzw. größer der {@link Range#end() Endposition} eines gegebenen Bereichs ist.
		 *
		 * @see Range#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für das Ende von {@link Range}s. */
		public static Comparable<Range> endingAt(final int index) {
			return new Comparable<Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.offset + value.length);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#start() Startposition}en mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der {@link Range#start() Startposition} eines gegebenen Bereichs ist.
		 *
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Range> startingAt(final int index) {
			return new Comparable<Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.offset);
				}

			};
		}

		{}

		/** Dieses Feld speichert den Typ des Bereichs. */
		final char type;

		/** Dieses Feld speichert die Startposition. */
		final int offset;

		/** Dieses Feld speichert die Länge. */
		final int length;

		/** Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 *
		 * @param type Typ.
		 * @param offset Startposition.
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn die Startposition oder die Länge negativ sind. */
		public Range(final char type, final int offset, final int length) throws IllegalArgumentException {
			if (offset < 0) throw new IllegalArgumentException("offset < 0");
			if (length < 0) throw new IllegalArgumentException("length < 0");
			this.type = type;
			this.offset = offset;
			this.length = length;
		}

		{}

		/** Diese Methode gibt den Typ des Bereichs zurück.
		 *
		 * @see FEMScript
		 * @return Bereichstyp. */
		public final char type() {
			return this.type;
		}

		/** Diese Methode gibt die Position zurück, vor der dieser Bereich endet.
		 *
		 * @return Endposition. */
		public final int end() {
			return this.offset + this.length;
		}

		/** Diese Methode gibt die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Startposition. */
		public final int start() {
			return this.offset;
		}

		/** Diese Methode gibt die Länge des die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Startposition. */
		public final int length() {
			return this.length;
		}

		/** Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.
		 *
		 * @param source Zeichenkette.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public final String extract(final String source) throws NullPointerException {
			final int start = this.offset;
			return source.substring(start, start + this.length);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int compareTo(final Range value) {
			return Comparators.compare(this.offset, value.offset);
		}

		/** {@inheritDoc} */
		@Override
		public final int hashCode() {
			return this.type ^ this.offset ^ this.length;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Range)) return false;
			final Range that = (Range)object;
			return (this.offset == that.offset) && (this.length == that.length) && (this.type == that.type);
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "'" + this.type + "'@" + this.offset + "/" + this.length;
		}

	}

	{}

	/** Dieses Feld speichert den leeren Quelltext ohne Bereiche. */
	public static final FEMScript EMPTY = new FEMScript("", new Range[0]);

	{}

	/** Dieses Feld speichert die Zeichenkette. */
	final String source;

	/** Dieses Feld speichert die Bereiche. */
	final Range[] ranges;

	/** Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 *
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public FEMScript(final String source, final Range[] ranges) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		final int length = source.length();
		for (final Range range: ranges) {
			final int start = range.offset;
			if (start < offset) throw new IllegalArgumentException("ranges overlapping");
			offset = start + range.length;
		}
		if (offset > length) throw new IllegalArgumentException("ranges exceeding");
		this.source = source;
		this.ranges = ranges.clone();
	}

	/** Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 *
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public FEMScript(final String source, final Collection<Range> ranges) throws NullPointerException, IllegalArgumentException {
		this(source, ranges.toArray(new Range[ranges.size()]));
	}

	{}

	/** Diese Methode gibt die Zeichenkette des Quelltexts zurück.
	 *
	 * @return Zeichenkette. */
	public final String source() {
		return this.source;
	}

	/** Diese Methode gibt die Verkettung der {@link Range#type() Typen} der {@link #ranges() Bereiche} als Zeichenkette zurück.
	 *
	 * @see Range#type()
	 * @see #ranges()
	 * @return Bereichstypen als Zeichenkette. */
	public final char[] types() {
		final int length = this.ranges.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = this.ranges[i].type;
		}
		return types;
	}

	/** Diese Methode gibt eine Kopie der Bereiche zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Bereiche. */
	public final Range[] ranges() {
		return this.ranges.clone();
	}

	/** Diese Methode gibt die Anzahl der Bereiche zurück.
	 *
	 * @see #get(int)
	 * @see #ranges()
	 * @see #iterator()
	 * @return Anzahl der Bereiche. */
	public final int length() {
		return this.ranges.length;
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette}, der nicht in
	 * einem der {@link #ranges() Bereiche} enthalten ist.
	 *
	 * @return normalisierter Quelltext. */
	public final FEMScript normalize() {
		final List<Range> normalRanges = new ArrayList<>(this.ranges.length);
		final StringBuilder normalSource = new StringBuilder();
		int start = 0;
		for (final Range range: this.ranges) {
			final int length = range.length;
			normalSource.append(range.extract(this.source));
			normalRanges.add(new Range(range.type, start, length));
			start += length;
		}
		return new FEMScript(normalSource.toString(), normalRanges);
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Bereich zurück. */
	@Override
	public final Range get(final int index) throws IndexOutOfBoundsException {
		return this.ranges[index];
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Range> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return Objects.hash(this.source) ^ Objects.hash((Object[])this.ranges);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMScript)) return false;
		final FEMScript that = (FEMScript)object;
		return this.source.equals(that.source) && Objects.equals(this.ranges, that.ranges);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return this.source();
	}

}