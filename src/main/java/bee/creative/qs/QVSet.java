package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QXSet Menge} von {@link QV Wertzuweisungen}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QVSet extends QXSet<QV, QVSet> {

	/** Diese Methode gibt eine Mengensicht auf alle {@link QN Hyperknoten} zurück, die in den Wertzuweisungen dieser Menge {@link QV#node() aufgeführten} sind.
	 *
	 * @return Hyperknoten der Wertzuweisungen dieser Menge. */
	public QNSet nodes();

	/** Diese Methode gibt eine Mengensicht auf alle nicht leeren Textwerte zurück, die in den Wertzuweisungen dieser Menge {@link QV#string() aufgeführten} sind.
	 *
	 * @return nicht leere Textwerte der Wertzuweisungen dieser Menge. */
	public QSSet strings();

	/** Diese Methode gibt eine Mengensicht auf die Wertzuweisungen zurück, deren {@link QV#node() Hyperknoten} in der gegebenen Menge enthalten sind.
	 * 
	 * @param nodes Hyperknotenfilter.
	 * @return Wertzuweisungen mit den gegebenen Hyperknoten. */
	public QVSet withNodes(final QNSet nodes) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt eine Mengensicht auf die Wertzuweisungen zurück, deren {@link QV#string() Textwerte} in der gegebenen Menge enthalten sind.
	 * 
	 * @param strings Textwertfilter.
	 * @return Wertzuweisungen mit den gegebenen Textwerten. */
	public QVSet withStrings(final QSSet strings) throws NullPointerException, IllegalArgumentException;

}
