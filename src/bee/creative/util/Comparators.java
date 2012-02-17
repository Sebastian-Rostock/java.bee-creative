package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zum Vergleich von Objekten sowie zur Erzeugung von
 * {@link Comparator}en.
 * 
 * @see Comparator
 * @author Sebastian Rostock 2011.
 */
public final class Comparators {

	/**
	 * Diese Klasse implementiert einen delegierenden {@link Comparator}, der seine Berechnungen an einen gegebenen
	 * {@link Comparator} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 * @param <GEntry2> Typ der Objekte des gegebenen {@link Comparator}s.
	 */
	static abstract class BaseComparator<GEntry, GEntry2> implements Comparator<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Comparator}.
		 */
		final Comparator<? super GEntry2> comparator;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public BaseComparator(final Comparator<? super GEntry2> comparator) throws NullPointerException {
			if(comparator == null) throw new NullPointerException("comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Diese Methode gibt den {@link Comparator} zurück.
		 * 
		 * @return {@link Comparator}.
		 */
		public Comparator<? super GEntry2> comparator() {
			return this.comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseComparator<?, ?> data = (BaseComparator<?, ?>)object;
			return Objects.equals(this.comparator, data.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der {@code null}-Eingaben vergleicht und alle anderen Eingaben
	 * an einen gegebenen {@link Comparator} weiterleitet. Der {@link Comparator} berechnet den Vergleichswert zweier
	 * Objekte {@code o1} und {@code o2} via:
	 * 
	 * <pre>
	 * ((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : comparator.compare(o1, o2)));
	 * </pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class NullComparator<GEntry> extends BaseComparator<GEntry, GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public NullComparator(final Comparator<? super GEntry> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry o1, final GEntry o2) {
			return ((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : this.comparator.compare(o1, o2)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof NullComparator<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("nullComparator", this.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der den Vergleichswert eines gegebenen {@link Comparator}s
	 * umkehrt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ReverseComparator<GEntry> extends BaseComparator<GEntry, GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public ReverseComparator(final Comparator<? super GEntry> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry o1, final GEntry o2) {
			return this.comparator.compare(o2, o1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ReverseComparator<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("reverseComparator", this.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der zwei {@link Iterable} mit Hilfe eines gegebenen
	 * {@link Comparator}s analog zu Zeichenketten vergleicht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der {@link Iterable}.
	 * @param <GValue> Typ der in den {@link Iterable} enthaltenen Werte sowie der vom gegebenen {@link Comparator}
	 *        verglichenen Objekte.
	 */
	public static final class IterableComparator<GEntry extends Iterable<? extends GValue>, GValue> extends
		BaseComparator<GEntry, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public IterableComparator(final Comparator<? super GValue> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry o1, final GEntry o2) {
			final Iterator<? extends GValue> i1 = o1.iterator(), i2 = o2.iterator();
			while(true){
				final boolean h1 = i1.hasNext(), h2 = i2.hasNext();
				if(!h1) return (h2 ? -1 : 0);
				if(!h2) return 1;
				final int comp = this.comparator.compare(i1.next(), i2.next());
				if(comp != 0) return comp;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof IterableComparator<?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("iterableComparator", this.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Comparator}, der die beiden Objekte in seiner Eingabe zuerst
	 * über einen ersten {@link Comparator} vergleicht und einen zweiten {@link Comparator} nur dann verwendet, wenn der
	 * erste {@link Comparator} die Gleichheit der beiden Objekte anzeigt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ChainedComparator<GEntry> implements Comparator<GEntry> {

		/**
		 * Dieses Feld speichert den primären {@link Comparator}.
		 */
		final Comparator<? super GEntry> comparator1;

		/**
		 * Dieses Feld speichert den sekundären {@link Comparator}.
		 */
		final Comparator<? super GEntry> comparator2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Comparator}en.
		 * 
		 * @param comparator1 primärer {@link Comparator}.
		 * @param comparator2 sekundärer {@link Comparator}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Comparator}en {@code null} ist.
		 */
		public ChainedComparator(final Comparator<? super GEntry> comparator1, final Comparator<? super GEntry> comparator2)
			throws NullPointerException {
			if(comparator1 == null) throw new NullPointerException("comparator1 is null");
			if(comparator2 == null) throw new NullPointerException("comparator2 is null");
			this.comparator1 = comparator1;
			this.comparator2 = comparator2;
		}

		/**
		 * Diese Methode gibt den primären {@link Comparator} zurück.
		 * 
		 * @return primärer {@link Comparator}.
		 */
		public Comparator<? super GEntry> comparator1() {
			return this.comparator1;
		}

		/**
		 * Diese Methode gibt den sekundären {@link Comparator} zurück.
		 * 
		 * @return sekundärer {@link Comparator}.
		 */
		public Comparator<? super GEntry> comparator2() {
			return this.comparator2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry o1, final GEntry o2) {
			final int comp = this.comparator1.compare(o1, o2);
			return ((comp != 0) ? comp : this.comparator2.compare(o1, o2));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparator1, this.comparator2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ChainedComparator<?>)) return false;
			final ChainedComparator<?> data = (ChainedComparator<?>)object;
			return Objects.equals(this.comparator1, data.comparator1, this.comparator2, data.comparator2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedComparator", this.comparator1, this.comparator2);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Comparator}, der die mit einem gegebenen {@link Converter}
	 * konvertierten Objekte zum Vergleich an einen gegebenen {@link Comparator} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Eingabe des {@link Converter}s sowie der vom {@link Comparator} zu vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter}s sowie der vom gegebenen {@link Comparator} zu vergleichenden
	 *        Objekte.
	 */
	public static final class ConvertedComparator<GEntry, GValue> extends BaseComparator<GEntry, GValue> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GEntry, ? extends GValue> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Comparator} und {@link Converter}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} bzw. der gegebene {@link Converter Converter}
		 *         {@code null} ist.
		 */
		public ConvertedComparator(final Comparator<? super GValue> comparator,
			final Converter<? super GEntry, ? extends GValue> converter) throws NullPointerException {
			super(comparator);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * Diese Methode gibt den {@link Converter} zurück.
		 * 
		 * @return den {@link Converter}.
		 */
		public Converter<? super GEntry, ? extends GValue> converter() {
			return this.converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry o1, final GEntry o2) {
			return this.comparator.compare(this.converter.convert(o1), this.converter.convert(o2));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparator, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedComparator<?, ?>)) return false;
			final ConvertedComparator<?, ?> data = (ConvertedComparator<?, ?>)object;
			return super.equals(object) && Objects.equals(this.converter, data.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedComparator", this.converter, this.comparator);
		}

	}

	/**
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, der als Zeichenkette kodierte {@link Integer}
	 * vergleicht.
	 */
	static final Comparator<String> STRING_NUMERICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String o1, final String o2) {
			final int s1 = o1.length(), s2 = o2.length();
			int a1 = 0, a2 = 0;
			{ // '0' überspringen
				while((a1 < s1) && (o1.charAt(a1) == '0')){
					a1++;
				}
				while((a2 < s2) && (o2.charAt(a2) == '0')){
					a2++;
				}
			}
			{ // War eine der Eingaben Leer oder "0"
				if(a1 == s1) return ((a2 == s2) ? 0 : -1);
				if(a2 == s2) return 1;
			}
			{ // Zahlen vergleichen
				final int comp = (s1 - a1) - (s2 - a2);
				if(comp != 0) return comp;
				return o1.substring(a1, s1).compareTo(o2.substring(a2, s2));
			}
		}

		@Override
		public String toString() {
			return Objects.toStringCall("stringNumericalComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, der Groß-/Kleinschreibung ignoriert.
	 */
	static final Comparator<String> STRING_ALPHABETICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String o1, final String o2) {
			return o1.compareToIgnoreCase(o2);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("stringAlphabeticalComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, gemischte Zeichenkette aus kodierten {@link Integer}
	 * und normalem Text vergleicht und dabei Groß-/Kleinschreibung ignoriert.
	 */
	static final Comparator<String> STRING_ALPHANUMERICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String o1, final String o2) {
			final int s1 = o1.length(), s2 = o2.length();
			int a1 = 0, a2 = 0, e1, e2;
			while((a1 < s1) && (a2 < s2)){
				{ // Buchstaben überspringen = Halt, wenn Ziffer gefunden
					for(e1 = a1; e1 < s1; e1++){
						final char c1 = o1.charAt(e1);
						if(('0' <= c1) && (c1 <= '9')){
							break;
						}
					}
					for(e2 = a2; e2 < s2; e2++){
						final char c2 = o2.charAt(e2);
						if(('0' <= c2) && (c2 <= '9')){
							break;
						}
					}
				}
				{ // Buchstaben vergleichen
					if(a1 == e1){
						if(a2 != e2) return -1;
					}else if(a2 != e2){
						final int comp = o1.substring(a1, e1).compareToIgnoreCase(o2.substring(a2, e2));
						if(comp != 0) return comp;
					}else return 1;
				}
				{ // '0' überspringen = Halt, wenn nicht '0' gefunden
					for(a1 = e1; a1 < s1; a1++){
						if(o1.charAt(a1) != '0'){
							break;
						}
					}
					for(a2 = e2; a2 < s2; a2++){
						if(o2.charAt(a2) != '0'){
							break;
						}
					}
				}
				{ // Ziffern überspringen = Halt, wenn nicht Ziffer gefunden
					for(e1 = a1; e1 < s1; e1++){
						final char c1 = o1.charAt(e1);
						if(('0' > c1) || (c1 > '9')){
							break;
						}
					}
					for(e2 = a2; e2 < s2; e2++){
						final char c2 = o2.charAt(e2);
						if(('0' > c2) || (c2 > '9')){
							break;
						}
					}
				}
				{ // Ziffern vergleichen
					if(a1 == e1){
						if(a2 != e2) return -1;
					}else if(a2 != e2){
						int comp = (e1 - a1) - (e2 - a2);
						if(comp != 0) return comp;
						comp = o1.substring(a1, e1).compareTo(o2.substring(a2, e2));
						if(comp != 0) return comp;
					}else return 1;
				}
				a1 = e1;
				a2 = e2;
			}
			return 0;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("stringAlphanumericalComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#longValue()}
	 * vergleicht.
	 */
	static final Comparator<Number> NUMBER_LONG_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number o1, final Number o2) {
			return Comparators.compare(o1.longValue(), o2.longValue());
		}

		@Override
		public String toString() {
			return Objects.toStringCall("numberLongComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#floatValue()}
	 * vergleicht.
	 */
	static final Comparator<Number> NUMBER_FLOAT_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number o1, final Number o2) {
			return Comparators.compare(o1.floatValue(), o2.floatValue());
		}

		@Override
		public String toString() {
			return Objects.toStringCall("numberFloatComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#intValue()}
	 * vergleicht.
	 */
	static final Comparator<Number> NUMBER_INTEGER_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number o1, final Number o2) {
			return Comparators.compare(o1.intValue(), o2.intValue());
		}

		@Override
		public String toString() {
			return Objects.toStringCall("numberIntegerComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#doubleValue()}
	 * vergleicht.
	 */
	static final Comparator<Number> NUMBER_DOUBLE_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number o1, final Number o2) {
			return Comparators.compare(o1.doubleValue(), o2.doubleValue());
		}

		@Override
		public String toString() {
			return Objects.toStringCall("numberDoubleComparator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} für die natürliche Ordnung.
	 */
	static final Comparator<? extends Comparable<?>> NATURAL_COMPARATOR = new Comparator<Comparable<Object>>() {

		@Override
		public int compare(final Comparable<Object> o1, final Comparable<Object> o2) {
			return o1.compareTo(o2);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("naturalComparator");
		}

	};

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als,
	 * gleich bzw. größer als der zweite Wert ist.
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final int value1, final int value2) {
		return (value1 < value2 ? -1 : (value1 == value2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als,
	 * gleich bzw. größer als der zweite Wert ist.
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final long value1, final long value2) {
		return (value1 < value2 ? -1 : (value1 == value2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als,
	 * gleich bzw. größer als der zweite Wert ist.
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final float value1, final float value2) {
		return (value1 < value2 ? -1 : (value1 > value2 ? 1 : 0));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als,
	 * gleich bzw. größer als der zweite Wert ist.
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final double value1, final double value2) {
		return (value1 < value2 ? -1 : (value1 > value2 ? 1 : 0));
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der {@code null}-Eingaben vergleicht sowie alle anderen Eingaben an
	 * einen gegebenen {@link Comparator} weiterleitet, und gibt ihn zurück. Der erzeugte {@link Comparator} berechnet den
	 * Vergleichswert zweier Objekte {@code o1} und {@code o2} via:
	 * 
	 * <pre>
	 * ((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : comparator.compare(o1, o2)))
	 * </pre>
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparator {@link Comparator}
	 * @return {@link NullComparator}
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry> Comparator<GEntry> nullComparator(final Comparator<? super GEntry> comparator)
		throws NullPointerException {
		return new NullComparator<GEntry>(comparator);
	}

	/**
	 * Diese Methode gibt den {@link Comparator} für die natürliche Ordnung zurück.
	 * 
	 * @see Comparable
	 * @param <GEntry> Typ der Objekte.
	 * @return {@link Comparable}-{@link Comparator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry extends Comparable<?>> Comparator<GEntry> naturalComparator() {
		return (Comparator<GEntry>)Comparators.NATURAL_COMPARATOR;
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der den Vergleichswert des gegebenen {@link Comparator}s umkehrt,
	 * und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparator {@link Comparator}.
	 * @return {@link ReverseComparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry> Comparator<GEntry> reverseComparator(final Comparator<? super GEntry> comparator)
		throws NullPointerException {
		return new ReverseComparator<GEntry>(comparator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der zwei {@link Iterable} mit Hilfe des gegebenen
	 * {@link Comparator}s analog zu Zeichenketten vergleicht, und gibt ihn zurück.
	 * 
	 * @see Iterable
	 * @param <GEntry> Typ der {@link Iterable}.
	 * @param <GValue> Typ der in den {@link Iterable} enthaltenen Werte sowie der vom gegebenen {@link Comparator} zu
	 *        verglichenen Objekte.
	 * @param comparator {@link Comparator}.
	 * @return {@link IterableComparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry extends Iterable<? extends GValue>, GValue> Comparator<GEntry> iterableComparator(
		final Comparator<? super GValue> comparator) throws NullPointerException {
		return new IterableComparator<GEntry, GValue>(comparator);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Comparator} und gibt ihn zurück. Der erzeugte {@link Comparator}
	 * vergleicht seine beiden Eingaben zuerst über den ersten {@link Comparator} und verwendet den zweiten
	 * {@link Comparator} nur dann, wenn der erste {@link Comparator} mit dem Vergleichswert {@code 0} die Gleichheit der
	 * beiden Objekte anzeigt.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparator1 erster {@link Comparator}.
	 * @param comparator2 zweiter {@link Comparator}.
	 * @return {@link ChainedComparator}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Comparator Comparatoren} {@code null} ist.
	 */
	public static <GEntry> Comparator<GEntry> chainedComparator(final Comparator<? super GEntry> comparator1,
		final Comparator<? super GEntry> comparator2) throws NullPointerException {
		return new ChainedComparator<GEntry>(comparator1, comparator2);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Comparator}, der die mit dem gegebenen {@link Converter}
	 * konvertierten Objekte zum Vergleich an den gegebenen {@link Comparator} delegiert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GEntry> Typ der Eingabe des {@link Converter} sowie der vom konvertierender {@link Comparator} zu
	 *        vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter} sowie der vom gegebenen {@link Comparator} zu vergleichenden
	 *        Objekte.
	 * @param converter {@link Converter}.
	 * @param comparator {@link Comparator}.
	 * @return {@link ConvertedComparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} oder der gegebene {@link Converter Converter}
	 *         {@code null} sind.
	 */
	public static <GEntry, GValue> Comparator<GEntry> convertedComparator(
		final Converter<? super GEntry, ? extends GValue> converter, final Comparator<? super GValue> comparator)
		throws NullPointerException {
		return new ConvertedComparator<GEntry, GValue>(comparator, converter);
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, der als Zeichenkette kodierte {@link Integer}
	 * vergleicht.
	 * 
	 * @return Numerischer {@link String}-{@link Comparator}.
	 */
	public static Comparator<String> stringNumericalComparator() {
		return Comparators.STRING_NUMERICAL_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, der Groß-/Kleinschreibung ignoriert.
	 * 
	 * @return Alphabetischer {@link String}-{@link Comparator}.
	 */
	public static Comparator<String> stringAlphabeticalComparator() {
		return Comparators.STRING_ALPHABETICAL_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, gemischte Zeichenkette aus kodierten
	 * {@link Integer} und normalem Text vergleicht und dabei Groß-/Kleinschreibung ignoriert.
	 * 
	 * @return Alphanumerischer {@link String}-{@link Comparator}.
	 */
	public static Comparator<String> stringAlphanumericalComparator() {
		return Comparators.STRING_ALPHANUMERICAL_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#longValue()}
	 * vergleicht.
	 * 
	 * @see Comparators#compare(long, long)
	 * @return {@link Number#longValue()}-{@link Number}-{@link Comparator}.
	 */
	public static Comparator<Number> numberLongComparator() {
		return Comparators.NUMBER_LONG_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#floatValue()}
	 * vergleicht.
	 * 
	 * @see Comparators#compare(float, float)
	 * @return {@link Number#floatValue()}-{@link Number}-{@link Comparator}.
	 */
	public static Comparator<Number> numberFloatComparator() {
		return Comparators.NUMBER_FLOAT_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#intValue()}
	 * vergleicht.
	 * 
	 * @see Comparators#compare(int, int)
	 * @return {@link Number#intValue()}-{@link Number}-{@link Comparator}.
	 */
	public static Comparator<Number> numberIntegerComparator() {
		return Comparators.NUMBER_INTEGER_COMPARATOR;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#doubleValue()}
	 * vergleicht.
	 * 
	 * @see Comparators#compare(double, double)
	 * @return {@link Number#doubleValue()}-{@link Number}-{@link Comparator}.
	 */
	public static Comparator<Number> numberDoubleComparator() {
		return Comparators.NUMBER_DOUBLE_COMPARATOR;
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Comparators() {
	}

}
