package bee.creative.compact;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Diese Klasse implementiert eine {@link List}, deren Daten in einem Array verwaltet werden. Der Speicherverbrauch
 * einer {@link CompactList} liegt bei ca. {@code 100%} des Speicherverbrauchs einer {@link ArrayList} .
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und liegen bei
 * einer mittigen Ausrichtung im Mittel bei {@code 50%} der Rechenzeit, die eine {@link ArrayList} dazu benötigen würde.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 */
public final class CompactList<GItem> extends CompactCollection<GItem> implements List<GItem>, RandomAccess {

	/**
	 * Diese Klasse implementiert eine {@link AbstractList} mit {@link RandomAccess}, die ihre Schnittstelle an eine
	 * {@link CompactList} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	protected final class ItemList<GItem> extends AbstractList<GItem> implements RandomAccess {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		private final CompactList<GItem> owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 */
		public ItemList(final CompactList<GItem> owner) {
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			return this.owner.get(index);
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
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.owner.customRemove(this.owner.from + fromIndex, toIndex - fromIndex);
		}

	}

	/**
	 * Dieses Feld speichert die relative Ausrichtungsposition.
	 */
	private float alignment = 0.5f;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemIndex(final Object item) {
		return this.indexOf(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customItemEquals(final Object key, final int hash, final Object item) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customAlignment(final int space) {
		return (int)(space * this.alignment);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link List}.
	 */
	public CompactList() {
		super();

	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link List} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactList(final int capacity) {
		super(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link List} mit den gegebenen Elementen.
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
	 * Diese Methode gibt die relative Ausrichtungsposition der Elemente im Array zurück. Bei der relativen
	 * Ausrichtungsposition {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen
	 * von Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das
	 * gegenteil, da hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen
	 * am Anfang des Arrays beschleunigt wird.
	 * 
	 * @return relative Ausrichtungsposition ({@code 0..1}).
	 */
	public float getAlignment() {
		return this.alignment;
	}

	/**
	 * Diese Methode setzt die relative Ausrichtungsposition der Elemente im Array. Bei der relativen Ausrichtungsposition
	 * {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende
	 * des Arrays beschleunigt wird. Für die relative Ausrichtungsposition {@code 1} gilt das gegenteil, da hier die
	 * Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des Arrays
	 * beschleunigt wird.
	 * 
	 * @param alignment relative Ausrichtungsposition ({@code 0..1}).
	 * @throws IllegalArgumentException Wenn die gegebene relative Ausrichtungsposition kleiner {@code 0}, größer
	 *         {@code 1} ist oder {@link Float#NaN}.
	 */
	public void setAlignment(final float alignment) throws IllegalArgumentException {
		if(!((alignment >= 0f) && (alignment <= 1f))) throw new IllegalArgumentException();
		this.alignment = alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GItem get(final int index) {
		if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
		return this.getItem(this.from + index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GItem set(final int index, final GItem element) {
		if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
		final int i = this.from + index;
		final GItem item = this.getItem(i);
		this.setItem(i, element);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(final GItem e) {
		this.add(this.size, e);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final GItem element) {
		if((index < 0) || (index > this.size)) throw new IndexOutOfBoundsException();
		this.customInsert(this.from + index, 1);
		this.setItem(this.from + index, element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(final Collection<? extends GItem> collection) {
		return this.addAll(this.size, collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(final int index, final Collection<? extends GItem> collection) {
		if((index < 0) || (index > this.size)) throw new IndexOutOfBoundsException();
		final int count = collection.size();
		if(count == 0) return false;
		this.customInsert(this.from + index, count);
		final Iterator<? extends GItem> iterator = collection.iterator();
		int from = this.from + index;
		final int last = from + count;
		while((from < last) && iterator.hasNext()){
			this.setItem(from++, iterator.next());
		}
		while(from < last){
			this.setItem(from++, null);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GItem remove(final int index) {
		if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
		final int i = this.from + index;
		final GItem item = this.getItem(i);
		this.customRemove(i, 1);
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(final Object o) {
		final int index = CompactData.indexOf(this.list, this.from, this.size, o);
		if(index < 0) return -1;
		return index - this.from;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(final Object o) {
		if(o == null){
			for(int from = this.from - 1, last = from + this.size; from < last; last--){
				if(this.getItem(last) == null) return last - this.from;
			}
		}else{
			for(int from = this.from - 1, last = from + this.size; from < last; last--){
				if(o.equals(this.getItem(last))) return last - this.from;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GItem> iterator() {
		return new CompactCollectionAscendingIterator<GItem>(this, this.firstIndex(), this.lastIndex() + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<GItem> listIterator() {
		return new ItemList<GItem>(this).listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<GItem> listIterator(final int index) {
		return new ItemList<GItem>(this).listIterator(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GItem> subList(final int fromIndex, final int toIndex) {
		return new ItemList<GItem>(this).subList(fromIndex, toIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return new ItemList<GItem>(this).toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return new ItemList<GItem>(this).toArray(a);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new ItemList<GItem>(this).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof List<?>)) return false;
		return new ItemList<GItem>(this).equals(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new ItemList<GItem>(this).toString();
	}

}