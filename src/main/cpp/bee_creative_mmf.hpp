/** Dieses Modul definiert Klassen und Methoden zu Bereitstellung der Daten einer Datei als <i>memory-mapped-file</i> sowie zur Interpretation von Speicherbereichen in als Zahlen in <i>big-endian</i> und <i>little-endian</i> Kodierung.
 * @author [cc-by] 2014-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_MMF_HPP
#define BEE_CREATIVE_MMF_HPP

#include "bee_creative_sys.hpp"
#include "bee_creative_ptr.hpp"

namespace bee_creative {

/** Diese Methode interpretiert die gegebene Anzahl an Byte des gegebenen Arrays als @c UINT32 in <i>big-endian</i> Bytereigenfolge.
 * @param array Array.
 * @param length Anzahl der Byte (<tt>0...4</tt>).
 * @return Zahlenwert. */
inline UINT32 getValueBE(UINT8 const* array, UINT8 length);

/** Diese Methode interpretiert die gegebene Anzahl an Byte des gegebenen Arrays als @c UINT32 in <i>little-endian</i> Bytereigenfolge.
 * @param array Array.
 * @param length Anzahl der Byte (<tt>0...4</tt>).
 * @return Zahlenwert. */
inline UINT32 getValueLE(UINT8 const* array, UINT8 length);

/** Diese Methode gibt die Anzahl der Byte zurück, um den gegebenen positiven Zahlenwert abzubilden.
 * @param value positiver Zahlenwert.
 * @return Anzahl der Byte (<tt>0...4</tt>). */
inline UINT8 getLengthf(UINT32 value);

/** Diese Klasse implementiert ein Objekt zur Bereitstellung der Daten eines <i>memory-mapped-file</i> als Array.<br>
 * Intern werden die Funktionen @c CreateFileMappingA, @c MapViewOfFile und @c UnmapViewOfFile genutzt. */
struct MMFArray {

	/** Diese Klasse definiert die referenzgezählten Nutzdaten eines @c MMFArray. */
	struct Data: public Obj<Data> {

		Data();

		~Data();

		private:

		friend MMFArray;

		Data(UINT8* addr, UINT32 size, Data* owner);

		/** Dieses Feld speichert den Beginn des Speicherbereichs. */
		UINT8* addr_;

		/** Dieses Feld speichert die Größe des Speicherbereichs. */
		UINT32 size_;

		/** Dieses Feld speichert die originalen Nutzdaten, wenn diese Nutzdaten einen Abschnitte darstellen. */
		Data::Ptr owner_;

	};

	/** Dieser Konstruktor erzeugt einen leeren @c MMFArray. */
	MMFArray();

	/**Dieser Konstruktor öffnet die Datei mit dem gegebenen Namen zum Lesen bzw. Schreiben.
	 * Wenn das Öffnen erfolglos ist, sind Adresse sowie Größe der Nutzdaten <tt>0</tt>.
	 * @param filename Dateiname.
	 * @param readonly @c true, wenn die Datei nur zum Lesen geöffnet werden soll. */
	MMFArray(PCCHAR filename, bool readonly = true);

	/** Diese Methode gibt die Adresse des ersten Bytes der Nutzdaten zurück.
	 * @return Adresse der Nutzdaten oder <tt>0</tt>. */
	UINT8* addr() const;

	/** Diese Methode gibt die Größe der Nutzdaten zurück.
	 * @return Größe der Nutzdaten oder <tt>0</tt>. */
	UINT32 size() const;

	/** Diese Methode gibt eine Sicht auf einen Abschnitt der Nutzdaten zurück.<br>
	 * Wenn der Abschnitt außerhalb der Nutzdaten liegt, wird ein leerer Abschnitt geliefert.
	 * @param offset Anzahl der Byte vor dem Abschnitt.
	 * @param length Anzahl der Byte im Abschnitt.
	 * @return Abschnitt als @c MMFArray. */
	MMFArray part(UINT32 offset, UINT32 length) const;

	private:

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	Data::Ptr data_;

};

}

#endif
