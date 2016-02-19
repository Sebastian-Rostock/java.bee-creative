package bee.creative.bex;

import java.util.Arrays;
import bee.creative.bex.BEX.BEXBaseFile;
import bee.creative.bex.BEX.BEXBaseList;
import bee.creative.bex.BEX.BEXBaseNode;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMDecoder.IAMListDecoder;
import bee.creative.iam.IAMException;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die Klassen und Methoden zur Dekodierung der {@link BEX} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXDecoder {

	/** Diese Klasse implementiert eine Verwaltung von Zeichenketten, die über {@link BEX#toString(MMFArray)} aus den Elementen eines {@link IAMListDecoder}
	 * ermittelt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXTextCache implements Items<String> {

		/** Dieses Feld speichert die Elemente, deren Zeichenketten verwaltet werden. */
		final IAMListDecoder __items;

		/** Dieses Feld puffert die Zeichenketten der Elemente. */
		String[] __cache;

		/** Dieser Konstruktor initialisiert die Elemente, deren Zeichenketten verwaltet werden.
		 * 
		 * @param items Elemente. */
		BEXTextCache(final IAMListDecoder items) {
			this.__items = items;
			this.setEnabled(false);
		}

		{}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @see IAMListDecoder#item(int)
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		public MMFArray item(final int index) {
			return this.__items.item(index);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link #get(int)} gelieferten Zeichenkette gepuffert werden. Andernfalls werden diese
		 * Zeichenketten bei jedem Aufruf von {@link #get(int)} über {@link BEX#toString(MMFArray)} aud dem {@code index}-ten Element abgeleitet.
		 * 
		 * @see #get(int)
		 * @return {@code true}, wenn die Pufferung aktiviert ist. */
		public boolean getEnabled() {
			return this.__cache != null;
		}

		/** Diese Methode aktiviert bzw. deaktiviert die Pufferung der von {@link #get(int)} gelieferten Zeichenketten.
		 * 
		 * @see #get(int)
		 * @param value {@code true}, wenn die Pufferung aktiviert ist. */
		public void setEnabled(final boolean value) {
			if (!value) {
				this.__cache = null;
			} else if (this.__cache == null) {
				this.__cache = new String[this.__items.itemCount()];
			}
		}

		{}

		/** Diese Methode gibt die Zeichenkette zum {@code index}-ten Element zurück. Wenn der Index ungültig ist, wird {@code ""} geliefert.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Zeichenkette oder {@code ""}. */
		@Override
		public String get(final int index) {
			final String[] cache = this.__cache;
			if (cache != null) {
				if ((index < 0) || (index >= cache.length)) return "";
				String result = cache[index];
				if (result != null) return result;
				cache[index] = result = BEX.toString(this.__items.item(index));
				return result;
			} else {
				final String result = BEX.toString(this.__items.item(index));
				return result;
			}
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toString(Iterables.filteredIterable(Filters.nullFilter(), Arrays.asList(this.__cache)));
		}

	}

	/** Diese Klasse implementiert ein {@link BEXFile}, das seine Daten aus dem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXFileDecoder extends BEXBaseFile {

		/** Dieses Feld speichert den leeren {@link BEXFileDecoder}. */
		public static final BEXFileDecoder EMPTY = new BEXFileDecoder();

		{}

		/** Dieses Feld speichert die Referenz des Wurzelelements. */
		final int __rootRef;

		/** Dieses Feld speichert die URI der Attributknoten. */
		final BEXTextCache __attrUriText;

		/** Dieses Feld speichert die Namen der Attributknoten. */
		final BEXTextCache __attrNameText;

		/** Dieses Feld speichert die Werte der Attributknoten. */
		final BEXTextCache __attrValueText;

		/** Dieses Feld speichert die URI der Elementknoten. */
		final BEXTextCache __chldUriText;

		/** Dieses Feld speichert die Namen der Elementknoten. */
		final BEXTextCache __chldNameText;

		/** Dieses Feld speichert die Werte der Textknoten. */
		final BEXTextCache __chldValueText;

		/** Dieses Feld speichert die URI-Spalte der Attributknotentabelle. */
		final MMFArray __attrUriRef;

		/** Dieses Feld speichert die Name-Spalte der Attributknotentabelle. */
		final MMFArray __attrNameRef;

		/** Dieses Feld speichert die Wert-Spalte der Attributknotentabelle. */
		final MMFArray __attrValueRef;

		/** Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle. */
		final MMFArray __attrParentRef;

		/** Dieses Feld speichert die URI-Spalte der Kindknotentabelle. */
		final MMFArray __chldUriRef;

		/** Dieses Feld speichert die Name-Spalte der Kindknotentabelle. */
		final MMFArray __chldNameRef;

		/** Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle. */
		final MMFArray __chldContentRef;

		/** Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle. */
		final MMFArray __chldAttributesRef;

		/** Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle. */
		final MMFArray __chldParentRef;

		/** Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle. */
		final MMFArray __chldListRange;

		/** Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle. */
		final MMFArray __attrListRange;

		/** Dieser Konstruktor initialisiert den leeren {@link BEXFileDecoder}. */
		BEXFileDecoder() {
			this.__rootRef = -1;
			this.__attrUriText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__attrNameText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__attrValueText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__chldUriText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__chldNameText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__chldValueText = new BEXTextCache(IAMListDecoder.EMPTY);
			this.__attrUriRef = MMFArray.EMPTY;
			this.__attrNameRef = MMFArray.EMPTY;
			this.__attrValueRef = MMFArray.EMPTY;
			this.__attrParentRef = MMFArray.EMPTY;
			this.__chldUriRef = MMFArray.EMPTY;
			this.__chldNameRef = MMFArray.EMPTY;
			this.__chldContentRef = MMFArray.EMPTY;
			this.__chldAttributesRef = MMFArray.EMPTY;
			this.__chldParentRef = MMFArray.EMPTY;
			this.__chldListRange = MMFArray.EMPTY;
			this.__attrListRange = MMFArray.EMPTY;
		}

		/** Dieser Kontrukteur initialisiert dieses {@link BEXFile} als Sicht auf den gegebenen Speicherbereich.
		 * 
		 * @param array Speicherbereich mit {@code INT32} Zahlen.
		 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
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

			this.__rootRef = rootRef;
			this.__attrUriText = new BEXTextCache(attrUriTextList);
			this.__attrNameText = new BEXTextCache(attrNameTextList);
			this.__attrValueText = new BEXTextCache(attrValueTextList);
			this.__chldUriText = new BEXTextCache(chldUriTextList);
			this.__chldNameText = new BEXTextCache(chldNameTextList);
			this.__chldValueText = new BEXTextCache(chldValueTextList);
			this.__attrUriRef = attrUriRef;
			this.__attrNameRef = attrNameRef;
			this.__attrValueRef = attrValueRef;
			this.__attrParentRef = attrParentRef;
			this.__chldUriRef = chldUriRef;
			this.__chldNameRef = chldNameRef;
			this.__chldContentRef = chldContentRef;
			this.__chldAttributesRef = chldAttributesRef;
			this.__chldParentRef = chldParentRef;
			this.__chldListRange = chldListRange;
			this.__attrListRange = attrListRange;

		}

		{}

		/** Diese Methode gibt die Verwaltung der URI der Attributknoten zurück.
		 * 
		 * @return Verwaltung der URI der Attributknoten. */
		public BEXTextCache attrUriCache() {
			return this.__attrUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Attributknoten. */
		public BEXTextCache attrNameCache() {
			return this.__attrNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Attributknoten. */
		public BEXTextCache attrValueCache() {
			return this.__attrValueText;
		}

		/** Diese Methode gibt die Verwaltung der URI der Elementknoten zurück.
		 * 
		 * @return Verwaltung der URI der Elementknoten. */
		public BEXTextCache chldUriCache() {
			return this.__chldUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Elementknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Elementknoten. */
		public BEXTextCache chldNameCache() {
			return this.__chldNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Textknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Textknoten. */
		public BEXTextCache chldValueCache() {
			return this.__chldValueText;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public BEXNode root() {
			if (this.__rootRef < 0) return new BEXNodeDecoder(this);
			return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, this.__rootRef), this);
		}

		/** {@inheritDoc} */
		@Override
		public BEXList list(final int key) {
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.__refOf(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.__refOf(key))).children();
			}
			return new BEXListDecoder(this);
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode node(final int key) {
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ATTR_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					if (ref >= this.__attrNameRef.length()) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					if (this.__chldNameRef.get(ref) == 0) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					final IAMArray names = this.__chldNameRef;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					if ((this.__chldNameRef.get(ref) == 0) || (this.__chldContentRef.get(ref) < 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELTX_NODE, ref), this);
				}
			}
			return new BEXNodeDecoder(this);
		}

	}

	/** Diese Klasse implementiert eine {@link BEXList}, die ihre Daten aus dem {@link MMFArray} seines Besitzers dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXListDecoder extends BEXBaseList {

		/** Dieses Feld speichert den leeren {@link BEXListDecoder}. */
		public static final BEXListDecoder EMPTY = new BEXListDecoder(BEXFileDecoder.EMPTY);

		{}

		/** Dieses Feld speichert den Schlüssel. */
		final int __key;

		/** Dieses Feld speichert die Referenz.
		 * 
		 * @see BEXFileDecoder#__attrListRange
		 * @see BEXFileDecoder#__chldListRange */
		final int __ref;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileDecoder __owner;

		/** Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 * 
		 * @param owner Besitzer. */
		BEXListDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder.__keyOf(BEXDecoder.BEX_VOID_TYPE, 0), 0, owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 * 
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer. */
		BEXListDecoder(final int key, final int ref, final BEXFileDecoder owner) {
			this.__key = key;
			this.__ref = ref;
			this.__owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int key() {
			return this.__key;
		}

		/** {@inheritDoc} */
		@Override
		public int type() {
			switch (BEXDecoder.__typeOf(this.__key)) {
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

		/** {@inheritDoc} */
		@Override
		public BEXFile owner() {
			return this.__owner;
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode get(final int index) {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = owner.__attrListRange;
					final int ref = this.__ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = this.__owner.__chldListRange;
					final int ref = this.__ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					if (owner.__chldNameRef.get(result) == 0) return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_TEXT_NODE, result), owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, result), owner);
				}
				case BEX_CHTX_LIST: {
					if (index != 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELTX_NODE, BEXDecoder.__refOf(key)), owner);
				}
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public int find(final String uri, final String name, final int start) throws NullPointerException {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner.__attrListRange, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this.__ref;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						if (useUri) {
							final IAMArray attrUri = owner.__attrUriText.item(owner.__attrUriRef.get(ref));
							if (!attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner.__attrNameText.item(owner.__attrNameRef.get(ref));
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
					final IAMArray array = owner.__chldListRange, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this.__ref;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						final int nameRef = owner.__chldNameRef.get(ref);
						if (nameRef == 0) {
							continue;
						}
						if (useUri) {
							final IAMArray _attrUri = owner.__chldUriText.item(owner.__chldUriRef.get(ref));
							if (!_attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner.__chldNameText.item(owner.__chldNameRef.get(ref));
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

		/** {@inheritDoc} */
		@Override
		public int length() {
			final int key = this.__key;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return 0;
				case BEX_ATTR_LIST: {
					final IAMArray array = this.__owner.__attrListRange;
					final int ref = this.__ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHLD_LIST: {
					final IAMArray array = this.__owner.__chldListRange;
					final int ref = this.__ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHTX_LIST:
					return 1;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode parent() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST:
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.__refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

	}

	/** Diese Klasse implementiert einen {@link BEXNode}, der seine Daten aus dem {@link MMFArray} seines Besitzers dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXNodeDecoder extends BEXBaseNode {

		/** Dieses Feld speichert den leeren {@link BEXNodeDecoder}. */
		public static final BEXNodeDecoder EMPTY = new BEXNodeDecoder(BEXFileDecoder.EMPTY);

		{}

		/** Dieses Feld speichert den Schlüssel. */
		final int __key;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileDecoder __owner;

		/** Dieser Konstruktor initialisiert den undefinierten Knoten.
		 * 
		 * @param owner Besitzer. */
		BEXNodeDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder.__keyOf(BEXDecoder.BEX_VOID_TYPE, 0), owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 * 
		 * @param key Schlüssel.
		 * @param owner Besitzer. */
		BEXNodeDecoder(final int key, final BEXFileDecoder owner) {
			this.__key = key;
			this.__owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int key() {
			return this.__key;
		}

		/** {@inheritDoc} */
		@Override
		public int type() {
			switch (BEXDecoder.__typeOf(this.__key)) {
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

		/** {@inheritDoc} */
		@Override
		public BEXFile owner() {
			return this.__owner;
		}

		/** {@inheritDoc} */
		@Override
		public String uri() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.__attrUriText.get(owner.__attrUriRef.get(BEXDecoder.__refOf(key)));
				case BEX_ELEM_NODE:
					return owner.__chldUriText.get(owner.__chldUriRef.get(BEXDecoder.__refOf(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public String name() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.__attrNameText.get(owner.__attrNameRef.get(BEXDecoder.__refOf(key)));
				case BEX_ELEM_NODE:
					return owner.__chldNameText.get(owner.__chldNameRef.get(BEXDecoder.__refOf(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public String value() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner.__attrValueText.get(owner.__attrValueRef.get(BEXDecoder.__refOf(key)));
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					final int contentRef = owner.__chldContentRef.get(ref);
					if (contentRef >= 0) return owner.__chldValueText.get(contentRef);
					return new BEXListDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, this.__owner).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner.__chldValueText.get(owner.__chldContentRef.get(BEXDecoder.__refOf(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public int index() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					final MMFArray array = owner.__attrParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.__refOf(key);
					return ref - owner.__attrListRange.get(owner.__chldAttributesRef.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner.__chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.__refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner.__chldListRange.get(-owner.__chldContentRef.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner.__chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder.__refOf(key);
					return ref - owner.__chldListRange.get(-owner.__chldContentRef.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode parent() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_NODE: {
					final MMFArray array = owner.__attrParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder.__refOf(key))), owner);
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner.__chldParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					final int ref = BEXDecoder.__refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner.__chldParentRef;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder.__refOf(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ELEM_NODE, BEXDecoder.__refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXList children() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					final int contentRef = owner.__chldContentRef.get(ref);
					if (contentRef >= 0) return new BEXListDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_CHTX_LIST, ref), 0, owner);
					return new BEXListDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, owner);
				}
				case BEX_VOID_TYPE:
				case BEX_ATTR_NODE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return new BEXListDecoder(owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXList attributes() {
			final int key = this.__key;
			final BEXFileDecoder owner = this.__owner;
			switch (BEXDecoder.__typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder.__refOf(key);
					return new BEXListDecoder(BEXDecoder.__keyOf(BEXDecoder.BEX_ATTR_LIST, ref), owner.__chldAttributesRef.get(ref), owner);
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

	/** Dieses Feld speichert die Typkennung für den undefinierten Knoten bzw. die undefinierte Knotenliste. */
	static final int BEX_VOID_TYPE = 0;

	/** Dieses Feld speichert die Typkennung für einen Attributknoten. */
	static final int BEX_ATTR_NODE = 1;

	/** Dieses Feld speichert die Typkennung für einen Elementknoten. */
	static final int BEX_ELEM_NODE = 2;

	/** Dieses Feld speichert die Typkennung für einen Textknoten. */
	static final int BEX_TEXT_NODE = 3;

	/** Dieses Feld speichert die Typkennung für den Textknoten eines Elementknoten. */
	static final int BEX_ELTX_NODE = 4;

	/** Dieses Feld speichert die Typkennung für eine Attributknotenliste. */
	static final int BEX_ATTR_LIST = 5;

	/** Dieses Feld speichert die Typkennung für eine Kindknotenliste. */
	static final int BEX_CHLD_LIST = 6;

	/** Dieses Feld speichert die Typkennung für die Kindknotenliste dem Textknoten eines Elementknoten. */
	static final int BEX_CHTX_LIST = 7;

	{}

	/** Diese Methode gibt die Referenz des gegebenen Schlüssels zurück.
	 * 
	 * @see #__keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Referenz. */
	final static int __refOf(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 * 
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel. */
	final static int __keyOf(final int type, final int ref) {
		return (ref << 3) | (type << 0);
	}

	/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 * 
	 * @see #__keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung. */
	final static int __typeOf(final int key) {
		return (key >> 0) & 7;
	}

}
