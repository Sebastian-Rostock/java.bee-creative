package bee.creative.data;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Converters;
import bee.creative.util.Field;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Iterables.FilteredIterable;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen {@link Pool}. Die Datensätze müssen Nachfahren von {@link AbstractItem} sein.
 * 
 * @author Sebastian Rostock 2011.
 * @param <GItem> Typ der Datensätze.
 */
public abstract class AbstractPool<GItem extends Item> implements Pool<GItem> {

	/**
	 * Diese Klasse implementiert eine {@link Selection}, die ein {@link FilteredIterable} verwendet.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Datensätze.
	 */
	public static final class FilteredSelection<GItem> implements Selection<GItem> {

		/**
		 * Dieses Feld speichert das {@link Iterable}.
		 */
		final Iterable<? extends GItem> iterable;

		/**
		 * Dieser Konstruktor initialisiert das {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable} der {@link Item}s.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public FilteredSelection(final Iterable<? extends GItem> iterable) throws NullPointerException {
			if(iterable == null) throw new NullPointerException();
			this.iterable = iterable;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Diese Implementation gibt das erste {@link Item} der {@link Selection} zurück, die von {@link #findAll(Field, Object)} für die gegebenen Parameter
		 * geliefert wird.
		 */
		@Override
		public <GValue> GItem find(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
			for(final GItem item: this.findAll(field, value))
				return item;
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> Selection<GItem> findAll(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
			if(field == null) throw new NullPointerException();
			return new FilteredSelection<GItem>(Iterables.filteredIterable(Filters.convertedFilter(Converters.fieldConverter(field), Filters.containsFilter(value)),
				this.iterable));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public Iterator<GItem> iterator() {
			return (Iterator<GItem>)this.iterable.iterator();
		}

	}

	/**
	 * Diese Methode implementiert {@link AbstractItem#state()}.
	 * 
	 * @param item {@link AbstractItem}.
	 * @return Status.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	protected abstract int state(final AbstractItem item) throws NullPointerException;

	/**
	 * Diese Methode implementiert {@link AbstractItem#delete()}. Sie sollte vom {@link Iterator} ({@link #iterator()}) bzw. der {@link Collection} (
	 * {@link #items(int)}) zum Entfernen eines {@link Item}s verwendet werden.
	 * 
	 * @param item {@link AbstractItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link AbstractItem} nicht zu diesem {@link AbstractPool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void delete(final AbstractItem item) throws NullPointerException, IllegalArgumentException {
		switch(item.state()){
			case Item.APPEND_STATE:
			case Item.UPDATE_STATE:
			case Item.REMOVE_STATE:
				if(!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doDelete((GItem)item);
			case Item.CREATE_STATE:
				return;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode implementiert {@link AbstractItem#append()}.
	 * 
	 * @param item {@link AbstractItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link AbstractItem} nicht zu diesem {@link AbstractPool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void append(final AbstractItem item) throws NullPointerException, IllegalArgumentException {
		switch(item.state()){
			case Item.CREATE_STATE:
			case Item.REMOVE_STATE:
			case Item.UPDATE_STATE:
				if(!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doAppend((GItem)item);
			case Item.APPEND_STATE:
				return;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode implementiert {@link AbstractItem#remove()}.
	 * 
	 * @param item {@link AbstractItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link Item#CREATE_STATE} befindet.
	 * @throws IllegalArgumentException Wenn das {@link AbstractItem} nicht zu diesem {@link AbstractPool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void remove(final AbstractItem item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		switch(item.state()){
			case Item.APPEND_STATE:
			case Item.UPDATE_STATE:
				if(!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doRemove((GItem)item);
			case Item.REMOVE_STATE:
				return;
			case Item.CREATE_STATE:
				throw new IllegalStateException();
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode implementiert {@link AbstractItem#remove()}.
	 * 
	 * @param item {@link AbstractItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link Item#APPEND_STATE} befindet.
	 * @throws IllegalArgumentException Wenn das {@link AbstractItem} nicht zu diesem {@link AbstractPool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void update(final AbstractItem item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		switch(item.state()){
			case Item.REMOVE_STATE:
			case Item.CREATE_STATE:
				if(!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doUpdate((GItem)item);
			case Item.UPDATE_STATE:
				return;
			case Item.APPEND_STATE:
				throw new IllegalStateException();
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode erstellt ein neues {@link Item} im {@link Item#CREATE_STATE} und gibt dieses zurück. Sie wird bei {@link Pool#create()} aufgerufen.
	 * 
	 * @see Item#state()
	 * @see Pool#create()
	 * @return neues {@link Item}.
	 */
	protected abstract GItem doCreate();

	/**
	 * Diese Methode wird bei {@link #delete(AbstractItem)} zur Zustandsüberführung in {@link Item#CREATE_STATE} aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doDelete(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#append()} zur Zustandsüberführung aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doAppend(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#remove()} zur Zustandsüberführung aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doRemove(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#update()} zur Zustandsüberführung aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doUpdate(final GItem item);

	/**
	 * {@inheritDoc}
	 * 
	 * @see #find(Field, Object, int)
	 */
	@Override
	public <GValue> GItem find(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
		return this.find(field, value, Item.APPEND_STATE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #items()
	 * @see FilteredSelection
	 */
	@Override
	public <GValue> GItem find(final Field<? super GItem, ? extends GValue> field, final GValue value, final int states) throws NullPointerException {
		return new FilteredSelection<GItem>(this.items(states)).find(field, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #findAll(Field, Object, int)
	 */
	@Override
	public <GValue> Selection<GItem> findAll(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
		return this.findAll(field, value, Item.APPEND_STATE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #items()
	 * @see FilteredSelection
	 */
	@Override
	public <GValue> Selection<GItem> findAll(final Field<? super GItem, ? extends GValue> field, final GValue value, final int states)
		throws NullPointerException {
		return new FilteredSelection<GItem>(this.items(states)).findAll(field, value);
	}

	/**
	 * {@inheritDoc} Diese entspricht der des {@link #type()}s.
	 */
	@Override
	public String label() {
		return this.type().label();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.items().size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<? extends GItem> items() {
		return this.items(Item.APPEND_STATE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GItem create() {
		return this.doCreate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GItem> iterator() {
		return Iterators.iterator(this.items().iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.owner());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof Pool<?>)) return false;
		final Pool<?> data = (Pool<?>)object;
		return Objects.equals(this.type(), data.type()) && Objects.equals(this.field(), data.field()) && Objects.equals(this.owner(), data.owner());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this);
	}

}