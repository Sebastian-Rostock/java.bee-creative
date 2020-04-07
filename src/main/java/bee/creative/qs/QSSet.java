package bee.creative.qs;

/** Diese Schnittstelle definiert eine {@link QN#get() Textwertmenge} mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QSSet extends QXSet<String, QSSet> {

	/** Diese Methode ist eine Abkürzung für {@link QVSet#withStrings(QSSet) this.owner().values().withStrings(this)}.
	 *
	 * @return Wertzuweisungen mit Textwert in dieser Menge. */
	public QVSet values();

	/** Diese Methode ist eine Abkürzung für {@link #values() values().putAll()} und gibt damit stets {@code false} zurück.
	 *
	 * @return {@code false}. */
	@Override
	public boolean putAll();

	/** Diese Methode ist eine Abkürzung für {@link #values() values().popAll()}. */
	@Override
	boolean popAll();

}
