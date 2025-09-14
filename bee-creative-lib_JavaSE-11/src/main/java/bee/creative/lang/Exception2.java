package bee.creative.lang;

import static bee.creative.util.Iterators.iteratorFrom;
import static bee.creative.util.Iterators.iteratorFromArray;
import java.util.Arrays;
import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import bee.creative.util.Collections.ConcatList;
import bee.creative.util.Iterator2;
import bee.creative.util.List2;

/** Diese Klasse implementiert eine {@link RuntimeException}, an welche mehrere {@link #getMessages() Nachrichten} {@link #push(Object) angefügt} werden können
 * und welche als Behandelt markiert werden kann. Die von {@link #getMessage()} gelieferte Zeichenkette entspricht dann der mit dem Trennzeichen {@code '\n'}
 * verketteten {@link #getMessages() Nachrichten}.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Exception2 extends RuntimeException implements Iterable<String> {

	/** Diese Methode gibt die gegebene Ausnahme als {@link Exception2} zurück. Wenn {@code cause} eine {@link Exception2} ist, wird diese unverändert geliefert.
	 * Andernfalls wird eine neue {@link Exception2} mit der gegebenen Ausnahme als {@link #useCause(Throwable) Ursache} erzeugt und geliefert.
	 *
	 * @param cause Ausnahme.
	 * @return {@link Exception2}. */
	public static Exception2 from(Throwable cause) {
		if (cause instanceof Exception2) return (Exception2)cause;
		return new Exception2().useCause(cause);
	}

	/** Dieser Konstruktor initialisiert eine neue Ausnahme ohne {@link #getCause() Ursache} und {@link #getMessages() Nachrichten}. */
	public Exception2() {
	}

	@Override
	public synchronized Throwable getCause() {
		return this.cause;
	}

	/** Diese Methode setzt die {@link #getCause() Ursache} dieser Ausnahme und gibt {@code this} zurück.
	 *
	 * @see #getCause()
	 * @param cause Ursache der Ausnahme oder {@code null}.
	 * @return {@code this}. */
	public Exception2 useCause(Throwable cause) {
		this.cause = cause;
		return this;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn diese Ausnahme als behandelt markiert wurde.
	 *
	 * @return Behandlungsmarkierung. */
	public boolean isHandled() {
		return this.handled;
	}

	/** Diese Methode markiert diese Ausnahme als behandelt bzw. nicht behandelt und gibt {@code this} zurück.
	 *
	 * @param handled {@code true}, wenn diese Ausnahme behandelt wurde;<br>
	 *        {@code false}, wenn diese Ausnahme nicht behandelt wurde.
	 * @return {@code this}. */
	public Exception2 useHandled(boolean handled) {
		this.handled = handled;
		return this;
	}

	@Override
	public String getMessage() {
		return Strings.join("\n", this);
	}

	/** Diese Methode liefert eine unveränderliche Sicht auf die Nachrichten dieser Ausnahme.
	 *
	 * @see #push(Object)
	 * @return Nachrichten. */
	public List2<String> getMessages() {
		return new ConcatList<>(this.messages, Collections.emptyList(), true);
	}

	/** Diese Methode fügt die gegebene Nachricht an das Ende der {@link #getMessages() Nachrichten} dieser Ausnahme an und gibt {@code this} zurück. Wenn die
	 * Nachricht {@code null} ist, wird sie ignoriert. Die {@link Object#toString() Textdarstellung} der Nachricht wird nur bei Bedarf ermittelt, bspw. durch
	 * {@link #getMessage()}.
	 *
	 * @see List#add(Object)
	 * @param message Nachricht oder {@code null}.
	 * @return {@code this}. */
	public Exception2 push(Object message) {
		if (message == null) return this;
		this.messages.add(message.toString());
		return this;
	}

	/** Diese Methode {@link String#format(String, Object...) formatiert} die gegebene Nachricht, fügt sie {@link #push(Object) hinzu} und gibt {@code this}
	 * zurück. Wenn die Nachricht {@code null} ist, wird sie ignoriert.
	 *
	 * @see #push(Object)
	 * @see Strings#formatFuture(String, Object...)
	 * @param format Nachricht oder {@code null}.
	 * @param args Formatargumente.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link String#format(String, Object...)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalFormatException Wenn {@link String#format(String, Object...)} eine entsprechende Ausnahme auslöst. */
	public Exception2 push(String format, Object... args) throws NullPointerException, IllegalFormatException {
		return format != null ? this.push(Strings.formatFuture(format, args)) : this;
	}

	/** Diese Methode fügt die gegebenen Nachrichten in der gegebenen Reihenfolge {@link #push(Object) hinzu} gibt {@code this} zurück. Wenn die Nachrichten
	 * {@code null} sind, werden sie ignoriert.
	 *
	 * @see #pushAll(Iterable)
	 * @param messages Nachrichten oder {@code null}.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code messages} {@code null} ist. */
	public Exception2 pushAll(Object... messages) {
		return messages != null ? this.pushAll(iteratorFromArray(messages)) : this;
	}

	/** Diese Methode fügt die gegebenen Nachrichten {@link #push(Object) hinzu} gibt {@code this} zurück. Wenn die Nachrichten {@code null} sind, werden sie
	 * ignoriert.
	 *
	 * @see #getMessages()
	 * @param messages Nachrichten oder {@code null}.
	 * @return {@code this}. */
	public Exception2 pushAll(Iterable<?> messages) {
		if (messages == null) return this;
		messages.forEach(this::push);
		return this;
	}

	@Override
	public Iterator2<String> iterator() {
		return iteratorFrom(this.messages.iterator());
	}

	private static final long serialVersionUID = 856089094921728079L;

	private Throwable cause;

	private boolean handled;

	private final List<String> messages = Collections.synchronizedList(new LinkedList<>());

}
