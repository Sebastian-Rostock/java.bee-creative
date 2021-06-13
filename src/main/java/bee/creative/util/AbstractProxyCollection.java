package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine abstrakte {@link Collection} als Platzhalter. Ihren Inhalt liest sie über {@link #getData(boolean)}. Änderungen am Inhalt
 * werden über {@link #setData(Collection)} geschrieben.
 *
 * @param <GItem> Typ der Elemente.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxyCollection<GItem, GData extends Collection<GItem>> implements Collection<GItem> {

	final class Iter implements Iterator<GItem> {

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

	/** Diese Klasse implementiert {@link AbstractProxyCollection#toCollection(Property)}. */
	static class PropertyCollection<GItem> extends AbstractProxyCollection<GItem, Collection<GItem>> {

		public final Property<Collection<GItem>> property;

		public PropertyCollection(final Property<Collection<GItem>> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		public Collection<GItem> getData(final boolean readonly) {
			return this.property.get();
		}

		@Override
		protected void setData(final Collection<GItem> items) {
			this.property.set(items);
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
	public Iterator<GItem> iterator() {
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

	/** Diese Methode ist eine Abkürzung für {@link AbstractProxyCollection#toCollection(Property) Properties.toCollection(Fields.toProperty(item, field))}. */
	public static <GItem, GEntry> Collection<GEntry> toCollection(final GItem item, final Field<? super GItem, Collection<GEntry>> field)
		throws NullPointerException {
		return AbstractProxyCollection.toCollection(Properties.from(field, item));
	}

	/** Diese Methode gibt eine {@link Collection} zurück, deren Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxyCollection
	 * @param property {@link Property}.
	 * @return {@link Collection}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GItem> Collection<GItem> toCollection(final Property<Collection<GItem>> property) throws NullPointerException {
		return new PropertyCollection<>(property);
	}

}