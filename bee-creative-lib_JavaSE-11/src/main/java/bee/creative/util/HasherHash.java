package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Berechnung des {@link Object#hashCode() Streuwerts} eines gegebenen Objekts.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface HasherHash {

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts zurück.
	 *
	 * @param input Objekt.
	 * @return {@link Object#hashCode() Streuwert}. */
	int hash(Object input);

}