package bee.creative.lang;

/** Diese Klasse implementiert ein abstraktes {@link Runnable} als leichtgewichtiger Thread, desses Ausführung über einen {@link ThreadPool} realisiert wird.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class Thread implements Runnable {

	/** Diese Methode wartet auf den Abschluss dieses Thread, sofern diese aktuell {@link #isAlive() verarbeitet} wird.
	 *
	 * @throws IllegalThreadStateException Wenn {@link ThreadPool#join(long, Runnable)} diese auslöst. */
	public void join() throws InterruptedException {
		this.geThreadPool().join(this);
	}

	/** Diese Methode wartet auf den Abschluss dieses Thread, sofern diese aktuell {@link #isAlive() verarbeitet} wird. Wenn die gegebene Wartezeit nicht
	 * {@code 0} ist, wird höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @throws IllegalThreadStateException Wenn {@link ThreadPool#join(long, Runnable)} diese auslöst.
	 * @throws InterruptedException Wenn {@link Object#wait(long)} diese auslöst. */
	public void join(final long timeout) throws IllegalArgumentException, InterruptedException {
		this.geThreadPool().join(timeout, this);
	}

	/** Diese Methode startet diesen Thread.
	 *
	 * @throws IllegalThreadStateException Wenn {@link ThreadPool#start(Runnable)} diese auslöst. */
	public void start() throws IllegalThreadStateException {
		this.geThreadPool().start(this);
	}

	/** Diese Methode unterbricht diesen Thread, sofern dieser aktuell {@link #isAlive() verarbeitet} wird.
	 *
	 * @throws SecurityException Wenn {@link ThreadPool#interrupt(Runnable)} diese auslöst. */
	public void interrupt() throws SecurityException {
		this.geThreadPool().interrupt(this);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieser Thread {@link #start() gestartet} und noch nicht abgeschlossen wurde.
	 *
	 * @return {@code true}, wenn der Thread aktuell ausgeführt wird; sonst {@code false}. */
	public boolean isAlive() {
		return this.geThreadPool().isAlive(this);
	}

	/** Diese Methode gibt den {@link ThreadPool} zurück, der für die Ausführung dieses Thread verwendet wird. Dieser {@link ThreadPool} ist wärend der Ausführung
	 * dieses Thread konstant. Er wird meist über eine statische Konstante bereitgestellt.
	 *
	 * @return {@link ThreadPool} zur Ausführung dieses Thread. */
	public abstract ThreadPool geThreadPool();

}
