package bee.creative.qs;

import bee.creative.util.Setter;

public interface QIBag<ITEM, THIS> extends QISet<ITEM> {

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}, der die
	 * {@link Object#toString() Textdarstellung} eines Elements dieser Datensammlung darstellt.
	 *
	 * @param items Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	void items(Setter<? super QN, ? super ITEM> items);

	QNSet nodes();

	QVSet values();

	THIS havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException;

}
