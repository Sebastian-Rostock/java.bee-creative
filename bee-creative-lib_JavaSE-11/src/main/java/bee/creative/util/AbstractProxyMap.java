package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Diese Klasse implementiert eine abstrakte {@link Map3} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Map)} geschrieben.
 *
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxyMap<GKey, GValue, GData extends Map<GKey, GValue>> implements Map3<GKey, GValue> {

	static abstract class Data<GItem, GData extends Collection<GItem>> extends AbstractProxyCollection<GItem, GData> {

		@Override
		protected void setData(GData items) {
		}

		@Override
		public boolean add(GItem e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends GItem> c) {
			throw new UnsupportedOperationException();
		}

	}

	class Keys extends Data<GKey, KeysData> implements Set2<GKey> {

		@Override
		protected KeysData getData(boolean readonly) {
			return new KeysData(readonly);
		}

		@Override
		protected void setData(KeysData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class KeysData extends Data<GKey, Set<GKey>> implements Set2<GKey> {

		final GData data;

		KeysData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Set<GKey> getData(boolean readonly) {
			return this.data.keySet();
		}

	}

	class Values extends Data<GValue, ValuesData> {

		@Override
		protected ValuesData getData(boolean readonly) {
			return new ValuesData(readonly);
		}

		@Override
		protected void setData(ValuesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class ValuesData extends Data<GValue, Collection<GValue>> {

		final GData data;

		ValuesData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Collection<GValue> getData(boolean readonly) {
			return this.data.values();
		}

	}

	class Entries extends Data<Entry<GKey, GValue>, EntriesData> implements Set2<Entry<GKey, GValue>> {

		final class Iter extends AbstractIterator<Entry<GKey, GValue>> {

			final class Next extends AbstractEntry<GKey, GValue> {

				final Entry<GKey, GValue> entry;

				public Next(Entry<GKey, GValue> entry) {
					this.entry = entry;
				}

				@Override
				public GKey getKey() {
					return this.entry.getKey();
				}

				@Override
				public GValue getValue() {
					return this.entry.getValue();
				}

				@Override
				public Entry2<GKey, GValue> useValue(GValue value) {
					this.entry.setValue(value);
					AbstractProxyMap.this.setData(Iter.this.data.data);
					return this;
				}

			}

			final EntriesData data;

			final Iterator<Entry<GKey, GValue>> iter;

			Iter() {
				this.data = new EntriesData(false);
				this.iter = this.data.iterator();
			}

			@Override
			public boolean hasNext() {
				return this.iter.hasNext();
			}

			@Override
			public Entry<GKey, GValue> next() {
				return new Next(this.iter.next());
			}

			@Override
			public void remove() {
				this.iter.remove();
				AbstractProxyMap.this.setData(this.data.data);
			}

		}

		@Override
		protected EntriesData getData(boolean readonly) {
			return new EntriesData(readonly);
		}

		@Override
		protected void setData(EntriesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class EntriesData extends Data<Entry<GKey, GValue>, Set<Entry<GKey, GValue>>> implements Set2<Entry<GKey, GValue>> {

		final GData data;

		EntriesData(boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Set<Entry<GKey, GValue>> getData(boolean readonly) {
			return this.data.entrySet();
		}

	}

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #get(Object)},
	 * {@link #containsKey(Object)}, {@link #containsValue(Object)}, {@link #equals(Object)}, {@link #hashCode()} und {@link #toString()} angefordert.
	 *
	 * @param readonly {@code true}, wenn der Inhalt nur zum Lesen verwendet wird und eine Kopie damit nicht nötig ist.<br>
	 *        {@code false}, wenn der Inhalt verändert werden könnte und daher ggf. eine Kopie nötig ist.
	 * @return Inhalt. */
	protected abstract GData getData(boolean readonly);

	/** Diese Methode setzt den Inhalt. Dieser wurde zuvor über {@link #getData(boolean)} zum Schreiben beschafft und anschließend verändert.
	 *
	 * @param items neuer Inhalt. */
	protected abstract void setData(GData items);

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public GValue get(Object key) {
		return this.getData(true).get(key);
	}

	@Override
	public GValue put(GKey key, GValue value) {
		final var data = this.getData(false);
		final var result = data.put(key, value);
		this.setData(data);
		return result;
	}

	@Override
	public void putAll(Map<? extends GKey, ? extends GValue> m) {
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
	public GValue remove(Object key) {
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
	public Set2<GKey> keySet() {
		return new Keys();
	}

	@Override
	public Collection2<GValue> values() {
		return new Values();
	}

	@Override
	public Set2<Entry<GKey, GValue>> entrySet() {
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

}