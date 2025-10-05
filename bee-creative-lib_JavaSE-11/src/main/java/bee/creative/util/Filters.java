package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterables.iterableFromArray;
import static bee.creative.util.Iterables.iterableToSet;
import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	/** Diese Methode ist eine Abkürzung für {@code value ? acceptFilter() : rejectFilter()}.
	 *
	 * @see #acceptFilter()
	 * @see #rejectFilter() */
	public static <ITEM> Filter3<ITEM> filterFrom(boolean value) {
		return value ? acceptFilter() : rejectFilter();
	}

	/** Diese Methode liefert den gegebenen {@link Filter} als {@link Filter3}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter3<ITEM> filterFrom(Filter<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		if (that instanceof Filter2) return ((Filter2<ITEM>)that).asFilter();
		return item -> that.accepts(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableFromArray(items))}.
	 *
	 * @see Iterables#iterableFromArray(Object...) */
	public static Filter3<Object> filterFromItems(Object... items) throws NullPointerException {
		return filterFromItems(iterableFromArray(items));
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableToSet(that))}.
	 *
	 * @see Iterables#iterableToSet(Iterable) */
	public static Filter3<Object> filterFromItems(Iterable<?> that) throws NullPointerException {
		return filterFromItems(iterableToSet(that));
	}

	/** Diese Methode liefet den {@link Filter} zu {@link Collection#contains(Object)}. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code that.contains(item)}. */
	public static Filter3<Object> filterFromItems(Collection<?> that) throws NullPointerException {
		notNull(that);
		return item -> that.contains(item);
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code item != null}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter3<ITEM> nullFilter() {
		return (Filter3<ITEM>)nullFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert. Die Akzeptanz ist stets {@code true}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter3<ITEM> acceptFilter() {
		return (Filter3<ITEM>)acceptFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze ablehnt. Die Akzeptanz ist stets {@code false}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter3<ITEM> rejectFilter() {
		return (Filter3<ITEM>)rejectFilter;
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die von {@code that} abgelehnt werden. Die Akzeptanz eines Datensatzen
	 * {@code item} ist {@code !that.accepts(item)}. */
	public static <ITEM> Filter3<ITEM> negatedFilter(Filter<? super ITEM> that) throws NullPointerException {
		notNull(that);
		return item -> !that.accepts(item);
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die von {@code that1} oder {@code that2} akzeptiert werden. Die Akzeptanz
	 * eines Datensatzes {@code item} ist {@code that1.accepts(item) || that2.accepts(item)}. */
	public static <ITEM> Filter3<ITEM> disjoinedFilter(Filter<? super ITEM> that1, Filter<? super ITEM> that2) throws NullPointerException {
		notNull(that1);
		notNull(that2);
		return item -> that1.accepts(item) || that2.accepts(item);
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die von {@code that1} und {@code that2} akzeptiert werden. Die Akzeptanz
	 * eines Datensatzes {@code item} ist {@code that1.accepts(item) && that2.accepts(item)}. */
	public static <ITEM> Filter3<ITEM> conjoinedFilter(Filter<? super ITEM> that1, Filter<? super ITEM> that2) throws NullPointerException {
		notNull(that1);
		notNull(that2);
		return item -> that1.accepts(item) && that2.accepts(item);
	}

	/** Diese Methode liefert einen übersetzten {@link Filter3}, der nur die Datensätze akzeptiert, die über {@code trans} umgewandelt von {@code that} akzeptiert
	 * werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code that.accepts(trans.get(item))}.
	 *
	 * @param <T2> Typ der Datensätze des gelieferten {@link Filter3}. */
	public static <T, T2> Filter3<T> translatedFilter(Filter<? super T2> that, Getter<? super T, ? extends T2> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return item -> that.accepts(trans.get(item));
	}

	/** Diese Methode liefert einen {@link Filter3}, der diesen {@link Filter3} mit dem gegebenen Synchronisationsobjekt {@code mutex} über
	 * {@code synchronized(mutex)} synchronisiert. Wenn letzteres {@code null} ist, wird der gelieferte {@link Filter3} verwendet. */
	public static <T> Filter3<T> synchronizedFilter(Filter<? super T> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Filter3<>() {

			@Override
			public boolean accepts(T item) {
				synchronized (notNull(mutex, this)) {
					return that.accepts(item);
				}
			}

		};
	}

	private static final Filter3<?> nullFilter = item -> item != null;

	private static final Filter3<?> acceptFilter = item -> true;

	private static final Filter3<?> rejectFilter = item -> false;

}
