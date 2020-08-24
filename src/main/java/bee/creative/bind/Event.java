package bee.creative.bind;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import bee.creative.emu.EMU;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.ref.PointerQueue;
import bee.creative.util.HashSet;

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
 * public class MyMessage { ... }
 * public interface MyObserver {
 * 	public void onMyEvent(MyMessage message);
 * }
 *
 * public class MySender {
 *
 * 	public static final Event&lt;MyMessage, MyObserver> MyEvent = new Event&lt;>(){
 * 		protected void customFire(Object sender, MyMessage message, MyListener observer) {
 * 			observer.onMyEvent(message);
 *		}
 * 	};
 *
 * 	public Observable&lt;MyMessage, MyObserver> getMyEvent() {
 * 		return MyEvent.toObservable(this);
 * 	}
 *
 * }
 * </pre>
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GMessage> Typ der Nachricht.
 * @param <GObserver> Typ der Empfänger. Dieser darf kein {@code Object[]} sein. */
public abstract class Event<GMessage, GObserver> extends BaseObject {

	/** Diese Klasse implementiert die Verwaltungsdaten eines Ereignissenders. Jede Instanz benötigt mindestens 24 Byte. */
	private static final class EventItem extends WeakReference<Object> {

		/** Dieses Feld speichert den Streuwert des Senders. */
		public final int hash;

		/** Dieses Feld speichert die angemeldeten Empfänger als {@link EventObserver}, {@code GListener} oder {@code Object[]} solcher Empfänger sein. */
		public volatile Object observer;

		public EventItem(final Object sender, final ReferenceQueue<? super Object> queue) {
			super(sender, queue);
			this.hash = System.identityHashCode(sender);
		}

		/** Diese Methode meldet den gegebenen Empfänger an.
		 *
		 * @param observer Empfänger. */
		public void put(final Object observer) {
			final Object observer2 = this.observer;
			if (observer2 instanceof Object[]) {
				final Object[] array = (Object[])observer2;
				final int length = array.length;
				final Object[] array2 = new Object[length + 1];
				System.arraycopy(array, 0, array2, 0, length);
				array2[length] = observer;
				this.observer = array2;
			} else if (EventObserver.get(observer2) != null) {
				this.observer = new Object[]{observer2, observer};
			} else {
				this.observer = observer;
			}
		}

		/** Diese Methode meldet den gegebenen Empfänger ab.
		 *
		 * @param observer Empfänger. */
		public void pop(final Object observer) {
			final Object observer2 = this.observer;
			if (observer2 instanceof Object[]) {
				final Object[] array = (Object[])observer2;
				final int length = array.length;
				int length2 = 0;
				for (int i = 0; i < length; i++) {
					final Object item = EventObserver.get(array[i]);
					if ((item == null) || (item == observer)) {
						array[i] = null;
					} else {
						length2++;
					}
				}
				if (length2 == 0) {
					this.observer = null;
				} else if (length2 == 1) {
					for (int i = 0; i < length; i++) {
						final Object item = array[i];
						if (item != null) {
							this.observer = item;
							return;
						}
					}
					this.observer = null;
				} else {
					final Object[] array2 = new Object[length2];
					for (int i = 0, i2 = 0; i < length; i++) {
						final Object item = array[i];
						if (item != null) {
							array2[i2++] = item;
						}
					}
					this.observer = array2;
				}
			} else {
				final Object item = EventObserver.get(observer2);
				if ((item == null) || (item == observer)) {
					this.observer = null;
				}
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get(), this.observer);
		}

	}

	/** Diese Klasse implementiert die Verwaltung der Ereignissender und ihrer Ereignisempfänger. */
	private static final class EventStore extends HashSet<Object> {

		/** Dieses Feld speichert das serialVersionUID. */
		private static final long serialVersionUID = -427892278890180125L;

		/** Dieses Feld speichert den Ereignissender für {@code null}. */
		private static final Object NULL = new Object();

		/** Dieses Feld speichert den {@link ReferenceQueue} zur Erzeugung der {@link EventItem}. */
		public final EventQueue eventQueue = new EventQueue(this);

		public EventStore() {
		}

		private Object toKey(final Object sender) {
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
		 * @param sender Ereignissender, {@link EventItem Verwaltungsdaten} oder {@code null}. */
		public void pop(final Object sender) {
			this.popIndexImpl(this.toKey(sender));
		}

		@Override
		public long emu() {
			return super.emu() + EMU.from(this.eventQueue);
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
			} else if (reference instanceof EventObserver) {
				final EventObserver eventListener = (EventObserver)reference;
				final EventStore eventStore = eventListener.eventStore;
				synchronized (eventStore) {
					final EventItem eventItem = eventListener.eventItem;
					synchronized (eventItem) {
						eventItem.pop(eventListener);
						if (eventItem.observer != null) return;
						eventStore.pop(eventStore);
					}
				}
			}
		}

	}

	private static final class EventObserver extends WeakReference<Object> {

		public static Object get(final Object object) {
			return object instanceof EventObserver ? ((EventObserver)object).get() : object;
		}

		public final EventItem eventItem;

