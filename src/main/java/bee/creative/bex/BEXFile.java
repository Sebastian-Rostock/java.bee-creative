package bee.creative.bex;

/** Diese Schnittstelle definiert die Verwaltung aller Element-, Text- und Attributknoten sowie aller Kind- und Attributknotenlisten, die in einem Dokument (vgl.
 * {@code XML} Datei) enthalten sind.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface BEXFile {

	/** Diese Methode gibt das Wurzelelement des Dokuments zurück.
	 * 
	 * @return Wurzelelement. */
	public BEXNode root();

	/** Diese Methode gibt die Knotenliste mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird eine undefinierte Knotenliste
	 * geliefert. Der gegebene Identifikator kann von dem der gelieferten Knotenliste abweichen.
	 * 
	 * @see BEXList#VOID_LIST
	 * @param key Identifikator.
	 * @return Knotenliste. */
	public BEXList list(int key);

	/** Diese Methode gibt den Knoten mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird ein undefinierter Knoten geliefert. Der
	 * gegebene Identifikator kann von dem des gelieferten Knoten abweichen.
	 * 
	 * @see BEXNode#VOID_NODE
	 * @param key Identifikator.
	 * @return Knoten. */
	public BEXNode node(int key);

}
