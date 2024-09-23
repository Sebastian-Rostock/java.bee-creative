package bee.creative.ber;

/** Diese Schnittstelle definiert den Empfänger der Referenzen und Elementen für {@link REFMAP#forEach(Object[], REFMAPRUN)}.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface REFMAPRUN {

	/** Diese Methode verarbeitet die gegebene Referenz {@code ref} und das gegebene Elemente {@code val}. */
	void run(int ref, Object val);

}