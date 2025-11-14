package bee.creative.fem;

import java.util.LinkedList;
import java.util.List;
import bee.creative.util.HashSet;
import bee.creative.util.Parser;

/** Diese Klasse implementiert den Parser zur Zerlegung einer Zeichenkette in Abschnitte eines {@link bee.creative.util.Parser.Result aufbereiteten Quelltexts}
 * im Rahmen gegebener {@link #params() Parameternamen}.
 *
 * @see FEMDomain#parseScriptToken(FEMParser)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMParser extends Parser {

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public final LinkedList<String> params() {
		return this.params;
	}

	/** Diese Methode setzt die Parameternamen und gibt {@code this} zurück.
	 *
	 * @param params Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMParser params(final List<String> params) throws NullPointerException {
		if (params.contains(null)) throw new NullPointerException();
		this.params.clear();
		this.params.addAll(params);
		return this;
	}

	/** Diese Methode gibt die Menge der aktuell erfassten Platzhalternamen zurück.
	 *
	 * @return Platzhalternamen. */
	public final HashSet<String> proxies() {
		return this.proxies;
	}

	private final LinkedList<String> params = new LinkedList<>();

	private final HashSet<String> proxies = new HashSet<>();

}