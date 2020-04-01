package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QN Kantenmenge} mit Bezug zu einem{@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface QESet extends QXSet<QE> {

	QNSet contexts();

	QNSet predicates();

	QNSet subjects();

	QNSet objects();

}
