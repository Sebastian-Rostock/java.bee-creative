package bee.creative.kb;

/** Diese Klasse implementiert den Änderungsbericht für {@link KBBuffer#commit()} und {@link KBBuffer#rollback()}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class KBUpdate {

	/** Diese Methode liefet den {@link KBBuffer Wissenspuffer}, über dessen Änderungen berichtet wird. */
	public KBBuffer owner() {
		return this.owner;
	}

	/** Diese Methode liefet bei einem {@link KBBuffer#commit()} den {@link KBState Wissensstand} vor der ersten Änderung. Bei einem {@link KBBuffer#rollback()}
	 * liefert sie den Wissensstand nach der letzten Änderung. */
	public KBState getOldState() {
		return this.oldState;
	}

	/** Diese Methode liefet bei einem {@link KBBuffer#commit()} den {@link KBState Wissensstand} nach der letzten Änderung. Bei einem {@link KBBuffer#rollback()}
	 * liefert sie den Wissensstand vor der ersten Änderung. */
	public KBState getNewState() {
		return this.newState;
	}

	/** Diese Methode liefet die {@link KBState Wissensmenge}, die durch die Änderung ergänzt wurde und bei Bedarf über {@link KBState#from(KBState, KBState)
	 * KBState.from(this.getOldState(), this.getNewState())} erzeugt wird. */
	public synchronized KBState getInserts() {
		return this.inserts == null ? this.inserts = KBState.from(this.oldState, this.newState) : this.inserts;
	}

	/** Diese Methode liefet die {@link KBState Wissensmenge}, die durch die Änderung entfernt wurde und bei Bedarf über {@link KBState#from(KBState, KBState)
	 * KBState.from(this.getNewState(), this.getOldState())} erzeugt wird. */
	public synchronized KBState getDeletes() {
		return this.deletes == null ? this.deletes = KBState.from(this.newState, this.oldState) : this.deletes;
	}

	final KBBuffer owner;

	final KBState oldState;

	final KBState newState;

	KBState inserts;

	KBState deletes;

	/** Dieser Konstruktor initialisiert das Änderungsprotokoll und schließt die Änderung ab. Wenn {@code commit} {@code true} ist, werden alle Änderungen
	 * angenommen. Andernfalls werden sie verworfen. */
	KBUpdate(KBBuffer owner, boolean commit) {
		this.owner = owner;
		var backup = owner.backup;
		if (backup != null) {
			if (commit) {
				this.oldState = new KBState(backup);
				this.newState = new KBState(owner);
			} else {
				this.oldState = new KBState(owner);
				this.newState = new KBState(backup);
				owner.nextRef = backup.nextRef;
				owner.rootRef = backup.rootRef;
				owner.sourceMap = backup.sourceMap;
				owner.targetMap = backup.targetMap;
				owner.valueRefMap = backup.valueRefMap;
				owner.valueStrMap = backup.valueStrMap;
			}
			owner.backup = null;
		} else {
			this.newState = this.oldState = new KBState(owner);
		}
	}

}
