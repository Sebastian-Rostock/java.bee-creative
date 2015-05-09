package bee.creative.bex;

import bee.creative.util.Comparables.Get;

/**
 * Die Schnittstelle BEXList definiert die homogene Schnittstelle die Kind- bzw. Attributknotenlisten. Die aufsteigende Navigation von einer Knotenliste zu
 * deren Elternknoten ist optional.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface BEXList extends Get<BEXNode>, Iterable<BEXNode> {

	/**
	 * Dieses Feld speichert das VOID_LIST.
	 */
	public static final int VOID_LIST = 0;

	/**
	 * Dieses Feld speichert das CHLT_LIST.
	 */
	public static final int CHLT_LIST = 1;

	/**
	 * Dieses Feld speichert das ATTR_LIST.
	 */
	public static final int ATTR_LIST = 2;

	/**
	 * Diese Methode gibt den Identifikator dieser Knotenliste zurück.
	 * 
	 * @return
	 */
	public int key();

	/**
	 * Diese Methode gibt die Typkennung dieser Knotenliste zurück. Die Typkennung ist bei einer Attri-butknotenliste 1, bei einer allgemeinen Kindknotenliste 2
	 * und bei einer undefinierten Knotenliste 0.
	 * 
	 * @return
	 */
	public int type();

	/**
	 * Diese Methode gibt das diese Knotenliste verwaltende Objekt zurück.
	 * 
	 * @return
	 */
	public BEXFile owner();

	/**
	 * Diese Methode gibt den index-ten Knoten dieser Knotenliste zurück. Bei einem ungültigen index wird ein undefinierter Knoten geliefert.
	 */
	@Override
	public BEXNode get(int index);

	/**
	 * Diese Methode sucht linear ab der gegebenen start-Position den ersten Element- bzw. Attribut-knoten mit der gegebenen uri sowie dem gegebenen name und gibt
	 * dessen Position zurück. Bei einer erfolglosen Suche wird -1 geliefert. Ein leerer uri bzw. name wird bei der Suche ignoriert, d.h. der gesuchte Knoten hat
	 * einen beliebigen URI bzw. Namen. Bei einer negativen start-Position wird immer -1 geliefert.
	 * 
	 * @param uri
	 * @param name
	 * @param start
	 * @return
	 */
	public int find(String uri, String name, int start);

	/**
	 * Diese Methode gibt die Länge dieser Knotenliste zurück. Die Länge ist bei einer undefinierten Knotenliste 0.
	 * 
	 * @return
	 */
	public int length();

	/**
	 * Diese Methode gibt den Elternknoten dieser Knotenliste zurück (optional). Der Elternknoten ist bei einer undefinierten Knotenliste ein undefinierter
	 * Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jeder Knotenliste ein undefinierter Knoten.
	 * 
	 * @return
	 */
	public BEXNode parent();

}
