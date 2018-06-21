/* [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_BEX_HPP

#define BEE_CREATIVE_BEX_HPP

#include "bee_creative_iam.hpp"

namespace bee {

namespace creative {

/**
 * Dieser Namensraum realisiert eine nurlesbare Implementation des <i>BEX</i>.<p>
 * Das <i>BEX – Binary Encoded XML</i> beschreibt eine nur lesbare Vereinfachung des <i>Document Object Model (DOM)</i> sowie ein binäres Datenformat zur redundanzarmen Abbildung der Daten eines <i>DOM</i> Dokuments. Ziel dieses Formats ist es, eine leichtgewichtige, nur lesende <i>DOM</i>-Implementation darauf aufsetzen zu können, welche signifikant weniger Arbeitsspeicher verbraucht, als eine zumeist auch modifizierende Implementation einer Standard <i>XML</i> Bibliothek.
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
namespace bex {

using std::string;

using mmf::MMFArray;
using iam::IAMListing;
using iam::IAMArray;
using iam::IAMIndex;
using iam::IAMException;

struct BEXNode;

struct BEXList;

/**
 * Diese Klasse implementiert die Verwaltung aller Element-, Text- und Attributknoten sowie aller Kind- und Attributknotenlisten, die in einem Dokument (vgl. XML Datei) enthalten sind.
 */
struct BEXFile {

	public:

	struct OBJECT: public RCObject<OBJECT> {

		public:

		OBJECT();

		private:

		friend BEXFile;

		friend BEXNode;

		friend BEXList;

		OBJECT(INT32 const* _array, INT32 const _length);

		MMFArray _fileData_;

		IAMArray _heapData_;

		INT32 _rootRef_;

		IAMIndex _nodeData_;

		IAMListing _attrUriText_;

		IAMListing _attrNameText_;

		IAMListing _attrValueText_;

		IAMListing _chldUriText_;

		IAMListing _chldNameText_;

		IAMListing _chldValueText_;

		IAMArray _attrUriRef_;

		IAMArray _attrNameRef_;

		IAMArray _attrValueRef_;

		IAMArray _attrParentRef_;

		IAMArray _chldUriRef_;

		IAMArray _chldNameRef_;

		IAMArray _chldContentRef_;

		IAMArray _chldAttributesRef_;

		IAMArray _chldParentRef_;

		IAMArray _chldListRange_;

		IAMArray _attrListRange_;

	};

	/**
	 * Dieser Kontrukteur erstellt eine leere Verwaltung.
	 */
	BEXFile();

	/**
	 * TODO
	 * @param _heap
	 */
	BEXFile(IAMArray const& _heapData);

	/**
	 * Dieser Kontrukteur initialisiert die Verwaltung mit den Daten der gegebenen Datei.
	 *
	 * @param _file Datei.
	 * @throws IAMException Wenn beim dekodieren der Datei ein Fehler erkannt wird.
	 */
	BEXFile(MMFArray const& _fileData);

	/**
	 * Dieser Kontrukteur initialisiert die Verwaltung als Sicht auf den gegebenen Speicherbereich.
	 *
	 * @param _array Zeiger auf den Speicherbereich mit @c INT32 Zahlen.
	 * @param _length Anzahl der Zahlen.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 */
	BEXFile(INT32 const* _array, INT32 const _length);

	/**
	 * Diese Methode gibt das Wurzelelement des Dokuments zurück.
	 *
	 * @return Wurzelelement.
	 */
	BEXNode root();

	/**
	 * Diese Methode gibt die Knotenliste mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird eine undefinierte Knotenliste geliefert. Der gegebene Identifikator kann von dem der gelieferten Knotenliste abweichen.
	 *
	 * @param key Identifikator.
	 * @return Knotenliste.
	 */
	BEXList list(UINT32 const key);

	/**
	 * Diese Methode gibt den Knoten mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird ein undefinierter Knoten geliefert. Der gegebene Identifikator kann von dem des gelieferten Knoten abweichen.
	 *
	 * @param key Identifikator.
	 * @return Knoten.
	 */
	BEXNode node(UINT32 const key);

