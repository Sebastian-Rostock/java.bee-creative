package bee.creative.qs;

/** Diese Schnittstelle definiert eine Wertzuweisung, welche einen {@link QN Hyperknoten} mit einem {@link QN#get() Textwert} verbindet.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QV extends QX {

	/** Diese Methode gibt den Hyperknoten zurück.
	 *
	 * @return Hyperknoten. */
	public QN node();

	/** Diese Methode gibt den {@link QN#get() Textwert} zurück.
	 *
	 * @return Textwert. */
	public String string();

}
