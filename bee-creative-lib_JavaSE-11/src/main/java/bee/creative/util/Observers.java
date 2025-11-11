package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import java.lang.ref.WeakReference;
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
 * static Observables<MyEvent, MyListener> onMyEventListeners = observersFrom(MyListener::onMyEvent);
 *
 * public Observable<MyEvent, MyListener> onMyEvent() {
 * 	return onMyEventListeners.observe(this);
 * }
 *
 * }
 * </pre>
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <M> Typ der Nachricht.
 * @param <L> Typ der Empfänger. Dieser darf kein {@code Object[]} sein. */
public abstract class Observers<M, L> {

	/** Diese Methode liefert eine neue {@link Observers Ereignisempfängerverwaltung}, die beim {@link Observers#fire(Object, Object) Auslösen eines Ereignisses}
	 * zur Benachrichtigung der Ereignisempfänger mit der Ereignisnachricht die gegebene Methode mit diesen Objekten aufruft.
	 *
	 * @param customFire Methode zur Benachrichtigung eines Ereignisempfängers mit einer Ereignisnachricht.
	 * @return Ereignisempfängerverwaltung. */
	public static <M, L> Observers<M, L> observersFrom(Setter<? super L, ? super M> customFire) {
		return new Observers<>() {

			@Override
			protected void customFire(Object sender, M message, L observer) {
				customFire.set(observer, message);
			}

		};
	}

	/** Diese Methode meldet den gegebenen Ereignisempfänger für den gegebenen Ereignissender an und gibt ihn zurück. Wenn der Empfänger {@code null} ist, wird er
	 * ignoriert. Andernfalls wird er beim zukünftigen {@link #fire(Object, Object) Auslösen} von Ereignissen informiert. Das mehrfache Anmelden des gleichen
	 * Empfängers sollte vermieden werden.
	 *
	 * @see Observers#putWeak(Object, Object)
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Ereignisempfänger oder {@code null}.
	 * @return {@code observer}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public <O2 extends L> O2 put(Object sender, O2 observer) throws IllegalArgumentException {
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
	public <O2 extends L> O2 putWeak(Object sender, O2 observer) throws IllegalArgumentException {
		return this.putImpl(sender, observer, true);
	}

	/** Diese Methode meldet den gegebenen Empfänger ab. Wenn der Empfänger {@code null} ist, wird er nicht abgemeldet. Andernfalls wird er beim zukünftigen
	 * {@link #fire(Object, Object) Auslösen} von Ereignissen nicht mehr informiert.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param observer Empfänger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Ereignisempfänger unzulässig ist. */
	public void pop(Object sender, Object observer) throws IllegalArgumentException {
		if (observer == null) return;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		synchronized (this.senderStore) {
			var senderEntry = this.senderStore.get(sender);
			if (senderEntry == null) return;
			synchronized (senderEntry) {
				senderEntry.pop(observer);
			}
		}
	}

	/** Diese Methode meldet alle Empfänger ab.
	 *
	 * @param sender Ereignissender oder {@code null}. */
	public void popAll(Object sender) {
		synchronized (this.senderStore) {
			var senderEntry = this.senderStore.get(sender);
			if (senderEntry == null) return;
			synchronized (senderEntry) {
				senderEntry.popAll();
			}
			this.senderStore.pop(senderEntry);
		}
	}

	/** Diese Methode löst ein Ereignis aus, benachrichtigt alle zu diesem Zeitpunkt für den gegebenen Ereignissender angemeldeten Ereignisempfänger mit der
	 * gegebenen Ereignisnachricht und gibt letztere zurück. Sofern die Empfänger es zulassen, kann die Nachricht {@code null} sein.
	 *
	 * @param sender Ereignissender oder {@code null}.
	 * @param message Ereignisnachricht oder {@code null}.
	 * @return Ereignisnachricht.
	 * @throws NullPointerException Wenn {@code event} {@code null} ist und die Empfänger dies nicht unterstützten. */
	public M fire(Object sender, M message) throws NullPointerException {
		Object senderObserver;
		synchronized (this.senderStore) {
			var senderEntry = this.senderStore.get(sender);
			if (senderEntry == null) return message;
			senderObserver = senderEntry.senderObserver;
		}
		if (senderObserver instanceof Object[]) {
			for (var observer: (Object[])senderObserver) {
				this.fireImpl(sender, observer, message);
			}
		} else {
			this.fireImpl(sender, senderObserver, message);
		}
		return message;
	}

