package bee.creative.log;

import bee.creative.util.Objects;
import bee.creative.util.Strings;

/** Diese Klasse dient der Erfassung hierarchischer Protokollzeilen. Diese werden in der {@link #toStrings() Textdarstellung} innerhalb von Protokollebenen
 * entsprechend eingerückt dargestellt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ScopedLogger {

	/** Dieses Feld speichert den Ring der erfassten Protokollzeilen. */
	private final ScopedEntryNode head = new ScopedEntryNode(null, null);

	/** Diese Methode öffnet eine neue Protokollebene, wodurch alle danach erfassten Protokollzeilen um eins weiter eingerückt werden. */
	public void enterScope() {
		this.enterScopeImpl(null, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see ScopedLoggerStrings#push(Object)
	 * @param text Kopfzeile.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void enterScope(final Object text) throws NullPointerException {
		this.enterScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see ScopedLoggerStrings#push(Object, Object[])
	 * @param text Formattext der Kopfzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Kopfzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void enterScope(final String text, final Object... args) throws NullPointerException {
		this.enterScopeImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void enterScopeImpl(final Object text, final Object[] args) {
		this.head.pushEnter(text, args);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene, wodurch alle danach erfassten Protokollzeilen um eins weniger eingerückt werden. Sie entfernt die
	 * Protokollebene zusammen mit ihrer Kopfzeile nur dann, wenn in der Protokollebene keine weiteren Protokollzeilen erfasst wurden. */
	public void leaveScope() {
		this.leaveScopeImpl(null, null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen erfasst wurden.
	 *
	 * @see ScopedLoggerStrings#push(Object)
	 * @param text Fußzeile.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void leaveScope(final Object text) throws NullPointerException {
		this.leaveScopeImpl(Objects.notNull(text), null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen erfasst wurden.
	 *
	 * @see ScopedLoggerStrings#push(Object, Object[])
	 * @param text Formattext der Fußzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Fußzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void leaveScope(final String text, final Object... args) throws NullPointerException {
		this.leaveScopeImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void leaveScopeImpl(final Object text, final Object[] args) {
		final ScopedEntryNode prev = this.head.prev;
		if (prev.indent() > 0) {
			prev.delete();
		} else {
			this.head.pushLeave(text, args);
		}
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @see ScopedLoggerStrings#push(Object)
	 * @param text Protokollzeile oder {@code null}.
	 * @throws NullPointerException Wenn {@code text} {@code null} ist. */
	public void pushEntry(final Object text) throws NullPointerException {
		this.pushEntryImpl(Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @see ScopedLoggerStrings#push(Object, Object[])
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushEntry(final String text, final Object... args) throws NullPointerException {
		this.pushEntryImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushEntryImpl(final Object text, final Object[] args) {
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
	 * @see ScopedLoggerStrings#push(Object)
	 * @param cause Fehlerursache.
	 * @param text Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code text} {@code null} ist. */
	public void pushError(final Throwable cause, final Object text) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), Objects.notNull(text), null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @see ScopedLoggerStrings#push(Object, Object[])
	 * @param cause Fehlerursache oder {@code null}.
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code cause} bzw. {@code args} {@code null} ist. */
	public void pushError(final Throwable cause, final String text, final Object... args) throws NullPointerException {
		this.pushErrorImpl(Objects.notNull(cause), text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushErrorImpl(final Throwable cause, final Object text, final Object[] args) {
		if (cause == null) {
			this.pushEntryImpl(text, args);
		} else if ((text == null) && (args == null)) {
			this.head.pushEntry(cause, null);
		} else {
			this.head.pushEnter(text, args);
			this.head.pushLeave(cause, null);
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #pushEntry(Object) this.pushEntry(logger)}. */
	public void pushLogger(final ScopedLogger logger) throws NullPointerException {
		this.pushEntry(logger);
	}

	/** Diese Methode entfernt alle bisher erfassten Protokollzeilen. */
	public void clear() {
		this.head.delete();
	}

	/** Diese Methode gibt die Textdarstellung der erfassten Protokollzeilen zurück. Diese entsteht aus {@link Strings#join(String, Object...) Strings.join("\n",
	 * this.toStrings())}.
	 *
	 * @see #toStrings() */
	@Override
	public String toString() {
		return Strings.join("\n", (Object[])this.toStrings());
	}

	/** Diese Methode gibt die Textdarstellungen aller erfassten Protokollzeilen zurück. Diese werden über ein neues {@link ScopedLoggerStrings} formatiert.
	 *
	 * @see #toStrings(ScopedLoggerStrings)
	 * @return Textdarstellungen. */
	public String[] toStrings() {
		return this.toStrings(new ScopedLoggerStrings());
	}

	/** Diese Methode gibt die Textdarstellungen aller erfassten Protokollzeilen zurück. Diese werden über den gegebenen {@link ScopedLoggerStrings} formatiert.
	 *
	 * @param result Generator der Textdarstellungen.
	 * @return Textdarstellungen.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
	public String[] toStrings(final ScopedLoggerStrings result) throws NullPointerException {
		this.toStringImpl(result);
		return result.get();
	}

	@SuppressWarnings ("javadoc")
	void toStringImpl(final ScopedLoggerStrings result) throws NullPointerException {
		final ScopedEntryNode prev = this.head;
		for (ScopedEntryNode next = prev.next; prev != next; next = next.next) {
			result.pushImpl(next);
		}
	}

}
