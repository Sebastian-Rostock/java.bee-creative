package bee.creative.util;

/** Diese Schnittstelle ergänzt ein {@link Comparable} insb. um eine Anbindung an Methoden von {@link Comparables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public interface Comparable2<GItem> extends Comparable<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Comparables#concat(Comparable, Comparable) Comparables.concat(this, that)}. */
	public Comparable2<GItem> concat(Comparable<? super GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Comparables#optionalize(Comparable) Comparables.optionalize(this)}. */
	public Comparable2<GItem> optionalize();

	/** Diese Methode ist eine Abkürzung für {@link Comparables#reverse(Comparable) Comparables.reverse(this)}. */
	public Comparable2<GItem> reverse();

	/** Diese Methode ist eine Abkürzung für {@link Comparables#translate(Comparable, Getter) Comparables.translate(this, trans)}. */
	public <GItem2> Comparable2<GItem2> translate(Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException;

}
