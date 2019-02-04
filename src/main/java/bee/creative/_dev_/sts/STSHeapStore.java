package bee.creative._dev_.sts;

import java.util.Arrays;
import bee.creative._dev_.sts.STSEdgeSet.ArrayEdgeSet;
import bee.creative.array.IntegerArraySection;
import bee.creative.fem.FEMBinary;
import bee.creative.iam.IAMMapping;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen {@link STSStore Graphspeicher}, der seine Daten im Arbeitsspeicher verwaltet.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
final class STSHeapStore extends STSStore {

	public static void main(final String[] args) throws Exception {
		final STSHeapStore store = new STSHeapStore();

		final STSNode p = store.putNode(FEMBinary.from("0x4657"));
		final STSNode s = store.putNode(FEMBinary.from("0x1234"));
		final STSNode o = store.putNode(FEMBinary.from("0x89AB"));
		final STSNode x = store.putNode(FEMBinary.from("0xCDEF"));
		store.putEdge(s, p, o);
		store.putEdge(s, p, x);

		System.out.println(store.getNodeSet());
		System.out.println(store.getEdgeSet());
		System.out.println(store.selectEdgeSet(s, null, o));

	}

	static int[] emptyEdges = {0};

	int edgeCount = 0;

	int[] edgeTable = new int[8];

	int[] edgeNexts = new int[8];

	int[] edgeObjects = new int[8];

	int[] edgeSubjects = new int[8];

	int[] edgePredicates = new int[8];

	int nodeCount = 0;

	int[] nodeTable = new int[8];

	int[] nodeNexts = new int[8];

	FEMBinary[] nodeValues = new FEMBinary[8];

	int[][] nodeObjectEdges = new int[8][];

	int[][] nodeSubjectEdges = new int[8][];

	int[][] nodePredicateEdges = new int[8][];

	/** Diese Methode reserviert Speicher für die gegebene Anzahl an Kanten.
	 *
	 * @param edgeCapacity Kapizität.
	 * @throws IllegalArgumentException Wenn die Kapazität kleiner als die aktuelle Anzahl der verwalteten Kanten ist. */
	public void allocateEdges(final int edgeCapacity) throws IllegalArgumentException {
		final int count = this.edgeCount + 1;
		if (edgeCapacity <= count) throw new IllegalArgumentException();
		final int mask = IAMMapping.mask(edgeCapacity);
		this.edgeTable = new int[mask + 1];
		this.edgeNexts = new int[edgeCapacity];
		this.edgeObjects = Arrays.copyOf(this.edgeObjects, edgeCapacity);
		this.edgeSubjects = Arrays.copyOf(this.edgeSubjects, edgeCapacity);
		this.edgePredicates = Arrays.copyOf(this.edgePredicates, edgeCapacity);
		for (int i = 1; i < count; i++) {
			final int index = this.getEdgeHashImpl(this.edgeSubjects[i], this.edgePredicates[i], this.edgeObjects[i]) & mask;
			this.edgeNexts[i] = this.edgeTable[index];
			this.edgeTable[index] = i;
		}
	}

	/** Diese Methode reserviert Speicher für die gegebene Anzahl an Knoten.
	 *
	 * @param nodeCapacity Kapizität.
	 * @throws IllegalArgumentException Wenn die Kapazität kleiner als die aktuelle Anzahl der verwalteten Knoten ist. */
	public void allocateNodes(final int nodeCapacity) throws IllegalArgumentException {
		final int count = this.nodeCount + 1;
		if (nodeCapacity <= count) throw new IllegalArgumentException();
		final int mask = IAMMapping.mask(nodeCapacity);
		this.nodeTable = new int[mask + 1];
		this.nodeNexts = new int[nodeCapacity];
		this.nodeValues = Arrays.copyOf(this.nodeValues, nodeCapacity);
		this.nodeObjectEdges = Arrays.copyOf(this.nodeObjectEdges, nodeCapacity);
		this.nodeSubjectEdges = Arrays.copyOf(this.nodeSubjectEdges, nodeCapacity);
		this.nodePredicateEdges = Arrays.copyOf(this.nodePredicateEdges, nodeCapacity);
		for (int i = 1; i < count; i++) {
			final int index = this.nodeValues[i].hashCode() & mask;
			this.nodeNexts[i] = this.nodeTable[index];
			this.nodeTable[index] = i;
		}
	}

	@SuppressWarnings ("javadoc")
	private STSEdge getEdgeImpl(final int subjectIndex, final int predicateIndex, final int objectIndex, final boolean readonly) {
		final int hash = this.getEdgeHashImpl(subjectIndex, predicateIndex, objectIndex);
		final int hashIndex = hash & (this.edgeTable.length - 1);
		int edgeIndex = this.edgeTable[hashIndex];
		while (edgeIndex != 0) {
			if ((this.edgeSubjects[edgeIndex] == subjectIndex) && (this.edgePredicates[edgeIndex] == predicateIndex) && (this.edgeObjects[edgeIndex] == objectIndex))
				return this.customGetEdge(edgeIndex);
			edgeIndex = this.edgeNexts[edgeIndex];
		}
		if (readonly) return null;
		edgeIndex = this.getEdgeIndexImpl();
		this.edgeNexts[edgeIndex] = this.edgeTable[hashIndex];
		this.edgeObjects[edgeIndex] = objectIndex;
		this.edgeSubjects[edgeIndex] = subjectIndex;
		this.edgePredicates[edgeIndex] = predicateIndex;
		this.edgeTable[hashIndex] = edgeIndex;
		this.putNodeEdgeSetImpl(this.nodeObjectEdges, objectIndex, edgeIndex);
		this.putNodeEdgeSetImpl(this.nodeSubjectEdges, subjectIndex, edgeIndex);
		this.putNodeEdgeSetImpl(this.nodePredicateEdges, predicateIndex, edgeIndex);
		return this.customGetEdge(edgeIndex);
	}

