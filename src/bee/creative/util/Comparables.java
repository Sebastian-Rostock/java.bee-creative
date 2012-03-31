package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zur binären Suche mit {@link Comparable}s als Suchkriterium sowie
 * zur Erzeugung von {@link Comparable}s.
 * <p>
 * <a name="comparables_old"><u>Binäre Suche mit {@link Arrays} und {@link Collections}</u></a>
 * <p>
 * Die Schwächen der Hilfsmethoden {@link Arrays#binarySearch(Object[], Object, Comparator)} und
 * {@link Collections#binarySearch(List, Object, Comparator)} liegen zum einen bei ihrer Beschränkung auf Suchkriterien,
 * die zum Typ der Elemente in den {@link Array}s bzw. {@link List}s kompatibel sein müssel, und zum anderen im
 * Indererminismus bei merhfach vorkommenden Elemente.
 * <p>
 * <a name="comparables_new"><u>Binäre Suche mit {@link Comparables}</u></a>
 * <p>
 * Die hier in {@link Comparables} implementierten Hilfsmethoden zur binären Suche abstrahieren Suchkriterien als
 * {@link Comparable}s, welche zu einem gegebenen Element einen Navigationswert berechnen. Bei einer binären Suche mit
 * dem einem {@link Comparable} {@code comparable} als Suchkriterium gilt ein Element {@code element} an Position
 * {@code index} als Treffer, wenn der Navigationswert {@code comparable.compareTo(element)} gleich {@code 0} ist. Wenn
 * der Navigationswert dagegen kleier oder größer als {@code 0} ist, wird die binäre Suche bei den Positionen kleier
 * bzw. größer als {@code index} fortgesetzt. Bei eienr erfolglosen benären Suche geben die Hilfsmethoden
 * <code>(-(<i>Einfügeposition</i>)-1)</code> zurück, sodass ein positiver Rückgabewert immer einen Treffer
 * signalisiert. Die <i>Einfügeposition</i> ist dabei die Position, an der ein Element seiner Ordnung entsprechend in
 * das {@link Array} bzw. die {@link List} eingefügt werden müsste. Das gegebene {@link Array} bzw. die gegebene
 * {@link List} muss bezüglich der Ordnung des {@link Comparable}s aufsteigend sortiert sein.
 * <p>
 * Nebn den für merhfach vorkommenden Elemente indererministischen {@code binarySearch()}-Methoden gibt es hier auch die
 * deterministischen {@code binarySearchFirst()}- und {@code binarySearchLast()}-Methoden, welche nach der kleinsten
 * bzw. größten Position eines Treffers suchen.
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
	 * Diese Klasse implementiert einen abstrakten delegierenden {@link Comparable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Comparable}s.
	 */
	static abstract class AbstractComparable<GEntry, GEntry2> implements Comparable<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Comparable}.
		 */
		final Comparable<? super GEntry2> comparable;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparable}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
		 */
		public AbstractComparable(final Comparable<? super GEntry2> comparable) throws NullPointerException {
			if(comparable == null) throw new NullPointerException("comparable is null");
			this.comparable = comparable;
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
			final AbstractComparable<?, ?> data = (AbstractComparable<?, ?>)object;
			return Objects.equals(this.comparable, data.comparable);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Comparable}, das {@code null}-Elemente als minimal betrachtet und alle
	 * anderen Eingaben an einen gegebenen {@link Comparable} delegiert. Der Navigationswert für ein Element
	 * {@code element} sowie ein {@link Comparable} {@code comparable} ergibt sich aus:
	 * 
	 * <pre>((element == null) ? 1 : comparable.compareTo(element))</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class NullComparable<GEntry> extends AbstractComparable<GEntry, GEntry> {

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
			return ((o == null) ? 1 : this.comparable.compareTo(o));
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
	 * Diese Klasse implementiert einen {@link Comparable}, der den Navigationswert eines gegebenen {@link Comparable}s
	 * umkehrt. Der Navigationswert für ein Element {@code element} sowie ein {@link Comparable} {@code comparable} ergibt
	 * sich aus:
	 * 
	 * <pre>-comparable.compareTo(element)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ReverseComparable<GEntry> extends AbstractComparable<GEntry, GEntry> {

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
	 * Diese Klasse implementiert einen verketteten {@link Comparable}, der den Navigationswert eines Elements zuerst über
	 * den ersten {@link Comparable} berechnet und nur dann den zweiten {@link Comparable} verwendet, wenn der erste
	 * {@link Comparable} den Navigationswert {@code 0} ermittelt hat. Der Navigationswert für ein Element {@code element}
	 * sowie die {@link Comparable}s {@code comparable1} und {@code comparable2} ergibt sich aus:
	 * 
	 * <pre>(comparable1.compareTo(element) != 0) ? comparable1.compareTo(element) : comparable2.compareTo(element)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
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
	 * konvertierten Elemente zur Berechnung des Navigationswerts an einen gegebenen {@link Comparable} delegiert. Der
	 * Navigationswert für ein Element {@code element}, einen {@link Converter} {@code onverter} und einen
	 * {@link Comparable} {@code comparable} ergibt sich aus:
	 * 
	 * <pre>comparable.compareTo(converter.convert(element))</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des {@link Converter}s sowie der Elemente.
	 * @param <GOutput> Typ der Ausgabe des {@link Converter}s sowie der Elemente des gegebenen {@link Comparable}s.
	 */
	public static final class ConvertedComparable<GInput, GOutput> extends AbstractComparable<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Comparable} und {@link Converter}.
		 * 
		 * @param comparable {@link Comparable}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparable} bzw. der gegebene {@link Converter}
		 *         {@code null} ist.
		 */
		public ConvertedComparable(final Comparable<? super GOutput> comparable,
			final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			super(comparable);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final GInput o) {
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
			return Objects.equals(this.comparable, data.comparable) && Objects.equals(this.converter, data.converter);
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
	 * Diese Methode erzeugt einen {@link Comparable}, das {@code null}-Elemente als minimal betrachtet und alle anderen
	 * Eingaben an einen gegebenen {@link Comparable} delegiert, und gibt ihn zurück. Der Navigationswert für ein Element
	 * {@code element} ergibt sich aus:
	 * 
	 * <pre>((element == null) ? 1 : comparable.compareTo(element))</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable {@link Comparable}
	 * @return {@link NullComparable}
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> Comparable<GEntry> nullComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new NullComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Comparable}, der den Navigationswert des gegebenen {@link Comparable}s umkehrt,
	 * und gibt ihn zurück. Der Navigationswert für ein Element {@code element} ergibt sich aus:
	 * 
	 * <pre>- comparable.compareTo(element)</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@link ReverseComparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> ReverseComparable<GEntry> reverseComparable(final Comparable<? super GEntry> comparable)
		throws NullPointerException {
		return new ReverseComparable<GEntry>(comparable);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Comparable}, der den Navigationswert eines Elements zuerst über den
	 * ersten {@link Comparable} berechnet und nur dann den zweiten {@link Comparable} verwendet, wenn der erste
	 * {@link Comparable} den Navigationswert {@code 0} ermittelt hat, und gibt ihn zurück. Der Navigationswert für ein
	 * Element {@code element} ergibt sich aus:
	 * 
	 * <pre>(comparable1.compareTo(element) != 0) ? comparable1.compareTo(element) : comparable2.compareTo(element)</pre>
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param comparable1 erster {@link Comparable}.
	 * @param comparable2 zweiter {@link Comparable}.
	 * @return {@link ChainedComparable}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Comparable} {@code null} ist.
	 */
	public static <GEntry> ChainedComparable<GEntry> chainedComparable(final Comparable<? super GEntry> comparable1,
		final Comparable<? super GEntry> comparable2) throws NullPointerException {
		return new ChainedComparable<GEntry>(comparable1, comparable2);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Comparable}, der die mit dem gegebenen {@link Converter}
	 * konvertierten Elemente zur Berechnung des Navigationswerts an den gegebenen {@link Comparable} delegiert, und gibt
	 * ihn zurück. Der Navigationswert für ein Element {@code element} ergibt sich aus:
	 * 
	 * <pre>comparable.compareTo(converter.convert(element))</pre>
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des {@link Converter}s sowie der Elemente.
	 * @param <GOutput> Typ der Ausgabe des {@link Converter}s sowie der Elemente des gegebenen {@link Comparable}s.
	 * @param converter {@link Converter}.
	 * @param comparable {@link Comparable}.
	 * @return {@link ConvertedComparable}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparable} oder der gegebene {@link Converter} {@code null}
	 *         sind.
	 */
	public static <GInput, GOutput> ConvertedComparable<GInput, GOutput> convertedComparable(
		final Converter<? super GInput, ? extends GOutput> converter, final Comparable<? super GOutput> comparable)
		throws NullPointerException {
		return new ConvertedComparable<GInput, GOutput>(comparable, converter);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearch(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearch(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearchFirst(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn das gegebene {@link Array} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen
	 *         {@link Array}s ist.
	 */
	public static <GItem> int binarySearchLast(final GItem[] array, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(array, comparable, 0, array.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param array {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. der gegebene {@link Comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen
	 *         {@link List} ist.
	 */
	public static <GItem> int binarySearchLast(final List<GItem> list, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(list, comparable, 0, list.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als
	 * Suchkriterium aus und gibt die größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>
	 * zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param list {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
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

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Comparables() {
	}

}
