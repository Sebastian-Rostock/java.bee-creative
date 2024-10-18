package bee.creative.kb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import bee.creative.fem.FEMString;
import bee.creative.lang.Objects;
import bee.creative.util.Getter;
import bee.creative.util.HashMapIO;
import bee.creative.util.HashMapOI;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert einen Wissensstand (<em>knowledge-base state</em>) als Menge {@link KBEdge typisierter gerichteter Kanen} zwischen Knoten mit
 * optinalem eineindeutigem {@link #getValue(int) Textwert}. , auf welche effizient sowohl von der{@link #getSourceRefs() Quellreferenzen} als auch von den
 * {@link #getTargetRefs() Zielreferenzen} zugegriffen werden kann. Jedem Textwert ist eineindeutig eine {@link #getValueRef(FEMString) Textreferenz}
 * zugeordnet.
 * <p>
 * Eine Hyperkantenmenge kann in eine {@link #toInts() kompakte Abschrift} überführt werden, die auch {@link #toBytes(ByteOrder) binarisiert} bereitgestellt
 * werden kann. Wenn eine Hyperkantenmenge aus einer solchen kompakte Abschrift erzeugt wird, erfolgt deren Expansion grundsätzlich beim ersten Zugriff auf die
 * Referenzen der {@link KBEdge Hyperkanten}, außer bei {@link #toInts()} und {@link #edges()}.
 * <p>
 * Die über {@link #getIndexRef()}, {@link #getCurrentExternalRef()} und {@link #getCurrentInternalRef()} und bereitgestellten Referenzen haben Bedeutung für
 * {@link KBBuffer} und {@link KBUpdate}.
 * <p>
 * ({@link KBEdge#sourceRef()}, {@link KBEdge#relationRef()} und {@link KBEdge#targetRef()})
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBState {

	/** Dieses Feld speichert die leere {@link KBState Hyperkantenmenge}. */
	public static final KBState EMPTY = new KBState();

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toInts() kompakten Abschrift}. */
	public static KBState from(int[] storage) {
		return new KBState(storage.clone());
	}

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit nativer
	 * Bytereihenfolge. */
	public static KBState from(byte[] bytes) {
		return KBState.from(bytes, ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit der gegebenen
	 * Bytereihenfolge. */
	public static KBState from(byte[] bytes, ByteOrder order) {
		return KBState.from(ByteBuffer.wrap(bytes), order);
	}

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toInts() kompakten Abschrift}. */
	public static KBState from(IntBuffer buffer) {
		var storage = new int[buffer.remaining()];
		buffer.duplicate().get(storage);
		return new KBState(storage);
	}

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit nativer
	 * Bytereihenfolge. */
	public static KBState from(ByteBuffer buffer) {
		return KBState.from(buffer, ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link KBState Hyperkantenmenge} zur gegebenen {@link #toBytes() binarisierten kompakten Abschrift} mit der gegebenen
	 * Bytereihenfolge. */
	public static KBState from(ByteBuffer buffer, ByteOrder order) {
		return KBState.from(buffer.duplicate().order(order).asIntBuffer());
	}

	/** Diese Methode liefert die gegebenen {@link KBEdge Hyperkanten} als {@link KBState Hyperkantenmenge}. */
	public static KBState from(KBEdge... edges) {
		return KBState.from(Arrays.asList(edges));
	}

	/** Diese Methode liefert die gegebenen {@link KBEdge Hyperkanten} als {@link KBState Hyperkantenmenge}. */
	public static KBState from(Iterable<KBEdge> edges) {
		var result = new KBState();
		edges.forEach(edge -> result.insertEdge(edge.sourceRef, edge.targetRef, edge.relationRef));
		return result;
	}

	/** Diese Methode liefert eine Kopie der gegebenen {@link KBState Hyperkantenmenge}. */
	public static KBState from(KBState state) {
		var result = new KBState();
		state.restore();
		result.indexRef = state.indexRef;
		result.currentInternalRef = state.currentInternalRef;
		result.currentExternalRef = state.currentExternalRef;
		result.valueRefMap = (ValueRefMap)state.valueRefMap.clone();
		result.valueStrMap = (ValueStrMap)state.valueStrMap.clone();
		state.forEachEdge(result::insertEdge);
		return result;
	}

	/** Diese Methode liefert die {@link KBEdge Hyperkanten} des {@code newState} ohne denen des {@code oldState}, bspw. für {@link KBUpdate#getInserts()} und
	 * {@link KBUpdate#getDeletes()}.
	 *
	 * @param oldState alte Hyperkantenmenge.
	 * @param newState neue Hyperkantenmenge.
	 * @return Differenz der Hyperkantenmengen. */
	public static KBState from(KBState oldState, KBState newState) {
		var result = new KBState();
		oldState.restore();
		newState.restore();
		if (oldState == newState) return result;
		result.indexRef = newState.indexRef - oldState.indexRef;
		result.currentInternalRef = newState.currentInternalRef - oldState.currentInternalRef;
		result.currentExternalRef = newState.currentExternalRef - oldState.currentExternalRef;
		var oldSourceMap = oldState.sourceMap;
		var newSourceMap = newState.sourceMap;
		var newSourceKeys = REFMAP.getKeys(newSourceMap);
		for (var newSourceIdx = newSourceMap.length - 1; 0 < newSourceIdx; newSourceIdx--) {
			var newRelationMap = KBState.asRefMap(REFMAP.getVal(newSourceMap, newSourceIdx));
			if (newRelationMap != null) {
				var sourceRef = REFSET.getRef(newSourceKeys, newSourceIdx);
				var oldSourceIdx = REFMAP.getIdx(oldSourceMap, sourceRef);
				if (oldSourceIdx == 0) {
					// COPY newRelationMap
					var newRelationKeys = REFMAP.getKeys(newRelationMap);
					for (var newRelationIdx = newRelationMap.length - 1; 0 < newRelationIdx; newRelationIdx--) {
						var newTargetVal = KBState.asRefVal(REFMAP.getVal(newRelationMap, newRelationIdx));
						if (newTargetVal != null) {
							// COPY newTargetVal
							var relationRef = REFSET.getRef(newRelationKeys, newRelationIdx);
							if (KBState.isRef(newTargetVal)) {
								result.insertEdge(sourceRef, KBState.asRef(newTargetVal), relationRef);
							} else {
								for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
									var newTargetRef = newTargetVal[newTargetIdx];
									if (newTargetRef != 0) {
										result.insertEdge(sourceRef, newTargetRef, relationRef);
									}
								}
							}
						}
					}
				} else {
					var oldRelationMap = KBState.asRefMap(REFMAP.getVal(oldSourceMap, oldSourceIdx));
					if (newRelationMap != oldRelationMap) {
						var newRelationKeys = REFMAP.getKeys(newRelationMap);
						for (var newRelationIdx = newRelationMap.length - 1; 0 < newRelationIdx; newRelationIdx--) {
							var newTargetVal = KBState.asRefVal(REFMAP.getVal(newRelationMap, newRelationIdx));
							if (newTargetVal != null) {
								var relationRef = REFSET.getRef(newRelationKeys, newRelationIdx);
								var oldRelationIdx = REFMAP.getIdx(oldRelationMap, relationRef);
								if (oldRelationIdx == 0) {
									// COPY newTargetVal
									if (KBState.isRef(newTargetVal)) {
										result.insertEdge(sourceRef, KBState.asRef(newTargetVal), relationRef);
									} else {
										for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
											var newTargetRef = newTargetVal[newTargetIdx];
											if (newTargetRef != 0) {
												result.insertEdge(sourceRef, newTargetRef, relationRef);
											}
										}
									}
								} else {
									var oldTargetVal = KBState.asRefVal(REFMAP.getVal(oldRelationMap, oldRelationIdx));
									if (newTargetVal != oldTargetVal) {
										if (KBState.isRef(oldTargetVal)) {
											var oldTargetRef = KBState.asRef(oldTargetVal);
											if (KBState.isRef(newTargetVal)) {
												var newTargetRef = KBState.asRef(newTargetVal);
												if (oldTargetRef != newTargetRef) {
													result.insertEdge(sourceRef, newTargetRef, relationRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (oldTargetRef != newTargetRef) {
															result.insertEdge(sourceRef, newTargetRef, relationRef);
														}
													}
												}
											}
										} else {
											if (KBState.isRef(newTargetVal)) {
												var newTargetRef = KBState.asRef(newTargetVal);
												if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
													result.insertEdge(sourceRef, newTargetRef, relationRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
															result.insertEdge(sourceRef, newTargetRef, relationRef);
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
		var oldValueStrMap = oldState.valueStrMap;
		var newValueStrMap = newState.valueStrMap;
		if (oldValueStrMap != newValueStrMap) {
			newValueStrMap.fastForEach((KBValues.RUN)(valueRef, newValueStr) -> {
				var oldValueStr = oldValueStrMap.get(valueRef);
				if (oldValueStr == null) {
					result.insertValue(valueRef, newValueStr);
				} else {
					if (!oldValueStr.equals(newValueStr)) throw new IllegalArgumentException(); // stimmt das?
				}
			});
		}
		return result;
	}

	public KBEdges edges() {
		return this.edges;
	}

	public KBValues values() {
		return this.values;
	}

	public FEMString getValue(int valueRef) {
		if (valueRef == 0) return null;
		this.restore();
		return this.valueStrMap.get(valueRef);
	}

	public int getValueRef(FEMString valueStr) {
		if (valueStr == null) return 0;
		this.restore();
		var valueRef = this.valueRefMap.get(valueStr);
		return valueRef != null ? valueRef : 0;
	}

	public int[] getValueRefs() {
		this.restore();
		return this.valueStrMap.fastKeys();
	}

	public int getValueCount() {
		this.restore();
		return this.valueStrMap.size();
	}

	/** Diese Methode liefert die als {@link KBEdge#sourceRef()} vorkommenden Referenzen. */
	public int[] getSourceRefs() {
		this.restore();
		return REFMAP.toArray(this.sourceMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#sourceRef()} vorkommenden Referenzen. */
	public int getSourceCount() {
		this.restore();
		return REFMAP.size(this.sourceMap);
	}

	/** Diese Methode liefert die als {@link KBEdge#relationRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code sourceRef} als
	 * {@link KBEdge#sourceRef()}. */
	public int[] getSourceRelationRefs(int sourceRef) {
		if (sourceRef == 0) return KBState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return KBState.EMPTY_REFS;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.toArray(relationMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#relationRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene
	 * {@code sourceRef} als {@link KBEdge#sourceRef()}. */
	public int getSourceRelationCount(int sourceRef) {
		if (sourceRef == 0) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.size(relationMap);
	}

	/** Diese Methode liefert eine der als {@link KBEdge#targetRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code sourceRef}
	 * als {@link KBEdge#sourceRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()} oder {@code 0}. */
	public int getSourceRelationTargetRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(targetVal)) return KBState.asRef(targetVal);
		return REFSET.getRef(targetVal);
	}

	/** Diese Methode liefert die als {@link KBEdge#targetRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code sourceRef} als
	 * {@link KBEdge#sourceRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()}. */
	public int[] getSourceRelationTargetRefs(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return KBState.EMPTY_REFS;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return KBState.EMPTY_REFS;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return KBState.EMPTY_REFS;
		var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(targetVal)) return new int[]{KBState.asRef(targetVal)};
		return REFSET.toArray(targetVal);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#targetRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene
	 * {@code sourceRef} als {@link KBEdge#sourceRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()}. */
	public int getSourceRelationTargetCount(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(targetVal)) return 1;
		return REFSET.size(targetVal);
	}

	/** Diese Methode liefert die als {@link KBEdge#targetRef()} vorkommenden Referenzen. */
	public int[] getTargetRefs() {
		this.restore();
		return REFMAP.toArray(this.targetMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#targetRef()} vorkommenden Referenzen. */
	public int getTargetCount() {
		this.restore();
		return REFMAP.size(this.targetMap);
	}

	/** Diese Methode liefert die als {@link KBEdge#relationRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code targetRef} als
	 * {@link KBEdge#targetRef()}. */
	public int[] getTargetRelationRefs(int targetRef) {
		if (targetRef == 0) return KBState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return KBState.EMPTY_REFS;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.toArray(relationMap);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#relationRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene
	 * {@code targetRef} als {@link KBEdge#targetRef()}. */
	public int getTargetRelationCount(int targetRef) {
		if (targetRef == 0) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.size(relationMap);
	}

	/** Diese Methode liefert eine der als {@link KBEdge#sourceRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code targetRef}
	 * als {@link KBEdge#targetRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()} oder {@code 0}. */
	public int getTargetRelationSourceRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(sourceVal)) return KBState.asRef(sourceVal);
		return REFSET.getRef(sourceVal);
	}

	/** Diese Methode liefert die als {@link KBEdge#sourceRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene {@code targetRef} als
	 * {@link KBEdge#targetRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()} oder {@code 0}. */
	public int[] getTargetRelationSourceRefs(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return KBState.EMPTY_REFS;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return KBState.EMPTY_REFS;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return KBState.EMPTY_REFS;
		var sourceVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(sourceVal)) return new int[]{KBState.asRef(sourceVal)};
		return REFSET.toArray(sourceVal);
	}

	/** Diese Methode liefert die Anzahl der als {@link KBEdge#sourceRef()} vorkommenden Referenzen aller {@link KBEdge Hyperkanten} mit der gegebene
	 * {@code targetRef} als {@link KBEdge#targetRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()} oder {@code 0}. */
	public int getTargetRelationSourceCount(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return 0;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return 0;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return 0;
		var sourceVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(sourceVal)) return 1;
		return REFSET.size(sourceVal);
	}

	/** Diese Methode liefert die Referenz auf die Entität des Inhaltsverzeichnisses oder {@code 0}. Wenn dieses Objekt über {@link #from(KBState, KBState)}
	 * erzeugt wurde, liefert sie {@code newState.getRootRef() - oldState.getRootRef()}. */
	public int getIndexRef() {
		return this.indexRef;
	}

	/** Diese Methode liefert die Referenz, von der aus die nächste für eine neue interne Entität ohne Textwert verfügbare Referenz gesucht wird. Wenn dieses
	 * Objekt über {@link #from(KBState, KBState)} erzeugt wurde, liefert sie {@code newState.getCurrentInternalRef() - oldState.getCurrentInternalRef()}. */
	public int getCurrentInternalRef() {
		return this.currentInternalRef;
	}

	/** Diese Methode liefert die Referenz, von der aus die nächste für eine neue externe Entität mit Textwert verfügbare Referenz gesucht wird. Wenn dieses
	 * Objekt über {@link #from(KBState, KBState)} erzeugt wurde, liefert sie {@code newState.getCurrentExternalRef() - oldState.getCurrentExternalRef()}. */
	public int getCurrentExternalRef() {
		return this.currentExternalRef;
	}

	public boolean containsEdge(KBEdge edge) {
		return (edge != null) && this.containsEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die {@link KBEdge Hyperkante} mit der gegebene {@code sourceRef} als {@link KBEdge#sourceRef()}, der
	 * gegebene {@code targetRef} als {@link KBEdge#targetRef()} und der gegebene {@code relationRef} als {@link KBEdge#relationRef()} vorkommt. */
	public boolean containsEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return false;
		var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (KBState.isRef(targetVal)) return KBState.asRef(targetVal) == targetRef;
		return REFSET.getIdx(targetVal, targetRef) != 0;
	}

	public boolean containsValue(FEMString valueStr) {
		if (valueStr == null) return false;
		this.restore();
		return this.valueRefMap.containsKey(valueStr);
	}

	public boolean containsValueRef(int valueRef) {
		if (valueRef == 0) return false;
		this.restore();
		return this.valueStrMap.containsKey(valueRef);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code sourceRef} als {@link KBEdge#sourceRef()} vorkommt. */
	public boolean containsSourceRef(int sourceRef) {
		if (sourceRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.sourceMap, sourceRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code relationRef} als {@link KBEdge#relationRef()} von {@link KBEdge
	 * Hyperkanten} mit der gegebene {@code sourceRef} als {@link KBEdge#sourceRef()} vorkommt. */
	public boolean containsSourceRelationRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var sourceIdx = REFMAP.getIdx(this.sourceMap, sourceRef);
		if (sourceIdx == 0) return false;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.sourceMap, sourceIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code targetRef} als {@link KBEdge#targetRef()} vorkommt. */
	public boolean containsTargetRef(int targetRef) {
		if (targetRef == 0) return false;
		this.restore();
		return REFMAP.getIdx(this.targetMap, targetRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebene Referenzen {@code relationRef} als {@link KBEdge#relationRef()} von {@link KBEdge
	 * Hyperkanten} mit der gegebene {@code relationRef} als {@link KBEdge#relationRef()} vorkommt. */
	public boolean containsTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		this.restore();
		var targetIdx = REFMAP.getIdx(this.targetMap, targetRef);
		if (targetIdx == 0) return false;
		var relationMap = KBState.asRefMap(REFMAP.getVal(this.targetMap, targetIdx));
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	/** Diese Methode liefert ein Wissensabschrift aller {@link KBEdge Kanten} und {@link FEMString Textwerte} als {@code int}-Array mit der Struktur
	 * {@code (indexRef, currentInternalRef, currentExternalRef, valueOffset,
	 * sourceCount, (sourceRef, targetRefCount, (targetRef, relationRef)[targetRefCount],
	 * targetSetCount, (relationRef,
	 * targetCount, targetRef[targetCount])[targetSetCount])[sourceCount],
	 * valueCount, (valueRef, valueSize, valueItem[valueSize])[valueCount])}, wobei {@code valueOffset} die Position von {@code valueCount} nennt.
	 *
	 * @return Wissensabschrift. */
	public int[] toInts() {
		if (this.storage != null) return this.storage.clone();

		var edgesSize = 0;
		var sourceMap = this.sourceMap;
		var sourceCount = 0;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = KBState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var relationSize = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = KBState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (KBState.isRef(targetVal)) {
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
					edgesSize += /*sourceRef, targetRefCount, targetSetCount*/ 3 + relationSize;
					sourceCount++;
				}
			}
		}

		var valuesSize = 0;
		var valueMap = this.valueStrMap;
		var valuesCount = valueMap.size();
		for (var entry: valueMap.fastEntries()) {
			valuesSize += entry.getValue().length() + 2;
		}

		var result = new int[4 + 1 + edgesSize + 1 + valuesSize];
		var cursor = 5;

		result[0] = this.indexRef;
		result[1] = this.currentInternalRef;
		result[2] = this.currentExternalRef;
		result[3] = edgesSize + 5;

		result[4] = sourceCount;
		for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
			var relationMap = KBState.asRefMap(sourceMap[sourceIdx]);
			if (relationMap != null) {
				var targetRefCount = 0;
				var targetSetCount = 0;
				for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
					var targetVal = KBState.asRefVal(relationMap[relationIdx]);
					if (targetVal != null) {
						if (KBState.isRef(targetVal)) {
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
					result[cursor++] = REFSET.getRef(REFMAP.getKeys(sourceMap), sourceIdx);
					result[cursor++] = targetRefCount;
					if (targetRefCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = KBState.asRefVal(relationMap[relationIdx]);
							if ((targetVal != null) && (KBState.isRef(targetVal) || (REFSET.size(targetVal) == 1))) {
								result[cursor++] = KBState.asRef(targetVal);
								result[cursor++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
							}
						}
					}
					result[cursor++] = targetSetCount;
					if (targetSetCount != 0) {
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = KBState.asRefVal(relationMap[relationIdx]);
							if (targetVal != null) {
								if (!KBState.isRef(targetVal)) {
									var targetCount = REFSET.size(targetVal);
									if (targetCount > 1) {
										result[cursor++] = REFSET.getRef(REFMAP.getKeys(relationMap), relationIdx);
										result[cursor++] = targetCount;
										for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
											var targetRef = targetVal[targetIdx];
											if (targetRef != 0) {
												result[cursor++] = targetRef;
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

		result[cursor++] = valuesCount;
		for (var value: valueMap.fastEntries()) {
			var valueRef = value.getKey();
			var valueStr = value.getValue();
			var valueSize = valueStr.length();
			result[cursor++] = valueRef;
			result[cursor++] = valueSize;
			valueStr.toInts(result, cursor);
			cursor += valueSize;
		}

		return result;
	}

	/** Diese Methode liefert die {@link #toInts() Wissensabschrift} als {@code byte}-Array mit nativer Bytereihenfolge. */
	public byte[] toBytes() {
		return this.toBytes(ByteOrder.nativeOrder());
	}

	/** Diese Methode liefert die {@link #toInts() Wissensabschrift} als {@code byte}-Array mit der gegebenen Bytereihenfolge {@code order}. */
	public byte[] toBytes(ByteOrder order) {
		var ints = this.toInts();
		var bytes = ByteBuffer.allocate(ints.length * 4);
		bytes.order(order).asIntBuffer().put(ints);
		return bytes.array();
	}

	@Override
	public String toString() {
		return Objects.toStringCall(false, true, this, "edges", this.edges, "values", this.values);
	}

	static boolean isRef(int[] ref_or_refset) {
		return ref_or_refset.length == 1;
	}

	static int[] toRef(int ref) {
		return new int[]{ref};
	}

	static int asRef(int[] ref) {
		return ref[0];
	}

	static int[] asRefVal(Object ref_or_refset) {
		return (int[])ref_or_refset;
	}

	static Object[] asRefMap(Object refmap) {
		return (Object[])refmap;
	}

	static <T> T computeSelect(int[] acceptRefset, int[] refuseRefset, int[] selectRefs, Getter<int[], T> useAcceptRefs) {
		if (refuseRefset != null) return useAcceptRefs.get(REFSET.except(REFSET.from(selectRefs), refuseRefset));
		if (acceptRefset != null) return useAcceptRefs.get(REFSET.intersect(REFSET.from(selectRefs), acceptRefset));
		return useAcceptRefs.get(REFSET.from(selectRefs));
	}

	static <T> T computeExcept(int[] acceptRefset, int[] refuseRefset, int[] exceptRefs, Getter<int[], T> useAcceptRefs, Getter<int[], T> useRefuseRefs) {
		if (exceptRefs.length == 0) return acceptRefset != null ? useAcceptRefs.get(acceptRefset) : useRefuseRefs.get(refuseRefset);
		if (acceptRefset != null) {
			var acceptRefset2 = REFSET.popAllRefs(REFSET.copy(acceptRefset), exceptRefs);
			return useAcceptRefs.get(REFSET.size(acceptRefset2) == REFSET.size(acceptRefset) ? acceptRefset : acceptRefset2);
		}
		if (refuseRefset != null) {
			var refuseRefset2 = REFSET.putAllRefs(REFSET.copy(refuseRefset), exceptRefs);
			return useRefuseRefs.get(REFSET.size(refuseRefset2) == REFSET.size(refuseRefset) ? refuseRefset : refuseRefset2);
		}
		return useRefuseRefs.get(REFSET.from(exceptRefs));
	}

	int indexRef;

	int currentExternalRef;

	int currentInternalRef;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@link KBEdge#sourceRef} auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@link KBEdge#relationRef} auf {@link KBEdge#targetRef}. Letztere sind dabei als {@code int[1]} oder gemäß {@link REFSET} abgebildet. */
	Object[] sourceMap;

	/** Dieses Feld speichert die Referenzabbildung gemäß {@link REFMAP} von {@link KBEdge#targetRef} auf Referenzabbildungen gemäß {@link REFMAP} von
	 * {@link KBEdge#relationRef} auf {@link KBEdge#sourceRef}. Letztere sind dabei als {@code int[1]} oder gemäß {@link REFSET} abgebildet. */
	Object[] targetMap;

	ValueStrMap valueStrMap;

	ValueRefMap valueRefMap;

	final KBEdges edges = new KBEdges(this);

	final KBValues values = new KBValues(this);

	int[] storage;

	/** Dieser Konstruktor erzeugt einen leeren Wissensstand. */
	KBState() {
		this.reset();
	}

	/** Dieser Konstruktor übernimmt die Merkmale der gegebenen {@link #toInts() Wissensabschrift}. */
	KBState(int[] storage) {
		this();
		this.indexRef = storage[0];
		this.currentInternalRef = storage[1];
		this.currentExternalRef = storage[2];
		this.storage = storage;
	}

	/** Dieser Konstruktor übernimmt die Merkmale des gegebenen {@link KBState Wissensstands}. */
	KBState(KBState that) {
		this.reset(that);
	}

	/** Diese Methode leert {@link #sourceMap}, {@link #targetMap}, {@link #valueRefMap} und {@link #valueStrMap}. */
	final void reset() {
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
		this.valueRefMap = new ValueRefMap();
		this.valueStrMap = new ValueStrMap();
	}

	final void reset(KBState that) {
		this.indexRef = that.indexRef;
		this.currentInternalRef = that.currentInternalRef;
		this.currentExternalRef = that.currentExternalRef;
		this.sourceMap = that.sourceMap;
		this.targetMap = that.targetMap;
		this.valueRefMap = that.valueRefMap;
		this.valueStrMap = that.valueStrMap;
		this.storage = that.storage;
	}

	/** Diese Methode ersetzt {@link #sourceMap} und {@link #targetMap} nur dann mit den in {@link #storage} hinterlegten {@link KBEdge Wissensstand}, wenn
	 * {@link #storage} nicht {@code null} ist. Anschließend wird {@link #storage} auf {@code null} gesetzt. */
	final void restore() throws IllegalStateException {
		if (this.storage == null) return;
		this.reset();
		try {
			KBState.selectEdges(this.storage, this::insertEdge);
			KBState.selectValues(this.storage, this::insertValue);
			this.storage = null;
		} finally {
			if (this.storage != null) {
				this.reset();
			}
		}
	}

	final void forEachEdge(KBEdges.RUN task) {
		this.forEachEdge(null, null, null, null, null, null, task);
	}

	final void forEachEdge(int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null, int[] acceptTargetRefset_or_null, int[] refuseTargetRefset_or_null,
		int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null, KBEdges.RUN task) {
		if (this.storage != null) {
			KBState.selectEdges(this.storage, acceptSourceRefset_or_null, refuseSourceRefset_or_null, acceptTargetRefset_or_null, refuseTargetRefset_or_null,
				acceptRelationRefset_or_null, refuseRelationRefset_or_null, task);
		} else {
			KBState.selectEdges(this.sourceMap, this.targetMap, acceptSourceRefset_or_null, refuseSourceRefset_or_null, acceptTargetRefset_or_null,
				refuseTargetRefset_or_null, acceptRelationRefset_or_null, refuseRelationRefset_or_null, task);
		}
	}

	final void forEachValue(KBValues.RUN task) {
		this.forEachValue(null, null, task);
	}

	final void forEachValue(int[] acceptValueRefset_or_null, int[] refuseValueRefset_or_null, KBValues.RUN task) {
		if (this.storage != null) {
			KBState.selectValues(this.storage, acceptValueRefset_or_null, refuseValueRefset_or_null, task);
		} else {
			KBState.selectValues(this.valueStrMap, acceptValueRefset_or_null, refuseValueRefset_or_null, task);
		}
	}

	final Iterator2<KBEdge> edgeIterator(int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null, int[] acceptTargetRefset_or_null,
		int[] refuseTargetRefset_or_null, int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null) {
		this.restore();
		var sourceIter = REFMAP.iterator(this.sourceMap, acceptSourceRefset_or_null, refuseSourceRefset_or_null);
		return Iterators.concatAll(Iterators.concatAll(new Iterator2<Iterator2<Iterator2<KBEdge>>>() {

			@Override
			public Iterator2<Iterator2<KBEdge>> next() {
				var relationIter = REFMAP.iterator(KBState.asRefMap(sourceIter.nextVal()), acceptRelationRefset_or_null, refuseRelationRefset_or_null);
				var sourceRef = sourceIter.nextRef();
				return new Iterator2<>() {

					@Override
					public Iterator2<KBEdge> next() {
						var targetVal = KBState.asRefVal(relationIter.nextVal());
						var relationRef = relationIter.nextRef();
						if (KBState.isRef(targetVal)) {
							var targetRef = KBState.asRef(targetVal);
							if (!REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) return Iterators.empty();
							return Iterators.fromItem(new KBEdge(sourceRef, targetRef, relationRef));
						}
						var targetIter = REFSET.iterator(targetVal, acceptTargetRefset_or_null, refuseTargetRefset_or_null);
						return new Iterator2<>() {

							@Override
							public KBEdge next() {
								return new KBEdge(sourceRef, targetIter.nextRef(), relationRef);
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

	final Iterator2<Entry<Integer, FEMString>> valueIterator(int[] acceptValueRefset_or_null, int[] refuseValueRefset_or_null) {
		this.restore();
		return ((acceptValueRefset_or_null == null) && (refuseValueRefset_or_null == null) ? this.valueStrMap.fastIterator()
			: this.valueStrMap.fastIterator().filter(entry -> REFSET.isValid(entry.getKey(), acceptValueRefset_or_null, refuseValueRefset_or_null)));
	}

	static final class ValueStrMap extends HashMapIO<FEMString> {

		public void pack() {
			var capacity = this.capacity() >> 1;
			if (this.size() > capacity) return;
			this.allocate(capacity);
		}

		public int[] fastKeys() {
			var result = new int[this.size()];
			var cursor = 0;
			for (var index = this.capacity() - 1; 0 <= index; index--) {
				if (this.customGetValue(index) != null) {
					result[cursor++] = this.customGetKeyInt(index);
				}
			}
			return result;
		}

		public Iterable2<Entry<Integer, FEMString>> fastEntries() {
			return () -> this.fastIterator();
		}

		public Iterator2<Entry<Integer, FEMString>> fastIterator() {
			return new ITER();
		}

		public void fastForEach(KBValues.RUN task) {
			for (var index = this.capacityImpl() - 1; 0 <= index; index--) {
				var valueStr = this.customGetValue(index);
				if (valueStr != null) {
					task.run(this.customGetKeyInt(index), valueStr);
				}
			}
		}

		@SuppressWarnings ("synthetic-access")
		final class ITER implements Iterator2<Entry<Integer, FEMString>> {

			@Override
			public Entry<Integer, FEMString> next() {
				if (!this.hasNext()) throw new NoSuchElementException();
				var result = this.next;
				while (true) {
					this.index--;
					if (this.index < 0) return result;
					var valueStr = ValueStrMap.this.customGetValue(this.index);
					if (valueStr != null) {
						var valueRef = ValueStrMap.this.customGetKey(this.index);
						this.next = new SimpleImmutableEntry<>(valueRef, valueStr);
						return result;
					}
				}
			}

			@Override
			public boolean hasNext() {
				return 0 <= this.index;
			}

			ITER() {
				this.index = ValueStrMap.this.capacityImpl();
				this.next();
			}

			private Entry<Integer, FEMString> next;

			private int index;

		}

		private static final long serialVersionUID = -1367743076464786820L;

	}

	static final class ValueRefMap extends HashMapOI<FEMString> {

		public void pack() {
			var capacity = this.capacity() >> 1;
			if (this.size() > capacity) return;
			this.allocate(capacity);
		}

		private static final long serialVersionUID = -4828467604110047839L;

	}

	private static final int[] EMPTY_REFS = new int[0];

	private static Object[] insertEdge(Object[] sourceMap, int sourceRef, int targetRef, int relationRef) {

		sourceMap = REFMAP.grow(sourceMap);

		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);
		if (sourceIdx == 0) throw new OutOfMemoryError();

		var sourceRelationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		sourceRelationMap = sourceRelationMap == null ? REFMAP.create() : REFMAP.grow(sourceRelationMap);
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) throw new OutOfMemoryError();

		var sourceRelationTargetVal = KBState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (sourceRelationTargetVal == null) {
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, KBState.toRef(targetRef));
		} else if (KBState.isRef(sourceRelationTargetVal)) {
			var targetRef2 = KBState.asRef(sourceRelationTargetVal);
			if (targetRef == targetRef2) return sourceMap;
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.from(targetRef, targetRef2));
		} else {
			var sourceRelationTargetSet = REFSET.grow(sourceRelationTargetVal);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
			var targetIdx = REFSET.putRef(sourceRelationTargetSet, targetRef);
			if (targetIdx == 0) throw new OutOfMemoryError();
		}

		return sourceMap;
	}

	private static void selectEdges(int[] storage, KBEdges.RUN task) {
		var cursor = 4;
		var sourceCount = storage[cursor++];
		while (0 < sourceCount--) {
			var sourceRef = storage[cursor++];
			var targetRefCount = storage[cursor++];
			while (0 < targetRefCount--) {
				var targetRef = storage[cursor++];
				var relationRef = storage[cursor++];
				task.run(sourceRef, targetRef, relationRef);
			}
			var targetSetCount = storage[cursor++];
			while (0 < targetSetCount--) {
				var relationRef = storage[cursor++];
				var targetCount = storage[cursor++];
				while (0 < targetCount--) {
					var targetRef = storage[cursor++];
					task.run(sourceRef, targetRef, relationRef);
				}
			}
		}
	}

	private static void selectEdges(int[] storage, int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null, int[] acceptTargetRefset_or_null,
		int[] refuseTargetRefset_or_null, int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null, KBEdges.RUN task) {
		if ((acceptSourceRefset_or_null == null) && (refuseSourceRefset_or_null == null)) {
			if ((acceptTargetRefset_or_null == null) && (refuseTargetRefset_or_null == null)) {
				if ((acceptRelationRefset_or_null == null) && (refuseRelationRefset_or_null == null)) {
					KBState.selectEdges(storage, task);
				} else {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
							task.run(sourceRef, targetRef, relationRef);
						}
					});
				}
			} else {
				if ((acceptRelationRefset_or_null == null) && (refuseRelationRefset_or_null == null)) {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
							task.run(sourceRef, targetRef, relationRef);
						}
					});
				} else {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
							if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
								task.run(sourceRef, targetRef, relationRef);
							}
						}
					});
				}
			}
		} else {
			if ((acceptTargetRefset_or_null == null) && (refuseTargetRefset_or_null == null)) {
				if ((acceptRelationRefset_or_null == null) && (refuseRelationRefset_or_null == null)) {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
							task.run(sourceRef, targetRef, relationRef);
						}
					});
				} else {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
							if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
								task.run(sourceRef, targetRef, relationRef);
							}
						}
					});
				}
			} else {
				if ((acceptRelationRefset_or_null == null) && (refuseRelationRefset_or_null == null)) {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
							if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
								task.run(sourceRef, targetRef, relationRef);
							}
						}
					});
				} else {
					KBState.selectEdges(storage, (sourceRef, targetRef, relationRef) -> {
						if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
							if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
								if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
									task.run(sourceRef, targetRef, relationRef);
								}
							}
						}
					});
				}
			}
		}
	}

	private static void selectEdges(Object[] sourceMap, Object[] targetMap, int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null,
		int[] acceptTargetRefset_or_null, int[] refuseTargetRefset_or_null, int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null,
		KBEdges.RUN task) {
		var allSources = (acceptSourceRefset_or_null == null) && (refuseSourceRefset_or_null == null);
		var allTargets = (acceptTargetRefset_or_null == null) && (refuseTargetRefset_or_null == null);
		var moreSources = REFMAP.size(sourceMap) >= REFMAP.size(targetMap);
		if (allSources ? (allTargets & moreSources) : (allTargets | moreSources)) {
			var sourceKeys = REFMAP.getKeys(sourceMap);
			for (var sourceIdx = sourceMap.length - 1; 0 < sourceIdx; sourceIdx--) {
				var relationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
				if (relationMap != null) {
					var sourceRef = REFSET.getRef(sourceKeys, sourceIdx);
					if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
						var relationKeys = REFMAP.getKeys(relationMap);
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
							if (targetVal != null) {
								var relationRef = REFSET.getRef(relationKeys, relationIdx);
								if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
									if (KBState.isRef(targetVal)) {
										var targetRef = KBState.asRef(targetVal);
										if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
											task.run(sourceRef, targetRef, relationRef);
										}
									} else {
										for (var targetIdx = targetVal.length - 1; 3 < targetIdx; targetIdx -= 3) {
											var targetRef = targetVal[targetIdx];
											if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
												task.run(sourceRef, targetRef, relationRef);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			var targetKeys = REFMAP.getKeys(targetMap);
			for (var targetIdx = targetMap.length - 1; 0 < targetIdx; targetIdx--) {
				var relationMap = KBState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
				if (relationMap != null) {
					var targetRef = REFSET.getRef(targetKeys, targetIdx);
					if (REFSET.isValid(targetRef, acceptTargetRefset_or_null, refuseTargetRefset_or_null)) {
						var relationKeys = REFMAP.getKeys(relationMap);
						for (var relationIdx = relationMap.length - 1; 0 < relationIdx; relationIdx--) {
							var sourceVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
							if (sourceVal != null) {
								var relationRef = REFSET.getRef(relationKeys, relationIdx);
								if (REFSET.isValid(relationRef, acceptRelationRefset_or_null, refuseRelationRefset_or_null)) {
									if (KBState.isRef(sourceVal)) {
										var sourceRef = KBState.asRef(sourceVal);
										if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
											task.run(sourceRef, targetRef, relationRef);
										}
									} else {
										for (var sourceIdx = sourceVal.length - 1; 3 < sourceIdx; sourceIdx -= 3) {
											var sourceRef = sourceVal[sourceIdx];
											if (REFSET.isValid(sourceRef, acceptSourceRefset_or_null, refuseSourceRefset_or_null)) {
												task.run(sourceRef, targetRef, relationRef);
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

	private static void selectValues(int[] storage, KBValues.RUN task) {
		KBState.selectValues(storage, null, null, task);
	}

	private static void selectValues(int[] storage, int[] acceptValueRefset_or_null, int[] refuseValueRefset_or_null, KBValues.RUN task) {
		var cursor = storage[4];
		var valueCount = storage[cursor++];
		while (0 < valueCount--) {
			var valueRef = storage[cursor++];
			var valueSize = storage[cursor++];
			if (REFSET.isValid(valueRef, acceptValueRefset_or_null, refuseValueRefset_or_null)) {
				var valueStr = FEMString.from(storage, cursor, valueSize);
				task.run(valueRef, valueStr);
			}
			cursor += valueSize;
		}
	}

	private static void selectValues(ValueStrMap valueStrMap, int[] acceptValueRefset_or_null, int[] refuseValueRefset_or_null, KBValues.RUN task) {
		if ((acceptValueRefset_or_null == null) && (refuseValueRefset_or_null == null)) {
			valueStrMap.fastForEach(task);
		} else {
			valueStrMap.fastForEach((KBValues.RUN)(valueRef, valueStr) -> {
				if (REFSET.isValid(valueRef, acceptValueRefset_or_null, refuseValueRefset_or_null)) {
					task.run(valueRef, valueStr);
				}
			});
		}
	}

	private void insertEdge(int sourceRef, int targetRef, int relationRef) {
		this.sourceMap = KBState.insertEdge(this.sourceMap, sourceRef, targetRef, relationRef);
		this.targetMap = KBState.insertEdge(this.targetMap, targetRef, sourceRef, relationRef);
	}

	private void insertValue(int valueRef, FEMString valueStr) {
		var newValueRef = valueRef;
		var newValueStr = valueStr.data();
		var oldValueRef = this.valueRefMap.put(newValueStr, newValueRef);
		if (oldValueRef != null) {
			if (oldValueRef.intValue() == valueRef) return;
			this.valueRefMap.put(valueStr, oldValueRef);
			throw new IllegalArgumentException();
		}
		var oldValueStr = this.valueStrMap.put(newValueRef, newValueStr);
		if (oldValueStr != null) {
			this.valueStrMap.put(newValueRef, oldValueStr);
			this.valueRefMap.remove(newValueStr);
			throw new IllegalArgumentException();
		}
	}

}