package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von Hyperkanten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QESet extends QXSet<QE, QESet> {

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

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#context() Kontextknoten} in der gegebenen Menge enthalten sind.
	 * 
	 * @param contexts Kontextknotenfilter.
	 * @return Hyperkanten mit den gegebenen Kontextknoten. */
	public QESet withContexts(final QNSet contexts) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#predicate() Prädikatknoten} in der gegebenen Menge enthalten sind.
	 * 
	 * @param predicates Prädikatknotenfilter.
	 * @return Hyperkanten mit den gegebenen Prädikatknoten. */
	public QESet withPredicates(final QNSet predicates) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#subject() Subjektknoten} in der gegebenen Menge enthalten sind.
	 * 
	 * @param subjects Subjektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Subjektknoten. */
	public QESet withSubjects(final QNSet subjects) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperkanten zurück, deren {@link QE#object() Objektknoten} in der gegebenen Menge enthalten sind.
	 * 
	 * @param objects Objektknotenfilter.
	 * @return Hyperkanten mit den gegebenen Objektknoten. */
	public QESet withObjects(final QNSet objects) throws NullPointerException, IllegalArgumentException;

}
