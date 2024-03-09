package bee.creative.util;

import java.util.Map;

/** Diese Schnittstelle definiert eine {@link Map} mit {@link Set2} und als {@link Field}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Map2<K, V> extends Map<K, V>, Getter<Object, V>, Setter<K, V> {

	@Override
	default void set(K item, V value) {
		this.put(item, value);
	}

	@Override
	Set2<Entry<K, V>> entrySet();

	@Override
	default void putAll(Map<? extends K, ? extends V> entries) {
		entries.forEach(this::put);
	}

	/** Diese Methode fügt die aus den gegebenen Elementen abgeleiteten Schlüssel-Wert-Paare hinzu.
	 *
	 * @param <E> Typ der Elemente.
	 * @param items Elemente.
	 * @param asKey Funktion zur Ableitung des Schlüssels.
	 * @param asValue Funktion zur Ableitung des Werts. */
	default <E> void putAll(Iterable<? extends E> items, Getter<? super E, ? extends K> asKey, Getter<? super E, ? extends V> asValue) {
		items.forEach(item -> this.put(asKey.get(item), asValue.get(item)));
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translate(Map, Translator, Translator) Collections#translate(this, keyTrans, valueTrans)}. */
	default <K2, V2> Map3<K2, V2> translate(Translator<K, K2> keyTrans, Translator<V, V2> valueTrans) throws NullPointerException {
		return Collections.translate(this, keyTrans, valueTrans);
	}

}
