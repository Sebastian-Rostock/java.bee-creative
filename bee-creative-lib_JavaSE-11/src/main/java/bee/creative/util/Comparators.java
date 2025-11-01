package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterables.emptyIterable;
import java.util.Comparator;

/** Diese Klasse implementiert grundlegende {@link Comparator}.
 *
 * @see Comparator
 * @author Sebastian Rostock 2011. */
public class Comparators {

	/** Diese Methode liefert den gegebenen {@link Comparator3}. */
	public static <T> Comparator3<T> comparator(Comparator3<T> that) {
		return notNull(that);
	}

	/** Diese Methode liefert den gegebenen {@link Comparator} als {@link Comparator3}. */
	@SuppressWarnings ("unchecked")
	public static <T> Comparator3<T> comparatorFrom(Comparator<? super T> that) {
		notNull(that);
		if (that instanceof Comparator3<?>) return (Comparator3<T>)that;
		return (item1, item2) -> that.compare(item1, item2);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translatedComparator(Comparator, Getter) translatedComparator(naturalComparator(), that)}. */
	public static <T, T2 extends Comparable<T2>> Comparator3<T> comparatorFrom(Getter<? super T, ? extends T2> that) {
		return translatedComparator(naturalComparator(), that);
	}

	/** Diese Methode liefet den {@link Comparator3}, der Zahlen über ihren {@link Number#intValue()} vergleicht. */
	public static Comparator3<Number> intComparator() {
		return intComparator;
	}

	/** Diese Methode liefet den {@link Comparator3}, der Zahlen über ihren {@link Number#longValue()} vergleicht. */
	public static Comparator3<Number> longComparator() {
		return longComparator;
	}

	/** Diese Methode liefet den {@link Comparator3}, der Zahlen über ihren {@link Number#floatValue()} vergleicht. */
	public static Comparator3<Number> floatComparator() {
		return floatComparator;
	}

	/** Diese Methode liefet den {@link Comparator3}, der Zahlen über ihren {@link Number#doubleValue()} vergleicht. */
	public static Comparator3<Number> doubleComparator() {
		return doubleComparator;
	}

	/** Diese Methode liefert den neutralen {@link Comparator3}, der als {@link Comparator#compare(Object, Object) Vergleichswert} stets {@code 0} liefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Comparator3<T> neutralComparator() {
		return (Comparator3<T>)neutralComparator;
	}

	/** Diese Methode liefert den natürlichen {@link Comparator3}, der den {@link Comparator#compare(Object, Object) Vergleichswert} der gegebenen
	 * {@link Comparable} liefert. */
	@SuppressWarnings ("unchecked")
	public static <T extends Comparable<? super T>> Comparator3<T> naturalComparator() {
		return (Comparator3<T>)naturalComparator;
	}

	/** Diese Methode liefet einen {@link Comparator3}, der als Zeichenkette kodierte Dezimalzahlen vergleicht. */
	public static Comparator3<String> numericalComparator() {
		return numericalComparator;
	}

	/** Diese Methode liefert einen {@link Comparator3}, der Zeichenketten vergleicht und die Groß-/Kleinschreibung ignoriert. */
	public static Comparator3<String> alphabeticalComparator() {
		return alphabeticalComparator;
	}

	/** Diese Methode liefert einen {@link Comparator3}, der Zeichenkette aus kodierten Dezimalzahlen und normalem Text vergleicht und dabei Groß-/Kleinschreibung
	 * ignoriert. */
	public static Comparator3<String> alphanumericalComparator() {
		return alphanumericalComparator;
	}

	/** Diese Methode liefert einen verketteten {@link Comparator3}, der den {@link Comparator#compare(Object, Object) Vergleichswert} des ersten gegebenen
	 * {@link Comparator} liefert, sofern dieser ungleich {@code 0} ist, und sonst den des zweiten gegebenen {@link Comparator} verwendet. */
	public static <T> Comparator3<T> concatComparator(Comparator<? super T> that1, Comparator<? super T> that2) throws NullPointerException {
		notNull(that1);
		notNull(that2);
		return (item1, item2) -> {
			var result = that1.compare(item1, item2);
			if (result != 0) return result;
			return that2.compare(item1, item2);
		};
	}

