package bee.creative.qs;

import java.util.Arrays;
import java.util.List;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;

/** Diese Schnittstelle definiert eine {@link QOSet Menge} von Hypertupeln.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QTSet extends QOSet<QT, QTSet> {

	/** {@inheritDoc} Sie liefert damit {@link QS#newTuples(List, Iterable) this.owner().newTuples(this.names(), this)}. */
	@Override
	default QTSet2 copy() {
		return this.owner().newTuples(this.names(), this);
	}

	/** {@inheritDoc} Sie liefert damit {@link QS#newTuples(List, Iterable) this.owner().newTuples(this.names(), Iterables.filter(this, filter))}. */
	@Override
	default QTSet2 copy(Filter<? super QT> filter) throws NullPointerException {
		return this.owner().newTuples(this.names(), Iterables.filter(this, filter));
	}

	/** Diese Methode liefert die Position, die der gegebene Rollennamen in der {@link #names() Liste der Rollennamen} hat. Wenn er dort nicht enthalten ist, wird
	 * {@code -1} geliefert.
	 *
	 * @param name Rollenname.
	 * @return Rolle oder {@code -1}. */
	int role(String name) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link #roles(List) this.roles(Arrays.asList(names))}. */
	default int[] roles(String... names) throws NullPointerException {
		return this.roles(Arrays.asList(names));
	}

	/** Diese Methode liefert die {@link #role(String) Positionen}, die die gegebenen Rollennamen in der {@link #names() Liste der Rollennamen} haben.
	 *
	 * @param names Rollennamen.
	 * @return Rollen. */
	int[] roles(List<String> names) throws NullPointerException;

	/** Diese Methode liefert den Namen der gegebenen Rolle und ist effektiv eine Abkürzung für {@link #names() this.names().get(role)}.
	 *
	 * @param role Rolle.
	 * @return Rollenname. */
	String name(int role) throws IllegalArgumentException;

	/** Diese Methode liefert die {@link #name(int) Namen der gegebenen Rollen}.
	 *
	 * @param roles Rollen.
	 * @return Rollennamen. */
	String[] names(int... roles) throws IllegalArgumentException;

	/** Diese Methode liefert die Namen der {@link QT#get(int) Rollen}, über welche die Hypertupel ihre Hyperknoten referenzieren.
	 *
	 * @return Rollennamen. */
	List<String> names();

	/** Diese Methode liefert eine Mengensicht auf die Hypertupel dieser Menge als Hyperkanten zurück.
	 *
	 * @param context {@link QT#get(int) Rolle} der {@link QE#context() Kontextknoten}.
	 * @param predicate {@link QT#get(int) Rolle} der {@link QE#predicate() Prädikatknoten}.
	 * @param subject {@link QT#get(int) Rolle} der {@link QE#subject() Subjektknoten}.
	 * @param object {@link QT#get(int) Rolle} der {@link QE#object() Objektknoten}.
	 * @return Hypertupel dieser Menge als Hyperkanten. */
	QESet edges(int context, int predicate, int subject, int object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #edges(int, int, int, int) this.edges(this.role(context), this.role(predicate), this.role(subject),
	 * this.role(object))}.
	 *
	 * @see #role(String)
	 * @param context Rollenname der {@link QE#context() Kontextknoten}.
	 * @param predicate Rollenname der {@link QE#predicate() Prädikatknoten}.
	 * @param subject Rollenname der {@link QE#subject() Subjektknoten}.
	 * @param object Rollenname der {@link QE#object() Objektknoten}.
	 * @return Hypertupel dieser Menge als Hyperkanten. */
	default QESet edges(String context, String predicate, String subject, String object) throws NullPointerException, IllegalArgumentException {
		return this.edges(this.role(context), this.role(predicate), this.role(subject), this.role(object));
	}

	/** Diese Methode liefert eine Mengensicht auf alle Hyperknoten, die in den Hypertupel dieser Menge über die gegebene {@link QT#get(int) Rolle} referenziert
	 * werden.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @return Hyperknoten der gegebenen Rolle. */
	QNSet nodes(int role) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #nodes(int) this.nodes(this.role(name))}.
	 *
	 * @see #role(String) */
	default QNSet nodes(String name) throws NullPointerException, IllegalArgumentException {
		return this.nodes(this.role(name));
	}

	/** Diese Methode liefert eine Mengensicht auf die Verbindung der Hypertupel dieser und der gegebenen Menge. Wenn die gegebene Menge keine {@link #names()
	 * Rollennamen} mit dieser gemein hat, enthält die gelieferte Menge jedes Hypertupel dieser Menge verkettet mit jedem Hypertupel der gegebenen Menge.
	 * Andernfalls weden die Hypertupel über die Hyperknoten ihrer gemeinsamen Rollen miteinander verbunden. Die Rollennamen der gelieferten Mengensicht bestehen
	 * aus den Rollennamen dieser Menge gefolgt von den nicht darin enthaltenen Rollennamen der gegebenen Menge.
	 *
	 * @param that gegebene Menge.
	 * @return {@code CROSS JOIN} bzw. {@code NATURAL JOIN} von {@code this} und {@code that}. */
	QTSet join(QTSet that) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Mengensicht auf einen Auszug der Hypertupel dieser Menge. Die Hypertupel in der gelieferten Menge enthalten dabei nur noch die
	 * Hyperknoten der gegebenen Rollen. Die {@link #names() Rollennamen} der gegebenen Rollen bleiben erhalten.
	 *
	 * @param roles Rollen der Hyperknoten.
	 * @return Hypertupel auf die gegebenen Rollen projiziert (reduziert und/oder umgeordnet). */
	QTSet select(int... roles) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #select(int...) this.select(this.roles(names))}.
	 *
	 * @see #roles(String...) */
	default QTSet select(String... names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	/** Diese Methode ist eine Abkürzung für {@link #select(int...) this.select(this.roles(names))}.
	 *
	 * @see #roles(List) */
	default QTSet select(List<String> names) throws NullPointerException, IllegalArgumentException {
		return this.select(this.roles(names));
	}

	/** Diese Methode liefert eine Mengensicht auf die Hypertupel, die sich aus denen dieser Menge durch Ersetzung der über die gegebene {@link QT#get(int) Rolle}
	 * referenzierten Hyperknoten mit dem gegebenen ergeben.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @param node Hyperknoten.
	 * @return Hypertupel mit dem gegebenen Hyperknoten. */
	QTSet withNode(int role, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #withNode(int, QN) this.withNode(this.role(name), node)}.
	 *
	 * @see #role(String) */
	default QTSet withNode(String name, QN node) throws NullPointerException, IllegalArgumentException {
		return this.withNode(this.role(name), node);
	}

	/** Diese Methode liefert eine Mengensicht auf die Hypertupel, die sich aus denen dieser Menge durch Ersetzung der über die gegebene {@link QT#get(int) Rolle}
	 * referenzierten Hyperknoten mit den gegebenen ergeben.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @param nodes Hyperknoten.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	QTSet withNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #withNodes(int, QNSet) this.withNodes(this.role(name), nodes)}.
	 *
	 * @see #role(String) */
	default QTSet withNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.withNodes(this.role(name), nodes);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withNames(List) this.withNames(Arrays.asList(names))}. */
	default QTSet withNames(String... names) throws NullPointerException, IllegalArgumentException {
		return this.withNames(Arrays.asList(names));
	}

	/** Diese Methode liefert eine Mengensicht auf diese Menge, bei welcher die {@link #names() Rollennamen} durch die gegeben ersetzt wurden. Die Anzahl der
	 * Rollen muss dabei erhalten bleiben.
	 *
	 * @param names neue Rollennamen.
	 * @return Mengensicht mit neuen Rollennamen. */
	QTSet withNames(List<String> names) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Mengensicht auf die Hypertupel, die über die gegebene {@link QT#get(int) Rolle} auf den gegebenen Hyperknoten verweisen.
	 *
	 * @param role Rolle des Hyperknoten.
	 * @param node Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	QTSet havingNode(int role, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #havingNode(int, QN) this.havingNode(this.role(name), node)}.
	 *
	 * @see #role(String) */
	default QTSet havingNode(String name, QN node) throws NullPointerException, IllegalArgumentException {
		return this.havingNode(this.role(name), node);
	}

	/** Diese Methode liefert eine Mengensicht auf die Hypertupel, die über die gegebene {@link QT#get(int) Rolle} auf einen Hyperknoten der gegebenen Menge
	 * verweisen.
	 *
	 * @param role Rolle des Hyperknoten.
	 * @param nodes Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	QTSet havingNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #havingNodes(int, QNSet) this.havingNodes(this.role(name), nodes)}.
	 *
	 * @see #role(String) */
	default QTSet havingNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException {
		return this.havingNodes(this.role(name), nodes);
	}

}
