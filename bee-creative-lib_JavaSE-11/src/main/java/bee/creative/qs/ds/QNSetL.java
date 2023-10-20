package bee.creative.qs.ds;

import java.util.Collection;
import java.util.Set;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Iterables;
import bee.creative.util.Iterator2;
import bee.creative.util.Properties;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

public interface QNSetL extends QNSet {

	DL link();

	QESet edges();

	QN target();

	QN source();

	default QN getNode() {
		return this.first();
	}

	default Set<QN> getNodes() {
		return this.toSet();
	}

	default boolean setNode(QN node) {
		return this.setNodes(node != null ? Iterables.fromItem(node) : Iterables.empty());
	}

	boolean setNodes(Iterable<? extends QN> nodes);

	default boolean putNode(QN node) {
		return node != null && this.putNodes(Iterables.fromItem(node));
	}

	boolean putNodes(Iterable<? extends QN> nodes);

	default boolean popNode(QN node) {
		return node != null && this.popNodes(Iterables.fromItem(node));
	}

	boolean popNodes(Iterable<? extends QN> nodes);

	default Set2<QN> asSet() {
		return new Set2<>() {

			@Override
			public int size() {
				return (int)QNSetL.this.size();
			}

			@Override
			public boolean isEmpty() {
				return QNSetL.this.isEmpty();
			}

			@Override
			public void clear() {
				QNSetL.this.setNodes(Iterables.empty());
			}

			@Override
			public boolean add(QN e) {
				return QNSetL.this.putNode(e);
			}

			@Override
			public boolean addAll(Collection<? extends QN> c) {
				return QNSetL.this.putNodes(c);
			}

			@Override
			public boolean remove(Object o) {
				return (o instanceof QN) && QNSetL.this.popNode((QN)o);
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				@SuppressWarnings ("unchecked")
				var nodes = (Iterable<QN>)Iterables.filter(c, e -> e instanceof QN);
				return QNSetL.this.popNodes(nodes);
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				var nodes = QNSetL.this.getNodes();
				nodes.retainAll(c);
				return QNSetL.this.setNodes(nodes);
			}

			@Override
			public boolean contains(Object o) {
				return QNSetL.this.getNodes().contains(o);
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				return QNSetL.this.getNodes().containsAll(c);
			}

			@Override
			public Iterator2<QN> iterator() {
				var iter = QNSetL.this.iterator();
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
						QNSetL.this.popNode(this.next);
						this.next = null;
					}

					private QN next;

				};
			}

			@Override
			public int hashCode() {
				return QNSetL.this.getNodes().hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return QNSetL.this.getNodes().equals(obj);
			}

			@Override
			public Object[] toArray() {
				return QNSetL.this.getNodes().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return QNSetL.this.getNodes().toArray(a);
			}

			@Override
			public String toString() {
				return QNSetL.this.getNodes().toString();
			}

		};
	}

	default Property2<QN> asProp() {
		return Properties.from(this::getNode, this::setNode);
	}

}
