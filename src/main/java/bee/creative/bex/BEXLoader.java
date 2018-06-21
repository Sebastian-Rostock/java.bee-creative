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
		final int rootRef;

		/** Dieses Feld speichert das die Zahlenfolgen verwaltende Inhaltsverzeichnis. */
		final IAMIndex nodeData;

		/** Dieses Feld speichert die URI der Attributknoten. */
		final BEXStringLoader attrUriText;

		/** Dieses Feld speichert die Namen der Attributknoten. */
		final BEXStringLoader attrNameText;

		/** Dieses Feld speichert die Werte der Attributknoten. */
		final BEXStringLoader attrValueText;

		/** Dieses Feld speichert die URI der Elementknoten. */
		final BEXStringLoader chldUriText;

		/** Dieses Feld speichert die Namen der Elementknoten. */
		final BEXStringLoader chldNameText;

		/** Dieses Feld speichert die Werte der Textknoten. */
		final BEXStringLoader chldValueText;

		/** Dieses Feld speichert die URI-Spalte der Attributknotentabelle. */
		final IAMArray attrUriRef;

		/** Dieses Feld speichert die Name-Spalte der Attributknotentabelle. */
		final IAMArray attrNameRef;

		/** Dieses Feld speichert die Wert-Spalte der Attributknotentabelle. */
		final IAMArray attrValueRef;

		/** Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle. */
		final IAMArray attrParentRef;

		/** Dieses Feld speichert die URI-Spalte der Kindknotentabelle. */
		final IAMArray chldUriRef;

		/** Dieses Feld speichert die Name-Spalte der Kindknotentabelle. */
		final IAMArray chldNameRef;

		/** Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle. */
		final IAMArray chldContentRef;

		/** Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle. */
		final IAMArray chldAttributesRef;

		/** Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle. */
		final IAMArray chldParentRef;

		/** Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle. */
		final IAMArray chldListRange;

		/** Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle. */
		final IAMArray attrListRange;

		/** Dieser Konstruktor initialisiert den leeren {@link BEXFileLoader}. */
		BEXFileLoader() {
			this.rootRef = -1;
			this.nodeData = IAMIndex.EMPTY;
			this.attrUriText = BEXStringLoader.EMPTY;
			this.attrNameText = BEXStringLoader.EMPTY;
			this.attrValueText = BEXStringLoader.EMPTY;
			this.chldUriText = BEXStringLoader.EMPTY;
			this.chldNameText = BEXStringLoader.EMPTY;
			this.chldValueText = BEXStringLoader.EMPTY;
			this.attrUriRef = IAMArray.EMPTY;
			this.attrNameRef = IAMArray.EMPTY;
			this.attrValueRef = IAMArray.EMPTY;
			this.attrParentRef = IAMArray.EMPTY;
			this.chldUriRef = IAMArray.EMPTY;
			this.chldNameRef = IAMArray.EMPTY;
			this.chldContentRef = IAMArray.EMPTY;
			this.chldAttributesRef = IAMArray.EMPTY;
			this.chldParentRef = IAMArray.EMPTY;
			this.chldListRange = IAMArray.EMPTY;
			this.attrListRange = IAMArray.EMPTY;
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

			this.rootRef = rootRef;
			this.nodeData = index;
			this.attrUriText = new BEXStringLoader(attrUriTextListing);
			this.attrNameText = new BEXStringLoader(attrNameTextListing);
			this.attrValueText = new BEXStringLoader(attrValueTextListing);
			this.chldUriText = new BEXStringLoader(chldUriTextListing);
			this.chldNameText = new BEXStringLoader(chldNameTextListing);
			this.chldValueText = new BEXStringLoader(chldValueTextListing);
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

		/** Diese Methode gibt die Verwaltung der URI der Attributknoten zurück.
		 *
		 * @return Verwaltung der URI der Attributknoten. */
		public final BEXStringLoader attrUriCache() {
			return this.attrUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Attributknoten zurück.
		 *
		 * @return Verwaltung der Namen der Attributknoten. */
		public final BEXStringLoader attrNameCache() {
			return this.attrNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Attributknoten zurück.
		 *
		 * @return Verwaltung der Werte der Attributknoten. */
		public final BEXStringLoader attrValueCache() {
			return this.attrValueText;
		}

		/** Diese Methode gibt die Verwaltung der URI der Elementknoten zurück.
		 *
		 * @return Verwaltung der URI der Elementknoten. */
		public final BEXStringLoader chldUriCache() {
			return this.chldUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Elementknoten zurück.
		 *
		 * @return Verwaltung der Namen der Elementknoten. */
		public final BEXStringLoader chldNameCache() {
			return this.chldNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Textknoten zurück.
		 *
		 * @return Verwaltung der Werte der Textknoten. */
		public final BEXStringLoader chldValueCache() {
			return this.chldValueText;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final BEXNode root() {
			if (this.rootRef < 0) return new BEXNodeLoader(this);
			return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, this.rootRef), this);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXList list(final int key) {
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key))).children();
			}
			return new BEXListLoader(this);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode node(final int key) {
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_NODE: {
					final int ref = BEXLoader.refOf(key);
					if (ref >= this.attrNameRef.length()) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader.refOf(key);
					if (this.chldNameRef.get(ref) == 0) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					final int ref = BEXLoader.refOf(key);
					final IAMArray names = this.chldNameRef;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					final int ref = BEXLoader.refOf(key);
					if ((this.chldNameRef.get(ref) == 0) || (this.chldContentRef.get(ref) < 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELTX_NODE, ref), this);
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
		final int key;

		/** Dieses Feld speichert die Referenz.
		 *
		 * @see BEXFileLoader#attrListRange
		 * @see BEXFileLoader#chldListRange */
		final int ref;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileLoader owner;

		/** Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 *
		 * @param owner Besitzer. */
		BEXListLoader(final BEXFileLoader owner) {
			this(BEXLoader.keyOf(BEXLoader.BEX_VOID_TYPE, 0), 0, owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 *
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer. */
		BEXListLoader(final int key, final int ref, final BEXFileLoader owner) {
			this.key = key;
			this.ref = ref;
			this.owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int key() {
			return this.key;
		}

		/** {@inheritDoc} */
		@Override
		public final int type() {
			switch (BEXLoader.typeOf(this.key)) {
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
			return this.owner;
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode get(final int index) {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					final IAMArray array = owner.attrListRange;
					final int ref = this.ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					final IAMArray array = this.owner.chldListRange;
					final int ref = this.ref;
					final int result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeLoader(owner);
					if (owner.chldNameRef.get(result) == 0) return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_TEXT_NODE, result), owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, result), owner);
				}
				case BEX_CHTX_LIST: {
					if (index != 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELTX_NODE, BEXLoader.refOf(key)), owner);
				}
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final int find(final String uri, final String name, final int start) throws NullPointerException {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					final boolean useUri = uri.length() != 0, useName = name.length() != 0;
					final IAMArray array = owner.attrListRange, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
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
					final IAMArray array = owner.chldListRange, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
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

		/** {@inheritDoc} */
		@Override
		public final int length() {
			final int key = this.key;
			switch (BEXLoader.typeOf(key)) {
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

		/** {@inheritDoc} */
		@Override
		public final BEXNode parent() {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_LIST:
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key)), owner);
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
		final int key;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileLoader owner;

		/** Dieser Konstruktor initialisiert den undefinierten Knoten.
		 *
		 * @param owner Besitzer. */
		BEXNodeLoader(final BEXFileLoader owner) {
			this(BEXLoader.keyOf(BEXLoader.BEX_VOID_TYPE, 0), owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 *
		 * @param key Schlüssel.
		 * @param owner Besitzer. */
		BEXNodeLoader(final int key, final BEXFileLoader owner) {
			this.key = key;
			this.owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int key() {
			return this.key;
		}

		/** {@inheritDoc} */
		@Override
		public final int type() {
			switch (BEXLoader.typeOf(this.key)) {
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
			return this.owner;
		}

		/** {@inheritDoc} */
		@Override
		public final String uri() {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.attrUriText.get(owner.attrUriRef.get(BEXLoader.refOf(key)));
				case BEX_ELEM_NODE:
					return owner.chldUriText.get(owner.chldUriRef.get(BEXLoader.refOf(key)));
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
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_NODE:
					return owner.attrNameText.get(owner.attrNameRef.get(BEXLoader.refOf(key)));
				case BEX_ELEM_NODE:
					return owner.chldNameText.get(owner.chldNameRef.get(BEXLoader.refOf(key)));
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
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner.attrValueText.get(owner.attrValueRef.get(BEXLoader.refOf(key)));
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader.refOf(key);
					final int contentRef = owner.chldContentRef.get(ref);
					if (contentRef >= 0) return owner.chldValueText.get(contentRef);
					return new BEXListLoader(BEXLoader.keyOf(BEXLoader.BEX_CHLD_LIST, ref), -contentRef, this.owner).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner.chldValueText.get(owner.chldContentRef.get(BEXLoader.refOf(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final int index() {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					final IAMArray array = owner.attrParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader.refOf(key);
					return ref - owner.attrListRange.get(owner.chldAttributesRef.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					final IAMArray array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader.refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					final IAMArray array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					final int ref = BEXLoader.refOf(key);
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXNode parent() {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_NODE: {
					final IAMArray array = owner.attrParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader.refOf(key))), owner);
				}
				case BEX_ELEM_NODE: {
					final IAMArray array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					final int ref = BEXLoader.refOf(key);
					final int parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					final IAMArray array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader.refOf(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		/** {@inheritDoc} */
		@Override
		public final BEXList children() {
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader.refOf(key);
					final int contentRef = owner.chldContentRef.get(ref);
					if (contentRef >= 0) return new BEXListLoader(BEXLoader.keyOf(BEXLoader.BEX_CHTX_LIST, ref), 0, owner);
					return new BEXListLoader(BEXLoader.keyOf(BEXLoader.BEX_CHLD_LIST, ref), -contentRef, owner);
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
			final int key = this.key;
			final BEXFileLoader owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ELEM_NODE: {
					final int ref = BEXLoader.refOf(key);
					return new BEXListLoader(BEXLoader.keyOf(BEXLoader.BEX_ATTR_LIST, ref), owner.chldAttributesRef.get(ref), owner);
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
		final IAMListing items;

		/** Dieses Feld puffert die Zeichenketten der Elemente. */
		String[] cache;

		/** Dieser Konstruktor initialisiert die Elemente, deren Zeichenketten verwaltet werden.
		 *
		 * @param items Elemente. */
		BEXStringLoader(final IAMListing items) {
			this.items = items;
		}

		{}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 *
		 * @see IAMListing#item(int)
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		public final IAMArray item(final int index) {
			return this.items.item(index);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link #get(int)} gelieferten Zeichenkette gepuffert werden. Andernfalls werden diese
		 * Zeichenketten bei jedem Aufruf von {@link #get(int)} erneut über {@link BEXFile#stringFrom(IAMArray)} aud dem {@code index}-ten Element abgeleitet.
		 *
		 * @see #get(int)
		 * @return {@code true}, wenn die Pufferung aktiviert ist. */
		public final boolean getEnabled() {
			return this.cache != null;
		}

		/** Diese Methode aktiviert bzw. deaktiviert die Pufferung der von {@link #get(int)} gelieferten Zeichenketten.
		 *
		 * @see #get(int)
		 * @param value {@code true}, wenn die Pufferung aktiviert ist. */
		public final void setEnabled(final boolean value) {
			if (!value) {
				this.cache = null;
			} else if (this.cache == null) {
				final int count = this.items.itemCount();
				if (count == 0) return;
				this.cache = new String[count];
			}
		}

		{}

		/** Diese Methode gibt die Zeichenkette zum {@code index}-ten Element zurück. Wenn der Index ungültig ist, wird {@code ""} geliefert.
		 *
		 * @param index Index.
		 * @return {@code index}-te Zeichenkette oder {@code ""}. */
		@Override
		public final String get(final int index) {
			final String[] cache = this.cache;
			if (cache != null) {
				if ((index < 0) || (index >= cache.length)) return "";
				String result = cache[index];
				if (result != null) return result;
				cache[index] = result = BEXFile.stringFrom(this.items.item(index));
				return result;
			} else {
				final String result = BEXFile.stringFrom(this.items.item(index));
				return result;
			}
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.formatIterable(true, Iterables.filteredIterable(Filters.nullFilter(), Arrays.asList(this.cache)));
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
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Referenz. */
	static int refOf(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 *
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel. */
	static int keyOf(final int type, final int ref) {
		return (ref << 3) | (type << 0);
	}

	/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 *
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung. */
	static int typeOf(final int key) {
		return (key >> 0) & 7;
	}

}
