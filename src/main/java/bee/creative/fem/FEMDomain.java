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
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert domänenspezifische Parse-, Formatierungs- und Kompilationsmethoden, welche der Übersetzung von Zeichenketten, aufbereitete
 * Quelltexten und Funktionen ineinander dienen.
 * <p>
 * Die auf {@link FEMParser Zeichenketten} operierenden {@code parse}-Methoden erkennen die darin Bedeutung tragenden {@link Token Abschnitte},
 * {@link FEMParser#push(Token) erfassen} diese und versehen sie mit entsprechender {@link Token#type() Typkennung} und {@link Token#tokens() Struktur}. Die auf
 * diesen typisierten und strukturierten {@link FEMToken Abschnitten} operierenden {@code parse}-Methoden übersetzen diese schließlich in Werte und Funktionen.
 * Die auf {@link FEMFunction Funktionen} operierenden {@code format}-Methoden erzeugen dazu wieder deren Textdarstellung.
 * <p>
 * Nachfahren sollten die Methoden {@link #parseValue(FEMToken, String)} und {@link #parseFunction(FEMToken, String)} überschreiben, um anwendungsspezifisch die
 * Kennung bzw. Textdarstellung von Konstanten un deren Werte bez. Funktionen zu überführen. Wenn an Stelle von {@link FEMClosure bindenden Parameterfuktionen}
 * einfache {@link FEMHandler Funktionszeiger} verwendet werden sollen, muss die Methode {@link #parseClosure(FEMToken)} überschriben werden und das Ergebnis
 * von {@link #parseHandler(FEMToken)} liefern.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain extends BaseObject {

	/** Dieses Feld speichert die normale {@link FEMDomain}. */
	public static final FEMDomain DEFAULT = new FEMDomain();

	/** Diese Methode parst die als maskierte Zeichenkette gegebene Konstente und gibt diese zurück. Sie realisiert damit die Umkehroperation zu
	 * {@link #printIdent(String)}. Das Parsen erfolgt über {@link Strings#parseSequence(CharSequence, char, char, char) Strings.parseSequence(string, '<', '\\',
	 * '>')}.
	 *
	 * @param src maskierte Zeichenkette.
	 * @return Zeichenkette oder {@code null}. */
	public String parseIdent(final String src) throws NullPointerException {
		return Strings.parseSequence(src, '<', '\\', '>');
	}

	/** Diese Methode parst die ggf. als {@link #parseIdent(String) maskierte} Zeichenkette gegebene Kennung und gibt diese zurück. Sie realisiert damit die
	 * Umkehroperation zu {@link #printConst(String)}.
	 *
	 * @param src Zeichenkette.
	 * @return Zeichenkette oder {@code null}. */
	public String parseConst(final String src) throws NullPointerException {
		return src.charAt(0) != '<' ? src : this.parseIdent(src);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Kennung und gibt ihn zurück. Die Erkennung erfolgt über
	 * {@link #parseSequence(FEMParser, char, char, char) this.parseSequence(parser, '<', '\\', '>')}. Als Abschnittstyp wird {@code '<'} verwendet. Wenn keine
	 * maskierte Kennung gefunden wurden, wird {@code null} geliefert. */
	protected Token parseIdent(final FEMParser src) throws NullPointerException {
		return this.parseSequence(src, '<', '\\', '>');
	}

	/** Diese Methode überführt den von {@link #parseIdent(FEMParser)} ermittelten Abschnitt in eine Kennung und gibt diese zurück. Für andere Abschnitte liefert
	 * sie {@code null}. */
	protected String parseIdent(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '<') return null;
		final String res = this.parseConst(tok.toString());
		if (res != null) return res;
		throw this.parseError(src, null);
	}

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link FEMScript aufbereiteten Quelltext} und gibt diesen zurück.
	 *
	 * @see #parseScript(FEMParser)
	 * @param src Zeichenkette, die geparst werden soll.
	 * @return aufbereiteter Quelltext. */
	public FEMScript parseScript(final String src) throws NullPointerException {
		final FEMParser parser = new FEMParser(src);
		final Token root = this.parseScript(parser);
		return FEMScript.from(root, parser.tokens());
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Funktionsliste und gibt deren Elternabschnitt zurück. Die Erkennung
	 * erfolgt gemäß folgender EBNF:<br>
	 * <pre>SCRIPT = {@link #parseGroup(FEMParser) GROUP}</pre> Als Abschnittstyp des Elternabschnitts wird {@code '*'} verwendet. Sein Abschnittswert ist die
	 * {@link Set Menge} der {@link String Namen} aller als {@link #parseValue(FEMParser) Wert} angegebenen {@link FEMParser#proxies() Platzhalter}. Die
	 * {@link Token#tokens() Kindabschnitte} sind die Abschnitte der Funktionen (FUNCTION). */
	protected Token parseScript(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		final List<Token> funs = this.parseGroup(src);
		if (src.symbol() >= 0) {
			funs.add(this.parseError(src));
		}
		return src.make('*', pos, funs).value(new HashSet<>(src.proxies()));
	}

	/** Diese Methode überführt den von {@link #parseScript(FEMParser)} ermittelten Abschnitt in eine Funktionsliste und gibt diese zurück. */
	protected List<FEMFunction> parseScript(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '*') throw this.parseError(src, null);
		try {
			for (final Object name: (Set<?>)tok.value()) {
				src.proxy(name.toString());
			}
		} catch (final Exception cause) {
			throw this.parseError(src, cause);
		}
		return this.parseGroup(src);
	}

	/** Diese Methode ist eine Abkürzung für {@link #parseGroup(FEMScript) this.parseGroup(this.parseScript(source))}. */
	public List<FEMFunction> parseGroup(final String source) throws NullPointerException, IllegalArgumentException {
		return this.parseGroup(this.parseScript(source));
	}

	/** Diese Methode überführt den gegebenen {@link FEMScript Quelltext} in eine {@link List Liste} von {@link FEMFunction Funktionen} und gibt diese zurück.
	 *
	 * @param src Quelltext, der bspw. über {@link #parseScript(String)} erzeugt wurde.
	 * @return Funktionsliste. */
	public List<FEMFunction> parseGroup(final FEMScript src) throws NullPointerException, IllegalArgumentException {
		return this.parseScript(new FEMToken().with(src.root()));
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Funktionsliste und gibt die Abschnitte der Funktionen (FUNCTION)
	 * zurück. Die Erkennung erfolgt gemäß folgender EBNF:<br>
	 * <pre>GROUP = {@link #parseInfo(FEMParser) SC} [ {@link #parseFunction(FEMParser) FUNCTION} { {@link #parseInfo(FEMParser) SC} ";" {@link #parseInfo(FEMParser) SC} {@link #parseFunction(FEMParser) FUNCTION} } {@link #parseInfo(FEMParser) SC} ]</pre> */
	protected List<Token> parseGroup(final FEMParser src) throws NullPointerException {
		final List<Token> res = new ArrayList<>();
		this.parseInfo(src);
		Token fun = this.parseFunction(src);
		if (fun == null) return res;
		res.add(fun);
		while (true) {
			this.parseInfo(src);
			if (src.symbol() != ';') return res;
			src.push(';');
			src.skip();
			this.parseInfo(src);
			fun = this.parseFunction(src);
			if (fun != null) {
				res.add(fun);
			} else {
				res.add(this.parseError(src));
			}
		}
	}

	/** Diese Methode überführt den von {@link #parseGroup(FEMParser)} ermittelten Abschnitt in eine Funktionsliste und gibt diese zurück. */
	protected List<FEMFunction> parseGroup(final FEMToken src) throws NullPointerException, FEMException {
		final List<FEMFunction> res = new ArrayList<>();
		for (final Token tok: src.token()) {
			res.add(this.parseFunction(src.with(tok)));
		}
		return res;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den verbleibenden {@link Token Abschnitt} der Eingabe als Fehler und gibt ihn zurück. Als
	 * Abschnittstyp wird {@code '!'} verwendet. */
	protected Token parseError(final FEMParser src) throws NullPointerException {
		final int pos = src.index(), len = src.length();
		final Token res = src.make('!', pos, len - pos);
		src.seek(len);
		return src.push(res);
	}

	/** Diese Methode liefert die Fehlermeldung zum gegebeen Abschnitt und seine Positionsangaben. */
	protected String parseError(final FEMToken src) throws NullPointerException {
		String str = src.token().toString();
		str = str.length() > 40 ? str.substring(0, 39) + "…" : str;
		return String.format("Fehler an Position %s:%s gefunden: %s", src.rowIndex(), src.colIndex(), str);
	}

	/** Diese Methode liefert die beschriftete {@link FEMException Ausnahme} zum gegebeen Abschnitt. */
	protected FEMException parseError(final FEMToken src, final Throwable cause) throws NullPointerException {
		return FEMException.from(cause).push(this.parseError(src));
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer direkten Konstanten und gibt ihn zurück. Als Abschnittstyp wird
	 * {@code '?'} verwendet. Wenn keine direkte Konstante gefunden wurde, wird {@code null} geliefet. Direkte Konstanten können grundsätzlich an folgenden
	 * Stellen vorkommen: <pre>(CONST; [CONST]; CONST{: CONST}; CONST/.../; CONST)</pre> */
	protected Token parseConst(final FEMParser src) throws NullPointerException {
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
		return pos != src.index() ? src.push('?', pos) : null;
	}

	/** Diese Methode überführt den von {@link #parseConst(FEMParser)} ermittelten Abschnitt in eine Kennung und gibt diese zurück. Für andere Abschnitte liefert
	 * sie {@code null}. */
	protected String parseConst(final FEMToken src) throws NullPointerException {
		final Token tok = src.token();
		if (tok.type() != '?') return null;
		return tok.toString();
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Wertliste und gibt deren Elternabschnitt zurück. Die Erkennung
	 * erfolgt gemäß folgender EBNF:<br>
	 * <pre>ARRAY = "[" [ {@link #parseInfo(FEMParser) SC} {@link #parseValue(FEMParser) VALUE} { {@link #parseInfo(FEMParser) SC} ";" {@link #parseInfo(FEMParser) SC} {@link #parseValue(FEMParser) VALUE} } ] {@link #parseInfo(FEMParser) SC} "]"</pre>
	 * Als Abschnittstyp des Elternabschnitts wird {@code '['} verwendet. Die {@link Token#tokens() Kindabschnitte} sind die der Werte (VALUE). */
	protected Token parseArray(final FEMParser src) throws NullPointerException {
		int sym = src.symbol();
		if (sym != '[') return null;
		final int pos = src.index();
		final List<Token> toks = new ArrayList<>();
		src.push('[');
		src.skip();
		this.parseInfo(src);
		sym = src.symbol();
		if (sym == ']') {
			src.push(']');
			src.skip();
		} else if (sym < 0) {
			toks.add(this.parseError(src));
		} else {
			Token tok = this.parseValue(src);
			if (tok == null) {
				toks.add(this.parseError(src));
			} else {
				toks.add(tok);
				while (true) {
					this.parseInfo(src);
					sym = src.symbol();
					if (sym == ']') {
						src.push(']');
						src.skip();
						break;
					} else if ((sym < 0) || (sym != ';')) {
						toks.add(this.parseError(src));
						break;
					} else {
						src.push(';');
						src.skip();
						this.parseInfo(src);
						tok = this.parseValue(src);
						if (tok == null) {
							toks.add(this.parseError(src));
							break;
						} else {
							toks.add(tok);
						}
					}
				}
			}
		}
		return src.make('[', pos, toks);
	}

	/** Diese Methode überführt den von {@link #parseArray(FEMParser)} ermittelten Abschnitt in eine Wertliste und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected FEMValue parseArray(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '[') return null;
		final List<FEMValue> res = new ArrayList<>();
		for (final Token item: tok) {
			res.add(this.parseValue(src.with(item)));
		}
		return FEMArray.from(res);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette und gibt ihn zurück. Die Erkennung erfolgt
	 * über {@link #parseSequence(FEMParser, char, char, char) this.parseSequence(parser, '\"', '\\', '\"')}. Als Abschnittstyp wird {@code '\"'} verwendet. Wenn
	 * keine maskierte Zeichenkette gefunden wurden, wird {@code null} geliefert. */
	protected Token parseString1(final FEMParser src) throws NullPointerException {
		return this.parseSequence(src, '\"', '\\', '\"');
	}

	/** Diese Methode überführt den von {@link #parseString1(FEMParser)} ermittelten Abschnitt in eine Zeichenkette und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected FEMValue parseString1(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '\"') return null;
		final String res = Strings.parseSequence(tok.toString(), '\"', '\\', '\"');
		if (res != null) return FEMString.from(res);
		throw this.parseError(src, null);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette und gibt ihn zurück. Die Erkennung erfolgt
	 * über {@link #parseSequence(FEMParser, char, char, char) this.parseSequence(parser, '\'', '\\', '\'')}. Als Abschnittstyp wird {@code '\''} verwendet. Wenn
	 * keine maskierte Zeichenkette gefunden wurden, wird {@code null} geliefert. */
	protected Token parseString2(final FEMParser src) throws NullPointerException {
		return this.parseSequence(src, '\'', '\\', '\'');
	}

	/** Diese Methode überführt den von {@link #parseString2(FEMParser)} ermittelten Abschnitt in eine Zeichenkette und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected FEMValue parseString2(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '\'') return null;
		final String res = Strings.parseSequence(tok.toString(), '\'', '\\', '\'');
		if (res != null) return FEMString.from(res);
		throw this.parseError(src, null);
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} einer Parameterfunktion und gibt deren Elternabschnitt zurück. Die
	 * Erkennung erfolgt gemäß folgender EBNF:<br>
	 * <pre>PARAM = "$" [ {@link #parseIdent(FEMParser) IDENT} | {@link #parseIndex(FEMParser) INDEX} | {@link #parseName(FEMParser) NAME} ]</pre><br>
	 * Als Abschnittstyp des Elternabschnitts wird {@code '$'} verwendet. Sein Abschnittswert ist die {@link FEMParam#index() Position} des referenzierten
	 * Parameters bzw. {@code null}, wenn kein {@link Token#tokens() Kindabschnitt} vorliegen. Als Kindabschnitt wird der Abschnitt der Kennung (IDENT), der
	 * Position (INDEX) bzw. des Namen (NAME) verwendet. Wenn Kennung, Position bzw. Name ungültig sind, wird als deren Abschnittstyp {@code '!'} verwendet. */
	protected Token parseParam(final FEMParser src) throws NullPointerException {
		if (src.symbol() != '$') return null;
		final Token res = src.push('$');
		src.skip();
		final Token ident = this.parseIdent(src), index, name, ref;
		if (ident == null) {
			index = this.parseIndex(src);
			name = index == null ? this.parseName(src) : null;
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
		if (val < 0) return ref.type('!');
		return src.make('$', res.start(), Arrays.asList(ref)).value(val);
	}

	/** Diese Methode überführt den von {@link #parseParam(FEMParser)} ermittelten Abschnitt in eine Parameterwerte liefernde Funktion und gibt diese zurück. Für
	 * andere Abschnitte liefert sie {@code null}. */
	protected FEMFunction parseParam(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '$') return null;
		if (tok.size() == 0) return FEMFrame.FUNCTION;
		try {
			return FEMParam.from((Integer)tok.value());
		} catch (final Exception cause) {
			throw this.parseError(src, cause);
		}
	}

	/** Diese Methode überführt den von {@link #parseHandler(FEMParser)} ermittelten Abschnitt in eine Parameterfunktion und gibt diese zurück. Für andere
	 * Abschnitte liefert sie {@code null}. */
	protected FEMFunction parseClosure(final FEMToken src) {
		final FEMValue res = this.parseHandler(src);
		if (res == null) return null;
		return FEMClosure.from(res.toFunction());
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} die {@link Token Abschnitte} eines Funktionszeigers und gibt dessen Elternabschnitt zurück. Die
	 * Erkennung erfolgt gemäß folgender EBNF:<br>
	 * <pre>HANDLER = "{" [ {@link #parseInfo(FEMParser) SC} ( {@link #parseName(FEMParser) NAME} | {@link #parseIdent(FEMParser) IDENT} ) { {@link #parseInfo(FEMParser) SC} ";" {@link #parseInfo(FEMParser) SC} ( {@link #parseName(FEMParser) NAME} | {@link #parseIdent(FEMParser) IDENT} ) } ] {@link #parseInfo(FEMParser) SC} ":" {@link #parseInfo(FEMParser) SC} {@link #parseFunction(FEMParser) FUNCTION} {@link #parseInfo(FEMParser) SC} "}"</pre>
	 * Als Abschnittstyp des Elternabschnitts wird <code>'{'</code> verwendet. Sein Abschnittswert ist die {@link List Liste} des Parameternamen. Als
	 * Kindabschnitte werden die Abschnitte der Namen (NAME), Kennungen (IDENT) sowei der der Funktion (FUNCTION) verwendet. Wenn Namen bzw. Kennungen ungültig
	 * sind, wird als deren Abschnittstyp {@code '!'} verwendet. */
	protected Token parseHandler(final FEMParser src) throws NullPointerException {
		int sym = src.symbol();
		if (sym != '{') return null;
		final int pos = src.index();
		final List<Token> toks = new ArrayList<>();
		src.push('{');
		src.skip();
		this.parseInfo(src);
		sym = src.symbol();
		if (sym == ':') {
			src.push(':');
			src.skip();
		} else if (sym < 0) {
			toks.add(this.parseError(src));
		} else {
			Token nam = this.parseIdent(src);
			if (nam == null) {
				nam = this.parseName(src);
			}
			if (nam == null) {
				toks.add(this.parseError(src));
			} else {
				toks.add(nam);
				while (true) {
					this.parseInfo(src);
					sym = src.symbol();
					if (sym == ':') {
						src.push(':');
						src.skip();
						break;
					} else if ((sym < 0) || (sym != ';')) {
						toks.add(this.parseError(src));
						break;
					} else {
						src.push(';');
						src.skip();
						this.parseInfo(src);
						nam = this.parseIdent(src);
						if (nam == null) {
							nam = this.parseName(src);
						}
						if (nam == null) {
							toks.add(this.parseError(src));
							break;
						} else {
							toks.add(nam);
						}
					}
				}
			}
		}
		final List<String> names = new ArrayList<>();
		for (final Token tok: toks) {
			final String nam = tok.toString();
			names.add(nam);
			if (src.params().contains(nam)) {
				tok.type('!');
			}
		}
		src.params().addAll(0, names);
		this.parseInfo(src);
		final Token tok = this.parseFunction(src);
		if (tok == null) {
			toks.add(this.parseError(src));
		} else {
			toks.add(tok);
			this.parseInfo(src);
			sym = src.symbol();
			if ((sym < 0) || (sym != '}')) {
				toks.add(this.parseError(src));
			} else {
				src.push('}');
				src.skip();
			}
		}
		src.params().subList(0, names.size()).clear();
		return src.make('{', pos, toks).value(names);
	}

	/** Diese Methode überführt den von {@link #parseHandler(FEMParser)} ermittelten Abschnitt in einen Funktionszeiger und gibt diese zurück. Für andere
	 * Abschnitte liefert sie {@code null}. */
	protected FEMValue parseHandler(FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() == '{') {
			final int idx = tok.size() - 1;
			if (idx < 0) throw this.parseError(src, null);
			final FEMFunction fun = this.parseFunction(src.with(tok.get(idx)));
			return FEMHandler.from(fun);
		} else if (tok.type() == '=') {
			if (tok.size() != 2) throw this.parseError(src, null);
			src = src.with(tok.get(0));
			final String str = this.parseIdent(src);
			final String ref = str != null ? str : this.parseConst(src);
			if (ref == null) throw this.parseError(src, null);
			src = src.with(tok.get(1));
			final FEMValue han = this.parseHandler(src);
			if (han == null) throw this.parseError(src, null);
			final FEMProxy res = src.proxy(ref);
			res.set(han.toFunction());
			return FEMHandler.from(res);
		}
		return null;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines Werts gibt ihn zurück. Die Erkennung erfolgt gemäß folgender
	 * EBNF:<br>
	 * <pre>VALUE = {@link #parseArray(FEMParser) ARRAY} | {@link #parseString1(FEMParser) STRING1} | {@link #parseString2(FEMParser) STRING2} | {@link #parseHandler(FEMParser) HANDLER} | ({@link #parseIdent(FEMParser) IDENT} | {@link #parseConst(FEMParser) CONST}) {@link #parseInfo(FEMParser) SC} [ {@link #parseHandler(FEMParser) HANDLER} ]</pre>
	 * Wenn eine Kennung (IDENT) oder eine Konstante (CONST) von einem Funktionszeiger (HANDLER) gefolgt wird, führt dies zur Erkennung einer {@link FEMProxy
	 * Platzhalterfunktion}, deren Elternabschnitt geliefert wird. Als deren Abschnittstyp wird dann {@code '='} verwendet. Die {@link Token#tokens()
	 * Kindabschnitte} sind die von Kennung bzw. Konstate und Funktionszeiger. */
	protected Token parseValue(final FEMParser src) throws NullPointerException {
		Token res = this.parseArray(src);
		if (res != null) return res;
		res = this.parseString1(src);
		if (res != null) return res;
		res = this.parseString2(src);
		if (res != null) return res;
		res = this.parseHandler(src);
		if (res != null) return res;
		res = this.parseIdent(src);
		if (res == null) {
			res = this.parseConst(src);
			if (res == null) return null;
		}
		this.parseInfo(src);
		final Token han = this.parseHandler(src);
		if (han == null) return res;
		if (!src.proxies().add(res.toString())) {
			res.type('!');
		}
		return src.make('=', res.start(), Arrays.asList(res, han));
	}

	/** Diese Methode überführt den von {@link #parseValue(FEMParser)} ermittelten Abschnitt in einen Wert und gibt diesen zurück. */
	protected FEMValue parseValue(final FEMToken src) throws NullPointerException, FEMException {
		FEMValue res = this.parseArray(src);
		if (res != null) return res;
		res = this.parseString1(src);
		if (res != null) return res;
		res = this.parseString2(src);
		if (res != null) return res;
		res = this.parseHandler(src);
		if (res != null) return res;
		final String str = this.parseIdent(src);
		final String ref = str != null ? str : this.parseConst(src);
		if (ref == null) throw this.parseError(src, null);
		try {
			res = this.parseValue(src, ref);
			if (res != null) return res;
		} catch (final Exception cause) {
			throw this.parseError(src, cause);
		}
		throw this.parseError(src, null);
	}

	/** Diese Methode überführt die gegebene Kennung bzw. Textdarstellung einer Konstanten in deren Wert und gibt diesen zurück. Wenn dies nicht möglich ist, wird
	 * {@code null} geliefert.
	 *
	 * @param ref Kennung bzw. Textdarstellung einer Konstanten.
	 * @return Wert der Konstanten oder {@code null}. */
	protected FEMValue parseValue(final FEMToken src, final String ref) throws NullPointerException {
		if (ref.equals("void")) return FEMVoid.INSTANCE;
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

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Funktion und gibt ihn zurück. Die Erkennung erfolgt gemäß folgender
	 * EBNF:<br>
	 * <pre>FUNCTION = ( {@link #parseParam(FEMParser) PARAM} | {@link #parseValue(FEMParser) VALUE} ) { {@link #parseInfo(FEMParser) SC} "(" {@link #parseGroup(FEMParser) GROUP} ")" }</pre>
	 * Wenn ein Parameter (PARAM) bzw. dem Wert (VALUE) von einer in runden Klammern eingeschlossenen Funktionsliste (GROUP) gefolgt wird, führt dies zur
	 * Erkennung einer {@link FEMComposite Funktionsverkettung}, deren Elternabschnitt geliefert wird. Als Abschnittstyp wird dann {@code '.'} verwendet. Die
	 * {@link Token#tokens() Kindabschnitte} sind die der aufgerufenen Funktion und die der Parameterfunktionsliste, letztere mit dem Abschnittstyp
	 * {@code '('}. */
	protected Token parseFunction(final FEMParser src) throws NullPointerException {
		Token res = this.parseParam(src);
		if (res == null) {
			res = this.parseValue(src);
			if (res == null) return null;
		}
		while (true) {
			this.parseInfo(src);
			if (src.symbol() != '(') return res;
			final int pos = src.index();
			src.push('(');
			src.skip();
			final List<Token> toks = this.parseGroup(src);
			if (src.symbol() == ')') {
				src.push(')');
				src.skip();
			} else {
				toks.add(this.parseError(src));
			}
			res = src.make('.', res.start(), Arrays.asList(res, src.make('(', pos, toks)));
		}
	}

	/** Diese Methode überführt den von {@link #parseFunction(FEMParser)} ermittelten Abschnitt in eine Funktion und gibt diese zurück. */
	protected FEMFunction parseFunction(FEMToken src) throws NullPointerException, FEMException {
		FEMFunction res;
		Token tok = src.token();
		if (tok.type() == '.') {
			if (tok.size() != 2) throw this.parseError(src, null);
			res = this.parseFunction(src.with(tok.get(0)));
			tok = tok.get(1);
			src = src.with(tok);
			if (tok.type() != '(') throw this.parseError(src, null);
			final List<FEMFunction> pars = this.parseGroup(src);
			return res.compose(pars);
		}
		res = this.parseParam(src);
		if (res != null) return res;
		res = this.parseArray(src);
		if (res != null) return res;
		res = this.parseString1(src);
		if (res != null) return res;
		res = this.parseString2(src);
		if (res != null) return res;
		res = this.parseClosure(src);
		if (res != null) return res;
		final String str = this.parseIdent(src);
		final String ref = str != null ? str : this.parseConst(src);
		if (ref == null) throw this.parseError(src, null);
		try {
			res = this.parseFunction(src, ref);
			if (res != null) return res;
			res = this.parseValue(src, ref);
			if (res != null) return res;
		} catch (final Exception cause) {
			throw this.parseError(src, cause);
		}
		throw this.parseError(src, null);
	}

	/** Diese Methode überführt die gegebene Kennung bzw. Textdarstellung einer Konstanten in deren Funktion und gibt diese zurück. Wenn dies nicht möglich ist,
	 * wird {@code null} geliefert.
	 *
	 * @param ref Kennung bzw. Textdarstellung einer Konstanten.
	 * @return Funktion der Konstanten oder {@code null}. */
	protected FEMFunction parseFunction(final FEMToken src, final String ref) {
		final FEMFunction res = src.proxies().get(ref);
		if (res != null) return res;
		return FEMUtil.get(ref);
	}

	/** Diese Methode {@link Parser#skip() überspringt} Leerraum und erfasst Kommentare gemäß folgender EBNF:
	 * <pre>SC = { {@link #parseSpace(FEMParser) SPACE} | {@link #parseComment(FEMParser) COMMENT} }</pre> */
	protected void parseInfo(final FEMParser src) throws NullPointerException {
		for (this.parseSpace(src); this.parseComment(src) != null; this.parseSpace(src)) {}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer Parameterposition und gibt ihn zurück. Als Abschnittstyp wird
	 * {@code '#'} verwendet. Wenn keine Parameterposition gefunden wurden, wird {@code null} geliefert. */
	protected Token parseIndex(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		for (int sym = src.symbol(); ('0' <= sym) && (sym <= '9'); sym = src.skip()) {}
		if (pos != src.index()) return src.push('#', pos);
		return null;
	}

	/** Diese Methode {@link Parser#skip() überspringt} alle Symbole eiens {@link Parser#isWhitespace(int) Leerraums}. */
	protected void parseSpace(final FEMParser src) throws NullPointerException {
		for (int sym = src.symbol(); Parser.isWhitespace(sym); sym = src.skip()) {}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines Parameternamen und gibt ihn zurück. Als Abschnittstyp wird
	 * {@code '~'} verwendet. Wenn kein Parameternamen gefunden wurden, wird {@code null} geliefert. Parameternamen können grundsätzlich an folgenden Stellen
	 * vorkommen: <pre>{NAME; NAME: $NAME({: $NAME}; $NAME /.../ $NAME)}; NAME</pre> */
	protected Token parseName(final FEMParser src) throws NullPointerException {
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
		return pos != src.index() ? src.push('~', pos) : null;
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette analog zu
	 * {@link Strings#parseSequence(CharSequence, char, char, char)} und gibt diesen nur dann zurück, wenn er am {@code openSymbol} erkannt wurde. Andernfalls
	 * liefert sie {@code null}. Als Abschnittstyp wird {@code openSymbol} eingesetzt, wenn die Sequenz nicht vorzeitig endet. Andernfalls wird die Zeichenkette
	 * als {@link #parseError(FEMParser) Fehlerbereich} mit dem Abschnittstyp '!' erfasst.
	 *
	 * @param openSym Erstes Symbol der Zeichenkette.
	 * @param maskSym Symbol zur Maskierung von {@code maskSymbol} und {@code closeSymbol}.
	 * @param closeSym Letztes Symbol der Zeichenkette. */
	protected Token parseSequence(final FEMParser src, final char openSym, final char maskSym, final char closeSym) throws NullPointerException {
		int sym = src.symbol();
		if (sym != openSym) return null;
		final int pos = src.index();
		while (true) {
			sym = src.skip();
			if (sym < 0) return src.push('!', pos);
			if (sym == maskSym) {
				sym = src.skip();
				if (sym < 0) return maskSym == closeSym ? src.push(openSym, pos) : src.push('!', pos);
				continue;
			}
			if (sym == closeSym) {
				src.skip();
				return src.push(openSym, pos);
			}
		}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines maskierten Kommentars und gibt ihn Abschnitt zurück. Die Erkennung
	 * erfolgt über {@link #parseSequence(FEMParser, char, char, char) this.parseSequence(parser, '/', '\\', '/')}. Als Abschnittstyp wird {@code '/'} verwendet.
	 * Wenn kein maskierter Kommentar gefunden wurden, wird {@code null} geliefert. */
	protected Token parseComment(final FEMParser src) throws NullPointerException {
		return this.parseSequence(src, '/', '\\', '/');
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
	 * damit die Umkehroperation zu {@link #parseConst(String)}. Die Maskierung ist notwendig, wenn die Zeichenkette ein {@link #parseName(FEMParser) zu
	 * maskierendes Zeichen enthält}. Wenn die Maskierung unnötig ist, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param src Zeichenkette.
	 * @return gegebene bzw. maskierte Zeichenkette. */
	public String printConst(final String src) throws NullPointerException {
		final FEMParser par = new FEMParser(src);
		this.parseName(par);
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

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(FEMScript) this.toPrinter(src).print()}. */
	public String printScript(final FEMScript src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(src).print();
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltextes.
	 *
	 * @param res Formatierer.
	 * @param src aufbereiteter Quelltext. */
	protected void printScript(final FEMPrinter res, final FEMScript src) throws NullPointerException {
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
	public String printGroup(final FEMFunction... src) throws NullPointerException, IllegalArgumentException {
		return this.toPrinter(src).print();
	}

	/** Diese Methode ist eine Abkürzung für {@link #toPrinter(Iterable) this.toPrinter(src).print()}. */
	public String printGroup(final Iterable<? extends FEMFunction> src) throws NullPointerException, IllegalArgumentException {
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
			res.push(";").pushBreakSpc();
			this.printFunction(res, iter.next());
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
		res.push(Strings.printSequence(src.toString(), '"', '\\', '"'));
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
			return;
		}
		switch (src.type().id()) {
			case FEMNative.ID:
				this.printNative(res, src.data());
				return;
			case FEMVoid.ID:
				this.printVoid(res, (FEMVoid)src.data());
				return;
			case FEMArray.ID:
				this.printArray(res, (FEMArray)src.data());
				return;
			case FEMHandler.ID:
				this.printHandler(res, (FEMHandler)src.data());
				return;
			case FEMBoolean.ID:
				this.printBoolean(res, (FEMBoolean)src.data());
				return;
			case FEMString.ID:
				this.printString(res, (FEMString)src.data());
				return;
			case FEMBinary.ID:
				this.printBinary(res, (FEMBinary)src.data());
				return;
			case FEMInteger.ID:
				this.printInteger(res, (FEMInteger)src.data());
				return;
			case FEMDecimal.ID:
				this.printDecimal(res, (FEMDecimal)src.data());
				return;
			case FEMDuration.ID:
				this.printDuration(res, (FEMDuration)src.data());
				return;
			case FEMDatetime.ID:
				this.printDatetime(res, (FEMDatetime)src.data());
				return;
			case FEMObject.ID:
				this.printObject(res, (FEMObject)src.data());
				return;
		}
		this.printConst(res, src);
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
	public FEMPrinter toPrinter(final FEMScript src) throws NullPointerException, IllegalArgumentException {
		final FEMPrinter res = new FEMPrinter();
		this.printScript(res, src);
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