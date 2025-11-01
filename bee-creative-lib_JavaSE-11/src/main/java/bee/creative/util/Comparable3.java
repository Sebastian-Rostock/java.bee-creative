package bee.creative.util;

import static bee.creative.util.Comparables.comparableAsEqFilter;
import static bee.creative.util.Comparables.comparableAsGtEqFilter;
import static bee.creative.util.Comparables.comparableAsGtFilter;
import static bee.creative.util.Comparables.comparableAsLtEqFilter;
import static bee.creative.util.Comparables.comparableAsLtFilter;
import static bee.creative.util.Comparables.concatComparable;
import static bee.creative.util.Comparables.optionalizedComparable;
import static bee.creative.util.Comparables.reversedComparable;
import static bee.creative.util.Comparables.translatedComparable;

/** Diese Schnittstelle ergänzt ein {@link Comparable} insb. um eine Anbindung an Methoden von {@link Comparables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Eingabe. */
public interface Comparable3<T> extends Comparable<T> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#concatComparable(Comparable, Comparable) concatComparable(this, that)}. */
	default Comparable3<T> concat(Comparable<? super T> that) throws NullPointerException {
		return concatComparable(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#reversedComparable(Comparable) reversedComparable(this)}. */
	default Comparable3<T> reverse() {
		return reversedComparable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translatedComparable(Comparable, Getter) translatedComparable(this, trans)}. */
	default <T2> Comparable3<T2> translate(Getter<? super T2, ? extends T> trans) throws NullPointerException {
		return translatedComparable(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#optionalizedComparable(Comparable) reversedComparable(this)}. */
	default Comparable3<T> optionalize() {
		return optionalizedComparable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#optionalizedComparable(Comparable, boolean) reversedComparable(this, first)}. */
	default Comparable3<T> optionalize(boolean first) {
		return optionalizedComparable(this, first);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableAsEqFilter(Comparable) comparableAsEqFilter(this)}. */
	default Filter3<T> asEqFilter() throws NullPointerException {
		return comparableAsEqFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableAsLtFilter(Comparable) comparableAsLtFilter(this)}. */
	default Filter3<T> asLtFilter() throws NullPointerException {
		return comparableAsLtFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableAsLtEqFilter(Comparable) comparableAsLtEqFilter(this)}. */
	default Filter3<T> asLtEqFilter() throws NullPointerException {
		return comparableAsLtEqFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableAsGtFilter(Comparable) comparableAsGtFilter(this)}. */
	default Filter3<T> asGtFilter() throws NullPointerException {
		return comparableAsGtFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableAsGtEqFilter(Comparable) comparableAsGtEqFilter(this)}. */
	default Filter3<T> asGtEqFilter() throws NullPointerException {
		return comparableAsGtEqFilter(this);
	}

}
