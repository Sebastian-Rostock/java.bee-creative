/* [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#include "bee_creative_iam.hpp"

namespace bee {

namespace creative {

namespace iam {

using mmf::MMFArray;

/**
 * Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
 * @param _dataType Datengrößentyp (1 = @c INT8, 2 = @c INT16, 3 = @c INT32).
 * @return Byteanzahl (1, 2, 4).
 */
inline UINT8 _iamByteCount_(UINT8 const _dataType) {
	return 1 << (_dataType - 1);
}

/**
 * Diese Methode gibt die kleinste Länge eines @c INT32 Arrays zurück, in dessen Speicherbereich ein @c INT8 Array mit der gegebenen Länge passen.
 * @param _byteCount Länge eines @c INT8 Arrays.
 * @return Länge des @c INT32 Arrays.
 */
inline UINT32 _iamByteAlign_(UINT32 _byteCount) {
	return (_byteCount + 3) >> 2;
}

/**
 * Diese Methode gibt die _index-te Zahl der gegebenen Zahlenfolge zurück.
 * @param _type Datengrößentyp zur Interpretation der Zahlenfolge (<code>[30:0][2:S]</code>).
 * @param _array Zahlenfolge.
 * @param _index Index.
 * @return _index-te Zahl.
 */
inline UINT32 _iamDataGet_(UINT8 const _type, PCVOID const _array, INT32 _index) {
	switch (_type) {
		case 0: // D0
			return (UINT32) _array;
		case 1: // D1
			return ((UINT8 const*) _array)[_index];
		case 2: // D2
			return ((UINT16 const*) _array)[_index];
		case 3: // D3
			return ((UINT32 const*) _array)[_index];
	}
	return 0;
}

/**
 * Diese Methode gibt die aus den gegebenen Größen zusammengesetzte Datentypkennung zurück.
 * @param _dataType Datengrößentyp (1 = INT8, 2 = INT16, 3 = INT32).
 * @param _sizeType Datenlängentyp (0 = statisch UINT32, 1 = dynamisch UINT8, 2 = dynamisch UINT16, 3 = dynamisch UINT32).
 * @return Datentypkennung (_dataType * 4 + _sizeType - 4).
 */
inline UINT8 _iamDataType_(UINT8 const _dataType, UINT8 const _sizeType) {
	return (_dataType << 2) + _sizeType - 4;
}

/**
 * Diese Methode gibt die _arrayIndex-te Zahlenfolge mit statischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von _arrayData.
 * @param _arraySize Größe einer Zahlenfolge (@c UINT32).
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @return _arrayIndex-te Zahlenfolge.
 */
template<typename PCDATA>
inline IAMArray _iamDataArray_S_(PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex) {
	UINT32 _length = (UINT32) _arraySize;
	PCDATA const _array = (PCDATA const) _arrayData;
	return IAMArray(_array + _arrayIndex * _length, _length);
}

/**
 * Diese Methode gibt die _arrayIndex-te Zahlenfolge mit dynamischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von _arrayData.
 * @tparam PCSIZE Datentyp zur Interpretation von _arraySize.
 * @param _arraySize Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @return _arrayIndex-te Zahlenfolge.
 */
template<typename PCDATA, typename PCSIZE>
inline IAMArray _iamDataArray_D_(PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex) {
	PCSIZE const _size = (PCSIZE const) _arraySize;
	UINT32 _offset = _size[_arrayIndex];
	UINT32 _length = _size[_arrayIndex + 1] - _offset;
	PCDATA const _array = (PCDATA const) _arrayData;
	return IAMArray(_array + _offset, _length);
}

/**
 * Diese Methode gibt die _arrayIndex-te Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param _type Datentypkennung zur Interpretation von _arraySize und _arrayData.
 * @param _arraySize Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @return _arrayIndex-te Zahlenfolge.
 */
inline IAMArray _iamDataArray_(UINT8 const _type, PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex) {
	switch (_type) {
		case 0: // D1, S0
			return _iamDataArray_S_<INT8 const*>(_arraySize, _arrayData, _arrayIndex);
		case 1: // D1, S1
			return _iamDataArray_D_<INT8 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex);
		case 2: // D1, S2
			return _iamDataArray_D_<INT8 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex);
		case 3: // D1, S3
			return _iamDataArray_D_<INT8 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex);
		case 4: // D2, S0
			return _iamDataArray_S_<INT16 const*>(_arraySize, _arrayData, _arrayIndex);
		case 5: // D2, S1
			return _iamDataArray_D_<INT16 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex);
		case 6: // D2, S2
			return _iamDataArray_D_<INT16 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex);
		case 7: // D2, S3
			return _iamDataArray_D_<INT16 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex);
		case 8: // D3, S0
			return _iamDataArray_S_<INT32 const*>(_arraySize, _arrayData, _arrayIndex);
		case 9: // D3, S1
			return _iamDataArray_D_<INT32 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex);
		case 10: // D3, S2
			return _iamDataArray_D_<INT32 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex);
		case 11: // D3, S3
			return _iamDataArray_D_<INT32 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex);
	}
	return IAMArray();
}

/**
 * Diese Methode gibt die _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge mit statischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von _arrayData.
 * @param _arraySize Größe einer Zahlenfolge (@c UINT32).
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @param _valueIndex Index der Zahl.
 * @return _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge.
 */
template<typename PCDATA>
inline INT32 _iamDataValue_S_(PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex, INT32 _valueIndex) {
	UINT32 _length = (UINT32) _arraySize;
	if ((UINT32) _valueIndex >= _length) return 0;
	PCDATA const _array = (PCDATA const) _arrayData;
	return _array[_arrayIndex * _length + _valueIndex];
}

/**
 * Diese Methode gibt die _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge mit dynamischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von _arrayData.
 * @tparam PCSIZE Datentyp zur Interpretation von _arraySize.
 * @param _arraySize Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @param _valueIndex Index der Zahl.
 * @return _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge.
 */
template<typename PCDATA, typename PCSIZE>
inline INT32 _iamDataValue_D_(PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex, INT32 _valueIndex) {
	PCSIZE const _size = (PCSIZE const) _arraySize;
	UINT32 _index = _size[_arrayIndex] + _valueIndex;
	if (_index >= _size[_arrayIndex + 1]) return 0;
	PCDATA const _array = (PCDATA const) _arrayData;
	return _array[_valueIndex];
}

/**
 * Diese Methode gibt die _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param _type Datentypkennung zur Interpretation von _arraySize und _arrayData.
 * @param _arraySize Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayData Speicherbereich mit den Daten der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @param _valueIndex Index der Zahl.
 * @return _valueIndex-te Zahl der _arrayIndex-ten Zahlenfolge.
 */
