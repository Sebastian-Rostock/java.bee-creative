package bee.creative.qs;

/** Diese Klasse implementiert einen Quad-Node - einen Knoten in einem Hypergraphen vierter Ordnung. Er kann als {@link QE#context() Kontext},
 * {@link QE#predicate Prädikat}, {@link QE#subject() Subjekt} oder {@link QE#object() Objekt} einer {@link QE Kante} eingesetzt werden und über einen
 * {@link #value() Textwert} verfügen.
 * <p>
 * {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf {@link #ident() Knotenkennung} und {@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QN extends QX {

	/** Diese Methode gibt die Kennugn des Knoten zurück.
	 *
	 * @return Knotenkennung. */
	long ident();

	/** Diese Methode gibt den Textwert des Knoten zurück. Dieser ist niemals {@code null}.
	 *
	 * @return Textwert oder {@code ""}. */
	String value();

	/** Diese Methode setzt den Textwert des Knoten. Dabei wird {@code null} wird wie {@code ""} behandelt.
	 *
	 * @param value Textwert oder {@code null}. */
	void value(final String value);

}
