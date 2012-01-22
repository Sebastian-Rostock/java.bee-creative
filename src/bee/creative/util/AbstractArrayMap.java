package bee.creative.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AbstractArrayMap<K, V> implements Map<K, V> {

	// - 

	// /**
	// * IDEE:
	// * <p>
	// * - nach hash eines GKey sortiertes array von GEntry
	// * - binäre suche
	// * - lineare suche ab treffer in beide richtungen mit hash und equals filter
	// * - gentry=gkey + gvalue array || gentry.gkey+gentry.gvalue ==> resize/insert event mit size/index

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<K> keySet() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}

}
