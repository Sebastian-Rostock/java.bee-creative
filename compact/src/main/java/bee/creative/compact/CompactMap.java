package bee.creative.compact;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Diese Klasse implementiert eine abstrakte {@link Map}, deren Daten in einem {@link Array} verwaltet werden.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
@SuppressWarnings ("javadoc")
public abstract class CompactMap<GKey, GValue> extends CompactData implements Map<GKey, GValue> {

	/**
	 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an die Schlüssel einer
	 * {@link CompactMap} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class KeySet<GKey> extends AbstractSet<GKey> {

		/**
		 * Dieses Feld speichert die {@link CompactMap}.
		 */
		protected final CompactMap<GKey, ?> data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
		 * 
		 * @param data {@link CompactMap}.
		 */
		public KeySet(final CompactMap<GKey, ?> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GKey> iterator() {
			return new CompactMap.CompactMapKeyAscendingIterator<GKey>(this.data, this.data.firstIndex(),
				this.data.lastIndex() + 1);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an die Einträge einer
	 * {@link CompactMap} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class EntrySet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

		/**
		 * Dieses Feld speichert die {@link CompactMap}.
		 */
		protected final CompactMap<GKey, GValue> data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
		 * 
		 * @param data {@link CompactMap}.
		 */
		public EntrySet(final CompactMap<GKey, GValue> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GKey, GValue>> iterator() {
			return new CompactMap.CompactMapEntryIterator<GKey, GValue>(this.data, this.data.firstIndex(),
				this.data.lastIndex() + 1);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link AbstractMap}, die ihre Schnittstelle an eine gegebene {@link Map} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class ItemMap<GKey, GValue> extends AbstractMap<GKey, GValue> {

		/**
		 * Dieses Feld speichert die {@link Map}.
		 */
		protected final Map<GKey, GValue> data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 * 
		 * @param data {@link Map}.
		 */
		public ItemMap(final Map<GKey, GValue> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return this.data.entrySet();
		}

	}

	/**
	 * Diese Klasse implementiert das {@link SimpleEntry} einer {@link CompactMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class ItemEntry<GKey, GValue> extends SimpleEntry<GKey, GValue> {

		/**
		 * Dieses Feld speichert die {@code SerialVersionUID}.
		 */
		private static final long serialVersionUID = -543360027933297926L;

		/**
		 * Dieses Feld speichert die {@link CompactMap}.
		 */
		protected final CompactMap<GKey, GValue> data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactMap} und den Index.
		 * 
		 * @param data {@link CompactMap}.
		 * @param index Index.
		 */
		public ItemEntry(final CompactMap<GKey, GValue> data, final int index) {
			super(data.getKey(index), data.getValue(index));
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue setValue(final GValue value) {
			final GKey key = this.getKey();
			final GValue result = super.setValue(value);
			final int index = this.data.customItemIndex(this.getKey());
			if(index < 0) throw new IllegalStateException();
			this.data.setEntry(index, key, value);
			return result;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link AbstractCollection}, das seine Schnittstelle an die Werte einer
	 * {@link CompactMap} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class ValueCollection<GValue> extends AbstractCollection<GValue> {

		/**
		 * Dieses Feld speichert die {@link CompactMap}.
		 */
		protected final CompactMap<?, GValue> data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
		 * 
		 * @param data {@link CompactMap}.
		 */
		public ValueCollection(final CompactMap<?, GValue> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GValue> iterator() {
			return new CompactMap.CompactMapValueIterator<GValue>(this.data, this.data.firstIndex(),
				this.data.lastIndex() + 1);
		}

	}

	/**
	 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der Schlüssel.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class CompactMapKeyAscendingIterator<GKey> extends
		CompactDataAscendingIterator<GKey, CompactMap<GKey, ?>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
		 * 
		 * @param map {@link CompactMap}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactMapKeyAscendingIterator(final CompactMap<GKey, ?> map, final int from, final int last) {
			super(map, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GKey next(final int index) {
			return this.data.getKey(index);
		}

	}

	/**
	 * Diese Klasse implementiert den absteigenden {@link Iterator} der Schlüssel.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class CompactMapKeyDescendingIterator<GKey> extends
		CompactDataDescendingIterator<GKey, CompactMap<GKey, ?>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
		 * 
		 * @param map {@link CompactMap}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactMapKeyDescendingIterator(final CompactMap<GKey, ?> map, final int from, final int last) {
			super(map, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GKey next(final int index) {
			return this.data.getKey(index);
		}

	}

	/**
	 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der Werte.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <V> Typ der Werte.
	 */
	protected static final class CompactMapValueIterator<V> extends CompactDataAscendingIterator<V, CompactMap<?, V>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
		 * 
		 * @param map {@link CompactMap}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactMapValueIterator(final CompactMap<?, V> map, final int from, final int last) {
			super(map, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected V next(final int index) {
			return this.data.getValue(index);
		}

	}

	/**
	 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der {@link ItemEntry}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	protected static final class CompactMapEntryIterator<GKey, V> extends
		CompactDataAscendingIterator<Entry<GKey, V>, CompactMap<GKey, V>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
		 * 
		 * @param map {@link CompactMap}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactMapEntryIterator(final CompactMap<GKey, V> map, final int from, final int last) {
			super(map, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Entry<GKey, V> next(final int index) {
			return this.data.getEntry(index);
		}

	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map}.
	 */
	public CompactMap() {
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactMap(final int capacity) {
		this.allocate(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen.
	 * 
	 * @see CompactData#allocate(int)
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 */
	public CompactMap(final Map<? extends GKey, ? extends GValue> map) {
		this.allocate(map.size());
		this.putAll(map);
	}

	/**
	 * Diese Methode gibt den Schlüssel des gegebenen Werts zurück.
	 * 
	 * @param value Wert.
	 * @return Schlüssel.
	 */
	protected abstract GKey getKey(final GValue value);

	/**
	 * Diese Methode setzt den Schlüssel des gegebenen Werts.
	 * 
	 * @param key Schlüssel.
	 * @param value Wert.
	 */
	protected abstract void setKey(GKey key, final GValue value);

	/**
	 * Diese Methode gibt den Schlüssel des {@code index}-ten Elements zurück.
	 * 
	 * @param index Index.
	 * @return Schlüssel des {@code index}-ten Elements.
	 */
	protected abstract GKey getKey(int index);

	/**
	 * Diese Methode gibt den Wert des {@code index}-ten Elements zurück.
	 * 
	 * @param index Index.
	 * @return Wert des {@code index}-ten Elements.
	 */
	protected abstract GValue getValue(int index);

	/**
	 * Diese Methode gibt das {@code index}-te Element zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Element
	 */
	protected final Entry<GKey, GValue> getEntry(final int index) {
		return new CompactMap.ItemEntry<GKey, GValue>(this, index);
	}

	/**
	 * Diese Methode setzt Schlüssel und Wert des {@code index}-ten Elements.
	 * 
	 * @param index Index.
	 * @param key Schlüssel.
	 * @param value Wert.
	 */
	protected abstract void setEntry(int index, GKey key, GValue value);

	/**
	 * Diese Methode sucht zuerst nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt den
	 * Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der
	 * Index, bei dem der Eintrag eingefügt werden müsste.
	 * 
	 * @see CompactData#equalsIndex(Object, int)
	 * @see CompactData#compareIndex(Object, int)
	 * @param key Syhlüssel.
	 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	@Override
	protected abstract int customItemIndex(final Object key);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.customRemove(this.from, this.size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<GValue> values() {
		return new CompactMap.ValueCollection<GValue>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<GKey> keySet() {
		return new CompactMap.KeySet<GKey>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return new CompactMap.EntrySet<GKey, GValue>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(final Object key) {
		return this.customItemIndex(key) >= 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue get(final Object key) {
		final int index = this.customItemIndex(key);
		if(index < 0) return null;
		return this.getValue(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue put(final GKey key, final GValue value) {
		int index = this.customItemIndex(key);
		if(index >= 0){
			final GValue item = this.getValue(index);
			this.setEntry(index, this.getKey(index), value);
			return item;
		}
		index = -index - 1;
		this.customInsert(index, 1);
		this.setEntry(index, key, value);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> map) {
		for(final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()){
			this.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue remove(final Object key) {
		final int index = this.customItemIndex(key);
		if(index < 0) return null;
		final GValue item = this.getValue(index);
		this.customRemove(index, 1);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new CompactMap.ItemMap<GKey, GValue>(this).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof Map<?, ?>)) return false;
		return new CompactMap.ItemMap<GKey, GValue>(this).equals(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new CompactMap.ItemMap<GKey, GValue>(this).toString();
	}

}