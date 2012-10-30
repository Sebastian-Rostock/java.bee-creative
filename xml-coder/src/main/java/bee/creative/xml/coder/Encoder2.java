package bee.creative.xml.coder;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import bee.creative.util.Comparators;
import bee.creative.util.Hash;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Unique;
import bee.creative.xml.coder.Decoder.DecodeDocument;

/**
 * Diese Klasse implementiert Klassen und Methoden zur Kodierung eines XML-Dokuments in eine optimierte binäre Darstellung.
 * <p>
 * Als Eingabe werden ein {@link XMLReader} und eine {@link InputSource} verwendet, wobei das {@link EncodeDocumentHandler} als {@link ContentHandler} die vom {@link XMLReader} gelesenen Daten aufnimmt. Die eingelesenen Daten des {@link EncodeDocumentHandler}s werden anschließend unter beachtung der Optionen des {@link Encoder2}s in ein {@link EncodeTarget} gespeichert.
 * 
 * @see Decoder
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Encoder2 {

	public static void main(final String[] args) {

		final EncodeDocument document = new EncodeDocument();

		document.builder() //

			.createElement("", "") //
			.createXmlns("", "") //
			.createReference("") //
			.commit() //
			.createComment("") //
			.commit();

		// element: 1
		// text:
		// PI:
		// entity

	}

	public static class EncodeBuilder<GThiz extends EncodeBuilder<?>> {

		protected List<Object> children = new ArrayList<Object>();

		protected List<List<EncodeItem>> navigations = new ArrayList<List<EncodeItem>>();

		protected EncodeDocument document;

		protected GThiz thiz() {
			return null;
		}

		Map<String, List<EncodeItem>> idPathMap = new HashMap<String, List<EncodeItem>>(0);

		protected boolean idTest(final String id) {
			return this.idPathMap.containsKey(id);
		}

		protected Iterable<String> prefixes() {
			return Arrays.asList(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.XML_NS_PREFIX);
		}

		/**
		 * Diese Methode gibt die {@code Uri} zum gegebenen {@code Prefix} oder {@code null} zurück.
		 * 
		 * @param prefix {@code Prefix}.
		 * @return {@code Uri} zum gegebenen {@code Prefix} oder {@code null}.
		 */
		protected String lookupUri(final String prefix) {
			if(XMLConstants.XML_NS_PREFIX.equals(prefix)) return XMLConstants.XML_NS_URI;
			if(XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) return XMLConstants.NULL_NS_URI;
			return null;
		}

		protected String lookupPrefix(final String uri) {
			if(XMLConstants.XML_NS_URI.equals(uri)) return XMLConstants.XML_NS_PREFIX;
			if(XMLConstants.NULL_NS_URI.equals(uri)) return XMLConstants.DEFAULT_NS_PREFIX;
			return null;
		}

		protected void assertNotNull(final Object string) throws IllegalStateException {
			if(string == null) throw new IllegalStateException();
		}

		protected void assertNotEmpty(final String string) throws IllegalStateException {
			this.assertNotNull(string);
			if(string.isEmpty()) throw new IllegalStateException();
		}

		protected void check() throws IllegalStateException {
			if(this.children == null) throw new IllegalStateException("already committed");
		}

		protected List<EncodeItem> commitChildren() throws IllegalStateException {
			this.check();
			final List<Object> source = this.children;
			final int size = source.size();
			final List<EncodeItem> target = new ArrayList<EncodeItem>(size);
			for(int i = 0; i < size; i++){
				final Object item = source.get(i);
				if(item instanceof EncodeElementBuilder<?>){
					final EncodeElementBuilder<?> builder = (EncodeElementBuilder<?>)item;
					final EncodeElement element = builder.commitResult();
					source.set(i, element);
					target.add(element);
				}else{
					target.add((EncodeItem)item);
				}
			}
			return target;
		}

		public EncodeElementBuilder<GThiz> createElement(final String name) throws IllegalStateException {
			return this.createElement(XMLConstants.NULL_NS_URI, name);
		}

		public EncodeElementBuilder<GThiz> createElement(final String uri, final String name) throws IllegalStateException {
			this.check();
			this.assertNotNull(uri);
			this.assertNotEmpty(name);
			final EncodeElementBuilder<GThiz> builder = new EncodeElementBuilder<GThiz>(this.thiz(), uri, name);
			this.children.add(builder);
			return builder;
		}

		/**
		 * Diese Methode fügt den {@link EncodeComment} mit dem gegebenen Wert als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#uniqueComment(String)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public GThiz createComment(final String value) throws NullPointerException, IllegalStateException {
			this.check();
			this.children.add(this.document.uniqueComment(value));
			return this.thiz();
		}

		/**
		 * Diese Methode fügt die {@link EncodeInstruction} mit den gegebenen Eigenschaften als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#uniqueInstruction(String, String)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 * @throws IllegalArgumentException Wenn der gegebene Name leer ist.
		 */
		public GThiz createInstruction(final String name, final String value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			this.check();
			this.children.add(this.document.uniqueInstruction(name, value));
			return this.thiz();
		}

	}

	public static class EncodeElementBuilder<GOwner extends EncodeBuilder<?>> extends EncodeBuilder<EncodeElementBuilder<GOwner>> {

		String uri;

		String name;

		Map<String, String> lookupPrefixMap;

		Map<String, String> lookupUriMap;

		GOwner owner;

		protected List<EncodeLabel> xmlns = new ArrayList<EncodeLabel>();

		protected List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>();

		EncodeElement result;

		public EncodeElementBuilder(final GOwner owner, final String _uri, final String _name) {
			this.owner = owner;
			this.uri = _uri;
			this.name = _name;
		}

		@Override
		protected EncodeElementBuilder<GOwner> thiz() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String lookupUri(final String prefix) {
			if(this.lookupUriMap == null) return this.owner.lookupPrefix(prefix);
			final String uri = this.lookupUriMap.get(prefix);
			if(uri != null) return uri;
			return this.owner.lookupPrefix(prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String lookupPrefix(final String uri) {
			if(this.lookupPrefixMap == null) return this.owner.lookupPrefix(uri);
			final String prefix = this.lookupPrefixMap.get(uri);
			if(prefix != null) return prefix;
			return this.owner.lookupPrefix(uri);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Iterable<String> prefixes() {
			if(this.lookupUriMap == null) return this.owner.prefixes();
			return Iterables.chainedIterable(this.owner.prefixes(), this.lookupUriMap.keySet());
		}

		public EncodeElementBuilder<GOwner> createId(final String value) {
			this.check();
			this.assertNotEmpty(value);

			return this.thiz();
		}

		/**
		 * Diese Methode fügt den {@link EncodeText} mit dem gegebenen Wert als letzten Kindknoten an und gibt {@code this} zurück.
		 * 
		 * @see EncodeDocument#uniqueText(String)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public EncodeElementBuilder<GOwner> createText(final String value) throws NullPointerException, IllegalStateException {
			this.check();
			this.children.add(this.document.uniqueText(value));
			return this.thiz();
		}

		/**
		 * Diese Methode erzeugt ein neues {@code Uri/Prefix}-{@link EncodeLabel} und gibt {@code this} zurück.
		 * 
		 * @see EncodeElement#label()
		 * @see EncodeElementLabel#lookupUriList()
		 * @see EncodeElementLabel#lookupPrefixList()
		 * @param uri {@code Uri}.
		 * @param prefix {@code Prefix}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalStateException Wenn dieser {@link EncodeBuilder} keine Modifikationen mehr zulässt.
		 */
		public final EncodeElementBuilder<GOwner> createXmlns(final String uri, final String prefix) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			this.check();
			if(uri == null) throw new NullPointerException("uri is null");
			if(prefix == null) throw new NullPointerException("name is null");
			if(this.lookupUriMap == null){
				this.lookupUriMap = new HashMap<String, String>(1);
				this.lookupPrefixMap = new HashMap<String, String>(1);
			}else if(this.lookupUriMap.containsKey(prefix)) throw new IllegalArgumentException("prefix already exists");
			this.lookupUriMap.put(prefix, uri);
			this.lookupPrefixMap.put(uri, prefix);
			return this.thiz();
		}

		public EncodeElementBuilder<GOwner> createAttribute(final String name, final String value) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			return this.createAttribute(XMLConstants.NULL_NS_URI, name, value);
		}

		public EncodeElementBuilder<GOwner> createAttribute(final String uri, final String name, final String value) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			this.check();
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			if(name.isEmpty()) throw new IllegalArgumentException("name is empty");
			final String prefix = this.lookupPrefix(uri);
			if(prefix == null) throw new IllegalArgumentException("uri has no prefix");
			for(final EncodeAttribute attribute: this.attributes){
				final EncodeAttributeLabel label = attribute.label;
				if(label.uri.string.equals(uri) && label.name.string.equals(name)) throw new IllegalArgumentException("attribute already exists");
			}
			final EncodeDocument document = this.document;
			final EncodeValue _uri = document.xmlnsUriPool.unique(uri);
			final EncodeValue _name = document.attributeNamePool.unique(name);
			final EncodeValue _value = document.valuePool.unique(value);
			final EncodeValue _prefix = document.xmlnsNamePool.unique(name);
			final EncodeAttributeLabel _label = document.attributeLabelPool_.get(new EncodeAttributeLabel(_uri, _name, _prefix));
			final EncodeAttribute _attribute = document.attributeNodePool.get(new EncodeAttribute(_label, _value));
			this.attributes.add(_attribute);
			return this.thiz();
		}

		public EncodeElementBuilder<GOwner> createReference(final String name) {
			this.check();
			this.assertNotEmpty(name);
			final EncodeDocument document = this.document;
			final EncodeValue _name = document.referenceNamePool.get(new EncodeValue(name));
			final EncodeReference _reference = document.referenceNodePool.get(new EncodeReference(_name));
			this.children.add(_reference);
			return this.thiz();
		}

		protected EncodeElement commitResult() {
			if(this.result != null) return this.result;
			this.check();

			this.assertNotNull(this.uri);
			this.assertNotEmpty(this.name);

			final Map<String, String> xmlnsMap = new HashMap<String, String>();
			for(final String name: this.prefixes()){
				xmlnsMap.put(name, this.lookupUri(name));
			}
			for(final Entry<String, String> entry: xmlnsMap.entrySet()){
				final String uri = entry.getValue();
				final String name = entry.getKey();

			}
			//

			final EncodeValue _uri = this.document.xmlnsUriPool.get(new EncodeValue(this.uri));
			final EncodeValue _name = this.document.attributeNamePool.get(new EncodeValue(this.name));

			// clean state

			this.lookupPrefixMap = null;
			this.lookupUriMap = null;

			return null;
		}

		public GOwner commit() {
			if(this.result != null) return this.owner;

			final int index = this.owner.children.indexOf(this);
			if(index < 0) throw new IllegalStateException();
			this.owner.children.set(index, this.commitResult());

			return this.owner;
		}

	}

	public static class EncodeDocumentBuilder extends EncodeBuilder<EncodeDocumentBuilder> {

		public EncodeDocumentBuilder() {
			this.document = new EncodeDocument();
		}

		public EncodeDocument commit() {
			if(this.children == null) return this.document;
			final List<EncodeItem> children = this.commitChildren();
			EncodeElement documentElement = null;
			for(final EncodeItem item: children){
				if(item instanceof EncodeElement){
					if(documentElement != null) throw new IllegalStateException();
					documentElement = (EncodeElement)item;
				}
			}
			if(documentElement == null) throw new IllegalStateException();
			final EncodeDocument document = this.document;
			document.documentElement = documentElement;
			document.documentChildren = document.elementChildrenPool.get(new EncodeGroup(children));
			this.children = null;
			return this.document;
		}

	}

	/**
	 * Diese Schnittstelle definiert die Ausgabe eines {@link Encoder2}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeTarget {

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
	 * Diese Schnittstelle definiert Methoden zum Zugriff auf den aktuellen Navifationspfad beim Einlesen eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeElementPath extends EncodeNavigationPath {

		/**
		 * Diese Methode gibt die {@code Uri} des {@link Element}s zurück.
		 * 
		 * @see Element#getNamespaceURI()
		 * @return {@code Uri} des {@link Element}s.
		 */
		public String elementUri();

		/**
		 * Diese Methode gibt den {@code Name} des {@link Element}s zurück.
		 * 
		 * @see Element#getLocalName()
		 * @return {@code Name} des {@link Element}s.
		 */
		public String elementName();

	}

	/**
	 * Diese Schnittstelle definiert Methoden zum Zugriff auf den aktuellen Navifationspfad beim Einlesen eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeAttributePath extends EncodeNavigationPath {

		/**
		 * Diese Methode gibt die {@code Uri} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getNamespaceURI()
		 * @see Attributes#getURI(int)
		 * @return {@code Uri} des {@link Attr}s.
		 */
		public String attributeUri();

		/**
		 * Diese Methode gibt den {@code Name} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attributes#getLocalName(int)
		 * @return {@code Name} des {@link Attr}s.
		 */
		public String attributeName();

		/**
		 * Diese Methode gibt den {@code Type} des {@link Attr}s zurück.
		 * 
		 * @see Attr#getSchemaTypeInfo()
		 * @see Attributes#getType(int)
		 * @return {@code Type} des {@link Attr}s.
		 */
		public String attributeType();

	}

	/**
	 * Diese Schnittstelle definiert den Navifationspfad beim Einlesen eines {@link Document}s, bestehend aus {@code Uri}s und {@code Name}s der {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeNavigationPath {

		/**
		 * Diese Methode gibt die {@code Uri} des {@code index}-ten {@link Element}s im Navifationspfad zurück.
		 * 
		 * @param index Index.
		 * @return {@code Uri} des {@code index}-ten {@link Element}s im Navifationspfad.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public String pathUri(int index) throws IndexOutOfBoundsException;

		/**
		 * Diese Methode gibt die {@code Uri}s der {@link Element}s im Navifationspfad als unveränderliche {@link List} zurück.
		 * 
		 * @see Element#getNamespaceURI()
		 * @see Collections#unmodifiableList(List)
		 * @return {@code Path}-{@code Uri}-{@link List}.
		 */
		public List<String> pathUris();

		/**
		 * Diese Methode gibt den {@code Name} des {@code index}-ten {@link Element}s im Navifationspfad zurück.
		 * 
		 * @param index Index.
		 * @return {@code Name} des {@code index}-ten {@link Element}s im Navifationspfad.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public String pathName(int index) throws IndexOutOfBoundsException;

		/**
		 * Diese Methode gibt die {@code Name}s der {@link Element}s im Navifationspfad als unveränderliche {@link List} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Collections#unmodifiableList(List)
		 * @return {@code Path}-{@code Name}-{@link List}.
		 */
		public List<String> pathNames();

		/**
		 * Diese Methode gibt die Länge des Navifationspfads, d.h. die Anzahl seiner {@link Element}s zurück.
		 * 
		 * @return Länge des Navifationspfads.
		 */
		public int pathLength();

	}

	/**
	 * Diese Schnittstelle definiert einen Filter zur Erkennung der {@code ID}-{@link Attr}s bzw. -{@link Element}s, die für die erzeugung des {@link EncodeDocument#navigationPathPool()} verwendet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeNavigationPathFilter {

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das im gegebenen {@link EncodeElementPath} beschriebene {@link Element} als {@code ID} des Navifationspfads verwendet werden soll.
		 * 
		 * @param elementPath {@link EncodeElementPath}.
		 * @return {@code true}, wenn das {@link Element} die {@code ID} des Navifationspfads enthält.
		 */
		public boolean isId(EncodeElementPath elementPath);

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das im gegebenen {@link EncodeAttributePath} beschriebene {@link Attr} als {@code ID} des Navifationspfads verwendet werden soll.
		 * 
		 * @param attributePath {@link EncodeAttributePath}.
		 * @return {@code true}, wenn das {@link Attr} die {@code ID} des Navifationspfads enthält.
		 */
		public boolean isId(EncodeAttributePath attributePath);

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index, der als Element in einem {@link EncodePool} verwendet werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem {

		public static final int TYPE_VALUE = 1;

		public static final int TYPE_LABEL = 2;

		public static final int TYPE_GROUP = 3;

		/**
		 * Dieses Feld speichert das nächste {@link EncodeItem} im {@link EncodePool}.
		 * 
		 * @see EncodePool#getEntryNext(EncodeItem)
		 * @see EncodePool#setEntryNext(EncodeItem, EncodeItem)
		 */
		EncodeItem next;

		/**
		 * Dieses Feld speichert den Index des Datensatzes.
		 */
		int index = 0;

		/**
		 * Diese Methode schreibt den Datensatz in das gegebenen {@link EncodeTarget}.
		 * 
		 * @param target {@link EncodeTarget}.
		 * @throws IOException Wenn das gegebene {@link EncodeTarget} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} {@code null} ist.
		 */
		public abstract void write(final EncodeTarget target) throws IOException, NullPointerException;

		/**
		 * Diese Methode gibt den Index des Datensatzes zurück. Beim Aufbau des {@link EncodePool}s entspricht dieser der absoluten Häufigkeit des Datensatzes im eingelesenen XML-Dokument.
		 * 
		 * @return Index des Datensatzes.
		 */
		public int getIndex() {
			return this.index;
		}

		public void setIndex(final int index) {
			this.index = index;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Hash}-{@code Set}. Wenn das via {@link #get(EncodeItem)} zu einem gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, werden dieses als Rückgabewert verwendet und dessen Wiederverwendung via {@link #insert(EncodeItem)} signalisiert. Das Einfügen eines neuen Elements wird dagegen mit {@link #insert(EncodeItem)} angezeigt. Die Implementation ähnelt einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
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

		protected void reuse(final GItem value) {
		}

		/**
		 * Diese Methode wird beim Einfügen des gegebenen Elements aufgerufen.
		 * 
		 * @see #get(EncodeItem)
		 * @param value Element.
		 */
		protected void insert(final GItem value) {
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
		 */
		public GItem get(final GItem key) throws NullPointerException {
			if(key == null) throw new NullPointerException();
			final GItem value = this.findEntry(key);
			if(value == null){
				this.insert(key);
				this.appendEntry(key, key, true);
				return key;
			}
			this.reuse(value);
			return value;
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
		public abstract int length();

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} zur Abstraktion der Position eines Kindknoten.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodePosition extends EncodeItem {

		/**
		 * Dieser Konstrukteur initialisiert den Index.
		 * 
		 * @param index Index.
		 */
		public EncodePosition(final int index) {
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return String.valueOf(this.index);
		}

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
		protected String string;

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
		 * Diese Methode gibt den {@link String} zurück.
		 * 
		 * @return {@link String}.
		 */
		public String string() {
			return this.string;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return Coder.encodeChars(this.string).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			final byte[] bytes = Coder.encodeChars(this.string);
			target.write(bytes, 0, bytes.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "[" + this.index + "]" + Objects.toString(this.string);
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
		 * @see EncodeValue#string()
		 * @param value {@code String}.
		 * @return einzigartiger {@link EncodeValue}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue unique(final String value) throws NullPointerException {
			return this.get(new EncodeValue(value));
		}

	}

	public static class EncodeXmlnsLabelPool extends EncodeLabelPool {

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} mit {@code Uri} und {@code Name}.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabel extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Uri}.
		 */
		protected final EncodeValue uri;

		/**
		 * Dieses Feld speichert den {@code Name}.
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert {@code Uri} und {@code Name}.
		 * 
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeLabel(final EncodeValue uri, final EncodeValue name) throws NullPointerException {
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			this.uri = uri;
			this.name = name;
		}

		/**
		 * Diese Methode gibt die {@code Uri} zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@code Uri}.
		 */
		public EncodeValue uri() {
			return this.uri;
		}

		/**
		 * Diese Methode gibt den {@code Name} zurück.
		 * 
		 * @see Node#getLocalName()
		 * @return {@code Name}.
		 */
		public EncodeValue name() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.uri.index, this.name.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, "EncodeLabel", "index", this.index, "uri", this.uri, "name", this.name);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabelPool extends EncodePool<EncodeLabel> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeLabel input) {
			return Objects.hash(input.uri, input.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeLabel input1, final EncodeLabel input2) {
			return (input1.name == input2.name) && (input1.uri == input2.uri);
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
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List}.
		 */
		protected final List<EncodeItem> items;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeGroup() {
			this.items = new ArrayList<EncodeItem>();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList} und den gegebenen {@link EncodeItem}s.
		 * 
		 * @param items {@link EncodeItem}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup(final List<? extends EncodeItem> items) throws NullPointerException {
			if(items == null) throw new NullPointerException("items is null");
			if(items.contains(null)) throw new NullPointerException("items contains null");
			this.items = new ArrayList<EncodeItem>(items);
		}

		/**
		 * Diese Methode gibt die {@link EncodeItem}-{@link List} zurück.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return {@link EncodeItem}-{@link List}.
		 */
		public List<EncodeItem> items() {
			return Collections.unmodifiableList(this.items);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.items.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeIndices(target, this.items);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeGroup", "index", this.index, "items", this.items);
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
			if(input1 == input2) return true;
			final List<?> items1 = input1.items, items2 = input2.items;
			if(items1.size() != items2.size()) return false;
			final Iterator<?> iterator1 = items1.iterator(), iterator2 = items2.iterator();
			while(iterator1.hasNext()){
				if(!iterator2.hasNext()) return false; // nur bei fehlerhafter size!
				if(iterator1.next() != iterator2.next()) return false;
			}
			return !iterator2.hasNext();
		}

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeGroup} mit den gegebenen {@link EncodeItem}s zurück.
		 * 
		 * @see EncodeGroup#items()
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeGroup}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup unique(final List<? extends EncodeItem> value) throws NullPointerException {
			return this.get(new EncodeGroup(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link EntityReference}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeText extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Data}-{@link EncodeValue}.
		 * 
		 * @see ProcessingInstruction#getData()
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeValue}s.
		 * 
		 * @param value {@link EncodeValue} für {@link EncodeInstruction#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */

		public EncodeText(final EncodeValue value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link EncodeValue} zurück.
		 * 
		 * @see ProcessingInstruction#getData()
		 * @see ProcessingInstruction#getNodeValue()
		 * @return {@code Value}-{@link EncodeValue}.
		 */
		public EncodeValue value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
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
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeText}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeTextPool extends EncodePool<EncodeText> {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeText#value()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param valuePool {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeTextPool(final EncodeValuePool valuePool) throws NullPointerException {
			if(valuePool == null) throw new NullPointerException();
			this.valuePool = valuePool;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeText input) {
			return Objects.hash(input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeText input1, final EncodeText input2) {
			return input1.value == input2.value;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeText#value()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeText#value()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeText} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeText#value()
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeText}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeText unique(final String value) throws NullPointerException {
			return this.get(new EncodeText(this.valuePool.unique(value)));
		}

		@Override
		protected void insert(final EncodeText value) {
			super.insert(value);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link EntityReference}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeReference extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Data}-{@link EncodeValue}.
		 * 
		 * @see ProcessingInstruction#getData()
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeValue}s.
		 * 
		 * @param name {@link EncodeValue} für {@link EncodeInstruction#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeReference(final EncodeValue name) throws NullPointerException {
			if(name == null) throw new NullPointerException();
			this.name = name;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link EncodeValue} zurück.
		 * 
		 * @see ProcessingInstruction#getData()
		 * @see ProcessingInstruction#getNodeValue()
		 * @return {@code Value}-{@link EncodeValue}.
		 */
		public EncodeValue name() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
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
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeReference}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeReferencePool extends EncodePool<EncodeReference> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeReference input) {
			return Objects.hash(input.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeReference input1, final EncodeReference input2) {
			return input1.name == input2.name;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link EntityReference}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeComment extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Data}-{@link EncodeValue}.
		 * 
		 * @see ProcessingInstruction#getData()
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeValue}s.
		 * 
		 * @param value {@link EncodeValue} für {@link EncodeInstruction#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */

		public EncodeComment(final EncodeValue value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link EncodeValue} zurück.
		 * 
		 * @see ProcessingInstruction#getData()
		 * @see ProcessingInstruction#getNodeValue()
		 * @return {@code Value}-{@link EncodeValue}.
		 */
		public EncodeValue value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
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
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeComment}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeCommentPool extends EncodePool<EncodeComment> {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeComment#value()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param valuePool {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeCommentPool(final EncodeValuePool valuePool) throws NullPointerException {
			if(valuePool == null) throw new NullPointerException();
			this.valuePool = valuePool;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeComment input) {
			return Objects.hash(input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeComment input1, final EncodeComment input2) {
			return input1.value == input2.value;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeComment#value()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeComment#value()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeComment} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeComment#value()
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeComment}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeComment unique(final String value) throws NullPointerException {
			return this.get(new EncodeComment(this.valuePool.unique(value)));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElement extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}-{@link EncodeValue} oder {@code null}.
		 * 
		 * @see Element#getNodeName()
		 */
		protected final EncodeValue name;

		/**
		 * Dieses Feld speichert das {@code Uri/Name}-{@link EncodeLabel} oder {@code null}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		protected final EncodeLabel label;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code Uri/Prefix}-{@link EncodeLabel}s oder {@code null}.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		protected final EncodeGroup xmlns;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code Children}.
		 * 
		 * @see Element#getChildNodes()
		 */
		protected final EncodeGroup children;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code Attributes}.
		 * 
		 * @see Element#getAttributes()
		 */
		protected final EncodeGroup attributes;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @see EncodeElement#name()
		 * @see EncodeElement#children()
		 * @see EncodeElement#attributes()
		 * @param name {@link EncodeValue} für {@link EncodeElement#name()}.
		 * @param children {@link EncodeGroup} für {@link EncodeElement#children()}.
		 * @param attributes {@link EncodeGroup} für {@link EncodeElement#attributes()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElement(final EncodeValue name, final EncodeGroup children, final EncodeGroup attributes) throws NullPointerException {
			if(name == null) throw new NullPointerException("name is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			this.name = name;
			this.label = null;
			this.xmlns = null;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @see EncodeElement#label()
		 * @see EncodeElement#xmlns()
		 * @see EncodeElement#children()
		 * @see EncodeElement#attributes()
		 * @param label {@link EncodeLabel} für {@link EncodeElement#label()}.
		 * @param xmlns {@link EncodeGroup} für {@link EncodeElement#xmlns()}.
		 * @param children {@link EncodeGroup} für {@link EncodeElement#children()}.
		 * @param attributes {@link EncodeGroup} für {@link EncodeElement#attributes()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElement(final EncodeLabel label, final EncodeGroup xmlns, final EncodeGroup children, final EncodeGroup attributes)
			throws NullPointerException {
			if(label == null) throw new NullPointerException("label is null");
			if(xmlns == null) throw new NullPointerException("spaces is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			this.name = null;
			this.label = label;
			this.xmlns = xmlns;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * Diese Methode gibt den {@code Name}-{@link EncodeValue} oder {@code null} zurück.
		 * 
		 * @see Element#getNodeName()
		 * @return {@code Name}-{@link EncodeValue} oder {@code null}.
		 */
		public EncodeValue name() {
			return this.name;
		}

		/**
		 * Diese Methode gibt das {@code Uri/Name}-{@link EncodeLabel} oder {@code null} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return {@code Uri/Name}-{@link EncodeLabel} oder {@code null}.
		 */
		public EncodeLabel label() {
			return this.label;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der {@code Uri/Prefix}-{@link EncodeLabel}s oder {@code null} zurück. Die Elemente dieser {@link EncodeGroup} sind primär nach {@link EncodeLabel#uri()} und sekundär nach {@link EncodeLabel#name()} aufsteigend sortiert.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 * @return {@link EncodeGroup} der {@code Uri/Prefix}-{@link EncodeLabel}s oder {@code null}.
		 */
		public EncodeGroup xmlns() {
			return this.xmlns;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der {@code Children} zurück.
		 * 
		 * @see Element#getChildNodes()
		 * @return {@link EncodeGroup} der {@code Children}.
		 */
		public EncodeGroup children() {
			return this.children;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der {@code Attributes} zurück. Die Elemente dieser {@link EncodeGroup} sind nach {@link EncodeAttribute#name()} bzw. primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} des {@link EncodeAttribute#label()} aufsteigend sortiert.
		 * 
		 * @see Element#getAttributes()
		 * @return {@link EncodeGroup} der {@code Attributes}.
		 */
		public EncodeGroup attributes() {
			return this.attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			if(this.name == null){
				Encoder2.writeInts(target, this.label.index, this.xmlns.index, this.children.index, this.attributes.index);
			}else{
				Encoder2.writeInts(target, this.name.index, this.children.index, this.attributes.index);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.name == null)
				return Objects.toStringCall(false, true, "EncodeElement", "index", this.index, "label", this.label, "xmlns", this.xmlns.index, "attributes",
					this.attributes.index, "children", this.children.index);
			return Objects.toStringCall(false, true, "EncodeElement", "index", this.index, "name", this.name, "attributes", this.attributes.index, "children",
				this.children.index);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeElement}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementPool extends EncodePool<EncodeElement> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElement input) {
			return Objects.hash(input.name, input.label, input.xmlns, input.children, input.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElement input1, final EncodeElement input2) {
			return (input1.name == input2.name) && (input1.label == input2.label) && (input1.xmlns == input2.xmlns) && (input1.children == input2.children)
				&& (input1.attributes == input2.attributes);
		}

	}

	public static abstract class EncodeElementLabel extends EncodeAttributeLabel {

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code Uri/Prefix}-{@link EncodeLabel}s.
		 * 
		 * @see Element#lookupNamespaceURI(String)
		 */
		protected EncodeGroup lookupUriList;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code Uri/Prefix}-{@link EncodeLabel}s.
		 * 
		 * @see Element#lookupPrefix(String)
		 */
		protected EncodeGroup lookupPrefixList;

		public EncodeElementLabel(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix, final EncodeGroup lookupUriList,
			final EncodeGroup lookupPrefixList) throws NullPointerException {
			super(uri, name, prefix);
			if(lookupUriList == null) throw new NullPointerException("lookupUriList is null");
			if(lookupPrefixList == null) throw new NullPointerException("lookupPrefixList is null");
			this.lookupUriList = lookupUriList;
			this.lookupPrefixList = lookupPrefixList;
		}

		public EncodeGroup lookupUriList() {
			return this.lookupUriList;
		}

		public EncodeGroup lookupPrefixList() {
			return this.lookupPrefixList;
		}

		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.uri.index, this.name.index, this.prefix.index, this.lookupUriList.index, this.lookupPrefixList.index);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttribute extends EncodeItem {

		/**
		 * Dieses Feld speichert das {@code Uri/Name/Prefix}-{@link EncodeAttributeLabel}.
		 * 
		 * @see Attr#getPrefix()
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		protected final EncodeAttributeLabel label;

		/**
		 * Dieses Feld speichert den {@code Value}-{@link EncodeValue}.
		 * 
		 * @see Attr#getNodeValue()
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param label {@link EncodeAttributeLabel} für {@link EncodeAttribute#label()}.
		 * @param value {@link EncodeValue} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttribute(final EncodeAttributeLabel label, final EncodeValue value) throws NullPointerException {
			if(label == null) throw new NullPointerException("label is null");
			if(value == null) throw new NullPointerException("value is null");
			this.label = label;
			this.value = value;
		}

		/**
		 * Diese Methode gibt das {@code Uri/Name/Prefix}-{@link EncodeAttributeLabel} zurück.
		 * 
		 * @see Attr#getPrefix()
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@code Uri/Name/Prefix}-{@link EncodeAttributeLabel}.
		 */
		public EncodeLabel label() {
			return this.label;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link EncodeValue} zurück.
		 * 
		 * @see Attr#getNodeValue()
		 * @return {@code Value}-{@link EncodeValue}.
		 */
		public EncodeValue value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.label.index, this.value.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, "EncodeAttribute", "index", this.index, "label", this.label, "value", this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeAttribute}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributePool extends EncodePool<EncodeAttribute> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeAttribute input) {
			return Objects.hash(input.label, input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeAttribute input1, final EncodeAttribute input2) {
			return (input1.label == input2.label) && (input1.value == input2.value);
		}

	}

	public static class EncodeAttributeLabel extends EncodeLabel {

		/**
		 * Dieses Feld speichert den {@code Prefix}.
		 * 
		 * @see Attr#getPrefix()
		 */
		protected final EncodeValue prefix;

		/**
		 * Dieser Konstrukteur initialisiert {@code Uri}, {@code Name} und {@code Prefix}.
		 * 
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix) throws NullPointerException {
			super(uri, name);
			if(prefix == null) throw new NullPointerException("prefix is null");
			this.prefix = prefix;
		}

		/**
		 * Diese Methode gibt den {@code Prefix} zurück.
		 * 
		 * @see Attr#getPrefix()
		 * @return {@code Name}.
		 */
		public EncodeValue prefix() {
			return this.prefix;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.uri.index, this.name.index, this.prefix.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, "EncodeAttributeLabel", "index", this.index, "uri", this.uri, "name", this.name, "prefix", this.prefix);
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
		 * Diese Methode gibt das einzigartige {@link EncodeAttributeLabel} zum gegebenen {@code String} zurück.
		 * 
		 * @param uri {@code Uri}.
		 * @param name {@code Name}.
		 * @param prefix {@code Prefix}.
		 * @return einzigartiges {@link EncodeAttributeLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributeLabel unique(final EncodeValue uri, final EncodeValue name, final EncodeValue prefix) throws NullPointerException {
			return get(new EncodeAttributeLabel(uri, name, prefix));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion einer {@link ProcessingInstruction}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeInstruction extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}-{@link EncodeValue}.
		 * 
		 * @see ProcessingInstruction#getTarget()
		 */
		protected final EncodeValue name;

		/**
		 * Dieses Feld speichert den {@code Data}-{@link EncodeValue}.
		 * 
		 * @see ProcessingInstruction#getData()
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeValue}s.
		 * 
		 * @param name {@link EncodeValue} für {@link EncodeInstruction#name()}.
		 * @param value {@link EncodeValue} für {@link EncodeInstruction#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */

		public EncodeInstruction(final EncodeValue name, final EncodeValue value) throws NullPointerException {
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			this.name = name;
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@code Name}-{@link EncodeValue} zurück.
		 * 
		 * @see ProcessingInstruction#getTarget()
		 * @see ProcessingInstruction#getNodeName()
		 * @return {@code Name}-{@link EncodeValue}.
		 */
		public EncodeValue name() {
			return this.name;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link EncodeValue} zurück.
		 * 
		 * @see ProcessingInstruction#getData()
		 * @see ProcessingInstruction#getNodeValue()
		 * @return {@code Value}-{@link EncodeValue}.
		 */
		public EncodeValue value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
			Encoder2.writeInts(target, this.name.index, this.value.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "<?[" + this.index + "]" + this.name + " " + this.value + "?>";
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeInstruction}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeInstructionPool extends EncodePool<EncodeInstruction> {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeInstruction#value()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param valuePool {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeInstructionPool(final EncodeValuePool valuePool) throws NullPointerException {
			if(valuePool == null) throw new NullPointerException();
			this.valuePool = valuePool;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeInstruction input) {
			return Objects.hash(input.name, input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeInstruction input1, final EncodeInstruction input2) {
			return (input1.name == input2.name) && (input1.value == input2.value);
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeInstruction#value()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeInstruction#value()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link EncodePool}s zur Abstraktion eines {@link Document} s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocument {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		protected final EncodeValuePool xmlnsUriPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttribute#value()} und {@link EncodeElement#children()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#name()} bei {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeValuePool xmlnsNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeXmlnsLabelPool xmlnsLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeElement#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeElement#label()}.
		 */
		protected final EncodeValuePool elementNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		protected final EncodeLabelPool elementLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeAttribute#label()}.
		 */
		protected final EncodeValuePool attributeNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		protected final EncodeLabelPool attributeLabelPool;

		protected final EncodeAttributeLabelPool attributeLabelPool_;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeGroupPool elementXmlnsPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 */
		protected final EncodeGroupPool elementChildrenPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 */
		protected final EncodeGroupPool elementAttributesPool;

		protected final EncodeTextPool textNodePool;

		protected final EncodeCommentPool commentNodePool;

		protected final EncodeValuePool instructionNamePool;

		protected final EncodeInstructionPool instructionNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und {@link EncodeElement#children()}.
		 */
		protected final EncodeElementPool elementNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributePool} für {@link EncodeElement#attributes()}.
		 */
		protected final EncodeAttributePool attributeNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		protected final EncodeGroupPool navigationPathPool;

		EncodeValuePool referenceNamePool;

		EncodeReferencePool referenceNodePool;

		protected EncodeGroup documentChildren;

		/**
		 * Dieses Feld speichert das {@link EncodeElement} für {@link Document#getDocumentElement()}.
		 * 
		 * @see Document#getDocumentElement()
		 */
		protected EncodeElement documentElement;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodePool}s.
		 */
		public EncodeDocument() {
			this.xmlnsUriPool = new EncodeValuePool();
			this.valuePool = new EncodeValuePool();
			this.xmlnsNamePool = new EncodeValuePool();
			this.xmlnsLabelPool = new EncodeXmlnsLabelPool();

			this.textNodePool = new EncodeTextPool(this.valuePool);
			this.commentNodePool = new EncodeCommentPool(this.valuePool);
			this.instructionNamePool = new EncodeValuePool();
			this.instructionNodePool = new EncodeInstructionPool(this.valuePool);

			this.elementNamePool = new EncodeValuePool();
			this.elementLabelPool = new EncodeLabelPool();
			this.attributeNamePool = new EncodeValuePool();
			this.attributeLabelPool = new EncodeLabelPool();
			this.elementXmlnsPool = new EncodeGroupPool();
			this.elementChildrenPool = new EncodeGroupPool();
			this.elementAttributesPool = new EncodeGroupPool();
			this.elementNodePool = new EncodeElementPool();
			this.attributeNodePool = new EncodeAttributePool();
			this.navigationPathPool = new EncodeGroupPool();
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeText} mit dem gegebenen Wert zurück.
		 * 
		 * @see EncodeTextPool#get(EncodeText)
		 * @see EncodeText#value()
		 * @param value Wert.
		 * @return {@link EncodeText}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		public EncodeText uniqueText(final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			return this.textNodePool.get(new EncodeText(this.valuePool.unique(value)));
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeComment} mit dem gegebenen Wert zurück.
		 * 
		 * @see EncodeCommentPool#get(EncodeComment)
		 * @see EncodeComment#value()
		 * @param value Wert.
		 * @return {@link EncodeComment}.
		 * @throws NullPointerException Wenn der gegebene Wert {@code null} ist.
		 */
		public EncodeComment uniqueComment(final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			return this.commentNodePool.get(new EncodeComment(this.valuePool.unique(value)));
		}

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeInstruction} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeInstructionPool#get(EncodeInstruction)
		 * @see EncodeInstruction#name()
		 * @see EncodeInstruction#value()
		 * @param name Name.
		 * @param value Wert.
		 * @return {@link EncodeInstruction}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Name leer ist.
		 */
		public EncodeInstruction uniqueInstruction(final String name, final String value) throws NullPointerException, IllegalArgumentException {
			if(name == null) throw new NullPointerException("name is null");
			if(name.isEmpty()) throw new NullPointerException("name is empty");
			if(value == null) throw new NullPointerException("value is null");
			return this.instructionNodePool.get(new EncodeInstruction(this.instructionNamePool.get(new EncodeValue(name)), valuePool.unique(value)));
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeLabel#uri()} zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#string()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		public EncodeValuePool uriPool() {
			return this.xmlnsUriPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttribute#value()} und {@link EncodeElement#children()} zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#string()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 * @return {@link EncodeValuePool} für {@link EncodeAttribute#value()} und {@link EncodeElement#children()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeLabel#name()} bei {@link EncodeElement#xmlns()} zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#string()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getPrefix()
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#name()} bei {@link EncodeElement#xmlns()}.
		 */
		public EncodeValuePool xmlnsNamePool() {
			return this.xmlnsNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeElement#xmlns()} zurück. Im {@link EncodeTarget} werden diese primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @see Node#getPrefix()
		 * @return {@link EncodeLabelPool} für {@link EncodeElement#xmlns()}.
		 */
		public EncodeLabelPool xmlnsLabelPool() {
			return this.xmlnsLabelPool;
		}

		public EncodeTextPool textNodePool() {
			return this.textNodePool;
		}

		public EncodeCommentPool commentNodePool() {
			return this.commentNodePool;
		}

		public EncodeValuePool instructionNamePool() {
			return this.instructionNamePool;
		}

		public EncodeInstructionPool instructionNodePool() {
			return this.instructionNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeElement#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeElement#label()} zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#string()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getLocalName()
		 * @return {@link EncodeValuePool} für {@link EncodeElement#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeElement#label()}.
		 */
		public EncodeValuePool elementNamePool() {
			return this.elementNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeElement#label()} zurück. Im {@link EncodeTarget} werden diese primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		public EncodeLabelPool elementLabelPool() {
			return this.elementLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeAttribute#label()} zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#string()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getNodeName()
		 * @see Attr#getLocalName()
		 * @return {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw. {@link EncodeLabel#name()} bei {@link EncodeAttribute#label()}.
		 */
		public EncodeValuePool attributeNamePool() {
			return this.attributeNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeAttribute#label()} zurück. Im {@link EncodeTarget} werden diese primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		public EncodeLabelPool attributeLabelPool() {
			return this.attributeLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#xmlns()} zurück. Im {@link EncodeTarget} werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 */
		public EncodeGroupPool elementXmlnsPool() {
			return this.elementXmlnsPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#children()} zurück. Im {@link EncodeTarget} werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getChildNodes()
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 */
		public EncodeGroupPool elementChildrenPool() {
			return this.elementChildrenPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#attributes()} zurück. Im {@link EncodeTarget} werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getAttributes()
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 */
		public EncodeGroupPool elementAttributesPool() {
			return this.elementAttributesPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und {@link EncodeElement#children()} zurück.
		 * 
		 * @see Element
		 * @return {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und {@link EncodeElement#children()}.
		 */
		public EncodeElementPool elementNodePool() {
			return this.elementNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeAttributePool} für {@link EncodeElement#attributes()} zurück.
		 * 
		 * @see Attr
		 * @return {@link EncodeAttributePool} für {@link EncodeElement#attributes()}.
		 */
		public EncodeAttributePool attributeNodePool() {
			return this.attributeNodePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link Document#getElementById(String)} zurück.
		 * <p>
		 * Das erste Element jeder {@link EncodeGroup} ist das {@link EncodeValue} mit der {@code ID} des {@link Element}s und die anderen Elemente beschriben als {@link EncodePosition} den Index des {@code Child}-{@link Node}s zur Navigation.
		 * <p>
		 * NEU:<br>
		 * Die ersten beiden Elemente jeder {@link EncodeGroup} definieren als {@link EncodeValue}s den Namen des Inhaltsverzeichnisses sowie die {@code ID} des {@link Element}s. Die folgenden Elemente beschriben mit je einer {@link EncodePosition} und einem {@link EncodeElement} den Index des {@code Child}-{@link Node}s sowie den Index des {@link Element}s zur Navigation in Richtung des Wurzelelements.<br>
		 * Der Pfad root/node3/node9 mit der Id "ID" im Index "INDEX" wird damit zu: <br>
		 * [INDEX, ID, 9, node9, 3, node3, x, root], wobei x der index des wutzelelements im der kondknotenliste des documents ist.
		 * 
		 * @see Attr#isId()
		 * @see Document#getElementById(String)
		 * @return {@link EncodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		public EncodeGroupPool navigationPathPool() {
			return this.navigationPathPool;
		}

		/**
		 * Diese Methode gibt das {@link EncodeElement} für {@link Document#getDocumentElement()} zurück.
		 * 
		 * @see Document#getDocumentElement()
		 * @return {@link EncodeElement} für {@link Document#getDocumentElement()}.
		 */
		public EncodeElement documentElement() {
			return this.documentElement;
		}

		public EncodeGroup documentChildren() {
			return this.documentChildren;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeDocument", //
				"uriPool", this.xmlnsUriPool, //
				"valuePool", this.valuePool, //
				"xmlnsNamePool", this.xmlnsNamePool, //
				"xmlnsLabelPool", this.xmlnsLabelPool, //
				"elementNamePool", this.elementNamePool, //
				"elementLabelPool", this.elementLabelPool, //
				"attributeNamePool", this.attributeNamePool, //
				"attributeLabelPool", this.attributeLabelPool, //
				"elementXmlnsPool", this.elementXmlnsPool, //
				"elementChildrenPool", this.elementChildrenPool, //
				"elementAttributesPool", this.elementAttributesPool, //
				"elementNodePool", this.elementNodePool, //
				"attributeNodePool", this.attributeNodePool, //
				"navigationPathPool", this.navigationPathPool, //
				"documentElement", this.documentElement //
				);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ContentHandler} zum Einlsenen eines {@link Document}s mit Hilfe eines {@link XMLReader}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocumentHandler implements ContentHandler {

		/**
		 * Diese Klasse implementiert einen {@link EncodeElementPath}, dessen Methoden auf einen gegebenen {@link EncodeNavigationPath} sowie ein gegebenes {@link EncodeElement} delegieren.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class ElementPath extends NavigationPath implements EncodeElementPath {

			/**
			 * Dieses Feld speichert das {@link EncodeElement}.
			 */
			protected final EncodeElement element;

			/**
			 * Dieser Konstrukteur initialisiert {@link EncodeNavigationPath} und {@link EncodeElement}.
			 * 
			 * @param path {@link EncodeNavigationPath}.
			 * @param element {@link EncodeElement}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public ElementPath(final EncodeNavigationPath path, final EncodeElement element) throws NullPointerException {
				super(path);
				if(element == null) throw new NullPointerException();
				this.element = element;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String elementUri() {
				return ((this.element.name == null) ? this.element.label.uri.string : "");
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String elementName() {
				return ((this.element.name == null) ? this.element.label.name.string : this.element.name.string);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall("ElementPath", this.pathNames(), this.elementName());
			}

		}

		/**
		 * Diese Klasse implementiert einen {@link EncodeAttributePath}, dessen Methoden auf einen gegebenen {@link EncodeNavigationPath} sowie gegebene {@link Attributes} delegieren.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class AttributePath extends NavigationPath implements EncodeAttributePath {

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
			public AttributePath(final EncodeNavigationPath path, final Attributes atts, final int index) throws NullPointerException {
				super(path);
				if(atts == null) throw new NullPointerException();
				this.atts = atts;
				this.index = index;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String attributeUri() {
				return this.atts.getURI(this.index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String attributeName() {
				return this.atts.getLocalName(this.index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String attributeType() {
				return this.atts.getType(this.index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall("AttributePath", this.pathNames(), this.attributeName());
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten {@link EncodeNavigationPath}, dessen Methoden auf einen gegebenen {@link EncodeNavigationPath} delegieren.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static abstract class NavigationPath implements EncodeNavigationPath {

			/**
			 * Dieses Feld speichert den {@link EncodeNavigationPath}.
			 */
			protected final EncodeNavigationPath path;

			/**
			 * Dieser Konstrukteur initialisiert den {@link EncodeNavigationPath}.
			 * 
			 * @param path {@link EncodeNavigationPath}.
			 * @throws NullPointerException Wenn der gegebene {@link EncodeNavigationPath} {@code null} ist.
			 */
			public NavigationPath(final EncodeNavigationPath path) throws NullPointerException {
				if(path == null) throw new NullPointerException();
				this.path = path;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final String pathUri(final int index) throws IndexOutOfBoundsException {
				return this.path.pathUri(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final List<String> pathUris() {
				return this.path.pathUris();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final String pathName(final int index) throws IndexOutOfBoundsException {
				return this.path.pathName(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final List<String> pathNames() {
				return this.path.pathNames();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final int pathLength() {
				return this.path.pathLength();
			}

		}

		/**
		 * Diese Klasse implementiert den dynamischen {@link EncodeNavigationPath}, der während des Einlesens vom {@link EncodeDocumentHandler} angepasst wird.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class NavigationStack implements EncodeNavigationPath {

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
			 * Dieser Konstrukteur initialisiert die {@link List}s.
			 */
			public NavigationStack() {
				this.pathUriStack = new ArrayList<String>();
				this.pathUris = Collections.unmodifiableList(this.pathUriStack);
				this.pathNameStack = new ArrayList<String>();
				this.pathNames = Collections.unmodifiableList(this.pathNameStack);
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
			public int pathLength() {
				return this.pathUriStack.size();
			}

			/**
			 * Diese Methode fügt das gegebene Segmant aus {@code Uri} und {@code Name} an das Ende des Navigationspfads an.
			 * 
			 * @param pathUri {@code Uri}.
			 * @param pathName {@code Name}.
			 */
			public void append(final String pathUri, final String pathName) {
				final int index = this.pathLength();
				this.pathUriStack.add(index, pathUri);
				this.pathNameStack.add(index, pathName);
			}

			/**
			 * Diese Methode entfernt das letzte Segment des Navigationspfads.
			 */
			public void remove() {
				final int index = this.pathLength() - 1;
				this.pathUriStack.remove(index);
				this.pathNameStack.remove(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall("NavigationStack", this.pathNames);
			}

		}

		/**
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code Uri} und {@code Prefix} als {@link EncodeLabel}s während des Einlesens eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class XmlnsStack {

			/**
			 * Dieses Feld speichert die {@code Uri}-{@code Prefix}-{@link Map}.
			 */
			protected final Map<String, String> map;

			/**
			 * Dieses Feld speichert den nächsten {@link XmlnsStack} oder {@code null}.
			 */
			protected final XmlnsStack next;

			/**
			 * Dieser Konstrukteur initialisiert den leeren {@link XmlnsStack}.
			 */
			public XmlnsStack() {
				this(null);
			}

			/**
			 * Dieser Konstrukteur initialisiert den nächsten {@link XmlnsStack}.
			 * 
			 * @param next nächster {@link XmlnsStack} oder {@code null}.
			 */
			public XmlnsStack(final XmlnsStack next) {
				this.map = new HashMap<String, String>(1);
				this.next = next;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, "EncodeXmlnsStack", this.map, this.next);
			}

		}

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

			/**
			 * Dieses Feld speichert die {@link EncodeElement#xmlns()} oder {@code null}.
			 */
			protected final List<EncodeLabel> xmlns;

			/**
			 * Dieses Feld speichert die {@link EncodeElement#children()}.
			 */
			protected final List<EncodeItem> children;

			/**
			 * Dieses Feld speichert die {@link EncodeElement#attributes()}.
			 */
			protected final List<? extends EncodeItem> attributes;

			/**
			 * Dieses Feld speichert die {@link EncodeGroup}s für den {@link EncodeDocument#navigationPathPool()}.
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
			 * @param attributes {@link EncodeAttribute}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final EncodeValue id, final String name, final List<EncodeAttribute> attributes, final CursorStack next) {
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
			 * @param spaces {@code Uri/Prefix}-{@link EncodeLabel}-{@link List}.
			 * @param attributes {@link EncodeAttribute}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final EncodeValue id, final String uri, final String name, final List<EncodeLabel> spaces, final List<EncodeAttribute> attributes,
				final CursorStack next) {
				this.id = id;
				this.uri = uri;
				this.name = name;
				this.xmlns = spaces;
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
				return Objects.toStringCall(true, true, "EncodeCursorStack", "id", this.id, "uri", this.uri, "name", this.name, "xmlns", this.xmlns, "children",
					this.children, "attributes", this.attributes, "navigations", this.navigations, "next", this.next);
			}

		}

		/**
		 * Dieses Feld speichert den {@link NavigationStack} für den aktuellen Navigationspfad und den {@link EncodeNavigationPathFilter}.
		 */
		protected final NavigationStack pathStack;

		/**
		 * Dieses Feld speichert den {@link XmlnsStack} für die aktuellen Paare aus {@code Uri} und {@code Prefix} oder {@code null}.
		 */
		protected XmlnsStack xmlnsStack;

		/**
		 * Dieses Feld speichert die aktuellen Paare aus {@code Uri} und {@code Prefix}.
		 */
		protected List<EncodeLabel> xmlnsCache;

		/**
		 * Dieses Feld speichert den {@link CursorStack} für das aktuelle {@link Element}.
		 */
		protected CursorStack cursorStack;

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
			if(document == null) throw new NullPointerException();
			this.document = document;
			this.pathStack = new NavigationStack();
			this.cursorStack = new CursorStack();
			this.xmlnsEnabled = xmlnsEnabled;
			this.navigationPathFilter = navigationPathFilter;
			if(!xmlnsEnabled) return;
			this.startPrefixMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
			this.startPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
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
			return this.pathStack;
		}

		/**
		 * Diese Methode gibt den {@link EncodeNavigationPathFilter} oder {@code null} zurück. Wenn er {@code null} ist, wird der {@link EncodeDocument#navigationPathPool()} nicht befüllt.
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
			for(XmlnsStack scope = this.xmlnsStack; scope != null; scope = scope.next){
				if(scope.map.values().remove(prefix)){
					if(scope.map.isEmpty() && (scope == this.xmlnsStack)){
						this.xmlnsStack = scope.next;
					}
					this.xmlnsCache = null;
					return;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startPrefixMapping(final String prefix, final String uri) {
			if(!this.xmlnsEnabled) return;
			XmlnsStack scope = this.xmlnsStack;
			if((scope == null) || scope.map.containsValue(prefix)){
				scope = new XmlnsStack(scope);
				this.xmlnsStack = scope;
			}
			scope.map.put(uri, prefix);
			this.xmlnsCache = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endElement(final String uri, final String name, final String qName) {
			final CursorStack cursor = this.cursorStack;
			final CursorStack nextCursor = this.cursorStack.next;
			final List<EncodeItem> children = cursor.children;
			final List<EncodeItem> nextChildren = new ArrayList<EncodeItem>(children.size());
			final StringBuilder textValue = new StringBuilder();
			final EncodeValuePool valuePool = this.document.valuePool;
			for(final EncodeItem oldNode: children){
				if(oldNode instanceof EncodeValue){
					final EncodeValue textNode = (EncodeValue)oldNode;
					textValue.append(textNode.string);
				}else{
					if(textValue.length() != 0){
						nextChildren.add(valuePool.unique(textValue.toString()));
						textValue.setLength(0);
					}
					final EncodeElement elementNode = (EncodeElement)oldNode;
					nextChildren.add(elementNode);
					if((this.navigationPathFilter != null) && (cursor.id == null)){
						final EncodeElementPath elementPath = new ElementPath(this.pathStack, elementNode);
						if(this.navigationPathFilter.isId(elementPath)){
							final List<EncodeItem> items = elementNode.children.items;
							if(items.size() != 1) throw new IllegalArgumentException("");
							cursor.id = (EncodeValue)items.get(0);
						}
					}
				}
			}
			if(textValue.length() != 0){
				nextChildren.add(valuePool.unique(textValue.toString()));
			}
			final EncodeElement elementNode =
				(this.xmlnsEnabled ? this.document.elementNodePool.unique(cursor.uri, cursor.name, cursor.xmlns, nextChildren, cursor.attributes)
					: this.document.elementNodePool.unique(cursor.name, nextChildren, cursor.attributes));
			nextCursor.children.add(elementNode);
			if(this.navigationPathFilter != null){
				if(cursor.id != null){
					cursor.navigations.add(new EncodeGroup(Arrays.asList(cursor.id)));
				}
				for(final EncodeGroup navigation: cursor.navigations){
					navigation.items.add(elementNode);
					nextCursor.navigations.add(navigation);
				}
			}
			this.pathStack.remove();
			this.cursorStack = nextCursor;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
			if(this.xmlnsEnabled && (this.xmlnsCache == null)){
				final Map<String, String> map = new HashMap<String, String>();
				final ArrayList<EncodeLabel> xmlns = new ArrayList<EncodeLabel>();
				final EncodeLabelPool xmlnsLabelPool = this.document.xmlnsLabelPool;
				for(XmlnsStack scope = this.xmlnsStack; scope != null; scope = scope.next){
					for(final Entry<String, String> entry: scope.map.entrySet()){
						final String xmlnsName = entry.getValue();
						if(!map.containsKey(xmlnsName)){
							final String xmlnsUri = entry.getKey();
							map.put(xmlnsName, xmlnsUri);
							xmlns.add(xmlnsLabelPool.unique(xmlnsUri, xmlnsName));
						}
					}
				}
				xmlns.trimToSize();
				if(xmlns.size() > 1){
					Collections.sort(xmlns, Encoder2.XmlnsComparator);
				}
				this.xmlnsCache = xmlns;
			}
			this.pathStack.append(uri, name);
			EncodeValue id = null;
			final int size = atts.getLength();
			final List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>(size);
			final EncodeAttributePool attributeNodePool = this.document.attributeNodePool;
			for(int i = 0; i < size; i++){
				final EncodeAttribute attributeNode =
					(this.xmlnsEnabled ? attributeNodePool.unique(atts.getURI(i), atts.getLocalName(i), atts.getValue(i)) : attributeNodePool.unique(
						atts.getLocalName(i), atts.getValue(i)));
				attributes.add(attributeNode);
				if((this.navigationPathFilter != null) && (id == null)){
					final EncodeAttributePath attributePath = new AttributePath(this.pathStack, atts, i);
					if(this.navigationPathFilter.isId(attributePath)){
						id = attributeNode.value;
					}
				}
			}
			if(size > 1){
				Collections.sort(attributes, (this.xmlnsEnabled ? Encoder2.AttributeLabelComparator : Encoder2.AttributeNameComparator));
			}
			this.cursorStack =
				(this.xmlnsEnabled ? new CursorStack(id, uri, name, this.xmlnsCache, attributes, this.cursorStack) : new CursorStack(id, name, attributes,
					this.cursorStack));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endDocument() {
			final CursorStack cursor = this.cursorStack;
			this.document.documentElement = (EncodeElement)cursor.children.remove(0);
			final EncodeGroupPool navigationPool = this.document.navigationPathPool;
			navigationPool.clear();
			for(final EncodeGroup navigation: cursor.navigations){
				final List<EncodeItem> items = navigation.items;
				int index = items.size();
				final List<EncodeItem> nextItems = new ArrayList<EncodeItem>(index);
				nextItems.add(items.get(0));
				EncodeElement parentNode = (EncodeElement)items.get(--index);
				while(index > 1){
					final EncodeElement childNode = (EncodeElement)items.get(--index);
					nextItems.add(new EncodePosition(parentNode.children.items.indexOf(childNode)));
					parentNode = childNode;
				}
				navigationPool.unique(nextItems);
			}
			cursor.navigations.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startDocument() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void characters(final char[] ch, final int start, final int length) {
			this.cursorStack.children.add(new EncodeValue(new String(ch, start, length)));
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
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void processingInstruction(final String target, final String data) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setDocumentLocator(final Locator locator) {
		}

	}

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von {@link EncodeLabel}s.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder2#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeLabel> LabelHasher = new Comparable<EncodeLabel>() {

		@Override
		public int compareTo(final EncodeLabel value) {
			return Coder.hashLabel(value.uri.index, value.name.index);
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
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s primär nach ihrem {@link EncodeLabel#name()} und sekundär nach ihrer {@link EncodeLabel#uri()} via {@link Encoder2#IndexComparator}.
	 */
	protected static final Comparator<EncodeLabel> LabelComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder2.IndexComparator.compare(o1.name, o2.name);
			if(comp != 0) return comp;
			return Encoder2.IndexComparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeValue}s nach ihrem {@link EncodeValue#string()}.
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
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s primär nach ihrer {@link EncodeLabel#uri()} und sekundär nach ihrem {@link EncodeLabel#name()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeLabel> XmlnsComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder2.ValueComparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder2.ValueComparator.compare(o1.name, o2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute}s nach ihrem {@link EncodeAttribute#name()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeAttribute> AttributeNameComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
			return Encoder2.ValueComparator.compare(value1.name, value2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute}s primär nach dem {@link EncodeLabel#name()} und sekundär nach der {@link EncodeLabel#uri()} der {@link EncodeAttribute#label()} via {@link Encoder2#ValueComparator}.
	 */
	protected static final Comparator<EncodeAttribute> AttributeLabelComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
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
			item.write(target);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeList}s ihre aufsummierten Längen, die Summe aller Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>size|offset[0]|...|offset[size]|values[0]|...|values[size-1]
	 * offset[0] = 0
	 * offset[i+1] = offset[i] + values[i].length</pre>
	 * 
	 * @see EncodeList#length()
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
			offset += values.get(i).length();
		}
		value[size] = offset;
		Encoder2.writeInts(target, value);
		for(final EncodeItem item: values){
			item.write(target);
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
	 * Diese Methode gibt die {@code Uri-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#uriPool()} eine {@code Uri-Hash-Table} erzeugt.
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
	 * Diese Methode setzt die {@code Uri-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#uriPool()} eine {@code Uri-Hash-Table} erzeugt.
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
	 * Diese Methode gibt die {@code Value-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#valuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#valueHash()
	 * @return {@code Value-Hash}-Aktivierung.
	 */
	public boolean isValueHashEnabled() {
		return this.valueHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Value-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#valuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#valueHash()
	 * @param value {@code Value-Hash}-Aktivierung.
	 */
	public void setValueHashEnabled(final boolean value) {
		this.valueHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code Uri} und einen {@code Prefix}.
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
	 * Diese Methode setzt die {@code xmlns}-Aktivierung. Wenn diese Option {@code true} ist, besitzen {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code Uri} und einen {@code Prefix}.
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
	 * Diese Methode gibt die {@code Xmlns-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#xmlnsNamePool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
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
	 * Diese Methode setzt die {@code Xmlns-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#xmlnsNamePool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
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
	 * Diese Methode gibt die {@code Xmlns-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#xmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
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
	 * Diese Methode setzt die {@code Xmlns-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#xmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
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
	 * Diese Methode gibt die {@code Element-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#elementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#elementNameHash()
	 * @return {@code Element-Name-Hash}-Aktivierung.
	 */
	public boolean isElementNameHashEnabled() {
		return this.elementNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Element-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#elementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#elementNameHash()
	 * @param value {@code Element-Name-Hash}-Aktivierung.
	 */
	public void setElementNameHashEnabled(final boolean value) {
		this.elementNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Element-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#elementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
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
	 * Diese Methode setzt die {@code Element-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#elementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
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
	 * Diese Methode gibt die {@code Attribute-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#attributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @return {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public boolean isAttributeNameHashEnabled() {
		return this.attributeNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Attribute-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#attributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @param value {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public void setAttributeNameHashEnabled(final boolean value) {
		this.attributeNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Attribute-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#attributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
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
	 * Diese Methode setzt die {@code Attribute-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#attributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
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
	 * Diese Methode gibt die {@code Navigation-Path}-Aktivierung zurück. Wenn diese Option {@code true} ist, werden Navigationsdaten für {@link EncodeDocument#navigationPathPool()} erzeugt.
	 * 
	 * @see Attr#isId()
	 * @see Document#getElementById(String)
	 * @see EncodeDocument#navigationPathPool()
	 * @return {@code Navigation-Path}-Aktivierung.
	 */
	public boolean isNavigationPathEnabled() {
		return this.navigationPathEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Navigation-Path}-Aktivierung. Wenn diese Option {@code true} ist, werden Navigationsdaten für {@link EncodeDocument#navigationPathPool()} erzeugt.
	 * 
	 * @see Attr#isId()
	 * @see Document#getElementById(String)
	 * @see EncodeDocument#navigationPathPool()
	 * @param value {@code Navigation-Path}-Aktivierung.
	 */
	public void setNavigationPathEnabled(final boolean value) {
		this.navigationPathEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Navigation-Path-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#navigationPathPool()} eine {@code Navigation-Path-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#navigationPathHash()
	 * @return {@code Navigation-Path-Hash}-Aktivierung.
	 */
	public boolean isNavigationPathHashEnabled() {
		return this.navigationPathHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Navigation-Path-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für {@link EncodeDocument#navigationPathPool()} eine {@code Navigation-Path-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashString(String)
	 * @see DecodeDocument#navigationPathHash()
	 * @param value {@code Navigation-Path-Hash}-Aktivierung.
	 */
	public void setNavigationPathHashEnabled(final boolean value) {
		this.navigationPathHashEnabled = value;
	}

	// /**
	// * Diese Methode kodiert das im {@code Source}-{@link File} gegebene XML-Dokument in eine optimierte binäre Darstellung und speichert diese im {@code Target}-{@link File}.
	// *
	// * @see #encode(XMLReader, InputSource, EncodeTarget)
	// * @param source {@code Source}-{@link File}.
	// * @param target {@code Target}-{@link File}.
	// * @throws IOException Wenn die verwendete {@link InputSource} bzw. das verwendete {@link EncodeTarget} eine {@link IOException} auslöst.
	// * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	// * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	// */
	// public void encode(final File source, final File target) throws IOException, SAXException, NullPointerException {
	// if(source == null) throw new NullPointerException("source is null");
	// if(target == null) throw new NullPointerException("target is null");
	// this.encode(XMLReaderFactory.createXMLReader(), new InputSource(new FileReader(source)), new EncodeTargetFile(target));
	// }

	// /**
	// * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues {@link EncodeDocument} ein und gibt dieses zurück.
	// *
	// * @see #encode(XMLReader, InputSource, EncodeDocument)
	// * @param reader {@link XMLReader}.
	// * @param source {@link InputSource}.
	// * @return {@link EncodeDocument}.
	// * @throws IOException Wenn die {@link InputSource} eine {@link IOException} auslöst.
	// * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	// * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	// */
	// public EncodeDocument encode(final XMLReader reader, final InputSource source) throws IOException, SAXException, NullPointerException {
	// if(reader == null) throw new NullPointerException("reader is null");
	// if(source == null) throw new NullPointerException("source is null");
	// final EncodeDocument target = new EncodeDocument();
	// this.encode(reader, source, target);
	// return target;
	// }

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
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
		final List<EncodeValue> uriPool = Encoder2.compilePool(source.uriPool(), 0, Encoder2.ValueComparator);
		final List<EncodeGroup> uriHash = Encoder2.compileHash(uriPool, this.uriHashEnabled, Encoder2.ValueHasher);
		final List<EncodeValue> valuePool = Encoder2.compilePool(source.valuePool(), 0, Encoder2.ValueComparator);
		final List<EncodeGroup> valueHash = Encoder2.compileHash(valuePool, this.valueHashEnabled, Encoder2.ValueHasher);
		final List<EncodeValue> xmlnsNamePool = Encoder2.compilePool(source.xmlnsNamePool(), 0, Encoder2.ValueComparator);
		final List<EncodeGroup> xmlnsNameHash = Encoder2.compileHash(xmlnsNamePool, this.xmlnsNameHashEnabled, Encoder2.ValueHasher);
		final List<EncodeLabel> xmlnsLabelPool = Encoder2.compilePool(source.xmlnsLabelPool(), 0, Encoder2.LabelComparator);
		final List<EncodeGroup> xmlnsLabelHash = Encoder2.compileHash(xmlnsLabelPool, this.xmlnsLabelHashEnabled, Encoder2.LabelHasher);
		final List<EncodeValue> elementNamePool = Encoder2.compilePool(source.elementNamePool(), 0, Encoder2.ValueComparator);
		final List<EncodeGroup> elementNameHash = Encoder2.compileHash(elementNamePool, this.elementNameHashEnabled, Encoder2.ValueHasher);
		final List<EncodeLabel> elementLabelPool = Encoder2.compilePool(source.elementLabelPool(), 0, Encoder2.LabelComparator);
		final List<EncodeGroup> elementLabelHash = Encoder2.compileHash(elementLabelPool, this.elementLabelHashEnabled, Encoder2.LabelHasher);
		final List<EncodeValue> attributeNamePool = Encoder2.compilePool(source.attributeNamePool(), 0, Encoder2.ValueComparator);
		final List<EncodeGroup> attributeNameHash = Encoder2.compileHash(attributeNamePool, this.attributeNameHashEnabled, Encoder2.ValueHasher);
		final List<EncodeLabel> attributeLabelPool = Encoder2.compilePool(source.attributeLabelPool(), 0, Encoder2.LabelComparator);
		final List<EncodeGroup> attributeLabelHash = Encoder2.compileHash(attributeLabelPool, this.attributeLabelHashEnabled, Encoder2.LabelHasher);
		final List<EncodeGroup> elementXmlnsPool = Encoder2.compilePool(source.elementXmlnsPool(), 0, Encoder2.IndexComparator);
		final List<EncodeGroup> elementChildrenPool = Encoder2.compilePool(source.elementChildrenPool(), 0, Encoder2.IndexComparator);
		final List<EncodeGroup> elementAttributesPool = Encoder2.compilePool(source.elementAttributesPool(), 0, Encoder2.IndexComparator);
		final List<EncodeElement> elementNodePool = Encoder2.compilePool(source.elementNodePool(), valuePool.size(), Encoder2.IndexComparator);
		final List<EncodeAttribute> attributeNodePool = Encoder2.compilePool(source.attributeNodePool(), 0, Encoder2.IndexComparator);
		final List<EncodeGroup> navigationPathPool = Encoder2.compilePool(source.navigationPathPool(), 0, Encoder2.GroupComparator);
		final List<EncodeGroup> navigationPathHash = Encoder2.compileHash(navigationPathPool, this.navigationPathHashEnabled, Encoder2.GroupHasher);
		Encoder2.writeLists(target, uriHash);
		Encoder2.writeLists(target, uriPool);
		Encoder2.writeLists(target, valueHash);
		Encoder2.writeLists(target, valuePool);
		Encoder2.writeLists(target, xmlnsNameHash);
		Encoder2.writeLists(target, xmlnsNamePool);
		Encoder2.writeLists(target, xmlnsLabelHash);
		Encoder2.writeItems(target, xmlnsLabelPool);
		Encoder2.writeLists(target, elementNameHash);
		Encoder2.writeLists(target, elementNamePool);
		Encoder2.writeLists(target, elementLabelHash);
		Encoder2.writeItems(target, elementLabelPool);
		Encoder2.writeLists(target, attributeNameHash);
		Encoder2.writeLists(target, attributeNamePool);
		Encoder2.writeLists(target, attributeLabelHash);
		Encoder2.writeItems(target, attributeLabelPool);
		Encoder2.writeLists(target, elementXmlnsPool);
		Encoder2.writeLists(target, elementChildrenPool);
		Encoder2.writeLists(target, elementAttributesPool);
		Encoder2.writeItems(target, elementNodePool);
		Encoder2.writeItems(target, attributeNodePool);
		Encoder2.writeLists(target, navigationPathHash);
		Encoder2.writeLists(target, navigationPathPool);
		Encoder2.writeInts(target, source.documentElement().getIndex() - valuePool.size());
	}

	// /**
	// * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues {@link EncodeDocument} ein und speichert dessen in das gegebene {@link EncodeTarget}.
	// *
	// * @see #encode(XMLReader, InputSource)
	// * @see #encode(EncodeDocument, EncodeTarget)
	// * @param reader {@link XMLReader}.
	// * @param source {@link InputSource}.
	// * @param target {@link EncodeTarget}.
	// * @throws IOException Wenn {@link InputSource} oder {@link EncodeTarget} eine {@link IOException} auslöst.
	// * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	// * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	// */
	// public void encode(final XMLReader reader, final InputSource source, final EncodeTarget target) throws IOException, SAXException, NullPointerException {
	// if(reader == null) throw new NullPointerException("reader is null");
	// if(source == null) throw new NullPointerException("source is null");
	// if(target == null) throw new NullPointerException("target is null");
	// this.encode(this.encode(reader, source), target);
	// }

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
