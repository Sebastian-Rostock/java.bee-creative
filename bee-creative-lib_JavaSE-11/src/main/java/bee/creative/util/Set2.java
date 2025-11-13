package bee.creative.util;

import static bee.creative.util.Collections.cartesianSet;
import static bee.creative.util.Collections.exceptSet;
import static bee.creative.util.Collections.intersectSet;
import static bee.creative.util.Collections.translatedSet;
import static bee.creative.util.Collections.unionSet;
import java.util.Map.Entry;
import java.util.Set;

/** Diese Schnittstelle definiert ein {@link Set} mit {@link Iterator3}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Set2<E> extends Set<E>, Collection2<E> {

	/** Diese Methode ist eine Abkürzung für {@link Collections#unionSet(Set, Set) unionSet(this, that)}. */
	default Set2<E> asUnionSet(Set<? extends E> that) throws NullPointerException {
		return unionSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#exceptSet(Set, Set) exceptSet(this, that)}. */
	default Set2<E> asExceptSet(Set<? extends E> that) throws NullPointerException {
		return exceptSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#intersectSet(Set, Set) intersectSet(this, that)}. */
	default Set2<E> asIntersectSet(Set<? extends E> that) throws NullPointerException {
		return intersectSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#cartesianSet(Set, Set) cartesianSet(this, that)}. */
	default <E2> Set2<Entry<E, E2>> asCartesianSet(Set<? extends E2> that) throws NullPointerException {
		return cartesianSet(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translatedSet(Set, Translator) translatedSet(this, trans)}. */
	default <E2> Set2<E2> asTranslatedSet(Translator<E, E2> trans) throws NullPointerException {
		return translatedSet(this, trans);
	}

	@Override
	Iterator3<E> iterator();

}
