package bee.creative.util;

import java.util.Comparator;

/** Diese Schnittstelle ergänzt einen {@link Comparator} insb. um eine Anbindung an Methoden von {@link Comparators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe */
public interface Comparator2<GItem> extends Comparator<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Comparators#concat(Comparator, Comparator) Comparators.concat(this, that)}. */
	public Comparator2<GItem> concat(Comparator<? super GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Comparators#iterable(Comparator) Comparators.iterable(this)}. */
	public Comparator2<Iterable<? extends GItem>> iterable();

	/** Diese Methode ist eine Abkürzung für {@link Comparators#optionalize(Comparator) Comparators.optionalize(this)}. */
	public Comparator2<GItem> optionalize();

	/** Diese Methode ist eine Abkürzung für {@link Comparators#reverse(Comparator) Comparators.reverse(this)}. */
	public Comparator2<GItem> reverse();

	/** Diese Methode ist eine Abkürzung für {@link Comparators#translate(Comparator, Getter) Comparators.translate(this, trans)}. */
	public <GItem2> Comparator<GItem2> translate(Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException;

}
