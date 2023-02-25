package bee.creative.util;

import java.util.Set;

/** Diese Schnittstelle definiert ein {@link Set} mit {@link Iterator2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Set2<E> extends Set<E>, Collection2<E> {

	@Override
	Iterator2<E> iterator();

}
