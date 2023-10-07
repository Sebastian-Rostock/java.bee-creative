package bee.creative.qs;

import java.util.List;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Translator2;

/** Diese Schnittstelle definiert einen Graphspeicher für einen Hypergraphen vierter Ordnung (Quad-Store), dessen {@link QN Hyperknoten} über einen optionalen
 * identifizierenden {@link QN#value() Textwert} verfügen und dessen {@link QE Hyperkanten} jeweils vier Hyperknoten in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} referenzieren. Ein Hyperknoten kann dazu in jeder dieser Rollen
 * vorkommen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QS {

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten}.
	 *
	 * @return Hyperkanten. */
	QESet edges();

	/** Diese Methode liefert die Mengensicht auf alle Hyperknoten, die über einen {@link QN#value() Textwert} verfügen. Sie ist eine Abkürzung für
	 * {@link #values() this.values().nodes())}.
	 *
	 * @return Hyperknoten. */
	QNSet nodes();

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten Textwerte.
	 *
	 * @return Textwerte. */
	QVSet values();

	/** Diese Methode liefert den {@link QN Hyperknoten}, der von diesem Graphspeicher {@link QN#owner() verwaltet} wird und dessen {@link QN#value() Textwert}
	 * gleich der {@link Object#toString() Textdarstellung} des gegebenen Objekts ist. Wenn dieser nicht existiert, wird {@code null} geliefert.
	 *
	 * @param value Textwert.
	 * @return Hyperknoten oder null. */
	QN getNode(Object value) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newEdge(QN) this.newEdge(this.newNode())}.
	 *
	 * @see #newNode()
	 * @return Hyperkante. */
	QE newEdge();

	/** /** Diese Methode ist eine Abkürzung für {@link #newEdge(QN, QN, QN, QN) this.newEdge(node, node, node, node)}.
	 *
	 * @param node Hyperknoten.
	 * @return Hyperkante. */
	QE newEdge(QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine temporäre {@link QE Hyperkante}, die von diesem Graphspeicher {@link QE#owner() verwaltet} wird und die gegebenen {@link QN
	 * Hyperknoten} mit den Rollen {@link QE#context() Kontext}, {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt}
	 * verbindet. Die gelieferte Hyperkante wird dabei nicht in den Graphspeicher {@link QE#put() eingefügt}. Sie kann zudem bereits im Graphspeicher
	 * {@link QE#state() enthalten} sein.
	 *
	 * @param context {@link QE#context() Kontextknoten}.
	 * @param predicate {@link QE#predicate() Prädikatknoten}.
	 * @param subject {@link QE#subject() Subjektknoten}.
	 * @param object {@link QE#object() Objektknoten}.
	 * @return Hyperkante. */
	QE newEdge(QN context, QN predicate, QN subject, QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(QE...) this.newEdges(this.newEdge())}.
	 *
	 * @see #newEdge()
	 * @return temporäre Hyperkantenmenge. */
	QESet newEdges();

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(QE...) this.newEdges(this.newEdge(node))}.
	 *
	 * @see #newEdge(QN)
	 * @return temporäre Hyperkantenmenge. */
	QESet newEdges(QN node);

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(QE...) this.newEdges(this.newEdge(context, predicate, subject, object))}.
	 *
	 * @see #newEdge(QN, QN, QN, QN)
	 * @return temporäre Hyperkantenmenge. */
	QESet newEdges(QN context, QN predicate, QN subject, QN object);

	/** Diese Methode ist eine Abkürzung für {@link #newEdges(Iterable) this.newEdges(Arrays.asList(edges))}.
	 *
	 * @param edges Hyperkanten.
	 * @return temporäre Hyperkantenmenge. */
	QESet newEdges(QE... edges) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QE Hyperkanten} in eine von diesem Graphspeicher {@link QESet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 *
	 * @param edges Hyperkanten.
	 * @return temporäre Hyperkantenmenge. */
	QESet newEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert einen neuen temporären {@link QN Hyperknoten}, der von diesem Graphspeicher {@link QN#owner() verwaltet} wird und dessen interne
	 * Kennung in diesem Graphspeicher einzigartig ist. Der gelieferte Hyperknoten wird dabei nicht in den Graphspeicher eingefügt. Dies kann nur {@link QE#put()
	 * indirekt} über eine {@link QE Hyperkante} erfolgen.
	 *
	 * @return Hyperknoten. */
	QN newNode();

	/** Diese Methode liefert den {@link QN Hyperknoten}, der von diesem Graphspeicher {@link QN#owner() verwaltet} wird und dessen {@link QN#value() Textwert}
	 * gleich der {@link Object#toString() Textdarstellung} des gegebenen Objekts ist. Der Hyperknoten wird bei Bedarf erzeugt und zusammen mit dem Textwert in
	 * den Graphspeicher eingefügt.
	 *
	 * @param value Textwert.
	 * @return Hyperknoten. */
	QN newNode(Object value) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newNodes(Iterable) this.newNodes(Arrays.asList(nodes))}.
	 *
	 * @param nodes Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	QNSet newNodes(QN... nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QE Hyperknoten} in eine von diesem Graphspeicher {@link QNSet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück.
	 *
	 * @param nodes Hyperknoten.
	 * @return temporäre Hyperknotenmenge. */
	QNSet newNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newValues(Iterable) this.newValues(Arrays.asList(values))}.
	 *
	 * @param values Textwerte.
	 * @return temporäre Textwertmenge. */
	QVSet newValues(Object... values) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die {@link Object#toString() Textdarstellungen} der gegebenen Objekte in eine von diesem Graphspeicher {@link QVSet#owner()
	 * verwaltete} temporäre Menge und gibt diese zurück.
	 *
	 * @param values Textwerte.
	 * @return temporäre Textwertmenge. */
	QVSet newValues(Iterable<?> values) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #newTuple(List) this.newTuple(Arrays.asList(nodes))}.
	 *
	 * @param nodes Hyperknoten.
	 * @return temporäres Hypertupel. */
	QT newTuple(QN... nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert ein temporäres {@link QT Hypertupel}, das von diesem Graphspeicher {@link QT#owner() verwaltet} wird und die gegebenen {@link QN
	 * Hyperknoten} in der gegebenen Reihenfolge miteinander verbindet. Die gegebene Liste darf nicht leer sein.
	 *
	 * @param nodes Hyperknoten.
	 * @return Hypertupel. */
	QT newTuple(List<? extends QN> nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QN Hyperknoten} in eine von diesem Graphspeicher {@link QTSet#owner() verwaltete} temporäre Menge von
	 * {@link QT Hypertupeln} und gibt diese zurück. Die Anzahl der gegebenen Hyperknoten muss ein ganzzahliges Vielfaches der {@link List#size() Anzahl} der
	 * gegebenen Rollennamen sein, da stets diese Anzahl direkt aufeinanderfolgender Hyperknoten zu einem Hypertupel miteinander verbunden wird. Die Rollennamen
	 * dürfen keine Duplikate enthalten und nicht leer sein.
	 *
	 * @param names Rollennamen.
	 * @param tuples Hyperknotentabelle.
	 * @return temporäre Hypertupelmenge. */
	QTSet newTuples(List<String> names, QN... tuples) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überführt die gegebenen {@link QT Hypertupel} in eine von diesem Graphspeicher {@link QTSet#owner() verwaltete} temporäre Menge und gibt
	 * diese zurück. Die {@link QT#size() Anzahl} der Hyperknoten eines jeden Hypertupel muss gleich der {@link List#size() Anzahl} der gegebenen Rollennamen
	 * sein. Die Rollennamen dürfen keine Duplikate enthalten und nicht leer sein.
	 *
	 * @param names Rollennamen.
	 * @param tuples Hypertupel.
	 * @return temporäre Hypertupelmenge. */
	QTSet newTuples(List<String> names, Iterable<? extends QT> tuples) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert einen {@link Translator2}, der einen {@link QN Hyperknoten} bidirektional in seinen {@link QN#value() Textwert} übersetzt.
	 *
	 * @return Übersetzer von Hyperknoten zu Textwerten und umgekehrt. */
	default Translator2<QN, String> valueTrans() {
		return new AbstractTranslator<>() {
	
			@Override
			public boolean isTarget(Object object) {
				return object instanceof String;
			}
	
			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}
	
			@Override
			public String toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? ((QN)object).value() : null;
			}
	
			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? QS.this.getNode(object) : null;
			}
	
		};
	}

}
