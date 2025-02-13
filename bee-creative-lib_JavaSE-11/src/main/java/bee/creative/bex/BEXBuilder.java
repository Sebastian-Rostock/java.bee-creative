package bee.creative.bex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import bee.creative.bex.BEXLoader.BEXFileLoader;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMIndexBuilder;
import bee.creative.iam.IAMListing;
import bee.creative.iam.IAMListingBuilder;
import bee.creative.lang.Objects;
import bee.creative.util.Comparators;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert die Algorithmen zur Kodierung der {@code Binary Encoded XML} Datenstrukturen.
 *
 * @see BEXFileBuilder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXBuilder {

	/** Diese Klasse implementiert ein Objekt zur Zusammenstellung und Kodierung der Daten für einen {@link BEXFileLoader}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXFileBuilder {

		/** Dieser Konstruktor initialisiert einen leeren {@link BEXFileBuilder}. */
		public BEXFileBuilder() {
			this.clear();
		}

		/** Diese Methode beendet die Bestückung des aktuellen Attributknoten und gibt {@code this} zurück. Anschließend wird die Bestückung des Elternknoten
		 * (Elementknoten) fortgesetzt.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Attributknoten bestückt wird oder dessen Name unbestimmt ist. */
		public BEXFileBuilder putAttr() throws IllegalStateException {
			var stack = this.stack;
			if ((stack.type != BEXStack.ATTR) || (stack.name == null)) throw new IllegalStateException();
			var parent = stack.parent;
			var uri = this.attrUriText.getItem(this.attrUriEnabled ? stack.uri : null);
			var name = this.attrNameText.getItem(stack.name);
			var value = this.attrValueText.getItem(stack.value);
			parent.attributes.put(uri).put(name).put(value).put(null).put(this.attrParentEnabled ? parent.item : null);
			this.stack = parent;
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Attributknoten und gibt {@code this} zurück. Die Bestückung des neuen, aktuellen Attributknoten kann
		 * anschließend über {@link #useUri(String)}, {@link #useName(String)} und {@link #useValue(String)} erfolgen und muss mit {@link #putAttr()} abgeschlossen
		 * werden. Die Bestückung des Elternknoten (Elementknoten) kann erst nach dem Aufruf von {@link #putAttr()} fortgesetzt werden.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Elementknoten bestückt wird. */
		public BEXFileBuilder newAttr() throws IllegalStateException {
			var parent = this.stack;
			if (parent.type != BEXStack.ELEM) throw new IllegalStateException();
			var stack = new BEXStack();
			stack.type = BEXStack.ATTR;
			stack.parent = parent;
			this.stack = stack;
			return this;
		}

		/** Diese Methode beendet die Bestückung des aktuellen Textknoten und gibt {@code this} zurück. Anschließend wird die Bestückung des Elternknoten
		 * (Elementknoten) fortgesetzt.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Textknoten bestückt wird. */
		public BEXFileBuilder putText() throws IllegalStateException {
			var stack = this.stack;
			if (stack.type != BEXStack.TEXT) throw new IllegalStateException();
			stack.type = BEXStack.ELEM;
			var value = stack.value;
			if (value == null) return this;
			this.text.append(value);
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Textknoten und gibt {@code this} zurück. Die Bestückung des neuen, aktuellen Textknoten kann
		 * anschließend über {@link #useValue(String)} erfolgen und muss mit {@link #putText()} abgeschlossen werden. Die Bestückung des Elternknoten
		 * (Elementknoten) kann erst nach dem Aufruf von {@link #putText()} fortgesetzt werden.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Elementknoten bestückt wird. */
		public BEXFileBuilder newText() throws IllegalStateException {
			var stack = this.stack;
			if (stack.type != BEXStack.ELEM) throw new IllegalStateException();
			stack.type = BEXStack.TEXT;
			stack.value = null;
			return this;
		}

		/** Diese Methode beendet die Bestückung des aktuellen Elementknoten und gibt {@code this} zurück. Anschließend wird die Bestückung des Elternknoten
		 * (Elementknoten) fortgesetzt.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Textknoten bestückt wird. */
		public BEXFileBuilder putElem() throws IllegalStateException {
			var stack = this.stack;
			if (stack.type != BEXStack.ELEM) throw new IllegalStateException();
			this.putTextImpl(stack);
			var parent = stack.parent;
			var chldItems = stack.children.items;
			var attrItems = stack.attributes.items;
			var uri = this.chldUriText.getItem(this.chldUriEnabled ? stack.uri : null);
			var name = this.chldNameText.getItem(stack.name);
			var content = ((chldItems.size() == 5) && (chldItems.get(1) == null)) ? //
				chldItems.get(2) : //
				(stack.children = this.chldParentEnabled && !chldItems.isEmpty() ? this.chldTablePart.addItem(stack.children) : this.chldTablePart.getItem(chldItems));
			var attributes = //
				(stack.attributes =
					this.attrParentEnabled && !attrItems.isEmpty() ? this.attrTablePart.addItem(stack.attributes) : this.attrTablePart.getItem(attrItems));
			if (parent == null) return this;
			this.stack = parent;
			parent.children.put(uri).put(name).put(content).put(attributes).put(this.chldParentEnabled ? parent.item : null);
			if (parent.type != BEXStack.VOID) return this;
			parent.type = BEXStack.ELEM;
			this.putElem();
			parent.type = BEXStack.ROOT;
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Elementknoten und gibt {@code this} zurück. Die Bestückung des neuen, aktuellen Elementknoten kann
		 * anschließend über {@link #useUri(String)}, {@link #useName(String)}, {@link #newAttr()}, {@link #newText()} und {@link #newElem()} erfolgen und muss mit
		 * {@link #putElem()} abgeschlossen werden. Die Bestückung des Elternknoten (Elementknoten) kann erst nach dem Aufruf von {@link #putElem()} fortgesetzt
		 * werden.
		 *
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird. */
		public BEXFileBuilder newElem() throws IllegalStateException {
			var parent = this.stack;
			if ((parent.type != BEXStack.VOID) && (parent.type != BEXStack.ELEM)) throw new IllegalStateException();
			this.putTextImpl(parent);
			var stack = new BEXStack();
			stack.type = BEXStack.ELEM;
			stack.item = new BEXParentItem(parent.children, parent.children.items.size() / 5);
			stack.parent = parent;
			stack.children = new BEXGroupItem();
			stack.attributes = new BEXGroupItem();
			this.stack = stack;
			return this;
		}

		/** Diese Methode ließt die gegebene {@code XML} Datei ein, fügt das darin beschriebenen Wurzelelement an und gibt {@code this} zurück.
		 *
		 * @see #putNode(InputStream)
		 * @param file Datei.
		 * @return {@code this}.
		 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
		 * @throws SAXException Wenn die Datei nicht geparst werden kann.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird.
		 * @throws ParserConfigurationException */
		public BEXFileBuilder putNode(File file) throws IOException, SAXException, NullPointerException, IllegalStateException, ParserConfigurationException {
			try (var stream = new FileInputStream(file)) {
				return this.putNode(stream);
			}
		}

		/** Diese Methode ließt den gegebenen {@code XML} Datenstrom ein, fügt das darin beschriebenen Wurzelelement an und gibt {@code this} zurück.
		 *
		 * @see #putNode(InputSource, XMLReader)
		 * @param stream Datenstrom.
		 * @return {@code this}.
		 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
		 * @throws SAXException Wenn die Datei nicht geparst werden kann.
		 * @throws NullPointerException Wenn {@code stream} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird.
		 * @throws ParserConfigurationException */
		public BEXFileBuilder putNode(InputStream stream)
			throws IOException, SAXException, NullPointerException, IllegalStateException, ParserConfigurationException {
			return this.putNode(new InputSource(stream), SAXParserFactory.newInstance().newSAXParser().getXMLReader());
		}

		/** Diese Methode fügt abhängig vom Typ des gegebenen Knoten einen Text-, Element- oder Attributknoten an und gibt {@code this} zurück.
		 * <ul>
		 * <li>Wenn der Knotentyp {@link Node#ATTRIBUTE_NODE} ist, wird analog zu {@link #newAttr()}, {@link #useUri(String)}, {@link #useName(String)},
		 * {@link #useValue(String)} und {@link #putAttr()} ein neuer Attributknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#TEXT_NODE} oder {@link Node#CDATA_SECTION_NODE} ist, wird analog zu {@link #newText()}, {@link #useValue(String)} und
		 * {@link #putText()} ein neuer Textknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#ELEMENT_NODE} ist, wird analog zu {@link #newElem()}, {@link #useUri(String)}, {@link #useName(String)} und
		 * {@link #putNode(Node)} ein neuer Elementknoten mit allen seinen Kind- und Attributknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#DOCUMENT_NODE} ist, wird analog zu {@link #putNode(Node)} das Wurzelelement erzeugt.</li>
		 * <li>Wenn der Knotentyp keiner der oben ganennten ist, wird der Knoten ignoriert.</li>
		 * </ul>
		 *
		 * @param node Knoten.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht der passende Knoten bestückt wird. */
		public BEXFileBuilder putNode(Node node) throws NullPointerException, IllegalStateException {
			switch (node.getNodeType()) {
				case Node.DOCUMENT_NODE: {
					return this.putNode(((Document)node).getDocumentElement());
				}
				case Node.ATTRIBUTE_NODE: {
					var name = node.getLocalName();
					return this.newAttr().useUri(node.getNamespaceURI()).useName(name != null ? name : node.getNodeName()).useValue(node.getNodeValue()).putAttr();
				}
				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE: {
					if (this.stack.type != BEXStack.ELEM) throw new IllegalStateException();
					var value = node.getNodeValue();
					if (value == null) return this;
					this.text.append(value);
					return this;
				}
				case Node.ELEMENT_NODE: {
					var name = node.getLocalName();
					this.newElem().useUri(node.getNamespaceURI()).useName(name != null ? name : node.getNodeName());
					var children = node.getChildNodes();
					for (int i = 0, length = children.getLength(); i < length; i++) {
						this.putNode(children.item(i));
					}
					var attributes = node.getAttributes();
					for (int i = 0, length = attributes.getLength(); i < length; i++) {
						this.putNode(attributes.item(i));
					}
					return this.putElem();
				}
			}
			return this;
		}

		/** Diese Methode ließt die gegebene Datenquelle mit dem gegebenen Parser ein, fügt das darin beschriebenen Wurzelelement an und gibt {@code this} zurück.
		 *
		 * @param source Datenquelle.
		 * @param reader Parser.
		 * @return {@code this}.
		 * @throws IOException Wenn die Datenquelle nicht geöffnet werden kann.
		 * @throws SAXException Wenn die Datenquelle nicht geparst werden kann.
		 * @throws NullPointerException Wenn {@code source} bzw. {@code reader} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird. */
		public BEXFileBuilder putNode(InputSource source, XMLReader reader) throws IOException, SAXException, NullPointerException, IllegalStateException {
			reader.setContentHandler(new DefaultHandler() {

				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					BEXFileBuilder.this.newElem().useUri(uri).useName(localName);
					for (int i = 0, length = attributes.getLength(); i < length; i++) {
						BEXFileBuilder.this.newAttr().useUri(attributes.getURI(i)).useName(attributes.getLocalName(i)).useValue(attributes.getValue(i)).putAttr();
					}
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					BEXFileBuilder.this.putElem();
				}

				@Override
				public void characters(char[] ch, int start, int length) throws SAXException {
					if (BEXFileBuilder.this.stack.type != BEXStack.ELEM) throw new IllegalStateException();
					BEXFileBuilder.this.text.append(ch, start, length);
				}

				@Override
				public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
					this.characters(ch, start, length);
				}

			});
			reader.parse(source);
			return this;
		}

		/** Diese Methode setzt den URI des aktuellen Element- bzw. Attributknoten gibt {@code this} zurück.
		 *
		 * @see #newAttr()
		 * @see #newElem()
		 * @see BEXNode#uri()
		 * @param uri URI oder {@code null}.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Element- noch ein Attributknoten ist. */
		public BEXFileBuilder useUri(String uri) throws IllegalStateException {
			var node = this.stack;
			if ((node.type != BEXStack.ATTR) && (node.type != BEXStack.ELEM)) throw new IllegalStateException();
			node.uri = uri;
			return this;
		}

		/** Diese Methode setzt den Namen des aktuellen Element- bzw. Attributknoten gibt {@code this} zurück.
		 *
		 * @see #newAttr()
		 * @see #newElem()
		 * @see BEXNode#name()
		 * @param name Name.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Element- noch ein Attributknoten ist.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalArgumentException Wenn {@code name} leer ist. */
		public BEXFileBuilder useName(String name) throws IllegalStateException, NullPointerException, IllegalArgumentException {
			var stack = this.stack;
			if ((stack.type != BEXStack.ATTR) && (stack.type != BEXStack.ELEM)) throw new IllegalStateException();
			if (name == null) throw new IllegalArgumentException("name = null");
			if (name.isEmpty()) throw new IllegalArgumentException();
			stack.name = name;
			return this;
		}

		/** Diese Methode setzt den Namen des aktuellen Text- bzw. Attributknoten gibt {@code this} zurück.
		 *
		 * @see #newAttr()
		 * @see #newText()
		 * @see BEXNode#value()
		 * @param value Wert oder {@code null}.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Text- noch ein Attributknoten ist. */
		public BEXFileBuilder useValue(String value) throws IllegalStateException {
			var node = this.stack;
			if ((node.type != BEXStack.ATTR) && (node.type != BEXStack.TEXT)) throw new IllegalStateException();
			node.value = value;
			return this;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die URI von Attributknoten erfasst werden. Andernfalls werden diese ignoriert.
		 *
		 * @see #useUri(String)
		 * @see #useAttrUriEnabled(boolean)
		 * @return {@code true}, wenn die URI von Attributknoten erfasst werden. */
		public boolean isAttrUriEnabled() {
			return this.attrUriEnabled;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Elternknoten von Attributknoten erfasst werden. Andernfalls werden diese ignoriert.
		 *
		 * @see #useAttrParentEnabled(boolean)
		 * @return {@code true}, wenn die Elternknoten von Attributknoten erfasst werden. */
		public boolean isAttrParentEnabled() {
			return this.attrParentEnabled;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die URI von Elementknoten erfasst werden. Andernfalls werden diese ignoriert.
		 *
		 * @see #useUri(String)
		 * @see #useChldUriEnabled(boolean)
		 * @return {@code true}, wenn die URI von Elementknoten erfasst werden. */
		public boolean isChldUriEnabled() {
			return this.chldUriEnabled;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Elternknoten von Kindknoten erfasst werden. Andernfalls werden diese ignoriert.
		 *
		 * @see #useChldParentEnabled(boolean)
		 * @return {@code true}, wenn die Elternknoten von Kindknoten erfasst werden. */
		public boolean isChldParentEnabled() {
			return this.chldParentEnabled;
		}

		/** Diese Methode setzt die Aktivierung der URI an Attributknoten und gibt {@code this} zurück.
		 *
		 * @see #useUri(String)
		 * @see #newAttr()
		 * @see #newElem()
		 * @see #newText()
		 * @see #isAttrUriEnabled()
		 * @param value {@code true}, wenn die URI von Attributknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileBuilder useAttrUriEnabled(boolean value) throws IllegalStateException {
			if (this.stack.type != BEXStack.VOID) throw new IllegalStateException();
			this.attrUriEnabled = value;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der Elternknoten an Attributknoten und gibt {@code this} zurück. Wenn die Elternknoten an Attributknoten erfasst
		 * werden, werden auch die der Kindknoten erfasst.
		 *
		 * @see #isAttrParentEnabled()
		 * @param value {@code true}, wenn die Elternknoten von Attributknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileBuilder useAttrParentEnabled(boolean value) throws IllegalStateException {
			if (this.stack.type != BEXStack.VOID) throw new IllegalStateException();
			this.attrParentEnabled = value;
			if (!value) return this;
			this.chldParentEnabled = true;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der URI an Elementknoten und gibt {@code this} zurück.
		 *
		 * @see #useUri(String)
		 * @see #newAttr()
		 * @see #newElem()
		 * @see #newText()
		 * @see #isChldUriEnabled()
		 * @param value {@code true}, wenn die URI von Elementknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileBuilder useChldUriEnabled(boolean value) throws IllegalStateException {
			if (this.stack.type != BEXStack.VOID) throw new IllegalStateException();
			this.chldUriEnabled = value;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der Elternknoten an Kindknoten und gibt {@code this} zurück. Wenn die Elternknoten an Kindknoten ignoriert werden,
		 * werden auch die der Attributknoten ignoriert.
		 *
		 * @see #isAttrParentEnabled()
		 * @param value {@code true}, wenn die Elternknoten von Kindknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileBuilder useChldParentEnabled(boolean value) throws IllegalStateException {
			if (this.stack.type != BEXStack.VOID) throw new IllegalStateException();
			this.chldParentEnabled = value;
			if (value) return this;
			this.attrParentEnabled = false;
			return this;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.text.setLength(0);
			this.stack = new BEXStack();
			this.stack.type = BEXStack.VOID;
			this.stack.children = new BEXGroupItem();
			this.stack.attributes = new BEXGroupItem();
			this.stack.item = new BEXParentItem(this.stack.children, 0);
			this.attrUriText.clear();
			this.attrNameText.clear();
			this.attrValueText.clear();
			this.attrTablePart.clear();
			this.chldUriText.clear();
			this.chldNameText.clear();
			this.chldValueText.clear();
			this.chldTablePart.clear();
		}

		/** Diese Methode kodiert die gesammelten Daten in einen {@link IAMIndex} und gibt diesen zurück.
		 *
		 * @return {@link IAMIndex}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten nicht das Wurzelelement ist. */
		public IAMIndexBuilder toIndex() throws IllegalStateException {
			if (this.stack.type != BEXStack.ROOT) throw new IllegalStateException();
			this.updatePART(this.attrTablePart, +1);
			this.updatePART(this.chldTablePart, -1);
			var result = new IAMIndexBuilder();
			result.put(-1, this.encodeHEAD());
			result.put(-1, this.encodeTEXT(this.attrUriText));
			result.put(-1, this.encodeTEXT(this.attrNameText));
			result.put(-1, this.encodeTEXT(this.attrValueText));
			result.put(-1, this.encodeTEXT(this.chldUriText));
			result.put(-1, this.encodeTEXT(this.chldNameText));
			result.put(-1, this.encodeTEXT(this.chldValueText));
			result.put(-1, this.encodePROP(this.attrTablePart, 0));
			result.put(-1, this.encodePROP(this.attrTablePart, 1));
			result.put(-1, this.encodePROP(this.attrTablePart, 2));
			result.put(-1, this.encodePROP(this.attrTablePart, 4));
			result.put(-1, this.encodePROP(this.chldTablePart, 0));
			result.put(-1, this.encodePROP(this.chldTablePart, 1));
			result.put(-1, this.encodePROP(this.chldTablePart, 2));
			result.put(-1, this.encodePROP(this.chldTablePart, 3));
			result.put(-1, this.encodePROP(this.chldTablePart, 4));
			result.put(-1, this.encodePART(this.attrTablePart));
			result.put(-1, this.encodePART(this.chldTablePart));
			return result;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.attrTablePart.items.size(), this.chldTablePart.items.size());
		}

		/** Dieses Feld speichert den Puffer zur Zusammenfassung benachbarter Textknoten. */
		final StringBuilder text = new StringBuilder();

		/** Dieses Feld speichert den aktuellen Knoten. */
		BEXStack stack = new BEXStack();

		/** Dieses Feld speichert die einzigartigen URI der Attributknoten. */
		final BEXTextPool attrUriText = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Namen der Attributknoten. */
		final BEXTextPool attrNameText = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Werte der Attributknoten. */
		final BEXTextPool attrValueText = new BEXTextPool();

		/** Dieses Feld speichert die Abschnitte der Attributknotentabelle. */
		final BEXGroupPool attrTablePart = new BEXGroupPool();

		/** Dieses Feld speichert die Aktivierung der URI von Attributknoten. */
		boolean attrUriEnabled = true;

		/** Dieses Feld speichert die Aktivierung der Elternknoten von Attributknoten. */
		boolean attrParentEnabled = false;

		/** Dieses Feld speichert die einzigartigen URI der Elementknoten. */
		final BEXTextPool chldUriText = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Namen der Elementknoten. */
		final BEXTextPool chldNameText = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Werte der Textknoten. */
		final BEXTextPool chldValueText = new BEXTextPool();

		/** Dieses Feld speichert die Abschnitte der Kindknotentabelle. */
		final BEXGroupPool chldTablePart = new BEXGroupPool();

		/** Dieses Feld speichert die Aktivierung der URI von Kindknoten. */
		boolean chldUriEnabled = true;

		/** Dieses Feld speichert die Aktivierung der Elternknoten von Kindknoten. */
		boolean chldParentEnabled = false;

		/** Diese Methode fügt die in {@link #text} gesammelte Zeichenkette als Textknoten an den gegebenen Elternknoten an.
		 *
		 * @param parent Elternknoten. */
		void putTextImpl(BEXStack parent) {
			var text = this.text;
			if (text.length() == 0) return;
			var value = this.chldValueText.getItem(text.toString());
			text.setLength(0);
			parent.children.put(null).put(null).put(value).put(null).put(this.chldParentEnabled ? parent.item : null);
		}

		/** Diese Methode aktualisiert die Ordnung der zusammengestellten Datensätze, die Anzahl der Zeilen der durch diese Datensätze beschriebenen Tabelle sowie
		 * die Schlüssel und Startpositionen der Datensätze.
		 *
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @param step Inkrement für den Zähler der Schlüssel (-1=Kindknoten, +1=Attributknoten). */
		void updatePART(BEXGroupPool pool, int step) {
			var groups = pool.items;
			Collections.sort(groups, BEXGroupItem.ORDER);
			var key = 0;
			var offset = 0;
			for (var group: groups) {
				group.key = key;
				group.offset = offset;
				key += step;
				offset += group.items.size() / 5;
			}
			pool.length = offset;
		}

		/** Diese Methode kodiert die Längen der gegebenen Tabellenabschnitte in eine Zahlenfolge und gibt diese in einer Auflistung zurück.
		 *
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @return Auflistung mit einer Zahlenfolge. */
		IAMListingBuilder encodePART(BEXGroupPool pool) {
			var groups = pool.items;
			var length = groups.size();
			var value = new int[length + 1];
			for (var i = 0; i < length; i++) {
				value[i] = groups.get(i).offset;
			}
			value[length] = pool.length;
			var encoder = new IAMListingBuilder();
			encoder.put(-1, IAMArray.from(value));
			return encoder;
		}

		/** Diese Methode kodiert eine Spalte der gegebenen Tabellenabschnitte in eine Zahlenfolge und gibt diese in einer Auflistung zurück.
		 *
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @param prop Spaltenindex (0..4).
		 * @return Auflistung mit einer Zahlenfolge. */
		IAMListingBuilder encodePROP(BEXGroupPool pool, int prop) {
			var groups = pool.items;
			var length = pool.length;
			var value = new int[length];
			var index = 0;
			for (var group: groups) {
				var items = group.items;
				var count = items.size();
				for (var i = prop; i < count; i += 5) {
					var item = items.get(i);
					value[index++] = item != null ? item.key() : 0;
				}
			}
			var empty = false;
			for (var i = (prop == 0) || (prop == 4) ? 0 : length; (i < length) && (empty = value[i] == 0); i++) {}
			var result = new IAMListingBuilder();
			result.put(-1, empty ? IAMArray.EMPTY : IAMArray.from(value));
			return result;
		}

		/** Diese Methode kodiert die gegebenen Zeichenketten in eine Liste von Zahlenfolgen und gibt diese zurück.
		 *
		 * @see BEXFile#arrayFrom(String)
		 * @param pool Auflistung von Zeichenketten.
		 * @return Auflistung von Zahlenfolgen. */
		IAMListingBuilder encodeTEXT(final BEXTextPool pool) {
			var texts = pool.items;
			Collections.sort(texts, BEXTextItem.ORDER);
			var encoder = new IAMListingBuilder();
			for (var text: texts) {
				text.key = encoder.put(-1, BEXFile.arrayFrom(text.text));
			}
			return encoder;
		}

		/** Diese Methode kodiert die Datentypkennung sowie den Verweis auf das Wurzelelement in eine Auflistung von Zahlenfolgen und gibt diese zurück.
		 *
		 * @return Auflistung von Zahlenfolgen. */
		IAMListing encodeHEAD() {
			var header = new IAMListingBuilder();
			header.put(IAMArray.from(0xBE10BA5E, this.stack.children.offset));
			return header;
		}

	}

	/** Diese Klasse implementiert einen Datensatz, dem ein identifizierender {@link #key()} zugeordnet werden kann.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static abstract class BEXItem {

		/** Dieses Feld speichert den Schlüssel zur Referenzierung. */
		public int key;

		/** Diese Methode gibt den Schlüssel zur Referenzierung zurück.
		 *
		 * @return Schlüssel. */
		public int key() {
			return this.key;
		}

		@Override
		public String toString() {
			return "@" + this.key();
		}

	}

	/** Diese Klasse implementiert ein einen Datensatz, der zur Verwaltung einzigartiger Zeichenketten eingesetzt wird.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXTextItem extends BEXItem {

		/** Dieses Feld speichert einen {@link Comparator}, der einen Datensatz mit leerem {@link #text} vor einen mit nichtleerem {@link #text} ordnet und
		 * andernfalls zwei Datensätze bezüglich des {@link #key} aufsteigend ordnet. */
		public static final Comparator<BEXTextItem> ORDER = (item1, item2) -> {
			var result = Boolean.compare(item2.text.isEmpty(), item1.text.isEmpty());
			if (result != 0) return result;
			return Comparators.compare(item1.key, item2.key);
		};

		/** Dieses Feld speichert die Zeichenkette. */
		public String text;

		/** Dieser Konstruktor initialisiert die Zeichenkette.
		 *
		 * @param text Zeichenkette. */
		public BEXTextItem(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.text);
		}

	}

	/** Diese Klasse implementiert ein einen Datensatz, der zur Verwaltung einzigartiger Kind- und Attributknotenlisten eingesetzt wird.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXGroupItem extends BEXItem {

		/** Dieses Feld speichert einen {@link Comparator}, der einen Datensatz mit leeren {@link #items} vor einen mit nichtleeren {@link #items} ordnet und
		 * andernfalls zwei Datensätze bezüglich des {@link #key} aufsteigend ordnet. */
		public static final Comparator<BEXGroupItem> ORDER = (item1, item2) -> {
			var result = Boolean.compare(item2.items.isEmpty(), item1.items.isEmpty());
			if (result != 0) return result;
			return Comparators.compare(item1.key, item2.key);
		};

		/** Dieses Feld speichert die Zeilennummer in der Kind- bzw. Attributknotentabelle, ab der die Kind- bzw. Attributknotenliste abgelegt ist. */
		public int offset;

		/** Dieses Feld speichert die Kind- bzw. Attributknotenliste. Ein Knoten besteht hierbei immer aus fünf aufeinander folgenden Datensätzen:
		 * {@code 0=uri/null}, {@code 1=name/null}, {@code 2=value/children}, {@code 3=attributes/null} , {@code 4=parent/null}. */
		public List<BEXItem> items;

		/** Dieser Konstruktor initialisiert {@link #items} mit einer leeren Liste. */
		public BEXGroupItem() {
			this(new ArrayList<>());
		}

		/** Dieser Konstruktor initialisiert die Gruppe mit den gegebenen Kind- bzw. Attributknoten.
		 *
		 * @param items Kind- bzw. Attributknoten. */
		public BEXGroupItem(List<BEXItem> items) {
			this.items = items;
		}

		/** Diese Methode fügt den gegebenen Datensatz an {@link #items} an und gibt {@code this} zurück.
		 *
		 * @param item Datensatz oder {@code null}.
		 * @return {@code this}. */
		public BEXGroupItem put(BEXItem item) {
			this.items.add(item);
			return this;
		}

		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.items);
		}

	}

	/** Diese Klasse implementiert einen Datensatz, der das Elternelement eines Kind- bzw- Attributknoten eingesetzt wird.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXParentItem extends BEXItem {

		/** Dieses Feld speichert die Kindknotenliste, in der das Elternelement verwaltet wird. */
		public BEXGroupItem group;

		/** Dieses Feld speichert die logische Position des Elternelements in der ihn verwaltenden Kindknotenliste. */
		public int index;

		/** Dieser Konstruktor initialisiert Kindknotenliste und Position.
		 *
		 * @see #group
		 * @see #index
		 * @param group Kindknotenliste.
		 * @param index Position. */
		public BEXParentItem(BEXGroupItem group, int index) {
			this.group = group;
			this.index = index;
		}

		@Override
		public int key() {
			return this.group.offset + this.index;
		}

	}

	/** Diese Klasse implementiert einen abstrakten {@link HashMap} zur Verwaltung einzigartiger Datensätze.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Eingabedaten.
	 * @param <GItem> Typ der Datensätze. */
	static abstract class BEXPool<GData, GItem extends BEXItem> extends HashMap<GData, GItem> {

		/** Dieses Feld speichert die Liste aller einzigartigen Datensätze. */
		public final List<GItem> items = new ArrayList<>();

		/** Diese Methode fügt den gegebenen Datensatz an {@link #items} an und gibt {@code this} zurück.
		 *
		 * @param item Datensatz.
		 * @return {@code this}. */
		public GItem addItem(GItem item) {
			this.items.add(item);
			return item;
		}

		public GItem getItem(GData source) throws RuntimeException {
			return this.install(source);
		}

		@Override
		public void clear() {
			this.items.clear();
		}

		@Override
		public String toString() {
			return Objects.toString(true, this.items);
		}

		@Override
		protected void customReuseEntry(int entryIndex) {
			this.customGetValue(entryIndex).key--;
		}

		private static final long serialVersionUID = 4281145819764444974L;

	}

	/** Diese Klasse implementiert den {@link BEXPool} zur Verwaltung einzigartiger Zeichenketten.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXTextPool extends BEXPool<String, BEXTextItem> {

		@Override
		public BEXTextItem getItem(String input) throws NullPointerException {
			return super.getItem(input != null ? input : "");
		}

		@Override
		public void clear() {
			super.clear();
			this.getItem(null);
		}

		@Override
		protected BEXTextItem customInstallValue(String key) {
			return this.addItem(new BEXTextItem(key));
		}

		private static final long serialVersionUID = 2008762461427364602L;

	}

	/** Diese Klasse implementiert den {@link BEXPool} zur Verwaltung einzigartiger Kind- bzw. Attributknotenlisten.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXGroupPool extends BEXPool<List<BEXItem>, BEXGroupItem> {

		/** Dieses Feld speichert die Anzahl der Zeilen der Tabelle, die durch die Datensätze in {@link #items} beschrieben wird. */
		public int length;

		@Override
		public BEXGroupItem getItem(List<BEXItem> input) throws NullPointerException {
			return super.getItem(input != null ? input : Collections.<BEXItem>emptyList());
		}

		@Override
		public void clear() {
			super.clear();
			this.getItem(null);
		}

		@Override
		protected BEXGroupItem customInstallValue(List<BEXItem> key) {
			return this.addItem(new BEXGroupItem(key));
		}

		private static final long serialVersionUID = 2119371369741103587L;

	}

	/** Diese Klasse implementiert die allgemeinen Zustandsdaten, die während der Bestückung von Knoten eingesetzt werden.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class BEXStack {

		/** Dieses Feld speichert den Datentyp des Dokumentknoten ohne Wurzelelement. */
		public static final int VOID = 0;

		/** Dieses Feld speichert den Datentyp eines Elementknoten. */
		public static final int ELEM = 1;

		/** Dieses Feld speichert den Datentyp eines Attributknoten. */
		public static final int ATTR = 2;

		/** Dieses Feld speichert den Datentyp eines Textknoten. */
		public static final int TEXT = 3;

		/** Dieses Feld speichert den Datentyp des Dokumentknoten mit Wurzelelement. */
		public static final int ROOT = 4;

		/** Dieses Feld speichert die Zustandsdaten des Elternknoten. */
		public BEXStack parent;

		/** Dieses Feld speichert den Typ der Zustandsdaten. */
		public int type;

		/** Dieses Feld speichert den URI des Knoten. */
		public String uri;

		/** Dieses Feld speichert den Namen des Knoten. */
		public String name;

		/** Dieses Feld speichert den Wert des Knoten. */
		public String value;

		/** Dieses Feld speichert das {@link BEXItem} dieses Knoten. */
		public BEXItem item;

		/** Dieses Feld speichert die Kindknotenliste. */
		public BEXGroupItem children;

		/** Dieses Feld speichert die Attributknotenliste. */
		public BEXGroupItem attributes;

	}

}
