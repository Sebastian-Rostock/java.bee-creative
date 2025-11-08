package bee.creative.util;

/** Diese Schnittstelle definiert eine {@link Map2} mit {@link Set2} und {@link Collection2}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Map3<K, V> extends Map2<K, V>, Getter2<Object, V>, Setter2<K, V>, Filter2<Object> {

	@Override
	Set2<K> keySet();

	@Override
	Collection2<V> values();

	/** Diese Methode liefert die {@link Filter3}-Schnittstelle zu {@link #containsKey(Object)}. */
	@Override
	default Filter3<Object> asFilter() {
		return this::containsKey;
	}

	/** Diese Methode liefert die {@link Getter3}-Schnittstelle zu {@link #get(Object)}. */
	@Override
	default Getter3<Object, V> asGetter() {
		return this::get;
	}

	/** Diese Methode liefert die {@link Setter3}-Schnittstelle zu {@link #put(Object, Object)}. */
	@Override
	default Setter3<K, V> asSetter() {
		return this::put;
	}

}
