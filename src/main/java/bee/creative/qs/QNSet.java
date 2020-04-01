package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QN Knotenmenge} mit Bezug zu einem{@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QNSet extends QXSet<QN> {

	@Override
	QNSet clone();

	/** Diese Methode gibt die Menge der nichtleeren {@link QN#value() Textwerte} aller in dieser Menge enthaltenen Knoten zurück.
	 *
	 * @return Menge der Textwerte. */
	QVSet values();

}