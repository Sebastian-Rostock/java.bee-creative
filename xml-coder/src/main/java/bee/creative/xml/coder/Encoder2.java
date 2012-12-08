package bee.creative.xml.coder;

import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import bee.creative.array.ArraySection;
import bee.creative.array.CompactIntegerArray;
import bee.creative.array.CompactObjectArray;
import bee.creative.array.ObjectArraySection;
import bee.creative.util.Comparators;
import bee.creative.util.Hash;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;
import bee.creative.util.Unique;
import bee.creative.xml.coder.Decoder.DecodeDocument;
import bee.creative.xml.coder.Encoder.EncodeTarget;
import bee.creative.xml.coder.Encoder.EncodeTargetFile;

/**
 * Diese Klasse implementiert Klassen und Methoden zur Kodierung eines XML-Dokuments in eine optimierte binäre Darstellung.
 * <p>
 * Der aufbau eines {@link EncodeDocument}s kann mit Hilfe des {@link EncodeDocumentBuilder}s oder über einen {@link EncodeDocumentHandler} und einen {@link XMLReader} erfolgen.
 * <p>
 * Als Eingabe werden ein {@link XMLReader} und eine {@link InputSource} verwendet, wobei das {@link EncodeDocumentHandler} als {@link ContentHandler} die vom {@link XMLReader} gelesenen Daten aufnimmt. Die eingelesenen Daten des {@link EncodeDocumentHandler}s werden anschließend unter beachtung der Optionen des {@link Encoder2}s in ein {@link EncodeTarget} gespeichert.
 * <p>
 * 
 * @see Decoder
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Encoder2 {

	public static void main(final String[] args) throws NullPointerException, IOException, SAXException {

		XMLReader reader = XMLReaderFactory.createXMLReader();
		InputSource source = new InputSource(new FileReader(new File("D:\\projects\\java\\bee-creative\\xml-coder\\src\\main\\java\\cds.xml")));
		EncodeDocument target = new EncodeDocument();

		reader.setContentHandler(new EncodeDocumentHandler(target, false, new EncodeNavigationPathFilter() {

			@Override
			public boolean isId(EncodeAttributePath attributePath) {
				return false;
			}

			@Override
			public boolean isId(EncodeElementPath elementPath) {
				return "title".equals(elementPath.elementName());
			}

		}));
		reader.parse(source);

		System.out.println(target);

		final EncodeDocumentBuilder builder = new EncodeDocumentBuilder();

		builder //
			.createInstruction("Instruction", "InstructionParams") //

			.createElement("default-uri", "name") //
			.createId("LALA") //
			.createComment("Comment") //
			.createText("TEXT") //
			.createXmlns("default-uri", "") //
			.createXmlns("http://www.w3.org/XML/1998/namespace", "devXml") //
			.createReference("F") //
			.createInstruction("ins", "sdsd") //
			.createElement("test") //
			.createId("LALO") //
		;
		// .commit()
		// .commit();

		System.out.println(builder.commit());
		// element: 1
		// text:
		// PI:
		// entity

	}

	/**
	 * Diese Schnittstelle definiert die Ausgabe eines {@link Encoder2}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeTarget_ {

		/**
		 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array ab dem gegebenen Index in die Ausgabe an deren aktuelle Schreibposition und setzt diese Schreibposition anschließend an das Ende des soeben geschriebenen Datenbereiches.
		 * 
		 * @see DataOutput#write(byte[], int, int)
		 * @param array {@code byte}-Array.
		 * @param offset Index des ersten geschriebenen {@code byte}s.
		 * @param length Anzahl der geschriebenen {@code byte}s.
		 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
		 */
		public void write(byte[] array, int offset, int length) throws IOException;

	}

	/**
	 * Diese Klasse implementiert definiert Methoden zum Zugriff auf den aktuellen Navifationspfad beim Einlesen eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementPath extends EncodeNavigationPath {

		/**
		 * Dieses Feld speichert den {@link EncodeNavigationPath}.
		 */
		protected final EncodeNavigationPath path;

		/**
		 * Dieses Feld speichert den {@code Child}-Index.
		 */
		protected final int index;

		/**
		 * Dieses Feld speichert den {@link EncodeElementNode}.
		 */
		protected final EncodeElementNode element;

		/**
		 * Dieser Konstrukteur initialisiert {@link EncodeNavigationPath}, {@link EncodeElementNode} und {@code Child}-Index.
		 * 
		 * @param path {@link EncodeNavigationPath}.
		 * @param index {@code Child}-Index.
		 * @param element {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementPath(final EncodeNavigationPath path, final int index, final EncodeElementNode element) throws NullPointerException {
			if((path == null) || (element == null)) throw new NullPointerException();
			this.path = path;
			this.index = index;
			this.element = element;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected EncodeNavigationPath path() {
			return this.path;
		}

		/**
		 * Diese Methode gibt die {@code Uri} des {@link Element}s zurück.
		 * 
		 * @see Element#getNamespaceURI()
		 * @return {@code Uri} des {@link Element}s.
		 */
		public String elementUri() {
			return this.element.label.uri.string;
		}

		/**
		 * Diese Methode gibt den {@code Child}-Index des {@link Element}s zurück, den dieses in den {@link Node#getChildNodes()} seines {@code Eltern}-{@link Node}s hat.
		 * 
		 * @see Node#getChildNodes()
		 * @return {@code Child}-Index des {@link Element}s.
		 */
		public int elementIndex() {
			return this.index;
		}

		/**
		 * Diese Methode gibt den {@code Name} des {@link Element}s zurück.
		 * 
		 * @see Element#getLocalName()
		 * @return {@code Name} des {@link Element}s.
		 */
		public String elementName() {
			return this.element.label.name.string;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeElementPath", "pathUris", this.pathUris(), "pathNames", this.pathNames(), "pathIndices",
				this.pathIndices(), "elementUri", this.elementUri(), "elementName", this.elementName(), "elementIndex", this.elementIndex());
		}

	}

	/**
	 * Diese Klasse implementiert Methoden zum Zugriff auf den aktuellen Navifationspfad beim Einlesen eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributePath extends EncodeNavigationPath {

		/**
		 * Dieses Feld speichert den {@link EncodeNavigationPath}.
		 */
		protected final EncodeNavigationPath path;

		/**
		 * Dieses Feld speichert den {@code Type}.
		 */
		protected final String type;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeNode}.
		 */
		protected final EncodeAttributeNode attribute;

		/**
		 * Dieser Konstrukteur initialisiert {@link EncodeNavigationPath}, {@link EncodeAttributeNode} und {@code Type}.
		 * 
		 * @param path {@link EncodeNavigationPath}.
		 * @param type {@code Type}.
		 * @param attribute {@link EncodeAttributeNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributePath(final EncodeNavigationPath path, final String type, final EncodeAttributeNode attribute) throws NullPointerException {
			if((path == null) || (type == null) || (attribute == null)) throw new NullPointerException();
			this.path = path;
			this.type = type;
			this.attribute = attribute;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected EncodeNavigationPath path() {
			return this.path;
		}

		/**
		 * Diese Methode gibt den {@link EncodeAttributeNode} des {@link Attr}s zurück.
		 * 
		 * @return {@link EncodeAttributeNode}.
		 */
		public EncodeAttributeNode attribute() {
			return this.attribute;
		}

		/**
		 * Diese Methode gibt die {@code Uri} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getNamespaceURI()
		 * @see Attributes#getURI(int)
		 * @return {@code Uri} des {@link Attr}s.
		 */
		public String attributeUri() {
			return this.attribute.label.uri.string;
		}

		/**
		 * Diese Methode gibt den {@code Name} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attributes#getLocalName(int)
		 * @return {@code Name} des {@link Attr}s.
		 */
		public String attributeName() {
			return this.attribute.label.name.string;
		}

		/**
		 * Diese Methode gibt den {@code Type} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getSchemaTypeInfo()
		 * @see Attributes#getType(int)
		 * @return {@code Type} des {@link Attr}s.
		 */
		public String attributeType() {
			return this.type;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeAttributePath", "pathUris", this.pathUris(), "pathNames", this.pathNames(), "pathIndices",
				this.pathIndices(), "attributeUri", this.attributeUri(), "attributeName", this.attributeName(), "attributeType", this.attributeType());
		}

	}

	/**
	 * Diese abstrakte Klasse definiert den Navifationspfad beim Einlesen eines {@link Document}s, bestehend aus {@code Uri}s und {@code Name}s von {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeNavigationPath {

		/**
		 * Diese Methode gibt den {@link EncodeNavigationPath} zurück, an dessen Methoden alle Aufrufe delegiert werden.
		 * 
		 * @return {@link EncodeNavigationPath}.
		 */
		protected abstract EncodeNavigationPath path();

		/**
		 * Diese Methode gibt die {@code Uri} des {@code index}-ten {@link Element}s im Navifationspfad zurück.
		 * 
		 * @param index Index.
		 * @return {@code Uri} des {@code index}-ten {@link Element}s im Navifationspfad.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public String pathUri(final int index) throws IndexOutOfBoundsException {
			return this.path().pathUri(index);
		}

		/**
		 * Diese Methode gibt die {@code Uri}s der {@link Element}s im Navifationspfad als unveränderliche {@link List} zurück.
		 * 
		 * @see Element#getNamespaceURI()
		 * @see Collections#unmodifiableList(List)
		 * @return {@code Uri}-{@link List}.
		 */
		public List<String> pathUris() {
			return this.path().pathUris();
		}

		/**
		 * Diese Methode gibt den {@code Name} des {@code index}-ten {@link Element}s im Navifationspfad zurück.
		 * 
		 * @param index Index.
		 * @return {@code Name} des {@code index}-ten {@link Element}s im Navifationspfad.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public String pathName(final int index) throws IndexOutOfBoundsException {
			return this.path().pathName(index);
		}

		/**
		 * Diese Methode gibt die {@code Name}s der {@link Element}s im Navifationspfad als unveränderliche {@link List} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Collections#unmodifiableList(List)
		 * @return {@code Name}-{@link List}.
		 */
		public List<String> pathNames() {
			return this.path().pathNames();
		}

		/**
		 * Diese Methode gibt den {@code Child}-Index des {@code index}-ten {@link Element}s im Navifationspfad zurück, den dieses {@link Element} in den {@link Node#getChildNodes()} seines {@code Eltern}-{@link Node}s hat.
		 * 
		 * @param index Index.
		 * @return {@code Child}-Index des {@code index}-ten {@link Element}s im Navifationspfad.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public int pathIndex(final int index) throws IndexOutOfBoundsException {
			return this.path().pathIndex(index);
		}

		/**
		 * Diese Methode gibt die {@code Child}-Indices der {@link Element}s im Navifationspfad als unveränderliche {@link List} zurück. Der {@code Child}-Index ist die Position eines {@link Element}s in den {@link Node#getChildNodes()} seines {@code Eltern}-{@link Node}s.
		 * 
		 * @see Node#getChildNodes()
		 * @see Collections#unmodifiableList(List)
		 * @return {@code Child}-Index-{@link List}.
		 */
		public List<Integer> pathIndices() {
			return this.path().pathIndices();
		}

		/**
		 * Diese Methode gibt die Länge des Navigationspfads, d.h. die Anzahl seiner {@link Element}s zurück.
		 * 
		 * @return Länge des Navigationspfads.
		 */
		public int pathLength() {
			return this.path().pathLength();
		}

	}

	/**
	 * Diese Schnittstelle definiert einen Filter zur Erkennung der {@code ID}-{@link Attr}s bzw. -{@link Element}s, die für die erzeugung des {@link EncodeDocument#getNavigationEntryPool()} verwendet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeNavigationPathFilter {

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das im gegebenen {@link EncodeElementPath} beschriebene {@link Element} als {@code ID} des Navigationspfads verwendet werden soll.
		 * 
		 * @param elementPath {@link EncodeElementPath}.
		 * @return {@code true}, wenn das {@link Element} die {@code ID} des Navigationspfads enthält.
		 */
		public boolean isId(EncodeElementPath elementPath);

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das im gegebenen {@link EncodeAttributePath} beschriebene {@link Attr} als {@code ID} des Navigationspfads verwendet werden soll.
		 * 
		 * @param attributePath {@link EncodeAttributePath}.
		 * @return {@code true}, wenn das {@link Attr} die {@code ID} des Navigationspfads enthält.
		 */
		public boolean isId(EncodeAttributePath attributePath);

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index, der als Element in einem {@link EncodePool} verwendet werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem implements Cloneable {

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeValue}.
		 */
		public static final int TYPE_VALUE = 1;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeGroup}.
		 */
		public static final int TYPE_GROUP = 2;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeIndices}.
		 */
		public static final int TYPE_INDICES = 3;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeXmlnsLabel}.
		 */
		public static final int TYPE_XMLNS_LABEL = 4;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeElementLabel}.
		 */
		public static final int TYPE_ELEMENT_LABEL = 5;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeAttributeLabel}.
		 */
		public static final int TYPE_ATTRIBUTE_LABEL = 6;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeNavigationEntry}.
		 */
		public static final int TYPE_NAVIGATION_ENTRY = 7;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeTextNode}.
		 */
		public static final int TYPE_TEXT_NODE = 8;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeElementNode}.
		 */
		public static final int TYPE_ELEMENT_NODE = 9;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeCommentNode}.
		 */
		public static final int TYPE_COMMENT_NODE = 10;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeReferenceNode}.
		 */
		public static final int TYPE_REFERENCE_NODE = 11;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeAttributeNode}.
		 */
		public static final int TYPE_ATTRIBUTE_NODE = 12;

		/**
		 * Dieses Feld speichert den Typ von {@link EncodeInstructionNode}.
		 */
		public static final int TYPE_INSTRUCTION_NODE = 13;

		/**
		 * Dieses Feld speichert das nächste {@link EncodeItem} im {@link EncodePool}.
		 * 
		 * @see EncodePool#getEntryNext(EncodeItem)
		 * @see EncodePool#setEntryNext(EncodeItem, EncodeItem)
		 */
		EncodeItem next = this;

		/**
		 * Dieses Feld speichert den Index des Datensatzes.
		 */
		protected int index = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeItem clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}

		/**
		 * Diese Methode gibt den Typ des Datensatzes zurück.
		 * 
		 * @return Typ.
		 */
		public abstract int getType();

		/**
		 * Diese Methode gibt den Index des Datensatzes zurück.
		 * 
		 * @return Index des Datensatzes.
		 */
		public int getIndex() {
			return this.index;
		}

		/**
		 * Diese Methode setzt den Index des Datensatzes.
		 * 
		 * @param index Index des Datensatzes.
		 */
		public void setIndex(final int index) {
			this.index = index;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeValue}, sonst {@code null}.
		 */
		public EncodeValue asValue() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einer {@link EncodeGroup}, sonst {@code null}.
		 */
		public EncodeGroup asGroup() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei {@link EncodeIndices}, sonst {@code null}.
		 */
		public EncodeIndices asIndices() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeXmlnsLabel}, sonst {@code null}.
		 */
		public EncodeXmlnsLabel asXmlnsLabel() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeElementLabel}, sonst {@code null}.
		 */
		public EncodeElementLabel asElementLabel() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeAttributeLabel}, sonst {@code null}.
		 */
		public EncodeAttributeLabel asAttributeLabel() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeNavigationEntry}, sonst {@code null}.
		 */
		public EncodeNavigationEntry asNavigationEntry() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeTextNode}, sonst {@code null}.
		 */
		public EncodeTextNode asTextNode() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeElementNode}, sonst {@code null}.
		 */
		public EncodeElementNode asElementNode() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeCommentNode}, sonst {@code null}.
		 */
		public EncodeCommentNode asCommentNode() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeReferenceNode}, sonst {@code null}.
		 */
		public EncodeReferenceNode asReferenceNode() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeAttributeNode}, sonst {@code null}.
		 */
		public EncodeAttributeNode asAttributeNode() {
			return null;
		}

		/**
		 * Diese Methode gibt {@code this} oder {@code null} zurück.
		 * 
		 * @return {@code this} bei einem {@link EncodeInstructionNode}, sonst {@code null}.
		 */
		public EncodeInstructionNode asInstructionNode() {
			return null;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Hash}-{@code Set}. Wenn das via {@link #unique(EncodeItem)} zu einem gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, werden dieses als Rückgabewert verwendet und dessen Wiederverwendung via {@link #reuse(EncodeItem)} signalisiert. Das Einfügen eines neuen Elements wird dagegen mit {@link #insert(EncodeItem)} angezeigt. Die Implementation ähnelt einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
	 * 
	 * @see Hash
	 * @see Unique
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class EncodePool<GItem extends EncodeItem> extends Hash<GItem, GItem, GItem> implements Iterable<GItem> {

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #reuse(EncodeItem)}.
		 */
		protected int reused = 0;

		/**
		 * Dieser Konstrukteur initialisiert die Größe der {@link Hash}-Tabelle mit {@code 128} Einträgen.
		 * 
		 * @see #verifyLength(int)
		 */
		public EncodePool() {
			this.verifyLength(128);
		}

		/**
		 * {@inheritDoc} Die aktuelle Größe der Tabelle wird verdoppelt, wenn die Anzahl der Einträge diese überschreitet.
		 */
		@Override
		protected int getLength(final int size, final int length) {
			return ((size > length) ? length << 1 : length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GItem getEntryKey(final GItem entry) {
			return entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected GItem getEntryNext(final GItem entry) {
			return (GItem)entry.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntryNext(final GItem entry, final GItem next) {
			entry.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GItem getEntryValue(final GItem entry) {
			return entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GItem createEntry(final GItem key, final GItem value, final GItem next, final int hash) {
			value.next = next;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int getKeyHash(final GItem key) {
			return this.hash(key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final GItem entry, final GItem key, final int hash) {
			return this.equals(entry, key);
		}

		/**
		 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Elements zurück. Das Element ist nie {@code null}.
		 * 
		 * @param input Element.
		 * @return {@link Object#hashCode() Streuwert}.
		 */
		protected abstract int hash(final GItem input);

		/**
		 * Diese Methode wird beim Wiederverwenden des gegebenen Elements aufgerufen. Hierbei wird der {@link EncodeItem#index} um {@code 1} erhöht.
		 * 
		 * @see #unique(EncodeItem)
		 * @param value Element.
		 */
		protected void reuse(final GItem value) {
			value.index++;
			this.reused++;
		}

		/**
		 * Diese Methode wird beim Einfügen des gegebenen Elements aufgerufen. Hierbei wird der {@link EncodeItem#index} aus {@code 1} gesetzt.
		 * 
		 * @see #unique(EncodeItem)
		 * @param value Element.
		 */
		protected void insert(final GItem value) {
			value.index = 1;
		}

		/**
		 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Elemente zurück. Die Elemente sind nie {@code null}.
		 * 
		 * @param input1 Element 1.
		 * @param input2 Element 2.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Elemente.
		 */
		protected abstract boolean equals(final GItem input1, final GItem input2);

		/**
		 * Diese Methode gibt das einzigartige, zum gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} Element zurück. Wenn ein solches Element gefunden wurde, wird dessen Wiederverwendung via {@link #reuse(EncodeItem)} signalisiert. Sollte dagegen kein {@link #equals(EncodeItem, EncodeItem) äquivalentes} Element gefunden werden, werden das gegebene Element in den {@link EncodePool} eingefügt, das Einfügen mit {@link #insert(EncodeItem)} angezeigt und das Element zurück gegeben.
		 * 
		 * @see #hash(EncodeItem)
		 * @see #equals(EncodeItem, EncodeItem)
		 * @param key Element.
		 * @return einzigartiges, {@link #equals(EncodeItem, EncodeItem) äquivalentes} Element.
		 * @throws NullPointerException Wenn das gegebene Element {@code null} ist.
		 * @throws IllegalArgumentException Wenn das gegebene Element eingefügt werden müsste, aber bereits in einen {@link EncodePool} ergefügt wurde.
		 */
		public GItem unique(final GItem key) throws NullPointerException, IllegalArgumentException {
			if(key == null) throw new NullPointerException();
			final GItem value = this.findEntry(key);
			if(value == null){
				if(key.next != key) throw new IllegalArgumentException("item managed elsewhere");
				this.insert(key);
				this.appendEntry(key, key, true);
				return key;
			}
			this.reuse(value);
			return value;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} zu einem Element dieses {@link EncodePool}s ist.
		 * 
		 * @see #hash(EncodeItem)
		 * @see #equals(EncodeItem, EncodeItem)
		 * @param key Element.
		 * @return {@code true}, wenn ein zum gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalentes} enthalten ist.
		 * @throws NullPointerException Wenn das gegebene Element {@code null} ist.
		 */
		public boolean contains(final GItem key) throws NullPointerException {
			if(key == null) throw new NullPointerException();
			return this.findEntry(key) != null;
		}

		/**
		 * Diese Methode gibt die Anzahl der Elemente zurück.
		 * 
		 * @return Anzahl der Elemente.
		 */
		public int size() {
			return this.getSize();
		}

		/**
		 * Diese Methode entfernt alle Elemente.
		 */
		public void clear() {
			this.clearEntries();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return this.getEntries();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.format("%s(%.1f %%)", this.getClass().getSimpleName(), (1f - (this.reused / (float)(this.size() + this.reused))) * 100f);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeItem} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValue extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@link String}.
		 */
		protected final String string;

		/**
		 * Dieser Konstrukteur initialisiert den {@link String}.
		 * 
		 * @param string {@link String}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue(final String string) throws NullPointerException {
			if(string == null) throw new NullPointerException();
			this.string = string;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeValue clone() throws CloneNotSupportedException {
			return new EncodeValue(this.string);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_VALUE;
		}

		/**
		 * Diese Methode gibt den {@link String} zurück.
		 * 
		 * @return {@link String}.
		 */
		public String getString() {
			return this.string;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeValue asValue() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("V" + this.index, this.string);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeValue}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValuePool extends EncodePool<EncodeValue> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeValue input) {
			return Objects.hash(input.string);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeValue input1, final EncodeValue input2) {
			return input1.string.equals(input2.string);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeValue} zum gegebenen {@code String} zurück.
		 * 
		 * @see EncodeValue#EncodeValue(String)
		 * @see EncodeValuePool#unique(EncodeValue)
		 * @see EncodeValue#getString()
		 * @param value {@code String}.
		 * @return einzigartiger {@link EncodeValue}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue unique(final String value) throws NullPointerException {
			return this.unique(new EncodeValue(value));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeItem} als Gruppe mehrerer {@link EncodeItem}s.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeGroup extends EncodeItem {

		/**
		 * Diese Klasse implementiert das {@link EncodeItem}-{@link CompactObjectArray}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class EncodeItemArray extends CompactObjectArray<EncodeItem> {

			/**
			 * Diese Klasse implementiert die {@link ArraySection} zu {@link EncodeItemArray}.
			 * 
			 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			protected static final class EncodeItemArraySection extends CompactObjectArraySection<EncodeItem> {

				/**
				 * Dieser Konstrukteur initialisiert den Besitzer.
				 * 
				 * @param owner Besitzer.
				 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
				 */
				public EncodeItemArraySection(final EncodeItemArray owner) throws NullPointerException {
					super(owner);
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				protected boolean equals(final EncodeItem[] array1, final EncodeItem[] array2, final int index1, final int index2) {
					return array1[index1] == array2[index2];
				}

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compare(final EncodeItem o1, final EncodeItem o2) {
				return o1.index - o2.index;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected EncodeItem[] newArray(final int length) {
				return new EncodeItem[length];
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public ObjectArraySection<EncodeItem> section() {
				return new EncodeItemArraySection(this);
			}

		}

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List}.
		 */
		protected final CompactObjectArray<EncodeItem> items = new EncodeItemArray();

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param items {@link EncodeItem}-{@code Array}.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeItem}-{@code Array} {@code null} ist oder enthält.
		 */
		public EncodeGroup(final EncodeItem... items) {
			if((items == null) || Arrays.asList(items).contains(null)) throw new NullPointerException();
			this.items.add(items);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param items {@link EncodeItem}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup(final List<? extends EncodeItem> items) throws NullPointerException {
			if((items == null) || items.contains(null)) throw new NullPointerException();
			this.items.values().addAll(items);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeGroup clone() throws CloneNotSupportedException {
			return new EncodeGroup(this.items.values());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_GROUP;
		}

		/**
		 * Diese Methode gibt die {@link EncodeItem}-{@link List} zurück.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return {@link EncodeItem}-{@link List}.
		 */
		public List<EncodeItem> getItems() {
			return Collections.unmodifiableList(this.items.values());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeGroup asGroup() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, "G" + this.index, this.items.values());
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeGroup}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeGroupPool extends EncodePool<EncodeGroup> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeGroup input) {
			return Objects.hash(input.items);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeGroup input1, final EncodeGroup input2) {
			return input1.items.equals(input2.items);
		}

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeGroup} mit den gegebenen {@link EncodeItem}s zurück.
		 * 
		 * @see EncodeGroup#EncodeGroup(List)
		 * @see EncodeGroupPool#unique(EncodeGroup)
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeGroup}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup unique(final List<? extends EncodeItem> value) throws NullPointerException {
			return this.unique(new EncodeGroup(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines Navigationspfads.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeIndices extends EncodeItem {

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List}.
		 */
		protected final CompactIntegerArray indices = new CompactIntegerArray();

		/**
		 * Dieser Konstrukteur initialisiert die {@code Indices}.
		 * 
		 * @param indices {@code Indices}.
		 * @throws NullPointerException Wenn die gegebenen {@code Indices} {@code null} sind.
		 */
		public EncodeIndices(final int... indices) throws NullPointerException {
			if(indices == null) throw new NullPointerException();
			this.indices.add(indices);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@code Indices}.
		 * 
		 * @param indices {@link Integer}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link Integer}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeIndices(final List<Integer> indices) throws NullPointerException {
			if((indices == null) || indices.contains(null)) throw new NullPointerException();
			this.indices.values().addAll(indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeIndices clone() throws CloneNotSupportedException {
			return new EncodeIndices(this.indices.toArray());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_INDICES;
		}

		/**
		 * Diese Methode gibt die {@code Index}-{@link List} zurück.
		 * 
		 * @return {@code Index}-{@link List}.
		 */
		public List<Integer> getIndices() {
			return Collections.unmodifiableList(this.indices.values());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeIndices asIndices() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("I" + this.index, this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeIndices}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeIndicesPool extends EncodePool<EncodeIndices> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeIndices input) {
			return Objects.hash(input.indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeIndices input1, final EncodeIndices input2) {
			return input1.indices.equals(input2.indices);
		}

		/**
		 * Diese Methode gibt die einzigartigen {@link EncodeIndices} mit der gegebenen {@code Indices} zurück.
		 * 
		 * @see EncodeIndices#EncodeIndices(int...)
		 * @see EncodeIndicesPool#unique(EncodeIndices)
		 * @param indices {@code Indices}.
		 * @return einzigartige {@link EncodeIndices}.
		 * @throws NullPointerException Wenn die gegebenen {@code Indices} {@code null} sind.
		 */
		public EncodeIndices unique(final int... indices) throws NullPointerException {
			return this.unique(new EncodeIndices(indices));
		}

		/**
		 * Diese Methode gibt die einzigartigen {@link EncodeIndices} mit der gegebenen {@code Indices} zurück.
		 * 
		 * @see EncodeIndices#EncodeIndices(List)
		 * @see EncodeIndicesPool#unique(EncodeIndices)
		 * @param indices {@code Indices}.
		 * @return einzigartige {@link EncodeIndices}.
		 * @throws NullPointerException Wenn die gegebenen {@code Indices} {@code null} sind oder enthalten.
		 */
		public EncodeIndices unique(final List<Integer> indices) throws NullPointerException {
			return this.unique(new EncodeIndices(indices));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@code Uri}-{@code Prefix}-Paars.
	 * 
	 * @see Node#lookupPrefix(String)
	 * @see Node#lookupNamespaceURI(String)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeXmlnsLabel extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Uri}.
		 */
		protected final EncodeValue uri;

		/**
		 * Dieses Feld speichert den {@code Prefix}.
		 */
		protected final EncodeValue prefix;

		/**
		 * Dieser Konstrukteur initialisiert {@code Uri} und {@code Prefix}.
		 * 
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeXmlnsLabel(final EncodeValue uri, final EncodeValue prefix) throws NullPointerException {
			if((uri == null) || (prefix == null)) throw new NullPointerException();
			this.uri = uri;
			this.prefix = prefix;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeXmlnsLabel clone() throws CloneNotSupportedException {
			return new EncodeXmlnsLabel(this.uri, this.prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_XMLNS_LABEL;
		}

		/**
		 * Diese Methode gibt die {@code Uri} zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@code Uri}.
		 */
		public EncodeValue getUri() {
			return this.uri;
		}

		/**
		 * Diese Methode gibt den {@code Prefix} zurück.
		 * 
		 * @see Node#getPrefix()
		 * @return {@code Prefix}.
		 */
		public EncodeValue getPrefix() {
			return this.prefix;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeXmlnsLabel asXmlnsLabel() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("L" + this.index, this.uri, this.prefix);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeXmlnsLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeXmlnsLabelPool extends EncodePool<EncodeXmlnsLabel> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeXmlnsLabel input) {
			return Objects.hash(input.uri, input.prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeXmlnsLabel input1, final EncodeXmlnsLabel input2) {
			return (input1.uri == input2.uri) && (input1.prefix == input2.prefix);
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeXmlnsLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeXmlnsLabel#EncodeXmlnsLabel(EncodeValue, EncodeValue)
		 * @see EncodeXmlnsLabelPool#unique(EncodeXmlnsLabel)
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return einzigartiges {@link EncodeXmlnsLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeXmlnsLabel unique(final EncodeValue uri, final EncodeValue prefix) throws NullPointerException {
			return this.unique(new EncodeXmlnsLabel(uri, prefix));
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code Uri} und {@code Prefix} als während des Einlesens oder Aufbauens eines {@link EncodeDocument}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeXmlnsBuilder {

		/**
		 * Dieses Feld speichert den {@code Eltern}-{@link EncodeXmlnsBuilder} oder {@code null}.
		 */
		protected EncodeXmlnsBuilder parent;

		/**
		 * Dieses Feld speichert das {@link EncodeDocument}.
		 */
		protected final EncodeDocument document;

		/**
		 * Dieses Feld speichert die {@code Prefix}-{@code Uri}-{@link Map} oder {@code null}.
		 */
		protected Map<String, String> lookupUriMap;

		/**
		 * Dieses Feld speichert die {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupUriMap} oder {@code null}.
		 */
		protected List<EncodeXmlnsLabel> lookupUriList;

		/**
		 * Dieses Feld speichert die {@code Uri}-{@code Prefix}-{@link Map} oder {@code null}.
		 */
		protected Map<String, String> lookupPrefixMap;

		/**
		 * Dieses Feld speichert die {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupPrefixMap} oder {@code null}.
		 */
		protected List<EncodeXmlnsLabel> lookupPrefixList;

		/**
		 * Dieser Konstrukteur initialisiert den {@code Eltern}-{@link EncodeXmlnsBuilder}. Als {@link EncodeDocument} wird das des {@code Eltern}-{@link EncodeXmlnsBuilder}s verwendet.
		 * 
		 * @see #append(String, String)
		 * @param parent {@code Eltern}-{@link EncodeXmlnsBuilder}.
		 * @throws NullPointerException Wenn der gegebene {@link EncodeXmlnsBuilder} {@code null} ist.
		 */
		public EncodeXmlnsBuilder(final EncodeXmlnsBuilder parent) throws NullPointerException {
			if(parent == null) throw new NullPointerException();
			this.parent = parent;
			this.document = parent.document;
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link EncodeDocument}.
		 * 
		 * @param document {@link EncodeDocument}.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeDocument} {@code null} ist.
		 */
		public EncodeXmlnsBuilder(final EncodeDocument document) throws NullPointerException {
			if(document == null) throw new NullPointerException();
			this.parent = null;
			this.document = document;
		}

		/**
		 * Diese Methode gibt das {@link Iterable} über alle {@code Prefix}es zurück, wobei {@code Prefix}e auch mehrfach vorkommen können.
		 * 
		 * @return {@code Prefix}-{@link Iterable}.
		 */
		protected Iterable<String> prefixes() {
			final Map<String, String> lookupUriMap = this.lookupUriMap;
			final EncodeXmlnsBuilder parent = this.parent;
			if(lookupUriMap != null){
				if(parent != null) return Iterables.chainedIterable(parent.prefixes(), lookupUriMap.keySet());
				return lookupUriMap.keySet();
			}
			if(parent != null) return parent.prefixes();
			return Iterables.voidIterable();
		}

		/**
		 * Diese Methode erzeugt beide {@link Map}s.
		 */
		protected void createMaps() {
			this.lookupUriMap = new LinkedHashMap<String, String>(1);
			this.lookupPrefixMap = new LinkedHashMap<String, String>(1);
		}

		/**
		 * Diese Methode fügt das gegebene {@code Uri/Prefix}-Paar in die {@link Map}s ein.
		 * 
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @throws IllegalArgumentException Wenn eine der Eingaben ungültig ist.
		 */
		protected void updateMaps(final String uri, final String prefix) throws IllegalArgumentException {
			if(XMLConstants.NULL_NS_URI.equals(uri)) throw new IllegalArgumentException("uri invalid");
			if(XMLConstants.XML_NS_PREFIX.equals(prefix)) throw new IllegalArgumentException("prefix invalid");
			this.lookupUriMap.put(prefix, uri);
			this.lookupPrefixMap.put(uri, prefix);
		}

		/**
		 * Diese Methode erzeugt die beiden {@link List}s der {@code Uri/Prefix}-Paare, sofern diese notwendig ist.
		 */
		protected void updateLists() {
			if(this.lookupUriList != null) return;
			if(this.lookupUriMap == null){
				if(this.parent == null){
					this.lookupUriList = Collections.emptyList();
					this.lookupPrefixList = Collections.emptyList();
				}else{
					this.parent.updateLists();
					this.lookupUriList = this.parent.lookupUriList;
					this.lookupPrefixList = this.parent.lookupPrefixList;
				}
				return;
			}
			final EncodeDocument document = this.document;
			final Map<String, String> lookupUriMap = new LinkedHashMap<String, String>();
			final Map<String, String> lookupPrefixMap = new LinkedHashMap<String, String>();
			final ArrayList<EncodeXmlnsLabel> lookupUriList = new ArrayList<EncodeXmlnsLabel>();
			final ArrayList<EncodeXmlnsLabel> lookupPrefixList = new ArrayList<EncodeXmlnsLabel>();
			for(final String xmlnsPrefix: this.prefixes()){
				final String xmlnsUri = this.lookupUri(xmlnsPrefix);
				lookupUriMap.put(xmlnsPrefix, xmlnsUri);
			}
			for(final Entry<String, String> xmlnsEntry: lookupUriMap.entrySet()){
				final String xmlnsUri = xmlnsEntry.getValue();
				final String xmlnsPrefix = xmlnsEntry.getKey();
				final EncodeXmlnsLabel xmlnsLabel = document.createXmlnsLabel(xmlnsUri, xmlnsPrefix);
				lookupUriList.add(xmlnsLabel);
				lookupPrefixMap.put(xmlnsUri, this.lookupPrefix(xmlnsUri));
			}
			lookupUriList.trimToSize();
			for(final Entry<String, String> xmlnsEntry: lookupPrefixMap.entrySet()){
				final String xmlnsUri = xmlnsEntry.getKey();
				final String xmlnsPrefix = xmlnsEntry.getValue();
				final EncodeXmlnsLabel xmlnsLabel = document.createXmlnsLabel(xmlnsUri, xmlnsPrefix);
				lookupPrefixList.add(xmlnsLabel);
			}
			lookupPrefixList.trimToSize();
			if(lookupUriList.size() > 1){
				Collections.sort(lookupUriList, Encoder2.EncodeXmlnsLabel_PrefixUri_Comparator);
			}
			if(lookupPrefixList.size() > 1){
				Collections.sort(lookupPrefixList, Encoder2.EncodeXmlnsLabel_UriPrefix_Comparator);
			}
			this.lookupUriList = lookupUriList;
			this.lookupPrefixList = lookupPrefixList;
		}

		/**
		 * Diese Methode setzt beide {@link List}s auf {@code null}.
		 */
		protected void removeLists() {
			this.lookupUriList = null;
			this.lookupPrefixList = null;
		}

		/**
		 * Diese Methode ordnet dem gegebenen {@code Prefix} die gegebene {@code Uri} zu.
		 * 
		 * @see EncodeElementNodeBuilder#createXmlns(String, String)
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn dem {@code Prefix} bereits eine andere {@code Uri} zugeordnet ist.
		 */
		public void create(final String uri, final String prefix) throws NullPointerException, IllegalArgumentException {
			if((uri == null) || (prefix == null)) throw new NullPointerException();
			if(uri.equals(this.lookupUri(prefix))) return;
			if(this.lookupUriMap == null){
				this.createMaps();
			}else if(this.lookupUriMap.containsKey(prefix)) throw new IllegalArgumentException("prefix already exists");
			this.updateMaps(uri, prefix);
			this.removeLists();
		}

		/**
		 * Diese Methode ordnet dem gegebenen {@code Prefix} die gegebene {@code Uri} zu und gibt {@code this} bzw. einen neuen {@link EncodeXmlnsBuilder} zurück. Ein neuer {@link EncodeXmlnsBuilder} wird nur dann zurück gegeben, wenn dem {@code Prefix} in diesem {@link EncodeXmlnsBuilder} bereits eine {@code Uri} zugeordnet ist.
		 * 
		 * @see EncodeDocumentHandler#startPrefixMapping(String, String)
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return {@code this} oder ein neuer {@link EncodeXmlnsBuilder}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeXmlnsBuilder append(final String uri, final String prefix) throws NullPointerException {
			if((uri == null) || (prefix == null)) throw new NullPointerException();
			if(this.lookupUriMap == null){
				this.createMaps();
			}else if(this.lookupUriMap.containsKey(prefix)){
				final EncodeXmlnsBuilder builder = new EncodeXmlnsBuilder(this);
				builder.createMaps();
				builder.updateMaps(uri, prefix);
				return builder;
			}
			this.updateMaps(uri, prefix);
			this.removeLists();
			return this;
		}

		/**
		 * Diese Methode entfernt die letzte Zuordnung des gegebenen {@code Prefix}es und gibt {@code this} bzw. den {@code Eltern}-{@link EncodeXmlnsBuilder} zurück. Der {@code Eltern}-{@link EncodeXmlnsBuilder} wird nur dann zurück gegeben, dieser {@link EncodeXmlnsBuilder} durch das Entfernen der Zuordnung leer geworden ist.
		 * 
		 * @see EncodeDocumentHandler#endPrefixMapping(String)
		 * @param prefix {@code Prefix}.
		 * @return {@code this} oder {@code Eltern}-{@link EncodeXmlnsBuilder}.
		 */
		public EncodeXmlnsBuilder remove(final String prefix) {
			final EncodeXmlnsBuilder parent = this.parent;
			final Map<String, String> lookupUriMap = this.lookupUriMap;
			if(lookupUriMap != null){
				final String uri = lookupUriMap.remove(prefix);
				if(uri != null){
					final Map<String, String> lookupPrefixMap = this.lookupPrefixMap;
					lookupPrefixMap.remove(uri);
					for(final Entry<String, String> entry: lookupUriMap.entrySet()){
						if(uri.equals(entry.getValue())){
							lookupPrefixMap.put(uri, entry.getKey());
						}
					}
					this.removeLists();
					if((parent == null) || !lookupUriMap.isEmpty()) return this;
					return parent;
				}
			}
			if(parent == null) return this;
			this.parent = parent.remove(prefix);
			return this;
		}

		/**
		 * Diese Methode gibt die dem gegebenen {@code Prefix} zugeordnete {@code Uri} oder {@code null} zurück. Als Suchraum gelten dieser und der {@code Eltern}-{@link EncodeXmlnsBuilder}.
		 * 
		 * @param prefix {@code Prefix}.
		 * @return {@code Uri} oder {@code null}.
		 */
		public String lookupUri(final String prefix) {
			if(prefix == null) return null;
			final Map<String, String> lookupUriMap = this.lookupUriMap;
			if(lookupUriMap != null){
				final String uri = lookupUriMap.get(prefix);
				if(uri != null) return uri;
			}
			final EncodeXmlnsBuilder parent = this.parent;
			if(parent != null) return parent.lookupUri(prefix);
			if(prefix.equals(XMLConstants.XML_NS_PREFIX)) return XMLConstants.XML_NS_URI;
			if(prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) return XMLConstants.NULL_NS_URI;
			return null;
		}

		/**
		 * Diese Methode gibt die {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupUri(String)} zurück.
		 * 
		 * @see EncodeElementLabel#getLookupUriList()
		 * @return {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupUri(String)}.
		 */
		public List<EncodeXmlnsLabel> lookupUriList() {
			this.updateLists();
			return this.lookupUriList;
		}

		/**
		 * Diese Methode gibt den zuletzt der gegebenen {@code Uri} zugeordnete {@code Prefix} oder {@code null} zurück. Als Suchraum gelten dieser und der {@code Eltern}-{@link EncodeXmlnsBuilder}.
		 * 
		 * @param uri {@code Uri}.
		 * @return {@code Prefix} oder {@code null}.
		 */
		public String lookupPrefix(final String uri) {
			if(uri == null) return null;
			final Map<String, String> lookupPrefixMap = this.lookupPrefixMap;
			if(lookupPrefixMap != null){
				final String prefix = lookupPrefixMap.get(uri);
				if(prefix != null) return prefix;
			}
			final EncodeXmlnsBuilder parent = this.parent;
			if(parent != null) return parent.lookupPrefix(uri);
			if(uri.equals(XMLConstants.XML_NS_URI)) return XMLConstants.XML_NS_PREFIX;
			if(uri.equals(XMLConstants.NULL_NS_URI)) return XMLConstants.DEFAULT_NS_PREFIX;
			return null;
		}

		/**
		 * Diese Methode gibt die {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupPrefix(String)} zurück.
		 * 
		 * @see EncodeElementLabel#getLookupPrefixList()
		 * @return {@link List} der {@link EncodeXmlnsLabel}s zu {@link #lookupPrefix(String)}.
		 */
		public List<EncodeXmlnsLabel> lookupPrefixList() {
			this.updateLists();
			return this.lookupPrefixList;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines Navigationspfades mit Id.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeNavigationEntry extends EncodeItem {

		/**
		 * Dieses Feld speichert die {@code Id}.
		 */
		protected final EncodeValue id;

		/**
		 * Dieses Feld speichert die {@code Indices}.
		 */
		protected final EncodeIndices indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Id} und {@code Indices}.
		 * 
		 * @param id {@code Id}.
		 * @param indices {@code Indices}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeNavigationEntry(final EncodeValue id, final EncodeIndices indices) throws NullPointerException {
			if((id == null) || (indices == null)) throw new NullPointerException();
			this.id = id;
			this.indices = indices;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeNavigationEntry clone() throws CloneNotSupportedException {
			return new EncodeNavigationEntry(this.id, this.indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_NAVIGATION_ENTRY;
		}

		/**
		 * Diese Methode gibt die {@code Id} zurück.
		 * 
		 * @see Document#getElementById(String)
		 * @see EncodeDocument#getNavigationEntryPool()
		 * @return {@code Id}.
		 */
		public EncodeValue getId() {
			return this.id;
		}

		/**
		 * Diese Methode gibt die {@code Indices} zurück. Jeder Index beschreibt die Position eines {@link EncodeElementNode}s in den {@link Node#getChildNodes()} seines {@code Eltern}-{@link Node}s. Der erste Index bezieht sich auf {@link Document#getChildNodes()}, die folgenden beziehen sich dann auf die {@link Element#getChildNodes()} des jeweils zuvor referenzierten {@link Element}s.
		 * 
		 * @see Document#getElementById(String)
		 * @see EncodeDocument#getNavigationEntryPool()
		 * @return {@code Indices}.
		 */
		public EncodeIndices getIndices() {
			return this.indices;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeNavigationEntry asNavigationEntry() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("N" + this.index, this.id, this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeNavigationEntry}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeNavigationEntryPool extends EncodePool<EncodeNavigationEntry> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeNavigationEntry input) {
			return Objects.hash(input.id.string);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeNavigationEntry input1, final EncodeNavigationEntry input2) {
			return Objects.equals(input1.id.string, input2.id.string);
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeNavigationEntry} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeNavigationEntry#EncodeNavigationEntry(EncodeValue, EncodeIndices)
		 * @see EncodeNavigationEntryPool#unique(EncodeNavigationEntry)
		 * @param id {@code Id}.
		 * @param indices {@code Indices}.
		 * @return einzigartiges {@link EncodeNavigationEntry}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeNavigationEntry unique(final EncodeValue id, final EncodeIndices indices) throws NullPointerException {
			return this.unique(new EncodeNavigationEntry(id, indices));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Node} mit {@code Value}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeNode extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Value}.
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert den {@code Value}.
		 * 
		 * @param value {@code Value}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeNode(final EncodeValue value) throws NullPointerException {
			if(value == null) throw new NullPointerException();
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeNode clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}

		/**
		 * Diese Methode gibt den {@code Value} zurück.
		 * 
		 * @return {@code Value}.
		 */
		public EncodeValue getValue() {
			return this.value;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeNode}s.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeNodePool<GItem extends EncodeNode> extends EncodePool<GItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final GItem input) {
			return Objects.hash(input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final GItem input1, final GItem input2) {
			return input1.value == input2.value;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktens Objekt zum Schrrittweisen Aufbau eines {@link EncodeDocument}s bzw. {@link EncodeElementNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ dieses Objekts.
	 */
	public static abstract class EncodeBuilder<GThiz extends EncodeBuilder<?>> {

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsBuilder} oder {@code null}.
		 */
		protected EncodeXmlnsBuilder xmlns;

		/**
		 * Dieses Feld speichert die {@code Children} oder {@code null}.
		 */
		protected List<Object> children = new ArrayList<Object>(0);

		/**
		 * Dieses Feld speichert das {@link EncodeDocument} oder {@code null}.
		 */
		protected EncodeDocument document;

		/**
		 * Diese Methode gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		protected abstract GThiz thiz();

		/**
		 * Diese Methode setzt {@link #xmlns} und {@link #children} auf {@code null}.
		 */
		protected void clear() {
			this.xmlns = null;
			this.children = null;
		}

		/**
		 * Diese Methode prüft die Modifizierbarkeit.
		 * 
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		protected void check() throws IllegalStateException {
			if(this.children == null) throw new IllegalStateException("already committed");
		}

		/**
		 * Diese Methode gibt die {@link EncodeItem}-{@link List} der {@code Children} zurück. Diese kann {@link EncodeTextNode}s, {@link EncodeCommentNode}s, {@link EncodeElementNode}s, {@link EncodeReferenceNode}s und {@link EncodeInstructionNode}s enthalten. Jeden noch nicht bestätigte {@link EncodeElementNodeBuilder} wird via {@link EncodeElementNodeBuilder#result()} bestätigt.
		 * 
		 * @return {@link EncodeItem}-{@link List} der {@code Children}
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		protected List<EncodeItem> children() throws IllegalStateException {
			this.check();
			final List<Object> source = this.children;
			final int size = source.size();
			final List<EncodeItem> target = new ArrayList<EncodeItem>(size);
			for(int i = 0; i < size; i++){
				final Object item = source.get(i);
				if(item instanceof EncodeElementNodeBuilder<?>){
					final EncodeElementNodeBuilder<?> builder = (EncodeElementNodeBuilder<?>)item;
					final EncodeElementNode result = builder.result();
					source.set(i, result);
					target.add(i, result);
				}else{
					target.add(i, (EncodeItem)item);
				}
			}
			return target;
		}

		/**
		 * Diese Methode registriert den gegebenen Navigationspfad unter der gegebenen Id. Der Navigationspfad beschreibt dabei die Kindknotenindices zur Navigation zum Ziel {@link EncodeElementNode}.
		 * 
		 * @param id Id.
		 * @param indices Navigationspfad.
		 * @throws IllegalArgumentException Wenn der gegebenen Id bereits ein Navigationspfad zugeordnet ist.
		 */
		protected abstract void createId(String id, List<Integer> indices) throws IllegalArgumentException;

		/**
		 * Diese Methode fügt einen neuen {@link EncodeElementNodeBuilder} zur Definition eines {@link EncodeElementNode}s mit dem gegebenen {@code Name} als letzten Kindknoten an und gibt ihn zurück. Als {@code Uri} wird {@link XMLConstants#NULL_NS_URI} verwendet.
		 * 
		 * @see #createElement(String, String)
		 * @param name {@code Name}.
		 * @return {@link EncodeElementNodeBuilder}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public EncodeElementNodeBuilder<GThiz> createElement(final String name) throws IllegalStateException {
			return this.createElement(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * Diese Methode fügt einen neuen {@link EncodeElementNodeBuilder} zur Definition eines {@link EncodeElementNode}s mit der gegebenen {@code Uri} und dem gegebenen {@code Name} als letzten Kindknoten an und gibt ihn zurück.
		 * 
		 * @see EncodeElementLabel#getUri()
		 * @see EncodeElementLabel#getName()
		 * @see EncodeElementNodeBuilder#commit()
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @return {@link EncodeElementNodeBuilder}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public EncodeElementNodeBuilder<GThiz> createElement(final String uri, final String name) throws NullPointerException, IllegalStateException {
			this.check();
			final EncodeElementNodeBuilder<GThiz> builder = new EncodeElementNodeBuilder<GThiz>(this.thiz(), uri, name, this.children.size());
			this.children.add(builder.index, builder);
			return builder;
		}

		/**
		 * Diese Methode fügt den {@link EncodeCommentNode} mit dem gegebenen {@code Value} als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#getCommentNodePool()
		 * @see EncodeDocument#createCommentNode(String)
		 * @param value {@code Value}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public GThiz createComment(final String value) throws NullPointerException, IllegalStateException {
			this.check();
			this.children.add(this.document.createCommentNode(value));
			return this.thiz();
		}

		/**
		 * Diese Methode fügt die {@link EncodeInstructionNode} mit den gegebenen Eigenschaften als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#getInstructionNodePool()
		 * @see EncodeDocument#createInstructionNode(String, String)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public GThiz createInstruction(final String name, final String value) throws NullPointerException, IllegalStateException {
			this.check();
			this.children.add(this.document.createInstructionNode(name, value));
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Text}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeTextNode extends EncodeNode {

		/**
		 * Dieser Konstrukteur initialisiert den {@code Value}.
		 * 
		 * @param value {@code Value}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeTextNode(final EncodeValue value) throws NullPointerException {
			super(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeTextNode clone() throws CloneNotSupportedException {
			return new EncodeTextNode(this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_TEXT_NODE;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Text#getData()
		 * @see Text#getNodeValue()
		 */
		@Override
		public EncodeValue getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeTextNode asTextNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("T" + this.index, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeTextNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeTextNodePool extends EncodeNodePool<EncodeTextNode> {

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeTextNode} mit dem gegebenen {@code Value} zurück.
		 * 
		 * @see EncodeTextNode#EncodeTextNode(EncodeValue)
		 * @see EncodeTextNodePool#unique(EncodeTextNode)
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeTextNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeTextNode unique(final EncodeValue value) throws NullPointerException {
			return this.unique(new EncodeTextNode(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementNode extends EncodeItem {

		/**
		 * Dieses Feld speichert das {@code Label} bzw. den {@code Name}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getPrefix()
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		protected final EncodeAttributeLabel label;

		/**
		 * Dieses Feld speichert die {@code Children}.
		 * 
		 * @see Element#getChildNodes()
		 */
		protected final EncodeGroup children;

		/**
		 * Dieses Feld speichert die {@code Attributes}.
		 * 
		 * @see Element#getAttributes()
		 */
		protected final EncodeGroup attributes;

		/**
		 * Dieser Konstrukteur initialisiert {@code Label}, {@code Children} und {@code Attributes}.
		 * 
		 * @param label {@code Label}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNode(final EncodeAttributeLabel label, final EncodeGroup children, final EncodeGroup attributes) throws NullPointerException {
			if((label == null) || (children == null) || (attributes == null)) throw new NullPointerException();
			this.label = label;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeElementNode clone() throws CloneNotSupportedException {
			return new EncodeElementNode(this.label, this.children, this.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_ELEMENT_NODE;
		}

		/**
		 * Diese Methode gibt das {@code Label} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getPrefix()
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 * @return {@code Label}.
		 */
		public EncodeAttributeLabel getLabel() {
			return this.label;
		}

		/**
		 * Diese Methode gibt die {@code Children} zurück.
		 * 
		 * @see Element#getChildNodes()
		 * @return {@code Children}.
		 */
		public EncodeGroup getChildren() {
			return this.children;
		}

		/**
		 * Diese Methode gibt die {@code Attributes} zurück. Die {@link EncodeAttributeNode}s sind darin primär nach {@link EncodeAttributeLabel#getName()} und sekundär nach {@link EncodeAttributeLabel#getUri()} aufsteigend sortiert.
		 * 
		 * @see Element#getAttributes()
		 * @return {@link EncodeGroup} der {@code Attributes}.
		 */
		public EncodeGroup getAttributes() {
			return this.attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeElementNode asElementNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, "E" + this.index, this.label, this.attributes, this.children);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeElementNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementNodePool extends EncodePool<EncodeElementNode> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementNode input) {
			return Objects.hash(input.label, input.children, input.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementNode input1, final EncodeElementNode input2) {
			return (input1.label == input2.label) && (input1.children == input2.children) && (input1.attributes == input2.attributes);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeElementNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeElementNode#EncodeElementNode(EncodeElementLabel, EncodeGroup, EncodeGroup)
		 * @see EncodeElementNodePool#unique(EncodeElementNode)
		 * @param label {@code Label}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return einzigartiger {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNode unique(final EncodeElementLabel label, final EncodeGroup children, final EncodeGroup attributes) throws NullPointerException {
			return this.unique(new EncodeElementNode(label, children, attributes));
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zum Schrrittweisen Aufbau eines {@link EncodeElementNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GParent> Typ des {@code Eltern}-{@link EncodeBuilder}s.
	 */
	public static class EncodeElementNodeBuilder<GParent extends EncodeBuilder<?>> extends EncodeBuilder<EncodeElementNodeBuilder<GParent>> {

		/**
		 * Dieses Feld speichert die {@code Uri} oder {@code null}.
		 */
		protected String uri;

		/**
		 * Dieses Feld speichert den {@code Name} oder {@code null}.
		 */
		protected String name;

		/**
		 * Dieses Feld speichert die Position dieses {@link EncodeElementNodeBuilder}s in seinem {@code Eltern}-{@link EncodeBuilder}.
		 */
		protected final int index;

		/**
		 * Dieses Feld speichert den {@code Eltern}-{@link EncodeBuilder} oder {@code null}.
		 */
		protected final GParent parent;

		/**
		 * Dieses Feld speichert die {@code Attributes} oder {@code null}.
		 */
		protected List<EncodeAttributeNode> attributes;

		/**
		 * Dieses Feld speichert den {@link EncodeElementNode} oder {@code null}.
		 */
		protected EncodeElementNode result;

		/**
		 * Dieser Konstrukteur initialisiert den {@code Eltern}-{@link EncodeBuilder}, die {@code Uri}, den {@code Name} sowie den {@code Child}-Index, den der definierte {@link EncodeElementNode} in den {@code Children} des {@code Eltern}-{@link EncodeBuilder} besitzt.
		 * 
		 * @param parent {@code Eltern}-{@link EncodeBuilder}.
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param index {@code Child}-Index.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNodeBuilder(final GParent parent, final String uri, final String name, final int index) {
			if((uri == null) || (name == null) || (parent == null)) throw new NullPointerException();
			this.uri = uri;
			this.name = name;
			this.index = index;
			this.parent = parent;
			this.xmlns = new EncodeXmlnsBuilder(parent.xmlns);
			this.document = parent.document;
			this.attributes = new ArrayList<EncodeAttributeNode>(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected EncodeElementNodeBuilder<GParent> thiz() {
			return this;
		}

		/**
		 * {@inheritDoc} Auch {@link #uri}, {@link #name}, {@link #document} und {@link #attributes} werden auf {@code null} gesetzt.
		 */
		@Override
		protected void clear() {
			super.clear();
			this.uri = null;
			this.name = null;
			this.document = null;
			this.attributes = null;
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link EncodeElementNode}s ab und gibt ihn zurück. Nach dem Aufruf dieser Methode lässt dieser {@link EncodeElementNodeBuilder} keine Modifikationen mehr zu.
		 * 
		 * @return {@link EncodeElementNode}.
		 * @throws IllegalArgumentException Wenn der {@code Uri} kein {@code Prefix} zugeordnet ist.
		 */
		protected EncodeElementNode result() throws IllegalArgumentException {
			if(this.result != null) return this.result;
			final String prefix = this.xmlns.lookupPrefix(this.uri);
			if(prefix == null) throw new IllegalArgumentException("uri has no prefix");
			this.result =
				this.document.createElementNode(this.uri, this.name, prefix, this.xmlns.lookupUriList(), this.xmlns.lookupPrefixList(), this.children(),
					this.attributes);
			this.clear();
			return this.result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void createId(final String id, final List<Integer> indices) throws IllegalArgumentException {
			indices.add(0, Integer.valueOf(this.index));
			this.parent.createId(id, indices);
		}

		/**
		 * Diese Methode erzeugt einen Navigationspfad mit der gegebenen {@code Id} zum definierten {@link EncodeElementNode} und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#getNavigationEntryPool()
		 * @param id {@code Id}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn die gegebene {@code Id} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 * @throws IllegalArgumentException Wenn die gegebene {@code Id} bereits verwendet wird.
		 */
		public EncodeElementNodeBuilder<GParent> createId(final String id) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			this.check();
			if(id == null) throw new NullPointerException();
			this.createId(id, new ArrayList<Integer>());
			return this.thiz();
		}

		/**
		 * Diese Methode fügt den {@link EncodeTextNode} mit dem gegebenen Wert als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public EncodeElementNodeBuilder<GParent> createText(final String value) throws NullPointerException, IllegalStateException {
			this.check();
			this.children.add(this.document.createTextNode(value));
			return this.thiz();
		}

		/**
		 * Diese Methode erzeugt ein neues {@link EncodeXmlnsLabel} und gibt {@code this} zurück.
		 * 
		 * @see EncodeElementNode#getLabel()
		 * @see EncodeElementLabel#getLookupUriList()
		 * @see EncodeElementLabel#getLookupPrefixList()
		 * @see EncodeDocument#getXmlnsLabelPool()
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 * @throws IllegalArgumentException Wenn dem {@code Prefix} bereits eine andere {@code Uri} zugeordnet ist.
		 */
		public EncodeElementNodeBuilder<GParent> createXmlns(final String uri, final String prefix) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			this.check();
			this.xmlns.create(uri, prefix);
			return this.thiz();
		}

		/**
		 * Diese Methode erzeugt den {@link EncodeAttributeNode} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Als {@code Uri} wird {@link XMLConstants#NULL_NS_URI} verwendet.
		 * 
		 * @see #createAttribute(String, String, String)
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 * @throws IllegalArgumentException Wenn der {@code Uri} kein {@code Prefix} zugeordnet ist oder bereits ein {@link EncodeAttributeNode} mit der gegebenen {@code Uri} und dem gegebenen {@code Name} existiert.
		 */
		public EncodeElementNodeBuilder<GParent> createAttribute(final String name, final String value) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			return this.createAttribute(XMLConstants.NULL_NS_URI, name, value);
		}

		/**
		 * Diese Methode erzeugt den {@link EncodeAttributeNode} mit den gegebenen Eigenschaften und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#getAttributeNodePool()
		 * @see EncodeDocument#createAttributeNode(String, String, String, String)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 * @throws IllegalArgumentException Wenn der gegebenen {@code Uri} kein {@code Prefix} zugeordnet ist oder bereits ein {@link EncodeAttributeNode} mit der gegebenen {@code Uri} und dem gegebenen {@code Name} existiert.
		 */
		public EncodeElementNodeBuilder<GParent> createAttribute(final String uri, final String name, final String value) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			if((uri == null) || (name == null) || (value == null)) throw new NullPointerException();
			this.check();
			final String prefix = this.xmlns.lookupPrefix(uri);
			if(prefix == null) throw new IllegalArgumentException("uri has no prefix");
			for(final EncodeAttributeNode attribute: this.attributes){
				final EncodeAttributeLabel label = attribute.label;
				if(label.uri.string.equals(uri) && label.name.string.equals(name)) throw new IllegalArgumentException("attribute already exists");
			}
			this.attributes.add(this.document.createAttributeNode(uri, name, prefix, value));
			Collections.sort(this.attributes, Encoder2.EncodeAttributeNode_Label_Comparator);
			return this.thiz();
		}

		/**
		 * Diese Methode fügt den {@link EncodeReferenceNode} mit dem gegebenen {@code Name} als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#getCommentNodePool()
		 * @see EncodeDocument#createReferenceNode(String)
		 * @param name {@code Name}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public EncodeElementNodeBuilder<GParent> createReference(final String name) {
			this.check();
			this.children.add(this.document.createReferenceNode(name));
			return this.thiz();
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link EncodeElementNode}s ab und gibt den {@code Eltern}-{@link EncodeBuilder} zurück. Nach dem Aufruf dieser Methode lässt dieser {@link EncodeElementNodeBuilder} keine Modifikationen mehr zu.
		 * 
		 * @return {@code Eltern}-{@link EncodeBuilder}.
		 * @throws IllegalArgumentException Wenn der {@code Uri} kein {@code Prefix} zugeordnet ist.
		 */
		public GParent commit() throws IllegalArgumentException {
			final GParent parent = this.parent;
			if(this.result != null) return parent;
			parent.children.set(this.index, this.result());
			return parent;
		}

	}

	/**
	 * Diese Klasse implementiert das {@link EncodeXmlnsLabel} zur Abstraktion von {@code Uri}, {@code Name} und {@code Prefix} sowie den {@code Xmlns} eines {@link Element}s.
	 * 
	 * @see Element#getLocalName()
	 * @see Element#getNamespaceURI()
	 * @see Element#getPrefix()
	 * @see Element#lookupPrefix(String)
	 * @see Element#lookupNamespaceURI(String)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementLabel extends EncodeAttributeLabel {

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der nach {@code Prefix} sortierten {@code Xmlns}es.
		 * 
		 * @see Element#lookupNamespaceURI(String)
		 */
		protected EncodeGroup lookupUriList;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der nach {@code Uri} sortierten {@code Xmlns}es.
		 * 
		 * @see Element#lookupPrefix(String)
		 */
		protected EncodeGroup lookupPrefixList;

		/**
		 * Dieser Konstrukteur initialisiert {@code Uri}, {@code Name}, {@code Prefix} und {@code Xmlns}es.
		 * 
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @param lookupUriList nach {@code Prefix} sortierten {@code Xmlns}es.
		 * @param lookupPrefixList nach {@code Uri} sortierten {@code Xmlns}es.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementLabel(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix, final EncodeGroup lookupUriList,
			final EncodeGroup lookupPrefixList) throws NullPointerException {
			super(uri, name, prefix);
			if((lookupUriList == null) || (lookupPrefixList == null)) throw new NullPointerException();
			this.lookupUriList = lookupUriList;
			this.lookupPrefixList = lookupPrefixList;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeElementLabel clone() throws CloneNotSupportedException {
			return new EncodeElementLabel(this.uri, this.name, this.prefix, this.lookupUriList, this.lookupPrefixList);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_ELEMENT_LABEL;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der nach {@code Prefix} sortierten {@code Xmlns}es zurück.
		 * 
		 * @see Element#lookupNamespaceURI(String)
		 * @return nach {@code Prefix} sortierten {@code Xmlns}es.
		 */
		public EncodeGroup getLookupUriList() {
			return this.lookupUriList;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der nach {@code Uri} sortierten {@code Xmlns}es zurück.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @return nach {@code Uri} sortierten {@code Xmlns}es.
		 */
		public EncodeGroup getLookupPrefixList() {
			return this.lookupPrefixList;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeElementLabel asElementLabel() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("L" + this.index, this.uri, this.name, this.prefix, this.lookupUriList, this.lookupPrefixList);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeElementLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementLabelPool extends EncodePool<EncodeElementLabel> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementLabel input) {
			return Objects.hash(input.uri, input.name, input.prefix, input.lookupUriList, input.lookupPrefixList);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementLabel input1, final EncodeElementLabel input2) {
			return (input1.uri == input2.uri) && (input1.name == input2.name) && (input1.prefix == input2.prefix) && (input1.lookupUriList == input2.lookupUriList)
				&& (input1.lookupPrefixList == input2.lookupPrefixList);
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElementLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttributeLabel#EncodeAttributeLabel(EncodeValue, EncodeValue, EncodeValue)
		 * @see EncodeAttributeLabelPool#unique(EncodeAttributeLabel)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @param lookupUriList nach {@code Prefix} sortierten {@code Xmlns}es.
		 * @param lookupPrefixList nach {@code Uri} sortierten {@code Xmlns}es.
		 * @return einzigartiges {@link EncodeElementLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementLabel unique(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix, final EncodeGroup lookupUriList,
			final EncodeGroup lookupPrefixList) throws NullPointerException {
			return this.unique(new EncodeElementLabel(uri, name, prefix, lookupUriList, lookupPrefixList));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeNode extends EncodeNode {

		/**
		 * Dieses Feld speichert das {@code Label}.
		 * 
		 * @see Attr#getPrefix()
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		protected final EncodeAttributeLabel label;

		/**
		 * Dieser Konstrukteur initialisiert {@code Label} und {@code Value}.
		 * 
		 * @param label {@code Label}.
		 * @param value {@code Value}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeNode(final EncodeAttributeLabel label, final EncodeValue value) throws NullPointerException {
			super(value);
			if(label == null) throw new NullPointerException();
			this.label = label;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeAttributeNode clone() throws CloneNotSupportedException {
			return new EncodeAttributeNode(this.label, this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_ATTRIBUTE_NODE;
		}

		/**
		 * Diese Methode gibt das {@code Label} zurück.
		 * 
		 * @see Attr#getPrefix()
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@code Label}.
		 */
		public EncodeXmlnsLabel getLabel() {
			return this.label;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Attr#getValue()
		 * @see Attr#getNodeValue()
		 */
		@Override
		public EncodeValue getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeAttributeNode asAttributeNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("A" + this.index, this.label, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeAttributeNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeNodePool extends EncodePool<EncodeAttributeNode> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeAttributeNode input) {
			return Objects.hash(input.label, input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeAttributeNode input1, final EncodeAttributeNode input2) {
			return (input1.label == input2.label) && (input1.value == input2.value);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeAttributeNode} mit den gegebenen Wert zurück.
		 * 
		 * @see EncodeAttributeNode#EncodeAttributeNode(EncodeAttributeLabel, EncodeValue)
		 * @see EncodeAttributeNodePool#unique(EncodeAttributeNode)
		 * @param label {@code Label}.
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeAttributeNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeNode unique(final EncodeAttributeLabel label, final EncodeValue value) throws NullPointerException {
			return this.unique(new EncodeAttributeNode(label, value));
		}

	}

	/**
	 * Diese Klasse implementiert das {@link EncodeXmlnsLabel} zur Abstraktion von {@code Uri}, {@code Name} und {@code Prefix} eines {@link Attr}s.
	 * 
	 * @see Attr#getLocalName()
	 * @see Attr#getNamespaceURI()
	 * @see Attr#getPrefix()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeLabel extends EncodeXmlnsLabel {

		/**
		 * Dieses Feld speichert den {@code Name}.
		 * 
		 * @see Attr#getLocalName()
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert {@code Uri}, {@code Name} und {@code Prefix}.
		 * 
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix) throws NullPointerException {
			super(uri, prefix);
			if(name == null) throw new NullPointerException();
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeAttributeLabel clone() throws CloneNotSupportedException {
			return new EncodeAttributeLabel(this.uri, this.name, this.prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_ATTRIBUTE_LABEL;
		}

		/**
		 * Diese Methode gibt den {@code Name} zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @return {@code Name}.
		 */
		public EncodeValue getName() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeAttributeLabel asAttributeLabel() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("L" + this.index, this.uri, this.name, this.prefix);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeAttributeLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeLabelPool extends EncodePool<EncodeAttributeLabel> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeAttributeLabel input) {
			return Objects.hash(input.uri, input.name, input.prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeAttributeLabel input1, final EncodeAttributeLabel input2) {
			return (input1.uri == input2.uri) && (input1.name == input2.name) && (input1.prefix == input2.prefix);
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttributeLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttributeLabel#EncodeAttributeLabel(EncodeValue, EncodeValue, EncodeValue)
		 * @see EncodeAttributeLabelPool#unique(EncodeAttributeLabel)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @return einzigartiges {@link EncodeAttributeLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel unique(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix) throws NullPointerException {
			return this.unique(new EncodeAttributeLabel(uri, name, prefix));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link EntityReference}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeReferenceNode extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}.
		 * 
		 * @see Entity#getNotationName()
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert den {@code Name}.
		 * 
		 * @param name {@code Name}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 */
		public EncodeReferenceNode(final EncodeValue name) throws NullPointerException {
			if(name == null) throw new NullPointerException();
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeReferenceNode clone() throws CloneNotSupportedException {
			return new EncodeReferenceNode(this.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_REFERENCE_NODE;
		}

		/**
		 * Diese Methode gibt den {@code Name}zurück. * @see Entity#getNotationName()
		 * 
		 * @return {@code Name}.
		 */
		public EncodeValue getName() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeReferenceNode asReferenceNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("R" + this.index, this.name);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeReferenceNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeReferenceNodePool extends EncodePool<EncodeReferenceNode> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeReferenceNode input) {
			return Objects.hash(input.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeReferenceNode input1, final EncodeReferenceNode input2) {
			return (input1.name == input2.name);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeReferenceNode} mit dem gegebenen {@code Name} zurück.
		 * 
		 * @see EncodeReferenceNode#EncodeReferenceNode(EncodeValue)
		 * @see EncodeReferenceNodePool#unique(EncodeReferenceNode)
		 * @param name {@code Name}.
		 * @return einzigartiger {@link EncodeReferenceNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 */
		public EncodeReferenceNode unique(final EncodeValue name) throws NullPointerException {
			return this.unique(new EncodeReferenceNode(name));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Comment}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeCommentNode extends EncodeNode {

		/**
		 * Dieser Konstrukteur initialisiert den {@code Value}.
		 * 
		 * @param value {@code Value}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeCommentNode(final EncodeValue value) throws NullPointerException {
			super(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeCommentNode clone() throws CloneNotSupportedException {
			return new EncodeCommentNode(this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_COMMENT_NODE;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Comment#getData()
		 * @see Comment#getNodeValue()
		 */
		@Override
		public EncodeValue getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeCommentNode asCommentNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("C" + this.index, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeCommentNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeCommentNodePool extends EncodeNodePool<EncodeCommentNode> {

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeCommentNode} mit dem gegebenen {@code Value} zurück.
		 * 
		 * @see EncodeCommentNode#EncodeCommentNode(EncodeValue)
		 * @see EncodeCommentNodePool#unique(EncodeCommentNode)
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeCommentNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeCommentNode unique(final EncodeValue value) throws NullPointerException {
			return this.unique(new EncodeCommentNode(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link ProcessingInstruction}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeInstructionNode extends EncodeNode {

		/**
		 * Dieses Feld speichert den {@code Name}.
		 * 
		 * @see ProcessingInstruction#getTarget()
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert {@code Name} und {@code Value}.
		 * 
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeInstructionNode(final EncodeValue name, final EncodeValue value) throws NullPointerException {
			super(value);
			if(name == null) throw new NullPointerException();
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeInstructionNode clone() throws CloneNotSupportedException {
			return new EncodeInstructionNode(this.name, this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getType() {
			return EncodeItem.TYPE_INSTRUCTION_NODE;
		}

		/**
		 * Diese Methode gibt den {@code Name}zurück.
		 * 
		 * @see ProcessingInstruction#getTarget()
		 * @see ProcessingInstruction#getNodeName()
		 * @return {@code Name}.
		 */
		public EncodeValue getName() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see ProcessingInstruction#getData()
		 * @see ProcessingInstruction#getNodeValue()
		 */
		@Override
		public EncodeValue getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EncodeInstructionNode asInstructionNode() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("P" + this.index, this.name, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeInstructionNode}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeInstructionNodePool extends EncodePool<EncodeInstructionNode> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeInstructionNode input) {
			return Objects.hash(input.name, input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeInstructionNode input1, final EncodeInstructionNode input2) {
			return (input1.name == input2.name) && (input1.value == input2.value);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeInstructionNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeInstructionNode#EncodeInstructionNode(EncodeValue, EncodeValue)
		 * @see EncodeInstructionNodePool#unique(EncodeInstructionNode)
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeInstructionNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeInstructionNode unique(final EncodeValue name, final EncodeValue value) throws NullPointerException {
			return this.unique(new EncodeInstructionNode(name, value));
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link EncodePool}s zur Abstraktion eines {@link Document} s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocument {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()}.
		 */
		protected EncodeValuePool xmlnsUriPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()}.
		 */
		protected EncodeValuePool xmlnsPrefixPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		protected EncodeXmlnsLabelPool xmlnsLabelPool = new EncodeXmlnsLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		protected EncodeGroupPool xmlnsLookupPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeTextNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected EncodeTextNodePool textNodePool = new EncodeTextNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeTextNode#getValue()}.
		 */
		protected EncodeValuePool textValuePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeElementNodePool} für {@link EncodeDocument#getDocumentElement()}, {@link EncodeDocument#getDocumentChildren()} und {@link EncodeElementNode#getChildren()}.
		 */
		protected EncodeElementNodePool elementNodePool = new EncodeElementNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeElementLabel#getName()}.
		 */
		protected EncodeValuePool elementNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeElementLabelPool} für {@link EncodeElementNode#getLabel()}.
		 */
		protected EncodeElementLabelPool elementLabelPool = new EncodeElementLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected EncodeGroupPool elementChildrenPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementNode#getAttributes()}.
		 */
		protected EncodeGroupPool elementAttributesPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeNodePool} für {@link EncodeElementNode#getAttributes()}.
		 */
		protected EncodeAttributeNodePool attributeNodePool = new EncodeAttributeNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()}.
		 */
		protected EncodeValuePool attributeNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()}.
		 */
		protected EncodeAttributeLabelPool attributeLabelPool = new EncodeAttributeLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttributeNode#getValue()}.
		 */
		protected EncodeValuePool attributeValuePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeCommentNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		protected EncodeCommentNodePool commentNodePool = new EncodeCommentNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeCommentNode#getValue()}.
		 */
		protected EncodeValuePool commentValuePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeReferenceNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected EncodeReferenceNodePool referenceNodePool = new EncodeReferenceNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()}.
		 */
		protected EncodeValuePool referenceNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		protected EncodeInstructionNodePool instructionNodePool = new EncodeInstructionNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()}.
		 */
		protected EncodeValuePool instructionNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeInstructionNode#getValue()}.
		 */
		protected EncodeValuePool instructionValuePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeNavigationEntry#getId()}.
		 */
		protected EncodeValuePool navigationIdPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeNavigationEntryPool} für {@link Document#getElementById(String)}.
		 */
		protected EncodeNavigationEntryPool navigationEntryPool = new EncodeNavigationEntryPool();

		/**
		 * Dieses Feld speichert den {@link EncodeIndicesPool} für {@link EncodeNavigationEntry#getIndices()}.
		 */
		protected EncodeIndicesPool navigationIndicesPool = new EncodeIndicesPool();

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} für {@link Document#getChildNodes()}.
		 */
		protected EncodeGroup documentChildren;

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()} zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()}.
		 */
		public EncodeValuePool getXmlnsUriPool() {
			return this.xmlnsUriPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()} zurück.
		 * 
		 * @see Node#getPrefix()
		 * @return {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()}.
		 */
		public EncodeValuePool getXmlnsPrefixPool() {
			return this.xmlnsPrefixPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()} zurück.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		public EncodeXmlnsLabelPool getXmlnsLabelPool() {
			return this.xmlnsLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		public EncodeGroupPool getXmlnsLookupPool() {
			return this.xmlnsLookupPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeTextNodePool} für {@link EncodeElementNode#getChildren()} zurück.
		 * 
		 * @return {@link EncodeTextNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		public EncodeTextNodePool getTextNodePool() {
			return this.textNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeTextNode#getValue()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeTextNode#getValue()}.
		 */
		public EncodeValuePool getTextValuePool() {
			return this.textValuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementNodePool} für {@link EncodeDocument#getDocumentElement()}, {@link EncodeDocument#getDocumentChildren()} und {@link EncodeElementNode#getChildren()} zurück.
		 * 
		 * @return {@link EncodeElementNodePool} für {@link EncodeDocument#getDocumentElement()}, {@link EncodeDocument#getDocumentChildren()} und {@link EncodeElementNode#getChildren()}.
		 */
		public EncodeElementNodePool getElementNodePool() {
			return this.elementNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeElementLabel#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeElementLabel#getName()}.
		 */
		public EncodeValuePool getElementNamePool() {
			return this.elementNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementLabelPool} für {@link EncodeElementNode#getLabel()} zurück.
		 * 
		 * @return {@link EncodeElementLabelPool} für {@link EncodeElementNode#getLabel()}.
		 */
		public EncodeElementLabelPool getElementLabelPool() {
			return this.elementLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElementNode#getChildren()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElementNode#getChildren()}.
		 */
		public EncodeGroupPool getElementChildrenPool() {
			return this.elementChildrenPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElementNode#getAttributes()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElementNode#getAttributes()}.
		 */
		public EncodeGroupPool getElementAttributesPool() {
			return this.elementAttributesPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeAttributeNodePool} für {@link EncodeElementNode#getAttributes()} zurück.
		 * 
		 * @return {@link EncodeAttributeNodePool} für {@link EncodeElementNode#getAttributes()}.
		 */
		public EncodeAttributeNodePool getAttributeNodePool() {
			return this.attributeNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()}.
		 */
		public EncodeValuePool getAttributeNamePool() {
			return this.attributeNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()} zurück.
		 * 
		 * @return {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()}.
		 */
		public EncodeAttributeLabelPool getAttributeLabelPool() {
			return this.attributeLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttributeNode#getValue()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeAttributeNode#getValue()}.
		 */
		public EncodeValuePool getAttributeValuePool() {
			return this.attributeValuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeCommentNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()} zurück.
		 * 
		 * @return {@link EncodeCommentNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		public EncodeCommentNodePool getCommentNodePool() {
			return this.commentNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeCommentNode#getValue()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeCommentNode#getValue()}.
		 */
		public EncodeValuePool getCommentValuePool() {
			return this.commentValuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeReferenceNodePool} für {@link EncodeElementNode#getChildren()} zurück.
		 * 
		 * @return {@link EncodeReferenceNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		public EncodeReferenceNodePool getReferenceNodePool() {
			return this.referenceNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()}.
		 */
		public EncodeValuePool getReferenceNamePool() {
			return this.referenceNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()} zurück.
		 * 
		 * @return {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		public EncodeInstructionNodePool getInstructionNodePool() {
			return this.instructionNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()}.
		 */
		public EncodeValuePool getInstructionNamePool() {
			return this.instructionNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeInstructionNode#getValue()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeInstructionNode#getValue()}.
		 */
		public EncodeValuePool getInstructionValuePool() {
			return this.instructionValuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeNavigationEntry#getId()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeNavigationEntry#getId()}.
		 */
		public EncodeValuePool getNavigationIdPool() {
			return this.navigationIdPool;
		}

		/**
		 ** Diese Methode gibt den {@link EncodeNavigationEntryPool} mit den Navigationspfaden für {@link Document#getElementById(String)} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		public EncodeNavigationEntryPool getNavigationEntryPool() {
			return this.navigationEntryPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeIndicesPool} für {@link EncodeNavigationEntry#getIndices()} zurück.
		 * 
		 * @return {@link EncodeIndicesPool} für {@link EncodeNavigationEntry#getIndices()}.
		 */
		public EncodeIndicesPool getNavigationIndicesPool() {
			return this.navigationIndicesPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementNode} für {@link Document#getDocumentElement()} zurück.
		 * 
		 * @see Document#getDocumentElement()
		 * @return {@link EncodeElementNode} für {@link Document#getDocumentElement()} oder {@code null}.
		 * @throws IllegalStateException Wenn kein oder mehrere {@link EncodeElementNode}s als {@link Document#getDocumentElement()} definiert wurden.
		 */
		public EncodeElementNode getDocumentElement() throws IllegalStateException {
			if(this.documentChildren == null) return null;
			EncodeElementNode documentElement = null;
			for(final EncodeItem item: this.documentChildren.items.values()){
				if(item.getType() == EncodeItem.TYPE_ELEMENT_NODE){
					if(documentElement != null) throw new IllegalStateException("multiple document elements");
					documentElement = item.asElementNode();
				}
			}
			if(documentElement == null) throw new IllegalStateException("no document element");
			return documentElement;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} für {@link Document#getChildNodes()} zurück.
		 * 
		 * @see Document#getChildNodes()
		 * @return {@link EncodeGroup} für {@link Document#getChildNodes()}.
		 */
		public EncodeGroup getDocumentChildren() {
			return this.documentChildren;
		}

		/**
		 * Diese Methode setzt die {@link EncodeGroup} für {@link Document#getChildNodes()}.
		 * 
		 * @see Document#getChildNodes()
		 * @param documentChildren {@link EncodeElementNode} für {@link Document#getChildNodes()}.
		 */
		public void setDocumentChildren(final EncodeGroup documentChildren) {
			this.documentChildren = documentChildren;
		}

		/**
		 * Diese Methode gibt das {@link EncodeXmlnsLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeXmlnsLabelPool#unique(EncodeValue, EncodeValue)
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return {@link EncodeXmlnsLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeXmlnsLabel createXmlnsLabel(final String uri, final String prefix) throws NullPointerException {
			if((uri == null) || (prefix == null)) throw new NullPointerException();
			return this.xmlnsLabelPool.unique(this.xmlnsUriPool.unique(uri), this.xmlnsPrefixPool.unique(prefix));
		}

		/**
		 * Diese Methode gibt den {@link EncodeTextNode} mit dem gegebenen {@code Value} zurück.
		 * 
		 * @see EncodeTextNodePool#unique(EncodeValue)
		 * @param value {@code Value}.
		 * @return {@link EncodeTextNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeTextNode createTextNode(final String value) throws NullPointerException {
			return this.textNodePool.unique(this.textValuePool.unique(value));
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeElementNodePool#unique(EncodeElementLabel, EncodeGroup, EncodeGroup)
		 * @param label {@code Label}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNode createElementNode(final EncodeElementLabel label, final List<? extends EncodeItem> children,
			final List<? extends EncodeItem> attributes) throws NullPointerException {
			if((label == null) || (children == null) || children.contains(null) || (attributes == null) || attributes.contains(null))
				throw new NullPointerException();
			return this.elementNodePool.unique(label, this.elementChildrenPool.unique(children), this.elementAttributesPool.unique(attributes));
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeDocument#createElementLabel(String, String, String, List, List)
		 * @see EncodeElementNodePool#unique(EncodeElementLabel, EncodeGroup, EncodeGroup)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @param lookupUriList nach {@code Prefix} sortierten {@code Xmlns}es.
		 * @param lookupPrefixList nach {@code Uri} sortierten {@code Xmlns}es.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNode createElementNode(final String uri, final String name, final String prefix, final List<? extends EncodeItem> lookupUriList,
			final List<? extends EncodeItem> lookupPrefixList, final List<? extends EncodeItem> children, final List<? extends EncodeItem> attributes)
			throws NullPointerException {
			if((children == null) || children.contains(null) || (attributes == null) || attributes.contains(null)) throw new NullPointerException();
			return this.elementNodePool.unique(this.createElementLabel(uri, name, prefix, lookupUriList, lookupPrefixList),
				this.elementChildrenPool.unique(children), this.elementAttributesPool.unique(attributes));
		}

		/**
		 * Diese Methode gibt das {@link EncodeElementLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeElementLabelPool#unique(EncodeValue, EncodeValue, EncodeValue, EncodeGroup, EncodeGroup)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @param lookupUriList nach {@code Prefix} sortierten {@code Xmlns}es.
		 * @param lookupPrefixList nach {@code Uri} sortierten {@code Xmlns}es.
		 * @return {@link EncodeElementLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementLabel createElementLabel(final String uri, final String name, final String prefix, final List<? extends EncodeItem> lookupUriList,
			final List<? extends EncodeItem> lookupPrefixList) throws NullPointerException {
			if((uri == null) || (name == null) || (prefix == null) || (lookupUriList == null) || lookupUriList.contains(null) || (lookupPrefixList == null)
				|| lookupPrefixList.contains(null)) throw new NullPointerException();
			return this.elementLabelPool.unique(this.xmlnsUriPool.unique(uri), this.elementNamePool.unique(name), this.xmlnsPrefixPool.unique(prefix),
				this.xmlnsLookupPool.unique(lookupUriList), this.xmlnsLookupPool.unique(lookupPrefixList));
		}

		/**
		 * Diese Methode gibt den {@link EncodeAttributeNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttributeNodePool#unique(EncodeAttributeLabel, EncodeValue)
		 * @see EncodeAttributeLabelPool#unique(EncodeValue, EncodeValue, EncodeValue)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @param value {@code Value}.
		 * @return {@link EncodeAttributeNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeNode createAttributeNode(final String uri, final String name, final String prefix, final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException();
			return this.attributeNodePool.unique(this.createAttributeLabel(uri, name, prefix), this.attributeValuePool.unique(value));
		}

		/**
		 * Diese Methode gibt das {@link EncodeAttributeLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttributeLabelPool#unique(EncodeValue, EncodeValue, EncodeValue)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @return {@link EncodeAttributeLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel createAttributeLabel(final String uri, final String name, final String prefix) throws NullPointerException {
			if((uri == null) || (name == null) || (prefix == null)) throw new NullPointerException();
			return this.attributeLabelPool.unique(this.xmlnsUriPool.unique(uri), this.attributeNamePool.unique(name), this.xmlnsPrefixPool.unique(prefix));
		}

		/**
		 * Diese Methode gibt den {@link EncodeCommentNode} mit dem gegebenen {@code Value} zurück.
		 * 
		 * @see EncodeCommentNodePool#unique(EncodeValue)
		 * @param value {@code Value}.
		 * @return {@link EncodeCommentNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeCommentNode createCommentNode(final String value) throws NullPointerException {
			return this.commentNodePool.unique(this.commentValuePool.unique(value));
		}

		/**
		 * Diese Methode gibt den {@link EncodeReferenceNode} mit dem gegebenen {@code Name} zurück.
		 * 
		 * @see EncodeReferenceNodePool#unique(EncodeValue)
		 * @param name {@code Name}.
		 * @return {@link EncodeReferenceNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 */
		public EncodeReferenceNode createReferenceNode(final String name) throws NullPointerException {
			return this.referenceNodePool.unique(this.referenceNamePool.unique(name));
		}

		/**
		 * Diese Methode gibt den {@link EncodeInstructionNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeInstructionNodePool#unique(EncodeValue, EncodeValue)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@link EncodeInstructionNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeInstructionNode createInstructionNode(final String name, final String value) throws NullPointerException {
			if((name == null) || (value == null)) throw new NullPointerException();
			return this.instructionNodePool.unique(this.instructionNamePool.unique(name), this.instructionValuePool.unique(value));
		}

		/**
		 * Diese Methode gibt das {@link EncodeNavigationEntry} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeNavigationEntryPool#unique(EncodeValue, EncodeIndices)
		 * @param id {@code Id}.
		 * @param indices {@code Indices}.
		 * @return {@link EncodeNavigationEntry}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn bereits ein {@link EncodeNavigationEntry} mit der gegebenen {@code Id} existiert.
		 */
		public EncodeNavigationEntry createNavigationEntry(final String id, final int... indices) throws NullPointerException, IllegalArgumentException {
			if(id == null) throw new NullPointerException();
			final EncodeNavigationEntry entry = new EncodeNavigationEntry(new EncodeValue(id), new EncodeIndices(indices));
			if(this.navigationEntryPool.contains(entry)) throw new IllegalArgumentException("id already exists");
			return this.navigationEntryPool.unique(this.navigationIdPool.unique(entry.id), this.navigationIndicesPool.unique(entry.indices));
		}

		/**
		 * Diese Methode gibt das {@link EncodeNavigationEntry} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeNavigationEntryPool#unique(EncodeValue, EncodeIndices)
		 * @param id {@code Id}.
		 * @param indices {@code Indices}.
		 * @return {@link EncodeNavigationEntry}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn bereits ein {@link EncodeNavigationEntry} mit der gegebenen {@code Id} existiert.
		 */
		public EncodeNavigationEntry createNavigationEntry(final String id, final List<Integer> indices) throws NullPointerException, IllegalArgumentException {
			if(id == null) throw new NullPointerException();
			final EncodeNavigationEntry entry = new EncodeNavigationEntry(new EncodeValue(id), new EncodeIndices(indices));
			if(this.navigationEntryPool.contains(entry)) throw new IllegalArgumentException("id already exists");
			return this.navigationEntryPool.unique(this.navigationIdPool.unique(entry.id), this.navigationIndicesPool.unique(entry.indices));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeDocument", //
				"xmlnsUriPool", this.xmlnsUriPool, //
				"xmlnsPrefixPool", this.xmlnsPrefixPool, //
				"xmlnsLabelPool", this.xmlnsLabelPool, //
				"xmlnsLookupPool", this.xmlnsLookupPool, //
				"textNodePool", this.textNodePool, //
				"textValuePool", this.textValuePool, //
				"elementNodePool", this.elementNodePool, //
				"elementNamePool", this.elementNamePool, //
				"elementLabelPool", this.elementLabelPool, //
				"elementChildrenPool", this.elementChildrenPool, //
				"elementAttributesPool", this.elementAttributesPool, //
				"attributeNodePool", this.attributeNodePool, //
				"attributeNamePool", this.attributeNamePool, //
				"attributeLabelPool", this.attributeLabelPool, //
				"attributeValuePool", this.attributeValuePool, //
				"commentNodePool", this.commentNodePool, //
				"commentValuePool", this.commentValuePool, //
				"referenceNodePool", this.referenceNodePool, //
				"referenceNamePool", this.referenceNamePool, //
				"instructionNodePool", this.instructionNodePool, //
				"instructionNamePool", this.instructionNamePool, //
				"instructionValuePool", this.instructionValuePool, //
				"navigationIdPool", this.navigationIdPool, //
				"navigationEntryPool", this.navigationEntryPool, //
				"navigationIndicesPool", this.navigationIndicesPool, //
				"documentChildren", this.documentChildren //
				);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zum Schrrittweisen Aufbau eines {@link EncodeDocument}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocumentBuilder extends EncodeBuilder<EncodeDocumentBuilder> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link EncodeDocumentBuilder} mit einem neuen {@link EncodeDocument}.
		 */
		public EncodeDocumentBuilder() {
			this(new EncodeDocument());
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link EncodeDocument}.
		 * 
		 * @param document {@link EncodeDocument}.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeDocument} {@code null} ist.
		 */
		public EncodeDocumentBuilder(final EncodeDocument document) throws NullPointerException {
			this.xmlns = new EncodeXmlnsBuilder(this.document = document);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected EncodeDocumentBuilder thiz() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void createId(final String id, final List<Integer> indices) throws IllegalArgumentException {
			this.document.createNavigationEntry(id, indices);
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link EncodeDocument}s ab und gibt dieses zurück. Nach dem Aufruf dieser Methode lässt dieser {@link EncodeDocumentBuilder} keine Modifikationen mehr zu.
		 * 
		 * @return {@link EncodeDocument}.
		 * @throws IllegalStateException Wenn kein oder mehrere {@link EncodeElementNode}s als {@link Document#getDocumentElement()} definiert wurden.
		 */
		public EncodeDocument commit() throws IllegalStateException {
			if(this.children == null) return this.document;
			this.document.documentChildren = this.document.elementChildrenPool.unique(this.children());
			this.clear();
			return this.document;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ContentHandler} zum Einlsenen eines {@link Document}s mit Hilfe eines {@link XMLReader}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocumentHandler implements ContentHandler {

		/**
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Inhalte eine {@link Element}s während des Einlesens eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class ElementStack {

			/**
			 * Dieses Feld speichert das {@link EncodeElementLabel}.
			 */
			protected final EncodeElementLabel label;

			/**
			 * Dieses Feld speichert die {@link EncodeElementNode#getChildren()}.
			 */
			protected final List<EncodeItem> children;

			/**
			 * Dieses Feld speichert die {@link EncodeElementNode#getAttributes()}.
			 */
			protected final List<? extends EncodeItem> attributes;

			/**
			 * Dieses Feld speichert den nächsten {@link ElementStack} oder {@code null}.
			 */
			protected final ElementStack next;

			/**
			 * Dieser Konstrukteur initialisiert den leeren {@link ElementStack}.
			 */
			public ElementStack() {
				this(null, null, null);
			}

			/**
			 * Dieser Konstrukteur initialisiert den {@link ElementStack}.
			 * 
			 * @param label {@link EncodeElementLabel}.
			 * @param attributes {@link EncodeAttributeNode}-{@link List}.
			 * @param next nächster {@link ElementStack} oder {@code null}.
			 */
			public ElementStack(final EncodeElementLabel label, final List<? extends EncodeItem> attributes, final ElementStack next) {
				this.label = label;
				this.children = new ArrayList<EncodeItem>(0);
				this.attributes = attributes;
				this.next = next;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, true, "ElementStack", "label", this.label, "attributes", this.attributes, "children", this.children, "next",
					this.next);
			}

		}

		/**
		 * Diese Klasse implementiert den dynamischen {@link EncodeNavigationPath}, der während des Einlesens vom {@link EncodeDocumentHandler} angepasst wird.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class NavigationStack extends EncodeNavigationPath {

			/**
			 * Dieses Feld speichert die unveränderliche {@link List} der {@code Uri}s.
			 */
			protected final List<String> pathUris;

			/**
			 * Dieses Feld speichert die {@link List} der {@code Uri}s.
			 */
			protected final List<String> pathUriStack;

			/**
			 * Dieses Feld speichert die unveränderliche {@link List} der {@code Name}s.
			 */
			protected final List<String> pathNames;

			/**
			 * Dieses Feld speichert die {@link List} der {@code Name}s.
			 */
			protected final List<String> pathNameStack;

			/**
			 * Dieses Feld speichert die unveränderliche {@link List} der {@code Child}-Indices.
			 */
			protected final List<Integer> pathIndices;

			/**
			 * Dieses Feld speichert die {@link List} der {@code Child}-Indices.
			 */
			protected final List<Integer> pathIndexStack;

			/**
			 * Dieser Konstrukteur initialisiert die {@link List}s.
			 */
			public NavigationStack() {
				this.pathUriStack = new ArrayList<String>();
				this.pathUris = Collections.unmodifiableList(this.pathUriStack);
				this.pathNameStack = new ArrayList<String>();
				this.pathNames = Collections.unmodifiableList(this.pathNameStack);
				this.pathIndexStack = new ArrayList<Integer>();
				this.pathIndices = Collections.unmodifiableList(this.pathIndexStack);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected EncodeNavigationPath path() {
				return null;
			}

			/**
			 * Diese Methode fügt das gegebene Segment aus {@code Uri}, {@code Name} und {@code Index} an das Ende des Navigationspfads an.
			 * 
			 * @param pathUri {@code Uri}.
			 * @param pathName {@code Name}.
			 * @param pathIndex {@code Index}.
			 */
			protected void append(final String pathUri, final String pathName, final int pathIndex) {
				final int index = this.pathLength();
				this.pathUriStack.add(index, pathUri);
				this.pathNameStack.add(index, pathName);
				this.pathIndexStack.add(index, Integer.valueOf(pathIndex));
			}

			/**
			 * Diese Methode entfernt das letzte Segment des Navigationspfads.
			 */
			protected void remove() {
				final int index = this.pathLength() - 1;
				this.pathUriStack.remove(index);
				this.pathNameStack.remove(index);
				this.pathIndexStack.remove(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String pathUri(final int index) throws IndexOutOfBoundsException {
				return this.pathUriStack.get(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public List<String> pathUris() {
				return this.pathUris;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String pathName(final int index) throws IndexOutOfBoundsException {
				return this.pathNameStack.get(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public List<String> pathNames() {
				return this.pathNames;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int pathIndex(final int index) throws IndexOutOfBoundsException {
				return this.pathIndexStack.get(index).intValue();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public List<Integer> pathIndices() {
				return this.pathIndices;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int pathLength() {
				return this.pathUriStack.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, true, "NavigationStack", "pathUris", this.pathUris(), "pathNames", this.pathNames(), "pathIndices",
					this.pathIndices());
			}

		}

		/**
		 * Dieses Feld speichert das {@link EncodeDocument}.
		 */
		protected final EncodeDocument document;

		/**
		 * Dieses Feld speichert den {@link StringBuilder} für den aktuellen {@link EncodeTextNode}.
		 */
		protected final StringBuilder valueStack;

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsBuilder}.
		 */
		protected EncodeXmlnsBuilder xmlnsStack;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
		 */
		protected final boolean xmlnsEnabled;

		/**
		 * Dieses Feld speichert den {@link ElementStack} für den aktuellen {@link EncodeElementNode}.
		 */
		protected ElementStack elementStack;

		/**
		 * Dieses Feld speichert den {@link NavigationStack} für den aktuellen Navigationspfad und den {@link EncodeNavigationPathFilter}.
		 */
		protected final NavigationStack navigationStack;

		/**
		 * Dieses Feld speichert den {@link EncodeNavigationPathFilter} oder {@code null}.
		 */
		protected final EncodeNavigationPathFilter navigationPathFilter;

		/**
		 * Dieser Konstrukteur initialisiert das {@link EncodeDocument}, {@code xmlns}-Aktivierung und {@link EncodeNavigationPathFilter}.
		 * 
		 * @see #isXmlnsEnabled()
		 * @see #isNavigationPathEnabled()
		 * @param document {@link EncodeDocument}.
		 * @param xmlnsEnabled {@code xmlns}-Aktivierung.
		 * @param navigationPathFilter {@link EncodeNavigationPathFilter} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeDocument} {@code null} ist.
		 */
		public EncodeDocumentHandler(final EncodeDocument document, final boolean xmlnsEnabled, final EncodeNavigationPathFilter navigationPathFilter)
			throws NullPointerException {
			this.document = document;
			this.valueStack = new StringBuilder();
			this.navigationStack = new NavigationStack();
			this.navigationPathFilter = navigationPathFilter;
			this.xmlnsEnabled = xmlnsEnabled;
			if(!xmlnsEnabled) return;
			this.xmlnsStack = new EncodeXmlnsBuilder(document);
		}

		/**
		 * Diese Methode fügt einen {@link EncodeTextNode} mit dem via {@link #characters(char[], int, int)} gesammelten {@link #valueStack Textwert} an die {@link ElementStack#children Children} des {@link #elementStack aktuellen Elements}, sofern dieser Textwert nicht leer ist. Nach dem Aufruf dieser Methode ist der gesammelte {@link #valueStack Textwert} in jedem Fall leer.
		 */
		protected void commitValue() {
			if(this.valueStack.length() == 0) return;
			final String value = this.valueStack.toString();
			this.valueStack.setLength(0);
			this.elementStack.children.add(this.document.createTextNode(value));
		}

		/**
		 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück.
		 * 
		 * @see Encoder2#isXmlnsEnabled()
		 * @return {@code xmlns}-Aktivierung.
		 */
		public boolean isXmlnsEnabled() {
			return this.xmlnsEnabled;
		}

		/**
		 * Diese Methode gibt den aktuellen Navifationspfad zurück.
		 * 
		 * @return {@link EncodeNavigationPath}.
		 */
		public EncodeNavigationPath getNavigationPath() {
			return new EncodeNavigationPath() {

				@Override
				protected EncodeNavigationPath path() {
					return EncodeDocumentHandler.this.navigationStack;
				}

			};
		}

		/**
		 * Diese Methode gibt den {@link EncodeNavigationPathFilter} oder {@code null} zurück. Wenn er {@code null} ist, wird der {@link EncodeDocument#getNavigationEntryPool()} nicht befüllt.
		 * 
		 * @see Encoder2#getNavigationPathFilter()
		 * @return {@link EncodeNavigationPathFilter} oder {@code null}.
		 */
		public EncodeNavigationPathFilter getNavigationPathFilter() {
			return this.navigationPathFilter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endPrefixMapping(final String prefix) {
			if(!this.xmlnsEnabled) return;
			this.xmlnsStack = this.xmlnsStack.remove(prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startPrefixMapping(final String prefix, final String uri) {
			if(!this.xmlnsEnabled) return;
			this.xmlnsStack = this.xmlnsStack.append(uri, prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endElement(final String uri, final String name, final String qName) {
			this.commitValue();
			final ElementStack element = this.elementStack;
			final List<EncodeItem> children = element.children;
			final NavigationStack navigationStack = this.navigationStack;
			final EncodeNavigationPathFilter filter = this.navigationPathFilter;
			if(filter != null){
				for(int i = 0, size = children.size(); i < size; i++){
					final EncodeElementNode child = children.get(i).asElementNode();
					if((child != null) && filter.isId(new EncodeElementPath(navigationStack, i, child))){
						final List<EncodeItem> items = child.children.items.values();
						if(items.size() != 1) throw new IllegalArgumentException("invalid element content for id");
						final EncodeTextNode item = items.get(0).asTextNode();
						if(item == null) throw new IllegalArgumentException("invalid element content for id");
						this.document.createNavigationEntry(item.value.string, navigationStack.pathIndexStack);
					}
				}
			}
			element.next.children.add(this.document.createElementNode(element.label, children, element.attributes));
			navigationStack.remove();
			this.elementStack = element.next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String elementUri, final String elementName, final String qName, final Attributes attributeList) {
			this.commitValue();
			final int attributeCount = attributeList.getLength();
			final List<EncodeAttributeNode> attributes = new ArrayList<EncodeAttributeNode>(attributeCount);
			EncodeElementLabel elementLabel;
			if(this.xmlnsEnabled){
				elementLabel =
					this.document.createElementLabel(elementUri, elementName, this.xmlnsStack.lookupPrefix(elementUri), this.xmlnsStack.lookupUriList(),
						this.xmlnsStack.lookupPrefixList());
				this.navigationStack.append(elementUri, elementName, this.elementStack.children.size());
				for(int i = 0; i < attributeCount; i++){
					final String attributeUri = attributeList.getURI(i);
					attributes.add(this.document.createAttributeNode(attributeUri, attributeList.getLocalName(i), this.xmlnsStack.lookupPrefix(attributeUri),
						attributeList.getValue(i)));
				}
			}else{
				elementLabel =
					this.document.createElementLabel(XMLConstants.NULL_NS_URI, elementName, XMLConstants.DEFAULT_NS_PREFIX, Collections.<EncodeItem>emptyList(),
						Collections.<EncodeItem>emptyList());
				this.navigationStack.append(XMLConstants.NULL_NS_URI, elementName, this.elementStack.children.size());
				for(int i = 0; i < attributeCount; i++){
					attributes.add(this.document.createAttributeNode(XMLConstants.NULL_NS_URI, attributeList.getLocalName(i), XMLConstants.DEFAULT_NS_PREFIX,
						attributeList.getValue(i)));
				}
			}
			final EncodeNavigationPathFilter filter = this.navigationPathFilter;
			if(filter != null){
				for(int i = 0; i < attributeCount; i++){
					final EncodeAttributeNode attributeNode = attributes.get(i);
					if(filter.isId(new EncodeAttributePath(this.navigationStack, attributeList.getType(i), attributeNode))){
						this.document.createNavigationEntry(attributeNode.value.string, this.navigationStack.pathIndexStack);
					}
				}
			}
			if(attributeCount > 1){
				Collections.sort(attributes, Encoder2.EncodeAttributeNode_Label_Comparator);
			}
			this.elementStack = new ElementStack(elementLabel, attributes, this.elementStack);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endDocument() {
			this.document.documentChildren = this.document.elementChildrenPool.unique(this.elementStack.children);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startDocument() {
			if(this.elementStack != null) throw new IllegalStateException("document already built");
			this.elementStack = new ElementStack();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void characters(final char[] ch, final int start, final int length) {
			this.valueStack.append(ch, start, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void ignorableWhitespace(final char[] ch, final int start, final int length) {
			this.characters(ch, start, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void skippedEntity(final String name) {
			this.commitValue();
			this.elementStack.children.add(this.document.createReferenceNode(name));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void processingInstruction(final String target, final String data) {
			this.commitValue();
			this.elementStack.children.add(this.document.createInstructionNode(target, data));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setDocumentLocator(final Locator locator) {
		}

	}

	/**
	 * Dieses Feld speichert den {@link EncodeNavigationPathFilter}, der die {@code ID}-{@link Attr}s an ihrem {@code Type} erkennt.
	 * 
	 * @see EncodeAttributePath#attributeType()
	 */
	protected static final EncodeNavigationPathFilter Default_EncodeNavigationPathFilter = new EncodeNavigationPathFilter() {

		@Override
		public boolean isId(final EncodeAttributePath attributePath) {
			return "ID".equals(attributePath.attributeType());
		}

		@Override
		public boolean isId(final EncodeElementPath elementPath) {
			return false;
		}

	};

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von {@link EncodeValue}s.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#compileHash(List, boolean, Hasher)
	 */
	public static final Hasher<EncodeValue> EncodeValue_Hasher = new Hasher<EncodeValue>() {

		@Override
		public int hash(final EncodeValue value) {
			return Coder.hashString(value.string);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur absteigenden Sortierung von {@link EncodeItem}s nach ihrem {@link EncodeItem#getIndex()}.
	 */
	public static final Comparator<EncodeItem> EncodeItem_Index_Comparator = new Comparator<EncodeItem>() {

		@Override
		public int compare(final EncodeItem o1, final EncodeItem o2) {
			return o2.index - o1.index;
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeValue}s nach ihrem {@link EncodeValue#getString()}.
	 */
	public static final Comparator<EncodeValue> EncodeValue_String_Comparator = new Comparator<EncodeValue>() {

		@Override
		public int compare(final EncodeValue o1, final EncodeValue o2) {
			return Comparators.compare(o1.string, o2.string);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeXmlnsLabel}s primär nach ihrer {@code Uri} und sekundär nach ihrem {@code Prefix} via {@link Encoder2#EncodeValue_String_Comparator}.
	 * 
	 * @see EncodeXmlnsLabel#getUri()
	 * @see EncodeXmlnsLabel#getPrefix()
	 * @see Encoder2#EncodeValue_String_Comparator
	 */
	public static final Comparator<EncodeXmlnsLabel> EncodeXmlnsLabel_UriPrefix_Comparator = new Comparator<EncodeXmlnsLabel>() {

		@Override
		public int compare(final EncodeXmlnsLabel o1, final EncodeXmlnsLabel o2) {
			final int comp = Encoder2.EncodeValue_String_Comparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder2.EncodeValue_String_Comparator.compare(o1.prefix, o2.prefix);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeXmlnsLabel}s primär nach ihrem {@link EncodeXmlnsLabel#getPrefix()} und sekundär nach ihrer {@link EncodeXmlnsLabel#getUri()} via {@link Encoder2#EncodeValue_String_Comparator}.
	 */
	public static final Comparator<EncodeXmlnsLabel> EncodeXmlnsLabel_PrefixUri_Comparator = new Comparator<EncodeXmlnsLabel>() {

		@Override
		public int compare(final EncodeXmlnsLabel o1, final EncodeXmlnsLabel o2) {
			final int comp = Encoder2.EncodeValue_String_Comparator.compare(o1.prefix, o2.prefix);
			if(comp != 0) return comp;
			return Encoder2.EncodeValue_String_Comparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttributeNode}s nach ihrem {@link EncodeAttributeNode#getLabel()} via {@link Encoder2#EncodeAttributeLabel_NameUri_Comparator}.
	 */
	public static final Comparator<EncodeAttributeNode> EncodeAttributeNode_Label_Comparator = new Comparator<EncodeAttributeNode>() {

		@Override
		public int compare(final EncodeAttributeNode value1, final EncodeAttributeNode value2) {
			return Encoder2.EncodeAttributeLabel_NameUri_Comparator.compare(value1.label, value2.label);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttributeLabel}s primär nach ihrem {@link EncodeAttributeLabel#getName()} und sekundär nach ihrer {@link EncodeAttributeLabel#getUri()} via {@link Encoder2#EncodeValue_String_Comparator}.
	 */
	protected static final Comparator<EncodeAttributeLabel> EncodeAttributeLabel_NameUri_Comparator = new Comparator<EncodeAttributeLabel>() {

		@Override
		public int compare(final EncodeAttributeLabel o1, final EncodeAttributeLabel o2) {
			final int comp = Encoder2.EncodeValue_String_Comparator.compare(o1.name, o2.name);
			if(comp != 0) return comp;
			return Encoder2.EncodeValue_String_Comparator.compare(o1.uri, o2.uri);
		}

	};

	public static final Hasher<EncodeNavigationEntry> EncodeNavigationEntry_Id_Hasher = new Hasher<EncodeNavigationEntry>() {

		@Override
		public int hash(final EncodeNavigationEntry value) {
			return Coder.hashString(value.id.string);
		}

	};

	public static final Comparator<EncodeNavigationEntry> EncodeNavigationEntry_Id_Comparator = new Comparator<EncodeNavigationEntry>() {

		@Override
		public int compare(final EncodeNavigationEntry o1, final EncodeNavigationEntry o2) {
			return Encoder2.EncodeValue_String_Comparator.compare(o1.id, o2.id);
		}

	};

	/**
	 * Diese Methode schreibt den Datensatz in das gegebenen {@link EncodeTarget}.
	 * 
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn das gegebene {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} {@code null} ist.
	 */
	static void write(final EncodeItem source, final EncodeTarget target) throws IOException, NullPointerException {
		switch(source.getType()){
			case EncodeItem.TYPE_INDICES:{
				final EncodeIndices item = source.asIndices();
				Encoder2.writeInts(target, item.indices.toArray());
				break;
			}
			case EncodeItem.TYPE_VALUE:{
				final EncodeValue item = source.asValue();
				final byte[] bytes = Coder.encodeChars(item.string);
				target.write(bytes, 0, bytes.length);
				break;
			}
			case EncodeItem.TYPE_GROUP:{
				final EncodeGroup item = source.asGroup();
				Encoder2.writeIndices(target, item.items.values());
				break;
			}
			case EncodeItem.TYPE_XMLNS_LABEL:{
				final EncodeXmlnsLabel item = source.asXmlnsLabel();
				Encoder2.writeInts(target, item.uri.index, item.prefix.index);
				break;
			}
			case EncodeItem.TYPE_ELEMENT_NODE:{
				final EncodeElementNode item = source.asElementNode();
				Encoder2.writeInts(target, item.label.index, item.children.index, item.attributes.index);
				break;
			}
			case EncodeItem.TYPE_ELEMENT_LABEL:{
				final EncodeElementLabel item = source.asElementLabel();
				Encoder2.writeInts(target, item.uri.index, item.name.index, item.prefix.index, item.lookupUriList.index, item.lookupPrefixList.index);
				break;
			}
			case EncodeItem.TYPE_ATTRIBUTE_LABEL:{
				final EncodeAttributeLabel item = source.asAttributeLabel();
				Encoder2.writeInts(target, item.uri.index, item.name.index, item.prefix.index);
				break;
			}
			case EncodeItem.TYPE_ATTRIBUTE_NODE:{
				final EncodeAttributeNode item = source.asAttributeNode();
				Encoder2.writeInts(target, item.label.index, item.value.index);
				break;
			}
			case EncodeItem.TYPE_INSTRUCTION_NODE:{
				final EncodeInstructionNode item = source.asInstructionNode();
				Encoder2.writeInts(target, item.name.index, item.value.index);
				break;
			}
		}
	}

	/**
	 * Diese Methode schreibt das gegebenen {@code int}-Array in das gegebene {@link EncodeTarget}.
	 * 
	 * @see Coder#encodeIndices(int...)
	 * @see EncodeTarget#write(byte[], int, int)
	 * @param target {@link EncodeTarget}.
	 * @param values {@code int}-Array.
	 * @throws IOException Wenn das gegebene {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. das gegebene {@code int}-Array {@code null} ist.
	 */
	protected static void writeInts(final EncodeTarget target, final int... values) throws IOException, NullPointerException {
		if((target == null) || (values == null)) throw new NullPointerException();
		final byte[] array = Coder.encodeIndices(values);
		target.write(array, 0, array.length);
	}

	/**
	 * Diese Methode schreibt die Anzahl der gegebenen {@link EncodeItem}s sowie jedes der {@link EncodeItem}s in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>N|item1|...|itemN</pre>
	 * 
	 * @see Encoder2#writeInts(EncodeTarget, int...)
	 * @see EncodeItem#write(EncodeTarget)
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeItem}- {@link List} {@code null} ist.
	 */
	protected static void writeItems(final EncodeTarget target, final List<? extends EncodeItem> values) throws IOException, NullPointerException {
		if((target == null) || (values == null)) throw new NullPointerException();
		Encoder2.writeInts(target, values.size());
		for(final EncodeItem item: values){
			Encoder2.write(item, target);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeItem}s ihre aufsummierten Längen, die Summe aller Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>size|offset[0]|...|offset[size]|values[0]|...|values[size-1]
	 * offset[0] = 0
	 * offset[i+1] = offset[i] + values[i].length</pre>
	 * 
	 * @see EncodeItem#getLength()
	 * @see Encoder2#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeItem}- {@link List} {@code null} ist.
	 */
	protected static void writeLists(final EncodeTarget target, final List<? extends EncodeItem> values) throws IOException, NullPointerException {
		if((target == null) || (values == null)) throw new NullPointerException();
		Encoder2.writeInts(target, values.size());
		final int size = values.size();
		int offset = 0;
		final int[] value = new int[size + 1];
		for(int i = 0; i < size; i++){
			value[i] = offset;
			final EncodeItem item = values.get(i);
			switch(item.getType()){
				case EncodeItem.TYPE_VALUE:
					offset += Coder.encodeChars(item.asValue().string).length;
					break;
				case EncodeItem.TYPE_GROUP:
					offset += item.asGroup().items.size();
					break;
				case EncodeItem.TYPE_INDICES:
					offset += item.asIndices().indices.size();
					break;
			}
		}
		value[size] = offset;
		Encoder2.writeInts(target, value);
		for(final EncodeItem item: values){
			Encoder2.write(item, target);
		}
	}

	/**
	 * Diese Methode schreibt die Indices der gegebenen {@link EncodeItem}s in das gegebene {@link EncodeTarget}.
	 * 
	 * @see Encoder2#writeInts(EncodeTarget, int...)
	 * @see EncodeItem#index
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeItem}- {@link List} {@code null} ist.
	 */
	protected static void writeIndices(final EncodeTarget target, final List<? extends EncodeItem> values) throws IOException, NullPointerException {
		if((target == null) || (values == null)) throw new NullPointerException();
		final int size = values.size();
		if(size == 0) return;
		final int[] value = new int[size];
		for(int i = 0; i < size; i++){
			value[i] = values.get(i).index;
		}
		Encoder2.writeInts(target, value);
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link EncodePool}s in eine neue {@link List} ein, sortiert diese {@link List} mit dem gegebenen {@link Comparator}, setzt den Index der Elemente auf den in der {@link List} und gibt die {@link List} zurück. Der Index des {@code i}-ten Elements ist {@code i}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @see EncodeItem#getIndex()
	 * @see Collections#sort(List, Comparator)
	 * @param items {@link EncodePool} mit den Elementen.
	 * @param comparator {@link Comparator} zum Sortieren.
	 * @return {@link EncodeItem}-{@link List}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends EncodeItem> List<GItem> compilePool(final EncodePool<? extends GItem> items, final Comparator<? super GItem> comparator)
		throws NullPointerException {
		if((items == null) || (comparator == null)) throw new NullPointerException();
		final List<GItem> list = new ArrayList<GItem>(items.size());
		Iterators.appendAll(list, items.iterator());
		final int size = list.size();
		if(size > 1){
			Collections.sort(list, comparator);
		}
		for(int i = 0; i < size; i++){
			list.get(i).index = i;
		}
		return list;
	}

	/**
	 * Diese Methode erzeugt eine {@link List} aus {@link EncodeGroup}s als {@code Hash-Table} für die gegebenen Elemente und gibt diese {@link List} zurück. Der {@link Object#hashCode() Streuwert} der Elemente wird mit Hilfe des gegebenen {@link Comparable}s berechnet.
	 * <p>
	 * Die Größe {@code size} der {@code Hash-Table} ist eine Potenz von {@code 2}. Die Position, an der ein Element {@code item} in die {@code Hash-Table} einsortiert wird, ergibt sich aus {@code hasher.compareTo(item) & (size - 1)}.
	 * <p>
	 * Wenn die {@code Hash-Table}-Erzeugung {@code true} ist, wird die {@code Hash-Table} erzeugt. Anderenfalls wird {@link Collections#EMPTY_LIST} zurück gegeben.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} mit den Elementen.
	 * @param enabled {@code Hash-Table}-Erzeugung.
	 * @param hasher {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts}.
	 * @return {@link EncodeGroup}-{@link List} als {@code Hash-Table} oder {@link Collections#EMPTY_LIST}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GItem extends EncodeItem> List<EncodeGroup> compileHash(final List<? extends GItem> items, final boolean enabled,
		final Hasher<? super GItem> hasher) throws NullPointerException {
		if((items == null) || (hasher == null)) throw new NullPointerException();
		if(!enabled) return Collections.emptyList();
		final int size = items.size();
		if(size == 0) return Collections.emptyList();
		int count = 1;
		while(count < size){
			count <<= 1;
		}
		final List<EncodeGroup> list = new ArrayList<EncodeGroup>(count);
		for(int i = 0; i < count; i++){
			list.add(i, new EncodeGroup());
		}
		count--;
		for(int i = 0; i < size; i++){
			final GItem value = items.get(i);
			final int index = hasher.hash(value) & count;
			list.get(index).items.add(value);
		}
		for(final EncodeGroup item: list){
			item.items.compact();
		}
		return list;
	}

	/**
	 * Dieses Feld speichert die {@code Uri-Hash}-Aktivierung.
	 */
	protected boolean xmlnsUriHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Value-Hash}-Aktivierung.
	 */
	protected boolean navigationEntryHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
	 */
	protected boolean xmlnsEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	protected boolean xmlnsPrefixHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	protected boolean xmlnsLabelHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Element-Name-Hash}-Aktivierung.
	 */
	protected boolean elementNameHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Element-Label-Hash}-Aktivierung.
	 */
	protected boolean elementLabelHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Attribute-Name-Hash}-Aktivierung.
	 */
	protected boolean attributeNameHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Attribute-Label-Hash}-Aktivierung.
	 */
	protected boolean attributeLabelHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Navigation-Path}-Aktivierung.
	 */
	protected boolean navigationPathEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Navigation-Path-Hash}-Aktivierung.
	 */
	protected boolean navigationPathHashEnabled = true;

	/**
	 * Dieses Feld speichert den {@link EncodeNavigationPathFilter}.
	 */
	protected EncodeNavigationPathFilter navigationPathFilter = Encoder2.Default_EncodeNavigationPathFilter;

	private boolean referenceNameHashEnabled;

	private boolean instructionNameHashEnabled;

	private boolean navigationIdHashEnabled;

	private boolean valueHashEnabled;

	/**
	 * Dieser Konstrukteur initialisiert den {@link Encoder2} und aktiviert alle Optionen.
	 */
	public Encoder2() {
	}

	/**
	 * Diese Methode gibt die {@code Value-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getValuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#valueHash()
	 * @return {@code Value-Hash}-Aktivierung.
	 */
	public boolean isValueHashEnabled() {
		return this.navigationEntryHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Value-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getValuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#valueHash()
	 * @param value {@code Value-Hash}-Aktivierung.
	 */
	public void setValueHashEnabled(final boolean value) {
		this.navigationEntryHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen {@link EncodeElementNode}s und {@link EncodeAttributeNode}s neben einem {@code Name} auch eine {@code Uri} und einen {@code Prefix}.
	 * 
	 * @see Node#getPrefix()
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @return {@code xmlns}-Aktivierung.
	 */
	public boolean isXmlnsEnabled() {
		return this.xmlnsEnabled;
	}

	/**
	 * Diese Methode setzt die {@code xmlns}-Aktivierung. Wenn diese Option {@code true} ist, besitzen {@link EncodeElementNode}s und {@link EncodeAttributeNode}s neben einem {@code Name} auch eine {@code Uri} und einen {@code Prefix}.
	 * 
	 * @see Node#getPrefix()
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @param value {@code xmlns}-Aktivierung.
	 */
	public void setXmlnsEnabled(final boolean value) {
		this.xmlnsEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Uri-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsUriPool()} eine {@code Uri-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#uriHash()
	 * @return {@code Uri-Hash}-Aktivierung.
	 */
	public boolean isUriHashEnabled() {
		return this.xmlnsUriHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Uri-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsUriPool()} eine {@code Uri-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#uriHash()
	 * @param value {@code Uri-Hash}-Aktivierung.
	 */
	public void setUriHashEnabled(final boolean value) {
		this.xmlnsUriHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Xmlns-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsPrefixPool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#xmlnsNameHash()
	 * @return {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	public boolean isXmlnsNameHashEnabled() {
		return this.xmlnsPrefixHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Xmlns-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsPrefixPool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#xmlnsNameHash()
	 * @param value {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	public void setXmlnsNameHashEnabled(final boolean value) {
		this.xmlnsPrefixHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Xmlns-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#xmlnsLabelHash()
	 * @return {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	public boolean isXmlnsLabelHashEnabled() {
		return this.xmlnsLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Xmlns-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getXmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#xmlnsLabelHash()
	 * @param value {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	public void setXmlnsLabelHashEnabled(final boolean value) {
		this.xmlnsLabelHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Element-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getElementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#elementNameHash()
	 * @return {@code Element-Name-Hash}-Aktivierung.
	 */
	public boolean isElementNameHashEnabled() {
		return this.elementNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Element-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getElementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#elementNameHash()
	 * @param value {@code Element-Name-Hash}-Aktivierung.
	 */
	public void setElementNameHashEnabled(final boolean value) {
		this.elementNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Element-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getElementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#elementLabelHash()
	 * @return {@code Element-Label-Hash}-Aktivierung.
	 */
	public boolean isElementLabelHashEnabled() {
		return this.elementLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Element-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getElementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#elementLabelHash()
	 * @param value {@code Element-Label-Hash}-Aktivierung.
	 */
	public void setElementLabelHashEnabled(final boolean value) {
		this.elementLabelHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Attribute-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getAttributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @return {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public boolean isAttributeNameHashEnabled() {
		return this.attributeNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Attribute-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getAttributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @param value {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public void setAttributeNameHashEnabled(final boolean value) {
		this.attributeNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Attribute-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getAttributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#attributeLabelHash()
	 * @return {@code Attribute-Label-Hash}-Aktivierung.
	 */
	public boolean isAttributeLabelHashEnabled() {
		return this.attributeLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Attribute-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getAttributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#isXmlnsEnabled()
	 * @see DecodeDocument#attributeLabelHash()
	 * @param value {@code Attribute-Label-Hash}-Aktivierung.
	 */
	public void setAttributeLabelHashEnabled(final boolean value) {
		this.attributeLabelHashEnabled = value;
	}

	/**
	 * Diese Methode gibt den {@link EncodeNavigationPathFilter} zurück.
	 * 
	 * @return {@link EncodeNavigationPathFilter}.
	 */
	public EncodeNavigationPathFilter getNavigationPathFilter() {
		return this.navigationPathFilter;
	}

	/**
	 * Diese Methode setzt den {@link EncodeNavigationPathFilter}. Wenn der gegebene {@link EncodeNavigationPathFilter} {@code null} ist, wird der Standardfilter verwendet, der die {@code ID}-{@link Attr}s an ihrem {@code Type} erkennt.
	 * 
	 * @see EncodeAttributePath#attributeType()
	 * @param value {@link EncodeNavigationPathFilter} oder {@code null}.
	 */
	public void setNavigationPathFilter(final EncodeNavigationPathFilter value) {
		this.navigationPathFilter = ((value == null) ? Encoder2.Default_EncodeNavigationPathFilter : value);
	}

	/**
	 * Diese Methode gibt die {@code Navigation-Path}-Aktivierung zurück. Wenn diese Option {@code true} ist, werden Navigationsdaten für {@link EncodeDocument#getNavigationEntryPool()} erzeugt.
	 * 
	 * @see Attr#isId()
	 * @see Document#getElementById(String)
	 * @see EncodeDocument#getNavigationEntryPool()
	 * @return {@code Navigation-Path}-Aktivierung.
	 */
	public boolean isNavigationPathEnabled() {
		return this.navigationPathEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Navigation-Path}-Aktivierung. Wenn diese Option {@code true} ist, werden Navigationsdaten für {@link EncodeDocument#getNavigationEntryPool()} erzeugt.
	 * 
	 * @see Attr#isId()
	 * @see Document#getElementById(String)
	 * @see EncodeDocument#getNavigationEntryPool()
	 * @param value {@code Navigation-Path}-Aktivierung.
	 */
	public void setNavigationPathEnabled(final boolean value) {
		this.navigationPathEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Navigation-Path-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getNavigationEntryPool()} eine {@code Navigation-Path-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#navigationPathHash()
	 * @return {@code Navigation-Path-Hash}-Aktivierung.
	 */
	public boolean isNavigationPathHashEnabled() {
		return this.navigationPathHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Navigation-Path-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getNavigationEntryPool()} eine {@code Navigation-Path-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#navigationPathHash()
	 * @param value {@code Navigation-Path-Hash}-Aktivierung.
	 */
	public void setNavigationPathHashEnabled(final boolean value) {
		this.navigationPathHashEnabled = value;
	}

	/**
	 * Diese Methode kodiert das im {@code Source}-{@link File} gegebene XML-Dokument in eine optimierte binäre Darstellung und speichert diese im {@code Target}-{@link File}.
	 * 
	 * @see #encode(XMLReader, InputSource, EncodeTarget)
	 * @param source {@code Source}-{@link File}.
	 * @param target {@code Target}-{@link File}.
	 * @throws IOException Wenn die verwendete {@link InputSource} bzw. das verwendete {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final File source, final File target) throws IOException, SAXException, NullPointerException {
		if((source == null) || (target == null)) throw new NullPointerException();
		this.encode(XMLReaderFactory.createXMLReader(), new InputSource(new FileReader(source)), new EncodeTargetFile(target));
	}

	/**
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues {@link EncodeDocument} ein und gibt dieses zurück.
	 * 
	 * @see #encode(XMLReader, InputSource, EncodeDocument)
	 * @param reader {@link XMLReader}.
	 * @param source {@link InputSource}.
	 * @return {@link EncodeDocument}.
	 * @throws IOException Wenn die {@link InputSource} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public EncodeDocument encode(final XMLReader reader, final InputSource source) throws IOException, SAXException, NullPointerException {
		if((reader == null) || (source == null)) throw new NullPointerException();
		final EncodeDocument target = new EncodeDocument();
		this.encode(reader, source, target);
		return target;
	}

	/**
	 * Diese Methode speichert das gegebene {@link EncodeDocument} in das gegebene {@link EncodeTarget}.
	 * 
	 * @see #compilePool(EncodePool, int, Comparator)
	 * @see #compileHash(List, boolean, Comparable)
	 * @param source {@link EncodeDocument}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final EncodeDocument source, final EncodeTarget target) throws IOException, NullPointerException {
		if((source == null) || (target == null)) throw new NullPointerException();

		// Value

		// final List<EncodeValue> valuePool = Encoder2.compilePool(source.valuePool, Encoder2.EncodeItem_Index_Comparator);
		// final List<EncodeGroup> valueHash = Encoder2.compileHash(valuePool, this.valueHashEnabled, Encoder2.ValueHasher);

		final List<EncodeValue> xmlnsUriPool = Encoder2.compilePool(source.xmlnsUriPool, Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> xmlnsUriHash = Encoder2.compileHash(xmlnsUriPool, this.xmlnsUriHashEnabled, Encoder2.EncodeValue_Hasher);

		final List<EncodeValue> xmlnsPrefixPool = Encoder2.compilePool(source.xmlnsPrefixPool, Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> xmlnsPrefixHash = Encoder2.compileHash(xmlnsPrefixPool, this.xmlnsPrefixHashEnabled, Encoder2.EncodeValue_Hasher);

		final List<EncodeValue> elementNamePool = Encoder2.compilePool(source.elementNamePool, Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> elementNameHash = Encoder2.compileHash(elementNamePool, this.elementNameHashEnabled, Encoder2.EncodeValue_Hasher);

		final List<EncodeValue> attributeNamePool = Encoder2.compilePool(source.getAttributeNamePool(), Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> attributeNameHash = Encoder2.compileHash(attributeNamePool, this.attributeNameHashEnabled, Encoder2.EncodeValue_Hasher);

		final List<EncodeValue> referenceNamePool = Encoder2.compilePool(source.referenceNamePool, Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> referenceNameHash = Encoder2.compileHash(referenceNamePool, this.referenceNameHashEnabled, Encoder2.EncodeValue_Hasher);

		final List<EncodeValue> instructionNamePool = Encoder2.compilePool(source.instructionNamePool, Encoder2.EncodeValue_String_Comparator);
		final List<EncodeGroup> instructionNameHash = Encoder2.compileHash(instructionNamePool, this.instructionNameHashEnabled, Encoder2.EncodeValue_Hasher);

		// Label

		final List<EncodeXmlnsLabel> xmlnsLabelPool = Encoder2.compilePool(source.xmlnsLabelPool, Encoder2.EncodeXmlnsLabel_PrefixUri_Comparator);

		final List<EncodeElementLabel> elementLabelPool = Encoder2.compilePool(source.elementLabelPool, Encoder2.EncodeAttributeLabel_NameUri_Comparator);

		final List<EncodeAttributeLabel> attributeLabelPool = Encoder2.compilePool(source.attributeLabelPool, Encoder2.EncodeAttributeLabel_NameUri_Comparator);

		// Data

		final List<EncodeTextNode> textNodePool = Encoder2.compilePool(source.textNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeCommentNode> commentNodePool = Encoder2.compilePool(source.commentNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeElementNode> elementNodePool = Encoder2.compilePool(source.elementNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeAttributeNode> attributeNodePool = Encoder2.compilePool(source.attributeNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeReferenceNode> referenceNodePool = Encoder2.compilePool(source.referenceNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeInstructionNode> instructionNodePool = Encoder2.compilePool(source.instructionNodePool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeGroup> xmlnsLookupPool = Encoder2.compilePool(source.xmlnsLookupPool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeGroup> elementChildrenPool = Encoder2.compilePool(source.elementChildrenPool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeGroup> elementAttributesPool = Encoder2.compilePool(source.elementAttributesPool, Encoder2.EncodeItem_Index_Comparator);

		final List<EncodeIndices> navigationIndicesPool = Encoder2.compilePool(source.navigationIndicesPool, Encoder2.EncodeItem_Index_Comparator);

		// Navigation

		final List<EncodeNavigationEntry> navigationEntryPool = Encoder2.compilePool(source.navigationEntryPool, Encoder2.EncodeNavigationEntry_Id_Comparator);
		final List<EncodeGroup> navigationEntryHash =
			Encoder2.compileHash(navigationEntryPool, this.navigationEntryHashEnabled, Encoder2.EncodeNavigationEntry_Id_Hasher);

		// Encoder2.writeLists(target, uriHash);
		// Encoder2.writeLists(target, uriPool);
		// Encoder2.writeLists(target, valueHash);
		// Encoder2.writeLists(target, valuePool);
		// Encoder2.writeLists(target, xmlnsNameHash);
		// Encoder2.writeLists(target, xmlnsNamePool);
		// Encoder2.writeLists(target, xmlnsLabelHash);
		// Encoder2.writeItems(target, xmlnsLabelPool);
		// Encoder2.writeLists(target, elementNameHash);
		// Encoder2.writeLists(target, elementNamePool);
		// Encoder2.writeLists(target, elementLabelHash);
		// Encoder2.writeItems(target, elementLabelPool);
		// Encoder2.writeLists(target, attributeNameHash);
		// Encoder2.writeLists(target, attributeNamePool);
		// Encoder2.writeLists(target, attributeLabelHash);
		// Encoder2.writeItems(target, attributeLabelPool);
		// Encoder2.writeLists(target, elementXmlnsPool);
		// Encoder2.writeLists(target, elementChildrenPool);
		// Encoder2.writeLists(target, elementAttributesPool);
		// Encoder2.writeItems(target, elementNodePool);
		// Encoder2.writeItems(target, attributeNodePool);
		// Encoder2.writeLists(target, navigationPathHash);
		// Encoder2.writeLists(target, navigationPathPool);
		// Encoder2.writeInts(target, source.getDocumentElement().getIndex() - valuePool.size());
		System.out.println(source);
	}

	/**
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues {@link EncodeDocument} ein und speichert dessen in das gegebene {@link EncodeTarget}.
	 * 
	 * @see #encode(XMLReader, InputSource)
	 * @see #encode(EncodeDocument, EncodeTarget)
	 * @param reader {@link XMLReader}.
	 * @param source {@link InputSource}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn {@link InputSource} oder {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final XMLReader reader, final InputSource source, final EncodeTarget target) throws IOException, SAXException, NullPointerException {
		if((reader == null) || (source == null) || (target == null)) throw new NullPointerException();
		this.encode(this.encode(reader, source), target);
	}

	/**
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in das gegebene {@link EncodeDocument} ein.
	 * 
	 * @see XMLReader#setContentHandler(ContentHandler)
	 * @see XMLReader#parse(InputSource)
	 * @param reader {@link XMLReader}.
	 * @param source {@link InputSource}.
	 * @param target {@link EncodeDocument}.
	 * @throws IOException Wenn die {@link InputSource} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final XMLReader reader, final InputSource source, final EncodeDocument target) throws IOException, SAXException, NullPointerException {
		if((reader == null) || (source == null) || (target == null)) throw new NullPointerException();
		final EncodeDocumentHandler adapter = new EncodeDocumentHandler(target, this.xmlnsEnabled, (this.navigationPathEnabled ? this.navigationPathFilter : null));
		reader.setContentHandler(adapter);
		reader.parse(source);
	}

}
