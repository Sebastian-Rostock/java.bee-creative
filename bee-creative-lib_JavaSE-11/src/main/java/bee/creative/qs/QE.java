package bee.creative.qs;

/** Diese Schnittstelle definiert eine Hyperkante vierter Ordnung (Quad-Edge), welche vier {@link QN Hyperknoten} in den Rollen {@link QE#context() Kontext},
 * {@link QE#predicate() Prädikat}, {@link QE#subject() Subjekt} und {@link QE#object() Objekt} verbindet. {@link #hashCode() Streuwert} und
 * {@link #equals(Object) Äquivalenz} basieren auf den der vier Hyperknoten.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QE extends QX {

	/** Diese Methode liefert den Kontextknoten, der das Wissen kennzeichnet, in dem die {@link #predicate() Prädikat}-{@link #subject() Subjekt}-{@link #object()
	 * Objekt}-Kante bekannt ist.
	 *
	 * @return Kontextknoten. */
	QN context();

	/** Diese Methode liefert den Prädikatknoten, der die Bedeutung der {@link #subject() Subjekt}-{@link #object() Objekt}-Kante kennzeichnet.
	 *
	 * @return Prädikatknoten. */
	QN predicate();

	/** Diese Methode liefert den Subjektknoten, der den Beginn der Kante zum {@link #object() Objekt} angibt.
	 *
	 * @return Subjektknoten. */
	QN subject();

	/** Diese Methode liefert den Objektknoten, der das Ende der Kante vom {@link #subject() Subjekt} angibt.
	 *
	 * @return Objektknoten. */
	QN object();

	/** Diese Methode speichert diese Hyperkante im {@link #owner() Graphspeicher} und liefert nur dann {@code true}, wenn dadurch der Inhalt des Graphspeichers
	 * verändert wurde.
	 *
	 * @see QESet#putAll()
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	boolean put();

	/** {@inheritDoc} Von den daraufhin nicht mehr in Hyperkanten verwendeten {QN Hyperknoten} bleiben nur die mit {@link QN#value() Textwert} gespeichert. */
	@Override
	boolean pop();

	/** Diese Methode liefert eine Kopie dieser Hyperkante mit dem gegebenen {@link QE#context() Kontextknoten}.
	 *
	 * @param context Kontextknoten.
	 * @return Hyperkante mit dem gegebenen Kontextknoten. */
	QE withContext(QN context) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Kopie dieser Hyperkante mit dem gegebenen {@link QE#predicate() Prädikatknoten}.
	 *
	 * @param predicate Prädikatknoten.
	 * @return Hyperkante mit dem gegebenen Prädikatknoten. */
	QE withPredicate(QN predicate) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Kopie dieser Hyperkante mit dem gegebenen {@link QE#subject() Subjektknoten}.
	 *
	 * @param subject Subjektknoten.
	 * @return Hyperkante mit dem gegebenen Subjektknoten. */
	QE withSubject(QN subject) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Kopie dieser Hyperkante mit dem gegebenen {@link QE#object() Objektknoten}.
	 *
	 * @param object Objektknoten.
	 * @return Hyperkante mit dem gegebenen Objektknoten. */
	QE withObject(QN object) throws NullPointerException, IllegalArgumentException;

}
