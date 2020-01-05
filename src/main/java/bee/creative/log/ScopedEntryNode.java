package bee.creative.log;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Knoten im doppelt verketteten Ring der von einem {@link ScopedLogger} erfassten Zeilen.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class ScopedEntryNode {

	/** Dieses Feld speichert den Zeilentext, Formattext oder {@code null}. */
	public final Object text;

	/** Dieses Feld speichert die Zeilenteile, Formatargumente oder {@code null}. */
	public final Object[] args;

	/** Dieses Feld speichert den vorherigen Knoten des Rings. */
	ScopedEntryNode prev = this;

	/** Dieses Feld speichert den nächsten Knoten des Rings. */
	ScopedEntryNode next = this;

	public ScopedEntryNode(final Object text, final Object[] args) {
		this.text = text;
		this.args = args;
	}

	/** Diese Methode {@link #insert(ScopedEntryNode) fügt} einen neuen {@link ScopedEntryNode} ein. */
	public void pushEntry(final Object text, final Object[] args) {
		this.insert(new ScopedEntryNode(text, args));
	}

	/** Diese Methode {@link #insert(ScopedEntryNode) fügt} einen neuen {@link ScopedEnterNode} ein. */
	public void pushEnter(final Object text, final Object[] args) {
		this.insert(new ScopedEnterNode(text, args));
	}

	/** Diese Methode {@link #insert(ScopedEntryNode) fügt} einen neuen {@link ScopedLeaveNode} ein. */
	public void pushLeave(final Object text, final Object[] args) {
		this.insert(new ScopedLeaveNode(text, args));
	}

	/** Diese Methode fügt den gegebenen Knoten als neuen {@link #prev} ein. */
	public void insert(final ScopedEntryNode node) {
		this.prev = ((node.prev = (node.next = this).prev).next = node);
	}

	/** Diese Methode entfernt diesen Knoten aus seinem bisherigen Ring und überführt ihn in einen Ring mit sich selbst. */
	public void delete() {
		(this.prev.next = this.next).prev = this.prev;
		this.prev = (this.next = this);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} oder {@code +1} zurück, wenn dieses Objekt ein {@link ScopedLeaveNode}, {@link ScopedEntryNode} bzw.
	 * {@link ScopedEnterNode} ist. */
	public int indent() {
		return 0;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.text, this.args);
	}

}