package bee.creative.str;

/** Diese Klasse implementiert den Änderungsbericht zu {@link STRStore#commit()} und {@link STRStore#rollback()}.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class STRUpdate {

	public STRStore getStore() {
		return this.store;
	}

	/** Diese Methode liefet bei einem {@link STRStore#commit()} die Kantenmenge vor der ersten Änderung. Bei einem {@link STRStore#rollback()} liefert sie die
	 * Kantenmenge nach der letzten Änderung. */
	public STRState getOldState() {
		return this.oldState;
	}

	/** Diese Methode liefet bei einem {@link STRStore#commit()} die Kantenmenge nach der letzten Änderung. Bei einem {@link STRStore#rollback()} liefert sie die
	 * Kantenmenge vor der ersten Änderung. */
	public STRState getNewState() {
		return this.newState;
	}

	// die in newState dazugekommenen inhalte
	public synchronized STRState getPutState() {
		return this.putState == null ? this.putState = STRState.from(this.oldState, this.newState) : this.putState;
	}

	// die in newState entfallenen inhalte
	public synchronized STRState getPopState() {
		return this.popState == null ? this.popState = STRState.from(this.newState, this.oldState) : this.popState;
	}

	STRUpdate(STRStore store, boolean commit) {
		this.store = store;
		if (store.backup != null) {
			if (commit) {
				this.oldState = new STRState(store.backup);
				this.newState = new STRState(store);
			} else {
				this.oldState = new STRState(store);
				this.newState = new STRState(store.backup);
			}
			store.backup = null;
		} else {
			this.newState = this.oldState = new STRState(store);
			
		}
	}

	private final STRStore store;

	private final STRState oldState;

	private final STRState newState;

	private STRState putState;

	private STRState popState;

}
