package bee.creative.util;

/** Diese Schnittstelle definiert eine {@link Map2} mit {@link Set2} und {@link Collection2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Map3<K, V> extends Map2<K, V> {

	@Override
	Set2<K> keySet();

	@Override
	Collection2<V> values();

}
