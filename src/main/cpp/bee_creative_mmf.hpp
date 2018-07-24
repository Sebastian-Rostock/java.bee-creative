/* [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_MMF_HPP
#define BEE_CREATIVE_MMF_HPP

#include "bee_creative.hpp"

namespace bee {

namespace creative {

/** Dieser Namensraum definiert Klassen und Methoden zu Bereitstellung der Daten einer Datei als <i>memory-mapped-file</i> sowie zur Interpretation von Speicherbereichen in als Zahlen in <i>big-endian</i> und <i>little-endian</i> Kodierung.
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
namespace mmf {

using namespace bee::creative;

/** Diese Methode interpretiert die gegebene Anzahl an Byte des gegebenen Arrays als @c UINT32 in <i>big-endian</i> Bytereigenfolge.
 * @param _array Array.
 * @param _length Anzahl der Byte (<tt>0...4</tt>).
 * @return Zahlenwert. */
inline UINT32 readBE(UINT8 const* _array, UINT8 _length);

/** Diese Methode interpretiert die gegebene Anzahl an Byte des gegebenen Arrays als @c UINT32 in <i>little-endian</i> Bytereigenfolge.
 * @param _array Array.
 * @param _length Anzahl der Byte (<tt>0...4</tt>).
 * @return Zahlenwert. */
inline UINT32 readLE(UINT8 const* _array, UINT8 _length);

/** Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Zahlenwert abzubilden.
 * @param _value positiver Zahlenwert.
 * @return Anzahl der Byte (<tt>0...4</tt>). */
inline UINT8 lengthOf(UINT32 _value);

/** Diese Klasse implementiert ein Objekt zur Bereitstellung der Daten eines <i>memory-mapped-file</i> als Array.<br>
 * Intern werden die Funktionen @c CreateFileMappingA, @c MapViewOfFile und @c UnmapViewOfFile genutzt. */
struct MMFArray {

	public:

	/** Diese Klasse definiert die referenzgezählten Nutzdaten eines @c MMFArray. */
	struct OBJECT: public RCObject<OBJECT> {

		public:

		OBJECT();

		~OBJECT();

		private:

		friend MMFArray;

		OBJECT(PVOID _data, UINT32 _size, OBJECT* _owner);

		/** Dieses Feld speichert den Beginn des Speicherbereichs. */
		PVOID _data_;

		/** Dieses Feld speichert die Größe des Speicherbereichs. */
		UINT32 _size_;

		/** Dieses Feld speichert den originalen Nutzdaten, wenn diese Nutzdaten einen Abschnitte darstellen. */
		RCPointer<OBJECT> _owner_;

	};

	/** Dieser Konstruktor erzeugt einen leeren @c MMFArray. */
	MMFArray();

	/**Dieser Konstruktor öffnet die Datei mit dem gegebenen Namen zum Lesen bzw. Schreiben.
	 * Wenn das Öffnen erfolglos ist, sind Adresse sowie Größe der Nutzdaten <tt>0</tt>.
	 * @param _filename Dateiname.
	 * @param _readonly @c true, wenn die Datei nur zum Lesen geöffnet werden soll. */
	MMFArray(PCCHAR _filename, bool _readonly = true);

	/** Diese Methode gibt die Adresse des ersten Bytes der Nutzdaten zurück.
	 * @return Adresse der Nutzdaten oder <tt>0</tt>. */
	PVOID data() const;

	/** Diese Methode gibt die Größe der Nutzdaten zurück.
	 * @return Größe der Nutzdaten oder <tt>0</tt>. */
	UINT32 size() const;

	/** Diese Methode gibt eine Sicht auf einen Abschnitt der Nutzdaten zurück.<br>
	 * Wenn der Abschnitt außerhalb der Nutzdaten liegt, wird ein leerer Abschnitt geliefert.
	 * @param _offset Anzahl der Byte vor dem Abschnitt.
	 * @param _length Anzahl der Byte im Abschnitt.
	 * @return Abschnitt als @c MMFArray. */
	MMFArray part(UINT32 _offset, UINT32 _length) const;

	private:

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	RCPointer<OBJECT> _object_;

};

}

}

}
#endif
