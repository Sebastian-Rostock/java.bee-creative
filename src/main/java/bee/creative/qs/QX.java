package bee.creative.qs;

/** Diese Schnittstelle definiert ein Objekt mit Bezg zu einem {@link #store() Quad-Store} - einem Speicher für einen Hypergraphen vierter Ordnung.
 * 
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QX {

	/** Diese Methode gibt den Graphspeicher zurück, der den Inhalt dieses Objekts verwaltet.
	 * 
	 * @return Graphspeicher. */
	QS store();

}
