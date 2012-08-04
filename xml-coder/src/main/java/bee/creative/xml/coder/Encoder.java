package bee.creative.xml.coder;

import java.io.DataOutput;
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
import bee.creative.xml.coder.Decoder.DecodeDocument;

/**
 * Diese Klasse implementiert Klassen und Methoden zur Kodierung eines XML-Dokuments in eine optimierte binäre
 * Darstellung.
 * <p>
 * Als Eingabe werden ein {@link XMLReader} und eine {@link InputSource} verwendet, wobei das
 * {@link EncodeDocumentHandler} als {@link ContentHandler} die vom {@link XMLReader} gelesenen Daten aufnimmt. Die
 * eingelesenen Daten des {@link EncodeDocumentHandler}s werden anschließend unter beachtung der Optionen des
 * {@link Encoder}s in ein {@link EncodeTarget} gespeichert.
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
		 * @see DataOutput#write(byte[], int, int)
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
	 * Diese Klasse implementiert ein abstraktes {@link Hash}-{@code Set}. Wenn das via {@link #get(EncodeItem)} zu einem
	 * gegebenen Element {@link #equals(EncodeItem, EncodeItem) äquivalente} Element ermittelt werden konnte, werden
	 * dieses als Rückgabewert verwendet und dessen Wiederverwendung via {@link #reuse(EncodeItem)} signalisiert. Das
	 * Einfügen eines neuen Elements wird dagegen mit {@link #insert(EncodeItem)} angezeigt. Die Implementation ähnelt
	 * einem {@link Unique}, jedoch mit deutlich geringere Speicherlast.
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
		int reuses;

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
		 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Elements zurück. Das Element ist nie
		 * {@code null}.
		 * 
		 * @param input Element.
		 * @return {@link Object#hashCode() Streuwert}.
		 */
		protected abstract int hash(final GItem input);

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
		protected abstract boolean equals(final GItem input1, final GItem input2);

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
			this.reuses = 0;
		}

		/**
		 * Diese Methode gibt die Anzahl der Aufrufe von {@link #reuse(EncodeItem)} zurück.
		 * 
		 * @return Anzahl der Aufrufe von {@link #reuse(EncodeItem)}.
		 */
		public int reuses() {
			return this.reuses;
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
			return Objects.toStringCall(false, true, this.getClass().getSimpleName(), "size", this.size(), "reuses", this.reuses, "compression", (100 * this.size())
				/ (this.size() + this.reuses + 1));
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
		 * @see EncodePool#setEntryNext(EncodeItem, EncodeItem)
		 */
		EncodeItem next;

		/**
		 * Dieses Feld speichert beim Aufbau der {@link EncodePool}s die absolute Häufigkeit und beim Speichern den Index
		 * des Datensatzes.
		 */
		protected int index = 1;

		/**
		 * Diese Methode schreibt den Datensatz in das gegebenen {@link EncodeTarget}.
		 * 
		 * @param target {@link EncodeTarget}.
		 * @throws IOException Wenn das gegebene {@link EncodeTarget} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} {@code null} ist.
		 */
		public abstract void write(final EncodeTarget target) throws IOException, NullPointerException;

		/**
		 * Diese Methode gibt den Index des Datensatzes zurück. Beim Aufbau des {@link EncodePool}s entspricht dieser der
		 * absoluten Häufigkeit des Datensatzes im eingelesenen XML-Dokument.
		 * 
		 * @return Index des Datensatzes.
		 */
		public int index() {
			return this.index;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz mit Index und mehreren Werten, der als Element in einem
	 * {@link EncodePool} verwendet werden kann.
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
	 * Diese Klasse implementiert eine {@link EncodeList} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeValue extends EncodeList {

		/**
		 * Dieses Feld speichert den {@link String}.
		 */
		protected final String value;

		/**
		 * Dieser Konstrukteur initialisiert den {@link String}.
		 * 
		 * @param value {@link String}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue(final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@link String} zurück.
		 * 
		 * @return {@link String}.
		 */
		public String value() {
			return this.value;
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
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
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
			return Objects.hash(input.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeValue input1, final EncodeValue input2) {
			return input1.value.equals(input2.value);
		}

		/**
		 * Diese Methode gibt den einzigartigen {@link EncodeValue} zum gegebenen {@code String} zurück.
		 * 
		 * @see EncodeValue#value()
		 * @param value {@code String}.
		 * @return einzigartiger {@link EncodeValue}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public EncodeValue unique(final String value) throws NullPointerException {
			return this.get(new EncodeValue(value));
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
		 * Dieses Feld speichert den {@code URI}.
		 */
		protected final EncodeValue uri;

		/**
		 * Dieses Feld speichert den {@code Name}.
		 */
		protected final EncodeValue name;

		/**
		 * Dieser Konstrukteur initialisiert {@code URI} und {@code Name}.
		 * 
		 * @param uri {@code URI}.
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
		 * Diese Methode gibt die {@code URI} zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@code URI}.
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
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeLabelPool extends EncodePool<EncodeLabel> {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		protected final EncodeValuePool uriPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#name()}.
		 */
		protected final EncodeValuePool namePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodePool}s.
		 * 
		 * @param uriPool {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 * @param namePool {@link EncodeValuePool} für {@link EncodeLabel#name()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeLabelPool(final EncodeValuePool uriPool, final EncodeValuePool namePool) throws NullPointerException {
			if(uriPool == null) throw new NullPointerException("uriPool is null");
			if(namePool == null) throw new NullPointerException("namePool is null");
			this.uriPool = uriPool;
			this.namePool = namePool;
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

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für{@link EncodeLabel#uri()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		public EncodeValuePool uriPool() {
			return this.uriPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeLabel#name()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#name()}.
		 */
		public EncodeValuePool namePool() {
			return this.namePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeLabel} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeLabel#uri()
		 * @see EncodeLabel#name()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @return einzigartiges {@link EncodeLabel}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeLabel unique(final String uri, final String name) throws NullPointerException {
			return this.get(new EncodeLabel(this.uriPool.unique(uri), this.namePool.unique(name)));
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
		protected final List<EncodeItem> values;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList}.
		 */
		public EncodeGroup() {
			this(new ArrayList<EncodeItem>());
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}-{@link List} mit einer neuen {@link ArrayList} und den
		 * gegebenen {@link EncodeItem}s.
		 * 
		 * @param value {@link EncodeItem}-{@link List}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup(final List<? extends EncodeItem> value) throws NullPointerException {
			if(value == null) throw new NullPointerException("value is null");
			if(value.contains(null)) throw new NullPointerException("value contains null");
			this.values = new ArrayList<EncodeItem>(value);
		}

		/**
		 * Diese Methode gibt die {@link EncodeItem}-{@link List} zurück.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return {@link EncodeItem}-{@link List}.
		 */
		public List<EncodeItem> values() {
			return Collections.unmodifiableList(this.values);
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
		public void write(final EncodeTarget target) throws IOException, NullPointerException {
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
			return Objects.hash(input.values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final EncodeGroup input1, final EncodeGroup input2) {
			return input1.values.equals(input2.values);
		}

		/**
		 * Diese Methode gibt die einzigartige {@link EncodeGroup} mit den gegebenen {@link EncodeItem}s zurück.
		 * 
		 * @see EncodeGroup#values()
		 * @param value {@link EncodeItem}-{@link List}.
		 * @return einzigartige {@link EncodeGroup}.
		 * @throws NullPointerException Wenn die gegebene {@link EncodeItem}-{@link List} {@code null} ist oder enthält.
		 */
		public EncodeGroup unique(final List<? extends EncodeItem> value) throws NullPointerException {
			return this.get(new EncodeGroup(value));
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
		 * Dieses Feld speichert das {@code URI/Name}-{@link EncodeLabel} oder {@code null}.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		protected final EncodeLabel label;

		/**
		 * Dieses Feld speichert die {@link EncodeGroup} der {@code URI/Prefix}-{@link EncodeLabel}s oder {@code null}.
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
		 * Diese Methode gibt das {@code URI/Name}-{@link EncodeLabel} oder {@code null} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return {@code URI/Name}-{@link EncodeLabel} oder {@code null}.
		 */
		public EncodeLabel label() {
			return this.label;
		}

		/**
		 * Diese Methode gibt die {@link EncodeGroup} der {@code URI/Prefix}-{@link EncodeLabel}s oder {@code null} zurück.
		 * Die Elemente dieser {@link EncodeGroup} sind primär nach {@link EncodeLabel#uri()} und sekundär nach
		 * {@link EncodeLabel#name()} aufsteigend sortiert.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 * @return {@link EncodeGroup} der {@code URI/Prefix}-{@link EncodeLabel}s oder {@code null}.
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
		 * Diese Methode gibt die {@link EncodeGroup} der {@code Attributes} zurück. Die Elemente dieser {@link EncodeGroup}
		 * sind nach {@link EncodeAttribute#name()} bzw. primär nach {@link EncodeLabel#name()} und sekundär nach
		 * {@link EncodeLabel#uri()} des {@link EncodeAttribute#label()} aufsteigend sortiert.
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
				Encoder.writeInts(target, this.label.index, this.xmlns.index, this.children.index, this.attributes.index);
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
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		protected final EncodeLabelPool labelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeGroupPool xmlnsPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 */
		protected final EncodeGroupPool childrenPool;

		/**
		 * Dieses Feld speichert den {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 */
		protected final EncodeGroupPool attributesPool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodePool}s.
		 * 
		 * @param labelPool {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 * @param xmlnsPool {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 * @param childrenPool {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 * @param attributesPool {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElementPool(final EncodeLabelPool labelPool, final EncodeGroupPool xmlnsPool, final EncodeGroupPool childrenPool,
			final EncodeGroupPool attributesPool) throws NullPointerException {
			if(labelPool == null) throw new NullPointerException("labelPool is null");
			if(xmlnsPool == null) throw new NullPointerException("xmlnsPool is null");
			if(childrenPool == null) throw new NullPointerException("childrenPool is null");
			if(attributesPool == null) throw new NullPointerException("attributesPool is null");
			this.labelPool = labelPool;
			this.xmlnsPool = xmlnsPool;
			this.childrenPool = childrenPool;
			this.attributesPool = attributesPool;
		}

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

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeElement#label()} zurück.
		 * 
		 * @return {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		public EncodeLabelPool labelPool() {
			return this.labelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#xmlns()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 */
		public EncodeGroupPool xmlnsPool() {
			return this.xmlnsPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#children()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 */
		public EncodeGroupPool childrenPool() {
			return this.childrenPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#attributes()} zurück.
		 * 
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 */
		public EncodeGroupPool attributesPool() {
			return this.attributesPool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElement} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeElement#name()
		 * @see EncodeElement#children()
		 * @see EncodeElement#attributes()
		 * @param name {@code Name}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return einzigartiges {@link EncodeElement}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElement unique(final String name, final List<? extends EncodeItem> children, final List<? extends EncodeItem> attributes)
			throws NullPointerException {
			return this.get(new EncodeElement(this.labelPool.namePool.unique(name), this.childrenPool.unique(children), this.attributesPool.unique(attributes)));
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeElement} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeLabel#uri()
		 * @see EncodeLabel#name()
		 * @see EncodeElement#label()
		 * @see EncodeElement#xmlns()
		 * @see EncodeElement#children()
		 * @see EncodeElement#attributes()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param xmlns {@code Xmlns}.
		 * @param children {@code Children}.
		 * @param attributes {@code Attributes}.
		 * @return einzigartiges {@link EncodeElement}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeElement unique(final String uri, final String name, final List<? extends EncodeItem> xmlns, final List<? extends EncodeItem> children,
			final List<? extends EncodeItem> attributes) throws NullPointerException {
			return this.get(new EncodeElement(this.labelPool.unique(uri, name), this.xmlnsPool.unique(xmlns), this.childrenPool.unique(children), this.attributesPool
				.unique(attributes)));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link EncodeItem} zur Abstraktion eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttribute extends EncodeItem {

		/**
		 * Dieses Feld speichert den {@code Name}-{@link EncodeValue} oder {@code null}.
		 * 
		 * @see Attr#getNodeName()
		 */
		protected final EncodeValue name;

		/**
		 * Dieses Feld speichert das {@code URI/Name}-{@link EncodeLabel} oder {@code null}.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		protected final EncodeLabel label;

		/**
		 * Dieses Feld speichert den {@code Value}-{@link EncodeValue}.
		 * 
		 * @see Attr#getNodeValue()
		 */
		protected final EncodeValue value;

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param name {@link EncodeValue} für {@link EncodeAttribute#name()}.
		 * @param value {@link EncodeValue} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttribute(final EncodeValue name, final EncodeValue value) throws NullPointerException {
			if(name == null) throw new NullPointerException("name is null");
			if(value == null) throw new NullPointerException("value is null");
			this.name = name;
			this.label = null;
			this.value = value;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link EncodeItem}s.
		 * 
		 * @param label {@link EncodeLabel} für {@link EncodeAttribute#label()}.
		 * @param value {@link EncodeValue} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttribute(final EncodeLabel label, final EncodeValue value) throws NullPointerException {
			if(label == null) throw new NullPointerException("label is null");
			if(value == null) throw new NullPointerException("value is null");
			this.name = null;
			this.label = label;
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@code Name}-{@link EncodeValue} oder {@code null} zurück.
		 * 
		 * @see Attr#getNodeName()
		 * @return {@code Name}-{@link EncodeValue} oder {@code null}.
		 */
		public EncodeValue name() {
			return this.name;
		}

		/**
		 * Diese Methode gibt das {@code URI/Name}-{@link EncodeLabel} oder {@code null} zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@code URI/Name}-{@link EncodeLabel} oder {@code null}.
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
			if(this.name == null) return Objects.toStringCall(false, true, "EncodeAttribute", "index", this.index, "label", this.label, "value", this.value);
			return Objects.toStringCall(false, true, "EncodeAttribute", "index", this.index, "name", this.name, "value", this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link EncodePool} der {@link EncodeAttribute}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeAttributePool extends EncodePool<EncodeAttribute> {

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		protected final EncodeLabelPool labelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Unique}s für Beschriftung und Wert.
		 * 
		 * @param labelPool {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 * @param valuePool {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttributePool(final EncodeLabelPool labelPool, final EncodeValuePool valuePool) throws NullPointerException {
			if(labelPool == null) throw new NullPointerException("labelPool is null");
			if(valuePool == null) throw new NullPointerException("valuePool is null");
			this.labelPool = labelPool;
			this.valuePool = valuePool;
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

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeAttribute#label()} zurück.
		 * 
		 * @return {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		public EncodeLabelPool labelPool() {
			return this.labelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttribute#value()} zurück.
		 * 
		 * @return {@link EncodeValuePool} für {@link EncodeAttribute#value()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttribute} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeAttribute#name()
		 * @see EncodeAttribute#value()
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeAttribute}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttribute unique(final String name, final String value) throws NullPointerException {
			return this.get(new EncodeAttribute(this.labelPool.namePool.unique(name), this.valuePool.unique(value)));
		}

		/**
		 * Diese Methode gibt das einzigartige {@link EncodeAttribute} mit den gegebenen Eigenschaften zurück.
		 * 
		 * @see EncodeLabel#uri()
		 * @see EncodeLabel#name()
		 * @see EncodeAttribute#label()
		 * @see EncodeAttribute#value()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param value {@code Value}.
		 * @return einzigartiges {@link EncodeAttribute}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public EncodeAttribute unique(final String uri, final String name, final String value) throws NullPointerException {
			return this.get(new EncodeAttribute(this.labelPool.unique(uri, name), this.valuePool.unique(value)));
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link EncodePool}s zur Abstraktion eines {@link Document}
	 * s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocument {

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		protected final EncodeValuePool uriPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttribute#value()} und
		 * {@link EncodeElement#children()}.
		 */
		protected final EncodeValuePool valuePool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeLabel#name()} bei
		 * {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeValuePool xmlnsNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeElement#xmlns()}.
		 */
		protected final EncodeLabelPool xmlnsLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeElement#name()} bzw.
		 * {@link EncodeLabel#name()} bei {@link EncodeElement#label()}.
		 */
		protected final EncodeValuePool elementNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		protected final EncodeLabelPool elementLabelPool;

		/**
		 * Dieses Feld speichert den {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw.
		 * {@link EncodeLabel#name()} bei {@link EncodeAttribute#label()}.
		 */
		protected final EncodeValuePool attributeNamePool;

		/**
		 * Dieses Feld speichert den {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		protected final EncodeLabelPool attributeLabelPool;

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

		/**
		 * Dieses Feld speichert den {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und
		 * {@link EncodeElement#children()}.
		 */
		protected final EncodeElementPool elementNodePool;

		/**
		 * Dieses Feld speichert den {@link EncodeAttributePool} für {@link EncodeElement#attributes()}.
		 */
		protected final EncodeAttributePool attributeNodePool;

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
			this.uriPool = new EncodeValuePool();
			this.valuePool = new EncodeValuePool();
			this.xmlnsNamePool = new EncodeValuePool();
			this.xmlnsLabelPool = new EncodeLabelPool(this.uriPool, this.xmlnsNamePool);
			this.elementNamePool = new EncodeValuePool();
			this.elementLabelPool = new EncodeLabelPool(this.uriPool, this.elementNamePool);
			this.attributeNamePool = new EncodeValuePool();
			this.attributeLabelPool = new EncodeLabelPool(this.uriPool, this.attributeNamePool);
			this.elementXmlnsPool = new EncodeGroupPool();
			this.elementChildrenPool = new EncodeGroupPool();
			this.elementAttributesPool = new EncodeGroupPool();
			this.elementNodePool = new EncodeElementPool(this.elementLabelPool, this.elementXmlnsPool, this.elementChildrenPool, this.elementAttributesPool);
			this.attributeNodePool = new EncodeAttributePool(this.attributeLabelPool, this.valuePool);
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeLabel#uri()} zurück. Im {@link EncodeTarget}
		 * werden diese nach ihrem {@link EncodeValue#value()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#uri()}.
		 */
		public EncodeValuePool uriPool() {
			return this.uriPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttribute#value()} und
		 * {@link EncodeElement#children()} zurück. Im {@link EncodeTarget} werden diese nach ihrem
		 * {@link EncodeValue#value()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 * @return {@link EncodeValuePool} für {@link EncodeAttribute#value()} und {@link EncodeElement#children()}.
		 */
		public EncodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeLabel#name()} bei {@link EncodeElement#xmlns()}
		 * zurück. Im {@link EncodeTarget} werden diese nach ihrem {@link EncodeValue#value()} aufsteigend sortiert
		 * gespeichert.
		 * 
		 * @see Node#getPrefix()
		 * @return {@link EncodeValuePool} für {@link EncodeLabel#name()} bei {@link EncodeElement#xmlns()}.
		 */
		public EncodeValuePool xmlnsNamePool() {
			return this.xmlnsNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeElement#xmlns()} zurück. Im {@link EncodeTarget}
		 * werden diese primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} aufsteigend
		 * sortiert gespeichert.
		 * 
		 * @see Node#getNamespaceURI()
		 * @see Node#getPrefix()
		 * @return {@link EncodeLabelPool} für {@link EncodeElement#xmlns()}.
		 */
		public EncodeLabelPool xmlnsLabelPool() {
			return this.xmlnsLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeElement#name()} bzw. {@link EncodeLabel#name()}
		 * bei {@link EncodeElement#label()} zurück. Im {@link EncodeTarget} werden diese nach ihrem
		 * {@link EncodeValue#value()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getLocalName()
		 * @return {@link EncodeValuePool} für {@link EncodeElement#name()} bzw. {@link EncodeLabel#name()} bei
		 *         {@link EncodeElement#label()}.
		 */
		public EncodeValuePool elementNamePool() {
			return this.elementNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeElement#label()} zurück. Im {@link EncodeTarget}
		 * werden diese primär nach {@link EncodeLabel#name()} und sekundär nach {@link EncodeLabel#uri()} aufsteigend
		 * sortiert gespeichert.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return {@link EncodeLabelPool} für {@link EncodeElement#label()}.
		 */
		public EncodeLabelPool elementLabelPool() {
			return this.elementLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw. {@link EncodeLabel#name()}
		 * bei {@link EncodeAttribute#label()} zurück. Im {@link EncodeTarget} werden diese nach ihrem
		 * {@link EncodeValue#value()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getNodeName()
		 * @see Attr#getLocalName()
		 * @return {@link EncodeValuePool} für {@link EncodeAttribute#name()} bzw. {@link EncodeLabel#name()} bei
		 *         {@link EncodeAttribute#label()}.
		 */
		public EncodeValuePool attributeNamePool() {
			return this.attributeNamePool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeLabelPool} für {@link EncodeAttribute#label()} zurück. Im
		 * {@link EncodeTarget} werden diese primär nach {@link EncodeLabel#name()} und sekundär nach
		 * {@link EncodeLabel#uri()} aufsteigend sortiert gespeichert.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@link EncodeLabelPool} für {@link EncodeAttribute#label()}.
		 */
		public EncodeLabelPool attributeLabelPool() {
			return this.attributeLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#xmlns()} zurück. Im {@link EncodeTarget}
		 * werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Node#lookupPrefix(String)
		 * @see Node#lookupNamespaceURI(String)
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#xmlns()}.
		 */
		public EncodeGroupPool elementXmlnsPool() {
			return this.elementXmlnsPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#children()} zurück. Im
		 * {@link EncodeTarget} werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getChildNodes()
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#children()}.
		 */
		public EncodeGroupPool elementChildrenPool() {
			return this.elementChildrenPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeGroupPool} für {@link EncodeElement#attributes()} zurück. Im
		 * {@link EncodeTarget} werden diese nach ihrer Häufigkeit aufsteigend sortiert gespeichert.
		 * 
		 * @see Element#getAttributes()
		 * @return {@link EncodeGroupPool} für {@link EncodeElement#attributes()}.
		 */
		public EncodeGroupPool elementAttributesPool() {
			return this.elementAttributesPool;
		}

		/**
		 * Diese Methode gibt den {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und
		 * {@link EncodeElement#children()} zurück.
		 * 
		 * @see Element
		 * @return {@link EncodeElementPool} für {@link EncodeDocument#documentElement()} und
		 *         {@link EncodeElement#children()}.
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
		 * Diese Methode gibt das {@link EncodeElement} für {@link Document#getDocumentElement()} zurück.
		 * 
		 * @see Document#getDocumentElement()
		 * @return {@link EncodeElement} für {@link Document#getDocumentElement()}.
		 */
		public EncodeElement documentElement() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "EncodeDocument", //
				"uriPool", this.uriPool, //
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
				"documentElement", this.documentElement //
				);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ContentHandler} zum Einlsenen eines {@link Document}s mit Hilfe eines
	 * {@link XMLReader}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class EncodeDocumentHandler implements ContentHandler {

		/**
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Paare aus {@code URI} und {@code Prefix} als
		 * {@link EncodeLabel}s während des Einlesens eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class XmlnsStack {

			/**
			 * Dieses Feld speichert die {@code URI}-{@code Prefix}-{@link Map}.
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
		 * Diese Klasse implementiert ein Objekt zur Verwaltung der Inhalte eine {@link Element}s während des Einlesens
		 * eines {@link Document}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected static final class CursorStack {

			/**
			 * Dieses Feld speichert die {@code URI}.
			 * 
			 * @see Element#getNamespaceURI()
			 */
			protected final String uri;

			/**
			 * Dieses Feld speichert den {@code Name}.
			 * 
			 * @see Element#getLocalName()
			 */
			protected final String name;

			/**
			 * Dieses Feld speichert die {@link EncodeLabel}-{@link List} für die Paare aus {@code URI} und {@code Prefix}.
			 * 
			 * @see Node#lookupPrefix(String)
			 * @see Node#lookupNamespaceURI(String)
			 */
			protected final List<EncodeLabel> spaces;

			/**
			 * Dieses Feld speichert die {@link EncodeItem}-{@link List} für die {@link Element#getChildNodes()}.
			 */
			protected final List<EncodeItem> children;

			/**
			 * Dieses Feld speichert die {@link EncodeAttribute}-{@link List} für die {@link Element#getAttributes()}.
			 */
			protected final List<? extends EncodeItem> attributes;

			/**
			 * Dieses Feld speichert den nächsten {@link CursorStack} oder {@code null}.
			 */
			protected final CursorStack next;

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
			public CursorStack(final String uri, final String name, final List<EncodeLabel> spaces, final List<EncodeAttribute> attributes, final CursorStack next) {
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
				return Objects.toStringCall(true, "EncodeCursorStack", this.uri, this.name, this.spaces, this.children, this.attributes, this.next);
			}

		}

		/**
		 * Dieses Feld speichert den {@link XmlnsStack} für die aktuellen Paare aus {@code URI} und {@code Prefix} oder
		 * {@code null}.
		 */
		protected XmlnsStack xmlnsStack;

		/**
		 * Dieses Feld speichert die aktuellen Paare aus {@code URI} und {@code Prefix}.
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
		 * Dieser Konstrukteur initialisiert das {@link EncodeDocument} und die {@code xmlns}-Aktivierung.
		 * 
		 * @see #isXmlnsEnabled()
		 * @param document {@link EncodeDocument}.
		 * @param xmlnsEnabled {@code xmlns}-Aktivierung.
		 * @throws NullPointerException Wenn das gegebene {@link EncodeDocument} {@code null} ist.
		 */
		public EncodeDocumentHandler(final EncodeDocument document, final boolean xmlnsEnabled) throws NullPointerException {
			if(document == null) throw new NullPointerException("document is null");
			this.document = document;
			this.cursorStack = new CursorStack();
			this.xmlnsEnabled = xmlnsEnabled;
			if(!xmlnsEnabled) return;
			this.startPrefixMapping(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
			this.startPrefixMapping(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
		}

		/**
		 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen
		 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
		 * {@code Prefix}.
		 * 
		 * @see EncodeLabel#uri()
		 * @see EncodeLabel#name()
		 * @see EncodeElement#name()
		 * @see EncodeElement#label()
		 * @see EncodeAttribute#name()
		 * @see EncodeAttribute#label()
		 * @return {@code xmlns}-Aktivierung.
		 */
		public boolean isXmlnsEnabled() {
			return this.xmlnsEnabled;
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
			final CursorStack oldCursor = this.cursorStack;
			final CursorStack newCursor = this.cursorStack.next;
			final List<EncodeItem> oldChildren = oldCursor.children;
			final List<EncodeItem> newChildren = new ArrayList<EncodeItem>(oldChildren.size());
			final StringBuilder textValue = new StringBuilder();
			final EncodeValuePool valuePool = this.document.valuePool;
			for(final EncodeItem oldNode: oldChildren){
				if(oldNode instanceof EncodeValue){
					final EncodeValue textChildNode = (EncodeValue)oldNode;
					textValue.append(textChildNode.value);
				}else{
					if(textValue.length() != 0){
						newChildren.add(valuePool.unique(textValue.toString()));
						textValue.setLength(0);
					}
					newChildren.add(oldNode);
				}
			}
			if(textValue.length() != 0){
				newChildren.add(valuePool.unique(textValue.toString()));
			}
			if(this.xmlnsEnabled){
				newCursor.children.add(this.document.elementNodePool.unique(oldCursor.uri, oldCursor.name, oldCursor.spaces, newChildren, oldCursor.attributes));
			}else{
				newCursor.children.add(this.document.elementNodePool.unique(oldCursor.name, newChildren, oldCursor.attributes));
			}
			this.cursorStack = newCursor;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void startElement(final String uri, final String name, final String qName, final Attributes atts) {
			if(this.xmlnsEnabled){
				if(this.xmlnsCache == null){
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
						Collections.sort(xmlns, Encoder.XmlnsComparator);
					}
					this.xmlnsCache = xmlns;
				}
				final int size = atts.getLength();
				final List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>(size);
				final EncodeAttributePool attributeNodePool = this.document.attributeNodePool;
				for(int i = 0; i < size; i++){
					attributes.add(attributeNodePool.unique(atts.getURI(i), atts.getLocalName(i), atts.getValue(i)));
				}
				if(size > 1){
					Collections.sort(attributes, Encoder.AttributeLabelComparator);
				}
				this.cursorStack = new CursorStack(uri, name, this.xmlnsCache, attributes, this.cursorStack);
			}else{
				final int size = atts.getLength();
				final List<EncodeAttribute> attributes = new ArrayList<EncodeAttribute>(size);
				final EncodeAttributePool attributeNodePool = this.document.attributeNodePool;
				for(int i = 0; i < size; i++){
					attributes.add(attributeNodePool.unique(atts.getLocalName(i), atts.getValue(i)));
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
			this.document.documentElement = (EncodeElement)this.cursorStack.children.get(0);
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
	 * @see Coder#hashValue(String)
	 * @see Encoder#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeValue> ValueHasher = new Comparable<EncodeValue>() {

		@Override
		public int compareTo(final EncodeValue value) {
			return Coder.hashValue(value.value);
		}

	};

	/**
	 * Dieses Feld speichert das {@link Comparable} zur Berechnung des {@link Object#hashCode() Streuwerts} von
	 * {@link EncodeLabel}s.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see Encoder#compilePool(EncodePool, int, Comparator)
	 */
	protected static final Comparable<EncodeLabel> LabelHasher = new Comparable<EncodeLabel>() {

		@Override
		public int compareTo(final EncodeLabel value) {
			return Coder.hashLabel(value.uri.index, value.name.index);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeItem}s nach ihrem
	 * {@link EncodeItem#index()}.
	 */
	protected static final Comparator<EncodeItem> IndexComparator = new Comparator<EncodeItem>() {

		@Override
		public int compare(final EncodeItem o1, final EncodeItem o2) {
			return Comparators.compare(o1.index, o2.index);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s primär nach
	 * ihrem {@link EncodeLabel#name()} und sekundär nach ihrer {@link EncodeLabel#uri()} via
	 * {@link Encoder#IndexComparator}.
	 */
	protected static final Comparator<EncodeLabel> LabelComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder.IndexComparator.compare(o1.name, o2.name);
			if(comp != 0) return comp;
			return Encoder.IndexComparator.compare(o1.uri, o2.uri);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeValue}s nach ihrem
	 * {@link EncodeValue#value()}.
	 */
	protected static final Comparator<EncodeValue> ValueComparator = new Comparator<EncodeValue>() {

		@Override
		public int compare(final EncodeValue o1, final EncodeValue o2) {
			return Comparators.compare(o1.value, o2.value);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeLabel}s primär nach
	 * ihrer {@link EncodeLabel#uri()} und sekundär nach ihrem {@link EncodeLabel#name()} via
	 * {@link Encoder#ValueComparator}.
	 */
	protected static final Comparator<EncodeLabel> XmlnsComparator = new Comparator<EncodeLabel>() {

		@Override
		public int compare(final EncodeLabel o1, final EncodeLabel o2) {
			final int comp = Encoder.ValueComparator.compare(o1.uri, o2.uri);
			if(comp != 0) return comp;
			return Encoder.ValueComparator.compare(o1.name, o2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute}s nach ihrem
	 * {@link EncodeAttribute#name()} via {@link Encoder#ValueComparator}.
	 */
	protected static final Comparator<EncodeAttribute> AttributeNameComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
			return Encoder.ValueComparator.compare(value1.name, value2.name);
		}

	};

	/**
	 * Dieses Feld speichert den {@link Comparator} zur aufsteigenden Sortierung von {@link EncodeAttribute}s primär nach
	 * dem {@link EncodeLabel#name()} und sekundär nach der {@link EncodeLabel#uri()} der {@link EncodeAttribute#label()}
	 * via {@link Encoder#ValueComparator}.
	 */
	protected static final Comparator<EncodeAttribute> AttributeLabelComparator = new Comparator<EncodeAttribute>() {

		@Override
		public int compare(final EncodeAttribute value1, final EncodeAttribute value2) {
			final int comp = Encoder.ValueComparator.compare(value1.label.name, value2.label.name);
			if(comp != 0) return comp;
			return Encoder.ValueComparator.compare(value1.label.uri, value2.label.uri);
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
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. das gegebene {@code int}-Array
	 *         {@code null} ist.
	 */
	protected static void writeInts(final EncodeTarget target, final int... values) throws IOException, NullPointerException {
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		final byte[] array = Coder.encodeIndices(values);
		target.write(array, 0, array.length);
	}

	/**
	 * Diese Methode schreibt die Anzahl der gegebenen {@link EncodeItem}s sowie jedes der {@link EncodeItem}s in das
	 * gegebene {@link EncodeTarget}.
	 * 
	 * <pre>N|item1|...|itemN</pre>
	 * 
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @see EncodeItem#write(EncodeTarget)
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeItem}-
	 *         {@link List} {@code null} ist.
	 */
	protected static void writeItems(final EncodeTarget target, final List<? extends EncodeItem> values) throws IOException, NullPointerException {
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		Encoder.writeInts(target, values.size());
		for(final EncodeItem item: values){
			item.write(target);
		}
	}

	/**
	 * Diese Methode speichert dia Anzahl der gegebenen {@link EncodeList}s ihre aufsummierten Längen, die Summe aller
	 * Längen, sowie jedes der {@link EncodeItem} in das gegebene {@link EncodeTarget}.
	 * 
	 * <pre>size|offset[0]|...|offset[size]|values[0]|...|values[size-1]
	 * offset[0] = 0
	 * offset[i+1] = offset[i] + values[i].length</pre>
	 * 
	 * @see EncodeList#length()
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeList}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeList}-
	 *         {@link List} {@code null} ist.
	 */
	protected static void writeLists(final EncodeTarget target, final List<? extends EncodeList> values) throws IOException, NullPointerException {
		if(target == null) throw new NullPointerException("target is null");
		if(values == null) throw new NullPointerException("values is null");
		Encoder.writeInts(target, values.size());
		final int size = values.size();
		int offset = 0;
		final int[] value = new int[size + 1];
		for(int i = 0; i < size; i++){
			value[i] = offset;
			offset += values.get(i).length();
		}
		value[size] = offset;
		Encoder.writeInts(target, value);
		for(final EncodeItem item: values){
			item.write(target);
		}
	}

	/**
	 * Diese Methode schreibt die Indices der gegebenen {@link EncodeItem}s in das gegebene {@link EncodeTarget}.
	 * 
	 * @see Encoder#writeInts(EncodeTarget, int...)
	 * @see EncodeItem#index
	 * @param target {@link EncodeTarget}.
	 * @param values {@link EncodeItem}-{@link List}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn das gegebene {@link EncodeTarget} bzw. die gegebene {@link EncodeItem}-
	 *         {@link List} {@code null} ist.
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
		Encoder.writeInts(target, value);
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link EncodePool}s in eine neue {@link List} ein, sortiert diese
	 * {@link List} mit dem gegebenen {@link Comparator}, setzt den Index der Elemente unter Beachtung der gegebenen
	 * Verschiebung, entfernt alle Elemente aus dem {@link EncodePool} und gibt die {@link List} zurück. Der Index des
	 * {@code i}-ten Elements ergibt sich aus:
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
	 * @throws NullPointerException Wenn der gegebene {@link EncodePool} bzw. der gegebene {@link Comparator} {@code null}
	 *         ist.
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
	 * Diese Methode erzeugt eine {@link List} aus {@link EncodeGroup}s als {@code Hash-Table} für die gegebenen Elemente
	 * und gibt diese zurück. Der {@link Object#hashCode() Streuwert} der Elemente wird mit Hilfe des gegebenen
	 * {@link Comparable}s berechnet.
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
	protected static <GItem extends EncodeItem> List<EncodeGroup> cimpileHash(final List<? extends GItem> values, final boolean enabled,
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
			list.get(index).values.add(value);
		}
		return list;
	}

	/**
	 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
	 */
	protected boolean xmlnsEnabled = true;

	/**
	 * Dieses Feld speichert die {@code URI-Hash}-Aktivierung.
	 */
	protected boolean uriHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Value-Hash}-Aktivierung.
	 */
	protected boolean valueHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	protected boolean xmlnsNameHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	protected boolean xmlnsLabelHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Element-Name-Hash}-Aktivierung.
	 */
	protected boolean elementNameHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Element-Label-Hash}-Aktivierung.
	 */
	protected boolean elementLabelHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Attribute-Name-Hash}-Aktivierung.
	 */
	protected boolean attributeNameHashEnabled = false;

	/**
	 * Dieses Feld speichert die {@code Attribute-Label-Hash}-Aktivierung.
	 */
	protected boolean attributeLabelHashEnabled = false;

	/**
	 * Dieser Konstrukteur initialisiert den {@link Encoder}.
	 */
	public Encoder() {
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
	 * Diese Methode gibt die {@code URI-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#uriPool()} eine {@code URI-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#uriHash()
	 * @return {@code URI-Hash}-Aktivierung.
	 */
	public boolean isUriHashEnabled() {
		return this.uriHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code URI-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#uriPool()} eine {@code URI-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#uriHash()
	 * @param value {@code URI-Hash}-Aktivierung.
	 */
	public void setUriHashEnabled(final boolean value) {
		this.uriHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Value-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#valuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#valueHash()
	 * @return {@code Value-Hash}-Aktivierung.
	 */
	public boolean isValueHashEnabled() {
		return this.valueHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Value-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#valuePool()} eine {@code Value-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#valueHash()
	 * @param value {@code Value-Hash}-Aktivierung.
	 */
	public void setValueHashEnabled(final boolean value) {
		this.valueHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Xmlns-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#xmlnsNamePool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#xmlnsNameHash()
	 * @return {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	public boolean isXmlnsNameHashEnabled() {
		return this.xmlnsNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Xmlns-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#xmlnsNamePool()} eine {@code Xmlns-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#xmlnsNameHash()
	 * @param value {@code Xmlns-Name-Hash}-Aktivierung.
	 */
	public void setXmlnsNameHashEnabled(final boolean value) {
		this.xmlnsNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Xmlns-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#xmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#xmlnsLabelHash()
	 * @return {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	public boolean isXmlnsLabelHashEnabled() {
		return this.xmlnsLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Xmlns-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#xmlnsLabelPool()} eine {@code Xmlns-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#xmlnsLabelHash()
	 * @param value {@code Xmlns-Label-Hash}-Aktivierung.
	 */
	public void setXmlnsLabelHashEnabled(final boolean value) {
		this.xmlnsLabelHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Element-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#elementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#elementNameHash()
	 * @return {@code Element-Name-Hash}-Aktivierung.
	 */
	public boolean isElementNameHashEnabled() {
		return this.elementNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Element-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#elementNamePool()} eine {@code Element-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#elementNameHash()
	 * @param value {@code Element-Name-Hash}-Aktivierung.
	 */
	public void setElementNameHashEnabled(final boolean value) {
		this.elementNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Element-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#elementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#elementLabelHash()
	 * @return {@code Element-Label-Hash}-Aktivierung.
	 */
	public boolean isElementLabelHashEnabled() {
		return this.elementLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Element-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#elementLabelPool()} eine {@code Element-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#elementLabelHash()
	 * @param value {@code Element-Label-Hash}-Aktivierung.
	 */
	public void setElementLabelHashEnabled(final boolean value) {
		this.elementLabelHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Attribute-Name-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#attributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @return {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public boolean isAttributeNameHashEnabled() {
		return this.attributeNameHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Attribute-Name-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#attributeNamePool()} eine {@code Attribute-Name-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashValue(String)
	 * @see DecodeDocument#attributeNameHash()
	 * @param value {@code Attribute-Name-Hash}-Aktivierung.
	 */
	public void setAttributeNameHashEnabled(final boolean value) {
		this.attributeNameHashEnabled = value;
	}

	/**
	 * Diese Methode gibt die {@code Attribute-Label-Hash}-Aktivierung zurück. Wenn diese Option {@code true} ist, wird
	 * für {@link EncodeDocument#attributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#attributeLabelHash()
	 * @return {@code Attribute-Label-Hash}-Aktivierung.
	 */
	public boolean isAttributeLabelHashEnabled() {
		return this.attributeLabelHashEnabled;
	}

	/**
	 * Diese Methode setzt die {@code Attribute-Label-Hash}-Aktivierung. Wenn diese Option {@code true} ist, wird für
	 * {@link EncodeDocument#attributeLabelPool()} eine {@code Attribute-Label-Hash-Table} erzeugt.
	 * 
	 * @see Coder#hashLabel(int, int)
	 * @see DecodeDocument#attributeLabelHash()
	 * @param value {@code Attribute-Label-Hash}-Aktivierung.
	 */
	public void setAttributeLabelHashEnabled(final boolean value) {
		this.attributeLabelHashEnabled = value;
	}

	/**
	 * Diese Methode kodiert das im {@code Source}-{@link File} gegebene XML-Dokument in eine optimierte binäre
	 * Darstellung und speichert diese im {@code Target}-{@link File}.
	 * 
	 * @see #encode(XMLReader, InputSource, EncodeTarget)
	 * @param source {@code Source}-{@link File}.
	 * @param target {@code Target}-{@link File}.
	 * @throws IOException Wenn die verwendete {@link InputSource} bzw. das verwendete {@link EncodeTarget} eine
	 *         {@link IOException} auslöst.
	 * @throws SAXException Wenn der verwendete {@link XMLReader} eine {@link SAXException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final File source, final File target) throws IOException, SAXException, NullPointerException {
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
		this.encode(XMLReaderFactory.createXMLReader(), new InputSource(new FileReader(source)), new EncodeTargetFile(target));
	}

	/**
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues
	 * {@link EncodeDocument} ein und gibt dieses zurück.
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
	 * @see #cimpileHash(List, boolean, Comparable)
	 * @param source {@link EncodeDocument}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn das {@link EncodeTarget} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn eine der eingaben {@code null} ist.
	 */
	public void encode(final EncodeDocument source, final EncodeTarget target) throws IOException, NullPointerException {
		if(source == null) throw new NullPointerException("source is null");
		if(target == null) throw new NullPointerException("target is null");
		final List<EncodeValue> uriPool = Encoder.compilePool(source.uriPool(), 0, Encoder.ValueComparator);
		final List<EncodeGroup> uriHash = Encoder.cimpileHash(uriPool, this.uriHashEnabled, Encoder.ValueHasher);
		final List<EncodeValue> valuePool = Encoder.compilePool(source.valuePool(), 0, Encoder.ValueComparator);
		final List<EncodeGroup> valueHash = Encoder.cimpileHash(valuePool, this.valueHashEnabled, Encoder.ValueHasher);
		final List<EncodeValue> xmlnsNamePool = Encoder.compilePool(source.xmlnsNamePool(), 0, Encoder.ValueComparator);
		final List<EncodeGroup> xmlnsNameHash = Encoder.cimpileHash(xmlnsNamePool, this.xmlnsNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> xmlnsLabelPool = Encoder.compilePool(source.xmlnsLabelPool(), 0, Encoder.LabelComparator);
		final List<EncodeGroup> xmlnsLabelHash = Encoder.cimpileHash(xmlnsLabelPool, this.xmlnsLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeValue> elementNamePool = Encoder.compilePool(source.elementNamePool(), 0, Encoder.ValueComparator);
		final List<EncodeGroup> elementNameHash = Encoder.cimpileHash(elementNamePool, this.elementNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> elementLabelPool = Encoder.compilePool(source.elementLabelPool(), 0, Encoder.LabelComparator);
		final List<EncodeGroup> elementLabelHash = Encoder.cimpileHash(elementLabelPool, this.elementLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeValue> attributeNamePool = Encoder.compilePool(source.attributeNamePool(), 0, Encoder.ValueComparator);
		final List<EncodeGroup> attributeNameHash = Encoder.cimpileHash(attributeNamePool, this.attributeNameHashEnabled, Encoder.ValueHasher);
		final List<EncodeLabel> attributeLabelPool = Encoder.compilePool(source.attributeLabelPool(), 0, Encoder.LabelComparator);
		final List<EncodeGroup> attributeLabelHash = Encoder.cimpileHash(attributeLabelPool, this.attributeLabelHashEnabled, Encoder.LabelHasher);
		final List<EncodeGroup> elementXmlnsPool = Encoder.compilePool(source.elementXmlnsPool(), 0, Encoder.IndexComparator);
		final List<EncodeGroup> elementChildrenPool = Encoder.compilePool(source.elementChildrenPool(), 0, Encoder.IndexComparator);
		final List<EncodeGroup> elementAttributesPool = Encoder.compilePool(source.elementAttributesPool(), 0, Encoder.IndexComparator);
		final List<EncodeElement> elementNodePool = Encoder.compilePool(source.elementNodePool(), valuePool.size(), Encoder.IndexComparator);
		final List<EncodeAttribute> attributeNodePool = Encoder.compilePool(source.attributeNodePool(), 0, Encoder.IndexComparator);
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
		Encoder.writeInts(target, source.documentElement().index() - valuePool.size());
	}

	/**
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in ein neues
	 * {@link EncodeDocument} ein und speichert dessen in das gegebene {@link EncodeTarget}.
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
	 * Diese Methode ließt die gegebene {@link InputSource} mit dem gegebenen {@link XMLReader} in das gegebene
	 * {@link EncodeDocument} ein.
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
		final EncodeDocumentHandler adapter = new EncodeDocumentHandler(target, this.isXmlnsEnabled());
		reader.setContentHandler(adapter);
		reader.parse(source);
	}

}
