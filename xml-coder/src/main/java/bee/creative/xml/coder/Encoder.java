package bee.creative.xml.coder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
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
import bee.creative.array.ArrayCopy;
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
		 * Index in die Ausgabe.
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
	 * {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, wird die Wiederverwendung via
	 * {@link #reuse(EncodeItem)} signalisiert. Das Einfügen eines neuen Elements wird dagegen mit
	 * {@link #insert(EncodeItem)} angezeigt.
	 * <p>
	 * Die Implementation ähnelt einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
	 * 
	 * @see Unique
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class EncodeCache<GItem extends EncodeItem> extends Hash<GItem, GItem, GItem> implements
		Iterable<GItem> {

		/**
		 * Dieser Konstrukteur initialisiert die Größe der {@link Hash}-Tabelle mit {@code 512}.
		 */
		public EncodeCache() {
			this.verifyLength(128);
		}

		/**
		 * Diese Methode gibt das einzigartiges, zum gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente}
		 * Element zurück. Wenn ein solches Element gefunden wurde, wird dessen Wiederverwendung via
		 * {@link #reuse(EncodeItem)} signalisiert. Sollte dagegen kein {@link #equals(EncodeItem, EncodeItem) äquivalentes}
		 * Element gefunden werden, werden das gegebene Element in den {@link Hash} eingefügt, das Einfügen mit
		 * {@link #insert(EncodeItem)} angezeigt und das Element zurück gegeben.
		 * 
		 * @see #hash(EncodeItem)
		 * @see #equals(EncodeItem, EncodeItem)
		 * @param key Element.
		 * @return einzigartiges, {@link #equals(EncodeItem, EncodeItem) äquivalentes} Element.
		 */
		public final GItem get(final GItem key) {
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
			int hash = this.hash(key);
			hash ^= (hash >>> 20) ^ (hash >>> 12);
			return hash ^ (hash >>> 7) ^ (hash >>> 4);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean getEntryEquals(final GItem entry, final GItem key, final int hash) {
			return this.equals(entry, key);
		}

		/**
		 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Eingabe zurück. Die Eingabe ist nie
		 * {@code null}.
		 * 
		 * @param input Eingabe.
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

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index zur Verwaltung der Daten eingelesener
	 * {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem {

		/**
		 * Dieses Feld speichert das nächste {@link EncodeItem} im {@link EncodeCache}.
		 * 
		 * @see EncodeCache#getEntryNext(EncodeItem)
		 */
		EncodeItem next;

		/**
		 * Dieses Feld speichert beim Einlesen die absolute Häufigkeit und beim Speichern den Index des Datensatzes.
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
	 * Diese Klasse implementiert ein abstraktes {@link Unique} für {@link EncodeItem}s. In der Methode
	 * {@link #reuse(EncodeItem)} wird der Index der {@link EncodeItem}s erhöht.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der {@link EncodeItem}s.
	 */
	public static abstract class EncodeItemCache<GItem extends EncodeItem> extends EncodeCache<GItem> {

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #reuse(EncodeItem)}.
		 */
		int reuseCount;

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #insert(EncodeItem)}.
		 */
		int insertCount;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void reuse(final GItem value) {
			this.reuseCount++;
			value.index++;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insert(final GItem value) {
			this.insertCount++;
		}

		/**
		 * Diese Methode leert den {@link EncodeCache} und setzt die Zähler zurück.
		 */
		@Override
		public void clear() {
			super.clear();
			this.reuseCount = 0;
			this.insertCount = 0;
		}

		/**
		 * Diese Methode gibt die Anzahl der Aufrufe von {@link #reuse(EncodeItem)} zurück.
		 * 
		 * @return Anzahl der Aufrufe von {@link #reuse(EncodeItem)}.
		 */
		public int reuseCount() {
			return this.reuseCount;
		}

		/**
		 * Diese Methode gibt die Anzahl der Aufrufe von {@link #insert(EncodeItem)} zurück.
		 * 
		 * @return Anzahl der Aufrufe von {@link #insert(EncodeItem)}.
		 */
		public int insertCount() {
			return this.insertCount;
		}

		/**
		 * Diese Methode fügt alle {@link EncodeItem}s des {@link EncodeItemCache}s in eine neue {@link List} ein, sortiert
		 * diese {@link List} mit dem gegebenen {@link Comparator}, setzt den Index der Elemente unter Beachtung des
		 * gegebenen Offsets, entfernt alle Elemente aus dem {@link EncodeItemCache} und gibt die {@link List} zurück. Der
		 * Index des {@code i} -ten {@link EncodeItem}s der erzeugetn {@link List} ergibt sich aus:
		 * 
		 * <pre>list.get(i).index = i + offset</pre>
		 * 
		 * @see EncodeItem#index
		 * @see EncodeItemCache#clear()
		 * @see Collections#sort(List, Comparator)
		 * @param offset Offset der Indizes.
		 * @param comparator {@link Comparator}.
		 * @return {@link EncodeItem}-{@link List}.
		 */
		public final List<GItem> compile(final int offset, final Comparator<? super GItem> comparator) {
			final List<GItem> list = new ArrayList<GItem>(this.size());
			Iterators.appendAll(list, this.getEntries());
			this.clear();
			Collections.sort(list, comparator);
			for(int i = 0, size = list.size(); i < size; i++){
				list.get(i).index = i + offset;
			}
			return list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, this.getClass().getSimpleName(), "reuseCount", this.reuseCount,
				"insertCount", this.insertCount);
		}

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
	 * Diese Klasse implementiert einen abstrakten {@link EncodeItemCache} für {@link EncodeList}s.
	 * 
	 * @see EncodeItemCache
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der {@link EncodeList}s.
	 */
	public static abstract class EncodeListCache<GItem extends EncodeList> extends EncodeItemCache<GItem> {

		/**
		 * Dieses Feld speichert die Anzahl der Werte in den {@link EncodeList}s.
		 * 
		 * @see EncodeList#length()
		 */
		int valueCount;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insert(final GItem value) {
			super.insert(value);
			this.valueCount += value.length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			super.clear();
			this.valueCount = 0;
		}

		/**
		 * Diese Methode gibt die Anzahl der Werte in den {@link EncodeList}s zurück.
		 * 
		 * @see EncodeList#length()
		 * @return Anzahl der Werte in den {@link EncodeList}s.
		 */
		public int valueCount() {
			return this.valueCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, this.getClass().getSimpleName(), "reuseCount", this.reuseCount,
				"insertCount", this.insertCount, "valueCount", this.valueCount);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValue extends EncodeList {

		/**
		 * Dieses Feld speichert das verwendete {@link Charset}.
		 */
		public static final Charset CHARSET = Charset.forName("UTF-8");

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
			return this.value.getBytes(EncodeValue.CHARSET).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			final byte[] bytes = this.value.getBytes(EncodeValue.CHARSET);
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
	 * Diese Klasse implementiert den {@link EncodeListCache} zu {@link EncodeValue}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValueCache extends EncodeListCache<EncodeValue> {

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
			return "Label[" + this.index + "]( " + this.name + " / " + this.uri + " )";
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodeItemCache} zu {@link EncodeLabel}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabelCache extends EncodeItemCache<EncodeLabel> {

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code URI}s.
		 */
		public final EncodeValueCache uriCache;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für den {@code Name}s.
		 */
		public final EncodeValueCache nameCache;

		/**
		 * Dieser Konstrukteur initialisiert {@code URI}- und {@code Name}-{@link EncodeValueCache}.
		 * 
		 * @param uriCache {@link EncodeValueCache} für die {@code URI}s.
		 * @param nameCache {@link EncodeValueCache} für den {@code Name}s.
		 */
		public EncodeLabelCache(final EncodeValueCache uriCache, final EncodeValueCache nameCache) {
			this.uriCache = uriCache;
			this.nameCache = nameCache;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @return einzigartiges {@link EncodeLabel}.
		 */
		public EncodeLabel unique(final String uri, final String name) {
			return this.get(new EncodeLabel(this.uriCache.unique(uri), this.nameCache.unique(name)));
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
	 * Diese Klasse implementiert das {@link Element}-{@link EncodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementNode extends EncodeItem {

		/**
		 * Dieses Feld speichert das {@link EncodeLabel}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabel label;

		/**
		 * Dieses Feld speichert die {@link EncodeElementXmlns}.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public final EncodeElementXmlns xmlns;

		/**
		 * Dieses Feld speichert die {@link EncodeElementChildren}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeElementChildren children;

		/**
		 * Dieses Feld speichert die {@link EncodeElementAttributes}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeElementAttributes attributes;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param label {@link EncodeLabel} für {@link Element#getLocalName()} und {@link Element#getNamespaceURI()}.
		 * @param xmlns {@link EncodeElementXmlns} für {@link Element#lookupPrefix(String)} und
		 *        {@link Element#lookupNamespaceURI(String)}.
		 * @param children {@link EncodeElementChildren} für {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeElementAttributes} für {@link Element#getAttributes()}.
		 */
		public EncodeElementNode(final EncodeLabel label, final EncodeElementXmlns xmlns,
			final EncodeElementChildren children, final EncodeElementAttributes attributes) {
			this.label = label;
			this.xmlns = xmlns;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			Encoder.writeInts(target, this.label.index, this.xmlns.index, this.children.index, this.attributes.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, "Element[" + this.index + "]", this.label, this.xmlns, this.attributes,
				this.children);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodeItemCache} zu {@link EncodeElementNode}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementNodeCache extends EncodeItemCache<EncodeElementNode> {

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die {@link Element}-{@code Label}s.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabelCache labelCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementXmlnsCache} für die {@code Xmlns}-{@code Label}s.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public final EncodeElementXmlnsCache xmlnsCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementChildrenCache} für die {@link Element#getChildNodes()}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeElementChildrenCache childrenCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementAttributesCache} für die {@link Element#getAttributes()}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeElementAttributesCache attributesCache;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItemCache}s und {@link EncodeLabelCache}s.
		 * 
		 * @param labelCache {@link EncodeLabelCache} für die {@link Element}-{@code Label}s.
		 * @param xmlnsCache {@link EncodeElementXmlnsCache} für die {@code Xmlns}-{@code Label}s.
		 * @param childrenCache {@link EncodeElementChildrenCache} für die {@link Element#getChildNodes()}.
		 * @param attributesCache {@link EncodeElementAttributesCache} für die {@link Element#getAttributes()}.
		 */
		public EncodeElementNodeCache(final EncodeLabelCache labelCache, final EncodeElementXmlnsCache xmlnsCache,
			final EncodeElementChildrenCache childrenCache, final EncodeElementAttributesCache attributesCache) {
			this.labelCache = labelCache;
			this.xmlnsCache = xmlnsCache;
			this.childrenCache = childrenCache;
			this.attributesCache = attributesCache;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElementNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri {@code URI} des {@link Element}-{@code Label}s.
		 * @param name {@code Name} des {@link Element}-{@code Label}s.
		 * @param xmlns {@link EncodeLabel}-{@link List} der {@code Xmlns}-{@code Label}s.
		 * @param children {@link EncodeItem}-{@link List} der {@link Element#getChildNodes()}.
		 * @param attributes {@link EncodeAttributeNode}-{@link List} der {@link Element#getAttributes()}.
		 * @return einzigartige {@link EncodeElementNode}.
		 */
		public EncodeElementNode unique(final String uri, final String name, final List<EncodeLabel> xmlns,
			final List<EncodeItem> children, final List<EncodeAttributeNode> attributes) {
			return this.get(new EncodeElementNode(this.labelCache.unique(uri, name), this.xmlnsCache.unique(xmlns),
				this.childrenCache.unique(children), this.attributesCache.unique(attributes)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementNode input) {
			return Objects.hash(input.label, input.xmlns, input.children, input.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementNode input1, final EncodeElementNode input2) {
			return (input1.label == input2.label) && (input1.xmlns == input2.xmlns) && (input1.children == input2.children)
				&& (input1.attributes == input2.attributes);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} von {@link EncodeLabel}s.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementXmlns extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeLabel}-{@link List}, deren Elemente via {@link Encoder#XmlnsComparator}
		 * sortiert wurden.
		 */
		public final List<EncodeLabel> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeLabel}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeElementXmlns() {
			this(new ArrayList<EncodeLabel>());
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeLabel}-{@link List}.
		 * 
		 * @param value {@link EncodeLabel}-{@link List}.
		 */
		public EncodeElementXmlns(final List<EncodeLabel> value) {
			this.values = value;
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
			return Objects.toStringCall(true, "LabelList[" + this.index + "]", this.values);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodeListCache} zu {@link EncodeElementXmlns}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementXmlnsCache extends EncodeListCache<EncodeElementXmlns> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeElementXmlns} mit der gegebenen {@link EncodeLabel}-
		 * {@link List} zurück.
		 * 
		 * @param value {@link EncodeLabel}-{@link List}.
		 * @return einzigartige {@link EncodeElementXmlns}.
		 */
		public EncodeElementXmlns unique(final List<EncodeLabel> value) {
			return this.get(new EncodeElementXmlns(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementXmlns input) {
			return Objects.hash(input.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementXmlns input1, final EncodeElementXmlns input2) {
			return input1.values.equals(input2.values);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} von {@link EncodeItem}s.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementChildren extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List}.
		 */
		public final List<EncodeItem> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeElementChildren() {
			this(new ArrayList<EncodeItem>());
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List}.
		 * 
		 * @param value {@link EncodeItem}-{@link List}.
		 */
		public EncodeElementChildren(final List<EncodeItem> value) {
			this.values = value;
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
			return Objects.toStringCall(true, "Children[" + this.index + "]", this.values);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodeListCache} zu {@link EncodeElementChildren}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementChildrenCache extends EncodeListCache<EncodeElementChildren> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeElementChildren} mit der gegebenen {@link EncodeItem}-
		 * {@link List} zurück.
		 * 
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeElementChildren}.
		 */
		public EncodeElementChildren unique(final List<EncodeItem> value) {
			return this.get(new EncodeElementChildren(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementChildren input) {
			return Objects.hash(input.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementChildren input1, final EncodeElementChildren input2) {
			return input1.values.equals(input2.values);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} von {@link EncodeAttributeNode}s.
	 * 
	 * @see Element#getAttributes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementAttributes extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeAttributeNode}-{@link List}.
		 */
		public final List<EncodeAttributeNode> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeAttributeNode}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeElementAttributes() {
			this(new ArrayList<EncodeAttributeNode>());
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeAttributeNode}-{@link List}.
		 * 
		 * @param value {@link EncodeAttributeNode}-{@link List}.
		 */
		public EncodeElementAttributes(final List<EncodeAttributeNode> value) {
			this.values = value;
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
			return Objects.toStringCall(true, "AttributeList[" + this.index + "]", this.values);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodeListCache} zu {@link EncodeElementAttributes}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeElementAttributesCache extends EncodeListCache<EncodeElementAttributes> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeElementAttributes} mit der gegebenen {@link EncodeAttributeNode}
		 * - {@link List} zurück.
		 * 
		 * @param value {@link EncodeAttributeNode}-{@link List}.
		 * @return einzigartige {@link EncodeElementAttributes}.
		 */
		public EncodeElementAttributes unique(final List<EncodeAttributeNode> value) {
			return this.get(new EncodeElementAttributes(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElementAttributes input) {
			return Objects.hash(input.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElementAttributes input1, final EncodeElementAttributes input2) {
			return input1.values.equals(input2.values);
		}

	}

	/**
	 * Diese Klasse implementiert das {@link Attr}-{@link EncodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeNode extends EncodeItem {

		/**
		 * Dieses Feld speichert das {@link EncodeLabel}.
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
		 * @param label {@link EncodeLabel} für {@link Attr#getLocalName()} und {@link Attr#getNamespaceURI()}.
		 * @param value {@link EncodeValue} für {@link Attr#getNodeValue()}.
		 */
		public EncodeAttributeNode(final EncodeLabel label, final EncodeValue value) {
			this.label = label;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final EncodeTarget target) throws IOException {
			Encoder.writeInts(target, this.label.index, this.value.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "Attribute[" + this.index + "]( " + this.label + " = " + this.value + " )";
		}
	}

	/**
	 * Diese Klasse implementiert den {@link EncodeItemCache} zu {@link EncodeAttributeNode}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributeNodeCache extends EncodeItemCache<EncodeAttributeNode> {

		/**
		 * Dieses Feld speichert das {@link EncodeLabelCache} für die {@link Attr}-{@code Label}s.
		 */
		public final EncodeLabelCache labelCache;

		/**
		 * Dieses Feld speichert das {@link EncodeValueCache} für den {@code Value}.
		 */
		public final EncodeValueCache valueCache;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param labelCache {@link EncodeLabelCache} für die {@link Attr}-{@code Label}s.
		 * @param valueCache {@link EncodeValueCache} für den {@code Value}.
		 */
		public EncodeAttributeNodeCache(final EncodeLabelCache labelCache, final EncodeValueCache valueCache) {
			this.labelCache = labelCache;
			this.valueCache = valueCache;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttributeNode} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri Namensraum.
		 * @param name Beschriftung.
		 * @param value Textwert.
		 * @return einzigartiges {@link EncodeAttributeNode}.
		 */
		public EncodeAttributeNode unique(final String uri, final String name, final String value) {
			return this.get(new EncodeAttributeNode(this.labelCache.unique(uri, name), this.valueCache.unique(value)));
		}

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

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code URI} und {@code Prefix} als
	 * {@link EncodeLabel}s während des Einlesens eines {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class EncodeXmlnsStack {

		/**
		 * Dieses Feld speichert die {@code URI}-{@code Prefix}-{@link Map}.
		 */
		final Map<String, String> map;

		/**
		 * Dieses Feld speichert den nächsten {@link EncodeXmlnsStack} oder {@code null}.
		 */
		final EncodeXmlnsStack next;

		/**
		 * Dieser Konstrukteur initialisiert den leeren {@link EncodeXmlnsStack}.
		 */
		public EncodeXmlnsStack() {
			this(null);
		}

		/**
		 * Dieser Konstrukteur initialisiert den nächsten {@link EncodeXmlnsStack}.
		 * 
		 * @param next nächster {@link EncodeXmlnsStack} oder {@code null}.
		 */
		public EncodeXmlnsStack(final EncodeXmlnsStack next) {
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
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Inhalte eine {@link Element}s während des Einlesens eines
	 * {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class EncodeCursorStack {

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
		final List<EncodeLabel> xmlns;

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List} für die {@link Element#getChildNodes()}.
		 */
		final List<EncodeItem> children;

		/**
		 * Dieses Feld speichert die {@link EncodeAttributeNode}-{@link List} für die {@link Element#getAttributes()}.
		 */
		final List<EncodeAttributeNode> attributes;

		/**
		 * Dieses Feld speichert den nächsten {@link EncodeCursorStack} oder {@code null}.
		 */
		final EncodeCursorStack next;

		/**
		 * Dieser Konstrukteur initialisiert den leeren {@link EncodeCursorStack}.
		 */
		public EncodeCursorStack() {
			this(null, null, null, null, null);
		}

		/**
		 * Dieser Konstrukteur initialisiert den {@link EncodeCursorStack}.
		 * 
		 * @param uri {@code URI} ({@link Element#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Element#getLocalName()}).
		 * @param xmlns {@link EncodeLabel}-{@link List} für die Paare aus {@code URI} und {@code Prefix} (
		 *        {@link Node#lookupPrefix(String)}, {@link Node#lookupNamespaceURI(String)}).
		 * @param attributes {@link EncodeAttributeNode}-{@link List} für die {@link Element#getAttributes()}.
		 * @param next nächster {@link EncodeCursorStack} oder {@code null}.
		 */
		public EncodeCursorStack(final String uri, final String name, final List<EncodeLabel> xmlns,
			final List<EncodeAttributeNode> attributes, final EncodeCursorStack next) {
			this.uri = uri;
			this.name = name;
			this.xmlns = xmlns;
			this.children = new ArrayList<EncodeItem>(0);
			this.attributes = attributes;
			this.next = next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, "EncodeCursorStack", this.uri, this.name, this.xmlns, this.children,
				this.attributes, this.next);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ContentHandler} zum Einlsenen eines {@link Document}s mit Hilfe eines
	 * {@link XMLReader}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeContentHandler implements ContentHandler {

		/**
		 * Dieses Feld speichert die aktuellen Paare aus {@code URI} und {@code Prefix}.
		 */
		List<EncodeLabel> xmlns;

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsStack} für die aktuellen Paare aus {@code URI} und {@code Prefix}
		 * oder {@code null}.
		 */
		EncodeXmlnsStack xmlnsStack;

		/**
		 * Dieses Feld speichert den {@link EncodeCursorStack} für das aktuelle {@link Element}.
		 */
		EncodeCursorStack cursorStack;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code URI}s.
		 * 
		 * @see Node#getNamespaceURI()
		 */
		public final EncodeValueCache uriValueCache;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code Value}s.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 */
		public final EncodeValueCache valueCache;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code Prefix}es.
		 * 
		 * @see Node#getPrefix()
		 */
		public final EncodeValueCache xmlnsNameCache;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@code Xmlns}-{@code Prefix}
		 * .
		 * 
		 * @see Node#getNamespaceURI()
		 * @see Node#getPrefix()
		 */
		public final EncodeLabelCache xmlnsLabelCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementNodeCache} für die {@link Element}-Daten.
		 * 
		 * @see EncodeElementNode
		 */
		public final EncodeElementNodeCache elementCache;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@link Element}-{@code Name}s.
		 * 
		 * @see Element#getLocalName()
		 */
		public final EncodeValueCache elementNameCache;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@link Element}-{@code Name}
		 * .
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabelCache elementLabelCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementXmlnsCache} für die {@code Xmlns}.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 */
		public final EncodeElementXmlnsCache elementXmlnsCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementChildrenCache} für die {@link Element#getChildNodes()}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeElementChildrenCache elementChildrenCache;

		/**
		 * Dieses Feld speichert den {@link EncodeElementChildrenCache} für die {@link Element#getAttributes()}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeElementAttributesCache elementAttributesCache;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeNodeCache} für die {@link Attr}-Daten.
		 * 
		 * @see EncodeAttributeNode
		 */
		public final EncodeAttributeNodeCache attributeCache;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@link Attr}-{@code Name}s.
		 * 
		 * @see Attr#getLocalName()
		 */
		public final EncodeValueCache attributeNameCache;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@link Attr}-{@code Name}.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		public final EncodeLabelCache attributeLabelCache;

		/**
		 * Dieses Feld speichert das {@code Document}-{@link EncodeItem}.
		 * 
		 * @see Document#getDocumentElement()
		 */
		public EncodeItem documentElement;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItemCache}s und {@link EncodeListCache}s.
		 */
		public EncodeContentHandler() {
			this.cursorStack = new EncodeCursorStack();
			this.uriValueCache = new EncodeValueCache();
			this.valueCache = new EncodeValueCache();
			this.xmlnsNameCache = new EncodeValueCache();
			this.xmlnsLabelCache = new EncodeLabelCache(this.uriValueCache, this.xmlnsNameCache);
			this.elementNameCache = new EncodeValueCache();
			this.elementLabelCache = new EncodeLabelCache(this.uriValueCache, this.elementNameCache);
			this.elementXmlnsCache = new EncodeElementXmlnsCache();
			this.elementChildrenCache = new EncodeElementChildrenCache();
			this.elementAttributesCache = new EncodeElementAttributesCache();
			this.elementCache =
				new EncodeElementNodeCache(this.elementLabelCache, this.elementXmlnsCache, this.elementChildrenCache,
					this.elementAttributesCache);
			this.attributeNameCache = new EncodeValueCache();
			this.attributeLabelCache = new EncodeLabelCache(this.uriValueCache, this.attributeNameCache);
			this.attributeCache = new EncodeAttributeNodeCache(this.attributeLabelCache, this.valueCache);
			this.startPrefixMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
			this.startPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endPrefixMapping(final String prefix) {
			for(EncodeXmlnsStack scope = this.xmlnsStack; scope != null; scope = scope.next){
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
			EncodeXmlnsStack scope = this.xmlnsStack;
			if((scope == null) || scope.map.containsValue(prefix)){
				scope = new EncodeXmlnsStack(scope);
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
			final EncodeCursorStack oldCursor = this.cursorStack;
			final EncodeCursorStack newCursor = this.cursorStack.next;
			final List<EncodeItem> oldChildren = oldCursor.children;
			final List<EncodeItem> newChildren = new ArrayList<EncodeItem>(oldChildren.size());
			final StringBuilder textValue = new StringBuilder();
			for(final EncodeItem oldNode: oldChildren){
				if(oldNode instanceof EncodeValue){
					final EncodeValue textChildNode = (EncodeValue)oldNode;
					textValue.append(textChildNode.value);
				}else{
					if(textValue.length() != 0){
						newChildren.add(this.valueCache.unique(textValue.toString()));
						textValue.setLength(0);
					}
					newChildren.add(oldNode);
				}
			}
			if(textValue.length() != 0){
				newChildren.add(this.valueCache.unique(textValue.toString()));
			}
			final EncodeElementNode elementNode =
				this.elementCache.unique(oldCursor.uri, oldCursor.name, oldCursor.xmlns, newChildren, oldCursor.attributes);
			newCursor.children.add(elementNode);
			this.cursorStack = newCursor;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
			if(this.xmlns == null){
				final Map<String, String> map = new HashMap<String, String>();
				final ArrayList<EncodeLabel> xmlns = new ArrayList<EncodeLabel>();
				for(EncodeXmlnsStack scope = this.xmlnsStack; scope != null; scope = scope.next){
					for(final Entry<String, String> entry: scope.map.entrySet()){
						final String xmlnsName = entry.getValue();
						if(!map.containsKey(xmlnsName)){
							final String xmlnsUri = entry.getKey();
							map.put(xmlnsName, xmlnsUri);
							xmlns.add(this.xmlnsLabelCache.unique(xmlnsUri, xmlnsName));
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
			final List<EncodeAttributeNode> attributes = new ArrayList<EncodeAttributeNode>(size);
			for(int i = 0; i < size; i++){
				attributes.add(this.attributeCache.unique(atts.getURI(i), atts.getLocalName(i), atts.getValue(i)));
			}
			if(size > 1){
				Collections.sort(attributes, Encoder.AttributeComparator);
			}
			this.cursorStack = new EncodeCursorStack(uri, name, this.xmlns, attributes, this.cursorStack);
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
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttributeNode} primär
	 * nach den {@code Name}-{@link EncodeValue} und sekundär nach den {@code URI}-{@link EncodeValue} ihrer
	 * {@link EncodeLabel}s via {@link Encoder#ValueComparator}.
	 */
	static final Comparator<EncodeAttributeNode> AttributeComparator = new Comparator<EncodeAttributeNode>() {

		@Override
		public int compare(final EncodeAttributeNode value1, final EncodeAttributeNode value2) {
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
	 * @see ArrayCopy#copy(int[], int, byte[], int, int)
	 * @see EncodeTarget#write(byte[], int, int)
	 * @param target {@link EncodeTarget}.
	 * @param value {@code int}-Array.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 */
	static void writeInts(final EncodeTarget target, final int... value) throws IOException {
		final int count = value.length << 2;
		final byte[] array = new byte[count];
		ArrayCopy.copy(value, 0, array, 0, count);
		target.write(array, 0, count);
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
	 * Dieser Konstrukteur initialisiert den {@link Encoder}.
	 */
	public Encoder() {
	}

	/**
	 * Diese Methode liest die gegebene Quell-{@link File} mit dem einem neuen {@link XMLReader} in einen neuen
	 * {@link EncodeContentHandler} ein und speichert dessen Daten in die gegebene Ziel-{@link File}.
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
			new RandomAccessFile(target, "rw")));
	}

	/**
	 * Diese Methode liest die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in einen neuen
	 * {@link EncodeContentHandler} ein und speichert dessen Daten in das gegebene {@link EncodeTarget}.
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
		final EncodeContentHandler handler = new EncodeContentHandler();
		reader.setContentHandler(handler);
		reader.parse(source);
		final List<EncodeValue> uriCharsCache = handler.uriValueCache.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> xmlnsCharsCache = handler.xmlnsNameCache.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> elementCharsCache = handler.elementNameCache.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> attributeCharsCache = handler.attributeNameCache.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> valueCache = handler.valueCache.compile(0, Encoder.IndexComparator);
		final List<EncodeLabel> xmlnsLabelCache = handler.xmlnsLabelCache.compile(0, Encoder.LabelComparator);
		final List<EncodeLabel> elementLabelCache = handler.elementLabelCache.compile(0, Encoder.LabelComparator);
		final List<EncodeLabel> attributeLabelCache = handler.attributeLabelCache.compile(0, Encoder.LabelComparator);
		final List<EncodeElementXmlns> elementXmlnsCache = handler.elementXmlnsCache.compile(0, Encoder.IndexComparator);
		final List<EncodeElementChildren> elementChildrenCache =
			handler.elementChildrenCache.compile(0, Encoder.IndexComparator);
		final List<EncodeElementAttributes> elementAttributesCache =
			handler.elementAttributesCache.compile(0, Encoder.IndexComparator);
		final List<EncodeElementNode> elementCache =
			handler.elementCache.compile(valueCache.size(), Encoder.IndexComparator);
		final List<EncodeAttributeNode> attributeCache = handler.attributeCache.compile(0, Encoder.IndexComparator);
		Encoder.writeLists(target, uriCharsCache);
		Encoder.writeLists(target, xmlnsCharsCache);
		Encoder.writeLists(target, elementCharsCache);
		Encoder.writeLists(target, attributeCharsCache);
		Encoder.writeLists(target, valueCache);
		Encoder.writeItems(target, xmlnsLabelCache);
		Encoder.writeItems(target, elementLabelCache);
		Encoder.writeItems(target, attributeLabelCache);
		Encoder.writeLists(target, elementXmlnsCache);
		Encoder.writeLists(target, elementChildrenCache);
		Encoder.writeLists(target, elementAttributesCache);
		Encoder.writeItems(target, elementCache);
		Encoder.writeItems(target, attributeCache);
		Encoder.writeInts(target, handler.documentElement.index - valueCache.size());
	}

}
