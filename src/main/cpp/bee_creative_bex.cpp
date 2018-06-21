/* [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

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

inline INT32 _bexRef_(UINT32 key) {
	return key >> 3;
}

inline UINT32 _bexKey_(UINT8 type, UINT32 ref) {
	return type | (ref << 3);
}

inline UINT8 _bexType_(UINT32 key) {
	return key & 7;
}

inline string _bexString_(IAMArray const& array) {
	string result((PCCHAR) array.data());
	return result;
}

inline IAMArray _bexArray_(string const& string) {
	IAMArray result((INT8 const*) string.data(), string.length() + 1);
	return result;
}

/**
 * Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c BEXFile.
 * @param _fileData Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist.
 */
inline INT32 const* _bexFileChecked_(MMFArray const& _fileData) {
	if (_fileData.size() & 3) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) _fileData.data();
}

/**
 * Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c BEXFile.
 * @param _heapData Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist.
 */
inline INT32 const* _bexFileChecked_(IAMArray const& _heapData) {
	if (_heapData.mode() != 4) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) _heapData.data();
}

BEXFile::OBJECT::OBJECT()
	: _rootRef_(-1) {
}

// class BEXNode

BEXFile::OBJECT::OBJECT(INT32 const* _array, INT32 const _length)
	: OBJECT() {
	if (_length < 3) throw IAMException(IAMException::INVALID_LENGTH);

	UINT32 _header = _array[0];
	if (_header != 0xBE10BA5E) throw IAMException(IAMException::INVALID_HEADER);

	INT32 _rootRef = _array[1];
	IAMIndex _nodeData(_array + 2, _length - 2);
	if (false || //
	    (_nodeData.mappingCount() != 0) || //
	    (_nodeData.listingCount() != 17) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMListing _attrUriTextList = _nodeData.listing(0);
	IAMListing _attrNameTextList = _nodeData.listing(1);
	IAMListing _attrValueTextList = _nodeData.listing(2);
	IAMListing _chldUriTextList = _nodeData.listing(3);
	IAMListing _chldNameTextList = _nodeData.listing(4);
	IAMListing _chldValueTextList = _nodeData.listing(5);
	IAMListing _attrUriRefList = _nodeData.listing(6);
	IAMListing _attrNameRefList = _nodeData.listing(7);
	IAMListing _attrValueRefList = _nodeData.listing(8);
	IAMListing _attrParentRefList = _nodeData.listing(9);
	IAMListing _chldUriRefList = _nodeData.listing(10);
	IAMListing _chldNameRefList = _nodeData.listing(11);
	IAMListing _chldContentRefList = _nodeData.listing(12);
	IAMListing _chldAttributesRefList = _nodeData.listing(13);
	IAMListing _chldParentRefList = _nodeData.listing(14);
	IAMListing _attrListRangeList = _nodeData.listing(15);
	IAMListing _chldListRangeList = _nodeData.listing(16);
	if (false || //
	    (_attrUriRefList.itemCount() != 1) || //
	    (_attrNameRefList.itemCount() != 1) || //
	    (_attrValueRefList.itemCount() != 1) || //
	    (_attrParentRefList.itemCount() != 1) || //
	    (_chldUriRefList.itemCount() != 1) || //
	    (_chldNameRefList.itemCount() != 1) || //
	    (_chldContentRefList.itemCount() != 1) || //
	    (_chldAttributesRefList.itemCount() != 1) || //
	    (_chldParentRefList.itemCount() != 1) || //
	    (_attrListRangeList.itemCount() != 1) || //
	    (_chldListRangeList.itemCount() != 1) //
	    ) throw IAMException(IAMException::INVALID_VALUE);

	IAMArray _attrUriRef = _attrUriRefList.item(0);
	IAMArray _attrNameRef = _attrNameRefList.item(0);
	IAMArray _attrValueRef = _attrValueRefList.item(0);
	IAMArray _attrParentRef = _attrParentRefList.item(0);
	IAMArray _chldUriRef = _chldUriRefList.item(0);
	IAMArray _chldNameRef = _chldNameRefList.item(0);
	IAMArray _chldContentRef = _chldContentRefList.item(0);
	IAMArray _chldAttributesRef = _chldAttributesRefList.item(0);
	IAMArray _chldParentRef = _chldParentRefList.item(0);
	IAMArray _chldListRange = _chldListRangeList.item(0);
	IAMArray _attrListRange = _attrListRangeList.item(0);
	INT32 _attrCount = _attrNameRef.length();
	INT32 _chldCount = _chldNameRef.length();

	if (false || //
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
	_nodeData_ = _nodeData;
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

RCPointer<BEXFile::OBJECT> _BEX_FILE_OBJECT_(new BEXFile::OBJECT());

BEXFile::BEXFile()
	: _object_(_BEX_FILE_OBJECT_) {
}

BEXFile::BEXFile(IAMArray const& _heapData)
	: _object_(new OBJECT(_bexFileChecked_(_heapData), _heapData.length())) {
	_object_->_heapData_ = _heapData;
}

BEXFile::BEXFile(MMFArray const& _fileData)
	: _object_(new OBJECT(_bexFileChecked_(_fileData), _fileData.size() >> 2)) {
	_object_->_fileData_ = _fileData;
}

BEXFile::BEXFile(INT32 const* _array, INT32 const _length)
	: _object_(new OBJECT(_array, _length)) {
}

BEXNode BEXFile::root() {
	INT32 _ref = _object_.get()->_rootRef_;
	if (_ref < 0) return BEXNode(*this);
	return BEXNode(_bexKey_(BEX_ELEM_NODE, _ref), *this);
}

BEXNode BEXFile::node(UINT32 const _key) {
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

BEXList BEXFile::list(UINT32 const _key) {
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

