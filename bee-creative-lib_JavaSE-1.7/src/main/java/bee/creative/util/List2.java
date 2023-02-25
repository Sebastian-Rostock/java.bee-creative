package bee.creative.util;

import java.util.List;

/** Diese Schnittstelle definiert eine {@link List} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface List2<E> extends List<E>, Collection2<E> {

	@Override
	Iterator2<E> iterator();

}
