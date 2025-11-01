package bee.creative.util;

import static bee.creative.util.Comparators.compare;
import static bee.creative.util.Iterables.iterableFromArray;
import static bee.creative.util.Properties.propertyFromValue;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.nanoTime;
import java.util.Comparator;
import bee.creative.lang.Callable2;
import bee.creative.lang.Objects;
import bee.creative.lang.Runnable2;

/** Diese Klasse implementiert ein Objekt zur Messung der Rechenzeit sowie der Speicherbelegung, die von einer {@link Runnable2 Testmethode} benötigt werden.
 * <p>
 * Im nachfolgenden Beispiel wird eine anonyme {@link Runnable2 Testmethode} instanziiert und vermessen: <pre> var tester = new Tester(() -> { ... });
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Tester {

	/** Dieses Feld speichert den {@link Comparator} zu {@link #usedTime}. */
	public static final Comparator3<Tester> USED_TIME_ORDER = (tester1, tester2) -> compare(tester1.usedTime, tester2.usedTime);

	/** Dieses Feld speichert den {@link Comparator} zu {@link #usedMemory}. */
	public static final Comparator3<Tester> USED_MEMORY_ORDER = (tester1, tester2) -> compare(tester1.usedMemory, tester2.usedMemory);

	/** Diese Methode erzeugt den {@link Tester} zu {@code task.run()} und gibt ihn über {@link System#err} aus. Wenn sein {@link #cause} nicht {@code null} ist,
	 * wird diese ebenfalls darüber ausgegeben.
	 *
	 * @param task Testmethode. */
	public static void testRun(Runnable2 task) {
		var run = new Tester(task);
		System.err.println(run);
		if (run.cause == null) return;
		run.cause.printStackTrace();
	}

	/** Diese Methode erzeugt den {@link Tester} zu {@code task.call()}, gibt ihn über {@link System#err} aus und lifert das Ergebnis von {@code task.call()}.
	 * Wenn sein {@link #cause} nicht {@code null} ist, wird diese ebenfalls darüber ausgegeben und {@code null} geliefert.
	 *
	 * @param task Testmethode. */
	public static <T> T testCall(Callable2<T> task) {
		var res = propertyFromValue((T)null);
		testRun(() -> res.set(task.call()));
		return res.get();
	}

	/** Diese Methode liefert den {@link Tester} zum arithmetischen Mittel ({@link #usedTime} und {@link #usedMemory}) der gegebenen Tester.
	 *
	 * @param testers {@link Tester}, deren arithmetisches Mittel ermitttelt wird.
	 * @return {@code avg}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester avgTester(Tester... testers) throws NullPointerException {
		return avgTester(iterableFromArray(testers));
	}

	/** Diese Methode liefert den {@link Tester} zum arithmetischen Mittel ({@link #usedTime} und {@link #usedMemory}) der gegebenen Tester.
	 *
	 * @param testers {@link Tester}, deren arithmetisches Mittel ermitttelt wird.
	 * @return {@code avg}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester avgTester(Iterable<? extends Tester> testers) throws NullPointerException {
		var count = 0L;
		var usedTime = 0L;
		var usedMemory = 0L;
		for (var tester: testers) {
			count++;
			usedTime += tester.usedTime;
			usedMemory += tester.usedMemory;
		}
		if (count == 0) return new Tester(0, 0);
		return new Tester(usedTime / count, usedMemory / count);
	}

	/** Diese Methode liefert den {@link Tester} zum Minimum ({@link #usedTime} und {@link #usedMemory}) der gegebenen Tester.
	 *
	 * @param testers {@link Tester}, deren Minimum ermitttelt wird.
	 * @return {@code min}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester minTester(Tester... testers) throws NullPointerException {
		return minTester(iterableFromArray(testers));
	}

	/** Diese Methode liefert den {@link Tester} zum Minimum ({@link #usedTime} und {@link #usedMemory}) der gegebenen.
	 *
	 * @param testers {@link Tester}, deren Minimum ermitttelt wird.
	 * @return {@code min}-{@link Tester}.
	 * @throws NullPointerException Wenn {@code testers} {@code null} ist oder enthält. */
	public static Tester minTester(Iterable<? extends Tester> testers) throws NullPointerException {
		var usedTime = MAX_VALUE;
		var usedMemory = MAX_VALUE;
		for (var tester: testers) {
			usedTime = min(usedTime, tester.usedTime);
			usedMemory = min(usedMemory, tester.usedMemory);
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

	/** Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte. Die Messung der Speicherbelegung erfolgt synchron mit Bereinigung durch
	 * {@link Runtime#gc()}.
	 *
	 * @param task Testmethode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public Tester(Runnable2 task) throws NullPointerException {
		this(-1, task);
	}

	/** Dieser Konstruktor ruft die gegebenen Testmethode auf und ermittelt die Messwerte. Wenn das gegebene Interval größer als {@code 0} ist, wird ein
	 * {@link Sampler} zur asynchronen Messung der maximalen Speicherbelegung verwendet. Wenn es kleiner {@code 0} ist, erfolgt die Messung mit Bereinigung durch
	 * {@link Runtime#gc()}.
	 *
	 * @param mode Interval der asynchronen Messung der Speicherbelegung in Millisekunden, nagativ bei synchroner Messung und {@code 0} bei deaktivierter.
	 * @param task Testmethode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public Tester(int mode, Runnable2 task) throws NullPointerException {
		Objects.notNull(task);
		var runtime = getRuntime();
		Throwable cause = null;
		if (mode > 0) {
			var sampler = new Sampler(mode);
			runtime.gc();
			sampler.activate();
			this.enterMemory = runtime.totalMemory() - runtime.freeMemory();
			this.enterTime = nanoTime();
			try {
				task.run();
			} catch (Throwable cause2) {
				cause = cause2;
			} finally {
				this.leaveTime = nanoTime();
				sampler.deactivate();
			}
			runtime.gc();
			this.leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = this.leaveTime - this.enterTime - sampler.usedTime;
			this.usedMemory = max(sampler.usedMemory, this.leaveMemory) - this.enterMemory;
		} else {
			if (mode < 0) {
				runtime.gc();
				Thread.yield();
			}
			this.enterMemory = runtime.totalMemory() - runtime.freeMemory();
			this.enterTime = nanoTime();
			try {
				task.run();
			} catch (Throwable cause2) {
				cause = cause2;
			}
			this.leaveTime = nanoTime();
			if (mode < 0) {
				runtime.gc();
				Thread.yield();
			}
			this.leaveMemory = runtime.totalMemory() - runtime.freeMemory();
			this.usedTime = this.leaveTime - this.enterTime;
			this.usedMemory = this.leaveMemory - this.enterMemory;
		}
		this.cause = cause;
	}

	@Override
	public String toString() {
		return String.format("usedTime: %4.3f ms  usedMemory: %+4.3f MB  cause: %s", this.usedTime / 1000000f, this.usedMemory / 1048576f, this.cause);
	}

	private Tester(long usedTime, long usedMemory) throws NullPointerException {
		this.usedTime = usedTime;
		this.usedMemory = usedMemory;
		this.enterTime = 0;
		this.enterMemory = 0;
		this.leaveTime = usedTime;
		this.leaveMemory = usedMemory;
		this.cause = null;
	}

	/** Diese Klasse implementiert den {@link Thread} zur parallelen Messung der maximale Speicherbelegung in Byte.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	private static final class Sampler extends Thread {

		/** Dieses Feld speichert das Interval in Millisekunden, in dem die Messung erfolgt. Es ist {@code 0}, wenn die Messung beendet werden soll. */
		public int millis;

		/** Dieses Feld speichert die Rechenzeit in Nanosekunden, die zur Messung benötigt wurde.
		 *
		 * @see System#nanoTime() */
		public long usedTime;

		/** Dieses Feld speichert die maximale Speicherbelegung in Byte, die während der Messung ermittelt wurde.
		 *
		 * @see Runtime#freeMemory()
		 * @see Runtime#totalMemory() */
		public long usedMemory;

		/** Dieser Konstruktor initialisiert das Interval in Millisekunden.
		 *
		 * @param millis Interval der Messung in Millisekunden.
		 * @throws IllegalArgumentException Wenn {@code millis <= 0} ist. */
		public Sampler(int millis) throws IllegalArgumentException {
			super("Tester-Thread");
			if (millis <= 0) throw new IllegalArgumentException();
			this.millis = millis;
			this.setPriority(min(currentThread().getPriority() + 1, Thread.MAX_PRIORITY));
		}

		@Override
		public void run() {
			var runtime = getRuntime();
			var enterTime = 0L;
			var leaveTime = 0L;
			while (true) {
				var enterTime2 = nanoTime();
				this.usedTime += leaveTime - enterTime;
				runtime.gc();
				this.usedMemory = max(runtime.totalMemory() - runtime.freeMemory(), this.usedMemory);
				enterTime = enterTime2;
				leaveTime = nanoTime();
				synchronized (this) {
					if (this.millis <= 0) {
						break;
					}
					try {
						this.wait(this.millis);
					} catch (InterruptedException cancel) {
						break;
					}
				}
			}
			this.usedTime += leaveTime - enterTime;
		}

		/** Diese Methode {@link #start() aktiviert} die periodische Messung. */
		public void activate() {
			this.start();
		}

		/** Diese Methode {@link #join() deaktiviert} die periodische Messung. */
		public synchronized void deactivate() {
			try {
				this.millis = 0;
				this.join();
			} catch (InterruptedException ignore) {}
		}

	}

}
