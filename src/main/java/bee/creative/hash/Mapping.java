package bee.creative.hash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import bee.creative.util.Objects;

class HashMap30<GKey, GValue> extends HashData<GKey, GValue> implements Map<GKey, GValue> {

	public HashMap30() {
		super(true, true);
	}

	public HashMap30(final int capacity) {
		super(true, true);
		this.allocate(capacity);
	}

	public HashMap30(final int capacity, final boolean withHashes) {
		super(true, withHashes);
		this.allocate(capacity);
	}

	public HashMap30(final boolean withHashes) {
		super(true, withHashes);

	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public GValue get(Object key) {
		return null;
	}

	@Override
	public GValue put(GKey key, GValue value) {
		return null;
	}

	@Override
	public GValue remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends GKey, ? extends GValue> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<GKey> keySet() {
		return null;
	}

	@Override
	public Collection<GValue> values() {
		return null;
	}

	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return null;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}

class HashSet30<E> extends HashData<E, E> implements Set<E> {

	public HashSet30(boolean withHashes) {
		super(false, withHashes);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(final Object o) {
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return null;
	}

	@Override
	public boolean add(final E e) {
		return false;
	}

	@Override
	public boolean remove(final Object o) {
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return false;
	}

	/** Removes from this set all of its elements that are contained in the specified collection (optional operation). If the specified collection is also a set,
	 * this operation effectively modifies this set so that its value is the <i>asymmetric set difference</i> of the two sets.
	 * <p>
	 * This implementation determines which is the smaller of this set and the specified collection, by invoking the <tt>size</tt> method on each. If this set has
	 * fewer elements, then the implementation iterates over this set, checking each element returned by the iterator in turn to see if it is contained in the
	 * specified collection. If it is so contained, it is removed from this set with the iterator's <tt>remove</tt> method. If the specified collection has fewer
	 * elements, then the implementation iterates over the specified collection, removing from this set each element returned by the iterator, using this set's
	 * <tt>remove</tt> method.
	 * <p>
	 * Note that this implementation will throw an <tt>UnsupportedOperationException</tt> if the iterator returned by the <tt>iterator</tt> method does not
	 * implement the <tt>remove</tt> method.
	 *
	 * @param c collection containing elements to be removed from this set
	 * @return <tt>true</tt> if this set changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation is not supported by this set
	 * @throws ClassCastException if the class of an element of this set is incompatible with the specified collection
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this set contains a null element and the specified collection does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>), or if the specified collection is null
	 * @see #remove(Object)
	 * @see #contains(Object) */
	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean modified = false;

		if (this.size() > c.size()) {
			for (final Iterator<?> i = c.iterator(); i.hasNext();) {
				modified |= this.remove(i.next());
			}
		} else {
			for (final Iterator<?> i = this.iterator(); i.hasNext();) {
				if (c.contains(i.next())) {
					i.remove();
					modified = true;
				}
			}
		}
		return modified;
	}

	@Override
	public void clear() {
	}

	@Override
	public final int hashCode() {
		int result = 0;
		for (final Object item: this) {
			result += Objects.hash(item);
		}
		return result;
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Set)) return false;
		final Set<?> that = (Set<?>)object;
		if (that.size() != this.size()) return false;
		try {
			return this.containsAll(that);
		} catch (final ClassCastException cause) {} catch (final NullPointerException cause) {}
		return false;
	}

}
