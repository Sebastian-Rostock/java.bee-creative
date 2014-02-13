package bee.creative.xml.bex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.util.Bytes;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueSet;

/**
 * Diese Klasse implementiert ein Objekt zur Kodierung eines {@link Document}s in das Binärformat BEX.
 * <p>
 * <h3>BEX – Binary Encoded XML (Document)</h3>
 * <p>
 * Binary Encoded XML (Document) ist das Format einer Binärdatei zur Abbildung eines XML Dokuments. Ziel dieses Formats ist es, eine nur lesende
 * DOM-Implementation darauf aufsetzen zu können, welche signifikant weniger Arbeitsspeicher verbraucht, als eine zumeist auch schreiben könnende Implementation
 * einer Standard XML Softwarebibliothek.
 * <p>
 * <table border="1" style="vertical-align: top">
 * <tr>
 * <th>Struktur</th>
 * <th>Feld</th>
 * <th>Format</th>
 * <th>Anzahl</th>
 * <th>Beschreibung</th>
 * </tr>
 * <tr>
 * <td rowspan="5">
 * <code>BEX</code> <br>
 * Kodiert einen DOM Dokumentknoten mit seinen enthaltenen Text-, Element- und Attributknoten als binäre Datenstruktur zum wahlfreien Zugriff.</td>
 * <td><code>namePool</code></td>
 * <td><code>ValuePool</code></td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die URIs und die Namen von Element- und Attributknoten.</td>
 * </tr>
 * <tr>
 * <td><code>valuePool</code></td>
 * <td><code>ValuePool</code></td>
 * <td><code>1</code></td>
 * <td>Auflistung der Zeichenketten für die Werte von Text- und Attributknoten.</td>
 * </tr>
 * <tr>
 * <td><code>childrenPool</code></td>
 * <td><code>ChildrenPool</code></td>
 * <td><code>1</code></td>
 * <td>Auflistung der Kindknotenlisten für Element- und Dokumentknoten.</td>
 * </tr>
 * <tr>
 * <td><code>attributesPool</code></td>
 * <td><code>AttributesPool</code></td>
 * <td><code>1</code></td>
 * <td>Auflistung der Attributknotenlisten für Elementknoten.</td>
 * </tr>
 * <tr>
 * <td><code>documentChildren</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf die Kindknotenliste des Dokumentknoten.</td>
 * </tr>
 * <tr>
 * <td rowspan="4">
 * <code>ValuePool</code> <br>
 * Kodiert eine Auflistung von Zeichenketten.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Anzahl der Zeichenketten.</td>
 * </tr>
 * <tr>
 * <td><code>start[0..size]</code></td>
 * <td><code>int</code></td>
 * <td><code>size+1</code></td>
 * <td>Startpositionen der Zeichenketten im Speicherbereich value.<br>
 * Das erste Zeichen der i-ten Zeichenkette liegt bei Zeichen start[i] und das nach dem letzten bei start[i+1].</td>
 * </tr>
 * <tr>
 * <td><code>-</code></td>
 * <td><code>byte</code></td>
 * <td><code>0..2</code></td>
 * <td>Füllbytes für Datenausrichtung in 4 Byte Blöcken.</td>
 * </tr>
 * <tr>
 * <td><code>value[0..size-1]</code></td>
 * <td><code>ValueItem</code></td>
 * <td><code>size</code></td>
 * <td>Speicherbereich mit den Daten der Zeichenketten.</td>
 * </tr>
 * <tr>
 * <td rowspan="2">
 * <code>ValueItem</code> <br>
 * Kodiert eine Zeichenkette.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>-</code></td>
 * <td>Länge der Zeichenkette.</td>
 * </tr>
 * <tr>
 * <td><code>data</code></td>
 * <td><code>char</code></td>
 * <td><code>size</code></td>
 * <td>Zeichen der Zeichenkette.</td>
 * </tr>
 * <tr>
 * <td rowspan="4">
 * <code>ChildrenPool<code><br>
 * Kodiert eine Auflistung von Kindknotenlisten.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Anzahl der Kindknotenlisten.</td>
 * </tr>
 * <tr>
 * <td><code>start[0..size]</code></td>
 * <td><code>int</code></td>
 * <td><code>size+1</code></td>
 * <td>Startpositionen der Kindknotenlisten im Speicherbereich entry.<br>
 * Der erste Kindknoten der i-ten Kindknotenliste liegt bei Kindknoten start[i] und der nach dem letzten bei start[i+1].</td>
 * </tr>
 * <tr>
 * <td><code>-</code></td>
 * <td><code>byte</code></td>
 * <td><code>0..12</code></td>
 * <td>Füllbytes für Datenausrichtung in 16 Byte Blöcken.</td>
 * </tr>
 * <tr>
 * <td><code>entry[0..size-1]</code></td>
 * <td><code>ChildrenList</code></td>
 * <td><code>size</code></td>
 * <td>Speicherbereich mit den Kindknoten der Kindknotenlisten.</td>
 * </tr>
 * <tr>
 * <td rowspan="2">
 * <code>ChildrenList</code><br>
 * Kodiert eine Kindknotenliste.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>-</code></td>
 * <td>Länge der Liste.</td>
 * </tr>
 * <tr>
 * <td><code>item[0..size-1]</code></td>
 * <td><code>ChildrenItem</code></td>
 * <td><code>size</code></td>
 * <td>Kindknoten der Liste.</td>
 * </tr>
 * <tr>
 * <td rowspan="5">
 * <code>ChildrenItem</code><br>
 * Kodiert einen Kindknoten als Text- oder Elementknoten.</td>
 * <td><code>uri</code></td>
 * <td><code>short</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den URI des Elementknoten im namePool.<br>
 * Ist bei abwesendem URI oder einem Textknoten -1.</td>
 * </tr>
 * <tr>
 * <td><code>name</code></td>
 * <td><code>short</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den Namen des Elementknoten im namePool.<br>
 * Ist bei einem Textknoten -1.</td>
 * </tr>
 * <tr>
 * <td><code>content</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den Wert des Textknoten bzw. den Textinhalt des kindelementlosen Elementknoten im valuePool.<br>
 * Ist bei einem Elementknoten mit Kindelementen -1.</td>
 * </tr>
 * <tr>
 * <td><code>children</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf die Kindknotenliste des Elementknoten im childrenPool.<br>
 * Ist bei einem kindelementlosen Elementknoten oder einem Textknoten -1.</td>
 * </tr>
 * <tr>
 * <td><code>attributes</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf die Attributknotenliste des Elementknoten im attributesPool.<br>
 * Ist bei einer leeren Attributknotenliste oder einem Textknoten -1.</td>
 * </tr>
 * <tr>
 * <td rowspan="4">
 * <code>AttributesPool</code><br>
 * Kodiert eine Auflistung von Attributknotenlisten.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Anzahl der Attributknotenlisten.</td>
 * </tr>
 * <tr>
 * <td><code>start[0..size]</code></td>
 * <td><code>int</code></td>
 * <td><code>size+1</code></td>
 * <td>Startpositionen der Attributknotenlisten im Speicherbereich entry.<br>
 * Der erste Attributknoten der i-ten Attributknotenliste liegt bei Attributknoten start[i] und der nach dem letzten bei start[i+1].</td>
 * </tr>
 * <tr>
 * <td><code>-</code></td>
 * <td><code>byte</code></td>
 * <td><code>0..4</code></td>
 * <td>Füllbytes für Datenausrichtung in 8 Byte Blöcken.</td>
 * </tr>
 * <tr>
 * <td><code>entry[0..size-1]</code></td>
 * <td><code>AttributesList</code></td>
 * <td><code>size</code></td>
 * <td>Speicherbereich mit den Attributknoten der Attributknotenlisten.</td>
 * </tr>
 * <tr>
 * <td rowspan="2">
 * <code>AttributesList</code> <br>
 * Kodiert eine Attributknotenliste.</td>
 * <td><code>size</code></td>
 * <td><code>int</code></td>
 * <td><code>-</code></td>
 * <td>Länge der Liste.</td>
 * </tr>
 * <tr>
 * <td><code>item[0..size-1]</code></td>
 * <td><code>AttributeItem</code></td>
 * <td><code>size</code></td>
 * <td>Attributknoten der Liste.</td>
 * </tr>
 * <tr>
 * <td rowspan="3">
 * <code>AttributeItem</code> <br>
 * Kodiert einen Attributknoten.</td>
 * <td><code>uri</code></td>
 * <td><code>short</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den URI des Attributknoten im namePool.<br>
 * Ist bei abwesendem URI -1.</td>
 * </tr>
 * <tr>
 * <td><code>name</code></td>
 * <td><code>short</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den Namen des Attributknoten im namePool.</td>
 * </tr>
 * <tr>
 * <td><code>value</code></td>
 * <td><code>int</code></td>
 * <td><code>1</code></td>
 * <td>Referenz auf den Wert des Attributknoten im valuePool.</td>
 * </tr>
 * </table>
 * <p>
 * Binärdatei Blockweise in den Arbeitsspeicher laden und dort zur Wiederverwendung gemäß einer most-recently-used Strategie vorhalten. Die size-Listen sollten
 * dazu vollständig im Arbeitsspeicher vorgehalten werden.
 * 
 * @see Decoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Encoder {

	/**
	 * Diese Klasse implementiert einen über einen {@link #key Schlüssel} referenzierbaren Datensatz, der in einem {@link Pool} verwaltet wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static class Item {

		/**
		 * Dieses Feld speichert das leere {@link Item} mit dem Schlüssel {@code -1}.
		 */
		static final Item VOID = new Item();

		/**
		 * Dieses Feld speichert den Schlüssel zut Referenzierung.
		 */
		public int key = -1;

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link UniqueSet} zur Verwaltung einzigartiger {@link Item}s mit beliebigen Nutzdaten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 * @param <GItem> Typ der {@link Item}s.
	 */
	static abstract class Pool<GData, GItem extends Item> extends UniqueSet<GItem> implements Comparator<GItem> {

		/**
		 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
		 */
		int reuses = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final GItem compile(final GItem input) {
			input.key = 1;
			return input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void reuse(final GItem input, final GItem output) {
			output.key++;
			this.reuses++;
		}

		/**
		 * Diese Methode gibt ein neues {@link Item} mit den gegebenen Nutzdaten zurück.
		 * 
		 * @param data Nutzdaten.
		 * @return neues {@link Item}.
		 */
		public abstract GItem item(GData data);

		/**
		 * Diese Methode gibt das einzigartige {@link Item} mit den gegebenen Nutzdaten zurück. Wenn die Nutzdaten {@code null} sind, wird {@link Item#VOID} zurück
		 * gegeben.
		 * 
		 * @see #get(Object)
		 * @param data Nutzdaten.
		 * @return einzigartiges {@link Item}.
		 */
		public final Item unique(final GData data) {
			return data == null ? Item.VOID : this.get(this.item(data));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int compare(final GItem o1, final GItem o2) {
			return o2.key - o1.key;
		}

		/**
		 * Diese Methode leert die gesammelten Objekte.
		 */
		public final void clear() {
			this.entryMap.clear();
		}

		/**
		 * Diese Methode gibt die nach {@link Item#key} absteigend sortierte Liste aller {@link Item}s zurück.
		 * 
		 * @return sortierte Liste aller {@link Item}s.
		 */
		public final List<GItem> items() {
			final List<GItem> items = new ArrayList<GItem>(this.entryMap.values());
			Collections.sort(items, this);
			return items;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.entryMap.size(), this.reuses);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Item} zur Abbildung von Kind- bzw. Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class EntryItem extends Item {

		/**
		 * Dieses Feld speichert die Kind- bzw. Attributknotenliste. Ein Kindknoten besteht immer aus 5 auf einander folgenden Elementen, ein Attributknoten aus 3.
		 */
		public final Item[] data;

		/**
		 * Dieser Konstruktor initialisiert die Kind- bzw. Attributknotenliste.
		 * 
		 * @param data Kind- bzw. Attributknotenliste.
		 */
		public EntryItem(final Item[] data) {
			this.data = data;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link EntryItem}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class EntryPool extends Pool<Item[], EntryItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EntryItem item(final Item[] data) {
			return new EntryItem(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash(final EntryItem input) throws NullPointerException {
			return Arrays.hashCode(input.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final EntryItem input1, final EntryItem input2) throws NullPointerException {
			final Item[] data1 = input1.data, data2 = input2.data;
			final int length = data1.length;
			if(length != data2.length) return false;
			for(int i = 0; i < length; i++)
				if(data1[i] != data2[i]) return false;
			return true;
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Item} zur Abbildung von Textwerten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ValueItem extends Item {

		/**
		 * Dieses Feld speichert den Textwert.
		 */
		public final char[] data;

		/**
		 * Dieser Konstruktor initialisiert den Textwert.
		 * 
		 * @param data Textwert.
		 */
		public ValueItem(final String data) {
			this.data = data.toCharArray();
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link ValueItem}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ValuePool extends Pool<String, ValueItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ValueItem item(final String data) {
			return new ValueItem(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash(final ValueItem input) throws NullPointerException {
			return Arrays.hashCode(input.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final ValueItem input1, final ValueItem input2) throws NullPointerException {
			return Arrays.equals(input1.data, input2.data);
		}

	}

	/**
	 * Dieses Feld speichert die Anzahl der bisher geschriebenen Byte.
	 */
	int cursor;

	/**
	 * Dieses Feld speichert den Schreibpuffer für {@link EncodeTarget#write(byte[], int, int)}.
	 */
	final byte[] array;

	/**
	 * Dieses Feld speichert den Puffer zur Zusammenfassung benachbarter Textknoten.
	 */
	final StringBuilder text;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Node#getNamespaceURI()} und {@link Node#getNodeName()}.
	 */
	final ValuePool namePool;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Node#getNodeValue()}.
	 */
	final ValuePool valuePool;

	/**
	 * Dieses Feld speichert den {@link EntryPool} für {@link Node#getChildNodes()}.
	 */
	final EntryPool childrenPool;

	/**
	 * Dieses Feld speichert den {@link EntryPool} für {@link Node#getAttributes()}.
	 */
	final EntryPool attributesPool;

	/**
	 * Dieser Konstruktor initialisiert den {@link Encoder}.
	 */
	public Encoder() {
		this.text = new StringBuilder();
		this.array = new byte[16];
		this.namePool = new ValuePool();
		this.valuePool = new ValuePool();
		this.childrenPool = new EntryPool();
		this.attributesPool = new EntryPool();
	}

	/**
	 * Diese Methode schreibt ausrichtende Füllwerte in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param alignMask Bitmaske ((2^n)-1).
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeSpace(final EncodeTarget target, final int alignMask) throws IOException {
		final int cursor = this.cursor, insert = ((cursor + alignMask) & ~alignMask) - cursor;
		final byte[] array = this.array;
		Arrays.fill(array, 0, insert, (byte)0);
		target.write(array, 0, insert);
		// System.out.println("space: " + insert);
		// System.out.println(((FileEncodeTarget)target).file.getFilePointer());
		this.cursor = cursor + insert;
	}

	/**
	 * Diese Methode schreibt {@link ValueItem}s in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param items {@link ValueItem}s.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeValues(final EncodeTarget target, final List<ValueItem> items) throws IOException {
		final int size = items.size();
		final byte[] array = this.array;
		Bytes.set4(array, 0, size);
		Bytes.set4(array, 4, 0);
		target.write(array, 0, 8);
		int start = 0;
		for(int i = 0; i < size; i++){
			start += items.get(i).data.length;
			Bytes.set4(array, 0, start);
			target.write(array, 0, 4);
		}
		this.cursor += 4 + 4 + (size * 4) + (start * 2);
		this.writeSpace(target, 3);
		for(int i = 0; i < size; i++){
			final char[] data = items.get(i).data;
			final int length = data.length;
			for(int j = 0; j < length; j++){
				Bytes.set2(array, 0, data[j]);
				target.write(array, 0, 2);
			}
		}
	}

	/**
	 * Diese Methode schreibt die {@link EntryItem}s der Kindknotenlisten in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param items {@link EntryItem}s der Kindknotenlisten.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeChildren(final EncodeTarget target, final List<EntryItem> items) throws IOException {
		final int size = items.size();
		final byte[] array = this.array;
		Bytes.set4(array, 0, size);
		Bytes.set4(array, 4, 0);
		target.write(array, 0, 8);
		int start = 0;
		for(int i = 0; i < size; i++){
			start += items.get(i).data.length / 5;
			Bytes.set4(array, 0, start);
			target.write(array, 0, 4);
		}
		this.cursor += 4 + 4 + (size * 4) + (start * 16);
		this.writeSpace(target, 15);
		for(int i = 0; i < size; i++){
			final Item[] data = items.get(i).data;
			final int length = data.length;
			for(int j = 0; j < length;){
				Bytes.set2(array, 0, data[j++].key);
				Bytes.set2(array, 2, data[j++].key);
				Bytes.set4(array, 4, data[j++].key);
				Bytes.set4(array, 8, data[j++].key);
				Bytes.set4(array, 12, data[j++].key);
				target.write(array, 0, 16);
			}
		}
	}

	/**
	 * Diese Methode schreibt die {@link EntryItem}s der Attributknotenlisten in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param items {@link EntryItem}s der Attributknotenlisten.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeAttributes(final EncodeTarget target, final List<EntryItem> items) throws IOException {
		final int size = items.size();
		final byte[] array = this.array;
		Bytes.set4(array, 0, size);
		Bytes.set4(array, 4, 0);
		target.write(array, 0, 8);
		int start = 0;
		for(int i = 0; i < size; i++){
			start += items.get(i).data.length / 3;
			Bytes.set4(array, 0, start);
			target.write(array, 0, 4);
		}
		this.cursor += 4 + 4 + (size * 4) + (start * 8);
		this.writeSpace(target, 7);
		for(int i = 0; i < size; i++){
			final Item[] data = items.get(i).data;
			final int length = data.length;
			for(int j = 0; j < length;){
				Bytes.set2(array, 0, data[j++].key);
				Bytes.set2(array, 2, data[j++].key);
				Bytes.set4(array, 4, data[j++].key);
				target.write(array, 0, 8);
			}
		}
	}

	/**
	 * Diese Methode kodiert den gegebenen {@link String} in die Daten eines Textknoten und gibt diese zurück. Wenn der {@link String} leer ist, wird {@code null}
	 * zurück gegeben.
	 * 
	 * @param text Text.
	 * @return Daten eines Textknoten oder {@code null}.
	 */
	Item[] encodeText(final String text) {
		if(text.isEmpty()) return null;
		return new Item[]{Item.VOID, Item.VOID, this.valuePool.unique(text), Item.VOID, Item.VOID};
	}

	/**
	 * Diese Methode kodiert den gegebenen {@link Node} in die Daten eines Elementknoten und gibt diese zurück.
	 * 
	 * @param element {@link Node}.
	 * @return Daten eines Elementknoten.
	 */
	Item[] encodeElement(final Node element) {
		final Item[] children = this.encodeChildren(element.getChildNodes());
		final Item[] attributes = this.encodeAttributes(element.getAttributes());
		final Item uriRef = this.namePool.unique(element.getNamespaceURI());
		final Item nameRef = this.namePool.unique(element.getNodeName());
		final Item contentRef;
		final Item childrenRef;
		final Item attributesRef;
		if(children.length > 0){
			if((children.length == 5) && (children[1] == Item.VOID)){
				contentRef = children[2];
				childrenRef = Item.VOID;
			}else{
				contentRef = Item.VOID;
				childrenRef = this.childrenPool.unique(children);
			}
		}else{
			contentRef = Item.VOID;
			childrenRef = Item.VOID;
		}
		if(attributes.length > 1){
			attributesRef = this.attributesPool.unique(attributes);
		}else{
			attributesRef = Item.VOID;
		}
		return new Item[]{uriRef, nameRef, contentRef, childrenRef, attributesRef};
	}

	/**
	 * Diese Methode kodiert die gegebene {@link NodeList} in die Daten einer Kindknotenliste und gibt diese zurück.
	 * 
	 * @param nodes {@link NodeList}.
	 * @return Daten einer Kindknotenliste.
	 */
	Item[] encodeChildren(final NodeList nodes) {
		final StringBuilder text = this.text;
		final List<Item[]> items = new ArrayList<Item[]>();
		final int length = nodes.getLength();
		for(int i = 0; i < length; i++){
			final Node node = nodes.item(i);
			switch(node.getNodeType()){
				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE:
					text.append(node.getNodeValue());
					break;
				case Node.ELEMENT_NODE:
					items.add(this.encodeText(text.toString()));
					text.setLength(0);
					items.add(this.encodeElement(node));
					break;
				default:
					throw new IllegalArgumentException("Type of node not supported: " + node.getNodeType());
			}
		}
		items.add(this.encodeText(text.toString()));
		text.setLength(0);
		items.removeAll(Collections.singleton(null));
		final int size = items.size();
		final Item[] data = new Item[size * 5];
		for(int i = 0; i < size; i++){
			System.arraycopy(items.get(i), 0, data, i * 5, 5);
		}
		return data;
	}

	/**
	 * Diese Methode kodiert die gegebene {@link NamedNodeMap} in die Daten einer Attributknotenliste und gibt diese zurück.
	 * 
	 * @param nodes {@link NamedNodeMap}.
	 * @return Daten einer Attributknotenliste.
	 */
	Item[] encodeAttributes(final NamedNodeMap nodes) {
		final int length = nodes.getLength();
		final Item[] data = new Item[length * 3];
		for(int i = 0, j = 0; i < length; i++){
			final Node node = nodes.item(i);
			data[j++] = this.namePool.unique(node.getNamespaceURI());
			data[j++] = this.namePool.unique(node.getNodeName());
			data[j++] = this.valuePool.unique(node.getNodeValue());
		}
		return data;
	}

	/**
	 * Diese Methode nummeriert die Schlüssel der gegebenen {@link Item}s aufsteigend.
	 * 
	 * @see Pool#items()
	 * @param items {@link Item}s.
	 */
	void prepareKeys(final List<? extends Item> items) {
		int key = 0;
		for(final Item item: items){
			item.key = key++;
		}
	}

	/**
	 * Diese Methode kodiert das gegebene {@link Document} in das Binärformat und schreibt dieses in das gegebene {@link EncodeTarget}.
	 * 
	 * @param source {@link Document}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public void encode(final Document source, final EncodeTarget target) throws IOException {
		this.cursor = 0;
		this.text.setLength(0);
		this.namePool.clear();
		this.valuePool.clear();
		this.childrenPool.clear();
		this.attributesPool.clear();
		final Item[] children = this.encodeChildren(source.getChildNodes());
		if((children.length != 5) || (children[1] == Item.VOID)) throw new IllegalArgumentException("Document must have one child element.");
		final Item childrenRef = this.childrenPool.unique(children);
		final List<ValueItem> nameList = this.namePool.items();
		final List<ValueItem> valueList = this.valuePool.items();
		final List<EntryItem> childrenList = this.childrenPool.items();
		final List<EntryItem> attributesList = this.attributesPool.items();
		this.prepareKeys(nameList);
		this.prepareKeys(valueList);
		this.prepareKeys(childrenList);
		this.prepareKeys(attributesList);
		this.writeValues(target, nameList);
		this.writeValues(target, valueList);
		this.writeChildren(target, childrenList);
		this.writeAttributes(target, attributesList);
		final byte[] array = this.array;
		Bytes.set4(array, 0, childrenRef.key);
		target.write(array, 0, 4);
		// System.out.println(Objects.toStringCallFormat(true, true, this, //
		// "namePool", this.namePool, //
		// "valuePool", this.valuePool, //
		// "childrenPool", this.childrenPool, //
		// "attributesPool", this.attributesPool //
		// ));
	}

}