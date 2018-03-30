package bee.creative._dev_.sts;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import bee.creative._dev_.sts.STSEdgeSet.SectionEdgeSet;
import bee.creative._dev_.sts.STSItemSet.ItemIndex;
import bee.creative._dev_.sts.STSNodeSet.ArrayNodeSet;
import bee.creative._dev_.sts.STSNodeSet.SectionNodeSet;
import bee.creative.array.CompactIntegerArray;
import bee.creative.array.IntegerArraySection;
import bee.creative.util.Getter;
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

	/** Dieses Feld speichert die leere Kantenmenge. */
	protected final STSEdgeSet emptyEdgeSet = new SectionEdgeSet(this, 0, 0);

	/** Dieses Feld speichert die leere Knotenmenge. */
	protected final STSNodeSet emptyNodeSet = new SectionNodeSet(this, 0, 0);

	/** Dieses Feld speichert den {@link Getter} zu {@link #customSelectObjectEdgeSet(int)}. */
	protected final Getter<STSNode, STSEdgeSet> selectObjectEdgeSetGetter = new Getter<STSNode, STSEdgeSet>() {

		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectObjectEdgeSet(input.index);
		}

	};

	/** Dieses Feld speichert den {@link Getter} zu {@link #customSelectSubjectEdgeSet(int)}. */
	protected final Getter<STSNode, STSEdgeSet> selectSubjectEdgeSetGetter = new Getter<STSNode, STSEdgeSet>() {

		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectSubjectEdgeSet(input.index);
		}

	};

	/** Dieses Feld speichert den {@link Getter} zu {@link #customSelectPredicateEdgeSet(int)}. */
	protected final Getter<STSNode, STSEdgeSet> selectPredicateEdgeSetGetter = new Getter<STSNode, STSEdgeSet>() {

		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectPredicateEdgeSet(input.index);
		}

	};

	{}

	public STSEdge getEdge(final STSEdge edge) throws NullPointerException {
		return this.contains(edge) ? edge : this.getEdge(edge.subject(), edge.predicate(), edge.object());
	}

	public STSEdge getEdge(final STSNode subject, final STSNode predicate, final STSNode object) throws NullPointerException {
		final STSNode subjectNode = this.getNode(subject);
		if (subjectNode == null) return null;
		final STSNode predicateNode = this.getNode(predicate);
		if (predicateNode == null) return null;
		final STSNode objectNode = this.getNode(object);
		if (objectNode == null) return null;
		return this.customGetEdge(subjectNode.index, predicateNode.index, objectNode.index);
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge aller verwalteten Kanten} zurück.
	 *
	 * @return Kantenmenge. */
	public STSEdgeSet getEdgeSet() {
		return new SectionEdgeSet(this, this.customGetEdgeIndex(), this.customGetEdgeCount());
	}

	{}
	/** Diese Methode gibt den Knoten mit den Merkmalen des gegebenen zurück.
	 *
	 * @see #getNode(String, String)
	 * @param node Knoten.
	 * @return Knoten oder {@code null}.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist. */
	public STSNode getNode(final STSNode node) throws NullPointerException {
		return this.contains(node) ? node : this.getNode(node.namespace(), node.localname());
	}

	/** Diese Methode gibt den Knoten mit den gegebenen Merkmalen zurück.
	 *
	 * @param namespace Namensraum.
	 * @param localname Lokalname.
	 * @return Knoten oder {@code null}.
	 * @throws NullPointerException Wenn {@code namespace} bzw. {@code localname} {@code null} ist. */
	public abstract STSNode getNode(final String namespace, final String localname) throws NullPointerException;

	/** Diese Methode gibt die {@link STSNodeSet Menge aller verwalteten Knoten} zurück.
	 *
	 * @return Knotenmenge. */
	public STSNodeSet getNodeSet() {
		return new SectionNodeSet(this, this.customGetNodeIndex(), this.customGetNodeCount());
	}

	/** Diese Methode gibt die Megne der Knoten mit den Merkmalen der gegebenen zurück.
	 *
	 * @param nodes Knoten.
	 * @return Knotenmegne.
	 * @throws NullPointerException Wenn {@code nodes} {@code null} ist. */
	public STSNodeSet getNodeSet(final STSNodeSet nodes) throws NullPointerException {
		return this.contains(nodes) ? nodes : this.getNodeSetImpl(new NodeObjectGetIterator(this, nodes.iterator()));
	}

	public STSNodeSet getNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		return nodes instanceof STSNodeSet ? this.getNodeSet((STSNodeSet)nodes) : this.getNodeSetImpl(new NodeObjectGetIterator(this, nodes.iterator()));
	}

	public STSNodeSet getNodeSet(final Iterable<? extends String> namespaces, final Iterable<? extends String> localnames) throws NullPointerException {
		return this.getNodeSetImpl(new NodePropertyGetIterator(this, namespaces.iterator(), localnames.iterator()));
	}

	{}
	public abstract STSEdge putEdge(STSNode s, STSNode p, STSNode o);

	public abstract STSEdge[] putEdges(STSEdge... edges);

	public abstract STSEdge[] putEdges(STSNode... edges);

	{}
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

	{}
	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} zurück, in denen die gegebenen Subjekte, Prädikate bzw. Objekte enthalten sind. Für
	 * uneingeschränkte Komponenten ist {@code null} anzugeben.
	 *
	 * @param subjectFilter Subjektfilter oder {@code null}.
	 * @param predicateFilter Prädikatfilter oder {@code null}.
	 * @param objectFilter Objektfilter oder {@code null}.
	 * @return gefilterte Kanten. */
	public STSEdgeSet selectEdgeSet(final STSNode subjectFilter, final STSNode predicateFilter, final STSNode objectFilter) {
		if ((subjectFilter != null) && (predicateFilter != null) && (objectFilter != null))
			return this.getEdgeSetImpl(this.getEdge(subjectFilter, predicateFilter, objectFilter));
		final STSEdgeSet objectSet = this.selectEdgeSetImpl(objectFilter, this.selectObjectEdgeSetGetter);
		final STSEdgeSet subjectSet = this.selectEdgeSetImpl(subjectFilter, this.selectSubjectEdgeSetGetter);
		final STSEdgeSet predicateSet = this.selectEdgeSetImpl(predicateFilter, this.selectPredicateEdgeSetGetter);
		return this.selectEdgeSetImpl(subjectSet, predicateSet, objectSet);
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} zurück, in denen die gegebenen Subjekte, Prädikate bzw. Objekte enthalten sind. Für
	 * uneingeschränkte Komponenten ist {@code null} anzugeben.
	 *
	 * @param subjectFilter Subjektfilter oder {@code null}.
	 * @param predicateFilter Prädikatfilter oder {@code null}.
	 * @param objectFilter Objektfilter oder {@code null}.
	 * @return gefilterte Kanten. */
	public STSEdgeSet selectEdgeSet(final STSNodeSet subjectFilter, final STSNodeSet predicateFilter, final STSNodeSet objectFilter) {
		final STSEdgeSet objectSet = this.selectEdgeSetImpl(objectFilter, this.selectObjectEdgeSetGetter);
		final STSEdgeSet subjectSet = this.selectEdgeSetImpl(subjectFilter, this.selectSubjectEdgeSetGetter);
		final STSEdgeSet predicateSet = this.selectEdgeSetImpl(predicateFilter, this.selectPredicateEdgeSetGetter);
		return this.selectEdgeSetImpl(subjectSet, predicateSet, objectSet);
	}

	{}
	public abstract STSNodeSet selectNodeSet(String namespaceFilter, String localnameFilter);

	{}
	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Datensatz von diesem Graphspeicher verwaltet wird.
	 *
	 * @param item Datensatz oder {@code null}.
	 * @return {@code true}, wenn der Datensatz enthalten ist. */
	public final boolean contains(final STSItem item) {
		return (item != null) && (item.store == this);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebene Datensatzmenge von diesem Graphspeicher verwaltet wird.
	 *
	 * @param items Datensatzmenge oder {@code null}.
	 * @return {@code true}, wenn der Datensatz enthalten ist. */
	public final boolean contains(final STSItemSet<?> items) {
		return (items != null) && (items.store == this);
	}

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

	protected final IntegerArraySection getItemArrayImpl(final Iterator<? extends STSItem> items) throws NullPointerException {
		final CompactIntegerArray result = new CompactIntegerArray();
		result.setAlignment(0);
		while (items.hasNext()) {
			final STSItem item = items.next();
			if (item != null) {
				result.add(item.index);
			}
		}
		return this.cleanupArray(result);
	}

	{}

	{}

	protected final STSNodeSet getNodeSetImpl(final Iterator<? extends STSNode> nodes) throws NullPointerException {
		return this.getNodeSetImpl(getItemArrayImpl(nodes));
	}

	{}

	{}

	/** Diese Methode gibt die {@link STSEdgeSet#toIntersection(STSEdgeSet) Schnittmenge} der Kantenmengen zurück, die nicht {@code null} sind. Wenn alle Mengen
	 * {@code null} sind, wird die {@link #getEdgeSet() Menge aller Kanten} geliefert.
	 *
	 * @param subjectSet Menge der Kanten mit bestimmten Subjekten.
	 * @param predicateSet Menge der Kanten mit bestimmten Prädikaten.
	 * @param objectSet Menge der Kanten mit bestimmten Objekten.
	 * @return Schnittmenge der Kantenmengen. */
	protected final STSEdgeSet selectEdgeSetImpl(final STSEdgeSet subjectSet, final STSEdgeSet predicateSet, final STSEdgeSet objectSet) {
		if (subjectSet != null) {
			if (predicateSet != null) {
				if (objectSet != null) return subjectSet.toIntersection(predicateSet).toIntersection(objectSet);
				return subjectSet.toIntersection(predicateSet);
			} else {
				if (objectSet != null) return subjectSet.toIntersection(objectSet);
				return subjectSet;
			}
		} else {
			if (predicateSet != null) {
				if (objectSet != null) return predicateSet.toIntersection(objectSet);
				return predicateSet;
			} else {
				if (objectSet != null) return objectSet;
				return this.getEdgeSet();
			}
		}
	}

	protected final STSEdgeSet selectEdgeSetImpl(final STSNode filter, final Getter<STSNode, STSEdgeSet> getter) {
		if (filter == null) return null;
		final STSNode node = this.getNode(filter);
		return node != null ? getter.get(node) : null;
	}

	protected final STSEdgeSet selectEdgeSetImpl(final STSNodeSet filter, final Getter<STSNode, STSEdgeSet> getter) {
		if (filter == null) return null;
		final Iterator<STSNode> iterator = this.getNodeSet(filter).iterator();
		if (!iterator.hasNext()) return this.emptyEdgeSet;
		STSEdgeSet result = getter.get(iterator.next());
		while (iterator.hasNext()) {
			result = result.toUnion(getter.get(iterator.next()));
		}
		return result;
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

	/** Diese Methode gibt die Menge der Knoten mit den gegebenen Positionen zurück.<br>
	 * <b>Die gegebene Positionsliste muss aufsteigend geordnet und Duplikatfrei sein!</b>
	 *
	 * @param indices Positionen verwalteter Knoten.
	 * @return Knotenmenge.
	 * @throws NullPointerException Wenn {@code indices} {@code null} ist. */
	protected final STSNodeSet getNodeSetImpl(final IntegerArraySection indices) throws NullPointerException {
		if (indices.size() == 0) return this.emptyNodeSet;
		return new ArrayNodeSet(this, indices);
	}

	protected STSEdge customGetEdge(final int index) {
		return new STSEdge(this, index);
	}

	protected abstract STSEdge customGetEdge(int subjectIndex, int predicateIndex, int objectIndex);

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

	/** Diese Methode gibt die Position der ersten Kante zurück.
	 *
	 * @return erste Kantenposition. */
	protected int customGetEdgeIndex() {
		return 0;
	}

	protected abstract int customGetEdgeCount();

	protected abstract int customGetEdgeObject(int index);

	protected abstract int customGetEdgeSubject(int index);

	protected abstract int customGetEdgePredicate(int index);

	/** Diese Methode gibt den Knoten mit der gegebenen Position zurück.
	 *
	 * @param index Position eines verwalteten Knoten.
	 * @return Knoten. */
	protected STSNode customGetNode(final int index) {
		return new STSNode(this, index);
	}

	/** Diese Methode gibt die Position des ersten Knoten zurück.
	 *
	 * @return erste Knotenposition. */
	protected int customGetNodeIndex() {
		return 0;
	}

	/** Diese Methode gibt die Anzahl der verwalteten Knoten zurück.
	 *
	 * @see STSNodeSet#minSize()
	 * @see STSNodeSet#maxSize()
	 * @return Knotenanzahl. */
	protected abstract int customGetNodeCount();

	/** Diese Methode gibt den Lokalnamen des Knoten mit der gegebenen Position zurück.
	 *
	 * @see STSNode#localname()
	 * @param index Position eines verwalteten Knoten.
	 * @return Lokalname. */
	protected abstract String customGetNodeLocalname(int index);

	/** Diese Methode gibt den Namensraum des Knoten mit der gegebenen Position zurück.
	 *
	 * @see STSNode#namespace()
	 * @param index Position eines verwalteten Knoten.
	 * @return Namensraum. */
	protected abstract String customGetNodeNamespace(int index);

}
