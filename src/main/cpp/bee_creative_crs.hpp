/** Dieses Modul vereinfacht die Handhabung kritischer Abschnitte.
 * @author [cc-by] 2013-2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */

#ifndef BEE_CREATIVE_CRS_HPP
#define BEE_CREATIVE_CRS_HPP

#include <windef.h>
#include <winbase.h>

namespace bee_creative {

/** Diese Klasse implementiert ein Objekt zum Schutz eines kritischen Abschnitts. Intern wird ein @c CRITICAL_SECTION genutzt. */
struct CriticalSection {

	/** Dieser Konstruktor initialisiert den kritischen Abschnitt. */
	CriticalSection() {
		InitializeCriticalSection(&critical_section_);
	}

	/** Dieser Konstruktor finalisiert den kritischen Abschnitt. */
	~CriticalSection() {
		DeleteCriticalSection(&critical_section_);
	}

	/** Diese Methode versucht den kritischen Abschnitt zu betreten.
	 * @return <tt>true</tt>, wenn der kritischen Abschnitt betreten;<br><tt>false</tt>, wenn er nicht betreten wurde, weil sich ein paralleler Verarbeitungsstrang darin befindet. */
	bool test() {
		return TryEnterCriticalSection(&critical_section_);
	}

	/** Diese Methode betritt den kritischen Abschnitt. Der aufruf blockiert, solange sich ein paralleler Verarbeitungsstrang darin befindet. */
	void enter() {
		EnterCriticalSection(&critical_section_);
	}

	/** Diese Methode verlässt den kritischen Abschnitt. Die bei @c enter() wartenden Verarbeitungsstränge werden automatisch fortgesetzt. */
	void leave() {
		LeaveCriticalSection(&critical_section_);
	}

	private:

	/** Dieses Feld speichert die Zustandsdaten. */
	CRITICAL_SECTION critical_section_;

};

/** Diese Klasse implementiert den <em>scope-guard</em> zu einem kritischen Abschnitt @c CSObject. */
struct CriticalSectionGuard {

	/** Dieser Konstruktor betritt den kritischen Abschnitt über @c CSObject.enter().
	 * @param _state kritischer Abschnitt. */
	explicit CriticalSectionGuard(CriticalSection &cs) : critical_section_(cs) {
		critical_section_.enter();
	}

	/** Dieser Destruktor verlässt den kritischen Abschnitt über @c CSObject.leave(). */
	~CriticalSectionGuard() {
		critical_section_.leave();
	}

	private:

	/** Dieses Feld verweist auf den kritischen Abschnitt. */
	CriticalSection &critical_section_;

};

}

#endif
