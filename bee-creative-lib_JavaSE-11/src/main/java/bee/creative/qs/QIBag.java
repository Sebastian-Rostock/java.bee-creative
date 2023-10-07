package bee.creative.qs;

import bee.creative.util.Setter;

public interface QIBag<GI, GIBag> extends QISet<GI> {

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}, der die
	 * {@link Object#toString() Textdarstellung} eines Elements dieser Datensammlung darstellt.
	 *
	 * @param items Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	void items(Setter<? super QN, ? super GI> items);

	QNSet nodes();

	QVSet values();

	GIBag havingNodes(QNSet nodes) throws NullPointerException, IllegalArgumentException;

}
