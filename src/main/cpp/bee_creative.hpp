/* [cc-by] 2013..2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]*/

#ifndef BEE_CREATIVE_HPP
#define BEE_CREATIVE_HPP

// RCCounter
#include <boost/smart_ptr/detail/atomic_count.hpp>

// RCObject
#include <boost/smart_ptr/intrusive_ref_counter.hpp>

// RCPointer
#include <boost/smart_ptr/intrusive_ptr.hpp>

#include <stdarg.h>
#include <windef.h>
#include <winbase.h>

namespace bee {

/** Dieser Namensraum definiert grundlegende Datentypen für primitive Zahlenwerte sowie zur Verwaltung referenzgezählter Objekte.
 * @author [cc-by] 2013..2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
namespace creative {

typedef void* PVOID;

typedef void const* PCVOID;

typedef char* PCHAR;

typedef char const* PCCHAR;

typedef signed char INT8;

typedef signed short INT16;

typedef int INT32;

typedef long long INT64;

typedef unsigned char UINT8;

typedef unsigned short UINT16;

typedef unsigned int UINT32;

typedef unsigned long long UINT64;

/** Diese Klasse definiert die Datentypen zur Interpretation eines Speicherbereichs als Paar aus zwei gleichen Komponenten in Big-Endian, Little-Endian, nativer und nativ-inverser Bytereihenfolge.
 * @tparam _PART Typ der Komponenten. */
template<typename _PART>
struct INTXXBO {

	/** Diese Datenstruktur definiert ein Paar aus zwei Komponenten in Big-Endian Bytereihenfolge. */
	struct BE {

		/** Dieses Feld speichert die höherwertige Komponente. */
		_PART getHI;

		/** Dieses Feld speichert die niederwertige Komponente. */
		_PART getLO;

		/** Dieser Konstruktor initialisiert die Komponenten.
		 * @param _lo niederwertige Komponente.
		 * @param _hi höherwertige Komponente. */
		BE(_PART const _lo, _PART const _hi)
				: getHI(_hi), getLO(_lo) {
		}

	};

	/** Diese Datenstruktur definiert ein Paar aus zwei Komponenten in Little-Endian Bytereihenfolge. */
	struct LE {

		/** Dieses Feld speichert die niederwertige Komponente. */
		_PART getLO;

		/** Dieses Feld speichert die höherwertige Komponente. */
		_PART getHI;

		/** Dieser Konstruktor initialisiert die Komponenten.
		 * @param _lo niederwertige Komponente.
		 * @param _hi höherwertige Komponente. */
		LE(_PART const _lo, _PART const _hi)
				: getLO(_lo), getHI(_hi) {
		}

	};

	/** Diese Datenstruktur steht für ein Paar aus zwei Komponenten in der nativen Bytereihenfolge. */
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
	typedef LE NE;
#else
	typedef BE NE;
#endif

	/** Diese Datenstruktur steht für ein Paar aus zwei Komponenten in der zur nativen inversen Bytereihenfolge. */
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
	typedef BE IE;
#else
	typedef LE IE;
#endif

};

/** Diese Datenstruktur erlaubt die Interpretation eines ein Byte großen Speicherbereichs als @c INT8 sowie als @c UINT8. */
union INT8S {

	/** Dieses Feld interpretiert den Speicherbereich als @c INT8. */
	INT8 asINT8;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT8. */
	UINT8 asUINT8;

	/** Dieser Konstruktor initialisiert den @c UINT8.
	 * @param _value @c INT8. */
	INT8S(UINT8 const _value)
			: asUINT8(_value) {
	}

};

/** Diese Datenstruktur erlaubt die Interpretation eines zwei Byte großen Speicherbereichs als @c INT16, als @c UINT16 sowie als Paar aus zwei @c INT8S. */
union INT16S {

	/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT8S. */
	typedef INTXXBO<INT8S> INT16BO;

	/** Dieses Feld interpretiert den Speicherbereich als @c INT16. */
	INT16 asINT16;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT16. */
	UINT16 asUINT16;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Little-Endian Bytereihenfolge. */
	INT16BO::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Big-Endian Bytereihenfolge. */
	INT16BO::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Native-Endian Bytereihenfolge. */
	INT16BO::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Inverse-Endian Bytereihenfolge. */
	INT16BO::IE asIE;

