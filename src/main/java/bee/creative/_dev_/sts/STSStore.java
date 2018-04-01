package bee.creative._dev_.sts;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import bee.creative._dev_.sts.STSEdgeSet.ArrayEdgeSet;
import bee.creative._dev_.sts.STSEdgeSet.SequenceEdgeSet;
import bee.creative._dev_.sts.STSItemSet.ItemIterator;
import bee.creative._dev_.sts.STSNodeSet.ArrayNodeSet;
import bee.creative._dev_.sts.STSNodeSet.SequenceNodeSet;
import bee.creative.array.CompactIntegerArray;
import bee.creative.array.IntegerArraySection;
import bee.creative.fem.FEMBinary;
import bee.creative.util.Getter;

/** Diese Klasse implementiert einen abstrakten Speicher zur Verwaltung eines Graphe aus {@link STSNode Knoten} und {@link STSEdge Kanten}, bei welchem jeder
 * Knoten einen ihn identifizierenden {@link STSNode#value() Wert} besitzt und jede Kante eine Verbindung dreier Knoten in den Rollen {@link STSEdge#subject()
 * Subjekt}, {@link STSEdge#subject() Prädikat} und {@link STSEdge#subject() Objekt} darstellt.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class STSStore {

	/** Diese Klasse implementiert den {@link Getter} zu {@link STSStore#customSelectEdgeSetByObject(int)}. */
	protected final class SelectObjectEdgeSetGetter implements Getter<STSNode, STSEdgeSet> {

		/** {@inheritDoc} */
		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectEdgeSetByObject(input.index);
		}

	}

	/** Diese Klasse implementiert den {@link Getter} zu {@link STSStore#customSelectEdgeSetBySubject(int)}. */
	protected final class SelectSubjectEdgeSetGetter implements Getter<STSNode, STSEdgeSet> {

		/** {@inheritDoc} */
		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectEdgeSetBySubject(input.index);
		}

	}

	/** Diese Klasse implementiert den {@link Getter} zu {@link STSStore#customSelectEdgeSetByPredicate(int)}. */
	protected final class SelectPredicateEdgeSetGetter implements Getter<STSNode, STSEdgeSet> {

		/** {@inheritDoc} */
		@Override
		public STSEdgeSet get(final STSNode input) {
			return STSStore.this.customSelectEdgeSetByPredicate(input.index);
		}

	}

	{}

	/** Dieses Feld speichert den Generator für {@link #hash}. */
	protected static AtomicInteger hashGen = new AtomicInteger(1);

	{}

	/** Dieses Feld speichert den Streuwert als Identifikator des Graphspeichers. */
	protected final int hash = STSStore.hashGen.getAndIncrement();

	{}

	/** Diese Methode überführt die gegebenen Datensätze in die duplikatfrei aufsteigend geordnete Liste ihrer {@link STSItem#index Positionen} und gibt diese
	 * zurück.
	 *
	 * @param items Datensätze.
	 * @return Positionsmenge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
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
		int sourceIndex = startIndex, targetIndex = startIndex, target = Integer.MAX_VALUE;
		while (sourceIndex < finalIndex) {
			final int source = array[sourceIndex];
			if (source != target) {
				array[targetIndex++] = target = source;
			}
			sourceIndex++;
		}
		return IntegerArraySection.from(Arrays.copyOfRange(array, startIndex, targetIndex));
	}

	/** Diese Methode überführt die gegebene Datensätze über {@link #toArrayImpl(Iterator)} in eine geordnete Positionsliste und gibt die dazu erzeugte
	 * Kantenmente zurück.
	 *
	 * @see ArrayEdgeSet
	 * @see #toArrayImpl(Iterator)
	 * @param edges Kanten.
	 * @return Kantenmenge
	 * @throws NullPointerException Wenn {@code edges} {@code null} ist. */
	protected final STSEdgeSet toEdgeSetImpl(final Iterator<? extends STSItem> edges) throws NullPointerException {
		return new ArrayEdgeSet(this, this.toArrayImpl(edges));
	}

	/** Diese Methode überführt die gegebene Datensätze über {@link #toArrayImpl(Iterator)} in eine geordnete Positionsliste und gibt die dazu erzeugte
	 * Knotenmenge zurück.
	 *
	 * @see ArrayNodeSet
	 * @see #toArrayImpl(Iterator)
	 * @param nodes Knoten.
	 * @return Knotenmenge
	 * @throws NullPointerException Wenn {@code nodes} {@code null} ist. */
	protected final STSNodeSet toNodeSetImpl(final Iterator<? extends STSItem> nodes) throws NullPointerException {
		return new ArrayNodeSet(this, this.toArrayImpl(nodes));
	}

	/** Diese Methode gibt die verwaltete Kante mit den Merkmalen der gegebenen zurück. Bei erfolgloser Suche wird {@code null} geliefert.
	 *
	 * @see #contains(STSItem)
	 * @see #getEdge(STSNode, STSNode, STSNode)
	 * @see STSEdge#object()
	 * @see STSEdge#subject()
	 * @see STSEdge#predicate()
	 * @param edge gegebene Kante.
	 * @return Kante oder {@code null}.
	 * @throws NullPointerException Wenn {@code edge} {@code null} ist. */
	public STSEdge getEdge(final STSEdge edge) throws NullPointerException {
		if (this.contains(edge)) return edge;
		return this.getEdge(edge.subject(), edge.predicate(), edge.object());
	}

	/** Diese Methode gibt die verwaltete Kante mit den gegebenen Merkmalen zurück. Bei erfolgloser Suche wird {@code null} geliefert.
	 *
	 * @see #getNode(STSNode)
	 * @param subject {@link STSEdge#subject() Subjektknoten}.
	 * @param predicate {@link STSEdge#predicate() Prädikatknoten}.
	 * @param object {@link STSEdge#object() Objektknoten}.
	 * @return Kante oder {@code null}.
	 * @throws NullPointerException Wenn {@code subject}, {@code predicate} bzw. {@code object} {@code null} ist. */
	public STSEdge getEdge(final STSNode subject, final STSNode predicate, final STSNode object) throws NullPointerException {
		final STSNode subjectNode = this.getNode(subject), predicateNode = this.getNode(predicate), objectNode = this.getNode(object);
		if ((subjectNode == null) || (predicateNode == null) || (objectNode == null)) return null;
		return this.customGetEdge(subjectNode.index, predicateNode.index, objectNode.index);
	}

	/** Diese Methode implementiert {@link #getEdge(STSNode, STSNode, STSNode)}. */
	@SuppressWarnings ("javadoc")
	protected abstract STSEdge customGetEdge(int subjectIndex, int predicateIndex, int objectIndex);

	/** Diese Methode gibt die {@link STSEdgeSet Menge aller verwalteten Kanten} zurück.
	 *
	 * @return Kantenmenge. */
	public STSEdgeSet getEdgeSet() {
		return new SequenceEdgeSet(this, this.customGetEdgeIndex(), this.customGetEdgeCount());
	}

	/** Diese Methode gibt die Kante mit der gegebenen Position zurück.
	 *
	 * @see STSEdgeSet#iterator()
	 * @param index Position einer verwalteten Kante.
	 * @return Kante. */
	protected STSEdge customGetEdge(final int index) {
		return new STSEdge(this, index);
	}

	/** Diese Methode gibt eine Kantenmenge zur gegebenen Kante zurück. Wenn dieser {@code null} ist, wird die leere Menge geliefert.
	 *
	 * @param edge Kante oder {@code null}.
	 * @return Kantenmenge. */
	protected STSEdgeSet customGetEdgeSet(final STSEdge edge) {
		if (edge == null) return new SequenceEdgeSet(this, 0, 0);
		return new SequenceEdgeSet(this, edge.index, 1);
	}

	/** Diese Methode gibt die Position der ersten verwalteten Kante zurück.
	 *
	 * @see #getEdgeSet()
	 * @return erste Kantenposition. */
	protected int customGetEdgeIndex() {
		return 0;
	}

	/** Diese Methode gibt die Anzahl der verwalteten Kanten zurück.
	 *
	 * @see #getEdgeSet()
	 * @see STSEdgeSet#minSize()
	 * @see STSEdgeSet#maxSize()
	 * @return Kantenanzahl. */
	protected abstract int customGetEdgeCount();

	/** Diese Methode implementiert {@link STSEdge#object()}.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Objektknoten. */
	protected STSNode customGetEdgeObjectNode(final int index) {
		return this.customGetNode(this.customGetEdgeObjectIndex(index));
	}

	/** Diese Methode gibt die Position des Objektknotens der gegebenen Kante zurück.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Position des Objektknotens. */
	protected abstract int customGetEdgeObjectIndex(int index);

	/** Diese Methode implementiert {@link STSEdge#subject()}.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Subjektknoten. */
	protected STSNode customGetEdgeSubjectNode(final int index) {
		return this.customGetNode(this.customGetEdgeSubjectIndex(index));
	}

	/** Diese Methode gibt die Position des Subjektknotens der gegebenen Kante zurück.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Position des Subjektknotens. */
	protected abstract int customGetEdgeSubjectIndex(int index);

	/** Diese Methode implementiert {@link STSEdge#predicate()}.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Prädikatknoten. */
	protected STSNode customGetEdgePredicateNode(final int index) {
		return this.customGetNode(this.customGetEdgePredicateIndex(index));
	}

	/** Diese Methode gibt die Position des Prädikatknotens der gegebenen Kante zurück.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Position des Prädikatknotens. */
	protected abstract int customGetEdgePredicateIndex(int index);

	/** Diese Methode implementiert {@link STSEdge#toString()}.
	 *
	 * @param index Position einer verwalteten Kante.
	 * @return Textdarstellung. */
	protected String customGetEdgeString(final int index) {
		return "(" + this.customGetEdgeSubjectNode(index) + " " + this.customGetEdgePredicateNode(index) + " " + this.customGetEdgePredicateNode(index) + ")";
	}

	/** Diese Methode gibt die Menge der verwalteten Kanten mit den Merkmalen der gegebenen zurück.
	 *
	 * @see #getEdge(STSEdge)
	 * @see #contains(STSItemSet)
	 * @param edges gegebene Kanten.
	 * @return Kantenmegne.
	 * @throws NullPointerException Wenn {@code edges} {@code null} ist. */
	public STSEdgeSet getEdgeSet(final Iterable<? extends STSEdge> edges) throws NullPointerException {
		if (edges instanceof STSEdgeSet) {
			final STSEdgeSet result = (STSEdgeSet)edges;
			if (this.contains(result)) return result;
		}
		return this.customGetEdgeSetByItem(edges.iterator());
	}

	/** Diese Methode implementiert {@link #getEdgeSet(Iterable)}. */
	@SuppressWarnings ("javadoc")
	protected STSEdgeSet customGetEdgeSetByItem(final Iterator<? extends STSEdge> edges) throws NullPointerException {
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

	/** Diese Methode gibt die Menge der verwalteten Kanten mit den gegebenen Merkmalen zurück. Die gegebenen Listen stehen für die Spalten einer Merkmalstabelle.
	 *
	 * @param subjects {@link STSEdge#subject() Knotenliste der Subjektspalte}.
	 * @param predicates {@link STSEdge#predicate() Knotenliste der Prädikatspalte}.
	 * @param objects {@link STSEdge#object() Knotenliste der Objektspalte}.
	 * @return Kantenmenge.
	 * @throws NullPointerException Wenn eine der Listen {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Listen nicht die gleiche Länge haben. */
	public STSEdgeSet getEdgeSet(final List<? extends STSNode> subjects, final List<? extends STSNode> predicates, final List<? extends STSNode> objects)
		throws NullPointerException, IllegalArgumentException {
		if ((subjects.size() != predicates.size()) || (predicates.size() != objects.size())) throw new IllegalArgumentException();
		return this.customGetEdgeSetByFields(subjects.iterator(), predicates.iterator(), objects.iterator());
	}

	/** Diese Methode implementiert {@link #getEdgeSet(List, List, List)}. */
	@SuppressWarnings ("javadoc")
	protected STSEdgeSet customGetEdgeSetByFields(final Iterator<? extends STSNode> subjects, final Iterator<? extends STSNode> predicates,
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

	/** Diese Methode gibt den Knoten mit dem Wert des gegebenen zurück. Bei erfolgloser Suche wird {@code null} geliefert.
	 *
	 * @see #contains(STSItem)
	 * @see #getNode(FEMBinary)
	 * @param node gegebener Knoten.
	 * @return Knoten oder {@code null}.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist. */
	public STSNode getNode(final STSNode node) throws NullPointerException {
		if (this.contains(node)) return node;
		return this.getNode(node.value());
	}

	/** Diese Methode gibt den Knoten mit dem gegebenen Wert zurück. Bei erfolgloser Suche wird {@code null} geliefert.
	 *
	 * @param value {@link STSNode#value() Knotenwert}.
	 * @return Knoten oder {@code null}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public STSNode getNode(final FEMBinary value) throws NullPointerException {
		return this.customGetNode(value.data());
	}

	/** Diese Methode implementiert {@link #getNode(FEMBinary)}. */
	@SuppressWarnings ("javadoc")
	protected abstract STSNode customGetNode(FEMBinary value);

	/** Diese Methode gibt die {@link STSNodeSet Menge aller verwalteten Knoten} zurück.
	 *
	 * @return Knotenmenge. */
	public STSNodeSet getNodeSet() {
		return new SequenceNodeSet(this, this.customGetNodeIndex(), this.customGetNodeCount());
	}

	/** Diese Methode gibt den Knoten mit der gegebenen Position zurück.
	 *
	 * @param index Position eines verwalteten Knoten.
	 * @return Knoten. */
	protected STSNode customGetNode(final int index) {
		return new STSNode(this, index);
	}

	/** Diese Methode gibt eine Knotenmenge zum gegebenen Knoten zurück. Wenn dieser {@code null} ist, wird die leere Menge geliefert.
	 *
	 * @param node Knoten oder {@code null}.
	 * @return Knotenmenge. */
	protected STSNodeSet customGetNodeSet(final STSNode node) {
		if (node == null) return new SequenceNodeSet(this, 0, 0);
		return new SequenceNodeSet(this, node.index, 1);
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

	/** Diese Methode implementiert {@link STSNode#value()}.
	 *
	 * @param index Position eines verwalteten Knoten.
	 * @return Knotenwert. */
	protected abstract FEMBinary customGetNodeValue(int index);

	/** Diese Methode implementiert {@link STSNode#toString()}.
	 *
	 * @param index Position eines verwalteten Knoten.
	 * @return Textdarstellung. */
	protected String customGetNodeString(final int index) {
		return this.customGetNodeValue(index).toString(false);
	}

	/** Diese Methode gibt die Megne der Knoten mit den Merkmalen der gegebenen zurück.
	 *
	 * @see #getNode(STSNode)
	 * @see #contains(STSItemSet)
	 * @param nodes gegebene Knoten.
	 * @return Knotenmegne.
	 * @throws NullPointerException Wenn {@code nodes} {@code null} ist. */
	public STSNodeSet getNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) {
			final STSNodeSet result = (STSNodeSet)nodes;
			if (this.contains(result)) return result;
		}
		return this.customGetNodeSetByItem(nodes.iterator());
	}

	/** Diese Methode implementiert {@link #getNodeSet(Iterable)}. */
	@SuppressWarnings ("javadoc")
	protected STSNodeSet customGetNodeSetByItem(final Iterator<? extends STSNode> nodes) throws NullPointerException {
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

	/** Diese Methode gibt die Menge der verwalteten Knoten mit den gegebenen Werten zurück.
	 *
	 * @param values {@link STSNode#value() Wertliste}.
	 * @return Knotenmegne.
	 * @throws NullPointerException Wenn eine der Listen {@code null} ist. */
	public STSNodeSet getNodeSet(final List<? extends FEMBinary> values) throws NullPointerException {
		return this.customGetNodeSetByFields(values.iterator());
	}

	/** Diese Methode implementiert {@link #getNodeSet(List)}. */
	@SuppressWarnings ("javadoc")
	protected STSNodeSet customGetNodeSetByFields(final Iterator<? extends FEMBinary> values) throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.customGetNode(values.next());
			}

			@Override
			public boolean hasNext() {
				return values.hasNext();
			}

		});
	}

	/** Diese Methode gibt die verwaltete Kante mit den Merkmalen der gegebenen zurück und erzeugt diesen bei Bedarf.
	 *
	 * @see #contains(STSItem)
	 * @see #putEdge(STSNode, STSNode, STSNode)
	 * @see STSEdge#object()
	 * @see STSEdge#subject()
	 * @see STSEdge#predicate()
	 * @param edge gegebene Kante.
	 * @return Kante.
	 * @throws NullPointerException Wenn {@code edge} {@code null} ist. */
	public STSEdge putEdge(final STSEdge edge) throws NullPointerException {
		if (this.contains(edge)) return edge;
		return this.putEdge(edge.subject(), edge.predicate(), edge.object());
	}

	/** Diese Methode gibt die verwaltete Kante mit den Merkmalen der gegebenen zurück und erzeugt diesen bei Bedarf.
	 *
	 * @see #putNode(STSNode)
	 * @param subject {@link STSEdge#subject() Subjektknoten}.
	 * @param predicate {@link STSEdge#predicate() Prädikatknoten}.
	 * @param object {@link STSEdge#object() Objektknoten}.
	 * @return Kante.
	 * @throws NullPointerException Wenn {@code subject}, {@code predicate} bzw. {@code object} {@code null} ist. */
	public STSEdge putEdge(final STSNode subject, final STSNode predicate, final STSNode object) throws NullPointerException {
		return this.customPutEdge(this.putNode(subject).index, this.putNode(predicate).index, this.putNode(object).index);
	}

	/** Diese Methode implementiert {@link #putEdge(STSNode, STSNode, STSNode)}. */
	@SuppressWarnings ("javadoc")
	protected abstract STSEdge customPutEdge(int subjectIndex, int predicateIndex, int objectIndex);

	/** Diese Methode gibt die Menge der verwalteten Kanten mit den Merkmalen der gegebenen zurück und erzeugt diese bei Bedarf.
	 *
	 * @see #putEdge(STSEdge)
	 * @see #contains(STSItemSet)
	 * @param edges Kanten.
	 * @return Kantenmegne.
	 * @throws NullPointerException Wenn {@code edges} {@code null} ist. */
	public STSEdgeSet putEdgeSet(final Iterable<? extends STSEdge> edges) throws NullPointerException {
		if (edges instanceof STSEdgeSet) {
			final STSEdgeSet result = (STSEdgeSet)edges;
			if (this.contains(result)) return result;
		}
		return this.customPutEdgeSetByItem(edges.iterator());
	}

	/** Diese Methode implementiert {@link #putEdgeSet(Iterable)}. */
	@SuppressWarnings ("javadoc")
	protected STSEdgeSet customPutEdgeSetByItem(final Iterator<? extends STSEdge> edges) throws NullPointerException {
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

	/** Diese Methode gibt die Menge der verwalteten Kanten mit den gegebenen Merkmalen zurück und erzeugt diese bei Bedarf. Die gegebenen Listen stehen für die
	 * Spalten einer Merkmalstabelle.
	 *
	 * @param subjects {@link STSEdge#subject() Knotenliste der Subjektspalte}.
	 * @param predicates {@link STSEdge#predicate() Knotenliste der Prädikatspalte}.
	 * @param objects {@link STSEdge#object() Knotenliste der Objektspalte}.
	 * @return Kantenmenge.
	 * @throws NullPointerException Wenn eine der Listen {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Listen nicht die gleiche Länge haben. */
	public STSEdgeSet putEdgeSet(final List<? extends STSNode> subjects, final List<? extends STSNode> predicates, final List<? extends STSNode> objects)
		throws NullPointerException, IllegalArgumentException {
		if ((subjects.size() != predicates.size()) || (predicates.size() != objects.size())) throw new IllegalArgumentException();
		return this.customPutEdgeSetByFields(subjects.iterator(), predicates.iterator(), objects.iterator());
	}

	/** Diese Methode implementiert {@link #putEdgeSet(List, List, List)}. */
	@SuppressWarnings ("javadoc")
	protected STSEdgeSet customPutEdgeSetByFields(final Iterator<? extends STSNode> subjects, final Iterator<? extends STSNode> predicates,
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

	/** Diese Methode gibt den Knoten mit dem Wert des gegebenen zurück und erzeugt diesen bei Bedarf.
	 *
	 * @see #contains(STSItem)
	 * @see #putNode(FEMBinary)
	 * @param node gegebener Knoten.
	 * @return Knoten.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist. */
	public STSNode putNode(final STSNode node) throws NullPointerException {
		if (this == node.store) return node;
		return this.putNode(node.value());
	}

	/** Diese Methode gibt den Knoten mit dem gegebenen Wert zurück und erzeugt diesen bei Bedarf.
	 *
	 * @param value {@link STSNode#value() Knotenwert}.
	 * @return Knoten.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public STSNode putNode(final FEMBinary value) throws NullPointerException {
		return this.customPutNode(value.data());
	}

	/** Diese Methode implementiert {@link #putNode(FEMBinary)}. */
	@SuppressWarnings ("javadoc")
	protected abstract STSNode customPutNode(FEMBinary value);

	/** Diese Methode gibt die Megne der Knoten mit den Merkmalen der gegebenen zurück und erzeugt diesen bei Bedarf.
	 *
	 * @see #getNode(STSNode)
	 * @see #contains(STSItemSet)
	 * @param nodes gegebene Knotenmegne.
	 * @return Knotenmegne.
	 * @throws NullPointerException Wenn {@code nodes} {@code null} ist. */
	public STSNodeSet putNodeSet(final Iterable<? extends STSNode> nodes) throws NullPointerException {
		if (nodes instanceof STSNodeSet) {
			final STSNodeSet result = (STSNodeSet)nodes;
			if (this.contains(result)) return result;
		}
		return this.customPutNodeSetByItem(nodes.iterator());
	}

	/** Diese Methode implementiert {@link #putNodeSet(Iterable)}. */
	@SuppressWarnings ("javadoc")
	protected STSNodeSet customPutNodeSetByItem(final Iterator<? extends STSNode> nodes) throws NullPointerException {
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

	/** Diese Methode gibt die Menge der verwalteten Knoten mit den gegebenen Werten zurück und erzeugt diesen bei Bedarf.
	 *
	 * @param values {@link STSNode#value() Wertliste}.
	 * @return Knotenmegne.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist. */
	public STSNodeSet putNodeSet(final List<? extends FEMBinary> values) throws NullPointerException {
		return this.customPutNodeSetByFields(values.iterator());
	}

	/** Diese Methode implementiert {@link #putNodeSet(List)}. */
	@SuppressWarnings ("javadoc")
	protected STSNodeSet customPutNodeSetByFields(final Iterator<? extends FEMBinary> values) throws NullPointerException {
		return this.toNodeSetImpl(new ItemIterator() {

			@Override
			public STSItem next() {
				return STSStore.this.putNode(values.next());
			}

			@Override
			public boolean hasNext() {
				return values.hasNext();
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
			return this.customGetEdgeSet(this.getEdge(subjectFilter, predicateFilter, objectFilter));
		final STSEdgeSet objectSet = this.selectEdgeSetImpl(objectFilter, new SelectObjectEdgeSetGetter());
		final STSEdgeSet subjectSet = this.selectEdgeSetImpl(subjectFilter, new SelectSubjectEdgeSetGetter());
		final STSEdgeSet predicateSet = this.selectEdgeSetImpl(predicateFilter, new SelectPredicateEdgeSetGetter());
		return this.customSelectEdgeSet(subjectSet, predicateSet, objectSet);
	}

	/** Diese Methode implementiert für {@link #selectEdgeSet(STSNode, STSNode, STSNode)} die Überführung eines Filterknoten in eine Kantenmenge bzw.
	 * {@code null}.
	 *
	 * @see SelectObjectEdgeSetGetter
	 * @see SelectSubjectEdgeSetGetter
	 * @see SelectPredicateEdgeSetGetter
	 * @param filter Filterknoten.
	 * @param getter Methode zur Ermittlung der Kantenmenge zu einem Knoten bzw. {@code null}, wenn zum gegebenen Filterknoten kein verwalteter Knoten existiert.
	 * @return Kantenmenge oder {@code null}. */
	protected final STSEdgeSet selectEdgeSetImpl(final STSNode filter, final Getter<STSNode, STSEdgeSet> getter) {
		if (filter == null) return null;
		final STSNode node = this.getNode(filter);
		return node != null ? getter.get(node) : null;
	}

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} zurück, deren Subjekte, Prädikate bzw. Objekte in den gegebenen Mengen enthalten sind. Für
	 * uneingeschränkte Komponenten ist {@code null} anzugeben.
	 *
	 * @param subjectFilter Subjektfilter oder {@code null}.
	 * @param predicateFilter Prädikatfilter oder {@code null}.
	 * @param objectFilter Objektfilter oder {@code null}.
	 * @return gefilterte Kanten. */
	public STSEdgeSet selectEdgeSet(final STSNodeSet subjectFilter, final STSNodeSet predicateFilter, final STSNodeSet objectFilter) {
		final STSEdgeSet objectSet = this.selectEdgeSetImpl(objectFilter, new SelectObjectEdgeSetGetter());
		final STSEdgeSet subjectSet = this.selectEdgeSetImpl(subjectFilter, new SelectSubjectEdgeSetGetter());
		final STSEdgeSet predicateSet = this.selectEdgeSetImpl(predicateFilter, new SelectPredicateEdgeSetGetter());
		return this.customSelectEdgeSet(subjectSet, predicateSet, objectSet);
	}

	/** Diese Methode implementiert für {@link #selectEdgeSet(STSNodeSet, STSNodeSet, STSNodeSet)} die Überführung einer Filterknotenmenge in eine Kantenmenge
	 * bzw. {@code null}.
	 *
	 * @see SelectObjectEdgeSetGetter
	 * @see SelectSubjectEdgeSetGetter
	 * @see SelectPredicateEdgeSetGetter
	 * @param filter Filterknotenmege.
	 * @param getter Methode zur Ermittlung der Kantenmenge zu einem gegebenen Knoten bzw. {@code null}, wenn zum gegebenen Filterknoten kein verwalteter Knoten
	 *        existiert.
	 * @return Kantenmenge oder {@code null}. */
	protected final STSEdgeSet selectEdgeSetImpl(final STSNodeSet filter, final Getter<STSNode, STSEdgeSet> getter) {
		if (filter == null) return null;
		final Iterator<STSNode> iterator = this.getNodeSet(filter).iterator();
		if (!iterator.hasNext()) return this.customGetEdgeSet(null);
		STSEdgeSet result = getter.get(iterator.next());
		while (iterator.hasNext()) {
			result = result.toUnion(getter.get(iterator.next()));
		}
		return result;
	}

	/** Diese Methode gibt die {@link STSEdgeSet#toIntersection(STSEdgeSet) Schnittmenge} der Kantenmengen zurück, die nicht {@code null} sind. Wenn alle Mengen
	 * {@code null} sind, wird die {@link #getEdgeSet() Menge aller Kanten} geliefert.
	 *
	 * @param subjectSet Menge der Kanten mit bestimmten Subjekten.
	 * @param predicateSet Menge der Kanten mit bestimmten Prädikaten.
	 * @param objectSet Menge der Kanten mit bestimmten Objekten.
	 * @return Schnittmenge der Kantenmengen. */
	protected STSEdgeSet customSelectEdgeSet(final STSEdgeSet subjectSet, final STSEdgeSet predicateSet, final STSEdgeSet objectSet) {
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

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgeObjectIndex(int) Objekt} zurück.
	 *
	 * @param objectIndex {@link STSItem#index Position} des {@link STSEdge#object() Objektknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectEdgeSetByObject(int objectIndex);

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgeSubjectIndex(int) Subjekt} zurück.
	 *
	 * @param subjectIndex {@link STSItem#index Position} des {@link STSEdge#subject() Subjektknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectEdgeSetBySubject(int subjectIndex);

	/** Diese Methode gibt die {@link STSEdgeSet Menge der Kanten} mit dem gegebenen {@link #customGetEdgePredicateIndex(int) Prädikat} zurück.
	 *
	 * @param predicateIndex {@link STSItem#index Position} des {@link STSEdge#predicate() Prädikatknoten}.
	 * @return Kantenmenge. */
	protected abstract STSEdgeSet customSelectEdgeSetByPredicate(int predicateIndex);

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

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		return (object == this) || ((object instanceof STSStore) && (this.hash == object.hashCode()));
	}

}
