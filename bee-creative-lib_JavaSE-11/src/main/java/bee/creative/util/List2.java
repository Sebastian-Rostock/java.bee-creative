package bee.creative.util;

import java.util.List;

/** Diese Schnittstelle definiert eine {@link List} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface List2<E> extends List<E>, Collection2<E> {

	@Override
	Iterator2<E> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Collections#reverse(List) Collections.reverse(this)}. */
	default List2<E> reverse(final List<E> items) throws NullPointerException {
		return Collections.reverse(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(List, List, boolean) Collections.concat(this, that, true)}. */
	default List2<E> concat(List<E> that) throws NullPointerException {
		return Collections.concat(this, that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(List, List, boolean) Collections.concat(this, that, extendMode)}. */
	default List2<E> concat(List<E> that, boolean extendMode) throws NullPointerException {
		return Collections.concat(this, that, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translate(List, Translator) Collections#translate(this, trans)}. */
	@Override
	default <E2> List2<E2> translate(Translator<E, E2> trans) throws NullPointerException {
		return Collections.translate(this, trans);
	}

}
