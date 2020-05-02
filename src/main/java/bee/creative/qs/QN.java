package bee.creative.qs;

/** Diese Schnittstelle definiert einen Hyperknoten vierter Ordnung (Quad-Node). Er kann als {@link QE#context() Kontext}, {@link QE#predicate Prädikat},
 * {@link QE#subject() Subjekt} oder {@link QE#object() Objekt} einer {@link QE Hyperkante} eingesetzt werden und über einen {@link #value() Textwert} als
 * extere Kennung verfügen. {@link #compareTo(QN) Ordnung}, {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf {@link #ident()
 * Knotenkennung} und {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QN extends QX, Comparable<QN> {

	/** Diese Methode gibt die Kennugn des Hyperknoten zurück.
	 *
	 * @return Knotenkennung. */
	public long ident();

	/** Diese Methode gibt den Textwert des Hyperknoten zurück. Wenn der Hyperknoten keinen Textwert besitzt, wird {@code null} geliefert. Andernfalls kann dieser
	 * zur {@link QS#newNode(String) Identifikation} des Hyperknoten im {@link #owner() Graphspeicher} eingesetzt werden.
	 * 
	 * @return Textwert oder {@code null}. */
	public String value();

}
