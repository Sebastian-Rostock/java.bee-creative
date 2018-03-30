package bee.creative._dev_.sts;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import bee.creative._dev_.sts.STSEdgeSet.SectionEdgeSet;
import bee.creative._dev_.sts.STSItemSet.ItemIndex;
import bee.creative._dev_.sts.STSNodeSet.ArrayNodeSet;
import bee.creative._dev_.sts.STSNodeSet.SectionNodeSet;
import bee.creative.array.CompactIntegerArray;
import bee.creative.array.IntegerArraySection;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Speicher zur Verwaltung eines Graphe aus {@link STSNode Knoten} und {@link STSEdge Kanten}, bei welchem jeder Knoten einen
 * {@link STSNode#localname() Lokalnamen} bezüglich eines {@link STSNode#namespace() Namensraums} besitzt und jede Kante eine Verbindung dreier Knoten in den
 * Rollen {@link STSEdge#subject() Subjekt}, {@link STSEdge#subject() Prädikat} und {@link STSEdge#subject() Objekt} darstellt.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class STSStore {

	protected static abstract class BaseIterator<GItem> implements Iterator<GItem> {

		final STSStore store;

		public BaseIterator(final STSStore store) {
			this.store = store;
		}

		{}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class EdgeIndexIterator extends BaseIterator<STSEdge> {

		final ItemIndex itemIndex;

		STSEdge next;

		public EdgeIndexIterator(final STSStore store, final ItemIndex itemIndex) {
			super(store);
			this.itemIndex = itemIndex;
			this.next();
		}

		{}

		@Override
		public STSEdge next() {
			final STSEdge next = this.next;
			final int index = this.itemIndex.next();
			this.next = index != Integer.MAX_VALUE ? this.store.customGetEdge(index) : null;
			return next;
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class NodeIndexIterator extends BaseIterator<STSNode> {

		final ItemIndex itemIndex;

		STSNode next;

		public NodeIndexIterator(final STSStore store, final ItemIndex itemIndex) {
			super(store);
			this.itemIndex = itemIndex;
			this.next();
		}

		{}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public STSNode next() {
			final STSNode next = this.next;
			final int index = this.itemIndex.next();
			this.next = index != Integer.MAX_VALUE ? this.store.customGetNode(index) : null;
			return next;
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class NodeObjectGetIterator extends BaseIterator<STSNode> {

		final Iterator<? extends STSNode> nodes;

		public NodeObjectGetIterator(final STSStore store, final Iterator<? extends STSNode> nodes) {
			super(store);
			this.nodes = nodes;
		}

		{}

		@Override
		public boolean hasNext() {
			return this.nodes.hasNext();
		}

		@Override
		public STSNode next() {
			return this.store.getNode(this.nodes.next());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class NodeObjectPutIterator extends NodeObjectGetIterator {

		public NodeObjectPutIterator(final STSStore store, final Iterator<? extends STSNode> nodes) {
			super(store, nodes);
		}

		{}

		@Override
		public STSNode next() {
			return this.store.putNode(this.nodes.next());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class NodePropertyGetIterator extends BaseIterator<STSNode> {

		final Iterator<? extends String> namespaces;

		final Iterator<? extends String> localnames;

		public NodePropertyGetIterator(final STSStore store, final Iterator<? extends String> namespaces, final Iterator<? extends String> localnames) {
			super(store);
			this.namespaces = namespaces;
			this.localnames = localnames;
		}

		{}

		@Override
		public boolean hasNext() {
			return this.namespaces.hasNext() && this.localnames.hasNext();
		}

		@Override
		public STSNode next() {
			return this.store.getNode(this.namespaces.next(), this.localnames.next());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class NodePropertyPutIterator extends NodePropertyGetIterator {

		public NodePropertyPutIterator(final STSStore store, final Iterator<? extends String> namespaces, final Iterator<? extends String> localnames) {
			super(store, namespaces, localnames);
		}

		{}

		@Override
		public STSNode next() {
			return this.store.putNode(this.namespaces.next(), this.localnames.next());
		}

	}

	{}

	private static int nextHash;

	{}

	private static synchronized int newHash() {
		return Objects.hashPush(++STSStore.nextHash, 0);
	}

	public static STSStore newHeapStore() {
		// alles im ram
		return null;
	}

	public static STSStore newFileStore(final File path) {
		// verzeichnis, in welchem die dateien mit festgelegten namen enthalten sind
		return null;
	}

	{}

	final int hash = STSStore.newHash();

	protected final STSEdgeSet emptyEdgeSet = new SectionEdgeSet(this, 0, 0);

	protected final STSNodeSet emptyNodeSet = new SectionNodeSet(this, 0, 0);

	{}

	public STSNode getNode(final STSNode node) throws NullPointerException {
		if (this == node.store) return node;
		return this.getNode(node.namespace(), node.localname());
	}

	/** Diese Methode gibt den Knoten mit den gegebenen Merkmalen zurück.
	 *
	 * @param namespace Namensraum.
	 * @param localname Lokalname.
	 * @return Knoten oder {@code null}. */
	public abstract STSNode getNode(String namespace, String localname) throws NullPointerException;

	public STSNodeSet getNodeSet(final STSNodeSet nodes) throws NullPointerException {
		if (this == nodes.store) return nodes;
		return this.getNodeSetImpl(new NodeObjectGetIterator(this, nodes.iterator()));
	}

	public STSNodeSet getNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) return this.getNodeSet((STSNodeSet)nodes);
		return this.getNodeSetImpl(new NodeObjectGetIterator(this, nodes.iterator()));
	}

	public STSNodeSet getNodeSet(final Iterable<? extends String> namespaces, final Iterable<? extends String> localnames) throws NullPointerException {
		return this.getNodeSetImpl(new NodePropertyGetIterator(this, namespaces.iterator(), localnames.iterator()));
	}

	public STSNode putNode(final STSNode node) throws NullPointerException {
		if (this == node.store) return node;
		return this.putNode(node.namespace(), node.localname());
	}

	public abstract STSNode putNode(String namespace, String localname) throws NullPointerException;

	public STSNodeSet putNodeSet(final STSNodeSet nodes) throws NullPointerException {
		if (this == nodes.store) return nodes;
		return this.getNodeSetImpl(new NodeObjectPutIterator(this, nodes.iterator()));
	}

	public STSNodeSet putNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) return this.getNodeSet((STSNodeSet)nodes);
		return this.getNodeSetImpl(new NodeObjectPutIterator(this, nodes.iterator()));
	}

	public STSNodeSet putNodeSet(final Iterable<? extends String> namespaces, final Iterable<? extends String> localnames) throws NullPointerException {
		return this.getNodeSetImpl(new NodePropertyPutIterator(this, namespaces.iterator(), localnames.iterator()));
	}

	public abstract STSNodeSet selectNodeSet(String namespaceFilter, String localnameFilter);

	{}

	protected CompactIntegerArray setupArray() {
		final CompactIntegerArray result = new CompactIntegerArray();
		result.setAlignment(0);
		return result;
	}

	protected IntegerArraySection cleanupArray(final CompactIntegerArray result) {
		final int[] array = result.array();
		final int startIndex = result.startIndex(), finalIndex = result.finalIndex();
		Arrays.sort(array, startIndex, finalIndex);
		int sourceIndex = startIndex, targetIndex = startIndex, target = -1;
		while (sourceIndex < finalIndex) {
			final int source = array[sourceIndex];
			if (source != target) {
				array[targetIndex++] = target = source;
			}
			sourceIndex++;
		}
		return IntegerArraySection.from(Arrays.copyOfRange(array, startIndex, targetIndex));
	}

	protected final STSNodeSet getNodeSetImpl(final Iterator<? extends STSNode> nodes) throws NullPointerException {
		final CompactIntegerArray result = this.setupArray();
		while (nodes.hasNext()) {
			final STSNode item = nodes.next();
			if (item != null) {
				result.add(item.index);
			}
		}
		return this.customGetNodeSet(this.cleanupArray(result));
	}

	{}

	protected final STSEdgeSet getEdgeSetImpl(final STSEdge edge) {
		if (edge == null) return this.emptyEdgeSet;
		return new SectionEdgeSet(this, edge.index, 1);
	}

	protected final STSNodeSet getNodeSetImpl(final STSNode edge) {
		if (edge == null) return this.emptyNodeSet;
		return new SectionNodeSet(this, edge.index, 1);
	}

	/** Diese Methode gibt die Position der ersten Kante zurück.
	 *
	 * @return erste Kantenposition. */
	protected int customGetEdgeIndex() {
		return 0;
	}

	{}

	/** Diese Methode liefert das Ergebnis von {@link #customSelectObjectEdgeSet(int)} zum gegebenen {@link STSEdge#object() Objektknoten} bzw. {@code null}, wenn
	 * der Knoten nicht von diesem Graphspeicher verwaltet wird.
	 *
	 * @param objectFilter Objekt oder {@code null}.
	 * @return Kantenmenge oder {@code null}. */
	protected final STSEdgeSet selectObjectEdgeSetImpl(final STSNode objectFilter) {
		if ((objectFilter == null) || (objectFilter.store != this)) return null;
		return this.customSelectObjectEdgeSet(objectFilter.index);
	}

	/** Diese Methode liefert das Ergebnis von {@link #customSelectSubjectEdgeSet(int)} zum gegebenen {@link STSEdge#subject() Subjektknoten} bzw. {@code null},
	 * wenn der Knoten nicht von diesem Graphspeicher verwaltet wird.
	 *
	 * @param subjectFilter Subjekt oder {@code null}.
	 * @return Kantenmenge oder {@code null}. */
	protected final STSEdgeSet selectSubjectEdgeSetImpl(final STSNode subjectFilter) {
		if ((subjectFilter == null) || (subjectFilter.store != this)) return null;
		return this.customSelectSubjectEdgeSet(subjectFilter.index);
	}

	/** Diese Methode liefert das Ergebnis von {@link #customSelectObjectEdgeSet(int)} zum gegebenen {@link STSEdge#predicate() Prädikatknoten} bzw. {@code null},
	 * wenn der Knoten nicht von diesem Graphspeicher verwaltet wird.
	 * 
	 * @param predicateFilter Prädikat oder {@code null}.
	 * @return Kantenmenge oder {@code null}. */
	protected final STSEdgeSet selectPredicateEdgeSetImpl(final STSNode predicateFilter) {
		if ((predicateFilter == null) || (predicateFilter.store != this)) return null;
		return this.customSelectPredicateEdgeSet(predicateFilter.index);
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgeObject(int) Objekt} zurück.
	 *
	 * @param objectIndex {@link STSItem#index Position} des {@link STSEdge#object() Objektknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectObjectEdgeSet(int objectIndex);

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgeSubject(int) Subjekt} zurück.
	 *
	 * @param subjectIndex {@link STSItem#index Position} des {@link STSEdge#subject() Subjektknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectSubjectEdgeSet(int subjectIndex);

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgePredicate(int) Prädikat} zurück.
	 *
	 * @param predicateIndex {@link STSItem#index Position} des {@link STSEdge#predicate() Prädikatknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectPredicateEdgeSet(int predicateIndex);

	{}

	public abstract STSEdge getEdge(STSNode s, STSNode p, STSNode o);

	public abstract STSEdge putEdge(STSNode s, STSNode p, STSNode o);

	public abstract STSEdge[] putEdges(STSEdge... edges);

	public abstract STSEdge[] putEdges(STSNode... edges);

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} zurück, in denen die gegebenen Komponenten enthalten sind. Für uneingeschränkte Komponenten ist
	 * {@code null} anzugeben.
	 *
	 * @param subjectFilter Subjektfilter oder {@code null}.
	 * @param predicateFilter Prädikatfilter oder {@code null}.
	 * @param objectFilter Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	public STSEdgeSet selectEdgeSet(final STSNode subjectFilter, final STSNode predicateFilter, final STSNode objectFilter) {
		if ((subjectFilter != null) && (predicateFilter != null) && (objectFilter != null))
			return this.getEdgeSetImpl(this.getEdge(subjectFilter, predicateFilter, objectFilter));
		final STSEdgeSet objectSet = this.selectObjectEdgeSetImpl(objectFilter);
		final STSEdgeSet subjectSet = this.selectSubjectEdgeSetImpl(subjectFilter);
		final STSEdgeSet predicateSet = predicateFilter != null ? this.customSelectPredicateEdgeSet(predicateFilter) : null;

	}

	protected final STSEdgeSet selectEdgeSetImpl(final STSEdgeSet subjectFilter, final STSEdgeSet predicateFilter, final STSEdgeSet objectFilter) {
		if (subjectFilter != null) {
			if (predicateFilter != null) {
				if (objectFilter != null) return subjectFilter.toIntersection(predicateFilter).toIntersection(objectFilter);
				return subjectFilter.toIntersection(predicateFilter);
			} else {
				if (objectFilter != null) return subjectFilter.toIntersection(objectFilter);
				return subjectFilter;
			}
		} else {
			if (predicateFilter != null) {
				if (objectFilter != null) return predicateFilter.toIntersection(objectFilter);
				return predicateFilter;
			} else {
				if (objectFilter != null) return objectFilter;
				return this.getEdgeSet();
			}
		}
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge aller verwalteten Kanten} zurück.
	 *
	 * @return Kantenmenge. */
	public STSEdgeSet getEdgeSet() {
		return new SectionEdgeSet(this, this.customGetEdgeIndex(), this.customGetEdgeCount());
	}

	/** Diese Methode gibt den {@link Iterator} über die Assoziationen zurück, in denen die gegebenen Komponenten enthalten sind.<br>
	 * Eine Komponenten gilt als nicht eingeschränkt, wenn das entsprechende Filterkriterium {@code null} ist.
	 *
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	public abstract Iterator<STSEdge> edgeIterator(Set<STSNode> s, Set<STSNode> p, Set<STSNode> o);

	protected STSEdge customGetEdge(final int index) {
		return new STSEdge(this, index);
	}

	protected abstract int customGetEdgeCount();

	protected Iterator<STSEdge> customGetEdgeIterator(final ItemIndex itemIndex) {
		return new EdgeIndexIterator(this, itemIndex);
	}

	protected abstract int customGetEdgeObject(int index);

	protected abstract int customGetEdgeSubject(int index);

	protected abstract int customGetEdgePredicate(int index);

	protected STSNode customGetNode(final int index) {
		return new STSNode(this, index);
	}

	protected STSNodeSet customGetNodeSet(final IntegerArraySection indexArray) {
		return new ArrayNodeSet(this, indexArray);
	}

	protected abstract int customGetNodeCount();

	protected Iterator<STSNode> customGetNodeIterator(final ItemIndex itemIndex) {
		return new NodeIndexIterator(this, itemIndex);
	}

	protected abstract String customGetNodeLocalname(int index);

	protected abstract String customGetNodeNamespace(int index);

}
