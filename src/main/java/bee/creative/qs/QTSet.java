package bee.creative.qs;

import java.util.Arrays;
import java.util.List;

public interface QTSet extends QOSet<QT, QTSet> {

	/** Diese Methode liefert die Position, die der gegebene Rollennamen in der {@link #names() Liste der Rollennamen} hat. Wenn er dort nicht enthalten ist, wird
	 * {@code -1} geliefert. */
	public int role(String name) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link #roles(List) this.roles(Arrays.asList(names))}.
	 *
	 * @see Arrays#asList(Object...) */
	public int[] roles(String... names) throws NullPointerException;

	public int[] roles(List<String> names) throws NullPointerException;

	/** Diese Methode liefert die Namen der {@link QT#get(int) Rollen}, über welche die Hypertupel ihre Hyperknoten referenzieren.
	 *
	 * @return Rollennamen. */
	public List<String> names();

	/** Diese Methode gibt eine Mengensicht auf alle Hyperknoten zurück, die in den Hypertupel dieser Menge {@link QT#get(int) aufgeführten} sind.
	 *
	 * @return Hyperknoten der Hypertupel dieser Menge. */
	public QNSet nodes();

	/** Diese Methode gibt eine Mengensicht auf alle Hyperknoten zurück, die in den Hypertupel dieser Menge über die gegebene {@link QT#get(int) Rolle}
	 * referenziert werden.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @return Hyperknoten der gegebenen Rolle. */
	public QNSet nodes(int role) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #nodes(int) this.nodes(this.role(name))}.
	 *
	 * @see #role(String) */
	public QNSet nodes(String name) throws NullPointerException, IllegalArgumentException;

	public QTSet join(QTSet that) throws NullPointerException, IllegalArgumentException; // join auf gleich benannten spalten, sonst cross join

	public QTSet join(QTSet that, int... roles) throws NullPointerException, IllegalArgumentException; // join auf gleich benannten spalten, sonst cross join

	/** Diese Methode ist eine Abkürzung für {@link #join(QTSet, int...) this.join(that, this.roles(names))}.
	 *
	 * @see #roles(String...) */
	public QTSet join(QTSet that, String... names) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #join(QTSet, int...) this.join(that, this.roles(names))}.
	 *
	 * @see #roles(List) */
	public QTSet join(QTSet that, List<String> names) throws NullPointerException, IllegalArgumentException;

	public QTSet select(int... roles) throws NullPointerException, IllegalArgumentException; // reduzieren

	/** Diese Methode ist eine Abkürzung für {@link #select(int...) this.select(this.roles(names))}.
	 *
	 * @see #roles(String...) */
	public QTSet select(String... names) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #select(int...) this.select(this.roles(names))}.
	 *
	 * @see #roles(List) */
	public QTSet select(List<String> names) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die sich aus denen dieser Menge durch Ersetzung der über die gegebene {@link QT#get(int)
	 * Rolle} referenzierten Hyperknoten mit dem gegebenen ergeben.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @param node Hyperknoten.
	 * @return Hypertupel mit dem gegebenen Hyperknoten. */
	public QTSet withNode(int role, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #withNode(int, QN) this.withNode(this.role(name), node)}.
	 *
	 * @see #role(String) */
	public QTSet withNode(String name, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die sich aus denen dieser Menge durch Ersetzung der über die gegebene {@link QT#get(int)
	 * Rolle} referenzierten Hyperknoten mit den gegebenen ergeben.
	 *
	 * @param role Rolle der Hyperknoten.
	 * @param nodes Hyperknoten.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	public QTSet withNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #withNodes(int, QNSet) this.withNodes(this.role(name), nodes)}.
	 *
	 * @see #role(String) */
	public QTSet withNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #withNames(List) this.withNames(Arrays.asList(names))}.
	 *
	 * @see Arrays#asList(Object...) */
	public QTSet withNames(String... names) throws NullPointerException, IllegalArgumentException; // umbenennen

	public QTSet withNames(List<String> names) throws NullPointerException, IllegalArgumentException; // umbenennen

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die auf den gegebenen Hyperknoten verweisen.
	 *
	 * @param node Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	public QTSet havingNode(QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die über die gegebene {@link QT#get(int) Rolle} auf den gegebenen Hyperknoten verweisen.
	 *
	 * @param role Rolle des Hyperknoten.
	 * @param node Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	public QTSet havingNode(int role, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #havingNode(int, QN) this.havingNode(this.role(name), node)}.
	 *
	 * @see #role(String) */
	public QTSet havingNode(String name, QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die auf Hyperknoten der gegebenen Menge verweisen.
	 *
	 * @param nodes Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	public QTSet havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hypertupel zurück, die über die gegebene {@link QT#get(int) Rolle} auf einen Hyperknoten der gegebenen Menge
	 * verweisen.
	 *
	 * @param role Rolle des Hyperknoten.
	 * @param nodes Hyperknotenfilter.
	 * @return Hypertupel mit den gegebenen Hyperknoten. */
	public QTSet havingNodes(int role, QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link #havingNodes(int, QNSet) this.havingNodes(this.role(name), nodes)}.
	 *
	 * @see #role(String) */
	public QTSet havingNodes(String name, QNSet nodes) throws NullPointerException, IllegalArgumentException;

}
