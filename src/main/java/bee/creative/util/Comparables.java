package bee.creative.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zur binären Suche mit {@link Comparable}s als Suchkriterium sowie zur Erzeugung von {@link Comparable}s.
 * <p>
 * <a name="comparables_old"><u>Binäre Suche mit {@link Arrays} und {@link Collections}</u></a>
 * <p>
 * Die Schwächen der Hilfsmethoden {@link Arrays#binarySearch(Object[], Object, Comparator)} und {@link Collections#binarySearch(List, Object, Comparator)}
 * liegen zum einen bei ihrer Beschränkung auf Suchkriterien, die zum Typ der Elemente in den Arrays bzw. {@link List}s kompatibel sein müssel, und zum anderen
 * im Indererminismus bei merhfach vorkommenden Elemente.
 * <p>
 * <a name="comparables_new"><u>Binäre Suche mit {@link Comparables}</u></a>
 * <p>
 * Die hier in {@link Comparables} implementierten Hilfsmethoden zur binären Suche abstrahieren Suchkriterien als {@link Comparable}s, welche zu einem gegebenen
 * Element einen Navigationswert berechnen. Bei einer binären Suche mit dem einem {@link Comparable} {@code comparable} als Suchkriterium gilt ein Element
 * {@code element} an Position {@code index} als Treffer, wenn der Navigationswert {@code comparable.compareTo(element)} gleich {@code 0} ist. Wenn der
 * Navigationswert dagegen kleier oder größer als {@code 0} ist, wird die binäre Suche bei den Positionen kleier bzw. größer als {@code index} fortgesetzt. Bei
 * einer erfolglosen benären Suche geben die Hilfsmethoden <code>(-(<i>Einfügeposition</i>)-1)</code> zurück, sodass ein positiver Rückgabewert immer einen
 * Treffer signalisiert. Die <i>Einfügeposition</i> ist dabei die Position, an der ein Element seiner Ordnung entsprechend in das Array bzw. die {@link List}
 * eingefügt werden müsste. Das gegebene Array bzw. die gegebene {@link List} muss bezüglich der Ordnung des {@link Comparable}s aufsteigend sortiert sein.
 * <p>
 * Nebn den für merhfach vorkommenden Elemente indererministischen {@code binarySearch()}-Methoden gibt es hier auch die deterministischen
 * {@code binarySearchFirst()}- und {@code binarySearchLast()}-Methoden, welche nach der kleinsten bzw. größten Position eines Treffers suchen.
 * 
 * @see Arrays#binarySearch(Object[], Object)
 * @see Arrays#binarySearch(Object[], Object, Comparator)
 * @see Collections#binarySearch(List, Object)
 * @see Collections#binarySearch(List, Object, Comparator)
 * @see Comparable
 * @author Sebastian Rostock 2011.
 */
public class Comparables {

	/**
	 * Diese Schnittstelle definiert eine Methode zum Lesen eines Element zu einem gegebenen Index.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static interface Get<GItem> {

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public GItem get(int index) throws IndexOutOfBoundsException;

	}

	/**
	 * Diese Klasse implementiert eine beliebig sortierte Sicht auf die Elemente eines {@link Get}. Das zu einem Index via {@link #get(int)} gelieferten Elemente
	 * entspricht dem eines gegebenen {@link Get}s, dessen Index über ein gegebenes Array von Indizes ermittelt wird.
	 * 
	 * @see #get(int)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class GetSection<GItem> implements Get<GItem> {

		/**
		 * Dieses Feld speichert das {@link Get}.
		 */
		final Get<? extends GItem> get;

		/**
		 * Dieses Feld speichert den Suchraum.
		 */
		final int[] indices;

		/**
		 * Dieser Konstruktor initialisiert Besitzer und Indexraum.
		 * 
		 * @param get Besitzer.
		 * @param indices Indexraum.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public GetSection(final Get<? extends GItem> get, final int[] indices) throws NullPointerException {
			if ((get == null) || (indices == null)) throw new NullPointerException();
			this.get = get;
			this.indices = indices;
		}

		/**
		 * Diese Methode gibt das {@link Get} zurück, auf dessen Elemente via {@link #get(int)} zugegriffen werden kann.
		 * 
		 * @see #get(int)
		 * @see #indices()
		 * @return {@link Get}.
		 */
		public Get<? extends GItem> get() {
			return this.get;
		}

		/**
		 * Diese Methode gibt den Indexraum zurück, der darüber entscheidet, auf welche Elemente via {@link #get(int)} zugegriffen werden kann.
		 * 
		 * @see #get(int)
		 * @see #indices()
		 * @return Indexraum.
		 */
		public int[] indices() {
			return this.indices.clone();
		}

		/**
		 * {@inheritDoc} <br>
		 * Dieses entspricht dem Ausdruck {@code this.get().get(this.indices()[index])}.
		 */
		@Override
		public GItem get(final int index) throws IndexOutOfBoundsException {
			return this.get.get(this.indices[index]);
		}

	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
	 */
	static void check(final Comparable<?> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException();
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 */
	static void check(final int fromIndex, final int toIndex) throws IllegalArgumentException {
		if (fromIndex > toIndex) throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param length Größe des Bereiches.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > length}.
	 */
	static void check(final int length, final int fromIndex, final int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(fromIndex, toIndex);
		if (fromIndex < 0) throw new IndexOutOfBoundsException();
		if (toIndex > length) throw new IndexOutOfBoundsException();
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param array Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > array.length}.
	 */
	static void check(final Object[] array, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException, IndexOutOfBoundsException {
		if (array == null) throw new NullPointerException();
		Comparables.check(comparable);
		Comparables.check(array.length, fromIndex, toIndex);
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	static void check(final List<?> list, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException, IndexOutOfBoundsException {
		if (list == null) throw new NullPointerException();
		Comparables.check(comparable);
		Comparables.check(list.size(), fromIndex, toIndex);
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException}s aus.
	 * 
	 * @param items {@link Get} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn die gegebene {@link Get} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 */
	static void check(final Get<?> items, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException {
		if (items == null) throw new NullPointerException();
		Comparables.check(comparable);
		Comparables.check(fromIndex, toIndex);
	}

	{}

	/**
	 * Diese Methode wählt aus den gegebenen Elementen ein Element mit der kleinsten Ordnung und gibt es zurück. Der Navigationswert eines solchen Elements ist
	 * bezogen auf jedes der gegebenen Elemente immer kleiner oder gleich {@code 0}.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable Elemente.
	 * @return Element mit der kleinsten Ordnung oder {@code null}.
	 */
	public static <GEntry extends Comparable<? super GEntry>> GEntry min(final Iterable<? extends GEntry> iterable) {
		final Iterator<? extends GEntry> iterator = iterable.iterator();
		if (!iterator.hasNext()) return null;
		GEntry result = iterator.next();
		while (iterator.hasNext()) {
			final GEntry entry = iterator.next();
			if (entry.compareTo(result) < 0) {
				result = entry;
			}
		}
		return result;
	}

	/**
	 * Diese Methode wählt aus den gegebenen Elementen ein Element mit der größten Ordnung und gibt es zurück. Der Navigationswert eines solchen Elements ist
	 * bezogen auf jedes der gegebenen Elemente immer größer oder gleich {@code 0}.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable Elemente.
	 * @return Element mit der kleinsten Ordnung oder {@code null}.
	 */
	public static <GEntry extends Comparable<? super GEntry>> GEntry max(final Iterable<? extends GEntry> iterable) {
		final Iterator<? extends GEntry> iterator = iterable.iterator();
		if (!iterator.hasNext()) return null;
		GEntry result = iterator.next();
		while (iterator.hasNext()) {
			final GEntry entry = iterator.next();
			if (entry.compareTo(result) > 0) {
				result = entry;
			}
		}
		return result;
	}

	{}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der {@code null}-Elemente als minimal betrachtet und alle anderen Eingaben an einen gegebenen
	 * {@link Comparable} delegiert. Der Navigationswert für ein Element {@code element} ergibt sich aus:
	 * 
	 * <pre>((element == null) ? 1 : comparable.compareTo(element))</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@code null}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> nullComparable(final Comparable<? super GEntry> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException();
		return new Comparable<GEntry>() {

			@Override
			public int compareTo(final GEntry entry) {
				return ((entry == null) ? 1 : comparable.compareTo(entry));
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der den gegebenen {@link Comparator} sowie das gegebene Element zur Berechnung des Navigationswert
	 * verwendet. Das gegebene Element wird als erstes Argument des {@link Comparator}s verwendet. Der Navigationswert für ein Element {@code element} ergibt sich
	 * aus:
	 * 
	 * <pre>comparator.compare(entry, element)</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param entry erstes Argument des {@link Comparator}s.
	 * @param comparator {@link Comparator}.
	 * @return {@code entry}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> entryComparable(final GEntry entry, final Comparator<? super GEntry> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException();
		return new Comparable<GEntry>() {

			@Override
			public int compareTo(final GEntry element) {
				return comparator.compare(entry, element);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der den Navigationswert des gegebenen {@link Comparable} umkehrt. Der Navigationswert für ein Element
	 * {@code element} ergibt sich aus:
	 * 
	 * <pre>- comparable.compareTo(element)</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@code reverse}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> reverseComparable(final Comparable<? super GEntry> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException();
		return new Comparable<GEntry>() {

			@Override
			public int compareTo(final GEntry entry) {
				return -comparable.compareTo(entry);
			}

		};
	}

	/**
	 * Diese Methode gibt einen verketteten {@link Comparable} zurück, der den Navigationswert eines Elements zuerst über den ersten {@link Comparable} berechnet
	 * und nur dann den zweiten {@link Comparable} verwendet, wenn der erste {@link Comparable} den Navigationswert {@code 0} ermittelt hat. Der Navigationswert
	 * für ein Element {@code element} ergibt sich aus:
	 * 
	 * <pre>(comparable1.compareTo(element) != 0) ? comparable1.compareTo(element) : comparable2.compareTo(element)</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable1 erster {@link Comparable}.
	 * @param comparable2 zweiter {@link Comparable}.
	 * @return {@code chained}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable1} bzw. {@code comparable2} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> chainedComparable(final Comparable<? super GEntry> comparable1, final Comparable<? super GEntry> comparable2)
		throws NullPointerException {
		if ((comparable1 == null) || (comparable2 == null)) throw new NullPointerException();
		return new Comparable<GEntry>() {

			@Override
			public int compareTo(final GEntry entry) {
				final int result = comparable1.compareTo(entry);
				if (result != 0) return result;
				return comparable2.compareTo(entry);
			}

		};
	}

	/**
	 * Diese Methode gint einen konvertierenden {@link Comparable} zurück, der die mit dem gegebenen {@link Converter} konvertierten Elemente zur Berechnung des
	 * Navigationswerts an den gegebenen {@link Comparable} delegiert. Der Navigationswert für ein Element {@code element} ergibt sich aus:
	 * 
	 * <pre>comparable.compareTo(converter.convert(element))</pre>
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des {@link Converter}s sowie der Elemente.
	 * @param <GOutput> Typ der Ausgabe des {@link Converter}s sowie der Elemente des gegebenen {@link Comparable}s.
	 * @param converter {@link Converter}.
	 * @param comparable {@link Comparable}.
	 * @return {@code converted}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code comparable} {@code null} ist.
	 */
	public static <GInput, GOutput> Comparable<GInput> convertedComparable(final Converter<? super GInput, ? extends GOutput> converter,
		final Comparable<? super GOutput> comparable) throws NullPointerException {
		if ((converter == null) || (comparable == null)) throw new NullPointerException();
		return new Comparable<GInput>() {

			@Override
			public int compareTo(final GInput entry) {
				return comparable.compareTo(converter.convert(entry));
			}

		};
	}

	{}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 */
	public static <GItem> int binarySearch(final GItem[] array, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearch(final GItem[] array, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearch(final List<? extends GItem> list, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearch(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearch(final List<? extends GItem> list, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Get} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Get} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Get} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Get} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Get} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearch(final Get<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] array, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] array, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(array[from]) == 0)) return from;
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearchFirst(final List<? extends GItem> list, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearchFirst(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearchFirst(final List<? extends GItem> list, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(list.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Get} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Get} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Get} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Get} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Get} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearchFirst(final Get<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position eines
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 */
	public static <GItem> int binarySearchLast(final GItem[] array, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position eines
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearchLast(final GItem[] array, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(array[from]) == 0)) return from;
		return -from - 2;
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearchLast(final List<? extends GItem> list, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearchLast(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearchLast(final List<? extends GItem> list, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(list.get(from)) == 0)) return from;
		return -from - 2;
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Get} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position
	 * eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Get} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Get} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Get} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Get} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearchLast(final Get<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -from - 2;
	}

	{}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Comparables() {
	}

}
