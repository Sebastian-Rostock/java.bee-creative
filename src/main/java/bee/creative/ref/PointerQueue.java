package bee.creative.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert einen {@link ReferenceQueue}, der automatisch bereinigt wird. Jede über {@link #poll()}, {@link #remove()} oder
 * {@link #remove(long)} entfernte {@link Reference} wird dabei an {@link #customRemove(Reference)} übergeben, sofern sie nicht {@code null} ist. In dieser
 * Methode kann dann auf das Entfernen der {@link Reference} reagiert werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GObject> Typ der Objekte, auf welches die verwalteten {@link Reference} verweisen. */
public class PointerQueue<GObject> extends ReferenceQueue<GObject> {

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
			super("PointerQueueThread");
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
					final PointerQueue<?> queue = (PointerQueue<?>)node.get();
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

	@SuppressWarnings ("javadoc")
	private final Reference<? extends GObject> remove(final Reference<? extends GObject> reference) {
		if (reference == null) return null;
		this.customRemove(reference);
		return reference;
	}

	/** Diese Methode wird beim Entfernen einer {@link Reference} über {@link #poll()}, {@link #remove()} bzw. {@link #remove(long)} mit der entfernten
	 * {@link Reference} aufgerufen, sofern diese nicht {@code null} ist. Die Reaktion auf das Entfernen sollte so schnell es geht behandelt werden. Der aktuelle
	 * {@link Thread} sollte hierfür keinesfalls längere Zeit warten müssen.
	 * 
	 * @param reference {@link Reference}. */
	protected void customRemove(final Reference<? extends GObject> reference) {
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> poll() {
		return this.remove(super.poll());
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> remove() throws InterruptedException {
		return this.remove(0);
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> remove(final long timeout) throws IllegalArgumentException, InterruptedException {
		return this.remove(super.remove(timeout));
	}

}
