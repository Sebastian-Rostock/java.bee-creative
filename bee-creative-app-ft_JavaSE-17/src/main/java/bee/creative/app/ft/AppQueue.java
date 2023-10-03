package bee.creative.app.ft;

import java.util.LinkedList;
import java.util.concurrent.CancellationException;
import bee.creative.lang.Objects;

class AppQueue extends Thread {

	public AppQueue() {
		this.setDaemon(true);
		this.start();
	}

	public void onError(AppProcess proc, Throwable error) {
	}

	/** Diese Methode reagiert auf die Auswahl der nächsten Berechnung.
	 *
	 * @param proc nächste Berechnung oder {@code null} */
	public void onSelect(AppProcess proc) {
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this.queue) {
				this.next = this.queue.pollFirst();
			}
			try {
				this.onSelect(this.next);
			} catch (Throwable error) {
				this.onError(this.next, error);
			}
			if (this.next == null) {
				synchronized (this.queue) {
					try {
						this.queue.wait(3000);
					} catch (InterruptedException ignore) {}
				}
			} else {
				try {
					this.next.run();
				} catch (CancellationException ignore) {
					this.cancel();
				} catch (Throwable error) {
					this.onError(this.next, error);
				}
			}
		}
	}

	public void push(String title, AppTask task) {
		synchronized (this.queue) {
			this.queue.offerLast(new AppProcess(Objects.notNull(task), Objects.notNull(title)));
			this.queue.notifyAll();
		}
	}

	/** Diese Methode bricht alle Berechnungen ab. */
	public void cancel() {
		synchronized (this.queue) {
			this.queue.clear();
			if (this.next == null) return;
			this.next.isCanceled = true;
		}
	}

	public AppProcess current() {
		synchronized (this.queue) {
			return this.next;
		}
	}

	/** Diese Methode liefet nur dann {@code true}, wenn Berechnungen laufen. */
	public boolean isRunning() {
		synchronized (this.queue) {
			return !this.queue.isEmpty() || (this.next != null);
		}
	}

	public boolean isCanceled() {
		synchronized (this.queue) {
			return (this.next != null) && this.next.isCanceled;
		}
	}

	private AppProcess next;

	private final LinkedList<AppProcess> queue = new LinkedList<>();

}