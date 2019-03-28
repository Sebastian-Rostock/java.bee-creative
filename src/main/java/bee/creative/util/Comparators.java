package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;
import bee.creative.bind.Getter;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert grundlegende {@link Comparator}.
 *
 * @see Comparator
 * @author Sebastian Rostock 2011. */
public class Comparators {

	/** Diese Klasse implementiert einen abstrakten {@link Comparator} als {@link BaseObject}. */
	public static abstract class BaseComparator<GItem> extends BaseObject implements Comparator<GItem> {
	}

	/** Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#intValue()} vergleicht.
	 *
	 * @see Comparators#compare(int, int) */
	@SuppressWarnings ("javadoc")
	public static class IntComparator extends BaseComparator<Number> {

		public static final IntComparator INSTANCE = new IntComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.intValue(), item2.intValue());
		}

	}

	/** Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#longValue()} vergleicht. *
	 *
	 * @see Comparators#compare(long, long) */
	@SuppressWarnings ("javadoc")
	public static class LongComparator extends BaseComparator<Number> {

		public static final LongComparator INSTANCE = new LongComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.longValue(), item2.longValue());
		}

	}

	/** Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#floatValue()} vergleicht. *
	 *
	 * @see Comparators#compare(float, float) */
	@SuppressWarnings ("javadoc")
	public static class FloatComparator extends BaseComparator<Number> {

		public static final FloatComparator INSTANCE = new FloatComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.floatValue(), item2.floatValue());
		}

	}

	/** Diese Klasse implementiert den {@link Number}-{@link Comparator}, der Zahlen über ihren {@link Number#doubleValue()} vergleicht.
	 *
	 * @see Comparators#compare(double, double) */
	@SuppressWarnings ("javadoc")
	public static class DoubleComparator extends BaseComparator<Number> {

		public static final DoubleComparator INSTANCE = new DoubleComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.doubleValue(), item2.doubleValue());
		}

	}

	/** Diese Klasse implementiert den {@link String}-{@link Comparator}, der als Zeichenkette kodierte Dezimalzahlen vergleicht. */
	@SuppressWarnings ("javadoc")
	public static class NumericalComparator extends BaseComparator<String> {

		public static final Comparator<String> INSTANCE = new NumericalComparator();

		@Override
		public int compare(final String item1, final String item2) {
			final int s1 = item1.length(), s2 = item2.length();
			int a1 = 0, a2 = 0;
			boolean n1 = false, n2 = false;
			{ /** Vorzeichen ermitteln und überspringen */
				if (a1 < s1) {
					final int c1 = item1.charAt(a1);
					if (c1 == '-') {
						n1 = true;
						a1++;
					} else if (c1 == '+') {
						a1++;
					}
				}
				if (a2 < s2) {
					final int c2 = item2.charAt(a2);
					if (c2 == '-') {
						n2 = true;
						a2++;
					} else if (c2 == '+') {
						a2++;
					}
				}
			}
			{ /** '0' überspringen */
				while ((a1 < s1) && (item1.charAt(a1) == '0')) {
					a1++;
				}
				while ((a2 < s2) && (item2.charAt(a2) == '0')) {
					a2++;
				}
			}
			{ /** War eine der Eingaben Leer oder "0" */
				if (a1 == s1) {
					if (a2 == s2) return 0;
					return n2 ? +1 : -1;
				}
				if (a2 == s2) return n1 ? -1 : +1;
			}
			{ /** Zahlen vergleichen */
				if (n1 != n2) return n1 ? -1 : +1;
				final int comp = (s1 - a1) - (s2 - a2);
				if (comp != 0) return comp;
				return item1.substring(a1, s1).compareTo(item2.substring(a2, s2));
			}
		}

	}

	/** Diese Klasse implementiert den {@link String}-{@link Comparator}, der Groß-/Kleinschreibung ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class AlphabeticalComparator extends BaseComparator<String> {

		public static final AlphabeticalComparator INSTANCE = new AlphabeticalComparator();

		@Override
		public int compare(final String item1, final String item2) {
			return item1.compareToIgnoreCase(item2);
		}

	}

	/** Diese Klasse implementiert den {@link String}-{@link Comparator}, gemischte Zeichenkette aus kodierten Dezimalzahlen und normalem Text vergleicht und
	 * dabei Groß-/Kleinschreibung ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class AlphanumericalComparator extends BaseComparator<String> {

		public static final AlphanumericalComparator INSTANCE = new AlphanumericalComparator();

		@Override
		public int compare(final String item1, final String item2) {
			final int s1 = item1.length(), s2 = item2.length();
			int a1 = 0, a2 = 0, e1, e2;
			while ((a1 < s1) && (a2 < s2)) {
				{ /** Buchstaben überspringen => Halt, wenn Ziffer gefunden */
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
				{ /** Buchstaben vergleichen */
					if (a1 == e1) {
						if (a2 != e2) return -1;
					} else if (a2 != e2) {
						final int result = item1.substring(a1, e1).compareToIgnoreCase(item2.substring(a2, e2));
						if (result != 0) return result;
					} else return 1;
				}
				{ /** '0' überspringen => Halt, wenn nicht '0' gefunden */
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
				{ /** Ziffern überspringen => Halt, wenn nicht Ziffer gefunden */
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
				{ /** Ziffern vergleichen */
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

	}

	/** Diese Klasse implementiert {@link Comparators#naturalComparator()} */
	@SuppressWarnings ("javadoc")
	public static class NaturalComparator extends BaseComparator<Comparable<Object>> {

		public static final Comparator<?> INSTANCE = new NaturalComparator();

		@Override
		public int compare(final Comparable<Object> item1, final Comparable<Object> item2) {
			return item1.compareTo(item2);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#reverseComparator(Comparator)} */
	@SuppressWarnings ("javadoc")
	public static class ReverseComparator<GItem> implements Comparator<GItem> {

		public final Comparator<? super GItem> comparator;

		public ReverseComparator(final Comparator<? super GItem> comparator) {
			this.comparator = Objects.notNull(comparator);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			return this.comparator.compare(item2, item1);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparator);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#defaultComparator(Comparator)} */
	@SuppressWarnings ("javadoc")
	public static class DefaultComparator<GItem> implements Comparator<GItem> {

		public final Comparator<? super GItem> comparator;

		public DefaultComparator(final Comparator<? super GItem> comparator) {
			this.comparator = Objects.notNull(comparator);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			return Comparators.compare(item1, item2, this.comparator);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparator);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#iterableComparator(Comparator)} */
	@SuppressWarnings ("javadoc")
	public static class IterableComparator<GItem> implements Comparator<Iterable<? extends GItem>> {

		public final Comparator<? super GItem> comparator;

		public IterableComparator(final Comparator<? super GItem> comparator) {
			this.comparator = Objects.notNull(comparator);
		}

		@Override
		public int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
			return Comparators.compare(item1, item2, this.comparator);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparator);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#chainedComparator(Comparator, Comparator)} */
	@SuppressWarnings ("javadoc")
	public static class ChainedComparator<GItem> implements Comparator<GItem> {

		public final Comparator<? super GItem> comparator1;

		public final Comparator<? super GItem> comparator2;

		public ChainedComparator(final Comparator<? super GItem> comparator1, final Comparator<? super GItem> comparator2) {
			this.comparator1 = Objects.notNull(comparator1);
			this.comparator2 = Objects.notNull(comparator2);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			final int result = this.comparator1.compare(item1, item2);
			if (result != 0) return result;
			return this.comparator2.compare(item1, item2);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparator1, this.comparator2);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#translatedComparator(Getter, Comparator)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedComparator<GSource, GTarget> implements Comparator<GTarget> {

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public final Comparator<? super GSource> comparator;

		public TranslatedComparator(final Getter<? super GTarget, ? extends GSource> toSource, final Comparator<? super GSource> comparator) {
			this.toSource = Objects.notNull(toSource);
			this.comparator = Objects.notNull(comparator);
		}

		@Override
		public int compare(final GTarget item1, final GTarget item2) {
			return this.comparator.compare(this.toSource.get(item1), this.toSource.get(item2));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.comparator);
		}

	}

	/** Diese Klasse implementiert {@link Comparators#toComparable(Object, Comparator)} */
	@SuppressWarnings ("javadoc")
	static class ComparatorComparable<GItem> implements Comparable<GItem> {

		public final GItem item;

		public final Comparator<? super GItem> comparator;

		public ComparatorComparable(final GItem input, final Comparator<? super GItem> comparator) {
			this.item = input;
			this.comparator = Objects.notNull(comparator);
		}

		@Override
		public int compareTo(final GItem item) {
			return this.comparator.compare(this.item, item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.comparator);
		}

	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2) <= 0 ? item1 : item2}.
	 *
	 * @see #compare(Comparable, Comparable)
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @return Objekt mit der kleinsten ordnung oder {@code null}. */
	public static <GItem extends Comparable<? super GItem>> GItem min(final GItem item1, final GItem item2) {
		return Comparators.compare(item1, item2) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2, comparator) <= 0 ? item1 : item2}.
	 *
	 * @see #compare(Object, Object, Comparator)
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @param comparator {@link Comparator}.
	 * @return Objekt mit der kleinsten ordnung oder {@code null}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> GItem min(final GItem item1, final GItem item2, final Comparator<? super GItem> comparator) throws NullPointerException {
		return Comparators.compare(item1, item2, comparator) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück.
	 *
	 * @see #min(Comparable, Comparable)
	 * @see Iterators#iterator(Iterator)
	 * @param <GItem> Typ der Objekte.
	 * @param items Objekte oder {@code null}.
	 * @return Objekt mit der kleinsten Ordnung oder {@code null}. */
	public static <GItem extends Comparable<? super GItem>> GItem min(final Iterable<? extends GItem> items) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.min(iterator.next(), result);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück.
	 *
	 * @see #min(Object, Object, Comparator)
	 * @see Iterators#iterator(Iterator)
	 * @param <GItem> Typ der Objekte.
	 * @param items Objekte oder {@code null}.
	 * @param comparator {@link Comparator}.
	 * @return Objekt mit der kleinsten Ordnung oder {@code null}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> GItem min(final Iterable<? extends GItem> items, final Comparator<? super GItem> comparator) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.min(iterator.next(), result, comparator);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2) >= 0 ? item1 : item2}.
	 *
	 * @see #compare(Comparable, Comparable)
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @return Objekt mit der kleinsten ordnung oder {@code null}. */
	public static <GItem extends Comparable<? super GItem>> GItem max(final GItem item1, final GItem item2) {
		return Comparators.compare(item1, item2) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2, comparator) >= 0 ? item1 : item2}.
	 *
	 * @see #compare(Object, Object, Comparator)
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @param comparator {@link Comparator}.
	 * @return Objekt mit der kleinsten ordnung oder {@code null}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> GItem max(final GItem item1, final GItem item2, final Comparator<? super GItem> comparator) throws NullPointerException {
		return Comparators.compare(item1, item2, comparator) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück.
	 *
	 * @see #max(Comparable, Comparable)
	 * @see Iterators#iterator(Iterator)
	 * @param <GItem> Typ der Objekte.
	 * @param items Objekte oder {@code null}.
	 * @return Objekt mit der größten Ordnung oder {@code null}. */
	public static <GItem extends Comparable<? super GItem>> GItem max(final Iterable<? extends GItem> items) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.max(iterator.next(), result);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück.
	 *
	 * @see #max(Object, Object, Comparator)
	 * @see Iterators#iterator(Iterator)
	 * @param <GItem> Typ der Objekte.
	 * @param items Objekte oder {@code null}.
	 * @param comparator {@link Comparator}.
	 * @return Objekt mit der größten Ordnung oder {@code null}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> GItem max(final Iterable<? extends GItem> items, final Comparator<? super GItem> comparator) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.max(iterator.next(), result, comparator);
		}
		return result;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre>
	 *
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert. */
	public static int compare(final int item1, final int item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre>
	 *
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert. */
	public static int compare(final long item1, final long item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre>
	 *
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert. */
	public static int compare(final float item1, final float item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre>
	 *
	 * @param item1 erster Wert.
	 * @param item2 zweiter Wert.
	 * @return Vergleichswert. */
	public static int compare(final double item1, final double item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2))}.
	 *
	 * @see #compare(Object, Object, Comparator)
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @return Vergleichswert. */
	public static <GItem extends Comparable<? super GItem>> int compare(final GItem item1, final GItem item2) {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : comparator.compare(item1, item2))}.
	 *
	 * @param <GItem> Typ der Objekte.
	 * @param item1 erstes Objekt oder {@code null}.
	 * @param item2 zweites Objekt oder {@code null}.
	 * @param comparator {@link Comparator}.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> int compare(final GItem item1, final GItem item2, final Comparator<? super GItem> comparator) throws NullPointerException {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : Objects.notNull(comparator).compare(item1, item2));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@link Iterable} ist.
	 *
	 * @see #compare(Iterable, Iterable, Comparator)
	 * @see #naturalComparator()
	 * @param <GItem> Typ der Elemente der {@link Iterable}.
	 * @param item1 erster {@link Iterable}.
	 * @param item2 zweiter {@link Iterable}.
	 * @return Vergleichswert. */
	public static <GItem extends Comparable<? super GItem>> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
		return Comparators.compare(item1, item2, Comparators.<GItem>naturalComparator());
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
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
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2, final Comparator<? super GItem> comparator)
		throws NullPointerException {
		Objects.notNull(comparator);
		final Iterator<? extends GItem> iter1 = Iterators.iterator(item1), iter2 = Iterators.iterator(item2);
		while (true) {
			if (!iter1.hasNext()) return (iter2.hasNext() ? -1 : 0);
			if (!iter2.hasNext()) return 1;
			final int result = comparator.compare(iter1.next(), iter2.next());
			if (result != 0) return result;
		}
	}

	/** Diese Methode gibt den {@link Comparator} für die {@link Comparable natürliche Ordnung} zurück.
	 *
	 * @see Comparable
	 * @param <GEntry> Typ der Elemente.
	 * @return {@code natuaral}-{@link Comparable}. */
	@SuppressWarnings ("unchecked")
	public static <GEntry extends Comparable<? super GEntry>> Comparator<GEntry> naturalComparator() {
		return (Comparator<GEntry>)NaturalComparator.INSTANCE;
	}

	/** Diese Methode gibt einen neuen {@link Comparator} zurück, der {@code null}-Eingaben {@link #compare(Object, Object, Comparator) selbst vergleicht} und
	 * alle anderen Eingaben an den gegebenen {@link Comparator} weiterleitet.
	 *
	 * @see #compare(Object, Object, Comparator)
	 * @param <GItem> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@code default}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> Comparator<GItem> defaultComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		return new DefaultComparator<>(comparator);
	}

	/** Diese Methode gibt einen neuen {@link Comparator} zurück, der den Vergleichswert des gegebenen {@link Comparator} umkehrt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@code reverse}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> Comparator<GItem> reverseComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		return new ReverseComparator<>(comparator);
	}

	/** Diese Methode gibt einen neuen {@link Comparator} zurück, der zwei {@link Iterable} mit Hilfe des gegebenen {@link Comparator} analog zu Zeichenketten
	 * (d.h. lexikographisch) vergleicht.
	 *
	 * @see #compare(Iterable, Iterable, Comparator)
	 * @param <GItem> Typ der in den {@link Iterable} enthaltenen sowie vom gegebenen {@link Comparator} zu vergleichenden Elemente.
	 * @param comparator {@link Comparator}.
	 * @return {@link Iterable}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> Comparator<Iterable<? extends GItem>> iterableComparator(final Comparator<? super GItem> comparator) throws NullPointerException {
		return new IterableComparator<>(comparator);
	}

	/** Diese Methode gibt einen verketteten {@link Comparator} zurück, der seine Eingaben zuerst über den ersten {@link Comparator} vergleich und den zweiten
	 * {@link Comparator} nur dann verwenet, wenn der erste {@link Comparator} mit dem Vergleichswert {@code 0} die Gleichheit der Eingaben anzeigt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparator1 erster {@link Comparator}.
	 * @param comparator2 zweiter {@link Comparator}.
	 * @return {@code chained}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code comparator1} bzw. {@code comparator2} {@code null} ist. */
	public static <GItem> Comparator<GItem> chainedComparator(final Comparator<? super GItem> comparator1, final Comparator<? super GItem> comparator2)
		throws NullPointerException {
		return new ChainedComparator<>(comparator1, comparator2);
	}

	/** Diese Methode gibt einen navigierten {@link Comparator} zurück, der von seinen Eingaben mit dem gegebenen {@link Getter} zu den Eingaben des gegebenen
	 * {@link Comparator} navigiert. Der Vergleichswert zweier Elemente {@code item1} und {@code item2} ergibt sich aus
	 * {@code comparator.compare(navigator.get(item1), navigator.get(item2))}.
	 *
	 * @see Getter
	 * @param <GTarget> Typ der Eingabe des {@link Getter} sowie der Elemente des erzeugten {@link Comparator}.
	 * @param <GSource> Typ der Ausgabe des {@link Getter} sowie der Elemente des gegebenen {@link Comparator}.
	 * @param toSource {@link Getter} zur Navigation.
	 * @param comparator {@link Comparator}.
	 * @return {@code translated}-{@link Comparator}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code comparator} {@code null} ist. */
	public static <GTarget, GSource> Comparator<GTarget> translatedComparator(final Getter<? super GTarget, ? extends GSource> toSource,
		final Comparator<? super GSource> comparator) throws NullPointerException {
		return new TranslatedComparator<>(toSource, comparator);
	}

	/** Diese Methode gibt einen {@link Comparable} zurück, der den gegebenen {@link Comparator} sowie die gegebene Eingabe zur Berechnung des Navigationswert
	 * verwendet. Die gegebene Eingabe wird als erstes Argument des {@link Comparator} verwendet. Der Navigationswert für ein Element {@code item} ist
	 * {@code comparator.compare(input, item)}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param input erstes Argument des {@link Comparator}.
	 * @param comparator {@link Comparator}.
	 * @return {@code entry}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> Comparable<GItem> toComparable(final GItem input, final Comparator<? super GItem> comparator) throws NullPointerException {
		return new ComparatorComparable<>(input, comparator);
	}

}
