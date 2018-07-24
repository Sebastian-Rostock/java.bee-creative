/* [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#include "bee_creative_mmf.hpp"

#include <windows.h>
#include <winbase.h>

namespace bee {

namespace creative {

namespace mmf {

inline UINT32 readBE(UINT8 const* _array, UINT8 _length) {
	switch (_length) {
		case 0:
			return 0;
		case 1:
			return (_array[0] << 0);
		case 2:
			return (_array[0] << 8) | (_array[1] << 0);
		case 3:
			return (_array[0] << 16) | (_array[1] << 8) | (_array[2] << 0);
		case 4:
			return (_array[0] << 24) | (_array[1] << 16) | (_array[2] << 8) | (_array[3] << 0);
	}
	return 0;
}

inline UINT32 readLE(UINT8 const* _array, UINT8 _length) {
	switch (_length) {
		case 0:
			return 0;
		case 1:
			return (_array[0] << 0);
		case 2:
			return (_array[1] << 8) | (_array[0] << 0);
		case 3:
			return (_array[2] << 16) | (_array[1] << 8) | (_array[0] << 0);
		case 4:
			return (_array[3] << 24) | (_array[2] << 16) | (_array[1] << 8) | (_array[0] << 0);
	}
	return 0;
}

inline UINT8 lengthOf(UINT32 _value) {
	if (_value & 0xFF000000) return 4;
	if (_value & 0xFFFF0000) return 3;
	if (_value & 0xFFFFFF00) return 2;
	if (_value) return 1;
	return 0;
}

// class MMFArray

MMFArray::OBJECT::OBJECT()
		: _data_(0), _size_(0), _owner_() {
}

MMFArray::OBJECT::OBJECT(PVOID _data, UINT32 _size, OBJECT* _owner)
		: _data_(_data), _size_(_size), _owner_(_owner) {
}

MMFArray::OBJECT::~OBJECT() {
	if (_owner_ || !_data_) return;
	UnmapViewOfFile(_data_);
}

/* Dieses Feld speichert die leeren Nutzdaten eines @c MMFArray. */
RCPointer<MMFArray::OBJECT> _MMF_VIEW_OBJECT_(new MMFArray::OBJECT());

MMFArray::MMFArray()
		: _object_(_MMF_VIEW_OBJECT_) {
}

MMFArray::MMFArray(PCCHAR _filename, bool _readonly)
		: _object_(_MMF_VIEW_OBJECT_) {
	if (!_filename) return;
	HANDLE _filehandle = CreateFile(_filename, !_readonly ? GENERIC_WRITE | GENERIC_READ : GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
	if (_filehandle == INVALID_HANDLE_VALUE) return;
	LARGE_INTEGER _filesize;
	if (GetFileSizeEx(_filehandle, &_filesize)) {
		_filesize.LowPart = _filesize.HighPart ? 0xFFFFFFFF : _filesize.LowPart;
	} else {
		_filesize.LowPart = 0;
	}
	UINT32 _viewsize = _filesize.LowPart;
	HANDLE _filemapping = _viewsize ? CreateFileMappingA(_filehandle, NULL, _readonly ? PAGE_READONLY : PAGE_READWRITE, 0, _viewsize, _filename) : NULL;
	CloseHandle(_filehandle);
	if (_filemapping == NULL) return;
	PVOID _viewdata = MapViewOfFile(_filemapping, _readonly ? FILE_MAP_READ : FILE_MAP_WRITE, 0, 0, 0);
	CloseHandle(_filemapping);
	if (!_viewdata) return;
	_object_.set(new OBJECT(_viewdata, _viewsize, 0));
}

PVOID MMFArray::data() const {
	OBJECT& _this = *_object_;
	PVOID _result = _this._data_;
	return _result;
}

UINT32 MMFArray::size() const {
	OBJECT& _this = *_object_;
	UINT32 _result = _this._size_;
	return _result;
}

MMFArray MMFArray::part(UINT32 _offset, UINT32 _length) const {
	OBJECT& _this = *_object_;
	MMFArray _result;
	if (_offset + _length > _this._size_) return _result;
	OBJECT* _owner = _this._owner_;
	_result._object_ = new OBJECT((UINT8*) _this._data_ + _offset, _length, _owner ? _owner : &_this);
	return _result;
}

}

}

}