	/** Dieser Konstruktor initialisiert den @c UINT16.
	 * @param _value @c INT16. */
	INT16S(INT16 const _value)
			: asINT16(_value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param _lo niederwertige Komponente.
	 * @param _hi höherwertige Komponente. */
	INT16S(INT8S const _lo, INT8S const _hi)
			: asNE(_lo, _hi) {
	}

};

/** Diese Datenstruktur erlaubt die Interpretation eines vier Byte großen Speicherbereichs als @c INT32, als @c UINT32 sowie als Paar aus zwei @c INT16S. */
union INT32S {

	/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT16S. */
	typedef INTXXBO<INT16S> INT32BO;

	/** Dieses Feld interpretiert den Speicherbereich als @c INT32. */
	INT32 asINT32;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT32. */
	UINT32 asUINT32;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Little-Endian Bytereihenfolge. */
	INT32BO::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Big-Endian Bytereihenfolge. */
	INT32BO::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Native-Endian Bytereihenfolge. */
	INT32BO::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Inverse-Endian Bytereihenfolge. */
	INT32BO::IE asIE;

	/** Dieser Konstruktor initialisiert den @c UINT32.
	 * @param _value @c INT32. */
	INT32S(INT32 const _value)
			: asINT32(_value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param _lo niederwertige Komponente.
	 * @param _hi höherwertige Komponente. */
	INT32S(INT16S const _lo, INT16S const _hi)
			: asNE(_lo, _hi) {
	}

};

/** Diese Datenstruktur erlaubt die Interpretation eines acht Byte großen Speicherbereichs als @c INT64, als @c UINT64 sowie als Paar aus zwei @c INT32S. */
union INT64S {

	/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT32S. */
	typedef INTXXBO<INT32S> INT64BO;

	/** Dieses Feld interpretiert den Speicherbereich als @c INT64. */
	INT64 asINT64;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT64. */
	UINT64 asUINT64;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Little-Endian Bytereihenfolge. */
	INT64BO::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Big-Endian Bytereihenfolge. */
	INT64BO::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Native-Endian Bytereihenfolge. */
	INT64BO::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Inverse-Endian Bytereihenfolge. */
	INT64BO::IE asIE;

	/** Dieser Konstruktor initialisiert den @c UINT64.
	 * @param _value @c INT64. */
	INT64S(INT64 const _value)
			: asINT64(_value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param _lo niederwertige Komponente.
	 * @param _hi höherwertige Komponente. */
	INT64S(INT32S const _lo, INT32S const _hi)
			: asNE(_lo, _hi) {
	}

};

/** Diese Klasse implementiert einen Referenzzähler.<p>
 * Der Referenzzähler ist <em>thread-safe</em> und wird intern über @c boost::detail::atomic_count realisiert. */
struct RCCounter {

	/** Dieser Konstruktor initialisiert den Referenzzähler mit <tt>0</tt>. */
	RCCounter()
			: _value_(0) {
	}

	/** Diese Methode addiert <tt>-1</tt> zum Zähler um gibt den Wert zurück.
	 * @return Wert des Referenzzählers nach der Modifikation. */
	INT32 dec() {
		return --_value_;
	}

	/** Diese Methode addiert <tt>+1</tt> zum Zähler um gibt den Wert zurück.
	 * @return Wert des Referenzzählers nach der Modifikation. */
	INT32 inc() {
		return ++_value_;
	}

	/** Diese Methode gibt den Wert des Zählers zurück.
	 * @return Wert des Referenzzählers. */
	INT32 value() const {
		return _value_;
	}

	/** @copydoc dec() */
	INT32 operator--() {
		return dec();
	}

	/** @copydoc inc() */
	INT32 operator++() {
		return inc();
	}

	/** @copydoc value() */
	operator INT32() const {
		return _value_;
	}

	private:

	/** Dieses Feld speichert den Referenzzähler. */
	boost::detail::atomic_count _value_;

};

/** Diese Klasse implementiert einen Verweis auf ein referenzgezähltes Objekt.<p>
 * Intern wird ein @c boost::intrusive_ptr genutzt.
 * @tparam _ITEM Typ des referenzgezählten Objekts. */
template<typename _ITEM> //
struct RCPointer {

	/** Dieser Datentyp definiert das referenzgezählte Objekt. */
	typedef _ITEM ITEM;

