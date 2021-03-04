package bee.creative.qs;

/** Diese Schnittstelle definiert eine Hyperkante vierter Ordnung (Quad-Edge), welche vier {@link QN Hyperknoten} in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} verbindet. {@link #hashCode() Streuwert} und
 * {@link #equals(Object) Äquivalenz} basieren auf den der vier Hyperknoten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QE extends QX {

	/** Diese Methode gibt den Kontextknoten zurück, welcher das Wissen kennzeichnet, in welchem die {@link #predicate() Prädikat}-{@link #subject()
	 * Subjekt}-{@link #object() Objekt}-Kante bekannt ist.
	 *
	 * @return Kontextknoten. */
	public QN context();

	/** Diese Methode gibt den Prädikatknoten zurück, welcher die Bedeutung der {@link #subject() Subjekt}-{@link #object() Objekt}-Kante nennt.
	 *
	 * @return Prädikatknoten. */
	public QN predicate();

	/** Diese Methode gibt den Subjektknoten zurück, welcher den Beginn der Kante zum {@link #object() Objekt} angibt.
	 *
	 * @return Subjektknoten. */
	public QN subject();

	/** Diese Methode gibt den Objektknoten zurück, welcher das Ende der Kante vom {@link #subject() Subjekt} angibt.
	 *
	 * @return Objektknoten. */
	public QN object();

	/** Diese Methode speichert diese Hyperkante im {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch der Inhalt des
	 * Graphspeichers verändert wurde.
	 *
	 * @see QESet#putAll()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	public boolean put();

	/** {@inheritDoc} Von den daraufhin nicht mehr in Hyperkanten verwendeten {QN Hyperknoten} bleiben nur die mit {@link QN#value() Textwert} gespeichert. */
	@Override
	public boolean pop();

	/** Diese Methode gibt diese Hyperkante mit dem gegebenen {@link QE#context() Kontextknoten} zurück.
	 *
	 * @param context Kontextknoten.
	 * @return Hyperkante mit dem gegebenen Kontextknoten. */
	public QE withContext(final QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt diese Hyperkante mit dem gegebenen {@link QE#predicate() Prädikatknoten} zurück.
	 *
	 * @param predicate Prädikatknoten.
	 * @return Hyperkante mit dem gegebenen Prädikatknoten. */
	public QE withPredicate(final QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt diese Hyperkante mit dem gegebenen {@link QE#subject() Subjektknoten} zurück.
	 *
	 * @param subject Subjektknoten.
	 * @return Hyperkante mit dem gegebenen Subjektknoten. */
	public QE withSubject(final QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt diese Hyperkante mit dem gegebenen {@link QE#object() Objektknoten} zurück.
	 *
	 * @param object Objektknoten.
	 * @return Hyperkante mit dem gegebenen Objektknoten. */
	public QE withObject(final QN object) throws NullPointerException, IllegalArgumentException;

}
