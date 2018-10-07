package bee.creative.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Node}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseNodeData<GThis extends BaseNodeData<?>> extends BaseBuilder<Node, GThis> {

	/** Diese Klasse implementiert den Konfigurator für einen Attributknoten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AttrData<GOwner extends BaseNodeData<?>> extends BaseNodeData<AttrData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeAttr();

		/** Diese Methode entfernt den {@link #getNode() aktuelen Attributknoten} aus dem Elementknoten des Besitzers und gibt den Besitzer zurück. Wenn der
		 * aktuelle Knoten {@code null} ist, erfolgt keine Änderung.
		 *
		 * @see #closeAttr()
		 * @see Element#removeAttributeNode(Attr)
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Attributknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist. */
		public final GOwner removeAttr() throws IllegalStateException {
			try {
				final GOwner owner = this.closeAttr();
				if (!this.hasNode()) return owner;
				final Attr attr = (Attr)this.getNode();
				final Element parent = (Element)owner.getNode();
				parent.removeAttributeNode(attr);
				return owner;
			} catch (final RuntimeException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/** {@inheritDoc} */
		@Override
		protected final AttrData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für einen Kindknoten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ChldData<GOwner extends BaseNodeData<?>> extends BaseNodeData<ChldData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeChld();

		/** Diese Methode fügt den {@link #getNode() aktuellen Kindknoten} an der gegebenen Position im Elementknoten des Besitzers ein und gibt den Besitzer
		 * zurück. Negative Positionen zählen vom Ende der Kindknotenliste, sodass der Kindknoten für die Positionen {@code -1}, {@code -2} usw. zum letzten,
		 * vorletzten usw. Kindknoten wird. Wenn die effektive Position außerhalb der Kindknotenliste liegt, wird der Kindknoten am Beginn bzw. am Ende der Liste
		 * eingefügt.
		 *
		 * @see #closeChld()
		 * @see Node#insertBefore(Node, Node)
		 * @param index Position.
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Kindknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist. */
		public final GOwner insertChld(int index) throws IllegalStateException {
			try {
				final GOwner owner = this.closeChld();
				final NodeList list = owner.getChldList();
				final int length = list.getLength();
				if (index < 0) {
					index = 1 + index + length;
					if (index < 0) {
						index = 0;
					}
				}
				final Node next = list.item(index);
				final Node chld = this.getNode();
				final Node parent = owner.getNode();
				parent.insertBefore(chld, next);
				return owner;
			} catch (final RuntimeException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/** Diese Methode entfernt den {@link #getNode() aktuelen Kindknoten} aus dem Elementknoten des Besitzers und gibt den Besitzer zurück. Wenn der aktuelle
		 * Knoten {@code null} ist, erfolgt keine Änderung.
		 *
		 * @see #closeChld()
		 * @see Element#removeAttributeNode(Attr)
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Kindknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist. */
		public final GOwner removeChld() throws IllegalStateException {
			try {
				final GOwner owner = this.closeChld();
				if (!this.hasNode()) return owner;
				final Node parent = owner.getNode();
				parent.removeChild(this.getNode());
				return owner;
			} catch (final RuntimeException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/** {@inheritDoc} */
		@Override
		protected final ChldData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert die leere Attributknotenliste. */
	public static final NamedNodeMap EMPTY_ATTR_MAP = new NamedNodeMap() {

		@Override
		public Node item(final int index) {
			return null;
		}

		@Override
		public int getLength() {
			return 0;
		}

		@Override
		public Node setNamedItem(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
		}

		@Override
		public Node getNamedItem(final String name) {
			return null;
		}

		@Override
		public Node setNamedItemNS(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
		}

		@Override
		public Node getNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
			return null;
		}

		@Override
		public Node removeNamedItem(final String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
		}

		@Override
		public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
		}

	};

	/** Dieses Feld speichert die leere Kondknotenliste. */
	public static final NodeList EMPTY_CHLD_LIST = new NodeList() {

		@Override
		public Node item(final int index) {
			return null;
		}

		@Override
		public int getLength() {
			return 0;
		}

	};

	/** Dieses Feld speichert den aktuellen Knoten. */
	Node node;

	/** Diese Methode gibt einen neuen Konfigurator für einen Attributknoten zurück.
	 *
	 * @return Konfigurator. */
	protected final AttrData<GThis> newAttrData() {
		return new AttrData<GThis>() {

			@Override
			public final GThis closeAttr() {
				return BaseNodeData.this.customThis();
			}

		};
	}

	/** Diese Methode gibt einen neuen Konfigurator für einen Kindnoten zurück.
	 *
	 * @return Konfigurator. */
	protected final ChldData<GThis> newChldData() {
		return new ChldData<GThis>() {

			@Override
			public final GThis closeChld() {
				return BaseNodeData.this.customThis();
			}

		};
	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	protected GThis use(final BaseNodeData<?> data) {
		if (data == null) return this.customThis();
		this.node = data.node;
		return this.customThis();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich dem gegebenen ist.
	 *
	 * @see #getType()
	 * @param nodeType Knotentyp.
	 * @return {@code true}, wenn der {@link #getNode() aktuelle Knoten} den gegebenen Knotentyp hat. */
	public final boolean hasType(final int nodeType) {
		return this.getType() == nodeType;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#TEXT_NODE} ist.
	 *
	 * @see #hasType(int)
	 * @return {@code true} bei einem Textknoten. */
	public final boolean hasType_TEXT() {
		return this.hasType(Node.TEXT_NODE);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#ELEMENT_NODE} ist.
	 *
	 * @see #hasType(int)
	 * @return {@code true} bei einem Elementknoten. */
	public final boolean hasType_ELEM() {
		return this.hasType(Node.ELEMENT_NODE);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#ATTRIBUTE_NODE} ist.
	 *
	 * @see #hasType(int)
	 * @return {@code true} bei einem Attributknoten. */
	public final boolean hasType_ATTR() {
		return this.hasType(Node.ATTRIBUTE_NODE);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#DOCUMENT_NODE} ist.
	 *
	 * @see #hasType(int)
	 * @return {@code true} bei einem Dokumentknoten. */
	public final boolean hasType_DOCU() {
		return this.hasType(Node.DOCUMENT_NODE);
	}

	/** Diese Methode gibt den Knotentyp des {@link #getNode() aktuellen Knote} zurück. Wenn es {@link #hasNode() keinen solchen Knoten gibt}, wird {@code 0}
	 * geliefert.
	 *
	 * @return Knotentyp oder {@code 0}. */
	public final int getType() {
		final Node node = this.getNode();
		if (node == null) return 0;
		return node.getNodeType();
	}

	/** Diese Methode gibt den aktuellen Knote oder {@code null} zurück.
	 *
	 * @see #useNode(Node)
	 * @return Knoten oder {@code null}. */
	public final Node getNode() {
		return this.node;
	}

	/** Diese Methode setzt den {@link #getNode() aktuellen Knoten} und gibt {@code this} zurück.
	 *
	 * @param node Knoten oder {@code null}.
	 * @return {@code this}. */
	protected GThis useNode(final Node node) {
		this.node = node;
		return this.customThis();
	}

	/** Diese Methode gibt den Wert bzw. Inhalt des {@link #getNode() aktuellen Knoten} zurück zurück.
	 *
	 * @see #getValue(String)
	 * @return Wert bzw. Inhalt oder {@code null}. */
	public final String getValue() {
		return this.getValue(null);
	}

	/** Diese Methode gibt den Wert bzw. Inhalt des {@link #getNode() aktuellen Knoten} zurück. Wenn der Knoten ein {@link #hasType_ELEM() Elementknoten} ist,
	 * wird dessen {@link Node#getTextContent() Inhalt} geliefert. Wenn es {@link #hasNode() keinen aktuellen Knoten gibt}, wird der gegebene Vorgabewert
	 * geliefert. Andernfalls wird der {@link Node#getNodeValue() Wert} des Knoten geliefert.
	 *
	 * @see #hasType_ELEM()
	 * @see Node#getNodeValue()
	 * @see Node#getTextContent()
	 * @param defaultValue Vorgabewert.
	 * @return Wert bzw. Inhalt oder Vorgabewert. */
	public final String getValue(final String defaultValue) {
		final Node node = this.getNode();
		if (node == null) return defaultValue;
		if (node.getNodeType() == Node.ELEMENT_NODE) return node.getTextContent();
		return node.getNodeValue();
	}

	/** Diese Methode setzt den Wert des {@link #getNode() aktuellen Knoten} und gibt {@code this} zurück.
	 *
	 * @see Node#setNodeValue(String)
	 * @see Node#setTextContent(String)
	 * @param value Wert.
	 * @return {@code this}.
	 * @throws DOMException Wenn {@link Node#setNodeValue(String)} bzw. {@link Node#setTextContent(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasNode() keinen aktuellen Knoten} gibt. */
	public final GThis useValue(final String value) throws DOMException, IllegalStateException {
		if (!this.hasNode()) throw new IllegalStateException();
		final Node node = this.getNode();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			node.setTextContent(value);
		} else {
			node.setNodeValue(value);
		}
		return this.customThis();
	}

	/** Diese Methode gibt den Elternknoten des {@link #getNode() aktuellen Knoten} zurück. Wenn aktuell {@link #hasNode() kein Knoten} gewählt ist oder dieser
	 * keinen Elternknoten hat, wird {@code null} geliefert.
	 *
	 * @see Attr#getOwnerElement()
	 * @see Node#getParentNode()
	 * @return Elternknoten oder {@code null}. */
	public final Node getParent() {
		final Node node = this.getNode();
		if (node == null) return null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) return ((Attr)node).getOwnerElement();
		return node.getParentNode();
	}

	/** Diese Methode gibt das {@link Document} zum {@link #getNode() aktuellen Knoten} zurück. Wenn der der aktuelle Knoten {@code null} ist, wird {@code null}
	 * geliefert.
	 *
	 * @see Node#getOwnerDocument()
	 * @return {@link Document} oder {@code null}. */
	public final Document getDocument() {
		final Node node = this.getNode();
		if (node == null) return null;
		if (node instanceof Document) return (Document)node;
		return node.getOwnerDocument();
	}

	/** Diese Methode gibt die Attributknoten des {@link #getNode() aktuellen Knoten} zurück. Wenn aktuell {@link #hasNode() kein Knoten} gewählt ist oder dieser
	 * keine Attribute hat, wird {@link #EMPTY_ATTR_MAP} geliefert.
	 *
	 * @see Node#getAttributes()
	 * @return Attributknoten oder {@link #EMPTY_ATTR_MAP}. */
	public final NamedNodeMap getAttrMap() {
		final Node node = this.getNode();
		if (node == null) return BaseNodeData.EMPTY_ATTR_MAP;
		final NamedNodeMap result = node.getAttributes();
		if (result == null) return BaseNodeData.EMPTY_ATTR_MAP;
		return result;
	}

	/** Diese Methode gibt die Anzahl der Attributknoten des {@link #getNode() aktuellen Knoten} zurück.
	 *
	 * @see #getAttrMap()
	 * @return Attributknotenanzahl. */
	public final int getAttrCount() {
		return this.getAttrMap().getLength();
	}

	/** Diese Methode gibt die Kindknoten des {@link #getNode() aktuellen Knoten} zurück. Wenn aktuell {@link #hasNode() kein Knoten} gewählt ist oder dieser
	 * keine Kindknoten hat, wird {@link #EMPTY_CHLD_LIST} geliefert.
	 *
	 * @see Node#getChildNodes()
	 * @return Kindknoten oder {@link #EMPTY_CHLD_LIST}. */
	public final NodeList getChldList() {
		final Node node = this.getNode();
		if (node == null) return BaseNodeData.EMPTY_CHLD_LIST;
		final NodeList result = node.getChildNodes();
		if (result == null) return BaseNodeData.EMPTY_CHLD_LIST;
		return result;
	}

	/** Diese Methode gibt die Anzahl der Kindknoten des {@link #getNode() aktuellen Knoten} zurück.
	 *
	 * @see #getChldList()
	 * @return Kindknotenanzahl. */
	public final int getChldCount() {
		return this.getChldList().getLength();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn es einen {@link #getNode() aktuelle Knoten} gibt, d.h. dieser nicht {@code null} ist.
	 *
	 * @return {@code true}, wenn {@link #getNode()} nicht {@code null} liefert. */
	public final boolean hasNode() {
		return this.getNode() != null;
	}

	/** Diese Methode fügt einen neuen {@link Attr Attributknoten} mit dem gegebenen Namen in die Attributknotenliste ein und gibt den Konfigurator dieses Knoten
	 * zurück.
	 *
	 * @see Document#createAttribute(String)
	 * @see NamedNodeMap#setNamedItem(Node)
	 * @param name Name.
	 * @return Konfigurator des Attributknoten.
	 * @throws DOMException Wenn {@link NamedNodeMap#setNamedItem(Node)} bzw. {@link Document#createAttribute(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasType_ELEM() aktuell keinen Elementknoten} gibt. */
	public final AttrData<GThis> newAttr(final String name) throws DOMException, IllegalStateException {
		if (!this.hasNode()) throw new IllegalStateException();
		final Attr item = this.getDocument().createAttribute(name);
		final NamedNodeMap list = this.getAttrMap();
		list.setNamedItem(item);
		return this.newAttrData().useNode(item);
	}

	/** Diese Methode fügt einen neuen {@link Attr Attributknoten} mit dem gegebenen URI und Namen in die Attributknotenliste ein und gibt den Konfigurator dieses
	 * Knoten zurück.
	 *
	 * @see Document#createAttributeNS(String, String)
	 * @see NamedNodeMap#setNamedItemNS(Node)
	 * @param uri URI.
	 * @param name Name.
	 * @return Konfigurator des Attributknoten.
	 * @throws DOMException Wenn {@link NamedNodeMap#setNamedItemNS(Node)} bzw. {@link Document#createAttributeNS(String, String)} eine entsprechende Ausnahme
	 *         auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasType_ELEM() aktuell keinen Elementknoten} gibt. */
	public final AttrData<GThis> newAttr(final String uri, final String name) throws DOMException, IllegalStateException {
		if (!this.hasType_ELEM()) throw new IllegalStateException();
		final Attr item = this.getDocument().createAttributeNS(uri, name);
		final NamedNodeMap list = this.getAttrMap();
		list.setNamedItemNS(item);
		return this.newAttrData().useNode(item);
	}

	/** Diese Methode fügt einen neuen {@link Text Textknoten} an die Kindknotenliste an und gibt den Konfigurator dieses Knoten zurück.
	 *
	 * @return Konfigurator des Textknoten.
	 * @throws DOMException Wenn {@link Node#appendChild(Node)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasNode() keinen aktuellen Knoten} gibt. */
	public final ChldData<GThis> newText() throws DOMException, IllegalStateException {
		if (!this.hasNode()) throw new IllegalStateException();
		final Node node = this.getNode();
		final Document docu = this.getDocument();
		final Node text = docu.createTextNode("");
		node.appendChild(text);
		return this.newChldData().useNode(text);
	}

	/** Diese Methode fügt einen neuen {@link Text Textknoten} mit dem gegebenen Wert an die Kindknotenliste an und gibt {@code this} zurück.
	 *
	 * @see #newText()
	 * @see #useValue(String)
	 * @param text Wert.
	 * @return {@code this}.
	 * @throws DOMException Wenn {@link #newText()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn {@link #newText()} eine entsprechende Ausnahme auslöst. */
	public final GThis newText(final String text) throws DOMException, IllegalStateException {
		return this.newText().useValue(text).closeChld();
	}

	/** Diese Methode fügt einen neuen {@link Element Elementknoten} mit dem gegebenen Namen an die Kindknotenliste an und gibt den Konfigurator dieses Knoten
	 * zurück.
	 *
	 * @see Document#createElement(String)
	 * @param name Name.
	 * @return Konfigurator des Elementknoten.
	 * @throws DOMException Wenn {@link Node#appendChild(Node)} bzw. {@link Document#createElement(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasNode() keinen aktuellen Knoten} gibt. */
	public final ChldData<GThis> newElem(final String name) throws DOMException, IllegalStateException {
		if (!this.hasNode()) throw new IllegalStateException();
		final Node node = this.getNode();
		final Document docu = this.getDocument();
		final Node elem = docu.createElement(name);
		node.appendChild(elem);
		return this.newChldData().useNode(elem);
	}

	/** Diese Methode fügt einen neuen {@link Element Elementknoten} mit dem gegebenen URI und Namen an die Kindknotenliste an und gibt den Konfigurator dieses
	 * Knoten zurück.
	 *
	 * @see Document#createElementNS(String, String)
	 * @param uri URI.
	 * @param name Name.
	 * @return Konfigurator des Elementknoten.
	 * @throws DOMException Wenn {@link Node#appendChild(Node)} bzw. {@link Document#createElementNS(String, String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalStateException Wenn es {@link #hasNode() keinen aktuellen Knoten} gibt. */
	public final ChldData<GThis> newElem(final String uri, final String name) throws DOMException, IllegalStateException {
		if (!this.hasNode()) throw new IllegalStateException();
		final Node node = this.getNode();
		final Document docu = this.getDocument();
		final Node elem = docu.createElementNS(uri, name);
		node.appendChild(elem);
		return this.newChldData().useNode(elem);
	}

	/** Diese Methode gibt den Konfigurator für den Attributknoten mit der gegebenen Position zurück. Negative Positionen zählen vom Ende der Attributknotenliste.
	 *
	 * @see #getAttrMap()
	 * @param index Position.
	 * @return Konfigurator. */
	public final AttrData<GThis> openAttr(final int index) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.item(index < 0 ? list.getLength() + index : index);
		return this.newAttrData().useNode(item);
	}

	/** Diese Methode gibt den Konfigurator für den Attributknoten mit dem gegebenen Namen zurück.
	 *
	 * @see NamedNodeMap#getNamedItem(String)
	 * @param name Name.
	 * @return Konfigurator. */
	public final AttrData<GThis> openAttr(final String name) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.getNamedItem(name);
		return this.newAttrData().useNode(item);
	}

	/** Diese Methode gibt den Konfigurator für den Attributknoten mit dem gegebenen URI und Namen zurück.
	 *
	 * @see NamedNodeMap#getNamedItemNS(String, String)
	 * @param uri URI.
	 * @param name Name.
	 * @return Konfigurator. */
	public final AttrData<GThis> openAttr(final String uri, final String name) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.getNamedItemNS(uri, name);
		return this.newAttrData().useNode(item);
	}

	/** Diese Methode gibt den Konfigurator für den Elementknoten mit der gegebenen Position zurück. Negative Positionen zählen vom Ende der Kindknotenliste.
	 *
	 * @see #getChldList()
	 * @param index Position.
	 * @return Konfigurator. */
	public final ChldData<GThis> openChld(final int index) {
		final NodeList list = this.getChldList();
		final Node item = list.item(index < 0 ? list.getLength() + index : index);
		return this.newChldData().useNode(item);
	}

	/** Diese Methode gibt den Konfigurator für den Elementknoten mit dem gegebenen Namen zurück.
	 *
	 * @see #getChldList()
	 * @see Node#getNodeType()
	 * @see Node#getNodeName()
	 * @param name Name.
	 * @return Konfigurator. */
	public final ChldData<GThis> openChld(final String name) {
		final NodeList list = this.getChldList();
		for (int i = 0, length = list.getLength(); i < length; i++) {
			final Node item = list.item(i);
			if ((item.getNodeType() == Node.ELEMENT_NODE) && Objects.equals(name, item.getNodeName())) //
				return this.newChldData().useNode(item);
		}
		return this.newChldData();
	}

	/** Diese Methode gibt den Konfigurator für den Elementknoten mit dem gegebenen URI und Namen zurück.
	 *
	 * @see #getChldList()
	 * @see Node#getNodeType()
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @param uri URI.
	 * @param name Name.
	 * @return Konfigurator. */
	public final ChldData<GThis> openChld(final String uri, final String name) {
		final NodeList list = this.getChldList();
		for (int i = 0, length = list.getLength(); i < length; i++) {
			final Node item = list.item(i);
			if ((item.getNodeType() == Node.ELEMENT_NODE) && Objects.equals(uri, item.getNamespaceURI()) && Objects.equals(name, item.getLocalName())) //
				return this.newChldData().useNode(item);
		}
		return this.newChldData();
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getNode() */
	@Override
	public final Node get() throws IllegalStateException {
		return this.getNode();
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.getNode());
	}

}