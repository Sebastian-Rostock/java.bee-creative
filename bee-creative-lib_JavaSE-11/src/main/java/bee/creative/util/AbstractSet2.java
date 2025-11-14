package bee.creative.util;

import java.util.AbstractSet;

/** Diese Klasse implementiert ein {@link AbstractSet} als {@link Set2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class AbstractSet2<E> extends AbstractSet<E> implements Set2<E> {

	@Override
	public abstract Iterator3<E> iterator();

}
