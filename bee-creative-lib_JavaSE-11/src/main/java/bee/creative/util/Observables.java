package bee.creative.util;

import java.lang.ref.WeakReference;
import bee.creative.lang.Objects;
import bee.creative.ref.WeakReference2;

/** Diese Klasse implementiert eine threadsichere Verwaltung von Ereignisempfängern, welche jederzeit {@link #put(Object, Object) angemeldet},
 * {@link #pop(Object, Object) abgemeldet} sowie {@link #fire(Object, Object) benachrichtigt} werden können und bezüglich eines {@link WeakReference schwach}
 * referenzierten Ereignissenders gespeichert werden. Die Ereignisempfänger können ebenfalls {@link WeakReference schwach} {@link #putWeak(Object, Object)
 * angemeldet} werden.
 * <p>
 * Beim {@link #fire(Object, Object) Benachrichtigen} der Ereignisempfänger wird eine gegebene Nachricht an alle zu diesem Zeitpunkt für einen gegebenen
 * Ereignissender angemeldeten Empfänger {@link #customFire(Object, Object, Object) gesendet}. Die Reihenfolge der Benachrichtigung der Empfänger entspricht der
 * Reihenfolge ihrer Anmeldung.
 * <p>
 * <pre>
 *
 * public class MySender {
 *
 * public static class MyEvent {
 * }
 *
 * public	static interface MyListener {
 * 	public void onMyEvent(MyEvent event);
 * }
 *
 * static final Observables<MyEvent, MyListener> onMyEventListeners = Observables.from(MyListener::onMyEvent);
 *
 * public Observable<MyEvent, MyListener> onMyEvent() {
 * 	return onMyEventListeners.observe(this);
 * }
 *
 * }
 * </pre>
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GMessage> Typ der Nachricht.
 * @param <GObserver> Typ der Empfänger. Dieser darf kein {@code Object[]} sein. */
public abstract class Observables<GMessage, GObserver> {

	/** Diese Klasse implementiert die Verwaltungsdaten eines Ereignissenders. */
	private static final class WeakSender extends WeakReference2<Object> {

		/** Dieses Feld speichert den Streuwert des Senders. */
		public final int hash;

		public final SenderStore store;

		/** Dieses Feld speichert die angemeldeten Empfänger als {@link WeakObserver}, {@code GObserver} oder {@code Object[]} solcher Empfänger. */
		public volatile Object observer;

		public WeakSender(final Object sender, final SenderStore store) {
			super(sender);
			this.hash = System.identityHashCode(sender);
			this.store = store;
		}

		@Override
		protected void customRemove() {
			final SenderStore store = this.store;
			synchronized (store) {
				store.pop(this);
			}
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
			} else if (WeakObserver.get(observer2) != null) {
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
					final Object item = WeakObserver.get(array[i]);
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
				final Object item = WeakObserver.get(observer2);
				if ((item == null) || (item == observer)) {
					this.observer = null;
				}
			}
		}

