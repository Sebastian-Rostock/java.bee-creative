package bee.creative.fem;

import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.util.HashMap;
import bee.creative.util.Parser.Result;
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert ein Objekt zur Bereitstellung eines {@link #token() typisierten Abschnitt} eines {@link Result aufbereiteten Quelltexts}, um
 * diesen Abschnitt in {@link FEMFunction Funktionen} überführen zu können. Dazu wird auch eine {@link #proxies() Abbildung von Namen auf Platzhalter} zur
 * Wiederverwendung derselben bereitgestellt.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMToken {

	private final Token token;

	private final Map<String, FEMFunction> proxies;

	/** Dieser Konstruktor initialisiert das Objekt mit dem {@link Token#EMPTY leeren Abschnitt} sowie einer leeren Plazhalterabbildung. */
	public FEMToken() {
		this.token = Token.EMPTY;
		this.proxies = new HashMap<>();
	}

	/** Dieser Konstruktor initialisiert das Objekt mit dem gegebenen Abschnitt sowie der Plazhalterabbildung des gegebenen Objekts. */
	protected FEMToken(final FEMToken parent, final Token token) throws NullPointerException {
		this.token = token;
		this.proxies = parent.proxies;
	}

	/** Diese Methode gibt einen neuen Kompiller mit dem gegebenen typisierten Abschnitt zurück. Der gelieferte Kompiler verwendet die {@link #proxies() Abbildung
	 * von Namen auf Platzhalter} dieses Kompilers. */
	public FEMToken with(final Token token) throws NullPointerException {
		return new FEMToken(this, Objects.notNull(token));
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

	/** Diese Methode gibt die bisher zur Wiederverwendung erzeugten Platzhalter zurück.
	 *
	 * @return Abbildung von Namen auf Platzhalter. */
	public Map<String, FEMFunction> proxies() {
		return this.proxies;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.token(), this.proxies());
	}

}