package bee.creative.xml.coder;

/**
 * Diese Schnittstelle definiert eine Methode zur Berechnung des {@link Object#hashCode() Streuwerts} eines Werts.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts.
 */
public interface Hasher<GValue> {

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Werts zurück.
	 * 
	 * @param value Wert.
	 * @return {@link Object#hashCode() Streuwert} des Werts.
	 */
	public int hash(GValue value);

}
