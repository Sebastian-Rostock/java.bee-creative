package bee.creative.util;

import static bee.creative.util.Iterators.iteratorFrom;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/** Diese Klasse implementiert eine abstrakte {@link List2} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(List)} geschrieben.
 *
 * @param <E> Typ der Elemente.
 * @param <D> Typ des Inhalts. */
public abstract class AbstractProxyList<E, D extends List<E>> extends AbstractList2<E> {

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public E get(int index) {
		return this.getData(true).get(index);
	}

	@Override
	public E set(int index, E e) {
		var data = this.getData(false);
		var result = data.set(index, e);
		this.setData(data);
		return result;
	}

	@Override
	public boolean add(E e) {
		var data = this.getData(false);
		if (!data.add(e)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public void add(int index, E c) {
		var data = this.getData(false);
		data.add(index, c);
		this.setData(data);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		var data = this.getData(false);
		if (!data.addAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		var data = this.getData(false);
		if (!data.addAll(index, c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public E remove(int index) {
		var data = this.getData(false);
		var result = data.remove(index);
		this.setData(data);
		return result;
	}

	@Override
	public boolean remove(Object o) {
		var data = this.getData(false);
		if (!data.remove(o)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		var data = this.getData(false);
		if (!data.removeAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean contains(Object item) {
		return this.getData(true).contains(item);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.getData(true).containsAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		var data = this.getData(false);
		if (!data.retainAll(c)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public int indexOf(Object o) {
		return this.getData(true).indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.getData(true).lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new Iter(index);
	}

	@Override
	public void clear() {
		var data = this.getData(false);
		data.clear();
		this.setData(data);
	}

	@Override
	public int hashCode() {
		return this.getData(true).hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this.getData(true).equals(object);
	}

	@Override
	public Iterator3<E> iterator() {
		return iteratorFrom(this.listIterator(0));
	}

	@Override
	public Object[] toArray() {
		return this.getData(true).toArray();
	}

	@Override
	public <T> T[] toArray(T[] result) {
		return this.getData(true).toArray(result);
	}

	@Override
	public String toString() {
		return this.getData(true).toString();
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if (fromIndex == toIndex) return;
		var data = this.getData(false);
		data.subList(fromIndex, toIndex).clear();
		this.setData(data);
	}

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #get(int)},
	 * {@link #indexOf(Object)}, {@link #lastIndexOf(Object)}, {@link #contains(Object)}, {@link #containsAll(Collection)}, {@link #equals(Object)},
	 * {@link #hashCode()}, {@link #iterator()}, {@link #toArray()}, {@link #toArray(Object[])} und {@link #toString()} angefordert.
	 *
	 * @param readonly {@code true}, wenn der Inhalt nur zum Lesen verwendet wird und eine Kopie damit nicht nötig ist.<br>
	 *        {@code false}, wenn der Inhalt verändert werden könnte und daher ggf. eine Kopie nötig ist.
	 * @return Inhalt. */
	protected abstract D getData(boolean readonly);

	/** Diese Methode setzt den Inhalt. Dieser wurde zuvor über {@link #getData(boolean)} zum Schreiben beschafft und anschließend verändert.
	 *
	 * @param items neuer Inhalt. */
	protected abstract void setData(D items);

	class Iter implements ListIterator<E> {

		@Override
		public boolean hasNext() {
			return this.iter.hasNext();
		}

		@Override
		public E next() {
			return this.iter.next();
		}

		@Override
		public boolean hasPrevious() {
			return this.iter.hasPrevious();
		}

		@Override
		public E previous() {
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
		public void set(E e) {
			this.iter.set(e);
			AbstractProxyList.this.setData(this.data);
		}

		@Override
		public void add(E e) {
			this.iter.add(e);
			AbstractProxyList.this.setData(this.data);
		}

		final D data;

		final ListIterator<E> iter;

		Iter(int index) {
			this.data = AbstractProxyList.this.getData(false);
			this.iter = this.data.listIterator(index);
		}

	}

}