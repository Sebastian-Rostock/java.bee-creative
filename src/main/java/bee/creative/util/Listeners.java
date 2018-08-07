package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert eine threadsichere Verwaltung von Ereignisempfängern, welche jederzeit {@link #put(Object, Object) angemeldet},
 * {@link #pop(Object, Object) abgemeldet} bzw. {@link #fire(Object, Object) benachrichtigt} werden können und bezüglich eines {@link WeakReference schwach}
 * referenzierten Ereignissenders gespeichert werden. Wenn der Speicher des Senders frei gegeben wird, werden die für diesen angemeldeten Empfänger automatisch
 * abgemeldet.
 * <p>
 * Die Empfänger können direkt oder {@link WeakReference schwach} referenziert werden, sodass für jeden angemeldeten Empfänger 4 bzw. 24 Byte Verwaltungsdaten
 * anfallen. Die Verwaltungsdaten je Sender betragen dagegen 32 oder 44 Byte, wenn dafür ein bzw. mehrere Empfänger angemeldet sind.
 * <p>
 * Beim {@link #fire(Object, Object) Auslösen} eines Ereignisses wird eine gegebene Nachricht an alle zu diesem Zeitpunkt für einen gegebenen Sender
 * {@link #put(Object, Object) angemeldeten} Empfänger {@link #customFire(Object, Object, Object) gesendet}. Die Reihenfolge der Benachrichtigung der Empfänger
 * entsprichtz der Reihenfolge ihrer Anmeldung.
 * <p>
 * TODO nutzungsbeispiel
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GListener> Typ der Empfänger. Dieser darf kein {@code Object[]} sein.
 * @param <GMessage> Typ der Nachricht. */
public abstract class Listeners<GListener, GMessage> implements Consumer<GMessage>, Iterable<GListener> {

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

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get(), this.listener);
		}

	}

	/** Diese Klasse implementiert die Verwaltung der Ereignissender und ihrer Ereignisempfänger. */
	private static final class EventStore extends HashSet<Object> {

		/** Dieses Feld speichert das serialVersionUID. */
		private static final long serialVersionUID = -427892278890180125L;

		/** Dieses Feld speichert den Ereignissender für {@code null}. */
		private static final Object NULL = new Object();

		/** Dieses Feld speichert den {@link ReferenceQueue} zur Erzeugung der {@link EventItem}. */
		public final ReferenceQueue<Object> eventQueue = new ReferenceQueue2<Object>() {

			@Override
			protected void customRemove(final Reference<? extends Object> reference) {
				if (reference instanceof EventItem) {
					EventStore.this.popItem(reference);
				} else if (reference instanceof EventListener) {
					// TODO
				}
			}

		};

		@SuppressWarnings ("javadoc")
		public EventStore() {
		}

		@SuppressWarnings ("javadoc")
		private final Object toKey(final Object sender) {
			return sender != null ? sender : EventStore.NULL;
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, wird {@code null} geliefert.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten oder {@code null}. */
		public EventItem getItem(final Object sender) {
			final int entryIndex = this.getIndexImpl(this.toKey(sender));
			return entryIndex < 0 ? null : this.customGetKey(entryIndex);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, werden sie angelegt.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten. */
		public EventItem putItem(final Object sender) {
			return this.customGetKey(this.putIndexImpl(this.toKey(sender)));
		}

		/** Diese Methode entfernt die Verwaltungsdaten zum gegebenen Ereignissender.
		 *
		 * @param sender Ereignissender, {@link EventItem} oder {@code null}. */
		public void popItem(final Object sender) {
			this.popIndexImpl(this.toKey(sender));
		}

		/** {@inheritDoc} */
		@Override
		protected EventItem customGetKey(final int entryIndex) {
			return (EventItem)super.customGetKey(entryIndex);
		}

		/** {@inheritDoc} */
		@Override
		protected void customSetKey(final int entryIndex, final Object item, final int itemHash) {
			super.customSetKey(entryIndex, item instanceof EventItem ? item : new EventItem(item, this.eventQueue), itemHash);
		}

		/** {@inheritDoc} */
		@Override
		protected int customHash(final Object item) {
			return item instanceof EventItem ? ((EventItem)item).hash : System.identityHashCode(item);
		}

		/** {@inheritDoc} */
		@Override
		protected int customHashKey(final int entryIndex) {
			return this.customGetKey(entryIndex).hash;
		}

		/** {@inheritDoc} */
		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item) {
			final EventItem key = this.customGetKey(entryIndex);
			return (key == item) || (key.get() == item);
		}

		/** {@inheritDoc} */
		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item, final int keyHash) {
			final EventItem key = this.customGetKey(entryIndex);
			return (key.hash == keyHash) && ((key == item) || (key.get() == item));
		}

	}

	@SuppressWarnings ("javadoc")
	private static final class EventListener extends WeakReference<Object> {

		public EventItem owner; // TODO?

		public EventListener(final Object listener, final ReferenceQueue<? super Object> queue) {
			super(listener, queue);
		}

	}

	/** Dieses Feld speichert die {@link EventItem}. */
	private final EventStore eventStore = new EventStore();

	@SuppressWarnings ("javadoc")
	private final Object getImpl(final Object listener) {
		return listener instanceof EventListener ? ((EventListener)listener).get() : listener;
	}

	@SuppressWarnings ("javadoc")
	private final void putImpl(final Object sender, final Object listener) {
		synchronized (this.eventStore) {
			final EventItem eventItem = this.eventStore.putItem(sender);
			final Object eventListener = eventItem.listener;
			if (eventListener instanceof Object[]) {
				final Object[] array = (Object[])eventListener;
				final int length = array.length;
				final Object[] array2 = new Object[length + 1];
				System.arraycopy(array, 0, array2, 0, length);
				array2[length] = listener;
				eventItem.listener = array2;
			} else if (this.getImpl(eventListener) != null) {
				eventItem.listener = new Object[]{eventListener, listener};
			} else {
				eventItem.listener = listener;
			}
		}
	}

	private final void fireImpl(final Object sender, final Object listener, final GMessage message) {
		@SuppressWarnings ("unchecked")
		final GListener listener2 = (GListener)this.getImpl(listener);
		if (listener2 == null) return;
		this.customFire(sender, listener2, message);
	}

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen
	 * informiert. Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden.
	 *
	 * @see Listeners#putWeak(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public final GListener put(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return null;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		this.putImpl(sender, listener);
		return listener;
	}

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück.<br>
	 * Wenn der Empfänger {@code null} ist, wird er ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen
	 * informiert. Das mehrfache Anmelden des gleichen Empfängers sollte vermieden werden. Der Empfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @see Listeners#put(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param listener Ereignisempfänger oder {@code null}.
	 * @return {@code listener}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public final GListener putWeak(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return null;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		this.putImpl(sender, new EventListener(listener, this.eventStore.eventQueue));
		return listener;
	}

	/** Diese Methode meldet den gegebenen Empfänger ab. Wenn der Empfänger {@code null} ist, wird er nicht abgemeldet. Andernfalls wird er beim zukünftigen
	 * {@link #fire(Object) Auslösen} von Ereignissen nicht mehr informiert.
	 *
	 * @see #put(Object)
	 * @param listener Empfänger oder {@code null}. */
	public final void pop(final Object sender, final GListener listener) throws IllegalArgumentException {
		if (listener == null) return;
		if (listener instanceof Object[]) throw new IllegalArgumentException();
		synchronized (this.eventStore) {
			final EventItem eventItem = this.eventStore.getItem(sender);
			if (eventItem == null) return;
			final Object eventListener = eventItem.listener;
			if (eventListener instanceof Object[]) {
				final Object[] array = (Object[])eventListener;
				final int length = array.length;
				int length2 = 0;
				for (int i = 0; i < length; i++) {
					final Object item = this.getImpl(array[i]);
					if ((item == null) || (item == listener)) {
						array[i] = null;
					} else {
						length2++;
					}
				}
				if (length2 != 0) {
					final Object[] array2 = new Object[length2];
					for (int i = 0, i2 = 0; i < length; i++) {
						final Object item = array[i];
						if (item != null) {
							array2[i2++] = item;
						}
					}

				} else {
					eventItem.listener = null;
					this.eventStore.popItem(sender);
				}
				// TODO 1. bestimmen der länge; abgelaufene auf null setzen; 2. abschreiben der einträge != null
				final Object[] newArray = new Object[length + 1];
				System.arraycopy(array, 0, newArray, 0, length);
				newArray[length] = listener;
				eventItem.listener = newArray;
			} else {
				final Object item = this.getImpl(eventListener);
				if ((item == null) || (item == listener)) {
					eventItem.listener = null;
					this.eventStore.popItem(sender);
				}
			}
		}
	}

	public final GMessage fire(final Object sender, final GMessage message) {
		final Object listener;
		synchronized (this.eventStore) {
			final EventItem event = this.eventStore.getItem(sender);
			if (event == null) return message;
			listener = event.listener;
		}
		if (listener instanceof Object[]) {
			for (final Object item: (Object[])listener) {
				this.fireImpl(sender, item, message);
			}
		} else {
			this.fireImpl(sender, listener, message);
		}
		return message;
	}

	/** Diese Methode gibt das an den gegebenen Sender gebundene Ereignis zurück.
	 *
	 * @param sender Ereignissender.
	 * @return Ereignis. */
	public final Event<GListener, GMessage> toEvent(final Object sender) {
		return new Event<GListener, GMessage>() {

			@Override
			public GListener put(final GListener listener) {
				return Listeners.this.put(sender, listener);
			}

			@Override
			public GListener putWeak(final GListener listener) throws IllegalArgumentException {
				return Listeners.this.putWeak(sender, listener);
			}

			@Override
			public void pop(final GListener listener) {
				Listeners.this.pop(sender, listener);
			}

			@Override
			public GMessage fire(final GMessage message) {
				return Listeners.this.fire(sender, message);
			}

		};
	}

	/** Diese Methode wird durch {@link #fire(Object, Object)} aufgerufen und soll dem gegebenen Empfänger die gegebene Nachricht senden.
	 *
	 * @param sender Sender oder {@code null}.
	 * @param handler Empfänger.
	 * @param message Nachricht oder {@code null}. */
	protected abstract void customFire(Object sender, GListener handler, GMessage message);

}
