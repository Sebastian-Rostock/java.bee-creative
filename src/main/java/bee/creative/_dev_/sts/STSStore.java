package bee.creative._dev_.sts;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative._dev_.sts.STSEdgeSet.ArrayEdgeSet;
import bee.creative._dev_.sts.STSEdgeSet.SectionEdgeSet;
import bee.creative._dev_.sts.STSItemSet.ItemIterator;
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

	protected final IntegerArraySection toArrayImpl(final Iterator<? extends STSItem> items) throws NullPointerException {
		final CompactIntegerArray result = new CompactIntegerArray();
		result.setAlignment(0);
		while (items.hasNext()) {
			final STSItem item = items.next();
			if (item != null) {
				result.add(item.index);
			}
		}
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

	protected final STSEdgeSet toEdgeSetImpl(final Iterator<? extends STSItem> edges) throws NullPointerException {
		return new ArrayEdgeSet(this, this.toArrayImpl(edges));
	}

	protected final STSNodeSet toNodeSetImpl(final Iterator<? extends STSItem> nodes) throws NullPointerException {
		return new ArrayNodeSet(this, this.toArrayImpl(nodes));
	}

	protected final STSNodeSet getNodeSetImpl(final STSNode edge) {
		if (edge == null) return this.emptyNodeSet;
		return new SectionNodeSet(this, edge.index, 1);
	}

	protected final STSNodeSet getNodeSetImpl(final Iterator<? extends STSNode> nodes) throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.getNode(nodes.next());
			}

			@Override
			public boolean hasNext() {
				return nodes.hasNext();
			}

		});
	}

	protected final STSNodeSet getNodeSetImpl(final Iterator<? extends String> namespaces, final Iterator<? extends String> localnames)
		throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.getNode(namespaces.next(), localnames.next());
			}

			@Override
			public boolean hasNext() {
				return namespaces.hasNext() && localnames.hasNext();
			}

		});
	}

	protected final STSNodeSet putNodeSetImpl(final Iterator<? extends STSNode> nodes) throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.putNode(nodes.next());
			}

			@Override
			public boolean hasNext() {
				return nodes.hasNext();
			}

		});
	}

	protected final STSNodeSet putNodeSetImpl(final Iterator<? extends String> namespaces, final Iterator<? extends String> localnames)
		throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.putNode(namespaces.next(), localnames.next());
			}

			@Override
			public boolean hasNext() {
				return namespaces.hasNext() && localnames.hasNext();
			}

		});
	}

	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}
	{}

	public STSEdge getEdge(final STSEdge edge) throws NullPointerException {
		if (this.contains(edge)) return edge;
		return this.getEdge(edge.subject(), edge.predicate(), edge.object());
	}

	public STSEdge getEdge(final STSNode subject, final STSNode predicate, final STSNode object) throws NullPointerException {
		final STSNode subjectNode = this.getNode(subject), predicateNode = this.getNode(predicate), objectNode = this.getNode(object);
		if ((subjectNode == null) || (predicateNode == null) || (objectNode == null)) return null;
		return this.customGetEdge(subjectNode.index, predicateNode.index, objectNode.index);
	}

	protected final STSEdge getEdgeImpl(final int index) {
		return new STSEdge(this, index);
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge aller verwalteten Kanten} zurück.
	 *
	 * @return Kantenmenge. */
	public STSEdgeSet getEdgeSet() {
		return new SectionEdgeSet(this, this.customGetEdgeIndex(), this.customGetEdgeCount());
	}

	public STSEdgeSet getEdgeSet(final STSEdgeSet edges) throws NullPointerException {
		if (this.contains(edges)) return edges;
		return this.getEdgeSetImpl(edges.iterator());
	}

	public STSEdgeSet getEdgeSet(final Iterable<? extends STSEdge> edges) throws NullPointerException {
		if (edges instanceof STSEdgeSet) return this.getEdgeSet((STSEdgeSet)edges);
		return this.getEdgeSetImpl(edges.iterator());
	}

	public STSEdgeSet getEdgeSet(final List<? extends STSNode> subjects, final List<? extends STSNode> predicates, final List<? extends STSNode> objects)
		throws NullPointerException, IllegalArgumentException {
		if ((subjects.size() != predicates.size()) || (predicates.size() != objects.size())) throw new IllegalArgumentException();
		return this.getEdgeSetImpl(subjects.iterator(), predicates.iterator(), objects.iterator());
	}

	protected final STSEdgeSet getEdgeSetImpl(final STSEdge edge) {
		if (edge == null) return this.emptyEdgeSet;
		return new SectionEdgeSet(this, edge.index, 1);
	}

	/** Diese Methode implementiert {@link #getEdgeSet(Iterable)}. */
	protected final STSEdgeSet getEdgeSetImpl(final Iterator<? extends STSEdge> edges) throws NullPointerException {
		return this.toEdgeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.getEdge(edges.next());
			}

			@Override
			public boolean hasNext() {
				return edges.hasNext();
			}

		});
	}

	/** Diese Methode implementiert {@link #getEdgeSet(List, List, List)}. */
	protected final STSEdgeSet getEdgeSetImpl(final Iterator<? extends STSNode> subjects, final Iterator<? extends STSNode> predicates,
		final Iterator<? extends STSNode> objects) throws NullPointerException {
		return this.toEdgeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.getEdge(subjects.next(), predicates.next(), objects.next());
			}

			@Override
			public boolean hasNext() {
				return subjects.hasNext() && predicates.hasNext() && objects.hasNext();
			}

		});
	}

	public STSEdge putEdge(final STSEdge edge) throws NullPointerException {
		if (this.contains(edge)) return edge;
		return this.putEdge(edge.subject(), edge.predicate(), edge.object());
	}

	public abstract STSEdge putEdge(STSNode s, STSNode p, STSNode o);

	public STSEdgeSet putEdgeSet(final STSEdgeSet edges) throws NullPointerException {
		if (this.contains(edges)) return edges;
		return this.putEdgeSetImpl(edges.iterator());
	}

	public STSEdgeSet putEdgeSet(final Iterable<? extends STSEdge> edges) throws NullPointerException {
		if (edges instanceof STSEdgeSet) return this.putEdgeSet((STSEdgeSet)edges);
		return this.putEdgeSetImpl(edges.iterator());
	}

	public STSEdgeSet putEdgeSet(final List<? extends STSNode> subjects, final List<? extends STSNode> predicates, final List<? extends STSNode> objects)
		throws NullPointerException, IllegalArgumentException {
		if ((subjects.size() != predicates.size()) || (predicates.size() != objects.size())) throw new IllegalArgumentException();
		return this.putEdgeSetImpl(subjects.iterator(), predicates.iterator(), objects.iterator());
	}

	protected final STSEdgeSet putEdgeSetImpl(final Iterator<? extends STSEdge> edges) throws NullPointerException {
		return this.toEdgeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.putEdge(edges.next());
			}

			@Override
			public boolean hasNext() {
				return edges.hasNext();
			}

		});
	}

	protected final STSEdgeSet putEdgeSetImpl(final Iterator<? extends STSNode> subjects, final Iterator<? extends STSNode> predicates,
		final Iterator<? extends STSNode> objects) throws NullPointerException {
		return this.toEdgeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.putEdge(subjects.next(), predicates.next(), objects.next());
			}

			@Override
			public boolean hasNext() {
				return subjects.hasNext() && predicates.hasNext() && objects.hasNext();
			}

		});
	}

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

	/** Diese Methode gibt den Knoten mit der gegebenen Position zurück.
	 *
	 * @param index Position eines verwalteten Knoten.
	 * @return Knoten. */
	protected final STSNode getNodeImpl(final int index) {
		return new STSNode(this, index);
	}

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
		if (this.contains(nodes)) return nodes;
		return this.getNodeSetImpl(nodes.iterator());
	}

	public STSNodeSet getNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) return this.getNodeSet((STSNodeSet)nodes);
		return this.getNodeSetImpl(nodes.iterator());
	}

	public STSNodeSet getNodeSet(final List<? extends String> namespaces, final List<? extends String> localnames)
		throws NullPointerException, IllegalArgumentException {
		if (namespaces.size() != localnames.size()) throw new IllegalArgumentException();
		return this.getNodeSetImpl(namespaces.iterator(), localnames.iterator());
	}

	public STSNode putNode(final STSNode node) throws NullPointerException {
		if (this == node.store) return node;
		return this.putNode(node.namespace(), node.localname());
	}

	public abstract STSNode putNode(String namespace, String localname) throws NullPointerException;

	public STSNodeSet putNodeSet(final STSNodeSet nodes) throws NullPointerException {
		if (this.contains(nodes)) return nodes;
		return this.putNodeSetImpl(nodes.iterator());
	}

	public STSNodeSet putNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) return this.putNodeSet((STSNodeSet)nodes);
		return this.putNodeSetImpl(nodes.iterator());
	}

	public STSNodeSet putNodeSet(final List<? extends String> namespaces, final List<? extends String> localnames)
		throws NullPointerException, IllegalArgumentException {
		if (namespaces.size() != localnames.size()) throw new IllegalArgumentException();
		return this.putNodeSetImpl(namespaces.iterator(), localnames.iterator());
	}

	{}

	{}

	{}

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

	{}

	{}

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

	protected abstract STSEdge customGetEdge(int subjectIndex, int predicateIndex, int objectIndex);

	protected abstract STSEdge customPutEdge(int subjectIndex, int predicateIndex, int objectIndex);

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

}
