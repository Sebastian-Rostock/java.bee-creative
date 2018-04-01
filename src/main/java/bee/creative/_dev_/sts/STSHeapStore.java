package bee.creative._dev_.sts;

import java.util.Arrays;
import bee.creative.array.CompactIntegerArray;
import bee.creative.array.CompactObjectArray;
import bee.creative.fem.FEMBinary;
import bee.creative.iam.IAMMapping;
import bee.creative.util.HashMap;
import bee.creative.util.Objects;

public final class STSHeapStore extends STSStore {

	static class IndexList extends CompactIntegerArray {

		public IndexList() {
			this.setAlignment(0);
			this.allocate(16);
		}

	}

	static class IndexListList extends CompactObjectArray<IndexList> {

		public IndexListList() {
			this.setAlignment(0);
			this.allocate(16);
		}

		@Override
		protected IndexList[] customNewArray(final int length) {
			return new IndexList[length];
		}

	}

	static class NodeMapping extends HashMap<FEMBinary, STSNode> {

		/** Dieses Feld speichert das serialVersionUID. */
		private static final long serialVersionUID = -6604021955501551348L;

	}

	int edgeCount = 0;

	int[] edgeTable = new int[8];

	int[] edgeNexts = new int[8];

	int[] edgeObjects = new int[8];

	int[] edgeSubjects = new int[8];

	int[] edgePredicates = new int[8];

	STSEdge getEdgeImpl(final int subjectIndex, final int predicateIndex, final int objectIndex, final boolean readonly) {
		final int hash = this.hashEdgeImpl(subjectIndex, predicateIndex, objectIndex);
		final int hashIndex = hash & (this.edgeTable.length - 1);
		int edgeIndex = this.edgeTable[hashIndex];
		while (edgeIndex != 0) {
			if ((this.edgeSubjects[edgeIndex] == subjectIndex) && (this.edgePredicates[edgeIndex] == predicateIndex) && (this.edgeObjects[edgeIndex] == objectIndex))
				return this.customGetEdge(edgeIndex);
			edgeIndex = this.edgeNexts[edgeIndex];
		}
		if (readonly) return null;
		edgeIndex = this.newEdgeImpl();
		this.edgeNexts[edgeIndex] = this.edgeTable[hashIndex];
		this.edgeTable[hashIndex] = edgeIndex;
		return this.customGetEdge(edgeIndex);
	}

	int newEdgeImpl() {
		final int count = this.edgeCount + 1, capacity = this.edgeNexts.length;
		if (count < capacity) return this.edgeCount = count;
		this.allocateEdges((capacity << 1) + capacity + 1);
		return this.edgeCount = count;
	}

	int hashEdgeImpl(final int subjectIndex, final int predicateIndex, final int objectIndex) {
		int result = Objects.hashInit();
		result = Objects.hashPush(result, subjectIndex);
		result = Objects.hashPush(result, predicateIndex);
		result = Objects.hashPush(result, objectIndex);
		return result;
	}

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
			final int index = this.hashEdgeImpl(this.edgeSubjects[i], this.edgePredicates[i], this.edgeObjects[i]) & mask;
			this.edgeNexts[i] = this.edgeTable[index];
			this.edgeTable[index] = i;
		}
	}

	{}

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

	@Override
	protected STSNode customGetNode(final FEMBinary value) {
		return null;
	}

	@Override
	protected int customGetNodeCount() {
		return 0;
	}

	@Override
	protected FEMBinary customGetNodeValue(final int index) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected STSEdge customPutEdge(final int subjectIndex, final int predicateIndex, final int objectIndex) {
		return this.getEdgeImpl(subjectIndex, predicateIndex, objectIndex, false);
	}

	@Override
	protected STSNode customPutNode(final FEMBinary value) {
		return null;
	}

	@Override
	protected STSEdgeSet customSelectEdgeSetByObject(final int objectIndex) {
		return null;
	}

	@Override
	protected STSEdgeSet customSelectEdgeSetBySubject(final int subjectIndex) {
		return null;
	}

	@Override
	protected STSEdgeSet customSelectEdgeSetByPredicate(final int predicateIndex) {
		return null;
	}

}
