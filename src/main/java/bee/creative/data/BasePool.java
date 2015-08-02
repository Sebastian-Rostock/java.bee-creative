package bee.creative.data;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Field;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen {@link Pool}. Die Datensätze müssen Nachfahren von {@link BaseItem} sein.
 * 
 * @author Sebastian Rostock 2011.
 * @param <GItem> Typ der Datensätze.
 */
public abstract class BasePool<GItem extends Item> implements Pool<GItem> {

	/**
	 * Diese Methode implementiert {@link BaseItem#delete()}. Sie sollte vom {@link Iterator} ({@link #iterator()}) bzw. der {@link Collection} (
	 * {@link #items(int)}) zum Entfernen eines {@link Item}s verwendet werden.
	 * 
	 * @param item {@link BaseItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link BaseItem} nicht zu diesem {@link BasePool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void delete(final BaseItem item) throws NullPointerException, IllegalArgumentException {
		switch (item.state()) {
			case Item.APPEND_STATE:
			case Item.UPDATE_STATE:
			case Item.REMOVE_STATE:
				if (!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doDelete((GItem)item);
			case Item.CREATE_STATE:
				return;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode implementiert {@link BaseItem#append()}.
	 * 
	 * @param item {@link BaseItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalArgumentException Wenn das {@link BaseItem} nicht zu diesem {@link BasePool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void append(final BaseItem item) throws NullPointerException, IllegalArgumentException {
		switch (item.state()) {
			case Item.CREATE_STATE:
			case Item.REMOVE_STATE:
			case Item.UPDATE_STATE:
				if (!this.equals(item.pool())) throw new IllegalArgumentException();
				this.doAppend((GItem)item);
			case Item.APPEND_STATE:
				return;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Diese Methode implementiert {@link BaseItem#remove()}.
	 * 
	 * @param item {@link BaseItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link Item#CREATE_STATE} befindet.
	 * @throws IllegalArgumentException Wenn das {@link BaseItem} nicht zu diesem {@link BasePool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void remove(final BaseItem item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		switch (item.state()) {
			case Item.APPEND_STATE:
			case Item.UPDATE_STATE:
				if (!this.equals(item.pool())) throw new IllegalArgumentException();
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
	 * Diese Methode implementiert {@link BaseItem#remove()}.
	 * 
	 * @param item {@link BaseItem}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link Item#APPEND_STATE} befindet.
	 * @throws IllegalArgumentException Wenn das {@link BaseItem} nicht zu diesem {@link BasePool} gehört oder {@link Item#state()} unbekannt ist.
	 */
	@SuppressWarnings ("unchecked")
	protected final void update(final BaseItem item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		switch (item.state()) {
			case Item.REMOVE_STATE:
			case Item.CREATE_STATE:
				if (!this.equals(item.pool())) throw new IllegalArgumentException();
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
	 * Diese Methode wird bei {@link #delete(BaseItem)} zur Überführung des Zustands zu {@link Item#CREATE_STATE} aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doDelete(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#append()} zur Überführung des Zustands zu {@link Item#APPEND_STATE} aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doAppend(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#remove()} zur Überführung des Zustands zu {@link Item#REMOVE_STATE} aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doRemove(final GItem item);

	/**
	 * Diese Methode wird bei {@link Item#update()} zur Überführung des Zustands zu {@link Item#UPDATE_STATE} aufgerufen.
	 * 
	 * @param item {@link Item}.
	 */
	protected abstract void doUpdate(final GItem item);

	{}

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
		return Iterators.iterator(this.items());
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
		if (object == this) return true;
		if (!(object instanceof Pool<?>)) return false;
		final Pool<?> data = (Pool<?>)object;
		return Objects.equals(this.type(), data.type()) && Objects.equals(this.field(), data.field()) && Objects.equals(this.owner(), data.owner());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this);
	}

}