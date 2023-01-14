/* [cc-by] 2014-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#include <windows.h>
#include <winbase.h>
#include "bee_creative_mmf.hpp"

namespace bee_creative {

inline UINT32 getValueBE(UINT8 const* array, UINT8 length) {
	switch (length) {
		case 0:
			return 0;
		case 1:
			return (array[0] << 0);
		case 2:
			return (array[0] << 8) | (array[1] << 0);
		case 3:
			return (array[0] << 16) | (array[1] << 8) | (array[2] << 0);
		case 4:
			return (array[0] << 24) | (array[1] << 16) | (array[2] << 8) | (array[3] << 0);
	}
	return 0;
}

inline UINT32 getValueLE(UINT8 const* array, UINT8 length) {
	switch (length) {
		case 0:
			return 0;
		case 1:
			return (array[0] << 0);
		case 2:
			return (array[1] << 8) | (array[0] << 0);
		case 3:
			return (array[2] << 16) | (array[1] << 8) | (array[0] << 0);
		case 4:
			return (array[3] << 24) | (array[2] << 16) | (array[1] << 8) | (array[0] << 0);
	}
	return 0;
}

inline UINT8 getLength(UINT32 value) {
	if (value & 0xFF000000) return 4;
	if (value & 0xFFFF0000) return 3;
	if (value & 0xFFFFFF00) return 2;
	if (value) return 1;
	return 0;
}

MMFArray::Data::Data() : addr_(0), size_(0), owner_() {
}

MMFArray::Data::Data(UINT8* addr, UINT32 size, Data* owner) : addr_(addr), size_(size), owner_(owner) {
}

MMFArray::Data::~Data() {
	if (owner_ || !addr_) return;
	UnmapViewOfFile(addr_);
}

MMFArray::Data::Ptr mmfarray_data_empty_(new MMFArray::Data());

MMFArray::MMFArray() : data_(mmfarray_data_empty_) {
}

MMFArray::MMFArray(PCCHAR filename, bool readonly) : data_(mmfarray_data_empty_) {
	if (!filename) return;
	HANDLE filehandle = CreateFile(filename, !readonly ? GENERIC_WRITE | GENERIC_READ : GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
	if (filehandle == INVALID_HANDLE_VALUE) return;
	LARGE_INTEGER filesize;
	if (GetFileSizeEx(filehandle, &filesize)) {
		filesize.LowPart = filesize.HighPart ? 0xFFFFFFFF : filesize.LowPart;
	} else {
		filesize.LowPart = 0;
	}
	UINT32 viewsize = filesize.LowPart;
	HANDLE filemapping = viewsize ? CreateFileMappingA(filehandle, NULL, readonly ? PAGE_READONLY : PAGE_READWRITE, 0, viewsize, filename) : NULL;
	CloseHandle(filehandle);
	if (filemapping == NULL) return;
	UINT8* viewdata = (UINT8*) MapViewOfFile(filemapping, readonly ? FILE_MAP_READ : FILE_MAP_WRITE, 0, 0, 0);
	CloseHandle(filemapping);
	if (!viewdata) return;
	data_ = new Data(viewdata, viewsize, 0);
}

UINT8* MMFArray::addr() const {
	Data& data = *data_;
	return data.addr_;
}

UINT32 MMFArray::size() const {
	Data& data = *data_;
	return data.size_;
}

MMFArray MMFArray::part(UINT32 offset, UINT32 length) const {
	Data& data = *data_;
	Data* owner = data.owner_.get();
	MMFArray result;
	if (offset + length > data.size_) return result;
	result.data_ = new Data(data.addr_ + offset, length, owner ? owner : &data);
	return result;
}

}

