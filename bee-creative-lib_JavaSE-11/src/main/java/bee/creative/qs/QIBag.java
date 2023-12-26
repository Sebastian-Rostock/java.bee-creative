package bee.creative.qs;

import bee.creative.util.Setter;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Sammlung von Elementen mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Elemente.
 * @param <THIS> Typ dieser Sammlung. */
public interface QIBag<ITEM, THIS> extends QISet<ITEM> {

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Sammlung, die einen Textwert {@link QN#value() besitzen}, der die
	 * {@link Object#toString() Textdarstellung} eines Elements dieser Sammlung darstellt.
	 *
	 * @param items Abbildung von {@link QN Hyperknoten} auf Elemente. */
	void items(Setter<? super QN, ? super ITEM> items);

	/** Diese Methode liefert eine Mengensicht auf alle Hyperknoten, die einen Textwert {@link QN#value() besitzen}, der die {@link Object#toString()
	 * Textdarstellung} eines Elements dieser Sammlung darstellt.
	 * 
	 * @return Hyperknoten dieser Sammlung. */
	QNSet nodes();

	/** Diese Methode liefert eine Mengensicht auf alle Textwerte, die die {@link Object#toString() Textdarstellung} eines Elements dieser Sammlung darstellen.
	 *
	 * @return Textwerte der Hyperknoten dieser Sammlung. */
	QVSet values();

	/** Diese Methode liefert eine Sammlungssicht auf die Elemente, die aus den {@link QN#value() Textwerten} der gegebenen {@link QN Hyperknoten} abgeleitet
	 * wurden.
	 *
	 * @param nodes Hyperknoten.
	 * @return Elemente der gegebenen Hyperknoten. */
	THIS havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException;

}
