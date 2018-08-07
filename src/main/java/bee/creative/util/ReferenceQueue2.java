package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert einen {@link ReferenceQueue}, der automatisch bereinigt wird. Jede über {@link #poll()}, {@link #remove()} oder
 * {@link #remove(long)} entfernte {@link Reference} wird dabei an {@link #customRemove(Reference)} übergeben, sofern sie nicht {@code null} ist. In dieser
 * Methode kann dann auf das Entfernen der {@link Reference} reagiert werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GObject> Typ der Objekte, auf welches die verwalteten {@link Reference} verweisen. */
public class ReferenceQueue2<GObject> extends ReferenceQueue<GObject> {

	@SuppressWarnings ("javadoc")
	private static final class QueueNode extends WeakReference<Object> {

		public static final QueueNode head = new QueueNode(null);

		public QueueNode prev;

		public QueueNode next;

		public QueueNode(final Object referent) {
			super(referent);
		}

	}

	@SuppressWarnings ("javadoc")
	private static final class QueueThread extends Thread {

		public QueueThread() {
			super(QueueThread.class.getSimpleName());
			this.setPriority(Thread.MAX_PRIORITY);
			this.setDaemon(true);
			this.start();
		}

		@Override
		public void run() {
			while (true) {
				final QueueNode head = QueueNode.head;
				QueueNode node;
				synchronized (head) {
					node = head.next;
				}
				while (head != node) {
					final ReferenceQueue2<?> queue = (ReferenceQueue2<?>)node.get();
					if (queue != null) {
						try {
							while (queue.poll() != null) {}
						} catch (final Throwable ignored) {}
						synchronized (head) {
							node = node.next;
						}
					} else {
						synchronized (head) {
							node = ((node.next.prev = node.prev).next = node.next);
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (final Throwable ignored) {}
			}
		}
	}

	static {
		new QueueThread();
	}

	{
		final QueueNode head = QueueNode.head, node = new QueueNode(this);
		synchronized (head) {
			head.prev = ((node.prev = (node.next = head).prev).next = node);
		}
	}

	private final Reference<? extends GObject> remove(final Reference<? extends GObject> reference) {
		if (reference == null) return null;
		this.customRemove(reference);
		return reference;
	}

	protected void customRemove(final Reference<? extends GObject> reference) {

	}

	@Override
	public final Reference<? extends GObject> poll() {
		return this.remove(super.poll());
	}

	@Override
	public final Reference<? extends GObject> remove() throws InterruptedException {
		return this.remove(0);
	}

	@Override
	public final Reference<? extends GObject> remove(final long timeout) throws IllegalArgumentException, InterruptedException {
		return this.remove(super.remove(timeout));
	}

}
