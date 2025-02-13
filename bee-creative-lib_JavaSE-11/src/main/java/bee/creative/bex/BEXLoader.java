package bee.creative.bex;

import java.util.Arrays;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMException;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMListing;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert die Algorithmen zur Dekodierung der {@code Binary Encoded XML} Datenstrukturen.
 *
 * @see BEXFileLoader
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXLoader {

	/** Diese Klasse implementiert ein {@link BEXFile}, das seine Daten aus dem {@link IAMIndex} bezieht.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXFileLoader extends BEXFile {

		/** Dieses Feld speichert den leeren {@link BEXFileLoader}. */
		public static final BEXFileLoader EMPTY = new BEXFileLoader();

		/** Dieser Kontrukteur initialisiert das {@link BEXFile} als Sicht auf den gegebenen {@link IAMIndex}.
		 *
		 * @param fileData {@link IAMIndex}.
		 * @throws IAMException Wenn {@code index} strukturell oder referenzienn ungültig ist.
		 * @throws NullPointerException Wenn {@code index} {@code null} ist. */
		public BEXFileLoader(IAMIndex fileData) throws IAMException, NullPointerException {

			if (false || //
				(fileData.mappingCount() != 0) || //
				(fileData.listingCount() != 18) //
			) throw new IAMException(IAMException.INVALID_VALUE);

			var headRootListing = fileData.listing(0);
			var attrUriTextListing = fileData.listing(1);
			var attrNameTextListing = fileData.listing(2);
			var attrValueTextListing = fileData.listing(3);
			var chldUriTextListing = fileData.listing(4);
			var chldNameTextListing = fileData.listing(5);
			var chldValueTextListing = fileData.listing(6);
			var attrUriRefListing = fileData.listing(7);
			var attrNameRefListing = fileData.listing(8);
			var attrValueRefListing = fileData.listing(9);
			var attrParentRefListing = fileData.listing(10);
			var chldUriRefListing = fileData.listing(11);
			var chldNameRefListing = fileData.listing(12);
			var chldContentRefListing = fileData.listing(13);
			var chldAttributesRefListing = fileData.listing(14);
			var chldParentRefListing = fileData.listing(15);
			var attrListRangeListing = fileData.listing(16);
			var chldListRangeListing = fileData.listing(17);

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

			var headRoot = headRootListing.item(0);
			var attrUriRef = attrUriRefListing.item(0);
			var attrNameRef = attrNameRefListing.item(0);
			var attrValueRef = attrValueRefListing.item(0);
			var attrParentRef = attrParentRefListing.item(0);
			var chldUriRef = chldUriRefListing.item(0);
			var chldNameRef = chldNameRefListing.item(0);
			var chldContentRef = chldContentRefListing.item(0);
			var chldAttributesRef = chldAttributesRefListing.item(0);
			var chldParentRef = chldParentRefListing.item(0);
			var chldListRange = chldListRangeListing.item(0);
			var attrListRange = attrListRangeListing.item(0);

			var headVal = headRoot.get(0);
			var rootRef = headRoot.get(1);
			var attrCount = attrNameRef.length();
			var chldCount = chldNameRef.length();

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
			this.fileData = fileData;
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

		/** Diese Methode gibt die Verwaltung der URI der Attributknoten zurück.
		 *
		 * @return Verwaltung der URI der Attributknoten. */
		public BEXStringLoader attrUriCache() {
			return this.attrUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Attributknoten zurück.
		 *
		 * @return Verwaltung der Namen der Attributknoten. */
		public BEXStringLoader attrNameCache() {
			return this.attrNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Attributknoten zurück.
		 *
		 * @return Verwaltung der Werte der Attributknoten. */
		public BEXStringLoader attrValueCache() {
			return this.attrValueText;
		}

		/** Diese Methode gibt die Verwaltung der URI der Elementknoten zurück.
		 *
		 * @return Verwaltung der URI der Elementknoten. */
		public BEXStringLoader chldUriCache() {
			return this.chldUriText;
		}

		/** Diese Methode gibt die Verwaltung der Namen der Elementknoten zurück.
		 *
		 * @return Verwaltung der Namen der Elementknoten. */
		public BEXStringLoader chldNameCache() {
			return this.chldNameText;
		}

		/** Diese Methode gibt die Verwaltung der Werte der Textknoten zurück.
		 *
		 * @return Verwaltung der Werte der Textknoten. */
		public BEXStringLoader chldValueCache() {
			return this.chldValueText;
		}

		@Override
		public BEXNode root() {
			if (this.rootRef < 0) return new BEXNodeLoader(this);
			return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, this.rootRef), this);
		}

		@Override
		public BEXList list(int key) {
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_LIST:
					return this.node(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key))).attributes();
				case BEX_CHLD_LIST:
				case BEX_CHTX_LIST:
					return this.node(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key))).children();
			}
			return new BEXListLoader(this);
		}

		@Override
		public BEXNode node(int key) {
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_NODE: {
					var ref = BEXLoader.refOf(key);
					if (ref >= this.attrNameRef.length()) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ATTR_NODE, ref), this);
				}
				case BEX_ELEM_NODE: {
					var ref = BEXLoader.refOf(key);
					if (this.chldNameRef.get(ref) == 0) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, ref), this);
				}
				case BEX_TEXT_NODE: {
					var ref = BEXLoader.refOf(key);
					var names = this.chldNameRef;
					if ((ref >= names.length()) || (names.get(ref) != 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_TEXT_NODE, ref), this);
				}
				case BEX_ELTX_NODE: {
					var ref = BEXLoader.refOf(key);
					if ((this.chldNameRef.get(ref) == 0) || (this.chldContentRef.get(ref) < 0)) return new BEXNodeLoader(this);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELTX_NODE, ref), this);
				}
			}
			return new BEXNodeLoader(this);
		}

		/** Dieses Feld speichert die Referenz des Wurzelelements. */
		final int rootRef;

		/** Dieses Feld speichert das die Zahlenfolgen verwaltende Inhaltsverzeichnis. */
		final IAMIndex fileData;

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
			this.fileData = IAMIndex.EMPTY;
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

	}

	/** Diese Klasse implementiert eine {@link BEXList}, die ihre Daten aus dem {@link IAMIndex} seines Besitzers bezieht.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXListLoader extends BEXList {

		/** Dieses Feld speichert den leeren {@link BEXListLoader}. */
		public static final BEXListLoader EMPTY = new BEXListLoader(BEXFileLoader.EMPTY);

		@Override
		public int key() {
			return this.key;
		}

		@Override
		public int type() {
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

		@Override
		public BEXFile owner() {
			return this.owner;
		}

		@Override
		public BEXNode get(int index) {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					var array = owner.attrListRange;
					var ref = this.ref;
					var result = array.get(ref) + index;
					if (result >= array.get(ref + 1)) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ATTR_NODE, result), owner);
				}
				case BEX_CHLD_LIST: {
					if (index < 0) return new BEXNodeLoader(owner);
					var array = this.owner.chldListRange;
					var ref = this.ref;
					var result = array.get(ref) + index;
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

		@Override
		public int find(String uri, String name, int start) throws NullPointerException {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ATTR_LIST: {
					if (start < 0) return -1;
					boolean useUri = uri.length() != 0, useName = name.length() != 0;
					IAMArray array = owner.attrListRange, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
					var ref = this.ref;
					int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						if (useUri) {
							var attrUri = owner.attrUriText.item(owner.attrUriRef.get(ref));
							if (!attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							var nameUri = owner.attrNameText.item(owner.attrNameRef.get(ref));
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
					boolean useUri = uri.length() != 0, useName = name.length() != 0;
					IAMArray array = owner.chldListRange, uriArray = BEXFile.arrayFrom(uri), nameArray = BEXFile.arrayFrom(name);
					var ref = this.ref;
					int startRef = array.get(ref), finalRef = array.get(ref + 1);
					for (ref = startRef + start; ref < finalRef; ref++) {
						var nameRef = owner.chldNameRef.get(ref);
						if (nameRef == 0) {
							continue;
						}
						if (useUri) {
							var _attrUri = owner.chldUriText.item(owner.chldUriRef.get(ref));
							if (!_attrUri.equals(uriArray)) {
								continue;
							}
						}
						if (useName) {
							var nameUri = owner.chldNameText.item(owner.chldNameRef.get(ref));
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

		@Override
		public int size() {
			var key = this.key;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return 0;
				case BEX_ATTR_LIST: {
					var array = this.owner.attrListRange;
					var ref = this.ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHLD_LIST: {
					var array = this.owner.chldListRange;
					var ref = this.ref;
					return array.get(ref + 1) - array.get(ref);
				}
				case BEX_CHTX_LIST:
					return 1;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		@Override
		public BEXNode parent() {
			var key = this.key;
			var owner = this.owner;
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
		BEXListLoader(BEXFileLoader owner) {
			this(BEXLoader.keyOf(BEXLoader.BEX_VOID_TYPE, 0), 0, owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel, Index und Besitzer.
		 *
		 * @param key Schlüssel mit dem Index des Elternknoten.
		 * @param ref Referenz auf die Knotenliste.
		 * @param owner Besitzer. */
		BEXListLoader(int key, int ref, BEXFileLoader owner) {
			this.key = key;
			this.ref = ref;
			this.owner = owner;
		}

	}

	/** Diese Klasse implementiert einen {@link BEXNode}, der seine Daten aus dem {@link IAMIndex} seines Besitzers bezieht.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXNodeLoader extends BEXNode {

		/** Dieses Feld speichert den leeren {@link BEXNodeLoader}. */
		public static final BEXNodeLoader EMPTY = new BEXNodeLoader(BEXFileLoader.EMPTY);

		@Override
		public int key() {
			return this.key;
		}

		@Override
		public int type() {
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

		@Override
		public BEXFile owner() {
			return this.owner;
		}

		@Override
		public String uri() {
			var key = this.key;
			var owner = this.owner;
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

		@Override
		public String name() {
			var key = this.key;
			var owner = this.owner;
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

		@Override
		public String value() {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return "";
				case BEX_ATTR_NODE:
					return owner.attrValueText.get(owner.attrValueRef.get(BEXLoader.refOf(key)));
				case BEX_ELEM_NODE: {
					var ref = BEXLoader.refOf(key);
					var contentRef = owner.chldContentRef.get(ref);
					if (contentRef >= 0) return owner.chldValueText.get(contentRef);
					return new BEXListLoader(BEXLoader.keyOf(BEXLoader.BEX_CHLD_LIST, ref), -contentRef, this.owner).get(0).value();
				}
				case BEX_TEXT_NODE:
				case BEX_ELTX_NODE:
					return owner.chldValueText.get(owner.chldContentRef.get(BEXLoader.refOf(key)));
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		@Override
		public int index() {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return -1;
				case BEX_ATTR_NODE: {
					var array = owner.attrParentRef;
					if (array.length() == 0) return -1;
					var ref = BEXLoader.refOf(key);
					return ref - owner.attrListRange.get(owner.chldAttributesRef.get(array.get(ref)));
				}
				case BEX_ELEM_NODE: {
					var array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					var ref = BEXLoader.refOf(key);
					var parentRef = array.get(ref);
					if (ref == parentRef) return -1;
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(parentRef));
				}
				case BEX_TEXT_NODE: {
					var array = owner.chldParentRef;
					if (array.length() == 0) return -1;
					var ref = BEXLoader.refOf(key);
					return ref - owner.chldListRange.get(-owner.chldContentRef.get(array.get(ref)));
				}
				case BEX_ELTX_NODE:
					return 0;
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		@Override
		public BEXNode parent() {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_VOID_TYPE:
					return new BEXNodeLoader(owner);
				case BEX_ATTR_NODE: {
					var array = owner.attrParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader.refOf(key))), owner);
				}
				case BEX_ELEM_NODE: {
					var array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					var ref = BEXLoader.refOf(key);
					var parentRef = array.get(ref);
					if (ref == parentRef) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, parentRef), owner);
				}
				case BEX_TEXT_NODE: {
					var array = owner.chldParentRef;
					if (array.length() == 0) return new BEXNodeLoader(owner);
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, array.get(BEXLoader.refOf(key))), owner);
				}
				case BEX_ELTX_NODE:
					return new BEXNodeLoader(BEXLoader.keyOf(BEXLoader.BEX_ELEM_NODE, BEXLoader.refOf(key)), owner);
			}
			throw new IAMException(IAMException.INVALID_HEADER);
		}

		@Override
		public BEXList children() {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ELEM_NODE: {
					var ref = BEXLoader.refOf(key);
					var contentRef = owner.chldContentRef.get(ref);
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

		@Override
		public BEXList attributes() {
			var key = this.key;
			var owner = this.owner;
			switch (BEXLoader.typeOf(key)) {
				case BEX_ELEM_NODE: {
					var ref = BEXLoader.refOf(key);
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

		/** Dieses Feld speichert den Schlüssel. */
		final int key;

		/** Dieses Feld speichert den Besitzer. */
		final BEXFileLoader owner;

		/** Dieser Konstruktor initialisiert den undefinierten Knoten.
		 *
		 * @param owner Besitzer. */
		BEXNodeLoader(BEXFileLoader owner) {
			this(BEXLoader.keyOf(BEXLoader.BEX_VOID_TYPE, 0), owner);
		}

		/** Dieser Konstruktor initialisiert Schlüssel und Besitzer.
		 *
		 * @param key Schlüssel.
		 * @param owner Besitzer. */
		BEXNodeLoader(int key, BEXFileLoader owner) {
			this.key = key;
			this.owner = owner;
		}

	}

	/** Diese Klasse implementiert eine Verwaltung von gepufferten Zeichenketten, die über {@link BEXFile#stringFrom(IAMArray)} aus den Elementen eines
	 * {@link IAMListing} ermittelt werden.
	 *
	 * @see #getEnabled()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXStringLoader implements Array<String> {

		/** Dieses Feld speichert den leeren {@link BEXStringLoader}. */
		public static final BEXStringLoader EMPTY = new BEXStringLoader(IAMListing.EMPTY);

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 *
		 * @see IAMListing#item(int)
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		public IAMArray item(int index) {
			return this.items.item(index);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link #get(int)} gelieferten Zeichenkette gepuffert werden. Andernfalls werden diese
		 * Zeichenketten bei jedem Aufruf von {@link #get(int)} erneut über {@link BEXFile#stringFrom(IAMArray)} aud dem {@code index}-ten Element abgeleitet.
		 *
		 * @see #get(int)
		 * @return {@code true}, wenn die Pufferung aktiviert ist. */
		public boolean getEnabled() {
			return this.cache != null;
		}

		/** Diese Methode aktiviert bzw. deaktiviert die Pufferung der von {@link #get(int)} gelieferten Zeichenketten.
		 *
		 * @see #get(int)
		 * @param value {@code true}, wenn die Pufferung aktiviert ist. */
		public void setEnabled(boolean value) {
			if (!value) {
				this.cache = null;
			} else if (this.cache == null) {
				var count = this.items.itemCount();
				if (count == 0) return;
				this.cache = new String[count];
			}
		}

		/** Diese Methode gibt die Zeichenkette zum {@code index}-ten Element zurück. Wenn der Index ungültig ist, wird {@code ""} geliefert.
		 *
		 * @param index Index.
		 * @return {@code index}-te Zeichenkette oder {@code ""}. */
		@Override
		public String get(int index) {
			var cache = this.cache;
			if (cache != null) {
				if ((index < 0) || (index >= cache.length)) return "";
				var result = cache[index];
				if (result != null) return result;
				cache[index] = result = BEXFile.stringFrom(this.items.item(index));
				return result;
			} else {
				var result = BEXFile.stringFrom(this.items.item(index));
				return result;
			}
		}

		@Override
		public String toString() {
			return Objects.toString(true, Iterables.filter(Arrays.asList(this.cache), Filters.empty()));
		}

		/** Dieses Feld speichert die Elemente, deren Zeichenketten verwaltet werden. */
		final IAMListing items;

		/** Dieses Feld puffert die Zeichenketten der Elemente. */
		String[] cache;

		/** Dieser Konstruktor initialisiert die Elemente, deren Zeichenketten verwaltet werden.
		 *
		 * @param items Elemente. */
		BEXStringLoader(IAMListing items) {
			this.items = items;
		}

	}

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

	/** Diese Methode gibt die Referenz des gegebenen Schlüssels zurück.
	 *
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Referenz. */
	static int refOf(int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

	/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
	 *
	 * @see BEXNode#key()
	 * @see BEXList#key()
	 * @param type Typkennung (0..7).
	 * @param ref Referenz als Zeilennummer des Datensatzes.
	 * @return Schlüssel. */
	static int keyOf(int type, int ref) {
		return (ref << 3) | (type << 0);
	}

	/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
	 *
	 * @see #keyOf(int, int)
	 * @param key Schlüssel.
	 * @return Typkennung. */
	static int typeOf(int key) {
		return (key >> 0) & 7;
	}

}
