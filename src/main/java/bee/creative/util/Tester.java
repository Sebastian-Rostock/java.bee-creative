package bee.creative.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import bee.creative.util.Comparators.BaseComparator;

/** Diese Klasse implementiert ein Objekt zur Messung der Rechenzeit sowie der Speicherbelegung, die von einer {@link Method Testmethode} benötigt werden.
 * <p>
 * Im nachfolgenden Beispiel wird eine anonyme {@link Method Testmethode} instanziiert und vermessen: <pre>
 * Tester result = new Tester(new Method() {
 *   public void run() throws Throwable {
 *     ...
 *   }
 * });</pre><br>
 * Als Lambda-Ausdruck wird der TEst entsprechend kürzer: <pre>
 * Tester result = new Tester(() -> { ... });</pre>
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Tester {

	/** Diese Schnittstelle definiert die Testmethode eines {@link Tester}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface Method {

		/** Diese Methode führt den Test aus. Ein gegebenenfalls geworfenes {@link Throwable} wird dann im {@link Tester} gespeichert.
		 *
		 * @throws Throwable Wenn während des Tests ein Fehler eintritt. */
		public void run() throws Throwable;

	}

	/** Diese Klasse implementiert den {@link Thread} zur parallelen Messung der maximale Speicherbelegung in Byte.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Sampler extends Thread {

		/** Dieses Feld speichert das Interval in Millisekunden, in dem die Messung erfolgt. Es ist {@code 0}, wenn die Messung beendet werden soll. */
		int millis;

		/** Dieses Feld speichert die Rechenzeit in Nanosekunden, die zur Messung benötigt wurde.
		 *
		 * @see System#nanoTime() */
		long usedTime;

		/** Dieses Feld speichert die maximale Speicherbelegung in Byte, die während der Messung ermittelt wurde.
		 *
		 * @see Runtime#freeMemory()
		 * @see Runtime#totalMemory() */
		long usedMemory;

		/** Dieser Konstruktor initialisiert das Interval in Millisekunden.
		 *
		 * @param millis Interval der Messung in Millisekunden.
		 * @throws IllegalArgumentException Wenn {@code millis <= 0} ist. */
		public Sampler(final int millis) throws IllegalArgumentException {
			super(Sampler.class.getSimpleName());
			if (millis <= 0) throw new IllegalArgumentException();
			this.millis = millis;
			this.setPriority(Math.min(Thread.currentThread().getPriority() + 1, Thread.MAX_PRIORITY));
		}

		/** Diese Methode aktiviert die periodische Messung.
		 *
		 * @see #start() */
		public final void activate() {
			this.start();
		}

		/** Diese Methode deaktiviert die periodische Messung.
		 *
		 * @see #join() */
		public final void deactivate() {
			this.millis = 0;
			try {
				this.join();
			} catch (final InterruptedException e) {}
		}

		/** {@inheritDoc} */
		@Override
		public final void run() {
			final Runtime runtime = Runtime.getRuntime();
			long enterTime = 0;
			long leaveTime = 0;
			do {
				final long enterTime2 = System.nanoTime();
				this.usedTime += leaveTime - enterTime;
				runtime.gc();
				this.usedMemory = Math.max(runtime.totalMemory() - runtime.freeMemory(), this.usedMemory);
				enterTime = enterTime2;
				leaveTime = System.nanoTime();
				try {
					Thread.sleep(this.millis);
				} catch (final InterruptedException e) {
					break;
				}
			} while (this.millis != 0);
			this.usedTime += leaveTime - enterTime;
		}

	}

	/** Diese Klasse implementiert {@link Tester#USED_TIME_ORDER}. */
	static class UsedTimeComparator extends BaseComparator<Tester> {

		@Override
		public int compare(final Tester tester1, final Tester tester2) {
			return Comparators.compare(tester1.usedTime, tester2.usedTime);
		}

	}

	/** Diese Klasse implementiert {@link Tester#USED_MEMORY_ORDER}. */
	static class UsedMemoryComparator extends BaseComparator<Tester> {

		@Override
		public int compare(final Tester tester1, final Tester tester2) {
			return Comparators.compare(tester1.usedMemory, tester2.usedMemory);
		}

	}

	/** Dieses Feld speichert den {@link Comparator} zu {@link #usedTime}. */
	public static final Comparator<Tester> USED_TIME_ORDER = new UsedTimeComparator();

	/** Dieses Feld speichert den {@link Comparator} zu {@link #usedMemory}. */
	public static final Comparator<Tester> USED_MEMORY_ORDER = new UsedMemoryComparator();

	/** Diese Methode ist eine Abkürzung für liefert den {@link Tester} zum arithmetischen Mittel ({@link #usedTime} und {@link #usedMemory}) der gegebenen
	 * Tester.
	 *
	 * @param testers {@link Tester}, deren arithmetisches Mittel ermitttelt wird.
	 * @return {@code svg}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester fromAvg(final Tester... testers) throws NullPointerException {
		return Tester.fromAvg(Arrays.asList(testers));
	}

	/** Diese Methode liefert den {@link Tester} zum arithmetischen Mittel ({@link #usedTime} und {@link #usedMemory}) der gegebenen Tester.
	 *
	 * @param testers {@link Tester}, deren arithmetisches Mittel ermitttelt wird.
	 * @return {@code svg}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester fromAvg(final List<Tester> testers) throws NullPointerException {
		final int count = testers.size();
		if (count == 0) return new Tester(0, 0);
		long usedTime = 0, usedMemory = 0;
		for (final Tester tester: testers) {
			usedTime += tester.usedTime;
			usedMemory += tester.usedMemory;
		}
		return new Tester(usedTime / count, usedMemory / count);
	}

	/** Diese Methode liefert den {@link Tester} zum Minimum ({@link #usedTime} und {@link #usedMemory}) der gegebenen Tester.
	 *
	 * @param testers {@link Tester}, deren Minimum ermitttelt wird.
	 * @return {@code min}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester fromMin(final Tester... testers) throws NullPointerException {
		return Tester.fromMin(Arrays.asList(testers));
	}

	/** Diese Methode liefert den {@link Tester} zum Minimum ({@link #usedTime} und {@link #usedMemory}) der gegebenen.
	 *
	 * @param testers {@link Tester}, deren Minimum ermitttelt wird.
	 * @return {@code min}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester fromMin(final List<Tester> testers) throws NullPointerException {
		long usedTime = Long.MAX_VALUE, usedMemory = Long.MAX_VALUE;
		for (final Tester tester: testers) {
			usedTime = Math.min(usedTime, tester.usedTime);
			usedMemory = Math.min(usedMemory, tester.usedMemory);
		}
		return new Tester(usedTime, usedMemory);
	}

	/** Dieses Feld speichert die Rechenzeit in Nanosekunden, die von der Testmethode benötigt wurde.
	 *
	 * @see System#nanoTime() */
	public final long usedTime;

	/** Dieses Feld speichert die Speicherbelegung in Byte, die von der Testmethode (maximal) benötigt wurde.
	 *
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory() */
	public final long usedMemory;

	/** Dieses Feld speichert den Zeitpunkt in Nanosekunden, an dem die Testmethode betreten wurde.
	 *
	 * @see System#nanoTime() */
	public final long enterTime;

	/** Dieses Feld speichert den Speicherstand in Byte, bevor die Testmethode betreten wurde.
	 *
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory() */
	public final long enterMemory;

	/** Dieses Feld speichert den Zeitpunkt in Nanosekunden, an dem die Testmethode verlassen wurde.
	 *
	 * @see System#nanoTime() */
	public final long leaveTime;

	/** Dieses Feld speichert den Speicherstand in Byte, nachdem die Testmethode verlassen wurde.
	 *
	 * @see Runtime#freeMemory()
	 * @see Runtime#totalMemory() */
	public final long leaveMemory;

	/** Dieses Feld speichert die Fehlerursache, wenn die Testmethode eiene ausnahme auslöst, oder {@code null}. */
	public final Throwable cause;

	@SuppressWarnings ("javadoc")
	private Tester(final long usedTime, final long usedMemory) throws NullPointerException {
		this.usedTime = usedTime;
		this.usedMemory = usedMemory;
		this.enterTime = 0;
		this.enterMemory = 0;
		this.leaveTime = usedTime;
		this.leaveMemory = usedMemory;
		this.cause = null;
	}

	/** Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte.<br>
	 * Die Messung der Speicherbelegung erfolgt synchron mit Bereinigung durch {@link Runtime#gc()}.
	 *
	 * @param method Testmethode.
	 * @throws NullPointerException Wenn die gegebene Testmethode {@code null} ist. */
	public Tester(final Method method) throws NullPointerException {
		this(-1, method);
	}

	/** Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte.<br>
	 * Wenn das gegebene Interval größer als {@code 0} ist, wird ein {@link Sampler} zur asynchronen Messung der maximalen Speicherbelegung verwendet. Wenn es
	 * kleiner {@code 0} ist, erfolgt die Messung mit Bereinigung durch {@link Runtime#gc()}.
	 *
	 * @param mode Interval der asynchronen Messung der Speicherbelegung in Millisekunden, nagativ bei synchroner Messung und {@code 0} bei deaktivierter.
	 * @param method Testmethode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public Tester(final int mode, final Method method) throws NullPointerException {
		Objects.notNull(method);
		final Runtime runtime = Runtime.getRuntime();
		Throwable cause = null;
		final long enterMemory, enterTime, leaveMemory, leaveTime;
		if (mode > 0) {
			final Sampler sampler = new Sampler(mode);
			runtime.gc();
			sampler.activate();
			enterMemory = runtime.totalMemory() - runtime.freeMemory();
			enterTime = System.nanoTime();
			try {
				method.run();
			} catch (final Throwable cause2) {
				cause = cause2;
			} finally {
				leaveTime = System.nanoTime();
				sampler.deactivate();
			}
			runtime.gc();
			leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = leaveTime - enterTime - sampler.usedTime;
			this.usedMemory = Math.max(sampler.usedMemory, leaveMemory) - enterMemory;
		} else {
			if (mode < 0) {
				runtime.gc();
				Thread.yield();
			}
			enterMemory = runtime.totalMemory() - runtime.freeMemory();
			enterTime = System.nanoTime();
			try {
				method.run();
			} catch (final Throwable cause2) {
				cause = cause2;
			}
			leaveTime = System.nanoTime();
			if (mode < 0) {
				runtime.gc();
				Thread.yield();
			}
			leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = leaveTime - enterTime;
			this.usedMemory = leaveMemory - enterMemory;
		}
		this.enterTime = enterTime;
		this.enterMemory = enterMemory;
		this.leaveTime = leaveTime;
		this.leaveMemory = leaveMemory;
		this.cause = cause;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return String.format("usedTime: %4.3f ms  usedMemory: %+4.3f MB  cause: %s", this.usedTime / 1000000f, this.usedMemory / 1048576f, this.cause);
	}

}
