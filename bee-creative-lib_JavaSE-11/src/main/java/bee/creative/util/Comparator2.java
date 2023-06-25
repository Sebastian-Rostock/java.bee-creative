package bee.creative.util;

import java.util.Comparator;

/** Diese Schnittstelle ergänzt einen {@link Comparator} insb. um eine Anbindung an Methoden von {@link Comparators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe */
public interface Comparator2<GItem> extends Comparator<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Comparators#concat(Comparator, Comparator) Comparators.concat(this, that)}. */
	default Comparator2<GItem> concat(Comparator<? super GItem> that) throws NullPointerException {
		return Comparators.concat(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#reverse(Comparator) Comparators.reverse(this)}. */
	default Comparator2<GItem> reverse() {
		return Comparators.reverse(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#iterable(Comparator) Comparators.iterable(this)}. */
	default Comparator2<Iterable<? extends GItem>> iterable() {
		return Comparators.iterable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#optionalize(Comparator) Comparators.optionalize(this)}. */
	default Comparator2<GItem> optionalize() {
		return Comparators.optionalize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translate(Comparator, Getter) Comparators.translate(this, trans)}. */
	default <GItem2> Comparator2<GItem2> translate(Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Comparators.translate(this, trans);
	}

}
