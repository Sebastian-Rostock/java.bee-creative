package bee.creative.lang;

/** Diese Klasse implementiert eine abstrakte {@link Runnable Berechnung}, die als leichtgewichtiger Thread über einen {@link ThreadPool} ausgeführt wird. Das
 * Starten dieses leichtgewichtigen Thread benötigt ca. 100.000 Prozessortakte weniger, als ein nativer {@link java.lang.Thread} dafür benötigt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Thread extends Runnable {

	/** Diese Methode {@link ThreadPool#join(Runnable) wartet} auf den Abschluss dieser Berechnung.
	 *
	 * @throws IllegalThreadStateException Wenn {@link ThreadPool#join(long, Runnable)} diese auslöst. */
	default void join() throws InterruptedException {
		this.getThreadPool().join(this);
	}

	/** Diese Methode {@link ThreadPool#join(long, Runnable) wartet} auf den Abschluss dieser Berechnung. Wenn die gegebene Wartezeit größer {@code 0} ist, wird
	 * höchstens solange gewartet.
	 *
	 * @param timeout Wartezeit in Millisekungen oder {@code 0}.
	 * @throws IllegalThreadStateException Wenn {@link ThreadPool#join(long, Runnable)} diese auslöst.
	 * @throws InterruptedException Wenn {@link ThreadPool#join(long, Runnable)} diese auslöst. */
	default void join(final long timeout) throws IllegalArgumentException, InterruptedException {
		this.getThreadPool().join(timeout, this);
	}

	/** Diese Methode {@link ThreadPool#start(Runnable) startet} diese Berechnung.
	 *
	 * @return {@code true}, wenn die Berechnung gestartet wurde;<br>
	 *         {@code false}, wenn sie bereits {@link #isAlive() verarbeitet} wird. */
	default boolean start() {
		return this.getThreadPool().start(this);
	}

	/** Diese Methode {@link ThreadPool#interrupt(Runnable) unterbricht} diese Berechnung.
	 *
	 * @throws SecurityException Wenn {@link ThreadPool#interrupt(Runnable)} diese auslöst. */
	default void interrupt() throws SecurityException {
		this.getThreadPool().interrupt(this);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Berechnung {@link ThreadPool#interrupt(Runnable) ausgeführt} wird.
	 *
	 * @return {@code true}, wenn diese Berechnung aktuell ausgeführt wird; sonst {@code false}. */
	default boolean isAlive() {
		return this.getThreadPool().isAlive(this);
	}

	/** Diese Methode gibt den {@link ThreadPool} zurück, der für die Ausführung dieser Berechnung verwendet wird. Dieser {@link ThreadPool} ist mindestens wärend
	 * der Ausführung dieser Berechnung konstant. Er wird meist über eine statische Konstante bereitgestellt.
	 *
	 * @return {@link ThreadPool} zur Ausführung dieser Berechnung. */
	ThreadPool getThreadPool();

}
