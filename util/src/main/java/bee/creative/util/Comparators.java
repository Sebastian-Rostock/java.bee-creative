package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zum Vergleich von Objekten sowie zur Erzeugung von {@link Comparator}en.
 * 
 * @see Comparator
 * @author Sebastian Rostock 2011.
 */
public class Comparators {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Comparator}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class AbstractComparator<GEntry> implements Comparator<GEntry> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Comparator}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Comparator}s.
	 */
	static abstract class AbstractDelegatingComparator<GEntry, GEntry2> implements Comparator<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Comparator}.
		 */
		final Comparator<? super GEntry2> comparator;

		/**
		 * Dieser Konstruktor initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public AbstractDelegatingComparator(final Comparator<? super GEntry2> comparator) throws NullPointerException {
			if(comparator == null) throw new NullPointerException("comparator is null");
			this.comparator = comparator;
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
			final AbstractDelegatingComparator<?, ?> data = (AbstractDelegatingComparator<?, ?>)object;
			return Objects.equals(this.comparator, data.comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der {@code null}-Eingaben vergleicht und alle anderen Eingaben an einen gegebenen {@link Comparator}
	 * weiterleitet. Der {@link Comparator} berechnet den Vergleichswert zweier Objekte {@code value1} und {@code value2} via:
	 * 
	 * <pre>
	 * ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : comparator.compare(value1, value2)));
	 * </pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class NullComparator<GEntry> extends AbstractDelegatingComparator<GEntry, GEntry> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Comparator}.
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
		public int compare(final GEntry value1, final GEntry value2) {
			return Comparators.compare(value1, value2, this.comparator);
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

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der den Vergleichswert eines gegebenen {@link Comparator}s umkehrt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ReverseComparator<GEntry> extends AbstractDelegatingComparator<GEntry, GEntry> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Comparator}.
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
		public int compare(final GEntry value1, final GEntry value2) {
			return this.comparator.compare(value2, value1);
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

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparator}, der zwei {@link Iterable} mit Hilfe eines gegebenen {@link Comparator}s analog zu Zeichenketten
	 * vergleicht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der {@link Iterable}.
	 * @param <GItem> Typ der in den {@link Iterable} enthaltenen Werte sowie der vom gegebenen {@link Comparator} verglichenen Objekte.
	 */
	public static final class IterableComparator<GEntry extends Iterable<? extends GItem>, GItem> extends AbstractDelegatingComparator<GEntry, GItem> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public IterableComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry value1, final GEntry value2) {
			return Comparators.compare(value1, value2, this.comparator);
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

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Comparator}, der die beiden Objekte in seiner Eingabe zuerst über einen ersten {@link Comparator}
	 * vergleicht und einen zweiten {@link Comparator} nur dann verwendet, wenn der erste {@link Comparator} die Gleichheit der beiden Objekte anzeigt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ChainedComparator<GEntry> extends AbstractComparator<GEntry> {

		/**
		 * Dieses Feld speichert den primären {@link Comparator}.
		 */
		final Comparator<? super GEntry> comparator1;

		/**
		 * Dieses Feld speichert den sekundären {@link Comparator}.
		 */
		final Comparator<? super GEntry> comparator2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Comparator}en.
		 * 
		 * @param comparator1 primärer {@link Comparator}.
		 * @param comparator2 sekundärer {@link Comparator}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ChainedComparator(final Comparator<? super GEntry> comparator1, final Comparator<? super GEntry> comparator2) throws NullPointerException {
			if(comparator1 == null) throw new NullPointerException("comparator1 is null");
			if(comparator2 == null) throw new NullPointerException("comparator2 is null");
			this.comparator1 = comparator1;
			this.comparator2 = comparator2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GEntry value1, final GEntry value2) {
			final int comp = this.comparator1.compare(value1, value2);
			return ((comp != 0) ? comp : this.comparator2.compare(value1, value2));
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
			return Objects.equals(this.comparator1, data.comparator1) && Objects.equals(this.comparator2, data.comparator2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.comparator1, this.comparator2);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Comparator}, der die mit einem gegebenen {@link Converter} konvertierten Objekte zum Vergleich an
	 * einen gegebenen {@link Comparator} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des {@link Converter}s sowie der vom {@link Comparator} zu vergleichenden Objekte.
	 * @param <GOutput> Typ der Ausgabe des {@link Converter}s sowie der vom gegebenen {@link Comparator} zu vergleichenden Objekte.
	 */
	public static final class ConvertedComparator<GInput, GOutput> extends AbstractDelegatingComparator<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Comparator} und {@link Converter}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedComparator(final Comparator<? super GOutput> comparator, final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			super(comparator);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final GInput value1, final GInput value2) {
			return this.comparator.compare(this.converter.convert(value1), this.converter.convert(value2));
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
			return Objects.equals(this.converter, data.converter) && Objects.equals(this.comparator, data.comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.comparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Comparator} für die natürliche Ordnung.
	 * 
	 * @see Comparable#compareTo(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NaturalComparator extends AbstractComparator<Comparable<Object>> {

		/**
		 * Dieses Feld speichert den {@link NaturalComparator}.
		 */
		public static final Comparator<? extends Comparable<?>> INSTANCE = new NaturalComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Comparable<Object> value1, final Comparable<Object> value2) {
			return value1.compareTo(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NaturalComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#longValue()} vergleicht.
	 * 
	 * @see Comparators#compare(long, long)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberLongComparator extends AbstractComparator<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberLongComparator}.
		 */
		public static final NumberLongComparator INSTANCE = new NumberLongComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Number value1, final Number value2) {
			return Comparators.compare(value1.longValue(), value2.longValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NumberLongComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#floatValue()} vergleicht.
	 * 
	 * @see Comparators#compare(float, float)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberFloatComparator extends AbstractComparator<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberFloatComparator}.
		 */
		public static final NumberFloatComparator INSTANCE = new NumberFloatComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Number value1, final Number value2) {
			return Comparators.compare(value1.floatValue(), value2.floatValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NumberFloatComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#intValue()} vergleicht.
	 * 
	 * @see Comparators#compare(int, int)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberIntegerComparator extends AbstractComparator<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberIntegerComparator}.
		 */
		public static final NumberIntegerComparator INSTANCE = new NumberIntegerComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Number value1, final Number value2) {
			return Comparators.compare(value1.intValue(), value2.intValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NumberIntegerComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#doubleValue()} vergleicht.
	 * 
	 * @see Comparators#compare(double, double)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberDoubleComparator extends AbstractComparator<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberDoubleComparator}.
		 */
		public static final NumberDoubleComparator INSTANCE = new NumberDoubleComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Number value1, final Number value2) {
			return Comparators.compare(value1.doubleValue(), value2.doubleValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NumberDoubleComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link String}-{@link Comparator}, der als Zeichenkette kodierte {@link Integer} vergleicht.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringNumericalComparator extends AbstractComparator<String> {

		/**
		 * Dieses Feld speichert den {@link StringNumericalComparator}.
		 */
		public static final StringNumericalComparator INSTANCE = new StringNumericalComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final String value1, final String value2) {
			final int s1 = value1.length(), s2 = value2.length();
			int a1 = 0, a2 = 0;
			{ // '0' überspringen
				while((a1 < s1) && (value1.charAt(a1) == '0')){
					a1++;
				}
				while((a2 < s2) && (value2.charAt(a2) == '0')){
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
				return value1.substring(a1, s1).compareTo(value2.substring(a2, s2));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof StringNumericalComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link String}-{@link Comparator}, der Groß-/Kleinschreibung ignoriert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringAlphabeticalComparator extends AbstractComparator<String> {

		/**
		 * Dieses Feld speichert den {@link StringAlphabeticalComparator}
		 */
		public static final StringAlphabeticalComparator INSTANCE = new StringAlphabeticalComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final String value1, final String value2) {
			return value1.compareToIgnoreCase(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof StringAlphabeticalComparator);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link String}-{@link Comparator}, gemischte Zeichenkette aus kodierten {@link Integer} und normalem Text vergleicht und
	 * dabei Groß-/Kleinschreibung ignoriert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringAlphanumericalComparator extends AbstractComparator<String> {

		/**
		 * Dieses Feld speichert den {@link StringAlphanumericalComparator}.
		 */
		public static final StringAlphanumericalComparator INSTANCE = new StringAlphanumericalComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final String value1, final String value2) {
			final int s1 = value1.length(), s2 = value2.length();
			int a1 = 0, a2 = 0, e1, e2;
			while((a1 < s1) && (a2 < s2)){
				{ // Buchstaben überspringen = Halt, wenn Ziffer gefunden
					for(e1 = a1; e1 < s1; e1++){
						final char c1 = value1.charAt(e1);
						if(('0' <= c1) && (c1 <= '9')){
							break;
						}
					}
					for(e2 = a2; e2 < s2; e2++){
						final char c2 = value2.charAt(e2);
						if(('0' <= c2) && (c2 <= '9')){
							break;
						}
					}
				}
				{ // Buchstaben vergleichen
					if(a1 == e1){
						if(a2 != e2) return -1;
					}else if(a2 != e2){
						final int comp = value1.substring(a1, e1).compareToIgnoreCase(value2.substring(a2, e2));
						if(comp != 0) return comp;
					}else return 1;
				}
				{ // '0' überspringen = Halt, wenn nicht '0' gefunden
					for(a1 = e1; a1 < s1; a1++){
						if(value1.charAt(a1) != '0'){
							break;
						}
					}
					for(a2 = e2; a2 < s2; a2++){
						if(value2.charAt(a2) != '0'){
							break;
						}
					}
				}
				{ // Ziffern überspringen = Halt, wenn nicht Ziffer gefunden
					for(e1 = a1; e1 < s1; e1++){
						final char c1 = value1.charAt(e1);
						if(('0' > c1) || (c1 > '9')){
							break;
						}
					}
					for(e2 = a2; e2 < s2; e2++){
						final char c2 = value2.charAt(e2);
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
						comp = value1.substring(a1, e1).compareTo(value2.substring(a2, e2));
						if(comp != 0) return comp;
					}else return 1;
				}
				a1 = e1;
				a2 = e2;
			}
			return (s1 - a1) - (s2 - a2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof StringAlphanumericalComparator);
		}

	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(value1 < value2 ? -1 : (value1 == value2 ? 0 : 1))</pre>
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final int value1, final int value2) {
		return (value1 < value2 ? -1 : (value1 == value2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(value1 < value2 ? -1 : (value1 == value2 ? 0 : 1))</pre>
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final long value1, final long value2) {
		return (value1 < value2 ? -1 : (value1 == value2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(value1 < value2 ? -1 : (value1 == value2 ? 0 : 1))</pre>
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final float value1, final float value2) {
		return (value1 < value2 ? -1 : (value1 > value2 ? 1 : 0));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(value1 < value2 ? -1 : (value1 == value2 ? 0 : 1))</pre>
	 * 
	 * @param value1 erster Wert.
	 * @param value2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final double value1, final double value2) {
		return (value1 < value2 ? -1 : (value1 > value2 ? 1 : 0));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>
	 * ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : value1.compareTo(value2)));
	 * </pre>
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param value1 erstes Objekt.
	 * @param value2 zweites Objekt.
	 * @return Vergleichswert.
	 */
	public static <GEntry extends Comparable<? super GEntry>> int compare(final GEntry value1, final GEntry value2) {
		return ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : value1.compareTo(value2)));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>
	 * ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : comparator.compare(value1, value2)))
	 * </pre>
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param value1 erstes Objekt.
	 * @param value2 zweites Objekt.
	 * @param comparator {@link Comparator}.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry> int compare(final GEntry value1, final GEntry value2, final Comparator<? super GEntry> comparator) throws NullPointerException {
		return ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : comparator.compare(value1, value2)));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@link Iterable} ist. Die gegebenen {@link Iterable} werden für den Verglcich parallel iteriert. Wenn der erste {@link Iterator} kein nächstes
	 * Element besitzt, der zweite {@link Iterator} jedoch ein nächstes Element liefern kann, wird {@code -1} zurück gegeben. Wenn beide {@link Iterator}en je ein
	 * nächstes Element liefern können, werden diese mit dem gegebenen {@link Comparator} verglichen. Wenn der so berechnete Vergleichswert unglich {@code 0} ist,
	 * wird er zurück gegeben. Anderenfalls läuft die Iteration weiter. Wenn der erste {@link Iterator} ein nächstes Element besitzt, der zweite {@link Iterator}
	 * jedoch kein nächstes Element liefern kann, wird {@code 1} zurück gegeben.
	 * 
	 * @param <GEntry> Typ der Elemente der {@link Iterable}.
	 * @param value1 erster {@link Iterable}.
	 * @param value2 zweiter {@link Iterable}.
	 * @param comparator {@link Comparator} für die Elemente der {@link Iterable}.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> int compare(final Iterable<? extends GEntry> value1, final Iterable<? extends GEntry> value2,
		final Comparator<? super GEntry> comparator) throws NullPointerException {
		final Iterator<? extends GEntry> i1 = value1.iterator(), i2 = value2.iterator();
		while(true){
			final boolean h1 = i1.hasNext(), h2 = i2.hasNext();
			if(!h1) return (h2 ? -1 : 0);
			if(!h2) return 1;
			final int comp = comparator.compare(i1.next(), i2.next());
			if(comp != 0) return comp;
		}
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der {@code null}-Eingaben vergleicht sowie alle anderen Eingaben an einen gegebenen {@link Comparator}
	 * weiterleitet, und gibt ihn zurück. Der erzeugte {@link Comparator} berechnet den Vergleichswert zweier Objekte {@code value1} und {@code value2} via:
	 * 
	 * <pre>
	 * ((value1 == null) ? ((value2 == null) ? 0 : -1) : ((value2 == null) ? 1 : comparator.compare(value1, value2)))
	 * </pre>
	 * 
	 * @see Comparators#compare(Object, Object, Comparator)
	 * @param <GEntry> Typ der Elemente.
	 * @param comparator {@link Comparator}
	 * @return {@link NullComparator}
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry> NullComparator<GEntry> nullComparator(final Comparator<? super GEntry> comparator) throws NullPointerException {
		return new NullComparator<GEntry>(comparator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der den Vergleichswert des gegebenen {@link Comparator}s umkehrt, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@link ReverseComparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry> ReverseComparator<GEntry> reverseComparator(final Comparator<? super GEntry> comparator) throws NullPointerException {
		return new ReverseComparator<GEntry>(comparator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparator}, der zwei {@link Iterable} mit Hilfe des gegebenen {@link Comparator}s analog zu Zeichenketten vergleicht,
	 * und gibt ihn zurück.
	 * 
	 * @see Iterable
	 * @see Comparators#compare(Iterable, Iterable, Comparator)
	 * @param <GEntry> Typ der {@link Iterable}.
	 * @param <GItem> Typ der in den {@link Iterable} enthaltenen Werte sowie der vom gegebenen {@link Comparator} zu verglichenen Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@link IterableComparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public static <GEntry extends Iterable<? extends GItem>, GItem> IterableComparator<GEntry, GItem> iterableComparator(
		final Comparator<? super GItem> comparator) throws NullPointerException {
		return new IterableComparator<GEntry, GItem>(comparator);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Comparator} und gibt ihn zurück. Der erzeugte {@link Comparator} vergleicht seine beiden Eingaben zuerst
	 * über den ersten {@link Comparator} und verwendet den zweiten {@link Comparator} nur dann, wenn der erste {@link Comparator} mit dem Vergleichswert
	 * {@code 0} die Gleichheit der beiden Objekte anzeigt.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparator1 erster {@link Comparator}.
	 * @param comparator2 zweiter {@link Comparator}.
	 * @return {@link ChainedComparator}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> ChainedComparator<GEntry> chainedComparator(final Comparator<? super GEntry> comparator1, final Comparator<? super GEntry> comparator2)
		throws NullPointerException {
		return new ChainedComparator<GEntry>(comparator1, comparator2);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Comparator}, der die mit dem gegebenen {@link Converter} konvertierten Objekte zum Vergleich an den
	 * gegebenen {@link Comparator} delegiert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des {@link Converter} sowie der vom konvertierender {@link Comparator} zu vergleichenden Elemente.
	 * @param <GOutput> Typ der Ausgabe des {@link Converter} sowie der vom gegebenen {@link Comparator} zu vergleichenden Elemente.
	 * @param converter {@link Converter}.
	 * @param comparator {@link Comparator}.
	 * @return {@link ConvertedComparator}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedComparator<GInput, GOutput> convertedComparator(final Converter<? super GInput, ? extends GOutput> converter,
		final Comparator<? super GOutput> comparator) throws NullPointerException {
		return new ConvertedComparator<GInput, GOutput>(comparator, converter);
	}

	/**
	 * Diese Methode gibt den {@link Comparator} für die natürliche Ordnung zurück.
	 * 
	 * @see Comparable
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link NaturalComparator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry extends Comparable<?>> Comparator<GEntry> naturalComparator() {
		return (Comparator<GEntry>)NaturalComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#longValue()} vergleicht.
	 * 
	 * @see Comparators#compare(long, long)
	 * @return {@link NumberLongComparator}.
	 */
	public static Comparator<Number> numberLongComparator() {
		return NumberLongComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#floatValue()} vergleicht.
	 * 
	 * @see Comparators#compare(float, float)
	 * @return {@link NumberFloatComparator}.
	 */
	public static Comparator<Number> numberFloatComparator() {
		return NumberFloatComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#intValue()} vergleicht.
	 * 
	 * @see Comparators#compare(int, int)
	 * @return {@link NumberIntegerComparator}.
	 */
	public static Comparator<Number> numberIntegerComparator() {
		return NumberIntegerComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link Number}-{@link Comparator} zurück, der Zahlen über ihren {@link Number#doubleValue()} vergleicht.
	 * 
	 * @see Comparators#compare(double, double)
	 * @return {@link NumberDoubleComparator}.
	 */
	public static Comparator<Number> numberDoubleComparator() {
		return NumberDoubleComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, der als Zeichenkette kodierte {@link Integer} vergleicht.
	 * 
	 * @return {@link StringNumericalComparator}.
	 */
	public static Comparator<String> stringNumericalComparator() {
		return StringNumericalComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, der Groß-/Kleinschreibung ignoriert.
	 * 
	 * @return {@link StringAlphabeticalComparator}.
	 */
	public static Comparator<String> stringAlphabeticalComparator() {
		return StringAlphabeticalComparator.INSTANCE;
	}

	/**
	 * Diese Methode gibt den {@link String}-{@link Comparator} zurück, gemischte Zeichenkette aus kodierten {@link Integer} und normalem Text vergleicht und
	 * dabei Groß-/Kleinschreibung ignoriert.
	 * 
	 * @return {@link StringAlphanumericalComparator}.
	 */
	public static Comparator<String> stringAlphanumericalComparator() {
		return StringAlphanumericalComparator.INSTANCE;
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Comparators() {
	}

}
