package bee.creative.fem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMFunction.FrameFunction;
import bee.creative.fem.FEMFunction.FutureFunction;
import bee.creative.fem.FEMFunction.TraceFunction;
import bee.creative.fem.FEMScript.Token;
import bee.creative.util.Filter;
import bee.creative.util.Getter;
import bee.creative.util.Natives;
import bee.creative.util.Objects;
import bee.creative.util.Setter;
import bee.creative.util.Strings;

/** Diese Klasse implementiert domänenspezifische Parse-, Formatierungs- und Kompilationsmethoden, welche der Übersetzung von Zeichenketten, aufbereitete
 * Quelltexten und Funktionen ineinander dienen.
 * <p>
 * Die {@code parseAs*}-Methoden zum Parsen einer {@link FEMParser#source() Zeichenkette} erkennen darin Bedeutung tragende {@link FEMScript.Token Bereiche} und
 * {@link FEMParser#putToken(Token) erfassen} diese falls nötig im gegebenen {@link FEMParser}. Die {@code formatAs*}-Methoden zum Formatieren eines gegebenen
 * Objekts erzeugen dagegen die Bedeutung tragenden Bereiche der Textdarstellung und {@link FEMFormatter#putToken(Object) erfassen} diese im gegebenen
 * {@link FEMFormatter}. Die {@code compileAs*}-Methoden zum Kompilieren {@link FEMScript aufbereiteter Quelltexte} übersetzen schließlich erkannte Bedeutung
 * tragende {@link FEMScript.Token Bereiche} in Funktionen.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain {

	/** Dieses Feld speichert die native {@link FEMDomain}, welche das Kompilieren nativer Konstanten und Methoden unterstützt.
	 *
	 * @see FEMNative
	 * @see FEMReflection */
	public static final FEMDomain NATIVE = new FEMDomain() {

		@Override
		protected FEMValue compileAsValue(FEMCompiler source, String string) throws NullPointerException, IllegalArgumentException {
			if (string.equals("null")) return FEMNative.NULL;
			if (string.equals("true")) return FEMNative.TRUE;
			if (string.equals("false")) return FEMNative.FALSE;
			try {
				return new FEMNative(new BigDecimal(string));
			} catch (final IllegalArgumentException cause) {}
			try {
				if (string.endsWith(".class")) return new FEMNative(Natives.parse(string));
			} catch (final IllegalArgumentException cause) {}
			return null;
		}

		@Override
		protected FEMValue compileAsString(FEMCompiler source) throws NullPointerException, IllegalArgumentException {
			final int symbol = source.symbol();
			if (symbol == '\"') {
				final FEMValue result = new FEMNative(Strings.parseSequence(source.section(), '\"').toString());
				source.skip();
				return result;
			} else if (symbol == '\'') {
				final FEMValue result = new FEMNative(new Character(Strings.parseSequence(source.section(), '\'').charAt(0)));
				source.skip();
				return result;
			}
			return null;
		}

		@Override
		protected FEMFunction compileAsFunction(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
			final FEMValue result = this.compileAsValue(source, string);
			if (result != null) return result;
			try {
				return FEMReflection.from(string);
			} catch (final IllegalArgumentException cause) {}
			return source.proxy(string);
		}

		@Override
		public String toString() {
			return "NATIVE";
		}

	};

	/** Dieses Feld speichert die normale {@link FEMDomain}. */
	public static final FEMDomain NORMAL = new FEMDomain();

	/** Dieses Feld speichert den {@link FEMScript#mode() Sktiptmodus} zum Parsen und Kompilieren eines Einzelwerts ({@code FEMValue}). */
	public static final int PARSE_VALUE = 2;

	/** Dieses Feld speichert den {@link FEMScript#mode() Sktiptmodus} zum Parsen und Kompilieren einer Wertliste ({@code List<FEMValue>}). */
	public static final int PARSE_VALUE_LIST = 3;

	/** Dieses Feld speichert den {@link FEMScript#mode() Sktiptmodus} zum Parsen und Kompilieren einer Funktion ({@code FEMFunction}). */
	public static final int PARSE_FUNCTION = 4;

	/** Dieses Feld speichert den {@link FEMScript#mode() Sktiptmodus} zum Parsen und Kompilieren einer Funktionsliste ({@code List<FEMFunction>}). */
	public static final int PARSE_FUNCTION_LIST = 5;

	/** Dieses Feld speichert den {@link FEMScript#mode() Sktiptmodus} zum Parsen und Kompilieren einer Wertliste ({@code Map<String, FEMProxy>}). */
	public static final int PARSE_PROXY_MAP = 6;

	{}

	/** Diese Methode parst die als maskierte Zeichenkette gegebene Konstante und gibt diese ohne Maskierung zurück. Sie realisiert damit die Umkehroperation von
	 * {@link #formatConst(String)}.
	 *
	 * @param string Zeichenkette.
	 * @return gegebene bzw. geparste Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String parseConst(final String string) throws NullPointerException {
		final String result = Strings.parseSequence(string, '<', '/', '>');
		return result != null ? result : string;
	}

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link FEMScript aufbereiteten Quelltext} und gibt diesen zurück. Sie erzeugt dazu einen
	 * {@link FEMParser} und delegiert diesen zusammen mit dem gegebenen Skriptmodus an {@link #parseAsScript(FEMParser, int)}.
	 *
	 * @see #PARSE_VALUE
	 * @see #PARSE_VALUE_LIST
	 * @see #PARSE_FUNCTION
	 * @see #PARSE_FUNCTION_LIST
	 * @see #PARSE_PROXY_MAP
	 * @param source Zeichenkette, die geparst werden soll.
	 * @param scriptMode Modus für {@link FEMScript#mode()}.
	 * @return aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMScript parseScript(final String source, final int scriptMode) throws NullPointerException {
		final FEMParser parser = new FEMParser().useSource(source);
		this.parseAsScript(parser, scriptMode);
		return FEMScript.from(scriptMode, source, parser.tokens());
	}

	/** Diese Methode implementiert {@link #parseScript(String, int)} und ist eine Abkürzung für {@code this.parseAsItems(target, itemLimit, itemParser)}, wobei
	 * {@code itemLimit} und {@code itemParser} abhängig vom gegebenen {@link FEMScript#mode() Skriptmodus} bestückt werden.<br>
	 * Die Implementation in {@link FEMDomain} erfasst für alle nicht unterstützten Skriptmodus einen {@link #parseAsError(FEMParser) Fehlerbereich}. Die maximale
	 * Elementanzahl {@code itemLimit} ist für {@link #PARSE_VALUE} und {@link #PARSE_FUNCTION} gleich {@code 1} und sonst {@code 0}. Der {@code itemParser}
	 * delegiert für {@link #PARSE_VALUE} und {@link #PARSE_VALUE_LIST} an {@link #parseAsValue(FEMParser)}, für {@link #PARSE_PROXY_MAP} an
	 * {@link #parseAsProxy(FEMParser)} sowie für {@link #PARSE_FUNCTION} und {@link #PARSE_FUNCTION_LIST} an {@link #parseAsFunction(FEMParser)}.
	 *
	 * @see #parseAsItems(FEMParser, int, Filter)
	 * @param target Parser.
	 * @param scriptMode Skriptmodus.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseAsScript(FEMParser target, int scriptMode) throws NullPointerException {
		int itemLimit = 1;
		Filter<FEMParser> itemParser;
		switch (scriptMode) {
			default:
				this.parseAsError(target);
				return;
			case PARSE_VALUE_LIST:
				itemLimit = 0;
			case PARSE_VALUE:
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this.parseAsValue(input);
					}

				};
			break;
			case PARSE_PROXY_MAP:
				itemLimit = 0;
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this.parseAsProxy(input);
					}

				};
			break;
			case PARSE_FUNCTION_LIST:
				itemLimit = 0;
			case PARSE_FUNCTION:
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this.parseAsFunction(input);
					}

				};
			break;
		}
		this.parseAsItems(target, itemLimit, itemParser);
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer mit Semikolon separierten Auflistung von über den gegebenen {@code itemParser} erkannten
	 * Elementen und gibt nur dann {@code true} zurück, wenn diese Auflistung am ersten Element erkannt wurden. Das Symbol {@code ';'} wird direkt als Typ des
	 * erfassten Bereichs eingesetzt. Zwischen all diesen Komponenten können beliebig viele {@link #parseAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @param itemLimit Maximale Anzahl der Elemente in der Auflistung, wobei Werte kleiner oder gleich {@code 0} für eine unbeschränkte Anzahl stehen.
	 * @param itemParser {@link Filter} zum Erkennen, Parsen und Erfassen der {@link Token Bereiche} eines Elements.
	 * @return {@code true}, wenn die Auflistung erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code itemParser} {@code null} ist. */
	protected boolean parseAsItems(final FEMParser target, int itemLimit, Filter<? super FEMParser> itemParser) throws NullPointerException {
		this.parseAsComments(target);
		if (!itemParser.accept(target)) return false;
		while (true) {
			--itemLimit;
			this.parseAsComments(target);
			if (target.isParsed()) return true;
			if ((itemLimit == 0) || (target.symbol() != ';')) return this.parseAsError(target);
			target.putToken(';');
			target.skip();
			this.parseAsComments(target);
			if (!itemParser.accept(target)) return this.parseAsError(target);
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer {@link FEMProxy benannten Funktion} und gibt nur dann {@code true} zurück, wenn diese an
	 * ihrer {@link #parseAsConst(FEMParser) Bezeichnung} erkannt wurde. Der Bezeichnung folg die als {@link #parseAsHandler(FEMParser) Funktionszeiger}
	 * angegebene Funktion, wobei zwischen diesen beiden Komponenten beliebig viele {@link #parseAsComments(FEMParser) Kommentare/Leerraum} stehen können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die benannten Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsProxy(final FEMParser target) throws NullPointerException {
		if (!this.parseAsConst(target)) return false;
		this.parseAsComments(target);
		if (!this.parseAsHandler(target)) return this.parseAsError(target);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Konstante und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür in spitze Klammen eingeschlossene und mit Schrägstrich {@link #parseAsSequence(FEMParser, char, char, char) maskierte} Zeichenkette sowie
	 * unmaskierte Bezeichner durch. Ein unmaskierter Bezeichner ist eine Zeichenkette ohne Leerraum, Schrägstrich, Semikolon sowie ohne runde, eckige oder
	 * geschweifte Klammer und nutzen den Bereichstyp {@code '?'}.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Konstante erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsConst(final FEMParser target) throws NullPointerException {
		if (this.parseAsSequence(target, '<', '/', '>')) return true;
		final int offset = target.index();
		int symbol = target.symbol();
		LOOP: while (true) {
			if (symbol <= ' ') {
				break LOOP;
			}
			switch (symbol) {
				case ';':
				case '/':
				case '(':
				case ')':
				case '[':
				case ']':
				case '{':
				case '}':
				break LOOP;
			}
			symbol = target.skip();
		}
		if (target.index() == offset) return false;
		target.putToken('?', offset);
		return true;
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Wertliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden eckigen
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseAsValue(FEMParser) Werte}. Die Wertliste endet mit
	 * der schließenden eckigen Klammer. Die Symbole {@code '['}, {@code ';'} und {@code ']'} werden direkt als Typ der erfassten Bereiche eingesetzt. Wenn die
	 * Wertliste fehlerhaft ist, wird für die öffnende eckige Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können beliebig
	 * viele {@link #parseAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Wertliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsArray(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '[') return false;
		final int openIndex = target.putToken('[');
		target.skip();
		this.parseAsComments(target);
		if (target.symbol() == ']') {
			target.putToken(']');
			target.skip();
			return true;
		} else if (target.isParsed() || !this.parseAsValue(target)) {
			target.setToken(openIndex, '!');
			return this.parseAsError(target);
		} else {
			while (true) {
				this.parseAsComments(target);
				if (target.symbol() == ']') {
					target.putToken(']');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this.parseAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseAsComments(target);
					if (!this.parseAsValue(target)) {
						target.setToken(openIndex, '!');
						return this.parseAsError(target);
					}
				}
			}
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Werts als Element einer {@link #parseAsArray(FEMParser) Wertliste} und gibt nur dann
	 * {@code true} zurück, wenn dieser erkannt wurde. Sie probiert hierfür {@link #parseAsArray(FEMParser) Wertlisten}, {@link #parseAsString(FEMParser)
	 * Zeichenketten}, {@link #parseAsHandler(FEMParser) Funktionszeiger} und {@link #parseAsConst(FEMParser) Konstanten} durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Wert erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsValue(final FEMParser target) throws NullPointerException {
		return this.parseAsArray(target) || this.parseAsString(target) || this.parseAsHandler(target) || this.parseAsConst(target);
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Zeichenkette und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür mit einfachen und doppelten Anführungszeichen {@link #parseAsSequence(FEMParser, char) maskierte} Zeichenketten durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Zeichenkette erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsString(final FEMParser target) throws NullPointerException {
		return this.parseAsSequence(target, '\'') || this.parseAsSequence(target, '\"');
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Funktionszeigers und gibt nur dann {@code true} zurück, wenn dieser an der öffnenden
	 * geschweiften Klammer erkannt wurde.<br>
	 * Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseAsName(FEMParser) Parameternamen}. Auf diese folgen dann ein Doppelpunkt,
	 * die {@link #parseAsFunction(FEMParser) Funktion} sowie die schließenden geschweifte Klammer. Die Symbole <code>'{'</code>, {@code ';'}, {@code ':'} und
	 * <code>'}'</code> werden direkt als Typ der erfassten Bereiche eingesetzt. Wenn de Funktionszeiger fehlerhaft ist, wird für die öffnende geschweifte Klammer
	 * sowie den Doppelpunkt der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können beliebig viele {@link #parseAsComments(FEMParser)
	 * Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Funktionszeiger erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsHandler(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '{') return false;
		final int openIndex = target.putToken('{'), closeIndex;
		target.skip();
		this.parseAsComments(target);
		if (target.symbol() == ':') {
			closeIndex = target.putToken(':');
			target.skip();
		} else if (target.isParsed() || !this.parseAsName(target)) {
			target.setToken(openIndex, '!');
			return this.parseAsError(target);
		} else {
			while (true) {
				this.parseAsComments(target);
				if (target.symbol() == ':') {
					closeIndex = target.putToken(':');
					target.skip();
					break;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this.parseAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseAsComments(target);
					if (!this.parseAsName(target)) {
						target.setToken(openIndex, '!');
						return this.parseAsError(target);
					}
				}
			}
		}
		this.parseAsComments(target);
		if (target.isParsed() || !this.parseAsFunction(target)) {
			target.setToken(openIndex, '!');
			target.setToken(closeIndex, '!');
			return this.parseAsError(target);
		}
		this.parseAsComments(target);
		if (target.isParsed() || (target.symbol() != '}')) {
			target.setToken(openIndex, '!');
			target.setToken(closeIndex, '!');
			return this.parseAsError(target);
		}
		target.putToken('}');
		target.skip();
		return true;
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Funktion und gibt nur dann {@code true} zurück, wenn diese erkannt wurde.<br>
	 * Sie probiert hierfür {@link #parseAsArray(FEMParser) Wertlisten}, {@link #parseAsString(FEMParser) Zeichenketten}, {@link #parseAsLocale(FEMParser)
	 * Parameterverweise}, {@link #parseAsHandler(FEMParser) Funktionszeiger} und {@link #parseAsConst(FEMParser) Konstanten} durch, wobei die letzten drei noch
	 * von beliebig viel {@link #parseAsComments(FEMParser) Leerraum/Kommentaren} und {@link #parseAsParams(FEMParser) Parameterlisten} gefolgt werden können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsFunction(final FEMParser target) throws NullPointerException {
		if (this.parseAsArray(target) || this.parseAsString(target)) return true;
		if (!this.parseAsLocale(target) && !this.parseAsHandler(target) && !this.parseAsConst(target)) return false;
		while (true) {
			this.parseAsComments(target);
			if (!this.parseAsParams(target)) return true;
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Parameterliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden runden
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseAsFunction(FEMParser) Parameterfunktionen}. Die
	 * Parameterliste endet mit der schließenden runden Klammer. Die Symbole {@code '('}, {@code ';'} und {@code ')'} werden direkt als Typ der erfassten Bereiche
	 * eingesetzt. Wenn die Wertliste fehlerhaft ist, wird für die öffnende runde Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten
	 * können beliebig viele {@link #parseAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Parameterliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsParams(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '(') return false;
		final int openIndex = target.putToken('(');
		target.skip();
		this.parseAsComments(target);
		if (target.symbol() == ')') {
			target.putToken(')');
			target.skip();
			return true;
		} else if (target.isParsed() || !this.parseAsFunction(target)) {
			target.setToken(openIndex, '!');
			return this.parseAsError(target);
		} else {
			while (true) {
				this.parseAsComments(target);
				if (target.symbol() == ')') {
					target.putToken(')');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this.parseAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseAsComments(target);
					if (!this.parseAsFunction(target)) {
						target.setToken(openIndex, '!');
						return this.parseAsError(target);
					}
				}
			}
		}
	}

	/** Diese Methode erfassten die verbleibende Eingabe als {@link Token Fehlerbereich} mit dem Typ {@code '!'} und gibt {@code true} zurück.
	 *
	 * @param target Parser.
	 * @return {@code true}.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsError(final FEMParser target) throws NullPointerException {
		final int index = target.index(), length = target.length();
		target.putToken('!', index, length - index);
		target.seek(length);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} eines Parameterindexes und gibt nur dann {@code true} zurück, wenn dieser erkannt wurde.<br>
	 * Sie sucht dazu eine nicht leere Zeichenkette aus dezimalen Ziffern und nutzt das Symbole {@code '#'} als Typ des erfassten Bereichs.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Parameterindex erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsIndex(final FEMParser target) throws NullPointerException {
		final int offset = target.index();
		for (int symbol = target.symbol(); ('0' <= symbol) && (symbol <= '9'); symbol = target.skip()) {}
		if (target.index() == offset) return false;
		target.putToken('#', offset);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} eines Parameternamen und gibt nur dann {@code true} zurück, wenn dieser erkannt wurde.<br>
	 * Sie sucht dazu eine nicht leere Zeichenkette ohne Leerraum, Schrägstrich, Doppelpunkt, Semikolon sowie ohne runde, eckige oder geschweifte Klammer. Das
	 * Symbol {@code '~'} wird als Typ des erfassten Bereichs eingesetzt.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Parametername erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsName(final FEMParser target) throws NullPointerException {
		final int offset = target.index();
		int symbol = target.symbol();
		LOOP: while (true) {
			if (symbol <= ' ') {
				break LOOP;
			}
			switch (symbol) {
				case '/':
				case ':':
				case ';':
				case '(':
				case ')':
				case '[':
				case ']':
				case '{':
				case '}':
				break LOOP;
			}
			symbol = target.skip();
		}
		if (target.index() == offset) return false;
		target.putToken('~', offset);
		return true;
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Parameterverweises und gibt nur dann {@code true} zurück, wenn dieser am Dollarzeichen
	 * erkannt wurde.<br>
	 * Diesem Zeichen kann ein {@link #parseAsIndex(FEMParser) Parameterindex} oder ein {@link #parseAsName(FEMParser) Parametername} folgen. Das Symbol
	 * {@code '$'} wird direkt als Typ des erfassten Bereichs eingesetzt.
	 *
	 * @param source Parser.
	 * @return {@code true}, wenn der Parameterverweis erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsLocale(final FEMParser source) throws NullPointerException {
		if (source.symbol() != '$') return false;
		source.putToken('$');
		source.skip();
		if (this.parseAsIndex(source)) return true;
		this.parseAsName(source);
		return true;
	}

	/** Diese Methode parst den {@link Token Bereich} von Leerraum, erfasst diese jedoch nicht.<br>
	 * Zum Leerraum zählen alle Symbole kleiner oder gleich {@code ' '}.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseAsSpace(final FEMParser source) throws NullPointerException {
		for (int symbol = source.symbol(); (symbol >= 0) && (symbol <= ' '); symbol = source.skip()) {}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} von Kommentaren.<br>
	 * Ein Kommentar wird als mit Schrägstrich {@link #parseAsSequence(FEMParser, char)} maskierte} Zeichenkette erkannt und erfasst. Vor und nach einem Kommentar
	 * kann beliebig viel {@link #parseAsSpace(FEMParser) Leerraum} stehen.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseAsComments(final FEMParser source) throws NullPointerException {
		do {
			this.parseAsSpace(source);
		} while (this.parseAsSequence(source, '/'));
	}

	/** Diese Methode ist eine Abkürzung für {@code this.parseAsSequence(source, maskSymbol, maskSymbol,
	 * maskSymbol)}.
	 *
	 * @see #parseAsSequence(FEMParser, char, char, char)
	 * @param source Parser.
	 * @param maskSymbol Maskierungszeichen.
	 * @return {@code true}, wenn die Sequenz erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsSequence(final FEMParser source, final char maskSymbol) throws NullPointerException {
		return this.parseAsSequence(source, maskSymbol, maskSymbol, maskSymbol);
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Zeichenkette analog zu {@link Strings#parseSequence(CharSequence, char, char, char)} und
	 * gibt nur dann {@code true} zurück, wenn diese am {@code openSymbol} erkannt wurde.<br>
	 * Für eine fehlerfreie Sequenz wird {@code openSymbol} als Typ des Bereichs eingesetzt. Eine fehlerhafte Sequenz geht dagegen bis zum Ende der Eingabe und
	 * wird mit dem Bereichstyp {@code '!'} erfasst.
	 *
	 * @param target Parser.
	 * @param openSymbol Erstes Symbol der Zeichenkette.
	 * @param maskSymbol Symbol zur Maskierungszeichen.
	 * @param closeSymbol Letztes Symbol der Zeichenkette.
	 * @return {@code true}, wenn die Sequenz erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseAsSequence(final FEMParser target, final char openSymbol, final char maskSymbol, final char closeSymbol) throws NullPointerException {
		if (target.symbol() != openSymbol) return false;
		final int offset = target.index();
		while (true) {
			int symbol = target.skip();
			if (symbol < 0) {
				target.putToken('!', offset);
				return true;
			}
			if (symbol == maskSymbol) {
				symbol = target.skip();
				if ((symbol == openSymbol) || (symbol == maskSymbol) || (symbol == closeSymbol)) {
					continue;
				} else if (maskSymbol == closeSymbol) {
					target.putToken(openSymbol, offset);
					return true;
				}
				target.putToken('!', offset);
				return true;
			} else if ((symbol == openSymbol) && (openSymbol != closeSymbol)) {
				target.putToken('!', offset);
				return true;
			} else if (symbol == closeSymbol) {
				target.skip();
				target.putToken(openSymbol, offset);
				return true;
			}
		}
	}

	{}

	/** Diese Methode ist eine Abkürzung für {@code this.formatConst(name, false)}.
	 *
	 * @see #formatConst(String, boolean)
	 * @param string Zeichenkette.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatConst(final String string) throws NullPointerException {
		return this.formatConst(string, false);
	}

	/** Diese Methode formatiert die als Zeichenkette gegebene Konstante und gibt sie falls nötig mit Maskierung als formatierte Zeichenkette zurück. Die
	 * Maskierung ist notwendig, wenn {@code forceMask} dies anzeigt oder die Zeichenkette ein Leerzeichen, Semikolon, Schrägstrich bzw. eine runde, eckige oder
	 * geschweifte Klammer enthält. Die Maskierung erfolgt via {@link Strings#formatSequence(CharSequence, char, char, char) Strings.formatSequence(string, '<',
	 * '/', '>')}. Wenn die Maskierung unnötig ist, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param forceMask {@code true}, wenn die Maskierung notwendig ist.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatConst(final String string, final boolean forceMask) throws NullPointerException {
		if (forceMask) return Strings.formatSequence(string, '<', '/', '>');
		for (int i = string.length(); i != 0;) {
			final char symbol = string.charAt(--i);
			if (symbol <= ' ') return Strings.formatSequence(string, '<', '/', '>');
			switch (symbol) {
				case ';':
				case '/':
				case '{':
				case '}':
				case '(':
				case ')':
				case '[':
				case ']':
					return Strings.formatSequence(string, '<', '/', '>');
			}
		}
		return string;
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen aufbereiteten Quelltextes zurück.
	 *
	 * @see #formatAsScript(FEMFormatter, FEMCompiler)
	 * @param script aufbereiteter Quelltext.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code script} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code script} nicht formatiert werden kann. */
	public String formatScript(final FEMScript script) {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsScript(target, new FEMCompiler().useScript(script));
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung des gegebenen Werts zurück.
	 *
	 * @see #formatAsValue(FEMFormatter, FEMValue)
	 * @param value Funktion.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
	public String formatValue(final FEMValue value) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsValue(target, value);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung einer Wertliste mit den gegebenen Werten zurück.
	 *
	 * @see #formatAsArray(FEMFormatter, Iterable)
	 * @param array Wertliste.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
	public String formatArray(final Iterable<? extends FEMValue> array) {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsArray(target, array);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Parameterwerte zurück.
	 *
	 * @see #formatAsFrame(FEMFormatter, Iterable)
	 * @param frame Parameterwerte.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code frame} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code frame} nicht formatiert werden kann. */
	public String formatFrame(final Iterable<? extends FEMValue> frame) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsFrame(target, frame);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Parameterfunktionen zurück.
	 *
	 * @see #formatAsParams(FEMFormatter, Iterable)
	 * @param params Parameterfunktionen.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public String formatParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsParams(target, params);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Funktion zurück.
	 *
	 * @see #formatAsFunction(FEMFormatter, FEMFunction)
	 * @param function Funktion.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code function} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
	public String formatFunction(final FEMFunction function) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this.formatAsFunction(target, function);
		return target.format();
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der Liste der gegebenen Elemente.<br>
	 * Die Elemente werden über den gegebenen {@link Setter} {@link Setter#set(Object, Object) formatiert} und mit {@code commaSymbol} separiert sowie in
	 * {@code openSymbol} und {@code closeSymbol} eingeschlossen. Bei einer licht leeren Liste {@link FEMFormatter#putBreakInc() beginnt} nach {@code openSymbol}
	 * eine neue Hierarchieebene, die vor {@code closeSymbol} {@link FEMFormatter#putBreakDec() endet}. Nach jedem {@code commaSymbol} wird ein
	 * {@link FEMFormatter#putBreakSpace() bedingtes Leerzeichen} eingefügt. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent()
	 * markiert}, wenn die Liste mehr als ein Element enthält.
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
	protected <GItem> void formatAsItems(final FEMFormatter target, final Iterable<? extends GItem> source, final Object openSymbol, final Object commaSymbol,
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

	/** Diese Methode formateirt und erfasst die Textdarstellung der Konstanten mit der gegebenen Bezeichnung.<br>
	 * Die Formatierung der Bezeichnung erfolgt dazu über {@link #formatConst(String)}.
	 *
	 * @param target Formatierer.
	 * @param source Bezeichnung.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatAsConst(final FEMFormatter target, final String source) throws NullPointerException {
		target.putToken(this.formatConst(source));
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Wertliste.<br>
	 * Hierbei werden die {@link #formatAsValue(FEMFormatter, FEMValue) formatierten} Werte mit {@code ";"} separiert sowie in {@code "["} und {@code "]"}
	 * eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die Wertliste mehr als
	 * ein Element enthält.
	 *
	 * @see #formatAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatAsValue(FEMFormatter, FEMValue)
	 * @param target Formatierer.
	 * @param source Wertliste.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsArray(FEMFormatter target, Iterable<? extends FEMValue> source) throws NullPointerException, IllegalArgumentException {
		this.formatAsItems(target, source, "[", ";", "]", new Setter<FEMFormatter, FEMValue>() {

			@Override
			public void set(final FEMFormatter input, final FEMValue value) {
				FEMDomain.this.formatAsValue(input, value);
			}

		});
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen Werts.<br>
	 * Für ein {@link FEMArray}, einen {@link FEMString}, eine {@link FEMFuture}, ein {@link FEMNative} ein {@link FEMObject} uns ein {@link FEMHandler} wird die
	 * Textdarstellung über {@link #formatAsArray(FEMFormatter, Iterable)}, {@link #formatAsString(FEMFormatter, FEMString)},
	 * {@link #formatAsFuture(FEMFormatter, FEMFuture)}, {@link #formatAsNative(FEMFormatter, FEMNative)}, {@link #formatAsObject(FEMFormatter, FEMObject)} bzw.
	 * {@link #formatAsHandler(FEMFormatter, FEMHandler)} erfasst. Jeder andere {@link FEMValue} wird über {@link FEMValue#toString()} in eine Zeichenkette
	 * überführt, welche anschließend über {@link #formatAsConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @param target Formatierer.
	 * @param source Wert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsValue(final FEMFormatter target, final FEMValue source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMArray) {
			this.formatAsArray(target, (FEMArray)source);
		} else if (source instanceof FEMString) {
			this.formatAsString(target, (FEMString)source);
		} else if (source instanceof FEMFuture) {
			this.formatAsFuture(target, (FEMFuture)source);
		} else if (source instanceof FEMNative) {
			this.formatAsNative(target, (FEMNative)source);
		} else if (source instanceof FEMObject) {
			this.formatAsObject(target, (FEMObject)source);
		} else if (source instanceof FEMHandler) {
			this.formatAsHandler(target, (FEMHandler)source);
		} else {
			this.formatAsConst(target, source.toString());
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Zeichenkette.<br>
	 * Die Formatierung erfolgt dazu über {@link Strings#formatSequence(CharSequence, char)} mit dem einfachen Anführungszeichen zur Maskierung.
	 *
	 * @param target Formatierer.
	 * @param source Zeichenkette.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatAsString(final FEMFormatter target, final FEMString source) throws NullPointerException {
		target.putToken(Strings.formatSequence(source.toString(), '"'));
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen Ergebniswerts.<br>
	 * Die Formatierung erfolgt dazu für eine bereits ausgewertete {@link FEMFuture} mit deren {@link FEMFuture#result(boolean) Ergebniswert} über
	 * {@link #formatAsValue(FEMFormatter, FEMValue)}. Andernfalls werden deren {@link FEMFuture#function() Funktion} über
	 * {@link #formatAsFunction(FEMFormatter, FEMFunction)} und deren {@link FEMFuture#frame() Stapelrahmen} über {@link #formatAsFrame(FEMFormatter, Iterable)}
	 * erfasst.
	 *
	 * @param target Formatierer.
	 * @param source Ergebniswert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsFuture(final FEMFormatter target, final FEMFuture source) throws NullPointerException, IllegalArgumentException {
		synchronized (source) {
			if (source.ready()) {
				this.formatAsValue(target, source.result());
			} else {
				this.formatAsFunction(target, source.function());
				this.formatAsFrame(target, source.frame());
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung des gegebenen nativen Objekts.<br>
	 * Die Formatierung erfolgt dazu für Nutzdaten vom Typ {@link String} und {@link Character} {@link Strings#formatSequence(CharSequence, char) maskiert} mit
	 * doppelten bzw. einfachen Anführungszeichen. Alle anderen Nutzdaten werden als Konstante {@link #formatAsConst(FEMFormatter, String) maskiert}.
	 *
	 * @param target Formatierer.
	 * @param source natives Objekt.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsNative(FEMFormatter target, FEMNative source) throws NullPointerException, IllegalArgumentException {
		final Object data = source.data();
		if (data instanceof String) {
			target.putToken(Strings.formatSequence(data.toString(), '\"'));
		} else if (data instanceof Character) {
			target.putToken(Strings.formatSequence(data.toString(), '\''));
		} else {
			this.formatAsConst(target, source.toString());
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Referenz.
	 *
	 * @param target Formatierer.
	 * @param source Referenz.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsObject(FEMFormatter target, FEMObject source) throws NullPointerException, IllegalArgumentException {
		target.putToken(source.toString());
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen Funktionszeigers.<br>
	 * Die {@link #formatAsFunction(FEMFormatter, FEMFunction) formatierte} Funktion wird dabei in <code>"{:"</code> und <code>"}"</code> eingeschlossen.
	 *
	 * @see #formatAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsHandler(final FEMFormatter target, final FEMHandler source) throws NullPointerException, IllegalArgumentException {
		target.putToken("{:");
		this.formatAsFunction(target, source.value());
		target.putToken("}");
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung der gegebene Funktion.<br>
	 * Für eine {@link TraceFunction}, eine {@link FrameFunction}, eine {@link FutureFunction} oder eine {@link ClosureFunction} wird die Textdarstellung ihrer
	 * referenzierten Funktion mit dieser Methode erfasst. Bei einer {@link ConcatFunction} oder einer {@link CompositeFunction} werden die Textdarstellungen der
	 * aufzurufenden Funktion mit dieser Methode sowie die der Parameterliste mit {@link #formatAsParams(FEMFormatter, Iterable)} erfasst. Bei einem
	 * {@link FEMProxy} wird dessen {@link FEMProxy#name() Name} über {@link #formatAsConst(FEMFormatter, String)} erfasst. Jeder andere {@link FEMValue} würd
	 * über {@link #formatAsValue(FEMFormatter, FEMValue)} erfasst. Jede andere {@link FEMFunction} wird über {@link FEMFunction#toString()} in eine Zeichenkette
	 * überführt, welche anschließend über {@link #formatAsConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsFunction(final FEMFormatter target, final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) {
			final FEMValue value = (FEMValue)source;
			this.formatAsValue(target, value);
		} else if (source instanceof FEMProxy) {
			final FEMProxy value = (FEMProxy)source;
			this.formatAsConst(target, value.name);
		} else if (source instanceof CompositeFunction) {
			final CompositeFunction value = (CompositeFunction)source;
			this.formatAsFunction(target, value.function);
			this.formatAsParams(target, Arrays.asList(value.params));
		} else if (source instanceof ConcatFunction) {
			final ConcatFunction concatFunction = (ConcatFunction)source;
			this.formatAsFunction(target, concatFunction.function);
			this.formatAsParams(target, Arrays.asList(concatFunction.params));
		} else if (source instanceof ClosureFunction) {
			final ClosureFunction closureFunction = (ClosureFunction)source;
			this.formatAsFunction(target, closureFunction.function);
		} else if (source instanceof FrameFunction) {
			final FrameFunction frameFunction = (FrameFunction)source;
			this.formatAsFunction(target, frameFunction.function);
		} else if (source instanceof FutureFunction) {
			final FutureFunction futureFunction = (FutureFunction)source;
			this.formatAsFunction(target, futureFunction.function);
		} else if (source instanceof TraceFunction) {
			final TraceFunction traceFunction = (TraceFunction)source;
			this.formatAsFunction(target, traceFunction.function);
		} else {
			this.formatAsConst(target, source.toString());
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung gegebenen Parameterwertliste.<br>
	 * Hierbei werden die nummerierten und {@link #formatAsFunction(FEMFormatter, FEMFunction) formatierten} Parameterwerte mit {@code ";"} separiert sowie in
	 * {@code "("} und {@code ")"} eingeschlossen erfasst. Die Nummerierung wird vor jedem Parameterwert als Ordnungsposition {@code i} im Format {@code "$i: "}
	 * angefügt. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 *
	 * @see #formatAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Parameterwerte.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsFrame(FEMFormatter target, Iterable<? extends FEMValue> source) throws NullPointerException, IllegalArgumentException {
		this.formatAsItems(target, source, "(", ";", ")", new Setter<FEMFormatter, FEMValue>() {

			int index = 1;

			@Override
			public void set(final FEMFormatter input, final FEMValue value) {
				input.putToken("$").putToken(new Integer(this.index)).putToken(": ");
				FEMDomain.this.formatAsValue(input, value);
				this.index++;
			}

		});
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Parameterfunktionsliste.<br>
	 * Hierbei werden die {@link #formatAsFunction(FEMFormatter, FEMFunction) formatierten} Parameterfunktionen mit {@code ";"} separiert sowie in {@code "("} und
	 * {@code ")"} eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die
	 * Funktionsliste mehr als ein Element enthält.
	 *
	 * @see #formatAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #formatAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Parameterfunktionen.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsParams(FEMFormatter target, Iterable<? extends FEMFunction> source) throws NullPointerException, IllegalArgumentException {
		this.formatAsItems(target, source, "(", ";", ")", new Setter<FEMFormatter, FEMFunction>() {

			@Override
			public void set(final FEMFormatter input, final FEMFunction value) {
				FEMDomain.this.formatAsFunction(input, value);
			}

		});
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltexts.
	 *
	 * @param target Formatierer.
	 * @param source Parser zum aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsScript(final FEMFormatter target, final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		while (true) {
			this.formatAsScript(target, source, false);
			if (source.symbol() < 0) return;
			target.putToken(source.section()).putBreakSpace();
			source.skip();
		}
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung der im aufbereiteten Quelltext gegebenen Sequenz von Namen, Werten und Funktionen.
	 *
	 * @param target Formatierer.
	 * @param source Parser zum aufbereiteten Quelltext.
	 * @param simpleSpace {@code true}, wenn hinter Kommentaren und Semikola ein einfaches Leerzeichen statt eines bedingten Umbruchs eingefügt werden soll.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatAsScript(final FEMFormatter target, final FEMCompiler source, final boolean simpleSpace)
		throws NullPointerException, IllegalArgumentException {
		int count = 0;
		while (true) {
			switch (source.symbol()) {
				case '/': {
					target.putToken(source.section());
					if (simpleSpace) {
						target.putToken(" ");
					} else {
						target.putBreakSpace();
					}
					source.skip();
					count++;
					break;
				}
				case ';': {
					target.putToken(";");
					if (simpleSpace) {
						target.putToken(" ");
					} else {
						target.putBreakSpace();
					}
					source.skip();
					count++;
					break;
				}
				case '(':
					target.putToken("(").putBreakInc();
					source.skip();
					this.formatAsScript(target, source, false);
					if (source.symbol() == ')') {
						target.putBreakDec().putToken(")");
						source.skip();
					}
				break;
				case '[':
					target.putToken("[").putBreakInc();
					source.skip();
					this.formatAsScript(target, source, false);
					if (source.symbol() == ']') {
						target.putBreakDec().putToken("]");
						source.skip();
					}
				break;
				case '{':
					target.putToken("{");
					source.skip();
					this.formatAsScript(target, source, true);
					if (source.symbol() == ':') {
						target.putToken(": ");
						source.skip();
						this.formatAsScript(target, source, false);
					}
					if (source.symbol() == '}') {
						target.putToken("}");
						source.skip();
					}
				break;
				default:
					target.putToken(source.section());
					source.skip();
				break;
				case ':':
				case ']':
				case '}':
				case ')':
				case -1:
					if (count < 2) return;
					target.putIndent();
					return;
			}
		}
	}

	{}

	/** Diese Methode ist eine Abkürzung für {@code this.compileScript(this.parseScript(source, scriptMode))}.
	 *
	 * @see #parseScript(String, int)
	 * @see #compileScript(FEMScript)
	 * @param source Zeichenkette, die geparst werden soll.
	 * @param scriptMode Skriptmodus für {@link FEMScript#mode()}.
	 * @return Kompiliertes Objekt abhängig vom Skriptmodus.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	public Object compileScript(final String source, final int scriptMode) throws NullPointerException, IllegalArgumentException {
		return this.compileScript(this.parseScript(source, scriptMode));
	}

	/** Diese Methode kompiliert den gegebenen {@link FEMScript aufbereiteten Quelltext} in ein von dessen {@link FEMScript#mode() Skriptmodus} abhängiges Objekt
	 * und gibt dieses zurück.
	 *
	 * @see #PARSE_VALUE
	 * @see #PARSE_VALUE_LIST
	 * @see #PARSE_FUNCTION
	 * @see #PARSE_FUNCTION_LIST
	 * @see #PARSE_PROXY_MAP
	 * @see #parseScript(String, int)
	 * @param source aufbereiteter Quelltext.
	 * @return Kompiliertes Objekt abhängig vom Skriptmodus.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist oder einen Fehlerbereich vom Typ {@code '!'} enthält. */
	public Object compileScript(final FEMScript source) throws NullPointerException, IllegalArgumentException {
		if (source.contains('!')) throw new IllegalArgumentException();
		return this.compileAsScript(new FEMCompiler().useScript(source));
	}

	/** Diese Methode implementiert {@link #compileScript(FEMScript)} und ist eine Abkürzung für {@code this.compileAsItems(source, itemLimit, itemCompiler)},
	 * wobei {@code itemLimit} und {@code itemCompiler} abhängig vom {@link FEMScript#mode() Skriptmodus} bestückt werden.<br>
	 * Die maximale Elementanzahl {@code itemLimit} ist für {@link #PARSE_VALUE} und {@link #PARSE_FUNCTION} gleich {@code 1} und sonst {@code 0}. Der
	 * {@code itemCompiler} delegiert für {@link #PARSE_VALUE} und {@link #PARSE_VALUE_LIST} an {@link #compileAsValue(FEMCompiler)}, für {@link #PARSE_PROXY_MAP}
	 * an {@link #compileAsProxy(FEMCompiler)} sowie für {@link #PARSE_FUNCTION} und {@link #PARSE_FUNCTION_LIST} an {@link #compileAsFunction(FEMCompiler)}.
	 *
	 * @see #parseAsScript(FEMParser, int)
	 * @param source Kompiler.
	 * @return Kompiliertes Objekt abhängig vom Skriptmodus.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist ider der Skriptmodus nicht unterstützt wird. */
	protected Object compileAsScript(FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		int itemLimit = 1;
		switch (source.script().mode()) {
			case PARSE_VALUE_LIST:
				itemLimit = 0;
			case PARSE_VALUE:
				final List<FEMValue> values = this.compileAsItems(source, itemLimit, new Getter<FEMCompiler, FEMValue>() {

					@Override
					public FEMValue get(FEMCompiler input) {
						return FEMDomain.this.compileAsValue(input);
					}

				});
				return itemLimit != 0 ? values.get(0) : values;
			case PARSE_PROXY_MAP:
				this.compileAsItems(source, 0, new Getter<FEMCompiler, FEMProxy>() {

					@Override
					public FEMProxy get(FEMCompiler input) {
						return FEMDomain.this.compileAsProxy(input);
					}

				});
				return source.proxies();
			case PARSE_FUNCTION_LIST:
				itemLimit = 0;
			case PARSE_FUNCTION:
				final List<FEMFunction> functions = this.compileAsItems(source, itemLimit, new Getter<FEMCompiler, FEMFunction>() {

					@Override
					public FEMFunction get(FEMCompiler input) {
						return FEMDomain.this.compileAsFunction(input);
					}

				});
				return itemLimit != 0 ? functions.get(0) : functions;
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode kompiliert eine mit Semikolon separierten Auflistung von über den gegebenen {@code itemCompiler} kompilierten Elementen und gibt diese
	 * zurück.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param source Kompiler.
	 * @param itemLimit Maximale Anzahl der Elemente in der Auflistung, wobei Werte kleiner oder gleich {@code 0} für eine unbeschränkte Anzahl stehen.
	 * @param itemCompiler {@link Getter} zum Kompilieren eines Elements.
	 * @return Auflistung der Elemente.
	 * @throws NullPointerException Wenn {@code source} bzw. {@code itemCompiler} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected <GItem> List<GItem> compileAsItems(final FEMCompiler source, int itemLimit, final Getter<? super FEMCompiler, ? extends GItem> itemCompiler)
		throws NullPointerException, IllegalArgumentException {
		final List<GItem> result = new ArrayList<>();
		this.compileAsComments(source);
		GItem item = itemCompiler.get(source);
		if (item == null) return result;
		result.add(item);
		while (true) {
			itemLimit--;
			this.compileAsComments(source);
			if (source.isParsed()) return result;
			if ((itemLimit == 0) || (source.symbol() != ';')) throw new IllegalArgumentException();
			source.skip();
			this.compileAsComments(source);
			item = itemCompiler.get(source);
			if (item == null) throw new IllegalArgumentException();
			result.add(item);
		}
	}

	/** Diese Methode kompiliert eine benannte Funktion und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} keine
	 * {@link #compileAsConst(FEMCompiler) Konstante} ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsProxy(FEMParser)
	 * @param source Kompiler.
	 * @return benannte Funktion oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMProxy compileAsProxy(FEMCompiler source) throws IllegalArgumentException {
		final String name = this.compileAsConst(source);
		if (name == null) return null;
		this.compileAsComments(source);
		final FEMHandler handler = this.compileAsHandler(source);
		if (handler == null) throw new IllegalArgumentException();
		final FEMProxy result = source.proxy(name);
		result.set(handler.toFunction());
		return result;
	}

	/** Diese Methode kompiliert eine Wertliste und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '['} ist, wird
	 * {@code null} geliefert.
	 *
	 * @see #parseAsArray(FEMParser)
	 * @param source Kompiler.
	 * @return Wertliste oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMArray compileAsArray(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '[') return null;
		source.skip();
		this.compileAsComments(source);
		if (source.symbol() == ']') {
			source.skip();
			return FEMArray.EMPTY;
		}
		final List<FEMValue> result = new ArrayList<>();
		FEMValue item = this.compileAsValue(source);
		if (item == null) throw new IllegalArgumentException();
		result.add(item);
		while (true) {
			this.compileAsComments(source);
			if (source.symbol() == ']') {
				source.skip();
				return FEMArray.from(result);
			} else if (source.symbol() == ';') {
				source.skip();
				this.compileAsComments(source);
				item = this.compileAsValue(source);
				if (item == null) throw new IllegalArgumentException();
				result.add(item);
			} else throw new IllegalArgumentException();
		}
	}

	/** value, null, illegal */
	/** Diese Methode kompiliert einen Wert und gibt diesen zurück. Sie probiert hierfür {@link #compileAsArray(FEMCompiler) Wertlisten},
	 * {@link #compileAsString(FEMCompiler) Zeichenketten}, {@link #compileAsHandler(FEMCompiler) Funktionszeiger} und {@link #compileAsConst(FEMCompiler)
	 * Konstanten} durch. Wenn hierbei kein Wert ermittelt werden konnte, wird {@code null} geliefert.
	 *
	 * @see #parseAsValue(FEMParser)
	 * @param source Kompiler.
	 * @return Wert oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileAsValue(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		FEMValue result = this.compileAsArray(source);
		if (result != null) return result;
		result = this.compileAsString(source);
		if (result != null) return result;
		result = this.compileAsHandler(source);
		if (result != null) return result;
		final String string = this.compileAsConst(source);
		if (string == null) return null;
		result = this.compileAsValue(source, string);
		if (result != null) return result;
		throw new IllegalArgumentException("Wert '" + string + "' ungültig.");
	}

	/** Diese Methode kompiliert den mit der gegebenen Zeichenkette beschriebenen Wert und gibt dieses zurück. Sie versucht dazu die Zeichenkette als
	 * {@link FEMVoid}, {@link FEMBoolean}, {@link FEMBinary}, {@link FEMDuration}, {@link FEMDatetime}, {@link FEMInteger} und {@link FEMDecimal} zu
	 * interpretieren. Wenn hierbei keine Konstante ermittelt werden konnte, wird {@code null} geliefert.
	 *
	 * @see #parseAsValue(FEMParser)
	 * @param source Kompiler.
	 * @param string Zeichenkette, bspw. einer Konstanten oder eines Objektnamens.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileAsValue(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
		if (string.equals("void")) return FEMVoid.INSTANCE;
		if (string.equals("true")) return FEMBoolean.TRUE;
		if (string.equals("false")) return FEMBoolean.FALSE;
		try {
			if (string.startsWith("0x")) return FEMBinary.from(string);
		} catch (final IllegalArgumentException cause) {
			return null;
		}
		try {
			if (string.startsWith("P") || string.startsWith("-P")) return FEMDuration.from(string);
		} catch (final IllegalArgumentException cause) {
			return null;
		}
		try {
			return FEMInteger.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDecimal.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDatetime.from(string);
		} catch (final IllegalArgumentException cause) {}
		return null;
	}

	/** Diese Methode kompiliert eine Zeichenkette und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '\''} oder
	 * {@code '\"'} ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsString(FEMParser)
	 * @param source Kompiler.
	 * @return Zeichenkette oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileAsString(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		final int symbol = source.symbol();
		if ((symbol != '\"') && (symbol != '\'')) return null;
		final String result = Strings.parseSequence(source.section(), (char)symbol);
		if (result == null) throw new IllegalArgumentException("Zeichenkette '" + source.section() + "' ungültig.");
		source.skip();
		return FEMString.from(result);
	}

	/** Diese Methode kompiliert einen Parameterverweise und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '$'}
	 * ist, wird {@code null} geliefert. Wenn diesem Bereich ein {@link #compileAsIndex(FEMCompiler) Parameterindex} bzw. {@link #compileAsName(FEMCompiler)
	 * Parametername} folgt, wird ein {@link FEMParam} mit dem entsprechenden geliefert. Der Parameterindex für den ersten Parameter ist {@code 1}. Kleinere Werte
	 * sind ungültig. Ein Parametername ist ungültig über {@link FEMCompiler#getParam(String)} dazu kein positiver Index ermittelt werden kann. Wenn dem ersten
	 * Bereich weder ein Parameterindex noch ein Parametername folgt, wird {@link FEMParam#VIEW} geliefert.
	 *
	 * @see #parseAsLocale(FEMParser)
	 * @see FEMParam#VIEW
	 * @see FEMParam#from(int)
	 * @param source Kompiler.
	 * @return Parameterverweise oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileAsLocale(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '$') return null;
		source.skip();
		final String name = this.compileAsName(source);
		if (name != null) {
			final int index = source.getParam(name);
			if (index < 0) throw new IllegalArgumentException("Parametername '" + name + "' ist unbekannt.");
			return FEMParam.from(index);
		}
		final Integer index = this.compileAsIndex(source);
		if (index != null) {
			if (index.intValue() < 1) throw new IllegalArgumentException("Parameterindex '" + index + "' ist ungültig.");
			return FEMParam.from(index.intValue() - 1);
		}
		return FEMParam.VIEW;
	}

	/** Diese Methode kompiliert einen Funktionszeiger und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ
	 * <code>'{'</code> ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsHandler(FEMParser)
	 * @param source Kompiler.
	 * @return Funktionszeiger oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMHandler compileAsHandler(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '{') return null;
		source.skip();
		int count = 0;
		while (true) {
			this.compileAsComments(source);
			if (source.symbol() < 0) throw new IllegalArgumentException();
			final String name = this.compileAsName(source);
			if (name != null) {
				source.putParam(count++, name);
				this.compileAsComments(source);
			}
			if (source.symbol() == ';') {
				if (name == null) throw new IllegalArgumentException();
				source.skip();
			} else if (source.symbol() == ':') {
				source.skip();
				final FEMFunction result = this.compileAsFunction(source);
				if (result == null) throw new IllegalArgumentException();
				this.compileAsComments(source);
				if (source.symbol() != '}') throw new IllegalArgumentException();
				source.skip();
				source.popParams(count);
				return FEMHandler.from(result);
			} else throw new IllegalArgumentException();
		}
	}

	/** Diese Methode kompiliert eine Funktion und gibt diese zurück. Sie probiert hierfür {@link #compileAsArray(FEMCompiler) Wertlisten},
	 * {@link #compileAsString(FEMCompiler) Zeichenketten}, {@link #compileAsHandler(FEMCompiler) Funktionszeiger}, {@link #compileAsLocale(FEMCompiler)
	 * Parameterverweise} und {@link #compileAsConst(FEMCompiler) Konstanten} durch. Wenn hierbei keine Funktion ermittelt werden konnte, wird {@code null}
	 * geliefert.
	 *
	 * @see #parseAsFunction(FEMParser)
	 * @param source Kompiler.
	 * @return Funktion oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileAsFunction(final FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		FEMFunction result = this.compileAsArray(source);
		if (result != null) return result;
		result = this.compileAsString(source);
		if (result != null) return result;
		result = this.compileAsHandler(source);
		if (result != null) return this.compileAsComposite(source, result);
		result = this.compileAsLocale(source);
		if (result != null) return this.compileAsComposite(source, result);
		final String string = this.compileAsConst(source);
		if (string == null) return null;
		result = this.compileAsFunction(source, string);
		if (result != null) return this.compileAsComposite(source, result);
		throw new IllegalArgumentException("Funktion '" + string + "' ungültig.");
	}

	/** Diese Methode kompiliert die mit der gegebenen Zeichenkette beschriebene Funktion und gibt diese zurück. Sie versucht dazu die Zeichenkette über
	 * {@link #compileAsValue(FEMCompiler, String)} in einen konstanten Wert zu übersetzen. Wenn hierbei keine Konstante ermittelt werden konnte, wird ein
	 * {@link FEMCompiler#proxy(String) Platzhalter mit dem gegebenen Namen} geliefert.
	 *
	 * @see #parseAsFunction(FEMParser)
	 * @param source Kompiler.
	 * @param string Zeichenkette, bspw. einer Konstanten oder eines Funktionsnamens.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileAsFunction(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
		final FEMValue result = this.compileAsValue(source, string);
		return result != null ? result : source.proxy(string);
	}

	/** Diese Methode kompiliert eine Parameterliste und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '('} ist,
	 * wird {@code null} geliefert. Die Parameterfunktionen werden hierzu über {@link #compileAsClosure(FEMFunction)} aus den Funktionen abgeleitet, die über
	 * {@link #compileAsFunction(FEMCompiler)} ermittelt wurden.
	 *
	 * @see #parseAsParams(FEMParser)
	 * @param source Kompiler.
	 * @return Parameterliste oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected List<FEMFunction> compileAsParams(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '(') return null;
		source.skip();
		this.compileAsComments(source);
		if (source.symbol() == ')') {
			source.skip();
			return Collections.emptyList();
		}
		FEMFunction item = this.compileAsFunction(source);
		if (item == null) throw new IllegalArgumentException();
		final List<FEMFunction> result = new ArrayList<>();
		result.add(this.compileAsClosure(item));
		while (true) {
			this.compileAsComments(source);
			if (source.symbol() == ')') {
				source.skip();
				return result;
			}
			if (source.symbol() != ';') throw new IllegalArgumentException();
			source.skip();
			this.compileAsComments(source);
			item = this.compileAsFunction(source);
			if (item == null) throw new IllegalArgumentException();
			result.add(this.compileAsClosure(item));
		}
	}

	/** Diese Methode gibt die gegebene Funktion als Parameterfunktion zurück. Ein {@link FEMHandler Funktionszeiger} wird hierbei zu einem {@link ClosureFunction
	 * Funktionszeiger mit Stapalrahmenbindung}. Alle andere Funktionen bleiben unverändert.
	 *
	 * @see #compileAsParams(FEMCompiler)
	 * @see FEMHandler#value()
	 * @see FEMFunction#toClosure()
	 * @param source Funktion.
	 * @return Parameterfunktion. */
	protected FEMFunction compileAsClosure(final FEMFunction source) {
		return source instanceof FEMHandler ? ((FEMHandler)source).value().toClosure() : source;
	}

	/** Diese Methode ist eine Abkürzung für
	 * {@code this.compileAsComposite(source, result, (result instanceof FEMHandler) || (result instanceof ClosureFunction))}.
	 *
	 * @param source Kompiler.
	 * @param result aufzurufende Funktion.
	 * @return Funktionsaufruf oder {@code result}.
	 * @throws NullPointerException Wenn {@code source} bzw. {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileAsComposite(FEMCompiler source, FEMFunction result) throws NullPointerException, IllegalArgumentException {
		return this.compileAsComposite(source, result, (result instanceof FEMHandler) || (result instanceof ClosureFunction));
	}

	/** Diese Methode kompiliert den Aufruf der gegebenen Funktion mit den aus {@link #compileAsParams(FEMCompiler) Parameterlisten} stammenden
	 * Parameterfunktionen und gibt diesen zurück. Dazu werden beliebig viele {@link #compileAsParams(FEMCompiler) Parameterlisten} kompiliert und alle außer der
	 * ersten Parameterliste über {@link FEMFunction#concat(List)} mit der aufzurufenden Funktion verkettet. Die Art der Anbindung für die erste Parameterliste
	 * wird über die Verkettungsoption {@code concat} bestimmt. Diese sollte {@code true} sein, wenn die gegebene Funktion ein {@link FEMHandler Funktionszeiger},
	 * ein {@link ClosureFunction Funktionszeiger mit Stapalrahmenbindung} oder eine Funktion ist, die erst bei ihrer Auswertung einen Funktionszeiger liefert.
	 *
	 * @param source Kompiler.
	 * @param result aufzurufende Funktion.
	 * @param concat {@code true}, wenn die erste Parameterliste über {@link FEMFunction#concat(List)} angebunden werden soll.<br>
	 *        {@code false}, wenn sie über {@link FEMFunction#compose(List)} angebunden werden soll.
	 * @return Funktionsaufruf oder {@code result}.
	 * @throws NullPointerException Wenn {@code source} bzw. {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileAsComposite(final FEMCompiler source, FEMFunction result, boolean concat) throws NullPointerException, IllegalArgumentException {
		List<FEMFunction> params = this.compileAsParams(source);
		if (params == null) return Objects.assertNotNull(result);
		result = concat ? result.concat(params) : result.compose(params);
		while (true) {
			params = this.compileAsParams(source);
			if (params == null) return result;
			result = result.concat(params);
			this.compileAsComments(source);
		}
	}

	/** Diese Methode kompiliert die Zeichenkette einer Konatenten und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ
	 * {@code '?'} oder {@code '<'} ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsConst(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected String compileAsConst(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		final int symbol = source.symbol();
		if (symbol == '?') {
			final String result = source.section();
			source.skip();
			return result;
		} else if (symbol == '<') {
			final String result = Strings.parseSequence(source.section(), '<', '/', '>');
			if (result == null) throw new IllegalArgumentException("Konstante '" + source.section() + "' ungültig.");
			source.skip();
			return result;
		} else return null;
	}

	/** Diese Methode kompiliert einen Parameternamen und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '~'}
	 * ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsName(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected String compileAsName(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '~') return null;
		final String result = source.section();
		source.skip();
		return result;
	}

	/** Diese Methode kompiliert einen Parameterindex und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '#'}
	 * ist, wird {@code null} geliefert.
	 *
	 * @see #parseAsIndex(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected Integer compileAsIndex(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '#') return null;
		final Integer result = Integer.valueOf(source.section());
		source.skip();
		return result;
	}

	/** Diese Methode überspringt Kommentare.
	 *
	 * @see #parseAsComments(FEMParser)
	 * @param source Kompiler.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	protected void compileAsComments(final FEMCompiler source) throws NullPointerException {
		for (int symbol = source.symbol(); symbol == '/'; symbol = source.skip()) {}
	}

}