/* [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#include "bee_creative_bex.hpp"

namespace bee_creative {

// Typkennung für den undefinierten Knoten bzw. die undefinierte Knotenliste.
const UINT8 BEX_VOID_TYPE = 0;

// Typkennung für einen Attributknoten.
const UINT8 BEX_ATTR_NODE = 1;

// Typkennung für einen Elementknoten.
const UINT8 BEX_ELEM_NODE = 2;

// Typkennung für einen Textknoten.
const UINT8 BEX_TEXT_NODE = 3;

// Typkennung für den Textknoten eines Elementknoten.
const UINT8 BEX_ELTX_NODE = 4;

// Typkennung für eine Attributknotenliste.
const UINT8 BEX_ATTR_LIST = 5;

// Typkennung für eine Kindknotenliste.
const UINT8 BEX_CHLD_LIST = 6;

// Typkennung für die Kindknotenliste dem Textknoten eines Elementknoten.
const UINT8 BEX_CHTX_LIST = 7;

/** Diese Methode gibt die Referenz des gegebenen Schlüssels zurück.
 * @param key Schlüssel.
 * @return Referenz. */
inline INT32 bex_ref_(UINT32 key) {
	return key >> 3;
}

/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
 * @param type Typkennung (0..7).
 * @param ref Referenz als Zeilennummer des Datensatzes.
 * @return Schlüssel. */
inline UINT32 bex_key_(UINT8 type, UINT32 ref) {
	return (ref << 3) | type;
}

/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
 * @param key Schlüssel.
 * @return Typkennung. */
inline UINT8 bex_type_(UINT32 key) {
	return key & 7;
}

/** Diese Methode gibt die gegebenen Zahlenfolge als Zeichenkette zurück.
 * @param array Zahlenfolge.
 * @return Zeichenkette. */
inline std::string bex_string_(IAMArray const& array) {
	std::string result((PCCHAR) array.data());
	return result;
}

/** Diese Methode gibt die gegebenen Zeichenkette als Zahlenfolge zurück.
 * @param string Zeichenkette.
 * @return Zahlenfolge. */
inline IAMArray bex_array_(std::string const& string) {
	IAMArray result((INT8 const*) string.data(), string.length() + 1);
	return result;
}

BEXFile::Data::Data() : root_ref_(-1) {
}

