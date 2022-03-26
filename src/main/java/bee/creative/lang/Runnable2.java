package bee.creative.util;

/** Diese Schnittstelle definiert die Testmethode eines {@link Tester}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Test {

	/** Diese Methode führt den Test aus. Ein gegebenenfalls geworfenes {@link Throwable} wird dann im {@link Tester} gespeichert.
	 *
	 * @throws Throwable Wenn während des Tests ein Fehler eintritt. */
	public void run() throws Throwable;

}