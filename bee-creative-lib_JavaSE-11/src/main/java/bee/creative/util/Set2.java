package bee.creative.util;

import java.util.Map.Entry;
import java.util.Set;

/** Diese Schnittstelle definiert ein {@link Set} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Set2<E> extends Set<E>, Collection2<E> {

	@Override
	Iterator2<E> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Collections#unionSet(Set, Set) Collections.union(this, that)}. */
	default Set2<E> union(Set<? extends E> that) throws NullPointerException {
		return Collections.unionSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#exceptSet(Set, Set) Collections.except(this, that)}. */
	default Set2<E> except(Set<? extends E> that) throws NullPointerException {
		return Collections.exceptSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#intersectSet(Set, Set) Collections.intersect(this, that)}. */
	default Set2<E> intersect(Set<? extends E> that) throws NullPointerException {
		return Collections.intersectSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#cartesianSet(Set, Set) Collections.cartesian(this, that)}. */
	default <E2> Set2<Entry<E, E2>> cartesian(Set<? extends E2> that) throws NullPointerException {
		return Collections.cartesianSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translatedSet(Set, Translator) Collections.translate(this, trans)}. */
	@Override
	default <E2> Set2<E2> translate(Translator<E, E2> trans) throws NullPointerException {
		return Collections.translatedSet(this, trans);
	}

}