BEXFile::Data::Data(IAMIndex const& file_data) : Data() {

	if (false || //
	    (file_data.mappingCount() != 0) || //
	    (file_data.listingCount() != 18) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMListing head_root_listing = file_data.listing(0);
	IAMListing attr_uri_text_list = file_data.listing(1);
	IAMListing attr_name_text_list = file_data.listing(2);
	IAMListing attr_value_text_list = file_data.listing(3);
	IAMListing chld_uri_text_list = file_data.listing(4);
	IAMListing chld_name_text_list = file_data.listing(5);
	IAMListing chld_value_text_list = file_data.listing(6);
	IAMListing attr_uri_ref_listing = file_data.listing(7);
	IAMListing attr_name_ref_listing = file_data.listing(8);
	IAMListing attr_value_ref_listing = file_data.listing(9);
	IAMListing attr_parent_ref_listing = file_data.listing(10);
	IAMListing chld_uri_ref_listing = file_data.listing(11);
	IAMListing chld_name_ref_listing = file_data.listing(12);
	IAMListing chld_content_ref_listing = file_data.listing(13);
	IAMListing chld_attributes_ref_listing = file_data.listing(14);
	IAMListing chld_parent_ref_listing = file_data.listing(15);
	IAMListing attr_list_range_listing = file_data.listing(16);
	IAMListing chld_list_range_listing = file_data.listing(17);

	if (false || //
	    (head_root_listing.itemCount() != 1) || //
	    (attr_uri_ref_listing.itemCount() != 1) || //
	    (attr_name_ref_listing.itemCount() != 1) || //
	    (attr_value_ref_listing.itemCount() != 1) || //
	    (attr_parent_ref_listing.itemCount() != 1) || //
	    (chld_uri_ref_listing.itemCount() != 1) || //
	    (chld_name_ref_listing.itemCount() != 1) || //
	    (chld_content_ref_listing.itemCount() != 1) || //
	    (chld_attributes_ref_listing.itemCount() != 1) || //
	    (chld_parent_ref_listing.itemCount() != 1) || //
	    (attr_list_range_listing.itemCount() != 1) || //
	    (chld_list_range_listing.itemCount() != 1) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMArray head_root = head_root_listing.item(0);
	IAMArray attr_uri_ref = attr_uri_ref_listing.item(0);
	IAMArray attr_name_ref = attr_name_ref_listing.item(0);
	IAMArray attr_value_ref = attr_value_ref_listing.item(0);
	IAMArray attr_parent_ref = attr_parent_ref_listing.item(0);
	IAMArray chld_uri_ref = chld_uri_ref_listing.item(0);
	IAMArray chld_name_ref = chld_name_ref_listing.item(0);
	IAMArray chld_content_ref = chld_content_ref_listing.item(0);
	IAMArray chld_attributes_ref = chld_attributes_ref_listing.item(0);
	IAMArray chld_parent_ref = chld_parent_ref_listing.item(0);
	IAMArray chld_list_range = chld_list_range_listing.item(0);
	IAMArray attr_list_range = attr_list_range_listing.item(0);

	INT32 root_mag = head_root.get(0);
	INT32 root_ref = head_root.get(1);
	INT32 attr_count = attr_name_ref.length();
	INT32 chld_count = chld_name_ref.length();

	if (false || //
	    (root_mag != (INT32) 0xBE10BA5E) || //
	    (root_ref < 0) || //
	    (chld_count <= root_ref) || //
	    ((attr_uri_ref.length() != attr_count) && (attr_uri_ref.length() != 0)) || //
	    (attr_value_ref.length() != attr_count) || //
	    ((attr_parent_ref.length() != attr_count) && (attr_parent_ref.length() != 0)) || //
	    ((chld_uri_ref.length() != chld_count) && (chld_uri_ref.length() != 0)) || //
	    (chld_content_ref.length() != chld_count) || //
	    (chld_attributes_ref.length() != chld_count) || //
	    ((chld_parent_ref.length() != chld_count) && (chld_parent_ref.length() != 0)) || //
	    (chld_list_range.length() < 3) || //
	    (attr_list_range.length() < 2) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	root_ref_ = root_ref;
	file_data_ = file_data;
	attr_uri_text_ = attr_uri_text_list;
	attr_name_text_ = attr_name_text_list;
	attr_value_text_ = attr_value_text_list;
	chld_uri_text_ = chld_uri_text_list;
	chld_name_text_ = chld_name_text_list;
	chld_value_text_ = chld_value_text_list;
	attr_uri_ref_ = attr_uri_ref;
	attr_name_ref_ = attr_name_ref;
	attr_value_ref_ = attr_value_ref;
	attr_parent_ref_ = attr_parent_ref;
	chld_uri_ref_ = chld_uri_ref;
	chld_name_ref_ = chld_name_ref;
	chld_content_ref_ = chld_content_ref;
	chld_attributes_ref_ = chld_attributes_ref;
	chld_rarent_ref_ = chld_parent_ref;
	chld_list_range_ = chld_list_range;
	attr_list_range_ = attr_list_range;

}

BEXNode::BEXNode() : owner_(), key_(bex_key_(BEX_VOID_TYPE, 0)) {
}

BEXNode::BEXNode(BEXFile const& owner) : owner_(owner), key_(bex_key_(BEX_VOID_TYPE, 0)) {
}

BEXNode::BEXNode(BEXFile const& owner, UINT32 const key) : owner_(owner), key_(key) {
}

UINT32 BEXNode::key() const {
	return key_;
}

UINT8 BEXNode::type() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return BEXNode::VOID_NODE;
		case BEX_ATTR_NODE:
			return BEXNode::ATTR_NODE;
		case BEX_ELEM_NODE:
			return BEXNode::ELEM_NODE;
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return BEXNode::TEXT_NODE;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXFile BEXNode::owner() const {
	return owner_;
}

std::string BEXNode::uri() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_ATTR_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.attr_uri_text_.item(data.attr_uri_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.chld_uri_text_.item(data.chld_uri_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
		case BEX_VOID_TYPE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return std::string();
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

std::string BEXNode::name() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_ATTR_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.attr_name_text_.item(data.attr_name_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.chld_name_text_.item(data.chld_name_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
		case BEX_VOID_TYPE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return std::string();
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

std::string BEXNode::value() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return std::string();
		case BEX_ATTR_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.attr_value_text_.item(data.attr_value_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			INT32 index = bex_ref_(key);
			INT32 content = data.chld_content_ref_.get(index);
			if (content >= 0) {
				IAMArray result = data.chld_value_text_.item(content);
				return bex_string_(result);
			} else {
				BEXList children(owner_, bex_key_(BEX_CHLD_LIST, index), -content);
				return children.get(0).value();
			}
		}
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray result = data.chld_value_text_.item(data.chld_content_ref_.get(bex_ref_(key)));
			return bex_string_(result);
		}
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

INT32 BEXNode::index() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return -1;
		case BEX_ATTR_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.attr_parent_ref_;
			if (array.length() == 0) return -1;
			INT32 index = bex_ref_(key);
			return index - data.attr_list_range_.get(data.chld_attributes_ref_.get(array.get(index)));
		}
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.chld_rarent_ref_;
			if (array.length() == 0) return -1;
			INT32 index = bex_ref_(key);
			INT32 parent = array.get(index);
			if (index == parent) return -1;
			return index - data.chld_list_range_.get(-data.chld_content_ref_.get(parent));
		}
		case BEX_TEXT_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.chld_rarent_ref_;
			if (array.length() == 0) return -1;
			INT32 index = bex_ref_(key);
			return index - data.chld_list_range_.get(-data.chld_content_ref_.get(array.get(index)));
		}
		case BEX_ELTX_NODE:
			return 0;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXNode BEXNode::parent() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return BEXNode(owner_);
		case BEX_ATTR_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.attr_parent_ref_;
			if (array.length() == 0) return BEXNode(owner_);
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, array.get(bex_ref_(key))));
		}
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.chld_rarent_ref_;
			if (array.length() == 0) return BEXNode(owner_);
			INT32 index = bex_ref_(key);
			INT32 _parent = array.get(index);
			if (index == _parent) return BEXNode(owner_);
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, _parent));
		}
		case BEX_TEXT_NODE: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& array = data.chld_rarent_ref_;
			if (array.length() == 0) return BEXNode(owner_);
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, array.get(bex_ref_(key))));
		}
		case BEX_ELTX_NODE:
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, bex_ref_(key)));
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXList BEXNode::attributes() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_ELEM_NODE: {
			BEXFile::Data& data = *owner_.data_;
			INT32 index = bex_ref_(key);
			return BEXList(owner_, bex_key_(BEX_ATTR_LIST, index), data.chld_attributes_ref_.get(index));
		}
		case BEX_VOID_TYPE:
		case BEX_ATTR_NODE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return BEXList(owner_);
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXList::BEXList() : owner_(), key_(bex_key_(BEX_VOID_TYPE, 0)), ref_(0) {
}

