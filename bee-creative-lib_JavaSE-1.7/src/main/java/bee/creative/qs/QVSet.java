package bee.creative.qs;

import java.util.Map;

/** Diese Schnittstelle definiert eine {@link QN#value() Textwertmenge} mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QVSet extends QXSet<String, QVSet> {

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Textwerte aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde. Damit wird die Verbindung dieser Textwerte zu den damit identifizierten {@link QN Hyperknoten}
	 * aufgelöst. Die {@link QN Hyperknoten} bleiben jedoch erhalten.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	@Override
	public boolean popAll();

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Hyperknoten} zurück, die einen der Textwerte dieser Menge {@link QN#value() besitzen}. Sie ist
	 * damit eine Abkürzung für {@link QNSet#havingValues(QVSet) this.owner().nodes().havingValues(this)}.
	 *
	 * @return Hyperknoten mit Textwert in dieser Menge. */
	public QNSet nodes();

	/** Diese Methode ergänzt die gegebene Abbildung um die {@link QN Hyperknoten}, die einen der Textwerte dieser Menge {@link QN#value() besitzen}.
	 * 
	 * @param nodes Abbildung von {@link QN#value() Textwerten} auf {@link QN Hyperknoten}. */
	public void nodes(Map<String, QN> nodes);

	/** {@inheritDoc} Sie liefert damit {@link QS#newValues(Iterable) this.owner().newValues(this)}. */
	@Override
	public QVSet copy();

}
