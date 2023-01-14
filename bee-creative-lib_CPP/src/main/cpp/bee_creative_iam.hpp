/** Dieses Modul realisiert eine schreibgeschützte Implementation des <i>IAM</i>.<p>
 * Das <i>IAM – Integer Array Model</i> beschreibt einerseits ein abstraktes Datenmodell aus Listen und Abbildungen sowie andererseits ein binäres und optimiertes Datenformat zur Auslagerung dieser Listen und Abbildungen in eine Datei. Ziel des Datenformats ist es, entsprechende Dateien per File-Mapping in den Arbeitsspeicher abzubilden und darauf sehr effiziente Lese- und Such-operationen ausführen zu können. Die Modifikation der Daten ist nicht vorgesehen.<p>
 * Die Klassen @c IAMArray, @c IAMEntry, @c IAMListing, @c IAMMapping und @c IAMIndex stellen alle eine Sich auf gegebene, extern verwaltete Speicherbereiche dar. Ihr Verhalten ist unbestimmt, wenn einer der Speicherbereiche freigegeben wird.
 * Die Klasse @c IAMIndex erlaubt das halten des Speicherbereichs, wenn dieser als @c MMFArray gegeben ist.
 * @author [cc-by] 2014-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_IAM_HPP
#define BEE_CREATIVE_IAM_HPP

#include "bee_creative_del.hpp"
#include "bee_creative_mmf.hpp"
#include "bee_creative_ptr.hpp"

namespace bee_creative {

/** Ein @c IAMArray ist eine abstrakte Zahlenfolge, welche in einer Auflistung (@c IAMListing) für die Elemente sowie einer Abbildung (@c IAMMapping) für die Schlüssel und Werte der Einträge (@c IAMEntry) verwendet wird. */
struct IAMArray {

	/** Dieser Kontrukteur erstellt eine leere Zahlenfolge. */
	IAMArray();

	/** Dieser Kontrukteur erstellt eine virtuelle Kopie der gegebenen Zahlenfolge.
	 * @param source Qualldaten. */
	IAMArray(IAMArray const& source);

	/** Dieser Kontrukteur erstellt eine Kopie der gegebenen Zahlenfolge.
	 * @param source Qualldaten.
	 * @param copy @c true, wenn eine Kopie des Speicherbereichs erzeugt werde soll. */
	IAMArray(IAMArray const& source, bool copy);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich.
	 * @param data Zeiger auf den Speicherbereich mit @c INT8 Zahlen.
	 * @param length Anzahl der Zahlen. */
	IAMArray(INT8 const* data, INT32 length);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich bzw. als Kopie des gegebenen Speicherbereichs.
	 * @param data Zeiger auf den Speicherbereich mit @c INT8 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @param copy @c true, wenn eine Kopie des gegebenen Speicherbereichs erzeugt werde soll. */
	IAMArray(INT8 const* data, INT32 length, bool copy);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich.
	 * @param data Zeiger auf den Speicherbereich mit @c INT16 Zahlen.
	 * @param length Anzahl der Zahlen. */
	IAMArray(INT16 const* data, INT32 length);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich bzw. als Kopie des gegebenen Speicherbereichs.
	 * @param data Zeiger auf den Speicherbereich mit @c INT16 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @param copy @c true, wenn eine Kopie des gegebenen Speicherbereichs erzeugt werde soll. */
	IAMArray(INT16 const* data, INT32 length, bool copy);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich.
	 * @param data Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param length Anzahl der Zahlen. */
	IAMArray(INT32 const* data, INT32 length);

	/** Dieser Kontrukteur erstellt eine Zahlenfolge als Sicht auf den gegebenen Speicherbereich bzw. als Kopie des gegebenen Speicherbereichs.
	 * @param data Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @param copy @c true, wenn eine Kopie des gegebenen Speicherbereichs erzeugt werde soll. */
	IAMArray(INT32 const* data, INT32 length, bool copy);

	/** Dieser Destrukteur gibt den ggf. reservierten Speicher wieder frei. */
	~IAMArray();

