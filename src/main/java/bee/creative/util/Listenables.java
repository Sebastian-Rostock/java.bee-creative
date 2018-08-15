package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import bee.creative.ref.PointerQueue;

/** Diese Klasse implementiert eine threadsichere Verwaltung von Ereignisempfängern, welche jederzeit {@link #put(Object, Object) angemeldet},
 * {@link #pop(Object, Object) abgemeldet} bzw. {@link #fire(Object, Object) benachrichtigt} werden können und bezüglich eines {@link WeakReference schwach}
 * referenzierten Ereignissenders gespeichert werden.
 * <p>
 * Die Empfänger können direkt oder {@link WeakReference schwach} referenziert werden, sodass für jeden angemeldeten Empfänger {@code 4} bzw. {@code 36} Byte
 * Verwaltungsdaten anfallen. Die Verwaltungsdaten je Sender betragen dagegen {@code 0}, {@code 48} oder {@code 60} Byte, wenn dafür keine, ein bzw. mehrere
 * Empfänger angemeldet sind.
 * <p>
 * Beim {@link #fire(Object, Object) Auslösen} eines Ereignisses wird eine gegebene Nachricht an alle zu diesem Zeitpunkt für einen gegebenen Sender
 * {@link #put(Object, Object) angemeldeten} Empfänger {@link #customFire(Object, Object, Object) gesendet}. Die Reihenfolge der Benachrichtigung der Empfänger
 * entsprichtz der Reihenfolge ihrer Anmeldung.
 * <p>
 * <pre>
 *
 * public interface MyListener {
 * 	public static class MyMessage { ... }
 * 	public void handleMyEvent(MyMessage event);
 * }
 *
 * public class MySender {
 *
 * 	public static final Listeners &lt;MyMessage, MyListener> MyCustomEventListeners = new Listeners<>(){
 * 		protected void customFire(Object sender, MyMessage event, MyListener listener) {
 * 			listener.handleMyEvent(event);
 *		}
 * 	};
 *
 * 	public Event&lt;MyMessage, MyListener> getMyCustomEventListeners() {
 * 		return MyCustomEventListeners.toEvent(this);
 * 	}
 *
 * }
 * </pre>
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GEvent> Typ der Nachricht.
 * @param <GListener> Typ der Empfänger. Dieser darf kein {@code Object[]} sein. */
public abstract class Listenables<GEvent, GListener> {

	/** Diese Klasse implementiert die Verwaltungsdaten eines Ereignissenders. Jede Instanz benötigt mindestens 24 Byte. */
	private static final class EventItem extends WeakReference<Object> {

		/** Dieses Feld speichert den Streuwert des Senders. */
		public final int hash;

		/** Dieses Feld speichert die angemeldeten Empfänger als {@link EventListener}, {@code GListener} oder {@code Object[]} solcher Empfänger sein. */
		public volatile Object listener;

		@SuppressWarnings ("javadoc")
		public EventItem(final Object sender, final ReferenceQueue<? super Object> queue) {
			super(sender, queue);
			this.hash = System.identityHashCode(sender);
		}

		/** Diese Methode meldet den gegebenen Empfänger an.
		 *
		 * @param listener Empfänger. */
		public void put(final Object listener) {
			final Object listener2 = this.listener;
			if (listener2 instanceof Object[]) {
				final Object[] array = (Object[])listener2;
				final int length = array.length;
				final Object[] array2 = new Object[length + 1];
				System.arraycopy(array, 0, array2, 0, length);
				array2[length] = listener;
				this.listener = array2;
			} else if (EventListener.get(listener2) != null) {
				this.listener = new Object[]{listener2, listener};
			} else {
				this.listener = listener;
			}
		}

