package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von {@link QN Hyperknoten}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QNSet extends QXSet<QN, QNSet> {

	/** Diese Methode gibt eine Mengensicht auf alle Textwerte zurück, die in den Hyperknoten dieser Menge {@link QN#value() aufgeführten} sind.
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
	public QNSet toCopy();

}