	/** Diese Methode gibt die <em>index-te</em> Zahl zurück.
	 * Bei einem ungültigen <em>index</em> wird <tt>0</tt> geliefert.
	 * @param index Index.
	 * @return <em>index-te</em> Zahl oder <tt>0</tt>. */
	INT32 get(INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge zurück (<tt>0</tt>..<tt>1073741823</tt>).
	 * @return Anzahl der Zahlen. */
	INT32 length() const;

	/** Diese Methode gibt den Speicherbereich mit den Zahlen zurück.
	 * @return Speicherbereich. */
	PCVOID data() const;

	/** Diese Methode gibt die Größe jeder Zahl in Byte zurück.
	 * @return Größe einer Zahl (<tt>1</tt>, <tt>2</tt>, <tt>4</tt>). */
	UINT8 mode() const;

	/** Diese Methode gibt den Streuwert zurück.
	 * @return Streuwert. */
	INT32 hash() const;

	/** Diese Methode gibt nur dann <tt>true</tt> zurück, wenn diese Zahlenfolge gleich der gegebenen Zahlenfolge ist.
	 * @param value Zahlenfolge.
	 * @return <tt>true</tt>, wenn diese Zahlenfolge gleich der gegebenen ist. */
	bool equals(IAMArray const& value) const;

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als <tt>0</tt> zurück, wenn die Ordnung dieser Zahlenfolge lexikografisch kleiner, gleich bzw. größer als die der gegebenen Zahlenfolge ist.
	 * @param value Zahlenfolge.
	 * @return Vergleichswert der Ordnungen. */
	INT32 compare(IAMArray const& value) const;

	/** Diese Methode gibt einen Abschnitt dieser Zahlenfolge ab der gegebenen Position und mit der gegebene Länge zurück.
	 * Wenn der Abschnitt außerhalb der Zahlenfolge liegt oder die Länge kleiner als <tt>1</tt> ist, wird eine leere Zahlenfolge geliefert.
	 * @param offset Position, ab welcher der Abschnitt beginnt.
	 * @param length Anzahl der Zahlen im Abschnitt.
	 * @return Abschnitt. */
	IAMArray section(INT32 offset, INT32 length) const;

	/** Diese Operator delegiert an @c get(). */
	INT32 operator[](INT32 index) const;

	/** Dieser Operator kopiert die gegebene Zahlenfolge.
	 * @param value Zahlenfolge.
	 * @return <tt>this</tt>. */
	IAMArray& operator=(IAMArray const& value);

	private:

	/** Dieses Feld verweist auf den Speicherbereich mit den Zahlen. */
	PCVOID data_;

	/** Dieses Feld speichert die Kombination aus Länge @c L und Datentyp @c D im Format <code>[30:L][2:D]</code>.<br>
	 * Die Datentypen 0-3 stehn für @c COPY-INT32 const*, @c VIEW-INT8 const*, @c VIEW-INT16 const* und @c VIEW-INT32 const*. */
	UINT32 size_;

};

/** Ein @c IAMEntry ist ein abstrakter Eintrag einer Abbildung (@c IAMMapping) und besteht aus einem Schlüssel und einem Wert, welche selbst Zahlenfolgen (@c IAMArray) sind. */
struct IAMEntry {

	/** Dieser Kontrukteur erstellt einen leeren Eintrag. */
	IAMEntry();

	/** Dieser Kontrukteur erstellt eine Kopie des gegebenen Eintrags.
	 * @param source Quelldaten. */
	IAMEntry(IAMEntry const& source);

	/** Dieser Kontrukteur erstellt einen Eintrag mit den gegebenen Eigenschaften.
	 * @param key Schlüssel.
	 * @param value Wert. */
	IAMEntry(IAMArray const& key, IAMArray const& value);

	/** Diese Methode gibt den Schlüssel als Zahlenfolge zurück.
	 * @return Schlüssel. */
	IAMArray const key() const;

