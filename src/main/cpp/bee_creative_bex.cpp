/* [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifdef false

#include "bee_creative_bex.hpp"

namespace bee {

namespace creative {

namespace bex {

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
 * @param _key Schlüssel.
 * @return Referenz. */
inline INT32 _bexRef_(UINT32 _key) {
	return _key >> 3;
}

/** Diese Methode gibt einen Schlüssel mit den gegebenen Eigenschaften zurück.
 * @param type Typkennung (0..7).
 * @param ref Referenz als Zeilennummer des Datensatzes.
 * @return Schlüssel. */
inline UINT32 _bexKey_(UINT8 _type, UINT32 _ref) {
	return (_ref << 3) | _type;
}

/** Diese Methode gibt die Typkennung des gegebenen Schlüssels zurück.
 * @param key Schlüssel.
 * @return Typkennung. */
inline UINT8 _bexType_(UINT32 _key) {
	return _key & 7;
}

/** Diese Methode gibt die gegebenen Zahlenfolge als Zeichenkette zurück.
 * @param _array Zahlenfolge.
 * @return Zeichenkette. */
inline string _bexString_(IAMArray const& _array) {
	string result((PCCHAR) _array.data());
	return result;
}

/** Diese Methode gibt die gegebenen Zeichenkette als Zahlenfolge zurück.
 * @param _string Zeichenkette.
 * @return Zahlenfolge. */
inline IAMArray _bexArray_(string const& _string) {
	IAMArray result((INT8 const*) _string.data(), _string.length() + 1);
	return result;
}

BEXFile::OBJECT::OBJECT()
	: _rootRef_(-1) {
}

// class BEXNode

