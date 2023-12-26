package bee.creative.qs;

/** Diese Schnittstelle definiert einen Hyperknoten vierter Ordnung (Quad-Node). Er kann als {@link QE#context() Kontext}, {@link QE#predicate Prädikat},
 * {@link QE#subject() Subjekt} oder {@link QE#object() Objekt} einer {@link QE Hyperkante} eingesetzt werden und über einen {@link #value() Textwert} als
 * extere Kennung verfügen. {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf dem {@link #owner() Graphspeicher} und einer
 * internen Knotenkennung.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QN extends QX {

	/** Diese Methode liefert den Textwert des Hyperknoten. Wenn der Hyperknoten keinen Textwert besitzt, wird {@code null} geliefert. Andernfalls kann dieser zur
	 * {@link QS#newNode(Object) Identifikation} des Hyperknoten im {@link #owner() Graphspeicher} eingesetzt werden.
	 *
	 * @return Textwert oder {@code null}. */
	String value();

	/** {@inheritDoc} Damit werden auch der {@link QN#value() Textwert} dieses Hyperknoten sowie alle diese Hyperknoten verwendenden {@link QE Hyperkanten}
	 * entfernt. */
	@Override
	boolean pop();

	/** {@inheritDoc} Der Hyperknoten ist nur dann im Graphspeicher enthalten, wenn er einen Textwert besitzt oder in einer gespeicherten {@link QE Hyperkante}
	 * verwendet wird. */
	@Override
	boolean state();

}
