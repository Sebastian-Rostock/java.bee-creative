package bee.creative.util2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import bee.creative.util.Builder;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link Collection}-{@link Builder}s.
 * 
 * @see CollectionBuilder1
 * @see CollectionBuilder2
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CollectionBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link Collection}-{@link Builder}, der eine konfigurierte {@link Collection} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Collection} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see Collections#checkedCollection(Collection, Class)
	 * @see Collections#synchronizedCollection(Collection)
	 * @see Collections#unmodifiableCollection(Collection)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GCollection> Typ der {@link Collection}.
	 */
	public static interface CollectionBuilder1<GValue, GCollection extends Collection<GValue>> extends ValuesBuilder<GValue>,
		CollectionBuilder2<GValue, GCollection> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> add(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> addAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> addAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> remove(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> removeAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> removeAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, Collection<GValue>> asCheckedCollection(Class<GValue> type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, Collection<GValue>> asSynchronizedCollection();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder2<GValue, Collection<GValue>> asUnmodifiableCollection();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GCollection build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Collection}-{@link Builder}, der eine konfigurierte {@link Collection} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Collection} umwandeln kann.
	 * 
	 * @see Collections#checkedCollection(Collection, Class)
	 * @see Collections#synchronizedCollection(Collection)
	 * @see Collections#unmodifiableCollection(Collection)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GCollection> Typ der {@link Collection}.
	 */
	public static interface CollectionBuilder2<GValue, GCollection extends Collection<GValue>> extends Builder<GCollection> {

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#checkedCollection(Collection, Class)}.
		 * 
		 * @see Collections#checkedCollection(Collection, Class)
		 * @param type {@link Class} der Werte.
		 * @return {@link Collection}-{@link Builder}.
		 */
		public CollectionBuilder2<GValue, Collection<GValue>> asCheckedCollection(Class<GValue> type);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#synchronizedCollection(Collection)}.
		 * 
		 * @see Collections#synchronizedCollection(Collection)
		 * @return {@link Collection}-{@link Builder}.
		 */
		public CollectionBuilder2<GValue, Collection<GValue>> asSynchronizedCollection();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#unmodifiableCollection(Collection)}.
		 * 
		 * @see Collections#unmodifiableCollection(Collection)
		 * @return {@link CollectionBuilder2}.
		 */
		public CollectionBuilder2<GValue, Collection<GValue>> asUnmodifiableCollection();

		/**
		 * Diese Methode gibt die konfigurierte {@link Collection} zurück.
		 * 
		 * @return {@link Collection}.
		 */
		@Override
		public GCollection build() throws IllegalStateException;

	}

	/**
	 * Diese Klasse implementiert den {@link CollectionBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GCollection> Typ der {@link Collection}.
	 */
	static final class CollectionBuilderImpl<GValue, GCollection extends Collection<GValue>> implements CollectionBuilder1<GValue, GCollection> {

		/**
		 * Dieses Feld speichert die {@link Collection}.
		 */
		final GCollection collection;

		/**
		 * Dieser Konstruktor initialisiert die {@link Collection}.
		 * 
		 * @param collection {@link Collection}.
		 */
		public CollectionBuilderImpl(final GCollection collection) {
			this.collection = collection;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> add(final GValue value) {
			this.collection.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> addAll(final GValue... value) {
			this.collection.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> addAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.collection, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> remove(final GValue value) {
			this.collection.remove(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> removeAll(final GValue... value) {
			this.collection.removeAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, GCollection> removeAll(final Iterable<? extends GValue> value) {
			Iterables.removeAll(this.collection, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, Collection<GValue>> asCheckedCollection(final Class<GValue> type) {
			return new CollectionBuilderImpl<GValue, Collection<GValue>>(Collections.checkedCollection(this.collection, type));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, Collection<GValue>> asSynchronizedCollection() {
			return new CollectionBuilderImpl<GValue, Collection<GValue>>(Collections.synchronizedCollection(this.collection));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<GValue, Collection<GValue>> asUnmodifiableCollection() {
			return new CollectionBuilderImpl<GValue, Collection<GValue>>(Collections.unmodifiableCollection(this.collection));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GCollection build() {
			return this.collection;
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link CollectionBuilder} zurück.
	 * 
	 * @return {@link CollectionBuilder}.
	 */
	public static CollectionBuilder use() {
		return new CollectionBuilder();
	}

	/**
	 * Diese Methode gibt einen {@link Collection}-{@link Builder} für die gegebene {@link Collection} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param <GCollection> Typ der {@link Collection}.
	 * @param collection {@link Collection}.
	 * @return {@link Collection}-{@link Builder}.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public <GValue, GCollection extends Collection<GValue>> CollectionBuilder1<GValue, GCollection> collection(final GCollection collection)
		throws NullPointerException {
		if(collection == null) throw new NullPointerException();
		return new CollectionBuilderImpl<GValue, GCollection>(collection);
	}

}