inline INT32 _iamDataValue_(UINT8 const _type, PCVOID const _arraySize, PCVOID const _arrayData, INT32 _arrayIndex, INT32 _valueIndex) {
	switch (_type) {
		case 0: // D1, S0
			return _iamDataValue_S_<INT8 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 1: // D1, S1
			return _iamDataValue_D_<INT8 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 2: // D1, S2
			return _iamDataValue_D_<INT8 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 3: // D1, S3
			return _iamDataValue_D_<INT8 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 4: // D2, S0
			return _iamDataValue_S_<INT16 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 5: // D2, S1
			return _iamDataValue_D_<INT16 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 6: // D2, S2
			return _iamDataValue_D_<INT16 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 7: // D2, S3
			return _iamDataValue_D_<INT16 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 8: // D3, S0
			return _iamDataValue_S_<INT32 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 9: // D3, S1
			return _iamDataValue_D_<INT32 const*, UINT8 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 10: // D3, S2
			return _iamDataValue_D_<INT32 const*, UINT16 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
		case 11: // D3, S3
			return _iamDataValue_D_<INT32 const*, UINT32 const*>(_arraySize, _arrayData, _arrayIndex, _valueIndex);
	}
	return 0;
}

/**
 * Diese Methode gibt die Länge der _arrayIndex-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @tparam PCSIZE Datentyp zur Interpretation von _arraySize.
 * @param _arraySize Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @return Länge der _arrayIndex-ten Zahlenfolge
 */
template<typename PCSIZE>
inline UINT32 _iamDataLength_D_(PCVOID const _arraySize, INT32 _arrayIndex) {
	PCSIZE const _size = (PCSIZE const) _arraySize;
	return _size[_arrayIndex + 1] - _size[_arrayIndex];
}

/**
 * Diese Methode gibt die Länge der _arrayIndex-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param _type Datenlängentyp zur Interpretation von _arraySize.
 * @param _arraySize Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param _arrayIndex Index der Zahlenfolge.
 * @return Länge der _arrayIndex-ten Zahlenfolge
 */
inline UINT32 _iamDataLength_(UINT8 const _type, PCVOID const _arraySize, INT32 _arrayIndex) {
	switch (_type) {
		case 0: // S0
			return (UINT32) _arraySize;
		case 1: // S1
			return _iamDataLength_D_<UINT8 const*>(_arraySize, _arrayIndex);
		case 2: // S2
			return _iamDataLength_D_<UINT16 const*>(_arraySize, _arrayIndex);
		case 3: // S3
			return _iamDataLength_D_<UINT32 const*>(_arraySize, _arrayIndex);
	}
	return 0;
}

/**
 * Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
 * @tparam PCSIZE Datentyp zur Interpretation von _arraySize.
 * @param _array Speicherbereich mit den Startpositionen der Zahlenfolgen.
 * @param _count Anzahl der Zahlenfolgen.
 * @throws IAMException Wenn die erste Zahl nicht 0 ist oder die Zahlen nicht monoton steigen.
 */
template<typename PCSIZE>
inline void _iamDataCheck_S_(PCVOID const _array, INT32 _count) {
	PCSIZE const _size = (PCSIZE const) _array;
	UINT32 value = _size[0];
	if (value) throw IAMException(IAMException::INVALID_OFFSET);
	for (INT32 i = 0; i <= _count; ++i) {
		UINT32 value2 = _size[i];
		if (value > value2) throw IAMException(IAMException::INVALID_OFFSET);
		value = value2;
	}
}

/**
 * Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
 * @param _type Datenlängentyp zur Interpretation von _arraySize.
 * @param _array Speicherbereich mit den Startpositionen der Zahlenfolgen.
 * @param _count Anzahl der Zahlenfolgen.
 * @throws IAMException Wenn die erste Zahl nicht 0 ist oder die Zahlen nicht monoton steigen.
 */
inline void _iamDataCheck_(UINT8 const _type, PCVOID const _array, INT32 _count) {
	switch (_type) {
		case 1: // S1
			_iamDataCheck_S_<UINT8 const*>(_array, _count);
			return;
		case 2: // S2
			_iamDataCheck_S_<UINT16 const*>(_array, _count);
			return;
		case 3: // S3
			_iamDataCheck_S_<UINT32 const*>(_array, _count);
			return;
	}
}

/**
 * Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück
 * @tparam PCDATA Datentyp zur Interpretation von _array.
 * @param _array Zahlenfolge.
 * @param _length Länge der Zahlenfolge.
 * @return Streuwert.
 */
template<typename PCDATA>
inline INT32 _iamArrayHash_(PCVOID const _array, INT32 _length) {
	PCDATA _data = (PCDATA) _array;
	INT32 _result = 0x811C9DC5;
	for (INT32 i = 0; i < _length; i++)
		_result = (_result * 0x01000193) ^ (INT32) _data[i];
	return _result;
}

/**
 * Diese Methode gibt nur dann @c true zurück, wenn die gegebenen Zahlenfolgen gleich sind.
 * @tparam PCDATA1 Datentyp zur Interpretation von _array1.
 * @tparam PCDATA2 Datentyp zur Interpretation von _array2.
 * @param _array1 Erste Zahlenfolge.
 * @param _array2 Zweite Zahlenfolge.
 * @param _length Länge der Zahlenfolgen.
 * @return @c true, wenn die Zahlenfolgen gleich sind.
 */
template<typename PCDATA1, typename PCDATA2>
inline bool _iamArrayEquals_(PCVOID const _array1, PCVOID const _array2, UINT32 _length) {
	PCDATA1 const _data1 = (PCDATA1 const) _array1;
	PCDATA2 const _data2 = (PCDATA2 const) _array2;
	for (UINT32 i = 0; i < _length; i++)
		if (_data1[i] != _data2[i]) return false;
	return true;
}

/**
 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als 0 zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw. größer als die der zweiten Zahlenfolge ist.
 * @tparam PCDATA1 Datentyp zur Interpretation von _array1.
 * @tparam PCDATA2 Datentyp zur Interpretation von _array2.
 * @param _array1 Erste Zahlenfolge.
 * @param _array2 Zweite Zahlenfolge.
 * @param _length1 Länge der ersten Zahlenfolge.
 * @param _length2 Länge der zweiten Zahlenfolge.
 * @return Vergleichswert der Ordnungen.
 */