	/** Dieser Datentyp definiert einen Verweis auf das referenzgezählte Objekt. */
	typedef RCPointer<ITEM> POINTER;

	/** Dieser Konstruktor initialisiert den Verweis mit <tt>null</tt>. */
	RCPointer() {
	}

	/** Dieser Konstruktor initialisiert den Verweis auf das gegebene referenzgezählte Objekt.
	 * @param _object referenzgezähltes Objekt oder <tt>null</tt>.
	 * @param _count <tt>true</tt>, wenn der Referenzzähler des Objekts erhöht werden soll;<br><tt>false</tt>, wenn der Referenzzähler des Objekts nicht verändert werden soll. */
	RCPointer(ITEM* _object, bool _count = true)
			: _pointer_(_object, _count) {
	}

	/** Dieser Konstruktor kopiert den gegebenen Verweis.
	 * @param _source Verweis. */
	RCPointer(POINTER const& _source)
			: _pointer_(_source) {
	}

	/** Diese Methode gibt das referenzgezählte Objekt oder <tt>null</tt> zurück.
	 * @return referenzgezähltes Objekt oder <tt>null</tt>. */
	ITEM* get() const {
		return _pointer_.get();
	}

	/** Diese Methode setzt diesen Verweis auf das gegebene referenzgezählte Objekt und gibt dieses zurück.<br>
	 * Wenn aktuell ein Objekt referenziert wird, wird dessen Referenzzähler reduziert und das Objekt ggf. freigegeben.<br>
	 * Der Referenzzähler des gegebenen Objekts wird erhöht, wenn dieses nicht <tt>null</tt> ist.
	 * @param _object referenzgezählte Objekt oder <tt>null</tt>.
	 * @return gegebenes Objekt. */
	ITEM* set(ITEM* _object) {
		_pointer_.reset(_object);
		return _object;
	}

	/** Diese Methode setzt diesen Verweis auf <tt>null</tt>.<br>
	 * Wenn aktuell ein Objekt referenziert wird, wird dessen Referenzzähler reduziert und das Objekt ggf. freigegeben. */
	void clear() {
		_pointer_.reset();
	}

	/** Dieser Operator gibt nur dann <tt>true</tt> zurück, wenn dieser Verweis nicht <tt>null</tt> ist.
	 * @return <tt>true</tt>, wenn der Verweis nicht <tt>null</tt> ist;<br><tt>false</tt> sonst. */
	operator bool() const {
		return _pointer_.get() != 0;
	}

	/** Dieser Operator gibt den Zeiger auf das referenzgezählte Objekt zurück.
	 * @return Zeiger. */
	operator ITEM*() const {
		return _pointer_.get();
	}

	/** Dieser Operator gibt nur dann <tt>true</tt> zurück, wenn dieser Verweis <tt>null</tt> ist.
	 * @return <tt>true</tt>, wenn der Verweis <tt>null</tt> ist;<br><tt>false</tt> sonst. */
	bool operator!() const {
		return !_pointer_.get();
	}

	/** Dieser Operator gibt die Referenz auf das referenzgezählte Objekt zurück.
	 * @return Referenz.
	 * @throws (EXCEPTION) Wenn dieser Verweis <tt>null</tt> ist. */
	ITEM& operator*() const {
		return *_pointer_;
	}

	/** Dieser Operator gibt den Zeiger auf das referenzgezählte Objekt zurück.
	 * @return Zeiger.
	 * @throws (EXCEPTION) Wenn dieser Verweis <tt>null</tt> ist. */
	ITEM* operator->() const {
		return &*_pointer_;
	}

	/** Dieser Operator setzt den Zeiger auf das referenzgezählte Objekt und gibt @c this zurück.
	 * @param _that Zeiger.
	 * @return @c this. */
	POINTER& operator=(ITEM* _that) {
		_pointer_ = _that;
		return *this;
	}

	/** Dieser Operator setzt den Zeiger auf das referenzgezählte Objekt und gibt @c this zurück.
	 * @param _that Zeiger.
	 * @return @c this. */
	POINTER& operator=(POINTER const& _that) {
		_pointer_ = _that._pointer_;
		return *this;
	}

	private:

