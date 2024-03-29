package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von Hyperkanten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QESet extends QXSet<QE, QESet> {

	/** Diese Methode gibt eine Mengensicht auf alle {@link QE#context() Kontextknoten} zurück, die in den Hyperkanten dieser Menge aufgeführt sind.
	 *
	 * @return Kontextknoten der Hyperkanten dieser Menge. */
	public QNSet contexts();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QE#predicate() Prädikatknoten} zurück, die in den Hyperkanten dieser Menge aufgeführt sind.
	 *
	 * @return Prädikatknoten der Hyperkanten dieser Menge. */
	public QNSet predicates();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QE#subject() Subjektknoten} zurück, die in den Hyperkanten dieser Menge aufgeführt sind.
	 *
	 * @return Subjektknoten der Hyperkanten dieser Menge. */
	public QNSet subjects();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QE#object() Objektknoten} zurück, die in den Hyperkanten dieser Menge aufgeführte sind.
	 *
	 * @return Objektknoten der Hyperkanten dieser Menge. */
	public QNSet objects();

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten dieser Menge als Hypertupel der Länge {@code 4} zurück.
	 *
	 * @param context Name der {@link QTSet#names() Rolle} {@code 0}, über welche die Hypertupel den {@link QE#context() Kontextknoten} referenzieren.
	 * @param predicate Name der {@link QTSet#names() Rolle} {@code 1}, über welche die Hypertupel den {@link QE#predicate() Prädikatknoten} referenzieren.
	 * @param subject Name der {@link QTSet#names() Rolle} {@code 2}, über welche die Hypertupel den {@link QE#subject() Subjektknoten} referenzieren.
	 * @param object Name der {@link QTSet#names() Rolle} {@code 3}, über welche die Hypertupel den {@link QE#object() Objektknoten} referenzieren.
	 * @return Hyperkanten dieser Menge als Hypertupel. */
	public QTSet tuples(String context, String predicate, String subject, String object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode speichert alle in dieser Menge enthaltenen Hyperkanten im {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde.
	 *
	 * @see QE#put()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	public boolean putAll();

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Hyperkanten aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde. Von den daraufhin nicht mehr in Hyperkanten verwendeten Hyperknoten bleiben nur die mit
	 * {@link QN#value() Textwert} erhalten.
	 *
	 * @see QE#pop()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	@Override
	public boolean popAll();

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#context() Kontextknoten}
	 * mit dem gegebenen ergeben.
	 *
	 * @param context Kontextknoten.
	 * @return Hyperkanten mit dem gegebenen Kontextknoten. */
	public QESet withContext(QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#context() Kontextknoten}
	 * mit den gegebenen ergeben.
	 *
	 * @param contexts Kontextknoten.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet withContexts(QNSet contexts) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#predicate()
	 * Prädikatknoten} mit dem gegebenen ergeben.
	 *
	 * @param predicate Prädikatknoten.
	 * @return Hyperkanten mit dem gegebenen Prädikatknoten. */
	public QESet withPredicate(QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#predicate()
	 * Prädikatknoten} mit den gegebenen ergeben.
	 *
	 * @param predicates Prädikatknoten.
	 * @return Hyperkanten mit den gegebenen Prädikatknoten. */
	public QESet withPredicates(QNSet predicates) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#subject() Subjektknoten}
	 * mit dem gegebenen ergeben.
	 *
	 * @param subject Subjektknoten.
	 * @return Hyperkanten mit dem gegebenen Subjektknoten. */
	public QESet withSubject(QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#subject() Subjektknoten}
	 * mit den gegebenen ergeben.
	 *
	 * @param subjects Subjektknoten.
	 * @return Hyperkanten mit den gegebenen Subjektknoten. */
	public QESet withSubjects(QNSet subjects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#object() Objektknoten} mit
	 * dem gegebenen ergeben.
	 *
	 * @param object Objektknoten.
	 * @return Hyperkanten mit dem gegebenen Objektknoten. */
	public QESet withObject(QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#object() Objektknoten} mit
	 * den gegebenen ergeben.
	 *
	 * @param objects Objektknoten.
	 * @return Hyperkanten mit den gegebenen Objektknoten. */
	public QESet withObjects(QNSet objects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen Hyperknoten.
	 *
	 * @see #havingContext(QN)
	 * @see #havingPredicate(QN)
	 * @see #havingSubject(QN)
	 * @see #havingObject(QN)
	 * @param node Hyperknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Kontextknoten. */
	public QESet havingNode(QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#context() Kontextknoten}, {@link QE#predicate() Prädikatknoten},
	 * {@link QE#subject() Subjektknoten} oder {@link QE#object() Objektknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @see #havingContexts(QNSet)
	 * @see #havingPredicates(QNSet)
	 * @see #havingSubjects(QNSet)
	 * @see #havingObjects(QNSet)
	 * @param nodes Hyperknotenfilter.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#context() Kontextknoten}.
	 *
	 * @param context Kontextknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Kontextknoten. */
	public QESet havingContext(QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#context() Kontextknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param contexts Kontextknotenfilter.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet havingContexts(QNSet contexts) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @param predicate Prädikatknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Prädikatknoten. */
	public QESet havingPredicate(QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#predicate() Prädikatknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param predicates Prädikatknotenfilter.
	 * @return Hyperkanten mit den gegebenen Prädikatknoten. */
	public QESet havingPredicates(QNSet predicates) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#subject() Subjektknoten}.
	 *
	 * @param subject Subjektknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Subjektknoten. */
	public QESet havingSubject(QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#subject() Subjektknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param subjects Subjektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Subjektknoten. */
	public QESet havingSubjects(QNSet subjects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#object() Objektknoten}.
	 *
	 * @param object Objektknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Objektknoten. */
	public QESet havingObject(QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#object() Objektknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param objects Objektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Objektknoten. */
	public QESet havingObjects(QNSet objects) throws NullPointerException, IllegalArgumentException;

	/** {@inheritDoc} Sie liefert damit {@link QS#newEdges(Iterable) this.owner().newEdges(this)}. */
	@Override
	public QESet copy();

}
