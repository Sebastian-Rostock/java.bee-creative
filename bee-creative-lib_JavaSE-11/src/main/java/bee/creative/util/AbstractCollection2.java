package bee.creative.util;

import java.util.AbstractCollection;

/** Diese Klasse implementiert eine {@link AbstractCollection} als {@link Collection2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class AbstractCollection2<E> extends AbstractCollection<E> implements Collection2<E> {

	@Override
	public abstract Iterator3<E> iterator();

}