	/** Dieses Feld speichert den Verweis auf das referenzgezählte Objekt. */
	boost::intrusive_ptr<ITEM> _pointer_;

};

/** Diese Klasse implementiert ein referenzgezähltes Objekt.<br>
 * Referenzgezählte Objekte werden als Nachfahren dieser Klasse definiert und über @c RCPointer referenziert.<p>
 * Der Referenzzähler ist <em>thread-safe</em> und wird intern über @c boost::sp_adl_block::intrusive_ref_counter angebunden.
 * @tparam _ITEM Typ des referenzgezählten Objekts, das von dieser Klasse erbt. */
template<typename _ITEM> //
struct RCObject: public boost::sp_adl_block::intrusive_ref_counter<_ITEM> {

	/** Dieser Datentyp definiert das referenzgezählte Objekt. */
	typedef _ITEM ITEM;

	/** Dieser Datentyp definiert einen Verweis auf das referenzgezählte Objekt. */
	typedef RCPointer<ITEM> POINTER;

	/** Dieser Konstruktor initialisiert den Referenzzähler mit <tt>0</tt>. */
	RCObject()
			: boost::sp_adl_block::intrusive_ref_counter<ITEM>() {
	}

	/** Dieser Konstruktor initialisiert den Referenzzähler mit <tt>0</tt>. */
	RCObject(RCObject<ITEM> const& _source)
			: boost::sp_adl_block::intrusive_ref_counter<ITEM>(_source) {
	}

	/** @copydoc counter() */
	RCCounter const& counter() const {
		return _counter_[-1];
	}

	protected:

	/** Diese Methode gibt den Referenzzähler zurück.
	 * @return Referenzzähler. */
	RCCounter& counter() {
		return _counter_[-1];
	}

	private:

	/** Dieses Feld speichert den Referenzzähler an Position <tt>-1</tt>. */
	RCCounter _counter_[0];

};

/** Diese Klasse implementiert ein Objekt zum Schutz eines kritischen Abschnitts. Intern wird ein @c CRITICAL_SECTION genutzt. */
struct CSObject {

	/** Dieser Konstruktor initialisiert den kritischen Abschnitt. */
	CSObject() {
		InitializeCriticalSection(&_data_);
	}

	/** Dieser Konstruktor finalisiert den kritischen Abschnitt. */
	~CSObject() {
		DeleteCriticalSection(&_data_);
	}

	/** Diese Methode versucht den kritischen Abschnitt zu betreten.
	 * @return <tt>true</tt>, wenn der kritischen Abschnitt betreten;<br><tt>false</tt>, wenn er nicht betreten wurde, weil sich ein paralleler Verarbeitungsstrang darin befindet. */
	bool test() {
		return TryEnterCriticalSection(&_data_);
	}

	/** Diese Methode betritt den kritischen Abschnitt. Der aufruf blockiert, solange sich ein paralleler Verarbeitungsstrang darin befindet. */
	void enter() {
		EnterCriticalSection(&_data_);
	}

	/** Diese Methode verlässt den kritischen Abschnitt. Die bei @c enter() wartenden Verarbeitungsstränge werden automatisch fortgesetzt. */
	void leave() {
		LeaveCriticalSection(&_data_);
	}

	private:

	/** Dieses Feld speichert die Zustandsdaten. */
	CRITICAL_SECTION _data_;

};

/** Diese Klasse implementiert den <em>scope-guard</em> zu einem kritischen Abschnitt @c CSObject. */
struct CSGuard {

	/** Dieser Konstruktor betritt den kritischen Abschnitt über @c CSObject.enter().
	 * @param _state kritischer Abschnitt. */
	explicit CSGuard(CSObject& _state)
			: _object_(_state) {
		_object_.enter();
	}

	/** Dieser Destruktor verlässt den kritischen Abschnitt über @c CSObject.leave(). */
	~CSGuard() {
		_object_.leave();
	}

	private:

	/** Dieses Feld verweist auf den kritischen Abschnitt. */
	CSObject& _object_;

};

/** Diese Klasse implementiert ein Regelwerk mit statische Methoden zur Finalisierung der Speicherbereiche von Elementen und Arrays.
 * @tparam _ITEM Typ der Elemente. */
template<typename _ITEM> //
struct DELETE_POLICY {

	/** Dieser Datentyp definiert die Elemente. */
	typedef _ITEM ITEM;

