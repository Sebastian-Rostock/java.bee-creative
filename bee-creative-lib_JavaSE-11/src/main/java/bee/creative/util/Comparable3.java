package bee.creative.util;

import static bee.creative.util.Comparables.comparableToEqFilter;
import static bee.creative.util.Comparables.comparableToGtEqFilter;
import static bee.creative.util.Comparables.comparableToGtFilter;
import static bee.creative.util.Comparables.comparableToLtEqFilter;
import static bee.creative.util.Comparables.comparableToLtFilter;
import static bee.creative.util.Comparables.concatComparable;
import static bee.creative.util.Comparables.optionalizedComparable;
import static bee.creative.util.Comparables.reversedComparable;
import static bee.creative.util.Comparables.translatedComparable;

/** Diese Schnittstelle ergänzt ein {@link Comparable} insb. um eine Anbindung an Methoden von {@link Comparables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Eingabe. */
public interface Comparable3<T> extends Comparable2<T> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#concatComparable(Comparable, Comparable) concatComparable(this, that)}. */
	default Comparable3<T> concat(Comparable<? super T> that) throws NullPointerException {
		return concatComparable(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#reversedComparable(Comparable) reversedComparable(this)}. */
	default Comparable3<T> reverse() {
		return reversedComparable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#optionalizedComparable(Comparable) reversedComparable(this)}. */
	default Comparable3<T> optionalize() {
		return optionalizedComparable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translatedComparable(Comparable, Getter) translatedComparable(this, trans)}. */
	default <GItem2> Comparable3<GItem2> translate(Getter<? super GItem2, ? extends T> trans) throws NullPointerException {
		return translatedComparable(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableToEqFilter(Comparable) comparableToEqFilter(this)}. */
	default Filter3<T> toEqFilter() throws NullPointerException {
		return comparableToEqFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableToLtFilter(Comparable) comparableToLtFilter(this)}. */
	default Filter3<T> toLtFilter() throws NullPointerException {
		return comparableToLtFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableToLtEqFilter(Comparable) comparableToLtEqFilter(this)}. */
	default Filter3<T> toLtEqFilter() throws NullPointerException {
		return comparableToLtEqFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableToGtFilter(Comparable) comparableToGtFilter(this)}. */
	default Filter3<T> toGtFilter() throws NullPointerException {
		return comparableToGtFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#comparableToGtEqFilter(Comparable) comparableToGtEqFilter(this)}. */
	default Filter3<T> toGtEqFilter() throws NullPointerException {
		return comparableToGtEqFilter(this);
	}

	@Override
	default Comparable3<T> asComparable() {
		return this;
	}

}
