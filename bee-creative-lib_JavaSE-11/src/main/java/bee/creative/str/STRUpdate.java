package bee.creative.str;

/** Diese Klasse implementiert den Änderungsbericht zu {@link STRBuffer#commit()} und {@link STRBuffer#rollback()}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class STRUpdate {

	/** Diese Methode liefet den {@link STRBuffer Kandenspeicher} einem {@link STRBuffer#commit()} die Kantenmenge vor der ersten Änderung. Bei einem
	 * {@link STRBuffer#rollback()} liefert sie die Kantenmenge nach der letzten Änderung. */
	public STRBuffer owner() {
		return this.store;
	}

	/** Diese Methode liefet bei einem {@link STRBuffer#commit()} die Kantenmenge vor der ersten Änderung. Bei einem {@link STRBuffer#rollback()} liefert sie die
	 * Kantenmenge nach der letzten Änderung. */
	public STRState getOldState() {
		return this.oldState;
	}

	/** Diese Methode liefet bei einem {@link STRBuffer#commit()} die Kantenmenge nach der letzten Änderung. Bei einem {@link STRBuffer#rollback()} liefert sie
	 * die Kantenmenge vor der ersten Änderung. */
	public STRState getNewState() {
		return this.newState;
	}

	/** Diese Methode liefet die Menge der durch die Änderung ergänzten Kanten sowie die dabei eingetretene Erhöhung von {@link STRState#getNextRef()} und
	 * {@link STRState#getRootRef()}. */
	public synchronized STRState getPutState() {
		return this.putState == null ? this.putState = STRState.from(this.oldState, this.newState) : this.putState;
	}

	/** Diese Methode liefet die Menge der durch die Änderung entfernten Kanten sowie die dabei eingetretene Senkung von {@link STRState#getNextRef()} und
	 * {@link STRState#getRootRef()}. */
	public synchronized STRState getPopState() {
		return this.popState == null ? this.popState = STRState.from(this.newState, this.oldState) : this.popState;
	}

	STRUpdate(STRBuffer store, boolean commit) {
		this.store = store;
		var backup = store.backup;
		if (backup != null) {
			if (commit) {
				this.oldState = new STRState(backup);
				this.newState = new STRState(store);
			} else {
				this.oldState = new STRState(store);
				this.newState = new STRState(backup);
				store.nextRef = backup.nextRef;
				store.rootRef = backup.rootRef;
				store.sourceMap = backup.sourceMap;
				store.targetMap = backup.targetMap;
			}
			store.backup = null;
		} else {
			this.newState = this.oldState = new STRState(store);
		}
	}

	private final STRBuffer store;

	private final STRState oldState;

	private final STRState newState;

	private STRState putState;

	private STRState popState;

}
