package bee.creative.bex;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import bee.creative.array.ByteArray;
import bee.creative.bex.BEXDecoder.BEXFileDecoder;
import bee.creative.iam.IAMEncoder;
import bee.creative.iam.IAMEncoder.IAMIndexEncoder;
import bee.creative.iam.IAMEncoder.IAMListEncoder;
import bee.creative.util.Comparators;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueMap;

/** Diese Klasse implementiert die Klassen und Methoden zur Kodierung der {@link BEX} Datenstrukturen.
 * 
 * @see BEXFileEncoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class BEXEncoder {

	/** Diese Klasse implementiert einen Datensatz, dem ein identifizierender {@link #key()} zugeordnet werden kann.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static abstract class BEXItem {

		/** Dieses Feld speichert den Schlüssel zur Referenzierung. */
		public int key;

		{}

		/** Diese Methode gibt den Schlüssel zur Referenzierung zurück.
		 * 
		 * @return Schlüssel. */
		public int key() {
			return this.key;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "@" + this.key();
		}

	}

	/** Diese Klasse implementiert ein einen Datensatz, der zur Verwaltung einzigartiger Zeichenketten eingesetzt wird.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXTextItem extends BEXItem {

		/** Dieses Feld speichert einen {@link Comparator}, der einen Datensatz mit leerem {@link #text} vor einen mit nichtleerem {@link #text} ordnet und
		 * andernfalls zwei Datensätze bezüglich des {@link #key} aufsteigend ordnet. */
		public static final Comparator<BEXTextItem> ORDER = new Comparator<BEXEncoder.BEXTextItem>() {

			@Override
			public int compare(final BEXTextItem item1, final BEXTextItem item2) {
				final int result = Boolean.compare(item2.text.isEmpty(), item1.text.isEmpty());
				if (result != 0) return result;
				return Comparators.compare(item1.key, item2.key);
			}

		};

		{}

		/** Dieses Feld speichert die Zeichenkette. */
		public String text;

		/** Dieser Konstruktor initialisiert die Zeichenkette.
		 * 
		 * @param text Zeichenkette. */
		public BEXTextItem(final String text) {
			this.text = text;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.text);
		}

	}

	/** Diese Klasse implementiert ein einen Datensatz, der zur Verwaltung einzigartiger Kind- und Attributknotenlisten eingesetzt wird.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXGroupItem extends BEXItem {

		/** Dieses Feld speichert einen {@link Comparator}, der einen Datensatz mit leeren {@link #items} vor einen mit nichtleeren {@link #items} ordnet und
		 * andernfalls zwei Datensätze bezüglich des {@link #key} aufsteigend ordnet. */
		public static final Comparator<BEXGroupItem> ORDER = new Comparator<BEXGroupItem>() {

			@Override
			public int compare(final BEXGroupItem item1, final BEXGroupItem item2) {
				final int result = Boolean.compare(item2.items.isEmpty(), item1.items.isEmpty());
				if (result != 0) return result;
				return Comparators.compare(item1.key, item2.key);
			}

		};

		{}

		/** Dieses Feld speichert die Zeilennummer in der Kind- bzw. Attributknotentabelle, ab der die Kind- bzw. Attributknotenliste abgelegt ist. */
		public int offset;

		/** Dieses Feld speichert die Kind- bzw. Attributknotenliste. Ein Knoten besteht hierbei immer aus fünf aufeinander folgenden Datensätzen:<br>
		 * {@code 0=uri/null}, {@code 1=name/null}, {@code 2=value/children}, {@code 3=attributes/null} , {@code 4=parent/null}. */
		public List<BEXItem> items;

		/** Dieser Konstruktor initialisiert {@link #items} mit einer leeren Liste. */
		public BEXGroupItem() {
			this(new ArrayList<BEXItem>());
		}

		/** Dieser Konstruktor initialisiert die Gruppe mit den gegebenen Kind- bzw. Attributknoten.
		 * 
		 * @param items Kind- bzw. Attributknoten. */
		public BEXGroupItem(final List<BEXItem> items) {
			this.items = items;
		}

		{}

		/** Diese Methode fügt den gegebenen Datensatz an {@link #items} an und gibt {@code this} zurück.
		 * 
		 * @param item Datensatz oder {@code null}.
		 * @return {@code this}. */
		public BEXGroupItem put(final BEXItem item) {
			this.items.add(item);
			return this;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.items);
		}

	}

	/** Diese Klasse implementiert einen Datensatz, der das Elternelement eines Kind- bzw- Attributknoten eingesetzt wird.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXParentItem extends BEXItem {

		/** Dieses Feld speichert die Kindknotenliste, in der das Elternelement verwaltet wird. */
		public BEXGroupItem group;

		/** Dieses Feld speichert die logische Position des Elternelements in der ihn verwaltenden Kindknotenliste. */
		public int index;

		/** Dieser Konstruktor initialisiert Kindknotenliste und Position.
		 * 
		 * @see #group
		 * @see #index
		 * @param group Kindknotenliste.
		 * @param index Position. */
		public BEXParentItem(final BEXGroupItem group, final int index) {
			this.group = group;
			this.index = index;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int key() {
			return this.group.offset + this.index;
		}

	}

	/** Diese Klasse implementiert eine abstrakte {@link UniqueMap} zur Verwaltung einzigartiger Datensätze.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Eingabedaten.
	 * @param <GItem> Typ der Datensätze. */
	static abstract class BEXPool<GData, GItem extends BEXItem> extends UniqueMap<GData, GItem> {

		/** Dieses Feld speichert die Liste aller einzigartigen Datensätze. */
		public final List<GItem> items = new ArrayList<>();

		/** Dieser Konstruktor ruft {@link #clear()} auf. */
		public BEXPool() {
			this.clear();
		}

		{}

		/** Diese Methode fügt den gegebenen Datensatz an {@link #items} an und gibt {@code this} zurück.
		 * 
		 * @param item Datensatz.
		 * @return {@code this}. */
		public GItem put(final GItem item) {
			this.items.add(item);
			return item;
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			super.clear();
			this.items.clear();
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected void reuse(final GData input, final GItem output) {
			output.key--;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toString(true, this.items);
		}

	}

	/** Diese Klasse implementiert den {@link BEXPool} zur Verwaltung einzigartiger Zeichenketten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXTextPool extends BEXPool<String, BEXTextItem> {

		/** {@inheritDoc} */
		@Override
		protected BEXTextItem compile(final String input) {
			return this.put(new BEXTextItem(input));
		}

		/** {@inheritDoc} */
		@Override
		public BEXTextItem get(final String input) throws NullPointerException {
			return super.get(input != null ? input : "");
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			super.clear();
			this.get(null);
		}

	}

	/** Diese Klasse implementiert den {@link BEXPool} zur Verwaltung einzigartiger Kind- bzw. Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXGroupPool extends BEXPool<List<BEXItem>, BEXGroupItem> {

		/** Dieses Feld speichert die Anzahl der Zeilen der Tabelle, die durch die Datensätze in {@link #items} beschrieben wird. */
		public int length;

		{}

		/** {@inheritDoc} */
		@Override
		protected BEXGroupItem compile(final List<BEXItem> input) {
			return this.put(new BEXGroupItem(input));
		}

		/** {@inheritDoc} */
		@Override
		public BEXGroupItem get(final List<BEXItem> input) throws NullPointerException {
			return super.get(input != null ? input : Collections.<BEXItem>emptyList());
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			super.clear();
			this.get(null);
		}

	}

	/** Diese Klasse implementiert die allgemeinen Zustandsdaten, die während der Bestückung von Knoten eingesetzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class BEXStack {

		/** Dieses Feld speichert den Datentyp des Dokumentknoten ohne Wurzelelement. */
		public static final int VOID = 0;

		/** Dieses Feld speichert den Datentyp eines Elementknoten. */
		public static final int ELEM = 1;

		/** Dieses Feld speichert den Datentyp eines Attributknoten. */
		public static final int ATTR = 2;

		/** Dieses Feld speichert den Datentyp eines Textknoten. */
		public static final int TEXT = 3;

		/** Dieses Feld speichert den Datentyp des Dokumentknoten mit Wurzelelement. */
		public static final int ROOT = 4;

		{}

		/** Dieses Feld speichert die Zustandsdaten des Elternknoten. */
		public BEXStack parent;

		/** Dieses Feld speichert den Typ der Zustandsdaten. */
		public int type;

		/** Dieses Feld speichert den URI des Knoten. */
		public String uri;

		/** Dieses Feld speichert den Namen des Knoten. */
		public String name;

		/** Dieses Feld speichert den Wert des Knoten. */
		public String value;

		/** Dieses Feld speichert das {@link BEXItem} dieses Knoten. */
		public BEXItem item;

		/** Dieses Feld speichert die Kindknotenliste. */
		public BEXGroupItem children;

		/** Dieses Feld speichert die Attributknotenliste. */
		public BEXGroupItem attributes;

	}

	/** Diese Klasse implementiert ein Objekt zur Zusammenstellung und Kodierung der Daten für einen {@link BEXFileDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXFileEncoder implements IAMEncoder.DataEncoder {

		/** Dieses Feld speichert den Puffer zur Zusammenfassung benachbarter Textknoten. */
		final StringBuilder _text_ = new StringBuilder();

		/** Dieses Feld speichert den aktuellen Knoten. */
		BEXStack _stack_ = new BEXStack();

		/** Dieses Feld speichert die einzigartigen URI der Attributknoten. */
		final BEXTextPool _attrUriText_ = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Namen der Attributknoten. */
		final BEXTextPool _attrNameText_ = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Werte der Attributknoten. */
		final BEXTextPool _attrValueText_ = new BEXTextPool();

		/** Dieses Feld speichert die Abschnitte der Attributknotentabelle. */
		final BEXGroupPool _attrTablePart_ = new BEXGroupPool();

		/** Dieses Feld speichert die Aktivierung der URI von Attributknoten. */
		boolean _attrUriEnabled_ = true;

		/** Dieses Feld speichert die Aktivierung der Elternknoten von Attributknoten. */
		boolean _attrParentEnabled_ = false;

		/** Dieses Feld speichert die einzigartigen URI der Elementknoten. */
		final BEXTextPool _chldUriText_ = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Namen der Elementknoten. */
		final BEXTextPool _chldNameText_ = new BEXTextPool();

		/** Dieses Feld speichert die einzigartigen Werte der Textknoten. */
		final BEXTextPool _chldValueText_ = new BEXTextPool();

		/** Dieses Feld speichert die Abschnitte der Kindknotentabelle. */
		final BEXGroupPool _chldTablePart_ = new BEXGroupPool();

		/** Dieses Feld speichert die Aktivierung der URI von Kindknoten. */
		boolean _chldUriEnabled_ = true;

		/** Dieses Feld speichert die Aktivierung der Elternknoten von Kindknoten. */
		boolean _chldParentEnabled_ = false;

		/** Dieser Konstruktor initialisiert einen leeren {@link BEXFileEncoder}. */
		public BEXFileEncoder() {
			this.clear();
		}

		{}

		/** Diese Methode fügt die in {@link #_text_} gesammelte Zeichenkette als Textknoten an den gegebenen Elternknoten an.
		 * 
		 * @param parent Elternknoten. */
		void _putText_(final BEXStack parent) {
			final StringBuilder text = this._text_;
			if (text.length() == 0) return;
			final BEXItem value = this._chldValueText_.get(text.toString());
			text.setLength(0);
			parent.children.put(null).put(null).put(value).put(null).put(this._chldParentEnabled_ ? parent.item : null);
		}

		/** Diese Methode aktualisiert die Ordnung der zusammengestellten Datensätze, die Anzahl der Zeilen der durch diese Datensätze beschriebenen Tabelle sowie
		 * die Schlüssel und Startpositionen der Datensätze.
		 * 
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @param step Inkrement für den Zähler der Schlüssel (-1=Kindknoten, +1=Attributknoten). */
		void _updatePART_(final BEXGroupPool pool, final int step) {
			final List<BEXGroupItem> groups = pool.items;
			Collections.sort(groups, BEXGroupItem.ORDER);
			int key = 0, offset = 0;
			for (final BEXGroupItem group: groups) {
				group.key = key;
				group.offset = offset;
				key += step;
				offset += group.items.size() / 5;
			}
			pool.length = offset;
		}

		/** Diese Methode kodiert die Längen der gegebenen Tabellenabschnitte in eine Zahlenfolge und gibt diese in einer Liste zurück.
		 * 
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @return Auflistung mit einer Zahlenfolge. */
		IAMListEncoder _encodePART_(final BEXGroupPool pool) {
			final List<BEXGroupItem> groups = pool.items;
			final int length = groups.size();
			final int[] value = new int[length + 1];
			for (int i = 0; i < length; i++) {
				value[i] = groups.get(i).offset;
			}
			value[length] = pool.length;
			final IAMListEncoder encoder = new IAMListEncoder();
			encoder.put(value, false);
			return encoder;
		}

		/** Diese Methode kodiert eine Spalte der gegebenen Tabellenabschnitte in eine Zahlenfolge und gibt diese in einer Liste zurück.
		 * 
		 * @param pool Auflistung von Tabellenabschnitten (Knotenlisten).
		 * @param prop Spaltenindex (0..4).
		 * @return Auflistung mit einer Zahlenfolge. */
		IAMListEncoder _encodePROP_(final BEXGroupPool pool, final int prop) {
			final List<BEXGroupItem> groups = pool.items;
			final int length = pool.length;
			final int[] value = new int[length];
			int index = 0;
			for (final BEXGroupItem group: groups) {
				final List<BEXItem> items = group.items;
				final int count = items.size();
				for (int i = prop; i < count; i += 5) {
					final BEXItem item = items.get(i);
					value[index++] = item != null ? item.key() : 0;
				}
			}
			boolean empty = false;
			for (int i = (prop == 0) || (prop == 4) ? 0 : length; (i < length) && (empty = value[i] == 0); i++) {}
			final IAMListEncoder encoder = new IAMListEncoder();
			encoder.put(empty ? new int[0] : value, false);
			return encoder;
		}

		/** Diese Methode kodiert die gegebenen Zeichenketten in eine Liste von Zahlenfolgen und gibt diese zurück.
		 * 
		 * @see BEX#toItem(String)
		 * @param pool Auflistung von Zeichenketten.
		 * @return Auflistung von Zahlenfolgen. */
		IAMListEncoder _encodeTEXT_(final BEXTextPool pool) {
			final List<BEXTextItem> texts = pool.items;
			Collections.sort(texts, BEXTextItem.ORDER);
			final IAMListEncoder encoder = new IAMListEncoder();
			for (final BEXTextItem text: texts) {
				text.key = encoder.put(BEX.toItem(text.text), false);
			}
			return encoder;
		}

		/** Diese Methode beendet die Bestückung des aktuellen Attributknoten und gibt {@code this} zurück.<br>
		 * Anschließend wird die Bestückung des Elternknoten (Elementknoten) fortgesetzt.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Attributknoten bestückt wird oder dessen Name unbestimmt ist. */
		public BEXFileEncoder putAttr() throws IllegalStateException {
			final BEXStack stack = this._stack_;
			if ((stack.type != BEXStack.ATTR) || (stack.name == null)) throw new IllegalStateException();
			final BEXStack parent = stack.parent;
			final BEXItem uri = this._attrUriText_.get(this._attrUriEnabled_ ? stack.uri : null);
			final BEXItem name = this._attrNameText_.get(stack.name);
			final BEXItem value = this._attrValueText_.get(stack.value);
			parent.attributes.put(uri).put(name).put(value).put(null).put(this._attrParentEnabled_ ? parent.item : null);
			this._stack_ = parent;
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Attributknoten und gibt {@code this} zurück.<br>
		 * Die Bestückung des neuen, aktuellen Attributknoten kann anschließend über {@link #useUri(String)}, {@link #useName(String)} und {@link #useValue(String)}
		 * erfolgen und muss mit {@link #putAttr()} abgeschlossen werden. Die Bestückung des Elternknoten (Elementknoten) kann erst nach dem Aufruf von
		 * {@link #putAttr()} fortgesetzt werden.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Elementknoten bestückt wird. */
		public BEXFileEncoder newAttr() throws IllegalStateException {
			final BEXStack parent = this._stack_;
			if (parent.type != BEXStack.ELEM) throw new IllegalStateException();
			final BEXStack stack = new BEXStack();
			stack.type = BEXStack.ATTR;
			stack.parent = parent;
			this._stack_ = stack;
			return this;
		}

		/** Diese Methode beendet die Bestückung des aktuellen Textknoten und gibt {@code this} zurück.<br>
		 * Anschließend wird die Bestückung des Elternknoten (Elementknoten) fortgesetzt.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Textknoten bestückt wird. */
		public BEXFileEncoder putText() throws IllegalStateException {
			final BEXStack stack = this._stack_;
			if (stack.type != BEXStack.TEXT) throw new IllegalStateException();
			stack.type = BEXStack.ELEM;
			final String value = stack.value;
			if (value == null) return this;
			this._text_.append(value);
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Textknoten und gibt {@code this} zurück.<br>
		 * Die Bestückung des neuen, aktuellen Textknoten kann anschließend über {@link #useValue(String)} erfolgen und muss mit {@link #putText()} abgeschlossen
		 * werden. Die Bestückung des Elternknoten (Elementknoten) kann erst nach dem Aufruf von {@link #putText()} fortgesetzt werden.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Elementknoten bestückt wird. */
		public BEXFileEncoder newText() throws IllegalStateException {
			final BEXStack stack = this._stack_;
			if (stack.type != BEXStack.ELEM) throw new IllegalStateException();
			stack.type = BEXStack.TEXT;
			stack.value = null;
			return this;
		}

		/** Diese Methode beendet die Bestückung des aktuellen Elementknoten und gibt {@code this} zurück.<br>
		 * Anschließend wird die Bestückung des Elternknoten (Elementknoten) fortgesetzt.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kein Textknoten bestückt wird. */
		public BEXFileEncoder putElem() throws IllegalStateException {
			final BEXStack stack = this._stack_;
			if (stack.type != BEXStack.ELEM) throw new IllegalStateException();
			this._putText_(stack);
			final BEXStack parent = stack.parent;
			final List<BEXItem> chldItems = stack.children.items;
			final List<BEXItem> attrItems = stack.attributes.items;
			final BEXItem uri = this._chldUriText_.get(this._chldUriEnabled_ ? stack.uri : null);
			final BEXItem name = this._chldNameText_.get(stack.name);
			final BEXItem content = ((chldItems.size() == 5) && (chldItems.get(1) == null)) ? //
				chldItems.get(2) : //
				(stack.children = this._chldParentEnabled_ && !chldItems.isEmpty() ? this._chldTablePart_.put(stack.children) : this._chldTablePart_.get(chldItems));
			final BEXItem attributes = //
				(stack.attributes = this._attrParentEnabled_ && !attrItems.isEmpty() ? this._attrTablePart_.put(stack.attributes) : this._attrTablePart_.get(attrItems));
			if (parent == null) return this;
			this._stack_ = parent;
			parent.children.put(uri).put(name).put(content).put(attributes).put(this._chldParentEnabled_ ? parent.item : null);
			if (parent.type != BEXStack.VOID) return this;
			parent.type = BEXStack.ELEM;
			this.putElem();
			parent.type = BEXStack.ROOT;
			return this;
		}

		/** Diese Methode beginnt die Bestückung eines neuen Elementknoten und gibt {@code this} zurück.<br>
		 * Die Bestückung des neuen, aktuellen Elementknoten kann anschließend über {@link #useUri(String)}, {@link #useName(String)}, {@link #newAttr()},
		 * {@link #newText()} und {@link #newElem()} erfolgen und muss mit {@link #putElem()} abgeschlossen werden. Die Bestückung des Elternknoten (Elementknoten)
		 * kann erst nach dem Aufruf von {@link #putElem()} fortgesetzt werden.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird. */
		public BEXFileEncoder newElem() throws IllegalStateException {
			final BEXStack parent = this._stack_;
			if ((parent.type != BEXStack.VOID) && (parent.type != BEXStack.ELEM)) throw new IllegalStateException();
			this._putText_(parent);
			final BEXStack stack = new BEXStack();
			stack.type = BEXStack.ELEM;
			stack.item = new BEXParentItem(parent.children, parent.children.items.size() / 5);
			stack.parent = parent;
			stack.children = new BEXGroupItem();
			stack.attributes = new BEXGroupItem();
			this._stack_ = stack;
			return this;
		}

		/** Diese Methode ließt die gegebene {@code XML} Datei ein, fügt das darin beschriebenen Wurzelelement an und gibt {@code this} zurück.
		 * 
		 * @see #putNode(InputSource, XMLReader)
		 * @param file Datei.
		 * @return {@code this}.
		 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
		 * @throws SAXException Wenn die Datei nicht geparst werden kann.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird. */
		public BEXFileEncoder putNode(final File file) throws IOException, SAXException, NullPointerException, IllegalStateException {
			try (FileInputStream stream = new FileInputStream(file)) {
				return this.putNode(new InputSource(stream), XMLReaderFactory.createXMLReader());
			}
		}

		/** Diese Methode fügt abhängig vom Typ des gegebenen Knoten einen Text-, Element- oder Attributknoten an und gibt {@code this} zurück.
		 * <ul>
		 * <li>Wenn der Knotentyp {@link Node#ATTRIBUTE_NODE} ist, wird analog zu {@link #newAttr()}, {@link #useUri(String)}, {@link #useName(String)},
		 * {@link #useValue(String)} und {@link #putAttr()} ein neuer Attributknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#TEXT_NODE} oder {@link Node#CDATA_SECTION_NODE} ist, wird analog zu {@link #newText()}, {@link #useValue(String)} und
		 * {@link #putText()} ein neuer Textknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#ELEMENT_NODE} ist, wird analog zu {@link #newElem()}, {@link #useUri(String)}, {@link #useName(String)} und
		 * {@link #putNode(Node)} ein neuer Elementknoten mit allen seinen Kind- und Attributknoten erzeugt.</li>
		 * <li>Wenn der Knotentyp {@link Node#DOCUMENT_NODE} ist, wird analog zu {@link #putNode(Node)} das Wurzelelement erzeugt.</li>
		 * <li>Wenn der Knotentyp keiner der oben ganennten ist, wird der Knoten ignoriert.</li>
		 * </ul>
		 * 
		 * @param node Knoten.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht der passende Knoten bestückt wird. */
		public BEXFileEncoder putNode(final Node node) throws NullPointerException, IllegalStateException {
			switch (node.getNodeType()) {
				case Node.DOCUMENT_NODE: {
					return this.putNode(((Document)node).getDocumentElement());
				}
				case Node.ATTRIBUTE_NODE: {
					final String name = node.getLocalName();
					return this.newAttr().useUri(node.getNamespaceURI()).useName(name != null ? name : node.getNodeName()).useValue(node.getNodeValue()).putAttr();
				}
				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE: {
					if (this._stack_.type != BEXStack.ELEM) throw new IllegalStateException();
					final String value = node.getNodeValue();
					if (value == null) return this;
					this._text_.append(value);
					return this;
				}
				case Node.ELEMENT_NODE: {
					final String name = node.getLocalName();
					this.newElem().useUri(node.getNamespaceURI()).useName(name != null ? name : node.getNodeName());
					final NodeList children = node.getChildNodes();
					for (int i = 0, length = children.getLength(); i < length; i++) {
						this.putNode(children.item(i));
					}
					final NamedNodeMap attributes = node.getAttributes();
					for (int i = 0, length = attributes.getLength(); i < length; i++) {
						this.putNode(attributes.item(i));
					}
					return this.putElem();
				}
			}
			return this;
		}

		/** Diese Methode ließt die gegebene Datenquelle mit dem gegebenen Parser ein, fügt das darin beschriebenen Wurzelelement an und gibt {@code this} zurück.
		 * 
		 * @param source Datenquelle.
		 * @param reader Parser.
		 * @return {@code this}.
		 * @throws IOException Wenn die Datenquelle nicht geöffnet werden kann.
		 * @throws SAXException Wenn die Datenquelle nicht geparst werden kann.
		 * @throws NullPointerException Wenn {@code source} bzw. {@code reader} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht das Wurzelement oder kein Elementknoten bestückt wird. */
		public BEXFileEncoder putNode(final InputSource source, final XMLReader reader) throws IOException, SAXException, NullPointerException,
			IllegalStateException {
			reader.setContentHandler(new DefaultHandler() {

				@Override
				public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
					BEXFileEncoder.this.newElem().useUri(uri).useName(localName);
					for (int i = 0, length = attributes.getLength(); i < length; i++) {
						BEXFileEncoder.this.newAttr().useUri(attributes.getURI(i)).useName(attributes.getLocalName(i)).useValue(attributes.getValue(i)).putAttr();
					}
				}

				@Override
				public void endElement(final String uri, final String localName, final String qName) throws SAXException {
					BEXFileEncoder.this.putElem();
				}

				@Override
				public void characters(final char[] ch, final int start, final int length) throws SAXException {
					if (BEXFileEncoder.this._stack_.type != BEXStack.ELEM) throw new IllegalStateException();
					BEXFileEncoder.this._text_.append(ch, start, length);
				}

				@Override
				public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
					this.characters(ch, start, length);
				}

			});
			reader.parse(source);
			return this;
		}

		/** Diese Methode setzt den URI des aktuellen Element- bzw. Attributknoten gibt {@code this} zurück.
		 * 
		 * @see #newAttr()
		 * @see #newElem()
		 * @see BEXNode#uri()
		 * @param uri URI oder {@code null}.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Element- noch ein Attributknoten ist. */
		public BEXFileEncoder useUri(final String uri) throws IllegalStateException {
			final BEXStack node = this._stack_;
			if ((node.type != BEXStack.ATTR) && (node.type != BEXStack.ELEM)) throw new IllegalStateException();
			node.uri = uri;
			return this;
		}

		/** Diese Methode setzt den Namen des aktuellen Element- bzw. Attributknoten gibt {@code this} zurück.
		 * 
		 * @see #newAttr()
		 * @see #newElem()
		 * @see BEXNode#name()
		 * @param name Name.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Element- noch ein Attributknoten ist.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalArgumentException Wenn {@code name} leer ist. */
		public BEXFileEncoder useName(final String name) throws IllegalStateException, NullPointerException, IllegalArgumentException {
			final BEXStack stack = this._stack_;
			if ((stack.type != BEXStack.ATTR) && (stack.type != BEXStack.ELEM)) throw new IllegalStateException();
			if (name == null) throw new IllegalArgumentException("name = null");
			if (name.isEmpty()) throw new IllegalArgumentException();
			stack.name = name;
			return this;
		}

		/** Diese Methode setzt den Namen des aktuellen Text- bzw. Attributknoten gibt {@code this} zurück.
		 * 
		 * @see #newAttr()
		 * @see #newText()
		 * @see BEXNode#value()
		 * @param value Wert oder {@code null}.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn der aktuelle Knoten weder ein Text- noch ein Attributknoten ist. */
		public BEXFileEncoder useValue(final String value) throws IllegalStateException {
			final BEXStack node = this._stack_;
			if ((node.type != BEXStack.ATTR) && (node.type != BEXStack.TEXT)) throw new IllegalStateException();
			node.value = value;
			return this;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die URI von Attributknoten erfasst werden. Andernfalls werden diese ignoriert.
		 * 
		 * @see #useUri(String)
		 * @see #useAttrUriEnabled(boolean)
		 * @return {@code true}, wenn die URI von Attributknoten erfasst werden. */
		public boolean isAttrUriEnabled() {
			return this._attrUriEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Elternknoten von Attributknoten erfasst werden. Andernfalls werden diese ignoriert.
		 * 
		 * @see #useAttrParentEnabled(boolean)
		 * @return {@code true}, wenn die Elternknoten von Attributknoten erfasst werden. */
		public boolean isAttrParentEnabled() {
			return this._attrParentEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die URI von Elementknoten erfasst werden. Andernfalls werden diese ignoriert.
		 * 
		 * @see #useUri(String)
		 * @see #useChldUriEnabled(boolean)
		 * @return {@code true}, wenn die URI von Elementknoten erfasst werden. */
		public boolean isChldUriEnabled() {
			return this._chldUriEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Elternknoten von Kindknoten erfasst werden. Andernfalls werden diese ignoriert.
		 * 
		 * @see #useChldParentEnabled(boolean)
		 * @return {@code true}, wenn die Elternknoten von Kindknoten erfasst werden. */
		public boolean isChldParentEnabled() {
			return this._chldParentEnabled_;
		}

		/** Diese Methode setzt die Aktivierung der URI an Attributknoten und gibt {@code this} zurück.
		 * 
		 * @see #useUri(String)
		 * @see #newAttr()
		 * @see #newElem()
		 * @see #newText()
		 * @see #isAttrUriEnabled()
		 * @param value {@code true}, wenn die URI von Attributknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileEncoder useAttrUriEnabled(final boolean value) throws IllegalStateException {
			if (this._stack_.type != BEXStack.VOID) throw new IllegalStateException();
			this._attrUriEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der Elternknoten an Attributknoten und gibt {@code this} zurück.<br>
		 * Wenn die Elternknoten an Attributknoten erfasst werden, werden auch die der Kindknoten erfasst.
		 * 
		 * @see #isAttrParentEnabled()
		 * @param value {@code true}, wenn die Elternknoten von Attributknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileEncoder useAttrParentEnabled(final boolean value) throws IllegalStateException {
			if (this._stack_.type != BEXStack.VOID) throw new IllegalStateException();
			this._attrParentEnabled_ = value;
			if (!value) return this;
			this._chldParentEnabled_ = true;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der URI an Elementknoten und gibt {@code this} zurück.
		 * 
		 * @see #useUri(String)
		 * @see #newAttr()
		 * @see #newElem()
		 * @see #newText()
		 * @see #isChldUriEnabled()
		 * @param value {@code true}, wenn die URI von Elementknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileEncoder useChldUriEnabled(final boolean value) throws IllegalStateException {
			if (this._stack_.type != BEXStack.VOID) throw new IllegalStateException();
			this._chldUriEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Aktivierung der Elternknoten an Kindknoten und gibt {@code this} zurück.<br>
		 * Wenn die Elternknoten an Kindknoten ignoriert werden, werden auch die der Attributknoten ignoriert.
		 * 
		 * @see #isAttrParentEnabled()
		 * @param value {@code true}, wenn die Elternknoten von Kindknoten erfasst werden.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell Text-, Element- oder Attributknoten bestückt werden. */
		public BEXFileEncoder useChldParentEnabled(final boolean value) throws IllegalStateException {
			if (this._stack_.type != BEXStack.VOID) throw new IllegalStateException();
			this._chldParentEnabled_ = value;
			if (value) return this;
			this._attrParentEnabled_ = false;
			return this;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this._text_.setLength(0);
			this._stack_ = new BEXStack();
			this._stack_.type = BEXStack.VOID;
			this._stack_.children = new BEXGroupItem();
			this._stack_.attributes = new BEXGroupItem();
			this._stack_.item = new BEXParentItem(this._stack_.children, 0);
			this._attrUriText_.clear();
			this._attrNameText_.clear();
			this._attrValueText_.clear();
			this._attrTablePart_.clear();
			this._chldUriText_.clear();
			this._chldNameText_.clear();
			this._chldValueText_.clear();
			this._chldTablePart_.clear();
		}

		{}

		/** {@inheritDoc}
		 * 
		 * @see #putNode(File)
		 * @see #putNode(Node)
		 * @see #putNode(InputSource, XMLReader) */
		@Override
		public byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			if (order == null) throw new NullPointerException("order = null");
			final BEXStack stack = this._stack_;
			if (stack.type != BEXStack.ROOT) throw new IllegalStateException();
			this._updatePART_(this._attrTablePart_, +1);
			this._updatePART_(this._chldTablePart_, -1);
			final byte[] dataA = new byte[8];
			ByteBuffer.wrap(dataA).order(order).putInt(0xBE10BA5E).putInt(stack.children.offset);
			final IAMIndexEncoder encoder = new IAMIndexEncoder();
			encoder.putList(this._encodeTEXT_(this._attrUriText_));
			encoder.putList(this._encodeTEXT_(this._attrNameText_));
			encoder.putList(this._encodeTEXT_(this._attrValueText_));
			encoder.putList(this._encodeTEXT_(this._chldUriText_));
			encoder.putList(this._encodeTEXT_(this._chldNameText_));
			encoder.putList(this._encodeTEXT_(this._chldValueText_));
			encoder.putList(this._encodePROP_(this._attrTablePart_, 0));
			encoder.putList(this._encodePROP_(this._attrTablePart_, 1));
			encoder.putList(this._encodePROP_(this._attrTablePart_, 2));
			encoder.putList(this._encodePROP_(this._attrTablePart_, 4));
			encoder.putList(this._encodePROP_(this._chldTablePart_, 0));
			encoder.putList(this._encodePROP_(this._chldTablePart_, 1));
			encoder.putList(this._encodePROP_(this._chldTablePart_, 2));
			encoder.putList(this._encodePROP_(this._chldTablePart_, 3));
			encoder.putList(this._encodePROP_(this._chldTablePart_, 4));
			encoder.putList(this._encodePART_(this._attrTablePart_));
			encoder.putList(this._encodePART_(this._chldTablePart_));
			final byte[] dataB = encoder.encode(order);
			return IAMEncoder.compact(new byte[][]{dataA, dataB});
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this._attrTablePart_.items.size(), this._chldTablePart_.items.size());
		}

	}

	{}

	/** Dieses Feld speichert die Bytereihenfolge. */
	ByteOrder _order_ = ByteOrder.nativeOrder();

	/** Dieses Feld speichert die Eingabedaten. */
	Object _source_;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object _target_;

	{}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @see #useOrderNative()
	 * @see #useOrderBigendian()
	 * @see #useOrderLittleendian()
	 * @return Bytereihenfolge. */
	public final ByteOrder getOrder() {
		return this._order_;
	}

	/** Diese Methode gibt die Eingabedaten zurück.
	 * 
	 * @see #useSource(File)
	 * @see #useSource(String)
	 * @see #useSource(Reader)
	 * @see #useSource(Document)
	 * @see #useSource(InputStream)
	 * @return Eingabedaten. */
	public final Object getSource() {
		return this._source_;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.
	 * 
	 * @see #useTarget(File)
	 * @see #useTarget(String)
	 * @see #useTarget(ByteArray)
	 * @see #useTarget(DataOutput)
	 * @see #useTarget(OutputStream)
	 * @return Ausgabedaten. */
	public final Object getTarget() {
		return this._target_;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.
	 * 
	 * @see BEXFileEncoder#encode(ByteOrder)
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final BEXEncoder useOrder(final ByteOrder order) {
		this._order_ = order;
		return this;
	}

	/** Diese Methode setzt die Bytereihenfolge auf {@link ByteOrder#nativeOrder() nativ} und gibt {@code this} zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @return {@code this}. */
	public final BEXEncoder useOrderNative() {
		return this.useOrder(ByteOrder.nativeOrder());
	}

	/** Diese Methode setzt die Bytereihenfolge auf {@link ByteOrder#BIG_ENDIAN} und gibt {@code this} zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @return {@code this}. */
	public final BEXEncoder useOrderBigendian() {
		return this.useOrder(ByteOrder.BIG_ENDIAN);
	}

	/** Diese Methode setzt die Bytereihenfolge auf {@link ByteOrder#LITTLE_ENDIAN nativ} und gibt {@code this} zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @return {@code this}. */
	public final BEXEncoder useOrderLittleendian() {
		return this.useOrder(ByteOrder.LITTLE_ENDIAN);
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebene Daten und gibt {@code this} zurück.
	 * 
	 * @param source Datei.
	 * @return {@code this}. */
	public final BEXEncoder useSource(final File source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die Daten mit dem gegebenen Namen und gibt {@code this} zurück.
	 * 
	 * @param source Dateiname.
	 * @return {@code this}. */
	public final BEXEncoder useSource(final String source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebenen und gibt {@code this} zurück.
	 * 
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXEncoder useSource(final Reader source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf das gegebenen Dokument gibt {@code this} zurück.
	 * 
	 * @param source Dokument.
	 * @return {@code this}. */
	public final BEXEncoder useSource(final Document source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebenen und gibt {@code this} zurück.
	 * 
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXEncoder useSource(final InputStream source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten auf die gegebene Daten und gibt {@code this} zurück.
	 * 
	 * @see FileOutputStream#FileOutputStream(File)
	 * @param target Datei.
	 * @return {@code this}. */
	public final BEXEncoder useTarget(final File target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten auf die Daten mit dem gegebenen Namen und gibt {@code this} zurück.
	 * 
	 * @see FileOutputStream#FileOutputStream(String)
	 * @param target Dateiname.
	 * @return {@code this}. */
	public final BEXEncoder useTarget(final String target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten die gegebenen und gibt {@code this} zurück.
	 * 
	 * @see ByteArray#add(byte[])
	 * @param target Ausgabedaten.
	 * @return {@code this}. */
	public final BEXEncoder useTarget(final ByteArray target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten die gegebenen und gibt {@code this} zurück.
	 * 
	 * @see DataOutput#write(byte[])
	 * @param target Ausgabedaten.
	 * @return {@code this}. */
	public final BEXEncoder useTarget(final DataOutput target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten die gegebenen und gibt {@code this} zurück.
	 * 
	 * @see OutputStream#write(byte[])
	 * @param target Ausgabedaten.
	 * @return {@code this}. */
	public final BEXEncoder useTarget(final OutputStream target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode transformiert die {@link #getSource() Eingabedaten} gemäß der {@link #getOrder() Bytereihenfolge} in die {@link #getTarget() Ausgabedaten}
	 * und gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 * @throws IOException Wenn die Ausgabedaten nicht geschrieben werden können.
	 * @throws SAXException Wenn die eingabedaten nicht gelesen werden können.
	 * @throws IllegalStateException Wenn die Bytereihenfolge, Eingabedaten oder Ausgabedaten ungültig sind. */
	public final BEXEncoder encode() throws IOException, SAXException, IllegalStateException {
		final Object target = this._target_;
		if (target instanceof File) {
			try (OutputStream result = new FileOutputStream((File)target)) {
				result.write(this._encodeSource_());
			}
		} else if (target instanceof String) {
			try (OutputStream result = new FileOutputStream((String)target)) {
				result.write(this._encodeSource_());
			}
		} else if (target instanceof ByteArray) {
			((ByteArray)target).add(this._encodeSource_());
		} else if (target instanceof DataOutput) {
			((DataOutput)target).write(this._encodeSource_());
		} else if (target instanceof OutputStream) {
			((OutputStream)target).write(this._encodeSource_());
		} else throw new IllegalStateException("target invalid");
		return this;
	}

	@SuppressWarnings ("javadoc")
	final byte[] _encodeSource_() throws IllegalStateException, IOException, SAXException {
		final ByteOrder order = this._order_;
		if (order == null) throw new IllegalStateException("order invalid");
		final Object source = this._source_;
		final BEXFileEncoder encoder = new BEXFileEncoder();
		if (source instanceof File) {
			encoder.putNode((File)source);
		} else if (source instanceof String) {
			encoder.putNode(new File((String)source));
		} else if (source instanceof Reader) {
			encoder.putNode(new InputSource((Reader)source), XMLReaderFactory.createXMLReader());
		} else if (source instanceof Document) {
			encoder.putNode((Document)source);
		} else if (source instanceof InputStream) {
			encoder.putNode(new InputSource((InputStream)source), XMLReaderFactory.createXMLReader());
		} else throw new IllegalStateException("source invalid");
		final byte[] result = encoder.encode(order);
		return result;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._order_, this._source_, this._target_);
	}

}
