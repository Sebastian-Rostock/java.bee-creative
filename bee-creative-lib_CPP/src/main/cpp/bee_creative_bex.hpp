/*Dieses Modul realisiert eine nurlesbare Implementation des <i>BEX</i>.<p>
 * Das <i>BEX – Binary Encoded XML</i> beschreibt eine nur lesbare Vereinfachung des <em>Document Object Model (DOM)</em> sowie ein binäres Datenformat zur redundanzarmen Abbildung der Daten eines <i>DOM</i> Dokuments. Ziel dieses Formats ist es, eine leichtgewichtige, nur lesende <i>DOM</i>-Implementation darauf aufsetzen zu können, welche signifikant weniger Arbeitsspeicher verbraucht, als eine zumeist auch modifizierende Implementation einer Standard <em>XML</em> Bibliothek.
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
/* [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_BEX_HPP
#define BEE_CREATIVE_BEX_HPP

#include "bee_creative_iam.hpp"

namespace bee_creative {

struct BEXNode;

struct BEXList;

/**Diese Klasse implementiert die Verwaltung aller Element-, Text- und Attributknoten sowie aller Kind- und Attributknotenlisten, die in einem XML Dokument enthalten sind. */
struct BEXFile {

	/** Diese Klasse definiert die referenzgezählten Nutzdaten eines @c BEXFile. */
	struct Data: public Obj<Data> {

		public:

		Data();

		private:

		friend BEXFile;

		friend BEXNode;

		friend BEXList;

		Data(IAMIndex const& base_data);

		/** Dieses Feld speichert das die Zahlenfolgen verwaltende Inhaltsverzeichnis. */
		IAMIndex file_data_;

		/** Dieses Feld speichert die URI der Attributknoten. */
		IAMListing attr_uri_text_;

		/** Dieses Feld speichert die Namen der Attributknoten. */
		IAMListing attr_name_text_;

		/** Dieses Feld speichert die Werte der Attributknoten. */
		IAMListing attr_value_text_;

		/** Dieses Feld speichert die URI der Elementknoten. */
		IAMListing chld_uri_text_;

		/** Dieses Feld speichert die Namen der Elementknoten. */
		IAMListing chld_name_text_;

		/** Dieses Feld speichert die Werte der Textknoten. */
		IAMListing chld_value_text_;

		/** Dieses Feld speichert die URI-Spalte der Attributknotentabelle. */
		IAMArray attr_uri_ref_;

		/** Dieses Feld speichert die Name-Spalte der Attributknotentabelle. */
		IAMArray attr_name_ref_;

		/** Dieses Feld speichert die Wert-Spalte der Attributknotentabelle. */
		IAMArray attr_value_ref_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Attributknotentabelle. */
		IAMArray attr_parent_ref_;

		/** Dieses Feld speichert die URI-Spalte der Kindknotentabelle. */
		IAMArray chld_uri_ref_;

		/** Dieses Feld speichert die Name-Spalte der Kindknotentabelle. */
		IAMArray chld_name_ref_;

		/** Dieses Feld speichert die Inhalt-Spalte der Kindknotentabelle. */
		IAMArray chld_content_ref_;

		/** Dieses Feld speichert die Attribut-Spalte der Kindknotentabelle. */
		IAMArray chld_attributes_ref_;

		/** Dieses Feld speichert die Elternknoten-Spalte der Kindknotentabelle. */
		IAMArray chld_rarent_ref_;

		/** Dieses Feld speichert Kindknotenlisten als Abschnitte der Kindknotentabelle. */
		IAMArray chld_list_range_;

		/** Dieses Feld speichert Attributknotenlisten als Abschnitte der Attributknotentabelle. */
		IAMArray attr_list_range_;

		/** Dieses Feld speichert die Referenz des Wurzelelements. */
		INT32 root_ref_;

	};

	/** Dieser Kontrukteur erstellt eine leere Verwaltung. */
	BEXFile();

	/** Dieser Kontrukteur initialisiert die Verwaltung als Sicht auf den gegebenen Auflistungen und Abbildungen.
	 * @param base_data Auflistungen und Abbildungen.
	 * @throws IAMException Wenn beim dekodieren der Auflistungen und Abbildungen ein Fehler erkannt wird. */
	BEXFile(IAMIndex const& base_data);

	/** Diese Methode gibt das Wurzelelement des Dokuments zurück.
	 * @return Wurzelelement. */
	BEXNode root() const;

