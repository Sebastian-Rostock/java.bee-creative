package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Comparator}.
 *
 * @see Comparator
 * @author Sebastian Rostock 2011. */
public class Comparators {

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher Zahlen über ihren {@link Number#intValue()} vergleicht.
	 *
	 * @see Comparators#compare(int, int) */
	@SuppressWarnings ("javadoc")
	public static class IntComparator extends AbstractComparator<Number> {

		public static final Comparator2<Number> INSTANCE = new IntComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.intValue(), item2.intValue());
		}

	}

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher Zahlen über ihren {@link Number#longValue()} vergleicht. *
	 *
	 * @see Comparators#compare(long, long) */
	@SuppressWarnings ("javadoc")
	public static class LongComparator extends AbstractComparator<Number> {

		public static final Comparator2<Number> INSTANCE = new LongComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.longValue(), item2.longValue());
		}

	}

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher Zahlen über ihren {@link Number#floatValue()} vergleicht. *
	 *
	 * @see Comparators#compare(float, float) */
	@SuppressWarnings ("javadoc")
	public static class FloatComparator extends AbstractComparator<Number> {

		public static final Comparator2<Number> INSTANCE = new FloatComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.floatValue(), item2.floatValue());
		}

	}

	/** Diese Klasse implementiert einen {@link Comparator}, welcher Zahlen über ihren {@link Number#doubleValue()} vergleicht.
	 *
	 * @see Comparators#compare(double, double) */
	@SuppressWarnings ("javadoc")
	public static class DoubleComparator extends AbstractComparator<Number> {

		public static final Comparator2<Number> INSTANCE = new DoubleComparator();

		@Override
		public int compare(final Number item1, final Number item2) {
			return Comparators.compare(item1.doubleValue(), item2.doubleValue());
		}

	}

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher als Zeichenkette kodierte Dezimalzahlen vergleicht. */
	@SuppressWarnings ("javadoc")
	public static class NumericalComparator extends AbstractComparator<String> {

		public static final Comparator2<String> INSTANCE = new NumericalComparator();

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

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher Zeichenketten in vergleicht und die Groß-/Kleinschreibung ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class AlphabeticalComparator extends AbstractComparator<String> {

		public static final Comparator2<String> INSTANCE = new AlphabeticalComparator();

		@Override
		public int compare(final String item1, final String item2) {
			return item1.compareToIgnoreCase(item2);
		}

	}

	/** Diese Klasse implementiert einen {@link Comparator2}, welcher Zeichenkette aus kodierten Dezimalzahlen und normalem Text vergleicht und dabei
	 * Groß-/Kleinschreibung ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class AlphanumericalComparator extends AbstractComparator<String> {

		public static final Comparator2<String> INSTANCE = new AlphanumericalComparator();

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

	/** Diese Klasse implementiert den neutralen {@link Comparator2}, welcher als {@link Comparator#compare(Object, Object) Vergleichswert} stets {@code 0}
	 * liefert. */
	@SuppressWarnings ("javadoc")
	public static class NeutralComparator extends AbstractComparator<Object> {

		public static final Comparator2<?> INSTANCE = new NeutralComparator();

	}

	/** Diese Klasse implementiert den natürlichen {@link Comparator2}, welcher den {@link Comparator#compare(Object, Object) Vergleichswert} der gegebenen
	 * {@link Comparable} liefert. */
	@SuppressWarnings ("javadoc")
	public static class NaturalComparator extends AbstractComparator<Comparable<Object>> {

		public static final Comparator2<?> INSTANCE = new NaturalComparator();

		@Override
		public int compare(final Comparable<Object> item1, final Comparable<Object> item2) {
			return item1.compareTo(item2);
		}

	}

	/** Diese Klasse implementiert einen verketteten {@link Comparator2}, welcher den {@link Comparator#compare(Object, Object) Vergleichswert} eines ersten
	 * gegebenen {@link Comparator} liefert, sofern dieser ungleich {@code 0} ist, und sonst den eines zweiten gegebenen {@link Comparator} verwendet.
	 *
	 * @param <GItem> Typ der Eingabe. */
	@SuppressWarnings ("javadoc")
	public static class ConcatComparator<GItem> extends AbstractComparator<GItem> {

		public final Comparator<? super GItem> comp1;

		public final Comparator<? super GItem> comp2;

		public ConcatComparator(final Comparator<? super GItem> target1, final Comparator<? super GItem> target2) throws NullPointerException {
			this.comp1 = Objects.notNull(target1);
			this.comp2 = Objects.notNull(target2);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			final int result = this.comp1.compare(item1, item2);
			if (result != 0) return result;
			return this.comp2.compare(item1, item2);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comp1, this.comp2);
		}

	}

	/** Diese Klasse implementiert einen umkehrenden {@link Comparator2}, welcher den {@link Comparator#compare(Object, Object) Vergleichswert} eines gegebenen
	 * {@link Comparator} mit umgekehrten Vorzeichen liefert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ReverseComparator<GItem> extends AbstractComparator<GItem> {

		public final Comparator<? super GItem> that;

		public ReverseComparator(final Comparator<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			return this.that.compare(item2, item1);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert den {@link Comparator2} zu {@link Comparators#compare(Iterable, Iterable, Comparator)}.
	 *
	 * @param <GItem> Typ der in den {@link Iterable} enthaltenen sowie vom gegebenen {@link Comparator} zu vergleichenden Elemente. */
	@SuppressWarnings ("javadoc")
	public static class IterableComparator<GItem> extends AbstractComparator<Iterable<? extends GItem>> {

		public final Comparator<? super GItem> that;

		public IterableComparator(final Comparator<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
			return Comparators.compare(item1, item2, this.that);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Comparator2}, welcher den {@link Comparator#compare(Object, Object) Vergleichswert} über
	 * {@code this.that.compare(this.trans.get(item1), this.trans.get(item2))} ermittelt.
	 *
	 * @param <GItem> Typ der Eingabe des {@link Getter} sowie der Eingabe dieses {@link Comparator2}.
	 * @param <GItem2> Typ der Ausgabe des {@link Getter} sowie der Eingabe des gegebenen {@link Comparator}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedComparator<GItem, GItem2> extends AbstractComparator<GItem> {

		public final Comparator<? super GItem2> that;

		public final Getter<? super GItem, ? extends GItem2> trans;

		public TranslatedComparator(final Comparator<? super GItem2> that, final Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			return this.that.compare(this.trans.get(item1), this.trans.get(item2));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert den {@link Comparator2} zu {@link Comparators#compare(Object, Object, Comparator)}.
	 *
	 * @param <GItem> Typ der Eingabe. */
	@SuppressWarnings ("javadoc")
	public static class OptionalizedComparator<GItem> extends AbstractComparator<GItem> {

		public final Comparator<? super GItem> that;

		public OptionalizedComparator(final Comparator<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public int compare(final GItem item1, final GItem item2) {
			return Comparators.compare(item1, item2, this.that);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2) <= 0 ? item1 : item2}. */
	public static <GItem extends Comparable<? super GItem>> GItem min(final GItem item1, final GItem item2) {
		return Comparators.compare(item1, item2) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2, comparator) <= 0 ? item1 : item2}. */
	public static <GItem> GItem min(final GItem item1, final GItem item2, final Comparator<? super GItem> order) throws NullPointerException {
		return Comparators.compare(item1, item2, order) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung oder {@code null} zurück. */
	public static <GItem extends Comparable<? super GItem>> GItem min(final Iterable<? extends GItem> items) throws NullPointerException {
		if (items == null) return null;
		final Iterator<? extends GItem> iterator = items.iterator();
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.min(iterator.next(), result);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung oder {@code null} zurück. */
	public static <GItem> GItem min(final Iterable<? extends GItem> items, final Comparator<? super GItem> order) throws NullPointerException {
		if (items == null) return null;
		final Iterator<? extends GItem> iterator = items.iterator();
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.min(iterator.next(), result, order);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2) >= 0 ? item1 : item2}. */
	public static <GItem extends Comparable<? super GItem>> GItem max(final GItem item1, final GItem item2) {
		return Comparators.compare(item1, item2) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@code Comparators.compare(item1, item2, comparator) >= 0 ? item1 : item2}. */
	public static <GItem> GItem max(final GItem item1, final GItem item2, final Comparator<? super GItem> order) throws NullPointerException {
		return Comparators.compare(item1, item2, order) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. */
	public static <GItem extends Comparable<? super GItem>> GItem max(final Iterable<? extends GItem> items) throws NullPointerException {
		if (items == null) return null;
		final Iterator<? extends GItem> iterator = items.iterator();
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.max(iterator.next(), result);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung oder {@code null} zurück. */
	public static <GItem> GItem max(final Iterable<? extends GItem> items, final Comparator<? super GItem> order) throws NullPointerException {
		if (items == null) return null;
		final Iterator<? extends GItem> iterator = items.iterator();
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			result = Comparators.max(iterator.next(), result, order);
		}
		return result;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(final int item1, final int item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(final long item1, final long item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(final float item1, final float item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(final double item1, final double item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2))}. */
	public static <GItem extends Comparable<? super GItem>> int compare(final GItem item1, final GItem item2) {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : comparator.compare(item1, item2))}. */
	public static <GItem> int compare(final GItem item1, final GItem item2, final Comparator<? super GItem> order) throws NullPointerException {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : order.compare(item1, item2));
	}

	/** Diese Methode ist eine Abkürzung für {@link #compare(Iterable, Iterable, Comparator) Comparators.compare(item1, item2, Comparators.natural())}.
	 *
	 * @see #natural() */
	public static <GItem extends Comparable<? super GItem>> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2) {
		return Comparators.compare(item1, item2, Comparators.<GItem>natural());
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@code Iterable} ist. Die gegebenen {@code Iterable} werden für den Vergleich parallel iteriert. Wenn der erste {@code Iterator} kein nächstes
	 * Element besitzt, der zweite {@code Iterator} jedoch ein nächstes Element liefern kann, wird {@code -1} zurück gegeben. Wenn beide {@code Iterator}en je ein
	 * nächstes Element liefern können, werden diese mit dem gegebenen {@link Comparator} verglichen. Wenn der so berechnete Vergleichswert unglich {@code 0} ist,
	 * wird er zurück gegeben. Anderenfalls läuft die Iteration weiter. Wenn der erste {@code Iterator} ein nächstes Element besitzt, der zweite {@code Iterator}
	 * jedoch kein nächstes Element liefern kann, wird {@code 1} zurück gegeben. */
	public static <GItem> int compare(final Iterable<? extends GItem> item1, final Iterable<? extends GItem> item2, final Comparator<? super GItem> order)
		throws NullPointerException {
		Objects.notNull(order);
		final Iterator<? extends GItem> iter1 = Objects.notNull(item1, Iterables.<GItem>empty()).iterator();
		final Iterator<? extends GItem> iter2 = Objects.notNull(item2, Iterables.<GItem>empty()).iterator();
		while (true) {
			if (!iter1.hasNext()) return (iter2.hasNext() ? -1 : 0);
			if (!iter2.hasNext()) return 1;
			final int result = order.compare(iter1.next(), iter2.next());
			if (result != 0) return result;
		}
	}

	/** Diese Methode liefert den {@link NeutralComparator}. */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Comparator2<GEntry> neutral() {
		return (Comparator2<GEntry>)NeutralComparator.INSTANCE;
	}

	/** Diese Methode liefert den {@link NaturalComparator}. */
	@SuppressWarnings ("unchecked")
	public static <GItem extends Comparable<? super GItem>> Comparator2<GItem> natural() {
		return (Comparator2<GItem>)NaturalComparator.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Comparator} als {@link Comparator2}. Wenn er {@code null} ist, wird de r{@link NeutralComparator} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Comparator2<GItem> from(final Comparator<? super GItem> that) {
		if (that == null) return Comparators.neutral();
		if (that instanceof Comparator2<?>) return (Comparator2<GItem>)that;
		return Comparators.translate(that, Getters.<GItem>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatComparator new ConcatComparator<>(comp1, comp2)}. */
	public static <GItem> Comparator2<GItem> concat(final Comparator<? super GItem> comp1, final Comparator<? super GItem> comp2) throws NullPointerException {
		return new ConcatComparator<>(comp1, comp2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseComparator new ReverseComparator<>(that)}. */
	public static <GItem> Comparator2<GItem> reverse(final Comparator<? super GItem> that) throws NullPointerException {
		return new ReverseComparator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link IterableComparator new IterableComparator<>(that)}. */
	public static <GItem> Comparator2<Iterable<? extends GItem>> iterable(final Comparator<? super GItem> that) throws NullPointerException {
		return new IterableComparator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedComparator new TranslatedComparator<>(that, trans)}. */
	public static <GItem, GItem2> Comparator2<GItem> translate(final Comparator<? super GItem2> that, final Getter<? super GItem, ? extends GItem2> trans)
		throws NullPointerException {
		return new TranslatedComparator<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedComparator new OptionalizedComparator<>(that)}. */
	public static <GItem> Comparator2<GItem> optionalize(final Comparator<? super GItem> that) throws NullPointerException {
		return new OptionalizedComparator<>(that);
	}

}
