package bee.creative.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.util.Assigner;
import bee.creative.util.Converter;
import bee.creative.util.Converters;
import bee.creative.util.Field;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Iterables.FilteredIterable;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Hilfsmethoden zur Erzeugung und Verwaltung von {@link Item}s.
 * 
 * @see Item
 * @see Pool
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Items {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Item}, dass seinen {@link AbstractPool} kennt und einen Teil seiner Schnittstelle an diesen Delegiert.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AbstractItem<GOwner> implements Item<GOwner> {

		/**
		 * Dieser Konstruktor initialisiert den {@link AbstractPool}.
		 * 
		 * @param pool {@link AbstractPool}.
		 * @throws NullPointerException Wenn der gegebene {@link AbstractPool} {@code null} ist.
		 */
		public AbstractItem(final AbstractPool<? extends Item<? super GOwner>, ? extends GOwner> pool) throws NullPointerException {
			if(pool == null) throw new NullPointerException("pool is null");
			this.pool = pool;
		}

		/**
		 * Dieses Feld speichert den {@link AbstractPool}.
		 */
		protected final AbstractPool<? extends Item<? super GOwner>, ? extends GOwner> pool;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AbstractPool<? extends Item<? super GOwner>, ? extends GOwner> pool() {
			return this.pool;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return this.pool.type();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOwner owner() {
			return this.pool.owner();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see AbstractPool#doAppend(Item)
		 */
		@Override
		public void append() {
			this.pool.append(this);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see AbstractPool#doRemove(Item)
		 */
		@Override
		public void remove() throws IllegalStateException {
			this.pool.remove(this);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see AbstractPool#doUpdate(Item)
		 */
		@Override
		public void update() throws IllegalStateException {
			this.pool.update(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final long value = this.key();
			return (int)(value ^ (value >>> 32));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Item<?>)) return false;
			final Item<?> data = (Item<?>)object;
			return (this.key() == data.key()) && Objects.equals(this.pool(), data.pool());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Pool}. Die Datensätze müssen Nachfahren von {@link AbstractItem} sein.
	 * 
	 * @author Sebastian Rostock 2011.
	 * @param <GItem> Typ der Datensätze.
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AbstractPool<GItem extends Item<?>, GOwner> implements Pool<GItem, GOwner> {

		/**
		 * Diese Methode implementiert {@link AbstractItem#append()}.
		 * 
		 * @param item {@link AbstractItem}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 * @throws IllegalArgumentException Wenn das {@link AbstractItem} nicht zu diesem {@link AbstractPool} gehört oder {@link Item#state()} unbekannt ist.
		 */
		@SuppressWarnings ("unchecked")
		final void append(final AbstractItem<?> item) throws NullPointerException, IllegalArgumentException {
			switch(item.state()){
				case Item.CREATE_STATE:
				case Item.REMOVE_STATE:
				case Item.UPDATE_STATE:
					if(item.pool != this) throw new IllegalArgumentException();
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
		final void remove(final AbstractItem<?> item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			switch(item.state()){
				case Item.APPEND_STATE:
				case Item.UPDATE_STATE:
					if(item.pool != this) throw new IllegalArgumentException();
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
		final void update(final AbstractItem<?> item) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			switch(item.state()){
				case Item.REMOVE_STATE:
				case Item.CREATE_STATE:
					if(item.pool != this) throw new IllegalArgumentException();
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
		 * Diese Methode erstellt ein neues {@link Item} im {@link Item#CREATE_STATE} und gibt dieses zurück.
		 * 
		 * @see Item#state()
		 * @return neues {@link Item}.
		 */
		protected abstract GItem doCreate();

		/**
		 * Diese Methode wird bei {@link Item#append()} aufgerufen.
		 * 
		 * @param item {@link Item}.
		 */
		protected abstract void doAppend(final GItem item);

		/**
		 * Diese Methode wird bei {@link Item#remove()} aufgerufen.
		 * 
		 * @param item {@link Item}.
		 */
		protected abstract void doRemove(final GItem item);

		/**
		 * Diese Methode wird bei {@link Item#update()} aufgerufen.
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
		@SuppressWarnings ("unchecked")
		@Override
		public Iterator<GItem> iterator() {
			return (Iterator<GItem>)this.items().iterator();
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
			if(!(object instanceof Pool<?, ?>)) return false;
			final Pool<?, ?> data = (Pool<?, ?>)object;
			return Objects.equals(this.type(), data.type()) && Objects.equals(this.owner(), data.owner());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Selection}, die ein {@link FilteredIterable} verwendet.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der {@link Item}s.
	 */
	public static final class FilteredSelection<GItem extends Item<?>> implements Selection<GItem> {

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
	 * Diese Klasse implementiert das abstrakte Ereignis, dass beim Modifizieren eines {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class ModifyItemEvent {

		/**
		 * Dieses Feld speichert den Modus, in dem normale Ereignisse ausgelöst werden.
		 */
		public static final int DEFAULT_MODE = 0;

		/**
		 * Dieses Feld speichert den Modus, in dem rückgängig machende Ereignisse ausgelöst werden.
		 */
		public static final int UNDOING_MODE = 1;

		/**
		 * Dieses Feld speichert den Modus, in dem wiederholende Ereignisse ausgelöst werden.
		 */
		public static final int REDOING_MODE = 2;

		/**
		 * Dieses Feld speichert den Modus.
		 * 
		 * @see #DEFAULT_MODE
		 * @see #UNDOING_MODE
		 * @see #REDOING_MODE
		 */
		public final int mode;

		/**
		 * Dieses Feld speichert den {@link Type} des modifizierten {@link Item}s.
		 */
		public final Type<?> type;

		/**
		 * Dieses Feld speichert das modifizierte {@link Item}.
		 */
		public final Item<?> item;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item modifiziertes {@link Item}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemEvent(final int mode, final Item<?> item) throws NullPointerException, IllegalArgumentException {
			if((mode < ModifyItemEvent.DEFAULT_MODE) || (mode > ModifyItemEvent.REDOING_MODE)) throw new IllegalArgumentException("mode is invalid");
			if(item == null) throw new NullPointerException("item is null");
			this.mode = mode;
			this.type = item.type();
			this.item = item;
		}

	}

	/**
	 * Diese Klasse implementiert das Ereignis, dass beim Modifizieren eienr Eigenschaft eines {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ModifyItemFieldEvent extends ModifyItemEvent {

		/**
		 * Dieses Feld speichert das {@link Field} zur modifizierten Eigenschaft.
		 */
		public final Field<?, ?> field;

		/**
		 * Dieses Feld speichert den alten Wert der Eigenschaft.
		 */
		public final Object oldValue;

		/**
		 * Dieses Feld speichert den neuen Wert der Eigenschaft.
		 */
		public final Object newValue;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @see #DEFAULT_MODE
		 * @param item modifiziertes {@link Item}.
		 * @param field {@link Field} zur modifizierten Eigenschaft.
		 * @param oldValue alter Wert oder {@code null}.
		 * @param newValue neuer Wert oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} bzw. {@link Field} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemFieldEvent(final Item<?> item, final Field<?, ?> field, final Object oldValue, final Object newValue) throws NullPointerException,
			IllegalArgumentException {
			this(ModifyItemEvent.DEFAULT_MODE, item, field, oldValue, newValue);
		}

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item modifiziertes {@link Item}.
		 * @param field {@link Field} zur modifizierten Eigenschaft.
		 * @param oldValue alter Wert oder {@code null}.
		 * @param newValue neuer Wert oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Item} bzw. {@link Field} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemFieldEvent(final int mode, final Item<?> item, final Field<?, ?> field, final Object oldValue, final Object newValue)
			throws NullPointerException, IllegalArgumentException {
			super(mode, item);
			if(field == null) throw new NullPointerException("field is null");
			this.field = field;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

	}

	/**
	 * Diese Klasse implementiert das Ereignis, dass beim Modifizieren des Status eins {@link Item}s ausgelöst wird.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ModifyItemStateEvent extends ModifyItemEvent {

		/**
		 * Dieses Feld speichert den alten Status des {@link Item}s.
		 */
		public final int oldState;

		/**
		 * Dieses Feld speichert den neuen Status des {@link Item}s.
		 */
		public final int newState;

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @see #DEFAULT_MODE
		 * @param item {@link Item}.
		 * @param oldState alter Status des {@link Item}s.
		 * @param newState neuer Status des {@link Item}s.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemStateEvent(final Item<?> item, final int oldState, final int newState) throws NullPointerException, IllegalArgumentException {
			this(ModifyItemEvent.DEFAULT_MODE, item, oldState, newState);
		}

		/**
		 * Dieser Konstruktor initialisiert das Ereignis.
		 * 
		 * @param mode Modus.
		 * @param item {@link Item}.
		 * @param oldState alter Status des {@link Item}s.
		 * @param newState neuer Status des {@link Item}s.
		 * @throws NullPointerException Wenn das gegebene {@link Item} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public ModifyItemStateEvent(final int mode, final Item<?> item, final int oldState, final int newState) throws NullPointerException,
			IllegalArgumentException {
			super(mode, item);
			this.oldState = oldState;
			this.newState = newState;
		}

	}

	/**
	 * Diese Schnittstelle definiert die Methoden zur Behandlung des {@link ModifyItemStateEvent}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ModifyItemStateListener {

		/**
		 * Diese Methode wird nach der Modifikation des Status eines {@link Item}s aufgerufen.
		 * 
		 * @param event {@link ModifyItemStateEvent}.
		 */
		public void onModifyItemState(ModifyItemStateEvent event);

	}

	/**
	 * Diese Schnittstelle definiert die Methoden zur Behandlung des {@link ModifyItemFieldEvent}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ModifyItemFieldListener {

		/**
		 * Diese Methode wird nach der Modifikation einer Eigenschaft eines {@link Item}s aufgerufen.
		 * 
		 * @param event {@link ModifyItemFieldEvent}.
		 */
		public void onModifyItemField(ModifyItemFieldEvent event);

	}

	/**
	 * Dieses Feld speichert den {@link Converter} für {@link #autoCopier()}.
	 */
	static final Converter<?, ?> AUTO_COPIER = new Converter<Object, Object>() {

		@Override
		public Object convert(final Object input) {
			if(input instanceof Set) return new HashSet<Object>((Set<?>)input);
			if(input instanceof Collection) return new ArrayList<Object>((Collection<?>)input);
			if(input instanceof Map) return new HashMap<Object, Object>((Map<?, ?>)input);
			return input;
		}

	};

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, dessen Ausgabe identisch zur Eingabe oder eine Kopie der Eingabe ist.
	 * <ul>
	 * <li>Wenn die Eingabe ein {@link Set} ist, wird ein neues {@link HashSet} mit dessen Elementen als Ausgabe verwendet.</li>
	 * <li>Wenn die Eingabe eine {@link Collection} ist, wird eine neue {@link ArrayList} mit deren Elementen als Ausgabe verwendet.</li>
	 * <li>Wenn die Eingabe eine {@link Map} ist, wird eine neue {@link HashMap} mit deren Einträge als Ausgabe verwendet.</li>
	 * <li>Wenn die Eingabe einen anderen Typ hat, wird sie unverändert als Ausgabe verwendet.</li>
	 * </ul>
	 * 
	 * @see #modifyField(Item, Field, Object, Converter, ModifyItemFieldListener)
	 * @param <GValue> Typ von Ein- und Ausgabe.
	 * @return {@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GValue> Converter<GValue, GValue> autoCopier() {
		return (Converter<GValue, GValue>)Items.AUTO_COPIER;
	}

	/**
	 * Diese Methode setzt den Wert des gegebenen {@link Field}s am gegebenen {@link Item} und benachrichtigt bei einer Änderung den gegebenen
	 * {@link ModifyItemFieldListener}. Eine Änderung liegt dann vor, denn der über {@link Field#get(Object)} ermittelte, aktuelle Wert nicht
	 * {@link Objects#equals(Object, Object) äquivalent} dem gegebenen Wert ist. In diesem Fall wird mit dem gegebenen {@link Converter} eine Kopie des aktuellen
	 * Werts erzeugt, der neue Wert über das {@link Field} gesetzt und ein {@link ModifyItemFieldEvent} mit dem kopierten als alten und dem gegebenen als neuen
	 * Wert ausgelöst.
	 * 
	 * @param item {@link Item}.
	 * @param field {@link Field}.
	 * @param value neuer Wert.
	 * @param copier {@link Converter} zum Kopieren des aktuellen Werts.
	 * @param listener {@link ModifyItemFieldListener}.
	 * @param <GItem> Typ des {@link Item}s.
	 * @param <GValue> Typ des Werts.
	 * @throws NullPointerException Wenn bis auf den gegebenen Wert eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends Item<?>, GValue> void modifyField(final GItem item, final Field<? super GItem, GValue> field, final GValue value,
		final Converter<? super GValue, ? extends GValue> copier, final ModifyItemFieldListener listener) throws NullPointerException {
		final GValue oldValue = field.get(item);
		if(Objects.equals(oldValue, value)) return;
		final GValue copyValue = copier.convert(oldValue);
		field.set(item, value);
		listener.onModifyItemField(new ModifyItemFieldEvent(item, field, copyValue, value));
	}

	/**
	 * Diese Methode überführt das gegebene {@link Item} in den gegebenen Status und benachrichtigt bei einer Änderung den gegebenen
	 * {@link ModifyItemStateListener}.
	 * 
	 * @see Item#append()
	 * @see Item#remove()
	 * @see Item#update()
	 * @see ModifyItemStateListener
	 * @param item {@link Item}.
	 * @param state Status ({@link Item#APPEND_STATE}, {@link Item#REMOVE_STATE}, {@link Item#UPDATE_STATE}).
	 * @param listener {@link ModifyItemStateListener}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalStateException Wenn der Übergang in den gegebenen Status unzulässig ist.
	 * @throws IllegalArgumentException Wenn der gegebene Status ungültig ist.
	 */
	public static void modifyState(final Item<?> item, final int state, final ModifyItemStateListener listener) throws NullPointerException,
		IllegalStateException, IllegalArgumentException {
		final int oldState = item.state();
		if(oldState == state) return;
		switch(state){
			case Item.APPEND_STATE:
				item.append();
				break;
			case Item.REMOVE_STATE:
				item.remove();
				break;
			case Item.UPDATE_STATE:
				item.update();
				break;
			default:
				throw new IllegalArgumentException();
		}
		listener.onModifyItemState(new ModifyItemStateEvent(item, oldState, state));
	}

	/**
	 * Diese Methode leert den gegebenen Zielpool und fügt anschließend alle Kopien zu allen Datensätzen des gegebenen Quellpools in den Zielpool ein. Die
	 * Implementation entspricht:
	 * 
	 * <pre>
	 * target.items().clear();
	 * for(GItem item: source)assigner.set(item, target.create());
	 * for(GItem item: source)assigner.assign(item, assigner.get(item));
	 * </pre>
	 * 
	 * @see Assigner
	 * @see Pool
	 * @param <GItem> Typ der Datensätze.
	 * @param assigner {@link Assigner}.
	 * @param source Quellpool.
	 * @param target Zielpool.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends Item<?>> void assignItems(final Assigner<? extends Item<?>> assigner, final Pool<GItem, ?> source, final Pool<GItem, ?> target)
		throws NullPointerException {
		if(assigner == null) throw new NullPointerException();
		target.items().clear();
		for(final GItem item: source){
			assigner.set(item, target.create());
		}
		for(final GItem item: source){
			assigner.assign(item, assigner.get(item));
		}
	}

	/**
	 * Diese Methode leert die gegebenen Zielabbildung und fügt anschließend alle via {@link Assigner#get(Object)} zu den Schlüsseln und Werten der Einträge der
	 * gegebenen Quellabbildung ermittelten Schlüssel-Wert-Paare in die Zielabbildung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(Entry<GKey, GValue> entry: source.entrySet())target.put(assigner.get(entry.getKey()), assigner.get(entry.getValue()));
	 * </pre>
	 * 
	 * @see Assigner
	 * @see Map
	 * @param <GInput> Typ der Eingabe.
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param assigner {@link Assigner}.
	 * @param source Quellabbildung.
	 * @param target Zielabbildung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GKey, GValue> void assignEntries(final Assigner<?> assigner, final Map<GKey, GValue> source, final Map<GKey, GValue> target)
		throws NullPointerException {
		if(assigner == null) throw new NullPointerException();
		target.clear();
		for(final Entry<GKey, GValue> entry: source.entrySet()){
			target.put(assigner.get(entry.getKey()), assigner.get(entry.getValue()));
		}
	}

	/**
	 * Diese Methode leert die gegebenen Zielsammlung und fügt anschließend alle via {@link Assigner#get(Object)} zu den Elemente der gegebenen Quellsammlung
	 * ermittelten Zielobjekte in die Zielsammlung ein. Die Implementation entspricht:
	 * 
	 * <pre>
	 * target.clear();
	 * for(GValue value: source)target.add(assigner.get(value));
	 * </pre>
	 * 
	 * @see Assigner
	 * @see Collection
	 * @param <GValue> Typ der Elemente.
	 * @param assigner {@link Assigner}.
	 * @param source Quellsammlung.
	 * @param target Zielsammlung.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> void assignValues(final Assigner<?> assigner, final Collection<GValue> source, final Collection<GValue> target)
		throws NullPointerException {
		if(assigner == null) throw new NullPointerException();
		target.clear();
		for(final GValue value: source){
			target.add(assigner.get(value));
		}
	}

}