	/** Diese Methode gibt die Knotenliste mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird eine undefinierte Knotenliste geliefert. Der gegebene Identifikator kann von dem der gelieferten Knotenliste abweichen.
	 * @param key Identifikator.
	 * @return Knotenliste. */
	BEXList list(UINT32 const key) const;

	/** Diese Methode gibt den Knoten mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird ein undefinierter Knoten geliefert. Der gegebene Identifikator kann von dem des gelieferten Knoten abweichen.
	 * @param key Identifikator.
	 * @return Knoten. */
	BEXNode node(UINT32 const key) const;

	private:

	friend BEXNode;

	friend BEXList;

	/** Dieses Feld speichert die referenzgezählten Nutzdaten. */
	Data::Ptr data_;

};

/** Diese Klasse implementiert die homogene Sicht auf Element-, Text- und Attributknoten. In besonderen Fällen wird sie auch zur Abbildung undefinierter Knoten verwendet. Die aufsteigende Navigation von einem Kind- bzw. Attributknoten zu dessen Elternknoten ist optional. */
struct BEXNode {

	/** Dieses Feld speichert die Typkennung eines undefinierten Knoten. */
	static UINT8 const VOID_NODE = 0;

	/** Dieses Feld speichert die Typkennung eines Elementknoten. */
	static UINT8 const ELEM_NODE = 1;

	/** Dieses Feld speichert die Typkennung eines Attributknoten. */
	static UINT8 const ATTR_NODE = 2;

	/** Dieses Feld speichert die Typkennung eines Textknoten. */
	static UINT8 const TEXT_NODE = 3;

	/** Dieser Konstruktor initialisiert den undefinierten Knoten mit leerer Verwaltung. */
	BEXNode();

	/** Dieser Konstruktor initialisiert den undefinierten Knoten.
	 * @param owner Besitzer. */
	BEXNode(BEXFile const& owner);

	/** Diese Methode gibt den Identifikator dieses Knoten zurück.
	 * @return Identifikator. */
	UINT32 key() const;

	/** Diese Methode gibt die Typkennung dieses Knoten zurück. Die Typkennung ist bei einem Attributknoten @c ATTR_NODE, bei einem Elementknoten @c ELEM_NODE, bei einem Textknoten @c TEXT_NODE und bei einem undefinierten Knoten @c VOID_NODE.
	 * @return Typkennung. */
	UINT8 type() const;

	/** Diese Methode gibt das diesen Knoten verwaltende @c BEXFile zurück.
	 * @return Besitzer. */
	BEXFile owner() const;

	/** Diese Methode gibt den URI des Namensraums dieses Knoten als Zeichenkette zurück. Der URI eines Textknoten, eines Element- bzw. Attributknoten ohne Namensraum sowie eines undefinierten Knoten ist leer.
	 * @return Uri. */
	std::string uri() const;

	/**Diese Methode gibt den Namen dieses Knoten als Zeichenkette zurück. Der Name eines Textknoten sowie eines undefinierten Knoten ist leer.
	 * @return Name. */
	std::string name() const;

	/** Diese Methode gibt den Wert dieses Knoten als Zeichenkette zurück. Der Wert eines Elementknoten ohne Kindknoten sowie eines undefinierten Knoten ist leer. Der Wert eines Elementknoten mit Kindknoten entspricht dem Wert seines ersten Kindknoten.
	 * @return Wert. */
	std::string value() const;

	/** Diese Methode gibt die Position dieses Knoten in der Kind- bzw. Attributknotenliste des Elternknotens zurück (optional). Die Position eines undefinierten Knoten ist «-1». Wenn die Navigation zum Elternknoten deaktiviert ist, ist die Position jedes Knoten «-1».
	 * @return Position. */
	INT32 index() const;

	/** Diese Methode gibt den Elternknoten dieses Knoten zurück (optional). Der Elternknoten des Wurzelelementknoten sowie eines undefinierten Knoten ist ein undefinierter Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jedes Knoten ein undefinierter Knoten.
	 * @return Elternknoten. */
	BEXNode parent() const;

	/** Diese Methode gibt die Kindknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributknoten sowie eines undefinierten Knoten ist eine undefinierte Knotenliste.
	 * @return Kindknotenliste. */
	BEXList children() const {
		UINT32 key = key_;
		switch (bex_type_(key)) {
			case BEX_ELEM_NODE: {
				BEXFile::Data& data = *owner_.data_;
				INT32 index = bex_ref_(key);
				INT32 _content = data.chld_content_ref_.get(index);
				if (_content >= 0) return BEXList(owner_, bex_key_(BEX_CHTX_LIST, index), 0);

				return BEXList(owner_, bex_key_(BEX_CHLD_LIST, index), -_content);
			}
			case BEX_VOID_TYPE:
			case BEX_ATTR_NODE:
			case BEX_TEXT_NODE:
			case BEX_ELTX_NODE:
				return BEXList(owner_);
		}
		throw IAMException(IAMException::INVALID_HEADER);
	}