template<typename PCDATA1, typename PCDATA2>
inline INT32 _iamArrayCompare_(PCVOID const _array1, PCVOID const _array2, UINT32 _length1, UINT32 _length2) {
	PCDATA1 const _data1 = (PCDATA1 const) _array1;
	PCDATA2 const _data2 = (PCDATA2 const) _array2;
	UINT32 _length = _length1 < _length2 ? _length1 : _length2;
	for (INT32 i = 0; i < _length; i++) {
		INT32 _result = _data1[i] - _data2[i];
		if (_result != 0) return _result;
	}
	return _length1 - _length2;
}

/** Diese Methode initialisiert ein @c IAMArray mit den gegebenen Daten.
 * @tparam PCDATA Datentyp zur Interpretation von _array.
 * @param _data Daten der Zahlenfolge.
 * @param _size Länge der Zahlenfolge.
 * @param _type Datentyp.
 * @param _array Zeiger auf den Speicherbereich mit Zahlen.
 * @param _length Anzahl der Zahlen. */
template<typename PCDATA>
inline void _iamArrayNew_(PCVOID & _data, UINT32 & _size, UINT8 const _type, PCDATA const _array, INT32 _length) {
	if (!_array || _length <= 0) {
		_size = 0;
		_data = 0;
	} else {
		UINT32 _count = _length <= 0x3FFFFFFF ? _length : 0x3FFFFFFF;
		if (_type) {
			_data = _array;
			_size = (_count << 2) | _type;
		} else {
			INT32 * _copy = new INT32[_count + 1];
			_copy[0] = 1;
			_copy++;
			for (INT32 i = 0; i < _count; i++)
				_copy[i] = _array[i];
			_data = _copy;
			_size = _count << 2;
		}
	}
}

/** Diese Methode gibt die Zahlenfolge zurück, der im kopierenden Konstrukteur sowie im Zuweisungsoperator von @c IAMArray verwendet wird.
 * @param _data Daten der Zahlenfolge.
 * @param _size Länge der Zahlenfolge.
 * @return Zahlenfolge. */
inline PCVOID _iamArrayDataUse_(PCVOID const _data, UINT32 _size) {
	if ((_size & 3) || !_data) return _data;
	INT32 const* _array = (INT32 const*) _data - 1;
	RCCounter& counter = *(RCCounter*) _array;
	counter.inc();
	return _data;
}

/** Diese Methode gibt den Speicher der gegebenen Zahlenfolge frei, wenn diese kopiert wurde.
 * @param _data Daten der Zahlenfolge.
 * @param _size Länge der Zahlenfolge. */
inline void _iamArrayDataFree_(PCVOID const _data, INT32 _size) {
	if ((_size & 3) || !_data) return;
	INT32 const* _array = (INT32 const*) _data - 1;
	RCCounter& counter = *(RCCounter*) _array;
	if (counter.dec() != 0) return;
	delete[] _array;
}

/**
 * Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c IAMIndex.
 * @param _fileData Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist.
 */
inline INT32 const* _iamIndexChecked_(MMFArray const& _fileData) {
	if (_fileData.size() & 3) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) _fileData.data();
}

/**
 * Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c IAMIndex.
 * @param _heapData Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist.
 */
inline INT32 const* _iamIndexChecked_(IAMArray const& _heapData) {
	if (_heapData.mode() != 4) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) _heapData.data();
}

// class IAMArray

IAMArray const IAMArray::EMPTY = IAMArray();

IAMArray::IAMArray()
	: _size_(0), _data_(0) {
}

IAMArray::IAMArray(IAMArray const& _source) {
	_data_ = _iamArrayDataUse_(_source._data_, _size_ = _source._size_);
}

IAMArray::IAMArray(IAMArray const& _source, bool _copy) {
	if (_copy) {
		UINT32 _size = _source._size_;
		PCVOID const _data = _source._data_;
		UINT32 _length = _size >> 2;
		switch (_size & 3) {
			case 0:
				_iamArrayNew_<INT32 const*>(_data_, _size_, 0, (INT32 const*) _data, _length);
				break;
			case 1:
				_iamArrayNew_<INT8 const*>(_data_, _size_, 0, (INT8 const*) _data, _length);
				break;
			case 2:
				_iamArrayNew_<INT16 const*>(_data_, _size_, 0, (INT16 const*) _data, _length);
				break;
			case 3:
				_iamArrayNew_<INT32 const*>(_data_, _size_, 0, (INT32 const*) _data, _length);
				break;
		}
	} else {
		_data_ = _iamArrayDataUse_(_source._data_, _size_ = _source._size_);
	}
}

IAMArray::IAMArray(INT8 const* _array, INT32 _length) {
	_iamArrayNew_<INT8 const*>(_data_, _size_, 1, _array, _length);
}

IAMArray::IAMArray(INT8 const* _array, INT32 _length, bool _copy) {
	_iamArrayNew_<INT8 const*>(_data_, _size_, _copy ? 0 : 1, _array, _length);
}

IAMArray::IAMArray(INT16 const* _array, INT32 _length) {
	_iamArrayNew_<INT16 const*>(_data_, _size_, 2, _array, _length);
}

IAMArray::IAMArray(INT16 const* _array, INT32 _length, bool _copy) {
	_iamArrayNew_<INT16 const*>(_data_, _size_, _copy ? 0 : 2, _array, _length);
}

IAMArray::IAMArray(INT32 const* _array, INT32 _length) {
	_iamArrayNew_<INT32 const*>(_data_, _size_, 3, _array, _length);
}

IAMArray::IAMArray(INT32 const* _array, INT32 _length, bool _copy) {
	_iamArrayNew_<INT32 const*>(_data_, _size_, _copy ? 0 : 3, _array, _length);
}

IAMArray::~IAMArray() {
	_iamArrayDataFree_(_data_, _size_);
}

INT32 IAMArray::get(INT32 _index) const {
	UINT32 _size = _size_;
	if (((UINT32) _index) >= (_size >> 2)) return 0;
	PCVOID const _data = _data_;
	switch (_size & 3) {
		case 0:
			return ((INT32 const*) _data)[_index];
		case 1:
			return ((INT8 const*) _data)[_index];
		case 2:
			return ((INT16 const*) _data)[_index];
		case 3:
			return ((INT32 const*) _data)[_index];
	}
	return 0;
}

INT32 IAMArray::length() const {
	INT32 _result = _size_ >> 2;
	return _result;
}

PCVOID IAMArray::data() const {
	return _data_;
}

UINT8 IAMArray::mode() const {
	UINT8 _result = (0x4214 >> ((_size_ & 3) << 2)) & 7;
	return _result;
}

