package bee.creative.fem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import bee.creative.bind.Getter;
import bee.creative.bind.Setter;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMFunction.FrameFunction;
import bee.creative.fem.FEMFunction.FutureFunction;
import bee.creative.fem.FEMFunction.TraceFunction;
import bee.creative.fem.FEMScript.Token;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Strings;
import bee.creative.util.Filter;

/** Diese Klasse implementiert domänenspezifische Parse-, Formatierungs- und Kompilationsmethoden, welche der Übersetzung von Zeichenketten, aufbereitete
 * Quelltexten und Funktionen ineinander dienen.
 * <p>
 * Die {@code parse*}-Methoden zum Parsen einer {@link FEMParser#source() Zeichenkette} erkennen darin Bedeutung tragende {@link FEMScript.Token Bereiche} und
 * {@link FEMParser#putToken(Token) erfassen} diese falls nötig im gegebenen {@link FEMParser}. Die {@code format*}-Methoden zum Formatieren eines gegebenen
 * Objekts erzeugen dagegen die Bedeutung tragenden Bereiche der Textdarstellung und {@link FEMFormatter#putToken(Object) erfassen} diese im gegebenen
 * {@link FEMFormatter}. Die {@code compile*}-Methoden zum Kompilieren {@link FEMScript aufbereiteter Quelltexte} übersetzen schließlich erkannte Bedeutung
 * tragende {@link FEMScript.Token Bereiche} in Funktionen.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain extends BaseObject {

	/** Diese Klasse implementiert eine {@link FEMDomain}, welche das Kompilieren nativer Konstanten und Methoden unterstützt.
	 *
	 * @see FEMNative
	 * @see FEMReflection */
	public static class NativeDomain extends FEMDomain {

		@Override
		protected FEMValue compileValue(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
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
		protected FEMValue compileString(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
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
		protected FEMFunction compileFunction(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
			final FEMValue result = this.compileValue(source, string);
			if (result != null) return result;
			try {
				return FEMReflection.from(string);
			} catch (final IllegalArgumentException cause) {}
			return source.proxy(string);
		}

	}

	/** Dieses Feld speichert die native {@link FEMDomain} */
	public static final NativeDomain NATIVE = new NativeDomain();

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

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer mit Semikolon separierten Auflistung von über den gegebenen {@code itemParser} erkannten
	 * Elementen und gibt nur dann {@code true} zurück, wenn diese Auflistung am ersten Element erkannt wurden. Das Symbol {@code ';'} wird direkt als Typ des
	 * erfassten Bereichs eingesetzt. Zwischen all diesen Komponenten können beliebig viele {@link #parseComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @param itemLimit Maximale Anzahl der Elemente in der Auflistung, wobei Werte kleiner oder gleich {@code 0} für eine unbeschränkte Anzahl stehen.
	 * @param itemParser {@link Filter} zum Erkennen, Parsen und Erfassen der {@link Token Bereiche} eines Elements.
	 * @return {@code true}, wenn die Auflistung erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code itemParser} {@code null} ist. */
	protected boolean parseItems(final FEMParser target, int itemLimit, final Filter<? super FEMParser> itemParser) throws NullPointerException {
		this.parseComments(target);
		if (!itemParser.accept(target)) return false;
		while (true) {
			--itemLimit;
			this.parseComments(target);
			if (target.isParsed()) return true;
			if ((itemLimit == 0) || (target.symbol() != ';')) return this.parseError(target);
			target.putToken(';');
			target.skip();
			this.parseComments(target);
			if (!itemParser.accept(target)) return this.parseError(target);
		}
	}

	/** Diese Methode erfassten die verbleibende Eingabe als {@link Token Fehlerbereich} mit dem Typ {@code '!'} und gibt {@code true} zurück.
	 *
	 * @param target Parser.
	 * @return {@code true}.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseError(final FEMParser target) throws NullPointerException {
		final int index = target.index(), length = target.length();
		target.putToken('!', index, length - index);
		target.seek(length);
		return true;
	}

	/** Diese Methode sollte den {@link FEMParser#tokens() Bereich} an der gegebenen Position als {@link Token Fehlerbereich} mit dem Typ {@code '!'} markieren,
	 * d.h. {@code target.setToken(index, '!')} aufrufe, wenn die Startbereiche unvollständiger {@link #parseArray(FEMParser) Wertlisten},
	 * {@link #parseParams(FEMParser) Parameterlisten} bzw. {@link #parseHandler(FEMParser) Funktionszeiger} derart gekennzeichnet werden sollen.
	 *
	 * @param target Parser.
	 * @param index Position des
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseError(final FEMParser target, final int index) {
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer {@link FEMProxy benannten Funktion} und gibt nur dann {@code true} zurück, wenn diese an
	 * ihrer {@link #parseConst(FEMParser) Bezeichnung} erkannt wurde. Der Bezeichnung folg die als {@link #parseHandler(FEMParser) Funktionszeiger} angegebene
	 * Funktion, wobei zwischen diesen beiden Komponenten beliebig viele {@link #parseComments(FEMParser) Kommentare/Leerraum} stehen können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die benannten Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseProxy(final FEMParser target) throws NullPointerException {
		if (!this.parseConst(target)) return false;
		this.parseComments(target);
		if (!this.parseHandler(target)) return this.parseError(target);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Konstante und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür in spitze Klammen eingeschlossene und mit Schrägstrich {@link #parseSequence(FEMParser, char, char, char) maskierte} Zeichenkette sowie unmaskierte
	 * Bezeichner durch. Ein unmaskierter Bezeichner ist eine Zeichenkette ohne Leerraum, Schrägstrich, Semikolon sowie ohne runde, eckige oder geschweifte
	 * Klammer und nutzen den Bereichstyp {@code '?'}.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Konstante erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseConst(final FEMParser target) throws NullPointerException {
		if (this.parseSequence(target, '<', '<', '>')) return true;
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
				case '<':
				case '>':
				break LOOP;
			}
			symbol = target.skip();
		}
		if (target.index() == offset) return false;
		target.putToken('?', offset);
		return true;
	}

	/** Diese Methode implementiert {@link #parseScript(String, int)} und ist eine Abkürzung für {@code this.parseItems(target, itemLimit, itemParser)}, wobei
	 * {@code itemLimit} und {@code itemParser} abhängig vom gegebenen {@link FEMScript#mode() Skriptmodus} bestückt werden. Die Implementation in
	 * {@link FEMDomain} erfasst für alle nicht unterstützten Skriptmodus einen {@link #parseError(FEMParser) Fehlerbereich}. Die maximale Elementanzahl
	 * {@code itemLimit} ist für {@link #PARSE_VALUE} und {@link #PARSE_FUNCTION} gleich {@code 1} und sonst {@code 0}. Der {@code itemParser} delegiert für
	 * {@link #PARSE_VALUE} und {@link #PARSE_VALUE_LIST} an {@link #parseValue(FEMParser)}, für {@link #PARSE_PROXY_MAP} an {@link #parseProxy(FEMParser)} sowie
	 * für {@link #PARSE_FUNCTION} und {@link #PARSE_FUNCTION_LIST} an {@link #parseFunction(FEMParser)}.
	 *
	 * @see #parseItems(FEMParser, int, Filter)
	 * @param target Parser.
	 * @param scriptMode Skriptmodus.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseScript(final FEMParser target, final int scriptMode) throws NullPointerException {
		int itemLimit = 1;
		Filter<FEMParser> itemParser;
		switch (scriptMode) {
			default:
				this.parseError(target);
				return;
			case PARSE_VALUE_LIST:
				itemLimit = 0;
			case PARSE_VALUE:
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(final FEMParser item) {
						return FEMDomain.this.parseValue(item);
					}

				};
			break;
			case PARSE_PROXY_MAP:
				itemLimit = 0;
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(final FEMParser item) {
						return FEMDomain.this.parseProxy(item);
					}

				};
			break;
			case PARSE_FUNCTION_LIST:
				itemLimit = 0;
			case PARSE_FUNCTION:
				itemParser = new Filter<FEMParser>() {

					@Override
					public boolean accept(final FEMParser item) {
						return FEMDomain.this.parseFunction(item);
					}

				};
			break;
		}
		this.parseItems(target, itemLimit, itemParser);
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Wertliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden eckigen
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseValue(FEMParser) Werte}. Die Wertliste endet mit der
	 * schließenden eckigen Klammer. Die Symbole {@code '['}, {@code ';'} und {@code ']'} werden direkt als Typ der erfassten Bereiche eingesetzt. Wenn die
	 * Wertliste fehlerhaft ist, wird für die öffnende eckige Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können beliebig
	 * viele {@link #parseComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Wertliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseArray(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '[') return false;
		final int openIndex = target.putToken('[');
		target.skip();
		this.parseComments(target);
		if (target.symbol() == ']') {
			target.putToken(']');
			target.skip();
			return true;
		} else if (target.isParsed() || !this.parseValue(target)) {
			this.parseError(target, openIndex);
			return this.parseError(target);
		} else {
			while (true) {
				this.parseComments(target);
				if (target.symbol() == ']') {
					target.putToken(']');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					this.parseError(target, openIndex);
					return this.parseError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseComments(target);
					if (!this.parseValue(target)) {
						this.parseError(target, openIndex);
						return this.parseError(target);
					}
				}
			}
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Werts als Element einer {@link #parseArray(FEMParser) Wertliste} und gibt nur dann
	 * {@code true} zurück, wenn dieser erkannt wurde. Sie probiert hierfür {@link #parseArray(FEMParser) Wertlisten}, {@link #parseString(FEMParser)
	 * Zeichenketten}, {@link #parseHandler(FEMParser) Funktionszeiger} und {@link #parseConst(FEMParser) Konstanten} durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Wert erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseValue(final FEMParser target) throws NullPointerException {
		return this.parseArray(target) || this.parseString(target) || this.parseHandler(target) || this.parseConst(target);
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Zeichenkette und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür mit einfachen und doppelten Anführungszeichen {@link #parseSequence(FEMParser, char) maskierte} Zeichenketten durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Zeichenkette erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseString(final FEMParser target) throws NullPointerException {
		return this.parseSequence(target, '\'') || this.parseSequence(target, '\"');
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Funktionszeigers und gibt nur dann {@code true} zurück, wenn dieser an der öffnenden
	 * geschweiften Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseName(FEMParser) Parameternamen}. Auf
	 * diese folgen dann ein Doppelpunkt, die {@link #parseFunction(FEMParser) Funktion} sowie die schließenden geschweifte Klammer. Die Symbole <code>'{'</code>,
	 * {@code ';'}, {@code ':'} und <code>'}'</code> werden direkt als Typ der erfassten Bereiche eingesetzt. Wenn de Funktionszeiger fehlerhaft ist, wird für die
	 * öffnende geschweifte Klammer sowie den Doppelpunkt der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können beliebig viele
	 * {@link #parseComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Funktionszeiger erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseHandler(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '{') return false;
		final int openIndex = target.putToken('{'), closeIndex;
		target.skip();
		this.parseComments(target);
		if (target.symbol() == ':') {
			closeIndex = target.putToken(':');
			target.skip();
		} else if (target.isParsed() || !this.parseName(target)) {
			this.parseError(target, openIndex);
			return this.parseError(target);
		} else {
			while (true) {
				this.parseComments(target);
				if (target.symbol() == ':') {
					closeIndex = target.putToken(':');
					target.skip();
					break;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					this.parseError(target, openIndex);
					return this.parseError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseComments(target);
					if (!this.parseName(target)) {
						this.parseError(target, openIndex);
						return this.parseError(target);
					}
				}
			}
		}
		this.parseComments(target);
		if (target.isParsed() || !this.parseFunction(target)) {
			this.parseError(target, openIndex);
			this.parseError(target, closeIndex);
			return this.parseError(target);
		}
		this.parseComments(target);
		if (target.isParsed() || (target.symbol() != '}')) {
			this.parseError(target, openIndex);
			this.parseError(target, closeIndex);
			return this.parseError(target);
		}
		target.putToken('}');
		target.skip();
		return true;
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Funktion und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür {@link #parseArray(FEMParser) Wertlisten}, {@link #parseString(FEMParser) Zeichenketten}, {@link #parseLocale(FEMParser) Parameterverweise},
	 * {@link #parseHandler(FEMParser) Funktionszeiger} und {@link #parseConst(FEMParser) Konstanten} durch, wobei die letzten drei noch von beliebig viel
	 * {@link #parseComments(FEMParser) Leerraum/Kommentaren} und {@link #parseParams(FEMParser) Parameterlisten} gefolgt werden können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseFunction(final FEMParser target) throws NullPointerException {
		if (this.parseArray(target) || this.parseString(target)) return true;
		if (!this.parseLocale(target) && !this.parseHandler(target) && !this.parseConst(target)) return false;
		while (true) {
			this.parseComments(target);
			if (!this.parseParams(target)) return true;
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Parameterliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden runden
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #parseFunction(FEMParser) Parameterfunktionen}. Die
	 * Parameterliste endet mit der schließenden runden Klammer. Die Symbole {@code '('}, {@code ';'} und {@code ')'} werden direkt als Typ der erfassten Bereiche
	 * eingesetzt. Wenn die Wertliste fehlerhaft ist, wird für die öffnende runde Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten
	 * können beliebig viele {@link #parseComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Parameterliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseParams(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '(') return false;
		final int openIndex = target.putToken('(');
		target.skip();
		this.parseComments(target);
		if (target.symbol() == ')') {
			target.putToken(')');
			target.skip();
			return true;
		} else if (target.isParsed() || !this.parseFunction(target)) {
			this.parseError(target, openIndex);
			return this.parseError(target);
		} else {
			while (true) {
				this.parseComments(target);
				if (target.symbol() == ')') {
					target.putToken(')');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					this.parseError(target, openIndex);
					return this.parseError(target);
				} else {
					target.putToken(';');
					target.skip();
					this.parseComments(target);
					if (!this.parseFunction(target)) {
						this.parseError(target, openIndex);
						return this.parseError(target);
					}
				}
			}
		}
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} eines Parameterindexes und gibt nur dann {@code true} zurück, wenn dieser erkannt wurde. Sie
	 * sucht dazu eine nicht leere Zeichenkette aus dezimalen Ziffern und nutzt das Symbole {@code '#'} als Typ des erfassten Bereichs.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Parameterindex erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseIndex(final FEMParser target) throws NullPointerException {
		final int offset = target.index();
		for (int symbol = target.symbol(); ('0' <= symbol) && (symbol <= '9'); symbol = target.skip()) {}
		if (target.index() == offset) return false;
		target.putToken('#', offset);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} eines Parameternamen und gibt nur dann {@code true} zurück, wenn dieser erkannt wurde. Sie sucht
	 * dazu eine nicht leere Zeichenkette ohne Leerraum, Schrägstrich, Doppelpunkt, Semikolon sowie ohne Klammer. Das Symbol {@code '~'} wird als Typ des
	 * erfassten Bereichs eingesetzt.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Parametername erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseName(final FEMParser target) throws NullPointerException {
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
				case '<':
				case '>':
				break LOOP;
			}
			symbol = target.skip();
		}
		if (target.index() == offset) return false;
		target.putToken('~', offset);
		return true;
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Parameterverweises und gibt nur dann {@code true} zurück, wenn dieser am Dollarzeichen
	 * erkannt wurde. Diesem Zeichen kann ein {@link #parseIndex(FEMParser) Parameterindex} oder ein {@link #parseName(FEMParser) Parametername} folgen. Das
	 * Symbol {@code '$'} wird direkt als Typ des erfassten Bereichs eingesetzt.
	 *
	 * @param source Parser.
	 * @return {@code true}, wenn der Parameterverweis erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseLocale(final FEMParser source) throws NullPointerException {
		if (source.symbol() != '$') return false;
		source.putToken('$');
		source.skip();
		if (this.parseIndex(source)) return true;
		this.parseName(source);
		return true;
	}

	/** Diese Methode parst den {@link Token Bereich} von Leerraum, erfasst diese jedoch nicht. Zum Leerraum zählen alle Symbole kleiner oder gleich {@code ' '}.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseSpace(final FEMParser source) throws NullPointerException {
		for (int symbol = source.symbol(); (symbol >= 0) && (symbol <= ' '); symbol = source.skip()) {}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} von Kommentaren. Ein Kommentar wird als mit Schrägstrich
	 * {@link #parseSequence(FEMParser, char)} maskierte} Zeichenkette erkannt und erfasst. Vor und nach einem Kommentar kann beliebig viel
	 * {@link #parseSpace(FEMParser) Leerraum} stehen.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void parseComments(final FEMParser source) throws NullPointerException {
		do {
			this.parseSpace(source);
		} while (this.parseSequence(source, '/'));
	}

	/** Diese Methode ist eine Abkürzung für {@code this.parseSequence(source, maskSymbol, maskSymbol, maskSymbol)}.
	 *
	 * @see #parseSequence(FEMParser, char, char, char)
	 * @param source Parser.
	 * @param maskSymbol Maskierungszeichen.
	 * @return {@code true}, wenn die Sequenz erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseSequence(final FEMParser source, final char maskSymbol) throws NullPointerException {
		return this.parseSequence(source, maskSymbol, maskSymbol, maskSymbol);
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Zeichenkette analog zu {@link Strings#parseSequence(CharSequence, char, char, char)} und
	 * gibt nur dann {@code true} zurück, wenn diese am {@code openSymbol} erkannt wurde. Für eine fehlerfreie Sequenz wird {@code openSymbol} als Typ des
	 * Bereichs eingesetzt. Eine fehlerhafte Sequenz geht dagegen bis zum Ende der Eingabe und wird mit dem Bereichstyp {@code '!'} erfasst.
	 *
	 * @param target Parser.
	 * @param openSymbol Erstes Symbol der Zeichenkette.
	 * @param maskSymbol Symbol zur Maskierungszeichen.
	 * @param closeSymbol Letztes Symbol der Zeichenkette.
	 * @return {@code true}, wenn die Sequenz erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean parseSequence(final FEMParser target, final char openSymbol, final char maskSymbol, final char closeSymbol) throws NullPointerException {
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

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link FEMScript aufbereiteten Quelltext} und gibt diesen zurück. Sie erzeugt dazu einen
	 * {@link FEMParser} und delegiert diesen zusammen mit dem gegebenen Skriptmodus an {@link #parseScript(FEMParser, int)}.
	 *
	 * @see #PARSE_VALUE
	 * @see #PARSE_VALUE_LIST
	 * @see #PARSE_FUNCTION
	 * @see #PARSE_FUNCTION_LIST
	 * @see #PARSE_PROXY_MAP
	 * @see #parseScript(FEMParser, int)
	 * @param source Zeichenkette, die geparst werden soll.
	 * @param scriptMode Modus für {@link FEMScript#mode()}.
	 * @return aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMScript parseScript(final String source, final int scriptMode) throws NullPointerException {
		final FEMParser parser = new FEMParser().useSource(source);
		this.parseScript(parser, scriptMode);
		return FEMScript.from(scriptMode, source, parser.tokens());
	}

	/** Diese Methode parst die als maskierte Zeichenkette gegebene Konstante und gibt diese ohne Maskierung zurück. Sie realisiert damit die Umkehroperation von
	 * {@link #formatConst(String)}.
	 *
	 * @param string Zeichenkette.
	 * @return gegebene bzw. geparste Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String parseConst(final String string) throws NullPointerException {
		final String result = Strings.parseSequence(string, '<', '<', '>');
		return result != null ? result : string;
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltextes.
	 *
	 * @param target Formatierer.
	 * @param script aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code script} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code script} nicht formatiert werden kann. */
	protected void formatScript(final FEMFormatter target, final FEMScript script) throws NullPointerException, IllegalArgumentException {
		this.formatScript(target, new FEMCompiler().useScript(script));
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltexts.
	 *
	 * @param target Formatierer.
	 * @param source Parser zum aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void formatScript(final FEMFormatter target, final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		while (true) {
			this.formatScript(target, source, false);
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
	protected void formatScript(final FEMFormatter target, final FEMCompiler source, final boolean simpleSpace)
		throws NullPointerException, IllegalArgumentException {
		boolean indent = false;
		while (true) {
			final String string = source.section();
			final int symbol = source.symbol();
			source.skip();
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
	 * {@link #formatConst(String)}.
	 *
	 * @param target Formatierer.
	 * @param source Bezeichnung.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void formatConst(final FEMFormatter target, final String source) throws NullPointerException {
		target.putToken(this.formatConst(source));
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

	/** Diese Methode formatiert und erfasst die Textdarstellung der gegebene Funktion. Für eine {@link TraceFunction}, eine {@link FrameFunction}, eine
	 * {@link FutureFunction} oder eine {@link ClosureFunction} wird die Textdarstellung ihrer referenzierten Funktion mit dieser Methode erfasst. Bei einer
	 * {@link ConcatFunction} oder einer {@link CompositeFunction} werden die Textdarstellungen der aufzurufenden Funktion mit dieser Methode sowie die der
	 * Parameterliste mit {@link #formatParams(FEMFormatter, Iterable)} erfasst. Bei einem {@link FEMProxy} wird dessen {@link FEMProxy#name() Name} über
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
		} else if (source instanceof ConcatFunction) {
			final ConcatFunction concatFunction = (ConcatFunction)source;
			this.formatFunction(target, concatFunction.function);
			this.formatParams(target, Arrays.asList(concatFunction.params));
		} else if (source instanceof CompositeFunction) {
			final CompositeFunction value = (CompositeFunction)source;
			this.formatFunction(target, value.function);
			this.formatParams(target, Arrays.asList(value.params));
		} else if (source instanceof ClosureFunction) {
			final ClosureFunction closureFunction = (ClosureFunction)source;
			this.formatFunction(target, closureFunction.function);
		} else if (source instanceof FrameFunction) {
			final FrameFunction frameFunction = (FrameFunction)source;
			this.formatFunction(target, frameFunction.function);
		} else if (source instanceof FutureFunction) {
			final FutureFunction futureFunction = (FutureFunction)source;
			this.formatFunction(target, futureFunction.function);
		} else if (source instanceof TraceFunction) {
			final TraceFunction traceFunction = (TraceFunction)source;
			this.formatFunction(target, traceFunction.function);
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

	/** Diese Methode ist eine Abkürzung für {@link #formatConst(String, boolean) this.formatConst(name, false)}.
	 *
	 * @param string Zeichenkette.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatConst(final String string) throws NullPointerException {
		return this.formatConst(string, false);
	}

	/** Diese Methode formatiert die als Zeichenkette gegebene Konstante und gibt sie falls nötig mit Maskierung als formatierte Zeichenkette zurück. Die
	 * Maskierung ist notwendig, wenn {@code forceMask} dies anzeigt oder wenn die Zeichenkette ein {@link #formatConstCheck(String) zu maskierendes Zeichen
	 * enthält}. Die Maskierung erfolgt über {@link Strings#formatSequence(CharSequence, char, char, char) Strings.formatSequence(string, '<', '<', '>')}. Wenn
	 * die Maskierung unnötig ist, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param forceMask {@code true}, wenn die Maskierung notwendig ist.
	 * @return gegebene bzw. formateirte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public String formatConst(final String string, final boolean forceMask) throws NullPointerException {
		return forceMask || this.formatConstCheck(string) ? Strings.formatSequence(string, '<', '<', '>') : Objects.notNull(string);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebene Zeichenkette ein zu maskierendes Steuerzeichen enthält, bspw. ein Leerzeichen,
	 * Dollarzeichen, Semikolon, Schrägstrich, Apostroph oder eine runde, spitze, eckige bzw. geschweifte Klammer.
	 *
	 * @param string Zeichenkette.
	 * @return {@code true}, wenn die Zeichenkette Steuerzeichen enthält.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	protected boolean formatConstCheck(final String string) throws NullPointerException {
		for (int i = string.length(); i != 0;) {
			final char symbol = string.charAt(--i);
			if (symbol <= ' ') return true;
			switch (symbol) {
				case '\'':
				case '"':
				case ';':
				case '$':
				case '/':
				case '{':
				case '}':
				case '(':
				case ')':
				case '[':
				case ']':
				case '<':
				case '>':
					return true;
			}
		}
		return false;
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
		return this.compileScript(new FEMCompiler().useScript(source));
	}

	/** Diese Methode implementiert {@link #compileScript(FEMScript)} und ist eine Abkürzung für {@code this.compileAsItems(source, itemLimit, itemCompiler)},
	 * wobei {@code itemLimit} und {@code itemCompiler} abhängig vom {@link FEMScript#mode() Skriptmodus} bestückt werden. Die maximale Elementanzahl
	 * {@code itemLimit} ist für {@link #PARSE_VALUE} und {@link #PARSE_FUNCTION} gleich {@code 1} und sonst {@code 0}. Der {@code itemCompiler} delegiert für
	 * {@link #PARSE_VALUE} und {@link #PARSE_VALUE_LIST} an {@link #compileValue(FEMCompiler)}, für {@link #PARSE_PROXY_MAP} an
	 * {@link #compileProxy(FEMCompiler)} sowie für {@link #PARSE_FUNCTION} und {@link #PARSE_FUNCTION_LIST} an {@link #compileFunction(FEMCompiler)}.
	 *
	 * @see #parseScript(FEMParser, int)
	 * @param source Kompiler.
	 * @return Kompiliertes Objekt abhängig vom Skriptmodus.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist ider der Skriptmodus nicht unterstützt wird. */
	public Object compileScript(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		int itemLimit = 1;
		switch (source.script().mode()) {
			case PARSE_VALUE_LIST:
				itemLimit = 0;
			case PARSE_VALUE:
				final List<FEMValue> values = this.compileItems(source, itemLimit, new Getter<FEMCompiler, FEMValue>() {

					@Override
					public FEMValue get(final FEMCompiler input) {
						return FEMDomain.this.compileValue(input);
					}

				});
				return itemLimit != 0 ? values.get(0) : values;
			case PARSE_PROXY_MAP:
				this.compileItems(source, 0, new Getter<FEMCompiler, FEMProxy>() {

					@Override
					public FEMProxy get(final FEMCompiler input) {
						return FEMDomain.this.compileProxy(input);
					}

				});
				return source.proxies();
			case PARSE_FUNCTION_LIST:
				itemLimit = 0;
			case PARSE_FUNCTION:
				final List<FEMFunction> functions = this.compileItems(source, itemLimit, new Getter<FEMCompiler, FEMFunction>() {

					@Override
					public FEMFunction get(final FEMCompiler input) {
						return FEMDomain.this.compileFunction(input);
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
	protected <GItem> List<GItem> compileItems(final FEMCompiler source, int itemLimit, final Getter<? super FEMCompiler, ? extends GItem> itemCompiler)
		throws NullPointerException, IllegalArgumentException {
		final List<GItem> result = new ArrayList<>();
		this.compileComments(source);
		GItem item = itemCompiler.get(source);
		if (item == null) return result;
		result.add(item);
		while (true) {
			itemLimit--;
			this.compileComments(source);
			if (source.isParsed()) return result;
			if ((itemLimit == 0) || (source.symbol() != ';')) throw new IllegalArgumentException();
			source.skip();
			this.compileComments(source);
			item = itemCompiler.get(source);
			if (item == null) throw new IllegalArgumentException();
			result.add(item);
		}
	}

	/** Diese Methode kompiliert eine benannte Funktion und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} keine
	 * {@link #compileConst(FEMCompiler) Konstante} ist, wird {@code null} geliefert.
	 *
	 * @see #parseProxy(FEMParser)
	 * @param source Kompiler.
	 * @return benannte Funktion oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMProxy compileProxy(final FEMCompiler source) throws IllegalArgumentException {
		final String name = this.compileConst(source);
		if (name == null) return null;
		this.compileComments(source);
		final FEMHandler handler = this.compileHandler(source);
		if (handler == null) throw new IllegalArgumentException();
		final FEMProxy result = source.proxy(name);
		result.set(handler.toFunction());
		return result;
	}

	/** Diese Methode kompiliert eine Wertliste und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '['} ist, wird
	 * {@code null} geliefert.
	 *
	 * @see #parseArray(FEMParser)
	 * @param source Kompiler.
	 * @return Wertliste oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMArray compileArray(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '[') return null;
		source.skip();
		this.compileComments(source);
		if (source.symbol() == ']') {
			source.skip();
			return FEMArray.EMPTY;
		}
		final List<FEMValue> result = new ArrayList<>();
		FEMValue item = this.compileValue(source);
		if (item == null) throw new IllegalArgumentException();
		result.add(item);
		while (true) {
			this.compileComments(source);
			if (source.symbol() == ']') {
				source.skip();
				return FEMArray.from(result);
			} else if (source.symbol() == ';') {
				source.skip();
				this.compileComments(source);
				item = this.compileValue(source);
				if (item == null) throw new IllegalArgumentException();
				result.add(item);
			} else throw new IllegalArgumentException();
		}
	}

	/** value, null, illegal */
	/** Diese Methode kompiliert einen Wert und gibt diesen zurück. Sie probiert hierfür {@link #compileArray(FEMCompiler) Wertlisten},
	 * {@link #compileString(FEMCompiler) Zeichenketten}, {@link #compileHandler(FEMCompiler) Funktionszeiger} und {@link #compileConst(FEMCompiler) Konstanten}
	 * durch. Wenn hierbei kein Wert ermittelt werden konnte, wird {@code null} geliefert.
	 *
	 * @see #parseValue(FEMParser)
	 * @param source Kompiler.
	 * @return Wert oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileValue(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		FEMValue result = this.compileArray(source);
		if (result != null) return result;
		result = this.compileString(source);
		if (result != null) return result;
		result = this.compileHandler(source);
		if (result != null) return result;
		final String string = this.compileConst(source);
		if (string == null) return null;
		result = this.compileValue(source, string);
		if (result != null) return result;
		throw new IllegalArgumentException("Wert '" + string + "' ungültig.");
	}

	/** Diese Methode kompiliert den mit der gegebenen Zeichenkette beschriebenen Wert und gibt dieses zurück. Sie versucht dazu die Zeichenkette als
	 * {@link FEMVoid}, {@link FEMBoolean}, {@link FEMBinary}, {@link FEMDuration}, {@link FEMDatetime}, {@link FEMInteger} und {@link FEMDecimal} zu
	 * interpretieren. Wenn hierbei keine Konstante ermittelt werden konnte, wird {@code null} geliefert.
	 *
	 * @see #parseValue(FEMParser)
	 * @param source Kompiler.
	 * @param string Zeichenkette, bspw. einer Konstanten oder eines Objektnamens.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileValue(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
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
	 * @see #parseString(FEMParser)
	 * @param source Kompiler.
	 * @return Zeichenkette oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMValue compileString(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		final int symbol = source.symbol();
		if ((symbol != '\"') && (symbol != '\'')) return null;
		final String result = Strings.parseSequence(source.section(), (char)symbol);
		if (result == null) throw new IllegalArgumentException("Zeichenkette '" + source.section() + "' ungültig.");
		source.skip();
		return FEMString.from(result);
	}

	/** Diese Methode kompiliert einen Parameterverweise und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '$'}
	 * ist, wird {@code null} geliefert. Wenn diesem Bereich ein {@link #compileIndex(FEMCompiler) Parameterindex} bzw. {@link #compileName(FEMCompiler)
	 * Parametername} folgt, wird ein {@link FEMParam} mit dem entsprechenden geliefert. Der Parameterindex für den ersten Parameter ist {@code 1}. Kleinere Werte
	 * sind ungültig. Ein Parametername ist ungültig über {@link FEMCompiler#getParam(String)} dazu kein positiver Index ermittelt werden kann. Wenn dem ersten
	 * Bereich weder ein Parameterindex noch ein Parametername folgt, wird {@link FEMFrame#FUNCTION} geliefert.
	 *
	 * @see #parseLocale(FEMParser)
	 * @see FEMFrame#FUNCTION
	 * @see FEMParam#from(int)
	 * @param source Kompiler.
	 * @return Parameterverweise oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileLocale(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '$') return null;
		source.skip();
		final String name = this.compileName(source);
		if (name != null) {
			final int index = source.getParam(name);
			if (index < 0) throw new IllegalArgumentException("Parametername '" + name + "' ist unbekannt.");
			return FEMParam.from(index);
		}
		final Integer index = this.compileIndex(source);
		if (index != null) {
			if (index.intValue() < 1) throw new IllegalArgumentException("Parameterindex '" + index + "' ist ungültig.");
			return FEMParam.from(index.intValue() - 1);
		}
		return FEMFrame.FUNCTION;
	}

	/** Diese Methode kompiliert einen Funktionszeiger und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ
	 * <code>'{'</code> ist, wird {@code null} geliefert.
	 *
	 * @see #parseHandler(FEMParser)
	 * @param source Kompiler.
	 * @return Funktionszeiger oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMHandler compileHandler(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '{') return null;
		source.skip();
		int count = 0;
		while (true) {
			this.compileComments(source);
			if (source.symbol() < 0) throw new IllegalArgumentException();
			final String name = this.compileName(source);
			if (name != null) {
				source.putParam(count++, name);
				this.compileComments(source);
			}
			if (source.symbol() == ';') {
				if (name == null) throw new IllegalArgumentException();
				source.skip();
			} else if (source.symbol() == ':') {
				source.skip();
				final FEMFunction result = this.compileFunction(source);
				if (result == null) throw new IllegalArgumentException();
				this.compileComments(source);
				if (source.symbol() != '}') throw new IllegalArgumentException();
				source.skip();
				source.popParams(count);
				return FEMHandler.from(result);
			} else throw new IllegalArgumentException();
		}
	}

	/** Diese Methode kompiliert eine Funktion und gibt diese zurück. Sie probiert hierfür {@link #compileArray(FEMCompiler) Wertlisten},
	 * {@link #compileString(FEMCompiler) Zeichenketten}, {@link #compileHandler(FEMCompiler) Funktionszeiger}, {@link #compileLocale(FEMCompiler)
	 * Parameterverweise} und {@link #compileConst(FEMCompiler) Konstanten} durch. Wenn hierbei keine Funktion ermittelt werden konnte, wird {@code null}
	 * geliefert.
	 *
	 * @see #parseFunction(FEMParser)
	 * @param source Kompiler.
	 * @return Funktion oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileFunction(final FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		FEMFunction result = this.compileArray(source);
		if (result != null) return result;
		result = this.compileString(source);
		if (result != null) return result;
		result = this.compileHandler(source);
		if (result != null) return this.compileComposite(source, result);
		result = this.compileLocale(source);
		if (result != null) return this.compileComposite(source, result);
		final String string = this.compileConst(source);
		if (string == null) return null;
		result = this.compileFunction(source, string);
		if (result != null) return this.compileComposite(source, result);
		throw new IllegalArgumentException("Funktion '" + string + "' ungültig.");
	}

	/** Diese Methode kompiliert die mit der gegebenen Zeichenkette beschriebene Funktion und gibt diese zurück. Sie versucht dazu die Zeichenkette über
	 * {@link #compileValue(FEMCompiler, String)} in einen konstanten Wert zu übersetzen. Wenn hierbei keine Konstante ermittelt werden konnte, wird ein
	 * {@link FEMCompiler#proxy(String) Platzhalter mit dem gegebenen Namen} geliefert.
	 *
	 * @see #parseFunction(FEMParser)
	 * @param source Kompiler.
	 * @param string Zeichenkette, bspw. einer Konstanten oder eines Funktionsnamens.
	 * @return Funktion.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileFunction(final FEMCompiler source, final String string) throws NullPointerException, IllegalArgumentException {
		final FEMValue result = this.compileValue(source, string);
		return result != null ? result : source.proxy(string);
	}

	/** Diese Methode kompiliert eine Parameterliste und gibt diese zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '('} ist,
	 * wird {@code null} geliefert. Die Parameterfunktionen werden hierzu über {@link #compileClosure(FEMFunction)} aus den Funktionen abgeleitet, die über
	 * {@link #compileFunction(FEMCompiler)} ermittelt wurden.
	 *
	 * @see #parseParams(FEMParser)
	 * @param source Kompiler.
	 * @return Parameterliste oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected List<FEMFunction> compileParams(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '(') return null;
		source.skip();
		this.compileComments(source);
		if (source.symbol() == ')') {
			source.skip();
			return Collections.emptyList();
		}
		FEMFunction item = this.compileFunction(source);
		if (item == null) throw new IllegalArgumentException();
		final List<FEMFunction> result = new ArrayList<>();
		result.add(this.compileClosure(item));
		while (true) {
			this.compileComments(source);
			if (source.symbol() == ')') {
				source.skip();
				return result;
			}
			if (source.symbol() != ';') throw new IllegalArgumentException();
			source.skip();
			this.compileComments(source);
			item = this.compileFunction(source);
			if (item == null) throw new IllegalArgumentException();
			result.add(this.compileClosure(item));
		}
	}

	/** Diese Methode gibt die gegebene Funktion als Parameterfunktion zurück. Ein {@link FEMHandler Funktionszeiger} wird hierbei zu einem {@link ClosureFunction
	 * Funktionszeiger mit Stapalrahmenbindung}. Alle andere Funktionen bleiben unverändert.
	 *
	 * @see #compileParams(FEMCompiler)
	 * @see FEMHandler#value()
	 * @see FEMFunction#toClosure()
	 * @param source Funktion.
	 * @return Parameterfunktion. */
	protected FEMFunction compileClosure(final FEMFunction source) {
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
	protected FEMFunction compileComposite(final FEMCompiler source, final FEMFunction result) throws NullPointerException, IllegalArgumentException {
		return this.compileComposite(source, result, (result instanceof FEMHandler) || (result instanceof ClosureFunction));
	}

	/** Diese Methode kompiliert den Aufruf der gegebenen Funktion mit den aus {@link #compileParams(FEMCompiler) Parameterlisten} stammenden Parameterfunktionen
	 * und gibt diesen zurück. Dazu werden beliebig viele {@link #compileParams(FEMCompiler) Parameterlisten} kompiliert und alle außer der ersten Parameterliste
	 * über {@link FEMFunction#concat(List)} mit der aufzurufenden Funktion verkettet. Die Art der Anbindung für die erste Parameterliste wird über die
	 * Verkettungsoption {@code concat} bestimmt. Diese sollte {@code true} sein, wenn die gegebene Funktion ein {@link FEMHandler Funktionszeiger}, ein
	 * {@link ClosureFunction Funktionszeiger mit Stapalrahmenbindung} oder eine Funktion ist, die erst bei ihrer Auswertung einen Funktionszeiger liefert.
	 *
	 * @param source Kompiler.
	 * @param result aufzurufende Funktion.
	 * @param concat {@code true}, wenn die erste Parameterliste über {@link FEMFunction#concat(List)} angebunden werden soll. {@code false}, wenn sie über
	 *        {@link FEMFunction#compose(List)} angebunden werden soll.
	 * @return Funktionsaufruf oder {@code result}.
	 * @throws NullPointerException Wenn {@code source} bzw. {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMFunction compileComposite(final FEMCompiler source, FEMFunction result, final boolean concat)
		throws NullPointerException, IllegalArgumentException {
		List<FEMFunction> params = this.compileParams(source);
		if (params == null) return Objects.notNull(result);
		result = concat ? result.concat(params) : result.compose(params);
		while (true) {
			params = this.compileParams(source);
			if (params == null) return result;
			result = result.concat(params);
			this.compileComments(source);
		}
	}

	/** Diese Methode kompiliert die Zeichenkette einer Konatenten und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ
	 * {@code '?'} oder {@code '<'} ist, wird {@code null} geliefert.
	 *
	 * @see #parseConst(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected String compileConst(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
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
	 * @see #parseName(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected String compileName(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '~') return null;
		final String result = source.section();
		source.skip();
		return result;
	}

	/** Diese Methode kompiliert einen Parameterindex und gibt diesen zurück. Wenn der {@link FEMCompiler#section() aktuelle Bereich} nicht vom Typ {@code '#'}
	 * ist, wird {@code null} geliefert.
	 *
	 * @see #parseIndex(FEMParser)
	 * @param source Kompiler.
	 * @return Parametername oder {@code null}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected Integer compileIndex(final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		if (source.symbol() != '#') return null;
		final Integer result = Integer.valueOf(source.section());
		source.skip();
		return result;
	}

	/** Diese Methode überspringt Kommentare.
	 *
	 * @see #parseComments(FEMParser)
	 * @param source Kompiler.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	protected void compileComments(final FEMCompiler source) throws NullPointerException {
		for (int symbol = source.symbol(); symbol == '/'; symbol = source.skip()) {}
	}

}