	/** Diese Methode gibt das an den gegebenen Sender gebundene Ereignisquelle zurück.
	 *
	 * @param sender Ereignissender.
	 * @return Ereignisquelle. */
	public Observable<M, L> observe(Object sender) {
		return new Observable<>() {

			@Override
			public <O2 extends L> O2 put(O2 observer) throws IllegalArgumentException {
				return Observers.this.put(sender, observer);
			}

			@Override
			public <O2 extends L> O2 putWeak(O2 observer) throws IllegalArgumentException {
				return Observers.this.putWeak(sender, observer);
			}

			@Override
			public void pop(Object observer) {
				Observers.this.pop(sender, observer);
			}

			@Override
			public M fire(M message) {
				return Observers.this.fire(sender, message);
			}

		};
	}

	/** Diese Methode wird durch {@link #fire(Object, Object)} aufgerufen und soll dem gegebenen Empfänger die gegebene Nachricht senden.
	 *
	 * @param sender Sender oder {@code null}.
	 * @param message Nachricht oder {@code null}.
	 * @param observer Empfänger. */
	protected abstract void customFire(Object sender, M message, L observer);

	private static Object observerFrom(Object object) {
		return object instanceof WeakObserver ? ((WeakObserver)object).get() : object;
	}

	private final SenderStore senderStore = new SenderStore();

	private <O2 extends L> O2 putImpl(Object sender, O2 observer, boolean weak) {
		if (observer == null) return null;
		if (observer instanceof Object[]) throw new IllegalArgumentException();
		var store = this.senderStore;
		synchronized (store) {
			var source = store.put(sender);
			synchronized (source) {
				source.put(weak ? new WeakObserver(observer, source) : observer);
			}
		}
		return observer;
	}

	@SuppressWarnings ("unchecked")
	private void fireImpl(Object sender, Object observer, M message) {
		observer = observerFrom(observer);
		if (observer == null) return;
		this.customFire(sender, message, (L)observer);
	}

	/** Diese Klasse implementiert die Verwaltungsdaten eines Ereignissenders. */
	private static class WeakSender extends WeakReference2<Object> {

		/** Dieses Feld speichert den Streuwert des Senders. */
		public final int senderHash;

		public final SenderStore senderStore;

		/** Dieses Feld speichert die angemeldeten Empfänger als {@link WeakObserver}, {@code L} oder {@code Object[]} solcher Empfänger. */
		public volatile Object senderObserver;

		public WeakSender(Object sender, SenderStore store) {
			super(sender);
			this.senderHash = System.identityHashCode(sender);
			this.senderStore = store;
		}

		/** Diese Methode meldet den gegebenen Empfänger an.
		 *
		 * @param observer Empfänger. */
		public void put(Object observer) {
			var senderObserver = this.senderObserver;
			if (senderObserver instanceof Object[]) {
				var observerArray = (Object[])senderObserver;
				var observerCount = observerArray.length;
				var observerArray2 = new Object[observerCount + 1];
				System.arraycopy(observerArray, 0, observerArray2, 0, observerCount);
				observerArray2[observerCount] = observer;
				this.senderObserver = observerArray2;
			} else if (observerFrom(senderObserver) != null) {
				this.senderObserver = new Object[]{senderObserver, observer};
			} else {
				this.senderObserver = observer;
			}
		}

