package bee.creative.qs;

/** Diese Schnittstelle definiert einen Graphspeicher für einen Hypergraphen vierter Ordnung (Quad-Store), dessen {@link QN Hyperknoten} über einen optionalen
 * {@link QN#get() Textwert} verfügen und dessen {@link QE Hyperkanten} jeweils vier Hyperknoten in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} referenzieren. Ein Hyperknoten kann dazu in jeder dieser Rollen
 * vorkommen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QS {

	/** Diese Methode gibt eine Mengensicht auf aller die diesem Graphspeicher enthaltenen {@link QE Hyperkanten} zurück.
	 * 
	 * @return Hyperkanten. */
	public QESet edges();

	/** Diese Methode gibt eine Mengensicht auf alls in diesem Graphspeicher enthaltenen {@link QV Wertzuweisung} zurück.
	 *
	 * @return Wertzuweisungen. */
	public QVSet values();

	/** Diese Methode gibt eine neue temporäre {@link QE Hyperkante} zurück, die von diesem Graphspeicher {@link QE#owner() verwaltet} wird und die gegebenen
	 * {@link QN Hyperknoten} mit den Rollen {@link QE#context() Kontext}, {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object()
	 * Objekt} verbindet. Die gelieferte Hyperkante wird dabei nicht in den Graphspeicher {@link QESet#putAll() eingefügt} und kann darüber hinaus bereits im
	 * Graphspeicher {@link QESet#hasAny() enthalten} sein.
	 * 
	 * @param context {@link QE#context() Kontextknoten}.
	 * @param predicate {@link QE#predicate() Prädikatknoten}.
	 * @param subject {@link QE#subject() Subjektknoten}.
	 * @param object {@link QE#object() Objektknoten}.
	 * @return temporäre Hyperkante. */
	public QE newEdge(final QN context, final QN predicate, final QN subject, final QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QE Hyperkanten} in eine von diesem Graphspeicher {@link QESet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 * 
	 * @param edges Hyperkanten.
	 * @return temporäre Hyperkantenmenge. */
	public QESet newEdges(final Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt einen neuen temporären {@link QN Hyperknoten} zurück, der von diesem Graphspeicher {@link QN#owner() verwaltet} wird und dessen
	 * {@link QN#id() Kennung} in diesem Graphspeicher einzigartig ist. Der gelieferte Hyperknoten wird dabei nicht in den Graphspeicher {@link QNSet#putAll()
	 * eingefügt} und kann darüber hinaus bereits im Graphspeicher {@link QNSet#hasAny() enthalten} sein.
	 * 
	 * @return temporärer Hyperknoten. */
	public QN newNode();

	/** Diese Methode überführt die gegebenen {@link QE Hyperknoten} in eine von diesem Graphspeicher {@link QNSet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 * 
	 * @param nodes Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	public QNSet newNodes(final Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine neue temporäre {@link QV Wertzuweisung} zurück, die von diesem Graphspeicher {@link QV#owner() verwaltet} wird und den gegebenen
	 * {@link QN Hyperknoten} mit dem ggebenen {@link QN#get() Textwert} verbindet. Die gelieferte Wertzuweisung wird dabei nicht in den Graphspeicher
	 * {@link QVSet#putAll() eingefügt} und kann darüber hinaus bereits im Graphspeicher {@link QVSet#hasAny() enthalten} sein.
	 * 
	 * @param node Hyperknoten.
	 * @param text Textwert.
	 * @return temporäre Wertzuweisung. */
	public QV newValue(final QN node, final String text) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QV Wertzuweisung} in eine von diesem Graphspeicher {@link QVSet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 * 
	 * @param values Wertzuweisungen.
	 * @return temporäre Wertzuweisungsmenge. */
	public QVSet newValues(final Iterable<? extends QV> values) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen Textwert in eine von diesem Graphspeicher {@link QSSet#owner() verwaltete} temporäre Menge und gibt diese zurück.
	 * 
	 * @param strings Textwerte.
	 * @return temporäre Textwertmenge. */
	public QSSet newString(final Iterable<? extends String> strings) throws NullPointerException, IllegalArgumentException;

}