	private:

	friend BEXNode;

	friend BEXList;

	RCPointer<OBJECT> _object_;

};

/**
 * Diese Klasse implementiert die homogene Sicht auf Element-, Text- und Attributknoten. In besonderen Fällen wird sie auch zur Abbildung undefinierter Knoten verwendet. Die aufsteigende Navigation von einem Kind- bzw. Attributknoten zu dessen Elternknoten ist optional.
 */
struct BEXNode {

	public:

	/**
	 * Dieses Feld speichert die Typkennung eines undefinierten Knoten.
	 */
	static UINT8 const VOID_NODE = 0;

	/**
	 * Dieses Feld speichert die Typkennung eines Elementknoten.
	 */
	static UINT8 const ELEM_NODE = 1;

	/**
	 * Dieses Feld speichert die Typkennung eines Attributknoten.
	 */
	static UINT8 const ATTR_NODE = 2;

	/**
	 * Dieses Feld speichert die Typkennung eines Textknoten.
	 */
	static UINT8 const TEXT_NODE = 3;

	/**
	 * Dieser Konstruktor initialisiert den undefinierten Knoten mit leerer Verwaltung.
	 */
	BEXNode();

	/**
	 * Dieser Konstruktor initialisiert den undefinierten Knoten.
	 *
	 * @param _owner Besitzer.
	 */
	BEXNode(BEXFile const& _owner);

	/**
	 * Diese Methode gibt den Identifikator dieses Knoten zurück.
	 *
	 * @return Identifikator.
	 */
	UINT32 key() const;

	/**
	 * Diese Methode gibt die Typkennung dieses Knoten zurück. Die Typkennung ist bei einem Attributknoten «1», bei einem Elementknoten «2», bei einem Textknoten «3» und bei einem undefinierten Knoten «0».
	 *
	 * @return Typkennung.
	 */
	UINT8 type() const;

	/**
	 * Diese Methode gibt das diesen Knoten verwaltende Objekt zurück.
	 *
	 * @return Besitzer.
	 */
	BEXFile owner() const;

	/**
	 * Diese Methode gibt den URI des Namensraums dieses Knoten als Zeichenkette zurück. Der URI eines Textknoten, eines Element- bzw. Attributknoten ohne Namensraum sowie eines undefinierten Knoten ist leer.
	 *
	 * @return Uri.
	 */
	string uri() const;

	/**
	 * Diese Methode gibt den Namen dieses Knoten als Zeichenkette zurück. Der Name eines Textknoten sowie eines undefinierten Knoten ist leer.
	 *
	 * @return Name.
	 */
	string name() const;

	/**
	 * Diese Methode gibt den Wert dieses Knoten als Zeichenkette zurück. Der Wert eines Elementknoten ohne Kindknoten sowie eines undefinierten Knoten ist leer. Der Wert eines Elementknoten mit Kindknoten entspricht dem Wert seines ersten Kindknoten.
	 *
	 * @return Wert.
	 */
	string value() const;

	/**
	 * Diese Methode gibt die Position dieses Knoten in der Kind- bzw. Attributknotenliste des Elternknotens zurück (optional). Die Position eines undefinierten Knoten ist «-1». Wenn die Navigation zum Elternknoten deaktiviert ist, ist die Position jedes Knoten «-1».
	 *
	 * @return Position.
	 */
	INT32 index() const;

	/**
	 * Diese Methode gibt den Elternknoten dieses Knoten zurück (optional). Der Elternknoten des Wurzelelementknoten sowie eines undefinierten Knoten ist ein undefinierter Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jedes Knoten ein undefinierter Knoten.
	 *
	 * @return Elternknoten.
	 */
	BEXNode parent() const;

	/**
	 * Diese Methode gibt die Kindknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributknoten sowie eines undefinierten Knoten ist eine undefinierte Knotenliste.
	 *
	 * @return Kindknotenliste.
	 */
	BEXList children() const;

