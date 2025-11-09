package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Diese Klasse implementiert eine abstrakte {@link Map3} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Map)} geschrieben.
 *
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte.
 * @param <D> Typ des Inhalts. */
public abstract class AbstractProxyMap<K, V, D extends Map<K, V>> implements Map3<K, V> {

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public V get(Object key) {
		return this.getData(true).get(key);
	}

	@Override
	public V put(K key, V value) {
		var data = this.getData(false);
		var result = data.put(key, value);
		this.setData(data);
		return result;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		var data = this.getData(false);
		data.putAll(m);
		this.setData(data);
	}

	@Override
	public boolean containsKey(Object key) {
		return this.getData(true).containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.getData(true).containsValue(value);
	}

	@Override
	public V remove(Object key) {
		var data = this.getData(false);
		var result = data.remove(key);
		this.setData(data);
		return result;
	}

	@Override
	public void clear() {
		var data = this.getData(false);
		data.clear();
		this.setData(data);
	}

	@Override
	public Set2<K> keySet() {
		return new Keys();
	}

	@Override
	public Collection2<V> values() {
		return new Values();
	}

	@Override
	public Set2<Entry<K, V>> entrySet() {
		return new Entries();
	}

	@Override
	public int hashCode() {
		return this.getData(true).hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this.getData(true).equals(object);
	}

	@Override
	public String toString() {
		return this.getData(true).toString();
	}

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #get(Object)},
	 * {@link #containsKey(Object)}, {@link #containsValue(Object)}, {@link #equals(Object)}, {@link #hashCode()} und {@link #toString()} angefordert.
	 *
	 * @param readonly {@code true}, wenn der Inhalt nur zum Lesen verwendet wird und eine Kopie damit nicht nötig ist.<br>
	 *        {@code false}, wenn der Inhalt verändert werden könnte und daher ggf. eine Kopie nötig ist.
	 * @return Inhalt. */
	protected abstract D getData(boolean readonly);

	/** Diese Methode setzt den Inhalt. Dieser wurde zuvor über {@link #getData(boolean)} zum Schreiben beschafft und anschließend verändert.
	 *
	 * @param items neuer Inhalt. */
	protected abstract void setData(D items);

	static abstract class Data<E, D extends Collection<E>> extends AbstractProxyCollection<E, D> {

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void setData(D items) {
		}

	}

	class Keys extends Data<K, KeysData> implements Set2<K> {

		@Override
		protected KeysData getData(boolean readonly) {
			return new KeysData(readonly);
		}

		@Override
		protected void setData(KeysData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class KeysData extends Data<K, Set<K>> implements Set2<K> {

		@Override
		protected Set<K> getData(boolean readonly) {
			return this.data.keySet();
		}

		final D data;

		KeysData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

	}

	class Values extends Data<V, ValuesData> {

		@Override
		protected ValuesData getData(boolean readonly) {
			return new ValuesData(readonly);
		}

		@Override
		protected void setData(ValuesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class ValuesData extends Data<V, Collection<V>> {

		@Override
		protected Collection<V> getData(boolean readonly) {
			return this.data.values();
		}

		final D data;

		ValuesData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

	}

	class Entries extends Data<Entry<K, V>, EntriesData> implements Set2<Entry<K, V>> {

		@Override
		protected EntriesData getData(boolean readonly) {
			return new EntriesData(readonly);
		}

		@Override
		protected void setData(EntriesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

		final class Iter extends AbstractIterator<Entry<K, V>> {
		
			@Override
			public boolean hasNext() {
				return this.iter.hasNext();
			}

			@Override
			public Entry<K, V> next() {
				return new Next(this.iter.next());
			}

			@Override
			public void remove() {
				this.iter.remove();
				AbstractProxyMap.this.setData(this.data.data);
			}

			final EntriesData data;
		
			final Iterator<Entry<K, V>> iter;
		
			Iter() {
				this.data = new EntriesData(false);
				this.iter = this.data.iterator();
			}
		
			final class Next extends AbstractEntry3<K, V> {
			
				final Entry<K, V> entry;
			
				public Next(Entry<K, V> entry) {
					this.entry = entry;
				}
			
				@Override
				public K getKey() {
					return this.entry.getKey();
				}
			
				@Override
				public V getValue() {
					return this.entry.getValue();
				}
			
				@Override
				public Entry3<K, V> useValue(V value) {
					this.entry.setValue(value);
					AbstractProxyMap.this.setData(Iter.this.data.data);
					return this;
				}
			
			}
		
		}

	}

	class EntriesData extends Data<Entry<K, V>, Set<Entry<K, V>>> implements Set2<Entry<K, V>> {

		@Override
		protected Set<Entry<K, V>> getData(boolean readonly) {
			return this.data.entrySet();
		}

		final D data;

		EntriesData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

	}

}