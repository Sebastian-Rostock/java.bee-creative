package bee.creative.bind;

import java.lang.ref.WeakReference;

/** Diese Schnittstelle definiert eine eeobachtbares Objekt als Ereignisquelle, bei welcher Ereignisempfänger jederzeit {@link #put(Object) angemeldet},
 * {@link #pop(Object) abgemeldet} bzw. {@link #fire(Object) benachrichtigt} werden können.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GMessage> Typ des Ereignisses bzw. der Ereignisnachricht.
 * @param <GObserver> Typ der Ereignisempfänger. Dieser darf kein {@code Object[]} sein. */
public interface Observable<GMessage, GObserver> {

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück. Wenn der Ereignisempfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses informiert. Das mehrfache Anmelden des gleichen Empfängers
	 * sollte vermieden werden.
	 *
	 * @see Event#put(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GObserver put(GObserver listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück. Wenn der Ereignisempfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses informiert. Das mehrfache Anmelden des gleichen
	 * Ereignisempfängers sollte vermieden werden. Der Ereignisempfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @see Event#putWeak(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GObserver putWeak(GObserver listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis ab und gibt {@code this} zurück. Wenn der Ereignisempfänger {@code null} ist, wird
	 * er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses nicht mehr informiert.
	 *
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(GObserver listener) throws IllegalArgumentException;

	/** Diese Methode löst das Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt angemeldeten Ereignisempfänger mit der gegebenen Ereignisnachricht und gibt
	 * letztere zurück.
	 *
	 * @see Event#fire(Object, Object)
	 * @param message Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code message} {@code null} ist und die Empfänger dies nicht unterstützten.
	 * @throws UnsupportedOperationException Wenn diese Methode nicht unterstützt ist. */
	public GMessage fire(GMessage message) throws NullPointerException, UnsupportedOperationException;

}
