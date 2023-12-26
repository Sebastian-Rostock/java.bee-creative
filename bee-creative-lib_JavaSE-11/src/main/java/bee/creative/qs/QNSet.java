package bee.creative.qs;

import bee.creative.util.Filter;
import bee.creative.util.Setter;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von {@link QN Hyperknoten}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QNSet extends QXSet<QN, QNSet> {

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Hyperknoten aus dem {@link #owner() Graphspeicher} und liefert nur dann {@code true}, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde. Damit werden auch die {@link QN#value() Textwerte} dieser Hyperknoten sowie alle diese Hyperknoten
	 * verwendenden {@link QE Hyperkanten} entfernt.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	@Override
	boolean popAll();

	/** Diese Methode liefert eine Mengensicht auf alle Textwerte, die in den Hyperknoten dieser Menge {@link QN#value() aufgeführt} sind.
	 *
	 * @return Textwerte der Hyperknoten dieser Menge. */
	QVSet values();

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}.
	 *
	 * @param values Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	void values(Setter<? super QN, ? super String> values) throws NullPointerException;

	/** Diese Methode liefert eine Mengensicht auf die Hyperknoten dieser Menge als Hypertupel der Länge {@code 1}.
	 *
	 * @param name Name der {@link QTSet#names() Rolle} {@code 0}, über welche die Hypertupel ihren Hyperknoten referenzieren.
	 * @return Hyperknoten dieser Menge als Hypertupel. */
	QTSet tuples(String name) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert eine Mengensicht auf die Hyperknoten, die einen {@link QN#value() Textwert} besitzen. Die Mengensicht entspricht
	 * {@code this.intersect(this.owner().nodes())}.
	 *
	 * @see #havingValues(QVSet)
	 * @return Hyperknoten mit Textwerten. */
	QNSet havingValue();

	/** Diese Methode liefert eine Mengensicht auf die Hyperknoten, deren {@link QN#value() Textwerte} in der gegebenen Menge enthalten sind. Die Mengensicht
	 * entspricht {@code this.intersect(values.nodes())}.
	 *
	 * @param values Textwertfilter.
	 * @return Hyperknoten mit den gegebenen Textwerten. */
	QNSet havingValues(QVSet values) throws NullPointerException, IllegalArgumentException;

	/** {@inheritDoc} Sie liefert damit {@link QS#newNodes(Iterable) this.owner().newNodes(this)}. */
	@Override
	QNSet2 copy();

	/** {@inheritDoc} Sie liefert damit {@link QS#newNodes(Iterable) this.owner().newNodes(Iterables.filter(this, filter))}. */
	@Override
	QNSet2 copy(Filter<? super QN> filter) throws NullPointerException;

}