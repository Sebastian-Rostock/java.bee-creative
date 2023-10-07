package bee.creative.util;

import java.util.Collection;

/** Diese Schnittstelle definiert eine {@link Collection} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Collection2<E> extends Collection<E> {

	@Override
	Iterator2<E> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(Collection, Collection, boolean) Collections.concat(this, that, true)}. */
	default Collection2<E> concat(Collection<E> that) throws NullPointerException {
		return Collections.concat(this, that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(Collection, Collection, boolean) Collections.concat(this, that, extendMode)}. */
	default Collection2<E> concat(Collection<E> that, boolean extendMode) throws NullPointerException {
		return Collections.concat(this, that, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translate(Collection, Translator) Collections#translate(this, trans)}. */
	default <E2> Collection2<E2> translate(Translator<E, E2> trans) throws NullPointerException {
		return Collections.translate(this, trans);
	}

}
