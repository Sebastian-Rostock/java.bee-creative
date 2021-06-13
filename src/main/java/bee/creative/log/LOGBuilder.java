package bee.creative.log;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.lang.Objects;
import bee.creative.lang.Strings;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Iterators;

/** Diese Klasse dient der Erfassung hierarchischer Protokollzeilen. Diese werden in der {@link #toStrings() Textdarstellung} innerhalb von Protokollebenen
 * entsprechend eingerückt dargestellt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class LOGBuilder implements Iterable<LOGEntry> {

	private static final class Iter extends AbstractIterator<LOGEntry> {

		final LOGEntry head;

		LOGEntry next;

		Iterator<LOGEntry> iter = Iterators.empty();

		public Iter(final LOGEntry head) {
			this.next = this.head = head;
			this.next();
		}

		@Override
		public LOGEntry next() {
			while (true) {
				if (this.iter.hasNext()) return this.iter.next();
				final LOGEntry next = this.next;
				final Object text = (this.next = next.next).text;
				if (!(text instanceof LOGBuilder)) return next;
				this.iter = ((LOGBuilder)text).iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return (this.head != this.next) || this.iter.hasNext();
		}

	}

	/** Dieses Feld speichert den Ring der erfassten Protokollzeilen. */
	private final LOGEntry head = new LOGEntry(null, null);

	/** Diese Methode öffnet eine neue Protokollebene, wodurch alle danach erfassten Protokollzeilen um eins weiter eingerückt werden. */
	public void enterScope() {
		this.enterScopeImpl(null, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @param text Kopfzeile.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void enterScope(final Object text) throws NullPointerException {
		this.enterScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @param text Formattext der Kopfzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Kopfzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void enterScope(final String text, final Object... args) throws NullPointerException {
		this.enterScopeImpl(text, Objects.notNull(args));
	}

	private void enterScopeImpl(final Object text, final Object[] args) {
		this.head.pushEnter(text, args);
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
	public void leaveScope(final Object text) throws NullPointerException {
		this.leaveScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen {@link #pushEntry(Object) erfasst} wurden.
	 *
	 * @param text Formattext der Fußzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Fußzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void leaveScope(final String text, final Object... args) throws NullPointerException {
		this.leaveScopeImpl(text, Objects.notNull(args));
	}

	private void leaveScopeImpl(final Object text, final Object[] args) {
		final LOGEntry prev = this.head.prev;
		if (prev.indent() > 0) {
			prev.delete();
		} else {
			this.head.pushLeave(text, args);
		}
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene. Wenn das gegebene Objekt ein {@link LOGBuilder} ist, werden dessen
	 * Protokollzeilen eingebettet und über {@link #iterator()} an der entsprechenden Position geliefert.
	 *
	 * @param text Protokollzeile oder {@code null}.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void pushEntry(final Object text) throws NullPointerException {
		this.pushEntryImpl(Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushEntry(final String text, final Object... args) throws NullPointerException {
		this.pushEntryImpl(text, Objects.notNull(args));
	}

	private void pushEntryImpl(final Object text, final Object[] args) {
		if ((text == null) && (args == null)) return;
		this.head.pushEntry(text, args);
	}

	/** Diese Methode ist eine Abkürzung für {@link #pushEntry(Object) this.pushEntry(cause)}. */
	public void pushError(final Throwable cause) throws NullPointerException {
		this.pushEntry(cause);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @param cause Fehlerursache.
	 * @param text Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code text} {@code null} ist. */
	public void pushError(final Throwable cause, final Object text) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @param cause Fehlerursache oder {@code null}.
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code args} {@code null} ist. */
	public void pushError(final Throwable cause, final String text, final Object... args) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), text, Objects.notNull(args));
	}

	private void pushErrorImpl(final Throwable cause, final Object text, final Object[] args) {
		this.head.pushEnter(text, args);
		this.head.pushLeave(cause, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #pushEntry(Object) this.pushEntry(logger)}. */
	public void pushLogger(final LOGBuilder logger) throws NullPointerException {
		this.pushEntry(logger);
	}

	/** Diese Methode entfernt alle bisher erfassten Protokollzeilen. */
	public void clear() {
		this.head.delete();
	}

	@Override
	public Iterator<LOGEntry> iterator() {
		return new Iter(this.head);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn aktuell keine Protokollzeilen erfasst sind.
	 *
	 * @return {@code true}, wenn das Protokoll leer ist. */
	public boolean isEmpty() {
		return this.head == this.head.next;
	}

	/** Diese Methode gibt die Textdarstellung der erfassten Protokollzeilen zurück. Diese entsteht aus {@link #toStrings() Strings.join("\n",
	 * this.toStrings())}. */
	@Override
	public String toString() {
		return Strings.join("\n", Arrays.asList(this.toStrings()));
	}

	/** Diese Methode gibt die {@link LOGPrinter Textdarstellungen} aller erfassten Protokollzeilen zurück.
	 *
	 * @return Textdarstellungen. */
	public String[] toStrings() {
		return new LOGPrinter().get(this);
	}

}