BEXFile::OBJECT::OBJECT(IAMIndex const& _fileData)
	: OBJECT() {

	if (false || //
	    (_fileData.mappingCount() != 0) || //
	    (_fileData.listingCount() != 18) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMListing _headRootListing = _fileData.listing(0);
	IAMListing _attrUriTextList = _fileData.listing(1);
	IAMListing _attrNameTextList = _fileData.listing(2);
	IAMListing _attrValueTextList = _fileData.listing(3);
	IAMListing _chldUriTextList = _fileData.listing(4);
	IAMListing _chldNameTextList = _fileData.listing(5);
	IAMListing _chldValueTextList = _fileData.listing(6);
	IAMListing _attrUriRefListing = _fileData.listing(7);
	IAMListing _attrNameRefListing = _fileData.listing(8);
	IAMListing _attrValueRefListing = _fileData.listing(9);
	IAMListing _attrParentRefListing = _fileData.listing(10);
	IAMListing _chldUriRefListing = _fileData.listing(11);
	IAMListing _chldNameRefListing = _fileData.listing(12);
	IAMListing _chldContentRefListing = _fileData.listing(13);
	IAMListing _chldAttributesRefListing = _fileData.listing(14);
	IAMListing _chldParentRefListing = _fileData.listing(15);
	IAMListing _attrListRangeListing = _fileData.listing(16);
	IAMListing _chldListRangeListing = _fileData.listing(17);

	if (false || //
	    (_headRootListing.itemCount() != 1) || //
	    (_attrUriRefListing.itemCount() != 1) || //
	    (_attrNameRefListing.itemCount() != 1) || //
	    (_attrValueRefListing.itemCount() != 1) || //
	    (_attrParentRefListing.itemCount() != 1) || //
	    (_chldUriRefListing.itemCount() != 1) || //
	    (_chldNameRefListing.itemCount() != 1) || //
	    (_chldContentRefListing.itemCount() != 1) || //
	    (_chldAttributesRefListing.itemCount() != 1) || //
	    (_chldParentRefListing.itemCount() != 1) || //
	    (_attrListRangeListing.itemCount() != 1) || //
	    (_chldListRangeListing.itemCount() != 1) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMArray _headRoot = _headRootListing.item(0);
	IAMArray _attrUriRef = _attrUriRefListing.item(0);
	IAMArray _attrNameRef = _attrNameRefListing.item(0);
	IAMArray _attrValueRef = _attrValueRefListing.item(0);
	IAMArray _attrParentRef = _attrParentRefListing.item(0);
	IAMArray _chldUriRef = _chldUriRefListing.item(0);
	IAMArray _chldNameRef = _chldNameRefListing.item(0);
	IAMArray _chldContentRef = _chldContentRefListing.item(0);
	IAMArray _chldAttributesRef = _chldAttributesRefListing.item(0);
	IAMArray _chldParentRef = _chldParentRefListing.item(0);
	IAMArray _chldListRange = _chldListRangeListing.item(0);
	IAMArray _attrListRange = _attrListRangeListing.item(0);

	INT32 _headVal = _headRoot.get(0);
	INT32 _rootRef = _headRoot.get(1);
	INT32 _attrCount = _attrNameRef.length();
	INT32 _chldCount = _chldNameRef.length();

	if (false || //
		(_headVal != (INT32)0xBE10BA5E) || //
	    (_rootRef < 0) || //
	    (_chldCount <= _rootRef) || //
	    ((_attrUriRef.length() != _attrCount) && (_attrUriRef.length() != 0)) || //
	    (_attrValueRef.length() != _attrCount) || //
	    ((_attrParentRef.length() != _attrCount) && (_attrParentRef.length() != 0)) || //
	    ((_chldUriRef.length() != _chldCount) && (_chldUriRef.length() != 0)) || //
	    (_chldContentRef.length() != _chldCount) || //
	    (_chldAttributesRef.length() != _chldCount) || //
	    ((_chldParentRef.length() != _chldCount) && (_chldParentRef.length() != 0)) || //
	    (_chldListRange.length() < 3) || //
	    (_attrListRange.length() < 2) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	_rootRef_ = _rootRef;
	_fileData_ = _fileData;
	_attrUriText_ = _attrUriTextList;
	_attrNameText_ = _attrNameTextList;
	_attrValueText_ = _attrValueTextList;
	_chldUriText_ = _chldUriTextList;
	_chldNameText_ = _chldNameTextList;
	_chldValueText_ = _chldValueTextList;
	_attrUriRef_ = _attrUriRef;
	_attrNameRef_ = _attrNameRef;
	_attrValueRef_ = _attrValueRef;
	_attrParentRef_ = _attrParentRef;
	_chldUriRef_ = _chldUriRef;
	_chldNameRef_ = _chldNameRef;
	_chldContentRef_ = _chldContentRef;
	_chldAttributesRef_ = _chldAttributesRef;
	_chldParentRef_ = _chldParentRef;
	_chldListRange_ = _chldListRange;
	_attrListRange_ = _attrListRange;

}

BEXNode::BEXNode()
	: _key_(_bexKey_(BEX_VOID_TYPE, 0)), _owner_() {
}

BEXNode::BEXNode(BEXFile const& _owner)
	: _key_(_bexKey_(BEX_VOID_TYPE, 0)), _owner_(_owner) {
}

BEXNode::BEXNode(UINT32 const _key, BEXFile const& _owner)
	: _key_(_key), _owner_(_owner) {
}

UINT32 BEXNode::key() const {
	return _key_;
}

UINT8 BEXNode::type() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
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
	return _owner_;
}

string BEXNode::uri() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_ATTR_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_attrUriText_.item(_object->_attrUriRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_chldUriText_.item(_object->_chldUriRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
		case BEX_VOID_TYPE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return string();
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

string BEXNode::name() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_ATTR_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_attrNameText_.item(_object->_attrNameRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_chldNameText_.item(_object->_chldNameRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
		case BEX_VOID_TYPE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return string();
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

string BEXNode::value() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return string();
		case BEX_ATTR_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_attrValueText_.item(_object->_attrValueRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			INT32 _index = _bexRef_(_key);
			INT32 _content = _object->_chldContentRef_.get(_index);
			if (_content >= 0) {
				IAMArray _result = _object->_chldValueText_.item(_content);
				return _bexString_(_result);
			} else {
				BEXList _children(_bexKey_(BEX_CHLD_LIST, _index), -_content, _owner_);
				return _children.get(0).value();
			}
		}
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray _result = _object->_chldValueText_.item(_object->_chldContentRef_.get(_bexRef_(_key)));
			return _bexString_(_result);
		}
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

INT32 BEXNode::index() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return -1;
		case BEX_ATTR_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_attrParentRef_;
			if (_array.length() == 0) return -1;
			INT32 _index = _bexRef_(_key);
			return _index - _object->_attrListRange_.get(_object->_chldAttributesRef_.get(_array.get(_index)));
		}
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_chldParentRef_;
			if (_array.length() == 0) return -1;
			INT32 _index = _bexRef_(_key);
			INT32 _parent = _array.get(_index);
			if (_index == _parent) return -1;
			return _index - _object->_chldListRange_.get(-_object->_chldContentRef_.get(_parent));
		}
		case BEX_TEXT_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_chldParentRef_;
			if (_array.length() == 0) return -1;
			INT32 _index = _bexRef_(_key);
			return _index - _object->_chldListRange_.get(-_object->_chldContentRef_.get(_array.get(_index)));
		}
		case BEX_ELTX_NODE:
			return 0;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXNode BEXNode::parent() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return BEXNode(_owner_);
		case BEX_ATTR_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_attrParentRef_;
			if (_array.length() == 0) return BEXNode(_owner_);
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _array.get(_bexRef_(_key))), _owner_);
		}
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_chldParentRef_;
			if (_array.length() == 0) return BEXNode(_owner_);
			INT32 _index = _bexRef_(_key);
			INT32 _parent = _array.get(_index);
			if (_index == _parent) return BEXNode(_owner_);
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _parent), _owner_);
		}
		case BEX_TEXT_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _array = _object->_chldParentRef_;
			if (_array.length() == 0) return BEXNode(_owner_);
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _array.get(_bexRef_(_key))), _owner_);
		}
		case BEX_ELTX_NODE:
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _bexRef_(_key)), _owner_);
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXList BEXNode::children() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			INT32 _index = _bexRef_(_key);
			INT32 _content = _object->_chldContentRef_.get(_index);
			if (_content >= 0) return BEXList(_bexKey_(BEX_CHTX_LIST, _index), 0, _owner_);
			return BEXList(_bexKey_(BEX_CHLD_LIST, _index), -_content, _owner_);
		}
		case BEX_VOID_TYPE:
		case BEX_ATTR_NODE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return BEXList(_owner_);
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXList BEXNode::attributes() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_ELEM_NODE: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			INT32 _index = _bexRef_(_key);
			return BEXList(_bexKey_(BEX_ATTR_LIST, _index), _object->_chldAttributesRef_.get(_index), _owner_);
		}
		case BEX_VOID_TYPE:
		case BEX_ATTR_NODE:
		case BEX_TEXT_NODE:
		case BEX_ELTX_NODE:
			return BEXList(_owner_);
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

