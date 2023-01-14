/* [cc-by] 2014-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#include "bee_creative_iam.hpp"

namespace bee_creative {

/** Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
 * @param data_type Datengrößentyp (1 = @c INT8, 2 = @c INT16, 3 = @c INT32).
 * @return Byteanzahl (1, 2, 4). */
inline UINT8 iam_byte_count_(UINT8 const data_type) {
	return 1 << (data_type - 1);
}

/** Diese Methode gibt die kleinste Länge eines @c INT32 Arrays zurück, in dessen Speicherbereich ein @c INT8 Array mit der gegebenen Länge passen.
 * @param byte_count Länge eines @c INT8 Arrays.
 * @return Länge des @c INT32 Arrays. */
inline UINT32 iam_byte_align_(UINT32 byte_count) {
	return (byte_count + 3) >> 2;
}

/** Diese Methode gibt die index-te Zahl der gegebenen Zahlenfolge zurück.
 * @param type Datengrößentyp zur Interpretation der Zahlenfolge (<code>[30:0][2:S]</code>).
 * @param array Zahlenfolge.
 * @param index Index.
 * @return index-te Zahl. */
inline UINT32 iam_data_get_(UINT8 const type, PCVOID const array, INT32 index) {
	switch (type) {
		case 0: // D0
			return (UINT64) array;
		case 1: // D1
			return ((UINT8 const*) array)[index];
		case 2: // D2
			return ((UINT16 const*) array)[index];
		case 3: // D3
			return ((UINT32 const*) array)[index];
	}
	return 0;
}

/** Diese Methode gibt die aus den gegebenen Größen zusammengesetzte Datentypkennung zurück.
 * @param data_type Datengrößentyp (1 = INT8, 2 = INT16, 3 = INT32).
 * @param size_type Datenlängentyp (0 = statisch UINT32, 1 = dynamisch UINT8, 2 = dynamisch UINT16, 3 = dynamisch UINT32).
 * @return Datentypkennung (data_type * 4 + size_type - 4). */
inline UINT8 iam_data_type_(UINT8 const data_type, UINT8 const size_type) {
	return (data_type << 2) + size_type - 4;
}

/** Diese Methode gibt die array_index-te Zahlenfolge mit statischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von array_data.
 * @param array_size Größe einer Zahlenfolge (@c UINT32).
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @return array_index-te Zahlenfolge. */
template<typename PCDATA>
inline IAMArray iam_data_array_s_(PCVOID const array_size, PCVOID const array_data, INT32 array_index) {
	UINT32 length = (UINT64) array_size;
	PCDATA const array = (PCDATA const) array_data;
	return IAMArray(array + array_index * length, length);
}

/** Diese Methode gibt die array_index-te Zahlenfolge mit dynamischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von array_data.
 * @tparam PCSIZE Datentyp zur Interpretation von array_size.
 * @param array_size Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @return array_index-te Zahlenfolge. */
template<typename PCDATA, typename PCSIZE>
inline IAMArray iam_data_array_d_(PCVOID const array_size, PCVOID const array_data, INT32 array_index) {
	PCSIZE const size = (PCSIZE const) array_size;
	UINT32 offset = size[array_index];
	UINT32 length = size[array_index + 1] - offset;
	PCDATA const array = (PCDATA const) array_data;
	return IAMArray(array + offset, length);
}

/** Diese Methode gibt die array_index-te Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param type Datentypkennung zur Interpretation von array_size und array_data.
 * @param array_size Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @return array_index-te Zahlenfolge. */
inline IAMArray iam_data_array_(UINT8 const type, PCVOID const array_size, PCVOID const array_data, INT32 array_index) {
	switch (type) {
		case 0: // D1, S0
			return iam_data_array_s_<INT8 const*>(array_size, array_data, array_index);
		case 1: // D1, S1
			return iam_data_array_d_<INT8 const*, UINT8 const*>(array_size, array_data, array_index);
		case 2: // D1, S2
			return iam_data_array_d_<INT8 const*, UINT16 const*>(array_size, array_data, array_index);
		case 3: // D1, S3
			return iam_data_array_d_<INT8 const*, UINT32 const*>(array_size, array_data, array_index);
		case 4: // D2, S0
			return iam_data_array_s_<INT16 const*>(array_size, array_data, array_index);
		case 5: // D2, S1
			return iam_data_array_d_<INT16 const*, UINT8 const*>(array_size, array_data, array_index);
		case 6: // D2, S2
			return iam_data_array_d_<INT16 const*, UINT16 const*>(array_size, array_data, array_index);
		case 7: // D2, S3
			return iam_data_array_d_<INT16 const*, UINT32 const*>(array_size, array_data, array_index);
		case 8: // D3, S0
			return iam_data_array_s_<INT32 const*>(array_size, array_data, array_index);
		case 9: // D3, S1
			return iam_data_array_d_<INT32 const*, UINT8 const*>(array_size, array_data, array_index);
		case 10: // D3, S2
			return iam_data_array_d_<INT32 const*, UINT16 const*>(array_size, array_data, array_index);
		case 11: // D3, S3
			return iam_data_array_d_<INT32 const*, UINT32 const*>(array_size, array_data, array_index);
	}
	return IAMArray();
}

/** Diese Methode gibt die value_index-te Zahl der array_index-ten Zahlenfolge mit statischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von array_data.
 * @param array_size Größe einer Zahlenfolge (@c UINT32).
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @param value_index Index der Zahl.
 * @return value_index-te Zahl der array_index-ten Zahlenfolge. */
template<typename PCDATA>
inline INT32 iam_data_value_s_(PCVOID const array_size, PCVOID const array_data, INT32 array_index, INT32 value_index) {
	UINT32 length = (UINT64) array_size;
	if ((UINT32) value_index >= length) return 0;
	PCDATA const array = (PCDATA const) array_data;
	return array[array_index * length + value_index];
}

/** Diese Methode gibt die value_index-te Zahl der array_index-ten Zahlenfolge mit dynamischer Größe aus dem gegebenen Speicherbereich zurück.
 * @tparam PCDATA Datentyp zur Interpretation von array_data.
 * @tparam PCSIZE Datentyp zur Interpretation von array_size.
 * @param array_size Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @param value_index Index der Zahl.
 * @return value_index-te Zahl der array_index-ten Zahlenfolge. */