INT32 IAMArray::hash() const {
	UINT32 _size = _size_;
	PCVOID const _data = _data_;
	UINT32 _length = _size >> 2;
	switch (_size & 3) {
		case 0:
			return _iamArrayHash_<INT32 const*>(_data, _length);
		case 1:
			return _iamArrayHash_<INT8 const*>(_data, _length);
		case 2:
			return _iamArrayHash_<INT16 const*>(_data, _length);
		case 3:
			return _iamArrayHash_<INT32 const*>(_data, _length);
	}
	return 0;
}

bool IAMArray::equals(IAMArray const& _value) const {
	UINT32 _size1 = _size_, _size2 = _value._size_, _length = _size1 >> 2;
	if (_length != (_size2 >> 2)) return false;
	PCVOID const _data1 = _data_, _data2 = _value._data_;
	switch (((_size1 & 3) << 2) | (_size2 & 3)) {
		case 0:
			return _iamArrayEquals_<INT32 const*, INT32 const*>(_data1, _data2, _length);
		case 1:
			return _iamArrayEquals_<INT32 const*, INT8 const*>(_data1, _data2, _length);
		case 2:
			return _iamArrayEquals_<INT32 const*, INT16 const*>(_data1, _data2, _length);
		case 3:
			return _iamArrayEquals_<INT32 const*, INT32 const*>(_data1, _data2, _length);
		case 4:
			return _iamArrayEquals_<INT8 const*, INT32 const*>(_data1, _data2, _length);
		case 5:
			return _iamArrayEquals_<INT8 const*, INT8 const*>(_data1, _data2, _length);
		case 6:
			return _iamArrayEquals_<INT8 const*, INT16 const*>(_data1, _data2, _length);
		case 7:
			return _iamArrayEquals_<INT8 const*, INT32 const*>(_data1, _data2, _length);
		case 8:
			return _iamArrayEquals_<INT16 const*, INT32 const*>(_data1, _data2, _length);
		case 9:
			return _iamArrayEquals_<INT16 const*, INT8 const*>(_data1, _data2, _length);
		case 10:
			return _iamArrayEquals_<INT16 const*, INT16 const*>(_data1, _data2, _length);
		case 11:
			return _iamArrayEquals_<INT16 const*, INT32 const*>(_data1, _data2, _length);
		case 12:
			return _iamArrayEquals_<INT32 const*, INT32 const*>(_data1, _data2, _length);
		case 13:
			return _iamArrayEquals_<INT32 const*, INT8 const*>(_data1, _data2, _length);
		case 14:
			return _iamArrayEquals_<INT32 const*, INT16 const*>(_data1, _data2, _length);
		case 15:
			return _iamArrayEquals_<INT32 const*, INT32 const*>(_data1, _data2, _length);
	}
	return false;
}

INT32 IAMArray::compare(IAMArray const& _value) const {
	UINT32 _size1 = _size_, _size2 = _value._size_;
	PCVOID const _data1 = _data_, _data2 = _value._data_;
	UINT32 _length1 = _size1 >> 2, _length2 = _size2 >> 2;
	switch (((_size1 & 3) << 2) | (_size2 & 3)) {
		case 0:
			return _iamArrayCompare_<INT32 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 1:
			return _iamArrayCompare_<INT32 const*, INT8 const*>(_data1, _data2, _length1, _length2);
		case 2:
			return _iamArrayCompare_<INT32 const*, INT16 const*>(_data1, _data2, _length1, _length2);
		case 3:
			return _iamArrayCompare_<INT32 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 4:
			return _iamArrayCompare_<INT8 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 5:
			return _iamArrayCompare_<INT8 const*, INT8 const*>(_data1, _data2, _length1, _length2);
		case 6:
			return _iamArrayCompare_<INT8 const*, INT16 const*>(_data1, _data2, _length1, _length2);
		case 7:
			return _iamArrayCompare_<INT8 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 8:
			return _iamArrayCompare_<INT16 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 9:
			return _iamArrayCompare_<INT16 const*, INT8 const*>(_data1, _data2, _length1, _length2);
		case 10:
			return _iamArrayCompare_<INT16 const*, INT16 const*>(_data1, _data2, _length1, _length2);
		case 11:
			return _iamArrayCompare_<INT16 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 12:
			return _iamArrayCompare_<INT32 const*, INT32 const*>(_data1, _data2, _length1, _length2);
		case 13:
			return _iamArrayCompare_<INT32 const*, INT8 const*>(_data1, _data2, _length1, _length2);
		case 14:
			return _iamArrayCompare_<INT32 const*, INT16 const*>(_data1, _data2, _length1, _length2);
		case 15:
			return _iamArrayCompare_<INT32 const*, INT32 const*>(_data1, _data2, _length1, _length2);
	}
	return 0;
}

IAMArray IAMArray::section(INT32 _offset, INT32 _length) const {
	UINT32 _size = _size_;
	if (_offset < 0 || _length <= 0 || (UINT32) (_offset + _length) > (_size >> 2)) return IAMArray();
	switch (_size & 3) {
		case 0:
			return IAMArray(((INT32 const*) _data_) + _offset, _length, true);
		case 1:
			return IAMArray(((INT8 const*) _data_) + _offset, _length);
		case 2:
			return IAMArray(((INT16 const*) _data_) + _offset, _length);
		case 3:
			return IAMArray(((INT32 const*) _data_) + _offset, _length);
	}
	return IAMArray();
}

INT32 IAMArray::operator[](INT32 _index) const {
	return get(_index);
}

IAMArray & IAMArray::operator=(IAMArray const& _source) {
	_iamArrayDataFree_(_data_, _size_);
	_data_ = _iamArrayDataUse_(_source._data_, _size_ = _source._size_);
	return *this;
}

// class IAMEntry

IAMEntry::IAMEntry()
	: _key_(), _value_() {
}

IAMEntry::IAMEntry(const IAMEntry & _source)
	: _key_(_source._key_), _value_(_source._value_) {
}

IAMEntry::IAMEntry(IAMArray const& _key, IAMArray const& _value)
	: _key_(_key), _value_(_value) {
}

IAMArray const IAMEntry::key() const {
	return _key_;
}

INT32 IAMEntry::key(INT32 _index) const {
	return _key_.get(_index);
}

INT32 IAMEntry::keyLength() const {
	return _key_.length();
}

IAMArray const IAMEntry::value() const {
	return _value_;
}

INT32 IAMEntry::value(INT32 _index) const {
	return _value_.get(_index);
}

INT32 IAMEntry::valueLength() const {
	return _value_.length();
}

// class IAMListing