		public final EventStore eventStore;

		public EventObserver(final Object referent, final EventItem eventItem, final EventStore eventStore) {
			super(referent, eventStore.eventQueue);
			this.eventItem = eventItem;
			this.eventStore = eventStore;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get());
		}

	}

	private final class EventObservable implements Observable<GMessage, GObserver> {

		public final Object sender;

		public EventObservable(final Object sender) {
			this.sender = sender;
		}

		@Override
		public GObserver put(final GObserver observer) {
			return Event.this.put(this.sender, observer);
		}

		@Override
		public GObserver putWeak(final GObserver observer) {
			return Event.this.putWeak(this.sender, observer);
		}

		@Override
		public void pop(final GObserver observer) {
			Event.this.pop(this.sender, observer);
		}

		@Override
		public GMessage fire(final GMessage message) {
			return Event.this.fire(this.sender, message);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sender);
		}

	}

	/** Diese Methode liefert eine neue {@link Event Ereignisempfängerverwaltung}, die beim {@link Event#fire(Object, Object) Auslösen eines Ereignisses} zur
	 * Benachrichtigung der Ereignisempfänger mit der Ereignisnachricht die gegebene Methode mit diesen Objekten aufruft.
	 * 
	 * @param customFire Methode zur Benachrichtigung eines Ereignisempfängers mit einer Ereignisnachricht.
	 * @return Ereignisempfängerverwaltung. */
	public static <GMessage, GObserver> Event<GMessage, GObserver> from(final Setter<? super GObserver, ? super GMessage> customFire) {
		return new Event<GMessage, GObserver>() {

			@Override
			protected void customFire(final Object sender, final GMessage message, final GObserver observer) {
				customFire.set(observer, message);
			}

		};
	}

	/** Dieses Feld speichert die {@link EventItem}. */
	private final EventStore eventStore = new EventStore();

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück. Wenn der Empfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen informiert. Das mehrfache Anmelden des gleichen
	 * Empfängers sollte vermieden werden.
	 *
	 * @see Event#putWeak(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code observer}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GObserver put(final Object sender, final GObserver observer) throws IllegalArgumentException {
		if (observer == null) return null;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.put(sender);
			synchronized (eventItem) {
				eventItem.put(observer);
			}
		}
		return observer;
	}

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück. Wenn der Empfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen informiert. Das mehrfache Anmelden des gleichen
	 * Empfängers sollte vermieden werden. Der Empfänger wird über eine {@link WeakReference} referenziert.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code observer}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GObserver putWeak(final Object sender, final GObserver observer) throws IllegalArgumentException {
		if (observer == null) return null;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.put(sender);
			synchronized (eventItem) {
				eventItem.put(new EventObserver(observer, eventItem, eventStore));
			}
		}
		return observer;
	}

	/** Diese Methode meldet den gegebenen Empfänger ab. Wenn der Empfänger {@code null} ist, wird er nicht abgemeldet. Andernfalls wird er beim zukünftigen
	 * {@link #fire(Object, Object) Auslösen} von Ereignissen nicht mehr informiert.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Empfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(final Object sender, final GObserver observer) throws IllegalArgumentException {
		if (observer == null) return;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.get(sender);
			if (eventItem == null) return;
			synchronized (eventItem) {
				eventItem.pop(observer);
			}
		}
	}

	/** Diese Methode löst ein Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt für den gegebenen Ereignissender angemeldeten Ereignisempfänger mit der
	 * gegebenen Ereignisnachricht und gibt letztere zurück. Sofern die Empfänger es zulassen, kann die Nachricht {@code null} sein.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param message Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code event} {@code null} ist und die Empfänger dies nicht unterstützten. */
	public GMessage fire(final Object sender, final GMessage message) throws NullPointerException {
		final Object observer;
		final EventStore eventStore = this.eventStore;
		synchronized (eventStore) {
			final EventItem eventItem = eventStore.get(sender);
			if (eventItem == null) return message;
			observer = eventItem.observer;
		}
		if (observer instanceof Object[]) {
			for (final Object item: (Object[])observer) {
				this.fireImpl(sender, item, message);
			}
		} else {
			this.fireImpl(sender, observer, message);
		}
		return message;
	}

	private void fireImpl(final Object sender, final Object observer, final GMessage message) {
		@SuppressWarnings ("unchecked")
		final GObserver observer2 = (GObserver)EventObserver.get(observer);
		if (observer2 == null) return;
		this.customFire(sender, message, observer2);
	}

	/** Diese Methode gibt das an den gegebenen Sender gebundene Ereignis zurück.
	 *
	 * @param sender Ereignissender.
	 * @return Ereignis. */
	public Observable<GMessage, GObserver> toObservable(final Object sender) {
		return new EventObservable(sender);
	}

	/** Diese Methode wird durch {@link #fire(Object, Object)} aufgerufen und soll dem gegebenen Empfänger die gegebene Nachricht senden.
	 *
	 * @param sender Sender oder {@code null}.
	 * @param message Nachricht oder {@code null}.
	 * @param observer Empfänger. */
	protected abstract void customFire(Object sender, GMessage message, GObserver observer);

}