template<typename PCDATA, typename PCSIZE>
inline INT32 iam_data_value_d_(PCVOID const array_size, PCVOID const array_data, INT32 array_index, INT32 value_index) {
	PCSIZE const size = (PCSIZE const) array_size;
	UINT32 index = size[array_index] + value_index;
	if (index >= size[array_index + 1]) return 0;
	PCDATA const array = (PCDATA const) array_data;
	return array[value_index];
}

/** Diese Methode gibt die value_index-te Zahl der array_index-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param type Datentypkennung zur Interpretation von array_size und array_data.
 * @param array_size Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_data Speicherbereich mit den Daten der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @param value_index Index der Zahl.
 * @return value_index-te Zahl der array_index-ten Zahlenfolge. */
inline INT32 iam_data_value_(UINT8 const type, PCVOID const array_size, PCVOID const array_data, INT32 array_index, INT32 value_index) {
	switch (type) {
		case 0: // D1, S0
			return iam_data_value_s_<INT8 const*>(array_size, array_data, array_index, value_index);
		case 1: // D1, S1
			return iam_data_value_d_<INT8 const*, UINT8 const*>(array_size, array_data, array_index, value_index);
		case 2: // D1, S2
			return iam_data_value_d_<INT8 const*, UINT16 const*>(array_size, array_data, array_index, value_index);
		case 3: // D1, S3
			return iam_data_value_d_<INT8 const*, UINT32 const*>(array_size, array_data, array_index, value_index);
		case 4: // D2, S0
			return iam_data_value_s_<INT16 const*>(array_size, array_data, array_index, value_index);
		case 5: // D2, S1
			return iam_data_value_d_<INT16 const*, UINT8 const*>(array_size, array_data, array_index, value_index);
		case 6: // D2, S2
			return iam_data_value_d_<INT16 const*, UINT16 const*>(array_size, array_data, array_index, value_index);
		case 7: // D2, S3
			return iam_data_value_d_<INT16 const*, UINT32 const*>(array_size, array_data, array_index, value_index);
		case 8: // D3, S0
			return iam_data_value_s_<INT32 const*>(array_size, array_data, array_index, value_index);
		case 9: // D3, S1
			return iam_data_value_d_<INT32 const*, UINT8 const*>(array_size, array_data, array_index, value_index);
		case 10: // D3, S2
			return iam_data_value_d_<INT32 const*, UINT16 const*>(array_size, array_data, array_index, value_index);
		case 11: // D3, S3
			return iam_data_value_d_<INT32 const*, UINT32 const*>(array_size, array_data, array_index, value_index);
	}
	return 0;
}

/** Diese Methode gibt die Länge der array_index-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @tparam PCSIZE Datentyp zur Interpretation von array_size.
 * @param array_size Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @return Länge der array_index-ten Zahlenfolge. */
template<typename PCSIZE>
inline UINT32 iam_data_length_d_(PCVOID const array_size, INT32 array_index) {
	PCSIZE const size = (PCSIZE const) array_size;
	return size[array_index + 1] - size[array_index];
}

/** Diese Methode gibt die Länge der array_index-ten Zahlenfolge aus dem gegebenen Speicherbereich zurück.
 * @param type Datenlängentyp zur Interpretation von array_size.
 * @param array_size Größe der Zahlenfolge bzw. Speicherbereich mit den Längen der Zahlenfolgen.
 * @param array_index Index der Zahlenfolge.
 * @return Länge der array_index-ten Zahlenfolge. */
inline UINT32 iam_data_length_(UINT8 const type, PCVOID const array_size, INT32 array_index) {
	switch (type) {
		case 0: // S0
			return (UINT64) array_size;
		case 1: // S1
			return iam_data_length_d_<UINT8 const*>(array_size, array_index);
		case 2: // S2
			return iam_data_length_d_<UINT16 const*>(array_size, array_index);
		case 3: // S3
			return iam_data_length_d_<UINT32 const*>(array_size, array_index);
	}
	return 0;
}

/** Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
 * @tparam PCSIZE Datentyp zur Interpretation von array_size.
 * @param array Speicherbereich mit den Startpositionen der Zahlenfolgen.
 * @param count Anzahl der Zahlenfolgen.
 * @throws IAMException Wenn die erste Zahl nicht 0 ist oder die Zahlen nicht monoton steigen. */
template<typename PCSIZE>
inline void iam_data_check_d_(PCVOID const array, INT32 count) {
	PCSIZE const size = (PCSIZE const) array;
	UINT32 prev = size[0];
	if (prev) throw IAMException(IAMException::INVALID_OFFSET);
	for (INT32 i = 0; i <= count; ++i) {
		UINT32 next = size[i];
		if (prev > next) throw IAMException(IAMException::INVALID_OFFSET);
		prev = next;
	}
}

/** Diese Methode prüft die Monotonität der gegebenen Zahlenfolge.
 * @param type Datenlängentyp zur Interpretation von array_size.
 * @param array Speicherbereich mit den Startpositionen der Zahlenfolgen.
 * @param count Anzahl der Zahlenfolgen.
 * @throws IAMException Wenn die erste Zahl nicht 0 ist oder die Zahlen nicht monoton steigen. */
inline void iam_data_check_(UINT8 const type, PCVOID const array, INT32 count) {
	switch (type & 3) {
		case 1: // S1
			iam_data_check_d_<UINT8 const*>(array, count);
			return;
		case 2: // S2
			iam_data_check_d_<UINT16 const*>(array, count);
			return;
		case 3: // S3
			iam_data_check_d_<UINT32 const*>(array, count);
			return;
	}
}

/** Diese Methode initialisiert ein @c IAMArray mit den gegebenen Daten.
 * @tparam PCDATA Datentyp zur Interpretation von array.
 * @param data Daten der Zahlenfolge.
 * @param size Länge der Zahlenfolge.
 * @param type Datentyp.
 * @param array Zeiger auf den Speicherbereich mit Zahlen.
 * @param length Anzahl der Zahlen. */
template<typename PCDATA>
inline void iamarray_new_(PCVOID& data, UINT32& size, UINT8 const type, PCDATA const array, INT32 length) {
	if (!array || length <= 0) {
		size = 0;
		data = 0;
	} else {
		UINT32 count = length <= 0x3FFFFFFF ? length : 0x3FFFFFFF;
		if (type) {
			data = array;
			size = (count << 2) | type;
		} else {
			INT32* copy = new INT32[count + 1];
			copy[0] = 1;
			copy++;
			for (UINT32 i = 0; i < count; i++)
				copy[i] = array[i];
			data = copy;
			size = count << 2;
		}
	}
}

/** Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück
 * @tparam PCDATA Datentyp zur Interpretation von array.
 * @param array Zahlenfolge.
 * @param length Länge der Zahlenfolge.
 * @return Streuwert. */
template<typename PCDATA>
inline INT32 iamarray_hash_(PCVOID const array, INT32 length) {
	PCDATA data = (PCDATA) array;
	INT32 result = 0x811C9DC5;
	for (INT32 i = 0; i < length; i++)
		result = (result * 0x01000193) ^ (INT32) data[i];
	return result;
}

/** Diese Methode gibt nur dann @c true zurück, wenn die gegebenen Zahlenfolgen gleich sind.
 * @tparam PCDATA1 Datentyp zur Interpretation von array1.
 * @tparam PCDATA2 Datentyp zur Interpretation von array2.
 * @param array1 Erste Zahlenfolge.
 * @param array2 Zweite Zahlenfolge.
 * @param length Länge der Zahlenfolgen.
 * @return @c true, wenn die Zahlenfolgen gleich sind. */
template<typename PCDATA1, typename PCDATA2>
inline bool iamarray_equals_(PCVOID const array1, PCVOID const array2, UINT32 length) {
	PCDATA1 const data1 = (PCDATA1 const) array1;
	PCDATA2 const data2 = (PCDATA2 const) array2;
	for (UINT32 i = 0; i < length; i++)
		if (data1[i] != data2[i]) return false;
	return true;
}

/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als 0 zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw. größer als die der zweiten Zahlenfolge ist.
 * @tparam PCDATA1 Datentyp zur Interpretation von array1.
 * @tparam PCDATA2 Datentyp zur Interpretation von array2.
 * @param array1 Erste Zahlenfolge.
 * @param array2 Zweite Zahlenfolge.
 * @param length1 Länge der ersten Zahlenfolge.
 * @param length2 Länge der zweiten Zahlenfolge.
 * @return Vergleichswert der Ordnungen. */
template<typename PCDATA1, typename PCDATA2>
inline INT32 iamarray_compare_(PCVOID const array1, PCVOID const array2, UINT32 length1, UINT32 length2) {
	PCDATA1 const data1 = (PCDATA1 const) array1;
	PCDATA2 const data2 = (PCDATA2 const) array2;
	UINT32 length = length1 < length2 ? length1 : length2;
	for (UINT32 i = 0; i < length; i++) {
		INT32 result = data1[i] - data2[i];
		if (result != 0) return result;
	}
	return length1 - length2;
}

typedef boost::detail::atomic_count iamarray_count;

/** Diese Methode gibt die Zahlenfolge zurück, der im kopierenden Konstrukteur sowie im Zuweisungsoperator von @c IAMArray verwendet wird.
 * @param data Daten der Zahlenfolge.
 * @param size Länge der Zahlenfolge.
 * @return Zahlenfolge. */
inline PCVOID iamarray_data_use_(PCVOID const data, UINT32 size) {
	if ((size & 3) || !data) return data;
	INT32 const* array = (INT32 const*) data - 1;
	iamarray_count& count = *(iamarray_count*) array;
	++count;
	return data;
}

/** Diese Methode gibt den Speicher der gegebenen Zahlenfolge frei, wenn diese kopiert wurde.
 * @param data Daten der Zahlenfolge.
 * @param size Länge der Zahlenfolge. */
inline void iamarray_data_free_(PCVOID const data, INT32 size) {
	if ((size & 3) || !data) return;
	INT32 const* array = (INT32 const*) data - 1;
	iamarray_count& count = *(iamarray_count*) array;
	if (--count) return;
	delete[] array;
}

IAMArray::IAMArray() : data_(0), size_(0) {
}

IAMArray::IAMArray(IAMArray const& source) {
	data_ = iamarray_data_use_(source.data_, size_ = source.size_);
}

IAMArray::IAMArray(IAMArray const& source, bool copy) {
	if (copy) {
		UINT32 size = source.size_;
		PCVOID data = source.data_;
		UINT32 length = size >> 2;
		switch (size & 3) {
			case 0:
				iamarray_new_<INT32 const*>(data_, size_, 0, (INT32 const*) data, length);
				break;
			case 1:
				iamarray_new_<INT8 const*>(data_, size_, 0, (INT8 const*) data, length);
				break;
			case 2:
				iamarray_new_<INT16 const*>(data_, size_, 0, (INT16 const*) data, length);
				break;
			case 3:
				iamarray_new_<INT32 const*>(data_, size_, 0, (INT32 const*) data, length);
				break;
		}
	} else {
		data_ = iamarray_data_use_(source.data_, size_ = source.size_);
	}
}

IAMArray::IAMArray(INT8 const* array, INT32 length) {
	iamarray_new_<INT8 const*>(data_, size_, 1, array, length);
}

IAMArray::IAMArray(INT8 const* array, INT32 length, bool copy) {
	iamarray_new_<INT8 const*>(data_, size_, copy ? 0 : 1, array, length);
}

IAMArray::IAMArray(INT16 const* array, INT32 length) {
	iamarray_new_<INT16 const*>(data_, size_, 2, array, length);
}

IAMArray::IAMArray(INT16 const* array, INT32 length, bool copy) {
	iamarray_new_<INT16 const*>(data_, size_, copy ? 0 : 2, array, length);
}

IAMArray::IAMArray(INT32 const* array, INT32 length) {
	iamarray_new_<INT32 const*>(data_, size_, 3, array, length);
}

IAMArray::IAMArray(INT32 const* array, INT32 length, bool copy) {
	iamarray_new_<INT32 const*>(data_, size_, copy ? 0 : 3, array, length);
}

IAMArray::~IAMArray() {
	iamarray_data_free_(data_, size_);
}

INT32 IAMArray::get(INT32 index) const {
	UINT32 size = size_;
	if (((UINT32) index) >= (size >> 2)) return 0;
	PCVOID const data = data_;
	switch (size & 3) {
		case 0:
			return ((INT32 const*) data)[index];
		case 1:
			return ((INT8 const*) data)[index];
		case 2:
			return ((INT16 const*) data)[index];
		case 3:
			return ((INT32 const*) data)[index];
	}
	return 0;
}

