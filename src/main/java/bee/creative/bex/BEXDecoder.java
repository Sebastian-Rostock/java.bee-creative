package bee.creative.bex;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import bee.creative.bex.BEX.BEXBaseFile;
import bee.creative.bex.BEX.BEXBaseList;
import bee.creative.bex.BEX.BEXBaseNode;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMDecoder.IAMHeader;
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
public final class BEXDecoder {

	/** Diese Klasse implementiert eine Verwaltung von Zeichenketten, die über {@link BEX#toString(MMFArray)} aus den Elementen eines {@link IAMListDecoder}
	 * ermittelt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXTextCache implements Items<String> {

		/** Dieses Feld speichert den leeren {@link BEXTextCache}. */
		public static final BEXTextCache EMPTY = new BEXTextCache(IAMListDecoder.EMPTY);

		{}

		/** Dieses Feld speichert die Elemente, deren Zeichenketten verwaltet werden. */
		final IAMListDecoder _items_;

		/** Dieses Feld puffert die Zeichenketten der Elemente. */
		String[] _cache_;

		/** Dieser Konstruktor initialisiert die Elemente, deren Zeichenketten verwaltet werden.
		 * 
		 * @param items Elemente. */
		BEXTextCache(final IAMListDecoder items) {
			this._items_ = items;
			this.setEnabled(false);
		}

