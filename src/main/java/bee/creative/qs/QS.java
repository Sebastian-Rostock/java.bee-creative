package bee.creative.qs;

/** Diese Schnittstelle definiert einen Graphspeicher für einen Hypergraphen vierter Ordnung (Quad-Store), dessen {@link QN Hyperknoten} über einen optionalen
 * identifizierenden {@link QN#value() Textwert} verfügen und dessen {@link QE Hyperkanten} jeweils vier Hyperknoten in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} referenzieren. Ein Hyperknoten kann dazu in jeder dieser Rollen
 * vorkommen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QS {

	/** Diese Methode gibt eine Mengensicht auf alle Hyperknoten zurück, die über einen {@link QN#value() Textwert} verfügen. Sie ist eine Abkürzung für
	 * {@link #values() this.values().nodes())}.
	 *
	 * @return Hyperknoten. */
	public QNSet nodes();

	/** Diese Methode gibt eine Mengensicht auf alle gespeicherten {@link QE Hyperkanten} zurück.
	 *
	 * @return Hyperkanten. */
	public QESet edges();

	/** Diese Methode gibt eine Mengensicht auf alle gespeicherten Textwerte zurück.
	 *
	 * @return Textwerte. */
	public QVSet values();

	/** Diese Methode ist eine Abkürzung für {@link #newEdge(QN) this.newEdge(this.newNode())}.
	 *
	 * @see #newNode()
	 * @return Hyperkante. */
	public QE newEdge();

	/** /** Diese Methode ist eine Abkürzung für {@link #newEdge(QN, QN, QN, QN) this.newEdge(node, node, node, node)}.
	 *
	 * @param node Hyperknoten.
	 * @return Hyperkante. */
	public QE newEdge(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine neue temporäre {@link QE Hyperkante} zurück, die von diesem Graphspeicher {@link QE#owner() verwaltet} wird und die gegebenen
	 * {@link QN Hyperknoten} mit den Rollen {@link QE#context() Kontext}, {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object()
	 * Objekt} verbindet. Die gelieferte Hyperkante wird dabei nicht in den Graphspeicher {@link QESet#putAll() eingefügt} und kann darüber hinaus bereits im
	 * Graphspeicher {@link QESet#hasAny() enthalten} sein.
	 *
	 * @param context {@link QE#context() Kontextknoten}.
	 * @param predicate {@link QE#predicate() Prädikatknoten}.
	 * @param subject {@link QE#subject() Subjektknoten}.
	 * @param object {@link QE#object() Objektknoten}.
	 * @return Hyperkante. */
	public QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(QE) this.newEdges(this.newEdge())}.
	 *
	 * @see #newEdge()
	 * @return temporäre Hyperkantenmenge. */
	public QESet newEdges();

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(Iterable) this.newEdges(Iterables.itemIterable(edge))}.
	 *
	 * @param edge Hyperkante.
	 * @return temporäre Hyperkantenmenge. */
	public QESet newEdges(final QE edge) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QE Hyperkanten} in eine von diesem Graphspeicher {@link QESet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 *
	 * @param edges Hyperkanten.
	 * @return temporäre Hyperkantenmenge. */
	public QESet newEdges(final Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt einen neuen temporären {@link QN Hyperknoten} zurück, der von diesem Graphspeicher {@link QN#owner() verwaltet} wird und dessen
	 * {@link QN#key() Kennung} in diesem Graphspeicher einzigartig ist. Der gelieferte Hyperknoten wird dabei nicht in den Graphspeicher eingefügt, kann aber
	 * bereits im Graphspeicher {@link QNSet#hasAny() enthalten} sein.
	 *
	 * @return Hyperknoten. */
	public QN newNode();

	/** Diese Methode gibt einen temporären {@link QN Hyperknoten} mit dem gegebenen {@link QN#value() Textwert} zurück, der von diesem Graphspeicher
	 * {@link QN#owner() verwaltet} wird und dessen Textwert in diesem Graphspeicher einzigartig ist. Der gelieferte Hyperknoten wird dabei zum Textwert
	 * ermittelt, wenn dieser bereits in den Graphspeicher eingefügt wurde. Andernfalls wird ein neuer Hyperknoten erzeugt und zusammen mit dem Textwert in den
	 * Graphspeicher eingefügt.<br>
	 * Die Textwert der in den Hyperkanten nicht mehr verwendten Hyperknoten zu entfernen
	 *
	 * @return Hyperknoten. */
	public QN newNode(String text);

	/** Diese Methode erzeigt die gegebene Anzahl {@link #newNode() neuer temporärer Hyperknoten} ung gibt diese in einer temporären Menge zurück.
	 *
	 * @param size Anzahl der Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	public QNSet newNodes(final long size) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newNodes(Iterable) this.newNodes(Iterables.itemIterable(node))}.
	 *
	 * @param node Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	public QNSet newNodes(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QE Hyperknoten} in eine von diesem Graphspeicher {@link QNSet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 *
	 * @param nodes Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	public QNSet newNodes(final Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newValues(Iterable) this.newValues(Iterables.itemIterable(value))}.
	 *
	 * @param value Textwert.
	 * @return temporäre Textwertmenge. */
	public QVSet newValues(final String value) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen Textwert in eine von diesem Graphspeicher {@link QVSet#owner() verwaltete} temporäre Menge und gibt diese zurück.
	 *
	 * @param values Textwerte.
	 * @return temporäre Textwertmenge. */
	public QVSet newValues(final Iterable<? extends String> values) throws NullPointerException, IllegalArgumentException;

}
