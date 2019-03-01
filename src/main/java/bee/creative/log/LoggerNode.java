package bee.creative.log;

import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Knoten im doppelt verketteten Ring der von einem {@link Logger} erfassten Zeilen.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class LoggerNode {

	/** Dieses Feld speichert den Zeilentext, Formattext oder {@code null}. */
	public Object text;

	/** Dieses Feld speichert die Zeilenteile, Formatargumente oder {@code null}. */
	public Object[] args;

	/** Dieses Feld speichert den vorherigen Knoten des Rings. */
	LoggerNode prev = this;

	/** Dieses Feld speichert den nächsten Knoten des Rings. */
	LoggerNode next = this;

	@SuppressWarnings ("javadoc")
	LoggerNode(final Object text, final Object[] args) {
		this.text = text;
		this.args = args;
	}

	/** Diese Methode {@link #insert(LoggerNode) fügt} einen neuen {@link LoggerNode} ein. */
	void pushLine(final Object text, final Object[] args) {
		this.insert(new LoggerNode(text, args));
	}

	/** Diese Methode {@link #insert(LoggerNode) fügt} einen neuen {@link LoggerNodeO} ein. */
	void pushOpen(final Object text, final Object[] args) {
		this.insert(new LoggerNodeO(text, args));
	}

	/** Diese Methode {@link #insert(LoggerNode) fügt} einen neuen {@link LoggerNodeC} ein. */
	void pushClose(final Object text, final Object[] args) {
		this.insert(new LoggerNodeC(text, args));
	}

	/** Diese Methode fügt den gegebenen Knoten als neuen {@link #prev} ein. */
	void insert(final LoggerNode node) {
		this.prev = ((node.prev = (node.next = this).prev).next = node);
	}

	/** Diese Methode entfernt diesen Knoten aus seinem bisherigen Ring und überführt ihn in einen Ring mit sich selbst. */
	void delete() {
		(this.prev.next = this.next).prev = this.prev;
		this.prev = (this.next = this);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein {@link LoggerNodeO} ist. */
	boolean isOpen() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein {@link LoggerNodeC} ist. */
	boolean isClose() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.text, this.args);
	}

}