BEXList::BEXList(BEXFile const& owner) : owner_(owner), key_(bex_key_(BEX_VOID_TYPE, 0)), ref_(0) {
}

BEXList::BEXList(BEXFile const& owner, UINT32 const key, UINT32 const ref) : owner_(owner), key_(key), ref_(ref) {
}

UINT32 BEXList::key() const {
	return key_;
}

UINT8 BEXList::type() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return BEXList::VOID_LIST;
		case BEX_ATTR_LIST:
			return BEXList::ATTR_LIST;
		case BEX_CHLD_LIST:
		case BEX_CHTX_LIST:
			return BEXList::CHLD_LIST;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXFile BEXList::owner() const {
	return owner_;
}

BEXNode BEXList::get(INT32 const index) const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return BEXNode(owner_);
		case BEX_ATTR_LIST: {
			if (index < 0) return BEXNode(owner_);
			BEXFile::Data& data = *owner_.data_;
			IAMArray& _ranges = data.attr_list_range_;
			INT32 ref = ref_;
			INT32 result = _ranges.get(ref) + index;
			if (result >= _ranges.get(ref + 1)) return BEXNode(owner_);
			return BEXNode(owner_, bex_key_(BEX_ATTR_NODE, result));
		}
		case BEX_CHLD_LIST: {
			if (index < 0) return BEXNode(owner_);
			BEXFile::Data& data = *owner_.data_;
			IAMArray& _ranges = data.chld_list_range_;
			INT32 ref = ref_;
			INT32 result = _ranges.get(ref) + index;
			if (result >= _ranges.get(ref + 1)) return BEXNode(owner_);
			if (data.chld_name_ref_.get(result) == 0) return BEXNode(owner_, bex_key_(BEX_TEXT_NODE, result));
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, result));
		}
		case BEX_CHTX_LIST: {
			if (index != 0) return BEXNode(owner_);
			return BEXNode(owner_, bex_key_(BEX_ELTX_NODE, bex_ref_(key)));
		}
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