		{}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @see IAMListDecoder#item(int)
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		public final MMFArray item(final int index) {
			return this._items_.item(index);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link #get(int)} gelieferten Zeichenkette gepuffert werden. Andernfalls werden diese
		 * Zeichenketten bei jedem Aufruf von {@link #get(int)} erneut über {@link BEX#toString(MMFArray)} aud dem {@code index}-ten Element abgeleitet.
		 * 
		 * @see #get(int)
		 * @return {@code true}, wenn die Pufferung aktiviert ist. */
		public final boolean getEnabled() {
			return this._cache_ != null;
		}

		/** Diese Methode aktiviert bzw. deaktiviert die Pufferung der von {@link #get(int)} gelieferten Zeichenketten.
		 * 
		 * @see #get(int)
		 * @param value {@code true}, wenn die Pufferung aktiviert ist. */
		public final void setEnabled(final boolean value) {
			if (!value) {
				this._cache_ = null;
			} else if (this._cache_ == null) {
				final int count = this._items_.itemCount();
				if (count == 0) return;
				this._cache_ = new String[count];
			}
		}

		{}

		/** Diese Methode gibt die Zeichenkette zum {@code index}-ten Element zurück. Wenn der Index ungültig ist, wird {@code ""} geliefert.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Zeichenkette oder {@code ""}. */
		@Override
		public final String get(final int index) {
			final String[] cache = this._cache_;
			if (cache != null) {
				if ((index < 0) || (index >= cache.length)) return "";
				String result = cache[index];
				if (result != null) return result;
				cache[index] = result = BEX.toString(this._items_.item(index));
				return result;
			} else {
				final String result = BEX.toString(this._items_.item(index));
				return result;
			}
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.formatIterable(true, Iterables.filteredIterable(Filters.nullFilter(), Arrays.asList(this._cache_)));
		}

	}

	/** Diese Klasse implementiert ein {@link BEXFile}, das seine Daten aus dem {@link MMFArray} dekodiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXFileDecoder extends BEXBaseFile {

		/** Dieses Feld speichert den leeren {@link BEXFileDecoder}. */
		public static final BEXFileDecoder EMPTY = new BEXFileDecoder();

		/** Dieses Feld speichert den {@link IAMHeader} einer {@code BEX_FILE} Datenstruktur. */
		public static final IAMHeader HEADER = new IAMHeader(0xFFFFFFFF, 0xBE10BA5E);

		{}

		/** Dieses Feld speichert die Referenz des Wurzelelements. */
		final int _rootRef_;

		/** Dieses Feld speichert die URI der Attributknoten. */
		final BEXTextCache _attrUriText_;

		/** Dieses Feld speichert die Namen der Attributknoten. */
		final BEXTextCache _attrNameText_;

		/** Dieses Feld speichert die Werte der Attributknoten. */
		final BEXTextCache _attrValueText_;

		/** Dieses Feld speichert die URI der Elementknoten. */
		final BEXTextCache _chldUriText_;

		/** Dieses Feld speichert die Namen der Elementknoten. */
		final BEXTextCache _chldNameText_;

		/** Dieses Feld speichert die Werte der Textknoten. */
		final BEXTextCache _chldValueText_;

		/** Dieses Feld speichert die URI-Spalte der Attributknotentabelle. */
		final MMFArray _attrUriRef_;

		/** Dieses Feld speichert die Name-Spalte der Attributknotentabelle. */
		final MMFArray _attrNameRef_;

		/** Dieses Feld speichert die Wert-Spalte der Attributknotentabelle. */
		final MMFArray _attrValueRef_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle. */
		final MMFArray _attrParentRef_;

		/** Dieses Feld speichert die URI-Spalte der Kindknotentabelle. */
		final MMFArray _chldUriRef_;

		/** Dieses Feld speichert die Name-Spalte der Kindknotentabelle. */
		final MMFArray _chldNameRef_;

		/** Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle. */
		final MMFArray _chldContentRef_;

		/** Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle. */
		final MMFArray _chldAttributesRef_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle. */
		final MMFArray _chldParentRef_;

		/** Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle. */
		final MMFArray _chldListRange_;

		/** Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle. */
		final MMFArray _attrListRange_;

		/** Dieser Konstruktor initialisiert den leeren {@link BEXFileDecoder}. */
		BEXFileDecoder() {
			this._rootRef_ = -1;
			this._attrUriText_ = BEXTextCache.EMPTY;
			this._attrNameText_ = BEXTextCache.EMPTY;
			this._attrValueText_ = BEXTextCache.EMPTY;
			this._chldUriText_ = BEXTextCache.EMPTY;
			this._chldNameText_ = BEXTextCache.EMPTY;
			this._chldValueText_ = BEXTextCache.EMPTY;
			this._attrUriRef_ = MMFArray.EMPTY;
			this._attrNameRef_ = MMFArray.EMPTY;
			this._attrValueRef_ = MMFArray.EMPTY;
			this._attrParentRef_ = MMFArray.EMPTY;
			this._chldUriRef_ = MMFArray.EMPTY;
			this._chldNameRef_ = MMFArray.EMPTY;
			this._chldContentRef_ = MMFArray.EMPTY;
			this._chldAttributesRef_ = MMFArray.EMPTY;
			this._chldParentRef_ = MMFArray.EMPTY;
			this._chldListRange_ = MMFArray.EMPTY;
			this._attrListRange_ = MMFArray.EMPTY;
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

			this._rootRef_ = rootRef;
			this._attrUriText_ = new BEXTextCache(attrUriTextList);
			this._attrNameText_ = new BEXTextCache(attrNameTextList);
			this._attrValueText_ = new BEXTextCache(attrValueTextList);
			this._chldUriText_ = new BEXTextCache(chldUriTextList);
			this._chldNameText_ = new BEXTextCache(chldNameTextList);
			this._chldValueText_ = new BEXTextCache(chldValueTextList);
			this._attrUriRef_ = attrUriRef;
			this._attrNameRef_ = attrNameRef;
			this._attrValueRef_ = attrValueRef;
			this._attrParentRef_ = attrParentRef;
			this._chldUriRef_ = chldUriRef;
			this._chldNameRef_ = chldNameRef;
			this._chldContentRef_ = chldContentRef;
			this._chldAttributesRef_ = chldAttributesRef;
			this._chldParentRef_ = chldParentRef;
			this._chldListRange_ = chldListRange;
			this._attrListRange_ = attrListRange;

		}

		{}

		/** Diese Methode gibt die Verwaltung der URI der Attributknoten zurück.
		 * 
		 * @return Verwaltung der URI der Attributknoten. */
		public BEXTextCache attrUriCache() {
			return this._attrUriText_;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Attributknoten. */
		public BEXTextCache attrNameCache() {
			return this._attrNameText_;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Attributknoten. */
		public BEXTextCache attrValueCache() {
			return this._attrValueText_;
		}

		/** Diese Methode gibt die Verwaltung der URI der Elementknoten zurück.
		 * 
		 * @return Verwaltung der URI der Elementknoten. */
		public BEXTextCache chldUriCache() {
			return this._chldUriText_;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Elementknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Elementknoten. */
		public BEXTextCache chldNameCache() {
			return this._chldNameText_;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Textknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Textknoten. */
		public BEXTextCache chldValueCache() {
			return this._chldValueText_;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public BEXNode root() {
			if (this._rootRef_ < 0) return new BEXNodeDecoder(this);
			return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, this._rootRef_), this);
		}

		/** {@inheritDoc} */
		@Override
		public BEXList list(final int key) {
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, BEXDecoder._refOf_(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, BEXDecoder._refOf_(key))).children();
			}
			return new BEXListDecoder(this);
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode node(final int key) {
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ATTR_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					if (ref >= this._attrNameRef_.length()) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					if (this._chldNameRef_.get(ref) == 0) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					final IAMArray names = this._chldNameRef_;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					if ((this._chldNameRef_.get(ref) == 0) || (this._chldContentRef_.get(ref) < 0)) return new BEXNodeDecoder(this);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELTX_NODE, ref), this);
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
		final int _key_;

		/** Dieses Feld speichert die Referenz.
		 * 
		 * @see BEXFileDecoder#_attrListRange_
		 * @see BEXFileDecoder#_chldListRange_ */
		final int _ref_;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileDecoder _owner_;

		/** Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 * 
		 * @param owner Besitzer. */
		BEXListDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder._keyOf_(BEXDecoder.BEX_VOID_TYPE, 0), 0, owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 * 
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer. */
		BEXListDecoder(final int key, final int ref, final BEXFileDecoder owner) {
			this._key_ = key;
			this._ref_ = ref;
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int key() {
			return this._key_;
		}

		/** {@inheritDoc} */
		@Override
		public int type() {
			switch (BEXDecoder._typeOf_(this._key_)) {
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
			return this._owner_;
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode get(final int index) {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = owner._attrListRange_;
					final int ref = this._ref_;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeDecoder(owner);
					final IAMArray array = this._owner_._chldListRange_;
					final int ref = this._ref_;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeDecoder(owner);
					if (owner._chldNameRef_.get(result) == 0) return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_TEXT_NODE, result), owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, result), owner);
				}
				case BEX_CHTX_LIST: {
					if (index != 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELTX_NODE, BEXDecoder._refOf_(key)), owner);
				}
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public int find(final String uri, final String name, final int start) throws NullPointerException {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner._attrListRange_, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this._ref_;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						if (useUri) {
							final IAMArray attrUri = owner._attrUriText_.item(owner._attrUriRef_.get(ref));
							if (!attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner._attrNameText_.item(owner._attrNameRef_.get(ref));
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
					final IAMArray array = owner._chldListRange_, uriArray = BEX.toArray(uri), nameArray = BEX.toArray(name);
					int ref = this._ref_;
					final int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						final int nameRef = owner._chldNameRef_.get(ref);
						if (nameRef == 0) {
							continue;
						}
						if (useUri) {
							final IAMArray _attrUri = owner._chldUriText_.item(owner._chldUriRef_.get(ref));
							if (!_attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							final IAMArray nameUri = owner._chldNameText_.item(owner._chldNameRef_.get(ref));
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
			final int key = this._key_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return 0;
				case BEX_ATTR_LIST: {
					final IAMArray array = this._owner_._attrListRange_;
					final int ref = this._ref_;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHLD_LIST: {
					final IAMArray array = this._owner_._chldListRange_;
					final int ref = this._ref_;
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
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_LIST:
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, BEXDecoder._refOf_(key)), owner);
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
		final int _key_;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileDecoder _owner_;

		/** Dieser Konstruktor initialisiert den undefinierten Knoten.
		 * 
		 * @param owner Besitzer. */
		BEXNodeDecoder(final BEXFileDecoder owner) {
			this(BEXDecoder._keyOf_(BEXDecoder.BEX_VOID_TYPE, 0), owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 * 
		 * @param key Schlüssel.
		 * @param owner Besitzer. */
		BEXNodeDecoder(final int key, final BEXFileDecoder owner) {
			this._key_ = key;
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int key() {
			return this._key_;
		}

		/** {@inheritDoc} */
		@Override
		public int type() {
			switch (BEXDecoder._typeOf_(this._key_)) {
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
			return this._owner_;
		}

		/** {@inheritDoc} */
		@Override
		public String uri() {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ATTR_NODE:
					return owner._attrUriText_.get(owner._attrUriRef_.get(BEXDecoder._refOf_(key)));
				case BEX_ELEM_NODE:
					return owner._chldUriText_.get(owner._chldUriRef_.get(BEXDecoder._refOf_(key)));
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
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ATTR_NODE:
					return owner._attrNameText_.get(owner._attrNameRef_.get(BEXDecoder._refOf_(key)));
				case BEX_ELEM_NODE:
					return owner._chldNameText_.get(owner._chldNameRef_.get(BEXDecoder._refOf_(key)));
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
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner._attrValueText_.get(owner._attrValueRef_.get(BEXDecoder._refOf_(key)));
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					final int contentRef = owner._chldContentRef_.get(ref);
					if (contentRef >= 0) return owner._chldValueText_.get(contentRef);
					return new BEXListDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, this._owner_).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner._chldValueText_.get(owner._chldContentRef_.get(BEXDecoder._refOf_(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public int index() {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					final MMFArray array = owner._attrParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder._refOf_(key);
					return ref - owner._attrListRange_.get(owner._chldAttributesRef_.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner._chldParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder._refOf_(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner._chldListRange_.get(-owner._chldContentRef_.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner._chldParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXDecoder._refOf_(key);
					return ref - owner._chldListRange_.get(-owner._chldContentRef_.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXNode parent() {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeDecoder(owner);
				case BEX_ATTR_NODE: {
					final MMFArray array = owner._attrParentRef_;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder._refOf_(key))), owner);
				}
				case BEX_ELEM_NODE: {
					final MMFArray array = owner._chldParentRef_;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					final int ref = BEXDecoder._refOf_(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					final MMFArray array = owner._chldParentRef_;
					if (array.length() == 0) return new BEXNodeDecoder(owner);
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, array.get(BEXDecoder._refOf_(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ELEM_NODE, BEXDecoder._refOf_(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public BEXList children() {
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					final int contentRef = owner._chldContentRef_.get(ref);
					if (contentRef >= 0) return new BEXListDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_CHTX_LIST, ref), 0, owner);
					return new BEXListDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_CHLD_LIST, ref), -contentRef, owner);
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
			final int key = this._key_;
			final BEXFileDecoder owner = this._owner_;
			switch (BEXDecoder._typeOf_(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXDecoder._refOf_(key);
					return new BEXListDecoder(BEXDecoder._keyOf_(BEXDecoder.BEX_ATTR_LIST, ref), owner._chldAttributesRef_.get(ref), owner);
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
	 * @see #_keyOf_(int, int)
	 * @param key Schlüssel.
	 * @return Referenz. */
	final static int _refOf_(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 * 
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel. */
	final static int _keyOf_(final int type, final int ref) {
		return (ref << 3) | (type << 0);
	}

	/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 * 
	 * @see #_keyOf_(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung. */
	final static int _typeOf_(final int key) {
		return (key >> 0) & 7;
	}

	{}

	/** Dieses Feld speichert die Eingabedaten. */
	Object _source_;

	/** Dieses Feld speichert das Ausgabedaten. */
	BEXFileDecoder _target_;

	{}

	/** Diese Methode gibt die Eingabedaten zurück.
	 * 
	 * @see #useSource(File)
	 * @see #useSource(String)
	 * @see #useSource(byte[])
	 * @see #useSource(ByteBuffer)
	 * @see #useSource(MMFArray)
	 * @return Eingabedaten. */
	public final Object getSource() {
		return this._source_;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.<br>
	 * Diese werden in {@link #decode()} gesetzt.
	 * 
	 * @return Ausgabedaten. */
	public final BEXFileDecoder getTarget() {
		return this._target_;
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebene Datei und gibt {@code this} zurück.
	 * 
	 * @see MMFArray#MMFArray(File, ByteOrder)
	 * @param source Datei.
	 * @return {@code this}. */
	public final BEXDecoder useSource(final File source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die Datei mit dem gegebenen Namen und gibt {@code this} zurück.
	 * 
	 * @see MMFArray#MMFArray(File, ByteOrder)
	 * @param source Dateiname.
	 * @return {@code this}. */
	public final BEXDecoder useSource(final String source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf dei gegebenen und gibt {@code this} zurück.
	 * 
	 * @see MMFArray#MMFArray(byte[], ByteOrder)
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXDecoder useSource(final byte[] source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebenen und gibt {@code this} zurück.
	 * 
	 * @see MMFArray#MMFArray(ByteBuffer)
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXDecoder useSource(final ByteBuffer source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten auf die gegebenen und gibt {@code this} zurück.
	 * 
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXDecoder useSource(final MMFArray source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode transformiert die {@link #getSource() Eingabedaten} in die {@link #getTarget() Ausgabedaten} und gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
	 * @throws IAMException Wenn die Eingabedaten fehlerhaft kodiert sind.
	 * @throws IllegalStateException Wenn die Eingabedaten un gültig sind. */
	public final BEXDecoder decode() throws IOException, IAMException, IllegalStateException {
		this._target_ = null;
		Object source = this._source_;
		if (source instanceof String) {
			source = new File((String)source);
		}
		if (source instanceof File) {
			final File file = (File)source;
			source = new MMFArray(file, BEXFileDecoder.HEADER.orderOf(file));
		}
		if (source instanceof byte[]) {
			final byte[] bytes = (byte[])source;
			source = new MMFArray(bytes, BEXFileDecoder.HEADER.orderOf(bytes));
		}
		if (source instanceof ByteBuffer) {
			source = new MMFArray((ByteBuffer)source);
		}
		if (source instanceof MMFArray) {
			this._target_ = new BEXFileDecoder((MMFArray)source);
		} else throw new IllegalStateException("source invalid");
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._source_, this._target_);
	}

}
