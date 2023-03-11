package bee.creative.util;

import java.util.Map;

/** Diese Schnittstelle definiert eine {@link Map} mit {@link Set2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Map2<K, V> extends Map<K, V> {

	@Override
	Set2<Entry<K, V>> entrySet();

}
