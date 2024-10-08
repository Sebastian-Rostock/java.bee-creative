package bee.creative.str;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.function.Consumer;
import bee.creative.lang.Strings;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine Menge {@link STREdge typisierter Hyperkanten}, auf welche effizient sowohl von der {@link #getSourceRefs() Quellreferenzen}
 * als auch von den {@link #getTargetRefs() Zielreferenzen} zugegriffen werden kann.
 * <p>
 * Eine Hyperkantenmenge kann in eine {@link #toInts() kompakte Abschrift} überführt werden, die auch {@link #toBytes(ByteOrder) binarisiert} bereitgestellt
 * werden kann. Wenn eine Hyperkantenmenge aus einer solchen kompakte Abschrift erzeugt wird, erfolgt deren Expansion grundsätzlich beim ersten Zugriff auf die
 * Referenzen der {@link STREdge Hyperkanten}, außer bei {@link #toInts()} und {@link #edges()}.
 * <p>
 * Die über {@link #getNextRef()} und {@link #getRootRef()} bereitgestellten Referenzen haben Bedeutung für {@link STRBuffer} und {@link STRUpdate}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class STRState {

	/** Dieses Feld speichert die leere {@link STRState Hyperkantenmenge}. */
	public static final STRState EMPTY = new STRState();

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toInts() kompakten Abschrift}. */
	public static STRState from(int[] storage) {
		return new STRState(storage.clone());
	}

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit nativer
	 * Bytereihenfolge. */
	public static STRState from(byte[] bytes) {
		return STRState.from(bytes, ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit der gegebenen
	 * Bytereihenfolge. */
	public static STRState from(byte[] bytes, ByteOrder order) {
		return STRState.from(ByteBuffer.wrap(bytes), order);
	}

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toInts() kompakten Abschrift}. */
	public static STRState from(IntBuffer buffer) {
		var storage = new int[buffer.remaining()];
		buffer.duplicate().get(storage);
		return new STRState(storage);
	}

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit nativer
	 * Bytereihenfolge. */
	public static STRState from(ByteBuffer buffer) {
		return STRState.from(buffer, ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link STRState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit der gegebenen
	 * Bytereihenfolge. */
	public static STRState from(ByteBuffer buffer, ByteOrder order) {
		return STRState.from(buffer.duplicate().order(order).asIntBuffer());
	}

	/** Diese Methode liefert die gegebenen {@link STREdge Hyperkanten} als {@link STRState Hyperkantenmenge}. */
	public static STRState from(STREdge... edges) {
		return STRState.from(Arrays.asList(edges));
	}

	/** Diese Methode liefert die gegebenen {@link STREdge Hyperkanten} als {@link STRState Hyperkantenmenge}. */
	public static STRState from(Iterable<STREdge> edges) {
		var result = new STRState();
		edges.forEach(edge -> result.insert(edge.sourceRef, edge.targetRef, edge.relationRef));
		return result;
	}

	/** Diese Methode liefert eine Kopie der gegebenen {@link STRState Hyperkantenmenge}. */
	public static STRState from(STRState edges) {
		var result = new STRState();
		edges.forEachEdge(result::insert);
		return result;
	}

	/** Diese Methode liefert die {@link STREdge Hyperkanten} des {@code newState} ohne denen des {@code oldState}, bspw. für {@link STRUpdate#getPutState()} und
	 * {@link STRUpdate#getPopState()}.
	 *
	 * @param oldState alte Hyperkantenmenge.
	 * @param newState neue Hyperkantenmenge.
	 * @return Differenz der Hyperkantenmengen. */
	public static STRState from(STRState oldState, STRState newState) {
		var result = new STRState();
		oldState.restore();
		newState.restore();
		if (oldState == newState) return result;
		result.nextRef = newState.nextRef - oldState.nextRef;
		result.rootRef = newState.rootRef - oldState.rootRef;
		var oldSourceMap = oldState.sourceMap;
		var newSourceMap = newState.sourceMap;
		var newSourceKeys = REFMAP.getKeys(newSourceMap);
		for (var newSourceIdx = newSourceMap.length - 1; 0 < newSourceIdx; newSourceIdx--) {
			var newRelationMap = STRState.asRefMap(REFMAP.getVal(newSourceMap, newSourceIdx));
			if (newRelationMap != null) {
				var sourceRef = REFSET.getRef(newSourceKeys, newSourceIdx);
				var oldSourceIdx = REFMAP.getIdx(oldSourceMap, sourceRef);
				if (oldSourceIdx == 0) {
					// COPY newRelationMap
					var newRelationKeys = REFMAP.getKeys(newRelationMap);
					for (var newRelationIdx = newRelationMap.length - 1; 0 < newRelationIdx; newRelationIdx--) {
						var newTargetVal = STRState.asRefVal(REFMAP.getVal(newRelationMap, newRelationIdx));
						if (newTargetVal != null) {
							// COPY newTargetVal
							var relationRef = REFSET.getRef(newRelationKeys, newRelationIdx);
							if (STRState.isRef(newTargetVal)) {
								result.insert(sourceRef, relationRef, STRState.asRef(newTargetVal));
							} else {
								for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
									var newTargetRef = newTargetVal[newTargetIdx];
									if (newTargetRef != 0) {
										result.insert(sourceRef, relationRef, newTargetRef);
									}
								}
							}
						}
					}
				} else {
					var oldRelationMap = STRState.asRefMap(REFMAP.getVal(oldSourceMap, oldSourceIdx));
					if (newRelationMap != oldRelationMap) {
						var newRelationKeys = REFMAP.getKeys(newRelationMap);
						for (var newRelationIdx = newRelationMap.length - 1; 0 < newRelationIdx; newRelationIdx--) {
							var newTargetVal = STRState.asRefVal(REFMAP.getVal(newRelationMap, newRelationIdx));
							if (newTargetVal != null) {
								var relationRef = REFSET.getRef(newRelationKeys, newRelationIdx);
								var oldRelationIdx = REFMAP.getIdx(oldRelationMap, relationRef);
								if (oldRelationIdx == 0) {
									// COPY newTargetVal
									if (STRState.isRef(newTargetVal)) {
										result.insert(sourceRef, relationRef, STRState.asRef(newTargetVal));
									} else {
										for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
											var newTargetRef = newTargetVal[newTargetIdx];
											if (newTargetRef != 0) {
												result.insert(sourceRef, relationRef, newTargetRef);
											}
										}
									}
								} else {
									var oldTargetVal = STRState.asRefVal(REFMAP.getVal(oldRelationMap, oldRelationIdx));
									if (newTargetVal != oldTargetVal) {
										if (STRState.isRef(oldTargetVal)) {
											var oldTargetRef = STRState.asRef(oldTargetVal);
											if (STRState.isRef(newTargetVal)) {
												var newTargetRef = STRState.asRef(newTargetVal);
												if (oldTargetRef != newTargetRef) {
													result.insert(sourceRef, relationRef, newTargetRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (oldTargetRef != newTargetRef) {
															result.insert(sourceRef, relationRef, newTargetRef);
														}
													}
												}
											}
										} else {
											if (STRState.isRef(newTargetVal)) {
												var newTargetRef = STRState.asRef(newTargetVal);
												if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
													result.insert(sourceRef, relationRef, newTargetRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
															result.insert(sourceRef, relationRef, newTargetRef);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public STREdges edges() {
		return edges;
	}

	public STRValues values() {
		return values;
	}

	/** Diese Methode liefert die Referenz auf die nächste neue Entität oder {@code 0}. Wenn dieses Objekt über {@link #from(STRState, STRState)} erzeugt wurde,
	 * liefert sie {@code newState.getNextRef() - oldState.getNextRef()}. */
	public int getNextRef() {
		return this.nextRef;
	}

	/** Diese Methode liefert die Referenz auf die Entität des Inhaltsverzeichnisses oder {@code 0}. Wenn dieses Objekt über {@link #from(STRState, STRState)}
	 * erzeugt wurde, liefert sie {@code newState.getRootRef() - oldState.getRootRef()}. */
	public int getRootRef() {
		return this.rootRef;
	}

	String TODO_getValue(int valueRef) {
		if (valueRef == 0) return null;
		this.restore();
		// TODO
		return null;
	}

	int TODO_getValueRef(String value) {
		if (value == null) return 0;
		this.restore();
		// TODO
		return 0;
	}

	int[] TODO_getValueRefs() {
		this.restore();
		// TODO
		return STRState.EMPTY_REFS;
	}

	int TODO_getValueCount() {
		this.restore();
		// TODO
		return 0;
	}

	/** Diese Methode liefert die als {@link STREdge#sourceRef()} vorkommenden Referenzen. */
	public int[] getSourceRefs() {
		this.restore();
		return REFMAP.toArray(this.sourceMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#sourceRef()} vorkommenden Referenzen. */
	public int getSourceCount() {
		this.restore();
		return REFMAP.size(this.sourceMap);
	}

	/** Diese Methode liefert die als {@link STREdge#relationRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene {@code sourceRef}
	 * als {@link STREdge#sourceRef()}. */
	public int[] getSourceRelationRefs(int sourceRef) {
		if (sourceRef == 0) return STRState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return STRState.EMPTY_REFS;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.toArray(relationMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#relationRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code sourceRef} als {@link STREdge#sourceRef()}. */
	public int getSourceRelationCount(int sourceRef) {
		if (sourceRef == 0) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.size(relationMap);
	}

	/** Diese Methode liefert eine der als {@link STREdge#targetRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code sourceRef} als {@link STREdge#sourceRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()} oder {@code 0}. */
	public int getSourceRelationTargetRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(targetVal)) return STRState.asRef(targetVal);
		return REFSET.getRef(targetVal);
	}

	/** Diese Methode liefert die als {@link STREdge#targetRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene {@code sourceRef} als
	 * {@link STREdge#sourceRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()}. */
	public int[] getSourceRelationTargetRefs(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return STRState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return STRState.EMPTY_REFS;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return STRState.EMPTY_REFS;
		var targetVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(targetVal)) return new int[]{STRState.asRef(targetVal)};
		return REFSET.toArray(targetVal);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#targetRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code sourceRef} als {@link STREdge#sourceRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()}. */
	public int getSourceRelationTargetCount(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(targetVal)) return 1;
		return REFSET.size(targetVal);
	}

	/** Diese Methode liefert die als {@link STREdge#targetRef()} vorkommenden Referenzen. */
	public int[] getTargetRefs() {
		this.restore();
		return REFMAP.toArray(this.targetMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#targetRef()} vorkommenden Referenzen. */
	public int getTargetCount() {
		this.restore();
		return REFMAP.size(this.targetMap);
	}

	/** Diese Methode liefert die als {@link STREdge#relationRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene {@code targetRef}
	 * als {@link STREdge#targetRef()}. */
	public int[] getTargetRelationRefs(int targetRef) {
		if (targetRef == 0) return STRState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return STRState.EMPTY_REFS;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.toArray(relationMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#relationRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code targetRef} als {@link STREdge#targetRef()}. */
	public int getTargetRelationCount(int targetRef) {
		if (targetRef == 0) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.size(relationMap);
	}

	/** Diese Methode liefert eine der als {@link STREdge#sourceRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code targetRef} als {@link STREdge#targetRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()} oder {@code 0}. */
	public int getTargetRelationSourceRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(sourceVal)) return STRState.asRef(sourceVal);
		return REFSET.getRef(sourceVal);
	}

	/** Diese Methode liefert die als {@link STREdge#sourceRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene {@code targetRef} als
	 * {@link STREdge#targetRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()} oder {@code 0}. */
	public int[] getTargetRelationSourceRefs(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return STRState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return STRState.EMPTY_REFS;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return STRState.EMPTY_REFS;
		var sourceVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(sourceVal)) return new int[]{STRState.asRef(sourceVal)};
		return REFSET.toArray(sourceVal);
	}

	/** Diese Methode liefert die Anzahl der als {@link STREdge#sourceRef()} vorkommenden Referenzen aller {@link STREdge Hyperkanten} mit der gegebene
	 * {@code targetRef} als {@link STREdge#targetRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()} oder {@code 0}. */
	public int getTargetRelationSourceCount(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(sourceVal)) return 1;
		return REFSET.size(sourceVal);
	}

	public boolean containsEdge(STREdge edge) {
		return (edge != null) && this.containsEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die {@link STREdge Hyperkante} mit der gegebene {@code sourceRef} als {@link STREdge#sourceRef()}, der
	 * gegebene {@code targetRef} als {@link STREdge#targetRef()} und der gegebene {@code relationRef} als {@link STREdge#relationRef()} vorkommt. */
	public boolean containsEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var targetVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (STRState.isRef(targetVal)) return STRState.asRef(targetVal) == targetRef;
		return REFSET.getIdx(targetVal, targetRef) != 0;
	}

	public boolean containsValue(String value) {
		return this.TODO_getValueRef(value) != 0;
	}

	public boolean containsValueRef(int valueRef) {
		return this.TODO_getValue(valueRef) != null;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code sourceRef} als {@link STREdge#sourceRef()} vorkommt. */
	public boolean containsSourceRef(int sourceRef) {
		if (sourceRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.sourceMap, sourceRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code relationRef} als {@link STREdge#relationRef()} von {@link STREdge
	 * Hyperkanten} mit der gegebene {@code sourceRef} als {@link STREdge#sourceRef()} vorkommt. */
	public boolean containsSourceRelationRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code targetRef} als {@link STREdge#targetRef()} vorkommt. */
	public boolean containsTargetRef(int targetRef) {
		if (targetRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.targetMap, targetRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code relationRef} als {@link STREdge#relationRef()} von {@link STREdge
	 * Hyperkanten} mit der gegebene {@code relationRef} als {@link STREdge#relationRef()} vorkommt. */
	public boolean containsTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = STRState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	/** Diese Methode übergibt die Referenzen aller {@link STREdge Hyperkanten} an {@link STREdges.RUN#run(int, int, int) task.run()}. */
	void forEachEdge(STREdges.RUN task) {
		if (this.storage != null) {
			STRState.select(this.storage, task);
		} else {
			STRState.select(this.sourceMap, task);
		}
	}

	void forEachEdge(Consumer<? super STREdge> action) {
		this.forEachEdge((STREdges.RUN)(sourceRef, targetRef, relationRef) -> action.accept(new STREdge(sourceRef, targetRef, relationRef)));
	}

	Iterator2<STREdge> edgeIterator() {
		this.restore();
		var sourceIter = REFMAP.iterator(this.sourceMap);
		return Iterators.concatAll(Iterators.concatAll(new Iterator2<Iterator2<Iterator2<STREdge>>>() {

			@Override
			public Iterator2<Iterator2<STREdge>> next() {
				var relationIter = REFMAP.iterator(STRState.asRefMap(sourceIter.nextVal()));
				var sourceRef = sourceIter.nextRef();
				return new Iterator2<>() {

					@Override
					public Iterator2<STREdge> next() {
						var targetVal = STRState.asRefVal(relationIter.nextVal());
						var relationRef = relationIter.nextRef();
						if (STRState.isRef(targetVal)) return Iterators.fromItem(new STREdge(sourceRef, STRState.asRef(targetVal), relationRef));
						var targetIter = REFSET.iterator(targetVal);
						return new Iterator2<>() {

							@Override
							public STREdge next() {
								return new STREdge(sourceRef, targetIter.nextRef(), relationRef);
							}

							@Override
							public boolean hasNext() {
								return targetIter.hasNext();
							}

						};
					}

					@Override
					public boolean hasNext() {
						return relationIter.hasNext();
					}

				};
			}

			@Override
			public boolean hasNext() {
				return sourceIter.hasNext();
			}

		}));
	}

	Iterator2<STREdge> edgeIterator(REFSET sourceRefs, REFSET targetRefs, REFSET relationRefs) {
		return null; // TODO
	}

	/** Diese Methode liefert ein kompakte Abschrift aller {@link STREdge Hyperkanten} dieser Menge als {@code int}-Array mit der Struktur
	 * {@code (nextRef, rootRef, sourceCount, (sourceRef, targetRefCount, (targetRef, relationRef)[targetRefCount], targetSetCount, (relationRef, targetCount, targetRef[targetCount])[targetSetCount])[sourceCount])}.
	 *
	 * @return kompakte Abschrift. */
	public int[] toInts() {
		if (this.storage != null) return this.storage.clone();
		var sourceMap = this.sourceMap;
		var sourceCount = 0;
		var storageSize = 3;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = STRState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var relationSize = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = STRState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (STRState.isRef(targetVal)) {
							relationSize += /*relationRef*/ 1 + /*targetRef*/ 1;
						} else {
							var targetCount = REFSET.size(targetVal);
							if (targetCount == 1) {
								relationSize += /*relationRef*/ 1 + /*targetRef*/ 1;
							} else if (targetCount > 0) {
								relationSize += /*relationRef*/ 1 + /*targetCount*/ 1 + /*targetRef*/ targetCount;
							}
						}
					}
				}
				if (relationSize != 0) {
					storageSize += /*sourceRef, targetRefCount, targetSetCount*/ 3 + relationSize;
					sourceCount++;
				}
			}
		}
		var storage = new int[storageSize];
		var storageIdx = 0;
		storage[storageIdx++] = this.rootRef;
		storage[storageIdx++] = this.nextRef;
		storage[storageIdx++] = sourceCount;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = STRState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var targetRefCount = 0;
				var targetSetCount = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = STRState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (STRState.isRef(targetVal)) {
							targetRefCount++;
						} else {
							var targetCount = REFSET.size(targetVal);
							if (targetCount == 1) {
								targetRefCount++;
							} else if (targetCount > 0) {
								targetSetCount++;
							}
						}
					}
				}
				if ((targetRefCount != 0) || (targetSetCount != 0)) {
					storage[storageIdx++] = REFSET.getRef(REFMAP.getKeys(sourceMap), sourceIdx);
					storage[storageIdx++] = targetRefCount;
					if (targetRefCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = STRState.asRefVal(relationMap[relationIdx]);
							if ((targetVal != null) && (STRState.isRef(targetVal) || (REFSET.size(targetVal) == 1))) {
								storage[storageIdx++] = STRState.asRef(targetVal);
								storage[storageIdx++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
							}
						}
					}
					storage[storageIdx++] = targetSetCount;
					if (targetSetCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = STRState.asRefVal(relationMap[relationIdx]);
							if (targetVal != null) {
								if (!STRState.isRef(targetVal)) {
									var targetCount = REFSET.size(targetVal);
									if (targetCount > 1) {
										storage[storageIdx++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
										storage[storageIdx++] = targetCount;
										for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
											var targetRef = targetVal[targetIdx];
											if (targetRef != 0) {
												storage[storageIdx++] = targetRef;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return storage;
	}

	/** Diese Methode liefert die {@link #toInts()} als {@code byte}-Array mit nativer Bytereihenfolge. */
	public byte[] toBytes() {
		return this.toBytes(ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link #toInts()} als {@code byte}-Array mit der gegebenen Bytereihenfolge {@code order}. */
	public byte[] toBytes(ByteOrder order) {
		var ints = this.toInts();
		var bytes = ByteBuffer.allocate(ints.length * 4);
		bytes.order(order).asIntBuffer().put(ints);
		return bytes.array();
	}

	public byte[] persist() throws IOException {
		var o = new BINWRITER();
		for (var i: this.toInts()) {
			o.writeInt(i);
		}
		return o.getBytes();
	}

	@Override
	public String toString() {
		var res = new StringBuilder("{ ");
		Strings.join(res, ", ", this.edges());
		return res.append(" }").toString();
	}

	static boolean isRef(int[] val) {
		return val.length == 1;
	}

	static int[] toRef(int ref) {
		return new int[]{ref};
	}

	static int asRef(int[] val) {
		return val[0];
	}

	static int[] asRefVal(Object val) {
		return (int[])val;
	}

	static Object[] asRefMap(Object val) {
		return (Object[])val;
	}

	int nextRef;

	int rootRef;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@link STREdge#sourceRef} auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@link STREdge#relationRef} auf {@link STREdge#targetRef}. Letztere sind dabei als {@code int[1]} oder gemäß {@link REFSET} abgebildet. */
	Object[] sourceMap = REFMAP.EMPTY;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@link STREdge#targetRef} auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@link STREdge#relationRef} auf {@link STREdge#sourceRef}. Letztere sind dabei als {@code int[1]} oder gemäß {@link REFSET} abgebildet. */
	Object[] targetMap = REFMAP.EMPTY;
	
	

	final STREdges edges = new STREdges(this);

	final STRValues values = new STRValues(this);

	int[] storage;

	/** Dieser Konstruktor erzeugt eine leere Menge. */
	STRState() {
	}

	/** Dieser Konstruktor übernimmt die Merkmale der gegebenen {@link #toInts() kompakten Abschrift}. */
	STRState(int[] storage) {
		this.nextRef = storage[0];
		this.rootRef = storage[1];
		this.storage = storage;
	}

	/** Dieser Konstruktor übernimmt die Merkmale des gegebenen {@link STRState}. */
	STRState(STRState that) {
		this.rootRef = that.rootRef;
		this.nextRef = that.nextRef;
		this.sourceMap = that.sourceMap;
		this.targetMap = that.targetMap;
		this.storage = that.storage;
	}

	/** Diese Methode ersetzt {@link #sourceMap} und {@link #targetMap} nur dann mit den in {@link #storage} hinterlegten {@link STREdge Hyperkanten}, wenn
	 * {@link #storage} nicht {@code null} ist. Anschließend wird {@link #storage} auf {@code null} gesetzt. */
	void restore() throws IllegalStateException {
		if (this.storage == null) return;
		try {
			this.sourceMap = REFMAP.EMPTY;
			this.targetMap = REFMAP.EMPTY;
			STRState.select(this.storage, this::insert);
			this.storage = null;
		} finally {
			if (this.storage != null) {
				this.sourceMap = REFMAP.EMPTY;
				this.targetMap = REFMAP.EMPTY;
			}
		}
	}

	private static final int[] EMPTY_REFS = new int[0];

	static void select(int[] storage, STREdges.RUN task) {
		var storageIdx = 2;
		var sourceCount = storage[storageIdx++];
		while (0 < sourceCount--) {
			var sourceRef = storage[storageIdx++];
			var targetRefCount = storage[storageIdx++];
			while (0 < targetRefCount--) {
				var targetRef = storage[storageIdx++];
				var relationRef = storage[storageIdx++];
				task.run(sourceRef, targetRef, relationRef);
			}
			var targetSetCount = storage[storageIdx++];
			while (0 < targetSetCount--) {
				var relationRef = storage[storageIdx++];
				var targetCount = storage[storageIdx++];
				while (0 < targetCount--) {
					var targetRef = storage[storageIdx++];
					task.run(sourceRef, targetRef, relationRef);
				}
			}
		}
	}

	static void select(Object[] sourceMap, STREdges.RUN task) {
		var sourceKeys = REFMAP.getKeys(sourceMap);
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = STRState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
			if (relationMap != null) {
				var sourceRef = REFSET.getRef(sourceKeys, sourceIdx);
				var relationKeys = REFMAP.getKeys(relationMap);
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = STRState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
					if (targetVal != null) {
						var relationRef = REFSET.getRef(relationKeys, relationIdx);
						if (STRState.isRef(targetVal)) {
							task.run(sourceRef, STRState.asRef(targetVal), relationRef);
						} else {
							for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
								var targetRef = targetVal[targetIdx];
								if (targetRef != 0) {
									task.run(sourceRef, targetRef, relationRef);
								}
							}
						}
					}
				}
			}
		}
	}

	private static Object[] insert(Object[] sourceMap, int sourceRef, int relationRef, int targetRef) throws IllegalStateException {

		sourceMap = REFMAP.grow(sourceMap);

		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceIdx == 0) throw new IllegalStateException();

		var sourceRelationMap = STRState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		sourceRelationMap = sourceRelationMap == null ? REFMAP.create() : REFMAP.grow(sourceRelationMap);
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) throw new IllegalStateException();

		var sourceRelationTargetVal = STRState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, STRState.toRef(targetRef));
		} else if (STRState.isRef(sourceRelationTargetVal)) {
			var targetRef2 = STRState.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return sourceMap;
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(sourceRelationTargetVal);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
			var targetIdx = REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (targetIdx == 0) throw new IllegalStateException();
		}

		return sourceMap;
	}

	private void insert(int sourceRef, int relationRef, int targetRef) throws IllegalStateException {
		this.sourceMap = STRState.insert(this.sourceMap, sourceRef, relationRef, targetRef);
		this.targetMap = STRState.insert(this.targetMap, targetRef, relationRef, sourceRef);
	}

	String l(int[] a, int o, int l) {

		return new String(a, o, l);
	}

}