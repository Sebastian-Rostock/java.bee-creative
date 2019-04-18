package bee.creative.lang;

import java.util.ArrayList;
import java.util.Arrays;
import bee.creative.util.HashMap3;
import bee.creative.util.HashSet3;

/** Diese Klasse implementiert einen {@link java.lang.Thread Thread}-Puffer, welcher Methoden zum {@link #start(Runnable) Starten}, {@link #isAlive(Runnable)
 * Überwachen}, {@link #interrupt(Runnable) Unterbrechen} und {@link #join(Runnable) Abwarten} der Auswertung beliebiger {@link Runnable Berechnungen}
 * bereitstellt.<br>
 * {@link #getActiveName() Name} und {@link #getActivePriority() Priorität} der zur Auswerung eingesetzten {@link Thread Threads} können jederzeit angepasst
 * werden und werden beim Starten neuer Berechnungen angewendet. Nach der Auswertung ihrer Berechnung warten diese Threads auf ihre Wiederverwendung. Dazu kann
 * eingestellt werden, wieviele auf ihre Wiederverwendung wartende Threads {@link #getWaitingReserve() mindestens vorgehalten werden} und wie lange die nicht
 * hierfür reservierten Threads {@link #getWaitingTimeout() maximal warten}, bevor sie verworfen werden.
 * <p>
 * <b>Achtung: </b> Bei der Verwendung von {@link ThreadLocal} muss darauf geachtet werden, diese diese korrekt initialisiert und finalisiert werden, da deren
 * Wert sonst unbeabsichtigt wiederverwendet werden oder ein Speicherleck entstehen kann.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ThreadPool {

	/** Diese Klasse implementiert einen wiederverwendbaren Thread zur Ausführung eines {@link Runnable}. */
	private static final class ThreadItem extends java.lang.Thread {

		public final ThreadNode node;

		public Object run;

		public Runnable task;

		public ThreadItem(final ThreadPool pool, final ThreadGroup group) {
			super(group, "");
			this.node = new ThreadNode(pool, this);
		}

		@Override
		public void run() {
			this.node.pool.runItem(this);
		}

	}

	/** Diese Klasse implementiert einen Knoten in der doppelt verketteten Liste zur Verwaltung der wartenden {@link ThreadItem}. */
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

	/** Dieses Feld speichert den initialwert für {@link java.lang.Thread#getThreadGroup()} neu erzeugter {@link ThreadItem}. */
	private final ThreadGroup group;

	/** Dieses Feld speichert den initialwert für {@link java.lang.Thread#isDaemon()} neu erzeugter {@link ThreadItem}. */
	private final boolean daemon;

	/** Dieses Feld bildet von den aktuell ausgeführten {@link Runnable Berehnungen} auf die dafür eingesetzten {@link ThreadItem Threads} ab. */
	private final HashMap3<Runnable, ThreadItem> activeMap = new HashMap3<>(0);

	/** Dieses Feld speichert den {@link java.lang.Thread#setName(String) Namen} für die nächsten {@link #start(Runnable) gestartetetn} {@link ThreadItem
	 * Threads}. */
	private String activeName = "ThreadItem";

	/** Dieses Feld speichert die {@link java.lang.Thread#setPriority(int) Priorität} für die nächsten {@link #start(Runnable) gestartetetn} {@link ThreadItem
	 * Threads}. */
	private int activePriority = java.lang.Thread.NORM_PRIORITY;

	/** Dieses Feld speichert die auf ihre Wiederverwendung wartenden {@link ThreadItem Threads}. */
	private final ThreadNode waitingList = new ThreadNode(this);

	/** Dieses Feld speichert die Anzahl der in {@link #waitingList} enthaltenen {@link ThreadItem Threads}. */
	private int waitingCount;

	/** Dieses Feld speichert die Anzahl der in {@link #waitingList} mindestens vorgehalten {@link ThreadItem Threads}. */
	private int waitingReserve;

	/** Dieses Feld speichert die Begrenzung der Lebenszeit der in {@link #waitingList} enthaltenen {@link ThreadItem Threads}. */
	private long waitingTimeout = 60000;

	/** Dieser Konstruktor ist eine Abkürzung für {@link #ThreadPool(boolean) new ThreadPool(true)}. */
	public ThreadPool() {
		this(true);
	}

	/** Dieser Konstruktor ist eine Abkürzung für {@link #ThreadPool(ThreadGroup, boolean) new ThreadPool(null, daemon)}. */
	public ThreadPool(final boolean daemon) {
		this(null, daemon);
	}

	/** Dieser Konstruktor initialisiert das den {@link ThreadPool} mit den gegebenen Merkmalen zur Erzeugung von Threads, ohne {@link #setWaitingReserve(int)
	 * Reserve} und mit einer {@link #setWaitingTimeout(long) Wiederverwendungszeit} von einer Minute.
	 *
	 * @see java.lang.Thread#setDaemon(boolean)
	 * @see java.lang.Thread#getThreadGroup()
	 * @param group Thread-Gruppe zur Erzeugung der Threads oder {@code null}.
	 * @param daemon {@code true}, wenn zur Auswertung der Berechnung Daemon-Threads eingesetzt werden sollen. */
	public ThreadPool(ThreadGroup group, final boolean daemon) {
		if (group == null) {
			final SecurityManager security = System.getSecurityManager();
			if (security != null) {
				group = security.getThreadGroup();
			}
			if (group == null) {
				group = java.lang.Thread.currentThread().getThreadGroup();
			}
		}
		this.group = group;
		this.daemon = daemon;
	}

	/** Diese Methode implementiert {@link ThreadItem#run()}. */
	final void runItem(final ThreadItem item) {
		while (true) {
			final Runnable task = this.getTask(item);
			if (task == null) return;
			this.runTask(item, task);
		}
	}

	/** Diese Methode führt den gegebenen {@code task} aus, entfernt ihn anschließend aus {@link #activeMap}, löst seine Verbindung zum gegebenen {@code item} und
	 * trägt das {@code item} nach fehlerfreier Berechnung in die {@link #waitingList} ein. */
	private final void runTask(final ThreadItem item, final Runnable task) {
		boolean okay = false;
		try {
			task.run();
			okay = true;
		} finally {
			synchronized (this.activeMap) {
				item.run = null;
				item.task = null;
				this.activeMap.remove(task);
				this.activeMap.notifyAll();
				if (okay) {
					item.node.insert(this.waitingList);
					this.waitingCount++;
					java.lang.Thread.interrupted();
					this.checkActive();
				}
			}
		}
	}

	/** Diese Methode wartet auf die Bestückung des gegebenen Threads mit einer Berechnung und gibt diese zurück. Wenn der Thread verworfen wurde, wird
	 * {@code null} geliefert. */
	private final Runnable getTask(final ThreadItem item) {
		synchronized (this.waitingList) {
			synchronized (this.activeMap) {
				final Runnable task = item.task;
				if (task != null) return task;
			}
			final long start = System.currentTimeMillis();
			long sleep = this.waitingTimeout;
			while (true) {
				try {
					this.waitingList.wait(sleep);
				} catch (final InterruptedException ignore) {}
				synchronized (this.activeMap) {
					final Runnable task = item.task;
					if (task != null) return task;
					if ((this.waitingTimeout == 0) || (this.waitingCount <= this.waitingReserve)) {
						sleep = 0;
					} else {
						sleep = (start + this.waitingTimeout) - System.currentTimeMillis();
						if (sleep <= 0) {
							item.node.delete();
							return null;
						}
					}
				}
			}
		}
	}

	/** Diese Methode liefert den aktiven Thread zur gegebenen Berechnung oder {@code null}.
	 *
	 * @param task Berechnung.
	 * @return Thread oder null.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist. */
	private final ThreadItem getThread(final Runnable task) throws NullPointerException {
		return this.activeMap.get(Objects.notNull(task));
	}

	/** Diese Methode liefert die aktiven Threads zu den gegebenen Berechnungen.
	 *
	 * @param tasks Berechnungen.
	 * @return Threads.
	 * @throws NullPointerException Wenn {@link java.lang.Thread#interrupt()} diese auslöst. */
	private final HashSet3<ThreadItem> getThreads(final Iterable<? extends Runnable> tasks) throws NullPointerException {
		final HashSet3<ThreadItem> items = new HashSet3<>(10);
		for (final Runnable task: tasks) {
			items.add(this.getThread(task));
		}
		items.remove(null);
		return items;
	}

	/** Diese Methode halbiert die {@link HashMap3#capacity() Kapazität} der {@link #activeMap}, wenn diese Kapazität unter 25 % ausgenutzt wird. */
	private final void checkActive() {
		if (this.activeMap.size() >= (this.activeMap.capacity() / 4)) return;
		this.activeMap.allocate(this.activeMap.capacity() / 2);
	}

	/** Diese Methode {@link Object#notifyAll() benachrichtigt} alle auf {@link #waitingList} {@link Object#wait(long) wartenden} Threads. */
	private final void checkWaiting() {
		synchronized (this.waitingList) {
			this.waitingList.notifyAll();
		}
	}

	/** Diese Methode implementiert {@link #join(long, Runnable)} ohne Synchronisation. */
	private final void joinImpl(final ThreadItem item, final long timeout) throws InterruptedException {
		if (item == null) return;
		final Object run = item.run;
		if (timeout == 0) {
			while (true) {
				this.activeMap.wait(0);
				if (item.run != run) return;
			}
		} else {
			final long until = System.currentTimeMillis() + timeout;
			long sleep = timeout;
			while (true) {
				this.activeMap.wait(sleep);
				if (item.run != run) return;
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

	/** Diese Methode wartet auf den Abschluss der gegebenen Berechnung, sofern diese aktuell {@link #isAlive(Runnable) verarbeitet} wird. Wenn die gegebene
	 * Wartezeit nicht {@code 0} ist, wird höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void join(final long timeout, final Runnable task) throws NullPointerException, IllegalArgumentException, InterruptedException {
		if (timeout < 0) throw new IllegalArgumentException();
		synchronized (this.activeMap) {
			this.joinImpl(this.getThread(task), timeout);
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

	/** Diese Methode {@link #join(long, Runnable) wartet} auf den Abschluss der gegebenen Berechnungen, sofern diese aktuell {@link #isAlive(Runnable)
	 * verarbeitet} werden. Wenn die gegebene Wartezeit nicht {@code 0} ist, wird höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code timeout} negativ ist.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void joinAll(final long timeout, final Iterable<? extends Runnable> tasks)
		throws NullPointerException, IllegalArgumentException, InterruptedException {
		if (timeout < 0) throw new IllegalArgumentException();
		synchronized (this.activeMap) {
			this.joinAllImpl(this.getThreads(tasks), timeout);
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
		if (timeout < 0) throw new IllegalArgumentException();
		synchronized (this.activeMap) {
			this.joinAllImpl(new ArrayList<>(this.activeMap.values()), timeout);
		}
	}

	/** Diese Methode implementiert {@link #start(Runnable)} ohne Synchronisation. */
	private final void startImpl(final Runnable task) throws IllegalThreadStateException {
		if (this.activeMap.containsKey(Objects.notNull(task))) throw new IllegalThreadStateException();
		final ThreadNode node = this.waitingList.next;
		final ThreadItem item;
		if (this.waitingList != node) {
			item = node.item;
			node.delete();
			this.waitingCount--;
		} else {
			item = new ThreadItem(this, this.group);
			item.setDaemon(this.daemon);
			item.start();
		}
		item.run = new Object();
		item.setName(this.activeName);
		if (item.getPriority() != this.activePriority) {
			item.setPriority(this.activePriority);
		}
		this.activeMap.put(task, item);
		item.task = task;
	}

	/** Diese Methode startet die gegebene Berechnung in einem eigenen {@link Thread}.
	 *
	 * @see #join(long, Runnable)
	 * @see #interrupt(Runnable)
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist.
	 * @throws IllegalThreadStateException Wenn die Berechnung bereits {@link #isAlive(Runnable) verarbeitet} wird. */
	public void start(final Runnable task) throws NullPointerException, IllegalThreadStateException {
		synchronized (this.activeMap) {
			this.checkActive();
			this.startImpl(task);
		}
		this.checkWaiting();
	}

	/** Diese Methode ist eine Abkürzung für {@link #startAll(Iterable) this.startAll(Arrays.asList(tasks))}.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalThreadStateException Wenn {@link #startAll(Iterable)} diese auslöst. */
	public void startAll(final Runnable... tasks) throws NullPointerException, IllegalThreadStateException {
		this.startAll(Arrays.asList(tasks));
	}

	/** Diese Methode {@link #start(Runnable) startet} die gegebenen Berechnungen in jeweils einem eigenen {@link Thread}.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws IllegalThreadStateException Wenn {@link #start(Runnable)} diese auslöst. Dies kann auch auftreten, wenn {@code tasks} Duplikate enthält. */
	public void startAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, IllegalThreadStateException {
		synchronized (this.activeMap) {
			for (final Runnable task: tasks) {
				this.startImpl(task);
			}
			this.checkActive();
		}
		this.checkWaiting();
	}

	/** Diese Methode implementiert {@link #interrupt(Runnable)} ohne Synchronisation. */
	private final void interruptImpl(final ThreadItem item) throws SecurityException {
		if (item == null) return;
		item.interrupt();
	}

	/** Diese Methode {@link java.lang.Thread#interrupt() unterbricht} die gegebene Berechnung, sofern diese aktuell {@link #isAlive(Runnable) verarbeitet} wird.
	 *
	 * @param task Berechnung.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist.
	 * @throws SecurityException Wenn {@link java.lang.Thread#interrupt()} diese auslöst. */
	public void interrupt(final Runnable task) throws NullPointerException, SecurityException {
		synchronized (this.activeMap) {
			this.interruptImpl(this.getThread(task));
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
	 * @throws SecurityException Wenn {@link java.lang.Thread#interrupt()} diese auslöst. */
	public void interruptAll(final Runnable... tasks) throws NullPointerException, SecurityException { // ok
		this.interruptAll(Arrays.asList(tasks));
	}

	/** Diese Methode {@link java.lang.Thread#interrupt() unterbricht} die gegebenen Berechnungen, sofern diese aktuell {@link #isAlive(Runnable) verarbeitet}
	 * werden.
	 *
	 * @param tasks Berechnungen.
	 * @throws NullPointerException Wenn {@code tasks} {@code null} ist oder enthält.
	 * @throws SecurityException Wenn {@link java.lang.Thread#interrupt()} diese auslöst. */
	public void interruptAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, SecurityException {
		synchronized (this.activeMap) {
			this.interruptAllImpl(this.getThreads(tasks));
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #interruptAll(Iterable) this.interruptAll(this.getActiveTasks())}.
	 *
	 * @see #getActiveTasks()
	 * @throws SecurityException Wenn {@link java.lang.Thread#interrupt()} diese auslöst. */
	public void interruptAllActive() throws SecurityException {
		synchronized (this.activeMap) {
			this.interruptAllImpl(new ArrayList<>(this.activeMap.values()));
		}
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebene Berechnung {@link #start(Runnable) gestartet} und noch nicht abgeschlossen wurde.
	 *
	 * @param task Berechnung.
	 * @return {@code true}, wenn die Berechnung aktuell ausgeführt wird; sonst {@code false}.
	 * @throws NullPointerException Wenn {@code task} {@code null} ist. */
	public boolean isAlive(final Runnable task) throws NullPointerException {
		synchronized (this.activeMap) {
			return this.activeMap.containsKey(Objects.notNull(task));
		}
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn zur Auswertung der Berechnung Daemon-Threads eingesetzt werden. Wenn sie {@code false} liefert,
	 * werden hierfür User-Threads eingesetzt.
	 *
	 * @see java.lang.Thread#setDaemon(boolean)
	 * @return {@code true} für Daemon-Threads; {@code false} für User-Threads. */
	public boolean isDaemon() {
		return this.daemon;
	}

	/** Diese Methode gibt die {@link ThreadGroup} zurück, die zur Erzeugung neuer Threads verwendet wird. Diese wurd im
	 * {@link ThreadPool#ThreadPool(ThreadGroup, boolean) Konstruktor} initialisiert.
	 *
	 * @return {@link ThreadGroup} zur {@link java.lang.Thread#Thread(ThreadGroup, String) Erzeugung} neuer Threads. */
	public ThreadGroup geThreadGroup() {
		return this.group;
	}

	/** Diese Methode gibt die Liste der {@link #isAlive(Runnable) aktuell ausgeführten} Berehnungen zurück.
	 *
	 * @return Liste ausgeführter Berehnungen. */
	public Runnable[] getActiveTasks() {
		synchronized (this.activeMap) {
			return this.activeMap.keySet().toArray(new Runnable[this.activeMap.size()]);
		}
	}

	/** Diese Methode gibt die Anzahl der {@link #isAlive(Runnable) aktuell ausgeführten} Berehnungen zurück.
	 *
	 * @return Anzahl ausgeführter Berehnungen. */
	public int getActiveCount() {
		synchronized (this.activeMap) {
			return this.activeMap.size();
		}
	}

	/** Diese Methode gibt den {@link java.lang.Thread#getName() Namen} für die Threads der nächsten {@link #start(Runnable) gestarteten} Berechnungen zurück.
	 *
	 * @return Name der nächsten Threads. */
	public String getActiveName() {
		synchronized (this.activeMap) {
			return this.activeName;
		}
	}

	/** Diese Methode gibt die {@link java.lang.Thread#getPriority() Priorität} für die Threads der nächsten {@link #start(Runnable) gestarteten} Berechnungen
	 * zurück.
	 *
	 * @return Priorität der nächsten Threads. */
	public int getActivePriority() {
		synchronized (this.activeMap) {
			return this.activePriority;
		}
	}

	/** Diese Methode gibt die Anzahl der aktuell auf ihre Wiederverwendung wartenden Threads zurück.
	 *
	 * @return Anzahl wiederverwendbarer Threads. */
	public int getWaitingCount() {
		synchronized (this.activeMap) {
			return this.waitingCount;
		}
	}

	/** Diese Methode gibt die maximale Anzahl der auf ihre Wiederverwendung wartenden und als Reserve vorgehalten Threads zurück.
	 *
	 * @return Reserve an wartenden Threads. */
	public int getWaitingReserve() {
		synchronized (this.waitingList) {
			return this.waitingReserve;
		}
	}

	/** Diese Methode gibt Begrenzug der Lebenszeit der auf ihre Wiederverwendung wartenden Threads zurück.
	 *
	 * @return Maximale Lebenszeit in Millisekunden oder {@code 0}, bei unbegrenzter Lebenszeit. */
	public long getWaitingTimeout() {
		synchronized (this.waitingList) {
			return this.waitingTimeout;
		}
	}

	/** Diese Methode setzt den {@link java.lang.Thread#getName() Namen} für die Threads der nächsten {@link #start(Runnable) gestarteten} Berechnungen.
	 *
	 * @param value Name.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public void setActiveName(final String value) throws NullPointerException {
		synchronized (this.activeMap) {
			this.activeName = Objects.notNull(value);
		}
	}

	/** Diese Methode setzt die {@link java.lang.Thread#getPriority() Priorität} für die Threads der nächsten {@link #start(Runnable) gestarteten} Berechnungen.
	 *
	 * @param value Priorität.
	 * @throws IllegalArgumentException Wenn {@link java.lang.Thread#setPriority(int)} diese auslösen würde. */
	public void setActivePriority(final int value) throws IllegalArgumentException {
		if ((value < java.lang.Thread.MIN_PRIORITY) || (value > java.lang.Thread.MAX_PRIORITY)) throw new IllegalArgumentException();
		synchronized (this.activeMap) {
			this.activePriority = value;
		}
	}

	/** Diese Methode setzt die maximale Anzahl der auf ihre Wiederverwendung wartenden und als Reserve vorgehalten Threads.
	 *
	 * @param value Reserve an wartenden Threads.
	 * @throws IllegalArgumentException Wenn {@code value} negativ ist. */
	public void setWaitingReserve(final int value) throws IllegalArgumentException {
		if (value < 0) throw new IllegalArgumentException();
		synchronized (this.waitingList) {
			this.waitingReserve = value;
		}
	}

	/** Diese Methode setzt die Begrenzug der Lebenszeit der auf ihre Wiederverwendung wartenden Threads.
	 *
	 * @param value Maximale Lebenszeit in Millisekunden oder {@code 0}, bei unbegrenzter Lebenszeit.
	 * @throws IllegalArgumentException Wenn {@code value} negativ ist. */
	public void setWaitingTimeout(final long value) throws IllegalArgumentException {
		if (value < 0) throw new IllegalArgumentException();
		synchronized (this.waitingList) {
			this.waitingTimeout = value;
			this.waitingList.notifyAll();
		}
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.isDaemon(), this.getWaitingReserve(), this.getWaitingTimeout());
	}

}