		/** Diese Methode meldet den gegebenen Empfänger ab.
		 *
		 * @param listener Empfänger. */
		public void pop(final Object listener) {
			final Object listener2 = this.listener;
			if (listener2 instanceof Object[]) {
				final Object[] array = (Object[])listener2;
				final int length = array.length;
				int length2 = 0;
				for (int i = 0; i < length; i++) {
					final Object item = EventListener.get(array[i]);
					if ((item == null) || (item == listener)) {
						array[i] = null;
					} else {
						length2++;
					}
				}
				if (length2 == 0) {
					this.listener = null;
				} else if (length2 == 1) {
					for (int i = 0; i < length; i++) {
						final Object item = array[i];
						if (item != null) {
							this.listener = item;
							return;
						}
					}
					this.listener = null;
				} else {
					final Object[] array2 = new Object[length2];
					for (int i = 0, i2 = 0; i < length; i++) {
						final Object item = array[i];
						if (item != null) {
							array2[i2++] = item;
						}
					}
					this.listener = array2;
				}
			} else {
				final Object item = EventListener.get(listener2);
				if ((item == null) || (item == listener)) {
					this.listener = null;
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get(), this.listener);
		}

	}

	/** Diese Klasse implementiert die Verwaltung der Ereignissender und ihrer Ereignisempfänger. */
	@SuppressWarnings ("javadoc")
	private static final class EventStore extends HashSet<Object> {

		/** Dieses Feld speichert das serialVersionUID. */
		private static final long serialVersionUID = -427892278890180125L;

		/** Dieses Feld speichert den Ereignissender für {@code null}. */
		private static final Object NULL = new Object();

		/** Dieses Feld speichert den {@link ReferenceQueue} zur Erzeugung der {@link EventItem}. */
		public final EventQueue eventQueue = new EventQueue(this);

		public EventStore() {
		}

		private final Object toKey(final Object sender) {
			return sender != null ? sender : EventStore.NULL;
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, wird {@code null} geliefert.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten oder {@code null}. */
		public EventItem get(final Object sender) {
			final int entryIndex = this.getIndexImpl(this.toKey(sender));
			return entryIndex < 0 ? null : this.customGetKey(entryIndex);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, werden sie angelegt.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten. */
		public EventItem put(final Object sender) {
			return this.customGetKey(this.putIndexImpl(this.toKey(sender)));
		}

		/** Diese Methode entfernt die Verwaltungsdaten zum gegebenen Ereignissender.
		 *
		 * @param sender Ereignissender, {@link EventItem} oder {@code null}. */
		public void pop(final Object sender) {
			this.popIndexImpl(this.toKey(sender));
		}

		@Override
		protected EventItem customGetKey(final int entryIndex) {
			return (EventItem)super.customGetKey(entryIndex);
		}

		@Override
		protected void customSetKey(final int entryIndex, final Object item, final int itemHash) {
			super.customSetKey(entryIndex, item instanceof EventItem ? item : new EventItem(item, this.eventQueue), itemHash);
		}

		@Override
		protected int customHash(final Object item) {
			return item instanceof EventItem ? ((EventItem)item).hash : System.identityHashCode(item);
		}

		@Override
		protected int customHashKey(final int entryIndex) {
			return this.customGetKey(entryIndex).hash;
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item) {
			final EventItem key = this.customGetKey(entryIndex);
			return (key == item) || (key.get() == item);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item, final int keyHash) {
			final EventItem key = this.customGetKey(entryIndex);
			return (key.hash == keyHash) && ((key == item) || (key.get() == item));
		}

	}

	@SuppressWarnings ("javadoc")
	private static final class EventQueue extends PointerQueue<Object> {

		public final EventStore eventStore;

		public EventQueue(final EventStore eventStore) {
			this.eventStore = eventStore;
		}

		@Override
		protected void customRemove(final Reference<? extends Object> reference) {
			if (reference instanceof EventItem) {
				final EventStore eventStore = this.eventStore;
				synchronized (eventStore) {
					eventStore.pop(reference);
				}
			} else if (reference instanceof EventListener) {
				final EventListener eventListener = (EventListener)reference;
				final EventStore eventStore = eventListener.eventStore;
				synchronized (eventStore) {
					final EventItem eventItem = eventListener.eventItem;
					synchronized (eventItem) {
						eventItem.pop(eventListener);
						if (eventItem.listener != null) return;
						eventStore.pop(eventStore);
					}
				}
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this);
		}

	}

	@SuppressWarnings ("javadoc")
	private static final class EventListener extends WeakReference<Object> {

		public static Object get(final Object object) {
			return object instanceof EventListener ? ((EventListener)object).get() : object;
		}

		public EventItem eventItem;

		public EventStore eventStore;

		public EventListener(final Object referent, final EventItem eventItem, final EventStore eventStore) {
			super(referent, eventStore.eventQueue);
			this.eventItem = eventItem;
			this.eventStore = eventStore;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get());
		}

	}