	/** Diese Methode finalisiert den Speicherbereich des gegebenen Elements und gibt ihn frei.<p>
	 * Die Implementation in @c DELETE_POLICY ist <tt>delete _item</tt>.
	 * @param _item Element. */
	static void deleteItem(ITEM* _item) {
		delete _item;
	}

	/** Diese Methode finalisiert den Speicherbereichs des gegebenen Arrays.<p>
	 * Die Implementation in @c DELETE_POLICY ist <tt>delete[] _array</tt>.
	 * @param _array Beginn des Speicherbereichs. */
	static void deleteArray(ITEM* _array) {
		delete[] _array;
	}

};

/** Diese Klasse implementiert einen <em>scope-guard</em>, der ein gegebenes Element via @c DELETE_POLICY::deleteItem() freigibt.
 * @tparam _ITEM Typ des Objekts.
 * @tparam _ITEM_POLICY Regelwerk zur Finalisierung und Freigabe des gegebenes Elements. Das Regelwerk muss @c DELETE_POLICY oder dessen Nachfahre sein. */
template<typename _ITEM, typename _ITEM_POLICY = DELETE_POLICY<_ITEM>> //
struct DELETE_VALUE {

	/** Dieser Datentyp definiert das Element. */
	typedef _ITEM ITEM;

	/** Dieser Datentyp definiert das Regelwerk zur Finalisierung und Freigabe des gegebenes Elements. */
	typedef _ITEM_POLICY ITEM_POLICY;

	/** Dieser Konstruktor initialisiert das Element, das im Destruktor via @c DELETE_POLICY::deleteItem() freigegeben wird.
	 * @param _item Objekt. */
	explicit DELETE_VALUE(ITEM* _item)
			: _item_(_item) {
	}

	/** Dieser Destruktor gibt das Element via @c DELETE_POLICY::deleteItem() frei. */
	~DELETE_VALUE() {
		ITEM_POLICY::deleteItem(_item_);
	}

	/** Diese Methode bricht die Freigabe des Objekts ab.<br>
	 * Hierbei wird der Verweis auf das Element auf <tt>0</tt> gesetzt. */
	void cancel() {
		_item_ = 0;
	}

	/** Diese Methode gibt den Verweis auf das Element zurück.*/
	ITEM* value() {
		return _item_;
	}

	/** Diese Methode setzt den Verweis auf das Element.
	 * @param _item Element. */
	void value(ITEM* _item) {
		_item_ = _item;
	}

	/** Dieser Operator gibt die Verweis auf das Element zurück.
	 * @return Referenz. */
	ITEM& operator*() const {
		return *_item_;
	}

	/** Dieser Operator gibt den Verweis auf das Element zurück.
	 * @return Zeiger. */
	ITEM* operator->() const {
		return _item_;
	}

	private:

	/** Dieses Feld speichert das Objekt. */
	ITEM* _item_;

};

/** Diese Klasse implementiert einen <em>scope-guard</em>, der ein gegebenes Array via @c DELETE_POLICY::deleteArray() freigibt.
 * @tparam _ITEM Typ der Elemente des Arrays.
 * @tparam _ITEM_POLICY Regelwerk zur Finalisierung und Freigabe des gegebenes Arrays. Das Regelwerk muss @c DELETE_POLICY oder dessen Nachfahre sein. */
template<typename _ITEM, typename _ITEM_POLICY = DELETE_POLICY<_ITEM>> //
struct DELETE_ARRAY {

	/** Dieser Datentyp definiert die Elemente des Arrays. */
	typedef _ITEM ITEM;

	/** Dieser Datentyp definiert das Regelwerk zur Finalisierung und Freigabe des gegebenes Arrays. */
	typedef _ITEM_POLICY ITEM_POLICY;

	/** Dieser Konstruktor initialisiert das Array mit <tt>0</tt>. */
	DELETE_ARRAY()
			: _array_(0) {
	}

	/** Dieser Konstruktor initialisiert das Array, das im Destruktor via @c DELETE_POLICY::deleteArray() freigegeben wird.
	 * @param _array Array. */
	DELETE_ARRAY(ITEM* _array)
			: _array_(_array) {
	}

	/** Dieser Destruktor gibt das Array via @c DELETE_POLICY::deleteArray() frei. */
	~DELETE_ARRAY() {
		ITEM_POLICY::deleteArray(_array_);
	}