IAMListing::OBJECT::OBJECT()
	: _type_(0), _itemSize_(0), _itemData_(0), _itemCount_(0) {
}

IAMListing::OBJECT::OBJECT(INT32 const* _array, INT32 _length) {
	if (!_array || _length < 3) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 _offset = 0;
	UINT32 _header = _array[_offset];
	_offset++;
	if ((_header & 0xFFFFFFF0) != 0xF00D2000) throw IAMException(IAMException::INVALID_HEADER);

	UINT8 const _itemDataType = (_header >> 2) & 3;
	UINT8 const _itemSizeType = (_header >> 0) & 3;
	if (!_itemDataType) throw IAMException(IAMException::INVALID_HEADER);

	INT32 _itemCount = _array[_offset];
	_offset++;
	if (_itemCount > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID _itemSize;
	UINT32 _itemValue;
	if (_itemSizeType) {

		_itemSize = _array + _offset;
		_itemValue = _iamByteAlign_((_itemCount + 1) * _iamByteCount_(_itemSizeType));
		_offset += _itemValue;
		if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

//		_iamDataCheck_(_itemSizeType, _itemSize, _itemCount);
		_itemValue = _iamDataGet_(_itemSizeType, _itemSize, _itemCount);

	} else {

		_itemValue = _array[_offset];
		_offset++;
		if (_itemValue > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

		_itemSize = (PCVOID) _itemValue;
		_itemValue = _itemCount * _itemValue;

	}

	PCVOID _itemData = _array + _offset;
	_itemValue = _iamByteAlign_(_itemValue * _iamByteCount_(_itemDataType));
	_offset += _itemValue;
	if (_length != _offset) throw IAMException(IAMException::INVALID_LENGTH);

	_type_ = _iamDataType_(_itemDataType, _itemSizeType);
	_itemSize_ = _itemSize;
	_itemData_ = _itemData;
	_itemCount_ = _itemCount;
}

/* Dieses Feld speichert die leeren Nutzdaten eines @c IAMListing. */
RCPointer<IAMListing::OBJECT> _IAM_LISTING_OBJECT_(new IAMListing::OBJECT());

IAMListing::IAMListing()
	: _object_(_IAM_LISTING_OBJECT_) {
}

IAMListing::IAMListing(INT32 const* _array, INT32 _length)
	: _object_(new OBJECT(_array, _length)) {
}

void IAMListing::check() const {
	OBJECT& _this = *_object_;
	_iamDataCheck_(_this._type_ & 3, _this._itemSize_, _this._itemCount_);
}

IAMArray IAMListing::item(INT32 _itemIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _itemIndex >= _this._itemCount_) return IAMArray();
	return _iamDataArray_(_this._type_, _this._itemSize_, _this._itemData_, _itemIndex);
}

INT32 IAMListing::item(INT32 _itemIndex, INT32 _index) const {
	OBJECT& _this = *_object_;
	if (_index < 0 || (UINT32) _itemIndex >= _this._itemCount_) return 0;
	return _iamDataValue_(_this._type_, _this._itemSize_, _this._itemData_, _itemIndex, _index);
}

INT32 IAMListing::itemLength(INT32 _itemIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _itemIndex >= _this._itemCount_) return 0;
	return _iamDataLength_(_this._type_ & 3, _this._itemSize_, _itemIndex);
}

INT32 IAMListing::itemCount() const {
	OBJECT& _this = *_object_;
	return _this._itemCount_;
}

INT32 IAMListing::find(IAMArray const _item) const {
	OBJECT& _this = *_object_;
	UINT32 const _type = _this._type_;
	PCVOID const _itemSize = _this._itemSize_;
	PCVOID const _itemData = _this._itemData_;
	UINT32 const _itemCount = _this._itemCount_;
	for (UINT32 i = 0; i < _itemCount; i++) {
		IAMArray _array = _iamDataArray_(_type, _itemSize, _itemData, i);
		if (_array.equals(_item)) return (INT32) i;
	}
	return -1;
}

IAMArray IAMListing::operator[](INT32 _itemIndex) const {
	return item(_itemIndex);
}

INT32 IAMListing::operator[](IAMArray const _item) const {
	return find(_item);
}

// class IAMMapping

