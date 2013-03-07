package bee.creative.compact;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import bee.creative.array.Array;
import bee.creative.array.CompactArray;

/**
 * Diese Klasse implementiert eine {@link List}, deren Daten in einem Array verwaltet werden. Der Speicherverbrauch einer {@link CompactList} liegt bei ca. {@code 100%} des Speicherverbrauchs einer {@link ArrayList}.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und liegen bei einer mittigen Ausrichtung im Mittel bei {@code 50%} der Rechenzeit, die eine {@link ArrayList} dazu benötigen würde.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 */
public class CompactList<GItem> extends CompactCollection<GItem> implements List<GItem>, RandomAccess {

	/**
	 * Diese Klasse implementiert eine {@link List} als modifizierbare Sicht auf die Werte.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Array#values()
	 * @param <GItem> Typ der Werte ( {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder {@link Boolean}).
	 */
	protected static final class CompactListItems<GItem> extends AbstractList<GItem> implements RandomAccess {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactList<GItem> owner;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactListItems(final CompactList<GItem> owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.owner.customRemove(fromIndex, toIndex - fromIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			return this.owner.getItem(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem set(final int index, final GItem item) {
			return this.owner.set(index, item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GItem value) {
			this.owner.add(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem remove(final int index) {
			return this.owner.remove(index);
		}

	}

	/**
	 * Dieser Konstruktor initialisiert die {@link List}.
	 */
	public CompactList() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link List} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactList(final int capacity) {
		super(capacity);
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link List} mit den gegebenen Elementen.
	 * 
	 * @see Collection#addAll(Collection)
	 * @see CompactData#allocate(int)
	 * @param collection Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public CompactList(final Collection<? extends GItem> collection) {
		super(collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final int customItemIndex(final Object item) {
		return this.indexOf(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final boolean customItemEquals(final Object key, final int hash, final Object item) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final int customItemCompare(final Object key, final int hash, final Object item) {
		return 0;
	}

	/**
	 * Diese Methode gibt die relative Ausrichtungsposition der Elemente im Array zurück. Bei der relativen Ausrichtungsposition {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays beschleunigt wird.
	 * 
	 * @see CompactArray#getAlignment()
	 * @return relative Ausrichtungsposition ({@code 0..1}).
	 */
	public final float getAlignment() {
		return this.items.getAlignment();
	}

	/**
	 * Diese Methode setzt die relative Ausrichtungsposition der Elemente im Array. Bei der relativen Ausrichtungsposition {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays beschleunigt wird.
	 * 
	 * @see CompactArray#setAlignment(float)
	 * @param alignment relative Ausrichtungsposition ({@code 0..1}).
	 * @throws IllegalArgumentException Wenn die gegebene relative Ausrichtungsposition kleiner {@code 0}, größer {@code 1} ist oder {@link Float#NaN}.
	 */
	public final void setAlignment(final float alignment) throws IllegalArgumentException {
		this.items.setAlignment(alignment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GItem get(final int index) {
		return this.getItem(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GItem set(final int index, final GItem element) {
		final GItem item = this.getItem(index);
		this.setItem(index, element);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean add(final GItem e) {
		this.add(this.size(), e);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void add(final int index, final GItem element) {
		this.customInsert(index, 1);
		this.setItem(index, element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean addAll(final Collection<? extends GItem> collection) {
		return this.addAll(this.size(), collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean addAll(final int index, final Collection<? extends GItem> collection) {
		if(collection == null) throw new NullPointerException();
		if(collection.isEmpty()) return false;
		final Object[] items = collection.toArray();
		final int count = items.length;
		if(count == 0) return false;
		this.customInsert(index, count);
		this.setItems(index, items);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GItem remove(final int index) {
		final GItem item = this.getItem(index);
		this.customRemove(index, 1);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int indexOf(final Object item) {
		return this.items.values().indexOf(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int lastIndexOf(final Object item) {
		return this.items.values().lastIndexOf(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ListIterator<GItem> listIterator() {
		return new CompactListItems<GItem>(this).listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ListIterator<GItem> listIterator(final int index) {
		return new CompactListItems<GItem>(this).listIterator(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<GItem> subList(final int fromIndex, final int toIndex) {
		return new CompactListItems<GItem>(this).subList(fromIndex, toIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return new CompactListItems<GItem>(this).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		return new CompactListItems<GItem>(this).equals(object);
	}

}