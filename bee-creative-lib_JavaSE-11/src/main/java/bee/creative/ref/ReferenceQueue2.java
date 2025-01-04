package bee.creative.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen {@link ReferenceQueue}, der automatisch bereinigt wird. Jede über {@link #poll()}, {@link #remove()} oder
 * {@link #remove(long)} entfernte {@link Reference} wird dazu an {@link #customRemove(Reference)} übergeben, sofern sie nicht {@code null} ist. In dieser
 * Methode kann dann auf das Bereinigen der {@link Reference} reagiert werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Objekte, auf welche die verwalteten {@link Reference} verweisen. */
public class ReferenceQueue2<T> extends ReferenceQueue<T> {

	public ReferenceQueue2() {
		new QueueNode(this);
	}

	@Override
	public final Reference<? extends T> poll() {
		var result = super.poll();
		if (result == null) return null;
		this.customRemove(result);
		return result;
	}

	public final void pollAll() {
		while (this.poll() != null) {}
	}

	@Override
	public final Reference<? extends T> remove() throws InterruptedException {
		return this.remove(0);
	}

	@Override
	public final Reference<? extends T> remove(long timeout) throws IllegalArgumentException, InterruptedException {
		var result = super.remove(timeout);
		if (result == null) return null;
		this.customRemove(result);
		return result;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this);
	}

	/** Diese Methode wird beim Bereinigen einer {@link Reference} über {@link #poll()}, {@link #remove()} bzw. {@link #remove(long)} mit der entfernten
	 * {@link Reference} aufgerufen, sofern diese nicht {@code null} ist. Das Bereinigen sollte so schnell es geht behandelt werden. Der aktuelle {@link Thread}
	 * sollte hierfür keinesfalls längere Zeit warten müssen.
	 *
	 * @param reference bereinigte {@link Reference}. */
	protected void customRemove(Reference<? extends T> reference) {
	}

	private static final class QueueNode extends WeakReference<ReferenceQueue2<?>> {

		QueueNode prev;

		QueueNode next;

		QueueNode(ReferenceQueue2<?> queue) {
			super(queue);
			var head = QueueNode.head;
			synchronized (head) {
				head.prev = ((this.prev = (this.next = head).prev).next = this);
			}
		}

		private static final QueueNode head = new QueueNode();

		private QueueNode() {
			super(null);
			this.prev = (this.next = this);
			var thread = new Thread(() -> {
				while (true) {
					var head = this;
					var node = head;
					synchronized (head) {
						node = head.next;
					}
					while (head != node) {
						var queue = node.get();
						if (queue != null) {
							try {
								queue.pollAll();
							} catch (Throwable ignored) {}
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
					} catch (Throwable ignored) {}
				}
			}, "ReferenceQueue2-Thread");
			thread.setDaemon(true);
			thread.start();
		}

	}

}