/** Diese Methode gibt den Index des Schlüssels mit statischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, streuwertbasierter Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von _keyData.
 * @tparam PCRANGE Datentyp zur Interpretation von _rangeData.
 * @param _keySize Größe eines Schlüssels (@c UINT32).
 * @param _keyData Speicherbereich mit den Daten der Schlüssel.
 * @param _rangeMask Bitmaske für den Streuwert.
 * @param _rangeData Speicherbereich mit den Längen der Schlüsselbereiche.
 * @param _find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCRANGE>
INT32 _iamFind_HS_(PCVOID _keySize, PCVOID _keyData, UINT32 _rangeMask, PCVOID _rangeData, IAMArray const _find) {
	PCDATA const _data = (PCDATA) _keyData;
	UINT32 const _size = (UINT32) _keySize;
	PCRANGE const _range = (PCRANGE) _rangeData;
	if (_size != _find.length()) return -1;
	UINT32 _index = _find.hash() & _rangeMask;
	for (INT32 _l = _range[_index], _r = _range[_index + 1]; _l < _r; _l++) {
		UINT32 const _offset = _size * _l;
		UINT32 const _length = _size;
		IAMArray const _key(_data + _offset, _length);
		if (_key.equals(_find)) return _l;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit dynamischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, streuwertbasierter Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von _keyData.
 * @tparam PCSIZE Datentyp zur Interpretation von _keySize.
 * @tparam PCRANGE Datentyp zur Interpretation von _rangeData.
 * @param _keySize Speicherbereich mit den Längen der Schlüssel.
 * @param _keyData Speicherbereich mit den Daten der Schlüssel.
 * @param _rangeMask Bitmaske für den Streuwert.
 * @param _rangeData Speicherbereich mit den Längen der Schlüsselbereiche.
 * @param _find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCSIZE, typename PCRANGE>
INT32 _iamFind_HD_(PCVOID _keySize, PCVOID _keyData, UINT32 _rangeMask, PCVOID _rangeData, IAMArray const _find) {
	PCDATA const _data = (PCDATA) _keyData;
	PCSIZE const _size = (PCSIZE) _keySize;
	PCRANGE const _range = (PCRANGE) _rangeData;
	UINT32 const _index = _find.hash() & _rangeMask;
	for (INT32 _l = _range[_index], _r = _range[_index + 1]; _l < _r; _l++) {
		UINT32 const _offset = _size[_l];
		UINT32 const _length = _size[_l + 1] - _offset;
		IAMArray const _key(_data + _offset, _length);
		if (_key.equals(_find)) return _l;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit statischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, binärer Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von _keyData.
 * @param _keySize Größe eines Schlüssels (@c UINT32).
 * @param _keyData Speicherbereich mit den Daten der Schlüssel.
 * @param _keyCount Anzahl der Schlüssel.
 * @param _find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA>
INT32 _iamFind_BS_(PCVOID _keySize, PCVOID _keyData, UINT32 _keyCount, IAMArray const _find) {
	PCDATA const _data = (PCDATA) _keyData;
	UINT32 const _size = (UINT32) _keySize;
	if (_size != _find.length()) return -1;
	for (UINT32 _l = 0, _r = _keyCount; _l < _r;) {
		UINT32 const _c = (_l + _r) >> 1;
		UINT32 const _offset = _size * _c;
		UINT32 const _length = _size;
		IAMArray const _key(_data + _offset, _length);
		INT32 const _value = _key.compare(_find);
		if (_value < 0) _r = _c;
		else if (_value > 0) _l = _c + 1;
		else return _c;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit dynamischer Größe zurück, der äquivalent zur gegebenen Zahlenfolge ist.
 * Bei erfolgloser, binärer Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von _keyData.
 * @tparam PCSIZE Datentyp zur Interpretation von _keySize.
 * @param _keySize Speicherbereich mit den Längen der Schlüssel.
 * @param _keyData Speicherbereich mit den Daten der Schlüssel.
 * @param _keyCount Anzahl der Schlüssel.
 * @param _find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCSIZE>
INT32 _iamFind_BD_(PCVOID _keySize, PCVOID _keyData, UINT32 _keyCount, IAMArray const _find) {
	PCDATA const _data = (PCDATA) _keyData;
	PCSIZE const _size = (PCSIZE) _keySize;
	for (UINT32 _l = 0, _r = _keyCount; _l < _r;) {
		UINT32 const _c = (_l + _r) >> 1;
		UINT32 const _offset = _size[_c];
		UINT32 const _length = _size[_c + 1] - _offset;
		IAMArray const _key(_data + _offset, _length);
		INT32 const _value = _key.compare(_find);
		if (_value < 0) _r = _c;
		else if (_value > 0) _l = _c + 1;
		else return _c;
	}
	return -1;
}

IAMMapping::OBJECT::OBJECT()
	: _type_(0), _keySize_(0), _keyData_(0), _valueSize_(0), _valueData_(0), _rangeMask_(0), _rangeSize_(0), _entryCount_(0) {
}

IAMMapping::OBJECT::OBJECT(INT32 const* _array, INT32 _length) {
	if (!_array || _length < 4) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 _offset = 0;
	UINT32 _header = _array[_offset];
	_offset++;
	if ((_header & 0xFFFFFC00) != 0xF00D1000) throw IAMException(IAMException::INVALID_HEADER);

	UINT8 const _keyDataType = (_header >> 8) & 3;
	UINT8 const _keySizeType = (_header >> 6) & 3;
	UINT8 const _rangeSizeType = (_header >> 4) & 3;
	UINT8 const _valueDataType = (_header >> 2) & 3;
	UINT8 const _valueSizeType = (_header >> 0) & 3;
	if (!_keyDataType || !_valueDataType) throw IAMException(IAMException::INVALID_HEADER);

	UINT32 _entryCount = _array[_offset];
	_offset++;
	if (_entryCount > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	UINT32 _rangeMask;
	PCVOID _rangeSize;
	UINT32 _rangeValue;
	if (_rangeSizeType) {

		if (_length <= _offset) throw IAMException(IAMException::INVALID_LENGTH);

		_rangeMask = _array[_offset];
		_offset++;
		if ((_rangeMask < 1) || (_rangeMask > 0x1FFFFFFF) || ((_rangeMask + 1) & _rangeMask)) throw IAMException(IAMException::INVALID_VALUE);

		_rangeSize = _array + _offset;
		_rangeValue = _iamByteAlign_((_rangeMask + 2) * _iamByteCount_(_rangeSizeType));
		_offset += _rangeValue;
		if (_length <= _offset) throw IAMException(IAMException::INVALID_LENGTH);

//		_iamDataCheck_(_rangeSizeType, _rangeSize, _rangeMask + 1);
		_rangeValue = _iamDataGet_(_rangeSizeType, _rangeSize, _rangeMask + 1);
		if (_rangeValue != _entryCount) throw new IAMException(IAMException::INVALID_OFFSET);

	} else {

		_rangeMask = 0;
		_rangeSize = 0;

	}

	if (_length <= _offset) throw IAMException(IAMException::INVALID_LENGTH);

	PCVOID _keySize;
	UINT32 _keyValue;
	if (_keySizeType) {

		_keySize = _array + _offset;
		_keyValue = _iamByteAlign_((_entryCount + 1) * _iamByteCount_(_keySizeType));
		_offset += _keyValue;
		if (_length <= _offset) throw IAMException(IAMException::INVALID_LENGTH);

//		_iamDataCheck_(_keySizeType, _keySize, _entryCount);
		_keyValue = _iamDataGet_(_keySizeType, _keySize, _entryCount);

	} else {

		_keyValue = _array[_offset];
		_offset++;
		if (_length <= _offset) throw IAMException(IAMException::INVALID_LENGTH);

		_keySize = (PCVOID) _keyValue;
		_keyValue = _entryCount * _keyValue;

	}
	if (_keyValue > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID _keyData = _array + _offset;
	_keyValue = _iamByteAlign_(_keyValue * _iamByteCount_(_keyDataType));
	_offset += _keyValue;
	if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

	PCVOID _valueSize;
	UINT32 _valueValue;
	if (_valueSizeType) {

		_valueSize = _array + _offset;
		_valueValue = _iamByteAlign_((_entryCount + 1) * _iamByteCount_(_valueSizeType));
		_offset += _valueValue;
		if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

//		_iamDataCheck_(_valueSizeType, _valueSize, _entryCount);
		_valueValue = _iamDataGet_(_valueSizeType, _valueSize, _entryCount);

	} else {

		_valueValue = _array[_offset];
		_offset++;
		if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

		_valueSize = (PCVOID) _valueValue;
		_valueValue = _entryCount * _valueValue;

	}
	if (_valueValue > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID _valueData = _array + _offset;
	_valueValue = _iamByteAlign_(_valueValue * _iamByteCount_(_valueDataType));
	_offset += _valueValue;
	if (_length != _offset) throw IAMException(IAMException::INVALID_LENGTH);

	_type_ = (_iamDataType_(_keyDataType, _keySizeType) << 6) | (_rangeSizeType << 4) | _iamDataType_(_valueDataType, _valueSizeType);
	_keySize_ = _keySize;
	_keyData_ = _keyData;
	_valueSize_ = _valueSize;
	_valueData_ = _valueData;
	_rangeMask_ = _rangeMask;
	_rangeSize_ = _rangeSize;
	_entryCount_ = _entryCount;
}

RCPointer<IAMMapping::OBJECT> _IAM_MAPPING_OBJECT_(new IAMMapping::OBJECT());

IAMMapping::IAMMapping()
	: _object_(_IAM_MAPPING_OBJECT_) {
}

IAMMapping::IAMMapping(INT32 const* _array, INT32 _length)
	: _object_(new OBJECT(_array, _length)) {
}

void IAMMapping::check() const {
	OBJECT& _this = *_object_;
	_iamDataCheck_((_this._type_ >> 4) & 3, _this._rangeSize_, _this._rangeMask_ + 1);
	_iamDataCheck_((_this._type_ >> 6) & 3, _this._keySize_, _this._entryCount_);
	_iamDataCheck_(_this._type_ & 3, _this._valueSize_, _this._entryCount_);
}

IAMArray IAMMapping::key(INT32 _entryIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _entryIndex >= _this._entryCount_) return IAMArray();
	IAMArray _result = _iamDataArray_(_this._type_ >> 6, _this._keySize_, _this._keyData_, _entryIndex);
	return _result;
}

INT32 IAMMapping::key(INT32 _entryIndex, INT32 _index) const {
	OBJECT& _this = *_object_;
	if (_index < 0 || (UINT32) _entryIndex >= _this._entryCount_) return 0;
	INT32 _result = _iamDataValue_(_this._type_ >> 6, _this._keySize_, _this._keyData_, _entryIndex, _index);
	return _result;
}

INT32 IAMMapping::keyLength(INT32 _entryIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _entryIndex >= _this._entryCount_) return 0;
	INT32 _result = _iamDataLength_((_this._type_ >> 6) & 3, _this._keySize_, _entryIndex);
	return _result;
}

IAMArray IAMMapping::value(INT32 _entryIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _entryIndex >= _this._entryCount_) return IAMArray();
	IAMArray _result = _iamDataArray_(_this._type_ & 15, _this._valueSize_, _this._valueData_, _entryIndex);
	return _result;
}

INT32 IAMMapping::value(INT32 _entryIndex, INT32 _index) const {
	OBJECT& _this = *_object_;
	if (_index < 0 || (UINT32) _entryIndex >= _this._entryCount_) return 0;
	INT32 _result = _iamDataValue_(_this._type_ & 15, _this._valueSize_, _this._valueData_, _entryIndex, _index);
	return _result;
}

INT32 IAMMapping::valueLength(INT32 _entryIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _entryIndex >= _this._entryCount_) return 0;
	return _iamDataLength_(_this._type_ & 3, _this._valueSize_, _entryIndex);
}

IAMEntry IAMMapping::entry(INT32 _entryIndex) const {
	OBJECT& _this = *_object_;
	if ((UINT32) _entryIndex >= _this._entryCount_) return IAMEntry();
	IAMArray _key = _iamDataArray_(_this._type_ >> 6, _this._keySize_, _this._keyData_, _entryIndex);
	IAMArray _value = _iamDataArray_(_this._type_ & 15, _this._valueSize_, _this._valueData_, _entryIndex);
	IAMEntry _result = IAMEntry(_key, _value);
	return _result;
}

INT32 IAMMapping::entryCount() const {
	OBJECT& _this = *_object_;
	return _this._entryCount_;
}

INT32 IAMMapping::find(IAMArray const _key) const {
	OBJECT& _this = *_object_;
	switch (_this._type_ >> 4) {
		case 0: // D1, S0, R0
			return _iamFind_BS_<INT8 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 1: // D1, S0, R1
			return _iamFind_HS_<INT8 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 2: // D1, S0, R2
			return _iamFind_HS_<INT8 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 3: // D1, S0, R3
			return _iamFind_HS_<INT8 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 4: // D1, S1, R0
			return _iamFind_BD_<INT8 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 5: // D1, S1, R1
			return _iamFind_HD_<INT8 const*, UINT8 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 6: // D1, S1, R2
			return _iamFind_HD_<INT8 const*, UINT8 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 7: // D1, S1, R3
			return _iamFind_HD_<INT8 const*, UINT8 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 8: // D1, S2, R0
			return _iamFind_BD_<INT8 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 9: // D1, S2, R1
			return _iamFind_HD_<INT8 const*, UINT16 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 10: // D1, S2, R2
			return _iamFind_HD_<INT8 const*, UINT16 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 11: // D1, S2, R3
			return _iamFind_HD_<INT8 const*, UINT16 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 12: // D1, S3, R0
			return _iamFind_BD_<INT8 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 13: // D1, S3, R1
			return _iamFind_HD_<INT8 const*, UINT32 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 14: // D1, S3, R2
			return _iamFind_HD_<INT8 const*, UINT32 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 15: // D1, S3, R3
			return _iamFind_HD_<INT8 const*, UINT32 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 16: // D2, S0, R0
			return _iamFind_BS_<INT16 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 17: // D2, S0, R1
			return _iamFind_HS_<INT16 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 18: // D2, S0, R2
			return _iamFind_HS_<INT16 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 19: // D2, S0, R3
			return _iamFind_HS_<INT16 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 20: // D2, S1, R0
			return _iamFind_BD_<INT16 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 21: // D2, S1, R1
			return _iamFind_HD_<INT16 const*, UINT8 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 22: // D2, S1, R2
			return _iamFind_HD_<INT16 const*, UINT8 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 23: // D2, S1, R3
			return _iamFind_HD_<INT16 const*, UINT8 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 24: // D2, S2, R0
			return _iamFind_BD_<INT16 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 25: // D2, S2, R1
			return _iamFind_HD_<INT16 const*, UINT16 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 26: // D2, S2, R2
			return _iamFind_HD_<INT16 const*, UINT16 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 27: // D2, S2, R3
			return _iamFind_HD_<INT16 const*, UINT16 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 28: // D2, S3, R0
			return _iamFind_BD_<INT16 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 29: // D2, S3, R1
			return _iamFind_HD_<INT16 const*, UINT32 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 30: // D2, S3, R2
			return _iamFind_HD_<INT16 const*, UINT32 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 31: // D2, S3, R3
			return _iamFind_HD_<INT16 const*, UINT32 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 32: // D3, S0, R0
			return _iamFind_BS_<INT32 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 33: // D3, S0, R1
			return _iamFind_HS_<INT32 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 34: // D3, S0, R2
			return _iamFind_HS_<INT32 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 35: // D3, S0, R3
			return _iamFind_HS_<INT32 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 36: // D3, S1, R0
			return _iamFind_BD_<INT32 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 37: // D3, S1, R1
			return _iamFind_HD_<INT32 const*, UINT8 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 38: // D3, S1, R2
			return _iamFind_HD_<INT32 const*, UINT8 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 39: // D3, S1, R3
			return _iamFind_HD_<INT32 const*, UINT8 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 40: // D3, S2, R0
			return _iamFind_BD_<INT32 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 41: // D3, S2, R1
			return _iamFind_HD_<INT32 const*, UINT16 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 42: // D3, S2, R2
			return _iamFind_HD_<INT32 const*, UINT16 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 43: // D3, S2, R3
			return _iamFind_HD_<INT32 const*, UINT16 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 44: // D3, S3, R0
			return _iamFind_BD_<INT32 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._entryCount_, _key);
		case 45: // D3, S3, R1
			return _iamFind_HD_<INT32 const*, UINT32 const*, UINT8 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 46: // D3, S3, R2
			return _iamFind_HD_<INT32 const*, UINT32 const*, UINT16 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
		case 47: // D3, S3, R3
			return _iamFind_HD_<INT32 const*, UINT32 const*, UINT32 const*>(_this._keySize_, _this._keyData_, _this._rangeMask_, _this._rangeSize_, _key);
	}
	return -1;
}

IAMEntry IAMMapping::operator[](INT32 _entryIndex) const {
	return entry(_entryIndex);
}

INT32 IAMMapping::operator[](IAMArray const _key) const {
	return find(_key);
}

// class IAMIndex

IAMIndex::OBJECT::OBJECT()
	: _listingArray_(0), _listingCount_(0), _mappingArray_(0), _mappingCount_(0) {
}

IAMIndex::OBJECT::OBJECT(INT32 const* _array, INT32 _length) {
	if (!_array || _length < 5) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 _offset = 0;
	UINT32 _header = _array[_offset];
	_offset++;
	if (_header != 0xF00DBA5E) throw IAMException(IAMException::INVALID_HEADER);

	UINT32 _mappingCount = _array[_offset];
	_offset++;
	if (_mappingCount > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	UINT32 _listingCount = _array[_offset];
	_offset++;
	if (_listingCount > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* _mappingOffset = _array + _offset;
	_offset += _mappingCount + 1;
	if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

	UINT32 _mapDataLength = _mappingOffset[_mappingCount];
	if (_mapDataLength > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* _listingOffset = _array + _offset;
	_offset += _listingCount + 1;
	if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

	UINT32 _listDataLength = _listingOffset[_listingCount];
	if (_listDataLength > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* _mappingData = _array + _offset;
	_offset += _mapDataLength;
	if (_length < _offset) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 const* _listingData = _array + _offset;
	_offset += _listDataLength;
	if (_length != _offset) throw IAMException(IAMException::INVALID_LENGTH);

	IAMMapping* _mappingArray = new IAMMapping[_mappingCount];
	DELETE_ARRAY<IAMMapping> _mappingGuard(_mappingArray);

	IAMListing* _listingArray = new IAMListing[_listingCount];
	DELETE_ARRAY<IAMListing> _listingGuard(_listingArray);

	for (UINT32 i = 0; i < _mappingCount; i++) {
		INT32 const* _sourceArray = _mappingData + _mappingOffset[i];
		INT32 _sourceLength = _mappingOffset[i + 1] - _mappingOffset[i];
		_mappingArray[i] = IAMMapping(_sourceArray, _sourceLength);
	}

	for (UINT32 i = 0; i < _listingCount; i++) {
		INT32 const* _sourceArray = _listingData + _listingOffset[i];
		INT32 _sourceLength = _listingOffset[i + 1] - _listingOffset[i];
		_listingArray[i] = IAMListing(_sourceArray, _sourceLength);
	}

	_listingArray_ = _listingArray;
	_listingCount_ = _listingCount;
	_mappingArray_ = _mappingArray;
	_mappingCount_ = _mappingCount;

	_mappingGuard.cancel();
	_listingGuard.cancel();

}

IAMIndex::OBJECT::~OBJECT() {
	delete[] _listingArray_;
	delete[] _mappingArray_;
}

RCPointer<IAMIndex::OBJECT> _IAM_INDEX_OBJECT_(new IAMIndex::OBJECT());

IAMIndex::IAMIndex()
	: _object_(_IAM_INDEX_OBJECT_) {
}

IAMIndex::IAMIndex(IAMArray const& _heapData)
	: _object_(new OBJECT(_iamIndexChecked_(_heapData), _heapData.length())) {
	_object_->_heapData_ = _heapData;
}

IAMIndex::IAMIndex(MMFArray const& _fileData)
	: _object_(new OBJECT(_iamIndexChecked_(_fileData), _fileData.size() >> 2)) {
	_object_->_fileData_ = _fileData;
}

IAMIndex::IAMIndex(INT32 const* _array, INT32 _length)
	: _object_(new OBJECT(_array, _length)) {
}

void IAMIndex::check() const {
	OBJECT _this = *_object_;
	IAMListing* _listing = _this._listingArray_;
	for (IAMListing* _cancel = _listing + _this._listingCount_; _listing < _cancel; ++_listing) {
		_listing->check();
	}
	IAMMapping* _mapping = _this._mappingArray_;
	for (IAMMapping* _cancel = _mapping + _this._mappingCount_; _mapping < _cancel; ++_mapping) {
		_mapping->check();
	}
}

IAMListing IAMIndex::listing(INT32 _index) const {
	OBJECT _this = *_object_;
	if ((UINT32) _index >= _this._listingCount_) return IAMListing();
	return _this._listingArray_[_index];
}

INT32 IAMIndex::listingCount() const {
	OBJECT _this = *_object_;
	return _this._listingCount_;
}

IAMMapping IAMIndex::mapping(INT32 _index) const {
	OBJECT _this = *_object_;
	if ((UINT32) _index >= _this._mappingCount_) return IAMMapping();
	return _this._mappingArray_[_index];
}

INT32 IAMIndex::mappingCount() const {
	OBJECT _this = *_object_;
	return _this._mappingCount_;
}

// class IAMException

IAMException::IAMException(INT8 const _code)
	: _code_(_code) {
}

INT8 IAMException::code() const {
	return _code_;
}

}

}

}
