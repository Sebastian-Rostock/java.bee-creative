package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von Hyperkanten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QESet extends QXSet<QE, QESet> {

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Hyperknoten} zurück, die in den Hyperkanten dieser Menge aufgeführten sind. Sie ist damit eine
	 * Abkürzung für {@code this.contexts().union(this.predicates()).union(this.subjects()).union(this.objects())}.
	 *
	 * @see #contexts()
	 * @see #predicates()
	 * @see #subjects()
	 * @see #objects()
	 * @return Hyperknoten der Hyperkanten dieser Menge. */
	public QNSet nodes();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Kontextknoten} zurück, die in den Hyperkanten dieser Menge {@link QE#context() aufgeführten} sind.
	 *
	 * @return Kontextknoten der Hyperkanten dieser Menge. */
	public QNSet contexts();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Prädikatknoten} zurück, die in den Hyperkanten dieser Menge {@link QE#predicate() aufgeführten}
	 * sind.
	 *
	 * @return Prädikatknoten der Hyperkanten dieser Menge. */
	public QNSet predicates();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Subjektknoten} zurück, die in den Hyperkanten dieser Menge {@link QE#subject() aufgeführten} sind.
	 *
	 * @return Subjektknoten der Hyperkanten dieser Menge. */
	public QNSet subjects();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Objektknoten} zurück, die in den Hyperkanten dieser Menge {@link QE#object() aufgeführten} sind.
	 *
	 * @return Objektknoten der Hyperkanten dieser Menge. */
	public QNSet objects();

	/** Diese Methode speichert alle in dieser Menge enthaltenen Hyperkanten im {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde.
	 *
	 * @see QE#put()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	public boolean putAll();

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Hyperkanten aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde. Von den daraufhin nicht mehr in Hyperkanten verwendeten {QN Hyperknoten} bleiben nur die mit
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
	public QESet withContext(final QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#context() Kontextknoten}
	 * mit den gegebenen ergeben.
	 *
	 * @param contexts Kontextknoten.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#predicate()
	 * Prädikatknoten} mit dem gegebenen ergeben.
	 *
	 * @param predicate Prädikatknoten.
	 * @return Hyperkanten mit dem gegebenen Prädikatknoten. */
	public QESet withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#predicate()
	 * Prädikatknoten} mit den gegebenen ergeben.
	 *
	 * @param predicates Prädikatknoten.
	 * @return Hyperkanten mit den gegebenen Prädikatknoten. */
	public QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#subject() Subjektknoten}
	 * mit dem gegebenen ergeben.
	 *
	 * @param subject Subjektknoten.
	 * @return Hyperkanten mit dem gegebenen Subjektknoten. */
	public QESet withSubject(final QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#subject() Subjektknoten}
	 * mit den gegebenen ergeben.
	 *
	 * @param subjects Subjektknoten.
	 * @return Hyperkanten mit den gegebenen Subjektknoten. */
	public QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#object() Objektknoten} mit
	 * dem gegebenen ergeben.
	 *
	 * @param object Objektknoten.
	 * @return Hyperkanten mit dem gegebenen Objektknoten. */
	public QESet withObject(final QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, die sich aus denen dieser Menge durch Ersetzung ihres {@link QE#object() Objektknoten} mit
	 * den gegebenen ergeben.
	 *
	 * @param objects Objektknoten.
	 * @return Hyperkanten mit den gegebenen Objektknoten. */
	public QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen Hyperknoten.
	 *
	 * @see #havingNodes(QNSet)
	 * @param node Hyperknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Kontextknoten. */
	public QESet havingNode(final QN node) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#context() Kontextknoten}, {@link QE#predicate() Prädikatknoten},
	 * {@link QE#subject() Subjektknoten} oder {@link QE#object() Objektknoten} in der gegebenen Menge enthalten ist. Sie ist damit eine Abkürzung für
	 * {@code this.havingContexts(nodes).union(this.havingPredicates(nodes)).union(this.havingSubjects(nodes)).union(this.havingObjects(nodes))}.
	 *
	 * @see #havingContexts(QNSet)
	 * @see #havingPredicates(QNSet)
	 * @see #havingSubjects(QNSet)
	 * @see #havingObjects(QNSet)
	 * @param nodes Hyperknotenfilter.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet havingNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#context() Kontextknoten}.
	 *
	 * @see #havingContexts(QNSet)
	 * @param context Kontextknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Kontextknoten. */
	public QESet havingContext(final QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#context() Kontextknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param contexts Kontextknotenfilter.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet havingContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @see #havingPredicates(QNSet)
	 * @param predicate Prädikatknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Prädikatknoten. */
	public QESet havingPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#predicate() Prädikatknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param predicates Prädikatknotenfilter.
	 * @return Hyperkanten mit den gegebenen Prädikatknoten. */
	public QESet havingPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#subject() Subjektknoten}.
	 *
	 * @see #havingSubjects(QNSet)
	 * @param subject Subjektknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Subjektknoten. */
	public QESet havingSubject(final QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#subject() Subjektknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param subjects Subjektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Subjektknoten. */
	public QESet havingSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten mit dem gegebenen {@link QE#object() Objektknoten}.
	 *
	 * @see #havingObjects(QNSet)
	 * @param object Objektknotenfilter.
	 * @return Hyperkanten mit dem gegebenen Objektknoten. */
	public QESet havingObject(final QN object) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#object() Objektknoten} in der gegebenen Menge enthalten ist.
	 *
	 * @param objects Objektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Objektknoten. */
	public QESet havingObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException;

	/** {@inheritDoc} Sie liefert damit {@link QS#newEdges(Iterable) this.owner().newEdges(this)}. */
	@Override
	public QESet copy();

}
