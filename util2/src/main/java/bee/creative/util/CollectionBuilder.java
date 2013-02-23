package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link Collection}-{@link Builder}s.
 * 
 * @see CollectionBuilder1
 * @see CollectionBuilder2
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CollectionBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link Collection}-{@link Builder}, der eine konfigurierte {@link Collection} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Collection} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see Collections#checkedCollection(Collection, Class)
	 * @see Collections#synchronizedCollection(Collection)
	 * @see Collections#unmodifiableCollection(Collection)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <V> Typ der Werte.
	 * @param <L> Typ der {@link Collection}.
	 */
	public static interface CollectionBuilder1<V, L extends Collection<V>> extends ValuesBuilder<V>, CollectionBuilder2<V, L> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<V, L> add(V value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<V, L> addAll(V... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Collection}-{@link Builder}.
		 */
		@Override
		public CollectionBuilder1<V, L> addAll(Iterable<? extends V> value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<V, Collection<V>> asCheckedCollection(Class<V> clazz);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1<V, Collection<V>> asSynchronizedCollection();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder2<V, Collection<V>> asUnmodifiableCollection();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public L create();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Collection}-{@link Builder}, der eine konfigurierte {@link Collection} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link Collection} umwandeln kann.
	 * 
	 * @see Collections#checkedCollection(Collection, Class)
	 * @see Collections#synchronizedCollection(Collection)
	 * @see Collections#unmodifiableCollection(Collection)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <V> Typ der Werte.
	 * @param <L> Typ der {@link Collection}.
	 */
	public static interface CollectionBuilder2<V, L extends Collection<V>> extends Builder<L> {

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#checkedCollection(Collection, Class)}.
		 * 
		 * @see Collections#checkedCollection(Collection, Class)
		 * @param clazz {@link Class} der Werte.
		 * @return {@link Collection}-{@link Builder}.
		 */
		public CollectionBuilder2<V, Collection<V>> asCheckedCollection(Class<V> clazz);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#synchronizedCollection(Collection)}.
		 * 
		 * @see Collections#synchronizedCollection(Collection)
		 * @return {@link Collection}-{@link Builder}.
		 */
		public CollectionBuilder2<V, Collection<V>> asSynchronizedCollection();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Collection} via {@link Collections#unmodifiableCollection(Collection)}.
		 * 
		 * @see Collections#unmodifiableCollection(Collection)
		 * @return {@link CollectionBuilder2}.
		 */
		public CollectionBuilder2<V, Collection<V>> asUnmodifiableCollection();

		/**
		 * Diese Methode gibt die konfigurierte {@link Collection} zurück.
		 * 
		 * @return {@link Collection}.
		 */
		@Override
		public L create();

	}

	/**
	 * Diese Klasse implementiert den {@link CollectionBuilder1}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <V> Typ der Werte.
	 * @param <L> Typ der {@link Collection}.
	 */
	static final class CollectionBuilder1Impl<V, L extends Collection<V>> implements CollectionBuilder1<V, L> {

		/**
		 * Dieses Feld speichert die {@link Collection}.
		 */
		final L collection;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Collection}.
		 * 
		 * @param collection {@link Collection}.
		 */
		public CollectionBuilder1Impl(final L collection) {
			this.collection = collection;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, L> add(final V value) {
			this.collection.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, L> addAll(final V... value) {
			this.collection.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, L> addAll(final Iterable<? extends V> value) {
			Iterables.appendAll(this.collection, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, Collection<V>> asCheckedCollection(final Class<V> clazz) {
			return new CollectionBuilder1Impl<V, Collection<V>>(Collections.checkedCollection(this.collection, clazz));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, Collection<V>> asSynchronizedCollection() {
			return new CollectionBuilder1Impl<V, Collection<V>>(Collections.synchronizedCollection(this.collection));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CollectionBuilder1Impl<V, Collection<V>> asUnmodifiableCollection() {
			return new CollectionBuilder1Impl<V, Collection<V>>(Collections.unmodifiableCollection(this.collection));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public L create() {
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
	 * @param <V> Typ der Werte.
	 * @param <L> Typ der {@link Collection}.
	 * @param collection {@link Collection}.
	 * @return {@link Collection}-{@link Builder}.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public <V, L extends Collection<V>> CollectionBuilder1<V, L> collection(final L collection) throws NullPointerException {
		if(collection == null) throw new NullPointerException();
		return new CollectionBuilder1Impl<V, L>(collection);
	}

}