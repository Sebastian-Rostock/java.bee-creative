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

/**
 * Diese Klasse implementiert ein Objekt zur Kodierung eines {@link Document}s in das Binärformat BEX.
 * <p>
 * <h3>BEX – Binary Encoded XML (Document)</h3>
 * <p>
 * Binary Encoded XML (Document) beschreibt das binäre Datenformat zur redundanzarmen Abbildung eines XML Dokuments. Ziel dieses Formats ist es, eine
 * leichtgewichtige, nur lesende DOM-Implementation darauf aufsetzen zu können, welche signifikant weniger Arbeitsspeicher verbraucht, als eine zumeist auch
 * schreiben könnende Implementation einer Standard XML Softwarebibliothek. Diese leichtgewichtige DOM-Implementation kann die Binärdatei Blockweise in den
 * Arbeitsspeicher laden und dort zur Wiederverwendung gemäß einer most-recently-used Strategie vorhalten. Die offset-Listen sollten dazu vollständig im
 * Arbeitsspeicher vorgehalten werden.
 * <p>
 * In der Klasse {@link Decoder} wird eine solche leichtgewichtige DOM-Implementation bereit gestellt.
 * <p>
 * <h4>Datenstruktur: BEX</h4>
 * <p>
 * Kodiert einen DOM Dokumentknoten mit seinen enthaltenen Text-, Element- und Attributknoten als binäre Datenstruktur zum wahlfreien Zugriff.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>attrUriPool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die URIs der Attributknoten.</td>
 * </tr>
 * <tr>
 * <td>attrNamePool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die Namen der Attributknoten.</td>
 * </tr>
 * <tr>
 * <td>attrValuePool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die Werte der Attributknoten.</td>
 * </tr>
 * <tr>
 * <td>attrGroupPool</td>
 * <td>AttrGroupPool</td>
 * <td>1</td>
 * <td>Auflistung der Attributknotenlisten.</td>
 * </tr>
 * <tr>
 * <td>elemUriPool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die URIs der Elementknoten.</td>
 * </tr>
 * <tr>
 * <td>elemNamePool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die Namen der Elementknoten.</td>
 * </tr>
 * <tr>
 * <td>elemValuePool</td>
 * <td>TextValuePool</td>
 * <td>1</td>
 * <td>Auflistung der Zeichenketten für die Werte in Elementknoten.</td>
 * </tr>
 * <tr>
 * <td>elemGroupPool</td>
 * <td>ElemGroupPool</td>
 * <td>1</td>
 * <td>Auflistung der Kindknotenlisten für Element- und Dokumentknoten.</td>
 * </tr>
 * <tr>
 * <td>attributesPool</td>
 * <td>AttributesPool</td>
 * <td>1</td>
 * <td>Auflistung der Kindknotenlisten.</td>
 * </tr>
 * <tr>
 * <td>documentGroupRef</td>
 * <td>INT1..4</td>
 * <td>1</td>
 * <td>Kindknotenliste des Dokumentknoten. Ist (i+1+elemValuePool.size) als Referenz auf die i-te Kindknotenliste in elemGroupPool.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: TextValuePool</h4>
 * <p>
 * Kodiert eine Auflistung von Zeichenketten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>INT4</td>
 * <td>1</td>
 * <td>Anzahl der Zeichenketten.</td>
 * </tr>
 * <tr>
 * <td>length</td>
 * <td>INT1</td>
 * <td>1</td>
 * <td>Anzahl der Byte je Startposition.</td>
 * </tr>
 * <tr>
 * <td>offset</td>
 * <td>INT1..4</td>
 * <td>size</td>
 * <td>Startpositionen der Zeichenketten im Speicherbereich item. Das erste Byte der i-ten Zeichenkette liegt bei Position offset[i] und das nach dem letzten
 * bei offset[i+1]. Die Startposition offset[0] ist implizit 0.</td>
 * </tr>
 * <tr>
 * <td>item</td>
 * <td>TextValueItem</td>
 * <td>size</td>
 * <td>Speicherbereich mit den Bytes der Zeichenketten.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: TextValueItem</h4>
 * <p>
 * Kodiert eine Zeichenkette.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>-</td>
 * <td>-</td>
 * <td>Anzahl der Bytes in data.</td>
 * </tr>
 * <tr>
 * <td>data</td>
 * <td>INT1</td>
 * <td>size</td>
 * <td>Bytes der UTF-8-kodierten Zeichenkette.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: AttrGroupPool</h4>
 * <p>
 * Kodiert eine Auflistung von Attributknotenlisten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>INT4</td>
 * <td>1</td>
 * <td>Anzahl der Attributknotenlisten.</td>
 * </tr>
 * <tr>
 * <td>length</td>
 * <td>INT1</td>
 * <td>1</td>
 * <td>Anzahl der Byte je Startposition.</td>
 * </tr>
 * <tr>
 * <td>offset</td>
 * <td>INT1..4</td>
 * <td>size</td>
 * <td>Startpositionen der Attributknotenlisten im Speicherbereich item. Der erste Attributknoten der i-ten Attributknotenliste liegt bei Position offset[i] und
 * der nach dem letzten bei offset[i+1]. Die Startposition offset[0] ist implizit 0.</td>
 * </tr>
 * <tr>
 * <td>item</td>
 * <td>AttrGroupItem</td>
 * <td>size</td>
 * <td>Speicherbereich mit den Attributknoten aller Attributknotenlisten.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: AttrGroupItem</h4>
 * <p>
 * Kodiert eine Attributknotenliste.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>-</td>
 * <td>-</td>
 * <td>Länge der Liste.</td>
 * </tr>
 * <tr>
 * <td>node</td>
 * <td>AttrGrpupNode</td>
 * <td>size</td>
 * <td>Attributknoten der Liste.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: AttrGrpupNode</h4>
 * <p>
 * Kodiert einen Attributknoten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>uriRef</td>
 * <td>INT1..4</td>
 * <td>0..1</td> <tdIst abwesend, wenn der attrUriPool leer ist. Ist (0), wenn der Attributknoten keinen URI besitzt. Ist (i+1) als Referenz auf den i-ten URI
 * im attrUriPool.</td>
 * </tr>
 * <tr>
 * <td>nameRef</td>
 * <td>INT1..4</td>
 * <td>1</td>
 * <td>Ist (i) als Referenz auf den i-ten Namen im attrNamePool.</td>
 * </tr>
 * <tr>
 * <td>valueRef</td>
 * <td>INT1..4</td>
 * <td>1</td>
 * <td>Ist (i) als Referenz auf den i-ten Wert im attrValuePool.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: ElemGroupPool</h4>
 * <p>
 * Kodiert eine Auflistung von Kindknotenlisten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>INT4</td>
 * <td>1</td>
 * <td>Anzahl der Kindknotenlisten.</td>
 * </tr>
 * <tr>
 * <td>length</td>
 * <td>INT1</td>
 * <td>1</td>
 * <td>Anzahl der Byte je Startposition.</td>
 * </tr>
 * <tr>
 * <td>offset</td>
 * <td>INT1..4</td>
 * <td>size</td>
 * <td>Startpositionen der Kindknotenlisten im Speicherbereich item. Der erste Kindknoten der i-ten Kindknotenliste liegt bei Position offset[i] und der nach
 * dem letzten bei offset[i+1]. Die Startposition offset[0] ist implizit 0.</td>
 * </tr>
 * <tr>
 * <td>item</td>
 * <td>ElemGroupItem</td>
 * <td>size</td>
 * <td>Speicherbereich mit den Kindknoten der Kindknotenlisten.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: ElemGroupItem</h4>
 * <p>
 * Kodiert eine Auflistung von Kindknotenlisten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>size</td>
 * <td>-</td>
 * <td>-</td>
 * <td>Länge der Liste.</td>
 * </tr>
 * <tr>
 * <td>node</td>
 * <td>ElemGrpupNode</td>
 * <td>size</td>
 * <td>Kindknoten der Liste.</td>
 * </tr>
 * </table>
 * <h4>Datenstruktur: ElemGrpupNode</h4>
 * <p>
 * Kodiert einen Kindknoten meist als Element- und (selten) Textknoten.
 * </p>
 * <table style="border: silver solid 2px; border-collapse: collapse" border="1">
 * <tr style="font-weight:bold">
 * <td>Feld</td>
 * <td>Format</td>
 * <td>Anzahl</td>
 * <td>Beschreibung</td>
 * </tr>
 * <tr>
 * <td>uriRef</td>
 * <td>INT1..4</td>
 * <td>0..1</td>
 * <td>Ist abwesend, wenn der elemUriPool leer ist. Ist (0), wenn der Kindknoten ein Textknoten oder ein Elementknoten ohne URI ist. Ist (i+1) als Referenz auf
 * den i-ten URI im elemUriPool, wenn der Kindknoten ein Elementknoten ist.</td>
 * </tr>
 * <tr>
 * <td>nameRef</td>
 * <td>INT1..4</td>
 * <td>1</td>
 * <td>Ist (0), wenn der Kindknoten ein Textknoten ist. Ist (i+1) als Referenz auf den i-ten Namen im elemNamePool, wenn der Kindknoten ein Elementknoten ist.</td>
 * </tr>
 * <tr>
 * <td>contentRef</td>
 * <td>INT1..4</td>
 * <td>1</td>
 * <td>Ist (0), wenn der Kindknoten ein Elementknoten ohne Kindknoten ist. Ist (i+1) als Referenz auf den i-ten Wert im elemValuePool, wenn der Kindknoten ein
 * Textknoten oder ein kindelementloser Elementknoten ist. Ist (i+1+elemValuePool.size) als Referenz auf die i-te Kindknotenliste im elemGroupPool, wenn der
 * Kindknoten ein Elementknoten mit Kindelementen ist.</td>
 * </tr>
 * <tr>
 * <td>attributesRef</td>
 * <td>INT1..4</td>
 * <td>0..1</td>
 * <td>Ist abwesend, wenn der attrGroupPool leer ist. Ist (0), wenn der Kindknoten ein Textknoten oder ein Elementknoten ohne Attributknoten ist. Ist (i+1) als
 * Referenz auf die i-te Attributknotenliste im attrGroupPool, wenn der Kindknoten ein Elementknoten mit Attributknoten ist.</td>
 * </tr>
 * </table>
 * <p>
 * 
 * @see Decoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Encoder {

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz, der über einen {@link #key Schlüssel} eindeutig referenziert und in einem {@link Pool} verwaltet
	 * werden kann. Die Nutzdaten eines solchen Datensatzes verfügt über eine abstrakte Größe (z.B. Zeichenanzahl, Knotenanzahl).
	 * 
	 * @see ValueItem
	 * @see GroupItem
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static class Item {

		/**
		 * Dieses Feld speichert das leere {@link Item} mit dem Schlüssel {@code 0}.
		 */
		static final Item VOID = new Item();

		/**
		 * Dieses Feld speichert den Schlüssel zur Referenzierung. Dieser wird beim Einpflegen in den {@link Pool} hichgezählt.
		 * 
		 * @see Pool#reuse(Item, Item)
		 */
		public int key = 0;

		/**
		 * Dieses Feld speichert die abstrakte Größe der Nutzdaten.
		 */
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

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der Rückgabewert entspricht der Eingabe. Der {@link Item#key Schlüssel} des {@link Item}s wird hier auf {@code 1} gesetzt.
		 */
		@Override
		protected final GItem compile(final GItem input) {
			input.key = 1;
			return input;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Der {@link Item#key Schlüssel} des {@link Item}s wird hier auf {@code 1} gesetzt.
		 */
		@Override
		protected final void reuse(final GItem input, final GItem output) {
			output.key++;
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
		 * @return einzigartiges {@link Item} oder {@link Item#VOID}.
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
			final int count = this.entryMap.size();
			int minSize = Integer.MAX_VALUE, maxSize = Integer.MIN_VALUE, avgSize = 0;
			for(final Item item: this.entryMap.values()){
				final int size = item.size;
				minSize = Math.min(minSize, size);
				maxSize = Math.max(maxSize, size);
				avgSize += size;
			}
			if(count == 0) return Objects.toStringCallFormat(false, true, this, "items", count, "minSize", Float.NaN, "maxSize", Float.NaN, "avgSize", Float.NaN);
			return Objects.toStringCallFormat(false, true, this, "items", count, "minSize", minSize, "maxSize", maxSize, "avgSize", avgSize / (float)count);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Item} zur Abbildung von Textwerten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ValueItem extends Item {

		/**
		 * Dieses Feld speichert die URF-8 Kodierten Textwert.
		 */
		public final byte[] data;

		/**
		 * Dieser Konstruktor initialisiert den Textwert.
		 * 
		 * @param data Textwert.
		 */
		public ValueItem(final String data) {
			this.data = data.getBytes(Encoder.CHARSET);
			this.size = this.data.length;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link ValueItem}s zur verwaltung einzigartiger Textwerte.
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
	 * Diese Klasse implementiert ein {@link Item} zur Abbildung von Kind- bzw. Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class GroupItem extends Item {

		/**
		 * Dieses Feld speichert die Kind- bzw. Attributknotenliste. Ein Kindknoten besteht immer aus 4 auf einander folgenden Elementen, ein Attributknoten aus 3.
		 */
		public final Item[] data;

		/**
		 * Dieser Konstruktor initialisiert die Kind- bzw. Attributknotenliste.
		 * 
		 * @param data Kind- bzw. Attributknotenliste.
		 * @param size Länge.
		 */
		public GroupItem(final Item[] data, final int size) {
			this.data = data;
			this.size = size;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Pool} der {@link GroupItem}s zur Verwaltung einzigartiger Kind- bzw. Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class GroupPool extends Pool<Item[], GroupItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash(final GroupItem input) throws NullPointerException {
			return Arrays.hashCode(input.data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final GroupItem input1, final GroupItem input2) throws NullPointerException {
			final Item[] data1 = input1.data, data2 = input2.data;
			final int length = data1.length;
			if(length != data2.length) return false;
			for(int i = 0; i < length; i++)
				if(data1[i] != data2[i]) return false;
			return true;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link GroupPool} für Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class AttrGroupPool extends GroupPool {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupItem item(final Item[] data) {
			return new GroupItem(data, data.length / 3);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link GroupPool} für Kindknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ElemGrpupPool extends GroupPool {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupItem item(final Item[] data) {
			return new GroupItem(data, data.length / 4);
		}

	}

	/**
	 * Dieses Feld speichert das UTF-8-{@link Charset} zur Kodierung der Textwerte.
	 */
	static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Wert abzubilden.
	 * 
	 * @param maxValue positiver Wert.
	 * @return Länge.
	 */
	static int lengthOf(final int maxValue) {
		if(maxValue >= 0x01000000) return 4;
		if(maxValue >= 0x010000) return 3;
		if(maxValue >= 0x0100) return 2;
		if(maxValue >= 0x01) return 1;
		return 0;
	}

	/**
	 * Dieses Feld speichert den Schreibpuffer für {@link EncodeTarget#write(byte[], int, int)}.
	 */
	final byte[] array;

	/**
	 * Dieses Feld speichert den Puffer zur Zusammenfassung benachbarter Textknoten.
	 */
	final StringBuilder text;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Attr#getNamespaceURI()}.
	 */
	final ValuePool attrUriPool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf den {@link Attr#getNamespaceURI()}.
	 */
	int attrUriLength;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Attr#getNodeName()}.
	 */
	final ValuePool attrNamePool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf den {@link Attr#getNodeName()}.
	 */
	int attrNameLength;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Attr#getNodeValue()}.
	 */
	final ValuePool attrValuePool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf den {@link Attr#getNodeValue()}.
	 */
	int attrValueLength;

	/**
	 * Dieses Feld speichert den {@link GroupPool} für {@link Element#getAttributes()}.
	 */
	final GroupPool attrGroupPool;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Element#getNamespaceURI()}.
	 */
	final ValuePool elemUriPool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf den {@link Element#getNamespaceURI()}.
	 */
	int elemUriLength;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Element#getNodeName()}.
	 */
	final ValuePool elemNamePool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf den {@link Element#getNodeName()}.
	 */
	int elemNameLength;

	/**
	 * Dieses Feld speichert den {@link ValuePool} für {@link Text#getNodeValue()}.
	 */
	final ValuePool elemValuePool;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf {@link Text#getNodeValue()} und {@link Node#getChildNodes()}.
	 */
	int elemContentLength;

	/**
	 * Dieses Feld speichert die Länge einer Referenz auf {@link Element#getAttributes()}.
	 */
	int elemAttributesLength;

	/**
	 * Dieses Feld speichert den {@link GroupPool} für {@link Node#getChildNodes()}.
	 */
	final GroupPool elemGroupPool;

	/**
	 * Dieser Konstruktor initialisiert den {@link Encoder}.
	 */
	public Encoder() {
		this.text = new StringBuilder();
		this.array = new byte[16];
		this.attrUriPool = new ValuePool();
		this.attrNamePool = new ValuePool();
		this.attrValuePool = new ValuePool();
		this.elemUriPool = new ValuePool();
		this.elemNamePool = new ValuePool();
		this.elemValuePool = new ValuePool();
		this.elemGroupPool = new ElemGrpupPool();
		this.attrGroupPool = new AttrGroupPool();
	}

	/**
	 * Diese Methode schreibt den gegebenen Wert mit der gegebenen Länge in das gegebene {@link EncodeTarget}.
	 * 
	 * @param target {@link EncodeTarget}.
	 * @param value Wert.
	 * @param size Länge des Werts (0..4).
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
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

	/**
	 * Diese Methode schreibt die Startpositionen der gegebenen {@link Item}s in das gegebene {@link EncodeTarget}.
	 * 
	 * @param target {@link EncodeTarget}.
	 * @param items {@link Item}s.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeOffsets(final EncodeTarget target, final List<? extends Item> items) throws IOException {
		final int size = items.size();
		final int[] offsets = new int[size];
		int offset = 0;
		for(int i = 0; i < size; i++){
			offset += items.get(i).size;
			offsets[i] = offset;
		}
		final int length = Encoder.lengthOf(offset);
		this.write(target, size, 4);
		this.write(target, length, 1);
		for(int i = 0; i < size; i++){
			this.write(target, offsets[i], length);
		}
	}

	/**
	 * Diese Methode schreibt {@link ValueItem}s in das gegebene {@link EncodeTarget}.
	 * 
	 * @param target {@link EncodeTarget}.
	 * @param items {@link ValueItem}s.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeTextValues(final EncodeTarget target, final List<ValueItem> items) throws IOException {
		this.writeOffsets(target, items);
		final int size = items.size();
		for(int i = 0; i < size; i++){
			final byte[] data = items.get(i).data;
			target.write(data, 0, data.length);
		}
	}

	/**
	 * Diese Methode schreibt die {@link GroupItem}s der Kindknotenlisten in das gegebene {@link EncodeTarget}.
	 * 
	 * @param target {@link EncodeTarget}.
	 * @param items {@link GroupItem}s der Kindknotenlisten.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeElemGroups(final EncodeTarget target, final List<GroupItem> items) throws IOException {
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
	 * Diese Methode schreibt die {@link GroupItem}s der Attributknotenlisten in die Ausgabe.
	 * 
	 * @param target Ausgabe.
	 * @param items {@link GroupItem}s der Attributknotenlisten.
	 * @throws IOException Wenn beim Schreiben ein Fehler auftritt.
	 */
	void writeAttrGroups(final EncodeTarget target, final List<GroupItem> items) throws IOException {
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
		final Item uriRef = this.elemUriPool.unique(element.getNamespaceURI());
		final Item nameRef = this.elemNamePool.unique(element.getNodeName());
		final Item contentRef;
		final Item attributesRef;
		if(children.length > 0){
			if((children.length == 4) && (children[1] == Item.VOID)){
				contentRef = children[2];
			}else{
				contentRef = this.elemGroupPool.unique(children);
			}
		}else{
			contentRef = Item.VOID;
		}
		if(attributes.length > 1){
			attributesRef = this.attrGroupPool.unique(attributes);
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
			data[j++] = this.attrUriPool.unique(node.getNamespaceURI());
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

	/**
	 * Diese Methode entfernt alle intern verwaltenden Datenstrukturen und wird automatisch von {@link #encode(Document, EncodeTarget)} vor der Kodierung eines
	 * {@link Document}s aufgerufen.
	 */
	public void clear() {
		this.text.setLength(0);
		this.attrUriPool.clear();
		this.attrNamePool.clear();
		this.attrValuePool.clear();
		this.attrGroupPool.clear();
		this.elemUriPool.clear();
		this.elemNamePool.clear();
		this.elemValuePool.clear();
		this.elemGroupPool.clear();
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
		final Item childrenRef = this.elemGroupPool.unique(children);
		final List<ValueItem> attrUriList = this.attrUriPool.items();
		final List<ValueItem> attrNameList = this.attrNamePool.items();
		final List<ValueItem> attrValueList = this.attrValuePool.items();
		final List<GroupItem> attrGroupList = this.attrGroupPool.items();
		final List<ValueItem> elemUriList = this.elemUriPool.items();
		final List<ValueItem> elemNameList = this.elemNamePool.items();
		final List<ValueItem> elemValueList = this.elemValuePool.items();
		final List<GroupItem> elemGroupList = this.elemGroupPool.items();
		this.computeKeys(attrUriList, 1);
		this.computeKeys(attrNameList, 0);
		this.computeKeys(attrValueList, 0);
		this.computeKeys(elemUriList, 1);
		this.computeKeys(elemNameList, 1);
		this.computeKeys(elemValueList, 1);
		this.computeKeys(elemGroupList, 1 + elemValueList.size());
		this.computeKeys(attrGroupList, 1);
		this.attrUriLength = Encoder.lengthOf(attrUriList.size());
		this.attrNameLength = Encoder.lengthOf(attrNameList.size() - 1);
		this.attrValueLength = Encoder.lengthOf(attrValueList.size() - 1);
		this.elemUriLength = Encoder.lengthOf(elemUriList.size());
		this.elemNameLength = Encoder.lengthOf(elemNameList.size());
		this.elemContentLength = Encoder.lengthOf(elemValueList.size() + elemGroupList.size());
		this.elemAttributesLength = Encoder.lengthOf(attrGroupList.size());
		this.writeTextValues(target, attrUriList);
		this.writeTextValues(target, attrNameList);
		this.writeTextValues(target, attrValueList);
		this.writeAttrGroups(target, attrGroupList);
		this.writeTextValues(target, elemUriList);
		this.writeTextValues(target, elemNameList);
		this.writeTextValues(target, elemValueList);
		this.writeElemGroups(target, elemGroupList);
		this.write(target, childrenRef.key, this.elemContentLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCallFormat(true, true, this, "attrUriPool", this.attrUriPool, "attrNamePool", this.attrNamePool, "attrValuePool",
			this.attrValuePool, "attrGroupPool", this.attrGroupPool, "elemUriPool", this.elemUriPool, "elemNamePool", this.elemNamePool, "elemValuePool",
			this.elemValuePool, "elemGroupPool", this.elemGroupPool);
	}

}