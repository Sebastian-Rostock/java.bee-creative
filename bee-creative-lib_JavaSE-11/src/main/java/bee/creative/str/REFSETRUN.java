package bee.creative.str;

/** Diese Schnittstelle definiert den Empfänger der Referenzen für {@link REFSET#forEach(int[], REFSETRUN)}.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface REFSETRUN {

	/** Diese Methode verarbeitet die gegebene Referenz {@code ref}. */
	void run(int ref);

}