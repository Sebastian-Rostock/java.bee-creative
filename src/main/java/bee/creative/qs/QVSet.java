package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QN#value() Textwertmenge} mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QVSet extends QXSet<String, QVSet> {

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Hyperknoten} zurück, die einen der Textwerte dieser Menge {@link QN#value() besitzen}. Sie ist
	 * damit eine Abkürzung für {@link QNSet#havingValues(QVSet) this.owner().nodes().havingValues(this)}.
	 *
	 * @return Hyperknoten mit Textwert in dieser Menge. */
	public QNSet nodes();

	/** Diese Methode hat keinen effekt und gibt stets {@code false} zurück.
	 *
	 * @return {@code false}. */
	@Override
	public boolean putAll();

	/** Diese Methode hat keinen effekt und gibt stets {@code false} zurück.
	 *
	 * @return {@code false}. */
	@Override
	public boolean popAll();

	/** {@inheritDoc} Sie liefert damit {@link QS#newValues(Iterable) this.owner().newValues(this)}. */
	@Override
	public QVSet toCopy();

}
