package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Methode zur konfigurierten Erzeugung eines Datensatzes.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes.
 */
public interface Builder<GValue> {

	/**
	 * Diese Methode gibt den Konfigurierten Datensatz zurück.
	 * 
	 * @return Datensatz.
	 * @throws IllegalStateException Wenn die Konfiguration ungenügend ist.
	 */
	public GValue build() throws IllegalStateException;

}
