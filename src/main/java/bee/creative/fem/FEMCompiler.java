package bee.creative.fem;

import java.util.LinkedHashMap;
import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert einen Kompiler, welcher als {@link FEMParser} auf den {@link FEMScript#types() Abscnittstypen} eines {@link FEMScript
 * aufbereiteten Quelltexts} arbeitet.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCompiler {

	private final FEMScript script;

	private final Map<String, FEMProxy> proxies = new LinkedHashMap<>();

	private Token token;

	/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
	 *
	 * @param script Quelltext.
	 * @throws NullPointerException Wenn {@code script} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMCompiler(final FEMScript script) throws NullPointerException {

		this.script = script;
	}

	/** Diese Methode gibt den zu kompilierenden aufbereiteten Quelltext zurück. */
	public FEMScript script() {
		return this.script;
	}

	/** Diese Methode gibt die {@link Token#start() Startposition} des {@link #token() aktuellen Abscnitts} zurück. Sie ist eine Abkürzung für
	 * {@link Token#start() this.token().start()}. */
	public int scriptIndex() {
		return this.token().start();
	}

	/** Diese Methode gibt den Quelltexts zurück. Sie ist eine Abkürzung für {@link FEMScript#source() this.script().source()}. */
	public String scriptSource() {
		return this.script().source();
	}

	/** Diese Methode gibt die Spaltennummer zur {@link #scriptIndex() aktuellen Quelltextposition} zurück. */
	public int scriptColIndex() {
		final String src = this.scriptSource();
		final int pos = this.scriptIndex(), add = src.lastIndexOf('\n', pos);
		return (pos + 1) - (add < 0 ? 0 : add);
	}

	/** Diese Methode gibt die Zeilennummer zur {@link #scriptIndex() aktuellen Quelltextposition} zurück. */
	public int scriptRowIndex() {
		final String src = this.scriptSource();
		int res = 0;
		for (int pos = this.scriptIndex() + 1; pos >= 0; pos = src.lastIndexOf('\n', pos - 1), res++) {}
		return res;
	}

	/** Diese Methode gibt den aktuellen Abscnitt zurück. */
	public Token token() {
		return this.token;
	}

	public FEMCompiler token(final Token token) throws NullPointerException {
		this.token = Objects.notNull(token);
		return this;
	}

	/** Diese Methode gibt den Platzhalter mit dem gegebenen {@link FEMProxy#name() Namen} zurück.
	 *
	 * @param name {@link FEMProxy#from(String) Name und Kennung} des Platzhalters.
	 * @return Platzhalterfunktion. */
	public FEMProxy proxy(final String name) throws NullPointerException {
		FEMProxy res = this.proxies.get(name);
		if (res != null) return res;
		this.proxies.put(name, res = FEMProxy.from(name));
		return res;
	}

	/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.
	 *
	 * @return Abbildung von Namen auf Platzhalter. */
	public Map<String, FEMProxy> proxies() {
		return this.proxies;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.script(), this.proxies());
	}

}