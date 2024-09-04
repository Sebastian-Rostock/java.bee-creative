package bee.creative.ber;

/** Diese Klasse implementiert einen bidirectional-entity-relation Speicher.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERStore extends BERState {

	private Object owner;

	BERStore() {
		this(null);
	}

	BERStore(Object owner) {
		this.owner = owner;

	}

	Object getOwner() {
		return null;
	}

	void putEdges(BEREdges edges) {

	}

	void popEdges(BEREdges edges) {

	}

	public void setRootRef(int rootRef) {
		if (this.prevRootRef == null) {
			this.prevRootRef = this.rootRef;
		}
		this.rootRef = rootRef;
	}

	public void setNextRef(int nextRef) {
		if (this.prevNextRef == null) {
			this.prevNextRef = this.nextRef;
		}
		this.nextRef = nextRef;
	}

	int newEntityRef() {
		var nextRef = this.nextRef;
		while (this.isSourceRef(nextRef) || this.isTargetRef(nextRef)) {
			nextRef++;
		}
		this.setNewEntityRef(nextRef + 1);
		return nextRef;
	}

	/** Diese Methode gibt das zurück. wenn gegebener nodeRef als source oder target vorkommt, in cow-nodeRefGen eintragen und aus source/target maps entfernen
	 *
	 * @param entityRef */
	void popEntityRef(int entityRef) {

	}

	/** Diese Methode gibt das zurück. übernimmt die gegebenen refs zur wiederverwendung. weitere werden nach dem größten automatisch ergänzt. duplikate und refs
	 * <=0 nicht zulässig. leere liste nicht zulässig. */
	void setNewEntityRef(int entityRef) {

	}

	/** Diese Methode gibt das zurück. ersetzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der
	 * einzigartigen referenzen kopiert diese an den beginn von sourceRefs
	 *
	 * @param targetRef
	 * @param relationRef
	 * @param sourceRefs
	 * @return */
	int setSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {

		return 0;
	}

	public boolean putEdge(int sourceRef, int relationRef, int targetRef) {
		return putSourceRef(targetRef, relationRef, sourceRef);
	}
	public boolean putSourceRef(int targetRef, int relationRef, int sourceRef) {
		if ((sourceRef == 0) || (relationRef == 0) || (targetRef == 0)) return false;

		// grow sourceMap
		var prevSourceMap = this.prevSourceMap;
		var nextSourceMap = this.sourceMap;
		if (prevSourceMap == null) {
			nextSourceMap = REFMAP.grow(this.prevSourceMap = prevSourceMap = nextSourceMap);
			if (prevSourceMap == nextSourceMap) {
				nextSourceMap = REFMAP.copy(nextSourceMap);
			}
		} else {
			nextSourceMap = REFMAP.grow(nextSourceMap);
		}
		this.sourceMap = nextSourceMap;

		// grow prevSourceMap[sourceRef]
		var prevSourceRelationIdx = REFMAP.getIdx(prevSourceMap, sourceRef);
		var prevSourceRelationMap = prevSourceRelationIdx != 0 ? BEREdges.asRefMap(REFMAP.getVal(prevSourceMap, prevSourceRelationIdx)) : null;
		var nextSourceRelationIdx = REFMAP.putRef(nextSourceMap, sourceRef);
		var nextSourceRelationMap = BEREdges.asRefMap(REFMAP.getVal(nextSourceMap, nextSourceRelationIdx));
		if (nextSourceRelationMap == null) {
			nextSourceRelationMap = REFMAP.make();
		} else if (nextSourceRelationMap == prevSourceRelationMap) {
			nextSourceRelationMap = REFMAP.grow(prevSourceRelationMap);
			if (nextSourceRelationMap == prevSourceRelationMap) {
				nextSourceRelationMap = REFMAP.copy(prevSourceRelationMap);
			}
		} else {
			nextSourceRelationMap = REFMAP.grow(nextSourceRelationMap);
		}
		REFMAP.setVal(nextSourceMap, nextSourceRelationIdx, nextSourceRelationMap);

		var result = 0;

		// grow prevSourceMap[sourceRef][relationRef]
		var prevSourceRelationTargetIdx = prevSourceRelationMap != null ? REFMAP.getIdx(prevSourceRelationMap, relationRef) : 0;
		var prevSourceRelationTargetVal = prevSourceRelationTargetIdx != 0 ? REFMAP.getVal(prevSourceRelationMap, prevSourceRelationTargetIdx) : null;
		var nextSourceRelationTargetIdx = REFMAP.putRef(nextSourceRelationMap, relationRef);
		var nextSourceRelationTargetVal = REFMAP.getVal(nextSourceRelationMap, nextSourceRelationTargetIdx);
		if (nextSourceRelationTargetVal == null) { // ok
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, targetRef);
			result |= 1;
		} else if (BEREdges.isRef(nextSourceRelationTargetVal)) { // ok
			var targetRef2 = BEREdges.asRef(nextSourceRelationTargetVal);
			if (targetRef == targetRef2) return false;
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, REFSET.from(targetRef, targetRef2));
			result |= 1;
		} else if (nextSourceRelationTargetVal == prevSourceRelationTargetVal) {
			var prevSourceRelationTargetSet = BEREdges.asRefSet(prevSourceRelationTargetVal);
			var nextSourceRelationTargetSet = REFSET.grow(prevSourceRelationTargetSet);
			if (nextSourceRelationTargetSet == prevSourceRelationTargetSet) {
				nextSourceRelationTargetSet = REFSET.copy(prevSourceRelationTargetSet);
			}
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, nextSourceRelationTargetSet);
			var nextSourceRelationTargetCount = REFSET.size(nextSourceRelationTargetSet);
			REFSET.putRef(nextSourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetCount == REFSET.size(nextSourceRelationTargetSet)) return false;
		} else {
			var nextSourceRelationTargetSet = REFSET.grow(BEREdges.asRefSet(nextSourceRelationTargetVal));
			REFMAP.setVal(nextSourceRelationMap, nextSourceRelationTargetIdx, nextSourceRelationTargetSet);
			var nextSourceRelationTargetCount = REFSET.size(nextSourceRelationTargetSet);
			REFSET.putRef(nextSourceRelationTargetSet, targetRef);
			if (nextSourceRelationTargetCount == REFSET.size(nextSourceRelationTargetSet)) return false;
		}

		// grow targetMap
		var prevTargetMap = this.prevTargetMap;
		var nextTargetMap = this.targetMap;
		if (prevTargetMap == null) {
			nextTargetMap = REFMAP.grow(this.prevTargetMap = prevTargetMap = nextTargetMap);
			if (prevTargetMap == nextTargetMap) {
				nextTargetMap = REFMAP.copy(nextTargetMap);
			}
		} else {
			nextTargetMap = REFMAP.grow(nextTargetMap);
		}
		this.targetMap = nextTargetMap;

		return result != 0;
	}

	/** ergänzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der ergänzten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int putSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	/** entfernt von den als source von target und rel vorkommenden referenzen die > 0 gegebenen liefert die anzahl der entfernten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int popSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	BERUpdate commit() {

		this.prevRootRef = null;
		this.prevNextRef = null;

		return null;
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	BERUpdate rollback() {
		// der aktuelle
		var oldState = new BERState();
		// der in prev gespeicherte und wiederhergestellte
		var newState = new BERState();

		return null;
	}

	// verwirft die änderungen seit dem letzten commit.
	// stellt den gegebenen zustand wieder her.
	BERUpdate rollback(BERState state) {
		return null;
	}

	Integer prevRootRef;

	Integer prevNextRef;

	Object[] prevSourceMap;

	Object[] prevTargetMap;

}
