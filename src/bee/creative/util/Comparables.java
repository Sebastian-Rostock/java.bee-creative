package bee.creative.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zum Vergleich von Objekten sowie zur Erzeugung von
 * {@link Comparable}s.
 * 
 * @see Comparable
 * @author Sebastian Rostock 2011.
 */
public class Comparables {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dass auf einen {@link Comparable} verweist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Comparable}s.
	 */
	static abstract class ComparableLink<GInput> {

		/**
		 * Dieses Feld speichert den {@link Comparable}.
		 */
		final Comparable<? super GInput> comparable;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
		 */
		public ComparableLink(final Comparable<? super GInput> comparable) throws NullPointerException {
			if(comparable == null) throw new NullPointerException("comparable is null");
			this.comparable = comparable;
		}

		/**
		 * Diese Methode gibt den {@link Comparable} zurück.
		 * 
		 * @return {@link Comparable}.
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
	 * Diese Klasse implementiert einen {@link Comparable}, der {@code null}-Eingaben vergleicht und alle anderen Eingaben
	 * an einen gegebenen {@link Comparable} weiterleitet. Der {@link Comparable} berechnet den Vergleichswert zweier
	 * Objekte {@code o1} und {@code o2} via:
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
		 * Dieser Konstrukteur initialisiert den {@link Comparable}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Comparable}, der den Vergleichswert eines gegebenen {@link Comparable}s
	 * umkehrt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ReverseComparable<GEntry> extends ComparableLink<GEntry> implements Comparable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
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
	 * Diese Klasse implementiert einen verketteten {@link Comparable}, der die beiden Objekte in seiner Eingabe zuerst
	 * über einen ersten {@link Comparable} vergleicht und einen zweiten {@link Comparable} nur dann verwendet, wenn der
	 * erste {@link Comparable} die Gleichheit der beiden Objekte anzeigt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Objekte.
	 */
	public static final class ChainedComparable<GEntry> implements Comparable<GEntry> {

		/**
		 * Dieses Feld speichert den primären {@link Comparable}.
		 */
		final Comparable<? super GEntry> comparable1;

		/**
		 * Dieses Feld speichert den sekundären {@link Comparable}.
		 */
		final Comparable<? super GEntry> comparable2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Comparable}.
		 * 
		 * @param comparable1 primärer {@link Comparable}.
		 * @param comparable2 sekundärer {@link Comparable}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Comparable} {@code null} ist.
		 */
		public ChainedComparable(final Comparable<? super GEntry> comparable1, final Comparable<? super GEntry> comparable2)
			throws NullPointerException {
			if(comparable1 == null) throw new NullPointerException("comparable1 is null");
			if(comparable2 == null) throw new NullPointerException("comparable2 is null");
			this.comparable1 = comparable1;
			this.comparable2 = comparable2;
		}

		/**
		 * Diese Methode gibt den primären {@link Comparable} zurück.
		 * 
		 * @return primärer {@link Comparable}.
		 */
		public Comparable<? super GEntry> comparable1() {
			return this.comparable1;
		}

		/**
		 * Diese Methode gibt den sekundären {@link Comparable} zurück.
		 * 
		 * @return sekundärer {@link Comparable}.
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
	 * Diese Klasse implementiert einen konvertierenden {@link Comparable}, der die mit einem gegebenen {@link Converter}
	 * konvertierten Objekte zum Vergleich an einen gegebenen {@link Comparable} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Eingabe des {@link Converter}s sowie der vom {@link Comparable} zu vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter}s sowie der vom gegebenen {@link Comparable} zu vergleichenden
	 *        Objekte.
	 */
	public static final class ConvertedComparable<GEntry, GValue> extends ComparableLink<GValue> implements
		Comparable<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GEntry, ? extends GValue> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Comparable} und {@link Converter}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} bzw. der gegebene {@link Converter}
		 *         {@code null} ist.
		 */
		public ConvertedComparable(final Comparable<? super GValue> comparable,
			final Converter<? super GEntry, ? extends GValue> converter) throws NullPointerException {
			super(comparable);
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
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException Runtime-Exceptions}
	 * aus.
	 * 
	 * @param length Größe des Bereiches.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > length}.
	 */
	static void check(final int length, final int fromIndex, final int toIndex) throws IllegalArgumentException,
		IndexOutOfBoundsException {
		if(fromIndex > toIndex) throw new IllegalArgumentException("fromIndex > toIndex");
		if(fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex out of range: " + fromIndex);
		if(toIndex > length) throw new IndexOutOfBoundsException("toIndex out of range: " + toIndex);
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException Runtime-Exceptions}
	 * aus.
	 * 
	 * @param list Suchraum.
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn der gegebene Suchraum bzw. der gegebene {@link Comparable} {@code null} ist.
	 */
	static void check(final Object list, final Object comparable) throws NullPointerException {
		if(list == null) throw new NullPointerException("list is null");
		if(comparable == null) throw new NullPointerException("comparable is null");
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException Runtime-Exceptions}
	 * aus.
	 * 
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > array.length}.
	 */
	static void check(final Object[] array, final Object comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(array, comparable);
		Comparables.check(array.length, fromIndex, toIndex);
	}

	/**
	 * Diese Methode prüft ihre Eingaben und löst bei Fehlern entsprechende {@link RuntimeException Runtime-Exceptions}
	 * aus.
	 * 
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	static void check(final List<?> list, final Object comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(list, comparable);
		Comparables.check(list.size(), fromIndex, toIndex);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparable}, der {@code null}-Eingaben vergleicht sowie alle anderen Eingaben an
	 * einen gegebenen {@link Comparable} weiterleitet, und gibt ihn zurück. Der erzeugte {@link Comparable} berechnet den
	 * Vergleichswert zweier Objekte {@code o1} und {@code o2} via:
	 * 
	 * <pre>
	 * ((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : comparable.compare(o1, o2)))
	 * </pre>
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable {@link Comparable}
	 * @return {@link NullComparable}
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> nullComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new NullComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparable}, der den Vergleichswert des gegebenen {@link Comparable Comparables}
	 * umkehrt, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable {@link Comparable}.
	 * @return {@link ReverseComparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> reverseComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new ReverseComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Comparable} und gibt ihn zurück. Der erzeugte {@link Comparable}
	 * vergleicht seine beiden Eingaben zuerst über den ersten {@link Comparable} und verwendet den zweiten
	 * {@link Comparable} nur dann, wenn der erste {@link Comparable} mit dem Vergleichswert {@code 0} die Gleichheit der
	 * beiden Objekte anzeigt.
	 * 
	 * @param <GEntry> Typ der Objekte.
	 * @param comparable1 erster {@link Comparable}.
	 * @param comparable2 zweiter {@link Comparable}.
	 * @return {@link ChainedComparable}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> chainedComparable(final Comparable<? super GEntry> comparable1,
		final Comparable<? super GEntry> comparable2) throws NullPointerException {
		return new ChainedComparable<GEntry>(comparable1, comparable2);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Comparable}, der die mit dem gegebenen {@link Converter}
	 * konvertierten Objekte zum Vergleich an den gegebenen {@link Comparable} delegiert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GEntry> Typ der Eingabe des {@link Converter} sowie der vom konvertierender {@link Comparable} zu
	 *        vergleichenden Objekte.
	 * @param <GValue> Typ der Ausgabe des {@link Converter} sowie der vom gegebenen {@link Comparable} zu vergleichenden
	 *        Objekte.
	 * @param converter {@link Converter}.
	 * @param comparable {@link Comparable}.
	 * @return {@link ConvertedComparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} oder der gegebene {@link Converter} {@code null}
	 *         sind.
	 */
	public static <GEntry, GValue> Comparable<GEntry> convertedComparable(
		final Converter<? super GEntry, ? extends GValue> converter, final Comparable<? super GValue> comparable)
		throws NullPointerException {
		return new ConvertedComparable<GEntry, GValue>(comparable, converter);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der Index eines
	 * beliebigen dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable}
	 * gleich, wenn {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum
	 * gegebenen {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearch(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der Index eines
	 * beliebigen dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable}
	 * gleich, wenn {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum
	 * gegebenen {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearch(final GItem[] array, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if(comp < 0){
				last = next;
			}else if(comp > 0){
				from = next + 1;
			}else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der Index eines
	 * beliebigen dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable}
	 * gleich, wenn {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum
	 * gegebenen {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearch(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der Index eines
	 * beliebigen dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable}
	 * gleich, wenn {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum
	 * gegebenen {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Index des ersten Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
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

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den kleinsten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der kleinste Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinster Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den kleinsten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable }s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der kleinste Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinster Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] array, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if(comp <= 0){
				last = next;
			}else{
				from = next + 1;
			}
		}
		if((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(array[from]) == 0)) return from;
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den kleinsten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der kleinste Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinster Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearchFirst(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den kleinsten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der kleinste Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinster Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearchFirst(final List<GItem> list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
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

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den größten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der größte Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größter Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearchLast(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den größten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Das gegebene
	 * {@link Array} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn das
	 * {@link Array} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der größte Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in das {@link Array} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größter Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearchLast(final GItem[] array, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
		Comparables.check(array, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while(from < last){
			final int next = (from + last) >>> 1, comp = comparable.compareTo(array[next]);
			if(comp < 0){
				last = next;
			}else{
				from = next + 1;
			}
		}
		from--;
		if((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(array[from]) == 0)) return from;
		return -(from + 1);
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den größten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der größte Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größter Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearchLast(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} aus und
	 * gibt den größten Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die gegebene
	 * {@link List} muss dazu bezüglich der Ordnung des gegebenen {@link Comparable}s aufsteigend sortiert sein. Wenn die
	 * {@link List} mehrere Elemente enthällt, die zum gegebenen {@link Comparable} gleich sind, wird der größte Index
	 * dieser Elemente zurück gegeben. Ein Element {@code element} ist dann zum gegebenen {@link Comparable} gleich, wenn
	 * {@code comparable.compareTo(element) == 0}. Die <i>Einfügeposition</i> ist der Index, bei dem ein zum gegebenen
	 * {@link Comparable} gleiches Element in die {@link List} eingefügt werden müsste.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größter Index eines Treffers oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws ArrayIndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
	public static <GItem> int binarySearchLast(final List<GItem> list, final Comparable<? super GItem> comparable,
		final int fromIndex, final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException,
		ArrayIndexOutOfBoundsException {
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
