package bee.creative.qs;

/** Diese Schnittstelle definiert eine Textwertmenge mit Bezug zu einem{@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QVSet extends QXSet<String> {

	@Override
	QVSet clone();

	/** Diese Methode gibt die Menge der Knoen zu allen in dieser Menge enthaltenen {@link QN#value() Textwerte} zurück.
	 * 
	 * @return Knotenmenge. */
	QNSet nodes();

}