	/** Diese Methode gibt die <em>index-te</em> Zahl des Schlüssels zurück.
	 * Bei einem ungültigen <em>index</em> wird <tt>0</tt> geliefert.
	 * @param index Index.
	 * @return <em>index-te</em> Zahl des Schlüssels.
	 */
	INT32 key(INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge des Schlüssels zurück.
	 * @return Länge des Schlüssels. */
	INT32 keyLength() const;

	/** Diese Methode gibt den Wert als Zahlenfolge zurück.
	 * @return Wert. */
	IAMArray const value() const;

	/** Diese Methode gibt die <em>index-te</em> Zahl des Werts zurück.
	 * Bei einem ungültigen <em>index</em> wird <tt>0</tt> geliefert.
	 * @param index Index.
	 * @return <em>index-te</em> Zahl des Werts. */
	INT32 value(INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge des Werts zurück.
	 * @return Länge des Werts. */
	INT32 valueLength() const;

	private:

	/** Dieses Feld speichert den Schlüssel. */
	IAMArray const key_;

	/** Dieses Feld speichert den Wert. */
	IAMArray const value_;

};

/** Diese Klasse implementiert eine geordnete Auflistung von Elementen, welche selbst Zahlenfolgen (@c IAMArray) sind. */
struct IAMListing {

	/** Diese Klasse definiert die referenzgezählten Nutzdaten eines @c IAMListing. */
	struct Data: public Obj<Data> {

		Data();

		private:

		friend IAMListing;

		Data(INT32 const* array, INT32 length);

		/** Dieses Feld speichert den Kodierungstyp im Format <code>[28:0][2:ID][2:IL]</code>.<br>
		 * Die Werte 0-2 des Elementdatentyps @c ID stehn für @c INT8 const*, @c INT16 const* und @c INT32 const* (siehe @c #_itemSize_).<br>
		 * Die Werte 0-3 des Elementlängentyps @c IL stehn für @c UINT32, @c UINT8 const*, @c UINT16 const* und @c UINT32 const* (siehe @c #_itemData_). */
		UINT32 type_;

		/** Dieses Feld speichert die Anzahl der Elemente. */
		UINT32 item_count_;

		/** Dieses Feld speichert die Längen der Elemente. Die Interpretation dieses Feldes wird durch den Elementlängentyp vorgegeben. */
		PCVOID item_size_;

		/** Dieses Feld speichert die Zahlen der Elemente. Die Interpretation dieses Feldes wird durch den Elementdatentypen vorgegeben. */
		PCVOID item_data_;

	};

	/** Dieser Kontrukteur erstellt eine leere Auflistung. */
	IAMListing();

	/** Dieser Kontrukteur erstellt eine Auflistung als Sicht auf den gegebenen Speicherbereich.
	 * @param array Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird. */
	IAMListing(INT32 const* array, INT32 length);

	/** Diese Methode prüft die Kodierung der Längen der Zahlenfolgen.
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	void check() const;

	/** Diese Methode gibt das <em>item_index-te</em> Element als Zahlenfolge zurück.
	 * Bei einem ungültigen <em>item_index</em> wird eine leere Zahlenfolge geliefert.
	 * @param item_index Index des Elements.
	 * @return <em>item_index-tes</em> Element. */
	IAMArray item(INT32 item_index) const;

	/** Diese Methode gibt die <em>index-te</em> Zahl des <em>item_index-ten</em> Elements zurück.
	 * Bei einem ungültigen <em>index</em> bzw. <em>item_index</em> wird <tt>0</tt> geliefert.
	 * @param item_index Index des Elements.
	 * @param index Index der Zahl.
	 * @return <em>index-te</em> Zahl des <em>item_index-ten</em> Elements. */
	INT32 item(INT32 item_index, INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge des <em>item_index-ten</em> Elements zurück.
	 * Bei einem ungültigen <em>item_index</em> wird <tt>0</tt> geliefert.
	 * @param item_index Index des Elements.
	 * @return Länge des <em>item_index-ten</em> Elements. */
	INT32 itemLength(INT32 item_index) const;

	/** Diese Methode gibt die Anzahl der Elemente zurück (<tt>0</tt>..<tt>1073741823</tt>).
	 * @return Anzahl der Elemente. */
	INT32 itemCount() const;

	/** Diese Methode gibt den Index des Elements zurück, das äquivalent zum gegebenen ist.
	 * Die Suche erfolgt linear vom ersten zum letzten Element. Bei erfolgloser Suche wird <tt>-1</tt> geliefert.
	 * @param item Element.
	 * @return Index des gefundenen Elements. */
	INT32 find(IAMArray const item) const;

	/** Diese Operator delegiert an @c item(). */
	IAMArray operator[](INT32 item_index) const;

	/** Diese Operator delegiert an @c find(). */
	INT32 operator[](IAMArray const item) const;

	private:

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	Data::Ptr data_;

};

/** Diese Klasse implementiert eine Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen (@c IAMArray) sind. */
struct IAMMapping {

	/** Diese Klasse definiert die referenzgezählten Nutzdaten eines @c IAMMapping. */
	struct Data: public Obj<Data> {

		Data();

		private:

		friend IAMMapping;

		Data(INT32 const* array, INT32 length);

		/** Dieses Feld speichert den Kodierungstyp im Format <code>[22:0][2:KD][2:KL][2:RL][2:VD][2:VL]</code>.<br>
		 * Die Werte 0-2 des Schlüsseldatentyps @c KD stehn für @c INT8 const*, @c INT16 const* und @c INT32 const* (siehe @c #_keySize_).<br>
		 * Die Werte 0-3 des Schlüssellängentyps @c KL stehn für @c UINT32, @c UINT8 const* , @c UINT16 const* und @c UINT32 const* (siehe @c #_keyData_).<br>
		 * Die Werte 0-3 des Bereichslängentyps @c RL stehn für @c void, @c UINT8 const*, @c UINT16 const* und @c UINT32 const* (siehe @c #_rangeMask_).<br>
		 * Die Werte 0-2 des Wertdatentyps @c VD stehn für @c INT8 const*, @c INT16 const* und @c INT32 const* (siehe @c #_valueSize_).<br>
		 * Die Werte 0-3 des Wertlängentyps @c VL stehn für @c UINT32, @c UINT8 const*, @c UINT16 const* und @c UINT32 const* (siehe @c #_valueData_). */
		UINT32 type_;

		/** Dieses Feld speichert die Anzahl der Einträge. */
		UINT32 entry_count_;

		/** Dieses Feld speichert die Anzahl der Zahlen in einem Schlüssel. Die Interpretation dieses Feldes wird durch den Schlüssellängentyp vorgegeben. */
		PCVOID key_size_;

		/** Dieses Feld speichert den Speicherbereich mit den Zahlen der Schlüssel. Die Interpretation dieses Feldes wird durch den Schlüsseldatentyp vorgegeben. */
		PCVOID key_data_;

		/** Dieses Feld speichert die Anzahl der Zahlen in einem Wert. Die Interpretation dieses Feldes wird durch den Wertlängentyp vorgegeben. */
		PCVOID value_size_;

		/** Dieses Feld speichert den Speicherbereich mit den Zahlen der Werte. Die Interpretation dieses Feldes wird durch den Wertdatentyp vorgegeben. */
		PCVOID value_data_;

		/** Dieses Feld speichert die Startpositionen der Schlüsselbereiche. Die Interpretation dieses Feldes wird durch den Bereichslängentyp vorgegeben. */
		PCVOID range_size_;

		/** Dieses Feld speichert die Bitmaske zur Umrechnung des Streuwerts eines gesuchten Schlüssels in den Index des einzigen Schlüsselbereichs, in dem dieser Schlüssel enthalten sein kann. Die Streuwertsuche wird nur bei Bereichslängentyp 0 verwendet. */
		UINT32 range_mask_;

	};

	/** Dieser Konstruktor erstellt eine leere Abbildung. */
	IAMMapping();

	/** Dieser Kontrukteur erstellt eine Abbildung als Sicht auf den gegebenen Speicherbereich.
	 * @param data Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird. */
	IAMMapping(INT32 const* data, INT32 length);

	/** Diese Methode prüft die Kodierung der Längen der Schlüssel und Werte.
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	void check() const;

	/** Diese Methode gibt den Schlüssel des <em>entry_index-ten</em> Eintrags als Zahlenfolge zurück.
	 * Bei einem ungültigen <em>entry_index</em> wird eine leere Zahlenfolge geliefert.
	 * @param entry_index Index des Elements.
	 * @return Schlüssel des <em>entry_index-ten</em> Eintrags. */
	IAMArray key(INT32 entry_index) const;

	/** Diese Methode gibt die <em>index-te</em> Zahl des Schlüssels des <em>entry_index-ten</em> Eintrags zurück.
	 * Bei einem ungültigen <em>index</em> bzw. <em>entry_index</em> wird <tt>0</tt> geliefert.
	 * @param entry_index Index des Elements.
	 * @param index Index der Zahl.
	 * @return <em>index-te</em> Zahl des Schlüssels des <em>entry_index-ten</em> Eintrags. */
	INT32 key(INT32 entry_index, INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge des Schlüssels des <em>entry_index-ten</em> Eintrags zurück.
	 * Bei einem ungültigen <em>entry_index</em> wird <tt>0</tt> geliefert.
	 * @param entry_index Index des Elements.
	 * @return Länge des Schlüssels des <em>entry_index-ten</em> Eintrags. */
	INT32 keyLength(INT32 entry_index) const;

	/** Diese Methode gibt den Wert des <em>entry_index-ten</em> Eintrags als Zahlenfolge zurück.
	 * Bei einem ungültigen <em>entry_index</em> wird eine leere Zahlenfolge geliefert.
	 * @param entry_index Index des Elements.
	 * @return Wert des <em>entry_index-ten</em> Eintrags. */
	IAMArray value(INT32 entry_index) const;

	/** Diese Methode gibt die <em>index-te</em> Zahl des Werts des <em>entry_index-ten</em> Eintrags zurück.
	 * Bei einem ungültigen <em>index</em> bzw. <em>entry_index</em> wird <tt>0</tt> geliefert.
	 * @param entry_index Index des Elements.
	 * @param index Index der Zahl.
	 * @return <em>index-te</em> Zahl des Werts des <em>entry_index-ten</em> Eintrags. */
	INT32 value(INT32 entry_index, INT32 index) const;

	/** Diese Methode gibt die Länge der Zahlenfolge des Werts des <em>entry_index-ten</em> Eintrags zurück.
	 * Bei einem ungültigen <em>entry_index</em> wird <tt>0</tt> geliefert.
	 * @param entry_index Index des Elements.
	 * @return Länge des Werts des <em>entry_index-ten</em> Eintrags. */
	INT32 valueLength(INT32 entry_index) const;

	/** Diese Methode gibt den <em>entry_index-ten</em> Eintrag zurück.
	 * Bei einem ungültigen <em>entry_index</em> wird ein leerer Eintrag geliefert.
	 * @param entry_index Index des Elements.
	 * @return <em>entry_index-ter</em> Eintrag. */
	IAMEntry entry(INT32 entry_index) const;

	/** Diese Methode gibt die Anzahl der Einträge zurück (<tt>0</tt>..<tt>1073741823</tt>).
	 * @return Anzahl der Einträge. */
	INT32 entryCount() const;

	/** Diese Methode gibt den Index des Eintrags zurück, dessen Schlüssel gleich dem gegebenen Schlüssel ist.
	 * Die Suche erfolgt ordnungs- oder streuwertbasiert, d.h. indiziert. Bei erfolgloser Suche wird <tt>-1</tt> geliefert.
	 * @param key gesuchter Schlüssel.
	 * @return Index des gefundenen Eintrags. */
	INT32 find(IAMArray const key) const;

	/** Diese Operator delegiert an @c entry(). */
	IAMEntry operator[](INT32 entry_index) const;

	/** Diese Operator delegiert an @c find(). */
	INT32 operator[](IAMArray const key) const;

	private:

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	Data::Ptr data_;

};

/** Diese Klasse implementiert ein Inhaltsverzeichnis als Zusammenstellung beliebig vieler Auflistungen (@c IAMListing) und Abbildungen (@c IAMMapping).
 * Das Inhaltsverzeichnis wird als Interpretation eines gegebenen Speicherbereichs realisiert und belegt zusätzlich <tt>32 + 24 * listingCount() + 40 * mappingCount()</tt> Byte.
 * Für tausend Auflistungen und tausend Abbildungen macht dies weniger als <tt>64 KB</tt>.
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
struct IAMIndex {

	/** Diese Klasse definiert die referenzgezählten Nutzdaten einer @c IAMIndex. */
	struct Data: public Obj<Data> {

		Data();

		~Data();

		private:

		friend IAMIndex;

		Data(INT32 const* array, INT32 length);

		/** Dieses Feld speichert die Auflistungen. */
		IAMListing* listing_array_;

		/** Dieses Feld speichert die Abbildungen. */
		IAMMapping* mapping_array_;

		/** Dieses Feld speichert die Anzahl der Auflistungen. */
		UINT32 listing_count_;

		/** Dieses Feld speichert die Anzahl der Abbildungen. */
		UINT32 mapping_count_;

		/** Dieses Feld speichert den Speicherbereich, aus dem diese Nutzdaten geladen wurden. */
		MMFArray file_data_;

		/** Dieses Feld speichert den Speicherbereich, aus dem diese Nutzdaten geladen wurden. */
		IAMArray heap_data_;

	};

	/** Dieser Konstruktor initialisiert ein leeres Inhaltsverzeichnis. */
	IAMIndex();

	/** Dieser Kontrukteur initialisiert das Inhaltsverzeichnis als Sicht auf den gegebenen Speicherbereich.
	 * @param heap_data Speicherbereich.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird. */
	IAMIndex(IAMArray const& heap_data);

	/** Dieser Kontrukteur initialisiert das Inhaltsverzeichnis als Sicht auf den gegebenen Speicherbereich.
	 * @param file_data Speicherbereich.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird. */
	IAMIndex(MMFArray const& file_data);

	/** Dieser Kontrukteur initialisiert das Inhaltsverzeichnis als Sicht auf den gegebenen Speicherbereich.
	 * @param array Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param length Anzahl der Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird. */
	IAMIndex(INT32 const* array, INT32 length);

	/** Diese Methode prüft die Kodierung der Längen der Zahlenfolgen in den Abbildungen und Auflistungen.
	 * @throws IAMException Wenn die Kodierung ungültig ist. */
	void check() const;

	/** Diese Methode gibt die <em>index-te</em> Auflistung zurück.
	 * Bei einem ungültigen <em>index</em> wird die leere Auflistung geliefert.
	 * @param index Index.
	 * @return 	<em>index-te</em> Auflistung. */
	IAMListing listing(INT32 index) const;

	/** Diese Methode gibt die Anzahl der Auflistungen zurück (<tt>0</tt>..<tt>1073741823</tt>).
	 * @return Anzahl der Auflistungen. */
	INT32 listingCount() const;

	/** Diese Methode gibt die <em>index-te</em> Abbildung zurück.
	 * Bei einem ungültigen <em>index</em> wird eine leere Abbildung geliefert.
	 * @param index Index.
	 * @return 	<em>index-te</em> Abbildung. */
	IAMMapping mapping(INT32 index) const;

	/** Diese Methode gibt die Anzahl der Abbildungen zurück (<tt>0</tt>..<tt>1073741823</tt>).
	 * @return Anzahl der Abbildungen. */
	INT32 mappingCount() const;

	private:

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	Data::Ptr data_;

};

/** Diese Klasse definiert die Exception, die bei Dekodierungsfehlern ausgelöst wird. */
struct IAMException {

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn einer ungültigen Anzahl bzw. eines ungültigen Werts. */
	static INT8 const INVALID_VALUE = 1;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn ungültiger Startpositionen, d.h. negativer Längen. */
	static INT8 const INVALID_OFFSET = 2;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn eines ungenügend großen Speicherbereichs. */
	static INT8 const INVALID_LENGTH = 4;

	/** Dieses Feld identifiziert die Ausnahme bei der Erkennugn einer unbekannten Datentypkennung. */
	static INT8 const INVALID_HEADER = 8;

	/** Dieser Konstrukteur initialisiert die Fehlerursache.
	 * @param code Fehlerursache. */
	IAMException(INT8 const code);

	/** Diese Methode gibt die Fehlerursache zurück.
	 * @return Fehlerursache. */
	INT8 code() const;

	private:

	/** Dieses Feld speichert die Fehlerursache. */
	INT8 code_;

};

}

#endif
