package bee.creative.util;

import static bee.creative.util.Comparators.concatComparator;
import static bee.creative.util.Comparators.iterableComparator;
import static bee.creative.util.Comparators.optionalizedComparator;
import static bee.creative.util.Comparators.reversedComparator;
import static bee.creative.util.Comparators.translatedComparator;
import java.util.Comparator;

/** Diese Schnittstelle ergänzt einen {@link Comparator} insb. um eine Anbindung an Methoden von {@link Comparators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Eingabe */
public interface Comparator3<T> extends Comparator<T> {

	/** Diese Methode ist eine Abkürzung für {@link Comparators#concatComparator(Comparator, Comparator) concatComparator(this, that)}. */
	default Comparator3<T> concat(Comparator<? super T> that) throws NullPointerException {
		return concatComparator(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#reversedComparator(Comparator) reversedComparator(this)}. */
	default Comparator3<T> reverse() {
		return reversedComparator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#iterableComparator(Comparator) iterableComparator(this)}. */
	default Comparator3<Iterable<? extends T>> iterable() {
		return iterableComparator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translatedComparator(Comparator, Getter) translatedComparator(this, trans)}. */
	default <T2> Comparator3<T2> translate(Getter<? super T2, ? extends T> trans) throws NullPointerException {
		return translatedComparator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#optionalizedComparator(Comparator) optionalizedComparator(this)}. */
	default Comparator3<T> optionalize() {
		return optionalizedComparator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#optionalizedComparator(Comparator, boolean) optionalizedComparator(this, first)}. */
	default Comparator3<T> optionalize(boolean first) {
		return optionalizedComparator(this, first);
	}

}