// class BEXList

BEXList::BEXList()
	: _key_(_bexKey_(BEX_VOID_TYPE, 0)), _ref_(0), _owner_() {
}

BEXList::BEXList(BEXFile const& _owner)
	: _key_(_bexKey_(BEX_VOID_TYPE, 0)), _ref_(0), _owner_(_owner) {
}

BEXList::BEXList(UINT32 const _key, UINT32 const _ref, BEXFile const& _owner)
	: _key_(_key), _ref_(_ref), _owner_(_owner) {
}

UINT32 BEXList::key() const {
	return _key_;
}

UINT8 BEXList::type() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
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
	return _owner_;
}

BEXNode BEXList::get(INT32 const _index) const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return BEXNode(_owner_);
		case BEX_ATTR_LIST: {
			if (_index < 0) return BEXNode(_owner_);
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_attrListRange_;
			INT32 _ref = _ref_;
			INT32 _result = _ranges.get(_ref) + _index;
			if (_result >= _ranges.get(_ref + 1)) return BEXNode(_owner_);
			return BEXNode(_bexKey_(BEX_ATTR_NODE, _result), _owner_);
		}
		case BEX_CHLD_LIST: {
			if (_index < 0) return BEXNode(_owner_);
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_chldListRange_;
			INT32 _ref = _ref_;
			INT32 _result = _ranges.get(_ref) + _index;
			if (_result >= _ranges.get(_ref + 1)) return BEXNode(_owner_);
			if (_object->_chldNameRef_.get(_result) == 0) return BEXNode(_bexKey_(BEX_TEXT_NODE, _result), _owner_);
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _result), _owner_);
		}
		case BEX_CHTX_LIST: {
			if (_index != 0) return BEXNode(_owner_);
			return BEXNode(_bexKey_(BEX_ELTX_NODE, _bexRef_(_key)), _owner_);
		}
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

