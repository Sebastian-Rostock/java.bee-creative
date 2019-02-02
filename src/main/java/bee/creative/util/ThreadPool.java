package bee.creative.util;

import java.util.Arrays;

/** Diese Klasse implementiert einen {@link Thread}-Puffer, welcher Methoden zum {@link #start(Runnable) Starten}, {@link #isAlive(Runnable) Überwachen},
 * {@link #interrupt(Runnable) Unterbrechen} und {@link #join(Runnable) Abwarten} der Berechnung eines {@link Thread} bezüglich beliebiger {@link Runnable}
 * bereitstellt. Jeder inter dazu eingesetzte {@link Thread} arbeitet in einer gegebenen {@link #getPriority() Priorität} und kann nach der Abarbeitung eines
 * {@link Runnable} innerhalb einer gegebenen {@link #getTimeout() Wartezeit} wiederverwendet werden.
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
	 * Es schützt als Mutex auch die Modifikation von {@link ThreadPool#active}, {@link ThreadPool#waiting}, {@link ThreadItem#join} und {@link ThreadItem#task}.
	 * In {@link ThreadItem#run()} wird das Ende der Berechnung signalisiert, auf welche in {@link ThreadPool#join(Runnable, long)} gewartet wird. */
	private final HashMap3<Runnable, ThreadItem> active = new HashMap3<>();

	/** Dieses Feld speichert die wiederverwendbaren {@link ThreadItem}. */
	private final ThreadNode waiting = new ThreadNode(this);

	private final String name;

	private final int priority;

	private final long timeout;

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
		this.name = Objects.notNull(name);
		this.timeout = cleanup;
		this.priority = priority;
	}

	/** Diese Methode implementiert {@link ThreadItem#run()}. */
	final void run(final ThreadItem item) {
		final long timeout = this.timeout;
		final ThreadNode waiting = this.waiting;
		final HashMap3<?, ?> active = this.active;
		Runnable task;
		while (true) {
			synchronized (waiting) {
				synchronized (active) {
					task = item.task;
				}
				if (task == null) {
					final long until = System.currentTimeMillis() + timeout;
					long delay = timeout;
					while (true) {
						try {
							waiting.wait(delay);
						} catch (final InterruptedException ignore) {}
						if (timeout != 0) {
							delay = until - System.currentTimeMillis();
						}
						synchronized (active) {
							task = item.task;
							if (task != null) {
								break;
							}
							if ((timeout != 0) && (delay <= 0)) {
								item.node.delete();
								return;
							}
						}
					}
				}
			}
			boolean taskReady = false;
			try {
				Thread.interrupted();
				task.run();
				taskReady = true;
			} finally {
				synchronized (active) {
					item.join = null;
					item.task = null;
					active.remove(task);
					active.notifyAll();
					if (taskReady) {
						item.node.insert(waiting);
					}
				}
			}
		}
	}

	// ok
	public void join(final Runnable task) throws NullPointerException, InterruptedException {
		this.join(task, 0);
	}

	// okay
	public void join(final Runnable task, final long timeout) throws NullPointerException, IllegalArgumentException, InterruptedException {
		Objects.notNull(task);
		if (timeout < 0) throw new IllegalArgumentException();
		final HashMap3<?, ThreadItem> active = this.active;
		synchronized (active) {
			final ThreadItem item = active.get(task);
			if (item == null) return;
			final Object join = item.join;
			final long until = System.currentTimeMillis() + timeout;
			long delay = timeout;
			while (true) {
				active.wait(delay);
				if (item.join != join) return;
				if (timeout != 0) {
					delay = until - System.currentTimeMillis();
					if (delay <= 0) return;
				}
			}
		}
	}

	public void joinAll(final Runnable... tasks) throws NullPointerException, InterruptedException { // ok
		this.joinAll(tasks, 0);
	}

	public void joinAll(final Runnable[] tasks, final long timeout) throws NullPointerException, IllegalArgumentException, InterruptedException { // ok
		this.joinAll(Arrays.asList(tasks), 0);
	}

	public void joinAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, InterruptedException { // ok
		this.joinAll(tasks, 0);
	}

	public void joinAll(final Iterable<? extends Runnable> tasks, final long timeout)
		throws NullPointerException, IllegalArgumentException, InterruptedException { // ok
		for (final Runnable task: tasks) {
			this.join(task, timeout);
		}
	}

	public void start(final Runnable task) throws NullPointerException, IllegalThreadStateException {
		Objects.notNull(task);
		final ThreadNode waiting = this.waiting;
		final HashMap3<Runnable, ThreadItem> active = this.active;
		synchronized (active) {
			if (active.containsKey(task)) throw new IllegalThreadStateException();
			final ThreadNode next = waiting.next;
			final ThreadItem item;
			if (waiting != next) {
				next.delete();
				item = next.item;
			} else {
				item = new ThreadItem(this);
				item.setName(this.name);
				item.setDaemon(true);
				item.setPriority(this.priority);
				item.start();
			}
			item.join = new Object();
			item.task = task;
			active.put(task, item);
			final int capacity = active.capacity() / 3;
			if (active.size() < capacity) {
				active.allocate(capacity);
			}
		}
		synchronized (waiting) {
			waiting.notifyAll();
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

	public void interrupt(final Runnable task) throws NullPointerException, SecurityException {
		final HashMap3<?, ThreadItem> active = this.active;
		synchronized (active) {
			final ThreadItem item = active.get(task);
			if (item == null) return;
			item.interrupt();
		}
	}

	public void interruptAll(final Runnable... tasks) throws NullPointerException, SecurityException { // ok
		this.interruptAll(Arrays.asList(tasks));
	}

	public void interruptAll(final Iterable<? extends Runnable> tasks) throws NullPointerException, SecurityException { // ok
		for (final Runnable task: tasks) {
			this.interrupt(task);
		}
	}

	public boolean isAlive(final Runnable task) throws NullPointerException {// ok
		Objects.notNull(task);
		final HashMap3<?, ?> active = this.active;
		synchronized (active) {
			return active.containsKey(task);
		}
	}

	public Runnable[] activeTasks() {
		final HashMap3<Runnable, ?> active = this.active;
		synchronized (active) {
			return active.keySet().toArray(new Runnable[active.size()]);
		}
	}

	public int activeCount() {
		final HashMap3<?, ?> active = this.active;
		synchronized (active) {
			return active.size();
		}
	}

	public String getName() {
		return this.name;
	}

	public int getPriority() {
		return this.priority;
	}

	public long getTimeout() {
		return this.timeout;
	}

	@Override
	public String toString() {
		return "[" + this.name + "-" + this.priority + "-]";
	}

}
