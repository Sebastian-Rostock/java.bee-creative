package bee.creative.util;

import java.lang.ref.WeakReference;

/** Diese Schnittstelle definiert ein Ereignis, bei welchem Empfänger jederzeit {@link #put(Object) angemeldet}, {@link #pop(Object) abgemeldet} bzw.
 * {@link #fire(Object) benachrichtigt} werden können.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GMessage> Typ der Nachricht.
 * @param <GListener> Typ der Empfänger. Dieser darf kein {@code Object[]} sein. */
public interface Event<GMessage, GListener> {

	/** Diese Methode meldet den gegebenen Ereignisempfänger für dieses Ereignis an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} dieses Ereignisses informiert.
	 * Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden.
	 *
	 * @see Listeners#put(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GListener put(GListener listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für dieses Ereignis an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} dieses Ereignisses informiert.
	 * Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden. Der Empfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @see Listeners#putWeak(Object, Object)
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GListener putWeak(GListener listener) throws IllegalArgumentException;

	/** Diese Methode meldet den gegebenen Ereignisempfänger für dieses Ereignis ab und gibt {@code this} zurück. Wenn der Empfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object) Auslösen} dieses Ereignisses nicht mehr informiert.
	 *
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(GListener listener) throws IllegalArgumentException;

	/** Diese Methode löst dieses Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt angemeldeten Empfänger mit der gegebenen Nachricht und gibt letztere
	 * zurück. Sofern es die Empfänger es zulassen, kann die Nachricht {@code null} sein.
	 *
	 * @see Listeners#fire(Object, Object)
	 * @param message Nachricht oder {@code null}.
	 * @return Nachricht.
	 * @throws NullPointerException Wenn {@code message} {@code null} ist und die Empfänger dies nicht unterstützten. */
	public GMessage fire(GMessage message) throws NullPointerException;

}
