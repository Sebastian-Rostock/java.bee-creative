package bee.creative.bex;

import bee.creative.bex.BEX.BEXBaseFile;
import bee.creative.bex.BEX.BEXBaseList;
import bee.creative.bex.BEX.BEXBaseNode;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMDecoder.IAMListDecoder;
import bee.creative.iam.IAMException;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparables.Items;

/**
 * Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@link BEX} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class BEXDecoder {

	// TODO Cache für MMFArray -> String, clear(), setEnable()
	public static final class BEXTextCache implements Items<String> {

		protected final IAMListDecoder items;

		protected String[] cache;

		BEXTextCache(final IAMListDecoder items) {
			this.items = items;
			this.setEnabled(true);
		}

		/**
		 * Diese Methode gibt die {@code index}-te Zeichenkette zurück. Wenn der Index ungültig ist, wird {@code ""} geliefert.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Zeichenkette oder {@code ""}.
		 */
		@Override
		public String get(final int index) {
			final String[] cache = this.cache;
			if (cache != null) {
				if ((index < 0) || (index >= cache.length)) return "";
				String result = cache[index];
				if (result != null) return result;
				cache[index] = result = BEX.toString(this.items.item(index));
				return result;
			} else {
				final String result = BEX.toString(this.items.item(index));
				return result;
			}
		}

		public MMFArray item(final int i) {
			return this.items.item(i);
		}

		public boolean getEnabled() {
			return this.cache != null;
		}

		public void setEnabled(final boolean value) {
			if (!value) {
				this.cache = null;
			} else if (this.cache == null) {
				this.cache = new String[this.items.itemCount()];
			}
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link BEXFile}, das seine Daten aus dem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXFileDecoder extends BEXBaseFile {

		/**
		 * Dieses Feld speichert den leeren {@link BEXFileDecoder}.
		 */
		public static final BEXFileDecoder EMPTY = new BEXFileDecoder();

		{}

		/**
		 * Dieses Feld speichert die Referenz des Wurzelelements.
		 */
		protected final int rootRef;

		/**
		 * Dieses Feld speichert die URI der Attributknoten.
		 */
		protected final BEXTextCache attrUriText;

		/**
		 * Dieses Feld speichert die Namen der Attributknoten.
		 */
		protected final BEXTextCache attrNameText;

		/**
		 * Dieses Feld speichert die Werte der Attributknoten.
		 */
		protected final BEXTextCache attrValueText;

		/**
		 * Dieses Feld speichert die URI der Elementknoten.
		 */
		protected final BEXTextCache chldUriText;

		/**
		 * Dieses Feld speichert die Namen der Elementknoten.
		 */
		protected final BEXTextCache chldNameText;

		/**
		 * Dieses Feld speichert die Werte der Textknoten.
		 */
		protected final BEXTextCache chldValueText;

		/**
		 * Dieses Feld speichert die URI-Spalte der Attributknotentabelle.
		 */
		protected final MMFArray attrUriRef;

		/**
		 * Dieses Feld speichert die Name-Spalte der Attributknotentabelle.
		 */
		protected final MMFArray attrNameRef;

		/**
		 * Dieses Feld speichert die Wert-Spalte der Attributknotentabelle.
		 */
		protected final MMFArray attrValueRef;

		/**
		 * Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle.
		 */
		protected final MMFArray attrParentRef;

		/**
		 * Dieses Feld speichert die URI-Spalte der Kindknotentabelle.
		 */
		protected final MMFArray chldUriRef;

		/**
		 * Dieses Feld speichert die Name-Spalte der Kindknotentabelle.
		 */
		protected final MMFArray chldNameRef;

		/**
		 * Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle.
		 */
		protected final MMFArray chldContentRef;

		/**
		 * Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle.
		 */
		protected final MMFArray chldAttributesRef;

		/**
		 * Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle.
		 */
		protected final MMFArray chldParentRef;

		/**
		 * Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle.
		 */
		protected final MMFArray chldListRange;

		/**
		 * Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle.
		 */
		protected final MMFArray attrListRange;

		/**
		 * Dieser Konstruktor initialisiert den leeren {@link BEXFileDecoder}.
		 */
		BEXFileDecoder() {
			this.rootRef = -1;
			this.attrUriText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.attrNameText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.attrValueText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.chldUriText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.chldNameText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.chldValueText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.attrUriRef = MMFArray.EMPTY;
			this.attrNameRef = MMFArray.EMPTY;
			this.attrValueRef = MMFArray.EMPTY;
			this.attrParentRef = MMFArray.EMPTY;
			this.chldUriRef = MMFArray.EMPTY;
			this.chldNameRef = MMFArray.EMPTY;
			this.chldContentRef = MMFArray.EMPTY;
			this.chldAttributesRef = MMFArray.EMPTY;
			this.chldParentRef = MMFArray.EMPTY;
			this.chldListRange = MMFArray.EMPTY;
			this.attrListRange = MMFArray.EMPTY;
		}

		/**
		 * Dieser Kontrukteur initialisiert dieses {@link BEXFile} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 */
		public BEXFileDecoder(MMFArray array) throws IAMException, NullPointerException {
			array = array.toINT32();
			if (array.length() < 3) throw new IAMException(IAMException.INVALID_LENGTH);

			final int _header = array.get(0);
			if (_header != 0xBE10BA5E) throw new IAMException(IAMException.INVALID_HEADER);

			final int rootRef = array.get(1);
			final IAMIndexDecoder nodeData = new IAMIndexDecoder(array.section(2, array.length() - 2));
			if (false || //
				(nodeData.mapCount() != 0) || //
				(nodeData.listCount() != 17) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMListDecoder attrUriTextList = nodeData.list(0);
			final IAMListDecoder attrNameTextList = nodeData.list(1);
			final IAMListDecoder attrValueTextList = nodeData.list(2);
			final IAMListDecoder chldUriTextList = nodeData.list(3);
			final IAMListDecoder chldNameTextList = nodeData.list(4);
			final IAMListDecoder chldValueTextList = nodeData.list(5);
			final IAMListDecoder attrUriRefList = nodeData.list(6);
			final IAMListDecoder attrNameRefList = nodeData.list(7);
			final IAMListDecoder attrValueRefList = nodeData.list(8);
			final IAMListDecoder attrParentRefList = nodeData.list(9);
			final IAMListDecoder chldUriRefList = nodeData.list(10);
			final IAMListDecoder chldNameRefList = nodeData.list(11);
			final IAMListDecoder chldContentRefList = nodeData.list(12);
			final IAMListDecoder chldAttributesRefList = nodeData.list(13);
			final IAMListDecoder chldParentRefList = nodeData.list(14);
			final IAMListDecoder attrListRangeList = nodeData.list(15);
			final IAMListDecoder chldListRangeList = nodeData.list(16);
			if (false || //
				(attrUriRefList.itemCount() != 1) || //
				(attrNameRefList.itemCount() != 1) || //
				(attrValueRefList.itemCount() != 1) || //
				(attrParentRefList.itemCount() != 1) || //
				(chldUriRefList.itemCount() != 1) || //
				(chldNameRefList.itemCount() != 1) || //
				(chldContentRefList.itemCount() != 1) || //
				(chldAttributesRefList.itemCount() != 1) || //
				(chldParentRefList.itemCount() != 1) || //
				(attrListRangeList.itemCount() != 1) || //
				(chldListRangeList.itemCount() != 1) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			final MMFArray attrUriRef = attrUriRefList.item(0);
			final MMFArray attrNameRef = attrNameRefList.item(0);
			final MMFArray attrValueRef = attrValueRefList.item(0);
			final MMFArray attrParentRef = attrParentRefList.item(0);
			final MMFArray chldUriRef = chldUriRefList.item(0);
			final MMFArray chldNameRef = chldNameRefList.item(0);
			final MMFArray chldContentRef = chldContentRefList.item(0);
			final MMFArray chldAttributesRef = chldAttributesRefList.item(0);
			final MMFArray chldParentRef = chldParentRefList.item(0);
			final MMFArray chldListRange = chldListRangeList.item(0);
			final MMFArray attrListRange = attrListRangeList.item(0);
			final int attrCount = attrNameRef.length();
			final int chldCount = chldNameRef.length();

			if (false || //
				(rootRef < 0) || //
				(chldCount <= rootRef) || //
				((attrUriRef.length() != attrCount) && (attrUriRef.length() != 0)) || //
				(attrValueRef.length() != attrCount) || //
				((attrParentRef.length() != attrCount) && (attrParentRef.length() != 0)) || //
				((chldUriRef.length() != chldCount) && (chldUriRef.length() != 0)) || //
				(chldContentRef.length() != chldCount) || //
				(chldAttributesRef.length() != chldCount) || //
				((chldParentRef.length() != chldCount) && (chldParentRef.length() != 0)) || //
				(chldListRange.length() < 3) || //
				(attrListRange.length() < 2) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			this.rootRef = rootRef;
			this.attrUriText = new BEXTextCache(attrUriTextList);
			this.attrNameText = new BEXTextCache(attrNameTextList);
			this.attrValueText = new BEXTextCache(attrValueTextList);
			this.chldUriText = new BEXTextCache(chldUriTextList);
			this.chldNameText = new BEXTextCache(chldNameTextList);
			this.chldValueText = new BEXTextCache(chldValueTextList);
			this.attrUriRef = attrUriRef;
			this.attrNameRef = attrNameRef;
			this.attrValueRef = attrValueRef;
			this.attrParentRef = attrParentRef;
			this.chldUriRef = chldUriRef;
			this.chldNameRef = chldNameRef;
			this.chldContentRef = chldContentRef;
			this.chldAttributesRef = chldAttributesRef;
			this.chldParentRef = chldParentRef;
			this.chldListRange = chldListRange;
			this.attrListRange = attrListRange;

		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode root() {
			if (this.rootRef < 0) return new BEXNodeDecoder(this);
			return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, this.rootRef), this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXList list(final int key) {
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.refOf(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.refOf(key))).children();
			}
			return new BEXListDecoder(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode node(final int key) {
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ATTR_NODE: {
					final int ref = BEXDecoder.refOf(key);
					if (ref >= this.attrNameRef.length()) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.refOf(key);
					if (this.chldNameRef.get(ref) == 0) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					final int ref = BEXDecoder.refOf(key);
					final IAMArray names = this.chldNameRef;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					final int ref = BEXDecoder.refOf(key);
					if ((this.chldNameRef.get(ref) == 0) || (this.chldContentRef.get(ref) < 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELTX_NODE, ref), this);
				}
			}
			return new BEXNodeDecoder(this);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link BEXList}, die ihre Daten aus dem {@link MMFArray} seines Besitzers dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXListDecoder extends BEXBaseList {

		/**
		 * Dieses Feld speichert den leeren {@link BEXListDecoder}.
		 */
		public static final BEXListDecoder EMPTY = new BEXListDecoder(BEXFileDecoder.EMPTY);

		{}

		/**
		 * Dieses Feld speichert den Schlüssel.
		 */
		protected final int key;

		/**
		 * Dieses Feld speichert die Referenz.
		 * 
		 * @see BEXFileDecoder#attrListRange
		 * @see BEXFileDecoder#chldListRange
		 */
		protected final int ref;

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final BEXFileDecoder owner;

		/**
		 * Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 * 
		 * @param owner Besitzer.
		 */
		BEXListDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder.keyOf(BEXDecoder.BEX_VOID_TYPE, 0), 0, owner);
		}

		/**
		 * Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 * 
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer.
		 */
		BEXListDecoder(final int key, final int ref, final BEXFileDecoder owner) {
			this.key = key;
			this.ref = ref;
			this.owner = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key() {
			return this.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			switch (BEXDecoder.typeOf(this.key)) {
				case BEX_VOID_TYPE:
					return BEXList.VOID_LIST;
				case BEX_ATTR_LIST:
					return BEXList.ATTR_LIST;
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return BEXList.CHLD_LIST;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXFile owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode get(final int index) {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = owner.attrListRange;
					final int ref = this.ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = this.owner.chldListRange;
					final int ref = this.ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					if (owner.chldNameRef.get(result) == 0) return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_TEXT_NODE, result), owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, result), owner);
				}
				case BEX_CHTX_LIST: {
					if (index != 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELTX_NODE, BEXDecoder.refOf(key)), owner);
				}
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final String uri, final String name, final int start) throws NullPointerException {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner.attrListRange, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this.ref;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						if (useUri) {
							final IAMArray attrUri = owner.attrUriText.item(owner.attrUriRef.get(ref));
							if (!attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner.attrNameText.item(owner.attrNameRef.get(ref));
							if (!nameUri.equals(nameArray)) {
								continue;
							}
						}
						return ref - startRef;
					}
					return -1;
				}
				case BEX_CHLD_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner.chldListRange, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this.ref;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						final int nameRef = owner.chldNameRef.get(ref);
						if (nameRef == 0) {
							continue;
						}
						if (useUri) {
							final IAMArray _attrUri = owner.chldUriText.item(owner.chldUriRef.get(ref));
							if (!_attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner.chldNameText.item(owner.chldNameRef.get(ref));
							if (!nameUri.equals(nameArray)) {
								continue;
							}
						}
						return ref - startRef;
					}
					return -1;
				}
				case BEX_VOID_TYPE:
				case BEX_CHTX_LIST:
					return -1;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			final int key = this.key;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return 0;
				case BEX_ATTR_LIST: {
					final IAMArray array = this.owner.attrListRange;
					final int ref = this.ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHLD_LIST: {
					final IAMArray array = this.owner.chldListRange;
					final int ref = this.ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHTX_LIST:
					return 1;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode parent() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST:
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link BEXNode}, der seine Daten aus dem {@link MMFArray} seines Besitzers dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXNodeDecoder extends BEXBaseNode {

		/**
		 * Dieses Feld speichert den leeren {@link BEXNodeDecoder}.
		 */
		public static final BEXNodeDecoder EMPTY = new BEXNodeDecoder(BEXFileDecoder.EMPTY);

		{}

		/**
		 * Dieses Feld speichert den Schlüssel.
		 */
		protected final int key;

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final BEXFileDecoder owner;

		/**
		 * Dieser Konstruktor initialisiert den undefinierten Knoten.
		 * 
		 * @param owner Besitzer.
		 */
		BEXNodeDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder.keyOf(BEXDecoder.BEX_VOID_TYPE, 0), owner);
		}

		/**
		 * Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 * 
		 * @param key Schlüssel.
		 * @param owner Besitzer.
		 */
		BEXNodeDecoder(final int key, final BEXFileDecoder owner) {
			this.key = key;
			this.owner = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key() {
			return this.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			switch (BEXDecoder.typeOf(this.key)) {
				case BEX_VOID_TYPE:
					return BEXNode.VOID_NODE;
				case BEX_ATTR_NODE:
					return BEXNode.ATTR_NODE;
				case BEX_ELEM_NODE:
					return BEXNode.ELEM_NODE;
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return BEXNode.TEXT_NODE;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXFile owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String uri() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.attrUriText.get(owner.attrUriRef.get(BEXDecoder.refOf(key)));
				case BEX_ELEM_NODE:
					return owner.chldUriText.get(owner.chldUriRef.get(BEXDecoder.refOf(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.attrNameText.get(owner.attrNameRef.get(BEXDecoder.refOf(key)));
				case BEX_ELEM_NODE:
					return owner.chldNameText.get(owner.chldNameRef.get(BEXDecoder.refOf(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner.attrValueText.get(owner.attrValueRef.get(BEXDecoder.refOf(key)));
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.refOf(key);
					final int contentRef = owner.chldContentRef.get(ref);
					if (contentRef >= 0) return owner.chldValueText.get(contentRef);
					return new BEXListDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, this.owner).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner.chldValueText.get(owner.chldContentRef.get(BEXDecoder.refOf(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					final MMFArray array = owner.attrParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.refOf(key);
					return ref - owner.attrListRange.get(owner.chldAttributesRef.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.refOf(key);
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode parent() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_NODE: {
					final MMFArray array = owner.attrParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder.refOf(key))), owner);
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					final int ref = BEXDecoder.refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder.refOf(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXList children() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.refOf(key);
					final int contentRef = owner.chldContentRef.get(ref);
					if (contentRef >= 0) return new BEXListDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_CHTX_LIST, ref), 0, owner);
					return new BEXListDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, owner);
				}
				case BEX_VOID_TYPE:
				case BEX_ATTR_NODE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return new BEXListDecoder(owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXList attributes() {
			final int key = this.key;
			final BEXFileDecoder owner = this.owner;
			switch (BEXDecoder.typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.refOf(key);
					return new BEXListDecoder(BEXDecoder.keyOf(BEXDecoder.BEX_ATTR_LIST, ref), owner.chldAttributesRef.get(ref), owner);
				}
				case BEX_VOID_TYPE:
				case BEX_ATTR_NODE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return new BEXListDecoder(owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}
	}

	{}

	/**
	 * Dieses Feld speichert die Typkennung für den undefinierten Knoten bzw. die undefinierte Knotenliste.
	 */
	static final int BEX_VOID_TYPE = 0;

	/**
	 * Dieses Feld speichert die Typkennung für einen Attributknoten.
	 */
	static final int BEX_ATTR_NODE = 1;

	/**
	 * Dieses Feld speichert die Typkennung für einen Elementknoten.
	 */
	static final int BEX_ELEM_NODE = 2;

	/**
	 * Dieses Feld speichert die Typkennung für einen Textknoten.
	 */
	static final int BEX_TEXT_NODE = 3;

	/**
	 * Dieses Feld speichert die Typkennung für den Textknoten eines Elementknoten.
	 */
	static final int BEX_ELTX_NODE = 4;

	/**
	 * Dieses Feld speichert die Typkennung für eine Attributknotenliste.
	 */
	static final int BEX_ATTR_LIST = 5;

	/**
	 * Dieses Feld speichert die Typkennung für eine Kindknotenliste.
	 */
	static final int BEX_CHLD_LIST = 6;

	/**
	 * Dieses Feld speichert die Typkennung für die Kindknotenliste dem Textknoten eines Elementknoten.
	 */
	static final int BEX_CHTX_LIST = 7;

	{}

	/**
	 * Diese Methode gibt die Referenz des gegebenen Schlüssels zurück.
	 * 
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Referenz.
	 */
	final static int refOf(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/**
	 * Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 * 
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel.
	 */
	final static int keyOf(final int type, final int ref) {
		return (ref << 3) | (type << 0);
	}

	/**
	 * Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 * 
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung.
	 */
	final static int typeOf(final int key) {
		return (key >> 0) & 7;
	}

}
