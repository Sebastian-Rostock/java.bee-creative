package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;

/** Diese Klasse implementiert einen {@link Thread}-Puffer, welcher Methoden zum {@link #start(Runnable) Starten}, {@link #isActive(Runnable) Überwachen},
 * {@link #interrupt(Runnable) Unterbrechen} und {@link #join(Runnable) Abwarten} der Berechnung eines {@link Thread} bezüglich beliebiger {@link Runnable}
 * bereitstellt. Jeder inter dazu eingesetzte {@link Thread} arbeitet in einer gegebenen {@link #getActivePriority() Priorität} und kann nach der Abarbeitung eines
 * {@link Runnable} innerhalb einer gegebenen {@link #getWaitingTimeout() Wartezeit} wiederverwendet werden.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ThreadPool {

	/** Diese Klasse implementiert einen wiederverwendbaren Thread zur Ausführung eines {@link Runnable}. */
	private static final class ThreadItem extends Thread {

		public final ThreadNode node;

		public Object join;

		public Runnable task;

		public ThreadItem(final ThreadPool pool) {
			this.node = new ThreadNode(pool, this);
		}

		@Override
		public void run() {
			this.node.pool.run(this);
		}

	}

	/** Diese Klasse implementiert einen Knoten der doppelt verketteten Liste zur Verwaltung der wartenden {@link ThreadItem}. */
	private static final class ThreadNode {

		public final ThreadPool pool;

		public final ThreadItem item;

		public ThreadNode prev;

		public ThreadNode next;

		public ThreadNode(final ThreadPool pool) {
			this(pool, null);
		}

		public ThreadNode(final ThreadPool pool, final ThreadItem item) {
			this.pool = pool;
			this.item = item;
			this.prev = (this.next = this);
		}

		public void insert(final ThreadNode head) {
			head.prev = ((this.prev = (this.next = head).prev).next = this);
		}

		public void delete() {
			(this.prev.next = this.next).prev = this.prev;
		}

	}

	/** Dieses Feld bildet von den ausgeführten {@link Runnable} auf die dafür eingesetzten {@link ThreadItem} ab.<br>
	 * Es ist {@code synchronized} über {@link #activeMap}. In {@link ThreadItem#run()} wird das Ende der Berechnung darüber signalisiert, auf welche in
	 * {@link ThreadPool#join(long, Runnable)} gewartet wird. */
	private final HashMap3<Runnable, ThreadItem> activeMap = new HashMap3<>();

	private String activeName;

	private int activePriority = Thread.NORM_PRIORITY;

	/** Dieses Feld speichert die wiederverwendbaren {@link ThreadItem}.<br>
	 * Es ist {@code synchronized} über {@link #activeMap}. */
	private final ThreadNode waitingList = new ThreadNode(this);

	/** Dieses Feld speichert die Anzahl {@link ThreadItem} die mindestens vorgehalten werden sollen.<br>
	 * Es ist {@code synchronized} über {@link #waitingList}. */
	private int waitingReserve = 0;

	/** Dieses Feld speichert die Anzahl der wartenden {@link ThreadItem} in {@link #waitingList}.<br>
	 * Es ist {@code synchronized} über {@link #activeMap}. */
	private int waitingCount = 0;

	private long waitingTimeout = 60000;

	public ThreadPool() {
		this("ThreadPool-Item");
	}

	public ThreadPool(final String name) throws NullPointerException, IllegalArgumentException {
		this(name, Thread.NORM_PRIORITY);
	}

	public ThreadPool(final String name, final int priority) throws NullPointerException, IllegalArgumentException {
		this(name, priority, 60000);
	}

	public ThreadPool(final String name, final int priority, final long cleanup) throws NullPointerException, IllegalArgumentException {
		if ((priority < Thread.MIN_PRIORITY) || (priority > Thread.MAX_PRIORITY) || (cleanup < 0)) throw new IllegalArgumentException();
		this.activeName = Objects.notNull(name);
		this.activePriority = priority;
	}

	@SuppressWarnings ("javadoc")
	final ThreadItem toItemImpl(final Runnable task) throws NullPointerException {
		return this.activeMap.get(Objects.notNull(task));
	}

	@SuppressWarnings ("javadoc")
	final HashSet3<ThreadItem> toItemsImpl(final Iterable<? extends Runnable> tasks) throws NullPointerException {
		final HashSet3<ThreadItem> items = new HashSet3<>();
		for (final Runnable task: tasks) {
			items.add(this.toItemImpl(task));
		}
		return items;
	}

	private final void checkTimeoutImpl(final long timeout) {
		if (timeout < 0) throw new IllegalArgumentException();
	}

	private final Runnable getTaskImpl(final ThreadItem item) {
		synchronized (this.waitingList) {
			synchronized (this.activeMap) {
				item.setName(this.activeName);
				item.setPriority(this.activePriority);
				final Runnable task = item.task;
				if (task != null) return task;
			}
			final long until = System.currentTimeMillis() + this.waitingTimeout;
			long sleep = this.waitingTimeout;
			while (true) {
				try {
					this.waitingList.wait(sleep);
				} catch (final InterruptedException ignore) {}
				synchronized (this.activeMap) {
					Runnable task = item.task;
					if (task != null) return task;
					if (this.waitingTimeout == 0 || this.waitingCount <= this.waitingReserve) {
						sleep = 0;
					} else {
						sleep = until - System.currentTimeMillis();
						if (sleep <= 0) {
							item.node.delete();
							return null;
						}
					}
				}
			}
		}
	}

	private final void runTaskImpl(final ThreadItem item, final Runnable task) {
		boolean okay = false;
		try {
			Thread.interrupted();
			task.run();
			okay = true;
		} finally {
			synchronized (this.activeMap) {
				item.join = null;
				item.task = null;
				this.activeMap.remove(task);
				if (okay) {
					item.node.insert(this.waitingList);
					this.waitingCount++;
				}
				this.activeMap.notifyAll();
			}
		}
	}

	/** Diese Methode implementiert {@link ThreadItem#run()}. */
	final void run(final ThreadItem item) {
		while (true) {
			final Runnable task = this.getTaskImpl(item);
			if (task == null) return;
			this.runTaskImpl(item, task);
		}
	}

	/** Diese Methode implementiert {@link #join(long, Runnable)} ohne Synchronisation. */
	private final void joinImpl(final ThreadItem item, final long timeout) throws InterruptedException {
		if (item == null) return;
		final Object join = item.join;
		if (timeout == 0) {
			while (true) {
				this.activeMap.wait(0);
				if (item.join != join) return;
			}
		} else {
			final long until = System.currentTimeMillis() + timeout;
			long sleep = timeout;
			while (true) {
				this.activeMap.wait(sleep);
				if (item.join != join) return;
				sleep = until - System.currentTimeMillis();
				if (sleep <= 0) return;
			}
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #join(long, Runnable) this.join(0, task)}.
	 *
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void join(final Runnable task) throws NullPointerException, InterruptedException {
		this.join(0, task);
	}

	/** Diese Methode {@link Object#wait(long) wartet} auf den Abschluss der gegebenen Berechnung, sofern diese aktuell {@link #isActive(Runnable) verarbeitet}
	 * wird. Wenn die gegebene Wartezeit nicht {@code 0} ist, wird höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void join(final long timeout, final Runnable task) throws NullPointerException, IllegalArgumentException, InterruptedException {
		this.checkTimeoutImpl(timeout);
		synchronized (this.activeMap) {
			this.joinImpl(this.toItemImpl(task), timeout);
		}
	}

	/** Diese Methode implementiert {@link #joinAll(long, Iterable)} ohne Synchronisation. */
	private final void joinAllImpl(final Iterable<? extends ThreadItem> items, final long timeout) throws InterruptedException {
		if (timeout == 0) {
			for (final ThreadItem item: items) {
				this.joinImpl(item, 0);
			}
		} else {
			final long until = System.currentTimeMillis() + timeout;
			long sleep = timeout;
			while (true) {
				for (final ThreadItem item: items) {
					this.joinImpl(item, sleep);
					sleep = until - System.currentTimeMillis();
					if (sleep <= 0) return;
				}
			}
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #joinAll(long, Runnable...) this.joinAll(0, tasks)}.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAll(final Runnable... tasks) throws NullPointerException, InterruptedException {
		this.joinAll(0, tasks);
	}

	/** Diese Methode ist eine Abkürzung für {@link #joinAll(long, Iterable) this.joinAll(0, tasks)}.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, InterruptedException {
		this.joinAll(0, tasks);
	}

	/** Diese Methode ist eine Abkürzung für {@link #joinAll(long, Iterable) this.joinAll(0, Arrays.asList(tasks))}.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAll(final long timeout, final Runnable... tasks) throws NullPointerException, IllegalArgumentException, InterruptedException {
		this.joinAll(0, Arrays.asList(tasks));
	}

	/** Diese Methode {@link #join(long, Runnable) wartet} auf den Abschluss der gegebenen Berechnungen, sofern diese aktuell {@link #isActive(Runnable)
	 * verarbeitet} werden. Wenn die gegebene Wartezeit nicht {@code 0} ist, wird höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAll(final long timeout, final Iterable<? extends Runnable> tasks)
		throws NullPointerException, IllegalArgumentException, InterruptedException {
		this.checkTimeoutImpl(timeout);
		synchronized (this.activeMap) {
			this.joinAllImpl(this.toItemsImpl(tasks), timeout);
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #joinAllActive(long) this.joinAllActivejoinAll(0)}.
	 *
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAllActive() throws InterruptedException {
		this.joinAllActive(0);
	}

	/** Diese Methode ist eine Abkürzung für {@link #joinAll(long, Iterable) this.joinAll(timeout, this.getActiveTasks())}.
	 *
	 * @see #getActiveTasks()
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAllActive(final long timeout) throws IllegalArgumentException, InterruptedException {
		this.checkTimeoutImpl(timeout);
		synchronized (this.activeMap) {
			this.joinAllImpl(new ArrayList<>(this.activeMap.values()), timeout);
		}
	}

	public void start(final Runnable task) throws NullPointerException, IllegalThreadStateException {
		Objects.notNull(task);
		final ThreadItem item;
		synchronized (this.activeMap) {
			if (this.activeMap.containsKey(task)) throw new IllegalThreadStateException();
			final ThreadNode next = this.waitingList.next;
			if (this.waitingList != next) {
				item = next.item;
				next.delete();
				this.waitingCount--;
			} else {
				item = new ThreadItem(this);
				item.setDaemon(true);
				item.start();
			}
			item.join = new Object();
			item.task = task;
			this.activeMap.put(task, item);

			if (this.activeMap.size() < (this.activeMap.capacity() / 4)) {
				this.activeMap.allocate(this.activeMap.capacity() / 2);
			}
		}
		synchronized (this.waitingList) {
			this.waitingList.notifyAll();
		}
	}

	public void startAll(final Runnable... tasks) throws NullPointerException, IllegalThreadStateException { // ok
		this.startAll(Arrays.asList(tasks));
	}

	public void startAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, IllegalThreadStateException { // ok
		for (final Runnable task: tasks) {
			this.start(task);
		}
	}

	/** Diese Methode implementiert {@link #interrupt(Runnable)} ohne Synchronisation. */
	private final void interruptImpl(final ThreadItem item) throws SecurityException {
		if (item == null) return;
		item.interrupt();
	}

	/** Diese Methode {@link Thread#interrupt() unterbricht} die gegebene Berechnung, sofern diese aktuell {@link #isActive(Runnable) verarbeitet} wird.
	 *
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist.
	 * @throws SecurityException Wenn {@link Thread#interrupt()} diese auslöst. */
	public void interrupt(final Runnable task) throws NullPointerException, SecurityException {
		synchronized (this.activeMap) {
			this.interruptImpl(this.toItemImpl(task));
		}
	}

	/** Diese Methode implementiert {@link #interruptAll(Iterable)} ohne Synchronisation. */
	private final void interruptAllImpl(final Iterable<? extends ThreadItem> items) {
		for (final ThreadItem item: items) {
			this.interruptImpl(item);
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #interruptAll(Iterable) this.interruptAll(Arrays.asList(tasks))}.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws SecurityException Wenn {@link Thread#interrupt()} diese auslöst. */
	public void interruptAll(final Runnable... tasks) throws NullPointerException, SecurityException { // ok
		this.interruptAll(Arrays.asList(tasks));
	}

	/** Diese Methode {@link Thread#interrupt() unterbricht} die gegebenen Berechnungen, sofern diese aktuell {@link #isActive(Runnable) verarbeitet} werden.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws SecurityException Wenn {@link Thread#interrupt()} diese auslöst. */
	public void interruptAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, SecurityException {
		synchronized (this.activeMap) {
			this.interruptAllImpl(this.toItemsImpl(tasks));
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #interruptAll(Iterable) this.interruptAll(this.getActiveTasks())}.
	 *
	 * @see #getActiveTasks()
	 * @throws SecurityException Wenn {@link Thread#interrupt()} diese auslöst. */
	public void interruptAllActive() throws SecurityException {
		synchronized (this.activeMap) {
			this.interruptAllImpl(new ArrayList<>(this.activeMap.values()));
		}
	}

	public boolean isActive(final Runnable task) throws NullPointerException {
		synchronized (this.activeMap) {
			return this.activeMap.containsKey(Objects.notNull(task));
		}
	}

	public Runnable[] getActiveTasks() {
		synchronized (this.activeMap) {
			return this.activeMap.keySet().toArray(new Runnable[this.activeMap.size()]);
		}
	}

	public int getActiveCount() {
		synchronized (this.activeMap) {
			return this.activeMap.size();
		}
	}

	public String getActiveName() {
		synchronized (this.activeMap) {
			return this.activeName;
		}
	}

	public void setActiveName(final String value) {
		synchronized (this.activeMap) {
			this.activeName = Objects.notNull(value);
		}
	}

	public int getActivePriority() {
		synchronized (this.activeMap) {
			return this.activePriority;
		}
	}

	public void setActivePriority(final int value) {
		if ((value < Thread.MIN_PRIORITY) || (value > Thread.MAX_PRIORITY)) throw new IllegalArgumentException();
		synchronized (this.activeMap) {
			this.activePriority = value;
		}
	}

	public int getWaitingReserve() {
		synchronized (this.waitingList) {
			return this.waitingReserve;
		}
	}

	public int getWaitingCount() {
		synchronized (this.activeMap) {
			return this.waitingCount;
		}
	}

	public long getWaitingTimeout() {
		synchronized (this.waitingList) {
			return this.waitingTimeout;
		}
	}

	public void setWaitingReserve(final int value) throws IllegalAccessException {
		if (value < 0) throw new IllegalArgumentException();
		synchronized (this.waitingList) {
			this.waitingReserve = value;
		}
	}

	public void setWaitingTimeout(final long value) throws IllegalAccessException {
		this.checkTimeoutImpl(value);
		synchronized (this.waitingList) {
			this.waitingTimeout = value;
		}
	}

	@Override
	public String toString() {
		return "[" + this.getActiveName() + "-" + this.getActivePriority() + "-]";
	}

}
