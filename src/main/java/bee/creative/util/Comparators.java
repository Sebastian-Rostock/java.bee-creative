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
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, der als Zeichenkette kodierte Dezimalzahlen vergleicht.
	 */
	public static final Comparator<String> STRING_NUMERICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String item1, final String item2) {
			final int s1 = item1.length(), s2 = item2.length();
			int a1 = 0, a2 = 0;
			{ // '0' überspringen
				while ((a1 < s1) && (item1.charAt(a1) == '0')) {
					a1++;
				}
				while ((a2 < s2) && (item2.charAt(a2) == '0')) {
					a2++;
				}
			}
			{ // War eine der Eingaben Leer oder "0"
				if (a1 == s1) return ((a2 == s2) ? 0 : -1);
				if (a2 == s2) return 1;
			}
			{ // Zahlen vergleichen
				final int comp = (s1 - a1) - (s2 - a2);
				if (comp != 0) return comp;
				return item1.substring(a1, s1).compareTo(item2.substring(a2, s2));
			}
		}

		@Override
		public String toString() {
			return "STRING_NUMERICAL_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, der Groß-/Kleinschreibung ignoriert.
	 */
	public static final Comparator<String> STRING_ALPHABETICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String item1, final String item2) {
			return item1.compareToIgnoreCase(item2);
		}

		@Override
		public String toString() {
			return "STRING_ALPHABETICAL_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link String}-{@link Comparator}, gemischte Zeichenkette aus kodierten Dezimalzahlen und normalem Text vergleicht und dabei
	 * Groß-/Kleinschreibung ignoriert.
	 */
	public static final Comparator<String> STRING_ALPHANUMERICAL_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(final String item1, final String item2) {
			final int s1 = item1.length(), s2 = item2.length();
			int a1 = 0, a2 = 0, e1, e2;
			while ((a1 < s1) && (a2 < s2)) {
				{ // Buchstaben überspringen = Halt, wenn Ziffer gefunden
					for (e1 = a1; e1 < s1; e1++) {
						final char c1 = item1.charAt(e1);
						if (('0' <= c1) && (c1 <= '9')) {
							break;
						}
					}
					for (e2 = a2; e2 < s2; e2++) {
						final char c2 = item2.charAt(e2);
						if (('0' <= c2) && (c2 <= '9')) {
							break;
						}
					}
				}
				{ // Buchstaben vergleichen
					if (a1 == e1) {
						if (a2 != e2) return -1;
					} else if (a2 != e2) {
						final int comp = item1.substring(a1, e1).compareToIgnoreCase(item2.substring(a2, e2));
						if (comp != 0) return comp;
					} else return 1;
				}
				{ // '0' überspringen = Halt, wenn nicht '0' gefunden
					for (a1 = e1; a1 < s1; a1++) {
						if (item1.charAt(a1) != '0') {
							break;
						}
					}
					for (a2 = e2; a2 < s2; a2++) {
						if (item2.charAt(a2) != '0') {
							break;
						}
					}
				}
				{ // Ziffern überspringen = Halt, wenn nicht Ziffer gefunden
					for (e1 = a1; e1 < s1; e1++) {
						final char c1 = item1.charAt(e1);
						if (('0' > c1) || (c1 > '9')) {
							break;
						}
					}
					for (e2 = a2; e2 < s2; e2++) {
						final char c2 = item2.charAt(e2);
						if (('0' > c2) || (c2 > '9')) {
							break;
						}
					}
				}
				{ // Ziffern vergleichen
					if (a1 == e1) {
						if (a2 != e2) return -1;
					} else if (a2 != e2) {
						int comp = (e1 - a1) - (e2 - a2);
						if (comp != 0) return comp;
						comp = item1.substring(a1, e1).compareTo(item2.substring(a2, e2));
						if (comp != 0) return comp;
					} else return 1;
				}
				a1 = e1;
				a2 = e2;
			}
			return (s1 - a1) - (s2 - a2);
		}

		@Override
		public String toString() {
			return "STRING_ALPHANUMERICAL_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#longValue()} vergleicht.
	 * 
	 * @see Comparators#compare(long, long)
	 */
	public static final Comparator<Number> NUMBER_LONG_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.longValue(), item2.longValue());
		}

		@Override
		public String toString() {
			return "NUMBER_LONG_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#floatValue()} vergleicht.
	 * 
	 * @see Comparators#compare(float, float)
	 */
	public static final Comparator<Number> NUMBER_FLOAT_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.floatValue(), item2.floatValue());
		}

		@Override
		public String toString() {
			return "NUMBER_FLOAT_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#intValue()} vergleicht.
	 * 
	 * @see Comparators#compare(int, int)
	 */
	public static final Comparator<Number> NUMBER_INTEGER_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.intValue(), item2.intValue());
		}

		@Override
		public String toString() {
			return "NUMBER_INTEGER_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#doubleValue()} vergleicht.
	 * 
	 * @see Comparators#compare(double, double)
	 */
	public static final Comparator<Number> NUMBER_DOUBLE_COMPARATOR = new Comparator<Number>() {

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.doubleValue(), item2.doubleValue());
		}

		@Override
		public String toString() {
			return "NUMBER_DOUBLE_COMPARATOR";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} für die natürliche Ordnung.
	 */
	public static final Comparator<?> NATURAL_COMPARATOR = new Comparator<Comparable<Object>>() {

		@Override
		public int compare(final Comparable<Object> item1, final Comparable<Object> item2) {
			return item1.compareTo(item2);
		}

		@Override
		public String toString() {
			return "NATURAL_COMPARATOR";
		}

	};

	{}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(item1 < item2 ? -1 : (item1 == item2 ? 0 : 1))</pre>
	 * 
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final int item1, final int item2) {
		return (item1 < item2 ? -1 : (item1 == item2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(item1 < item2 ? -1 : (item1 == item2 ? 0 : 1))</pre>
	 * 
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final long item1, final long item2) {
		return (item1 < item2 ? -1 : (item1 == item2 ? 0 : 1));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(item1 < item2 ? -1 : (item1 == item2 ? 0 : 1))</pre>
	 * 
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final float item1, final float item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>(item1 < item2 ? -1 : (item1 == item2 ? 0 : 1))</pre>
	 * 
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert.
	 */
	public static int compare(final double item1, final double item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist.
	 * 
	 * @see #compare(Object, Object, Comparator)
	 * @see #naturalComparator()
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt.
	 * @param item2 zweites Objekt.
	 * @return Vergleichswert.
	 */
	public static <GItem extends Comparable<? super GItem>> int compare(final GItem item1, final GItem item2) {
		return Comparators.compare(item1, item2, Comparators.<GItem>naturalComparator());
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Der berechnete Vergleichswert entspricht:
	 * 
	 * <pre>
	 * ((item1 == null) ? ((item2 == null) ? 0 : -1) : ((item2 == null) ? 1 : comparator.compare(item1, item2)))
	 * </pre>
	 * 
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt.
	 * @param item2 zweites Objekt.
	 * @param comparator {@link Comparator}.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> int compare(final GItem item1, final GItem item2, final Comparator<? super GItem> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		return ((item1 == null) ? ((item2 == null) ? 0 : -1) : ((item2 == null) ? 1 : comparator.compare(item1, item2)));
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@link Iterable} ist.
	 * 
	 * @see #compare(Iterable, Iterable, Comparator)
	 * @see #naturalComparator()
	 * @param <GItem> Typ der Elemente der {@link Iterable}.
	 * @param item1 erster {@link Iterable}.
	 * @param item2 zweiter {@link Iterable}.
	 * @return Vergleichswert.
	 */
	public static <GItem extends Comparable<? super GItem>> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
		return Comparators.compare(item1, item2, Comparators.<GItem>naturalComparator());
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@link Iterable} ist. Die gegebenen {@link Iterable} werden für den Vergleich parallel iteriert. Wenn der erste {@link Iterator} kein nächstes
	 * Element besitzt, der zweite {@link Iterator} jedoch ein nächstes Element liefern kann, wird {@code -1} zurück gegeben. Wenn beide {@link Iterator}en je ein
	 * nächstes Element liefern können, werden diese mit dem gegebenen {@link Comparator} verglichen. Wenn der so berechnete Vergleichswert unglich {@code 0} ist,
	 * wird er zurück gegeben. Anderenfalls läuft die Iteration weiter. Wenn der erste {@link Iterator} ein nächstes Element besitzt, der zweite {@link Iterator}
	 * jedoch kein nächstes Element liefern kann, wird {@code 1} zurück gegeben.
	 * 
	 * @param <GItem> Typ der Elemente der {@link Iterable}.
	 * @param item1 erster {@link Iterable}.
	 * @param item2 zweiter {@link Iterable}.
	 * @param comparator {@link Comparator} für die Elemente der {@link Iterable}.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2, final Comparator<? super GItem> comparator)
		throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		final Iterator<? extends GItem> iter1 = Iterators.iterator(item1), iter2 = Iterators.iterator(item2);
		while (true) {
			if (!iter1.hasNext()) return (iter2.hasNext() ? -1 : 0);
			if (!iter2.hasNext()) return 1;
			final int result = comparator.compare(iter1.next(), iter2.next());
			if (result != 0) return result;
		}
	}

	/**
	 * Diese Methode gibt einen neuen {@link Comparator} zurück, der {@code null}-Eingaben vergleicht sowie alle anderen Eingaben an einen gegebenen
	 * {@link Comparator} weiterleitet
	 * 
	 * @see #compare(Object, Object, Comparator)
	 * @param <GItem> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@code null}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> Comparator<GItem> nullComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		return new Comparator<GItem>() {

			@Override
			public int compare(final GItem item1, final GItem item2) {
				return Comparators.compare(item1, item2, comparator);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("nullComparator", comparator);
			}

		};
	}

	/**
	 * Diese Methode gibt den {@link Comparator} für die natürliche Ordnung zurück.
	 * 
	 * @see Comparable
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link #NATURAL_COMPARATOR}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry extends Comparable<? super GEntry>> Comparator<GEntry> naturalComparator() {
		return (Comparator<GEntry>)Comparators.NATURAL_COMPARATOR;
	}

	/**
	 * Diese Methode gibt einen neuen {@link Comparator} zurück, der den Vergleichswert des gegebenen {@link Comparator}s umkehrt.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@code reverse}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> Comparator<GItem> reverseComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		return new Comparator<GItem>() {

			@Override
			public int compare(final GItem item1, final GItem item2) {
				return comparator.compare(item2, item1);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("reverseComparator", comparator);
			}

		};
	}

	/**
	 * Diese Methode gibt einen neuen {@link Comparator} zurück, der zwei {@link Iterable} mit Hilfe des gegebenen {@link Comparator}s analog zu Zeichenketten
	 * vergleicht.
	 * 
	 * @see #compare(Iterable, Iterable, Comparator)
	 * @param <GItem> Typ der in den {@link Iterable} enthaltenen sowie vom gegebenen {@link Comparator} zu vergleichenden Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@link Iterable}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> Comparator<Iterable<? extends GItem>> iterableComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		return new Comparator<Iterable<? extends GItem>>() {

			@Override
			public int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
				return Comparators.compare(item1, item2, comparator);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("iterableComparator", comparator);
			}

		};
	}

	/**
	 * Diese Methode gibt einen verketteten {@link Comparator} zurück, der seine Eingaben zuerst über den ersten {@link Comparator} vergleich und den zweiten
	 * {@link Comparator} nur dann verwenet, wenn der erste {@link Comparator} mit dem Vergleichswert {@code 0} die Gleichheit der Eingaben anzeigt.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param comparator1 erster {@link Comparator}.
	 * @param comparator2 zweiter {@link Comparator}.
	 * @return {@code chained}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator1} bzw. {@code comparator2} {@code null} ist.
	 */
	public static <GItem> Comparator<GItem> chainedComparator(final Comparator<? super GItem> comparator1, final Comparator<? super GItem> comparator2)
		throws NullPointerException {
		if (comparator1 == null) throw new NullPointerException("comparator1 = null");
		if (comparator2 == null) throw new NullPointerException("comparator2 = null");
		return new Comparator<GItem>() {

			@Override
			public int compare(final GItem item1, final GItem item2) {
				final int result = comparator1.compare(item1, item2);
				if (result != 0) return result;
				return comparator2.compare(item1, item2);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("chainedComparator", comparator1, comparator2);
			}

		};
	}

	/**
	 * Diese Methode gibt einen konvertierenden {@link Comparator} zurück, der seine Eingaben mit dem gegebenen {@link Converter} umwandelt und an den gegebenen
	 * {@link Comparator} delegiert. Der Vergleichswert zweier Elemente {@code item1} und {@code item2} ergibt sich aus
	 * {@code comparator.compare(converter.convert(item1), converter.convert(item2))}.
	 * 
	 * @see Converter
	 * @param <GItem> Typ der Eingabe des {@link Converter} sowie der Elemente des erzeugten {@link Comparator}.
	 * @param <GItem2> Typ der Ausgabe des {@link Converter} sowie der Elemente des gegebenen {@link Comparator}.
	 * @param converter {@link Converter}.
	 * @param comparator {@link Comparator}.
	 * @return {@code converted}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code comparator} {@code null} ist.
	 */
	public static <GItem, GItem2> Comparator<GItem> convertedComparator(final Converter<? super GItem, ? extends GItem2> converter,
		final Comparator<? super GItem2> comparator) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		if (comparator == null) throw new NullPointerException("comparator = null");
		return new Comparator<GItem>() {

			@Override
			public int compare(final GItem item1, final GItem item2) {
				return comparator.compare(converter.convert(item1), converter.convert(item2));
			}

			@Override
			public String toString() {
				return Objects.toStringCall("convertedComparator", converter, comparator);
			}

		};
	}

}
