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

	public void setRootRef(int rootRef) {
		if (this.prevRootRef == null) {
			this.prevRootRef = this.getRootRef();
		}
		this.rootRef = rootRef;
	}

	public void setNextRef(int nextRef) {
		if (this.prevNextRef == null) {
			this.prevNextRef = this.getNextRef();
		}
		this.nextRef = nextRef;
	}

	int newEntityRef() {
		var nextRef = this.nextRef;
		while (isSourceRef(nextRef) || isTargetRef(nextRef))
			nextRef++;
		setNewEntityRef(nextRef + 1);
		return nextRef;
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

	boolean putSourceRef(int targetRef, int relationRef, int sourceRef) {
		var nsm = sourceMap;
		var psm = prevSourceMap;
		if (psm == null) {
			prevSourceMap = psm = REFMAP.copy(nsm);
		}
		psm = REFMAP.grow(nsm);
		var psi = REFMAP.getIdx(psm, sourceRef);
		var nsi = REFMAP.getIdx(nsm, sourceRef);

		return false;
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

		prevRootRef = null;
		prevNextRef = null;

		return null;
	}

	/** verwirft die änderungen seit dem letzten commit. das betrifft getRootRef, getEntityRefs, getSource..., getTarget... */
	BERUpdate rollback() {
		// der aktuelle
		var oldState = new BERState();
		// der in prev gespeicherte und wiederhergestellte
		var newState = new BERState();

		return null;
	}

	// verwirft die änderungen seit dem letzten commit.
	// stellt den gegebenen zustand wieder her.
	BERUpdate rollback(BERState state) {
		return null;
	}

	Integer prevRootRef;

	Integer prevNextRef;

	Object[] prevSourceMap = REFMAP.EMPTY;

	Object[] prevTargetMap = REFMAP.EMPTY;

}