		/** Diese Methode meldet alle Empfänger ab. */
		public void popAll() {
			this.observer = null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get(), this.observer);
		}

	}

	private static final class WeakObserver extends WeakReference2<Object> {

		public static Object get(final Object object) {
			return object instanceof WeakObserver ? ((WeakObserver)object).get() : object;
		}

		public final WeakSender sender;

		public WeakObserver(final Object observer, final WeakSender sender) {
			super(observer);
			this.sender = sender;
		}

		@Override
		protected void customRemove() {
			final WeakSender sender = this.sender;
			final SenderStore store = sender.store;
			synchronized (store) {
				synchronized (sender) {
					sender.pop(this);
					if (sender.observer != null) return;
					store.pop(sender);
				}
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get());
		}

	}

	/** Diese Klasse implementiert die Verwaltung der Ereignissender und ihrer Ereignisempfänger. */
	private static final class SenderStore extends HashSet<Object> {

		/** Dieses Feld speichert das serialVersionUID. */
		private static final long serialVersionUID = -427892278890180125L;

		/** Dieses Feld speichert den Ereignissender für {@code null}. */
		private static final Object NULL = new Object();

		public SenderStore() {
			super(10);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, wird {@code null} geliefert.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten oder {@code null}. */
		public WeakSender get(final Object sender) {
			final int entryIndex = this.getIndexImpl(Objects.notNull(sender, SenderStore.NULL));
			return entryIndex < 0 ? null : this.customGetKey(entryIndex);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, werden sie angelegt.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten. */
		public WeakSender put(final Object sender) {
			return this.customGetKey(this.putIndexImpl(Objects.notNull(sender, SenderStore.NULL)));
		}

		/** Diese Methode entfernt die Verwaltungsdaten zum gegebenen Ereignissender.
		 *
		 * @param sender Ereignissender, {@link WeakSender Verwaltungsdaten} oder {@code null}. */
		public void pop(final Object sender) {
			this.popIndexImpl(Objects.notNull(sender, SenderStore.NULL));
			final int capacity = this.capacity() / 2;
			if (capacity <= this.size()) return;
			this.allocate(capacity);
		}

		@Override
		protected WeakSender customGetKey(final int entryIndex) {
			return (WeakSender)super.customGetKey(entryIndex);
		}

		@Override
		protected void customSetKey(final int entryIndex, final Object item, final int itemHash) {
			this.customSetKey(entryIndex, item instanceof WeakSender ? item : new WeakSender(item, this));
		}

		@Override
		protected int customHash(final Object item) {
			return item instanceof WeakSender ? ((WeakSender)item).hash : System.identityHashCode(item);
		}

		@Override
		protected int customHashKey(final int entryIndex) {
			return this.customGetKey(entryIndex).hash;
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item) {
			final WeakSender key = this.customGetKey(entryIndex);
			return (key == item) || (key.get() == item);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object item, final int keyHash) {
			final WeakSender key = this.customGetKey(entryIndex);
			return (key.hash == keyHash) && ((key == item) || (key.get() == item));
		}

	}

	private final class SenderObservable implements Observable<GMessage, GObserver> {

		public final Object sender;

		public SenderObservable(final Object sender) {
			this.sender = sender;
		}

		@Override
		public GObserver put(final GObserver observer) {
			return Observables.this.put(this.sender, observer);
		}

		@Override
		public GObserver putWeak(final GObserver observer) {
			return Observables.this.putWeak(this.sender, observer);
		}

		@Override
		public void pop(final GObserver observer) {
			Observables.this.pop(this.sender, observer);
		}

		@Override
		public GMessage fire(final GMessage message) {
			return Observables.this.fire(this.sender, message);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sender);
		}

	}

	/** Diese Methode liefert eine neue {@link Observables Ereignisempfängerverwaltung}, die beim {@link Observables#fire(Object, Object) Auslösen eines
	 * Ereignisses} zur Benachrichtigung der Ereignisempfänger mit der Ereignisnachricht die gegebene Methode mit diesen Objekten aufruft.
	 *
	 * @param customFire Methode zur Benachrichtigung eines Ereignisempfängers mit einer Ereignisnachricht.
	 * @return Ereignisempfängerverwaltung. */
	public static <GMessage, GObserver> Observables<GMessage, GObserver> from(final Setter<? super GObserver, ? super GMessage> customFire) {
		return new Observables<>() {

			@Override
			protected void customFire(final Object sender, final GMessage message, final GObserver observer) {
				customFire.set(observer, message);
			}

		};
	}

	/** Dieses Feld speichert die {@link WeakSender}. */
	private final SenderStore store = new SenderStore();

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück. Wenn der Empfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen informiert. Das mehrfache Anmelden des gleichen
	 * Empfängers sollte vermieden werden.
	 *
	 * @see Observables#putWeak(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code observer}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public GObserver put(final Object sender, final GObserver observer) throws IllegalArgumentException {
		return this.putImpl(sender, observer, false);
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
		return this.putImpl(sender, observer, true);
	}

	private GObserver putImpl(final Object sender, final GObserver observer, final boolean weak) {
		if (observer == null) return null;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		final SenderStore store = this.store;
		synchronized (store) {
			final WeakSender source = store.put(sender);
			synchronized (source) {
				source.put(weak ? new WeakObserver(observer, source) : observer);
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
		final SenderStore store = this.store;
		synchronized (store) {
			final WeakSender source = store.get(sender);
			if (source == null) return;
			synchronized (source) {
				source.pop(observer);
			}
		}
	}

	/** Diese Methode meldet alle Empfänger ab.
	 *
	 * @param sender Ereignissender oder {@code null}. */
	public void popAll(final Object sender) {
		final SenderStore store = this.store;
		synchronized (store) {
			final WeakSender source = store.get(sender);
			if (source == null) return;
			synchronized (source) {
				source.popAll();
			}
			store.pop(source);
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
		final SenderStore eventStore = this.store;
		synchronized (eventStore) {
			final WeakSender eventItem = eventStore.get(sender);
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
		final GObserver observer2 = (GObserver)WeakObserver.get(observer);
		if (observer2 == null) return;
		this.customFire(sender, message, observer2);
	}

	/** Diese Methode gibt das an den gegebenen Sender gebundene Ereignisquelle zurück.
	 *
	 * @param sender Ereignissender.
	 * @return Ereignisquelle. */
	public Observable<GMessage, GObserver> observe(final Object sender) {
		return new SenderObservable(sender);
	}

	/** Diese Methode wird durch {@link #fire(Object, Object)} aufgerufen und soll dem gegebenen Empfänger die gegebene Nachricht senden.
	 *
	 * @param sender Sender oder {@code null}.
	 * @param message Nachricht oder {@code null}.
	 * @param observer Empfänger. */
	protected abstract void customFire(Object sender, GMessage message, GObserver observer);

}
