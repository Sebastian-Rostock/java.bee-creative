/** Dieses Modul definiert <em>scope-guards</em> zum abbrechbaren Freigeben von Werten und Arrays.
 * @author [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_DEL_HPP
#define BEE_CREATIVE_DEL_HPP

namespace bee_creative {

/** Diese Klasse implementiert einen <em>scope-guard</em>, der ein gegebenes Element abbrechbar freigibt.
 * @tparam T Typ des Elements. */
template<typename T> //
struct DeleteValueGuard {

	/** Dieser Konstruktor initialisiert das Element, das im Destruktor  freigegeben wird.
	 * @param _item Objekt. */
	explicit DeleteValueGuard(T *_value) : value_(_value) {
	}

	/** Dieser Destruktor gibt das Element frei. */
	~DeleteValueGuard() {
		delete value_;
	}

	/** Diese Methode bricht die Freigabe des Objekts ab.<br>
	 * Hierbei wird der Verweis auf das Element auf <tt>0</tt> gesetzt. */
	void cancel() {
		value_ = 0;
	}

	/** Dieser Operator gibt die Verweis auf das Element zurück.
	 * @return Referenz. */
	T& operator*() const {
		return *value_;
	}

	/** Dieser Operator gibt den Verweis auf das Element zurück.
	 * @return Zeiger. */
	T* operator->() const {
		return value_;
	}

	private:

	/** Dieses Feld speichert das Objekt. */
	T *value_;

};

/** Diese Klasse implementiert einen <em>scope-guard</em>, der ein gegebenes abbrechbar Array freigibt.
 * @tparam ITEM Typ der Elemente des Arrays. */
template<typename T> //
struct DeleteArrayGuard {

	/** Dieser Konstruktor initialisiert das Array mit <tt>0</tt>. */
	DeleteArrayGuard() : array_(0) {
	}

	/** Dieser Konstruktor initialisiert das Array, das im Destruktor freigegeben wird.
	 * @param _array Array. */
	DeleteArrayGuard(T *_array) : array_(_array) {
	}

	/** Dieser Destruktor gibt das Array frei. */
	~DeleteArrayGuard() {
		delete[] array_;
	}

	/** Diese Methode bricht die Freigabe des Arrays ab.<br>
	 * Hierbei wird der Verweis auf das Array auf <tt>0</tt> gesetzt. */
	void cancel() {
		array_ = 0;
	}

	/** Diese Methode gibt den Verweis auf das Array zurück.*/
	T* array() {
		return array_;
	}

	/** Diese Methode setzt den Verweis auf das Array.
	 * @param _array Array. */
	void array(T *_array) {
		array_ = _array;
	}

	/** Dieser Operator gibt die Verweis auf das Array zurück.
	 * @return Referenz. */
	T& operator*() const {
		return *array_;
	}

	/** Dieser Operator gibt den Verweis auf das Array zurück.
	 * @return Zeiger. */
	T* operator->() const {
		return array_;
	}

	private:

	/** Dieses Feld speichert das Array. */
	T *array_;

};

}

#endif
