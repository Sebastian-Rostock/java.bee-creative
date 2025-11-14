package bee.creative.util;

import static bee.creative.util.Collections.concatList;
import static bee.creative.util.Collections.reversedList;
import static bee.creative.util.Collections.translatedList;
import java.util.List;

/** Diese Schnittstelle definiert eine {@link List} mit {@link Iterator3}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface List2<E> extends List<E>, Collection2<E> {

	/** Diese Methode ist eine Abkürzung für {@link #asConcatList(List, boolean) this.asConcatList(that, true)}. */
	default List2<E> asConcatList(List<E> that) throws NullPointerException {
		return this.asConcatList(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concatList(List, List, boolean) concatList(this, that, extendMode)}. */
	default List2<E> asConcatList(List<E> that, boolean extendMode) throws NullPointerException {
		return concatList(this, that, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#reversedList(List) reversedList(this)}. */
	default List2<E> asReversedList() throws NullPointerException {
		return reversedList(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translatedList(List, Translator) translatedList(this, trans)}. */
	default <E2> List2<E2> asTranslatedList(Translator<E, E2> trans) throws NullPointerException {
		return translatedList(this, trans);
	}

	@Override
	Iterator3<E> iterator();

}
