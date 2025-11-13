package bee.creative.qs.ds;

import java.util.Collection;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Iterables;
import bee.creative.util.Iterator3;
import bee.creative.util.Properties;
import bee.creative.util.Property3;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert eine veränderbare {@link QNSet Menge} von {@link QN Hyperknoten}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DSNSet extends QNSet {

	default QN getNode() {
		return this.first();
	}

	/** Diese Methode ersetzt alle Elemente dieser Menge mit dem gegebenen {@link QN Hyperknoten}. Wenn dieser {@code null} ist, wird die Menge geleert.
	 *
	 * @see #setNodes(Iterable)
	 * @param node Hyperknoten oder {@code null}.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	default boolean setNode(QN node) throws IllegalArgumentException {
		return this.setNodes(node != null ? Iterables.fromItem(node) : Iterables.emptyIterable());
	}

	/** Diese Methode ersetzt alle Elemente dieser Menge mit den gegebenen {@link QN Hyperknoten}.
	 *
	 * @param nodes Hyperknoten.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	boolean setNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ergänzt diese Menge um den gegebenen {@link QN Hyperknoten}.
	 *
	 * @see #putNodes(Iterable)
	 * @param node Hyperknoten oder {@code null}.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	default boolean putNode(QN node) throws IllegalArgumentException {
		return (node != null) && this.putNodes(Iterables.fromItem(node));
	}

	/** Diese Methode ergänzt diese Menge um die gegebenen {@link QN Hyperknoten}.
	 *
	 * @param nodes Hyperknoten.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	boolean putNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode entfernt den gegebenen {@link QN Hyperknoten} aus dieser Menge.
	 *
	 * @see #popNodes(Iterable)
	 * @param node Hyperknoten oder {@code null}.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	default boolean popNode(QN node) throws IllegalArgumentException {
		return (node != null) && this.popNodes(Iterables.fromItem(node));
	}

	/** Diese Methode entfernt die gegebenen {@link QN Hyperknoten} aus dieser Menge.
	 *
	 * @param nodes Hyperknoten.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	boolean popNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode erlaubt Zugriff auf den ersten bzw. einzigen {@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #getNode()
	 * @see #setNode(QN)
	 * @return Elementsicht. */
	default Property3<QN> asNode() {
		return Properties.propertyFrom(this::getNode, this::setNode);
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #size()
	 * @see #isEmpty()
	 * @see #setNodes(Iterable)
	 * @see #putNodes(Iterable)
	 * @see #popNodes(Iterable)
	 * @return Mengensicht. */
	default Set2<QN> asNodeSet() {
		return new Set2<>() {

			@Override
			public int size() {
				return (int)DSNSet.this.size();
			}

			@Override
			public boolean isEmpty() {
				return DSNSet.this.isEmpty();
			}

			@Override
			public void clear() {
				DSNSet.this.setNodes(Iterables.emptyIterable());
			}

			@Override
			public boolean add(QN e) {
				return DSNSet.this.putNode(e);
			}

			@Override
			public boolean addAll(Collection<? extends QN> c) {
				return DSNSet.this.putNodes(c);
			}

			@Override
			public boolean remove(Object o) {
				return (o instanceof QN) && DSNSet.this.popNode((QN)o);
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				@SuppressWarnings ("unchecked")
				var nodes = (Iterable<QN>)Iterables.filter(c, QN.class::isInstance);
				return DSNSet.this.popNodes(nodes);
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				var nodes = DSNSet.this.toSet();
				nodes.retainAll(c);
				return DSNSet.this.setNodes(nodes);
			}

			@Override
			public boolean contains(Object o) {
				return DSNSet.this.toSet().contains(o);
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				return DSNSet.this.toSet().containsAll(c);
			}

			@Override
			public Iterator3<QN> iterator() {
				var iter = DSNSet.this.iterator();
				return new Iterator3<>() {

					@Override
					public QN next() {
						return this.next = iter.next();
					}

					@Override
					public boolean hasNext() {
						return iter.hasNext();
					}

					@Override
					public void remove() {
						if (this.next == null) throw new IllegalStateException();
						DSNSet.this.popNode(this.next);
						this.next = null;
					}

					private QN next;

				};
			}

			@Override
			public int hashCode() {
				return DSNSet.this.toSet().hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return DSNSet.this.toSet().equals(obj);
			}

			@Override
			public Object[] toArray() {
				return DSNSet.this.toList().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return DSNSet.this.toList().toArray(a);
			}

			@Override
			public String toString() {
				return DSNSet.this.toList().toString();
			}

		};
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN#value() Textwert} des ersten bzw. einzigen {@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #asNode()
	 * @see QS#valueTrans()
	 * @return Textwert. */
	default Property3<String> asValue() {
		return this.asNode().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} de r{@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #asNodeSet()
	 * @see QS#valueTrans()
	 * @return Textwerte. */
	default Set2<String> asValueSet() {
		return this.asNodeSet().asTranslatedSet(this.owner().valueTrans());
	}

}
