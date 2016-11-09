package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Strings;

/** Diese Klasse implementiert eine Ausnahme, an welche mehrere {@link #getMessages() Nachrichten} {@link #push(String) angefügt} werden können und welche mit
 * einem {@link #getValue() Wert} sowie einem {@link #getContext() Kontextobjekt} die {@link #getCause() Ursache} eines Ausnahmefalls genauer beschreiben
 * kann.<br>
 * Die von {@link #getMessage()} gelieferte Zeichenkette entspricht den mit dem Trennzeichen {@code '\n'} verketteten {@link #getMessages() Nachrichten}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMException extends RuntimeException implements Iterable<String> {

	@SuppressWarnings ("javadoc")
	private static final long serialVersionUID = -2654985371977072939L;

	{}

	/** Diese Methode gibt die gegebene Ausnahme als {@link FEMException} zurück.<br>
	 * Wenn {@code cause} eine {@link FEMException} ist, wird diese unverändert geliefert.<br>
	 * Andernfalls wird eine neue {@link FEMException} mit der gegebenen Ausnahme als {@link #useCause(Throwable) Ursache} erzeugt und geliefert.
	 *
	 * @param cause Ausnahme.
	 * @return {@link FEMException}. */
	public static FEMException from(final Throwable cause) {
		if (cause instanceof FEMException) return (FEMException)cause;
		return new FEMException().useCause(cause);
	}

	{}

	/** Dieses Feld speichert den Wert. */
	FEMValue _value_;

	/** Dieses Feld speichert die Ursache. */
	Throwable _cause_;

	/** Dieses Feld speichert den Kontextobjekt. */
	FEMContext _context_;

	/** Dieses Feld speichert die Nachrichten. */
	final List<String> _messages_;

	/** Dieser Konstruktor initialisiert eine neue Ausnahme ohne {@link #getValue() Wert}, {@link #getContext() Kontextobjekt}, {@link #getCause() Ursache} und
	 * {@link #getMessages() Nachrichten}. */
	public FEMException() {
		this._messages_ = Collections.synchronizedList(new ArrayList<String>());
	}

	{}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück.
	 *
	 * @see #getValue()
	 * @param value Wert oder {@code null}.
	 * @return {@code this}. */
	public final FEMException useValue(final FEMValue value) {
		this._value_ = value;
		return this;
	}

	/** Diese Methode setzt die {@link #getCause() Ursache} des Ausnahmefalls und gibt {@code this} zurück.
	 *
	 * @see #getCause()
	 * @param cause Ursache des Ausnahmefalls.
	 * @return {@code this}. */
	public final FEMException useCause(final Throwable cause) {
		this._cause_ = cause;
		return this;
	}

	/** Diese Methode setzt das {@link #getContext() Kontextobjekt} und gibt {@code this} zurück.
	 *
	 * @see #getContext()
	 * @param context Kontextobjekt oder {@code null}.
	 * @return {@code this}. */
	public final FEMException useContext(final FEMContext context) {
		this._context_ = context;
		return this;
	}

	/** Diese Methode gibt den Wert zurück, der die {@link #getCause() Ursache} des Ausnahmefalls genauer beschreibt.
	 *
	 * @return Wert oder {@code null}. */
	public final FEMValue getValue() {
		return this._value_;
	}

	/** Diese Methode gibt das Kontextobjekt zurück, das die {@link #getCause() Ursache} des Ausnahmefalls genauer beschreibt bzw. zur Umwandlung des
	 * {@link #getValue() Werts} eingesetzt werden kann.
	 *
	 * @return Kontextobjekt oder {@code null}. */
	public final FEMContext getContext() {
		return this._context_;
	}

	/** Diese Methode gibt eine unveränderliche Sicht auf die Nachrichten dieser Ausnahme zurück.
	 *
	 * @see #push(String)
	 * @see #clearMessages()
	 * @return Nachrichten. */
	public final List<String> getMessages() {
		return Collections.unmodifiableList(this._messages_);
	}

	/** Diese Methode entfernt alle {@link #getMessages() Nachrichten} dieser Ausnahme und gibt {@code this} zurück.
	 *
	 * @see #getMessages()
	 * @return {@code this}. */
	public final FEMException clearMessages() {
		this._messages_.clear();
		return this;
	}

	/** Diese Methode fügt die gegebene Nachricht an das Ende der {@link #getMessages() Nachrichten} dieser Ausnahme an und gibt {@code this} zurück.<br>
	 * Wenn die Nachricht {@code null} ist, wird sie ignoriert.
	 *
	 * @see List#add(Object)
	 * @param message Nachricht oder {@code null}.
	 * @return {@code this}. */
	public final FEMException push(final String message) {
		if (message == null) return this;
		this._messages_.add(message);
		return this;
	}

	/** Diese Methode {@link String#format(String, Object...) formatiert} die gegebene Nachricht, fügt sie {@link #push(String) hinzu} und gibt {@code this}
	 * zurück.<br>
	 * Wenn die Nachricht {@code null} ist, wird sie ignoriert.
	 *
	 * @see #push(String)
	 * @see String#format(String, Object...)
	 * @param format Nachricht oder {@code null}.
	 * @param args Formatargumente.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link String#format(String, Object...)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalFormatException Wenn {@link String#format(String, Object...)} eine entsprechende Ausnahme auslöst. */
	public final FEMException push(final String format, final Object... args) throws NullPointerException, IllegalFormatException {
		if (format == null) return this;
		return this.push(String.format(format, args));
	}

	/** Diese Methode fügt die gegebenen Nachrichten in der gegebenen Reihenfolge {@link #push(String) hinzu} gibt {@code this} zurück.<br>
	 * Wenn die Nachrichten {@code null} sind, werden sie ignoriert.
	 *
	 * @see #pushAll(Iterable)
	 * @see Arrays#asList(Object...)
	 * @param messages Nachrichten oder {@code null}.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code messages} {@code null} ist. */
	public final FEMException pushAll(final String... messages) {
		if (messages == null) return this;
		return this.pushAll(Arrays.asList(messages));
	}

	/** Diese Methode fügt die gegebenen Nachrichten {@link #push(String) hinzu} gibt {@code this} zurück.<br>
	 * Wenn die Nachrichten {@code null} sind, werden sie ignoriert.
	 *
	 * @see #getMessages()
	 * @see Iterables#appendAll(Collection, Iterable)
	 * @param messages Nachrichten oder {@code null}.
	 * @return {@code this}. */
	public final FEMException pushAll(final Iterable<String> messages) {
		if (messages == null) return this;
		Iterables.appendAll(this._messages_, Iterables.filteredIterable(Filters.nullFilter(), messages));
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final Iterator<String> iterator() {
		return this._messages_.iterator();
	}

	/** {@inheritDoc} */
	@Override
	public final synchronized Throwable getCause() {
		return this._cause_;
	}

	/** {@inheritDoc} */
	@Override
	public final String getMessage() {
		return Strings.join("\n", this._messages_);
	}

}
