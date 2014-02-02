package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zur Messung der Rechenzeit sowie der Speicherbelegung, die von einer {@link Test Testmethode} benötigt werden.
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
		 * Diese Methode führt den Test aus. Ein gegebenenfalls geworfenes {@link Throwable} wird dann im {@link Tester} gespeichert.
		 * 
		 * @throws Throwable Wenn während des Tests ein Fehler eintritt.
		 */
		public void run() throws Throwable;

	}

	/**
	 * Diese Klasse implementiert den {@link Thread} zur parallelen Messung der maximale Speicherbelegung in Byte.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class Updater extends Thread {

		/**
		 * Dieses Feld speichert das Interval in Millisekunden, in dem die Messung erfolgt. Es ist {@code 0}, wenn die Messung beendet werden soll.
		 */
		int millis;

		/**
		 * Dieses Feld speichert die Rechenzeit in Nanosekunden, die zur Messung benötigt wurde.
		 * 
		 * @see System#nanoTime()
		 */
		long usedTime = 0;

		/**
		 * Dieses Feld speichert die maximale Speicherbelegung in Byte, die während der Messung ermittelt wurde.
		 * 
		 * @see Runtime#freeMemory()
		 * @see Runtime#totalMemory()
		 */
		long usedMemory = 0;

		/**
		 * Dieser Konstruktor initialisiert das Interval in Millisekunden.
		 * 
		 * @param millis Interval der Messung in Millisekunden.
		 * @throws IllegalArgumentException Wenn das gegebene Interval kleiner oder gleich {@code 0} ist.
		 */
		public Updater(final int millis) throws IllegalArgumentException {
			if(millis <= 0) throw new IllegalArgumentException("millis <= 0");
			this.millis = millis;
			this.setPriority(Math.min(Thread.currentThread().getPriority() + 1, Thread.MAX_PRIORITY));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			final Runtime runtime = Runtime.getRuntime();
			long enterTime = 0;
			long leaveTime = 0;
			do{
				final long enterTime2 = System.nanoTime();
				this.usedTime += leaveTime - enterTime;
				runtime.gc();
				this.usedMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), this.usedMemory);
				enterTime = enterTime2;
				leaveTime = System.nanoTime();
				try{
					Thread.sleep(this.millis);
				}catch(final InterruptedException e){
					break;
				}
			}while(this.millis != 0);
			this.usedTime += leaveTime - enterTime;
		}

		/**
		 * Diese Methode aktiviert die periodische Messung.
		 * 
		 * @see #start()
		 */
		public void activate() {
			this.start();
		}

		/**
		 * Diese Methode deaktiviert die periodische Messung.
		 * 
		 * @see #join()
		 */
		public void deactivate() {
			this.millis = 0;
			try{
				this.join();
			}catch(final InterruptedException e){}
		}

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
	 * Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte. Die Messung der Speicherbelegung erfolgt synchron von und nach dem
	 * Aufruf der Testmethode.
	 * 
	 * @param method Testmethode.
	 * @throws NullPointerException Wenn die gegebene Testmethode {@code null} ist.
	 */
	public Tester(final Test method) throws NullPointerException {
		this(0, method);
	}

	/**
	 * Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte. Wenn das gegebene Interval größer als {@code 0} ist, wird ein
	 * {@link Thread} zur asynchronen Messung der Speicherbelegung verwendet.
	 * 
	 * @param millis Interval der asynchronen Messung der Speicherbelegung in Millisekunden oder {@code 0}.
	 * @param method Testmethode.
	 * @throws NullPointerException Wenn die gegebene Testmethode {@code null} ist.
	 * @throws IllegalArgumentException Wenn das gegebene Interval kleiner als {@code 0} ist.
	 */
	public Tester(final int millis, final Test method) throws NullPointerException, IllegalArgumentException {
		if(millis < 0) throw new IllegalArgumentException("millis < 0");
		if(method == null) throw new NullPointerException("method is null");
		final Runtime runtime = Runtime.getRuntime();
		Throwable throwable = null;
		if(millis > 0){
			final Updater updater = new Updater(millis);
			runtime.gc();
			updater.activate();
			final long enterMemory = runtime.totalMemory() - runtime.freeMemory();
			final long enterTime = System.nanoTime();
			try{
				method.run();
			}catch(final Throwable e){
				throwable = e;
			}
			final long leaveTime = System.nanoTime();
			updater.deactivate();
			runtime.gc();
			final long leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = leaveTime - enterTime - updater.usedTime;
			this.usedMemory = Math.max(updater.usedMemory, leaveMemory) - enterMemory;
			this.enterTime = enterTime;
			this.enterMemory = enterMemory;
			this.leaveTime = leaveTime;
			this.leaveMemory = leaveMemory;
		}else{
			runtime.gc();
			final long enterMemory = runtime.totalMemory() - runtime.freeMemory();
			final long enterTime = System.nanoTime();
			try{
				method.run();
			}catch(final Throwable e){
				throwable = e;
			}
			final long leaveTime = System.nanoTime();
			runtime.gc();
			final long leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = leaveTime - enterTime;
			this.usedMemory = leaveMemory - enterMemory;
			this.enterTime = enterTime;
			this.enterMemory = enterMemory;
			this.leaveTime = leaveTime;
			this.leaveMemory = leaveMemory;
		}
		this.throwable = throwable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("usedTime %4.2f ms  usedMemory:  %4.2f MB  throwable: %s", this.usedTime / 1000000f, this.usedMemory / 1048576f, this.throwable);
	}

}
