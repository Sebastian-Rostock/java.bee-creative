package bee.creative.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hashes {

	
	public static void main(String[] args) {
		StringBuilder d;
	}

	
	static void test() {
		Hashes.test(new Runnable() {

			@Override
			public void run() {
			}

		});
	}

	static void test(final Runnable runnable) {
		System.out.println(new Test(runnable));
	}

	public static void main_(final String[] args) {
		final Map<Object, Object> map1 = new HashMap<Object, Object>(0);
		final HashMap2<Object, Object> map2 = new HashMap2<Object, Object>();
		final int count = 1024 * 1024 * 1;
		final List<Object> keys = new ArrayList<Object>(count);
		Hashes.test();
		System.out.println("make values");
		Hashes.test(new Runnable() {

			@Override
			public void run() {
				for(int i = 0; i < count; i++){
					keys.add(new Integer(i));
				}
			}

		});
		map2.setCapa(count);
		System.out.println("custom");
		Hashes.test(new Runnable() {

			@Override
			public void run() {
				for(Object key: keys){
					map2.put(key, null);
				}
			}
		});
		Hashes.test(new Runnable() {

			@Override
			public void run() {
				for(Object key: keys){
					map2.remove(key);
				}
			}
		});
		System.out.println("default");
		Hashes.test(new Runnable() {

			@Override
			public void run() {
				for(Object key: keys){
					map1.put(key, null);
				}
			}

		});
		Hashes.test(new Runnable() {

			@Override
			public void run() {
				for(Object key: keys){
					map1.remove(key);
				}
			}

		});
	}

	static class HashMap2<K, V> extends Hash<Object, V, HashMap2.Entry2<Object, V>> {

		static class Entry2<K, V> {

			K key;

			V value;

			Entry2<K, V> next;

			int hash;

		}

		public void setCapa(int c){
			verifyLength(getLength(c, getLength()));
		}
		
		public V put(final Object key, final V value) {
			final Entry2<Object, V> entry = this.appendEntry(key, value, false);
			if(entry == null) return null;
			return entry.value;
		}

		public V remove(final Object key) {
			final Entry2<Object, V> entry = this.removeEntry(key, true);
			if(entry == null) return null;
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object getEntryKey(final Entry2<Object, V> entry) {
			return entry.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final Entry2<Object, V> entry, final Object key, final int hash) {
			return (entry.hash == hash) && Objects.equals(entry.key, key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry2<Object, V> getEntryNext(final Entry2<Object, V> entry) {
			return entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final Entry2<Object, V> entry, final Entry2<Object, V> next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected V getEntryValue(final Entry2<Object, V> entry) {
			return entry.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry2<Object, V> createEntry(final Object key, final V value, final Entry2<Object, V> next,
			final int hash) {
			final Entry2<Object, V> entry = new Entry2<Object, V>();
			entry.key = key;
			entry.value = value;
			entry.next = next;
			entry.hash = hash;
			return entry;
		}

	}

}
