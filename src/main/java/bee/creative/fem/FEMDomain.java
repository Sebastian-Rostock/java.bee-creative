package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.bind.Setter;
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

	/** Diese Methode parst die ggf. als maskierte Zeichenkette gegebene Kennung und gibt diese ohne Maskierung zurück. Sie realisiert damit die Umkehroperation
	 * zu {@link #formatIdent(String)}. Die Maskierung liegt vor, wenn die Zeichenkette mit {@code '<'} beginnt. Wenn die Maskierung ungültig ist, wird
	 * {@code null} geliefert.
	 *
	 * @param src Zeichenkette.
	 * @return Kennung oder {@code null}.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public String parseIdent(final String src) throws NullPointerException {
		return src.charAt(0) != '<' ? src : Strings.parseSequence(src, '<', '\\', '>');
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
		final String res = this.parseIdent(tok.toString());
		if (res != null) return res;
		throw this.parseError(src, null);
	}

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link FEMScript aufbereiteten Quelltext} und gibt diesen zurück.
	 *
	 * @see #parseScript(FEMParser)
	 * @param src Zeichenkette, die geparst werden soll.
	 * @return aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
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

	/** Diese Methode ist eine Abkürzung für {@link #parseModule(FEMScript) this.parseModule(this.parseScript(source))}. */
	public List<FEMFunction> parseModule(final String source) throws NullPointerException, IllegalArgumentException {
		return this.parseModule(this.parseScript(source));
	}

	public List<FEMFunction> parseModule(final FEMScript source) throws NullPointerException, IllegalArgumentException {
		return this.parseModule(new FEMToken().with(source.root()));
	}

	/** Diese Methode überführt den von {@link #parseScript(FEMParser)} ermittelten Abschnitt in eine Funktionsliste und gibt diese zurück. */
	protected List<FEMFunction> parseModule(final FEMToken src) throws NullPointerException, FEMException {
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

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} einer maskierten Zeichenkette und gibt ihn zurück. Die Erkennung erfolgt
	 * über {@link #parseSequence(FEMParser, char, char, char) this.parseSequence(parser, '\"', '\\', '\"')}. Als Abschnittstyp wird {@code '\"'} verwendet. Wenn
	 * keine maskierte Zeichenkette gefunden wurden, wird {@code null} geliefert. */
	protected Token parseString1(final FEMParser src) throws NullPointerException {
		return this.parseSequence(src, '\"', '\\', '\"');
	}

	/** Diese Methode überführt den von {@link #parseString1(FEMParser)} ermittelten Abschnitt in eine Zeichenkette und gibt diese zurück. Für andere Abschnitte
	 * liefert sie {@code null}. */
	protected FEMString parseString1(final FEMToken src) throws NullPointerException, FEMException {
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
	protected FEMString parseString2(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '\'') return null;
		final String res = Strings.parseSequence(tok.toString(), '\'', '\\', '\'');
		if (res != null) return FEMString.from(res);
		throw this.parseError(src, null);
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
	protected FEMArray parseArray(final FEMToken src) throws NullPointerException, FEMException {
		final Token tok = src.token();
		if (tok.type() != '[') return null;
		final List<FEMValue> res = new ArrayList<>();
		for (final Token item: tok) {
			res.add(this.parseValue(src.with(item)));
		}
		return FEMArray.from(res);
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
	protected FEMClosure parseClosure(final FEMToken src) {
		final FEMHandler res = this.parseHandler(src);
		if (res == null) return null;
		return FEMClosure.from(res.value());
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
	protected FEMHandler parseHandler(FEMToken src) throws NullPointerException, FEMException {
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
			final FEMHandler han = this.parseHandler(src);
			if (han == null) throw this.parseError(src, null);
			final FEMProxy res = src.proxy(ref);
			res.set(han.toFunction());
			return FEMHandler.from(res);
		}
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

	/** Diese Methode {@link Parser#skip() überspringt} alle Symbole eiens Leerraums. Dazu zählen alle Symbole kleiner oder gleich dem Leerzeichen. */
	protected void parseSpace(final FEMParser src) throws NullPointerException {
		for (int sym = src.symbol(); (sym >= 0) && (sym <= ' '); sym = src.skip()) {}
	}

	/** Diese Methode {@link FEMParser#push(Token) erfasst} den {@link Token Abschnitt} eines Parameternamen und gibt ihn zurück. Als Abschnittstyp wird
	 * {@code '~'} verwendet. Wenn kein Parameternamen gefunden wurden, wird {@code null} geliefert. Parameternamen können grundsätzlich an folgenden Stellen
	 * vorkommen: <pre>{NAME; NAME: $NAME({: $NAME}; $NAME /.../ $NAME)}</pre> */
	protected Token parseName(final FEMParser src) throws NullPointerException {
		final int pos = src.index();
		LOOP: for (int sym = src.symbol(); sym > ' '; sym = src.skip()) {
			switch (sym) {
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

	/** Diese Methode ist eine Abkürzung für {@link #formatIdent(String, boolean) this.formatConst(name, false)}.
	 *
	 * @param string Zeichenkette.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatIdent(final String string) throws NullPointerException {
		return this.formatIdent(string, false);
	}

	/** Diese Methode formatiert die als Zeichenkette gegebene Konstante und gibt sie falls nötig mit Maskierung als formatierte Zeichenkette zurück. Die
	 * Maskierung ist notwendig, wenn {@code forceMask} dies anzeigt oder wenn die Zeichenkette ein {@link #parseName(FEMParser) zu maskierendes Zeichen enthält}.
	 * Die Maskierung erfolgt über {@link Strings#formatSequence(CharSequence, char, char, char) Strings.formatSequence(string, '<', '<', '>')}. Wenn die
	 * Maskierung unnötig ist, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param forceMask {@code true}, wenn die Maskierung notwendig ist.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatIdent(final String string, final boolean forceMask) throws NullPointerException {
		if (forceMask) return Strings.formatSequence(string, '<', '\\', '>');
		final FEMParser parser = new FEMParser(string);
		this.parseName(parser);
		return !parser.isParsed() ? this.formatIdent(string, true) : Objects.notNull(string);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.formatScript(source, null)}.
	 *
	 * @see #formatScript(FEMScript, String)
	 * @param source aufbereiteter Quelltext.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatScript(final FEMScript source) throws NullPointerException, IllegalArgumentException {
		return this.formatScript(source, null);
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen aufbereiteten Quelltextes zurück.
	 *
	 * @see #formatScript(FEMFormatter, FEMScript)
	 * @param source aufbereiteter Quelltext.
	 * @param indent Zeichenkette zur Einrückung einer Hierarchieebene oder {@code null}.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatScript(final FEMScript source, final String indent) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter().useIndent(indent);
		this.formatScript(target, source);
		return target.format();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.formatFrame(source, null)}.
	 *
	 * @see #formatFrame(FEMFrame, String)
	 * @param source Stapelrahmen.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFrame(final FEMFrame source) throws NullPointerException, IllegalArgumentException {
		return this.formatFrame(source, null);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.formatFrame(source.params(), indent)}.
	 *
	 * @see #formatFrame(FEMArray, String)
	 * @param source Stapelrahmen.
	 * @param indent Zeichenkette zur Einrückung einer Hierarchieebene oder {@code null}.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFrame(final FEMFrame source, final String indent) throws NullPointerException, IllegalArgumentException {
		return this.formatFrame(source.params(), indent);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.formatFrame(source, null)}.
	 *
	 * @see #formatFrame(FEMArray, String)
	 * @param source Stapelrahmen.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFrame(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		return this.formatFrame(source, null);
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen Stapelrahmen zurück.
	 *
	 * @see #formatFrame(FEMFormatter, Iterable)
	 * @param source Stapelrahmen.
	 * @param indent Zeichenkette zur Einrückung einer Hierarchieebene oder {@code null}.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFrame(final FEMArray source, final String indent) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter().useIndent(indent);
		this.formatFrame(target, source);
		return target.format();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.formatFunction(source, null)}.
	 *
	 * @see #formatFunction(FEMFunction, String)
	 * @param source Stapelrahmen.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		return this.formatFunction(source, null);
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Funktion zurück.
	 *
	 * @see #formatFunction(FEMFormatter, FEMFunction)
	 * @param source Funktion.
	 * @param indent Zeichenkette zur Einrückung einer Hierarchieebene oder {@code null}.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFunction(final FEMFunction source, final String indent) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter().useIndent(indent);
		this.formatFunction(target, source);
		return target.format();
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltextes.
	 *
	 * @param target Formatierer.
	 * @param script aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code script} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code script} nicht formatiert werden kann. */
	protected void formatScript(final FEMFormatter target, final FEMScript script) throws NullPointerException, IllegalArgumentException {
		Iterator<Token> source = script.iterator();
		while (true) {
			this.formatScript(target, source, false);
			if (!source.hasNext()) return;
			target.putToken(source.next()).putBreakSpace();
		}
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung der im aufbereiteten Quelltext gegebenen Sequenz von Namen, Werten und Funktionen.
	 *
	 * @param target Formatierer.
	 * @param source Parser zum aufbereiteten Quelltext.
	 * @param simpleSpace {@code true}, wenn hinter Kommentaren und Semikola ein einfaches Leerzeichen statt eines bedingten Umbruchs eingefügt werden soll.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatScript(final FEMFormatter target, final Iterator<Token> source, final boolean simpleSpace)
		throws NullPointerException, IllegalArgumentException {
		boolean indent = false;
		while (source.hasNext()) {
			Token tok = source.next();
			final String string = tok.toString();
			final int symbol = tok.type();
			switch (symbol) {
				case '/':
					target.putToken(string).putToken(" ");
				break;
				case '(':
				case '[':
					target.putToken(string).putBreakInc();
					this.formatScript(target, source, false);
				break;
				case '{':
					target.putToken(string);
					this.formatScript(target, source, true);
					target.putToken(" ");
					this.formatScript(target, source, false);
				break;
				case ']':
				case ')':
					target.putBreakDec().putToken(string);
				case -1:
					if (!indent) return;
					target.putIndent();
					return;
				case ':':
				case '}':
					target.putToken(string);
					return;
				case ';':
					indent = true;
					target.putToken(string);
					if (simpleSpace) {
						target.putToken(" ");
					} else {
						target.putBreakSpace();
					}
				break;
				default:
					target.putToken(string);
				break;
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der Konstanten mit der gegebenen Bezeichnung. Die Formatierung der Bezeichnung erfolgt dazu über
	 * {@link #formatIdent(String)}.
	 *
	 * @param target Formatierer.
	 * @param source Bezeichnung.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatConst(final FEMFormatter target, final String source) throws NullPointerException {
		target.putToken(this.formatIdent(source));
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der Liste der gegebenen Elemente. Die Elemente werden über den gegebenen {@link Setter}
	 * {@link Setter#set(Object, Object) formatiert} und mit {@code commaSymbol} separiert sowie in {@code openSymbol} und {@code closeSymbol} eingeschlossen. Bei
	 * einer licht leeren Liste {@link FEMFormatter#putBreakInc() beginnt} nach {@code openSymbol} eine neue Hierarchieebene, die vor {@code closeSymbol}
	 * {@link FEMFormatter#putBreakDec() endet}. Nach jedem {@code commaSymbol} wird ein {@link FEMFormatter#putBreakSpace() bedingtes Leerzeichen} eingefügt. Die
	 * aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn die Liste mehr als ein Element enthält.
	 *
	 * @see FEMFormatter#putBreakInc()
	 * @see FEMFormatter#putBreakDec()
	 * @see FEMFormatter#putBreakSpace()
	 * @see FEMFormatter#putIndent()
	 * @param <GItem> Typ der Elemente.
	 * @param target Formatierer.
	 * @param source Elementliste.
	 * @param openSymbol {@code null} oder Symbol, das vor der Liste angefügt wird.
	 * @param commaSymbol {@code null} oder Symbol, das zwischen die Elemente eingefügt wird.
	 * @param closeSymbol {@code null} oder Symbol, das nach der Liste angefügt wird.
	 * @param itemFormatter {@link Setter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
	protected <GItem> void formatItems(final FEMFormatter target, final Iterable<? extends GItem> source, final Object openSymbol, final Object commaSymbol,
		final Object closeSymbol, final Setter<? super FEMFormatter, ? super GItem> itemFormatter) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends GItem> iterator = source.iterator();
		if (iterator.hasNext()) {
			GItem value = iterator.next();
			if (iterator.hasNext()) {
				target.putIndent().putToken(openSymbol).putBreakInc();
				itemFormatter.set(target, value);
				do {
					value = iterator.next();
					target.putToken(commaSymbol).putBreakSpace();
					itemFormatter.set(target, value);
				} while (iterator.hasNext());
				target.putBreakDec().putToken(closeSymbol);
			} else {
				target.putToken(openSymbol).putBreakInc();
				itemFormatter.set(target, value);
				target.putBreakDec().putToken(closeSymbol);
			}
		} else {
			target.putToken(openSymbol).putToken(closeSymbol);
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Parameterfunktionsliste. Hierbei werden die
	 * {@link #formatFunction(FEMFormatter, FEMFunction) formatierten} Parameterfunktionen mit {@code ";"} separiert sowie in {@code "("} und {@code ")"}
	 * eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die Funktionsliste mehr
	 * als ein Element enthält.
	 *
	 * @see #formatItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Parameterfunktionen.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatParams(final FEMFormatter target, final Iterable<? extends FEMFunction> source) throws NullPointerException, IllegalArgumentException {
		this.formatItems(target, source, "(", ";", ")", new Setter<FEMFormatter, FEMFunction>() {

			@Override
			public void set(final FEMFormatter input, final FEMFunction value) {
				FEMDomain.this.formatFunction(input, value);
			}

		});
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen Stapelrahmen. Hierbei werden die nummerierten und
	 * {@link #formatFunction(FEMFormatter, FEMFunction) formatierten} Parameterwerte mit {@code ";"} separiert sowie in {@code "("} und {@code ")"}
	 * eingeschlossen erfasst. Die Nummerierung wird vor jedem Parameterwert als Ordnungsposition {@code i} im Format {@code "$i: "} angefügt. Die aktuelle
	 * Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 *
	 * @see #formatItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Stapelrahmen.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatFrame(final FEMFormatter target, final Iterable<? extends FEMValue> source) throws NullPointerException, IllegalArgumentException {
		this.formatItems(target, source, "(", ";", ")", new Setter<FEMFormatter, FEMValue>() {

			int index = 1;

			@Override
			public void set(final FEMFormatter input, final FEMValue value) {
				input.putToken("$").putToken(new Integer(this.index)).putToken(": ");
				FEMDomain.this.formatValue(input, value);
				this.index++;
			}

		});
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung der gegebene Funktion. Für eine {@link TraceFunction}, eine {@link FEMBinding}, eine
	 * {@link FutureFunction} oder eine {@link FEMClosure} wird die Textdarstellung ihrer referenzierten Funktion mit dieser Methode erfasst. Bei einer
	 * {@link FEMComposite} werden die Textdarstellungen der aufzurufenden Funktion mit dieser Methode sowie die der Parameterliste mit
	 * {@link #formatParams(FEMFormatter, Iterable)} erfasst. Bei einem {@link FEMProxy} wird dessen {@link FEMProxy#name() Name} über
	 * {@link #formatConst(FEMFormatter, String)} erfasst. Jeder andere {@link FEMValue} würd über {@link #formatValue(FEMFormatter, FEMValue)} erfasst. Jede
	 * andere {@link FEMFunction} wird über {@link FEMFunction#toString()} in eine Zeichenkette überführt, welche anschließend über
	 * {@link #formatConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatFunction(final FEMFormatter target, final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) {
			final FEMValue value = (FEMValue)source;
			this.formatValue(target, value);
		} else if (source instanceof FEMProxy) {
			final FEMProxy value = (FEMProxy)source;
			this.formatConst(target, value.name.toString());
		} else if (source instanceof FEMComposite) {
			final FEMComposite value = (FEMComposite)source;
			this.formatFunction(target, value.target);
			this.formatParams(target, Arrays.asList(value.params));
		} else if (source instanceof FEMClosure) {
			final FEMClosure closureFunction = (FEMClosure)source;
			target.putToken("{:");
			this.formatFunction(target, closureFunction.target);
			target.putToken("}");
		} else if (source instanceof FEMBinding) {
			final FEMBinding frameFunction = (FEMBinding)source;
			this.formatFunction(target, frameFunction.target);
		} else if (source instanceof FutureFunction) {
			final FutureFunction futureFunction = (FutureFunction)source;
			this.formatFunction(target, futureFunction.target);
		} else if (source instanceof TraceFunction) {
			final TraceFunction traceFunction = (TraceFunction)source;
			this.formatFunction(target, traceFunction.target);
		} else {
			this.formatConst(target, source.toString());
		}
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen Werts. Dazu delegiert sie den gegebenen Wert an die spezifischen
	 * Formatierungsmethoden. Werte unbekannter Datentypen werden über {@link FEMValue#toString()} in eine Zeichenkette überführt, welche anschließend über
	 * {@link #formatConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @see #formatFuture(FEMFormatter, FEMFuture)
	 * @see #formatNative(FEMFormatter, FEMNative)
	 * @see #formatVoid(FEMFormatter, FEMVoid)
	 * @see #formatArray(FEMFormatter, FEMArray)
	 * @see #formatHandler(FEMFormatter, FEMHandler)
	 * @see #formatBoolean(FEMFormatter, FEMBoolean)
	 * @see #formatString(FEMFormatter, FEMString)
	 * @see #formatBinary(FEMFormatter, FEMBinary)
	 * @see #formatInteger(FEMFormatter, FEMInteger)
	 * @see #formatDecimal(FEMFormatter, FEMDecimal)
	 * @see #formatDuration(FEMFormatter, FEMDuration)
	 * @see #formatDatetime(FEMFormatter, FEMDatetime)
	 * @see #formatObject(FEMFormatter, FEMObject)
	 * @see #formatConst(FEMFormatter, String)
	 * @param target Formatierer.
	 * @param source Wert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatValue(final FEMFormatter target, final FEMValue source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMFuture) {
			this.formatFuture(target, (FEMFuture)source);
		} else {
			switch (source.type().id()) {
				case FEMNative.ID:
					this.formatNative(target, (FEMNative)source.result());
				break;
				case FEMVoid.ID:
					this.formatVoid(target, (FEMVoid)source.data());
				break;
				case FEMArray.ID:
					this.formatArray(target, (FEMArray)source.data());
				break;
				case FEMHandler.ID:
					this.formatHandler(target, (FEMHandler)source.data());
				break;
				case FEMBoolean.ID:
					this.formatBoolean(target, (FEMBoolean)source.data());
				break;
				case FEMString.ID:
					this.formatString(target, (FEMString)source.data());
				break;
				case FEMBinary.ID:
					this.formatBinary(target, (FEMBinary)source.data());
				break;
				case FEMInteger.ID:
					this.formatInteger(target, (FEMInteger)source.data());
				break;
				case FEMDecimal.ID:
					this.formatDecimal(target, (FEMDecimal)source.data());
				break;
				case FEMDuration.ID:
					this.formatDuration(target, (FEMDuration)source.data());
				break;
				case FEMDatetime.ID:
					this.formatDatetime(target, (FEMDatetime)source.data());
				break;
				case FEMObject.ID:
					this.formatObject(target, (FEMObject)source.data());
				break;
				default:
					this.formatConst(target, source.toString());
				break;
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen Ergebniswerts. Die Formatierung erfolgt dazu für eine bereits ausgewertete
	 * {@link FEMFuture} mit deren {@link FEMFuture#result(boolean) Ergebniswert} über {@link #formatValue(FEMFormatter, FEMValue)}. Andernfalls werden deren
	 * {@link FEMFuture#function() Funktion} über {@link #formatFunction(FEMFormatter, FEMFunction)} und deren {@link FEMFuture#frame() Stapelrahmen} über
	 * {@link #formatFrame(FEMFormatter, Iterable)} erfasst.
	 *
	 * @param target Formatierer.
	 * @param source Ergebniswert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatFuture(final FEMFormatter target, final FEMFuture source) throws NullPointerException, IllegalArgumentException {
		synchronized (source) {
			if (source.ready()) {
				this.formatValue(target, source.result());
			} else {
				this.formatFunction(target, source.function());
				this.formatFrame(target, source.frame().params());
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen nativen Objekts. Die Formatierung erfolgt dazu für Nutzdaten vom Typ {@link String}
	 * und {@link Character} {@link Strings#formatSequence(CharSequence, char) maskiert} mit doppelten bzw. einfachen Anführungszeichen. Alle anderen Nutzdaten
	 * werden als Konstante {@link #formatConst(FEMFormatter, String) maskiert}.
	 *
	 * @param target Formatierer.
	 * @param source natives Objekt.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatNative(final FEMFormatter target, final FEMNative source) throws NullPointerException, IllegalArgumentException {
		final Object data = source.data();
		if (data instanceof String) {
			target.putToken(Strings.formatSequence(data.toString(), '\"'));
		} else if (data instanceof Character) {
			target.putToken(Strings.formatSequence(data.toString(), '\''));
		} else {
			this.formatConst(target, source.toString());
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen Leerwerts.
	 *
	 * @param target Formatierer.
	 * @param source Leerwerts.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatVoid(final FEMFormatter target, final FEMVoid source) throws NullPointerException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Wertliste. Hierbei werden die {@link #formatValue(FEMFormatter, FEMValue)
	 * formatierten} Werte mit {@code ";"} separiert sowie in {@code "["} und {@code "]"} eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als
	 * einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die Wertliste mehr als ein Element enthält.
	 *
	 * @see #formatItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatValue(FEMFormatter, FEMValue)
	 * @param target Formatierer.
	 * @param source Wertliste.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatArray(final FEMFormatter target, final FEMArray source) throws NullPointerException, IllegalArgumentException {
		this.formatItems(target, source, "[", ";", "]", new Setter<FEMFormatter, FEMValue>() {

			@Override
			public void set(final FEMFormatter input, final FEMValue value) {
				FEMDomain.this.formatValue(input, value);
			}

		});
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen Funktionszeigers. Die {@link #formatFunction(FEMFormatter, FEMFunction)
	 * formatierte} Funktion wird dabei in <code>"{:"</code> und <code>"}"</code> eingeschlossen.
	 *
	 * @see #formatFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatHandler(final FEMFormatter target, final FEMHandler source) throws NullPointerException, IllegalArgumentException {
		target.putToken("{: ");
		this.formatFunction(target, source.value());
		target.putToken("}");
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen Wahrheitswerts.
	 *
	 * @param target Formatierer.
	 * @param source Wahrheitswert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatBoolean(final FEMFormatter target, final FEMBoolean source) throws NullPointerException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Zeichenkette. Die Formatierung erfolgt dazu über
	 * {@link Strings#formatSequence(CharSequence, char)} mit dem einfachen Anführungszeichen zur Maskierung.
	 *
	 * @param target Formatierer.
	 * @param source Zeichenkette.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatString(final FEMFormatter target, final FEMString source) throws NullPointerException {
		target.putToken(Strings.formatSequence(source.toString(), '"'));
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Bytefolge.
	 *
	 * @param target Formatierer.
	 * @param source Bytefolge.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatBinary(final FEMFormatter target, final FEMBinary source) throws NullPointerException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Dezimalzahl.
	 *
	 * @param target Formatierer.
	 * @param source Dezimalzahl.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatInteger(final FEMFormatter target, final FEMInteger source) throws NullPointerException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Dezimalbruch.
	 *
	 * @param target Formatierer.
	 * @param source Dezimalbruch.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatDecimal(final FEMFormatter target, final FEMDecimal source) throws NullPointerException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Zeitspanne.
	 *
	 * @param target Formatierer.
	 * @param source Zeitspanne.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatDuration(final FEMFormatter target, final FEMDuration source) throws NullPointerException, IllegalArgumentException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Zeitangabe.
	 *
	 * @param target Formatierer.
	 * @param source Zeitangabe.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatDatetime(final FEMFormatter target, final FEMDatetime source) throws NullPointerException, IllegalArgumentException {
		target.putToken(source.toString());
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Referenz.
	 *
	 * @param target Formatierer.
	 * @param source Referenz.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatObject(final FEMFormatter target, final FEMObject source) throws NullPointerException, IllegalArgumentException {
		target.putToken(source.toString());
	}

}