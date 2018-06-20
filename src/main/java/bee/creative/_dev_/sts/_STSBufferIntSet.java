package bee.creative._dev_.sts;

import java.nio.ByteBuffer;

// OUTER NODE [ H | V1 | ... | V15 ]
// INNER NODE [ H | P0 | V1 | P1 | ... | V7 | P7 ]
// HEAD [ Type: short | Offset: byte | Length: byte ]
// NODE-Address zeigt auf H
public class _STSBufferIntSet {

	static final int NODE_SIZE = 16 * 4; // byte

	static final int INNER_NODE_TYPE = 64;

	static final int INNER_NODE_CAPACITY = (_STSBufferIntSet.NODE_SIZE >>> 3) - 1;

	static final int OUTER_NODE_TYPE = 128;

	static final int OUTER_NODE_CAPACITY = (_STSBufferIntSet.NODE_SIZE >>> 2) - 1;

	static interface Iter {

		public int next(int minNext);

	}

	ByteBuffer buffer;

	int nodeCount;

	int rootNode;

	int cc() {
		final int c = this.nodeCount, r = this.nodeCount * _STSBufferIntSet.NODE_SIZE;
		this.nodeCount = c + 1;
		return r;
	}

	void put(final int value) {
		final int rootId = this.rootNode;

		final int rootHead = this.getNodeHead(rootId);
		final int rootLength = this.getNodeLength(rootHead);

		if (this.isNodeTypwOuter(rootHead)) {
			if (rootLength == _STSBufferIntSet.OUTER_NODE_CAPACITY) {

				
				
				// Wurlen muss wachsen
				// Wurzel hatte nur werte -> 2 mal terminale mit je 50% der elemente erzeugen
				// mittleres element in den rootnode mittig eintragen, dessen heder neu setzen

				final int OUTER_NODE_LENGTH = _STSBufferIntSet.OUTER_NODE_CAPACITY >>> 1; // 7
				final int OUTER_NODE_OFFSET = _STSBufferIntSet.OUTER_NODE_CAPACITY >>> 2; // 3

				final int prevOuterId = this.newNodeId(); // beliebige daten im speicherbereich
				// OUETER, offset, length, capacity = 128, 3, 7, 15
				this.setNodeHead(prevOuterId, _STSBufferIntSet.OUTER_NODE_TYPE, OUTER_NODE_OFFSET, OUTER_NODE_LENGTH);

				final int nextOuterNode = this.newNodeId();
				this.setNodeHead(nextOuterNode, _STSBufferIntSet.OUTER_NODE_TYPE, OUTER_NODE_OFFSET, OUTER_NODE_LENGTH);

				// werte aus root in die neuen knoten kopieren
				this.copyList(rootId, 0, prevOuterId, OUTER_NODE_OFFSET, OUTER_NODE_LENGTH);
				// mittleren auslassen
				this.copyList(rootId, OUTER_NODE_LENGTH + 1, nextOuterNode, OUTER_NODE_OFFSET, OUTER_NODE_LENGTH);
				// mittleren extra halten
				final int rootCenter = this.getNodeValue(rootId, OUTER_NODE_LENGTH + 1);

				this.setNodeHead(rootId, _STSBufferIntSet.INNER_NODE_TYPE, OUTER_NODE_OFFSET, 1);
				this.setNodeValue(rootId, OUTER_NODE_OFFSET, rootCenter);
				this.setNodeValue(rootId, OUTER_NODE_OFFSET + OUTER_NODE_LENGTH, prevOuterId);
				this.setNodeValue(rootId, OUTER_NODE_OFFSET + OUTER_NODE_LENGTH + 1, nextOuterNode);

			}
		} else {
			if (rootLength == _STSBufferIntSet.INNER_NODE_CAPACITY) {
				 // mittig Vm behalten, Pm-1 und Pm überschreiben  
			}
		}
//		if (isInner(rootHead)) {
//			if (isFullInner(rootHead)) {
//				splitInnerRoot();
//			}
//			putInner(this.rootNode, value);
//		} else {
//			if (isFullOuter(this.rootNode)) {
//				splitOuterRoot();
//				putInner(this.rootNode, value);
//			} else {
//				this.putOuter(this.rootNode, value);
//			}
//		}
	}

	final void copyList(final int sourceId, int sourceOffset, final int targetId, int targetOffset, int length) {
		while (length > 0) {
			this.setNodeValue(targetId, targetOffset, this.getNodeValue(sourceId, sourceOffset));
			sourceOffset++;
			targetOffset++;
			length--;
		}
	}

	final boolean isNodeTypwOuter(final int nodeHead) {
		return nodeHead < 0;
	}

	final void setNodeHead(final int nodeId, final int nodeType, final int nodeOffset, final int nodeLength) {
		buffer.putInt(nodeId, (nodeType << 24) | (nodeOffset << 8) | (nodeLength << 0));
	}

	/** Diese Methode reserviert speicher für einen neuen Knoten und gibt dessen Kennung zurück. */
	final int newNodeId() {
		return 0;
	}

	final void setNodeValue(final int rootNode, final int index, final int value) {
		this.buffer.putInt(rootNode + (index * 4) + 4, value);
	}

	final int getNodeValue(final int rootNode, final int index) {
		return this.buffer.getInt(rootNode + (index * 4) + 4);
	}

	/** Diese Methode gibt die Anzahl der Elemente im Knoten mit den gegebenen Kopfdaten zurück. */
	final int getNodeLength(final int nodeHead) {
		return (nodeHead >>> 0) & 255;
	}

	/** Diese Methode gibt die Position des ersten Elements im Knoten mit den gegebenen Kopfdaten zurück. */
	final int getNodeOffset(final int nodeHead) {
		return (nodeHead >>> 8) & 255;
	}

	/** Diese Methode gibt die Kopfdaten des Knoten mit der gegebenen Kennung zurück. */
	final int getNodeHead(final int nodeId) {
		return this.buffer.getInt(nodeId);
	}

	private void putOuter(final int outerNode, final int value) {
//		// outerNode hat platz
//		final int s = valueStart(outerNode);
//		final int c = valueCount(outerNode);
//
//		int l = s;
//		int r = l + c;
//		final int m = (l + r) >> 2;
//
//		boolean insl;
//
//		final int v = valueAt(outerNode, m);
//
//		if (value == v) return; // bereits enthalten
//
//		if (value < v) {
//			insl = l != 0;
//			r = m;
//		} else {
//			insl = r != _STSBufferIntSet.NODE_SIZE;
//			l = m + 1;
//		}
//
//		while (l < r) {
//			// binär suchen
//
//		}
//
//		if (insl) {
//			// linls einfügen
//			moveL(outerNode, s, l - s);
//			valueSet(outerNode, l - 1, value);
//		} else {
//			// rechts einfügen
//			moveR(outerNode, l, (s + c) - l);
//			valueSet(outerNode, l, value);
//		}

	}

}