	@SuppressWarnings ("javadoc")
	private final class EventListenable implements Listenable<GEvent, GListener> {

		public final Object sender;

		public EventListenable(final Object sender) {
			this.sender = sender;
		}

		@Override
		public GListener put(final GListener listener) {
			return Listenables.this.put(this.sender, listener);
		}

		@Override
		public GListener putWeak(final GListener listener) throws IllegalArgumentException {
			return Listenables.this.putWeak(this.sender, listener);
		}

		@Override
		public void pop(final GListener listener) {
			Listenables.this.pop(this.sender, listener);
		}

		@Override
		public GEvent fire(final GEvent event) {
			return Listenables.this.fire(this.sender, event);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sender);
		}

	}

	/** Dieses Feld speichert die {@link EventItem}. */
	private final EventStore eventStore = new EventStore();

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen
	 * informiert. Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden.
	 *
	 * @see Listenables#putWeak(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public final GListener put(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return null;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.put(sender);
			synchronized (eventItem) {
				eventItem.put(listener);
			}
		}
		return listener;
	}

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen
	 * informiert. Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden. Der Empfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public final GListener putWeak(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return null;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.put(sender);
			synchronized (eventItem) {
				eventItem.put(new EventListener(listener, eventItem, eventStore));
			}
		}
		return listener;
	}

	/** Diese Methode meldet den gegebenen Empfänger ab. Wenn der Empfänger {@code null} ist, wird er nicht abgemeldet. Andernfalls wird er beim zukünftigen
	 * {@link #fire(Object, Object) Auslösen} von Ereignissen nicht mehr informiert.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param listener Empfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public final void pop(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.get(sender);
			if (eventItem == null) return;
			synchronized (eventItem) {
				eventItem.pop(listener);
			}
		}
	}

	/** Diese Methode löst ein Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt für den gegebenen Ereignissender angemeldeten Ereignisempfänger mit der
	 * gegebenen Ereignisnachricht und gibt letztere zurück. Sofern die Empfänger es zulassen, kann die Nachricht {@code null} sein.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param event Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code event} {@code null} ist und die Empfänger dies nicht unterstützten. */
	public final GEvent fire(final Object sender, final GEvent event) throws NullPointerException {
		final Object listener;
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.get(sender);
			if (eventItem == null) return event;
			listener = eventItem.listener;
		}
		if (listener instanceof Object[]) {
			for (final Object item: (Object[])listener) {
				this.fireImpl(sender, item, event);
			}
		} else {
			this.fireImpl(sender, listener, event);
		}
		return event;
	}

	@SuppressWarnings ("javadoc")
	private final void fireImpl(final Object sender, final Object listener, final GEvent event) {
		@SuppressWarnings ("unchecked")
		final GListener listener2 = (GListener)EventListener.get(listener);
		if (listener2 == null) return;
		this.customFire(sender, event, listener2);
	}

	/** Diese Methode gibt das an den gegebenen Sender gebundene Ereignis zurück.
	 *
	 * @param sender Ereignissender.
	 * @return Ereignis. */
	public final Listenable<GEvent, GListener> toListenable(final Object sender) {
		return new EventListenable(sender);
	}

	/** Diese Methode wird durch {@link #fire(Object, Object)} aufgerufen und soll dem gegebenen Empfänger die gegebene Nachricht senden.
	 *
	 * @param sender Sender oder {@code null}.
	 * @param event Nachricht oder {@code null}.
	 * @param listener Empfänger. */
	protected abstract void customFire(Object sender, GEvent event, GListener listener);

}
