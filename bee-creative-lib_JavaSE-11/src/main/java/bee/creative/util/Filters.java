package bee.creative.util;

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
	public static <ITEM> Filter2<ITEM> filterFrom(boolean value) {
		return value ? acceptFilter() : rejectFilter();
	}

	/** Diese Methode liefert den gegebenen {@link Filter}, sofern dieser nicht {@code null} ist. Anernfalls liefet sie {@link #emptyFilter()}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> filterFrom(Filter<? super ITEM> that) {
		return that == null ? emptyFilter() : (Filter2<ITEM>)that.asFilter();
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer als die des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) < 0}. */
	public static <ITEM> Filter2<ITEM> filterFromEqual(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) == 0;
	}

	/** Diese Klasse liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung kleiner als die des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) > 0}. */
	public static <ITEM> Filter2<ITEM> filterFromLower(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) > 0;
	}

	/** Diese Klasse implementiert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer der eines gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that.compareTo(item) < 0}.
	 *
	 * @param <ITEM> Typ der Datensätze. */
	public static <ITEM> Filter2<ITEM> filterFromHigher(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) < 0;
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableFromArray(items))}.
	 *
	 * @see Iterables#iterableFromArray(Object...) */
	public static Filter2<Object> filterFromItems(Object... items) throws NullPointerException {
		return filterFromItems(iterableFromArray(items));
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableToSet(that))}.
	 *
	 * @see Iterables#iterableToSet(Iterable) */
	public static Filter2<Object> filterFromItems(Iterable<?> that) throws NullPointerException {
		return filterFromItems(iterableToSet(that));
	}

	/** Diese Methode liefet den {@link Filter} zu {@link Collection#contains(Object)}. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code that.contains(item)}. */
	public static Filter2<Object> filterFromItems(Collection<?> that) throws NullPointerException {
		Objects.notNull(that);
		return that::contains;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code item != null}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> emptyFilter() {
		return (Filter2<ITEM>)emptyFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert. Die Akzeptanz ist stets {@code true}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> acceptFilter() {
		return (Filter2<ITEM>)acceptFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze ablehnt. Die Akzeptanz ist stets {@code false}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> rejectFilter() {
		return (Filter2<ITEM>)rejectFilter;
	}

	static final Filter2<?> emptyFilter = item -> item != null;

	static final Filter2<?> acceptFilter = item -> true;

	static final Filter2<?> rejectFilter = item -> false;

	static class SynchronizedFilter<ITEM> implements Filter2<ITEM> {

		public SynchronizedFilter(Filter<? super ITEM> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean accept(ITEM item) {
			synchronized (this.mutex) {
				return this.that.accept(item);
			}
		}

		private final Filter<? super ITEM> that;

		private final Object mutex;

	}

}
