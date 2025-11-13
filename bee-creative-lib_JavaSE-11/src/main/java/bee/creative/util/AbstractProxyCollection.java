package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;

/** Diese Klasse implementiert eine abstrakte {@link Collection2} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt
 * werden über {@link #setData(Collection)} geschrieben.
 *
 * @param <E> Typ der Elemente.
 * @param <D> Typ des Inhalts. */
public abstract class AbstractProxyCollection<E, D extends Collection<E>> implements Collection2<E> {

	@Override
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public boolean add(E e) {
		var data = this.getData(false);
		if (!data.add(e)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		var data = this.getData(false);
		if (!data.addAll(c)) return false;
		this.setData(data);
		return true;
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
		return new Iter();
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

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #contains(Object)},
	 * {@link #containsAll(Collection)}, {@link #equals(Object)}, {@link #hashCode()}, {@link #iterator()}, {@link #toArray()}, {@link #toArray(Object[])} und
	 * {@link #toString()} angefordert.
	 *
	 * @param readonly {@code true}, wenn der Inhalt nur zum Lesen verwendet wird und eine Kopie damit nicht nötig ist.<br>
	 *        {@code false}, wenn der Inhalt verändert werden könnte und daher ggf. eine Kopie nötig ist.
	 * @return Inhalt. */
	protected abstract D getData(boolean readonly);

	/** Diese Methode setzt den Inhalt. Dieser wurde zuvor über {@link #getData(boolean)} zum Schreiben beschafft und anschließend verändert.
	 *
	 * @param items neuer Inhalt. */
	protected abstract void setData(D items);

	final class Iter extends AbstractIterator<E> {

		@Override
		public boolean hasNext() {
			return this.iter.hasNext();
		}

		@Override
		public E next() {
			return this.iter.next();
		}

		@Override
		public void remove() {
			this.iter.remove();
			AbstractProxyCollection.this.setData(this.data);
		}

		final D data;

		final Iterator<E> iter;

		Iter() {
			this.data = AbstractProxyCollection.this.getData(false);
			this.iter = this.data.iterator();
		}

	}

}