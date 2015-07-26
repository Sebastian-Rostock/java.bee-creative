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

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Node}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseNodeData<GThiz extends BaseNodeData<?>> extends BaseBuilder<Node, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für einen Attributknoten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AttrData<GOwner extends BaseNodeData<?>> extends BaseNodeData<AttrData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeAttr();

		/**
		 * Diese Methode entfernt den {@link #getNode() aktuelen Attributknoten} aus dem Elementknoten des Besitzers und gibt den Besitzer zurück.<br>
		 * Wenn der aktuelle Knoten {@code null} ist, erfolgt keine Änderung.
		 * 
		 * @see #closeAttr()
		 * @see Element#removeAttributeNode(Attr)
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Attributknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist.
		 */
		public GOwner removeAttr() throws IllegalStateException {
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

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected AttrData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für einen Kindknoten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ChldData<GOwner extends BaseNodeData<?>> extends BaseNodeData<ChldData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeChld();

		/**
		 * Diese Methode fügt den {@link #getNode() aktuellen Kindknoten} an der gegebenen Position im Elementknoten des Besitzers ein und gibt den Besitzer zurück.<br>
		 * Negative Positionen zählen vom Ende der Kindknotenliste, sodass der Kindknoten für die Positionen {@code -1}, {@code -2} usw. zum letzten, vorletzten
		 * usw. Kindknoten wird. Wenn die effektive Position außerhalb der Kindknotenliste liegt, wird der Kindknoten am Beginn bzw. am Ende der Liste eingefügt.
		 * 
		 * @see #closeChld()
		 * @see Node#insertBefore(Node, Node)
		 * @param index Position.
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Kindknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist.
		 */
		public GOwner insertChld(int index) throws IllegalStateException {
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

		/**
		 * Diese Methode entfernt den {@link #getNode() aktuelen Kindknoten} aus dem Elementknoten des Besitzers und gibt den Besitzer zurück.<br>
		 * Wenn der aktuelle Knoten {@code null} ist, erfolgt keine Änderung.
		 * 
		 * @see #closeChld()
		 * @see Element#removeAttributeNode(Attr)
		 * @return Besitzer.
		 * @throws IllegalStateException Wenn der aktuelle Knoten kein Kindknoten bzw. der Knoten des Besitzers kein kompatibler Elementknoten ist.
		 */
		public GOwner removeChld() throws IllegalStateException {
			try {
				final GOwner owner = this.closeChld();
				if (!this.hasNode()) return owner;
				final Node parent = owner.getNode();
				parent.removeChild(this.node);
				return owner;
			} catch (final RuntimeException cause) {
				throw new IllegalStateException(cause);
			}
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ChldData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die leere Attributknotenliste.
	 */
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

	/**
	 * Dieses Feld speichert die leere Kondknotenliste.
	 */
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

	{}

	/**
	 * Dieses Feld speichert den aktuellen Knoten.
	 */
	Node node;

	{}

	/**
	 * Diese Methode gibt einen neuen Konfigurator für einen Attributknoten zurück.
	 * 
	 * @return Konfigurator.
	 */
	protected AttrData<GThiz> newAttrData() {
		return new AttrData<GThiz>() {

			@Override
			public GThiz closeAttr() {
				return BaseNodeData.this.thiz();
			}

		};
	}

	/**
	 * Diese Methode gibt einen neuen Konfigurator für einen Kindnoten zurück.
	 * 
	 * @return Konfigurator.
	 */
	protected ChldData<GThiz> newChldData() {
		return new ChldData<GThiz>() {

			@Override
			public GThiz closeChld() {
				return BaseNodeData.this.thiz();
			}

		};
	}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	protected GThiz use(final BaseNodeData<?> data) {
		if (data == null) return this.thiz();
		this.node = data.node;
		return this.thiz();
	}

	protected GThiz useNode(final Node node) {
		this.node = node;
		return this.thiz();
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich dem gegebenen ist.
	 * 
	 * @see #getType()
	 * @param nodeType Knotentyp.
	 * @return {@code true}, wenn der {@link #getNode() aktuelle Knoten} den gegebenen Knotentyp hat.
	 */
	public boolean hasType(final int nodeType) {
		return this.getType() == nodeType;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#TEXT_NODE} ist.
	 * 
	 * @see #hasType(int)
	 * @return {@code true} bei einem Textknoten.
	 */
	public boolean hasType_TEXT() {
		return this.hasType(Node.TEXT_NODE);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#ELEMENT_NODE} ist.
	 * 
	 * @see #hasType(int)
	 * @return {@code true} bei einem Elementknoten.
	 */
	public boolean hasType_ELEM() {
		return this.hasType(Node.ELEMENT_NODE);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#ATTRIBUTE_NODE} ist.
	 * 
	 * @see #hasType(int)
	 * @return {@code true} bei einem Attributknoten.
	 */
	public boolean hasType_ATTR() {
		return this.hasType(Node.ATTRIBUTE_NODE);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der {@link #getType() aktuelle Knotentyp} gleich {@link Node#DOCUMENT_NODE} ist.
	 * 
	 * @see #hasType(int)
	 * @return {@code true} bei einem Dokumentknoten.
	 */
	public boolean hasType_DOCU() {
		return this.hasType(Node.DOCUMENT_NODE);
	}

	/**
	 * Diese Methode gibt den Knotentyp des {@link #getNode() aktuellen Knote} zurück.<br>
	 * Wenn es {@link #hasNode() keinen solchen Knoten gibt}, wird {@code 0} geliefert.
	 * 
	 * @return Knotentyp oder {@code 0}.
	 */
	public int getType() {
		final Node node = this.node;
		if (node == null) return 0;
		return node.getNodeType();
	}

	/**
	 * Diese Methode gibt den aktuellen Knote oder {@code null} zurück.
	 * 
	 * @see #useNode(Node)
	 * @return Knoten oder {@code null}.
	 */
	public Node getNode() {
		return this.node;
	}

	public String getValue() {
		return this.getValue(null);
	}

	public String getValue(final String defaultValue) {
		final Node node = this.node;
		if (node == null) return defaultValue;
		if (node.getNodeType() == Node.ELEMENT_NODE) return node.getTextContent();
		return node.getNodeValue();
	}

	/**
	 * Diese Methode gibt den Elternknoten des {@link #getNode() aktuellen Knoten} zurück.<br>
	 * Wenn der der aktuelle Knoten {@code null} ist oder keinen Elternknoten hat, wird {@code null} geliefert.
	 * 
	 * @see Attr#getOwnerElement()
	 * @see Node#getParentNode()
	 * @return Elternknoten oder {@code null}.
	 */
	public Node getParent() {
		final Node node = this.node;
		if (node == null) return null;
		if (node.getNodeType() == Node.ATTRIBUTE_NODE) return ((Attr)node).getOwnerElement();
		return node.getParentNode();
	}

	/**
	 * Diese Methode gibt das {@link Document} zum {@link #getNode() aktuellen Knoten} zurück.<br>
	 * Wenn der der aktuelle Knoten {@code null} ist, wird {@code null} geliefert.
	 * 
	 * @see Node#getOwnerDocument()
	 * @return {@link Document} oder {@code null}.
	 */
	public Document getDocument() {
		final Node node = this.node;
		if (node == null) return null;
		if (node instanceof Document) return (Document)node;
		return node.getOwnerDocument();
	}

	public NamedNodeMap getAttrMap() {
		final Node node = this.node;
		if (node == null) return BaseNodeData.EMPTY_ATTR_MAP;
		final NamedNodeMap list = node.getAttributes();
		if (list == null) return BaseNodeData.EMPTY_ATTR_MAP;
		return list;
	}

	public int getAttrCount() {
		return this.getAttrMap().getLength();
	}

	public NodeList getChldList() {
		final Node node = this.node;
		if (node == null) return BaseNodeData.EMPTY_CHLD_LIST;
		final NodeList list = node.getChildNodes();
		if (list == null) return BaseNodeData.EMPTY_CHLD_LIST;
		return list;
	}

	public int getChldCount() {
		return this.getChldList().getLength();
	}

	public boolean hasNode() {
		return this.getNode() != null;
	}

	public GThiz useValue(final String value) {
		final Node node = this.node;
		if (node == null) throw new IllegalStateException();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			node.setTextContent(value);
		} else {
			node.setNodeValue(value);
		}
		return this.thiz();
	}

	public AttrData<GThiz> newAttr(final String name) {
		final Attr item = this.getDocument().createAttribute(name);
		final NamedNodeMap list = this.getAttrMap();
		list.setNamedItem(item);
		return this.newAttrData().useNode(item);
	}

	public AttrData<GThiz> newAttr(final String uri, final String name) {
		final Attr item = this.getDocument().createAttributeNS(uri, name);
		final NamedNodeMap list = this.getAttrMap();
		list.setNamedItemNS(item);
		return this.newAttrData().useNode(item);
	}

	public ChldData<GThiz> newText() throws DOMException {
		final Node node = this.node;
		final Document docu = this.getDocument();
		final Node text = docu.createTextNode("");
		node.appendChild(text);
		return this.newChldData().useNode(text);
	}

	/**
	 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Text} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
	 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
	 * 
	 * @see #insert(Node)
	 * @see Document#createTextNode(String)
	 * @param text Wert.
	 * @return {@code this}.
	 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
	 */
	public GThiz newText(final String text) throws DOMException {
		return this.newText().useValue(text).closeChld();
	}

	/**
	 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit dem gegebenen Namen ein, bewegt die {@link #index() aktuelle
	 * Position} hinter den eingefügten Knoten und gibt einen neuen {@link XMLNode} zum eingefügten {@link Element} zurück.
	 * 
	 * @see Document#createElement(String)
	 * @param name Name.
	 * @return {@link XMLNode} zum erzeugten Knoten.
	 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
	 */
	public ChldData<GThiz> newElem(final String name) throws DOMException {
		final Node node = this.node;
		final Document docu = this.getDocument();
		final Node elem = docu.createElement(name);
		node.appendChild(elem);
		return this.newChldData().useNode(elem);
	}

	/**
	 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit den gegebenen Eigenschaften ein, bewegt die {@link #index()
	 * aktuelle Position} hinter den eingefügten Knoten und gibt einen neuen {@link XMLNode} zum eingefügten {@link Element} zurück.
	 * 
	 * @see Document#createElementNS(String, String)
	 * @param uri Uri.
	 * @param name Name.
	 * @return {@link XMLNode} zum erzeugten Knoten.
	 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
	 */
	public ChldData<GThiz> newElem(final String uri, final String name) throws DOMException {
		final Node node = this.node;
		final Document docu = this.getDocument();
		final Node elem = docu.createElementNS(uri, name);
		node.appendChild(elem);
		return this.newChldData().useNode(elem);
	}

	public AttrData<GThiz> openAttr(final int index) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.item(index < 0 ? list.getLength() + index : index);
		return this.newAttrData().useNode(item);
	}

	public AttrData<GThiz> openAttr(final String name) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.getNamedItem(name);
		return this.newAttrData().useNode(item);
	}

	public AttrData<GThiz> openAttr(final String uri, final String name) {
		final NamedNodeMap list = this.getAttrMap();
		final Node item = list.getNamedItemNS(uri, name);
		return this.newAttrData().useNode(item);
	}

	public ChldData<GThiz> openChld(final int index) {
		final NodeList list = this.getChldList();
		final Node item = list.item(index < 0 ? list.getLength() + index : index);
		return this.newChldData().useNode(item);
	}

	public ChldData<GThiz> openChld(final String name) {
		// TODO
		return this.newChldData();
	}

	public ChldData<GThiz> openChld(final String uri, final String name) {
		// TODO

		return this.newChldData();
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz thiz();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getNode()
	 */
	@Override
	public Node build() throws IllegalStateException {
		return this.getNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.getNode());
	}

}