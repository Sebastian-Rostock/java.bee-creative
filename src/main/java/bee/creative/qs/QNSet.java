package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von {@link QN Hyperknoten}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QNSet extends QXSet<QN, QNSet> {

	/** Diese Methode ist eine Abkürzung für {@link QVSet#withNodes(QNSet) this.owner().values().withNodes(this)}.
	 * 
	 * @return Wertzuweisungen mit Hyperknoten in dieser Menge. */
	public QVSet values();

}