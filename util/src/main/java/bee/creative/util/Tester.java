package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zur Messung der Rechenzeit sowie der Speicherbelegung, die von einer
 * {@link Test Testmethode} benötigt werden.
 * <p>
 * Im nachfolgenden Beispiel wird eine anonyme {@link Test Testmethode} initialisiert und gleich vermessen:
 * 
 * <pre>
 * Tester result = new Tester(new Test() {
 * 
 * 	public void run() {
 * 		// ...
 * 	}
 * 
 * });
 * </pre>
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Tester {

	/**
	 * Diese Schnittstelle definiert die Testmethode eines {@link Tester}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface Test {

		/**
		 * Diese Methode führt den Test aus. Ein gegebenenfalls geworfenes {@link Throwable} wird dann im {@link Tester}
		 * gespeichert.
		 * 
		 * @throws Throwable Wenn während des Tests ein Fehler eintritt.
		 */
		public void run() throws Throwable;

	}

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
	 * Dieses Feld speichert das {@link Throwable} der Testmethode oder {@code null}.
	 */
	public final Throwable throwable;

	/**
	 * Dieser Konstrukteur ruft die gegebenen Testmethode auf und ermittelt die Messwerte. Zur Ermittlung der
	 * Speicherstände vor und nach dem Aufruf der Testmethode wird {@link Runtime#gc()} aufgerufen.
	 * 
	 * @param test Testmethode.
	 * @throws NullPointerException Wenn die gegebene Testmethode {@code null} ist.
	 */
	public Tester(final Test test) throws NullPointerException {
		if(test == null) throw new NullPointerException("method is null");
		final Runtime runtime = Runtime.getRuntime();
		Throwable throwable = null;
		runtime.gc();
		this.enterMemory = runtime.totalMemory() - runtime.freeMemory();
		this.enterTime = System.nanoTime();
		try{
			test.run();
		}catch(final Throwable e){
			throwable = e;
		}
		this.leaveTime = System.nanoTime();
		runtime.gc();
		this.leaveMemory = runtime.totalMemory() - runtime.freeMemory();
		this.usedTime = this.leaveTime - this.enterTime;
		this.usedMemory = this.leaveMemory - this.enterMemory;
		this.throwable = throwable;
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
