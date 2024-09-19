package bee.creative.ber;

/** liefert schreibgeschützte sicht auf vorherigen und neuen datenstand sowie die differenz */
class BERUpdate {

	public BERStore getStore() {
		return this.store;
	}

	public BERState getOldState() {
		return this.oldState;
	}

	public BERState getNewState() {
		return this.newState;
	}

	// berechnet bei bedarf die in newState dazugekommenen inhalte
	BEREdges getPutEdges() {
		return null;
	}

	// berechnet bei bedarf die in newState entfallenen inhalte
	BEREdges getPopEdges() {
		return null;
	}

	BERUpdate(BERStore store, boolean commit) {
		var prevState = new BERState2();
		prevState.nextRef = store.prevNextRef != null ? store.prevNextRef : store.nextRef;
		prevState.rootRef = store.prevRootRef != null ? store.prevRootRef : store.rootRef;
		prevState.sourceMap = store.prevSourceMap != null ? store.prevSourceMap : store.sourceMap;
		prevState.targetMap = store.prevTargetMap != null ? store.prevTargetMap : store.targetMap;
		var nextState = new BERState2();
		nextState.nextRef = store.nextRef;
		nextState.rootRef = store.rootRef;
		nextState.sourceMap = store.sourceMap;
		nextState.targetMap = store.targetMap;
		this.store = store;
		if (commit) {
			this.oldState = prevState;
			this.newState = nextState;
		} else {
			this.oldState = nextState;
			this.newState = prevState;
			store.nextRef = prevState.nextRef;
			store.rootRef = prevState.rootRef;
			store.sourceMap = prevState.sourceMap;
			store.targetMap = prevState.targetMap;
		}
		store.prevRootRef = null;
		store.prevNextRef = null;
		store.prevSourceMap = null;
		store.prevTargetMap = null;
	}

	final BERStore store;

	final BERState oldState;

	final BERState newState;

}
