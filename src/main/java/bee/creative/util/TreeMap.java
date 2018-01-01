package bee.creative.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import bee.creative.util._TreeData_.TreeData;

// TODO
class TreeMap<GKey, GValue> extends TreeData<GKey, GValue> implements Map<GKey, GValue> {

	public static void main(String[] args) throws Exception {
		TreeMap map = new TreeMap(10);
		map.print();
		map.put("a", "1");
		map.print();
		map.put("d", "2");
		map.print();
		map.put("b", "3");
		map.print();
		map.put("c", "4");
		map.print();
		map.put("e", "5");
		map.print();
 
		System.out.println(map.get("a"));
		System.out.println(map.get("b"));
		System.out.println(map.get("c"));
		System.out.println(map.get("d"));
		System.out.println(map.get("e"));
		System.out.println(map.capacityImpl());
	}
	
	
	
	public TreeMap() {
		super(true);
	}

	public TreeMap(int capacity) {
		super(true);
		allocateImpl(capacity);
	}
	
	public static <GKey, GValue> java.util.TreeMap<GKey, GValue> from(final Filter<Object> filter, final Comparator<? super GKey> comparator) {
		return new java.util.TreeMap(comparator);
	}

	@Override
	public int size() {
		return countImpl();
	}

	@Override
	public boolean isEmpty() {
		return countImpl() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return hasKeyImpl(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return hasValueImpl(value);
	}

	@Override
	public GValue get(Object key) {
		return getImpl(key);
	}

	@Override
	public GValue put(GKey key, GValue value) {
		return putImpl(key, value);
	}

	@Override
	public GValue remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends GKey, ? extends GValue> m) {
	}

	@Override
	public void clear() {
		clearImpl();
	}

	@Override
	public Set<GKey> keySet() {
		return  null;
	}

	@Override
	public Collection<GValue> values() {
		return null;
	}

	@Override
	public Set< Entry<GKey, GValue>> entrySet() {
		return null;
	}

}
