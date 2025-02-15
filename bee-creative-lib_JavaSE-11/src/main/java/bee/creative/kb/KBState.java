package bee.creative.kb;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMString;
import bee.creative.lang.Objects;
import bee.creative.util.Getter;
import bee.creative.util.HashMapIO;
import bee.creative.util.HashMapOI;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert einen Wissensstand (<em>knowledge-base state</em>) als Menge {@link KBEdge typisierter gerichteter Kanen} zwischen Knoten mit
 * optinalem eineindeutigem {@link #getValue(int) Textwert}, auf welche sowohl von der {@link #getSourceRefs() Quellreferenzen} als auch von den
 * {@link #getTargetRefs() Zielreferenzen} aus effizient zugegriffen werden kann. Jedem Textwert ist eineindeutig eine {@link #getValueRef(FEMString)
 * Textreferenz} zugeordnet.
 * <p>
 * Eine Wissensstand kann in eine {@link KBCodec#persistState(KBState) Wissensabschrift} überführt werden.
 * <p>
 * Die über {@link #getIndexRef()}, {@link #getExternalRef()} und {@link #getInternalRef()} und bereitgestellten Referenzen haben Bedeutung für
 * {@link KBBuffer}.
 * <p>
 * ({@link KBEdge#sourceRef()}, {@link KBEdge#relationRef()} und {@link KBEdge#targetRef()})
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBState implements Emuable {

	/** Dieses Feld speichert den leeren {@link KBState Wissensstand}. */
	public static final KBState EMPTY = new KBState();

	/** Diese Methode liefert einen neuen {@link KBState Wissensstand} mit den gegebenen {@link KBEdge Kanten}. */
	public static KBState from(KBEdge... edges) {
		return KBState.from(Arrays.asList(edges));
	}

	/** Diese Methode liefert einen neuen {@link KBState Wissensstand} mit den gegebenen {@link KBEdge Kanten}. */
	public static KBState from(Iterable<KBEdge> edges) {
		var result = new KBState();
		edges.forEach(edge -> result.insertEdgeNow(edge.sourceRef, edge.targetRef, edge.relationRef));
		return result;
	}

	/** Diese Methode liefert eine Kopie des gegebenen {@link KBState Wissensstands}. */
	public static KBState from(KBState state) {
		var result = new KBState();
		result.indexRef = state.indexRef;
		result.internalRef = state.internalRef;
		result.externalRef = state.externalRef;
		result.valueRefMap = (ValueRefMap)state.valueRefMap.clone();
		result.valueStrMap = (ValueStrMap)state.valueStrMap.clone();
		state.forEachEdge(result::insertEdgeNow); // TODO effizientere deep copy
		return result;
	}

	/** Diese Methode liefert einen neuen {@link KBState Wissensstand} mit den {@link #edges() Kanten} und {@link #values() Textwerte} des Wissensstands
	 * {@code newState} ohne denen des Wissensstands {@code oldState}.
	 *
	 * @param oldState alter Wissensstands.
	 * @param newState neuer Wissensstands.
	 * @return Wissensstand mit neuem und ohne altem Wissen. */
	public static KBState from(KBState oldState, KBState newState) {
		var result = new KBState();
		result.indexRef = newState.indexRef;
		result.internalRef = newState.internalRef;
		result.externalRef = newState.externalRef;
		if (oldState == newState) return result;
		KBState.selectInserts(oldState, newState, result::insertEdgeNow, result::insertValueNow);
		return result;
	}

	/** Diese Methode liefert die filterbare Sicht auf die {@link KBEdge Kanten} dieses Wissensstands. */
	public KBEdges edges() {
		return this.edges;
	}

	/** Diese Methode liefert die filterbare Sicht auf die {@link FEMString Textwerte} dieses Wissensstands. */
	public KBValues values() {
		return this.values;
	}

	/** Diese Methode liefert den {@link FEMString Textwert} mit der gegebenen Textreferenz {@code valueRef} oder {@code null}. */
	public synchronized FEMString getValue(int valueRef) {
		if (valueRef == 0) return null;
		return this.valueStrMap.get(valueRef);
	}

	/** Diese Methode liefert die Textreferenz zum gegebenen {@link FEMString Textwert} {@code valueStr} oder {@code 0}. */
	public synchronized int getValueRef(FEMString valueStr) {
		if (valueStr == null) return 0;
		var valueRef = this.valueRefMap.get(valueStr);
		return valueRef != null ? valueRef : 0;
	}

	/** Diese Methode liefert eine Kopie der Textreferenzen dieses Wissenstands. */
	public synchronized int[] getValueRefs() {
		return this.valueStrMap.fastKeys();
	}

	/** Diese Methode liefert die Anzahl der Textreferenzen dieses Wissenstands. */
	public synchronized int getValueCount() {
		return this.valueStrMap.size();
	}

	public synchronized int[] getSourceRefs() {
		return REFMAP.toArray(this.sourceMap);
	}

	public synchronized int getSourceCount() {
		return REFMAP.size(this.sourceMap);
	}

	public synchronized int[] getSourceRelationRefs(int sourceRef) {
		var relationMap = this.getRefmap(this.sourceMap, sourceRef);
		return REFMAP.toArray(relationMap);
	}

	public synchronized int getSourceRelationCount(int sourceRef) {
		var relationMap = this.getRefmap(this.sourceMap, sourceRef);
		return REFMAP.size(relationMap);
	}

	public synchronized int getSourceRelationTargetRef(int sourceRef, int relationRef) {
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		return KBState.isRef(targetVal) ? KBState.asRef(targetVal) : REFSET.getRef(targetVal);
	}

	public synchronized int[] getSourceRelationTargetRefs(int sourceRef, int relationRef) {
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		return KBState.isRef(targetVal) ? new int[]{KBState.asRef(targetVal)} : REFSET.toArray(targetVal);
	}

	public synchronized int getSourceRelationTargetCount(int sourceRef, int relationRef) {
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		return KBState.isRef(targetVal) ? 1 : REFSET.size(targetVal);
	}

	public synchronized int[] getTargetRefs() {
		return REFMAP.toArray(this.targetMap);
	}

	public synchronized int getTargetCount() {
		return REFMAP.size(this.targetMap);
	}

	public synchronized int[] getTargetRelationRefs(int targetRef) {
		var relationMap = this.getRefmap(this.targetMap, targetRef);
		return REFMAP.toArray(relationMap);
	}

	public synchronized int getTargetRelationCount(int targetRef) {
		var relationMap = this.getRefmap(this.targetMap, targetRef);
		return REFMAP.size(relationMap);
	}

	public synchronized int getTargetRelationSourceRef(int targetRef, int relationRef) {
		var sourceVal = this.getRefset(this.targetMap, targetRef, relationRef);
		return KBState.isRef(sourceVal) ? KBState.asRef(sourceVal) : REFSET.getRef(sourceVal);
	}

	/** Diese Methode liefert die {@link KBEdge#sourceRef() Quellreferenzen} aller {@link KBEdge Kanten} mit der gegebenen {@link KBEdge#targetRef() Zielreferenz}
	 * {@code targetRef} und der gegebenen {@link KBEdge#relationRef() Beziehungsreferenz} {@code relationRef}. */
	public synchronized int[] getTargetRelationSourceRefs(int targetRef, int relationRef) {
		var sourceVal = this.getRefset(this.targetMap, targetRef, relationRef);
		return KBState.isRef(sourceVal) ? new int[]{KBState.asRef(sourceVal)} : REFSET.toArray(sourceVal);
	}

	/** Diese Methode liefert die Anzahl der {@link KBEdge#sourceRef() Quellreferenzen} aller {@link KBEdge Kanten} mit der gegebenen {@link KBEdge#targetRef()
	 * Zielreferenz} {@code targetRef} und der gegebenen {@link KBEdge#relationRef() Beziehungsreferenz} {@code relationRef}. */
	public synchronized int getTargetRelationSourceCount(int targetRef, int relationRef) {
		var sourceVal = this.getRefset(this.targetMap, targetRef, relationRef);
		return KBState.isRef(sourceVal) ? 1 : REFSET.size(sourceVal);
	}

	/** Diese Methode liefert die Referenz auf die Entität des Inhaltsverzeichnisses oder {@code 0}. Wenn dieses Objekt über {@link #from(KBState, KBState)}
	 * erzeugt wurde, liefert sie {@code newState.getIndexRef()}. */
	public synchronized int getIndexRef() {
		return this.indexRef;
	}

	/** Diese Methode liefert die Referenz, von der aus die nächste für eine neue interne Entität ohne Textwert verfügbare Referenz gesucht wird. Wenn dieses
	 * Objekt über {@link #from(KBState, KBState)} erzeugt wurde, liefert sie {@code newState.getInternalRef()}. */
	public synchronized int getInternalRef() {
		return this.internalRef;
	}

	/** Diese Methode liefert die Referenz, von der aus die nächste für eine neue externe Entität mit Textwert verfügbare Referenz gesucht wird. Wenn dieses
	 * Objekt über {@link #from(KBState, KBState)} erzeugt wurde, liefert sie {@code newState.getExternalRef()}. */
	public synchronized int getExternalRef() {
		return this.externalRef;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand die gegebene {@link KBEdge Kante} enthält. */
	public boolean containsEdge(KBEdge edge) {
		return (edge != null) && this.containsEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand die {@link KBEdge Kante} mit der gegebenen {@link KBEdge#sourceRef() Quellreferenz}
	 * {@code sourceRef}, der gegebenen {@link KBEdge#targetRef() Zielreferenz} {@code targetRef} und der gegebenen {@link KBEdge#relationRef()
	 * Beziehungsreferenz} {@code relationRef} enthält. */
	public synchronized boolean containsEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return false;
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		return KBState.isRef(targetVal) ? KBState.asRef(targetVal) == targetRef : REFSET.getIdx(targetVal, targetRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand den gegebenen {@link #getValue(int) Textwert} {@code valueStr} enthält. */
	public synchronized boolean containsValue(FEMString valueStr) {
		if (valueStr == null) return false;
		return this.valueRefMap.containsKey(valueStr);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand die gegebene {@link #getValueRef(FEMString) Textwertreferenz} {@code valueRef}
	 * enthält. */
	public synchronized boolean containsValueRef(int valueRef) {
		if (valueRef == 0) return false;
		return this.valueStrMap.containsKey(valueRef);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand eine {@link KBEdge Kante} mit der gegebenen {@link KBEdge#sourceRef() Quellreferenz}
	 * enthält. */
	public synchronized boolean containsSourceRef(int sourceRef) {
		if (sourceRef == 0) return false;
		return REFMAP.getIdx(this.sourceMap, sourceRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand eine {@link KBEdge Kante} mit der gegebenen {@link KBEdge#sourceRef() Quellreferenz}
	 * {@code sourceRef} und der gegebenen {@link KBEdge#relationRef() Beziehungsreferenz} {@code relationRef} enthält. */
	public synchronized boolean containsSourceRelationRef(int sourceRef, int relationRef) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		var relationMap = this.getRefmap(this.sourceMap, sourceRef);
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand eine {@link KBEdge Kante} mit der gegebenen {@link KBEdge#targetRef() Zielreferenz}
	 * {@code targetRef}. */
	public synchronized boolean containsTargetRef(int targetRef) {
		if (targetRef == 0) return false;
		return REFMAP.getIdx(this.targetMap, targetRef) != 0;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn dieser Wissensstand eine {@link KBEdge Kante} mit der gegebenen {@link KBEdge#targetRef() Zielreferenz}
	 * {@code targetRef} und der gegebenen {@link KBEdge#relationRef() Beziehungsreferenz} {@code relationRef} enthält. */
	public synchronized boolean containsTargetRelationRef(int targetRef, int relationRef) {
		if ((targetRef == 0) || (relationRef == 0)) return false;
		var relationMap = this.getRefmap(this.targetMap, targetRef);
		return REFMAP.getIdx(relationMap, relationRef) != 0;
	}

	@Override
	public synchronized long emu() {
		return EMU.fromObject(this) + KBState.computeEdgeMapEMU(this.sourceMap) + KBState.computeEdgeMapEMU(this.targetMap) + this.valueRefMap.emu()
			+ this.valueStrMap.emu() + this.edges.emu() + this.values.emu();
	}

	@Override
	public synchronized String toString() {
		return Objects.toStringCall(true, true, this, "edges", this.edges, "values", this.values);
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

	static long computeEdgeMapEMU(Object[] sourceMap) {
		var result = new long[]{REFMAP.emu(sourceMap)};
		REFMAP.forEach(sourceMap, (sourceRef, sourceVal) -> {
			if (sourceVal == null) return;
			var relationMap = KBState.asRefMap(sourceVal);
			result[0] += REFMAP.emu(relationMap);
			REFMAP.forEach(relationMap, (relationRef, relationVal) -> {
				result[0] += REFSET.emu(KBState.asRefVal(relationVal));
			});
		});
		return result[0];
	}

	static <T> T computeSelect(int[] selectRefs, int[] acceptRefset, int[] refuseRefset, Getter<int[], T> useAcceptRefs) {
		if (refuseRefset != null) return useAcceptRefs.get(REFSET.trim(REFSET.except(REFSET.from(selectRefs), refuseRefset)));
		if (acceptRefset != null) return useAcceptRefs.get(REFSET.trim(REFSET.intersect(REFSET.from(selectRefs), acceptRefset)));
		return useAcceptRefs.get(REFSET.from(selectRefs));
	}

	static <T> T computeExcept(int[] exceptRefs, int[] acceptRefset, int[] refuseRefset, Getter<int[], T> useAcceptRefs, Getter<int[], T> useRefuseRefs) {
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

	/** Diese Methode leitet die {@link #edges() Kanten} und {@link #values() Textwerte} des Wissensstands {@code newState} ohne denen des Wissensstands
	 * {@code oldState} an {@code edgesTask} bzw. {@code valuesTask} weiter.
	 *
	 * @param oldState alter Wissensstands.
	 * @param newState neuer Wissensstands. */
	static void selectInserts(KBState oldState, KBState newState, KBEdgesTask edgesTask, KBValuesTask valuesTask) {
		if (oldState == newState) return;
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
								edgesTask.run(sourceRef, KBState.asRef(newTargetVal), relationRef);
							} else {
								for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
									var newTargetRef = newTargetVal[newTargetIdx];
									if (newTargetRef != 0) {
										edgesTask.run(sourceRef, newTargetRef, relationRef);
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
										edgesTask.run(sourceRef, KBState.asRef(newTargetVal), relationRef);
									} else {
										for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
											var newTargetRef = newTargetVal[newTargetIdx];
											if (newTargetRef != 0) {
												edgesTask.run(sourceRef, newTargetRef, relationRef);
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
													edgesTask.run(sourceRef, newTargetRef, relationRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (oldTargetRef != newTargetRef) {
															edgesTask.run(sourceRef, newTargetRef, relationRef);
														}
													}
												}
											}
										} else {
											if (KBState.isRef(newTargetVal)) {
												var newTargetRef = KBState.asRef(newTargetVal);
												if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
													edgesTask.run(sourceRef, newTargetRef, relationRef);
												}
											} else {
												for (var newTargetIdx = newTargetVal.length - 1; 3 < newTargetIdx; newTargetIdx -= 3) {
													var newTargetRef = newTargetVal[newTargetIdx];
													if (newTargetRef != 0) {
														if (REFSET.getIdx(oldTargetVal, newTargetRef) == 0) {
															edgesTask.run(sourceRef, newTargetRef, relationRef);
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
			newValueStrMap.fastForEach((KBValuesTask)(valueRef, valueStr) -> {
				if (!valueStr.equals(oldValueStrMap.get(valueRef))) {
					valuesTask.run(valueRef, valueStr);
				}
			});
		}
	}

	int indexRef;

	int externalRef;

	int internalRef;

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

	/** Dieser Konstruktor erzeugt einen leeren Wissensstand. */
	KBState() {
		this.reset();
	}

	/** Dieser Konstruktor {@link #reset(KBState) übernimmt} die Merkmale des gegebenen {@link KBState Wissensstands}. */
	KBState(KBState that) {
		this.reset(that);
	}

	final int[] getRefset(Object[] sourceMap, int sourceRef, int relationRef) {
		if (relationRef == 0) return REFSET.EMPTY;
		var relationMap = this.getRefmap(sourceMap, sourceRef);
		var relationIdx = REFMAP.getIdx(relationMap, relationRef);
		if (relationIdx == 0) return REFSET.EMPTY;
		var targetVal = KBState.asRefVal(REFMAP.getVal(relationMap, relationIdx));
		if (targetVal == null) return REFSET.EMPTY;
		return targetVal;
	}

	final Object[] getRefmap(Object[] sourceMap, int sourceRef) {
		if (sourceRef == 0) return REFMAP.EMPTY;
		var sourceIdx = REFMAP.getIdx(sourceMap, sourceRef);
		if (sourceIdx == 0) return REFMAP.EMPTY;
		var relationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		if (relationMap == null) return REFMAP.EMPTY;
		return relationMap;
	}

	/** Diese Methode leert {@link #sourceMap}, {@link #targetMap}, {@link #valueRefMap} und {@link #valueStrMap}. */
	final void reset() {
		this.sourceMap = REFMAP.EMPTY;
		this.targetMap = REFMAP.EMPTY;
		this.valueRefMap = new ValueRefMap();
		this.valueStrMap = new ValueStrMap();
	}

	/** Diese Methode übernimmt die Merkmale des gegebenen {@link KBState Wissensstands}. */
	final void reset(KBState that) {
		this.indexRef = that.indexRef;
		this.internalRef = that.internalRef;
		this.externalRef = that.externalRef;
		this.sourceMap = that.sourceMap;
		this.targetMap = that.targetMap;
		this.valueRefMap = that.valueRefMap;
		this.valueStrMap = that.valueStrMap;
	}

	final void forEachEdge(KBEdgesTask task) {
		this.forEachEdge(null, null, null, null, null, null, task);
	}

	// TODO forEachSource
	// TODO forEachSourceRelation
	// TODO forEachTarget
	// TODO forEachTargetRelation
	// TODO forEachRelation auto forEachSourceRelation vs. forEachTargetRelation

	final void forEachEdge(int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null, int[] acceptTargetRefset_or_null, int[] refuseTargetRefset_or_null,
		int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null, KBEdgesTask task) {
		var sourceMap = this.sourceMap;
		var targetMap = this.targetMap;
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

	final void forEachValue(KBValuesTask task) {
		this.forEachValue(null, null, task);
	}

	final void forEachValue(int[] acceptValueRefset_or_null, int[] refuseValueRefset_or_null, KBValuesTask task) {
		if ((acceptValueRefset_or_null == null) && (refuseValueRefset_or_null == null)) {
			this.valueStrMap.fastForEach(task);
		} else {
			this.valueStrMap.fastForEach((KBValuesTask)(valueRef, valueStr) -> {
				if (REFSET.isValid(valueRef, acceptValueRefset_or_null, refuseValueRefset_or_null)) {
					task.run(valueRef, valueStr);
				}
			});
		}
	}

	// TODO sourceIterator
	// TODO sourceRelationIterator
	// TODO targetIterator
	// TODO targetRelationIterator
	// TODO relationIterator
	final Iterator2<KBEdge> edgeIterator(int[] acceptSourceRefset_or_null, int[] refuseSourceRefset_or_null, int[] acceptTargetRefset_or_null,
		int[] refuseTargetRefset_or_null, int[] acceptRelationRefset_or_null, int[] refuseRelationRefset_or_null) {
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
		return ((acceptValueRefset_or_null == null) && (refuseValueRefset_or_null == null) ? this.valueStrMap.fastIterator()
			: this.valueStrMap.fastIterator().filter(entry -> REFSET.isValid(entry.getKey(), acceptValueRefset_or_null, refuseValueRefset_or_null)));
	}

	static final class ValueStrMap extends HashMapIO<FEMString> {

		@Override
		public long emu() {
			var result = super.emu();
			for (var index = this.capacityImpl() - 1; 0 <= index; index--) {
				result += EMU.from(this.customGetValue(index));
			}
			return result;
		}

		public void pack() {
			var capacity = this.capacityImpl() >> 1;
			if (this.size() > capacity) return;
			this.allocate(capacity);
		}

		public int[] fastKeys() {
			var result = new int[this.size()];
			var cursor = 0;
			for (var index = this.capacityImpl() - 1; 0 <= index; index--) {
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

		public void fastForEach(KBValuesTask task) {
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
			var capacity = this.capacityImpl() >> 1;
			if (this.size() > capacity) return;
			this.allocate(capacity);
		}

		private static final long serialVersionUID = -4828467604110047839L;

	}

	final void insertEdgeNow(int sourceRef, int targetRef, int relationRef) {
		this.insertEdgeIntoSourceMap(sourceRef, targetRef, relationRef);
		this.insertEdgeIntoTargetMap(sourceRef, targetRef, relationRef);
	}

	final void insertEdgeIntoTargetMap(int sourceRef, int targetRef, int relationRef) {
		this.targetMap = KBState.insertEdgeIntoMapSRT(this.targetMap, targetRef, relationRef, sourceRef);
	}

	final void insertEdgeIntoSourceMap(int sourceRef, int targetRef, int relationRef) {
		this.sourceMap = KBState.insertEdgeIntoMapSRT(this.sourceMap, sourceRef, relationRef, targetRef);
	}

	static Object[] insertEdgeIntoMapSRT(Object[] sourceMap, int sourceRef, int relationRef, int targetRef) {

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

	/** Diese Methode implementiert das Einfügen des gegebene Textwerts für {@link #from(KBState, KBState)}. */
	final void insertValueNow(int valueRef, FEMString valueStr) {
		var newValueRef = valueRef;
		var newValueStr = valueStr.data();
		this.insertValueNowIntoRefMap(newValueRef, newValueStr);
		this.insertValueNowIntoStrMap(newValueRef, newValueStr);
	}

	final void insertValueNowIntoRefMap(Integer newValueRef, FEMString newValueStr) {
		var oldValueRef = this.valueRefMap.put(newValueStr, newValueRef);
		if (oldValueRef != null) {
			if (oldValueRef.intValue() == newValueRef.intValue()) return;
			this.valueRefMap.put(newValueStr, oldValueRef);
			throw new IllegalArgumentException();
		}
	}

	final void insertValueNowIntoStrMap(Integer newValueRef, FEMString newValueStr) {
		var oldValueStr = this.valueStrMap.put(newValueRef, newValueStr);
		if (oldValueStr != null) {
			this.valueStrMap.put(newValueRef, oldValueStr);
			this.valueRefMap.remove(newValueStr);
			throw new IllegalArgumentException();
		}
	}

}