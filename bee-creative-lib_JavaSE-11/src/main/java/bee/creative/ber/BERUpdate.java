package bee.creative.ber;

/** Diese Klasse implementiert den Änderungsbericht zu {@link BERStore#commit()} und {@link BERStore#rollback()}.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BERUpdate {

	public BERStore getStore() {
		return this.store;
	}

	/** Diese Methode liefet bei einem {@link BERStore#commit()} die Kantenmenge vor der ersten Änderung. Bei einem {@link BERStore#rollback()} liefert sie die
	 * Kantenmenge nach der letzten Änderung. */
	public BERState getOldState() {
		return this.oldState;
	}

	/** Diese Methode liefet bei einem {@link BERStore#commit()} die Kantenmenge nach der letzten Änderung. Bei einem {@link BERStore#rollback()} liefert sie die
	 * Kantenmenge vor der ersten Änderung. */
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
				this.oldState = new BERState(store.backup);
				this.newState = new BERState(store);
			} else {
				this.oldState = new BERState(store);
				this.newState = new BERState(store.backup);
			}
			store.backup = null;
		} else {
			this.newState = this.oldState = new BERState(store);
		}
	}

	private final BERStore store;

	private final BERState oldState;

	private final BERState newState;

	private BERState putEdges;

	private BERState popEdges;

}
