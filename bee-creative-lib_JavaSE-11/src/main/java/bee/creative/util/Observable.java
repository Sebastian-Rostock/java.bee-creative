package bee.creative.util;

import java.lang.ref.WeakReference;

/** Diese Schnittstelle definiert ein beobachtbares Objekt als Ereignisquelle, bei welcher Ereignisempfänger jederzeit {@link #put(Object) angemeldet},
 * {@link #pop(Object) abgemeldet} bzw. {@link #fire(Object) benachrichtigt} werden können.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <M> Typ des Ereignisses bzw. der Ereignisnachricht.
 * @param <O> Typ der Ereignisempfänger. Dieser darf kein {@code Object[]} sein. */
public interface Observable<M, O> {

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück. Wenn der Ereignisempfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses informiert. Das mehrfache Anmelden des gleichen Empfängers
	 * sollte vermieden werden.
	 *
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public <O2 extends O> O2 put(O2 observer) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis an und gibt ihn zurück. Wenn der Ereignisempfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses informiert. Das mehrfache Anmelden des gleichen
	 * Ereignisempfängers sollte vermieden werden. Der Ereignisempfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public <O2 extends O> O2 putWeak(O2 observer) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für das Ereignis ab. Wenn der Ereignisempfänger {@code null} ist, wird er ignoriert. Andernfalls wird
	 * er beim zukünftigen {@link #fire(Object) Auslösen} des Ereignisses nicht mehr informiert.
	 *
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(Object observer) throws IllegalArgumentException;

	/** Diese Methode löst das Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt angemeldeten Ereignisempfänger mit der gegebenen Ereignisnachricht und gibt
	 * letztere zurück.
	 *
	 * @param message Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code message} {@code null} ist und die Empfänger dies nicht unterstützten.
	 * @throws UnsupportedOperationException Wenn diese Methode nicht unterstützt ist. */
	public M fire(M message) throws NullPointerException, UnsupportedOperationException;

}
