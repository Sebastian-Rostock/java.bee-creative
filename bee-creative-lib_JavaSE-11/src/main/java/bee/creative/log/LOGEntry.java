package bee.creative.log;

/** Diese Klasse implementiert die Protokollzeile eines {@link LOGBuilder} als Grundlage für einen Zeilentext.
 * <ul>
 * <li>Wenn das {@link #text()} einen {@link LOGBuilder} liefert, werden dessen Protokollzeilen rekursiv iteriert. Die Rekursion ist nicht gegen Endlosschleifen
 * abgesichert.</li>
 * <li>Wenn {@link #args()} {@code null} liefert, ergibt sich der Zeilentext aus der Textdarstellung von {@link #text()}. <br>
 * Solche Protokollzeilen werden durch {@link LOGBuilder#enterScope(Object)}, {@link LOGBuilder#pushEntry(Object)}, und {@link LOGBuilder#leaveScope(Object)}
 * bereitgestellt.</li>
 * <li>Wenn das {@link #text()} {@code null} liefert, ergibt sich die der Zeilentext aus der Verkettung der Textdarstellungen von {@link #args()}.<br>
 * Solche Protokollzeilen können durch {@link LOGBuilder#enterScope(String, Object...)}, {@link LOGBuilder#pushEntry(String, Object...)},
 * {@link LOGBuilder#pushError(Throwable, String, Object...)} und {@link LOGBuilder#leaveScope(String, Object...)} erzeugt werden, wenn als Formattext
 * {@code null} eingesetzt wird.</li>
 * <li>Andernfalls ergibt sich der Zeilentext über {@link String#format(String, Object...)} aus der Textdarstellung des Formattexts {@link #text()} und den
 * Formatargumenten {@link #args()}.</li>
 * </ul>
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGEntry {

	/** Diese Methode gibt Zeilentext, Formattext oder {@code null} zurück.
	 *
	 * @return Objekt der Protokollzeile. */
	public Object text() {
		return this.text;
	}

	/** Diese Methode gibt die Zeilenteile, Formatargumente oder {@code null} zurück.
	 *
	 * @return Objektliste der Protokollzeile. */
	public Object[] args() {
		return this.args;
	}

	/** Diese Methode gibt das Inkrement der Einrückung der der dieser Protokollzeile folgenden Protokollzeilen zurück.
	 *
	 * @return Inkrement der Einrückung ({@code -1}, {@code 0} oder {@code +1}). */
	public int indent() {
		return 0;
	}

	@Override
	public String toString() {
		return this.owner.printer.toString(this.text, this.args);
	}

	/** Dieses Feld speichert den {@link LOGBuilder}, Zeilentext, Formattext oder {@code null}. */
	final Object text;

	/** Dieses Feld speichert die Zeilenteile, Formatargumente oder {@code null}. */
	final Object[] args;

	/** Dieses Feld speichert den Besitzer mit dem {@link LOGPrinter}. */
	final LOGBuilder owner;

	/** Dieses Feld speichert den vorherigen Knoten des Rings. */
	LOGEntry prev = this;

	/** Dieses Feld speichert den nächsten Knoten des Rings. */
	LOGEntry next = this;

	LOGEntry(LOGBuilder owner, Object text, Object[] args) {
		this.text = text;
		this.args = args;
		this.owner = owner;
	}

	/** Diese Methode {@link #insert(LOGEntry) fügt} einen neuen {@link LOGEntry} ein. */
	void pushEntry(Object text, Object[] args) {
		this.insert(new LOGEntry(this.owner, text, args));
	}

	/** Diese Methode {@link #insert(LOGEntry) fügt} einen neuen {@link LOGEnter} ein. */
	void pushEnter(Object text, Object[] args) {
		this.insert(new LOGEnter(this.owner, text, args));
	}

	/** Diese Methode {@link #insert(LOGEntry) fügt} einen neuen {@link LOGLeave} ein. */
	void pushLeave(Object text, Object[] args) {
		this.insert(new LOGLeave(this.owner, text, args));
	}

	/** Diese Methode fügt den gegebenen Knoten als neuen {@link #prev} ein. */
	void insert(LOGEntry node) {
		this.prev = ((node.prev = (node.next = this).prev).next = node);
	}

	/** Diese Methode entfernt diesen Knoten aus seinem bisherigen Ring und überführt ihn in einen Ring mit sich selbst. */
	void delete() {
		(this.prev.next = this.next).prev = this.prev;
		this.prev = (this.next = this);
	}

}