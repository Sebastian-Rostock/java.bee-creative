package bee.creative.bex;

import java.util.Arrays;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMException;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMListing;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die Algorithmen zur Dekodierung der {@code Binary Encoded XML} Datenstrukturen.
 * 
 * @see BEXFileLoader
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class BEXLoader {

	/** Diese Klasse implementiert ein {@link BEXFile}, das seine Daten aus dem {@link IAMIndex} bezieht.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXFileLoader extends BEXFile {

		/** Dieses Feld speichert den leeren {@link BEXFileLoader}. */
		public static final BEXFileLoader EMPTY = new BEXFileLoader();

		{}

		/** Dieses Feld speichert die Referenz des Wurzelelements. */
		final int _rootRef_;

		/** Dieses Feld speichert die URI der Attributknoten. */
		final BEXStringLoader _attrUriText_;

		/** Dieses Feld speichert die Namen der Attributknoten. */
		final BEXStringLoader _attrNameText_;

		/** Dieses Feld speichert die Werte der Attributknoten. */
		final BEXStringLoader _attrValueText_;

		/** Dieses Feld speichert die URI der Elementknoten. */
		final BEXStringLoader _chldUriText_;

		/** Dieses Feld speichert die Namen der Elementknoten. */
		final BEXStringLoader _chldNameText_;

		/** Dieses Feld speichert die Werte der Textknoten. */
		final BEXStringLoader _chldValueText_;

		/** Dieses Feld speichert die URI-Spalte der Attributknotentabelle. */
		final IAMArray _attrUriRef_;

		/** Dieses Feld speichert die Name-Spalte der Attributknotentabelle. */
		final IAMArray _attrNameRef_;

		/** Dieses Feld speichert die Wert-Spalte der Attributknotentabelle. */
		final IAMArray _attrValueRef_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle. */
		final IAMArray _attrParentRef_;

		/** Dieses Feld speichert die URI-Spalte der Kindknotentabelle. */
		final IAMArray _chldUriRef_;

		/** Dieses Feld speichert die Name-Spalte der Kindknotentabelle. */
		final IAMArray _chldNameRef_;

		/** Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle. */
		final IAMArray _chldContentRef_;

		/** Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle. */
		final IAMArray _chldAttributesRef_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle. */
		final IAMArray _chldParentRef_;

		/** Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle. */
		final IAMArray _chldListRange_;

		/** Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle. */
		final IAMArray _attrListRange_;

		/** Dieser Konstruktor initialisiert den leeren {@link BEXFileLoader}. */
		BEXFileLoader() {
			this._rootRef_ = -1;
			this._attrUriText_ = BEXStringLoader.EMPTY;
			this._attrNameText_ = BEXStringLoader.EMPTY;
			this._attrValueText_ = BEXStringLoader.EMPTY;
			this._chldUriText_ = BEXStringLoader.EMPTY;
			this._chldNameText_ = BEXStringLoader.EMPTY;
			this._chldValueText_ = BEXStringLoader.EMPTY;
			this._attrUriRef_ = IAMArray.EMPTY;
			this._attrNameRef_ = IAMArray.EMPTY;
			this._attrValueRef_ = IAMArray.EMPTY;
			this._attrParentRef_ = IAMArray.EMPTY;
			this._chldUriRef_ = IAMArray.EMPTY;
			this._chldNameRef_ = IAMArray.EMPTY;
			this._chldContentRef_ = IAMArray.EMPTY;
			this._chldAttributesRef_ = IAMArray.EMPTY;
			this._chldParentRef_ = IAMArray.EMPTY;
			this._chldListRange_ = IAMArray.EMPTY;
			this._attrListRange_ = IAMArray.EMPTY;
		}

		/** Dieser Kontrukteur initialisiert das {@link BEXFile} als Sicht auf den gegebenen {@link IAMIndex}.
		 * 
		 * @param index {@link IAMIndex}.
		 * @throws IAMException Wenn {@code index} strukturell oder referenzienn ungültig ist.
		 * @throws NullPointerException Wenn {@code index} {@code null} ist. */
		public BEXFileLoader(final IAMIndex index) throws IAMException, NullPointerException {

			if (false || //
				(index.mappingCount() != 0) || //
				(index.listingCount() != 18) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMListing headRootListing = index.listing(0);
			final IAMListing attrUriTextListing = index.listing(1);
			final IAMListing attrNameTextListing = index.listing(2);
			final IAMListing attrValueTextListing = index.listing(3);
			final IAMListing chldUriTextListing = index.listing(4);
			final IAMListing chldNameTextListing = index.listing(5);
			final IAMListing chldValueTextListing = index.listing(6);
			final IAMListing attrUriRefListing = index.listing(7);
			final IAMListing attrNameRefListing = index.listing(8);
			final IAMListing attrValueRefListing = index.listing(9);
			final IAMListing attrParentRefListing = index.listing(11);
			final IAMListing chldUriRefListing = index.listing(11);
			final IAMListing chldNameRefListing = index.listing(12);
			final IAMListing chldContentRefListing = index.listing(13);
			final IAMListing chldAttributesRefListing = index.listing(14);
			final IAMListing chldParentRefListing = index.listing(15);
			final IAMListing attrListRangeListing = index.listing(16);
			final IAMListing chldListRangeListing = index.listing(17);

			if (false || //
				(headRootListing.itemCount() != 1) || //
				(attrUriRefListing.itemCount() != 1) || //
				(attrNameRefListing.itemCount() != 1) || //
				(attrValueRefListing.itemCount() != 1) || //
				(attrParentRefListing.itemCount() != 1) || //
				(chldUriRefListing.itemCount() != 1) || //
				(chldNameRefListing.itemCount() != 1) || //
				(chldContentRefListing.itemCount() != 1) || //
				(chldAttributesRefListing.itemCount() != 1) || //
				(chldParentRefListing.itemCount() != 1) || //
				(attrListRangeListing.itemCount() != 1) || //
				(chldListRangeListing.itemCount() != 1) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			final IAMArray headRoot = headRootListing.item(0);
			final IAMArray attrUriRef = attrUriRefListing.item(0);
			final IAMArray attrNameRef = attrNameRefListing.item(0);
			final IAMArray attrValueRef = attrValueRefListing.item(0);
			final IAMArray attrParentRef = attrParentRefListing.item(0);
			final IAMArray chldUriRef = chldUriRefListing.item(0);
			final IAMArray chldNameRef = chldNameRefListing.item(0);
			final IAMArray chldContentRef = chldContentRefListing.item(0);
			final IAMArray chldAttributesRef = chldAttributesRefListing.item(0);
			final IAMArray chldParentRef = chldParentRefListing.item(0);
			final IAMArray chldListRange = chldListRangeListing.item(0);
			final IAMArray attrListRange = attrListRangeListing.item(0);

			final int headVal = headRoot.get(0);
			final int rootRef = headRoot.get(1);
			final int attrCount = attrNameRef.length();
			final int chldCount = chldNameRef.length();

			if (false || //
				(headVal != 0xBE10BA5E) || //
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
			this._attrUriText_ = new BEXStringLoader(attrUriTextListing);
			this._attrNameText_ = new BEXStringLoader(attrNameTextListing);
			this._attrValueText_ = new BEXStringLoader(attrValueTextListing);
			this._chldUriText_ = new BEXStringLoader(chldUriTextListing);
			this._chldNameText_ = new BEXStringLoader(chldNameTextListing);
			this._chldValueText_ = new BEXStringLoader(chldValueTextListing);
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
		public final BEXStringLoader attrUriCache() {
			return this._attrUriText_;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Attributknoten. */
		public final BEXStringLoader attrNameCache() {
			return this._attrNameText_;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Attributknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Attributknoten. */
		public final BEXStringLoader attrValueCache() {
			return this._attrValueText_;
		}

		/** Diese Methode gibt die Verwaltung der URI der Elementknoten zurück.
		 * 
		 * @return Verwaltung der URI der Elementknoten. */
		public final BEXStringLoader chldUriCache() {
			return this._chldUriText_;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Elementknoten zurück.
		 * 
		 * @return Verwaltung der Namen der Elementknoten. */
		public final BEXStringLoader chldNameCache() {
			return this._chldNameText_;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Textknoten zurück.
		 * 
		 * @return Verwaltung der Werte der Textknoten. */
		public final BEXStringLoader chldValueCache() {
			return this._chldValueText_;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final BEXNode root() {
			if (this._rootRef_ < 0) return new BEXNodeLoader(this);
			return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, this._rootRef_), this);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXList list(final int key) {
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, BEXLoader._refOf_(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, BEXLoader._refOf_(key))).children();
			}
			return new BEXListLoader(this);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode node(final int key) {
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ATTR_NODE: {
					final int ref = BEXLoader._refOf_(key);
					if (ref >= this._attrNameRef_.length()) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader._refOf_(key);
					if (this._chldNameRef_.get(ref) == 0) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					final int ref = BEXLoader._refOf_(key);
					final IAMArray names = this._chldNameRef_;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					final int ref = BEXLoader._refOf_(key);
					if ((this._chldNameRef_.get(ref) == 0) || (this._chldContentRef_.get(ref) < 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELTX_NODE, ref), this);
				}
			}
			return new BEXNodeLoader(this);
		}

	}

	/** Diese Klasse implementiert eine {@link BEXList}, die ihre Daten aus dem {@link IAMIndex} seines Besitzers bezieht.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXListLoader extends BEXList {

		/** Dieses Feld speichert den leeren {@link BEXListLoader}. */
		public static final BEXListLoader EMPTY = new BEXListLoader(BEXFileLoader.EMPTY);

		{}

		/** Dieses Feld speichert den Schlüssel. */
		final int _key_;

		/** Dieses Feld speichert die Referenz.
		 * 
		 * @see BEXFileLoader#_attrListRange_
		 * @see BEXFileLoader#_chldListRange_ */
		final int _ref_;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileLoader _owner_;

		/** Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 * 
		 * @param owner Besitzer. */
		BEXListLoader(final BEXFileLoader owner) {
			this(BEXLoader._keyOf_(BEXLoader.BEX_VOID_TYPE, 0), 0, owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 * 
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer. */
		BEXListLoader(final int key, final int ref, final BEXFileLoader owner) {
			this._key_ = key;
			this._ref_ = ref;
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int key() {
			return this._key_;
		}

		/** {@inheritDoc} */
		@Override
		public final int type() {
			switch (BEXLoader._typeOf_(this._key_)) {
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
		public final BEXFile owner() {
			return this._owner_;
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode get(final int index) {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					final IAMArray array = owner._attrListRange_;
					final int ref = this._ref_;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					final IAMArray array = this._owner_._chldListRange_;
					final int ref = this._ref_;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeLoader(owner);
					if (owner._chldNameRef_.get(result) == 0) return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_TEXT_NODE, result), owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, result), owner);
				}
				case BEX_CHTX_LIST: {
					if (index != 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELTX_NODE, BEXLoader._refOf_(key)), owner);
				}
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final int find(final String uri, final String name, final int start) throws NullPointerException {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner._attrListRange_, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
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
					final IAMArray array = owner._chldListRange_, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
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
		public final int length() {
			final int key = this._key_;
			switch (BEXLoader._typeOf_(key)) {
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
		public final BEXNode parent() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_LIST:
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, BEXLoader._refOf_(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

	}

	/** Diese Klasse implementiert einen {@link BEXNode}, der seine Daten aus dem {@link IAMIndex} seines Besitzers bezieht.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXNodeLoader extends BEXNode {

		/** Dieses Feld speichert den leeren {@link BEXNodeLoader}. */
		public static final BEXNodeLoader EMPTY = new BEXNodeLoader(BEXFileLoader.EMPTY);

		{}

		/** Dieses Feld speichert den Schlüssel. */
		final int _key_;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileLoader _owner_;

		/** Dieser Konstruktor initialisiert den undefinierten Knoten.
		 * 
		 * @param owner Besitzer. */
		BEXNodeLoader(final BEXFileLoader owner) {
			this(BEXLoader._keyOf_(BEXLoader.BEX_VOID_TYPE, 0), owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 * 
		 * @param key Schlüssel.
		 * @param owner Besitzer. */
		BEXNodeLoader(final int key, final BEXFileLoader owner) {
			this._key_ = key;
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int key() {
			return this._key_;
		}

		/** {@inheritDoc} */
		@Override
		public final int type() {
			switch (BEXLoader._typeOf_(this._key_)) {
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
		public final BEXFile owner() {
			return this._owner_;
		}

		/** {@inheritDoc} */
		@Override
		public final String uri() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ATTR_NODE:
					return owner._attrUriText_.get(owner._attrUriRef_.get(BEXLoader._refOf_(key)));
				case BEX_ELEM_NODE:
					return owner._chldUriText_.get(owner._chldUriRef_.get(BEXLoader._refOf_(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final String name() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ATTR_NODE:
					return owner._attrNameText_.get(owner._attrNameRef_.get(BEXLoader._refOf_(key)));
				case BEX_ELEM_NODE:
					return owner._chldNameText_.get(owner._chldNameRef_.get(BEXLoader._refOf_(key)));
				case BEX_VOID_TYPE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return "";
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final String value() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner._attrValueText_.get(owner._attrValueRef_.get(BEXLoader._refOf_(key)));
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader._refOf_(key);
					final int contentRef = owner._chldContentRef_.get(ref);
					if (contentRef >= 0) return owner._chldValueText_.get(contentRef);
					return new BEXListLoader(BEXLoader._keyOf_(BEXLoader.BEX_CHLD_LIST, ref), -contentRef, this._owner_).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner._chldValueText_.get(owner._chldContentRef_.get(BEXLoader._refOf_(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final int index() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					final IAMArray array = owner._attrParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader._refOf_(key);
					return ref - owner._attrListRange_.get(owner._chldAttributesRef_.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					final IAMArray array = owner._chldParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader._refOf_(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner._chldListRange_.get(-owner._chldContentRef_.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					final IAMArray array = owner._chldParentRef_;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader._refOf_(key);
					return ref - owner._chldListRange_.get(-owner._chldContentRef_.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode parent() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_NODE: {
					final IAMArray array = owner._attrParentRef_;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader._refOf_(key))), owner);
				}
				case BEX_ELEM_NODE: {
					final IAMArray array = owner._chldParentRef_;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					final int ref = BEXLoader._refOf_(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					final IAMArray array = owner._chldParentRef_;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader._refOf_(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeLoader(BEXLoader._keyOf_(BEXLoader.BEX_ELEM_NODE, BEXLoader._refOf_(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXList children() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader._refOf_(key);
					final int contentRef = owner._chldContentRef_.get(ref);
					if (contentRef >= 0) return new BEXListLoader(BEXLoader._keyOf_(BEXLoader.BEX_CHTX_LIST, ref), 0, owner);
					return new BEXListLoader(BEXLoader._keyOf_(BEXLoader.BEX_CHLD_LIST, ref), -contentRef, owner);
				}
				case BEX_VOID_TYPE:
				case BEX_ATTR_NODE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return new BEXListLoader(owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXList attributes() {
			final int key = this._key_;
			final BEXFileLoader owner = this._owner_;
			switch (BEXLoader._typeOf_(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader._refOf_(key);
					return new BEXListLoader(BEXLoader._keyOf_(BEXLoader.BEX_ATTR_LIST, ref), owner._chldAttributesRef_.get(ref), owner);
				}
				case BEX_VOID_TYPE:
				case BEX_ATTR_NODE:
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return new BEXListLoader(owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}
	}

	/** Diese Klasse implementiert eine Verwaltung von gepufferten Zeichenketten, die über {@link BEXFile#stringFrom(IAMArray)} aus den Elementen eines
	 * {@link IAMListing} ermittelt werden.
	 * 
	 * @see #getEnabled()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class BEXStringLoader implements Items<String> {

		/** Dieses Feld speichert den leeren {@link BEXStringLoader}. */
		public static final BEXStringLoader EMPTY = new BEXStringLoader(IAMListing.EMPTY);

		{}

		/** Dieses Feld speichert die Elemente, deren Zeichenketten verwaltet werden. */
		final IAMListing _items_;

		/** Dieses Feld puffert die Zeichenketten der Elemente. */
		String[] _cache_;

		/** Dieser Konstruktor initialisiert die Elemente, deren Zeichenketten verwaltet werden.
		 * 
		 * @param items Elemente. */
		BEXStringLoader(final IAMListing items) {
			this._items_ = items;
		}

		{}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @see IAMListing#item(int)
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		public final IAMArray item(final int index) {
			return this._items_.item(index);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link #get(int)} gelieferten Zeichenkette gepuffert werden. Andernfalls werden diese
		 * Zeichenketten bei jedem Aufruf von {@link #get(int)} erneut über {@link BEXFile#stringFrom(IAMArray)} aud dem {@code index}-ten Element abgeleitet.
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
				cache[index] = result = BEXFile.stringFrom(this._items_.item(index));
				return result;
			} else {
				final String result = BEXFile.stringFrom(this._items_.item(index));
				return result;
			}
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.formatIterable(true, Iterables.filteredIterable(Filters.nullFilter(), Arrays.asList(this._cache_)));
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
	static int _refOf_(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 * 
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel. */
	static int _keyOf_(final int type, final int ref) {
		return (ref << 3) | (type << 0);
	}

	/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 * 
	 * @see #_keyOf_(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung. */
	static int _typeOf_(final int key) {
		return (key >> 0) & 7;
	}

}
