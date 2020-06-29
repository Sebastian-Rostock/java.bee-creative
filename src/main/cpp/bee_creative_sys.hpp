/** Dieses Modul definiert grundlegende Datentypen für primitive Zahlenwerte.
 * @author [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_SYS_HPP
#define BEE_CREATIVE_SYS_HPP

namespace bee_creative {

typedef char* PCHAR;
typedef char const* PCCHAR;

typedef void* PVOID;
typedef void const* PCVOID;

typedef signed char INT8;
typedef signed short INT16;
typedef signed int INT32;
typedef long long INT64;
typedef unsigned char UINT8;
typedef unsigned short UINT16;
typedef unsigned int UINT32;
typedef unsigned long long UINT64;

/** Diese Klasse definiert die Datentypen zur Interpretation eines Speicherbereichs als Paar aus zwei gleichen Komponenten in Big-Endian, Little-Endian, nativer und nativ-inverser Bytereihenfolge.
 * @tparam T Typ der Komponenten. */
template<typename T>
struct INTXO {

	/** Diese Datenstruktur definiert ein Paar aus zwei Komponenten in Big-Endian Bytereihenfolge. */
	struct BE {

		/** Dieser Konstruktor initialisiert die Komponenten.
		 * @param lo niederwertige Komponente.
		 * @param hi höherwertige Komponente. */
		BE(T const lo, T const hi) : getHI(hi), getLO(lo) {
		}

		/** Dieses Feld speichert die höherwertige Komponente. */
		T getHI;

		/** Dieses Feld speichert die niederwertige Komponente. */
		T getLO;

	};

	/** Diese Datenstruktur definiert ein Paar aus zwei Komponenten in Little-Endian Bytereihenfolge. */
	struct LE {

		/** Dieser Konstruktor initialisiert die Komponenten.
		 * @param lo niederwertige Komponente.
		 * @param hi höherwertige Komponente. */
		LE(T const lo, T const hi) : getLO(lo), getHI(hi) {
		}

		/** Dieses Feld speichert die niederwertige Komponente. */
		T getLO;

		/** Dieses Feld speichert die höherwertige Komponente. */
		T getHI;

	};

	/** Diese Datenstruktur steht für ein Paar aus zwei Komponenten in der nativen Bytereihenfolge. */
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
	typedef LE NE;
#else
	typedef BE NE;
#endif

	/** Diese Datenstruktur steht für ein Paar aus zwei Komponenten in der zur nativen reversen Bytereihenfolge. */
#if __BYTE_ORDER__ == __ORDER_LITTLE_ENDIAN__
	typedef BE RE;
#else
	typedef LE RE;
#endif

};

/** Diese Datenstruktur erlaubt die Interpretation eines ein Byte großen Speicherbereichs als @c INT8 sowie als @c UINT8. */
union INT8S {

	/** Dieser Konstruktor initialisiert den @c UINT8.
	 * @param value @c INT8. */
	INT8S(UINT8 const value) : asUINT8(value) {
	}

	/** Dieses Feld interpretiert den Speicherbereich als @c INT8. */
	INT8 asINT8;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT8. */
	UINT8 asUINT8;

};

/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT8S. */
struct INT16O: public INTXO<INT8S> {
};

/** Diese Datenstruktur erlaubt die Interpretation eines zwei Byte großen Speicherbereichs als @c INT16, als @c UINT16 sowie als Paar aus zwei @c INT8S. */
union INT16S {

	/** Dieser Konstruktor initialisiert den @c UINT16.
	 * @param value @c UINT16. */
	INT16S(UINT16 const value) : asUINT16(value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param lo niederwertige Komponente.
	 * @param hi höherwertige Komponente. */
	INT16S(INT8S const lo, INT8S const hi) : asNE(lo, hi) {
	}

	/** Dieses Feld interpretiert den Speicherbereich als @c INT16. */
	INT16 asINT16;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT16. */
	UINT16 asUINT16;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Little-Endian Bytereihenfolge. */
	INT16O::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Big-Endian Bytereihenfolge. */
	INT16O::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Native-Endian Bytereihenfolge. */
	INT16O::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT8S in Reverse-Endian Bytereihenfolge. */
	INT16O::RE asRE;

};

/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT16S. */
struct INT32O: public INTXO<INT16S> {
};

/** Diese Datenstruktur erlaubt die Interpretation eines vier Byte großen Speicherbereichs als @c INT32, als @c UINT32 sowie als Paar aus zwei @c INT16S. */
union INT32S {

	/** Dieser Konstruktor initialisiert den @c UINT32.
	 * @param value @c UINT32. */
	INT32S(UINT32 const value) : asUINT32(value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param lo niederwertige Komponente.
	 * @param hi höherwertige Komponente. */
	INT32S(INT16S const lo, INT16S const hi) : asNE(lo, hi) {
	}

	/** Dieses Feld interpretiert den Speicherbereich als @c INT32. */
	INT32 asINT32;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT32. */
	UINT32 asUINT32;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Little-Endian Bytereihenfolge. */
	INT32O::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Big-Endian Bytereihenfolge. */
	INT32O::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Native-Endian Bytereihenfolge. */
	INT32O::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT16S in Reverse-Endian Bytereihenfolge. */
	INT32O::RE asRE;

};

/** Diese Datenstruktur definiert die Datentypen für Paare aus zwei @c INT32S. */
struct INT64O: public INTXO<INT32S> {
};

/** Diese Datenstruktur erlaubt die Interpretation eines acht Byte großen Speicherbereichs als @c INT64, als @c UINT64 sowie als Paar aus zwei @c INT32S. */
union INT64S {

	/** Dieser Konstruktor initialisiert den @c UINT64.
	 * @param value @c UINT64. */
	INT64S(UINT64 const value) : asUINT64(value) {
	}

	/** Dieser Konstruktor initialisiert die Komponenten in Native-Endian Bytereihenfolge.
	 * @param lo niederwertige Komponente.
	 * @param hi höherwertige Komponente. */
	INT64S(INT32S const lo, INT32S const hi) : asNE(lo, hi) {
	}

	/** Dieses Feld interpretiert den Speicherbereich als @c INT64. */
	INT64 asINT64;

	/** Dieses Feld interpretiert den Speicherbereich als @c UINT64. */
	UINT64 asUINT64;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Little-Endian Bytereihenfolge. */
	INT64O::LE asLE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Big-Endian Bytereihenfolge. */
	INT64O::BE asBE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Native-Endian Bytereihenfolge. */
	INT64O::NE asNE;

	/** Dieses Feld interpretiert den Speicherbereich als Paar aus zwei @c INT32S in Reverse-Endian Bytereihenfolge. */
	INT64O::RE asRE;

};

}

#endif
