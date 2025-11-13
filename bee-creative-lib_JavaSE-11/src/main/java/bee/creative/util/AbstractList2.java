package bee.creative.util;

import java.util.AbstractList;

/** Diese Klasse implementiert eine {@link AbstractList} als {@link List2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class AbstractList2<E> extends AbstractList<E> implements List2<E> {

	@Override
	public Iterator3<E> iterator() {
		return Iterators.iteratorFrom(super.iterator());
	}

}
