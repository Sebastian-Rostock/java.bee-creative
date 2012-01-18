package bee.creative.util;

import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zum Vergleich von Objekten sowie zur Erzeugung von
 * {@link Comparable Comparables}.
 * 
 * @see Comparable
 * @author Sebastian Rostock 2011.
 */
public class Comparables {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dass auf einen {@link Comparable Comparable} verweist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Comparable Comparables}.
	 */
	static abstract class ComparableLink<GInput> {

		/**
		 * Dieses Feld speichert den {@link Comparable Comparable}.
		 */
		final Comparable<? super GInput> comparable;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable Comparable}.
		 * 
		 * @param comparable {@link Comparable Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
		 */
		public ComparableLink(final Comparable<? super GInput> comparable) throws NullPointerException {
			if(comparable == null) throw new NullPointerException("Comparable is null");
			this.comparable = comparable;
		}

		/**
		 * Diese Methode gibt den {@link Comparable Comparable} zurück.
		 * 
		 * @return {@link Comparable Comparable}.
		 */
		public Comparable<? super GInput> comparable() {
			return this.comparable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final ComparableLink<?> data = (ComparableLink<?>)object;
			return Objects.equals(this.comparable, data.comparable);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparable Comparable}, der {@code null}-Eingaben vergleicht und alle
	 * anderen Eingaben an einen gegebenen {@link Comparable Comparable} weiterleitet. Der {@link Comparable Comparable}
	 * berechnet den Vergleichswert zweier Objekte {@code o1} und {@code o2} via:
	 * 
	 * <pre>
	 * ((o == null) ? -1 : comparable.compareTo(o));
	 * </pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class NullComparable<GEntry> extends ComparableLink<GEntry> implements Comparable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable Comparable}.
		 * 
		 * @param comparable {@link Comparable Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
		 */
		public NullComparable(final Comparable<? super GEntry> comparable) throws NullPointerException {
			super(comparable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GEntry o) {
			return ((o == null) ? -1 : this.comparable.compareTo(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof NullComparable<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("nullComparable", this.comparable);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Comparable Comparable}, der den Vergleichswert eines gegebenen
	 * {@link Comparable Comparables} umkehrt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ReverseComparable<GEntry> extends ComparableLink<GEntry> implements Comparable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable Comparable}.
		 * 
		 * @param comparable {@link Comparable Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
		 */
		public ReverseComparable(final Comparable<? super GEntry> comparable) throws NullPointerException {
			super(comparable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GEntry o) {
			return -this.comparable.compareTo(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ReverseComparable<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("reverseComparable", this.comparable);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Comparable Comparable}, der die beiden Objekte in seiner
	 * Eingabe zuerst über einen ersten {@link Comparable Comparable} vergleicht und einen zweiten {@link Comparable
	 * Comparable} nur dann verwendet, wenn der erste {@link Comparable Comparable} die Gleichheit der beiden Objekte
	 * anzeigt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ChainedComparable<GEntry> implements Comparable<GEntry> {

		/**
		 * Dieses Feld speichert den primären {@link Comparable Comparable}.
		 */
		final Comparable<? super GEntry> comparable1;

		/**
		 * Dieses Feld speichert den sekundären {@link Comparable Comparable}.
		 */
		final Comparable<? super GEntry> comparable2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Comparable Comparableen}.
		 * 
		 * @param comparable1 primärer {@link Comparable Comparable}.
		 * @param comparable2 sekundärer {@link Comparable Comparable}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Comparable Comparableen} {@code null} ist.
		 */
		public ChainedComparable(final Comparable<? super GEntry> comparable1, final Comparable<? super GEntry> comparable2)
			throws NullPointerException {
			if(comparable1 == null) throw new NullPointerException("Comparable1 is null");
			if(comparable2 == null) throw new NullPointerException("Comparable2 is null");
			this.comparable1 = comparable1;
			this.comparable2 = comparable2;
		}

		/**
		 * Diese Methode gibt den primären {@link Comparable Comparable} zurück.
		 * 
		 * @return primärer {@link Comparable Comparable}.
		 */
		public Comparable<? super GEntry> comparable1() {
			return this.comparable1;
		}

		/**
		 * Diese Methode gibt den sekundären {@link Comparable Comparable} zurück.
		 * 
		 * @return sekundärer {@link Comparable Comparable}.
		 */
		public Comparable<? super GEntry> comparable2() {
			return this.comparable2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GEntry o) {
			final int comp = this.comparable1.compareTo(o);
			return ((comp != 0) ? comp : this.comparable2.compareTo(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparable1, this.comparable2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ChainedComparable<?>)) return false;
			final ChainedComparable<?> data = (ChainedComparable<?>)object;
			return Objects.equals(this.comparable1, data.comparable1, this.comparable2, data.comparable2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedComparable", this.comparable1, this.comparable2);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Comparable Comparable}, der die mit einem gegebenen
	 * {@link Converter Converter} konvertierten Objekte zum Vergleich an einen gegebenen {@link Comparable Comparable}
	 * delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Eingabe des {@link Converter Converters} sowie der vom {@link Comparable Comparable} zu
	 *        vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter Converters} sowie der vom gegebenen {@link Comparable
	 *        Comparable} zu vergleichenden Objekte.
	 */
	public static final class ConvertedComparable<GEntry, GValue> extends ComparableLink<GValue> implements
		Comparable<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GEntry, ? extends GValue> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Comparable Comparable} und {@link Converter Converter}.
		 * 
		 * @param comparable {@link Comparable Comparable}.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} bzw. der gegebene {@link Converter
		 *         Converter} {@code null} ist.
		 */
		public ConvertedComparable(final Comparable<? super GValue> comparable,
			final Converter<? super GEntry, ? extends GValue> converter) throws NullPointerException {
			super(comparable);
			if(converter == null) throw new NullPointerException("Converter is null");
			this.converter = converter;
		}

		/**
		 * Diese Methode gibt den {@link Converter Converter} zurück.
		 * 
		 * @return den {@link Converter Converter}.
		 */
		public Converter<? super GEntry, ? extends GValue> converter() {
			return this.converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GEntry o) {
			return this.comparable.compareTo(this.converter.convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.comparable, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedComparable<?, ?>)) return false;
			final ConvertedComparable<?, ?> data = (ConvertedComparable<?, ?>)object;
			return super.equals(object) && Objects.equals(this.converter, data.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedComparable", this.converter, this.comparable);
		}

	}

	/**
	 * Diese Methode führt die Bereichs- und Lageprüfung der gegebenen Indices aus und löst bei Fehlern entsprechende
	 * {@link RuntimeException Runtime-Exceptions} aus.
	 * 
	 * @param length Größe des Bereiches.
	 * @param fromIndex Beginn-Index (inklusiv).
	 * @param toIndex End-Index (exklusiv).
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	static void check(final int length, final int fromIndex, final int toIndex) throws IllegalArgumentException,
		IndexOutOfBoundsException {
		if(fromIndex > toIndex) throw new IllegalArgumentException("FromIndex > ToIndex");
		if(fromIndex < 0) throw new IndexOutOfBoundsException("FromIndex out of range: " + fromIndex);
		if(toIndex > length) throw new IndexOutOfBoundsException("ToIndex out of range: " + toIndex);
	}

	static void check(final Object list, final Object comparable) throws NullPointerException {
		if(list == null) throw new NullPointerException("List is null");
		if(comparable == null) throw new NullPointerException("Comparable is null");
	}

	static void check(final Object[] list, final Comparable<?> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable);
		Comparables.check(list.length, fromIndex, toIndex);
	}

	static void check(final List<?> list, final Comparable<?> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable);
		Comparables.check(list.size(), fromIndex, toIndex);
	}

	static <GItem> int search(final GItem[] list, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) {
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list[next]);
			if(comp < 0){
				last = next;
			}else if(comp > 0){
				from = next + 1;
			}else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparable Comparable}, der {@code null}-Eingaben vergleicht sowie alle anderen
	 * Eingaben an einen gegebenen {@link Comparable Comparable} weiterleitet, und gibt ihn zurück. Der erzeugte
	 * {@link Comparable Comparable} berechnet den Vergleichswert zweier Objekte {@code o1} und {@code o2} via:
	 * 
	 * <pre>((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : comparable.compare(o1, o2)))</pre>
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable {@link Comparable Comparable}
	 * @return {@link NullComparable Null-Comparable}
	 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> nullComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new NullComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparable Comparable}, der den Vergleichswert des gegebenen {@link Comparable
	 * Comparables} umkehrt, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable {@link Comparable Comparable}.
	 * @return {@link ReverseComparable Reverse-Comparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> reverseComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new ReverseComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Comparable Comparable} und gibt ihn zurück. Der erzeugte
	 * {@link Comparable Comparable} vergleicht seine beiden Eingaben zuerst über den ersten {@link Comparable Comparable}
	 * und verwendet den zweiten {@link Comparable Comparable} nur dann, wenn der erste {@link Comparable Comparable} mit
	 * dem Vergleichswert {@code 0} die Gleichheit der beiden Objekte anzeigt.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable1 erster {@link Comparable Comparable}.
	 * @param comparable2 zweiter {@link Comparable Comparable}.
	 * @return {@link ChainedComparable Chained-Comparable}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Comparable Comparableen} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> chainedComparable(final Comparable<? super GEntry> comparable1,
		final Comparable<? super GEntry> comparable2) throws NullPointerException {
		return new ChainedComparable<GEntry>(comparable1, comparable2);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Comparable Comparable}, der die mit dem gegebenen
	 * {@link Converter Converter} konvertierten Objekte zum Vergleich an den gegebenen {@link Comparable Comparable}
	 * delegiert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GEntry> Typ der Eingabe des {@link Converter Converter} sowie der vom konvertierender {@link Comparable
	 *        Comparable} zu vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter Converter} sowie der vom gegebenen {@link Comparable
	 *        Comparable} zu vergleichenden Objekte.
	 * @param converter {@link Converter Converter}.
	 * @param comparable {@link Comparable Comparable}.
	 * @return {@link ConvertedComparable Converted-Comparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable Comparable} oder der gegebene {@link Converter
	 *         Converter} {@code null} sind.
	 */
	public static <GEntry, GValue> Comparable<GEntry> convertedComparable(
		final Converter<? super GEntry, ? extends GValue> converter, final Comparable<? super GValue> comparable)
		throws NullPointerException {
		return new ConvertedComparable<GEntry, GValue>(comparable, converter);
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @see Arrays#asList(Object...)
	 * @see Comparables#binarySearch(List, Comparable)
	 * @param <GItem>
	 * @param list
	 * @param comparable
	 * @return
	 */
	public static <GItem> int binarySearch(final GItem[] list, final Comparable<? super GItem> comparable) {
		Comparables.check(list, comparable);
		return Comparables.search(list, comparable, 0, list.length);
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @see Arrays#asList(Object...)
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem>
	 * @param list
	 * @param comparable
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public static <GItem> int binarySearch(final GItem[] list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) {
		Comparables.check(list, comparable);
		return Comparables.search(list, comparable, fromIndex, toIndex);
	}

	public static <GItem> int binarySearch(final List<GItem> list, final Comparable<? super GItem> comparable) {
		return Comparables.binarySearch(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List List} eine binäre Suche mit dem gegebenen {@link Comparable
	 * Comparable} durch und gibt den Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
	 * Die gegebene {@link List List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable Comparables}
	 * aufsteigend sortiert sein. Wenn die Liste mehrere Elemente enthällt, die zum gegebenen {@link Comparable
	 * Comparable} gleich sind, wird der Index eines beliebigen dieser Elemente zurück gegeben. Ein Element
	 * {@code element} ist dann zum gegebenen {@link Comparable Comparable} {@code comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable Comparable} gleiches Element in die {@link List List} eingefügt werden müsste, um die Ordnung zu
	 * erhalten.
	 * 
	 * @see Arrays#binarySearch(Object[], Object)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable comparable}.
	 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List List} bzw. der gegebene {@link Comparable Comparable}
	 *         {@code null} ist.
	 * @throws ClassCastException Wenn das gegebene {@link Comparable Comparable} inkompatibel mit den Elementen der
	 *         gegebenen {@link List List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearch(final List<GItem> list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if(comp < 0){
				last = next;
			}else if(comp > 0){
				from = next + 1;
			}else return next;
		}
		return -(from + 1);
	}

	public static <GItem> int binarySearchFirst(final GItem[] list, final Comparable<? super GItem> comparable) {
		return Comparables.binarySearchFirst(Arrays.asList(list), comparable);
	}

	public static <GItem> int binarySearchFirst(final GItem[] list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) {
		return Comparables.binarySearchFirst(Arrays.asList(list), comparable, fromIndex, toIndex);
	}

	public static <GItem> int binarySearchFirst(final List<GItem> list, final Comparable<? super GItem> comparable) {
		return Comparables.binarySearchFirst(list, comparable, 0, list.size());
	}

	public static <GItem> int binarySearchFirst(final List<GItem> list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if(comp <= 0){
				last = next;
			}else{
				from = next + 1;
			}
		}
		if((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(list.get(from)) == 0)) return from;
		return -(from + 1);
	}

	public static <GItem> int binarySearchLast(final GItem[] list, final Comparable<? super GItem> comparable) {
		return Comparables.binarySearchLast(Arrays.asList(list), comparable);
	}

	public static <GItem> int binarySearchLast(final GItem[] list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) {
		return Comparables.binarySearchLast(Arrays.asList(list), comparable, fromIndex, toIndex);
	}

	public static <GItem> int binarySearchLast(final List<GItem> list, final Comparable<? super GItem> comparable) {
		return Comparables.binarySearchLast(list, comparable, 0, list.size());
	}

	public static <GItem> int binarySearchLast(final List<GItem> list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) {
		Comparables.check(list, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(list.get(next));
			if(comp < 0){
				last = next;
			}else{
				from = next + 1;
			}
		}
		from--;
		if((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(list.get(from)) == 0)) return from;
		return -(from + 1);
	}

}
