package bee.creative.xml.coder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import bee.creative.util.Comparators;
import bee.creative.util.Hash;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Unique;

/**
 * Diese Klasse implementiert Methoden zur Kodierung eines XML-Dokuments in eine optimierte binäre Darstellung.
 * 
 * @see Decoder
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Encoder {

	/**
	 * Diese Schnittstelle definiert die Ausgabe eines {@link Encoder}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface EncodeTarget {

		/**
		 * Diese Methode kopiert die gegebene Anzahl an {@code byte}s aus dem gegebenen {@code byte}-Array ab dem gegebenen
		 * Index in die Ausgabe an deren aktuelle Schreibposition und setzt diese Schreibposition anschließend an das Ende
		 * des soeben geschriebenen Datenbereiches.
		 * 
		 * @param array {@code byte}-Array.
		 * @param offset Index des ersten geschriebenen {@code byte}s.
		 * @param length Anzahl der geschriebenen {@code byte}s.
		 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
		 */
		public void write(byte[] array, int offset, int length) throws IOException;

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeTarget} mit {@link RandomAccessFile}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeTargetFile implements EncodeTarget {

		/**
		 * Dieses Feld speichert das {@link RandomAccessFile}.
		 */
		final RandomAccessFile file;

		/**
		 * Dieser Konstrukteur initialisiert den Dateinamen.
		 * 
		 * @see #EncodeTargetFile(File)
		 * @param fileName Dateiname.
		 * @throws NullPointerException Wenn der gegebene Dateiname {@code null} ist.
		 * @throws FileNotFoundException Wenn das gegebene {@link File} nicht im Modus {@code "rw"} geöffnet werden kann.
		 */
		public EncodeTargetFile(final String fileName) throws NullPointerException, FileNotFoundException {
			this(new File(fileName));
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link File}.
		 * 
		 * @see #EncodeTargetFile(RandomAccessFile)
		 * @param file {@link File}.
		 * @throws NullPointerException Wenn das gegebene {@link File} {@code null} ist.
		 * @throws FileNotFoundException Wenn das gegebene {@link File} nicht im Modus {@code "rw"} geöffnet werden kann.
		 */
		public EncodeTargetFile(final File file) throws NullPointerException, FileNotFoundException {
			this(new RandomAccessFile(file, "rw"));
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws NullPointerException Wenn das gegebene {@link RandomAccessFile} {@code null} ist.
		 */
		public EncodeTargetFile(final RandomAccessFile file) throws NullPointerException {
			if(file == null) throw new NullPointerException("file is null");
			this.file = file;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final byte[] values, final int offset, final int count) throws IOException {
			this.file.write(values, offset, count);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung einzigartiger Elemente in einer Art {@link Hash}-
	 * {@code Set}. Wenn das via {@link #get(EncodeItem)} zu einem gegebenen Element
	 * {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, werden dieses als Rückgabewert
	 * verwendet dessen Wiederverwendung via {@link #reuse(EncodeItem)} signalisiert. Das Einfügen eines neuen Elements
	 * wird dagegen mit {@link #insert(EncodeItem)} angezeigt.
	 * <p>
	 * Die Implementation ähnelt einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
	 * <p>
	 * In der Methode {@link #reuse(EncodeItem)} wird der Index der {@link EncodeItem}s erhöht.
	 * 
	 * @see Hash
	 * @see Unique
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class EncodePool<GItem extends EncodeItem> extends Hash<GItem, GItem, GItem> implements Iterable<GItem> {

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #reuse(EncodeItem)}.
		 */
		int reuses;

		/**
		 * Dieser Konstrukteur initialisiert die Größe der {@link Hash}-Tabelle mit {@code 128} Einträgen.
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
			return Coder.hashHash(this.hash(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final GItem entry, final GItem key, final int hash) {
			return this.equals(entry, key);
		}

		/**
		 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Elements zurück. Das Element ist nie
		 * {@code null}.
		 * 
		 * @param input Element.
		 * @return {@link Object#hashCode() Streuwert}.
		 */
		protected int hash(final GItem input) {
			return input.hashCode();
		}

		/**
		 * Diese Methode wird bei der Wiederverwendung der gegebenen Elements aufgerufen.
		 * 
		 * @see #get(EncodeItem)
		 * @param value Element.
		 */
		protected void reuse(final GItem value) {
			this.reuses++;
			value.index++;
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
		 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Elemente zurück. Die Elemente sind
		 * nie {@code null}.
		 * 
		 * @param input1 Element 1.
		 * @param input2 Element 2.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Elemente.
		 */
		protected boolean equals(final GItem input1, final GItem input2) {
			return input1.equals(input2);
		}

		/**
		 * Diese Methode gibt das einzigartige, zum gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente}
		 * Element zurück. Wenn ein solches Element gefunden wurde, wird dessen Wiederverwendung via
		 * {@link #reuse(EncodeItem)} signalisiert. Sollte dagegen kein {@link #equals(EncodeItem, EncodeItem) äquivalentes}
		 * Element gefunden werden, werden das gegebene Element in den {@link EncodePool} eingefügt, das Einfügen mit
		 * {@link #insert(EncodeItem)} angezeigt und das Element zurück gegeben.
		 * 
		 * @see #hash(EncodeItem)
		 * @see #equals(EncodeItem, EncodeItem)
		 * @param key Element.
		 * @return einzigartiges, {@link #equals(EncodeItem, EncodeItem) äquivalentes} Element.
		 * @throws NullPointerException Wenn das gegebene Element {@code null} ist.
		 */
		public final GItem get(final GItem key) throws NullPointerException {
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
			this.reuses = 0;
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
			return Objects.toStringCall(false, true, this.getClass().getSimpleName(), "size", this.size(), "reuses",
				this.reuses, "compression", (100 * this.size()) / (this.size() + this.reuses + 1));
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index, der als Element in einem {@link EncodePool}
	 * verwendet werden kann.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem {

		/**
		 * Dieses Feld speichert das nächste {@link EncodeItem} im {@link EncodePool}.
		 * 
		 * @see EncodePool#getEntryNext(EncodeItem)
		 */
		EncodeItem next;

		/**
		 * Dieses Feld speichert beim Aufbau der {@link EncodePool}s die absolute Häufigkeit und beim Speichern den Index
		 * des Datensatzes.
		 */
		public int index = 1;

		/**
		 * Diese Methode schreibt den Datensatz in das gegebenen {@link EncodeTarget}.
		 * 
		 * @param target {@link EncodeTarget}.
		 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
		 */
		public abstract void write(final EncodeTarget target) throws IOException;

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} als Sammlung mehrerer Werte.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeList extends EncodeItem {

		/**
		 * Diese Methode gibt die Länge bzw. Anzahl der Werte zurück.
		 * 
		 * @return Länge bzw. Anzahl der Werte.
		 */
		public abstract int length();

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
		public final String value;

		/**
		 * Dieser Konstrukteur initialisiert den {@link String}.
		 * 
		 * @param value {@link String}.
		 */
		public EncodeValue(final String value) {
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return Coder.encodeChars(this.value).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			final byte[] bytes = Coder.encodeChars(this.value);
			target.write(bytes, 0, bytes.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "[" + this.index + "]" + Objects.toString(this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} zu {@link EncodeValue}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValuePool extends EncodePool<EncodeValue> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeValue} zum gegebenen {@code String} zurück.
		 * 
		 * @param value {@code String}.
		 * @return einzigartige {@link EncodeValue}.
		 */
		public EncodeValue unique(final String value) {
			return this.get(new EncodeValue(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeValue input) {
			return Objects.hash(input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeValue input1, final EncodeValue input2) {
			return input1.value.equals(input2.value);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} mit {@code URI} und {@code Name}.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabel extends EncodeItem {

		/**
		 * Dieses Feld speichert die {@code URI}.
		 * 
		 * @see Node#getNamespaceURI()
		 */
		public final EncodeValue uri;

		/**
		 * Dieses Feld speichert den {@code Name}.
		 * 
		 * @see Node#getLocalName()
		 */
		public final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert {@code URI} und {@code Name}.
		 * 
		 * @param uri {@code URI} ({@link Node#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Node#getLocalName()}).
		 */
		public EncodeLabel(final EncodeValue uri, final EncodeValue name) {
			if(uri == null) throw new NullPointerException("uri is null");
			if(name == null) throw new NullPointerException("name is null");
			this.uri = uri;
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			Encoder.writeInts(target, this.uri.index, this.name.index);
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
	 * Diese Klasse implementiert den {@link EncodePool} zu {@link EncodeLabel}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabelPool extends EncodePool<EncodeLabel> {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@code URI}s.
		 */
		public final EncodeValuePool uriPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für den {@code Name}s.
		 */
		public final EncodeValuePool namePool;

		/**
		 * Dieser Konstrukteur initialisiert {@code URI}- und {@code Name}-{@link EncodeValuePool}.
		 * 
		 * @param uriPool {@link EncodeValuePool} für die {@code URI}s.
		 * @param namePool {@link EncodeValuePool} für den {@code Name}s.
		 */
		public EncodeLabelPool(final EncodeValuePool uriPool, final EncodeValuePool namePool) {
			this.uriPool = uriPool;
			this.namePool = namePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @return einzigartiges {@link EncodeLabel}.
		 */
		public EncodeLabel unique(final String uri, final String name) {
			return this.get(new EncodeLabel(this.uriPool.unique(uri), this.namePool.unique(name)));
		}

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
	 * Diese Klasse implementiert eine {@link EncodeList} als Gruppe von von {@link EncodeItem}s.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeGroup extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List}.
		 */
		public final List<EncodeItem> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeGroup() {
			this(new ArrayList<EncodeItem>());
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List}.
		 * 
		 * @param value {@link EncodeItem}-{@link List}.
		 */
		public EncodeGroup(final List<? extends EncodeItem> value) {
			this.values = new ArrayList<EncodeItem>(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.values.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			Encoder.writeIndices(target, this.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeGroup", "index", this.index, "values", this.values);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} zu {@link EncodeGroup}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeGroupPool extends EncodePool<EncodeGroup> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeGroup} mit der gegebenen {@link EncodeItem}-{@link List} zurück.
		 * 
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeGroup}.
		 */
		public EncodeGroup unique(final List<? extends EncodeItem> value) {
			return this.get(new EncodeGroup(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeGroup input) {
			return Objects.hash(input.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeGroup input1, final EncodeGroup input2) {
			return input1.values.equals(input2.values);
		}

	}

	/**
	 * Diese Klasse implementiert das {@link Element}-{@link EncodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElement extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}-{@link EncodeValue} oder {@code null}.
		 * 
		 * @see Element#getNodeName()
		 */
		public final EncodeValue name;

		/**
		 * Dieses Feld speichert das {@code URI/Name}-{@link EncodeLabel} oder {@code null}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabel label;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code URI/Prefix}-{@link EncodeLabel}s oder {@code null}.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public final EncodeGroup spaces;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeGroup children;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeGroup attributes;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param name {@link EncodeValue} für {@link Element#getNodeName()}.
		 * @param children {@link EncodeGroup} für {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeGroup} für {@link Element#getAttributes()}.
		 */
		public EncodeElement(final EncodeValue name, final EncodeGroup children, final EncodeGroup attributes) {
			if(name == null) throw new NullPointerException("name is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			this.name = name;
			this.label = null;
			this.spaces = null;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param label {@link EncodeLabel} für {@link Element#getLocalName()} und {@link Element#getNamespaceURI()}.
		 * @param spaces {@link EncodeGroup} für {@link Element#lookupPrefix(String)} und
		 *        {@link Element#lookupNamespaceURI(String)}.
		 * @param children {@link EncodeGroup} für {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeGroup} für {@link Element#getAttributes()}.
		 */
		public EncodeElement(final EncodeLabel label, final EncodeGroup spaces, final EncodeGroup children,
			final EncodeGroup attributes) {
			if(label == null) throw new NullPointerException("label is null");
			if(spaces == null) throw new NullPointerException("spaces is null");
			if(children == null) throw new NullPointerException("children is null");
			if(attributes == null) throw new NullPointerException("attributes is null");
			this.name = null;
			this.label = label;
			this.spaces = spaces;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			if(this.name == null){
				Encoder.writeInts(target, this.label.index, this.spaces.index, this.children.index, this.attributes.index);
			}else{
				Encoder.writeInts(target, this.name.index, this.children.index, this.attributes.index);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.name == null)
				return Objects.toStringCall(false, true, "EncodeElement", "index", this.index, "label", this.label, "spaces",
					this.spaces.index, "attributes", this.attributes.index, "children", this.children.index);
			return Objects.toStringCall(false, true, "EncodeElement", "index", this.index, "name", this.name, "attributes",
				this.attributes.index, "children", this.children.index);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} zu {@link EncodeElement}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementPool extends EncodePool<EncodeElement> {

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für die {@link Element}-{@code Label}s.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabelPool labelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@code Xmlns}-{@code Label}s.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public final EncodeGroupPool xmlnsPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@link Element#getChildNodes()}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeGroupPool childrenPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@link Element#getAttributes()}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeGroupPool attributesPool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodePool}s und {@link EncodeLabelPool}s.
		 * 
		 * @param labelPool {@link EncodeLabelPool} für die {@link Element}-{@code Label}s.
		 * @param xmlnsPool {@link EncodeGroupPool} für die {@code Xmlns}-{@code Label}s.
		 * @param childrenPool {@link EncodeGroupPool} für die {@link Element#getChildNodes()}.
		 * @param attributesPool {@link EncodeGroupPool} für die {@link Element#getAttributes()}.
		 */
		public EncodeElementPool(final EncodeLabelPool labelPool, final EncodeGroupPool xmlnsPool,
			final EncodeGroupPool childrenPool, final EncodeGroupPool attributesPool) {
			this.labelPool = labelPool;
			this.xmlnsPool = xmlnsPool;
			this.childrenPool = childrenPool;
			this.attributesPool = attributesPool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElement} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param name {@code Name} des {@link Element}-{@code Name}s.
		 * @param children {@link EncodeItem}-{@link List} der {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeAttribute}-{@link List} der {@link Element#getAttributes()}.
		 * @return einzigartige {@link EncodeElement}.
		 */
		public EncodeElement unique(final String name, final List<? extends EncodeItem> children,
			final List<? extends EncodeItem> attributes) {
			return this.get(new EncodeElement(this.labelPool.namePool.unique(name), this.childrenPool.unique(children),
				this.attributesPool.unique(attributes)));
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElement} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri {@code URI} des {@link Element}-{@code Label}s.
		 * @param name {@code Name} des {@link Element}-{@code Label}s.
		 * @param xmlns {@link EncodeLabel}-{@link List} der {@code Xmlns}-{@code Label}s.
		 * @param children {@link EncodeItem}-{@link List} der {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeAttribute}-{@link List} der {@link Element#getAttributes()}.
		 * @return einzigartige {@link EncodeElement}.
		 */
		public EncodeElement unique(final String uri, final String name, final List<? extends EncodeItem> xmlns,
			final List<? extends EncodeItem> children, final List<? extends EncodeItem> attributes) {
			return this.get(new EncodeElement(this.labelPool.unique(uri, name), this.xmlnsPool.unique(xmlns),
				this.childrenPool.unique(children), this.attributesPool.unique(attributes)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElement input) {
			return Objects.hash(input.name, input.label, input.spaces, input.children, input.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElement input1, final EncodeElement input2) {
			return (input1.name == input2.name) && (input1.label == input2.label) && (input1.spaces == input2.spaces)
				&& (input1.children == input2.children) && (input1.attributes == input2.attributes);
		}

	}

	/**
	 * Diese Klasse implementiert das {@link Attr}-{@link EncodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttribute extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}-{@link EncodeValue} oder {@code null}.
		 * 
		 * @see Attr#getNodeName()
		 * @see Attr#getLocalName()
		 */
		public final EncodeValue name;

		/**
		 * Dieses Feld speichert das {@code URI/Name}-{@link EncodeLabel}.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		public final EncodeLabel label;

		/**
		 * Dieses Feld speichert die {@link EncodeValue}.
		 * 
		 * @see Attr#getNodeValue()
		 */
		public final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param name {@link EncodeValue} für {@link Attr#getNodeName()}.
		 * @param value {@link EncodeValue} für {@link Attr#getNodeValue()}.
		 */
		public EncodeAttribute(final EncodeValue name, final EncodeValue value) {
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			this.name = name;
			this.label = null;
			this.value = value;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param label {@link EncodeLabel} für {@link Attr#getLocalName()} und {@link Attr#getNamespaceURI()}.
		 * @param value {@link EncodeValue} für {@link Attr#getNodeValue()}.
		 */
		public EncodeAttribute(final EncodeLabel label, final EncodeValue value) {
			if(label == null) throw new NullPointerException("label is null");
			if(value == null) throw new NullPointerException("value is null");
			this.name = null;
			this.label = label;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			if(this.name == null){
				Encoder.writeInts(target, this.label.index, this.value.index);
			}else{
				Encoder.writeInts(target, this.name.index, this.value.index);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.name == null)
				return Objects.toStringCall(false, true, "EncodeAttribute", "index", this.index, "label", this.label, "value",
					this.value);
			return Objects.toStringCall(false, true, "EncodeAttribute", "index", this.index, "name", this.name, "value",
				this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} zu {@link EncodeAttribute}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributePool extends EncodePool<EncodeAttribute> {

		/**
		 * Dieses Feld speichert das {@link EncodeLabelPool} für die {@link Attr}-{@code Label}s.
		 */
		public final EncodeLabelPool labelPool;

		/**
		 * Dieses Feld speichert das {@link EncodeValuePool} für den {@code Value}.
		 */
		public final EncodeValuePool valuePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param labelPool {@link EncodeLabelPool} für die {@link Attr}-{@code Label}s.
		 * @param valuePool {@link EncodeValuePool} für den {@code Value}.
		 */
		public EncodeAttributePool(final EncodeLabelPool labelPool, final EncodeValuePool valuePool) {
			this.labelPool = labelPool;
			this.valuePool = valuePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttribute} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttribute#name
		 * @see EncodeAttribute#value
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeAttribute}.
		 */
		public EncodeAttribute unique(final String name, final String value) {
			return this.get(new EncodeAttribute(this.labelPool.namePool.unique(name), this.valuePool.unique(value)));
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttribute} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeLabel#uri
		 * @see EncodeLabel#name
		 * @see EncodeAttribute#label
		 * @see EncodeAttribute#value
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeAttribute}.
		 */
		public EncodeAttribute unique(final String uri, final String name, final String value) {
			return this.get(new EncodeAttribute(this.labelPool.unique(uri, name), this.valuePool.unique(value)));
		}

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

	/**
	 * Diese Klasse implementiert den {@link ContentHandler} zum Einlsenen eines {@link Document}s mit Hilfe eines
	 * {@link XMLReader}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocument implements ContentHandler {

		/**
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code URI} und {@code Prefix} als
		 * {@link EncodeLabel}s während des Einlesens eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class XmlnsStack {

			/**
			 * Dieses Feld speichert die {@code URI}-{@code Prefix}-{@link Map}.
			 */
			final Map<String, String> map;

			/**
			 * Dieses Feld speichert den nächsten {@link XmlnsStack} oder {@code null}.
			 */
			final XmlnsStack next;

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
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Inhalte eine {@link Element}s während des Einlesens
		 * eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class CursorStack {

			/**
			 * Dieses Feld speichert die {@code URI}.
			 * 
			 * @see Element#getNamespaceURI()
			 */
			final String uri;

			/**
			 * Dieses Feld speichert den {@code Name}.
			 * 
			 * @see Element#getLocalName()
			 */
			final String name;

			/**
			 * Dieses Feld speichert die {@link EncodeLabel}-{@link List} für die Paare aus {@code URI} und {@code Prefix}.
			 * 
			 * @see Node#lookupPrefix(String)
			 * @see Node#lookupNamespaceURI(String)
			 */
			final List<EncodeLabel> spaces;

			/**
			 * Dieses Feld speichert die {@link EncodeItem}-{@link List} für die {@link Element#getChildNodes()}.
			 */
			final List<EncodeItem> children;

			/**
			 * Dieses Feld speichert die {@link EncodeAttribute}-{@link List} für die {@link Element#getAttributes()}.
			 */
			final List<? extends EncodeItem> attributes;

			/**
			 * Dieses Feld speichert den nächsten {@link CursorStack} oder {@code null}.
			 */
			final CursorStack next;

			/**
			 * Dieser Konstrukteur initialisiert den leeren {@link CursorStack}.
			 */
			public CursorStack() {
				this(null, null, null, null, null);
			}

			/**
			 * Dieser Konstrukteur initialisiert den {@link CursorStack}.
			 * 
			 * @see Element#getNodeName()
			 * @see Element#getAttributes()
			 * @param name {@code Name}.
			 * @param attributes {@link EncodeAttribute}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final String name, final List<EncodeAttribute> attributes, final CursorStack next) {
				this(null, name, null, attributes, next);
			}

			/**
			 * Dieser Konstrukteur initialisiert den {@link CursorStack}.
			 * 
			 * @see Element#getLocalName()
			 * @see Element#getNamespaceURI()
			 * @see Element#getAttributes()
			 * @see Element#lookupPrefix(String)
			 * @see Element#lookupNamespaceURI(String)
			 * @param uri {@code URI}.
			 * @param name {@code Name}.
			 * @param spaces {@code URI/Prefix}-{@link EncodeLabel}-{@link List}.
			 * @param attributes {@link EncodeAttribute}-{@link List}.
			 * @param next nächster {@link CursorStack} oder {@code null}.
			 */
			public CursorStack(final String uri, final String name, final List<EncodeLabel> spaces,
				final List<EncodeAttribute> attributes, final CursorStack next) {
				this.uri = uri;
				this.name = name;
				this.spaces = spaces;
				this.children = new ArrayList<EncodeItem>(0);
				this.attributes = attributes;
				this.next = next;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, "EncodeCursorStack", this.uri, this.name, this.spaces, this.children,
					this.attributes, this.next);
			}

		}

		/**
		 * Dieses Feld speichert die aktuellen Paare aus {@code URI} und {@code Prefix}.
		 */
		List<EncodeLabel> xmlns;

		/**
		 * Dieses Feld speichert den {@link XmlnsStack} für die aktuellen Paare aus {@code URI} und {@code Prefix} oder
		 * {@code null}.
		 */
		XmlnsStack xmlnsStack;

		/**
		 * Dieses Feld speichert den {@link CursorStack} für das aktuelle {@link Element}.
		 */
		CursorStack cursorStack;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen
		 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
		 * {@code Prefix}.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getNamespaceURI()
		 */
		final boolean xmlnsEnabled;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@code URI}s. Im {@link EncodeTarget} werden diese
		 * {@link EncodeValue}s nach ihrem {@link EncodeValue#value value} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 */
		public final EncodeValuePool uriPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@code Value}s. Im {@link EncodeTarget} werden diese
		 * {@link EncodeValue}s nach ihrem {@link EncodeValue#value value} aufsteigend sortiert gespeichert.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 */
		public final EncodeValuePool valuePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@code Prefix}es. Im {@link EncodeTarget} werden diese
		 * {@link EncodeValue}s nach ihrem {@link EncodeValue#value value} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getPrefix()
		 */
		public final EncodeValuePool xmlnsNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für die Paare aus {@code URI} und {@code Prefix}. Im
		 * {@link EncodeTarget} werden diese {@link EncodeLabel}s primär nach {@code Name} und sekundär nach {@code URI}
		 * aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @see Node#getPrefix()
		 */
		public final EncodeLabelPool xmlnsLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementPool} für die {@link Element}-Daten. Im {@link EncodeTarget} werden
		 * diese {@link EncodeElement}s nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see EncodeElement
		 */
		public final EncodeElementPool elementNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@link Element}-{@code Name}s. Im {@link EncodeTarget}
		 * werden diese {@link EncodeValue}s nach ihrem {@link EncodeValue#value value} aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getLocalName()
		 */
		public final EncodeValuePool elementNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für die Paare aus {@code URI} und {@link Element}-{@code Name}
		 * . Im {@link EncodeTarget} werden diese {@link EncodeLabel}s primär nach {@code Name} und sekundär nach
		 * {@code URI} aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabelPool elementLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@code Xmlns}. Im {@link EncodeTarget} werden diese
		 * {@link EncodeGroup}s nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 */
		public final EncodeGroupPool elementXmlnsPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@link Element#getChildNodes()}. Im
		 * {@link EncodeTarget} werden diese {@link EncodeGroup}s nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeGroupPool elementChildrenPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für die {@link Element#getAttributes()}. Im
		 * {@link EncodeTarget} werden diese {@link EncodeGroup}s nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeGroupPool elementAttributesPool;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributePool} für die {@link Attr}-Daten. Im {@link EncodeTarget} werden
		 * diese {@link EncodeAttribute}s nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see EncodeAttribute
		 */
		public final EncodeAttributePool attributeNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für die {@link Attr}-{@code Name}s. Im {@link EncodeTarget}
		 * werden diese {@link EncodeValue}s nach ihrem {@link EncodeValue#value value} aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getLocalName()
		 */
		public final EncodeValuePool attributeNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für die Paare aus {@code URI} und {@link Attr}-{@code Name}. Im
		 * {@link EncodeTarget} werden diese {@link EncodeLabel}s primär nach {@code Name} und sekundär nach {@code URI}
		 * aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		public final EncodeLabelPool attributeLabelPool;

		/**
		 * Dieses Feld speichert das {@code Document}-{@link EncodeItem}.
		 * 
		 * @see Document#getDocumentElement()
		 */
		public EncodeItem documentElement;

		/**
		 * Dieser Konstrukteur initialisiert die {@code xmlns}-Aktivierung. Wenn diese {@code true} ist, besitzen
		 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
		 * {@code Prefix}.
		 * 
		 * @param xmlnsEnabled {@code xmlns}-Aktivierung.
		 */
		public EncodeDocument(final boolean xmlnsEnabled) {
			this.xmlnsEnabled = xmlnsEnabled;
			this.cursorStack = new CursorStack();
			this.uriPool = new EncodeValuePool();
			this.valuePool = new EncodeValuePool();
			this.xmlnsNamePool = new EncodeValuePool();
			this.xmlnsLabelPool = new EncodeLabelPool(this.uriPool, this.xmlnsNamePool);
			this.elementNamePool = new EncodeValuePool();
			this.elementLabelPool = new EncodeLabelPool(this.uriPool, this.elementNamePool);
			this.elementXmlnsPool = new EncodeGroupPool();
			this.elementChildrenPool = new EncodeGroupPool();
			this.elementAttributesPool = new EncodeGroupPool();
			this.elementNodePool =
				new EncodeElementPool(this.elementLabelPool, this.elementXmlnsPool, this.elementChildrenPool,
					this.elementAttributesPool);
			this.attributeNamePool = new EncodeValuePool();
			this.attributeLabelPool = new EncodeLabelPool(this.uriPool, this.attributeNamePool);
			this.attributeNodePool = new EncodeAttributePool(this.attributeLabelPool, this.valuePool);
			if(!xmlnsEnabled) return;
			this.startPrefixMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
			this.startPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
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
					this.xmlns = null;
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
			this.xmlns = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endElement(final String uri, final String name, final String qName) {
			final CursorStack oldCursor = this.cursorStack;
			final CursorStack newCursor = this.cursorStack.next;
			final List<EncodeItem> oldChildren = oldCursor.children;
			final List<EncodeItem> newChildren = new ArrayList<EncodeItem>(oldChildren.size());
			final StringBuilder textValue = new StringBuilder();
			for(final EncodeItem oldNode: oldChildren){
				if(oldNode instanceof EncodeValue){
					final EncodeValue textChildNode = (EncodeValue)oldNode;
					textValue.append(textChildNode.value);
				}else{
					if(textValue.length() != 0){
						newChildren.add(this.valuePool.unique(textValue.toString()));
						textValue.setLength(0);
					}
					newChildren.add(oldNode);
				}
			}
			if(textValue.length() != 0){
				newChildren.add(this.valuePool.unique(textValue.toString()));
			}
			if(this.xmlnsEnabled){
				newCursor.children.add(this.elementNodePool.unique(oldCursor.uri, oldCursor.name, oldCursor.spaces,
					newChildren, oldCursor.attributes));
			}else{
				newCursor.children.add(this.elementNodePool.unique(oldCursor.name, newChildren, oldCursor.attributes));
			}
			this.cursorStack = newCursor;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
			if(this.xmlnsEnabled){
				if(this.xmlns == null){
					final Map<String, String> map = new HashMap<String, String>();
					final ArrayList<EncodeLabel> xmlns = new ArrayList<EncodeLabel>();
					for(XmlnsStack scope = this.xmlnsStack; scope != null; scope = scope.next){
						for(final Entry<String, String> entry: scope.map.entrySet()){
							final String xmlnsName = entry.getValue();
							if(!map.containsKey(xmlnsName)){
								final String xmlnsUri = entry.getKey();
								map.put(xmlnsName, xmlnsUri);
								xmlns.add(this.xmlnsLabelPool.unique(xmlnsUri, xmlnsName));
							}
						}
					}
					xmlns.trimToSize();
					if(xmlns.size() > 1){
						Collections.sort(xmlns, Encoder.XmlnsComparator);
					}
					this.xmlns = xmlns;
				}
				final int size = atts.getLength();
				final List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>(size);
				for(int i = 0; i < size; i++){
					attributes.add(this.attributeNodePool.unique(atts.getURI(i), atts.getLocalName(i), atts.getValue(i)));
				}
				if(size > 1){
					Collections.sort(attributes, Encoder.AttributeLabelComparator);
				}
				this.cursorStack = new CursorStack(uri, name, this.xmlns, attributes, this.cursorStack);
			}else{
				final int size = atts.getLength();
				final List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>(size);
				for(int i = 0; i < size; i++){
					attributes.add(this.attributeNodePool.unique(atts.getLocalName(i), atts.getValue(i)));
				}
				if(size > 1){
					Collections.sort(attributes, Encoder.AttributeNameComparator);
				}
				this.cursorStack = new CursorStack("", name, null, attributes, this.cursorStack);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endDocument() {
			this.documentElement = this.cursorStack.children.get(0);
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
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von
	 * {@link EncodeValue}s.
	 * 
	 * @see Encoder#compilePool(EncodePool, int, Comparator)
	 */
	static final Comparable<EncodeValue> ValueHasher = new Comparable<EncodeValue>() {

		@Override
		public int compareTo(final EncodeValue value) {
			return Coder.hashValue(value.value);
		}

	};

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von
	 * {@link EncodeLabel}s.
	 * 
	 * @see Encoder#compilePool(EncodePool, int, Comparator)
	 */
	static final Comparable<EncodeLabel> LabelHasher = new Comparable<EncodeLabel>() {

		@Override
		public int compareTo(final EncodeLabel value) {
			return Coder.hashLabel(value.uri.index, value.name.index);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeItem} nach ihren
	 * {@link EncodeItem#index indices}.
	 */
	static final Comparator<EncodeItem> IndexComparator = new Comparator<EncodeItem>() {

		@Override
		public int compare(final EncodeItem o1, final EncodeItem o2) {
			return Comparators.compare(o1.index, o2.index);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s nach ihren
	 * {@code Name}- und {@code URI}-{@link EncodeValue} via {@link Encoder#IndexComparator}.
	 */
	static final Comparator<EncodeLabel> LabelComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder.IndexComparator.compare(o1.name, o2.name);
			if(comp != 0) return comp;
			return Encoder.IndexComparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeValue} nach ihren
	 * {@link EncodeValue#value values}.
	 */
	static final Comparator<EncodeValue> ValueComparator = new Comparator<EncodeValue>() {

		@Override
		public int compare(final EncodeValue o1, final EncodeValue o2) {
			return Comparators.compare(o1.value, o2.value);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s primär nach
	 * ihren {@code URI}-{@link EncodeValue} und sekundär nach ihren {@code Name}-{@link EncodeValue} via
	 * {@link Encoder#ValueComparator}.
	 */
	static final Comparator<EncodeLabel> XmlnsComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder.ValueComparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder.ValueComparator.compare(o1.name, o2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute}s nach den
	 * {@code Name}-{@link EncodeValue} via {@link Encoder#ValueComparator}.
	 */
	static final Comparator<EncodeAttribute> AttributeNameComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
			return Encoder.ValueComparator.compare(value1.name, value2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute} primär nach
	 * den {@code Name}-{@link EncodeValue} und sekundär nach den {@code URI}-{@link EncodeValue} ihrer
	 * {@link EncodeLabel}s via {@link Encoder#ValueComparator}.
	 */
	static final Comparator<EncodeAttribute> AttributeLabelComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
			final EncodeLabel o1 = value1.label;
			final EncodeLabel o2 = value2.label;
			final int comp = Encoder.ValueComparator.compare(o1.name, o2.name);
			if(comp != 0) return comp;
			return Encoder.ValueComparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Diese Methode schreibt das gegebenen {@code int}-Array in das gegebene {@link EncodeTarget}.
	 * 
	 * @see Coder#encodeIndices(int...)
	 * @see EncodeTarget#write(byte[], int, int)
	 * @param target {@link EncodeTarget}.
	 * @param value {@code int}-Array.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 */
	static void writeInts(final EncodeTarget target, final int... value) throws IOException {
		final byte[] array = Coder.encodeIndices(value);
		target.write(array, 0, array.length);
	}

	/**
	 * Diese Methode schreibt dia Anzahl der gegebenen {@link EncodeItem}s sowie jedes der {@link EncodeItem} in das
	 * gegebene {@link EncodeTarget}.
	 * 
	 * <pre>N|item1|...|itemN</pre>
	 * 
	 * @see EncodeItem#write(EncodeTarget)
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param list {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 */
	static void writeItems(final EncodeTarget target, final List<? extends EncodeItem> list) throws IOException {
		Encoder.writeInts(target, list.size());
		for(final EncodeItem item: list){
			item.write(target);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeList}s ihre aufsummierten Längen, die Summe aller
	 * Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>N|offset1|...|offsetN|offsetN+1|item1|...|itemN
	 * offset0 = 0
	 * offsetI+1 = offsetI + list.get(I).length()</pre>
	 * 
	 * @see EncodeList#length()
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param list {@link EncodeList}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 */
	static void writeLists(final EncodeTarget target, final List<? extends EncodeList> list) throws IOException {
		Encoder.writeInts(target, list.size());
		final int count = list.size();
		int offset = 0;
		final int[] value = new int[count + 1];
		for(int i = 0; i < count; i++){
			value[i] = offset;
			offset += list.get(i).length();
		}
		value[count] = offset;
		Encoder.writeInts(target, value);
		for(final EncodeItem item: list){
			item.write(target);
		}
	}

	/**
	 * Diese Methode schreibt die Indices der gegebenen {@link EncodeItem}s in das gegebene {@link EncodeTarget}.
	 * 
	 * @see EncodeItem#index
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param list {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 */
	static void writeIndices(final EncodeTarget target, final List<? extends EncodeItem> list) throws IOException {
		final int count = list.size();
		if(count == 0) return;
		final int[] value = new int[count];
		for(int i = 0; i < count; i++){
			value[i] = list.get(i).index;
		}
		Encoder.writeInts(target, value);
	}

	/**
	 * Diese Methode fügt alle {@link EncodeItem}s des {@link EncodePool}s in eine neue {@link List} ein, sortiert diese
	 * {@link List} mit dem gegebenen {@link Comparator}, setzt den Index der Elemente unter Beachtung des gegebenen
	 * Offsets, entfernt alle Elemente aus dem {@link EncodePool} und gibt die {@link List} zurück. Der Index des
	 * {@code i} -ten {@link EncodeItem}s der erzeugetn {@link List} ergibt sich aus:
	 * 
	 * <pre>list.get(i).index = i + offset</pre>
	 * 
	 * @see EncodeItem#index
	 * @see EncodePool#clear()
	 * @see Collections#sort(List, Comparator)
	 * @param offset Offset der Indizes.
	 * @param comparator {@link Comparator}.
	 * @return {@link EncodeItem}-{@link List}.
	 */
	static <GItem extends EncodeItem> List<GItem> compilePool(final EncodePool<? extends GItem> pool, final int offset,
		final Comparator<? super GItem> comparator) {
		final List<GItem> list = new ArrayList<GItem>(pool.size());
		Iterators.appendAll(list, pool.iterator());
		pool.clear();
		Collections.sort(list, comparator);
		for(int i = 0, size = list.size(); i < size; i++){
			list.get(i).index = i + offset;
		}
		return list;
	}

	static <GItem extends EncodeItem> List<EncodeGroup> cimpileHash(final List<GItem> values, final boolean useHash,
		final Comparable<GItem> hasher) {
		if(!useHash) return Collections.emptyList();
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
		final int mask = count - 1;
		for(int i = 0; i < size; i++){
			final GItem value = values.get(i);
			final int index = hasher.compareTo(value) & mask;
			list.get(index).values.add(value);
		}
		return list;
	}

	boolean xmlnsEnabled = true;

	boolean uriHashEnabled = true;

	boolean valueHashEnabled = false;

	boolean xmlnsNameHashEnabled = true;

	boolean xmlnsLabelHashEnabled = true;

	boolean elementNameHashEnabled = true;

	boolean elementLabelHashEnabled = true;

	boolean attributeNameHashEnabled = true;

	boolean attributeLabelHashEnabled = true;

	/**
	 * Dieser Konstrukteur initialisiert den {@link Encoder}.
	 */
	public Encoder() {
	}

	/**
	 * Diese Methode liest die gegebene Quell-{@link File} mit dem einem neuen {@link XMLReader} in einen neuen
	 * {@link EncodeDocument} ein und speichert dessen Daten in die gegebene Ziel-{@link File}.
	 * 
	 * @see FileReader
	 * @see InputSource
	 * @see RandomAccessFile
	 * @see EncodeTargetFile
	 * @see #encode(XMLReader, InputSource, EncodeTarget)
	 * @param source Quell-{@link File}.
	 * @param target Ziel-{@link File}.
	 * @throws IOException Wenn das verwendete {@link RandomAccessFile} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 */
	public void encode(final File source, final File target) throws IOException, SAXException {
		this.encode(XMLReaderFactory.createXMLReader(), new InputSource(new FileReader(source)), new EncodeTargetFile(
			target));
	}

	/**
	 * Diese Methode liest die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in einen neuen
	 * {@link EncodeDocument} ein und speichert dessen Daten in das gegebene {@link EncodeTarget}.
	 * 
	 * @see XMLReader#setContentHandler(ContentHandler)
	 * @see XMLReader#parse(InputSource)
	 * @param reader {@link XMLReader}.
	 * @param source {@link InputSource}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 */
	public void encode(final XMLReader reader, final InputSource source, final EncodeTarget target) throws IOException,
		SAXException {
		final EncodeDocument handler = new EncodeDocument(this.xmlnsEnabled);
		reader.setContentHandler(handler);
		reader.parse(source);
		final List<EncodeValue> uriPool = //
			Encoder.compilePool(handler.uriPool, 0, Encoder.ValueComparator);
		final List<EncodeGroup> uriHash = //
			Encoder.cimpileHash(uriPool, this.uriHashEnabled, Encoder.ValueHasher);
		final List<EncodeValue> valuePool = //
			Encoder.compilePool(handler.valuePool, 0, Encoder.ValueComparator);
		final List<EncodeGroup> valueHash = //
			Encoder.cimpileHash(valuePool, this.valueHashEnabled, Encoder.ValueHasher);
		final List<EncodeValue> xmlnsNamePool = //
			Encoder.compilePool(handler.xmlnsNamePool, 0, Encoder.ValueComparator);
		final List<EncodeGroup> xmlnsNameHash = //
			Encoder.cimpileHash(xmlnsNamePool, this.xmlnsNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> xmlnsLabelPool = //
			Encoder.compilePool(handler.xmlnsLabelPool, 0, Encoder.LabelComparator);
		final List<EncodeGroup> xmlnsLabelHash = //
			Encoder.cimpileHash(xmlnsLabelPool, this.xmlnsLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeValue> elementNamePool = //
			Encoder.compilePool(handler.elementNamePool, 0, Encoder.ValueComparator);
		final List<EncodeGroup> elementNameHash = //
			Encoder.cimpileHash(elementNamePool, this.elementNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> elementLabelPool = //
			Encoder.compilePool(handler.elementLabelPool, 0, Encoder.LabelComparator);
		final List<EncodeGroup> elementLabelHash = //
			Encoder.cimpileHash(elementLabelPool, this.elementLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeValue> attributeNamePool =
			Encoder.compilePool(handler.attributeNamePool, 0, Encoder.ValueComparator);
		final List<EncodeGroup> attributeNameHash = //
			Encoder.cimpileHash(attributeNamePool, this.attributeNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> attributeLabelPool =
			Encoder.compilePool(handler.attributeLabelPool, 0, Encoder.LabelComparator);
		final List<EncodeGroup> attributeLabelHash = //
			Encoder.cimpileHash(attributeLabelPool, this.attributeLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeGroup> elementXmlnsPool = //
			Encoder.compilePool(handler.elementXmlnsPool, 0, Encoder.IndexComparator);
		final List<EncodeGroup> elementChildrenPool = //
			Encoder.compilePool(handler.elementChildrenPool, 0, Encoder.IndexComparator);
		final List<EncodeGroup> elementAttributesPool = //
			Encoder.compilePool(handler.elementAttributesPool, 0, Encoder.IndexComparator);
		final List<EncodeElement> elementNodePool = //
			Encoder.compilePool(handler.elementNodePool, valuePool.size(), Encoder.IndexComparator);
		final List<EncodeAttribute> attributeNodePool = //
			Encoder.compilePool(handler.attributeNodePool, 0, Encoder.IndexComparator);
		Encoder.writeLists(target, uriHash);
		Encoder.writeLists(target, uriPool);
		Encoder.writeLists(target, valueHash);
		Encoder.writeLists(target, valuePool);
		Encoder.writeLists(target, xmlnsNameHash);
		Encoder.writeLists(target, xmlnsNamePool);
		Encoder.writeLists(target, xmlnsLabelHash);
		Encoder.writeItems(target, xmlnsLabelPool);
		Encoder.writeLists(target, elementNameHash);
		Encoder.writeLists(target, elementNamePool);
		Encoder.writeLists(target, elementLabelHash);
		Encoder.writeItems(target, elementLabelPool);
		Encoder.writeLists(target, attributeNameHash);
		Encoder.writeLists(target, attributeNamePool);
		Encoder.writeLists(target, attributeLabelHash);
		Encoder.writeItems(target, attributeLabelPool);
		Encoder.writeLists(target, elementXmlnsPool);
		Encoder.writeLists(target, elementChildrenPool);
		Encoder.writeLists(target, elementAttributesPool);
		Encoder.writeItems(target, elementNodePool);
		Encoder.writeItems(target, attributeNodePool);
		Encoder.writeInts(target, handler.documentElement.index - valuePool.size());
	}

	/**
	 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen
	 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
	 * {@code Prefix}.
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
	 * Diese Methode setzt die {@code xmlns}-Aktivierung. Wenn diese Option {@code true} ist, besitzen
	 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
	 * {@code Prefix}.
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
	 * Diese Methode gibt die {@code URI-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird die
	 * {@code URI-Hash-Table} erzeugt.
	 * 
	 * @return {@code URI-Hash}-Aktivierung.
	 */
	public boolean isUriHashEnabled() {
		return this.uriHashEnabled;
	}

	/**
	 * Diese Methode setzt das uriHashEnabled.
	 * 
	 * @param uriHashEnabled uriHashEnabled.
	 */
	public void setUriHashEnabled(final boolean uriHashEnabled) {
		this.uriHashEnabled = uriHashEnabled;
	}

	/**
	 * Diese Methode gibt das valueHashEnabled zurück.
	 * 
	 * @return valueHashEnabled.
	 */
	public boolean isValueHashEnabled() {
		return this.valueHashEnabled;
	}

	/**
	 * Diese Methode setzt das valueHashEnabled.
	 * 
	 * @param valueHashEnabled valueHashEnabled.
	 */
	public void setValueHashEnabled(final boolean valueHashEnabled) {
		this.valueHashEnabled = valueHashEnabled;
	}

	/**
	 * Diese Methode gibt das xmlnsNameHashEnabled zurück.
	 * 
	 * @return xmlnsNameHashEnabled.
	 */
	public boolean isXmlnsNameHashEnabled() {
		return this.xmlnsNameHashEnabled;
	}

	/**
	 * Diese Methode setzt das xmlnsNameHashEnabled.
	 * 
	 * @param xmlnsNameHashEnabled xmlnsNameHashEnabled.
	 */
	public void setXmlnsNameHashEnabled(final boolean xmlnsNameHashEnabled) {
		this.xmlnsNameHashEnabled = xmlnsNameHashEnabled;
	}

	/**
	 * Diese Methode gibt das xmlnsLabelHashEnabled zurück.
	 * 
	 * @return xmlnsLabelHashEnabled.
	 */
	public boolean isXmlnsLabelHashEnabled() {
		return this.xmlnsLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt das xmlnsLabelHashEnabled.
	 * 
	 * @param xmlnsLabelHashEnabled xmlnsLabelHashEnabled.
	 */
	public void setXmlnsLabelHashEnabled(final boolean xmlnsLabelHashEnabled) {
		this.xmlnsLabelHashEnabled = xmlnsLabelHashEnabled;
	}

	/**
	 * Diese Methode gibt das elementNameHashEnabled zurück.
	 * 
	 * @return elementNameHashEnabled.
	 */
	public boolean isElementNameHashEnabled() {
		return this.elementNameHashEnabled;
	}

	/**
	 * Diese Methode setzt das elementNameHashEnabled.
	 * 
	 * @param elementNameHashEnabled elementNameHashEnabled.
	 */
	public void setElementNameHashEnabled(final boolean elementNameHashEnabled) {
		this.elementNameHashEnabled = elementNameHashEnabled;
	}

	/**
	 * Diese Methode gibt das elementLabelHashEnabled zurück.
	 * 
	 * @return elementLabelHashEnabled.
	 */
	public boolean isElementLabelHashEnabled() {
		return this.elementLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt das elementLabelHashEnabled.
	 * 
	 * @param elementLabelHashEnabled elementLabelHashEnabled.
	 */
	public void setElementLabelHashEnabled(final boolean elementLabelHashEnabled) {
		this.elementLabelHashEnabled = elementLabelHashEnabled;
	}

	/**
	 * Diese Methode gibt das attributeNameHashEnabled zurück.
	 * 
	 * @return attributeNameHashEnabled.
	 */
	public boolean isAttributeNameHashEnabled() {
		return this.attributeNameHashEnabled;
	}

	/**
	 * Diese Methode setzt das attributeNameHashEnabled.
	 * 
	 * @param attributeNameHashEnabled attributeNameHashEnabled.
	 */
	public void setAttributeNameHashEnabled(final boolean attributeNameHashEnabled) {
		this.attributeNameHashEnabled = attributeNameHashEnabled;
	}

	/**
	 * Diese Methode gibt das attributeLabelHashEnabled zurück.
	 * 
	 * @return attributeLabelHashEnabled.
	 */
	public boolean isAttributeLabelHashEnabled() {
		return this.attributeLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt das attributeLabelHashEnabled.
	 * 
	 * @param attributeLabelHashEnabled attributeLabelHashEnabled.
	 */
	public void setAttributeLabelHashEnabled(final boolean attributeLabelHashEnabled) {
		this.attributeLabelHashEnabled = attributeLabelHashEnabled;
	}

}
