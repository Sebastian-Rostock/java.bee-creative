package bee.creative.xml.bex;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import bee.creative.util.Bytes;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueSet;

 final class Encoder2 {

	/**
	 * Diese Klasse implementiert einen über einen {@link #key Schlüssel} referenzierbaren Datensatz, der in einem {@link Pool} verwaltet wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static class Item {

		/**
		 * Dieses Feld speichert das leere {@link Item} mit dem Schlüssel {@code 0}.
		 */
		static final Item VOID = new Item();

		/**
		 * Dieses Feld speichert den Schlüssel zur Referenzierung.
		 */
		public int key = 0;

		public int size;

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link UniqueSet} zur Verwaltung einzigartiger {@link Item}s mit beliebigen Nutzdaten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 * @param <GItem> Typ der {@link Item}s.
	 */
	static abstract class Pool<GData, GItem extends Item> extends UniqueSet<GItem> implements Comparator<GItem> {
		int reuses=0;
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
			output.key++;	reuses++;
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
		@Override
		public String toString() {
			return ""+reuses;
		}
	}

	/**
	 * Diese Klasse implementiert ein {@link Item} zur Abbildung von Textwerten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class TextItem extends Item {

		static final Charset CHARSET = Charset.forName("UTF-8");

		/**
		 * Dieses Feld speichert den Textwert.
		 */
		public final byte[] data;

		/**
		 * Dieser Konstruktor initialisiert den Textwert.
		 * 
		 * @param data Textwert.
		 */
		public TextItem(final String data) {
			this.data = data.getBytes(TextItem.CHARSET);
			this.size = this.data.length;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link TextItem}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class TextPool extends Pool<String, TextItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TextItem item(final String data) {
			return new TextItem(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash(final TextItem input) throws NullPointerException {
			return Arrays.hashCode(input.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final TextItem input1, final TextItem input2) throws NullPointerException {
			return Arrays.equals(input1.data, input2.data);
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
		 * @param size Länge.
		 */
		public EntryItem(final Item[] data, final int size) {
			this.data = data;
			this.size = size;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link EntryItem}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class EntryPool extends Pool<Item[], EntryItem> {

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

	static final class ChildrenPool extends EntryPool {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EntryItem item(final Item[] data) {
			return new EntryItem(data, data.length / 4);
		}

	}

	static final class AttributesPool extends EntryPool {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EntryItem item(final Item[] data) {
			return new EntryItem(data, data.length / 3);
		}

	}

	boolean uriEnabled;

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
	 * Dieses Feld speichert den {@link TextPool} für {@link Attr#getNamespaceURI()}.
	 */
	final TextPool attrUriPool;

	int attrUriLength;

	/**
	 * Dieses Feld speichert den {@link TextPool} für {@link Attr#getNodeName()}.
	 */
	final TextPool attrNamePool;

	int attrNameLength;

	/**
	 * Dieses Feld speichert den {@link TextPool} für {@link Attr#getNodeValue()}.
	 */
	final TextPool attrValuePool;

	int attrValueLength;

	/**
	 * Dieses Feld speichert den {@link TextPool} für {@link Element#getNamespaceURI()}.
	 */
	final TextPool elemUriPool;

	int elemUriLength;

	/**
	 * Dieses Feld speichert den {@link TextPool} für {@link Element#getNodeName()}.
	 */
	final TextPool elemNamePool;

	int elemNameLength;

	/**
	 * Dieses Feld speichert den {@link TextPool} für {@link Text#getNodeValue()}.
	 */
	final TextPool elemValuePool;

	/**
	 * Dieses Feld speichert den {@link EntryPool} für {@link Node#getChildNodes()}.
	 */
	final EntryPool elemChildrenPool;

	int elemContentLength;

	/**
	 * Dieses Feld speichert den {@link EntryPool} für {@link Node#getAttributes()}.
	 */
	final EntryPool elemAttributesPool;

	int elemAttributesLength;

	/**
	 * Dieser Konstruktor initialisiert den {@link Encoder2}.
	 */
	public Encoder2() {
		this.text = new StringBuilder();
		this.array = new byte[16];
		this.attrUriPool = new TextPool();
		this.attrNamePool = new TextPool();
		this.attrValuePool = new TextPool();
		this.elemUriPool = new TextPool();
		this.elemNamePool = new TextPool();
		this.elemValuePool = new TextPool();
		this.elemChildrenPool = new ChildrenPool();
		this.elemAttributesPool = new AttributesPool();
	}

	void clear() {
		this.cursor = 0;
		this.text.setLength(0);
		this.attrUriPool.clear();
		this.attrNamePool.clear();
		this.elemChildrenPool.clear();
		this.elemAttributesPool.clear();
	}

	void write(final EncodeTarget target, final int value, final int size) throws IOException {
		final byte[] array = this.array;
		switch(size){
			case 0:
				return;
			case 1:
				Bytes.set1(array, 0, value);
				break;
			case 2:
				Bytes.set2(array, 0, value);
				break;
			case 3:
				Bytes.set3(array, 0, value);
				break;
			case 4:
				Bytes.set4(array, 0, value);
				break;
			default:
				throw new IllegalArgumentException();
		}
		target.write(array, 0, size);
	}

	void writeOffsets(final EncodeTarget target, final List<? extends Item> items) throws IOException {
		final int size = items.size();
		final int[] offsets = new int[size];
		int offset = 0;
		for(int i = 0; i < size; i++){
			offset += items.get(i).size;
			offsets[i] = offset;
		}
		final int length = Encoder2.computeLength(offset);
		this.write(target, size, 4);
		this.write(target, length, 1);
		for(int i = 0; i < size; i++){
			this.write(target, offsets[i], length);
		}
	}

	/**
	 * Diese Methode schreibt {@link TextItem}s in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param items {@link TextItem}s.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeValues(final EncodeTarget target, final List<TextItem> items) throws IOException {
		this.writeOffsets(target, items);
		final int size = items.size();
		for(int i = 0; i < size; i++){
			final byte[] data = items.get(i).data;
			target.write(data, 0, data.length);
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
		this.writeOffsets(target, items);
		final int size = items.size();
		for(int i = 0; i < size; i++){
			final Item[] data = items.get(i).data;
			final int count = data.length;
			for(int j = 0; j < count;){
				this.write(target, data[j++].key, this.elemUriLength);
				this.write(target, data[j++].key, this.elemNameLength);
				this.write(target, data[j++].key, this.elemContentLength);
				this.write(target, data[j++].key, this.elemAttributesLength);
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
		this.writeOffsets(target, items);
		final int size = items.size();
		for(int i = 0; i < size; i++){
			final Item[] data = items.get(i).data;
			final int count = data.length;
			for(int j = 0; j < count;){
				this.write(target, data[j++].key, this.attrUriLength);
				this.write(target, data[j++].key, this.attrNameLength);
				this.write(target, data[j++].key, this.attrValueLength);
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
		return new Item[]{Item.VOID, Item.VOID, this.elemValuePool.unique(text), Item.VOID};
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
		final Item uriRef = this.uriEnabled ? this.elemUriPool.unique(element.getNamespaceURI()) : Item.VOID;
		final Item nameRef = this.elemNamePool.unique(element.getNodeName());
		final Item contentRef;
		final Item attributesRef;
		if(children.length > 0){
			if((children.length == 4) && (children[1] == Item.VOID)){
				contentRef = children[2];
			}else{
				contentRef = this.elemChildrenPool.unique(children);
			}
		}else{
			contentRef = Item.VOID;
		}
		if(attributes.length > 1){
			attributesRef = this.elemAttributesPool.unique(attributes);
		}else{
			attributesRef = Item.VOID;
		}
		return new Item[]{uriRef, nameRef, contentRef, attributesRef};
	}

	/**
	 * Diese Methode kodiert die gegebene {@link NodeList} in die Daten einer Kindknotenliste und gibt diese zurück.
	 * 
	 * @param nodes {@link NodeList}.
	 * @return Daten einer Kindknotenliste.
	 */
	Item[] encodeChildren(final NodeList nodes) {
		final StringBuilder text = this.text;
		final int length = nodes.getLength();
		final List<Item[]> items = new ArrayList<Item[]>(length);
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
		final Item[] data = new Item[size * 4];
		for(int i = 0; i < size; i++){
			System.arraycopy(items.get(i), 0, data, i * 4, 4);
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
			data[j++] = this.uriEnabled ? this.attrUriPool.unique(node.getNamespaceURI()) : Item.VOID;
			data[j++] = this.attrNamePool.unique(node.getNodeName());
			data[j++] = this.attrValuePool.unique(node.getNodeValue());
		}
		return data;
	}

	/**
	 * Diese Methode nummeriert die Schlüssel der gegebenen {@link Item}s aufsteigend.
	 * 
	 * @see Pool#items()
	 * @param items {@link Item}s.
	 * @param offset Schlüssel des ersten {@link Item}s.
	 */
	void computeKeys(final List<? extends Item> items, int offset) {
		for(final Item item: items){
			item.key = offset++;
		}
	}

	static int computeLength(final int maxValue) {
		if(maxValue >= 0x01000000) return 4;
		if(maxValue >= 0x010000) return 3;
		if(maxValue >= 0x0100) return 2;
		if(maxValue >= 0x01) return 1;
		return 0;
	}

	/**
	 * Diese Methode kodiert das gegebene {@link Document} in das Binärformat und schreibt dieses in das gegebene {@link EncodeTarget}.
	 * 
	 * @param source {@link Document}.
	 * @param target {@link EncodeTarget}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public void encode(final Document source, final EncodeTarget target) throws IOException {
		this.clear();
		final Item[] children = this.encodeChildren(source.getChildNodes());
		if((children.length != 4) || (children[1] == Item.VOID)) throw new IllegalArgumentException("Document must have one child element.");
		final Item childrenRef = this.elemChildrenPool.unique(children);
		final List<TextItem> attrUriList = this.attrUriPool.items();
		final List<TextItem> attrNameList = this.attrNamePool.items();
		final List<TextItem> attrValueList = this.attrValuePool.items();
		final List<TextItem> elemUriList = this.elemUriPool.items();
		final List<TextItem> elemNameList = this.elemNamePool.items();
		final List<TextItem> elemValueList = this.elemValuePool.items();
		final List<EntryItem> elemChildrenList = this.elemChildrenPool.items();
		final List<EntryItem> elemAttributesList = this.elemAttributesPool.items();
		this.computeKeys(attrUriList, 1);
		this.computeKeys(attrNameList, 0);
		this.computeKeys(attrValueList, 0);
		this.computeKeys(elemUriList, 1);
		this.computeKeys(elemNameList, 1);
		this.computeKeys(elemValueList, 1);
		this.computeKeys(elemChildrenList, 1 + elemValueList.size());
		this.computeKeys(elemAttributesList, 1);
		this.attrUriLength = Encoder2.computeLength(attrUriList.size());
		this.attrNameLength = Encoder2.computeLength(attrNameList.size() - 1);
		this.attrValueLength = Encoder2.computeLength(attrValueList.size() - 1);
		this.elemUriLength = Encoder2.computeLength(elemUriList.size());
		this.elemNameLength = Encoder2.computeLength(elemNameList.size());
		this.elemContentLength = Encoder2.computeLength(elemValueList.size() + elemChildrenList.size());
		this.elemAttributesLength = Encoder2.computeLength(elemAttributesList.size());
		this.writeValues(target, attrUriList);
		this.writeValues(target, attrNameList);
		this.writeValues(target, attrValueList);
		this.writeValues(target, elemUriList);
		this.writeValues(target, elemNameList);
		this.writeValues(target, elemValueList);
		this.writeAttributes(target, elemAttributesList);
		this.writeChildren(target, elemChildrenList);
		this.write(target, childrenRef.key, this.elemContentLength);
		
		System.out.println(Objects.toStringCallFormat(true, true, this, //
			"attrUriPool", this.attrUriPool, //
			"attrNamePool", this.attrNamePool, //
			"attrValuePool", this.attrValuePool, //
			"elemUriPool", this.elemUriPool, //
			"elemNamePool", this.elemNamePool, //
			"elemValuePool", this.elemValuePool, //
			"elemChildrenPool", this.elemChildrenPool, //
			"elemAttributesPool", this.elemAttributesPool //
			));
		
		this.clear();
	}

	public boolean isUriEnabled() {
		return this.uriEnabled;
	}

	public void setUriEnabled(final boolean uriEnabled) {
		this.uriEnabled = uriEnabled;
	}

}