	/**
	 * Diese Methode gibt die Attributknotenliste dieses Knoten zurück. Die Kindknotenliste eines Text- bzw. Attributknoten sowie eines undefinierten Knoten ist eine undefinierte Knotenliste.
	 *
	 * @return Attributknotenliste.
	 */
	BEXList attributes() const;

	private:

	friend BEXFile;

	friend BEXList;

	BEXNode(UINT32 const key, BEXFile const& owner);

	UINT32 _key_;

	BEXFile _owner_;

};

/**
 * Diese Klasse implementiert die homogene Sicht auf Kind- und Attributknotenlisten. Die aufsteigende Navigation von einer Knotenliste zu deren Elternknoten ist optional.
 */
struct BEXList {

	public:

	/**
	 * Dieses Feld speichert die Typkennung einer undefinierten Knotenliste.
	 */
	static UINT8 const VOID_LIST = 0;

	/**
	 * Dieses Feld speichert die Typkennung einer Kindknotenliste.
	 */
	static UINT8 const CHLD_LIST = 1;

	/**
	 * Dieses Feld speichert die Typkennung einer Attributknotenliste.
	 */
	static UINT8 const ATTR_LIST = 2;

	/**
	 * Dieser Konstruktor initialisiert die undefinierte Knotenliste mit leerer Verwaltung.
	 */
	BEXList();

	/**
	 * Dieser Konstruktor initialisiert die undefinierte Knotenliste.
	 *
	 * @param _owner Besitzer.
	 */
	BEXList(BEXFile const& _owner);

	/**
	 * Diese Methode gibt den Identifikator dieser Knotenliste zurück.
	 *
	 * @return TODO
	 */
	UINT32 key() const;

	/**
	 * Diese Methode gibt die Typkennung dieser Knotenliste zurück. Die Typkennung ist bei einer Attributknotenliste «1», bei einer allgemeinen Kindknotenliste «2» und bei einer undefinierten Knotenliste «0».
	 *
	 * @return TODO
	 */
	UINT8 type() const;

	/**
	 * Diese Methode gibt das diese Knotenliste verwaltende Objekt zurück.
	 *
	 * @return TODO
	 */
	BEXFile owner() const;

	/**
	 * Diese Methode gibt den «index»-ten Knoten dieser Knotenliste zurück. Bei einem ungültigen «index» wird ein undefinierter Knoten geliefert.
	 *
	 * @param index
	 * @return TODO
	 */
	BEXNode get(INT32 const index) const;

	/**
	 * Diese Methode sucht linear ab der gegebenen «start»-Position den ersten Element- bzw. Attributknoten mit der gegebenen «uri» sowie dem gegebenen «name» und gibt dessen Position zurück. Bei einer erfolglosen Suche wird «-1» geliefert. Ein leerer «uri» bzw. «name» wird bei der Suche ignoriert, d.h. der gesuchte Knoten hat einen beliebigen URI bzw. Namen. Bei einer negativen «start»-Position wird immer «-1» geliefert.
	 *
	 * @param uri TODO
	 * @param name TODO
	 * @param start TODO
	 * @return TODO
	 */
	INT32 find(string const& uri, string const& name, INT32 const start = 0) const;

	/**
	 * Diese Methode gibt die Länge dieser Knotenliste zurück. Die Länge ist bei einer undefinierten Knotenliste «0».
	 *
	 * @return TODO
	 */
	INT32 length() const;

	/**
	 * Diese Methode gibt den Elternknoten dieser Knotenliste zurück (optional). Der Elternknoten ist bei einer undefinierten Knotenliste ein undefinierter Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jeder Knotenliste ein undefinierter Knoten.
	 *
	 * @return TODO
	 */
	BEXNode parent() const;

	private:

	friend BEXFile;

	friend BEXNode;

	BEXList(UINT32 const key, UINT32 const ref, BEXFile const& owner);

	/**
	 * TODO
	 */
	UINT32 _key_;

	/**
	 * TODO
	 */
	UINT32 _ref_;

	/**
	 * TODO
	 */
	BEXFile _owner_;

};

}

}

}

#endif
