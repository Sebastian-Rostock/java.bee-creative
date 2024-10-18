package bee.creative.kb;

/** Diese Klasse implementiert den Änderungsbericht für {@link KBBuffer#commit()} und {@link KBBuffer#rollback()}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class KBUpdate {

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

	KBState inserts;

	KBState deletes;

	final KBState oldState;

	final KBState newState;

	/** Dieser Konstruktor initialisiert das Änderungsprotokoll und schließt die Änderung ab. Wenn {@code commit} {@code true} ist, werden alle Änderungen
	 * angenommen. Andernfalls werden sie verworfen. */
	KBUpdate(KBBuffer buffer, boolean commit) {
		var backup = buffer.backup;
		if (backup != null) {
			if (commit) {
				this.oldState = new KBState(backup);
				this.newState = new KBState(buffer);
			} else {
				this.oldState = new KBState(buffer);
				this.newState = new KBState(backup);
				buffer.entityRef = backup.entityRef;
				buffer.rootRef = backup.rootRef;
				buffer.sourceMap = backup.sourceMap;
				buffer.targetMap = backup.targetMap;
				buffer.valueRefMap = backup.valueRefMap;
				buffer.valueStrMap = backup.valueStrMap;
			}
			buffer.backup = null;
			buffer.backupEdges = false;
			buffer.backupValues = false;
		} else {
			this.newState = this.oldState = new KBState(buffer);
		}
	}

}
