package bee.creative.util;

import java.lang.ref.WeakReference;

/** Diese Klasse implementiert eine threadsichere Verwaltung von Ereignisempfängern, welche jederzeit {@link #put(Object) angemeldet}, {@link #pop(Object)
 * abgemeldet} bzw. {@link #fire(Object) benachrichtigt} werden können. Beim {@link #fire(Object) Auslösen} eines Ereignisses werden alle zu diesem Zeitpunkt
 * {@link #put(Object) angemeldeten} Empfänger mit einer gegebenen Nachricht {@link #customFire(Object, Object) informiert}. Die Empfänger werden in verketteten
 * Listen über {@link WeakReference} referenziert, sodass für jeden registrierten Empfänger ca. 32 Byte Verwaltungsdaten anfallen. Die Bereinigung verwaister
 * Empfänger erfolgt automatisch beim {@link #fire(Object) Auslösen} eines Ereignisses und kann auch {@link #clean() vorsätzlich} gestaretet werden. Die
 * Reihenfolge der Benachrichtigung der Empfänger erfolgt LIFO, d.h. umgekehrt zur Reihenfolge ihrer Anmeldung.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GHandler> Typ der Empfänger.
 * @param <GMessage> Typ der Nachricht. */
public abstract class Event<GHandler, GMessage> implements Consumer<GMessage>, Iterable<GHandler> {

	@SuppressWarnings ("javadoc")
	private static final class Entry<GHandler> extends WeakReference<GHandler> {

		/** Dieses Feld speichert den nächsten Eintrag in der über {@link Event#put(Object)} und {@link Event#pop(Object)} modifizierten Liste. */
		public Entry<GHandler> nextF;

		/** Dieses Feld speichert den nächsten Eintrag in der über {@link Event#fire(Object)} iterierten Liste. */
		public Entry<GHandler> nextP;

		public Entry(final GHandler handler) {
			super(handler);
		}

	}

	/** Dieses Feld speichert die Liste der Empfänger sowie das Mutex für {@link Entry#nextF}. */
	private final Entry<GHandler> root = new Entry<>(null);

	/** Dieses Feld speichert das Mutex für {@link Entry#nextP}. */
	private final Object lock = new Object();

	{}

	/** Diese Methode meldet den gegebenen Empfänger an und gibt {@code this} zurück. Wenn der Empfänger {@code null} ist, wird er nicht angemeldet. Andernfalls
	 * wird er beim zukünftigen {@link #fire(Object) Auslösen} von Ereignissen informiert. Mehrfache Anmeldungen des gleichen Empfängers ist möglich, sollte aber
	 * vermieden werden.
	 *
	 * @see #pop(Object)
	 * @param handler Empfänger oder {@code null}.
	 * @return this. */
	public final Event<GHandler, GMessage> put(final GHandler handler) {
		if (handler == null) return this;
		synchronized (this.root) {
			final Entry<GHandler> next = new Entry<>(handler);
			next.nextF = this.root.nextF;
			this.root.nextF = next;
		}
		return this;
	}

	/** Diese Methode meldet den gegebenen Empfänger ab und gibt {@code this} zurück. Wenn der Empfänger {@code null} ist, wird er nicht abgemeldet. Andernfalls
	 * wird er beim zukünftigen {@link #fire(Object) Auslösen} von Ereignissen nicht mehr informiert.
	 *
	 * @see #put(Object)
	 * @param handler Empfänger oder {@code null}.
	 * @return this. */
	public final Event<GHandler, GMessage> pop(final GHandler handler) {
		if (handler == null) return this;
		synchronized (this.root) {
			Entry<GHandler> prev = this.root, item = prev.nextF;
			while (item != null) {
				final GHandler thisHandler = item.get();
				final Entry<GHandler> next = item.nextF;
				if ((thisHandler == null) || this.customEquals(thisHandler, handler)) {
					prev.nextF = next;
				} else {
					prev = item;
				}
				item = next;
			}
		}
		return this;
	}

	/** Diese Methode benachrichtigt alle {@link #put(Object) angemeldeten} Empfänger mit der gegebenen Nachricht und gibt {@code this} zurück.
	 *
	 * @see #put(Object)
	 * @see #pop(Object)
	 * @param message Nachricht oder {@code null}.
	 * @return {@code this}. */
	public final Event<GHandler, GMessage> fire(final GMessage message) {
		synchronized (this.lock) {
			this.clean();
			for (Entry<GHandler> item = this.root.nextP; item != null; item = item.nextP) {
				final GHandler handler = item.get();
				if (handler != null) {
					this.customFire(handler, message);
				}
			}
		}
		return this;
	}

	/** Diese Methode entfernt verwaiste Empfänger und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public final Event<GHandler, GMessage> clean() {
		synchronized (this.lock) {
			synchronized (this.root) {
				Entry<GHandler> prev = this.root, item = prev.nextF;
				while (item != null) {
					item = item.get() != null ? (prev = (prev.nextP = item)).nextF : (prev.nextF = item.nextF);
				}
				prev.nextP = item;
			}
		}
		return this;
	}

	/** Diese Methode wird durch {@link #fire(Object)} aufgerufen und soll den gegebenen Empfänger über die gegebene Nachricht informieren.
	 *
	 * @see #fire(Object)
	 * @param handler Empfänger.
	 * @param message Nachricht oder {@code null}. */
	protected abstract void customFire(GHandler handler, GMessage message);

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Empfänger gleich sind. Sie wird in {@link #pop(Object)} zur Erkennung des
	 * abzumeldenden Empfängers verwendet. In {@link Event} erfolgt der Abgleich über Identität.
	 *
	 * @param thisHandler angemeldeter Empfänger.
	 * @param thatHandler abzumeldender Empfänger.
	 * @return Äquivalenz oder Identität der Empfänger. */
	protected boolean customEquals(final GHandler thisHandler, final GHandler thatHandler) {
		return thisHandler == thatHandler;
	}

}
