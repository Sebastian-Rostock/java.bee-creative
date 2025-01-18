package bee.creative.log;

import java.util.Iterator;
import bee.creative.lang.Objects;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Iterators;

/** Diese Klasse dient der Erfassung hierarchischer Protokollzeilen. Diese werden in der {@link #toString() Textdarstellung} innerhalb von Protokollebenen
 * entsprechend eingerückt dargestellt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGBuilder implements Iterable<LOGEntry> {

	public void setPrinter(LOGPrinter printer) throws NullPointerException {
		this.printer = Objects.notNull(printer);
	}

	/** Diese Methode öffnet eine neue Protokollebene, wodurch alle danach erfassten Protokollzeilen um eins weiter eingerückt werden. */
	public void enterScope() {
		this.enterScopeImpl(null, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @param text Kopfzeile.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void enterScope(Object text) throws NullPointerException {
		this.enterScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @param text Formattext der Kopfzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Kopfzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void enterScope(String text, Object... args) throws NullPointerException {
		this.enterScopeImpl(text, Objects.notNull(args));
	}

	/** Diese Methode verlässt die aktuelle Protokollebene, wodurch alle danach erfassten Protokollzeilen um eins weniger eingerückt werden. Sie entfernt die
	 * Protokollebene zusammen mit ihrer Kopfzeile nur dann, wenn in der Protokollebene keine weiteren Protokollzeilen {@link #pushEntry(Object) erfasst}
	 * wurden. */
	public void leaveScope() {
		this.leaveScopeImpl(null, null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen {@link #pushEntry(Object) erfasst} wurden.
	 *
	 * @param text Fußzeile.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void leaveScope(Object text) throws NullPointerException {
		this.leaveScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen {@link #pushEntry(Object) erfasst} wurden.
	 *
	 * @param text Formattext der Fußzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Fußzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void leaveScope(String text, Object... args) throws NullPointerException {
		this.leaveScopeImpl(text, Objects.notNull(args));
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene. Wenn das gegebene Objekt ein {@link LOGBuilder} ist, werden dessen
	 * Protokollzeilen eingebettet und über {@link #iterator()} an der entsprechenden Position geliefert. Wenn der gegebene {@link LOGBuilder} diesen einbettet,
	 * endet der {@link #iterator()} nicht!
	 *
	 * @param text Protokollzeile oder {@code null}.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void pushEntry(Object text) throws NullPointerException {
		this.pushEntryImpl(Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushEntry(String text, Object... args) throws NullPointerException {
		this.pushEntryImpl(text, Objects.notNull(args));
	}

	/** Diese Methode ist eine Abkürzung für {@link #pushEntry(Object) this.pushEntry(cause)}. */
	public void pushError(Throwable cause) throws NullPointerException {
		this.pushEntry(cause);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @param cause Fehlerursache.
	 * @param text Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code text} {@code null} ist. */
	public void pushError(Throwable cause, Object text) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @param cause Fehlerursache oder {@code null}.
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code args} {@code null} ist. */
	public void pushError(Throwable cause, String text, Object... args) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), text, Objects.notNull(args));
	}

	/** Diese Methode ist eine Abkürzung für {@link #pushEntry(Object) this.pushEntry(logger)}. */
	public void pushLogger(LOGBuilder logger) throws NullPointerException {
		this.pushEntry(logger);
	}

	/** Diese Methode entfernt alle bisher erfassten Protokollzeilen. */
	public void clear() {
		synchronized (this.head) {
			this.head.delete();
		}
	}

	public LOGPrinter printer() {
		return this.printer;
	}

	@Override
	public Iterator<LOGEntry> iterator() {
		return new ITER(this.head);
	}

	/** Diese Methode liefert nur dann {@code true}, wenn bisher keine verbleibenden Protokollzeilen erfasst wurden.
	 *
	 * @see #leaveScope()
	 * @see #leaveScope(Object)
	 * @see #leaveScope(String, Object...)
	 * @return {@code true}, wenn höchstens betretende Protokollzeilen erfasst wurden. */
	public boolean isClean() {
		for (var entry: this)
			if (entry.indent() <= 0) return false;
		return true;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn aktuell keine Protokollzeilen erfasst sind.
	 *
	 * @return {@code true}, wenn das Protokoll leer ist. */
	public boolean isEmpty() {
		return !this.iterator().hasNext();
	}

	/** Diese Methode liefert die Textdarstellung der erfassten Protokollzeilen. */
	@Override
	public String toString() {
		return this.printer.printAll(this);
	}

	/** Dieses Feld speichert den Ring der erfassten Protokollzeilen. */
	final LOGEntry head = new LOGEntry(this, null, null);

	LOGPrinter printer = new LOGPrinter();

	private void enterScopeImpl(Object text, Object[] args) {
		synchronized (this.head) {
			this.head.pushEnter(text, args);
		}
	}

	private void leaveScopeImpl(Object text, Object[] args) {
		synchronized (this.head) {
			var prev = this.head.prev;
			if (prev.indent() > 0) {
				prev.delete();
			} else {
				this.head.pushLeave(text, args);
			}
		}
	}

	private void pushEntryImpl(Object text, Object[] args) {
		synchronized (this.head) {
			if ((text == null) && (args == null)) return;
			this.head.pushEntry(text, args);
		}
	}

	private void pushErrorImpl(Throwable cause, Object text, Object[] args) {
		synchronized (this.head) {
			this.head.pushEnter(text, args);
			this.head.pushLeave(cause, null);
		}
	}

	private static final class ITER extends AbstractIterator<LOGEntry> {

		@Override
		public LOGEntry next() {
			var next = this.next;
			if (next == null) return super.next();
			this.next = this.seek();
			return next;
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		ITER(LOGEntry head) {
			this.prev = this.head = head;
			this.next = this.seek();
		}

		private final LOGEntry head;

		private LOGEntry prev;

		private LOGEntry next;

		private Iterator<LOGEntry> iter = Iterators.empty();

		private LOGEntry seek() {
			while (true) {
				if (this.iter.hasNext()) return this.iter.next();
				synchronized (this.head) {
					this.prev = this.prev.next;
					if (this.prev == this.head) return null;
					var text = this.prev.text;
					if (!(text instanceof LOGBuilder)) return this.prev;
					this.iter = ((LOGBuilder)text).iterator();
				}
			}
		}

	}

}