INT32 IAMArray::length() const {
	INT32 result = size_ >> 2;
	return result;
}

PCVOID IAMArray::data() const {
	return data_;
}

UINT8 IAMArray::mode() const {
	UINT8 result = (0x4214 >> ((size_ & 3) << 2)) & 7;
	return result;
}

INT32 IAMArray::hash() const {
	UINT32 size = size_;
	PCVOID const data = data_;
	UINT32 length = size >> 2;
	switch (size & 3) {
		case 0:
			return iamarray_hash_<INT32 const*>(data, length);
		case 1:
			return iamarray_hash_<INT8 const*>(data, length);
		case 2:
			return iamarray_hash_<INT16 const*>(data, length);
		case 3:
			return iamarray_hash_<INT32 const*>(data, length);
	}
	return 0;
}

bool IAMArray::equals(IAMArray const& value) const {
	UINT32 size1 = size_, size2 = value.size_, length = size1 >> 2;
	if (length != (size2 >> 2)) return false;
	PCVOID const data1 = data_, data2 = value.data_;
	switch (((size1 & 3) << 2) | (size2 & 3)) {
		case 0:
			return iamarray_equals_<INT32 const*, INT32 const*>(data1, data2, length);
		case 1:
			return iamarray_equals_<INT32 const*, INT8 const*>(data1, data2, length);
		case 2:
			return iamarray_equals_<INT32 const*, INT16 const*>(data1, data2, length);
		case 3:
			return iamarray_equals_<INT32 const*, INT32 const*>(data1, data2, length);
		case 4:
			return iamarray_equals_<INT8 const*, INT32 const*>(data1, data2, length);
		case 5:
			return iamarray_equals_<INT8 const*, INT8 const*>(data1, data2, length);
		case 6:
			return iamarray_equals_<INT8 const*, INT16 const*>(data1, data2, length);
		case 7:
			return iamarray_equals_<INT8 const*, INT32 const*>(data1, data2, length);
		case 8:
			return iamarray_equals_<INT16 const*, INT32 const*>(data1, data2, length);
		case 9:
			return iamarray_equals_<INT16 const*, INT8 const*>(data1, data2, length);
		case 10:
			return iamarray_equals_<INT16 const*, INT16 const*>(data1, data2, length);
		case 11:
			return iamarray_equals_<INT16 const*, INT32 const*>(data1, data2, length);
		case 12:
			return iamarray_equals_<INT32 const*, INT32 const*>(data1, data2, length);
		case 13:
			return iamarray_equals_<INT32 const*, INT8 const*>(data1, data2, length);
		case 14:
			return iamarray_equals_<INT32 const*, INT16 const*>(data1, data2, length);
		case 15:
			return iamarray_equals_<INT32 const*, INT32 const*>(data1, data2, length);
	}
	return false;
}

INT32 IAMArray::compare(IAMArray const& value) const {
	UINT32 size1 = size_, size2 = value.size_;
	PCVOID const data1 = data_, data2 = value.data_;
	UINT32 length1 = size1 >> 2, length2 = size2 >> 2;
	switch (((size1 & 3) << 2) | (size2 & 3)) {
		case 0:
			return iamarray_compare_<INT32 const*, INT32 const*>(data1, data2, length1, length2);
		case 1:
			return iamarray_compare_<INT32 const*, INT8 const*>(data1, data2, length1, length2);
		case 2:
			return iamarray_compare_<INT32 const*, INT16 const*>(data1, data2, length1, length2);
		case 3:
			return iamarray_compare_<INT32 const*, INT32 const*>(data1, data2, length1, length2);
		case 4:
			return iamarray_compare_<INT8 const*, INT32 const*>(data1, data2, length1, length2);
		case 5:
			return iamarray_compare_<INT8 const*, INT8 const*>(data1, data2, length1, length2);
		case 6:
			return iamarray_compare_<INT8 const*, INT16 const*>(data1, data2, length1, length2);
		case 7:
			return iamarray_compare_<INT8 const*, INT32 const*>(data1, data2, length1, length2);
		case 8:
			return iamarray_compare_<INT16 const*, INT32 const*>(data1, data2, length1, length2);
		case 9:
			return iamarray_compare_<INT16 const*, INT8 const*>(data1, data2, length1, length2);
		case 10:
			return iamarray_compare_<INT16 const*, INT16 const*>(data1, data2, length1, length2);
		case 11:
			return iamarray_compare_<INT16 const*, INT32 const*>(data1, data2, length1, length2);
		case 12:
			return iamarray_compare_<INT32 const*, INT32 const*>(data1, data2, length1, length2);
		case 13:
			return iamarray_compare_<INT32 const*, INT8 const*>(data1, data2, length1, length2);
		case 14:
			return iamarray_compare_<INT32 const*, INT16 const*>(data1, data2, length1, length2);
		case 15:
			return iamarray_compare_<INT32 const*, INT32 const*>(data1, data2, length1, length2);
	}
	return 0;
}

IAMArray IAMArray::section(INT32 offset, INT32 length) const {
	UINT32 size = size_;
	if (offset < 0 || length <= 0 || (UINT32) (offset + length) > (size >> 2)) return IAMArray();
	switch (size & 3) {
		case 0:
			return IAMArray(((INT32 const*) data_) + offset, length, true);
		case 1:
			return IAMArray(((INT8 const*) data_) + offset, length);
		case 2:
			return IAMArray(((INT16 const*) data_) + offset, length);
		case 3:
			return IAMArray(((INT32 const*) data_) + offset, length);
	}
	return IAMArray();
}

INT32 IAMArray::operator[](INT32 index) const {
	return get(index);
}

IAMArray& IAMArray::operator=(IAMArray const& source) {
	iamarray_data_free_(data_, size_);
	data_ = iamarray_data_use_(source.data_, size_ = source.size_);
	return *this;
}

IAMEntry::IAMEntry() : key_(), value_() {
}

IAMEntry::IAMEntry(const IAMEntry& source) : key_(source.key_), value_(source.value_) {
}

IAMEntry::IAMEntry(IAMArray const& key, IAMArray const& value) : key_(key), value_(value) {
}

IAMArray const IAMEntry::key() const {
	return key_;
}

INT32 IAMEntry::key(INT32 index) const {
	return key_.get(index);
}