INT32 BEXList::find(string const& _uri, string const& _name, INT32 const _start) const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_ATTR_LIST: {
			if (_start < 0) return -1;
			bool _useUri = _uri.length() != 0, _useName = _name.length() != 0;
			IAMArray _uriArray(_bexArray_(_uri)), _nameArray(_bexArray_(_name));
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_attrListRange_;
			INT32 _ref = _ref_;
			INT32 _startRef = _ranges.get(_ref), _finalRef = _ranges.get(_ref + 1);
			for (_ref = _startRef + _start; _ref < _finalRef; _ref++) {
				if (_useUri) {
					IAMArray _attrUri = _object->_attrUriText_.item(_object->_attrUriRef_.get(_ref));
					if (!_attrUri.equals(_uriArray)) continue;
				}
				if (_useName) {
					IAMArray _nameUri = _object->_attrNameText_.item(_object->_attrNameRef_.get(_ref));
					if (!_nameUri.equals(_nameArray)) continue;
				}
				return _ref - _startRef;
			}
			return -1;
		}
		case BEX_CHLD_LIST: {
			if (_start < 0) return -1;
			bool _useUri = _uri.length() != 0, _useName = _name.length() != 0;
			IAMArray _uriArray(_bexArray_(_uri)), _nameArray(_bexArray_(_name));
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_chldListRange_;
			INT32 _ref = _ref_;
			INT32 _startRef = _ranges.get(_ref), _finalRef = _ranges.get(_ref + 1);
			for (_ref = _startRef + _start; _ref < _finalRef; _ref++) {
				INT32 _nameRef = _object->_chldNameRef_.get(_ref);
				if (_nameRef == 0) continue;
				if (_useUri) {
					IAMArray _attrUri = _object->_chldUriText_.item(_object->_chldUriRef_.get(_ref));
					if (!_attrUri.equals(_uriArray)) continue;
				}
				if (_useName) {
					IAMArray _nameUri = _object->_chldNameText_.item(_object->_chldNameRef_.get(_ref));
					if (!_nameUri.equals(_nameArray)) continue;
				}
				return _ref - _startRef;
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
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return 0;
		case BEX_ATTR_LIST: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_attrListRange_;
			INT32 _ref = _ref_;
			return _ranges.get(_ref + 1) - _ranges.get(_ref);
		}
		case BEX_CHLD_LIST: {
			BEXFile::OBJECT* _object = _owner_._object_.get();
			IAMArray & _ranges = _object->_chldListRange_;
			INT32 _ref = _ref_;
			return _ranges.get(_ref + 1) - _ranges.get(_ref);
		}
		case BEX_CHTX_LIST:
			return 1;
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

BEXNode BEXList::parent() const {
	UINT32 _key = _key_;
	switch (_bexType_(_key)) {
		case BEX_VOID_TYPE:
			return BEXNode(_owner_);
		case BEX_ATTR_LIST:
		case BEX_CHLD_LIST:
		case BEX_CHTX_LIST:
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _bexRef_(_key)), _owner_);
	}
	throw IAMException(IAMException::INVALID_HEADER);
}

Ref<BEXFile::OBJECT> _BEX_FILE_OBJECT_(new BEXFile::OBJECT());

BEXFile::BEXFile()
	: _object_(_BEX_FILE_OBJECT_) {
}

BEXFile::BEXFile(IAMIndex const& _fileData)
	: _object_(new OBJECT(_fileData)) {
}

BEXNode BEXFile::root() const {
	INT32 _ref = _object_.get()->_rootRef_;
	if (_ref < 0) return BEXNode(*this);
	return BEXNode(_bexKey_(BEX_ELEM_NODE, _ref), *this);
}

BEXNode BEXFile::node(UINT32 const _key) const {
	switch (_bexType_(_key)) {
		case BEX_ATTR_NODE: {
			OBJECT* _object = _object_.get();
			INT32 _index = _bexRef_(_key);
			if (_index >= _object->_attrNameRef_.length()) return BEXNode(*this);
			return BEXNode(_bexKey_(BEX_ATTR_NODE, _index), *this);
		}
		case BEX_ELEM_NODE: {
			OBJECT* _object = _object_.get();
			INT32 _index = _bexRef_(_key);
			if (_object->_chldNameRef_.get(_index) == 0) return BEXNode(*this);
			return BEXNode(_bexKey_(BEX_ELEM_NODE, _index), *this);
		}
		case BEX_TEXT_NODE: {
			OBJECT* _object = _object_.get();
			INT32 _index = _bexRef_(_key);
			IAMArray & _names = _object->_chldNameRef_;
			if ((_index >= _names.length()) || (_names.get(_index) != 0)) return BEXNode(*this);
			return BEXNode(_bexKey_(BEX_TEXT_NODE, _index), *this);
		}
		case BEX_ELTX_NODE: {
			OBJECT* _object = _object_.get();
			INT32 _index = _bexRef_(_key);
			if ((_object->_chldNameRef_.get(_index) == 0) || (_object->_chldContentRef_.get(_index) < 0)) return BEXNode(*this);
			return BEXNode(_bexKey_(BEX_ELTX_NODE, _index), *this);
		}
	}
	return BEXNode(*this);
}

BEXList BEXFile::list(UINT32 const _key) const {
	switch (_bexType_(_key)) {
		case BEX_ATTR_LIST:
			return node(_bexKey_(BEX_ELEM_NODE, _bexRef_(_key))).attributes();
		case BEX_CHLD_LIST:
		case BEX_CHTX_LIST:
			return node(_bexKey_(BEX_ELEM_NODE, _bexRef_(_key))).children();
	}
	return BEXList(*this);
}

}

}

}

#endif
