package bee.creative.util;

public class Test {

	/**
	 * Dieses Feld speichert die Rechenzeit in Millisekunden.
	 */
	public final long cpu;

	/**
	 * Dieses Feld speichert den Speicherverbrauch in Byte.
	 */
	public final long ram;

	public Test(Runnable runnable) {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long ram1 = runtime.totalMemory() - runtime.freeMemory();
		long cpu1 = System.nanoTime();
		runnable.run();
		long cpu2 = System.nanoTime();
		runtime.gc();
		long ram2 = runtime.totalMemory() - runtime.freeMemory();

		cpu = (cpu2 - cpu1) / 1000000;
		ram = ram2;// - ram1;
	}

	@Override
	public String toString() {
		return String.format("<cpu %6.3f sec><ram %7.3f MB>", cpu / 1000f, ram / 1048576f);
	}
}