INT32 IAMEntry::keyLength() const {
	return key_.length();
}

IAMArray const IAMEntry::value() const {
	return value_;
}

INT32 IAMEntry::value(INT32 index) const {
	return value_.get(index);
}

INT32 IAMEntry::valueLength() const {
	return value_.length();
}

// class IAMListing

IAMListing::Data::Data() : type_(0), item_size_(0), item_data_(0), item_count_(0) {
}

IAMListing::Data::Data(INT32 const* array, INT32 length) {
	if (!array || length < 3) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 offset = 0;
	UINT32 header = array[offset];
	offset++;
	if ((header & 0xFFFFFFF0) != 0xF00D2000) throw IAMException(IAMException::INVALID_HEADER);

	UINT8 const item_data_type = (header >> 2) & 3;
	UINT8 const item_size_type = (header >> 0) & 3;
	if (!item_data_type) throw IAMException(IAMException::INVALID_HEADER);

	INT32 item_count = array[offset];
	offset++;
	if (item_count > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID item_size;
	UINT32 item_value;
	if (item_size_type) {

		item_size = array + offset;
		item_value = iam_byte_align_((item_count + 1) * iam_byte_count_(item_size_type));
		offset += item_value;
		if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

		item_value = iam_data_get_(item_size_type, item_size, item_count);

	} else {

		item_value = array[offset];
		offset++;
		if (item_value > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

		item_size = (PCVOID) (UINT64) item_value;
		item_value = item_count * item_value;

	}

	PCVOID item_data = array + offset;
	item_value = iam_byte_align_(item_value * iam_byte_count_(item_data_type));
	offset += item_value;
	if (length != offset) throw IAMException(IAMException::INVALID_LENGTH);

	type_ = iam_data_type_(item_data_type, item_size_type);
	item_count_ = item_count;
	item_size_ = item_size;
	item_data_ = item_data;
}

IAMListing::Data::Ptr iamlisting_data_empty_(new IAMListing::Data());

IAMListing::IAMListing() : data_(iamlisting_data_empty_) {
}

IAMListing::IAMListing(INT32 const* array, INT32 length) : data_(new Data(array, length)) {
}

void IAMListing::check() const {
	Data& data = *data_;
	iam_data_check_(data.type_ & 3, data.item_size_, data.item_count_);
}

IAMArray IAMListing::item(INT32 item_index) const {
	Data& data = *data_;
	if ((UINT32) item_index >= data.item_count_) return IAMArray();
	return iam_data_array_(data.type_, data.item_size_, data.item_data_, item_index);
}

INT32 IAMListing::item(INT32 item_index, INT32 index) const {
	Data& data = *data_;
	if (index < 0 || (UINT32) item_index >= data.item_count_) return 0;
	return iam_data_value_(data.type_, data.item_size_, data.item_data_, item_index, index);
}

INT32 IAMListing::itemLength(INT32 item_index) const {
	Data& data = *data_;
	if ((UINT32) item_index >= data.item_count_) return 0;
	return iam_data_length_(data.type_ & 3, data.item_size_, item_index);
}

INT32 IAMListing::itemCount() const {
	Data& data = *data_;
	return data.item_count_;
}

INT32 IAMListing::find(IAMArray const item) const {
	Data& data = *data_;
	UINT32 const type = data.type_;
	PCVOID const item_size = data.item_size_;
	PCVOID const item_data = data.item_data_;
	UINT32 const item_count = data.item_count_;
	for (UINT32 i = 0; i < item_count; i++) {
		IAMArray array = iam_data_array_(type, item_size, item_data, i);
		if (array.equals(item)) return (INT32) i;
	}
	return -1;
}

IAMArray IAMListing::operator[](INT32 item_index) const {
	return item(item_index);
}

INT32 IAMListing::operator[](IAMArray const item) const {
	return find(item);
}

/** Diese Methode gibt den Index des Schlüssels mit statischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, streuwertbasierter Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von key_data.
 * @tparam PCRANGE Datentyp zur Interpretation von range_data.
 * @param key_size Größe eines Schlüssels (@c UINT32).
 * @param key_data Speicherbereich mit den Daten der Schlüssel.
 * @param range_mask Bitmaske für den Streuwert.
 * @param range_data Speicherbereich mit den Längen der Schlüsselbereiche.
 * @param find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCRANGE>
INT32 iammapping_find_hs_(PCVOID key_size, PCVOID key_data, UINT32 range_mask, PCVOID range_data, IAMArray const find) {
	PCDATA const data = (PCDATA) key_data;
	INT32 const size = (UINT64) key_size;
	PCRANGE const range = (PCRANGE) range_data;
	if (size != find.length()) return -1;
	UINT32 index = find.hash() & range_mask;
	for (INT32 p = range[index], n = range[index + 1]; p < n; p++) {
		UINT32 const offset = size * p;
		UINT32 const length = size;
		IAMArray const key(data + offset, length);
		if (key.equals(find)) return p;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit dynamischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, streuwertbasierter Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von key_data.
 * @tparam PCSIZE Datentyp zur Interpretation von key_size.
 * @tparam PCRANGE Datentyp zur Interpretation von range_data.
 * @param key_size Speicherbereich mit den Längen der Schlüssel.
 * @param key_data Speicherbereich mit den Daten der Schlüssel.
 * @param range_mask Bitmaske für den Streuwert.
 * @param range_data Speicherbereich mit den Längen der Schlüsselbereiche.
 * @param find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCSIZE, typename PCRANGE>
INT32 iammapping_find_hd_(PCVOID key_size, PCVOID key_data, UINT32 range_mask, PCVOID range_data, IAMArray const find) {
	PCDATA const data = (PCDATA) key_data;
	PCSIZE const size = (PCSIZE) key_size;
	PCRANGE const range = (PCRANGE) range_data;
	UINT32 const index = find.hash() & range_mask;
	for (INT32 p = range[index], n = range[index + 1]; p < n; p++) {
		UINT32 const offset = size[p];
		UINT32 const length = size[p + 1] - offset;
		IAMArray const key(data + offset, length);
		if (key.equals(find)) return p;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit statischer Größe zurück, der der gegebenen Zahlenfolge ist.
 * Bei erfolgloser, binärer Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von key_data.
 * @param key_size Größe eines Schlüssels (@c UINT32).
 * @param key_data Speicherbereich mit den Daten der Schlüssel.
 * @param key_count Anzahl der Schlüssel.
 * @param find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA>
INT32 iammapping_find_bs_(PCVOID key_size, PCVOID key_data, UINT32 key_count, IAMArray const find) {
	PCDATA const data = (PCDATA) key_data;
	INT32 const size = (UINT64) key_size;
	if (size != find.length()) return -1;
	for (UINT32 p = 0, n = key_count; p < n;) {
		UINT32 const i = (p + n) >> 1;
		UINT32 const offset = size * i;
		UINT32 const length = size;
		IAMArray const key(data + offset, length);
		INT32 const order = key.compare(find);
		if (order < 0) n = i;
		else if (order > 0) p = i + 1;
		else return i;
	}
	return -1;
}

/** Diese Methode gibt den Index des Schlüssels mit dynamischer Größe zurück, der äquivalent zur gegebenen Zahlenfolge ist.
 * Bei erfolgloser, binärer Suche wird <tt>-1</tt> geliefert.
 * @tparam PCDATA Datentyp zur Interpretation von key_data.
 * @tparam PCSIZE Datentyp zur Interpretation von key_size.
 * @param key_size Speicherbereich mit den Längen der Schlüssel.
 * @param key_data Speicherbereich mit den Daten der Schlüssel.
 * @param key_count Anzahl der Schlüssel.
 * @param find Gesuchte Zahlenfolge.
 * @return Index des Schlüssels oder <tt>-1</tt>. */
template<typename PCDATA, typename PCSIZE>
INT32 iammapping_find_bd_(PCVOID key_size, PCVOID key_data, UINT32 key_count, IAMArray const find) {
	PCDATA const data = (PCDATA) key_data;
	PCSIZE const size = (PCSIZE) key_size;
	for (UINT32 p = 0, n = key_count; p < n;) {
		UINT32 const i = (p + n) >> 1;
		UINT32 const offset = size[i];
		UINT32 const length = size[i + 1] - offset;
		IAMArray const key(data + offset, length);
		INT32 const order = key.compare(find);
		if (order < 0) n = i;
		else if (order > 0) p = i + 1;
		else return i;
	}
	return -1;
}

IAMMapping::Data::Data() : type_(0), key_size_(0), key_data_(0), value_size_(0), value_data_(0), range_mask_(0), range_size_(0), entry_count_(0) {
}

IAMMapping::Data::Data(INT32 const* array, INT32 length) {
	if (!array || length < 4) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 offset = 0;
	UINT32 header = array[offset];
	offset++;
	if ((header & 0xFFFFFC00) != 0xF00D1000) throw IAMException(IAMException::INVALID_HEADER);

	UINT8 const key_data_type = (header >> 8) & 3;
	UINT8 const key_size_type = (header >> 6) & 3;
	UINT8 const range_size_type = (header >> 4) & 3;
	UINT8 const value_data_type = (header >> 2) & 3;
	UINT8 const value_size_type = (header >> 0) & 3;
	if (!key_data_type || !value_data_type) throw IAMException(IAMException::INVALID_HEADER);

	UINT32 entry_count = array[offset];
	offset++;
	if (entry_count > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	UINT32 range_mask;
	PCVOID range_size;
	UINT32 range_value;
	if (range_size_type) {

		if (length <= offset) throw IAMException(IAMException::INVALID_LENGTH);

		range_mask = array[offset];
		offset++;
		if ((range_mask < 1) || (range_mask > 0x1FFFFFFF) || ((range_mask + 1) & range_mask)) throw IAMException(IAMException::INVALID_VALUE);

		range_size = array + offset;
		range_value = iam_byte_align_((range_mask + 2) * iam_byte_count_(range_size_type));
		offset += range_value;
		if (length <= offset) throw IAMException(IAMException::INVALID_LENGTH);

		range_value = iam_data_get_(range_size_type, range_size, range_mask + 1);
		if (range_value != entry_count) throw new IAMException(IAMException::INVALID_OFFSET);

	} else {

		range_mask = 0;
		range_size = 0;

	}

	if (length <= offset) throw IAMException(IAMException::INVALID_LENGTH);

	PCVOID key_size;
	UINT32 key_value;
	if (key_size_type) {

		key_size = array + offset;
		key_value = iam_byte_align_((entry_count + 1) * iam_byte_count_(key_size_type));
		offset += key_value;
		if (length <= offset) throw IAMException(IAMException::INVALID_LENGTH);

		key_value = iam_data_get_(key_size_type, key_size, entry_count);

	} else {

		key_value = array[offset];
		offset++;
		if (length <= offset) throw IAMException(IAMException::INVALID_LENGTH);

		key_size = (PCVOID) (UINT64) key_value;
		key_value = entry_count * key_value;

	}
	if (key_value > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID key_data = array + offset;
	key_value = iam_byte_align_(key_value * iam_byte_count_(key_data_type));
	offset += key_value;
	if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

	PCVOID value_size;
	UINT32 value_value;
	if (value_size_type) {

		value_size = array + offset;
		value_value = iam_byte_align_((entry_count + 1) * iam_byte_count_(value_size_type));
		offset += value_value;
		if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

		value_value = iam_data_get_(value_size_type, value_size, entry_count);

	} else {

		value_value = array[offset];
		offset++;
		if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

		value_size = (PCVOID) (UINT64) value_value;
		value_value = entry_count * value_value;

	}
	if (value_value > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	PCVOID value_data = array + offset;
	value_value = iam_byte_align_(value_value * iam_byte_count_(value_data_type));
	offset += value_value;
	if (length != offset) throw IAMException(IAMException::INVALID_LENGTH);

	type_ = (iam_data_type_(key_data_type, key_size_type) << 6) | (range_size_type << 4) | iam_data_type_(value_data_type, value_size_type);
	key_size_ = key_size;
	key_data_ = key_data;
	value_size_ = value_size;
	value_data_ = value_data;
	range_mask_ = range_mask;
	range_size_ = range_size;
	entry_count_ = entry_count;
}

IAMMapping::Data::Ptr iammapping_data_empty_(new IAMMapping::Data());

IAMMapping::IAMMapping() : data_(iammapping_data_empty_) {
}

IAMMapping::IAMMapping(INT32 const* array, INT32 length) : data_(new Data(array, length)) {
}

void IAMMapping::check() const {
	Data& data = *data_;
	iam_data_check_(data.type_ >> 4, data.range_size_, data.range_mask_ + 1);
	iam_data_check_(data.type_ >> 6, data.key_size_, data.entry_count_);
	iam_data_check_(data.type_ >> 0, data.value_size_, data.entry_count_);
}

IAMArray IAMMapping::key(INT32 entry_index) const {
	Data& data = *data_;
	if ((UINT32) entry_index >= data.entry_count_) return IAMArray();
	IAMArray result = iam_data_array_(data.type_ >> 6, data.key_size_, data.key_data_, entry_index);
	return result;
}

INT32 IAMMapping::key(INT32 entry_index, INT32 index) const {
	Data& data = *data_;
	if (index < 0 || (UINT32) entry_index >= data.entry_count_) return 0;
	INT32 result = iam_data_value_(data.type_ >> 6, data.key_size_, data.key_data_, entry_index, index);
	return result;
}

INT32 IAMMapping::keyLength(INT32 entry_index) const {
	Data& data = *data_;
	if ((UINT32) entry_index >= data.entry_count_) return 0;
	INT32 result = iam_data_length_((data.type_ >> 6) & 3, data.key_size_, entry_index);
	return result;
}

IAMArray IAMMapping::value(INT32 entry_index) const {
	Data& data = *data_;
	if ((UINT32) entry_index >= data.entry_count_) return IAMArray();
	IAMArray result = iam_data_array_(data.type_ & 15, data.value_size_, data.value_data_, entry_index);
	return result;
}

INT32 IAMMapping::value(INT32 entry_index, INT32 index) const {
	Data& data = *data_;
	if (index < 0 || (UINT32) entry_index >= data.entry_count_) return 0;
	INT32 result = iam_data_value_(data.type_ & 15, data.value_size_, data.value_data_, entry_index, index);
	return result;
}

INT32 IAMMapping::valueLength(INT32 entry_index) const {
	Data& data = *data_;
	if ((UINT32) entry_index >= data.entry_count_) return 0;
	return iam_data_length_(data.type_ & 3, data.value_size_, entry_index);
}

IAMEntry IAMMapping::entry(INT32 entry_index) const {
	Data& data = *data_;
	if ((UINT32) entry_index >= data.entry_count_) return IAMEntry();
	IAMArray key = iam_data_array_(data.type_ >> 6, data.key_size_, data.key_data_, entry_index);
	IAMArray value = iam_data_array_(data.type_ & 15, data.value_size_, data.value_data_, entry_index);
	IAMEntry result = IAMEntry(key, value);
	return result;
}

INT32 IAMMapping::entryCount() const {
	Data& data = *data_;
	return data.entry_count_;
}

INT32 IAMMapping::find(IAMArray const key) const {
	Data& data = *data_;
	switch (data.type_ >> 4) {
		case 0: // D1, S0, R0
			return iammapping_find_bs_<INT8 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 1: // D1, S0, R1
			return iammapping_find_hs_<INT8 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 2: // D1, S0, R2
			return iammapping_find_hs_<INT8 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 3: // D1, S0, R3
			return iammapping_find_hs_<INT8 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 4: // D1, S1, R0
			return iammapping_find_bd_<INT8 const*, UINT8 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 5: // D1, S1, R1
			return iammapping_find_hd_<INT8 const*, UINT8 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 6: // D1, S1, R2
			return iammapping_find_hd_<INT8 const*, UINT8 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 7: // D1, S1, R3
			return iammapping_find_hd_<INT8 const*, UINT8 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 8: // D1, S2, R0
			return iammapping_find_bd_<INT8 const*, UINT16 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 9: // D1, S2, R1
			return iammapping_find_hd_<INT8 const*, UINT16 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 10: // D1, S2, R2
			return iammapping_find_hd_<INT8 const*, UINT16 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 11: // D1, S2, R3
			return iammapping_find_hd_<INT8 const*, UINT16 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 12: // D1, S3, R0
			return iammapping_find_bd_<INT8 const*, UINT32 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 13: // D1, S3, R1
			return iammapping_find_hd_<INT8 const*, UINT32 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 14: // D1, S3, R2
			return iammapping_find_hd_<INT8 const*, UINT32 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 15: // D1, S3, R3
			return iammapping_find_hd_<INT8 const*, UINT32 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 16: // D2, S0, R0
			return iammapping_find_bs_<INT16 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 17: // D2, S0, R1
			return iammapping_find_hs_<INT16 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 18: // D2, S0, R2
			return iammapping_find_hs_<INT16 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 19: // D2, S0, R3
			return iammapping_find_hs_<INT16 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 20: // D2, S1, R0
			return iammapping_find_bd_<INT16 const*, UINT8 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 21: // D2, S1, R1
			return iammapping_find_hd_<INT16 const*, UINT8 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 22: // D2, S1, R2
			return iammapping_find_hd_<INT16 const*, UINT8 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 23: // D2, S1, R3
			return iammapping_find_hd_<INT16 const*, UINT8 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 24: // D2, S2, R0
			return iammapping_find_bd_<INT16 const*, UINT16 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 25: // D2, S2, R1
			return iammapping_find_hd_<INT16 const*, UINT16 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 26: // D2, S2, R2
			return iammapping_find_hd_<INT16 const*, UINT16 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 27: // D2, S2, R3
			return iammapping_find_hd_<INT16 const*, UINT16 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 28: // D2, S3, R0
			return iammapping_find_bd_<INT16 const*, UINT32 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 29: // D2, S3, R1
			return iammapping_find_hd_<INT16 const*, UINT32 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 30: // D2, S3, R2
			return iammapping_find_hd_<INT16 const*, UINT32 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 31: // D2, S3, R3
			return iammapping_find_hd_<INT16 const*, UINT32 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 32: // D3, S0, R0
			return iammapping_find_bs_<INT32 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 33: // D3, S0, R1
			return iammapping_find_hs_<INT32 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 34: // D3, S0, R2
			return iammapping_find_hs_<INT32 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 35: // D3, S0, R3
			return iammapping_find_hs_<INT32 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 36: // D3, S1, R0
			return iammapping_find_bd_<INT32 const*, UINT8 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 37: // D3, S1, R1
			return iammapping_find_hd_<INT32 const*, UINT8 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 38: // D3, S1, R2
			return iammapping_find_hd_<INT32 const*, UINT8 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 39: // D3, S1, R3
			return iammapping_find_hd_<INT32 const*, UINT8 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 40: // D3, S2, R0
			return iammapping_find_bd_<INT32 const*, UINT16 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 41: // D3, S2, R1
			return iammapping_find_hd_<INT32 const*, UINT16 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 42: // D3, S2, R2
			return iammapping_find_hd_<INT32 const*, UINT16 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 43: // D3, S2, R3
			return iammapping_find_hd_<INT32 const*, UINT16 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 44: // D3, S3, R0
			return iammapping_find_bd_<INT32 const*, UINT32 const*>(data.key_size_, data.key_data_, data.entry_count_, key);
		case 45: // D3, S3, R1
			return iammapping_find_hd_<INT32 const*, UINT32 const*, UINT8 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 46: // D3, S3, R2
			return iammapping_find_hd_<INT32 const*, UINT32 const*, UINT16 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
		case 47: // D3, S3, R3
			return iammapping_find_hd_<INT32 const*, UINT32 const*, UINT32 const*>(data.key_size_, data.key_data_, data.range_mask_, data.range_size_, key);
	}
	return -1;
}

IAMEntry IAMMapping::operator[](INT32 entry_index) const {
	return entry(entry_index);
}

INT32 IAMMapping::operator[](IAMArray const key) const {
	return find(key);
}

IAMIndex::Data::Data() : listing_array_(0), listing_count_(0), mapping_array_(0), mapping_count_(0) {
}

IAMIndex::Data::Data(INT32 const* array, INT32 length) {
	if (!array || length < 5) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 offset = 0;
	UINT32 header = array[offset];
	offset++;
	if (header != 0xF00DBA5E) throw IAMException(IAMException::INVALID_HEADER);

	UINT32 mapping_count = array[offset];
	offset++;
	if (mapping_count > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	UINT32 listing_count = array[offset];
	offset++;
	if (listing_count > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* mapping_offset = array + offset;
	offset += mapping_count + 1;
	if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

	UINT32 mapping_data_length = mapping_offset[mapping_count];
	if (mapping_data_length > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* listing_offset = array + offset;
	offset += listing_count + 1;
	if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

	UINT32 listing_data_length = listing_offset[listing_count];
	if (listing_data_length > 0x3FFFFFFF) throw IAMException(IAMException::INVALID_VALUE);

	INT32 const* mapping_data = array + offset;
	offset += mapping_data_length;
	if (length < offset) throw IAMException(IAMException::INVALID_LENGTH);

	INT32 const* listing_data = array + offset;
	offset += listing_data_length;
	if (length != offset) throw IAMException(IAMException::INVALID_LENGTH);

	IAMMapping* mapping_array = new IAMMapping[mapping_count];
	DeleteArrayGuard<IAMMapping> mapping_guard(mapping_array);

	IAMListing* listing_array = new IAMListing[listing_count];
	DeleteArrayGuard<IAMListing> listing_guard(listing_array);

	for (UINT32 i = 0; i < mapping_count; i++) {
		INT32 const* source_array = mapping_data + mapping_offset[i];
		INT32 source_length = mapping_offset[i + 1] - mapping_offset[i];
		mapping_array[i] = IAMMapping(source_array, source_length);
	}

	for (UINT32 i = 0; i < listing_count; i++) {
		INT32 const* source_array = listing_data + listing_offset[i];
		INT32 source_length = listing_offset[i + 1] - listing_offset[i];
		listing_array[i] = IAMListing(source_array, source_length);
	}

	listing_array_ = listing_array;
	listing_count_ = listing_count;
	mapping_array_ = mapping_array;
	mapping_count_ = mapping_count;

	mapping_guard.cancel();
	listing_guard.cancel();

}

IAMIndex::Data::~Data() {
	delete[] listing_array_;
	delete[] mapping_array_;
}

/** Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c IAMIndex.
 * @param file_data Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist. */
inline INT32 const* iamindex_checked_(MMFArray const& file_data) {
	if (file_data.size() & 3) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) file_data.addr();
}

/** Diese Methode prüft die Größe des gegebenen Speicherbereichs zur Erzeugung eines neuen @c IAMIndex.
 * @param heap_data Speicherbereich.
 * @throws IAMException Wenn die Größe ungültig ist. */
inline INT32 const* iamindex_checked_(IAMArray const& heap_data) {
	if (heap_data.mode() != 4) throw IAMException(IAMException::INVALID_LENGTH);
	return (INT32 const*) heap_data.data();
}

IAMIndex::Data::Ptr iamindex_data_empty_(new IAMIndex::Data());

IAMIndex::IAMIndex() : data_(iamindex_data_empty_) {
}

IAMIndex::IAMIndex(IAMArray const& heap_data) : data_(new Data(iamindex_checked_(heap_data), heap_data.length())) {
	data_->heap_data_ = heap_data;
}

IAMIndex::IAMIndex(MMFArray const& file_data) : data_(new Data(iamindex_checked_(file_data), file_data.size() >> 2)) {
	data_->file_data_ = file_data;
}

IAMIndex::IAMIndex(INT32 const* array, INT32 length) : data_(new Data(array, length)) {
}

void IAMIndex::check() const {
	Data data = *data_;
	IAMListing* listing = data.listing_array_;
	for (IAMListing* stop = listing + data.listing_count_; listing < stop; ++listing) {
		listing->check();
	}
	IAMMapping* mapping = data.mapping_array_;
	for (IAMMapping* stop = mapping + data.mapping_count_; mapping < stop; ++mapping) {
		mapping->check();
	}
}

IAMListing IAMIndex::listing(INT32 index) const {
	Data data = *data_;
	if ((UINT32) index >= data.listing_count_) return IAMListing();
	return data.listing_array_[index];
}

INT32 IAMIndex::listingCount() const {
	Data data = *data_;
	return data.listing_count_;
}

IAMMapping IAMIndex::mapping(INT32 index) const {
	Data data = *data_;
	if ((UINT32) index >= data.mapping_count_) return IAMMapping();
	return data.mapping_array_[index];
}

INT32 IAMIndex::mappingCount() const {
	Data data = *data_;
	return data.mapping_count_;
}

IAMException::IAMException(INT8 const code) : code_(code) {
}

INT8 IAMException::code() const {
	return code_;
}

}