	/** Diese Methode bricht die Freigabe des Arrays ab.<br>
	 * Hierbei wird der Verweis auf das Array auf <tt>0</tt> gesetzt. */
	void cancel() {
		_array_ = 0;
	}

	/** Diese Methode gibt den Verweis auf das Array zurück.*/
	ITEM* array() {
		return _array_;
	}

	/** Diese Methode setzt den Verweis auf das Array.
	 * @param _array Array. */
	void array(ITEM* _array) {
		_array_ = _array;
	}

	/** Dieser Operator gibt die Verweis auf das Array zurück.
	 * @return Referenz. */
	ITEM& operator*() const {
		return *_array_;
	}

	/** Dieser Operator gibt den Verweis auf das Array zurück.
	 * @return Zeiger. */
	ITEM* operator->() const {
		return _array_;
	}

	/** Dieser Operator gibt den Verweis auf das Element an der gegebenen Position zurück.
	 * @param _index Position.
	 * @return Referenz.*/
	ITEM& operator[](INT32 _index) {
		return _array_[_index];
	}

	private:

	/** Dieses Feld speichert das Array. */
	ITEM* _array_;

};

/** Diese Methode initialisiert den Speicherbereich des gegebenen Zielarrays, indem sie für jedes Element den parameterlosen Konstruktor aufruft (d.h. <tt>new (&_targetArray[i]) _TARGET()</tt>).
 * Dies ist insbesondere für strukturabschließende Leerarrays relevant.
 * @tparam _TARGET Typ der Elemente im Zielarray.
 * @param _targetArray Zielarray.
 * @param _length Anzahl der Elemente im Zielarray. */
template<typename _TARGET>
inline void setupArray(_TARGET* _targetArray, INT32 const _length) {
	_TARGET* const _final = _targetArray + _length;
	while (_targetArray < _final) {
		new (_targetArray) _TARGET();
		++_targetArray;
	}
}

/** Diese Methode initialisiert den Speicherbereich des gegebenen Zielarrays, indem sie für jedes Element den Konstruktor mit einem Element des gegebenen Quellarrays aufruft (d.h. <tt>new (&_targetArray[i]) _TARGET(_sourceArray[i])</tt>).
 * Dies ist insbesondere für strukturabschließende Leerarrays relevant.
 * @tparam _TARGET Typ der Elemente im Zielarray.
 * @tparam _TARGET Typ der Elemente im Zielarray.
 * @param _sourceArray Quellarray.
 * @param _targetArray Zielarray.
 * @param _length Anzahl der Elemente im Quell- bzw. Zielarray. */
template<typename _SOURCE, typename _TARGET>
inline void setupArray(_SOURCE const* _sourceArray, _TARGET* _targetArray, INT32 const _length) {
	_TARGET* const _final = _targetArray + _length;
	while (_targetArray < _final) {
		new (_targetArray) _TARGET(*_sourceArray);
		++_sourceArray;
		++_targetArray;
	}
}

/** Diese Methode finalisiert den Speicherbereich des gegebenen Zielarrays, indem sie für jedes Element den parameterlosen Destruktor aufruft (d.h. <tt>_targetArray[i].~_TARGET()</tt>).
 * Dies ist insbesondere für strukturabschließende Leerarrays relevant.
 * @tparam _TARGET Typ der Elemente im Zielarray.
 * @param _targetArray Zielarray.
 * @param _length Anzahl der Elemente im Zielarray. */
template<typename _TARGET>
inline void resetArray(_TARGET* _targetArray, INT32 const _length) {
	_TARGET* const _final = _targetArray + _length;
	while (_targetArray < _final) {
		_targetArray->~_TARGET();
		++_targetArray;
	}
}

/** Diese Methode gibt -1, 0 bzw. +1 zurück, wenn die Ordnung des ersten gegebenen Werts kleiner, gleich bzw. größer als die des zweiten gegebenen Werts ist.
 * @tparam _VALUE Typ der Werte.
 * @param _thisValue erster Wert.
 * @param _thatValue zweiter Wert.
 * @return Vergleichswert. */
template<typename _VALUE>
inline INT8 compareValue(_VALUE const _thisValue, _VALUE const _thatValue) {
	if (_thisValue > _thatValue) return +1;
	if (_thisValue < _thatValue) return -1;
	return 0;
}

}

}

#endif
