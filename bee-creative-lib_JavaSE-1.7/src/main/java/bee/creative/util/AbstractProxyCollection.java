package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;

/** Diese Klasse implementiert eine abstrakte {@link Collection2} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt
 * werden über {@link #setData(Collection)} geschrieben.
 *
 * @param <GItem> Typ der Elemente.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxyCollection<GItem, GData extends Collection<GItem>> implements Collection2<GItem> {

	final class Iter extends AbstractIterator<GItem> {

		final GData data;

		final Iterator<GItem> iter;

		Iter() {
			this.data = AbstractProxyCollection.this.getData(false);
			this.iter = this.data.iterator();
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
		public void remove() {
			this.iter.remove();
			AbstractProxyCollection.this.setData(this.data);
		}

	}

	/** Diese Methode gibt den Inhalt zum Lesen bzw. Schreiben zurück. Zum Lesen wird er nur in {@link #size()}, {@link #isEmpty()}, {@link #contains(Object)},
	 * {@link #containsAll(Collection)}, {@link #equals(Object)}, {@link #hashCode()}, {@link #iterator()}, {@link #toArray()}, {@link #toArray(Object[])} und
	 * {@link #toString()} angefordert.
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
	public int size() {
		return this.getData(true).size();
	}

	@Override
	public boolean isEmpty() {
		return this.getData(true).isEmpty();
	}

	@Override
	public boolean add(final GItem e) {
		final GData data = this.getData(false);
		if (!data.add(e)) return false;
		this.setData(data);
		return true;
	}

	@Override
	public boolean addAll(final Collection<? extends GItem> c) {
		final GData data = this.getData(false);
		if (!data.addAll(c)) return false;
		this.setData(data);
		return true;
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
	public Iterator2<GItem> iterator() {
		return new Iter();
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