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
 * <p>
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von aufbereiteten Quelltexten.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMScript implements Items<Range>, Iterable<Range> {

	/** Diese Klasse implementiert ein Objekt, dass einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Range)}
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
					final int start = value._offset_;
					return index < start ? -1 : index > (value._length_ + start) ? +1 : 0;
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#end() Endposition}en mit der gegebenen Position vergleicht. Der
		 * Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position kleiner,
		 * gleich bzw. größer der {@link Range#end() Endposition} eines gegebenen Bereichs ist.
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
					return Comparators.compare(index, value._offset_ + value._length_);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#start() Startposition}en mit der gegebenen Position vergleicht.
		 * Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position
		 * kleiner, gleich bzw. größer der {@link Range#start() Startposition} eines gegebenen Bereichs ist.
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
					return Comparators.compare(index, value._offset_);
				}

			};
		}

		{}

		/** Dieses Feld speichert den Typ des Bereichs. */
		final char _type_;

		/** Dieses Feld speichert die Startposition. */
		final int _offset_;

		/** Dieses Feld speichert die Länge. */
		final int _length_;

		/** Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn die Startposition oder die Länge negativ sind. */
		public Range(final char type, final int start, final int length) throws IllegalArgumentException {
			if (start < 0) throw new IllegalArgumentException("start < 0");
			if (length < 0) throw new IllegalArgumentException("length < 0");
			this._type_ = type;
			this._offset_ = start;
			this._length_ = length;
		}

		{}

		/** Diese Methode gibt den Typ des Bereichs zurück.
		 * 
		 * @see FEMScript
		 * @return Bereichstyp. */
		public final char type() {
			return this._type_;
		}

		/** Diese Methode gibt die Position zurück, vord der die {@link Range} endet.
		 * 
		 * @return Endposition. */
		public final int end() {
			return this._offset_ + this._length_;
		}

		/** Diese Methode gibt die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition. */
		public final int start() {
			return this._offset_;
		}

		/** Diese Methode gibt die Länge des die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition. */
		public final int length() {
			return this._length_;
		}

		/** Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.
		 * 
		 * @param source Zeichenkette.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public final String extract(final String source) throws NullPointerException {
			final int start = this._offset_;
			return source.substring(start, start + this._length_);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int compareTo(final Range value) {
			return Comparators.compare(this._offset_, value._offset_);
		}

		/** {@inheritDoc} */
		@Override
		public final int hashCode() {
			return this._type_ ^ this._offset_ ^ this._length_;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Range)) return false;
			final Range that = (Range)object;
			return (this._offset_ == that._offset_) && (this._length_ == that._length_) && (this._type_ == that._type_);
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "'" + this._type_ + "'@" + this._offset_ + "/" + this._length_;
		}

	}

	{}

	/** Dieses Feld speichert den leeren Quelltext ohne Bereiche. */
	public static final FEMScript EMPTY = new FEMScript("", new Range[0]);

	{}

	/** Dieses Feld speichert die Zeichenkette. */
	final String _source_;

	/** Dieses Feld speichert die Bereiche. */
	final Range[] _ranges_;

	/** Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen. */
	public FEMScript(final String source, final Range[] ranges) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		final int length = source.length();
		for (final Range range: ranges) {
			final int start = range._offset_;
			if (start < offset) throw new IllegalArgumentException("ranges overlapping");
			offset = start + range._length_;
		}
		if (offset > length) throw new IllegalArgumentException("ranges exceeding");
		this._source_ = source;
		this._ranges_ = ranges.clone();
	}

	/** Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen. */
	public FEMScript(final String source, final Collection<? extends Range> ranges) throws NullPointerException, IllegalArgumentException {
		this(source, ranges.toArray(new Range[ranges.size()]));
	}

	{}

	/** Diese Methode gibt die Zeichenkette zurück.
	 * 
	 * @return Zeichenkette. */
	public final String source() {
		return this._source_;
	}

	/** Diese Methode gibt die Verkettung der {@link Range#type() Typen} der {@link #ranges() Bereiche} als Zeichenkette zurück.
	 * 
	 * @see Range#type()
	 * @see #ranges()
	 * @return Bereichstypen als Zeichenkette. */
	public final char[] types() {
		final int length = this._ranges_.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = this._ranges_[i]._type_;
		}
		return types;
	}

	/** Diese Methode gibt eine Koppie der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Bereiche. */
	public final Range[] ranges() {
		return this._ranges_.clone();
	}

	/** Diese Methode gibt die Anzahl der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #ranges()
	 * @see #iterator()
	 * @return Anzahl der Bereiche. */
	public final int length() {
		return this._ranges_.length;
	}

	/** Diese Methode gibt diesen Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette}, der nicht in
	 * einem der {@link #ranges() Bereiche} enthalten ist.
	 * 
	 * @return normalisierter Quelltext. */
	public final FEMScript normalize() {
		final List<Range> normalRanges = new ArrayList<>(this._ranges_.length);
		final StringBuilder normalSource = new StringBuilder();
		int start = 0;
		for (final Range range: this._ranges_) {
			final int length = range._length_;
			normalSource.append(range.extract(this._source_));
			normalRanges.add(new Range(range._type_, start, length));
			start += length;
		}
		return new FEMScript(normalSource.toString(), normalRanges);
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Bereich zurück. */
	@Override
	public final Range get(final int index) throws IndexOutOfBoundsException {
		return this._ranges_[index];
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Range> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return Objects.hash(this._source_) ^ Objects.hash((Object[])this._ranges_);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMScript)) return false;
		final FEMScript that = (FEMScript)object;
		return this._source_.equals(that._source_) && Objects.equals(this._ranges_, that._ranges_);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return this.source();
	}

}