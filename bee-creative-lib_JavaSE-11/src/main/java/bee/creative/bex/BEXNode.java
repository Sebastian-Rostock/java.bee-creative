package bee.creative.bex;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert die homogene Sicht auf Element-, Text- und Attributknoten. In besonderen Fällen wird sie auch zur Abbildung undefinierter Knoten
 * verwendet. Die aufsteigende Navigation von einem Kind- bzw. Attributknoten zu dessen Elternknoten ist optional.
 * <p>
 * Die Mathoden {@link #hashCode()} und {@link #equals(Object)} basieren auf {@link #key()} und {@link #owner()}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXNode {

	/** Dieses Feld speichert den leeren {@link BEXNode} als undefinierten Knoten im {@link BEXFile#EMPTY}. */
	public static final BEXNode EMPTY = new BEXNode();

	/** Dieses Feld speichert die Typkennung eines undefinierten Knoten. */
	public static final int VOID_NODE = 0;

	/** Dieses Feld speichert die Typkennung eines Elementknoten. */
	public static final int ELEM_NODE = 1;

	/** Dieses Feld speichert die Typkennung eines Attributknoten. */
	public static final int ATTR_NODE = 2;

	/** Dieses Feld speichert die Typkennung eines Textknoten. */
	public static final int TEXT_NODE = 3;

	/** Diese Methode gibt den Identifikator dieses Knoten zurück.
	 *
	 * @return Identifikator. */
	public int key() {
		return 0;
	}

	/** Diese Methode gibt die Typkennung dieses Knoten zurück. Die Typkennung ist bei einem Attributknoten {@link #ATTR_NODE}, bei einem Elementknoten
	 * {@link #ELEM_NODE}, bei einem Textknoten {@link #TEXT_NODE} und bei einem undefinierten Knoten {@link #VOID_NODE}.
	 *
	 * @return Typkennung. */
	public int type() {
		return BEXNode.VOID_NODE;
	}

	/** Diese Methode gibt das diesen Knoten verwaltende {@link BEXFile} zurück.
	 *
	 * @return Besitzer. */
	public BEXFile owner() {
		return BEXFile.EMPTY;
	}

	/** Diese Methode gibt den URI des Namensraums dieses Knoten als Zeichenkette zurück. Der URI eines Textknoten, eines Element- bzw. Attributknoten ohne
	 * Namensraum sowie eines undefinierten Knoten ist leer.
	 *
	 * @return URI. */
	public String uri() {
		return "";
	}

	/** Diese Methode gibt den Namen dieses Knoten als Zeichenkette zurück. Der Name eines Textknoten sowie eines undefinierten Knoten ist leer.
	 *
	 * @return Name. */
	public String name() {
		return "";
	}

	/** Diese Methode gibt den Wert dieses Knoten als Zeichenkette zurück. Der Wert eines Elementknoten ohne Kindknoten sowie eines undefinierten Knoten ist leer.
	 * Der Wert eines Elementknoten mit Kindknoten entspricht dem Wert seines ersten Kindknoten.
	 *
	 * @return Wert. */
	public String value() {
		return "";
	}

	/** Diese Methode gibt die Position dieses Knoten in der Kind- bzw. Attributknotenliste des Elternknotens zurück (optional). Die Position eines undefinierten
	 * Knoten ist {@code -1}. Wenn die Navigation zum Elternknoten deaktiviert ist, ist die Position jedes Knoten {@code -1}.
	 *
	 * @return Position. */
	public int index() {
		return -1;
	}

	/** Diese Methode gibt den Elternknoten dieses Knoten zurück (optional). Der Elternknoten des Wurzelelementknoten sowie eines undefinierten Knoten ist ein
	 * undefinierter Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jedes Knoten ein undefinierter Knoten.
	 *
	 * @return Elternknoten. */
	public BEXNode parent() {
		return BEXNode.EMPTY;
	}

	/** Diese Methode gibt die Kindknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributknoten sowie eines undefinierten Knoten ist eine
	 * undefinierte Knotenliste.
	 *
	 * @return Kindknotenliste. */
	public BEXList children() {
		return BEXList.EMPTY;
	}

	/** Diese Methode gibt die Attributknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attribut-knoten sowie eines undefinierten Knoten ist
	 * eine undefinierte Knotenliste.
	 *
	 * @return Attributknotenliste. */
	public BEXList attributes() {
		return BEXList.EMPTY;
	}

	@Override
	public int hashCode() {
		return this.key() ^ this.owner().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof BEXNode)) return false;
		var that = (BEXNode)object;
		return (this.key() == that.key()) && this.owner().equals(that.owner());
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.key(), this.type(), this.index(), //
			this.uri(), this.name(), this.value(), this.children().size(), this.attributes().size());
	}

}
