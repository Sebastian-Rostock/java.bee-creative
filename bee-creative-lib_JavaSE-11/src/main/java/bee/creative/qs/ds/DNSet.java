package bee.creative.qs.ds;

import java.util.Collection;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QS;
import bee.creative.util.Iterables;
import bee.creative.util.Iterator2;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert eine {@link QNSet Menge} von {@link QN Hyperknoten}, die über ein {@link #link() Datenfeld} einem {@link #source()
 * Subjektknoten} bzw. einem {@link #target() Objektknoten} zugeordnet ist.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DNSet extends QNSet, DO {

	/** Diese Methode liefert das Datenfeld, dessen {@link DL#node() Hyperknoten} in den {@link #edges() Hyperkanten} verwendet wird.
	 *
	 * @return Datenfeld. */
	DL link();

	/** Diese Methode liefert die Mengensicht auf alle {@link DL#edges() Hyperkanten des Datenfeldes}, die als {@link QE#subject() Subjektknoten} bzw. als
	 * {@link QE#object() Objektknoten} den dieses Objekts verwenden.
	 *
	 * @return Hyperkanten mit Subjekt- bzw. Objektbindung. */
	QESet edges();

	/** Diese Methode liefert den als {@link QE#object() Objektnoten} der {@link #edges() Hyperkanten} verwendeten {@link QN Hyperknoten} oder {@code null}.
	 *
	 * @return Objektnoten oder {@code null}. */
	QN target();

	/** Diese Methode liefert den als {@link QE#subject() Subjektnoten} der {@link #edges() Hyperkanten} verwendeten {@link QN Hyperknoten} oder {@code null}.
	 *
	 * @return Subjektnoten oder {@code null}. */
	QN source();

	/** Diese Methode ersetzt alle Elemente dieser Menge mit dem gegebenen {@link QN Hyperknoten}. Wenn dieser {@code null} ist, wird die Menge geleert.
	 *
	 * @see #setNodes(Iterable)
	 * @param node Hyperknoten oder {@code null}.
	 * @return {@code true}, wenn die Menge verändert wurde. {@code false} sonst. */
	default boolean setNode(QN node) throws IllegalArgumentException {
		return this.setNodes(node != null ? Iterables.fromItem(node) : Iterables.empty());
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
	 * @see #first()
	 * @see #setNode(QN)
	 * @return Elementsicht. */
	Property2<QN> asNode();

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
				return (int)DNSet.this.size();
			}

			@Override
			public boolean isEmpty() {
				return DNSet.this.isEmpty();
			}

			@Override
			public void clear() {
				DNSet.this.setNodes(Iterables.empty());
			}

			@Override
			public boolean add(QN e) {
				return DNSet.this.putNode(e);
			}

			@Override
			public boolean addAll(Collection<? extends QN> c) {
				return DNSet.this.putNodes(c);
			}

			@Override
			public boolean remove(Object o) {
				return (o instanceof QN) && DNSet.this.popNode((QN)o);
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				@SuppressWarnings ("unchecked")
				var nodes = (Iterable<QN>)Iterables.filter(c, e -> e instanceof QN);
				return DNSet.this.popNodes(nodes);
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				var nodes = DNSet.this.toSet();
				nodes.retainAll(c);
				return DNSet.this.setNodes(nodes);
			}

			@Override
			public boolean contains(Object o) {
				return DNSet.this.toSet().contains(o);
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				return DNSet.this.toSet().containsAll(c);
			}

			@Override
			public Iterator2<QN> iterator() {
				var iter = DNSet.this.iterator();
				return new Iterator2<>() {

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
						DNSet.this.popNode(this.next);
						this.next = null;
					}

					private QN next;

				};
			}

			@Override
			public int hashCode() {
				return DNSet.this.toSet().hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return DNSet.this.toSet().equals(obj);
			}

			@Override
			public Object[] toArray() {
				return DNSet.this.toList().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return DNSet.this.toList().toArray(a);
			}

			@Override
			public String toString() {
				return DNSet.this.toList().toString();
			}

		};
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN#value() Textwert} des ersten bzw. einzigen {@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #asNode()
	 * @see QS#valueTrans()
	 * @return Textwert. */
	default Property2<String> asValue() {
		return this.asNode().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} de r{@link QN Hyperknoten} dieser Menge.
	 *
	 * @see #asNodeSet()
	 * @see QS#valueTrans()
	 * @return Textwerte. */
	default Set2<String> asValueSet() {
		return this.asNodeSet().translate(this.owner().valueTrans());
	}

}
