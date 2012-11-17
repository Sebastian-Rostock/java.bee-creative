package bee.creative.xml.coder;

import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

		final Encoder2 encoder2 = new Encoder2();
		encoder2.setXmlnsEnabled(true);
		encoder2.encode(new File("D:\\projects\\java\\bee-creative\\xml-coder\\src\\main\\java\\cds.xml"), new File("cds.bin"));

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

		protected final int index;

		/**
		 * Dieses Feld speichert das {@link EncodeElementNode}.
		 */
		protected final EncodeElementNode element;

		/**
		 * Dieser Konstrukteur initialisiert {@link EncodeNavigationPath} und {@link EncodeElementNode}.
		 * 
		 * @param path {@link EncodeNavigationPath}.
		 * @param element {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementPath(final EncodeNavigationPath path, final int index, final EncodeElementNode element) throws NullPointerException {
			if(path == null) throw new NullPointerException();
			if(element == null) throw new NullPointerException();
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
			return Objects.toStringCall("EncodeElementPath", this.pathNames(), this.elementName());
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
		 * Dieses Feld speichert die {@link Attributes}.
		 */
		protected final Attributes atts;

		/**
		 * Dieses Feld speichert den Index.
		 */
		protected final int index;

		/**
		 * Dieser Konstrukteur initialisiert {@link EncodeNavigationPath}, {@link Attributes} und Index.
		 * 
		 * @param path {@link EncodeNavigationPath}.
		 * @param atts {@link Attributes}.
		 * @param index Index.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributePath(final EncodeNavigationPath path, final Attributes atts, final int index) throws NullPointerException {
			if(path == null) throw new NullPointerException();
			if(atts == null) throw new NullPointerException();
			this.path = path;
			this.atts = atts;
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected EncodeNavigationPath path() {
			return this.path;
		}

		/**
		 * Diese Methode gibt die {@code Uri} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getNamespaceURI()
		 * @see Attributes#getURI(int)
		 * @return {@code Uri} des {@link Attr}s.
		 */
		public String attributeUri() {
			return this.atts.getURI(this.index);
		}

		/**
		 * Diese Methode gibt den {@code Name} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attributes#getLocalName(int)
		 * @return {@code Name} des {@link Attr}s.
		 */
		public String attributeName() {
			return this.atts.getLocalName(this.index);
		}

		/**
		 * Diese Methode gibt den {@code Type} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getSchemaTypeInfo()
		 * @see Attributes#getType(int)
		 * @return {@code Type} des {@link Attr}s.
		 */
		public String attributeType() {
			return this.atts.getType(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("EncodeAttributePath", this.pathNames(), this.attributeName());
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

		public Iterable<String> getIds(EncodeNavigationPath navigationPath);

	}

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
				final EncodeIndices item = (EncodeIndices)source;
				Encoder2.writeInts(target, item.indices.toArray());
				break;
			}
			case EncodeItem.TYPE_VALUE:{
				final EncodeValue item = (EncodeValue)source;
				final byte[] bytes = Coder.encodeChars(item.string);
				target.write(bytes, 0, bytes.length);
				break;
			}
			case EncodeItem.TYPE_GROUP:{
				final EncodeGroup item = (EncodeGroup)source;
				Encoder2.writeIndices(target, item.items.values());
				break;
			}
			case EncodeItem.TYPE_XMLNS_LABEL:{
				final EncodeXmlnsLabel item = (EncodeXmlnsLabel)source;
				Encoder2.writeInts(target, item.uri.index, item.prefix.index);
				break;
			}
			case EncodeItem.TYPE_ELEMENT_NODE:{
				final EncodeElementNode item = (EncodeElementNode)source;
				Encoder2.writeInts(target, item.label.index, item.children.index, item.attributes.index);
				break;
			}
			case EncodeItem.TYPE_ELEMENT_LABEL:{
				final EncodeElementLabel item = (EncodeElementLabel)source;
				Encoder2.writeInts(target, item.uri.index, item.name.index, item.prefix.index, item.lookupUriList.index, item.lookupPrefixList.index);
				break;
			}
			case EncodeItem.TYPE_ATTRIBUTE_LABEL:{
				final EncodeAttributeLabel item = (EncodeAttributeLabel)source;
				Encoder2.writeInts(target, item.uri.index, item.name.index, item.prefix.index);
				break;
			}
			case EncodeItem.TYPE_ATTRIBUTE_NODE:{
				final EncodeAttributeNode item = (EncodeAttributeNode)source;
				Encoder2.writeInts(target, item.label.index, item.value.index);
				break;
			}
			case EncodeItem.TYPE_INSTRUCTION_NODE:{
				final EncodeInstructionNode item = (EncodeInstructionNode)source;
				Encoder2.writeInts(target, item.name.index, item.value.index);
				break;
			}
		}
	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index, der als Element in einem {@link EncodePool} verwendet werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem {

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
		int index = 0;

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

		public EncodeValue asValue() {
			return null;
		}

		public EncodeGroup asGroup() {
			return null;
		}

		public EncodeIndices asIndices() {
			return null;
		}

		public EncodeXmlnsLabel asXmlnsLabel() {
			return null;
		}

		public EncodeElementLabel asElementLabel() {
			return null;
		}

		public EncodeAttributeLabel asAttributeLabel() {
			return null;
		}

		public EncodeNavigationEntry asNavigationEntry() {
			return null;
		}

		public EncodeTextNode asTextNode() {
			return null;
		}

		public EncodeElementNode asElementNode() {
			return null;
		}

		public EncodeCommentNode asCommentNode() {
			return null;
		}

		public EncodeReferenceNode asReferenceNode() {
			return null;
		}

		public EncodeAttributeNode asAttributeNode() {
			return null;
		}

		public EncodeInstructionNode asInstructionNode() {
			return null;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Hash}-{@code Set}. Wenn das via {@link #get(EncodeItem)} zu einem gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, werden dieses als Rückgabewert verwendet und dessen Wiederverwendung via {@link #reuse(EncodeItem)} signalisiert. Das Einfügen eines neuen Elements wird dagegen mit {@link #insert(EncodeItem)} angezeigt. Die Implementation ähnelt einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
	 * 
	 * @see Hash
	 * @see Unique
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class EncodePool<GItem extends EncodeItem> extends Hash<GItem, GItem, GItem> implements Iterable<GItem> {

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
		 * Diese Methode wird beim Wiederverwenden des gegebenen Elements aufgerufen.
		 * 
		 * @see #get(EncodeItem)
		 * @param value Element.
		 */
		protected void reuse(final GItem value) {
			value.index++;
		}

		/**
		 * Diese Methode wird beim Einfügen des gegebenen Elements aufgerufen.
		 * 
		 * @see #get(EncodeItem)
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
		 * @throws IllegalArgumentException Wenn das gegebene Element eingefügt werden müsste, aber bereits von einem {@link EncodePool} verwaltet wird.
		 */
		public GItem get(final GItem key) throws NullPointerException, IllegalArgumentException {
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
			return Objects.toStringCall(false, true, this.getClass().getSimpleName(), "size", this.size());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index und mehreren Werten, der als Element in einem {@link EncodePool} verwendet werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeList extends EncodeItem {

		/**
		 * Diese Methode gibt die Länge des Datensatzes bzw. Anzahl seiner Werte zurück.
		 * 
		 * @return Länge des Datensatzes bzw. Anzahl der Werte.
		 */
		public abstract int getLength();

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValue extends EncodeList {

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
			if(string == null) throw new NullPointerException("string is null");
			this.string = string;
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
		public int getLength() {
			return Coder.encodeChars(this.string).length;
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
			return this.string;
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
		 * @see EncodeValuePool#get(EncodeValue)
		 * @see EncodeValue#getString()
		 * @param value {@code String}.
		 * @return einzigartiger {@link EncodeValue}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue unique(final String value) throws NullPointerException {
			return this.get(new EncodeValue(value));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} als Gruppe mehrerer {@link EncodeItem}s.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeGroup extends EncodeList {

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
			if(items == null) throw new NullPointerException("items is null");
			if(Arrays.asList(items).contains(null)) throw new NullPointerException("items contains null");
			this.items.add(items);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param items {@link EncodeItem}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup(final List<? extends EncodeItem> items) throws NullPointerException {
			if(items == null) throw new NullPointerException("items is null");
			if(items.contains(null)) throw new NullPointerException("items contains null");
			this.items.values().addAll(items);
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
		public int getLength() {
			return this.items.size();
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
			return Objects.toString(this.items);
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
		 * @see EncodeGroupPool#get(EncodeGroup)
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeGroup}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup unique(final List<? extends EncodeItem> value) throws NullPointerException {
			return this.get(new EncodeGroup(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines Navigationspfades mit Id.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeIndices extends EncodeList {

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
			if(indices == null) throw new NullPointerException("indices is null");
			this.indices.add(indices);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@code Indices}.
		 * 
		 * @param indices {@link Integer}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link Integer}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeIndices(final List<Integer> indices) throws NullPointerException {
			if(indices == null) throw new NullPointerException("indices is null");
			if(indices.contains(null)) throw new NullPointerException("indices contains null");
			this.indices.values().addAll(indices);
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
		public int getLength() {
			return this.indices.size();
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
			return Objects.toString(this.indices);
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
		 * @see EncodeIndicesPool#get(EncodeIndices)
		 * @param indices {@code Indices}.
		 * @return einzigartige {@link EncodeIndices}.
		 * @throws NullPointerException Wenn die gegebenen {@code Indices} {@code null} sind.
		 */
		public EncodeIndices unique(final int... indices) throws NullPointerException {
			return this.get(new EncodeIndices(indices));
		}

		/**
		 * Diese Methode gibt die einzigartigen {@link EncodeIndices} mit der gegebenen {@code Indices} zurück.
		 * 
		 * @see EncodeIndices#EncodeIndices(List)
		 * @see EncodeIndicesPool#get(EncodeIndices)
		 * @param indices {@code Indices}.
		 * @return einzigartige {@link EncodeIndices}.
		 * @throws NullPointerException Wenn die gegebenen {@code Indices} {@code null} sind oder enthalten.
		 */
		public EncodeIndices unique(final List<Integer> indices) throws NullPointerException {
			return this.get(new EncodeIndices(indices));
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(prefix == null) throw new NullPointerException("prefix is null");
			this.uri = uri;
			this.prefix = prefix;
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
			if(this.prefix.string.isEmpty()) return "xmlns=\"" + this.uri + "\"";
			return "xmlns:" + this.prefix + "=\"" + this.uri + "\"";
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
		 * @see EncodeXmlnsLabelPool#get(EncodeXmlnsLabel)
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return einzigartiges {@link EncodeXmlnsLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeXmlnsLabel unique(final EncodeValue uri, final EncodeValue prefix) throws NullPointerException {
			return this.get(new EncodeXmlnsLabel(uri, prefix));
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
			if(document == null) throw new NullPointerException("document is null");
			this.parent = null;
			this.document = document;
		}

		/**
		 * Diese Methode gibt das {@link Iterable} über alle {@code Prefix}es zurück, wobei {@code Prefix}e auch mehrfach vorkommen können.
		 * 
		 * @return {@code Prefix}-{@link Iterable}.
		 */
		private Iterable<String> prefixes() {
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
		private void createMaps() {
			this.lookupUriMap = new LinkedHashMap<String, String>(1);
			this.lookupPrefixMap = new LinkedHashMap<String, String>(1);
		}

		/**
		 * Diese Methode fügt das gegebene {@code Uri/Prefix}-Paar in die {@link Map}s ein.
		 * 
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 */
		private void updateMaps(final String uri, final String prefix) {
			this.lookupUriMap.put(prefix, uri);
			this.lookupPrefixMap.put(uri, prefix);
		}

		/**
		 * Diese Methode erzeugt die beiden {@link List}s der {@code Uri/Prefix}-Paare, sofern diese notwendig ist.
		 */
		private void updateLists() {
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
				final EncodeXmlnsLabel xmlnsLabel =
					document.xmlnsLabelPool.unique(document.xmlnsUriPool.unique(xmlnsUri), document.xmlnsPrefixPool.unique(xmlnsPrefix));
				lookupUriList.add(xmlnsLabel);
				lookupPrefixMap.put(xmlnsUri, this.lookupPrefix(xmlnsUri));
			}
			lookupUriList.trimToSize();
			for(final Entry<String, String> xmlnsEntry: lookupPrefixMap.entrySet()){
				final String xmlnsUri = xmlnsEntry.getKey();
				final String xmlnsPrefix = xmlnsEntry.getValue();
				final EncodeXmlnsLabel xmlnsLabel =
					document.xmlnsLabelPool.unique(document.xmlnsUriPool.unique(xmlnsUri), document.xmlnsPrefixPool.unique(xmlnsPrefix));
				lookupPrefixList.add(xmlnsLabel);
			}
			lookupPrefixList.trimToSize();
			Collections.sort(lookupUriList, Encoder2.XmlnsPrefixUriComparator);
			Collections.sort(lookupPrefixList, Encoder2.XmlnsUriPrefixComparator);
			this.lookupUriList = lookupUriList;
			this.lookupPrefixList = lookupPrefixList;
		}

		/**
		 * Diese Methode setzt beide {@link List}s auf {@code null}.
		 */
		private void removeLists() {
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(prefix == null) throw new NullPointerException("prefix is null");
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(prefix == null) throw new NullPointerException("prefix is null");
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
			if(id == null) throw new NullPointerException("id is null");
			if(indices == null) throw new NullPointerException("indices is null");
			this.id = id;
			this.indices = indices;
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
		 * Diese Methode gibt die {@code Indices} zurück.
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
			return Objects.toStringCall(this.id.string, this.indices);
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
		 * @see EncodeNavigationEntryPool#get(EncodeNavigationEntry)
		 * @param id {@code Id}.
		 * @param indices {@code Indices}.
		 * @return einzigartiges {@link EncodeNavigationEntry}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeNavigationEntry unique(final EncodeValue id, final EncodeIndices indices) throws NullPointerException {
			return this.get(new EncodeNavigationEntry(id, indices));
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
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
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
			return this.value.toString();
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
		 * @see EncodeTextNodePool#get(EncodeTextNode)
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeTextNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeTextNode unique(final EncodeValue value) throws NullPointerException {
			return this.get(new EncodeTextNode(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementNode extends EncodeItem {

		/**
		 * Dieses Feld speichert das {@code Label}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getPrefix()
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		protected final EncodeElementLabel label;

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
		public EncodeElementNode(final EncodeElementLabel label, final EncodeGroup children, final EncodeGroup attributes) throws NullPointerException {
			if(label == null) throw new NullPointerException("label is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			this.label = label;
			this.children = children;
			this.attributes = attributes;
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
		public EncodeElementLabel getLabel() {
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
			final StringBuilder builder = new StringBuilder();
			builder.append('<').append(this.label);
			for(final Object label: this.label.lookupUriList.items.values()){
				builder.append(' ').append(label);
			}
			for(final Object label: this.attributes.items.values()){
				builder.append(' ').append(label);
			}
			builder.append('>');
			for(final Object label: this.children.items.values()){
				builder.append(label);
			}
			builder.append("</").append(this.label).append(">");
			return builder.toString();
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
		 * @see EncodeElementNodePool#get(EncodeElementNode)
		 * @param label {@code Label}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return einzigartiger {@link EncodeElementNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementNode unique(final EncodeElementLabel label, final EncodeGroup children, final EncodeGroup attributes) throws NullPointerException {
			return this.get(new EncodeElementNode(label, children, attributes));
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			if(parent == null) throw new NullPointerException("parent is null");
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
			if(id == null) throw new NullPointerException("id ist null");
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			this.check();
			final String prefix = this.xmlns.lookupPrefix(uri);
			if(prefix == null) throw new IllegalArgumentException("uri has no prefix");
			for(final EncodeAttributeNode attribute: this.attributes){
				final EncodeAttributeLabel label = attribute.label;
				if(label.uri.string.equals(uri) && label.name.string.equals(name)) throw new IllegalArgumentException("attribute already exists");
			}
			this.attributes.add(this.document.createAttributeNode(uri, name, prefix, value));
			Collections.sort(this.attributes, Encoder2.AttributeNameUriComparator);
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
			if(lookupUriList == null) throw new NullPointerException("lookupUriList is null");
			if(lookupPrefixList == null) throw new NullPointerException("lookupPrefixList is null");
			this.lookupUriList = lookupUriList;
			this.lookupPrefixList = lookupPrefixList;
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
		 * @see EncodeAttributeLabelPool#get(EncodeAttributeLabel)
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
			return this.get(new EncodeElementLabel(uri, name, prefix, lookupUriList, lookupPrefixList));
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
			if(label == null) throw new NullPointerException("label is null");
			this.label = label;
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
			return this.label + "=\"" + this.value + "\"";
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
		 * @see EncodeAttributeNodePool#get(EncodeAttributeNode)
		 * @param label {@code Label}.
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeAttributeNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeNode unique(final EncodeAttributeLabel label, final EncodeValue value) throws NullPointerException {
			return this.get(new EncodeAttributeNode(label, value));
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
			if(name == null) throw new NullPointerException("name is null");
			this.name = name;
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
			if(this.prefix.string.isEmpty()) return this.name.string;
			return this.prefix.string + ":" + this.name.string;
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
		 * @see EncodeAttributeLabelPool#get(EncodeAttributeLabel)
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @return einzigartiges {@link EncodeAttributeLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel unique(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix) throws NullPointerException {
			return this.get(new EncodeAttributeLabel(uri, name, prefix));
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
			if(name == null) throw new NullPointerException("name is null");
			this.name = name;
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
			return "&" + this.name + ";";
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
		 * @see EncodeReferenceNodePool#get(EncodeReferenceNode)
		 * @param name {@code Name}.
		 * @return einzigartiger {@link EncodeReferenceNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Name} {@code null} ist.
		 */
		public EncodeReferenceNode unique(final EncodeValue name) throws NullPointerException {
			return this.get(new EncodeReferenceNode(name));
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
			return super.asCommentNode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "<!--" + this.value + "-->";
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
		 * @see EncodeCommentNodePool#get(EncodeCommentNode)
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeCommentNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeCommentNode unique(final EncodeValue value) throws NullPointerException {
			return this.get(new EncodeCommentNode(value));
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
			if(name == null) throw new NullPointerException("name is null");
			this.name = name;
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
			return "<?" + this.name + " " + this.value + "?>";
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
		 * @see EncodeInstructionNodePool#get(EncodeInstructionNode)
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiger {@link EncodeInstructionNode}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeInstructionNode unique(final EncodeValue name, final EncodeValue value) throws NullPointerException {
			return this.get(new EncodeInstructionNode(name, value));
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link EncodePool}s zur Abstraktion eines {@link Document} s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocument {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeNode#getValue()}.
		 */
		protected final EncodeValuePool valuePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()}.
		 */
		protected final EncodeValuePool xmlnsUriPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()}.
		 */
		protected final EncodeValuePool xmlnsPrefixPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		protected final EncodeXmlnsLabelPool xmlnsLabelPool = new EncodeXmlnsLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		protected final EncodeGroupPool xmlnsLookupPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeTextNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected final EncodeTextNodePool textNodePool = new EncodeTextNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeElementLabel#getName()}.
		 */
		protected final EncodeValuePool elementNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeElementNodePool} für {@link EncodeDocument#getDocumentElement()}, {@link EncodeDocument#getDocumentChildren()} und {@link EncodeElementNode#getChildren()}.
		 */
		protected final EncodeElementNodePool elementNodePool = new EncodeElementNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeElementLabelPool} für {@link EncodeElementNode#getLabel()}.
		 */
		protected final EncodeElementLabelPool elementLabelPool = new EncodeElementLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected final EncodeGroupPool elementChildrenPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElementNode#getAttributes()}.
		 */
		protected final EncodeGroupPool elementAttributesPool = new EncodeGroupPool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()}.
		 */
		protected final EncodeValuePool attributeNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeNodePool} für {@link EncodeElementNode#getAttributes()}.
		 */
		protected final EncodeAttributeNodePool attributeNodePool = new EncodeAttributeNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()}.
		 */
		protected final EncodeAttributeLabelPool attributeLabelPool = new EncodeAttributeLabelPool();

		/**
		 * Dieses Feld speichert den {@link EncodeCommentNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		protected final EncodeCommentNodePool commentNodePool = new EncodeCommentNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()}.
		 */
		protected final EncodeValuePool referenceNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeReferenceNodePool} für {@link EncodeElementNode#getChildren()}.
		 */
		protected final EncodeReferenceNodePool referenceNodePool = new EncodeReferenceNodePool();

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()}.
		 */
		protected final EncodeValuePool instructionNamePool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		protected final EncodeInstructionNodePool instructionNodePool = new EncodeInstructionNodePool();

		protected final EncodeValuePool navigationIdPool = new EncodeValuePool();

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} mit den Navigationspfaden für {@link Document#getElementById(String)}.
		 */
		protected final EncodeNavigationEntryPool navigationEntryPool = new EncodeNavigationEntryPool();

		protected final EncodeIndicesPool navigationIndicesPool = new EncodeIndicesPool();

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} für {@link Document#getChildNodes()}.
		 */
		protected EncodeGroup documentChildren;

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttributeNode#getValue()} und {@link EncodeElementNode#getChildren()} zurück. <br>
		 * Im {@link EncodeTarget} werden dessen Elemente nach ihrem {@link EncodeValue#getString()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNodeValue()
		 * @return {@link EncodeValuePool} für {@link EncodeAttributeNode#getValue()} und {@link EncodeElementNode#getChildren()}.
		 */
		public EncodeValuePool getValuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()} zurück. <br>
		 * Im {@link EncodeTarget} werden dessen Elemente nach ihrem {@link EncodeValue#getString()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getUri()}.
		 */
		public EncodeValuePool getXmlnsUriPool() {
			return this.xmlnsUriPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()} zurück. <br>
		 * Im {@link EncodeTarget} werden dessen Elemente nach ihrem {@link EncodeValue#getString()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getPrefix()
		 * @return {@link EncodeValuePool} für {@link EncodeXmlnsLabel#getPrefix()}.
		 */
		public EncodeValuePool getXmlnsPrefixPool() {
			return this.xmlnsPrefixPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()} zurück.<br>
		 * Im {@link EncodeTarget} werden dessen Elemente primär nach {@link EncodeXmlnsLabel#getPrefix()} und sekundär nach {@link EncodeXmlnsLabel#getUri()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeXmlnsLabelPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()}.
		 */
		public EncodeXmlnsLabelPool getXmlnsLabelPool() {
			return this.xmlnsLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElementLabel#getLookupUriList()} bzw. {@link EncodeElementLabel#getLookupPrefixList()} zurück.<br>
		 * Im {@link EncodeTarget} werden dessen Elemente nach Häufigkeit aufsteigend sortiert gespeichert.
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
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeElementLabel#getName()} zurück.<br>
		 * Im {@link EncodeTarget} werden dessen Elemente nach ihrem {@link EncodeValue#getString()} aufsteigend sortiert gespeichert.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeElementLabel#getName()}.
		 */
		public EncodeValuePool getElementNamePool() {
			return this.elementNamePool;
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
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeAttributeLabel#getName()}.
		 */
		public EncodeValuePool getAttributeNamePool() {
			return this.attributeNamePool;
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
		 * Diese Methode gibt den {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()} zurück.
		 * 
		 * @return {@link EncodeAttributeLabelPool} für {@link EncodeAttributeNode#getLabel()}.
		 */
		public EncodeAttributeLabelPool getAttributeLabelPool() {
			return this.attributeLabelPool;
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
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeReferenceNode#getName()}.
		 */
		public EncodeValuePool getReferenceNamePool() {
			return this.referenceNamePool;
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
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeInstructionNode#getName()}.
		 */
		public EncodeValuePool getInstructionNamePool() {
			return this.instructionNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()} zurück.
		 * 
		 * @return {@link EncodeInstructionNodePool} für {@link EncodeElementNode#getChildren()} und {@link EncodeDocument#getDocumentChildren()}.
		 */
		public EncodeInstructionNodePool getInstructionNodePool() {
			return this.instructionNodePool;
		}

		public EncodeValuePool getNavigationIdPool() {
			return this.navigationIdPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} mit den Navigationspfaden für {@link Document#getElementById(String)} zurück. Das erste Element jeder {@link EncodeGroup} bzw. jeses Navigationspfads ist der {@link EncodeValue} mit der {@code ID} des {@link Element}s, das über dieser {@code ID} erreicht werden soll. Die folgenden Elemente beschriben mit je einem {@link EncodePosition} und einem {@link EncodeElementNode} den Pfad zu diesem {@link Element}, wobei der {@link EncodePosition} den {@code Child}-Index des jeweils nachfolgenden {@link EncodeElementNode}s beschreibt.<br>
		 * Der Pfad {@code /node[7]/node[3]/node[9]} mit der {@code ID} {@code "VALUE"} wird damit zu: <code>{ "VALUE", 7, /node[7], 3, /node[7]/node[3], 9, /node[7]/node[3]/node[9] }</code>.
		 * 
		 * @see Attr#isId()
		 * @see Document#getElementById(String)
		 * @return {@link EncodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		public EncodeNavigationEntryPool getNavigationEntryPool() {
			return this.navigationEntryPool;
		}

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
				if(item instanceof EncodeElementNode){
					if(documentElement != null) throw new IllegalStateException("multiple document elements");
					documentElement = (EncodeElementNode)item;
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
		 * Diese Methode gibt den {@link EncodeTextNode} mit dem gegebenen {@code Value} zurück.
		 * 
		 * @see EncodeTextNodePool#unique(EncodeValue)
		 * @param value {@code Value}.
		 * @return {@link EncodeTextNode}.
		 * @throws NullPointerException Wenn der gegebene {@code Value} {@code null} ist.
		 */
		public EncodeTextNode createTextNode(final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			return this.textNodePool.unique(this.valuePool.unique(value));
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeElementNodePool#unique(EncodeElementLabel, EncodeGroup, EncodeGroup)
		 * @see EncodeElementLabelPool#unique(EncodeValue, EncodeValue, EncodeValue, EncodeGroup, EncodeGroup)
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
			final List<? extends EncodeItem> lookupPrefixList, final List<? extends EncodeItem> children, final List<? extends EncodeItem> attributes) {
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			if(prefix == null) throw new NullPointerException("prefix is null");
			if(lookupUriList == null) throw new NullPointerException("lookupUriList is null");
			if(lookupPrefixList == null) throw new NullPointerException("lookupPrefixList is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			return this.elementNodePool.unique(this.elementLabelPool.unique(this.xmlnsUriPool.unique(uri), this.elementNamePool.unique(name),
				this.xmlnsPrefixPool.unique(prefix), this.xmlnsLookupPool.unique(lookupUriList), this.xmlnsLookupPool.unique(lookupPrefixList)),
				this.elementChildrenPool.unique(children), this.elementAttributesPool.unique(attributes));
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
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			if(prefix == null) throw new NullPointerException("prefix is null");
			if(value == null) throw new NullPointerException("value is null");
			return this.attributeNodePool.unique(
				this.attributeLabelPool.unique(this.xmlnsUriPool.unique(uri), this.attributeNamePool.unique(name), this.xmlnsPrefixPool.unique(name)),
				this.valuePool.unique(value));
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
			if(value == null) throw new NullPointerException("value is null");
			return this.commentNodePool.unique(this.valuePool.unique(value));
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
			if(name == null) throw new NullPointerException("name is null");
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
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			return this.instructionNodePool.unique(this.instructionNamePool.unique(name), this.valuePool.unique(value));
		}

		public EncodeNavigationEntry createNavigationEntry(final String id, final List<Integer> indices) throws NullPointerException {
			if(id == null) throw new NullPointerException("id is null");
			final EncodeNavigationEntry entry = new EncodeNavigationEntry(new EncodeValue(id), new EncodeIndices(indices));
			if(this.navigationEntryPool.contains(entry)) throw new IllegalArgumentException("id already exists");
			return this.navigationEntryPool.unique(this.navigationIdPool.get(entry.id), this.navigationIndicesPool.get(entry.indices));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeDocument", //
				"valuePool", this.valuePool, //
				"xmlnsUriPool", this.xmlnsUriPool, //
				"xmlnsPrefixPool", this.xmlnsPrefixPool, //
				"xmlnsLabelPool", this.xmlnsLabelPool, //
				"xmlnsLookupPool", this.xmlnsLookupPool, //
				"textNodePool", this.textNodePool, //
				"elementNamePool", this.elementNamePool, //
				"elementNodePool", this.elementNodePool, //
				"elementLabelPool", this.elementLabelPool, //
				"elementChildrenPool", this.elementChildrenPool, //
				"elementAttributesPool", this.elementAttributesPool, //
				"attributeNamePool", this.attributeNamePool, //
				"attributeNodePool", this.attributeNodePool, //
				"attributeLabelPool", this.attributeLabelPool, //
				"commentNodePool", this.commentNodePool, //
				"referenceNamePool", this.referenceNamePool, //
				"referenceNodePool", this.referenceNodePool, //
				"instructionNamePool", this.instructionNamePool, //
				"instructionNodePool", this.instructionNodePool, //
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
		 * Dieses Feld speichert die {@link Map} der Navigationspfade.
		 */
		protected Map<String, List<Integer>> navigations;

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
			this.xmlns = new EncodeXmlnsBuilder(this.document = document) //
				.append(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX).append(XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
			this.navigations = new HashMap<String, List<Integer>>(1);
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
		protected void clear() {
			super.clear();
			this.navigations = null;
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
			final List<EncodeItem> children = this.children();
			final EncodeDocument document = this.document;
			document.documentChildren = document.elementChildrenPool.unique(children);
			// TODO document.navigationPathPool

			// for(final Entry<String, List<Integer>> entry: this.navigations.entrySet()){
			// final List<Integer> source = entry.getValue();
			// final int size = source.size();
			// final List<EncodeItem> target = new ArrayList<EncodeItem>((size * 2) + 1);
			// List<?> nodes = children;
			// target.add(document.valuePool.unique(entry.getKey()));
			// for(int i = 0; i < size; i++){
			// final int index = source.get(i).intValue();
			// target.add(new EncodePosition(index));
			// final EncodeElementNode node = (EncodeElementNode)nodes.get(index);
			// target.add(node);
			// nodes = node.children.items;
			// }
			// document.navigationPathPool.unique(target);
			// }
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
		protected static final class CursorStack {

			/**
			 * Dieses Feld speichert die {@code ID} oder {@code null}.
			 */
			protected EncodeValue id;

			/**
			 * Dieses Feld speichert die {@code Uri} oder {@code null}.
			 */
			protected final String uri;

			/**
			 * Dieses Feld speichert den {@code Name}.
			 */
			protected final String name;

			public String prefix;

			/**
			 * Dieses Feld speichert die {@link EncodeElementNode#getChildren()}.
			 */
			protected final List<EncodeItem> children;

			/**
			 * Dieses Feld speichert die {@link EncodeElementNode#getAttributes()}.
			 */
			protected final List<? extends EncodeItem> attributes;

			/**
			 * Dieses Feld speichert die {@link EncodeGroup}s für den {@link EncodeDocument#getNavigationEntryPool()}.
			 */
			protected final List<EncodeGroup> navigations;

			/**
			 * Dieses Feld speichert den nächsten {@link CursorStack} oder {@code null}.
			 */
			protected final CursorStack next;

			/**
			 * Dieser Konstrukteur initialisiert den leeren {@link CursorStack}.
			 */
			public CursorStack() {
				this(null, null, null, null, null, null);
			}

			/**
			 * Dieser Konstrukteur initialisiert den {@link CursorStack}.
			 * 
			 * @see Element#getNodeName()
			 * @see Element#getAttributes()
			 * @param id {@code ID}.
			 * @param name {@code Name}.
			 * @param attributes {@link EncodeAttributeNode}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final EncodeValue id, final String name, final List<EncodeAttributeNode> attributes, final CursorStack next) {
				this(id, null, name, null, attributes, next);
			}

			/**
			 * Dieser Konstrukteur initialisiert den {@link CursorStack}.
			 * 
			 * @see Element#getLocalName()
			 * @see Element#getNamespaceURI()
			 * @see Element#getAttributes()
			 * @see Element#lookupPrefix(String)
			 * @see Element#lookupNamespaceURI(String)
			 * @param id {@code ID}.
			 * @param uri {@code Uri}.
			 * @param name {@code Name}.
			 * @param prefix {@code Prefix}.
			 * @param attributes {@link EncodeAttributeNode}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final EncodeValue id, final String uri, final String name, final String prefix, final List<EncodeAttributeNode> attributes,
				final CursorStack next) {
				this.id = id;
				this.uri = uri;
				this.name = name;
				this.prefix = prefix;
				this.children = new ArrayList<EncodeItem>(0);
				this.attributes = attributes;
				this.navigations = new ArrayList<EncodeGroup>(0);
				this.next = next;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, true, "EncodeCursorStack", "id", this.id, "uri", this.uri, "name", this.name, "prefix", this.prefix, "children",
					this.children, "attributes", this.attributes, "navigations", this.navigations, "next", this.next);
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
			 * Diese Methode fügt das gegebene Segmant aus {@code Uri} und {@code Name} an das Ende des Navigationspfads an.
			 * 
			 * @param pathUri {@code Uri}.
			 * @param pathName {@code Name}.
			 */
			protected void append(final String pathUri, final String pathName, final int pathIndex) {
				final int index = this.pathLength();
				this.pathUriStack.add(index, pathUri);
				this.pathNameStack.add(index, pathName);
				this.pathIndexStack.add(index, Integer.valueOf(pathIndex));
			}

			/**
			 * Diese Methode entfernt das letzte Segment des Navigationspfads.
			 * 
			 * @return
			 */
			protected int remove() {
				final int index = this.pathLength() - 1;
				this.pathUriStack.remove(index);
				this.pathNameStack.remove(index);
				return this.pathIndexStack.remove(index).intValue();
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
				return Objects.toStringCall("NavigationStack", this.pathNames);
			}

		}

		protected EncodeXmlnsBuilder xmlnsBuilder;

		StringBuilder valueBuilder;

		/**
		 * Dieses Feld speichert den {@link CursorStack} für das aktuelle {@link Element}.
		 */
		protected CursorStack cursorStack;

		/**
		 * Dieses Feld speichert den {@link NavigationStack} für den aktuellen Navigationspfad und den {@link EncodeNavigationPathFilter}.
		 */
		protected final NavigationStack navigationStack;

		EncodeNavigationEntryPool navigationPool;

		/**
		 * Dieses Feld speichert das {@link EncodeDocument}.
		 */
		protected final EncodeDocument document;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung zurück.
		 */
		protected final boolean xmlnsEnabled;

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
			this.xmlnsBuilder = new EncodeXmlnsBuilder(document);
			this.document = document;
			this.navigationPool = new EncodeNavigationEntryPool();
			this.navigationStack = new NavigationStack();
			this.xmlnsEnabled = xmlnsEnabled;
			this.valueBuilder = new StringBuilder();
			this.navigationPathFilter = navigationPathFilter;
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
			return this.navigationStack;
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
			this.xmlnsBuilder = this.xmlnsBuilder.remove(prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startPrefixMapping(final String prefix, final String uri) {
			if(!this.xmlnsEnabled) return;
			this.xmlnsBuilder = this.xmlnsBuilder.append(uri, prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endElement(final String uri, final String name, final String qName) {
			this.commitValue();

			final CursorStack cursor = this.cursorStack;
			final CursorStack nextCursor = this.cursorStack.next;
			final List<EncodeItem> children = cursor.children;
			final EncodeNavigationPathFilter filter = this.navigationPathFilter;

			for(int i = 0, size = children.size(); i < size; i++){
				final EncodeItem oldNode = children.get(i);
				if(oldNode.getType() == EncodeItem.TYPE_ELEMENT_NODE){
					final EncodeElementNode elementNode = (EncodeElementNode)oldNode;

					if((filter != null) && filter.isId(new EncodeElementPath(this.navigationStack, i, elementNode))){
						final List<EncodeItem> items = elementNode.children.items.values();
						if(items.size() != 1) throw new IllegalArgumentException("invalid element content as id");
						final EncodeItem item = items.get(0);
						if(item.getType() != EncodeItem.TYPE_VALUE) throw new IllegalArgumentException("invalid element content as id");
						document.createNavigationEntry(item.asValue().string, navigationStack.pathIndexStack);
					}

				}
			}

			final EncodeElementNode elementNode =
				this.document.createElementNode(cursor.uri, cursor.name, cursor.prefix, this.xmlnsBuilder.lookupUriList(), this.xmlnsBuilder.lookupPrefixList(),
					children, cursor.attributes);

			// nextCursor.children.add(elementNode);
			// if(this.navigationPathFilter != null){
			// if(cursor.id != null){
			// cursor.navigations.add(new EncodeGroup(Arrays.asList(cursor.id)));
			// }
			// for(final EncodeGroup navigation: cursor.navigations){
			// navigation.items.add(elementNode);
			// nextCursor.navigations.add(navigation);
			// }
			// }
			this.navigationStack.remove();
			this.cursorStack = nextCursor;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
			this.commitValue();

			final int position = this.cursorStack.children.size();
			this.navigationStack.append(uri, name, position);
			final EncodeValue id = null;
			final int size = atts.getLength();
			final List<EncodeAttributeNode> attributes = new ArrayList<EncodeAttributeNode>(size);
			final EncodeAttributeNodePool attributeNodePool = this.document.attributeNodePool;

			final EncodeNavigationPathFilter filter = this.navigationPathFilter;

			for(int i = 0; i < size; i++){
				final String uri2 = atts.getURI(i);

				String prefix = this.xmlnsBuilder.lookupPrefix(uri2);
				if(prefix == null){
					this.xmlnsBuilder.append(uri2, prefix = "");
				}
				final EncodeAttributeNode attributeNode = this.document.createAttributeNode(uri2, atts.getLocalName(i), prefix, atts.getValue(i));
				attributes.add(attributeNode);
				if((filter != null) && filter.isId(new EncodeAttributePath(this.navigationStack, atts, i))){
					this.document.createNavigationEntry(attributeNode.value.string, this.navigationStack.pathIndexStack);
				}
			}
			if(size > 1){
				Collections.sort(attributes, Encoder2.AttributeNameUriComparator);
			}
			final List<EncodeXmlnsLabel> u = this.xmlnsBuilder.lookupUriList();
			final List<EncodeXmlnsLabel> v = this.xmlnsBuilder.lookupPrefixList();

			System.out.println(v);

			this.cursorStack = new CursorStack(id, uri, name, this.xmlnsBuilder.lookupPrefix(uri), attributes, this.cursorStack);
		}

		void createId(final CursorStack cursor, final String id, final int position) {

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endDocument() {
			final CursorStack cursor = this.cursorStack;
			// int p = navigationStack.

			this.document.documentChildren = this.document.elementChildrenPool.unique(cursor.children);
			// final EncodeGroupPool navigationPathPool = this.document.navigationPathPool;
			// for(final EncodeGroup navigation: cursor.navigations){
			// final List<EncodeItem> source = navigation.items;
			// final int size = source.size();
			// final List<EncodeItem> target = new ArrayList<EncodeItem>(size);
			// target.add(source.get(0));
			// EncodeGroup nodes = this.document.documentChildren;
			// EncodeElementNode parentNode = (EncodeElementNode)source.get(--index);
			// for(int i = 1; i < size; i++){
			// final EncodeElementNode node = (EncodeElementNode)source.get(--index);
			// nodes = node.children;
			// target.add(new EncodePosition(parentNode.children.items.indexOf(node)));
			// target.add(node);
			// parentNode = node;
			// }
			// navigationPathPool.unique(target);
			// }
			// cursor.navigations.clear();
			System.out.println(this.document);
		}

		protected void commitValue() {
			final String value = this.valueBuilder.toString();
			this.valueBuilder.setLength(0);
			if(value.isEmpty()) return;
			this.cursorStack.children.add(this.document.valuePool.unique(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startDocument() {
			if(this.cursorStack != null) throw new IllegalStateException("document already built");
			this.cursorStack = new CursorStack();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void characters(final char[] ch, final int start, final int length) {
			this.valueBuilder.append(ch, start, length);
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
			this.cursorStack.children.add(this.document.createReferenceNode(name));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void processingInstruction(final String target, final String data) {
			this.commitValue();
			this.cursorStack.children.add(this.document.createInstructionNode(target, data));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setDocumentLocator(final Locator locator) {
		}

	}

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von {@link EncodeXmlnsLabel}s.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeXmlnsLabel> LabelHasher = new Comparable<EncodeXmlnsLabel>() {

		@Override
		public int compareTo(final EncodeXmlnsLabel value) {
			return Coder.hashLabel(value.uri.index, value.prefix.index);
		}

	};

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von {@link EncodeValue}s.
	 * 
	 * @see Coder#hashString(String)
	 * @see Encoder2#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeValue> ValueHasher = new Comparable<EncodeValue>() {

		@Override
		public int compareTo(final EncodeValue value) {
			return Coder.hashString(value.string);
		}

	};

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von {@link EncodeGroup}s über den des erten Elements ({@link EncodeValue}).
	 * 
	 * @see Encoder2#ValueHasher
	 * @see Encoder2#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeGroup> GroupHasher = new Comparable<EncodeGroup>() {

		@Override
		public int compareTo(final EncodeGroup value) {
			return Encoder2.ValueHasher.compareTo((EncodeValue)value.items.get(0));
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeItem}s nach ihrem {@link EncodeItem#getIndex()}.
	 */
	protected static final Comparator<EncodeItem> IndexComparator = new Comparator<EncodeItem>() {

		@Override
		public int compare(final EncodeItem o1, final EncodeItem o2) {
			return o1.index - o2.index;
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeXmlnsLabel}s primär nach ihrer {@link EncodeXmlnsLabel#getUri()} und sekundär nach ihrem {@link EncodeXmlnsLabel#getPrefix()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeXmlnsLabel> XmlnsUriPrefixComparator = new Comparator<EncodeXmlnsLabel>() {

		@Override
		public int compare(final EncodeXmlnsLabel o1, final EncodeXmlnsLabel o2) {
			final int comp = Encoder2.ValueComparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder2.ValueComparator.compare(o1.prefix, o2.prefix);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeXmlnsLabel}s primär nach ihrem {@link EncodeXmlnsLabel#getPrefix()} und sekundär nach ihrer {@link EncodeXmlnsLabel#getUri()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeXmlnsLabel> XmlnsPrefixUriComparator = new Comparator<EncodeXmlnsLabel>() {

		@Override
		public int compare(final EncodeXmlnsLabel o1, final EncodeXmlnsLabel o2) {
			final int comp = Encoder2.ValueComparator.compare(o1.prefix, o2.prefix);
			if(comp != 0) return comp;
			return Encoder2.ValueComparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeValue}s nach ihrem {@link EncodeValue#getString()}.
	 */
	protected static final Comparator<EncodeValue> ValueComparator = new Comparator<EncodeValue>() {

		@Override
		public int compare(final EncodeValue o1, final EncodeValue o2) {
			return Comparators.compare(o1.string, o2.string);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeGroup}s nach ihrem ersten {@link EncodeItem} via {@link Encoder2#IndexComparator}.
	 */
	protected static final Comparator<EncodeGroup> GroupComparator = new Comparator<EncodeGroup>() {

		@Override
		public int compare(final EncodeGroup o1, final EncodeGroup o2) {
			return Encoder2.IndexComparator.compare(o1.items.get(0), o2.items.get(0));
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttributeNode}s primär nach dem {@link EncodeAttributeLabel#getName()} und sekundär nach der {@link EncodeAttributeLabel#getUri()} der {@link EncodeAttributeNode#getLabel()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeAttributeNode> AttributeNameUriComparator = new Comparator<EncodeAttributeNode>() {

		@Override
		public int compare(final EncodeAttributeNode value1, final EncodeAttributeNode value2) {
			final int comp = Encoder2.ValueComparator.compare(value1.label.name, value2.label.name);
			if(comp != 0) return comp;
			return Encoder2.ValueComparator.compare(value1.label.uri, value2.label.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link EncodeNavigationPathFilter}, der die {@code ID}-{@link Attr}s an ihrem {@code Type} erkennt.
	 * 
	 * @see EncodeAttributePath#attributeType()
	 */
	protected static final EncodeNavigationPathFilter NavigationPathFilter = new EncodeNavigationPathFilter() {

		@Override
		public boolean isId(final EncodeAttributePath attributePath) {
			return "ID".equals(attributePath.attributeType());
		}

		@Override
		public boolean isId(final EncodeElementPath elementPath) {
			return false;
		}

		@Override
		public Iterable<String> getIds(final EncodeNavigationPath navigationPath) {
			return Iterables.voidIterable();
		}

	};

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
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
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
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		Encoder2.writeInts(target, values.size());
		for(final EncodeItem item: values){
			Encoder2.write(item, target);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeList}s ihre aufsummierten Längen, die Summe aller Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>size|offset[0]|...|offset[size]|values[0]|...|values[size-1]
	 * offset[0] = 0
	 * offset[i+1] = offset[i] + values[i].length</pre>
	 * 
	 * @see EncodeList#getLength()
	 * @see Encoder2#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeList}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeList}- {@link List} {@code null} ist.
	 */
	protected static void writeLists(final EncodeTarget target, final List<? extends EncodeList> values) throws IOException, NullPointerException {
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		Encoder2.writeInts(target, values.size());
		final int size = values.size();
		int offset = 0;
		final int[] value = new int[size + 1];
		for(int i = 0; i < size; i++){
			value[i] = offset;
			offset += values.get(i).getLength();
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
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		final int size = values.size();
		if(size == 0) return;
		final int[] value = new int[size];
		for(int i = 0; i < size; i++){
			value[i] = values.get(i).index;
		}
		Encoder2.writeInts(target, value);
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link EncodePool}s in eine neue {@link List} ein, sortiert diese {@link List} mit dem gegebenen {@link Comparator}, setzt den Index der Elemente unter Beachtung der gegebenen Verschiebung, entfernt alle Elemente aus dem {@link EncodePool} und gibt die {@link List} zurück. Der Index des {@code i}-ten Elements ergibt sich aus:
	 * 
	 * <pre>values[i].index = i + offset</pre>
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @see EncodeItem#index
	 * @see EncodePool#clear()
	 * @see Collections#sort(List, Comparator)
	 * @param values {@link EncodePool} mit den Elementen.
	 * @param offset Verschiebung der Indizes.
	 * @param helper {@link Comparator} zum Sortieren.
	 * @return {@link EncodeItem}-{@link List}.
	 * @throws NullPointerException Wenn der gegebene {@link EncodePool} bzw. der gegebene {@link Comparator} {@code null} ist.
	 */
	protected static <GItem extends EncodeItem> List<GItem> compilePool(final EncodePool<? extends GItem> values, final int offset,
		final Comparator<? super GItem> helper) throws NullPointerException {
		if(values == null) throw new NullPointerException("values is null");
		if(helper == null) throw new NullPointerException("helper is null");
		final List<GItem> list = new ArrayList<GItem>(values.size());
		Iterators.appendAll(list, values.iterator());
		values.clear();
		Collections.sort(list, helper);
		for(int i = 0, size = list.size(); i < size; i++){
			list.get(i).index = i + offset;
		}
		return list;
	}

	/**
	 * Diese Methode erzeugt eine {@link List} aus {@link EncodeGroup}s als {@code Hash-Table} für die gegebenen Elemente und gibt diese zurück. Der {@link Object#hashCode() Streuwert} der Elemente wird mit Hilfe des gegebenen {@link Comparable}s berechnet.
	 * 
	 * @see #ValueHasher
	 * @see #LabelHasher
	 * @param <GItem> Typ der Elemente.
	 * @param values {@link List} mit den Elementen.
	 * @param enabled {@code true}, wenn die {@code Hash-Table} erzeugt werden soll.
	 * @param helper {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts}.
	 * @return {@link EncodeGroup}-{@link List} als {@code Hash-Table} (möglicherweise leer).
	 * @throws NullPointerException Wenn die gegebene {@link List} bzw. das gegebene {@link Comparable} {@code null} ist.
	 */
	protected static <GItem extends EncodeItem> List<EncodeGroup> compileHash(final List<? extends GItem> values, final boolean enabled,
		final Comparable<? super GItem> helper) throws NullPointerException {
		if(values == null) throw new NullPointerException("values is null");
		if(helper == null) throw new NullPointerException("helper is null");
		if(!enabled) return Collections.emptyList();
		final int size = values.size();
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
			final GItem value = values.get(i);
			final int index = helper.compareTo(value) & count;
			list.get(index).items.add(value);
		}
		return list;
	}

	/**
	 * Dieses Feld speichert die {@code Uri-Hash}-Aktivierung.
	 */
	protected boolean uriHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Value-Hash}-Aktivierung.
	 */
	protected boolean valueHashEnabled = true;

	/**
	 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
	 */
	protected boolean xmlnsEnabled = true;

	/**
	 * Dieses Feld speichert die {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	protected boolean xmlnsNameHashEnabled = true;

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
	protected EncodeNavigationPathFilter navigationPathFilter = Encoder2.NavigationPathFilter;

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
		return this.valueHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Value-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#getValuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#valueHash()
	 * @param value {@code Value-Hash}-Aktivierung.
	 */
	public void setValueHashEnabled(final boolean value) {
		this.valueHashEnabled = value;
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
		return this.uriHashEnabled;
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
		this.uriHashEnabled = value;
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
		return this.xmlnsNameHashEnabled;
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
		this.xmlnsNameHashEnabled = value;
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
		this.navigationPathFilter = ((value == null) ? Encoder2.NavigationPathFilter : value);
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
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
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
		if(reader == null) throw new NullPointerException("reader is null");
		if(source == null) throw new NullPointerException("source is null");
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
		System.out.println(source);
		// if(source == null) throw new NullPointerException("source is null");
		// if(target == null) throw new NullPointerException("target is null");
		// final List<EncodeValue> uriPool = Encoder2.compilePool(source.getXmlnsUriPool(), 0, Encoder2.ValueComparator);
		// final List<EncodeGroup> uriHash = Encoder2.compileHash(uriPool, this.uriHashEnabled, Encoder2.ValueHasher);
		// final List<EncodeValue> valuePool = Encoder2.compilePool(source.getValuePool(), 0, Encoder2.ValueComparator);
		// final List<EncodeGroup> valueHash = Encoder2.compileHash(valuePool, this.valueHashEnabled, Encoder2.ValueHasher);
		// final List<EncodeValue> xmlnsNamePool = Encoder2.compilePool(source.getXmlnsPrefixPool(), 0, Encoder2.ValueComparator);
		// final List<EncodeGroup> xmlnsNameHash = Encoder2.compileHash(xmlnsNamePool, this.xmlnsNameHashEnabled, Encoder2.ValueHasher);
		// final List<EncodeXmlnsLabel> xmlnsLabelPool = Encoder2.compilePool(source.getXmlnsLabelPool(), 0, Encoder2.XmlnsPrefixUriComparator);
		// final List<EncodeGroup> xmlnsLabelHash = Encoder2.compileHash(xmlnsLabelPool, this.xmlnsLabelHashEnabled, Encoder2.LabelHasher);
		// final List<EncodeValue> elementNamePool = Encoder2.compilePool(source.getElementNamePool(), 0, Encoder2.ValueComparator);
		// final List<EncodeGroup> elementNameHash = Encoder2.compileHash(elementNamePool, this.elementNameHashEnabled, Encoder2.ValueHasher);
		// final List<EncodeXmlnsLabel> elementLabelPool = Encoder2.compilePool(source.getElementLabelPool(), 0, Encoder2.XmlnsPrefixUriComparator);
		// final List<EncodeGroup> elementLabelHash = Encoder2.compileHash(elementLabelPool, this.elementLabelHashEnabled, Encoder2.LabelHasher);
		// final List<EncodeValue> attributeNamePool = Encoder2.compilePool(source.getAttributeNamePool(), 0, Encoder2.ValueComparator);
		// final List<EncodeGroup> attributeNameHash = Encoder2.compileHash(attributeNamePool, this.attributeNameHashEnabled, Encoder2.ValueHasher);
		// final List<EncodeXmlnsLabel> attributeLabelPool = Encoder2.compilePool(source.getAttributeLabelPool(), 0, Encoder2.XmlnsPrefixUriComparator);
		// final List<EncodeGroup> attributeLabelHash = Encoder2.compileHash(attributeLabelPool, this.attributeLabelHashEnabled, Encoder2.LabelHasher);
		// final List<EncodeGroup> elementXmlnsPool = Encoder2.compilePool(source.elementXmlnsPool(), 0, Encoder2.IndexComparator);
		// final List<EncodeGroup> elementChildrenPool = Encoder2.compilePool(source.getElementChildrenPool(), 0, Encoder2.IndexComparator);
		// final List<EncodeGroup> elementAttributesPool = Encoder2.compilePool(source.getElementAttributesPool(), 0, Encoder2.IndexComparator);
		// final List<EncodeElementNode> elementNodePool = Encoder2.compilePool(source.getElementNodePool(), valuePool.size(), Encoder2.IndexComparator);
		// final List<EncodeAttributeNode> attributeNodePool = Encoder2.compilePool(source.getAttributeNodePool(), 0, Encoder2.IndexComparator);
		// final List<EncodeGroup> navigationPathPool = Encoder2.compilePool(source.getNavigationPathPool(), 0, Encoder2.GroupComparator);
		// final List<EncodeGroup> navigationPathHash = Encoder2.compileHash(navigationPathPool, this.navigationPathHashEnabled, Encoder2.GroupHasher);
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
		if(reader == null) throw new NullPointerException("reader is null");
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
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
		if(reader == null) throw new NullPointerException("reader is null");
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
		final EncodeDocumentHandler adapter = new EncodeDocumentHandler(target, this.xmlnsEnabled, (this.navigationPathEnabled ? this.navigationPathFilter : null));
		reader.setContentHandler(adapter);
		reader.parse(source);
	}

}
