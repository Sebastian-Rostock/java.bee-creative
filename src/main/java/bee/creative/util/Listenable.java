package bee.creative.util;

import java.lang.ref.WeakReference;

/** Diese Schnittstelle definiert eine Ereignisquelle, bei welcher Ereignisempfänger jederzeit {@link #put(Object) angemeldet}, {@link #pop(Object) abgemeldet}
 * bzw. {@link #fire(Object) benachrichtigt} werden können.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GEvent> Typ des Ereignisses bzw. der Ereignisnachricht.
 * @param <GListener> Typ der Ereignisempfänger. Dieser darf kein {@code Object[]} sein. */
public interface Listenable<GEvent, GListener> {

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück.<br>
	 * Wenn der Ereignisempfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses
	 * informiert. Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden.
	 *
	 * @see Listenables#put(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GListener put(GListener listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück.<br>
	 * Wenn der Ereignisempfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses
	 * informiert. Das mehrfache Anmelden des gleichen Ereignisempfängers sollte vermieden werden. Der Ereignisempfänger wird über eine {@link WeakReference}
	 * referenziert.
	 *
	 * @see Listenables#putWeak(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GListener putWeak(GListener listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis ab und gibt {@code this} zurück. Wenn der Ereignisempfänger {@code null} ist, wird
	 * er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses nicht mehr informiert.
	 *
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(GListener listener) throws IllegalArgumentException;

	/** Diese Methode löst das Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt angemeldeten Ereignisempfänger mit der gegebenen Ereignisnachricht und gibt
	 * letztere zurück.
	 *
	 * @see Listenables#fire(Object, Object)
	 * @param event Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code message} {@code null} ist und die Empfänger dies nicht unterstützten.
	 * @throws UnsupportedOperationException Wenn diese Methode nicht unterstützt ist. */
	public GEvent fire(GEvent event) throws NullPointerException, UnsupportedOperationException;

}
