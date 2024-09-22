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

	// die in newState dazugekommenen inhalte
	public BERState getPutEdges() {
		return this.putEdges == null ? this.putEdges = BERState.from(this.oldState, this.newState) : this.putEdges;
	}

	// die in newState entfallenen inhalte
	public BERState getPopEdges() {
		return this.popEdges == null ? this.popEdges = BERState.from(this.newState, this.oldState) : this.popEdges;
	}

	BERUpdate(BERStore store, boolean commit) {
		this.store = store;
		if (store.backup != null) {
			if (commit) {
				this.oldState.setAll(store.backup);
				this.newState.setAll(store);
			} else {
				this.oldState.setAll(store);
				this.newState.setAll(store.backup);
			}
			store.backup = null;
		} else {
			this.oldState.setAll(store);
			this.newState.setAll(store);
		}
	}

	final BERStore store;

	final BERState oldState = new BERState();

	final BERState newState = new BERState();

	BERState putEdges;

	BERState popEdges;

}
