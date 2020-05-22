package bee.creative.qs;

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

	/** Diese Methode gibt eine Mengensichtp auf alle Textwerte zurück, die in den Hyperknoten dieser Menge {@link QN#value() aufgeführt} sind.
	 *
	 * @return Textwerte der Hyperknoten dieser Menge. */
	public QVSet values();

	/** Diese Methode gibt eine Mengensicht auf die Hyperknoten zurück, die einen {@link QN#value() Textwert} besitzen. Die Mengensicht entspricht
	 * {@link #values() this.values().nodes()}.
	 *
	 * @see #havingValues(QVSet)
	 * @return Hyperknoten mit Textwerten. */
	public QNSet havingValue();

	/** Diese Methode gibt eine Mengensicht auf die Hyperknoten zurück, deren {@link QN#value() Textwerte} in der gegebenen Menge enthalten sind. Die Mengensicht
	 * entspricht {@code this.intersect(values.nodes())}.
	 *
	 * @param values Textwertfilter.
	 * @return Hyperknoten mit den gegebenen Textwerten. */
	public QNSet havingValues(final QVSet values) throws NullPointerException, IllegalArgumentException;

	/** {@inheritDoc} Sie liefert damit {@link QS#newNodes(Iterable) this.owner().newNodes(this)}. */
	@Override
	public QNSet copy();

}