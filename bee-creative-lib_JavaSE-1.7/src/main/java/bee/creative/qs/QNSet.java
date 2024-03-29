package bee.creative.qs;

import java.util.Map;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von {@link QN Hyperknoten}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QNSet extends QXSet<QN, QNSet> {

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Hyperknoten aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde. Damit werden auch die {@link QN#value() Textwerte} dieser Hyperknoten sowie alle diese Hyperknoten
	 * verwendenden {@link QE Hyperkanten} entfernt.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	@Override
	public boolean popAll();

	/** Diese Methode gibt eine Mengensicht auf alle Textwerte zurück, die in den Hyperknoten dieser Menge {@link QN#value() aufgeführt} sind.
	 *
	 * @return Textwerte der Hyperknoten dieser Menge. */
	public QVSet values();

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten} dieser Menge, die einen Textwert {@link QN#value() besitzen}.
	 *
	 * @param values Abbildung von {@link QN Hyperknoten} auf {@link QN#value() Textwerte}. */
	public void values(Map<QN, String> values);

	/** Diese Methode gibt eine Mengensicht auf die Hyperknoten dieser Menge als Hypertupel der Länge {@code 1} zurück.
	 *
	 * @param name Name der {@link QTSet#names() Rolle} {@code 0}, über welche die Hypertupel ihren Hyperknoten referenzieren.
	 * @return Hyperknoten dieser Menge als Hypertupel. */
	public QTSet tuples(String name) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Hyperknoten zurück, die einen {@link QN#value() Textwert} besitzen. Die Mengensicht entspricht
	 * {@code this.intersect(this.owner().nodes())}.
	 *
	 * @see #havingValues(QVSet)
	 * @return Hyperknoten mit Textwerten. */
	public QNSet havingValue();

	/** Diese Methode gibt eine Mengensicht auf die Hyperknoten zurück, deren {@link QN#value() Textwerte} in der gegebenen Menge enthalten sind. Die Mengensicht
	 * entspricht {@code this.intersect(values.nodes())}.
	 *
	 * @param values Textwertfilter.
	 * @return Hyperknoten mit den gegebenen Textwerten. */
	public QNSet havingValues(QVSet values) throws NullPointerException, IllegalArgumentException;

	/** {@inheritDoc} Sie liefert damit {@link QS#newNodes(Iterable) this.owner().newNodes(this)}. */
	@Override
	public QNSet copy();

}