INT32 BEXList::find(std::string const& uri, std::string const& name, INT32 const start) const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_ATTR_LIST: {
			if (start < 0) return -1;
			bool use_uri = uri.length() != 0, use_name = name.length() != 0;
			IAMArray uri_array(bex_array_(uri)), name_array(bex_array_(name));
			BEXFile::Data& data = *owner_.data_;
			IAMArray& ranges = data.attr_list_range_;
			INT32 ref = ref_;
			INT32 start_ref = ranges.get(ref), final_ref = ranges.get(ref + 1);
			for (ref = start_ref + start; ref < final_ref; ref++) {
				if (use_uri) {
					IAMArray attr_uri = data.attr_uri_text_.item(data.attr_uri_ref_.get(ref));
					if (!attr_uri.equals(uri_array)) continue;
				}
				if (use_name) {
					IAMArray attr_name = data.attr_name_text_.item(data.attr_name_ref_.get(ref));
					if (!attr_name.equals(name_array)) continue;
				}
				return ref - start_ref;
			}
			return -1;
		}
		case BEX_CHLD_LIST: {
			if (start < 0) return -1;
			bool use_uri = uri.length() != 0, use_name = name.length() != 0;
			IAMArray uri_array(bex_array_(uri)), name_array(bex_array_(name));
			BEXFile::Data& data = *owner_.data_;
			IAMArray& ranges = data.chld_list_range_;
			INT32 ref = ref_;
			INT32 start_ref = ranges.get(ref), final_ref = ranges.get(ref + 1);
			for (ref = start_ref + start; ref < final_ref; ref++) {
				INT32 name_ref = data.chld_name_ref_.get(ref);
				if (name_ref == 0) continue;
				if (use_uri) {
					IAMArray chld_uri = data.chld_uri_text_.item(data.chld_uri_ref_.get(ref));
					if (!chld_uri.equals(uri_array)) continue;
				}
				if (use_name) {
					IAMArray chld_name = data.chld_name_text_.item(data.chld_name_ref_.get(ref));
					if (!chld_name.equals(name_array)) continue;
				}
				return ref - start_ref;
			}
			return -1;
		}
		case BEX_VOID_TYPE:
		case BEX_CHTX_LIST:
			return -1;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

INT32 BEXList::length() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return 0;
		case BEX_ATTR_LIST: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& ranges = data.attr_list_range_;
			INT32 ref = ref_;
			return ranges.get(ref + 1) - ranges.get(ref);
		}
		case BEX_CHLD_LIST: {
			BEXFile::Data& data = *owner_.data_;
			IAMArray& ranges = data.chld_list_range_;
			INT32 ref = ref_;
			return ranges.get(ref + 1) - ranges.get(ref);
		}
		case BEX_CHTX_LIST:
			return 1;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXNode BEXList::parent() const {
	UINT32 key = key_;
	switch (bex_type_(key)) {
		case BEX_VOID_TYPE:
			return BEXNode(owner_);
		case BEX_ATTR_LIST:
		case BEX_CHLD_LIST:
		case BEX_CHTX_LIST:
			return BEXNode(owner_, bex_key_(BEX_ELEM_NODE, bex_ref_(key)));
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXFile::Data::Ptr bexfile_data_empty_(new BEXFile::Data());

BEXFile::BEXFile() : data_(bexfile_data_empty_) {
}

BEXFile::BEXFile(IAMIndex const& base_data) : data_(new Data(base_data)) {
}

BEXNode BEXFile::root() const {
	INT32 ref = data_->root_ref_;
	if (ref < 0) return BEXNode(*this);
	return BEXNode(*this, bex_key_(BEX_ELEM_NODE, ref));
}

BEXNode BEXFile::node(UINT32 const key) const {
	switch (bex_type_(key)) {
		case BEX_ATTR_NODE: {
			Data& data = *data_;
			INT32 index = bex_ref_(key);
			if (index >= data.attr_name_ref_.length()) return BEXNode(*this);
			return BEXNode(*this, bex_key_(BEX_ATTR_NODE, index));
		}
		case BEX_ELEM_NODE: {
			Data& data = *data_;
			INT32 index = bex_ref_(key);
			if (data.chld_name_ref_.get(index) == 0) return BEXNode(*this);
			return BEXNode(*this, bex_key_(BEX_ELEM_NODE, index));
		}
		case BEX_TEXT_NODE: {
			Data& data = *data_;
			INT32 index = bex_ref_(key);
			IAMArray& names = data.chld_name_ref_;
			if ((index >= names.length()) || (names.get(index) != 0)) return BEXNode(*this);
			return BEXNode(*this, bex_key_(BEX_TEXT_NODE, index));
		}
		case BEX_ELTX_NODE: {
			Data& data = *data_;
			INT32 index = bex_ref_(key);
			if ((data.chld_name_ref_.get(index) == 0) || (data.chld_content_ref_.get(index) < 0)) return BEXNode(*this);
			return BEXNode(*this, bex_key_(BEX_ELTX_NODE, index));
		}
	}
	return BEXNode(*this);
}

BEXList BEXFile::list(UINT32 const key) const {
	switch (bex_type_(key)) {
		case BEX_ATTR_LIST:
			return node(bex_key_(BEX_ELEM_NODE, bex_ref_(key))).attributes();
		case BEX_CHLD_LIST:
		case BEX_CHTX_LIST:
			return node(bex_key_(BEX_ELEM_NODE, bex_ref_(key))).children();
	}
	return BEXList(*this);
}

}
