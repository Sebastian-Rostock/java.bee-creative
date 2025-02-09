package bee.creative.kb;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import bee.creative.fem.FEMString;
import bee.creative.util.AbstractList2;
import bee.creative.util.HashMapOI;
import bee.creative.util.List2;

/** Diese Klasse implementiert einen Wissenspuffer als veränderlichen {@link KBState Wissensstand}. Der Wissenspuffer ist nicht <em>thread-safe</em> und macht
 * vor der ersten Änderung grundsätzlich eine Sicherungskopie des aktuellen Wissensstands. Durch den Aufruf von {@link #commit()} bzw. {@link #rollback()}
 * können dann alle bis dahin gemachten Änderungen angenommen bzw. verworfen werden.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBBuffer extends KBState {

	/** Dieser Konstruktor erzeugt einen leeren Wissenspuffer. */
	public KBBuffer() {
	}

	/** Diese Methode setzt die Referenz auf die Entität des Inhaltsverzeichnisses. */
	public synchronized void setIndexRef(int indexRef) {
		this.backup();
		this.indexRef = indexRef;
	}

	/** Diese Methode setzt die Referenz, von der aus die nächste für eine neue interne Entität ohne Textwert verfügbare Referenz gesucht wird. */
	public synchronized void setInternalRef(int internalRef) {
		this.backup();
		this.internalRef = internalRef;
	}

	/** Diese Methode setzt die Referenz, von der aus die nächste für eine neue externe Entität mit Textwert verfügbare Referenz gesucht wird. */
	public synchronized void setExternalRef(int externalRef) {
		this.backup();
		this.externalRef = externalRef;
	}

	public synchronized FEMString getUndoInfo() {
		return this.undoInfo;
	}

	public synchronized void setUndoInfo(FEMString undoInfo) throws NullPointerException {
		if (undoInfo.equals(this.undoInfo)) return;
		this.backup();
		this.undoInfo = undoInfo;
	}

	public synchronized int getUndoLimit() {
		return this.undoHistory.getLimit();
	}

	public synchronized void setUndoLimit(int undoLimit) throws IllegalArgumentException {
		this.redoHistory.setLimit(undoLimit);
		this.undoHistory.setLimit(undoLimit);
	}

	public synchronized int getNextInternalRef() {
		this.backup();
		return this.nextInternalRef();
	}

	public synchronized int getNextExternalRef() {
		this.backup();
		return this.nextExternalRef();
	}

	public synchronized boolean fixSourceRelationRefs(int sourceRef, int... relationRefs) {
		if (sourceRef == 0) return false;
		var relationMap = this.getRefmap(this.sourceMap, sourceRef);
		var relationSet = REFSET.except(REFSET.copy(REFMAP.getKeys(relationMap)), REFSET.from(relationRefs));
		if (REFSET.size(relationSet) == 0) return false;
		this.backupEdges();
		REFSET.forEach(relationSet, relationRef -> this.fixSourceRelationTargetRefs(sourceRef, relationRef, REFSET.EMPTY_REFS));
		return true;
	}

	public synchronized boolean popSourceRelationRefs(int sourceRef, int... relationRefs) {
		if (sourceRef == 0) return false;
		var relationMap = this.getRefmap(this.sourceMap, sourceRef);
		var relationSet = REFSET.intersect(REFSET.from(relationRefs), REFMAP.getKeys(relationMap));
		if (REFSET.size(relationSet) == 0) return false;
		this.backupEdges();
		REFSET.forEach(relationSet, relationRef -> this.fixSourceRelationTargetRefs(sourceRef, relationRef, REFSET.EMPTY_REFS));
		return true;
	}

	public synchronized boolean fixSourceRelationTargetRefs(int sourceRef, int relationRef, int... targetRefs) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		if (KBState.isRef(targetVal)) {
			var targetRef = KBState.asRef(targetVal);
			if (this.contains(targetRefs, targetRef)) return false;
			this.backupEdges();
			this.deleteEdge(sourceRef, targetRef, relationRef);
		} else {
			var targetSet = REFSET.except(REFSET.copy(targetVal), REFSET.from(targetRefs));
			if (REFSET.size(targetSet) == 0) return false;
			this.backupEdges();
			REFSET.forEach(targetSet, targetRef -> this.deleteEdge(sourceRef, targetRef, relationRef));
		}
		return true;
	}

	public synchronized boolean setSourceRelationTargetRefs(int sourceRef, int relationRef, int... targetRefs) {
		return this.fixSourceRelationTargetRefs(sourceRef, relationRef, targetRefs) | this.putSourceRelationTargetRefs(sourceRef, relationRef, targetRefs);
	}

	public synchronized boolean putSourceRelationTargetRefs(int sourceRef, int relationRef, int... targetRefs) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		var targetSet = REFSET.from(targetRefs);
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		if (targetVal != null) {
			if (KBState.isRef(targetVal)) {
				var targetRef = KBState.asRef(targetVal);
				REFSET.popRef(targetSet, targetRef);
			} else {
				targetSet = REFSET.except(targetSet, targetVal);
			}
		}
		if (REFSET.size(targetSet) == 0) return false;
		this.backupEdges();
		REFSET.forEach(targetSet, targetRef -> this.deleteEdge(sourceRef, targetRef, relationRef));

		return true;

	}

	public synchronized boolean popSourceRelationTargetRefs(int sourceRef, int relationRef, int... targetRefs) {
		if ((sourceRef == 0) || (relationRef == 0)) return false;
		var targetVal = this.getRefset(this.sourceMap, sourceRef, relationRef);
		if (KBState.isRef(targetVal)) {
			var targetRef = KBState.asRef(targetVal);
			if (!this.contains(targetRefs, targetRef)) return false;
			this.backupEdges();
			this.deleteEdge(sourceRef, targetRef, relationRef);
		} else {
			var targetSet = REFSET.intersect(REFSET.from(targetRefs), targetVal);
			if (REFSET.size(targetSet) == 0) return false;
			this.backupEdges();
			REFSET.forEach(targetSet, targetRef -> this.deleteEdge(sourceRef, targetRef, relationRef));
		}
		return true;
	}

	public boolean putEdge(KBEdge edge) {
		return (edge != null) && this.putEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public synchronized boolean putEdge(int sourceRef, int targetRef, int relationRef) {
		this.backupEdges();
		return this.insertEdge(sourceRef, targetRef, relationRef);
	}

	public synchronized int putValue(FEMString valueStr) {
		this.backupValues();
		return this.insertValueStr(valueStr);
	}

	public synchronized void putValue(int valueRef, FEMString valueStr) {
		this.backupValues();
		this.insertValue(valueRef, valueStr);
	}

	public HashMapOI<FEMString> putAllValues(FEMString... values) {
		return this.putAllValues(Arrays.asList(values));
	}

	public synchronized HashMapOI<FEMString> putAllValues(Iterable<FEMString> values) {
		this.backupValues();
		var result = new HashMapOI<FEMString>();
		values.forEach(valueStr -> result.put(valueStr, this.insertValueStr(valueStr)));
		return result;
	}

	public synchronized boolean putAllValues(KBValues values) {
		this.backupValues();
		var result = this.valueStrMap.size();
		values.forEach(this::insertValue);
		return result != this.valueStrMap.size();
	}

	public boolean putAllEdges(KBEdge... edges) {
		return this.putAllEdges(Arrays.asList(edges));
	}

	public synchronized boolean putAllEdges(KBEdges edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach((sourceRef, targetRef, relationRef) -> result[0] = this.insertEdge(sourceRef, targetRef, relationRef) | result[0]);
		return result[0];
	}

	public synchronized boolean putAllEdges(Iterable<KBEdge> edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach(edge -> result[0] = this.insertEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | result[0]);
		return result[0];
	}

	public boolean popEdge(KBEdge edge) {
		return (edge != null) && this.popEdge(edge.sourceRef, edge.targetRef, edge.relationRef);
	}

	public synchronized boolean popEdge(int sourceRef, int targetRef, int relationRef) {
		this.backupEdges();
		return this.deleteEdge(sourceRef, targetRef, relationRef);
	}

	public boolean popAllEdges(KBEdge... edges) {
		return this.popAllEdges(Arrays.asList(edges));
	}

	public synchronized boolean popAllEdges(KBEdges edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach((sourceRef, targetRef, relationRef) -> result[0] = this.deleteEdge(sourceRef, targetRef, relationRef) | result[0]);
		return result[0];
	}

	public synchronized boolean popAllEdges(Iterable<KBEdge> edges) {
		this.backupEdges();
		var result = new boolean[1];
		edges.forEach(edge -> result[0] = this.deleteEdge(edge.sourceRef, edge.targetRef, edge.relationRef) | result[0]);
		return result[0];
	}

	public synchronized boolean popValue(FEMString valueStr) {
		this.backupValues();
		if (!this.deleteValueStr(valueStr)) return false;
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return true;
	}

	public synchronized boolean popValueRef(int valueRef) {
		this.backupValues();
		if (!this.deleteValueRef(valueRef)) return false;
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return true;
	}

	public boolean popAllValues(FEMString... valueStrs) {
		return this.popAllValues(Arrays.asList(valueStrs));
	}

	public synchronized boolean popAllValues(KBValues values) {
		this.backupValues();
		var result = this.valueStrMap.size();
		values.forEach(this::deleteValue);
		if (result == this.valueStrMap.size()) return false;
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return true;
	}

	public synchronized boolean popAllValues(Iterable<FEMString> valueStrs) {
		this.backupValues();
		var result = this.valueStrMap.size();
		valueStrs.forEach(this::deleteValueStr);
		if (result == this.valueStrMap.size()) return false;
		this.valueStrMap.pack();
		this.valueRefMap.pack();
		return true;
	}

	/** Diese Methode entfernt alle {@link #edges() Kanten} und {@link #values() Textwerte}. */
	public synchronized void clear() {
		this.backupEdges();
		this.backupValues();
		this.reset();
	}

	/** Diese Methode füt diesem Wissenspuffer alle {@link #edges() Kanten} und {@link #values() Textwerte} des gegebenen {@link KBState Wissensstandes} hinzu und
	 * übernimmt dessen Referenzen {@link #getIndexRef()}, {@link #getInternalRef()} und {@link #getExternalRef()}. */
	public synchronized void insertAll(KBState inserts) {
		this.backupEdges();
		this.backupValues();
		this.indexRef = inserts.indexRef;
		this.internalRef = inserts.internalRef;
		this.externalRef = inserts.externalRef;
		inserts.forEachEdge(this::insertEdge);
		inserts.forEachValue(this::insertValue);
	}

	/** Diese Methode entfernt aus diesem Wissenspuffer alle {@link #edges() Kanten} und {@link #values() Textwerte} des gegebenen {@link KBState Wissensstandes}.
	 * Die Referenzen {@link #getIndexRef()}, {@link #getInternalRef()} und {@link #getExternalRef()} bleiben unverändert. */
	public synchronized void deleteAll(KBState deletes) {
		this.backupEdges();
		this.backupValues();
		deletes.forEachEdge(this::deleteEdge);
		deletes.forEachValue(this::deleteValue);
		this.valueStrMap.pack();
		this.valueRefMap.pack();
	}

	/** Diese Methode ersetzt in diesem Wissenspuffer alle {@link #edges() Kanten}, {@link #values() Textwerte} sowie die Referenzen {@link #getIndexRef()},
	 * {@link #getInternalRef()} und {@link #getExternalRef()} durch die des gegebenen {@link KBState Wissensstandes}. */
	public synchronized void replaceAll(KBState state) {
		this.backup();
		this.backupEdges = true;
		this.backupValues = true;
		this.reset(KBState.from(state));
	}

	public synchronized boolean redo() {
		if (this.backup != null) throw new IllegalStateException();
		var redoItem = this.redoHistory.getFirstItem();
		if (redoItem == null) return false;
		this.undoHistory.putFirstItem();
		this.backupEdges();
		this.backupValues();
		var okay = false;
		try {
			this.deleteAll(redoItem.deleteData);
			this.insertAll(redoItem.insertData);
			this.valueStrMap.pack();
			this.valueRefMap.pack();
			this.undoHistory.addFirstItem(redoItem);
			this.redoHistory.popFirstItem();
			okay = true;
		} catch (IOException shouldNotHappen) {
			throw new IOError(shouldNotHappen);
		} finally {
			if (!okay) {
				this.rollback();
			}
		}
		return true;
	}

	public synchronized boolean undo() {
		if (this.backup != null) throw new IllegalStateException();
		var undoItem = this.undoHistory.getFirstItem();
		if (undoItem == null) return false;
		this.redoHistory.putFirstItem();
		this.backupEdges();
		this.backupValues();
		var okay = false;
		try {
			var insertData = undoItem.insertData;
			this.deleteAll(insertData);
			var deleteData = undoItem.deleteData;
			this.insertAll(deleteData);
			this.valueStrMap.pack();
			this.valueRefMap.pack();
			this.redoHistory.addFirstItem(undoItem);
			this.undoHistory.popFirstItem();
			okay = true;
		} catch (IOException shouldNotHappen) {
			throw new IOError(shouldNotHappen);
		} finally {
			if (!okay) {
				this.rollback();
			}
		}
		return true;
	}

	/** Diese Methode übernimmt alle Anderungen seit dem letzten {@link #commit()}, {@link #rollback()} bzw. der Erzeugung dieses Wissenspuffers. */
	public synchronized void commit() {
		var that = this.backup;
		if (that == null) return;
		var okay = false;
		try {
			if (this.undoHistory.getLimit() > 0) {
				this.undoHistory.putFirstItem();
				var undo = new HistoryItem();
				undo.info = FEMString.from(false, true, this.undoInfo.toBytes(true));
				{
					var inserts = new KBState();
					inserts.indexRef = this.indexRef;
					inserts.internalRef = this.internalRef;
					inserts.externalRef = this.externalRef;
					KBState.selectInserts(that, this, inserts::insertEdgeNowIntoSourceMap, inserts::insertValueNowIntoStrMap);
					undo.insertData = inserts.persist();
				}
				{
					var deleteState = new KBState();
					deleteState.indexRef = that.indexRef;
					deleteState.internalRef = that.internalRef;
					deleteState.externalRef = that.externalRef;
					KBState.selectInserts(this, that, deleteState::insertEdgeNowIntoSourceMap, deleteState::insertValueNowIntoStrMap);
					undo.deleteData = deleteState.persist();
				}
				this.undoHistory.addFirstItem(undo);
				this.redoHistory.popAll();
			}
			okay = true;
		} catch (IOException shouldNotHappen) {
			throw new IOError(shouldNotHappen);
		} finally {
			this.undoInfo = FEMString.EMPTY;
			this.backup = null;
			this.backupEdges = false;
			this.backupValues = false;
			if (!okay) {
				this.reset(that);
			}
		}
	}

	/** Diese Methode verwirft alle Anderungen seit dem letzten {@link #commit()}, {@link #rollback()} bzw. der Erzeugung dieses Wissenspuffers. */
	public synchronized void rollback() {
		var that = this.backup;
		if (that == null) return;
		this.reset(that);
		this.undoInfo = FEMString.EMPTY;
		this.backup = null;
		this.backupEdges = false;
		this.backupValues = false;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn es Änderungen seit dem letzten {@link #commit()}, {@link #rollback()} bzw. der Erzeugung dieses
	 * Wissenspuffersgibt gab. */
	public synchronized boolean hasBackup() {
		return this.backup != null;
	}

	public synchronized KBState getBackup() {
		var backup = this.backup;
		return backup != null ? backup : new KBState(this);
	}

	public synchronized KBState getSnapshot() {
		return this.backup != null ? KBState.from(this) : new KBState(this);
	}

	public List2<FEMString> getUndoInfos() {
		return this.undoHistory;
	}

	public List2<FEMString> getRedoInfos() {
		return this.redoHistory;
	}

	/** Dieses Feld speichert die Sicherungskopie für {@link #commit()} und {@link #rollback()} oder {@code null}. */
	private KBState backup;

	/** Dieses Feld speichert nur dann {@code true}, wenn {@link #backupEdges()} aufgerufen wurde. */
	private boolean backupEdges;

	/** Dieses Feld speichert nur dann {@code true}, wenn {@link #backupValues()} aufgerufen wurde. */
	private boolean backupValues;

	private FEMString undoInfo = FEMString.EMPTY;

	/** Dieses Feld speichert die umkehrbaren Änderungen und hat eine Potenz von zwei als Kapazität. */
	private final History undoHistory = new History(this);

	private final History redoHistory = new History(this);

	private void insertAll(byte[] insertData) throws IOException {
		ZIPDIS.inflate(insertData, zipdis -> {
			var inserts = new KBState();
			inserts.restoreRefs(zipdis);
			this.indexRef = inserts.indexRef;
			this.internalRef = inserts.internalRef;
			this.externalRef = inserts.externalRef;
			inserts.restoreEdgeMaps(zipdis, this::insertEdge);
			var count = zipdis.readInt(1)[0];
			inserts.restoreValueMaps(zipdis, count, this::insertValue);
			return inserts;
		});
	}

	private void deleteAll(byte[] insertData) throws IOException {
		ZIPDIS.inflate(insertData, zipdis -> {
			var deletes = new KBState();
			deletes.restoreRefs(zipdis);
			deletes.restoreEdgeMaps(zipdis, this::deleteEdge);
			var count = zipdis.readInt(1)[0];
			deletes.restoreValueMaps(zipdis, count, this::deleteValue);
			return deletes;
		});
	}

	private void backup() {
		if (this.backup != null) return;
		this.backup = new KBState(this);
		this.backupEdges = false;
		this.backupValues = false;
	}

	private void backupEdges() {
		if (this.backupEdges) return;
		this.backup();
		this.sourceMap = REFMAP.copy(this.sourceMap);
		this.targetMap = REFMAP.copy(this.targetMap);
		this.backupEdges = true;
	}

	private void backupValues() {
		if (this.backupValues) return;
		this.backup();
		this.valueStrMap = (ValueStrMap)this.valueStrMap.clone();
		this.valueRefMap = (ValueRefMap)this.valueRefMap.clone();
		this.backupValues = true;
	}

	private int nextExternalRef() {
		var externalRef = this.externalRef;
		externalRef = this.nextExternalRef(externalRef < 0 ? externalRef : -1);
		externalRef = externalRef < 0 ? externalRef : this.nextExternalRef(-1);
		this.externalRef = externalRef - 1;
		return externalRef;
	}

	private int nextExternalRef(int externalRef) {
		while (this.valueStrMap.containsKey(externalRef)) {
			externalRef--;
		}
		return externalRef;
	}

	private int nextInternalRef() {
		var internalRef = this.internalRef;
		internalRef = this.nextInternalRef(internalRef > 0 ? internalRef : 1);
		internalRef = internalRef > 0 ? internalRef : this.nextInternalRef(1);
		this.internalRef = internalRef + 1;
		return internalRef;
	}

	private int nextInternalRef(int internalRef) {
		while ((REFMAP.getIdx(this.sourceMap, internalRef) != 0) || (REFMAP.getIdx(this.targetMap, internalRef) != 0)) {
			internalRef++;
		}
		return internalRef;
	}

	private boolean insertEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return false;

		var sourceMap = this.sourceMap;
		var targetMap = this.targetMap;

		sourceMap = REFMAP.grow(sourceMap);
		targetMap = REFMAP.grow(targetMap);

		this.sourceMap = sourceMap;
		this.targetMap = targetMap;

		// PUT sourceMap
		var sourceIdx = REFMAP.putRef(sourceMap, sourceRef);

		// MAX sourceMap
		if (sourceIdx == 0) throw new IllegalStateException();

		// GET sourceRelationMap
		var sourceRelationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		var backupSourceRelationMap = (Object[])null;

		if (sourceRelationMap == null) {
			// NEW sourceRelationMap
			REFMAP.setVal(sourceMap, sourceIdx, REFMAP.EMPTY);
			sourceRelationMap = REFMAP.create();
		} else {
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			if (backupSourceIdx == 0) {
				// OWN sourceRelationMap
				sourceRelationMap = REFMAP.grow(sourceRelationMap);
			} else {
				backupSourceRelationMap = KBState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx));
				if (sourceRelationMap == backupSourceRelationMap) {
					// COW sourceRelationMap
					sourceRelationMap = REFMAP.grow(backupSourceRelationMap);
					if (sourceRelationMap == backupSourceRelationMap) {
						sourceRelationMap = REFMAP.copy(backupSourceRelationMap);
					}
				} else {
					// OWN sourceRelationMap
					sourceRelationMap = REFMAP.grow(sourceRelationMap);
				}
			}
		}
		REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);

		// PUT targetMap
		var targetIdx = REFMAP.putRef(targetMap, targetRef);

		// MAX targetMap
		if (targetIdx == 0) throw new IllegalStateException();

		// GET targetRelationMap
		var targetRelationMap = KBState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		var backupTargetRelationMap = (Object[])null;

		if (targetRelationMap == null) {
			// NEW targetRelationMap
			REFMAP.setVal(targetMap, targetIdx, REFMAP.EMPTY);
			targetRelationMap = REFMAP.create();
		} else {
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			if (backupTargetIdx == 0) {
				// OWN targetRelationMap
				targetRelationMap = REFMAP.grow(targetRelationMap);
			} else {
				backupTargetRelationMap = KBState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx));
				if (targetRelationMap == backupTargetRelationMap) {
					// COW targetRelationMap
					targetRelationMap = REFMAP.grow(backupTargetRelationMap);
					if (targetRelationMap == backupTargetRelationMap) {
						targetRelationMap = REFMAP.copy(backupTargetRelationMap);
					}
				} else {
					// OWN targetRelationMap
					targetRelationMap = REFMAP.grow(targetRelationMap);
				}
			}
		}
		REFMAP.setVal(targetMap, targetIdx, targetRelationMap);

		// PUT sourceRelationMap
		var sourceRelationIdx = REFMAP.putRef(sourceRelationMap, relationRef);

		// MAX sourceRelationMap
		if (sourceRelationIdx == 0) throw new IllegalStateException();

		// GET sourceRelationTargetVal
		var sourceRelationTargetSet = KBState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		var sourceRelationTargetSet2 = sourceRelationTargetSet;

		if (sourceRelationTargetSet == null) {
			// NEW sourceRelationTargetVal
			// REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.EMPTY);
			sourceRelationTargetSet = KBState.toRef(targetRef);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else if (KBState.isRef(sourceRelationTargetSet)) {
			// ADD sourceRelationTargetVal
			var targetRef2 = KBState.asRef(sourceRelationTargetSet);
			if (targetRef == targetRef2) return false; // OLD
			sourceRelationTargetSet = REFSET.from(targetRef, targetRef2);
			REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
		} else {
			var backupSourceRelationIdx = backupSourceRelationMap != null ? REFMAP.getIdx(backupSourceRelationMap, relationRef) : 0;
			var backupSourceRelationTargetVal =
				backupSourceRelationIdx != 0 ? KBState.asRefVal(REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx)) : null;
			if (sourceRelationTargetSet == backupSourceRelationTargetVal) {
				// COW sourceRelationTargetVal
				sourceRelationTargetSet = REFSET.grow(backupSourceRelationTargetVal);
				if (sourceRelationTargetSet == backupSourceRelationTargetVal) {
					sourceRelationTargetSet = REFSET.copy(backupSourceRelationTargetVal);
				}
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
				REFSET.putRef(sourceRelationTargetSet, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false; // OLD
			} else {
				// OWN sourceRelationTargetVal
				sourceRelationTargetSet = REFSET.grow(sourceRelationTargetSet);
				var nextSourceRelationTargetCount = REFSET.size(sourceRelationTargetSet);
				REFSET.putRef(sourceRelationTargetSet, targetRef);
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet);
				if (nextSourceRelationTargetCount == REFSET.size(sourceRelationTargetSet)) return false; // OLD
			}
		}

		// PUT targetRelationMap
		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);

		// MAX targetRelationMap
		if (targetRelationIdx == 0) {
			if (KBState.isRef(sourceRelationTargetSet)) {
				REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			} else if (KBState.isRef(sourceRelationTargetSet2)) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
			} else {
				REFSET.popRef(sourceRelationTargetSet, targetRef);
			}
			throw new IllegalStateException();
		}

		var targetRelationSourceVal = KBState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		var targetRelationSourceSet2 = targetRelationSourceVal;

		try {
			if (targetRelationSourceVal == null) {
				// REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.EMPTY);
				targetRelationSourceVal = KBState.toRef(sourceRef);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else if (KBState.isRef(targetRelationSourceVal)) {
				// ADD targetRelationSourceVal
				var sourceRef2 = KBState.asRef(targetRelationSourceVal);
				targetRelationSourceVal = REFSET.from(sourceRef, sourceRef2);
				REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
			} else {
				var backupTargetRelationIdx = backupTargetRelationMap != null ? REFMAP.getIdx(backupTargetRelationMap, relationRef) : 0;
				var backupTargetRelationSourceVal =
					backupTargetRelationIdx != 0 ? KBState.asRefVal(REFMAP.getVal(backupTargetRelationMap, backupTargetRelationIdx)) : null;
				if (targetRelationSourceVal == backupTargetRelationSourceVal) {
					// COW targetRelationSourceVal
					targetRelationSourceVal = REFSET.grow(backupTargetRelationSourceVal);
					if (targetRelationSourceVal == backupTargetRelationSourceVal) {
						targetRelationSourceVal = REFSET.copy(backupTargetRelationSourceVal);
					}
					REFSET.putRef(targetRelationSourceVal, sourceRef);
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				} else {
					// OWN targetRelationSourceVal
					targetRelationSourceVal = REFSET.grow(targetRelationSourceVal);
					REFSET.putRef(targetRelationSourceVal, sourceRef);
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceVal);
				}
			}
			targetRelationIdx = 0;
		} finally {
			if (targetRelationIdx != 0) {
				if (KBState.isRef(sourceRelationTargetSet)) {
					REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
				} else if (KBState.isRef(sourceRelationTargetSet2)) {
					REFMAP.setVal(sourceRelationMap, sourceRelationIdx, sourceRelationTargetSet2);
				} else {
					REFSET.popRef(sourceRelationTargetSet, targetRef);
				}
				if (KBState.isRef(targetRelationSourceVal)) {
					REFMAP.setVal(targetRelationMap, REFMAP.popRef(targetRelationMap, relationRef), null);
				} else if (KBState.isRef(targetRelationSourceSet2)) {
					REFMAP.setVal(targetRelationMap, targetRelationIdx, targetRelationSourceSet2);
				}
				throw new IllegalStateException();
			}
		}

		return true;
	}

	private void insertValue(int valueRef, FEMString valueStr) {
		if (valueRef >= 0) throw new IllegalArgumentException();
		var valueRef2 = this.valueRefMap.put(valueStr, valueRef);
		if (valueRef2 != null) {
			if (valueRef == valueRef2.intValue()) return;
			this.valueRefMap.put(valueStr, valueRef2);
			throw new IllegalArgumentException();
		}
		var valueStr2 = this.valueStrMap.put(valueRef, valueStr);
		if (valueStr2 != null) {
			this.valueStrMap.put(valueRef, valueStr2);
			this.valueRefMap.remove(valueStr);
			throw new IllegalArgumentException();
		}
	}

	private int insertValueStr(FEMString valueStr) {
		var valueRef = this.valueRefMap.get(valueStr.data());
		if (valueRef != null) return valueRef;
		valueRef = this.nextExternalRef();
		this.valueRefMap.put(valueStr, valueRef);
		this.valueStrMap.put(valueRef, valueStr);
		return valueRef;
	}

	private boolean deleteEdge(int sourceRef, int targetRef, int relationRef) {
		if ((sourceRef == 0) || (targetRef == 0) || (relationRef == 0)) return false;

		var sourceMap = this.sourceMap;
		var sourceIdx = REFMAP.getIdx(sourceMap, sourceRef);
		if (sourceIdx == 0) return false; // NOT sourceRef

		var targetMap = this.targetMap;
		var targetIdx = REFMAP.getIdx(targetMap, targetRef);
		if (targetIdx == 0) return false; // NOT targetRef

		var sourceRelationMap = KBState.asRefMap(REFMAP.getVal(sourceMap, sourceIdx));
		var sourceRelationIdx = REFMAP.getIdx(sourceRelationMap, relationRef);
		if (sourceRelationIdx == 0) return false; // NOT sourceRef relationRef

		var targetRelationMap = KBState.asRefMap(REFMAP.getVal(targetMap, targetIdx));
		var targetRelationIdx = REFMAP.putRef(targetRelationMap, relationRef);
		if (targetRelationIdx == 0) return false; // ERR targetRef relationRef (IllegalState)

		var backupSourceRelationMap = (Object[])null;
		{ // COW sourceRelationMap
			var backupSourceMap = this.backup.sourceMap;
			var backupSourceIdx = REFMAP.getIdx(backupSourceMap, sourceRef);
			if (backupSourceIdx != 0) {
				backupSourceRelationMap = KBState.asRefMap(REFMAP.getVal(backupSourceMap, backupSourceIdx));
				if (sourceRelationMap == backupSourceRelationMap) {
					sourceRelationMap = REFMAP.copy(sourceRelationMap);
					REFMAP.setVal(sourceMap, sourceIdx, sourceRelationMap);
				}
			}
		}

		var backupTargetRelationMap = (Object[])null;
		{ // COW targetRelationMap
			var backupTargetMap = this.backup.targetMap;
			var backupTargetIdx = REFMAP.getIdx(backupTargetMap, targetRef);
			if (backupTargetIdx != 0) {
				backupTargetRelationMap = KBState.asRefMap(REFMAP.getVal(backupTargetMap, backupTargetIdx));
				if (targetRelationMap == backupTargetRelationMap) {
					targetRelationMap = REFMAP.copy(targetRelationMap);
					REFMAP.setVal(targetMap, targetIdx, targetRelationMap);

				}
			}
		}

		var sourceRelationTargetSet = KBState.asRefVal(REFMAP.getVal(sourceRelationMap, sourceRelationIdx));
		if (KBState.isRef(sourceRelationTargetSet)) {
			var targetRef2 = KBState.asRef(sourceRelationTargetSet);
			if (targetRef != targetRef2) return false; // ERR sourceRef relationRef targetRef (IllegalState)
			REFMAP.setVal(sourceRelationMap, REFMAP.popRef(sourceRelationMap, relationRef), null);
			if (REFMAP.size(sourceRelationMap) == 0) { // POP sourceRelationMap
				REFMAP.setVal(sourceMap, REFMAP.popRef(sourceMap, sourceRef), null);
				this.sourceMap = REFMAP.pack(sourceMap);
			} else { // POP sourceRelationTargetSet
				REFMAP.setVal(sourceMap, sourceIdx, REFMAP.pack(sourceRelationMap));
			}
		} else {
			if (backupSourceRelationMap != null) { // COW sourceRelationTargetSet
				var backupSourceRelationIdx = REFMAP.getIdx(backupSourceRelationMap, relationRef);
				if (backupSourceRelationIdx != 0) {
					var backupSourceRelationTargetSet = REFMAP.getVal(backupSourceRelationMap, backupSourceRelationIdx);
					if (sourceRelationTargetSet == backupSourceRelationTargetSet) {
						sourceRelationTargetSet = REFSET.copy(sourceRelationTargetSet);
					}
				}
			}
			var sourceRelationTargetIdx = REFSET.popRef(sourceRelationTargetSet, targetRef);
			if (sourceRelationTargetIdx == 0) return false; // ERR sourceRef relationRef targetRef (IllegalState)
			if (REFSET.size(sourceRelationTargetSet) == 0) {
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, null);
				if (REFMAP.size(sourceRelationMap) == 0) { // POP sourceRelationMap
					REFMAP.setVal(sourceMap, REFMAP.popRef(sourceMap, sourceRef), null);
					this.sourceMap = REFMAP.pack(sourceMap);
				} else { // POP sourceRelationTargetSet
					REFMAP.setVal(sourceMap, sourceIdx, REFMAP.pack(sourceRelationMap));
				}
			} else { // SET sourceRelationTargetSet
				REFMAP.setVal(sourceRelationMap, sourceRelationIdx, REFSET.pack(sourceRelationTargetSet));
			}
		}

		var targetRelationSourceSet = KBState.asRefVal(REFMAP.getVal(targetRelationMap, targetRelationIdx));
		if (KBState.isRef(targetRelationSourceSet)) {
			var sourceRef2 = KBState.asRef(targetRelationSourceSet);
			if (sourceRef != sourceRef2) return false; // ERR sourceRef relationRef targetRef (IllegalState)
			REFMAP.setVal(targetRelationMap, REFMAP.popRef(targetRelationMap, relationRef), null);
			if (REFMAP.size(targetRelationMap) == 0) { // POP targetRelationMap
				REFMAP.setVal(targetMap, REFMAP.popRef(targetMap, targetRef), null);
				this.targetMap = REFMAP.pack(targetMap);
			} else { // POP targetRelationSourceSet
				REFMAP.setVal(targetMap, targetIdx, REFMAP.pack(targetRelationMap));
			}
		} else {
			if (backupTargetRelationMap != null) { // COW targetRelationSourceSet
				var backupTargetRelationIdx = REFMAP.getIdx(backupTargetRelationMap, relationRef);
				if (backupTargetRelationIdx != 0) {
					var backupTargetRelationSourceSet = REFMAP.getVal(backupTargetRelationMap, backupTargetRelationIdx);
					if (targetRelationSourceSet == backupTargetRelationSourceSet) {
						targetRelationSourceSet = REFSET.copy(targetRelationSourceSet);
					}
				}
			}
			var targetRelationSourceIdx = REFSET.popRef(targetRelationSourceSet, sourceRef);
			if (targetRelationSourceIdx == 0) return false; // ERR sourceRef relationRef targetRef (IllegalState)
			if (REFSET.size(targetRelationSourceSet) == 0) {
				REFMAP.setVal(targetRelationMap, targetRelationIdx, null);
				if (REFMAP.size(targetRelationMap) == 0) { // POP targetRelationMap
					REFMAP.setVal(targetMap, REFMAP.popRef(targetMap, targetRef), null);
					this.targetMap = REFMAP.pack(targetMap);
				} else { // POP targetRelationSourceSet
					REFMAP.setVal(targetMap, targetIdx, REFMAP.pack(targetRelationMap));
				}
			} else { // SET targetRelationSourceSet
				REFMAP.setVal(targetRelationMap, targetRelationIdx, REFSET.pack(targetRelationSourceSet));
			}
		}

		return true;
	}

	private void deleteValue(int valueRef, FEMString valueStr) {
		var valueStr2 = this.valueStrMap.remove(valueRef);
		var valueRef2 = this.valueRefMap.remove(valueStr);
		if (valueStr2 == null) {
			if (valueRef2 == null) return;
			this.valueRefMap.put(valueStr, valueRef2);
			throw new IllegalArgumentException();
		}
		if (valueRef2 == null) {
			this.valueStrMap.put(valueRef, valueStr2);
			throw new IllegalArgumentException();
		}
		if (valueRef == valueRef2.intValue()) return;
		this.valueRefMap.put(valueStr, valueRef2);
		this.valueStrMap.put(valueRef, valueStr2);
		throw new IllegalArgumentException();
	}

	private boolean deleteValueRef(int valueRef) {
		var valueStr = this.valueStrMap.remove(valueRef);
		if (valueStr == null) return false;
		this.valueRefMap.remove(valueStr);
		return true;
	}

	private boolean deleteValueStr(FEMString valueStr) {
		var valueRef = this.valueRefMap.remove(valueStr);
		if (valueRef == null) return false;
		this.valueStrMap.remove(valueRef);
		return true;
	}

	private boolean contains(int[] refs, int ref) {
		for (int ref2: refs) {
			if (ref == ref2) return true;
		}
		return false;
	}

	private static class History extends AbstractList2<FEMString> {

		@Override
		public FEMString get(int index) {
			synchronized (this.owner) {
				if ((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException(index);
				return this.items[(this.first + index) & (this.items.length - 1)].info;
			}

		}

		public void popAll() {
		}

		@Override
		public int size() {
			synchronized (this.owner) {
				return this.size;
			}
		}

		History(KBBuffer owner) {
			this.owner = owner;
			this.items = new HistoryItem[1];
		}

		private final KBBuffer owner;

		private int size;

		private int first;

		private int limit;

		private HistoryItem[] items;

		int getLimit() {
			return this.limit;
		}

		void setLimit(int limit) {
			if ((limit < 0) || (limit > 536870912)) throw new IllegalArgumentException();
			if (this.limit == limit) return;
			// TODO

			this.limit = limit;
		}

		HistoryItem getFirstItem() { // ersten eintrag lesen
			return this.items[this.first];
		}

		void popFirstItem() { // ersten einrtag entfernen
			var first = this.first;
			this.items[first] = null;
			this.first = (first + 1) & (this.items.length - 1);
		}

		void putFirstItem() { // platz für neuen ersten eintrag machen
			if (this.size < this.limit) {
				var length = this.items.length;
				if (this.size < length) return;
				var items = new HistoryItem[length + length];
				var count = length - this.first;
				System.arraycopy(this.items, this.first, items, 0, count);
				System.arraycopy(this.items, 0, items, count, this.first);
				this.first = 0;
				this.items = items;
			}
		}

		void addFirstItem(HistoryItem item) { // ersten eintrag anfügen
			var size = this.size;
			var mask = this.items.length - 1;
			this.first = (this.first + mask) & mask;
			if (size < this.limit) {
				this.size = size + 1;
			} else {
				this.items[(this.first + size) & mask] = null;
			}
			this.items[this.first] = item;
		}

	}

	private static class HistoryItem {

		FEMString info;

		byte[] insertData;

		byte[] deleteData;

	}

}