	/** Diese Methode liefert den {@link Comparator3}, zu {@link Comparators#compareAll(Iterable, Iterable, Comparator)}. */
	public static <T> Comparator3<Iterable<? extends T>> iterableComparator(Comparator<? super T> that) throws NullPointerException {
		notNull(that);
		return (item1, item2) -> compareAll(item1, item2, that);
	}

	/** Diese Methode liefert einen umkehrenden {@link Comparator3}, der den {@link Comparator#compare(Object, Object) Vergleichswert} des gegebenen
	 * {@link Comparator} mit umgekehrten Vorzeichen liefert. */
	public static <T> Comparator3<T> reversedComparator(Comparator<? super T> that) throws NullPointerException {
		notNull(that);
		return (item1, item2) -> that.compare(item2, item1);
	}

	/** Diese Methode liefert einen übersetzten {@link Comparator3}, der den {@link Comparator#compare(Object, Object) Vergleichswert} über
	 * {@code that.compare(trans.get(item1), trans.get(item2))} ermittelt. */
	public static <T, T2> Comparator3<T> translatedComparator(Comparator<? super T2> that, Getter<? super T, ? extends T2> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return (item1, item2) -> that.compare(trans.get(item1), trans.get(item2));
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalizedComparator(Comparator, boolean) optionalizedComparator(that, true)}. */
	public static <T> Comparator3<T> optionalizedComparator(Comparator<? super T> that) throws NullPointerException {
		return optionalizedComparator(that, true);
	}

