package bee.creative.ber;

/** Diese Klasse implementiert einen bidirectional-entity-relation Speicher.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class BERStore extends BERState {

	BERStore(Object owner) {

	}

	Object getOwner() {
		return null;
	}

	void putEdges(BEREdges edges) {

	}

	void popEdges(BEREdges edges) {

	}

	void setRootRef(int rootRef) {

	}

	int newEntityRef() {
		return 0;
	}

	/** Diese Methode gibt das zurück. wenn gegebener nodeRef als source oder target vorkommt, in cow-nodeRefGen eintragen und aus source/target maps entfernen
	 * 
	 * @param entityRef */
	void popEntityRef(int entityRef) {

	}

	/** Diese Methode gibt das zurück. übernimmt die gegebenen refs zur wiederverwendung. weitere werden nach dem größten automatisch ergänzt. duplikate und refs
	 * <=0 nicht zulässig. leere liste nicht zulässig. */
	void setNewEntityRef(int entityRef) {

	}

	/** Diese Methode gibt das zurück. ersetzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der
	 * einzigartigen referenzen kopiert diese an den beginn von sourceRefs
	 * 
	 * @param targetRef
	 * @param relationRef
	 * @param sourceRefs
	 * @return */
	int setSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	/** ergänzt die als source von target und rel vorkommenden referenzen mit den > 0 gegebenen liefert die anzahl der ergänzten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int putSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	/** entfernt von den als source von target und rel vorkommenden referenzen die > 0 gegebenen liefert die anzahl der entfernten referenzen kopiert diese an den
	 * beginn von sourceRefs */
	int popSourceRefs(int targetRef, int relationRef, int[] sourceRefs) {
		return 0;
	}

	BERUpdate commit() {
		return null;
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	BERUpdate rollback() {
		return null;
	}

	// verwirft die änderungen seit dem letzten commit.
	// stellt den gegebenen zustand wieder her.
	BERUpdate rollback(BERState state) {
		return null;
	}

}
