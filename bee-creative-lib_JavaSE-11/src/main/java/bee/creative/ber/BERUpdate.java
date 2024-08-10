package bee.creative.ber;

/** liefert schreibgeschützte sicht auf vorherigen und neuen datenstand sowie die differenz */
class BERUpdate {

	BERStore getStore() {
		return null;
	}

	BERState getOldState() {
		return null;
	}

	BERState getNewState() {
		return null;
	}

	// berechnet bei bedarf die in newState dazugekommenen inhalte
	BEREdges getPutEdges() {
		return null;
	}

	// berechnet bei bedarf die in newState entfallenen inhalte
	BEREdges getPopEdges() {
		return null;
	}
	
	
	
	
}