	/** Diese Methode gibt die Attributknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributknoten sowie eines undefinierten Knoten ist eine undefinierte Knotenliste.
	 * @return Attributknotenliste. */
	BEXList attributes() const;

	private:

	friend BEXFile;

	friend BEXList;

	BEXNode(BEXFile const& owner, UINT32 const key);

	/** Dieses Feld speichert den Besitzer. */
	BEXFile owner_;

	/** Dieses Feld speichert den Schlüssel. */
	UINT32 key_;

};

/** Diese Klasse implementiert die homogene Sicht auf Kind- und Attributknotenlisten. Die aufsteigende Navigation von einer Knotenliste zu deren Elternknoten ist optional. */
struct BEXList {

	/** Dieses Feld speichert die Typkennung einer undefinierten Knotenliste. */
	static UINT8 const VOID_LIST = 0;

	/** Dieses Feld speichert die Typkennung einer Kindknotenliste. */
	static UINT8 const CHLD_LIST = 1;

	/** Dieses Feld speichert die Typkennung einer Attributknotenliste. */
	static UINT8 const ATTR_LIST = 2;

	/** Dieser Konstruktor initialisiert die undefinierte Knotenliste mit leerer Verwaltung. */
	BEXList();

	/** Dieser Konstruktor initialisiert die undefinierte Knotenliste.
	 * @param owner Besitzer. */
	BEXList(BEXFile const& owner);

	/** Diese Methode gibt den Identifikator dieser Knotenliste zurück.
	 * @return Identifikator. */
	UINT32 key() const;

	/** Diese Methode gibt die Typkennung dieser Knotenliste zurück. Die Typkennung ist bei einer Attributknotenliste @c ATTR_LIST, bei einer allgemeinen Kindknotenliste @c CHLD_LIST und bei einer undefinierten Knotenliste @c VOID_LIST.
	 * @return Typkennung. */
	UINT8 type() const;

	/** Diese Methode gibt das diese Knotenliste verwaltende @c BEXFile zurück.
	 * @return Besitzer. */
	BEXFile owner() const;

	/** Diese Methode gibt den <em>index-ten</em> Knoten dieser Knotenliste zurück. Bei einem ungültigen <em>index</em> wird ein undefinierter Knoten geliefert.
	 * @param index Index.
	 * @return <em>index-ter</em> Knoten. */
	BEXNode get(INT32 const index) const;

	/** Diese Methode sucht linear ab der gegebenen <em>start</em>-Position den ersten Element- bzw. Attributknoten mit der gegebenen «uri» sowie dem gegebenen <em>name</em> und gibt dessen Position zurück. Bei einer erfolglosen Suche wird <tt>-1</tt> geliefert. Ein leerer <em>uri</em> bzw. <em>name</em> wird bei der Suche ignoriert, d.h. der gesuchte Knoten hat einen beliebigen URI bzw. Namen. Bei einer negativen <em>start</em>-Position wird immer <tt>-1</tt> geliefert.
	 * @param uri URI.
	 * @param name Name.
	 * @param start Position, ab der die Suche beginnt.
	 * @return Position des Treffers oder <tt>-1</tt>. */
	INT32 find(std::string const& uri, std::string const& name, INT32 const start = 0) const;

	/** Diese Methode gibt die Länge dieser Knotenliste zurück. Die Länge ist bei einer undefinierten Knotenliste <tt>0</tt>.
	 * @return Länge. */
	INT32 length() const;

	/** Diese Methode gibt den Elternknoten dieser Knotenliste zurück (optional). Der Elternknoten ist bei einer undefinierten Knotenliste ein undefinierter Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jeder Knotenliste ein undefinierter Knoten.
	 * @return Elternknoten. */
	BEXNode parent() const;

	private:

	friend BEXFile;

	friend BEXNode;

	BEXList(BEXFile const& owner, UINT32 const key, UINT32 const ref);

	/** Dieses Feld speichert den Besitzer. */
	BEXFile owner_;

	/** Dieses Feld speichert den Schlüssel. */
	UINT32 key_;

	/** Dieses Feld speichert die Referenz.
	 * @see BEXFile#attr_list_range_
	 * @see BEXFile#chld_list_range_ */
	UINT32 ref_;

};

}

#endif