	/** Diese Methode liefert einen {@link Comparator3}, der den {@link Comparator#compare(Object, Object) Vergleichswert} des gegebenen {@link Comparator}
	 * liefert, sofern keines der zu vergleichenden Objekte {@code null} ist. Andernfalls wird {@code null} abhängig von {@code first} als kleinster
	 * ({@code true}) bzw. größter ({@code false}) Wert erkannt. */
	public static <T> Comparator3<T> optionalizedComparator(Comparator<? super T> that, boolean first) throws NullPointerException {
		notNull(that);
		return first ? (item1, item2) -> compare(item1, item2, that) : (item1, item2) -> -compare(item2, item1, that);
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@link Comparators#compare(Comparable, Comparable) compare(item1, item2) <= 0 ? item1 : item2}. */
	public static <T extends Comparable<? super T>> T min(T item1, T item2) {
		return compare(item1, item2) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der kleinsten Ordnung zurück. Dieses ergibt sich aus
	 * {@link Comparators#compare(Object, Object, Comparator) compare(item1, item2, order) <= 0 ? item1 : item2}. */
	public static <T> T min(T item1, T item2, Comparator<? super T> order) throws NullPointerException {
		return compare(item1, item2, order) <= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das erste Objekt mit der kleinsten Ordnung oder {@code null} zurück. */
	public static <T extends Comparable<? super T>> T min(Iterable<? extends T> items) throws NullPointerException {
		if (items == null) return null;
		var iter = items.iterator();
		if (!iter.hasNext()) return null;
		var result = iter.next();
		while (iter.hasNext()) {
			result = min(result, iter.next());
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das erste Objekt mit der kleinsten Ordnung oder {@code null} zurück. */
	public static <T> T min(Iterable<? extends T> items, Comparator<? super T> order) throws NullPointerException {
		if (items == null) return null;
		var iter = items.iterator();
		if (!iter.hasNext()) return null;
		var result = iter.next();
		while (iter.hasNext()) {
			result = min(result, iter.next(), order);
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@link Comparators#compare(Comparable, Comparable) compare(item1, item2) >= 0 ? item1 : item2}. */
	public static <T extends Comparable<? super T>> T max(T item1, T item2) {
		return compare(item1, item2) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das Objekt mit der größten Ordnung zurück. Dieses ergibt sich aus
	 * {@link Comparators#compare(Object, Object, Comparator) compare(item1, item2, order) >= 0 ? item1 : item2}. */
	public static <T> T max(T item1, T item2, Comparator<? super T> order) throws NullPointerException {
		return compare(item1, item2, order) >= 0 ? item1 : item2;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das erste Objekt mit der größten Ordnung zurück. */
	public static <T extends Comparable<? super T>> T max(Iterable<? extends T> items) throws NullPointerException {
		if (items == null) return null;
		var iter = items.iterator();
		if (!iter.hasNext()) return null;
		var result = iter.next();
		while (iter.hasNext()) {
			result = max(result, iter.next());
		}
		return result;
	}

	/** Diese Methode gibt aus den gegebenen Objekten das erste Objekt mit der größten Ordnung oder {@code null} zurück. */
	public static <T> T max(Iterable<? extends T> items, Comparator<? super T> order) throws NullPointerException {
		if (items == null) return null;
		var iter = items.iterator();
		if (!iter.hasNext()) return null;
		var result = iter.next();
		while (iter.hasNext()) {
			result = max(result, iter.next(), order);
		}
		return result;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(int item1, int item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(long item1, long item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert ist. Der
	 * berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(float item1, float item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Wert kleienr als, gleich bzw. größer als der zweite Wert
	 * ist. Der berechnete Vergleichswert entspricht: <pre>(item1 < item2 ? -1 : (item1 > item2 ? 1 : 0))</pre> */
	public static int compare(double item1, double item2) {
		return (item1 < item2 ? -1 : (item1 > item2 ? 1 : 0));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2))}. */
	public static <T extends Comparable<? super T>> int compare(T item1, T item2) {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : item1.compareTo(item2));
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste Objekt kleienr als, gleich bzw. größer als das zweite
	 * Objekt ist. Wenn nur eines der Objekte {@code null} ist, wird dieses als das kleiner angesehen. Der berechnete Vergleichswert entspricht:
	 * {@code item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : order.compare(item1, item2))}. */
	public static <T> int compare(T item1, T item2, Comparator<? super T> order) throws NullPointerException {
		return item1 == null ? (item2 == null ? 0 : -1) : (item2 == null ? 1 : order.compare(item1, item2));
	}

	/** Diese Methode ist eine Abkürzung für {@link #compareAll(Iterable, Iterable, Comparator) compare(item1, item2, naturalComparator())}. */
	public static <T extends Comparable<? super T>> int compareAll(Iterable<? extends T> item1, Iterable<? extends T> item2) {
		return compareAll(item1, item2, naturalComparator());
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn das erste {@link Iterable} kleienr als, gleich bzw. größer als das
	 * zweite {@code Iterable} ist. Die gegebenen {@code Iterable} werden für den Vergleich parallel iteriert. Wenn der erste {@code Iterator} kein nächstes
	 * Element besitzt, der zweite {@code Iterator} jedoch ein nächstes Element liefern kann, wird {@code -1} zurück gegeben. Wenn beide {@code Iterator}en je ein
	 * nächstes Element liefern können, werden diese mit dem gegebenen {@link Comparator} verglichen. Wenn der so berechnete Vergleichswert unglich {@code 0} ist,
	 * wird er zurück gegeben. Anderenfalls läuft die Iteration weiter. Wenn der erste {@code Iterator} ein nächstes Element besitzt, der zweite {@code Iterator}
	 * jedoch kein nächstes Element liefern kann, wird {@code 1} zurück gegeben. */
	public static <T> int compareAll(Iterable<? extends T> item1, Iterable<? extends T> item2, Comparator<? super T> order) throws NullPointerException {
		notNull(order);
		var iter1 = (item1 = notNull(item1, emptyIterable())).iterator();
		var iter2 = (item2 = notNull(item2, emptyIterable())).iterator();
		while (true) {
			if (!iter1.hasNext()) return iter2.hasNext() ? -1 : 0;
			if (!iter2.hasNext()) return 1;
			var result = compare(iter1.next(), iter2.next(), order);
			if (result != 0) return result;
		}
	}

	private static final Comparator3<?> neutralComparator = (item1, item2) -> 0;

	private static final Comparator3<Comparable<Object>> naturalComparator = (item1, item2) -> item1.compareTo(item2);

	private static final Comparator3<Number> intComparator = (item1, item2) -> compare(item1.intValue(), item2.intValue());

	private static final Comparator3<Number> longComparator = (item1, item2) -> compare(item1.longValue(), item2.longValue());

	private static final Comparator3<Number> floatComparator = (item1, item2) -> compare(item1.floatValue(), item2.floatValue());

	private static final Comparator3<Number> doubleComparator = (item1, item2) -> compare(item1.doubleValue(), item2.doubleValue());

	private static final Comparator3<String> numericalComparator = (item1, item2) -> {
		var s1 = item1.length();
		var s2 = item2.length();
		var a1 = 0;
		var a2 = 0;
		var n1 = false;
		var n2 = false;
		{
			/** Vorzeichen ermitteln und überspringen */
			if (a1 < s1) {
				var c1 = item1.charAt(a1);
				if (c1 == '-') {
					n1 = true;
					a1++;
				} else if (c1 == '+') {
					a1++;
				}
			}
			if (a2 < s2) {
				var c2 = item2.charAt(a2);
				if (c2 == '-') {
					n2 = true;
					a2++;
				} else if (c2 == '+') {
					a2++;
				}
			}
		}
		{
			/** '0' überspringen */
			while ((a1 < s1) && (item1.charAt(a1) == '0')) {
				a1++;
			}
			while ((a2 < s2) && (item2.charAt(a2) == '0')) {
				a2++;
			}
		}
		{
			/** War eine der Eingaben Leer oder "0" */
			if (a1 == s1) {
				if (a2 == s2) return 0;
				return n2 ? +1 : -1;
			}
			if (a2 == s2) return n1 ? -1 : +1;
		}
		{ /** Zahlen vergleichen */
			if (n1 != n2) return n1 ? -1 : +1;
			var result = (s1 - a1) - (s2 - a2);
			if (result != 0) return result;
			result = item1.substring(a1, s1).compareTo(item2.substring(a2, s2));
			return result;
		}
	};

	private static final Comparator3<String> alphabeticalComparator = (item1, item2) -> item1.compareToIgnoreCase(item2);

	private static final Comparator3<String> alphanumericalComparator = (item1, item2) -> {
		var s1 = item1.length();
		var s2 = item2.length();
		int a1 = 0, a2 = 0, e1, e2;
		while ((a1 < s1) && (a2 < s2)) {
			{
				/** Buchstaben überspringen => Halt, wenn Ziffer gefunden */
				for (e1 = a1; e1 < s1; e1++) {
					final var c1 = item1.charAt(e1);
					if (('0' <= c1) && (c1 <= '9')) {
						break;
					}
				}
				for (e2 = a2; e2 < s2; e2++) {
					final var c2 = item2.charAt(e2);
					if (('0' <= c2) && (c2 <= '9')) {
						break;
					}
				}
			}
			{
				/** Buchstaben vergleichen */
				if (a1 == e1) {
					if (a2 != e2) return -1;
				} else if (a2 != e2) {
					final var result = item1.substring(a1, e1).compareToIgnoreCase(item2.substring(a2, e2));
					if (result != 0) return result;
				} else return 1;
			}
			{
				/** '0' überspringen => Halt, wenn nicht '0' gefunden */
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
			{
				/** Ziffern überspringen => Halt, wenn nicht Ziffer gefunden */
				for (e1 = a1; e1 < s1; e1++) {
					final var c1 = item1.charAt(e1);
					if (('0' > c1) || (c1 > '9')) {
						break;
					}
				}
				for (e2 = a2; e2 < s2; e2++) {
					final var c2 = item2.charAt(e2);
					if (('0' > c2) || (c2 > '9')) {
						break;
					}
				}
			}
			{
				/** Ziffern vergleichen */
				if (a1 == e1) {
					if (a2 != e2) return -1;
				} else if (a2 != e2) {
					var comp = (e1 - a1) - (e2 - a2);
					if (comp != 0) return comp;
					comp = item1.substring(a1, e1).compareTo(item2.substring(a2, e2));
					if (comp != 0) return comp;
				} else return 1;
			}
			a1 = e1;
			a2 = e2;
		}
		return (s1 - a1) - (s2 - a2);
	};

}
