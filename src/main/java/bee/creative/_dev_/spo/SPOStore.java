package bee.creative._dev_.spo;

import java.util.Iterator;
import java.util.Set;

/** Diese Klasse implementiert den Speicher zur Verwaltung von {@link SPOEdge Tripen}.
 * 
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface SPOStore {

	/** Diese Methode erzeugt einen nicht persistierten Knoten und gibt ihn zurück.
	 * 
	 * @param ns Namensraum.
	 * @param ln Lokalname.
	 * @return neuer ungespeicherter Knoten. */
	SPONode newNode(String ns, String ln);

	SPONode getNode(String ns, String ln);

	SPONode putNode(String ns, String ln);

	/** Diese Methode gibt das zurück.
	 * 
	 * @param fields Auflistung von Namensraum und Lokalname.
	 * @return */
	SPONode[] putNodes(String... fields);

	SPOEdge getEdge(SPONode s, SPONode p, SPONode o);

	SPOEdge putEdge(SPONode s, SPONode p, SPONode o);

	SPOEdge[] putEdges(SPOEdge... edges);

	SPOEdge[] putEdges(SPONode... edges);
	
	Iterator<SPONode> nodeIterator(String ns, String ln);

	/** Diese Methode gibt den {@link Iterator} über die Assoziationen zurück, in denen die gegebenen Komponenten enthalten sind.<br>
	 * Eine Komponenten gilt als nicht eingeschränkt, wenn das entsprechende Filterkriterium {@code null} ist.
	 * 
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	Iterator<SPOEdge> edgeIterator(SPONode s, SPONode p, SPONode o);

	/** Diese Methode gibt den {@link Iterator} über die Assoziationen zurück, in denen die gegebenen Komponenten enthalten sind.<br>
	 * Eine Komponenten gilt als nicht eingeschränkt, wenn das entsprechende Filterkriterium {@code null} ist.
	 * 
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	Iterator<SPOEdge> edgeIterator(Set<SPONode> s, Set<SPONode> p, Set<SPONode> o);

}