		/** Diese Methode meldet den gegebenen Empfänger ab.
		 *
		 * @param observer Empfänger. */
		public void pop(Object observer) {
			var senderObserver = this.senderObserver;
			if (senderObserver instanceof Object[]) {
				var observerArray = (Object[])senderObserver;
				var observerCount = observerArray.length;
				var observerCount2 = 0;
				for (var i = 0; i < observerCount; i++) {
					senderObserver = observerFrom(observerArray[i]);
					if ((senderObserver == null) || (senderObserver == observer)) {
						observerArray[i] = null;
					} else {
						observerCount2++;
					}
				}
				if (observerCount2 == 0) {
					this.senderObserver = null;
				} else if (observerCount2 == 1) {
					for (var i = 0; i < observerCount; i++) {
						senderObserver = observerArray[i];
						if (senderObserver != null) {
							this.senderObserver = senderObserver;
							return;
						}
					}
					this.senderObserver = null;
				} else {
					var observerArray2 = new Object[observerCount2];
					observerCount2 = 0;
					for (var i = 0; i < observerCount; i++) {
						senderObserver = observerArray[i];
						if (senderObserver != null) {
							observerArray2[observerCount2++] = senderObserver;
						}
					}
					this.senderObserver = observerArray2;
				}
			} else {
				senderObserver = observerFrom(senderObserver);
				if ((senderObserver == null) || (senderObserver == observer)) {
					this.senderObserver = null;
				}
			}
		}

		/** Diese Methode meldet alle Empfänger ab. */
		public void popAll() {
			this.senderObserver = null;
		}

		@Override
		protected void customRemove() {
			synchronized (this.senderStore) {
				this.senderStore.pop(this);
			}
		}

	}

	private static class WeakObserver extends WeakReference2<Object> {

		public final WeakSender sender;

		public WeakObserver(Object observer, WeakSender sender) {
			super(observer);
			this.sender = sender;
		}

		@Override
		protected void customRemove() {
			synchronized (this.sender.senderStore) {
				synchronized (this.sender) {
					this.sender.pop(this);
					if (this.sender.senderObserver != null) return;
					this.sender.senderStore.pop(this.sender);
				}
			}
		}

	}

	/** Diese Klasse implementiert die Verwaltung der Ereignissender und ihrer Ereignisempfänger. */
	private static class SenderStore extends HashSet<Object> {

		public SenderStore() {
			super(10);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, wird {@code null} geliefert.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten oder {@code null}. */
		public WeakSender get(Object sender) {
			var entryIndex = this.getIndexImpl(notNull(sender, NULL));
			return entryIndex < 0 ? null : this.customGetKey(entryIndex);
		}

		/** Diese Methode gibt die Verwaltungsdaten zum gegebenen Ereignissender zurück. Wenn keine hinterlegt sind, werden sie angelegt.
		 *
		 * @param sender Ereignissender oder {@code null}.
		 * @return Verwaltungsdaten. */
		public WeakSender put(Object sender) {
			return this.customGetKey(this.putIndexImpl(notNull(sender, NULL)));
		}

		/** Diese Methode entfernt die Verwaltungsdaten zum gegebenen Ereignissender.
		 *
		 * @param sender Ereignissender, {@link WeakSender Verwaltungsdaten} oder {@code null}. */
		public void pop(Object sender) {
			this.popIndexImpl(notNull(sender, NULL));
			var capacity = this.capacity() / 2;
			if (capacity <= this.size()) return;
			this.allocate(capacity);
		}

		@Override
		protected WeakSender customGetKey(int entryIndex) {
			return (WeakSender)super.customGetKey(entryIndex);
		}

		@Override
		protected void customSetKey(int entryIndex, Object item, int itemHash) {
			this.customSetKey(entryIndex, item instanceof WeakSender ? item : new WeakSender(item, this));
		}

		@Override
		protected int customHash(Object item) {
			return item instanceof WeakSender ? ((WeakSender)item).senderHash : System.identityHashCode(item);
		}

		@Override
		protected int customHashKey(int entryIndex) {
			return this.customGetKey(entryIndex).senderHash;
		}

		@Override
		protected boolean customEqualsKey(int entryIndex, Object item) {
			var key = this.customGetKey(entryIndex);
			return (key == item) || (key.get() == item);
		}

		@Override
		protected boolean customEqualsKey(int entryIndex, Object item, int keyHash) {
			var key = this.customGetKey(entryIndex);
			return (key.senderHash == keyHash) && ((key == item) || (key.get() == item));
		}

		private static final long serialVersionUID = -427892278890180125L;

		private static final Object NULL = new Object();

	}

}
