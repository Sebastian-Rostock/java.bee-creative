package bee.creative.util;

import java.util.Map;

/** Diese Schnittstelle definiert eine {@link Map} mit {@link Set2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Map2<K, V> extends Map<K, V> {

	@Override
	Set2<Entry<K, V>> entrySet();

	/** Diese Methode ist eine Abkürzung für {@link Collections#translate(Map, Translator, Translator) Collections#translate(this, keyTrans, valueTrans)}. */
	default <K2, V2> Map3<K2, V2> translate(Translator<K, K2> keyTrans, Translator<V, V2> valueTrans) throws NullPointerException {
		return Collections.translate(this, keyTrans, valueTrans);
	}

}
