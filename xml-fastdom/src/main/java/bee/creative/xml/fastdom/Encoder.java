package bee.creative.xml.fastdom;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index zur Verwaltung der Daten eingelesener
	 * {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class EncodeItem {

		/**
		 * Dieses Feld speichert beim Einlesen die absolute Häufigkeit und beim Speichern den Index des Datensatzes.
		 */
		public int index = 1;

		/**
		 * Diese Methode schreibt den Datensatz in das gegebenen {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
		 */
		public abstract void write(final RandomAccessFile file) throws IOException;

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Unique} für {@link EncodeItem}s. In der Methode
	 * {@link #reuse(EncodeItem, EncodeItem)} wird der Index der {@link EncodeItem}s erhöht.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der {@link EncodeItem}s (Ein- und Ausgabe).
	 */
	public static abstract class EncodeItemCache<GValue extends EncodeItem> extends Unique<GValue, GValue> {

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #reuse(EncodeItem, EncodeItem)}.
		 */
		public int reuseCount;

		/**
		 * Dieses Feld speichert die Anzahl der Aufrufe von {@link #compile(EncodeItem)}.
		 */
		public int compileCount;

		/**
		 * Dieser Konstrukteur initialisiert die interne {@link Map} mit {@link Unique#HASHMAP}.
		 */
		public EncodeItemCache() {
			super(Unique.HASHMAP);
		}

		/**
		 * Diese Methode leert die interne {@link Map} und setzt die Zähler zurück.
		 */
		public void clear() {
			this.map.clear();
			this.reuseCount = 0;
			this.compileCount = 0;
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
		public final List<GValue> compile(final int offset, final Comparator<? super GValue> comparator) {
			final List<GValue> list = new ArrayList<GValue>(this.entryMap().values());
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
		protected void reuse(final GValue input, final GValue output) {
			this.reuseCount++;
			output.index++;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue compile(final GValue input) {
			this.compileCount++;
			return input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, this.getClass().getSimpleName(), "reuseCount", this.reuseCount,
				"compileCount", this.compileCount);
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
	 * @param <GValue> Typ der {@link EncodeList}s (Ein- und Ausgabe).
	 */
	public static abstract class EncodeListCache<GValue extends EncodeList> extends EncodeItemCache<GValue> {

		/**
		 * Dieses Feld speichert die Anzahl der Werte in den {@link EncodeList}s.
		 */
		public int valueLength;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.valueLength = 0;
			super.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GValue compile(final GValue input) {
			this.valueLength += input.length();
			return super.compile(input);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link EncodeList} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeValue extends EncodeList {

		/**
		 * Dieses Feld speichert das verwendete {@link Charset}.
		 */
		public static final Charset CHARSET = Charset.forName("UTF-8");

		/**
		 * Dieses Feld speichert den {@link String}.
		 */
		public final String value;

		/**
		 * Dieses Feld speichert die Anzahl der {@code byte}s.
		 */
		public final int length;

		/**
		 * Dieser Konstrukteur initialisiert den {@link String}.
		 * 
		 * @param value {@link String}.
		 */
		public EncodeValue(final String value) {
			this.value = value;
			this.length = value.getBytes(EncodeValue.CHARSET).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final RandomAccessFile file) throws IOException {
			final byte[] bytes = this.value.getBytes(EncodeValue.CHARSET);
			Encoder.write(file, bytes, 0, bytes.length);
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
	public static final class EncodeValueCache extends EncodeListCache<EncodeValue> {

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
	public static final class EncodeLabel extends EncodeItem {

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
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeValues(file, this.uri.index, this.name.index);
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
	public static final class EncodeLabelCache extends EncodeItemCache<EncodeLabel> {

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
	public static final class EncodeElement extends EncodeItem {

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
		public EncodeElement(final EncodeLabel label, final EncodeElementXmlns xmlns, final EncodeElementChildren children,
			final EncodeElementAttributes attributes) {
			this.label = label;
			this.xmlns = xmlns;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeValues(file, this.label.index, this.xmlns.index, this.children.index, this.attributes.index);
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
	 * Diese Klasse implementiert den {@link EncodeItemCache} zu {@link EncodeElement}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeElementCache extends EncodeItemCache<EncodeElement> {

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
		public EncodeElementCache(final EncodeLabelCache labelCache, final EncodeElementXmlnsCache xmlnsCache,
			final EncodeElementChildrenCache childrenCache, final EncodeElementAttributesCache attributesCache) {
			this.labelCache = labelCache;
			this.xmlnsCache = xmlnsCache;
			this.childrenCache = childrenCache;
			this.attributesCache = attributesCache;
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
		public EncodeElement unique(final String uri, final String name, final List<EncodeLabel> xmlns,
			final List<EncodeItem> children, final List<EncodeAttribute> attributes) {
			return this.get(new EncodeElement(this.labelCache.unique(uri, name), this.xmlnsCache.unique(xmlns),
				this.childrenCache.unique(children), this.attributesCache.unique(attributes)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int hash(final EncodeElement input) {
			return Objects.hash(input.label, input.xmlns, input.children, input.attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeElement input1, final EncodeElement input2) {
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
	public static final class EncodeElementXmlns extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeLabel}-{@link List}, deren Elemente via {@link Encoder#XmlnsComparator}
		 * sortiert wurden.
		 */
		public final List<EncodeLabel> values;

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
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeIndices(file, this.values);
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
	public static final class EncodeElementXmlnsCache extends EncodeListCache<EncodeElementXmlns> {

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
	public static final class EncodeElementChildren extends EncodeList {

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
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeIndices(file, this.values);
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
	public static final class EncodeElementChildrenCache extends EncodeListCache<EncodeElementChildren> {

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
	 * Diese Klasse implementiert eine {@link EncodeList} von {@link EncodeAttribute}s.
	 * 
	 * @see Element#getAttributes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeElementAttributes extends EncodeList {

		/**
		 * Dieses Feld speichert die {@link EncodeAttribute}-{@link List}.
		 */
		public final List<EncodeAttribute> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeAttribute}-{@link List}.
		 * 
		 * @param value {@link EncodeAttribute}-{@link List}.
		 */
		public EncodeElementAttributes(final List<EncodeAttribute> value) {
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
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeIndices(file, this.values);
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
	public static final class EncodeElementAttributesCache extends EncodeListCache<EncodeElementAttributes> {

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeElementAttributes} mit der gegebenen {@link EncodeAttribute}-
		 * {@link List} zurück.
		 * 
		 * @param value {@link EncodeAttribute}-{@link List}.
		 * @return einzigartige {@link EncodeElementAttributes}.
		 */
		public EncodeElementAttributes unique(final List<EncodeAttribute> value) {
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
	public static final class EncodeAttribute extends EncodeItem {

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
		public EncodeAttribute(final EncodeLabel label, final EncodeValue value) {
			this.label = label;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final RandomAccessFile file) throws IOException {
			Encoder.writeValues(file, this.label.index, this.value.index);
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
	 * Diese Klasse implementiert den {@link EncodeItemCache} zu {@link EncodeAttribute}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeAttributeCache extends EncodeItemCache<EncodeAttribute> {

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
		public EncodeAttributeCache(final EncodeLabelCache labelCache, final EncodeValueCache valueCache) {
			this.labelCache = labelCache;
			this.valueCache = valueCache;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttribute} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param uri Namensraum.
		 * @param name Beschriftung.
		 * @param value Textwert.
		 * @return einzigartiges {@link EncodeAttribute}.
		 */
		public EncodeAttribute unique(final String uri, final String name, final String value) {
			return this.get(new EncodeAttribute(this.labelCache.unique(uri, name), this.valueCache.unique(value)));
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
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code URI} und {@code Prefix} als
	 * {@link EncodeLabel}s während des Einlesens eines {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class EncodeXmlnsStack {

		/**
		 * Dieses Feld speichert die {@code URI}-{@code Prefix}-{@link Map}.
		 */
		public final Map<String, String> map;

		/**
		 * Dieses Feld speichert den nächsten {@link EncodeXmlnsStack} oder {@code null}.
		 */
		public final EncodeXmlnsStack next;

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
	public static final class EncodeCursorStack {

		/**
		 * Dieses Feld speichert die {@code URI}.
		 * 
		 * @see Element#getNamespaceURI()
		 */
		public final String uri;

		/**
		 * Dieses Feld speichert den {@code Name}.
		 * 
		 * @see Element#getLocalName()
		 */
		public final String name;

		/**
		 * Dieses Feld speichert die {@link EncodeLabel}-{@link List} für die Paare aus {@code URI} und {@code Prefix}.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 */
		public final List<EncodeLabel> xmlns;

		/**
		 * Dieses Feld speichert die {@link EncodeItem}-{@link List} für die {@link Element#getChildNodes()}.
		 */
		public final List<EncodeItem> children;

		/**
		 * Dieses Feld speichert die {@link EncodeAttribute}-{@link List} für die {@link Element#getAttributes()}.
		 */
		public final List<EncodeAttribute> attributes;

		/**
		 * Dieses Feld speichert den nächsten {@link EncodeCursorStack} oder {@code null}.
		 */
		public final EncodeCursorStack next;

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
		 * @param attributes {@link EncodeAttribute}-{@link List} für die {@link Element#getAttributes()}.
		 * @param next nächster {@link EncodeCursorStack} oder {@code null}.
		 */
		public EncodeCursorStack(final String uri, final String name, final List<EncodeLabel> xmlns,
			final List<EncodeAttribute> attributes, final EncodeCursorStack next) {
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
	public static final class EncodeContentHandler implements ContentHandler {

		/**
		 * Dieses Feld speichert die aktuellen Paare aus {@code URI} und {@code Prefix}.
		 */
		protected List<EncodeLabel> xmlns;

		/**
		 * Dieses Feld speichert den {@link EncodeXmlnsStack} für die aktuellen Paare aus {@code URI} und {@code Prefix}
		 * oder {@code null}.
		 */
		protected EncodeXmlnsStack xmlnsStack;

		/**
		 * Dieses Feld speichert den {@link EncodeCursorStack} für das aktuelle {@link Element}.
		 */
		protected EncodeCursorStack cursorStack;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code URI}s.
		 * 
		 * @see Node#getNamespaceURI()
		 */
		public final EncodeValueCache uriValuePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code Value}s.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 */
		public final EncodeValueCache valuePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@code Prefix}es.
		 * 
		 * @see Node#getPrefix()
		 */
		public final EncodeValueCache xmlnsNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@code Xmlns}-{@code Prefix}
		 * .
		 * 
		 * @see Node#getNamespaceURI()
		 * @see Node#getPrefix()
		 */
		public final EncodeLabelCache xmlnsLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementCache} für die {@link Element}-Daten.
		 * 
		 * @see EncodeElement
		 */
		public final EncodeElementCache elementPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@link Element}-{@code Name}s.
		 * 
		 * @see Element#getLocalName()
		 */
		public final EncodeValueCache elementNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@link Element}-{@code Name}
		 * .
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final EncodeLabelCache elementLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementXmlnsCache} für die {@code Xmlns}.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 */
		public final EncodeElementXmlnsCache elementXmlnsPool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementChildrenCache} für die {@link Element#getChildNodes()}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final EncodeElementChildrenCache elementChildrenPool;

		/**
		 * Dieses Feld speichert den {@link EncodeElementChildrenCache} für die {@link Element#getAttributes()}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final EncodeElementAttributesCache elementAttributesPool;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributeCache} für die {@link Attr}-Daten.
		 * 
		 * @see EncodeAttribute
		 */
		public final EncodeAttributeCache attributePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValueCache} für die {@link Attr}-{@code Name}s.
		 * 
		 * @see Attr#getLocalName()
		 */
		public final EncodeValueCache attributeNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelCache} für die Paare aus {@code URI} und {@link Attr}-{@code Name}.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		public final EncodeLabelCache attributeLabelPool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItemCache}s und {@link EncodeListCache}s.
		 */
		public EncodeContentHandler() {
			this.cursorStack = new EncodeCursorStack();
			this.uriValuePool = new EncodeValueCache();
			this.valuePool = new EncodeValueCache();
			this.xmlnsNamePool = new EncodeValueCache();
			this.xmlnsLabelPool = new EncodeLabelCache(this.uriValuePool, this.xmlnsNamePool);
			this.elementNamePool = new EncodeValueCache();
			this.elementLabelPool = new EncodeLabelCache(this.uriValuePool, this.elementNamePool);
			this.elementXmlnsPool = new EncodeElementXmlnsCache();
			this.elementChildrenPool = new EncodeElementChildrenCache();
			this.elementAttributesPool = new EncodeElementAttributesCache();
			this.elementPool =
				new EncodeElementCache(this.elementLabelPool, this.elementXmlnsPool, this.elementChildrenPool,
					this.elementAttributesPool);
			this.attributeNamePool = new EncodeValueCache();
			this.attributeLabelPool = new EncodeLabelCache(this.uriValuePool, this.attributeNamePool);
			this.attributePool = new EncodeAttributeCache(this.attributeLabelPool, this.valuePool);
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
						newChildren.add(this.valuePool.unique(textValue.toString()));
						textValue.setLength(0);
					}
					newChildren.add(oldNode);
				}
			}
			if(textValue.length() != 0){
				newChildren.add(this.valuePool.unique(textValue.toString()));
			}
			final EncodeElement elementNode =
				this.elementPool.unique(oldCursor.uri, oldCursor.name, oldCursor.xmlns, newChildren, oldCursor.attributes);
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
				attributes.add(this.attributePool.unique(atts.getURI(i), atts.getLocalName(i), atts.getValue(i)));
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
	static public final Comparator<EncodeItem> IndexComparator = new Comparator<EncodeItem>() {

		@Override
		public int compare(final EncodeItem o1, final EncodeItem o2) {
			return Comparators.compare(o1.index, o2.index);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s nach ihren
	 * {@code Name}- und {@code URI}-{@link EncodeValue} via {@link Encoder#IndexComparator}.
	 */
	static public final Comparator<EncodeLabel> LabelComparator = new Comparator<EncodeLabel>() {

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
	static public final Comparator<EncodeValue> ValueComparator = new Comparator<EncodeValue>() {

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
	static public final Comparator<EncodeLabel> XmlnsComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder.ValueComparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder.ValueComparator.compare(o1.name, o2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute} primär nach
	 * den {@code Name}-{@link EncodeValue} und sekundär nach den {@code URI}-{@link EncodeValue} ihrer
	 * {@link EncodeLabel}s via {@link Encoder#ValueComparator}.
	 */
	static public final Comparator<EncodeAttribute> AttributeComparator = new Comparator<EncodeAttribute>() {

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
	 * Diese Methode schreibt den gegebenen Abschitt des gegebenen {@code byte}-Arrays in das gegebene
	 * {@link RandomAccessFile}.
	 * 
	 * @param file {@link RandomAccessFile}.
	 * @param value {@code byte}-Array.
	 * @param offset Index des erten geschriebenen {@code byte}s.
	 * @param count Anzahl der geschrieben {@code byte}s.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	static public void write(final RandomAccessFile file, final byte[] value, final int offset, final int count)
		throws IOException {
		file.write(value, offset, count);
	}

	/**
	 * Diese Methode schreibt das gegebenen {@code int}-Array in das gegebene {@link RandomAccessFile}.
	 * 
	 * @see ArrayCopy#copy(int[], int, byte[], int, int)
	 * @see Encoder#write(RandomAccessFile, byte[], int, int)
	 * @param file {@link RandomAccessFile}.
	 * @param value {@code int}-Array.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	static public void writeValues(final RandomAccessFile file, final int... value) throws IOException {
		final int count = value.length << 2;
		final byte[] array = new byte[count];
		ArrayCopy.copy(value, 0, array, 0, count);
		Encoder.write(file, array, 0, count);
	}

	/**
	 * Diese Methode schreibt die Indices der gegebenen {@link EncodeItem}s in das gegebene {@link RandomAccessFile}.
	 * 
	 * @see EncodeItem#index
	 * @see Encoder#writeValues(RandomAccessFile, int...)
	 * @param file {@link RandomAccessFile}.
	 * @param list {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	public static void writeIndices(final RandomAccessFile file, final List<? extends EncodeItem> list)
		throws IOException {
		final int count = list.size();
		if(count == 0) return;
		final int[] value = new int[count];
		for(int i = 0; i < count; i++){
			value[i] = list.get(i).index;
		}
		Encoder.writeValues(file, value);
	}

	/**
	 * Diese Methode schreibt dia Anzahl der gegebenen {@link EncodeItem}s sowie jedes der {@link EncodeItem} in das
	 * gegebene {@link RandomAccessFile}.
	 * 
	 * <pre>N|item1|...|itemN</pre>
	 * 
	 * @see EncodeItem#write(RandomAccessFile)
	 * @see Encoder#writeValues(RandomAccessFile, int...)
	 * @param file {@link RandomAccessFile}.
	 * @param list {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	public static void writeEncodeItems(final RandomAccessFile file, final List<? extends EncodeItem> list)
		throws IOException {
		Encoder.writeValues(file, list.size());
		for(final EncodeItem item: list){
			item.write(file);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeList}s ihre aufsummierten Längen, die Summe aller
	 * Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link RandomAccessFile}.
	 * 
	 * <pre>N|offset1|...|offsetN|offsetN+1|item1|...|itemN
	 * offset0 = 0
	 * offsetI+1 = offsetI + list.get(I).length()</pre>
	 * 
	 * @see EncodeList#length()
	 * @see Encoder#writeValues(RandomAccessFile, int...)
	 * @param file {@link RandomAccessFile}.
	 * @param list {@link EncodeList}-{@link List}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	public static void writeEncodeLists(final RandomAccessFile file, final List<? extends EncodeList> list)
		throws IOException {
		Encoder.writeValues(file, list.size());
		final int count = list.size();
		int offset = 0;
		final int[] value = new int[count + 1];
		for(int i = 0; i < count; i++){
			value[i] = offset;
			offset += list.get(i).length();
		}
		value[count] = offset;
		Encoder.writeValues(file, value);
		for(final EncodeItem item: list){
			item.write(file);
		}
	}

	/**
	 * Dieser Konstrukteur initialisiert den {@link Encoder}.
	 */
	public Encoder() {
	}

	/**
	 * Diese Methode liest die gegebene {@link InputSource} mit einem neuen {@link XMLReader} in einen neuen
	 * {@link EncodeContentHandler} ein und speichert dessen Daten in das gegebene {@link RandomAccessFile}.
	 * 
	 * @see Encoder#encode(XMLReader, InputSource, RandomAccessFile)
	 * @see XMLReaderFactory#createXMLReader()
	 * @param source {@link InputSource}.
	 * @param target {@link RandomAccessFile}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 */
	public void encode(final InputSource source, final RandomAccessFile target) throws IOException, SAXException {
		this.encode(XMLReaderFactory.createXMLReader(), source, target);
	}

	/**
	 * Diese Methode liest die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in einen neuen
	 * {@link EncodeContentHandler} ein und speichert dessen Daten in das gegebene {@link RandomAccessFile}.
	 * 
	 * @see XMLReader#setContentHandler(ContentHandler)
	 * @see XMLReader#parse(InputSource)
	 * @param reader {@link XMLReader}.
	 * @param source {@link InputSource}.
	 * @param target {@link RandomAccessFile}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 */
	public void encode(final XMLReader reader, final InputSource source, final RandomAccessFile target)
		throws IOException, SAXException {
		final EncodeContentHandler handler = new EncodeContentHandler();
		reader.setContentHandler(handler);
		reader.parse(source);
		final List<EncodeValue> uriCharsPool = handler.uriValuePool.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> xmlnsCharsPool = handler.xmlnsNamePool.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> elementCharsPool = handler.elementNamePool.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> attributeCharsPool = handler.attributeNamePool.compile(0, Encoder.ValueComparator);
		final List<EncodeValue> valuePool = handler.valuePool.compile(0, Encoder.IndexComparator);
		final List<EncodeLabel> xmlnsLabelPool = handler.xmlnsLabelPool.compile(0, Encoder.LabelComparator);
		final List<EncodeLabel> elementLabelPool = handler.elementLabelPool.compile(0, Encoder.LabelComparator);
		final List<EncodeLabel> attributeLabelPool = handler.attributeLabelPool.compile(0, Encoder.LabelComparator);
		final List<EncodeElementXmlns> elementXmlnsPool = handler.elementXmlnsPool.compile(0, Encoder.IndexComparator);
		final List<EncodeElementChildren> elementChildrenPool =
			handler.elementChildrenPool.compile(0, Encoder.IndexComparator);
		final List<EncodeElementAttributes> elementAttributesPool =
			handler.elementAttributesPool.compile(0, Encoder.IndexComparator);
		final List<EncodeElement> elementPool = handler.elementPool.compile(valuePool.size(), Encoder.IndexComparator);
		final List<EncodeAttribute> attributePool = handler.attributePool.compile(0, Encoder.IndexComparator);
		Encoder.writeEncodeLists(target, uriCharsPool);
		Encoder.writeEncodeLists(target, xmlnsCharsPool);
		Encoder.writeEncodeLists(target, elementCharsPool);
		Encoder.writeEncodeLists(target, attributeCharsPool);
		Encoder.writeEncodeLists(target, valuePool);
		Encoder.writeEncodeItems(target, xmlnsLabelPool);
		Encoder.writeEncodeItems(target, elementLabelPool);
		Encoder.writeEncodeItems(target, attributeLabelPool);
		Encoder.writeEncodeLists(target, elementXmlnsPool);
		Encoder.writeEncodeLists(target, elementChildrenPool);
		Encoder.writeEncodeLists(target, elementAttributesPool);
		Encoder.writeEncodeItems(target, elementPool);
		Encoder.writeEncodeItems(target, attributePool);
		final EncodeItem documentElement = handler.cursorStack.children.get(0);
		Encoder.writeValues(target, documentElement.index - valuePool.size());
	}

}
