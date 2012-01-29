package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zur Messung der Rechenzeit sowie der Speicherbelegung, die von eienr
 * Testmethode ({@link Runnable Runnables}) benötigt werden.
 * <p>
 * Im nachfolgenden Beispiel wird ein anonymes {@link Runnable Runnable} als Testmethode initialisiert und gleich
 * vermessen:
 * 
 * <pre>
 * Tester result = new Tester() {
 *   public void run() {
 *     // ...
 *   }
 * });
 * </pre>
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class Tester implements Runnable {

	/**
	 * Dieses Feld speichert die Rechenzeit in Nanosekunden, die von der Testmethode benötigt wurde.
	 * 
	 * @see System#nanoTime()
	 */
	public final long usedTime;

	/**
	 * Dieses Feld speichert die Speicherbelegung in Byte, die von der Testmethode benötigt wurde.
	 * 
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory()
	 */
	public final long usedMemory;

	/**
	 * Dieses Feld speichert den Zeitpunkt in Nanosekunden, an dem die Testmethode betreten wurde.
	 * 
	 * @see System#nanoTime()
	 */
	public final long enterTime;

	/**
	 * Dieses Feld speichert den Speicherstand in Byte, bevor die Testmethode betreten wurde.
	 * 
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory()
	 */
	public final long enterMemory;

	/**
	 * Dieses Feld speichert den Zeitpunkt in Nanosekunden, an dem die Testmethode verlassen wurde.
	 * 
	 * @see System#nanoTime()
	 */
	public final long leaveTime;

	/**
	 * Dieses Feld speichert den Speicherstand in Byte, nachdem die Testmethode verlassen wurde.
	 * 
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory()
	 */
	public final long leaveMemory;

	/**
	 * Dieser Konstrukteur ruft die Testmethode {@link Tester#run()} auf und ermittelt die Messwerte. Zur Ermittlung der
	 * Speicherstände vor und nach dem Aufruf der Testmethode wird {@link Runtime#gc()} aufgerufen.
	 */
	public Tester() {
		final Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		this.enterMemory = runtime.totalMemory() - runtime.freeMemory();
		this.enterTime = System.nanoTime();
		run();
		this.leaveTime = System.nanoTime();
		runtime.gc();
		this.leaveMemory = runtime.totalMemory() - runtime.freeMemory();
		this.usedTime = this.leaveTime - this.enterTime;
		this.usedMemory = this.leaveMemory - this.enterMemory;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(false, true, "Tester", "usedTime", this.usedTime, "usedMemory", this.usedMemory,
			"enterTime", this.enterTime, "leaveTime", this.leaveTime, "enterMemory", this.enterMemory, "leaveMemory",
			this.leaveMemory);
	}

}
