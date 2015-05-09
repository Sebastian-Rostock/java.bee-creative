package bee.creative.bex;

/**
 * Die Schnittstelle BEXNode definiert die homogene Schnittstelle eines Element-, Text- bzw. Attributknoten. In besonderen Fällen wird sie auch zur Abbildung
 * undefinierter Knoten verwendet. Die aufsteigende Navigation von einem Kind- bzw. Attributknoten zu dessen Elternknoten ist optional.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface BEXNode {

	/**
	 * Dieses Feld speichert das VOID_NODE.
	 */
	public static final int VOID_NODE = 0;

	/**
	 * Dieses Feld speichert das ELEM_NODE.
	 */
	public static final int ELEM_NODE = 1;

	/**
	 * Dieses Feld speichert das ATTR_NODE.
	 */
	public static final int ATTR_NODE = 2;

	/**
	 * Dieses Feld speichert das TEXT_NODE.
	 */
	public static final int TEXT_NODE = 3;

	/**
	 * Diese Methode gibt den Identifikator dieses Knoten zurück.
	 * 
	 * @return
	 */
	public int key();

	/**
	 * Diese Methode gibt die Typkennung dieses Knoten zurück. Die Typkennung ist bei einem Attributknoten 1, bei einem Elementknoten 2, bei einem Textknoten 3
	 * und bei einem undefinierten Knoten 0.
	 * 
	 * @return
	 */
	public int type();

	/**
	 * Diese Methode gibt das diesen Knoten verwaltende Objekt zurück.
	 * 
	 * @return
	 */
	public BEXFile owner();

	/**
	 * Diese Methode gibt den URI des Namensraums dieses Knoten als Zeichenkette zurück. Der URI eines Textkno-ten, eines Element- bzw. Attributknoten ohne
	 * Namensraum sowie eines undefinierten Knoten ist leer.
	 * 
	 * @return
	 */
	public String uri();

	/**
	 * Diese Methode gibt den Namen dieses Knoten als Zeichenkette zurück. Der Name eines Textknoten sowie eines undefinierten Knoten ist leer.
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Diese Methode gibt den Wert dieses Knoten als Zeichenkette zurück. Der Wert eines Elementknoten ohne Kindknoten sowie eines undefinierten Knoten ist leer.
	 * Der Wert eines Elementknoten mit Kindknoten entspricht dem Wert seines ersten Kindknoten.
	 * 
	 * @return
	 */
	public String value();

	/**
	 * Diese Methode gibt die Position dieses Knoten in der Kind- bzw. Attributknotenliste des Elternknotens zurück (optional). Die Position eines undefinierten
	 * Knoten ist -1. Wenn die Navigation zum Elternknoten deaktiviert ist, ist die Position jedes Knoten -1.
	 * 
	 * @return
	 */
	public int index();

	/**
	 * Diese Methode gibt den Elternknoten dieses Knoten zurück (optional). Der Elternknoten des Wurzelelementkno-ten sowie eines undefinierten Knoten ist ein
	 * undefinierter Knoten. Wenn die Navigation zum Elternknoten deak-tiviert ist, ist der Elternknoten jedes Knoten ein undefinierter Knoten.
	 * 
	 * @return
	 */
	public BEXNode parent();

	/**
	 * Diese Methode gibt die Kindknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributkno-ten sowie eines undefinierten Knoten ist eine
	 * undefinierte Knotenliste.
	 * 
	 * @return
	 */
	public BEXList children();

	/**
	 * Diese Methode gibt die Attributknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attribut-knoten sowie eines undefinierten Knoten ist
	 * eine undefinierte Knotenliste.
	 * 
	 * @return
	 */
	public BEXList attributes();

}
