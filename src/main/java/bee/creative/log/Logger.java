package bee.creative.log;

import bee.creative.util.Objects;
import bee.creative.util.Strings;

/** Diese Klasse dient der Erfassung hierarchischer Protokollzeilen. Diese Meldungen werden in der {@link #toStrings() Textdarstellung} innerhalb von
 * Protokollebenen entsprechend eingerückt dargestellt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Logger {

	/** Dieses Feld speichert den Ring der erfassten Protokollzeilen. */
	final LoggerNode head = new LoggerNode(null, null);

	/** Diese Methode öffnet eine neue Protokollebene, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins weiter eingerückt werden.
	 *
	 * @see LoggerResult#toIndent(int) */
	public void openScope() {
		this.openScopeImpl(null, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Kopfzeile, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see LoggerResult#push(Object)
	 * @see LoggerResult#toIndent(int)
	 * @param text Kopfzeile oder {@code null}. */
	public void openScope(final Object text) {
		this.openScopeImpl(text, null);
	}

	/** Diese Methode öffnet eine neue Protokollebene mit der gegebenen Kopfzeile, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins
	 * weiter eingerückt werden.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @see LoggerResult#toIndent(int)
	 * @param text Formattext der Kopfzeile oder {@code null}.
	 * @param args Formatargumente oder Bausteine der Kopfzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void openScope(final String text, final Object... args) throws NullPointerException {
		this.openScopeImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void openScopeImpl(final Object text, final Object[] args) {
		this.head.pushOpen(text, args);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins weniger eingerückt werden.
	 * Sie entfernt diese Protokollebene nur dann, wenn sie leer ist, d.h. wenn in ihr keine Protokollzeilen erfasst wurden.
	 *
	 * @see LoggerResult#toIndent(int) */
	public void closeScope() {
		this.closeScopeImpl(null, null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Fußzeile, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt diese Protokollebene nur dann, wenn sie leer ist, d.h. wenn in ihr keine Protokollzeilen erfasst wurden.
	 *
	 * @see LoggerResult#push(Object)
	 * @see LoggerResult#toIndent(int)
	 * @param text Fußzeile oder {@code null}. */
	public void closeScope(final Object text) {
		this.closeScopeImpl(text, null);
	}

	/** Diese Methode verlässt die aktuelle Protokollebene mit der gegebenen Fußzeile, wodurch alle danach erfassten Protokollebenen und Protokollzeilen um eins
	 * weniger eingerückt werden. Sie entfernt diese Protokollebene nur dann, wenn sie leer ist, d.h. wenn in ihr keine Protokollzeilen erfasst wurden.
	 *
	 * @see LoggerResult#push(Object, Object[])
	 * @see LoggerResult#toIndent(int)
	 * @param text Formattext der Fußzeile oder {@code null}.
	 * @param args Formatargumente oder Bausteine der Fußzeile.
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
	 * @param args Formatargumente oder Bausteine der Protokollzeile.
	 * @throws NullPointerException Wenn {@code args} {@code null} ist. */
	public void pushEntry(final String text, final Object... args) throws NullPointerException {
		this.pushEntryImpl(text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushEntryImpl(final Object text, final Object[] args) {
		if ((text == null) && (args == null)) return;
		this.head.pushLine(text, args);
	}

	public void pushEntry(final Throwable cause, final Object text) {
		this.pushEntryImpl(cause, text, null);
	}

	public void pushEntry(final Throwable cause, final String text, final Object... args) throws NullPointerException {
		this.pushEntryImpl(cause, text, Objects.notNull(args));
	}

	@SuppressWarnings ("javadoc")
	void pushEntryImpl(final Throwable cause, final Object text, final Object[] args) {
		if (cause == null) {
			this.pushEntryImpl(text, args);
		} else if ((text == null) && (args == null)) {
			this.pushEntryImpl(cause, null);
		} else {
			this.openScopeImpl(text, args);
			this.head.pushClose(cause, null);
		}
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
		final LoggerResult result = new LoggerResult();
		this.toStrings(result);
		return result.get();
	}

	/** Diese Methode gibt die Textdarstellungen aller erfassten Protokollzeilen zurück. Diese werden über den gegebenen {@link LoggerResult} formatiert.
	 *
	 * @param result Generator der Textdarstellungen. */
	public void toStrings(final LoggerResult result) throws NullPointerException {
		Objects.notNull(result);
		final LoggerNode prev = this.head;
		for (LoggerNode next = prev.next; prev != next; next = next.next) {
			result.pushImpl(next);
		}
	}

}
