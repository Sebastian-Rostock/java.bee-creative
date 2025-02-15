package bee.creative.kb;

/** Diese Schnittstelle definiert den Empfänger der Referenzen für {@link KBEdges#forEach(KBEdgesTask)}. */
public interface KBEdgesTask {

	/** Diese Methode verarbeitet die gegebene Quellreferenz {@code sourceRef}, Zielreferenz {@code targetRef} und Beziehungsreferenz {@code relationRef} einer
	 * {@link KBEdge Kante}. */
	void run(int sourceRef, int targetRef, int relationRef);

}