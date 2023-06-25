package bee.creative.util;

/** Diese Schnittstelle ergänzt ein {@link Comparable} insb. um eine Anbindung an Methoden von {@link Comparables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public interface Comparable2<GItem> extends Comparable<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#concat(Comparable, Comparable) Comparables.concat(this, that)}. */
	default Comparable2<GItem> concat(final Comparable<? super GItem> that) throws NullPointerException {
		return Comparables.concat(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#reverse(Comparable) Comparables.reverse(this)}. */
	default Comparable2<GItem> reverse() {
		return Comparables.reverse(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#optionalize(Comparable) Comparables.optionalize(this)}. */
	default Comparable2<GItem> optionalize() {
		return Comparables.optionalize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translate(Comparable, Getter) Comparables.translate(this, trans)}. */
	default <GItem2> Comparable2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Comparables.translate(this, trans);
	}

}
