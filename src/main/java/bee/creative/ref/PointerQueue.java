package bee.creative.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen {@link ReferenceQueue}, der automatisch bereinigt wird. Jede über {@link #poll()}, {@link #remove()} oder
 * {@link #remove(long)} entfernte {@link Reference} wird dabei an {@link #customRemove(Reference)} übergeben, sofern sie nicht {@code null} ist. In dieser
 * Methode kann dann auf das Entfernen der {@link Reference} reagiert werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GObject> Typ der Objekte, auf welches die verwalteten {@link Reference} verweisen. */
public class PointerQueue<GObject> extends ReferenceQueue<GObject> {

	@SuppressWarnings ("javadoc")
	private static final class QueueNode extends WeakReference<PointerQueue<?>> {

		public static final QueueNode head = new QueueNode();

		public QueueNode prev;

		public QueueNode next;

		private QueueNode() {
			super(null);
			this.prev = (this.next = this);
		}

		public QueueNode(final PointerQueue<?> referent) {
			super(referent);
			final QueueNode head = QueueNode.head;
			synchronized (head) {
				head.prev = ((this.prev = (this.next = head).prev).next = this);
			}
		}

	}

	@SuppressWarnings ("javadoc")
	private static final class QueueThread extends Thread {

		public QueueThread() {
			super("PointerQueue-Thread");
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
					final PointerQueue<?> queue = node.get();
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
		new QueueNode(this);
	}

	/** Diese Methode wird beim Entfernen einer {@link Reference} über {@link #poll()}, {@link #remove()} bzw. {@link #remove(long)} mit der entfernten
	 * {@link Reference} aufgerufen, sofern diese nicht {@code null} ist. Die Reaktion auf das Entfernen sollte so schnell es geht behandelt werden. Der aktuelle
	 * {@link Thread} sollte hierfür keinesfalls längere Zeit warten müssen.
	 *
	 * @param reference {@link Reference}. */
	protected void customRemove(final Reference<?> reference) {
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> poll() {
		final Reference<? extends GObject> result = super.poll();
		if (result == null) return null;
		this.customRemove(result);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> remove() throws InterruptedException {
		return this.remove(0);
	}

	/** {@inheritDoc} */
	@Override
	public final Reference<? extends GObject> remove(final long timeout) throws IllegalArgumentException, InterruptedException {
		final Reference<? extends GObject> result = super.remove(timeout);
		if (result == null) return null;
		this.customRemove(result);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this);
	}

}
