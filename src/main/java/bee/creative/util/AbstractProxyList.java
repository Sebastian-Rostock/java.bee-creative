package bee.creative.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** Diese Klasse implementiert eine abstrakte {@link List} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(List)} geschrieben.
 *
 * @param <GItem> Typ der Elemente.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxyList<GItem, GData extends List<GItem>> extends AbstractList<GItem> {

	@SuppressWarnings ("javadoc")
	class Iter implements ListIterator<GItem> {

		final GData data;

		final ListIterator<GItem> iter;

		Iter(final int index) {
			this.data = AbstractProxyList.this.getData(false);
			this.iter = this.data.listIterator(index);
		}

		@Override
		public boolean hasNext() {
			return this.iter.hasNext();
		}

		@Override
		public GItem next() {
			return this.iter.next();
		}

		@Override
		public boolean hasPrevious() {
			return this.iter.hasPrevious();
		}

		@Override
		public GItem previous() {
			return this.iter.previous();
		}

		@Override
		public int nextIndex() {
			return this.iter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return this.iter.previousIndex();
		}

		@Override
		public void remove() {
			this.iter.remove();
			AbstractProxyList.this.setData(this.data);
		}

		@Override
		public void set(final GItem e) {
			this.iter.set(e);
			AbstractProxyList.this.setData(this.data);
		}

		@Override
		public void add(final GItem e) {
			this.iter.add(e);
			AbstractProxyList.this.setData(this.data);
		}

	}

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #get(int)},
	 * {@link #indexOf(Object)}, {@link #lastIndexOf(Object)}, {@link #contains(Object)}, {@link #containsAll(Collection)}, {@link #equals(Object)},
	 * {@link #hashCode()}, {@link #iterator()}, {@link #toArray()}, {@link #toArray(Object[])} und {@link #toString()} angefordert.
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
	protected void removeRange(final int fromIndex, final int toIndex) {
		final GData data = this.getData(false);
		data.subList(fromIndex, toIndex).clear();
		this.setData(data);
	}

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public GItem get(final int index) {
		return this.getData(true).get(index);
	}

	@Override
	public GItem set(final int index, final GItem e) {
		final GData data = this.getData(false);
		final GItem result = data.set(index, e);
		this.setData(data);
		return result;
	}

	@Override
	public boolean add(final GItem e) {
		final GData data = this.getData(false);
		if (!data.add(e)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public void add(final int index, final GItem c) {
		final GData data = this.getData(false);
		data.add(index, c);
		this.setData(data);
	}

	@Override
	public boolean addAll(final Collection<? extends GItem> c) {
		final GData data = this.getData(false);
		if (!data.addAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends GItem> c) {
		final GData data = this.getData(false);
		if (!data.addAll(index, c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public GItem remove(final int index) {
		final GData data = this.getData(false);
		final GItem result = data.remove(index);
		this.setData(data);
		return result;
	}

	@Override
	public boolean remove(final Object o) {
		final GData data = this.getData(false);
		if (!data.remove(o)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		final GData data = this.getData(false);
		if (!data.removeAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean contains(final Object item) {
		return this.getData(true).contains(item);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.getData(true).containsAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		final GData data = this.getData(false);
		if (!data.retainAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public int indexOf(final Object o) {
		return this.getData(true).indexOf(o);
	}

	@Override
	public int lastIndexOf(final Object o) {
		return this.getData(true).lastIndexOf(o);
	}

	@Override
	public ListIterator<GItem> listIterator() {
		return this.listIterator(0);
	}

	@Override
	public ListIterator<GItem> listIterator(final int index) {
		return new Iter(index);
	}

	@Override
	public void clear() {
		final GData data = this.getData(false);
		data.clear();
		this.setData(data);
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
	public Iterator<GItem> iterator() {
		return this.listIterator(0);
	}

	@Override
	public Object[] toArray() {
		return this.getData(true).toArray();
	}

	@Override
	public <T> T[] toArray(final T[] result) {
		return this.getData(true).toArray(result);
	}

	@Override
	public String toString() {
		return this.getData(true).toString();
	}

}