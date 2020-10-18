package bee.creative.fem;

import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.util.HashMap;
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert ein Objekt zur Bereitstellung eines {@link #token() typisierten Abschnitt} eines {@link FEMScript aufbereiteten Quelltexts}, um
 * diesen Abschnitt in {@link FEMFunction Funktionen} überführen zu können. Dazu wird auch eine {@link #proxies() Abbildung von Namen auf Platzhalter} zur
 * {@link #proxy(String) Bestückung bzw. Wiederverwendung} derselben bereitgestellt.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCompiler {

	private final Token token;

	private final Map<String, FEMProxy> proxies;

	private FEMCompiler(final FEMCompiler parent, final Token token) throws NullPointerException {
		this.token = token;
		this.proxies = parent.proxies;
	}

	/** Dieser Konstruktor initialisiert den Kompiler mit dem {@link Token#EMPTY leeren Abschnitt} sowie eine leere Plazhalterabbildung. */
	public FEMCompiler() {
		this.token = Token.EMPTY;
		this.proxies = new HashMap<>();
	}

	/** Diese Methode gibt einen neuen Kompiller mit dem gegebenen typisierten Abschnitt zurück. Der gelieferte Kompiler verwendet die {@link #proxies() Abbildung
	 * von Namen auf Platzhalter} dieses Kompilers. */
	public FEMCompiler with(final Token token) throws NullPointerException {
		return new FEMCompiler(this, Objects.notNull(token));
	}

	/** Diese Methode gibt den aktuellen Abschnitt zurück. */
	public Token token() {
		return this.token;
	}

	/** Diese Methode gibt die Zeichenkette zurück, auf die sich die Positionsangaben dieses Abschnitts beziehen. Sie ist eine Abkürzung für {@link Token#source()
	 * this.token().source()}. */
	public String source() {
		return this.token.source();
	}

	/** Diese Methode gibt die {@link Token#start() Startposition} des {@link #token() aktuellen Abschnitts} zurück. Sie ist eine Abkürzung für
	 * {@link Token#start() this.token().start()}. */
	public int srcIndex() {
		return this.token.start();
	}

	/** Diese Methode gibt die Spaltennummer zur {@link #srcIndex() aktuellen Quelltextposition} zurück. */
	public int colIndex() {
		final String src = this.source();
		final int pos = this.srcIndex(), add = src.lastIndexOf('\n', pos);
		return (pos + 1) - (add < 0 ? 0 : add);
	}

	/** Diese Methode gibt die Zeilennummer zur {@link #srcIndex() aktuellen Quelltextposition} zurück. */
	public int rowIndex() {
		final String src = this.source();
		int res = 0;
		for (int pos = this.srcIndex() + 1; pos >= 0; pos = src.lastIndexOf('\n', pos - 1), res++) {}
		return res;
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
		return Objects.toInvokeString(this, this.token(), this.proxies());
	}

}