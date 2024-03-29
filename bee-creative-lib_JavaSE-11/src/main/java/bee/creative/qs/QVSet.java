package bee.creative.qs;

import bee.creative.util.Filter;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;

/** Diese Schnittstelle definiert eine {@link QN#value() Textwertmenge} mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QVSet extends QXSet<String, QVSet> {

	/** {@inheritDoc} Sie liefert damit {@link QS#newValues(Iterable) this.owner().newValues(this)} oder {@code this}. */
	@Override
	QVSet2 copy();

	/** {@inheritDoc} Sie liefert damit {@link QS#newNodes(Iterable) this.owner().newValues(Iterables.filter(this, filter))}. */
	@Override
	default QVSet2 copy(Filter<? super String> filter) throws NullPointerException {
		return this.owner().newValues(Iterables.filter(this, filter));
	}

	/** Diese Methode speichert alle in dieser Menge enthaltenen Textwerte im {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde. Textwerte ohne bisher damit identifizierten {@link QN Hyperknoten} werden mit neuen Hyperknoten verbunden.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	boolean putAll();

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Textwerte aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde. Damit wird die Verbindung dieser Textwerte zu den damit identifizierten {@link QN Hyperknoten}
	 * aufgelöst. Die {@link QN Hyperknoten} bleiben jedoch erhalten.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	@Override
	boolean popAll();

	/** Diese Methode liefert eine Mengensicht auf alle {@link QN Hyperknoten}, die einen der Textwerte dieser Menge {@link QN#value() besitzen}. Sie ist damit
	 * eine Abkürzung für {@link QNSet#havingValues(QVSet) this.owner().nodes().havingValues(this)}.
	 *
	 * @return Hyperknoten mit Textwert in dieser Menge. */
	QNSet nodes();

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten}, die einen der Textwerte dieser Menge {@link QN#value() besitzen}.
	 *
	 * @param nodes Abbildung von {@link QN#value() Textwerten} auf {@link QN Hyperknoten}. */
	void nodes(Setter<? super String, ? super QN> nodes);

}
