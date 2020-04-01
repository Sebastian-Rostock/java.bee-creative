package bee.creative.qs;

/** Diese Klasse implementiert einen Quad-Edge - eine Kante eines Hypergraphen vierter Ordnung. Er selbst und seine Knoten werden vom selben {@link #store()
 * Graphspeicher} verwaltet.
 * <p>
 * {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf den vier Knoten und dem {@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QE extends QX {

	/** Diese Methode gibt den Kontextknoten zurück, welcher das Wissen kennzeichnet, in welchem die {@link #predicate() Prädikat}-{@link #subject()
	 * Subjekt}-{@link #object() Objekt}-Kante bekannt ist.
	 *
	 * @return Kontextknoten. */
	QN context();

	/** Diese Methode gibt den Prädikatknoten zurück, welcher die Bedeutung der {@link #subject() Subjekt}-{@link #object() Objekt}-Kante nennt.
	 *
	 * @return Prädikatknoten. */
	QN predicate();

	/** Diese Methode gibt den Subjektknoten zurück, welcher den Beginn der Kante zum {@link #object() Objekt} angibt.
	 *
	 * @return Subjektknoten. */
	QN subject();

	/** Diese Methode gibt den Objektknoten zurück, welcher das Ende der Kante vom {@link #subject() Subjekt} angibt.
	 *
	 * @return Objektknoten. */
	QN object();

	/** Diese Methode ist eine Abkürzung für {@code this.store().edges().put(this)}. */
	boolean put();

	/** Diese Methode ist eine Abkürzung für {@code this.store().edges().pop(this)}. */
	boolean pop();

	/** Diese Methode ist eine Abkürzung für {@code this.store().edges().has(this)}. */
	boolean exists();

	/** Diese Methode gibt eine Kopie dieser Kante mit dem gegebenen {@link #context() Kontextknoten} zurück.
	 *
	 * @param node Kontextknoten.
	 * @return Kopie mit Kontextknoten.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code node} in einem anderen {@link #store() Graphspeicher} verwaltet wird. */
	QE withContext(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Kopie dieser Kante mit dem gegebenen {@link #predicate() Prädikatknoten} zurück.
	 *
	 * @param node Prädikatknoten.
	 * @return Kopie mit Prädikatknoten.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code node} in einem anderen {@link #store() Graphspeicher} verwaltet wird. */
	QE withPredicate(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Kopie dieser Kante mit dem gegebenen {@link #context() Subjektknoten} zurück.
	 *
	 * @param node Subjektknoten.
	 * @return Kopie mit Subjektknoten.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code node} in einem anderen {@link #store() Graphspeicher} verwaltet wird. */
	QE withSubject(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Kopie dieser Kante mit dem gegebenen {@link #context() Objektknoten} zurück.
	 *
	 * @param node Objektknoten.
	 * @return Kopie mit Objektknoten.
	 * @throws NullPointerException Wenn {@code node} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code node} in einem anderen {@link #store() Graphspeicher} verwaltet wird. */
	QE withObject(final QN node) throws NullPointerException, IllegalArgumentException;

}
