package bee.creative._dev_.sts;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import bee.creative._dev_.sts.STSItemSet.ItemIndex;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Speicher zur Verwaltung eines Graphe aus {@link STSNode Knoten} und {@link SPOEdge Kanten}, bei welchem jeder Knoten einen
 * {@link STSNode#localname() Lokalnamen} bezüglich eines {@link STSNode#namespace() Namensraums} besitzt und jede Kante eine Verbindung dreier Knoten in den
 * Rollen {@link SPOEdge#subject() Subjekt}, {@link SPOEdge#subject() Prädikat} und {@link SPOEdge#subject() Objekt} darstellt.
 * 
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class SPOStore {

	private static int nextHash;

	private static synchronized int newHash() {
		return Objects.hashPush(++nextHash, 0);
	}

	public static SPOStore newHeapStore() {
		// alles im ram
		return null;
	}

	public static SPOStore newFileStore(File path) {
		// verzeichnis, in welchem die dateien mit festgelegten namen enthalten sind
		return null;
	}

	final int hash = newHash();

	/** Diese Methode erzeugt einen nicht persistierten Knoten und gibt ihn zurück.
	 * 
	 * @param ns Namensraum.
	 * @param ln Lokalname.
	 * @return neuer ungespeicherter Knoten. */
	public abstract STSNode newNode(String ns, String ln);

	public abstract STSNode getNode(String ns, String ln);

	public abstract STSNode putNode(String ns, String ln);

	/** Diese Methode gibt das zurück.
	 * 
	 * @param fields Auflistung von Namensraum und Lokalname.
	 * @return */
	public abstract STSNode[] putNodes(String... fields);

	public abstract SPOEdge getEdge(STSNode s, STSNode p, STSNode o);

	public abstract SPOEdge putEdge(STSNode s, STSNode p, STSNode o);

	public abstract SPOEdge[] putEdges(SPOEdge... edges);

	public abstract SPOEdge[] putEdges(STSNode... edges);

	public abstract Iterator<STSNode> nodeIterator(String ns, String ln);

	/** Diese Methode gibt den {@link Iterator} über die Assoziationen zurück, in denen die gegebenen Komponenten enthalten sind.<br>
	 * Eine Komponenten gilt als nicht eingeschränkt, wenn das entsprechende Filterkriterium {@code null} ist.
	 * 
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	public abstract Iterator<SPOEdge> edgeIterator(STSNode s, STSNode p, STSNode o);

	/** Diese Methode gibt den {@link Iterator} über die Assoziationen zurück, in denen die gegebenen Komponenten enthalten sind.<br>
	 * Eine Komponenten gilt als nicht eingeschränkt, wenn das entsprechende Filterkriterium {@code null} ist.
	 * 
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Assoziationen. */
	public abstract Iterator<SPOEdge> edgeIterator(Set<STSNode> s, Set<STSNode> p, Set<STSNode> o);

	protected abstract String getNodeNamespace(int nodeIndex);

	protected abstract String getNodeLocalname(int index);

	protected int customNodeCount() {
		return 0;
	}

	protected Iterator<STSNode> customNodeIterator(ItemIndex index) {
		return null;
	}

}
