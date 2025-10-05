package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter3#accepts(Object)} akzeptieren oder ablehnen kann.
 *
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Datensätze. */
public interface Filter3<T> extends Filter2<T> {

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Filter3<T> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) this.toGetter().buffer(mode, hasher)::get}. */
	default Filter3<T> buffer(int mode, Hasher hasher) throws IllegalArgumentException {
		return this.toGetter().buffer(mode, hasher)::get;
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die von diesem {@link Filter3} abgelehnt werden. Die Akzeptanz eines
	 * Datensatzen {@code item} ist {@code !this.accept(item)}. */
	default Filter3<T> negate() {
		return Filters.negatedFilter(this);
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die von diesem oder dem gegebenen {@link Filter3} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) || that.accept(item)}. */
	default Filter3<T> disjoin(Filter3<? super T> that) throws NullPointerException {
		notNull(that);
		return item -> this.accepts(item) || that.accepts(item);
	}

	/** Diese Methode liefert einen {@link Filter3}, der nur die Datensätze akzeptiert, die diesem und dem gegebenen {@link Filter3} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) && that.accept(item)}. */
	default Filter3<T> conjoin(Filter3<? super T> that) throws NullPointerException {
		notNull(that);
		return item -> this.accepts(item) && that.accepts(item);
	}

	/** Diese Methode liefert einen übersetzten {@link Filter3}, der nur die Datensätze akzeptiert, die über den gegebenen {@link Getter} umgewandelt von diesem
	 * {@link Filter3} akzeptiert werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(trans.get(item))}.
	 *
	 * @param <T2> Typ der Datensätze des gelieferten {@link Filter3}. */
	default <T2> Filter3<T2> translate(Getter<? super T2, ? extends T> trans) throws NullPointerException {
		notNull(this);
		notNull(trans);
		return item -> this.accepts(trans.get(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Filter3<T> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode liefert einen {@link Filter3}, der diesen {@link Filter3} mit dem gegebenen Synchronisationsobjekt {@code mutex} über
	 * {@code synchronized(mutex)} synchronisiert. Wenn letzteres {@code null} ist, wird der gelieferte {@link Filter3} verwendet. */
	default Filter3<T> synchronize(Object mutex) {
		return new Filter3<>() {

			@Override
			public boolean accepts(T item) {
				synchronized (notNull(mutex, this)) {
					return Filter3.this.accepts(item);
				}
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #conjoin(Filter3) conjoin(that)}. */
	default Filter3<T> and(Filter3<? super T> that) throws NullPointerException {
		return this.conjoin(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #negate()}. */
	default Filter3<T> not() {
		return this.negate();
	}

	/** Diese Methode ist eine Abkürzung für {@link #disjoin(Filter3) disjoin(that)}. */
	default Filter3<T> or(Filter3<? super T> that) throws NullPointerException {
		return this.disjoin(that);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #accepts(Object)}. */
	default Getter3<T, Boolean> toGetter() {
		return this::accepts;
	}

	@Override
	default Filter3<T> asFilter() {
		return this;
	}

}
