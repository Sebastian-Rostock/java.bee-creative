package bee.creative.bex;

import bee.creative.util.Comparables.Items;

/**
 * Diese Schnittstelle definiert die homogene Sicht auf Kind- und Attributknotenlisten. Die aufsteigende Navigation von einer Knotenliste zu deren Elternknoten
 * ist optional.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface BEXList extends Items<BEXNode>, Iterable<BEXNode> {

	/**
	 * Dieses Feld speichert die Typkennung einer undefinierten Knotenliste.
	 */
	public static final int VOID_LIST = 0;

	/**
	 * Dieses Feld speichert die Typkennung einer Kindknotenliste.
	 */
	public static final int CHLD_LIST = 1;

	/**
	 * Dieses Feld speichert die Typkennung einer Attributknotenliste.
	 */
	public static final int ATTR_LIST = 2;

	/**
	 * Diese Methode gibt den Identifikator dieser Knotenliste zurück.
	 * 
	 * @return Identifikator.
	 */
	public int key();

	/**
	 * Diese Methode gibt die Typkennung dieser Knotenliste zurück. Die Typkennung ist bei einer Attributknotenliste 1, bei einer allgemeinen Kindknotenliste 2
	 * und bei einer undefinierten Knotenliste 0.
	 * 
	 * @return Typkennung.
	 */
	public int type();

	/**
	 * Diese Methode gibt das diese Knotenliste verwaltende {@link BEXFile} zurück.
	 * 
	 * @return Besitzer.
	 */
	public BEXFile owner();

	/**
	 * Diese Methode gibt den {@code index}-ten Knoten dieser Knotenliste zurück. Bei einem ungültigen {@code index} wird ein undefinierter Knoten geliefert.
	 */
	@Override
	public BEXNode get(int index);

	/**
	 * Diese Methode sucht linear ab der gegebenen {@code start}-Position den ersten Element- bzw. Attribut-knoten mit der gegebenen {@code uri} sowie dem
	 * gegebenen {@code name} und gibt dessen Position zurück. Bei einer erfolglosen Suche wird {@code -1} geliefert. Ein leerer {@code uri} bzw. {@code name}
	 * wird bei der Suche ignoriert, d.h. der gesuchte Knoten hat einen beliebigen URI bzw. Namen. Bei einer negativen {@code start}-Position wird immer
	 * {@code -1} geliefert.
	 * 
	 * @param uri URI.
	 * @param name Name.
	 * @param start Position, ab der die Suche beginnt.
	 * @return Position des Treffers oder {@code -1}.
	 * @throws NullPointerException Wenn {@code uri} bzw. {@code name} {@code null} ist.
	 */
	public int find(String uri, String name, int start) throws NullPointerException;

	/**
	 * Diese Methode gibt die Länge dieser Knotenliste zurück. Die Länge ist bei einer undefinierten Knotenliste {@code 0}.
	 * 
	 * @return Länge.
	 */
	public int length();

	/**
	 * Diese Methode gibt den Elternknoten dieser Knotenliste zurück (optional). Der Elternknoten ist bei einer undefinierten Knotenliste ein undefinierter
	 * Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jeder Knotenliste ein undefinierter Knoten.
	 * 
	 * @return Elternknoten.
	 */
	public BEXNode parent();

}