	@SuppressWarnings ("javadoc")
	private int getEdgeIndexImpl() {
		final int count = this.edgeCount + 1, capacity = this.edgeNexts.length;
		if (count < capacity) return this.edgeCount = count;
		this.allocateEdges((capacity << 1) + capacity + 1);
		return this.edgeCount = count;
	}

	@SuppressWarnings ("javadoc")
	private int getEdgeHashImpl(final int subjectIndex, final int predicateIndex, final int objectIndex) {
		int result = Objects.hashInit();
		result = Objects.hashPush(result, subjectIndex);
		result = Objects.hashPush(result, predicateIndex);
		result = Objects.hashPush(result, objectIndex);
		return result;
	}

	@SuppressWarnings ("javadoc")
	private STSNode getNodeImpl(final FEMBinary value, final boolean readonly) {
		final int hash = value.hashCode();
		final int hashIndex = hash & (this.nodeTable.length - 1);
		int nodeIndex = this.nodeTable[hashIndex];
		while (nodeIndex != 0) {
			if (this.nodeValues[nodeIndex].equals(value)) return this.customGetNode(nodeIndex);
			nodeIndex = this.nodeNexts[nodeIndex];
		}
		if (readonly) return null;
		nodeIndex = this.getNodeIndexImpl();
		this.nodeNexts[nodeIndex] = this.nodeTable[hashIndex];
		this.nodeValues[nodeIndex] = value;
		this.nodeObjectEdges[nodeIndex] = STSHeapStore.emptyEdges;
		this.nodeSubjectEdges[nodeIndex] = STSHeapStore.emptyEdges;
		this.nodePredicateEdges[nodeIndex] = STSHeapStore.emptyEdges;
		this.nodeTable[hashIndex] = nodeIndex;
		return this.customGetNode(nodeIndex);
	}

	@SuppressWarnings ("javadoc")
	private int getNodeIndexImpl() {
		final int count = this.nodeCount + 1, capacity = this.nodeNexts.length;
		if (count < capacity) return this.nodeCount = count;
		this.allocateNodes((capacity << 1) + capacity + 1);
		return this.nodeCount = count;
	}

	@SuppressWarnings ("javadoc")
	private STSEdgeSet getNodeEdgeSetImpl(final int[] edges) {
		return new ArrayEdgeSet(this, IntegerArraySection.from(edges, 1, edges[0] + 1));
	}

	@SuppressWarnings ("javadoc")
	private void putNodeEdgeSetImpl(final int[][] nodeEdges, final int nodeIndex, final int edgeIndex) {
		int[] edges = nodeEdges[nodeIndex];
		final int count = edges[0] + 1, capacity = edges.length;
		if (count >= capacity) {
			nodeEdges[nodeIndex] = edges = Arrays.copyOf(edges, (capacity << 1) + capacity + 1);
		}
		edges[0] = count;
		edges[count] = edgeIndex;
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdge customGetEdge(final int subjectIndex, final int predicateIndex, final int objectIndex) {
		return this.getEdgeImpl(subjectIndex, predicateIndex, objectIndex, true);
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetEdgeCount() {
		return this.edgeCount;
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetEdgeIndex() {
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetEdgeObjectIndex(final int index) {
		return this.edgeObjects[index];
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetEdgeSubjectIndex(final int index) {
		return this.edgeSubjects[index];
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetEdgePredicateIndex(final int index) {
		return this.edgePredicates[index];
	}

	/** {@inheritDoc} */
	@Override
	protected STSNode customGetNode(final FEMBinary value) {
		return this.getNodeImpl(value, true);
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetNodeCount() {
		return this.nodeCount;
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetNodeIndex() {
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	protected FEMBinary customGetNodeValue(final int index) {
		return this.nodeValues[index];
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdge customPutEdge(final int subjectIndex, final int predicateIndex, final int objectIndex) {
		return this.getEdgeImpl(subjectIndex, predicateIndex, objectIndex, false);
	}

	/** {@inheritDoc} */
	@Override
	protected STSNode customPutNode(final FEMBinary value) {
		return this.getNodeImpl(value, false);
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdgeSet customSelectEdgeSetByObject(final int objectIndex) {
		return this.getNodeEdgeSetImpl(this.nodeObjectEdges[objectIndex]);
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdgeSet customSelectEdgeSetBySubject(final int subjectIndex) {
		return this.getNodeEdgeSetImpl(this.nodeSubjectEdges[subjectIndex]);
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdgeSet customSelectEdgeSetByPredicate(final int predicateIndex) {
		return this.getNodeEdgeSetImpl(this.nodePredicateEdges[predicateIndex]);
	}

}
