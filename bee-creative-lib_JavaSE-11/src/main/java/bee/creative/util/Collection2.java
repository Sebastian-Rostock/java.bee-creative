package bee.creative.util;

import java.util.Collection;

/** Diese Schnittstelle definiert eine {@link Collection} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Collection2<E> extends Collection<E> {

	@Override
	Iterator2<E> iterator();

}
