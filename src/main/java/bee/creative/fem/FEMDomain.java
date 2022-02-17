package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.fem.FEMFunction.FutureFunction;
import bee.creative.fem.FEMFunction.TraceFunction;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Strings;
import bee.creative.util.Parser;
import bee.creative.util.Parser.Result;
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert domänenspezifische Parse-, Formatierungs- und Kompilationsmethoden, welche der Übersetzung von Zeichenketten, aufbereitete
 * Quelltexten und Funktionen ineinander dienen.
 * <p>
 * Die auf {@link FEMParser Zeichenketten} operierenden {@code parse}-Methoden erkennen die darin Bedeutung tragenden {@link Token Abschnitte},
 * {@link FEMParser#push(Token) erfassen} diese und versehen sie mit entsprechender {@link Token#type() Typkennung} und {@link Token#tokens() Struktur}. Die auf
 * diesen typisierten und strukturierten {@link FEMToken Abschnitten} operierenden {@code parse}-Methoden übersetzen diese schließlich in Werte und Funktionen.
 * Die auf {@link FEMFunction Funktionen} operierenden {@code format}-Methoden erzeugen dazu wieder deren Textdarstellung.
 * <p>
 * Nachfahren sollten die Methoden {@link #parseValueData(FEMToken, String)} und {@link #parseFunctionData(FEMToken, String)} überschreiben, um
 * anwendungsspezifisch die Kennung bzw. Textdarstellung von Konstanten un deren Werte bzw. Funktionen zu überführen. Wenn an Stelle von {@link FEMClosure
 * bindenden Parameterfuktionen} einfache {@link FEMHandler Funktionszeiger} verwendet werden sollen, muss die Methode {@link #parseClosureData(FEMToken)}
 * überschriben werden und das Ergebnis von {@link #parseHandler1Data(FEMToken)} bzw. {@link #parseHandler2Data(FEMToken)} liefern.
 * <p>
 * Die EBNF der erkannten Grammatik ist:
 * <table border="0" cellpadding="2" cellspacing="0">
 * <tr>
 * <td>{@link #parseScriptToken(FEMParser) SCRIPT}</td>
 * <td>::=</td>
 * <td>{@code GROUP1}</td>
 * </tr>
 * <tr>
 * <td>{@link #parseGroupToken(FEMParser, boolean) GROUP1}</td>
 * <td>::=</td>
 * <td>{@code SC} ({@code FUNCTION1} ({@code SC} {@code ";"} {@code SC} {@code FUNCTION1})* {@code SC})?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseGroupToken(FEMParser, boolean) GROUP2}</td>
 * <td>::=</td>
 * <td>{@code SC} ({@code FUNCTION2} ({@code SC} {@code ";"} {@code SC} {@code FUNCTION2})* {@code SC})?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseFunctionToken(FEMParser, boolean) FUNCTION1}</td>
 * <td>::=</td>
 * <td>({@code VALUE} | {@code PROXY}) ({@code SC} {@code "("} {@code GROUP2} {@code ")"})*</td>
 * </tr>
 * <tr>
 * <td>{@link #parseFunctionToken(FEMParser, boolean) FUNCTION2}</td>
 * <td>::=</td>
 * <td>({@code VALUE} | {@code PARAM}) ({@code SC} {@code "("} {@code GROUP2} {@code ")"})*</td>
 * </tr>
 * <tr>
 * <td>{@link #parseParamToken(FEMParser) PARAM}</td>
 * <td>::=</td>
 * <td>{@code "$"} ({@code IDENT} | {@code INDEX} | {@code NAME})?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseHandler2Token(FEMParser, Token) PROXY}</td>
 * <td>::=</td>
 * <td>({@code IDENT} | {@code CONST}) {@code SC} {@code HANDLER}</td>
 * </tr>
 * <tr>
 * <td>{@link #parseValueToken(FEMParser, boolean) VALUE}</td>
 * <td>::=</td>
 * <td>{@code ARRAY} | {@code HANDLER} | {@code STRING1} | {@code STRING2} | {@code IDENT} | {@code CONST}</td>
 * </tr>
 * <tr>
 * <td>{@link #parseArrayToken(FEMParser) ARRAY}</td>
 * <td>::=</td>
 * <td>{@code "["} ({@code SC} {@code VALUE} ({@code SC} {@code ";"} {@code SC} {@code VALUE})*)? {@code SC} {@code "]"}</td>
 * </tr>
 * <tr>
 * <td>{@link #parseHandler1Token(FEMParser) HANDLER}</td>
 * <td>::=</td>
 * <td><code>"{"</code> ({@code SC} ({@code IDENT} | {@code NAME}) ({@code SC} {@code ";"} {@code SC} ({@code IDENT} | {@code NAME}))*)? {@code SC} {@code ":"}
 * {@code SC} {@code FUNCTION2} {@code SC} <code>"}"</code></td>
 * </tr>
 * <tr>
 * <td>{@link #parseIgnoreToken(FEMParser) SC}</td>
 * <td>::=</td>
 * <td>({@code SPACE} | {@code COMMENT})?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseNameToken(FEMParser) NAME}</td>
 * <td>::=</td>
 * <td>? Zeichenkette ohne Leerraum sowie ohne {@code ";"}, {@code "/"}, {@code "("}, {@code ")"}, <code>"{"</code>, <code>"}"</code>, {@code ":"} ?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseConstToken(FEMParser) CONST}</td>
 * <td>::=</td>
 * <td>? Zeichenkette ohne Leerraum sowie ohne {@code ";"}, {@code "/"}, {@code "("}, {@code ")"}, <code>"{"</code>, <code>"}"</code>, {@code "["}, {@code "]"}
 * ?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseIndexToken(FEMParser) INDEX}</td>
 * <td>::=</td>
 * <td>? Zeichenkette aus Ziffern ?</td>
 * </tr>
 * <tr>
 * <td>{@link #parseSpaceToken(FEMParser) SPACE}</td>
 * <td>::=</td>
 * <td>? Zeichenkette aus Leerraum ?</td>
 * </tr>
 * </table>
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain extends BaseObject {

	/** Dieses Feld speichert die normale {@link FEMDomain}. */
	public static final FEMDomain DEFAULT = new FEMDomain();

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseScriptToken(FEMParser) Funktionsliste}. */
	public static int TYPE_SCRIPT = '*';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseErrorToken(FEMParser) Quelltextfehlers}. */
	public static int TYPE_ERROR = '!';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseIdentToken(FEMParser) maskierten Kennung}. */
	public static int TYPE_IDENT = '<';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseIndexToken(FEMParser) Parameterposition}. */
	public static int TYPE_INDEX = '#';

	/** Dieses Feld speichert den Abschnittstyp eines {@link #parseNameToken(FEMParser) Parameternamens}. */
	public static int TYPE_NAME = '~';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseConstToken(FEMParser) Konstanten}. */
	public static int TYPE_CONST = '?';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseArrayToken(FEMParser) Wertliste}. */
	public static int TYPE_ARRAY = '[';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseGroupToken(FEMParser, boolean) Funktionsliste}. */
	public static int TYPE_GROUP = '(';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseParamToken(FEMParser) Parameterfunktion}. */
	public static int TYPE_PARAM = '$';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseString1Token(FEMParser) Zeichenkette}. */
	public static int TYPE_STRING1 = '\'';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseString2Token(FEMParser) Zeichenkette}. */
	public static int TYPE_STRING2 = '\"';

	/** Dieses Feld speichert den Abschnittstyp eines {@link #parseHandler1Token(FEMParser) Funktionszeigers}. */
	public static int TYPE_HANDLER1 = '{';

	/** Dieses Feld speichert den Abschnittstyp eines {@link #parseHandler2Token(FEMParser, Token) Funktionszeigers als Platzhalter}. */
	public static int TYPE_HANDLER2 = '=';

	/** Dieses Feld speichert den Abschnittstyp eines {@link #parseCommentToken(FEMParser) Kommentars}. */
	public static int TYPE_COMMENT = '/';

	/** Dieses Feld speichert den Abschnittstyp einer {@link #parseCompositeToken(FEMParser, Token) Funktionsverkettung}. */
	public static int TYPE_COMPOSITE = '.';

	/** Diese Methode parst die als maskierte Zeichenkette gegebene Konstente und gibt diese zurück. Sie realisiert damit die Umkehroperation zu
	 * {@link #printIdent(String)}. Das Parsen erfolgt über {@link Strings#parseSequence(CharSequence, char, char, char) Strings.parseSequence(src, '<', '\\',
	 * '>')}.
	 *
	 * @param src maskierte Zeichenkette.
	 * @return Zeichenkette oder {@code null}. */
	public String parseIdent(final String src) throws NullPointerException {
		return Strings.parseSequence(src, '<', '\\', '>');
	}

	/** Diese Methode überführt den von {@link #parseIdentToken(FEMParser)} ermittelten Abschnitt in eine Kennung und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected String parseIdentData(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_IDENT) return null;
		final String res = this.parseIdent(tok.toString());
		if (res != null) return res;
		throw this.parseErrorData(src, null);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Kennung und gibt ihn zurück. Die Erkennung erfolgt über
	 * {@link #parseSequenceToken(FEMParser, int, char, char, char) this.parseSequence(src, FEMDomain.TYPE_IDENT, '<', '\\', '>')}. Als Abschnittstyp wird
	 * {@link #TYPE_IDENT} verwendet. Wenn keine maskierte Kennung gefunden wurden, wird {@code null} geliefert. */
	protected Token parseIdentToken(final FEMParser src) throws NullPointerException {
		return this.parseSequenceToken(src, FEMDomain.TYPE_IDENT, '<', '\\', '>');
	}

	/** Diese Methode parst die ggf. als {@link #parseIdent(String) maskierte} Zeichenkette gegebene Kennung und gibt diese zurück. Sie realisiert damit die
	 * Umkehroperation zu {@link #printConst(String)}.
	 *
	 * @param src Zeichenkette.
	 * @return Zeichenkette oder {@code null}. */
	public String parseConst(final String src) throws NullPointerException {
		return src.charAt(0) != '<' ? src : this.parseIdent(src);
	}

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link Result aufbereiteten Quelltext} und gibt diesen zurück.
	 *
	 * @see #parseScriptToken(FEMParser)
	 * @param src Zeichenkette, die geparst werden soll.
	 * @return aufbereiteter Quelltext. */
	public Result parseResult(final String src) throws NullPointerException {
		final FEMParser parser = new FEMParser(src);
		final Token res = this.parseScriptToken(parser);
		return Result.from(res, parser.tokens());
	}

	/** Diese Methode ist eine Abkürzung für {@link #parseScript(Result) this.parseGroup(this.parseResult(src))}. */
	public List<FEMFunction> parseScript(final String src) throws NullPointerException, IllegalArgumentException {
		return this.parseScript(this.parseResult(src));
	}

	/** Diese Methode überführt den gegebenen {@link Result Quelltext} in eine {@link List Liste} von {@link FEMFunction Funktionen} und gibt diese zurück.
	 *
	 * @param src Quelltext, der bspw. über {@link #parseResult(String)} erzeugt wurde.
	 * @return Funktionsliste. */
	public List<FEMFunction> parseScript(final Result src) throws NullPointerException, IllegalArgumentException {
		return this.parseScriptData(new FEMToken().with(src.root()));
	}

	/** Diese Methode überführt den von {@link #parseScriptToken(FEMParser)} ermittelten Abschnitt in eine Funktionsliste und gibt diese zurück. */
	protected List<FEMFunction> parseScriptData(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_SCRIPT) throw this.parseErrorData(src, null);
		try {
			for (final Object val: (Iterable<?>)tok.value()) {
				final String name = val.toString();
				src.proxies().put(name, FEMProxy.from(name));
			}
		} catch (final Exception cause) {
			throw this.parseErrorData(src, cause);
		}
		return this.parseGroupData(src);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Funktionsliste und gibt deren Elternabschnitt zurück. Als
	 * Abschnittstyp des Elternabschnitts wird {@link #TYPE_SCRIPT} verwendet. Sein Abschnittswert ist die {@link Set Menge} der {@link String Namen} aller als
	 * {@link #parseHandler2Token(FEMParser, Token) Funktionszeiger} angegebenen {@link FEMParser#proxies() Platzhalter}. Die {@link Token#tokens()
	 * Kindabschnitte} sind die Abschnitte der {@link #parseGroupToken(FEMParser, boolean) Funktionen}. */
	protected Token parseScriptToken(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		final List<Token> funs = this.parseGroupToken(src, true);
		if (src.symbol() >= 0) {
			funs.add(this.parseErrorToken(src));
		}
		return src.make(FEMDomain.TYPE_SCRIPT, pos, funs).value(new HashSet<>(src.proxies()));
	}

	/** Diese Methode überführt den von {@link #parseGroupToken(FEMParser, boolean)} ermittelten Abschnitt in eine Funktionsliste und gibt diese zurück. */
	protected List<FEMFunction> parseGroupData(final FEMToken src) throws NullPointerException, FEMException {
		final List<FEMFunction> res = new ArrayList<>();
		for (final Token tok: src.token()) {
			res.add(this.parseFunctionData(src.with(tok)));
		}
		return res;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Funktionsliste und gibt die Abschnitte der Funktionen zurück. */
	protected List<Token> parseGroupToken(final FEMParser src, final boolean allowProxy) throws NullPointerException {
		final List<Token> res = new ArrayList<>();
		this.parseIgnoreToken(src);
		Token fun = this.parseFunctionToken(src, allowProxy);
		if (fun == null) return res;
		res.add(fun);
		while (true) {
			this.parseIgnoreToken(src);
			if (src.symbol() != ';') return res;
			src.push(';');
			src.skip();
			this.parseIgnoreToken(src);
			fun = this.parseFunctionToken(src, allowProxy);
			if (fun != null) {
				res.add(fun);
			} else {
				res.add(this.parseErrorToken(src));
			}
		}
	}

	/** Diese Methode liefert die beschriftete {@link FEMException Ausnahme} zum gegebeen Abschnitt. */
	protected FEMException parseErrorData(final FEMToken src, final Throwable cause) throws NullPointerException {
		String str = src.token().toString();
		str = str.length() > 40 ? str.substring(0, 39) + "…" : str;
		final String parseErrorMessage = String.format("Fehler an Position %s:%s gefunden: %s", src.rowIndex(), src.colIndex(), str);
		return FEMException.from(cause).push(parseErrorMessage);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} der Eingabe bis zum nächsten Terminal bzw. Ende der Eingabe als Fehler und
	 * gibt ihn zurück. Als Abschnittstyp wird {@link #TYPE_ERROR} verwendet. Der Abschnitt wird auch dann geliefert, wenn er leer ist. */
	protected Token parseErrorToken(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		LOOP: for (int sym = src.symbol(); sym >= 0; sym = src.skip()) {
			switch (sym) {
				case ':':
				case ';':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
				break LOOP;
			}
		}
		return src.push(FEMDomain.TYPE_ERROR, pos);
	}

	/** Diese Methode überführt den von {@link #parseConstToken(FEMParser)} ermittelten Abschnitt in eine Kennung und gibt diese zurück. */
	protected String parseConstData(final FEMToken src) throws NullPointerException {
		return src.token().toString();
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Konstanten und gibt ihn zurück. Als Abschnittstyp wird
	 * {@link #TYPE_CONST} verwendet. Wenn keine Konstante erkant wurde, wird {@code null} geliefet. */
	protected Token parseConstToken(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		LOOP: for (int sym = src.symbol(); sym > ' '; sym = src.skip()) {
			switch (sym) {
				case ';':
				case '/':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
				break LOOP;
			}
		}
		return pos != src.index() ? src.push(FEMDomain.TYPE_CONST, pos) : null;
	}

	/** Diese Methode überführt den von {@link #parseArrayToken(FEMParser)} ermittelten Abschnitt in eine Wertliste und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected FEMValue parseArrayData(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_ARRAY) return null;
		final List<FEMValue> res = new ArrayList<>();
		for (final Token item: tok) {
			res.add(this.parseValueData(src.with(item)));
		}
		return FEMArray.from(res);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Wertliste und gibt deren Elternabschnitt zurück. Als Abschnittstyp
	 * des Elternabschnitts wird {@link #TYPE_ARRAY} verwendet. Die {@link Token#tokens() Kindabschnitte} sind die der {@link #parseValueToken(FEMParser, boolean)
	 * Werte}. */
	protected Token parseArrayToken(final FEMParser src) throws NullPointerException {
		int sym = src.symbol();
		if (sym != '[') return null;
		final int pos = src.index();
		final List<Token> toks = new ArrayList<>();
		src.push('[');
		src.skip();
		this.parseIgnoreToken(src);
		sym = src.symbol();
		if (sym == ']') {
			src.push(']');
			src.skip();
		} else if (sym < 0) {
			toks.add(this.parseErrorToken(src));
		} else {
			Token tok = this.parseValueToken(src, false);
			if (tok == null) {
				toks.add(this.parseErrorToken(src));
			} else {
				toks.add(tok);
				while (true) {
					this.parseIgnoreToken(src);
					sym = src.symbol();
					if (sym == ']') {
						src.push(']');
						src.skip();
						break;
					} else if ((sym < 0) || (sym != ';')) {
						toks.add(this.parseErrorToken(src));
						break;
					} else {
						src.push(';');
						src.skip();
						this.parseIgnoreToken(src);
						tok = this.parseValueToken(src, false);
						if (tok == null) {
							toks.add(this.parseErrorToken(src));
							break;
						} else {
							toks.add(tok);
						}
					}
				}
			}
		}
		return src.make(FEMDomain.TYPE_ARRAY, pos, toks);
	}

	/** Diese Methode überführt den von {@link #parseString1Token(FEMParser)} ermittelten Abschnitt in eine Zeichenkette und gibt diese zurück. Für andere
	 * Abschnitte liefert sie {@code null}. */
	protected FEMValue parseString1Data(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_STRING1) return null;
		final String res = Strings.parseSequence(tok.toString(), '\"', '\\', '\"');
		if (res != null) return FEMString.from(res);
		throw this.parseErrorData(src, null);
	}

	/** Diese Methode überführt den von {@link #parseString2Token(FEMParser)} ermittelten Abschnitt in eine Zeichenkette und gibt diese zurück. Für andere
	 * Abschnitte liefert sie {@code null}. */
	protected FEMValue parseString2Data(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_STRING2) return null;
		final String res = Strings.parseSequence(tok.toString(), '\'', '\\', '\'');
		if (res != null) return FEMString.from(res);
		throw this.parseErrorData(src, null);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette und gibt ihn zurück. Die Erkennung erfolgt
	 * über {@link #parseSequenceToken(FEMParser, int, char, char, char) this.parseSequence(parser, FEMDomain.TYPE_STRING1, '\"', '\\', '\"')}. Als Abschnittstyp
	 * wird {@link #TYPE_STRING1} verwendet. Wenn keine maskierte Zeichenkette gefunden wurden, wird {@code null} geliefert. */
	protected Token parseString1Token(final FEMParser src) throws NullPointerException {
		return this.parseSequenceToken(src, FEMDomain.TYPE_STRING1, '\"', '\\', '\"');
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette und gibt ihn zurück. Die Erkennung erfolgt
	 * über {@link #parseSequenceToken(FEMParser, int, char, char, char) this.parseSequence(parser, FEMDomain.TYPE_STRING2, '\'', '\\', '\'')}. Als Abschnittstyp
	 * wird {@link #TYPE_STRING2} verwendet. Wenn keine maskierte Zeichenkette gefunden wurden, wird {@code null} geliefert. */
	protected Token parseString2Token(final FEMParser src) throws NullPointerException {
		return this.parseSequenceToken(src, FEMDomain.TYPE_STRING2, '\'', '\\', '\'');
	}

	/** Diese Methode überführt den von {@link #parseParamToken(FEMParser)} ermittelten Abschnitt in eine Parameterwerte liefernde Funktion
	 * ({@link FEMFrame#FUNCTION} oder {@link FEMParam#from(int)}) und gibt diese zurück. Für andere Abschnitte liefert sie {@code null}. */
	protected FEMFunction parseParamData(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_PARAM) return null;
		if (tok.size() == 0) return FEMFrame.FUNCTION;
		try {
			return FEMParam.from((Integer)tok.value());
		} catch (final Exception cause) {
			throw this.parseErrorData(src, cause);
		}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Parameterfunktion und gibt deren Elternabschnitt zurück. Als
	 * Abschnittstyp des Elternabschnitts wird {@link #TYPE_PARAM} verwendet. Sein Abschnittswert ist die {@link FEMParam#index() Position} des referenzierten
	 * Parameters bzw. {@code null}, wenn kein {@link Token#tokens() Kindabschnitt} vorliegen. Als Kindabschnitt wird der Abschnitt der
	 * {@link #parseIdentToken(FEMParser) Kennung}, der {@link #parseIndexToken(FEMParser) Position} bzw. des {@link #parseNameToken(FEMParser) Namen} verwendet.
	 * Wenn Kennung, Position bzw. Name ungültig sind, wird als deren Abschnittstyp {@link #TYPE_ERROR} verwendet. */
	protected Token parseParamToken(final FEMParser src) throws NullPointerException {
		if (src.symbol() != '$') return null;
		final Token res = src.push(FEMDomain.TYPE_PARAM);
		src.skip();
		final Token ident = this.parseIdentToken(src), index, name, ref;
		if (ident == null) {
			index = this.parseIndexToken(src);
			name = index == null ? this.parseNameToken(src) : null;
		} else {
			index = null;
			name = ident;
		}
		final int val;
		if (index != null) {
			final String str = index.toString();
			ref = index;
			val = Integers.parseInt(str, 0, str.length()) - 1;
		} else if (name != null) {
			ref = name;
			val = src.params().indexOf(name.toString());
		} else return res;
		if (val < 0) return ref.type(FEMDomain.TYPE_ERROR);
		return src.make(FEMDomain.TYPE_PARAM, res.start(), Arrays.asList(ref)).value(val);
	}

	/** Diese Methode überführt den von {@link #parseHandler1Token(FEMParser)} bzw. {@link #parseHandler2Token(FEMParser, Token)} ermittelten Abschnitt in eine
	 * Parameterfunktion und gibt diese zurück. Für andere Abschnitte liefert sie {@code null}. */
	protected FEMFunction parseClosureData(final FEMToken src) {
		FEMHandler res = this.parseHandler1Data(src);
		if (res == null) return null;
		res = this.parseHandler2Data(src);
		if (res == null) return null;
		return FEMClosure.from(res.toFunction());
	}

	/** Diese Methode überführt den von {@link #parseHandler1Token(FEMParser)} ermittelten Abschnitt in einen Funktionszeiger und gibt diese zurück. Für andere
	 * Abschnitte liefert sie {@code null}. */
	protected FEMHandler parseHandler1Data(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_HANDLER1) return null;
		final int next = tok.size() - 1;
		if (next < 0) throw this.parseErrorData(src, null);
		final FEMFunction fun = this.parseFunctionData(src.with(tok.get(next)));
		return FEMHandler.from(fun);
	}

	/** Diese Methode überführt den von {@link #parseHandler2Token(FEMParser, Token)} ermittelten Abschnitt in einen Funktionszeiger und gibt diese zurück. Für
	 * andere Abschnitte liefert sie {@code null}. */
	protected FEMHandler parseHandler2Data(FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_HANDLER2) return null;
		if (tok.size() != 2) throw this.parseErrorData(src, null);
		src = src.with(tok.get(0));
		final String str = this.parseIdentData(src);
		final String ref = str != null ? str : this.parseConstData(src);
		if (ref == null) throw this.parseErrorData(src, null);
		src = src.with(tok.get(1));
		final FEMValue han = this.parseHandler1Data(src);
		if (han == null) throw this.parseErrorData(src, null);
		try {
			final FEMProxy res = (FEMProxy)src.proxies().get(ref);
			res.set(han.toFunction());
			return FEMHandler.from(res);
		} catch (final Exception cause) {
			throw this.parseErrorData(src, cause);
		}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} eines Funktionszeigers und gibt dessen Elternabschnitt zurück. Als
	 * Abschnittstyp des Elternabschnitts wird {@link #TYPE_HANDLER1} verwendet. Sein Abschnittswert ist die {@link List Liste} der Parameternamen. Als
	 * Kindabschnitte werden die Abschnitte der Namen und Kennungen sowei der der Funktion verwendet. Wenn Namen bzw. Kennungen ungültig sind, wird als deren
	 * Abschnittstyp {@link #TYPE_ERROR} verwendet. */
	protected Token parseHandler1Token(final FEMParser src) throws NullPointerException {
		int sym = src.symbol();
		if (sym != '{') return null;
		final int pos = src.index();
		final List<Token> toks = new ArrayList<>();
		src.push('{');
		src.skip();
		this.parseIgnoreToken(src);
		sym = src.symbol();
		if (sym == ':') {
			src.push(':');
			src.skip();
		} else if (sym < 0) {
			toks.add(this.parseErrorToken(src));
		} else {
			Token nam = this.parseIdentToken(src);
			if (nam == null) {
				nam = this.parseNameToken(src);
			}
			if (nam == null) {
				toks.add(this.parseErrorToken(src));
			} else {
				toks.add(nam);
				while (true) {
					this.parseIgnoreToken(src);
					sym = src.symbol();
					if (sym == ':') {
						src.push(':');
						src.skip();
						break;
					} else if ((sym < 0) || (sym != ';')) {
						toks.add(this.parseErrorToken(src));
						break;
					} else {
						src.push(';');
						src.skip();
						this.parseIgnoreToken(src);
						nam = this.parseIdentToken(src);
						if (nam == null) {
							nam = this.parseNameToken(src);
						}
						if (nam == null) {
							toks.add(this.parseErrorToken(src));
							break;
						} else {
							toks.add(nam);
						}
					}
				}
			}
		}
		final List<String> nams = new ArrayList<>();
		for (final Token tok: toks) {
			final String nam = tok.toString();
			if (nams.contains(nam) || src.params().contains(nam)) {
				tok.type(FEMDomain.TYPE_ERROR);
			}
			nams.add(nam);
		}
		src.params().addAll(0, nams);
		this.parseIgnoreToken(src);
		final Token tok = this.parseFunctionToken(src, false);
		if (tok == null) {
			toks.add(this.parseErrorToken(src));
		} else {
			toks.add(tok);
			this.parseIgnoreToken(src);
			sym = src.symbol();
			if ((sym < 0) || (sym != '}')) {
				toks.add(this.parseErrorToken(src));
			} else {
				src.push('}');
				src.skip();
			}
		}
		src.params().subList(0, nams.size()).clear();
		return src.make(FEMDomain.TYPE_HANDLER1, pos, toks).value(nams);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Platzhalterfunktion mit dem gegebenen Namen. Als Abschnittstyp wird
	 * {@link #TYPE_HANDLER2} verwendet. Die {@link Token#tokens() Kindabschnitte} sind die der gegebenen Kennung und des gefundenen Funktionszeigers. Wenn kein
	 * Funktionszeiger gefunden wurden, wird {@code null} geliefert. */
	protected Token parseHandler2Token(final FEMParser src, final Token name) throws NullPointerException, FEMException {
		this.parseIgnoreToken(src);
		final Token impl = this.parseHandler1Token(src);
		if (impl == null) return null;
		if (!src.proxies().add(name.toString())) {
			name.type(FEMDomain.TYPE_ERROR);
		}
		return src.make(FEMDomain.TYPE_HANDLER2, name.start(), Arrays.asList(name, impl));
	}

	/** Diese Methode überführt den von {@link #parseValueToken(FEMParser, boolean)} ermittelten Abschnitt in einen Wert und gibt diesen zurück. */
	protected FEMValue parseValueData(final FEMToken src) throws NullPointerException, FEMException {
		FEMValue res = this.parseArrayData(src);
		if (res != null) return res;
		res = this.parseString1Data(src);
		if (res != null) return res;
		res = this.parseString2Data(src);
		if (res != null) return res;
		res = this.parseHandler1Data(src);
		if (res != null) return res;
		res = this.parseHandler2Data(src);
		if (res != null) return res;
		final String str = this.parseIdentData(src);
		final String ref = str != null ? str : this.parseConstData(src);
		if (ref == null) throw this.parseErrorData(src, null);
		try {
			res = this.parseValueData(src, ref);
			if (res != null) return res;
		} catch (final Exception cause) {
			throw this.parseErrorData(src, cause);
		}
		throw this.parseErrorData(src, null);
	}

	/** Diese Methode überführt die gegebene Kennung bzw. Textdarstellung einer Konstanten in deren Wert und gibt diesen zurück. Wenn dies nicht möglich ist, wird
	 * {@code null} geliefert.
	 *
	 * @param ref Kennung bzw. Textdarstellung einer Konstanten.
	 * @return Wert der Konstanten oder {@code null}. */
	protected FEMValue parseValueData(final FEMToken src, final String ref) throws NullPointerException {
		if (ref.equals("null")) return FEMVoid.INSTANCE;
		if (ref.equals("true")) return FEMBoolean.TRUE;
		if (ref.equals("false")) return FEMBoolean.FALSE;
		if (ref.startsWith("0x")) return FEMBinary.from(ref);
		if (ref.startsWith("P") || ref.startsWith("-P")) return FEMDuration.from(ref);
		try {
			return FEMInteger.from(ref);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDecimal.from(ref);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDatetime.from(ref);
		} catch (final IllegalArgumentException cause) {}
		return null;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines Werts gibt ihn zurück. */
	protected Token parseValueToken(final FEMParser src, final boolean allowProxy) throws NullPointerException {
		Token res = this.parseArrayToken(src);
		if (res != null) return res;
		res = this.parseString1Token(src);
		if (res != null) return res;
		res = this.parseString2Token(src);
		if (res != null) return res;
		res = this.parseHandler1Token(src);
		if (res != null) return res;
		res = this.parseIdentToken(src);
		if (res == null) {
			res = this.parseConstToken(src);
			if (res == null) return null;
		}
		if (!allowProxy) return res;
		return Objects.notNull(this.parseHandler2Token(src, res), res);
	}

	/** Diese Methode überführt den von {@link #parseFunctionToken(FEMParser, boolean)} ermittelten Abschnitt in eine Funktion und gibt diese zurück. */
	protected FEMFunction parseFunctionData(final FEMToken src) throws NullPointerException, FEMException {
		FEMFunction res = this.parseCompositeData(src);
		if (res != null) return res;
		res = this.parseParamData(src);
		if (res != null) return res;
		res = this.parseArrayData(src);
		if (res != null) return res;
		res = this.parseString1Data(src);
		if (res != null) return res;
		res = this.parseString2Data(src);
		if (res != null) return res;
		res = this.parseClosureData(src);
		if (res != null) return res;
		final String str = this.parseIdentData(src);
		final String ref = str != null ? str : this.parseConstData(src);
		if (ref == null) throw this.parseErrorData(src, null);
		try {
			res = this.parseFunctionData(src, ref);
			if (res != null) return res;
			res = this.parseValueData(src, ref);
			if (res != null) return res;
		} catch (final Exception cause) {
			throw this.parseErrorData(src, cause);
		}
		throw this.parseErrorData(src, null);
	}

	/** Diese Methode überführt die gegebene Kennung bzw. Textdarstellung einer Konstanten in deren Funktion und gibt diese zurück. Wenn dies nicht möglich ist,
	 * wird {@code null} geliefert.
	 *
	 * @param ref Kennung bzw. Textdarstellung einer Konstanten.
	 * @return Funktion der Konstanten oder {@code null}. */
	protected FEMFunction parseFunctionData(final FEMToken src, final String ref) {
		final FEMFunction res = src.proxies().get(ref);
		if (res != null) return res;
		return FEMUtil.get(ref);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Funktion und gibt ihn zurück. Wenn ein Parameter bzw. Wert von einer
	 * in runden Klammern eingeschlossenen Funktionsliste gefolgt wird, führt dies zur Erkennung einer {@link FEMComposite Funktionsverkettung}, deren
	 * Elternabschnitt geliefert wird. Als Abschnittstyp wird dann {@link #TYPE_COMPOSITE} verwendet. Die {@link Token#tokens() Kindabschnitte} sind die der
	 * aufgerufenen Funktion und die der Parameterfunktionsliste, letztere mit dem Abschnittstyp {@link #TYPE_GROUP}. */
	protected Token parseFunctionToken(final FEMParser src, final boolean allowProxy) throws NullPointerException, FEMException {
		Token res = null;
		if (!allowProxy) {
			res = this.parseParamToken(src);
		}
		if (res == null) {
			res = this.parseValueToken(src, allowProxy);
			if (res == null) return null;
		}
		return Objects.notNull(this.parseCompositeToken(src, res), res);
	}

	/** Diese Methode überführt den von {@link #parseCompositeToken(FEMParser, Token)} ermittelten Abschnitt in eine Funtionsverkettung und gibt diese zurück. Für
	 * andere Abschnitte liefert sie {@code null}. */
	protected FEMFunction parseCompositeData(FEMToken src) throws NullPointerException, FEMException {
		Token tok = src.token();
		if (tok.type() != FEMDomain.TYPE_COMPOSITE) return null;
		if (tok.size() != 2) throw this.parseErrorData(src, null);
		final FEMFunction res = this.parseFunctionData(src.with(tok.get(0)));
		tok = tok.get(1);
		src = src.with(tok);
		if (tok.type() != FEMDomain.TYPE_GROUP) throw this.parseErrorData(src, null);
		final List<FEMFunction> pars = this.parseGroupData(src);
		return res.compose(pars);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer beliebig langen Funktonsverkettung und gibt ihn zurück. Als
	 * Abschnittstyp wird {@link #TYPE_COMPOSITE} verwendet. Als Kindabschnitte wird {@code target} sowie ein der Abschnitt der Parameterliste verwendet.
	 * Letzterer besitzt den Abschnittstyp {@link #TYPE_GROUP} und enthält als Kindabschnitte die der {@link #parseGroupToken(FEMParser, boolean)
	 * Parmetergruppe}. */
	protected Token parseCompositeToken(final FEMParser src, Token target) throws NullPointerException, FEMException {
		while (true) {
			this.parseIgnoreToken(src);
			if (src.symbol() != '(') return target;
			final int pos = src.index();
			src.push('(');
			src.skip();
			final List<Token> toks = this.parseGroupToken(src, false);
			if (src.symbol() == ')') {
				src.push(')');
				src.skip();
			} else {
				toks.add(this.parseErrorToken(src));
			}
			target = src.make(FEMDomain.TYPE_COMPOSITE, target.start(), Arrays.asList(target, src.make(FEMDomain.TYPE_GROUP, pos, toks)));
		}
	}

	/** Diese Methode {@link #parseSpaceToken(FEMParser) überspringt Leerraum} und {@link #parseCommentToken(FEMParser) erfasst Kommentare}. */
	protected void parseIgnoreToken(final FEMParser src) throws NullPointerException {
		for (this.parseSpaceToken(src); this.parseCommentToken(src) != null; this.parseSpaceToken(src)) {}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Parameterposition und gibt ihn zurück. Als Abschnittstyp wird
	 * {@link #TYPE_INDEX} verwendet. Wenn keine Parameterposition gefunden wurden, wird {@code null} geliefert. */
	protected Token parseIndexToken(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		for (int sym = src.symbol(); ('0' <= sym) && (sym <= '9'); sym = src.skip()) {}
		if (pos != src.index()) return src.push(FEMDomain.TYPE_INDEX, pos);
		return null;
	}

	/** Diese Methode {@link Parser#skip() überspringt} alle Symbole eiens {@link Parser#isWhitespace(int) Leerraums}. */
	protected void parseSpaceToken(final FEMParser src) throws NullPointerException {
		for (int sym = src.symbol(); Parser.isWhitespace(sym); sym = src.skip()) {}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines Parameternamen und gibt ihn zurück. Als Abschnittstyp wird
	 * {@link #TYPE_NAME} verwendet. Wenn kein Parameternamen gefunden wurden, wird {@code null} geliefert. */
	protected Token parseNameToken(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		LOOP: for (int sym = src.symbol(); !Parser.isWhitespace(sym); sym = src.skip()) {
			switch (sym) {
				case -1:
				case ';':
				case ':':
				case '/':
				case '(':
				case ')':
				case '{':
				case '}':
				break LOOP;
			}
		}
		return pos != src.index() ? src.push(FEMDomain.TYPE_NAME, pos) : null;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines maskierten Kommentars und gibt ihn Abschnitt zurück. Als
	 * Abschnittstyp wird {@link #TYPE_COMMENT} verwendet. Wenn kein maskierter Kommentar gefunden wurden, wird {@code null} geliefert. */
	protected Token parseCommentToken(final FEMParser src) throws NullPointerException {
		return this.parseSequenceToken(src, FEMDomain.TYPE_COMMENT, '/', '\\', '/');
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette analog zu
	 * {@link Strings#parseSequence(CharSequence, char, char, char)} und gibt diesen nur dann zurück, wenn er am {@code openSymbol} erkannt wurde. Andernfalls
	 * liefert sie {@code null}. Als Abschnittstyp wird {@code type} eingesetzt, wenn die Sequenz nicht vorzeitig endet. Andernfalls wird die Zeichenkette als
	 * {@link #parseErrorToken(FEMParser) Fehlerbereich} erfasst.
	 *
	 * @param type Abschnittstyp.
	 * @param openSym Erstes Symbol der Zeichenkette.
	 * @param maskSym Symbol zur Maskierung von {@code maskSymbol} und {@code closeSymbol}.
	 * @param closeSym Letztes Symbol der Zeichenkette. */
	protected Token parseSequenceToken(final FEMParser src, final int type, final char openSym, final char maskSym, final char closeSym)
		throws NullPointerException {
		int sym = src.symbol();
		if (sym != openSym) return null;
		final int pos = src.index();
		while (true) {
			sym = src.skip();
			if (sym < 0) {
				src.seek(pos);
				return this.parseErrorToken(src);
			}
			if (sym == maskSym) {
				sym = src.skip();
				if (sym < 0) {
					if (maskSym == closeSym) return src.push(type, pos);
					src.seek(pos);
					return this.parseErrorToken(src);
				}
				continue;
			}
			if (sym == closeSym) {
				src.skip();
				return src.push(type, pos);
			}
		}
	}

	/** Diese Methode formatiert die als Zeichenkette gegebene Konstante und gibt diese maskiert zurück. Sie realisiert damit die Umkehroperation zu
	 * {@link #parseIdent(String)}. Das Formatieren erfolgt über {@link Strings#printSequence(CharSequence, char, char, char) Strings.printSequence(string, '<',
	 * '\\', '>')}.
	 *
	 * @param src Zeichenkette.
	 * @return maskierte Zeichenkette. */
	public String printIdent(final String src) throws NullPointerException {
		return Strings.printSequence(src, '<', '\\', '>');
	}

	/** Diese Methode formatiert die als Zeichenkette gegebene Konstante und gibt sie falls nötig {@link #printIdent(String) maskiert} zurück. Sie realisiert
	 * damit die Umkehroperation zu {@link #parseConst(String)}. Die Maskierung ist notwendig, wenn die Zeichenkette ein {@link #parseNameToken(FEMParser) zu
	 * maskierendes Zeichen enthält}. Wenn die Maskierung unnötig ist, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param src Zeichenkette.
	 * @return gegebene bzw. maskierte Zeichenkette. */
	public String printConst(final String src) throws NullPointerException {
		final FEMParser par = new FEMParser(src);
		this.parseNameToken(par);
		return !par.isParsed() ? this.printIdent(src) : src;
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link #printConst(String) Textdarstellung} der gegebenen Konstanten. */
	protected void printConst(final FEMPrinter res, final Object src) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(src);
		res.push(new Object() {

			@Override
			public String toString() {
				return FEMDomain.this.printConst(src.toString());
			}

		});
	}

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(Result) this.toPrinter(src).print()}. */
	public String printResult(final Result src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(src).print();
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltextes.
	 *
	 * @param res Formatierer.
	 * @param src aufbereiteter Quelltext. */
	protected void printResult(final FEMPrinter res, final Result src) throws NullPointerException {
		boolean inline = false;
		for (final Token tok: src) {
			switch (tok.type()) {
				case '{':
					inline = true;
					res.push(tok);
				break;
				case ':':
					inline = false;
				case '/':
					res.push(tok).push(" ");
				break;
				case '(':
				case '[':
					res.push(tok).pushBreakInc();
				break;
				case ']':
				case ')':
					res.pushBreakDec().push(tok);
				break;
				case ';':
					if (inline) {
						res.push(tok).push(" ");
					} else {
						res.push(tok).pushBreakSpc().pushIndent();
					}
				break;
				default:
					res.push(tok);
			}
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(FEMFunction...) this.toPrinter(src).print()}. */
	public String printScript(final FEMFunction... src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(src).print();
	}

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(Iterable) this.toPrinter(src).print()}. */
	public String printScript(final Iterable<? extends FEMFunction> src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(src).print();
	}

	/** Diese Methode erfast die Textdarstellung der gegebenen Funktionen. Die Funktionen werden dazu einzeln {@link #printFunction(FEMPrinter, FEMFunction)
	 * erfasst} und mit Semikolon sowie einem {@link FEMPrinter#pushBreakSpc() bedingten Leerzeichen} getrennt. */
	protected void printGroup(final FEMPrinter res, final Iterable<? extends FEMFunction> src) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends FEMFunction> iter = src.iterator();
		if (!iter.hasNext()) return;
		this.printFunction(res, iter.next());
		if (!iter.hasNext()) return;
		res.pushIndent();
		do {
			this.printFunction(res.push(";").pushBreakSpc(), iter.next());
		} while (iter.hasNext());
	}

	/** Diese Methode erfasst die Textdarstellung des gegebenen Ergebniswerts. Die Formatierung erfolgt dazu für eine bereits ausgewertete {@link FEMFuture} mit
	 * deren {@link FEMFuture#result(boolean) Ergebniswert} über {@link #printValue(FEMPrinter, FEMValue)}. Andernfalls werden deren {@link FEMFuture#target()
	 * Funktion} über {@link #printFunction(FEMPrinter, FEMFunction)} und deren {@link FEMFuture#frame() Stapelrahmen} über
	 * {@link #printFrame(FEMPrinter, Iterable)} erfasst. */
	protected void printFuture(final FEMPrinter res, final FEMFuture src) throws NullPointerException, IllegalArgumentException {
		synchronized (src) {
			if (src.ready()) {
				this.printValue(res, src.result());
			} else {
				this.printFunction(res, src.target());
				this.printFrame(res, src.frame().params());
			}
		}
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung der {@link FutureFunction#target() Zielfunktion} der gegebenen
	 * Ergebnisfunktion. */
	protected void printFuture(final FEMPrinter res, final FutureFunction src) throws NullPointerException, IllegalArgumentException {
		this.printFunction(res, src.target());
	}

	/** Diese Methode {@link #printConst(FEMPrinter, Object) erfasst} die {@link Object#toString() Textdarstellung} des gegebenen Objekts. */
	protected void printNative(final FEMPrinter res, final Object src) throws NullPointerException, IllegalArgumentException {
		this.printConst(res, src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} des gegebenen Leerwerts. */
	protected void printVoid(final FEMPrinter res, final FEMVoid src) throws NullPointerException {
		res.push(src);
	}

	/** Diese Methode {@link #printGroup(FEMPrinter, Iterable) erfasst} die Textdarstellung der gegebenen Wertliste. Deren Werte werden dazu in eckige Klammern
	 * eingeschlossen und auf einer {@link FEMPrinter#pushBreakInc() neuen Ebene} erfasst. */
	protected void printArray(final FEMPrinter res, final FEMArray src) throws NullPointerException, IllegalArgumentException {
		res.push("[").pushBreakInc();
		this.printGroup(res, src);
		res.pushBreakDec().push("]");
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die Textdarstellung der gegebenen Zeichenkette. Diese wird über
	 * {@link Strings#printSequence(CharSequence, char, char, char) Strings.printSequence(string, '"', '\\', '"')} ermittelt. */
	protected void printString(final FEMPrinter res, final FEMString src) throws NullPointerException {
		if (src.find('"', 0) >= 0) {
			res.push(Strings.printSequence(src.toString(), '\'', '\\', '\''));
		} else {
			res.push(Strings.printSequence(src.toString(), '"', '\\', '"'));
		}
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} der gegebenen Bytefolge. */
	protected void printBinary(final FEMPrinter res, final FEMBinary src) throws NullPointerException {
		res.push(src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} der gegebenen Dezimalzahl. */
	protected void printInteger(final FEMPrinter res, final FEMInteger src) throws NullPointerException {
		res.push(src);
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung des gegebenen Funktionszeigers. Deren Funktion wird dabei in
	 * <code>"{:"</code> und <code>"}"</code> eingeschlossen. */
	protected void printHandler(final FEMPrinter res, final FEMHandler src) throws NullPointerException, IllegalArgumentException {
		res.push("{:");
		this.printFunction(res, src.value());
		res.push("}");
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} des gegebenen Wahrheitswerts. */
	protected void printBoolean(final FEMPrinter res, final FEMBoolean src) throws NullPointerException {
		res.push(src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} des gegebenen Dezimalbruchs. */
	protected void printDecimal(final FEMPrinter res, final FEMDecimal src) throws NullPointerException {
		res.push(src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} der gegebenen Zeitspanne. */
	protected void printDuration(final FEMPrinter res, final FEMDuration src) throws NullPointerException, IllegalArgumentException {
		res.push(src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} der gegebenen Zeitangabe. */
	protected void printDatetime(final FEMPrinter res, final FEMDatetime src) throws NullPointerException, IllegalArgumentException {
		res.push(src);
	}

	/** Diese Methode {@link FEMPrinter#push(Object) erfasst} die {@link Object#toString() Textdarstellung} der gegebenen Referenz. */
	protected void printObject(final FEMPrinter res, final FEMObject src) throws NullPointerException, IllegalArgumentException {
		res.push(src);
	}

	/** Diese Methode erfasst die Textdarstellung des gegebenen Werts. Werte bekannter Datentypen werden dazu an spezifische Methoden delegiert. Alle anderen
	 * Werte werden über deren {@link Object#toString() Textdarstellung} als Konstante {@link #printConst(FEMPrinter, Object) erfasst}.
	 *
	 * @see #printFuture(FEMPrinter, FEMFuture)
	 * @see #printNative(FEMPrinter, Object)
	 * @see #printVoid(FEMPrinter, FEMVoid)
	 * @see #printArray(FEMPrinter, FEMArray)
	 * @see #printString(FEMPrinter, FEMString)
	 * @see #printBinary(FEMPrinter, FEMBinary)
	 * @see #printInteger(FEMPrinter, FEMInteger)
	 * @see #printHandler(FEMPrinter, FEMHandler)
	 * @see #printBoolean(FEMPrinter, FEMBoolean)
	 * @see #printDecimal(FEMPrinter, FEMDecimal)
	 * @see #printDuration(FEMPrinter, FEMDuration)
	 * @see #printDatetime(FEMPrinter, FEMDatetime)
	 * @see #printObject(FEMPrinter, FEMObject)
	 * @see #printConst(FEMPrinter, Object) */
	protected void printValue(final FEMPrinter res, final FEMValue src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMFuture) {
			this.printFuture(res, (FEMFuture)src);
		} else {
			final int id = src.type().id();
			if (id == FEMNative.ID) {
				this.printNative(res, src.data());
			} else if (id == FEMVoid.ID) {
				this.printVoid(res, (FEMVoid)src.data());
			} else if (id == FEMArray.ID) {
				this.printArray(res, (FEMArray)src.data());
			} else if (id == FEMHandler.ID) {
				this.printHandler(res, (FEMHandler)src.data());
			} else if (id == FEMBoolean.ID) {
				this.printBoolean(res, (FEMBoolean)src.data());
			} else if (id == FEMString.ID) {
				this.printString(res, (FEMString)src.data());
			} else if (id == FEMBinary.ID) {
				this.printBinary(res, (FEMBinary)src.data());
			} else if (id == FEMInteger.ID) {
				this.printInteger(res, (FEMInteger)src.data());
			} else if (id == FEMDecimal.ID) {
				this.printDecimal(res, (FEMDecimal)src.data());
			} else if (id == FEMDuration.ID) {
				this.printDuration(res, (FEMDuration)src.data());
			} else if (id == FEMDatetime.ID) {
				this.printDatetime(res, (FEMDatetime)src.data());
			} else if (id == FEMObject.ID) {
				this.printObject(res, (FEMObject)src.data());
			} else {
				this.printConst(res, src);
			}
		}
	}

	/** Diese Methode erfasst die Textdarstellung der gegebene Funktion. Bekannte Funktionen werden dazu an spezifische Methoden delegiert. Alle anderen
	 * Funktionen werden über deren {@link Object#toString() Textdarstellung} als Konstante {@link #printConst(FEMPrinter, Object) erfasst}.
	 *
	 * @see #printValue(FEMPrinter, FEMValue)
	 * @see #printProxy(FEMPrinter, FEMProxy)
	 * @see #printClosure(FEMPrinter, FEMClosure)
	 * @see #printBinding(FEMPrinter, FEMBinding)
	 * @see #printComposite(FEMPrinter, FEMComposite)
	 * @see #printTrace(FEMPrinter, TraceFunction)
	 * @see #printFuture(FEMPrinter, FutureFunction) */
	protected void printFunction(final FEMPrinter res, final FEMFunction src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMValue) {
			this.printValue(res, (FEMValue)src);
		} else if (src instanceof FEMProxy) {
			this.printProxy(res, (FEMProxy)src);
		} else if (src instanceof FEMComposite) {
			this.printComposite(res, (FEMComposite)src);
		} else if (src instanceof FEMClosure) {
			this.printClosure(res, (FEMClosure)src);
		} else if (src instanceof FEMBinding) {
			this.printBinding(res, (FEMBinding)src);
		} else if (src instanceof FutureFunction) {
			this.printFuture(res, (FutureFunction)src);
		} else if (src instanceof TraceFunction) {
			this.printTrace(res, (TraceFunction)src);
		} else {
			this.printConst(res, src);
		}
	}

	/** Diese Methode {@link #printConst(FEMPrinter, Object) erfasst} die Textdarstellung des Namens des gegebenen Platzhalters. */
	protected void printProxy(final FEMPrinter res, final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		this.printConst(res, src.name());
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung der {@link FEMClosure#target() Zielfunktion} der gegebenen
	 * Parameterfunktion. Diese wird dabei in <code>"{:"</code> und <code>"}"</code> eingeschlossen. */
	protected void printClosure(final FEMPrinter res, final FEMClosure src) throws NullPointerException, IllegalArgumentException {
		res.push("{:");
		this.printFunction(res, src.target());
		res.push("}");
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung der {@link FEMBinding#target() Zielfunktion} der gegebenen
	 * Funktionsbindung. */
	protected void printBinding(final FEMPrinter res, final FEMBinding src) throws NullPointerException, IllegalArgumentException {
		this.printFunction(res, src.target());
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung der {@link FEMComposite#target() Zielfunktion} der gegebenen
	 * Funktionskomposition. */
	protected void printComposite(final FEMPrinter res, final FEMComposite src) throws NullPointerException, IllegalArgumentException {
		this.printFunction(res, src.target());
		this.printFrame(res, src);
	}

	/** Diese Methode {@link #printGroup(FEMPrinter, Iterable) erfasst} die Textdarstellung der gegebenen Parameterfunktionen. Diese werden dazu in runde Klammern
	 * eingeschlossen und auf einer {@link FEMPrinter#pushBreakInc() neuen Ebene} erfasst. */
	protected void printFrame(final FEMPrinter res, final Iterable<? extends FEMFunction> src) throws NullPointerException, IllegalArgumentException {
		res.push("(").pushBreakInc();
		this.printGroup(res, src);
		res.pushBreakDec().push(")");
	}

	/** Diese Methode {@link #printFunction(FEMPrinter, FEMFunction) erfasst} die Textdarstellung der {@link TraceFunction#target() Zielfunktion} der gegebenen
	 * Funktionsüberwachung. */
	protected void printTrace(final FEMPrinter res, final TraceFunction src) throws NullPointerException, IllegalArgumentException {
		this.printFunction(res, src.target());
	}

	/** Diese Methode erfasst die Textdarstellung des gegebenen aufbereiteten Quelltextes in einem {@link FEMPrinter} und gibt diesen zurück.
	 *
	 * @param src aufbereiteter Quelltext.
	 * @return Textdarstellung.
	 * @throws NullPointerException NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code src} nicht formatiert werden kann. */
	public FEMPrinter toPrinter(final Result src) throws NullPointerException, IllegalArgumentException {
		final FEMPrinter res = new FEMPrinter();
		this.printResult(res, src);
		return res;
	}

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(Iterable) this.toPrinter(Arrays.asList(src))}. */
	public FEMPrinter toPrinter(final FEMFunction... src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(Arrays.asList(src));
	}

	/** Diese Methode erfasst die Textdarstellung der gegebenen Funktionen in einem {@link FEMPrinter} und gibt diesen zurück.
	 *
	 * @param src Funktionen.
	 * @return Textdarstellung.
	 * @throws NullPointerException NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code src} nicht formatiert werden kann. */
	public FEMPrinter toPrinter(final Iterable<? extends FEMFunction> src) throws NullPointerException, IllegalArgumentException {
		final FEMPrinter res = new FEMPrinter();
		this.printGroup(res, src);
		return res;
	}

}