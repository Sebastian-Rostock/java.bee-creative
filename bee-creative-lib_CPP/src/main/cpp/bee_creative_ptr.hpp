/** Dieses Modul definiert grundlegende Datentypen zur Verwaltung referenzgezählter Objekte.
 * @author [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_PTR_HPP
#define BEE_CREATIVE_PTR_HPP

#include <boost/smart_ptr/intrusive_ref_counter.hpp>
#include <boost/smart_ptr/intrusive_ptr.hpp>

namespace bee_creative {

/** Diese Klasse implementiert ein referenzgezähltes Objekt.<br>
 * Referenzgezählte Objekte werden als Nachfahren dieser Klasse definiert und über @c Ref referenziert.<p>
 * Der Referenzzähler ist <em>thread-safe</em> und wird intern über @c boost::sp_adl_block::intrusive_ref_counter angebunden.
 * @tparam T Typ des referenzgezählten Objekts, das von dieser Klasse erbt. */
template<typename T> //
struct Obj: public boost::sp_adl_block::intrusive_ref_counter<T> {

	/** Diese Klasse implementiert einen Verweis auf ein referenzgezähltes Objekt.<p>
	 * Intern wird ein @c boost::intrusive_ptr genutzt. */
	struct Ptr: public boost::intrusive_ptr<T> {

		/** Dieser Konstruktor initialisiert den Verweis mit <tt>null</tt>. */
		Ptr() : boost::intrusive_ptr<T>() {
		}

		/** Dieser Konstruktor initialisiert den Verweis auf das gegebene referenzgezählte Objekt.
		 * @param object referenzgezähltes Objekt oder <tt>null</tt>.
		 * @param count <tt>true</tt>, wenn der Referenzzähler des Objekts erhöht werden soll;<br><tt>false</tt>, wenn der Referenzzähler des Objekts nicht verändert werden soll. */
		Ptr(T* object, bool count = true) : boost::intrusive_ptr<T>(object, count) {
		}

		/** Dieser Konstruktor kopiert den gegebenen Verweis.
		 * @param source Verweis. */
		Ptr(Ptr const& source) : boost::intrusive_ptr<T>(source) {
		}

	};

	/** Dieser Konstruktor initialisiert den Referenzzähler mit <tt>0</tt>. */
	Obj() : boost::sp_adl_block::intrusive_ref_counter<T>() {
	}

	/** Dieser Konstruktor initialisiert den Referenzzähler mit <tt>0</tt>. */
	Obj(Obj<T> const& _source) : boost::sp_adl_block::intrusive_ref_counter<T>(_source) {
	}

};

}

#endif
