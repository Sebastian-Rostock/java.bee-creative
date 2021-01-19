package bee.creative.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import bee.creative.bind.Field;
import bee.creative.bind.Properties;
import bee.creative.bind.Property;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine abstrakte {@link Map} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Map)} geschrieben.
 *
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxyMap<GKey, GValue, GData extends Map<GKey, GValue>> implements Map<GKey, GValue> {

	static abstract class Data<GItem, GData extends Collection<GItem>> extends AbstractProxyCollection<GItem, GData> {

		@Override
		protected void setData(final GData items) {
		}

		@Override
		public boolean add(final GItem e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection<? extends GItem> c) {
			throw new UnsupportedOperationException();
		}

	}

	class Keys extends Data<GKey, KeysData> implements Set<GKey> {

		@Override
		protected KeysData getData(final boolean readonly) {
			return new KeysData(readonly);
		}

		@Override
		protected void setData(final KeysData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class KeysData extends Data<GKey, Set<GKey>> implements Set<GKey> {

		final GData data;

		KeysData(final boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Set<GKey> getData(final boolean readonly) {
			return this.data.keySet();
		}

	}

	class Values extends Data<GValue, ValuesData> {

		@Override
		protected ValuesData getData(final boolean readonly) {
			return new ValuesData(readonly);
		}

		@Override
		protected void setData(final ValuesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

	}

	class ValuesData extends Data<GValue, Collection<GValue>> {

		final GData data;

		ValuesData(final boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Collection<GValue> getData(final boolean readonly) {
			return this.data.values();
		}

	}

	class Entries extends Data<Entry<GKey, GValue>, EntriesData> implements Set<Entry<GKey, GValue>> {

		final class Iter implements Iterator<Entry<GKey, GValue>> {

			@SuppressWarnings ("serial")
			final class Next extends SimpleEntry<GKey, GValue> {

				public Next(final Entry<GKey, GValue> entry) {
					super(entry);
				}

				@Override
				public GValue setValue(final GValue value) {
					final GValue result = super.setValue(value);
					AbstractProxyMap.this.setData(Iter.this.data.data);
					return result;
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
		protected EntriesData getData(final boolean readonly) {
			return new EntriesData(readonly);
		}

		@Override
		protected void setData(final EntriesData data) {
			AbstractProxyMap.this.setData(data.data);
		}

		@Override
		public Iterator<java.util.Map.Entry<GKey, GValue>> iterator() {
			return super.iterator();
		}

	}

	class EntriesData extends Data<Entry<GKey, GValue>, Set<Entry<GKey, GValue>>> implements Set<Entry<GKey, GValue>> {

		final GData data;

		EntriesData(final boolean readonly) {
			this.data = AbstractProxyMap.this.getData(readonly);
		}

		@Override
		protected Set<Entry<GKey, GValue>> getData(final boolean readonly) {
			return this.data.entrySet();
		}

	}

	/** Diese Klasse implementiert {@link AbstractProxyMap#toMap(Property)}. */
	static class PropertyMap<GKey, GValue> extends AbstractProxyMap<GKey, GValue, Map<GKey, GValue>> {
	
		public final Property<Map<GKey, GValue>> property;
	
		public PropertyMap(final Property<Map<GKey, GValue>> property) {
			this.property = Objects.notNull(property);
		}
	
		@Override
		public Map<GKey, GValue> getData(final boolean readonly) {
			return this.property.get();
		}
	
		@Override
		protected void setData(final Map<GKey, GValue> items) {
			this.property.set(items);
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
	protected abstract void setData(final GData items);

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public GValue get(final Object key) {
		return this.getData(true).get(key);
	}

	@Override
	public GValue put(final GKey key, final GValue value) {
		final GData data = this.getData(false);
		final GValue result = data.put(key, value);
		this.setData(data);
		return result;
	}

	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> m) {
		final GData data = this.getData(false);
		data.putAll(m);
		this.setData(data);
	}

	@Override
	public boolean containsKey(final Object key) {
		return this.getData(true).containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return this.getData(true).containsValue(value);
	}

	@Override
	public GValue remove(final Object key) {
		final GData data = this.getData(false);
		final GValue result = data.remove(key);
		this.setData(data);
		return result;
	}

	@Override
	public void clear() {
		final GData data = this.getData(false);
		data.clear();
		this.setData(data);
	}

	@Override
	public Set<GKey> keySet() {
		return new Keys();
	}

	@Override
	public Collection<GValue> values() {
		return new Values();
	}

	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return new Entries();
	}

	@Override
	public int hashCode() {
		return this.getData(true).hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		return this.getData(true).equals(object);
	}

	@Override
	public String toString() {
		return this.getData(true).toString();
	}

	/** Diese Methode ist eine Abkürzung für {@link AbstractProxyMap#toMap(Property) Properties.toMap(Fields.toProperty(item, field))}. */
	public static <GItem, GKey, GValue> Map<GKey, GValue> toMap(final GItem item, final Field<? super GItem, Map<GKey, GValue>> field)
		throws NullPointerException {
		return AbstractProxyMap.toMap(Properties.from(field, item));
	}

	/** Diese Methode gibt eine {@link Map} zurück, deren Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxyMap
	 * @param property {@link Property}.
	 * @return {@link Map}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GKey, GValue> Map<GKey, GValue> toMap(final Property<Map<GKey, GValue>> property) throws NullPointerException {
		return new PropertyMap<>(property);
	}

}