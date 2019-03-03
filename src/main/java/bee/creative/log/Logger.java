package bee.creative.log;

import bee.creative.util.Objects;
import bee.creative.util.Strings;

/** Diese Klasse dient der Erfassung hierarchischer Protokollzeilen. Diese werden in der {@link #toStrings() Textdarstellung} innerhalb von Protokollebenen
 * entsprechend eingerückt dargestellt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Logger {

	/** Dieses Feld speichert den Ring der erfassten Protokollzeilen. */
	private final LoggerNode head = new LoggerNode(null, null);

	/** Diese Methode ist eine Abkürzung für {@link #openScope(Object) this.openScope(null)}. */
	public void openScope() {
		this.openScope(null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see LoggerResult#push(Object)
	 * @param text Kopfzeile oder {@code null}. */
	public void openScope(final Object text) {
		this.openScopeImpl(text, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Protokollzeilen als Kopfzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @param text Formattext der Kopfzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Kopfzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void openScope(final String text, final Object... args) throws NullPointerException {
		this.openScopeImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void openScopeImpl(final Object text, final Object[] args) {
		this.head.pushOpen(text, args);
	}

	/** Diese Methode ist eine Abkürzung für {@link #closeScope(Object) this.closeScope(null)}. */
	public void closeScope() {
		this.closeScope(null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen erfasst wurden.
	 *
	 * @see LoggerResult#push(Object)
	 * @param text Fußzeile oder {@code null}. */
	public void closeScope(final Object text) {
		this.closeScopeImpl(text, null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Protokollzeilen als Fußzeile, wodurch alle danach erfassten Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt die Protokollebene zusammen mit ihrer Kopf- und Fußzeile nur dann, wenn in der Protokollebene keine weiteren
	 * Protokollzeilen erfasst wurden.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @param text Formattext der Fußzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Fußzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void closeScope(final String text, final Object... args) throws NullPointerException {
		this.closeScopeImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void closeScopeImpl(final Object text, final Object[] args) {
		final LoggerNode prev = this.head.prev;
		if (prev.isOpen()) {
			prev.delete();
		} else {
			this.head.pushClose(text, args);
		}
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @see LoggerResult#push(Object)
	 * @param text Protokollzeile oder {@code null}. */
	public void pushEntry(final Object text) {
		this.pushEntryImpl(text, null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushEntry(final String text, final Object... args) throws NullPointerException {
		this.pushEntryImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushEntryImpl(final Object text, final Object[] args) {
		if ((text == null) && (args == null)) return;
		this.head.pushLine(text, args);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @see LoggerResult#push(Object)
	 * @param cause Fehlerursache oder {@code null}.
	 * @param text Protokollzeile oder {@code null}. */
	public void pushError(final Throwable cause, final Object text) {
		this.pushErrorImpl(cause, text, null);
	}

	/** Diese Methode erfasst die gegebene Protokollzeile in der aktuellen Protokollebene als Kopfzeile der gegebenen Fehlerursache. Die Protokollzeile zur
	 * Fehlerursache wird danach um eins weiter eingerückt über {@link #pushEntry(Object)} erfasst.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @param cause Fehlerursache oder {@code null}.
	 * @param text Formattext der Protokollzeile oder {@code null}.
	 * @param args Formatargumente oder Textbausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushError(final Throwable cause, final String text, final Object... args) throws NullPointerException {
		this.pushErrorImpl(cause, text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushErrorImpl(final Throwable cause, final Object text, final Object[] args) {
		if (cause == null) {
			this.pushEntryImpl(text, args);
		} else if ((text == null) && (args == null)) {
			this.pushEntryImpl(cause, null);
		} else {
			this.head.pushOpen(text, args);
			this.head.pushClose(cause, null);
		}
	}

	/** Diese Methode erfasst die gegebenen Protokollzeilen in der aktuellen Protokollebene.
	 *
	 * @param logger Protokollzeilen oder {@code null}. */
	public void pushLogger(final Logger logger) {
		this.pushEntryImpl(logger, null);
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

	/** Diese Methode gibt die Textdarstellungen aller erfassten Protokollzeilen zurück. Diese werden über ein neues {@link LoggerResult} formatiert.
	 *
	 * @see #toStrings(LoggerResult)
	 * @return Textdarstellungen. */
	public String[] toStrings() {
		return this.toStrings(new LoggerResult());
	}

	/** Diese Methode gibt die Textdarstellungen aller erfassten Protokollzeilen zurück. Diese werden über den gegebenen {@link LoggerResult} formatiert.
	 *
	 * @param result Generator der Textdarstellungen.
	 * @return Textdarstellungen.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
	public String[] toStrings(final LoggerResult result) throws NullPointerException {
		toStringImpl(result);
		return result.get();
	}

	@SuppressWarnings ("javadoc")
	void toStringImpl(final LoggerResult result) throws NullPointerException {
		final LoggerNode prev = this.head;
		for (LoggerNode next = prev.next; prev != next; next = next.next) {
			result.pushImpl(next);
		}
	}

}
