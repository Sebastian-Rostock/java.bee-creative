package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** Diese Klasse implementiert ein auf {@link AbstractHashData} aufbauendes {@link Set}.
 * <p>
 * <b>Achtung:</b> Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Elemente erfolgt nicht wie in
 * {@link Set} beschrieben über die Methoden der Elemente, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEqualsKey(int, Object)}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractHashSet<GItem> extends AbstractHashData<GItem, GItem> implements Set<GItem> {

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann, und gibt {@code this} zurück.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public AbstractHashSet<GItem> allocate(final int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
		return this;
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public AbstractHashSet<GItem> compact() {
		this.allocateImpl(this.countImpl());
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected GItem customGetValue(final int entryIndex) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected GItem customSetValue(final int entryIndex, final GItem value) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.countImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.countImpl() == 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final Object item) {
		return this.hasKeyImpl(item);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> iterator() {
		return this.newKeysIteratorImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(final GItem item) {
		return this.putKeyImpl(item);
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object item) {
		return this.popKeyImpl(item);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(final Collection<?> items) {
		return Iterators.containsAll(this, items.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends GItem> items) {
		return Iterators.addAll(this, items.iterator());
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(final Collection<?> items) {
		return Iterators.retainAll(this.iterator(), items);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> items) {
		return this.newKeysImpl().removeAll(items);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearImpl();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.newKeysImpl().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Set<?>)) return false;
		final Set<?> that = (Set<?>)object;
		if (that.size() != this.size()) return false;
		return this.containsAll(that);
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.newKeysImpl().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <GItem> GItem[] toArray(final GItem[] result) {
		return this.newKeysImpl().toArray(result);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.newKeysImpl().toString();
	}

}
