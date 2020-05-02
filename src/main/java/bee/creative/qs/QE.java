package bee.creative.qs;

/** Diese Schnittstelle definiert eine Hyperkante vierter Ordnung (Quad-Edge), welcje vier {@link QN Hyperknoten} in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} verbindet. {@link #compareTo(QE) Ordnung}, {@link #hashCode()
 * Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf den en der vier Hyperknoten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QE extends QX, Comparable<QE> {

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

}
