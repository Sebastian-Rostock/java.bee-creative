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
import bee.creative.util.Setter;
import bee.creative.util.Strings;

/** Diese Schnittstelle definiert domänenspezifische Kompilations- und Formatierungsmethoden, die von einem {@link FEMCompiler} zur Übersetzung von Quelltexten
 * in Werte, Funktionen und Parameternamen bzw. von einem {@link FEMFormatter} Übersetzung von Werten und Funktionen in Quelltexte genutzt werden können.
 * <p>
 * Die {@code putAs*}-Methoden zum Parsen einer {@link FEMParser#source() Zeichenkette} erkennen die Bedeutung tragenden {@link FEMScript.Token Bereiche} und
 * {@link FEMParser#putToken(Token) erfassen} diese falls nötig im gegebenen {@link FEMParser}. Die {@code putAs*}-Methoden zum Formatieren eines gegebenen
 * Objekts erzeugen dagegen die Bedeutung tragenden Bereiche der Textdarstellung und {@link FEMFormatter#putToken(Object) erfassen} diese im gegebenen
 * {@link FEMFormatter}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMDomain {

	/** Dieses Feld speichert die native {@link FEMDomain} mit folgendem Verhalten:
	 * <dl>
	 * <dt>{@link #formatData(FEMFormatter, Object)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #_putAsFunction(FEMFormatter, FEMFunction)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #compileName(FEMCompiler)}</dt>
	 * <dd>Siehe {@link #NORMAL}.</dd>
	 * <dt>{@link #compileFunction(FEMCompiler)}</dt>
	 * <dd>Soweit möglich wird eine Instanz von {@link FEMNative} mit Nutzdaten vom Typ {@code null}, {@link String}, {@link Character}, {@link Boolean} oder
	 * {@link BigDecimal} geliefert.<br>
	 * Andernfalls wird der gelieferten Parameter über {@link FEMReflection#from(String)} ermittelt bzw. ein {@link FEMCompiler#proxy(String) Platzhalter}
	 * geliefert.</dd>
	 * </dl>
	*/
	public static final FEMDomain NATIVE = new FEMDomain() {

		@Override
		public FEMFunction parseFunction(final FEMCompiler compiler) throws IllegalArgumentException {
			String section = compiler.section();
			switch (compiler.symbol()) {
				case '"':
					return new FEMNative(Strings.parseSequence(section, '\"', '\\', '\"'));
				case '\'':
					return new FEMNative(new Character(Strings.parseSequence(section, '\'', '\\', '\'').charAt(0)));
				case '<':
					section = Strings.parseSequence(section, '<', '/', '>');
				default:
					if (section.equals("null")) return FEMNative.NULL;
					if (section.equals("true")) return FEMNative.TRUE;
					if (section.equals("false")) return FEMNative.FALSE;
					try {
						return new FEMNative(new BigDecimal(section));
					} catch (final IllegalArgumentException cause) {}
					try {
						return FEMReflection.from(section);
					} catch (final IllegalArgumentException cause) {}
					return compiler.proxy(section);
			}
		}

		@Override
		public String toString() {
			return "NATIVE";
		}

	};

	/** Dieses Feld speichert die normale {@link FEMDomain} mit folgendem Verhalten:
	 * <dl>
	 * <dt>{@link #formatData(FEMFormatter, Object)}</dt>
	 * <dd>{@link FEMScript Aufbereitete Quelltexte} werden über {@link FEMCompiler#formatScript(FEMFormatter)} formatiert.<br>
	 * {@link FEMFrame Stapelrahmen} und {@link FEMFunction Funktionen} werden über {@link #_putAsFunction(FEMFormatter, FEMFunction)} formatiert.<br>
	 * Alle anderen Objekte werden über {@link String#valueOf(Object)} formatieren.</dd>
	 * <dt>{@link #_putAsFunction(FEMFormatter, FEMFunction)}</dt>
	 * <dd>{@link FEMFunction Funktionen} werden über {@link FEMFunction#toScript(FEMFormatter)} formatiert.</dd>
	 * <dt>{@link #compileName(FEMCompiler)}</dt>
	 * <dd>Als Name wird der {@link FEMCompiler#section() aktuelle Bereich} geliefert.</dd>
	 * <dt>{@link #compileFunction(FEMCompiler)}</dt>
	 * <dd>Soweit möglich wird eine Instanz von {@link FEMString}, {@link FEMVoid}, {@link FEMBoolean}, {@link FEMInteger}, {@link FEMDecimal},
	 * {@link FEMDatetime}, {@link FEMDuration} oder {@link FEMBinary} geliefert.<br>
	 * Andernfalls wird ein {@link FEMCompiler#proxy(String) Platzhalter} geliefert.</dd>
	 * </dl>
	*/
	public static final FEMDomain NORMAL = new FEMDomain();

	public static final int PARSE_VALUE = 0;

	public static final int PARSE_VALUE_LIST = 1;

	public static final int PARSE_PROXY = 2;

	public static final int PARSE_PROXY_LIST = 3;

	public static final int PARSE_FUNCTION = 4;

	public static final int PARSE_FUNCTION_LIST = 5;

	{}

	/** Diese Methode ist eine Abkürzung für {@code this.formatConst(name, false)}.
	 *
	 * @see #formatConst(String, boolean)
	 * @param string Zeichenkette.
	 * @return gegebene bzw. formateirte Zeichenkette. */
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
	 * @return gegebene bzw. formateirte Zeichenkette. */
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
	 * @see #_putAsScript(FEMFormatter, FEMCompiler)
	 * @param source aufbereiteter Quelltext.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatScript(final FEMScript source) {
		final FEMFormatter target = new FEMFormatter();
		this._putAsScript(target, new FEMCompiler().useScript(source));
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung einer Wertliste mit den gegebenen Werten zurück.
	 *
	 * @see #_putAsArray(FEMFormatter, Iterable)
	 * @param array Wertliste.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
	public String formatArray(final Iterable<? extends FEMValue> array) {
		final FEMFormatter target = new FEMFormatter();
		this._putAsArray(target, array);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Parameterwerte zurück.
	 *
	 * @see #putAsFrame(FEMFormatter, Iterable)
	 * @param params Parameterwerte.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public String formatFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this.putAsFrame(target, params);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Parameterfunktionen zurück.
	 *
	 * @see #_putAsParams(FEMFormatter, Iterable)
	 * @param params Parameterfunktionen.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	public String formatParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this._putAsParams(target, params);
		return target.format();
	}

	/** Diese Methode gibt die Textdarstellung der gegebenen Funktion zurück.
	 *
	 * @see #_putAsFunction(FEMFormatter, FEMFunction)
	 * @param source Funktion.
	 * @return Textdarstellung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	public String formatFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		final FEMFormatter target = new FEMFormatter();
		this._putAsFunction(target, source);
		return target.format();
	}

	{}

	{}

	/** Diese Methode überführt die gegebene Zeichenkette in einen {@link FEMScript aufbereiteten Quelltext} und gibt diesen zurück. Sie erzeugt dazu einen
	 * {@link FEMParser} und delegiert diesen zusammen mit dem gegebenen Skriptmodus an {@link #putAsItems(FEMParser, int)}.
	 *
	 * @param source Zeichenkette, die geparst werden soll.
	 * @param scriptMode Modus für {@link FEMScript#mode()}.
	 * @return aufbereiteten Quelltext. */
	public FEMScript parseScript(final String source, final int scriptMode) throws NullPointerException {
		final FEMParser parser = new FEMParser().useSource(source);
		this.putAsItems(parser, scriptMode);
		return FEMScript.from(scriptMode, source, parser.tokens());
	}

	protected boolean putAsItems(FEMParser target, int scriptMode) {
		int limit = 1;
		Filter<FEMParser> filter;
		switch (scriptMode) {
			default:
				return !this._putAsError(target);
			case PARSE_VALUE_LIST:
				limit = 0;
			case PARSE_VALUE:
				filter = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this._putAsValue(input);
					}

				};
			break;
			case PARSE_PROXY_LIST:
				limit = 0;
			case PARSE_PROXY:
				filter = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this._putAsProxy(input);
					}

				};
			break;
			case PARSE_FUNCTION_LIST:
				limit = 0;
			case PARSE_FUNCTION:
				filter = new Filter<FEMParser>() {

					@Override
					public boolean accept(FEMParser input) {
						return FEMDomain.this._putAsFunction(input);
					}

				};
			break;
		}
		return this.putAsItems(target, limit, filter);
	}

	/** Diese Methode parst die {@link Token Bereiche} einer mit Semikolon separierten Auflistung von über den gegebenen {@code itemParser} erkannten Elementen
	 * und gibt nur dann {@code true} zurück, wenn diese am ersten Element erkannt wurden. Das Symbol {@code ';'} wird direkt als Bereichstyp des erfassten
	 * Bereichs eingesetzt. Zwischen all diesen Komponenten können beliebig viele {@link #_putAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @param limit Maximale Anzahl der Elemente in der Auflistung, wobei Werte kleiner {@code 0} für eine unbeschränkte Anzahl stehen.
	 * @param filter {@link Filter} zum Erkennen und Parsen der {@link Token Bereiche} eines Elements.
	 * @return {@code true}, wenn die Auflistung erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code filter} {@code null} ist. */
	protected boolean putAsItems(final FEMParser target, int limit, Filter<? super FEMParser> filter) throws NullPointerException {
		this._putAsComments(target);
		if (!filter.accept(target)) return false;
		while (true) {
			--limit;
			this._putAsComments(target);
			if (target.isParsed()) return true;
			if ((limit == 0) || (target.symbol() != ';')) return this._putAsError(target);
			target.putToken(';');
			target.skip();
			this._putAsComments(target);
			if (!filter.accept(target)) return this._putAsError(target);
		}
	}

	/** Diese Methode fügt die Textdarstellung der Liste der gegebenen Elemente an den gegebenen {@link FEMFormatter} an.<br>
	 * Die Elemente werden über den gegebenen {@link Setter} {@link Setter#set(Object, Object) formatiert} und mit {@code commaSymbol} separiert sowie in
	 * {@code openSymbol} und {@code closeSymbol} eingeschlossen.
	 * <p>
	 * Wenn die Liste leer ist, werden nur {@code openSymbol} und {@code closeSymbol} angefügt. Andernfalls {@link FEMFormatter#putBreakInc() beginnt} nach
	 * {@code openSymbol} eine neue Hierarchieebene, die vor {@code closeSymbol} {@link FEMFormatter#putBreakDec() endet}. Nach jedem {@code commaSymbol} wird ein
	 * {@link FEMFormatter#putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn die Liste mehr als ein Element enthält.
	 *
	 * @see FEMFormatter#putBreakInc()
	 * @see FEMFormatter#putBreakDec()
	 * @see FEMFormatter#putBreakSpace()
	 * @see FEMFormatter#putIndent()
	 * @param <GItem> Typ der Elemente.
	 * @param target Formatierer.
	 * @param items Elemente.
	 * @param openSymbol {@code null} oder Symbol, das vor der Liste angefügt wird.
	 * @param commaSymbol {@code null} oder Symbol, das zwischen die Elemente eingefügt wird.
	 * @param closeSymbol {@code null} oder Symbol, das nach der Liste angefügt wird.
	 * @param itemFormatter {@link Setter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
	protected <GItem> void putAsItems(final FEMFormatter target, final Iterable<? extends GItem> items, final Object openSymbol, final Object commaSymbol,
		final Object closeSymbol, final Setter<? super FEMFormatter, ? super GItem> itemFormatter) throws NullPointerException, IllegalArgumentException {
		final Iterator<? extends GItem> iterator = items.iterator();
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

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer {@link FEMProxy benannten Funktion} und gibt nur dann {@code true} zurück, wenn diese an
	 * ihrer {@link #_putAsConst(FEMParser) Bezeichnung} erkannt wurde. Der Bezeichnung folg die als {@link #_putAsHandler(FEMParser) Funktionszeiger} angegebene
	 * Funktion, wobei zwischen diesen beiden Komponenten beliebig viele {@link #_putAsComments(FEMParser) Kommentare/Leerraum} stehen können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die benannten Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsProxy(final FEMParser target) throws NullPointerException {
		if (!this._putAsConst(target)) return false;
		this._putAsComments(target);
		if (!this._putAsHandler(target)) return this._putAsError(target);
		return true;
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Konstante und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür in spitze Klammen eingeschlossene und mit Schrägstrich {@link #_putAsSequence(FEMParser, char, char, char) maskierte} Zeichenkette sowie
	 * unmaskierte Bezeichner durch. Ein unmaskierter Bezeichner ist eine Zeichenkette ohne Leerraum, Schrägstrich, Semikolon sowie ohne runde, eckige oder
	 * geschweifte Klammer und nutzen den Bereichstyp {@code '?'}.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Konstante erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsConst(final FEMParser target) throws NullPointerException {
		if (this._putAsSequence(target, '<', '/', '>')) return true;
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

	/** Diese Methode formateirt und erfasst die Textdarstellung der Konstanten mit der gegebenen Bezeichnung.<br>
	 * Die Formatierung der Bezeichnung erfolgt dazu über {@link #formatConst(String)}.
	 *
	 * @param target Formatierer.
	 * @param source Bezeichnung.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void _putAsConst(final FEMFormatter target, final String source) throws NullPointerException {
		target.putToken(this.formatConst(source));
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Wertliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden eckigen
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #_putAsValue(FEMParser) Werte}. Die Wertliste endet mit
	 * der schließenden eckigen Klammer. Die Symbole {@code '['}, {@code ';'} und {@code ']'} werden direkt als Bereichstyp der erfassten Bereiche eingesetzt.
	 * Wenn die Wertliste fehlerhaft ist, wird für die öffnende eckige Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können
	 * beliebig viele {@link #_putAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Wertliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsArray(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '[') return false;
		final int openIndex = target.putToken('[');
		target.skip();
		this._putAsComments(target);
		if (target.symbol() == ']') {
			target.putToken(']');
			target.skip();
			return true;
		} else if (target.isParsed() || !this._putAsValue(target)) {
			target.setToken(openIndex, '!');
			return this._putAsError(target);
		} else {
			while (true) {
				this._putAsComments(target);
				if (target.symbol() == ']') {
					target.putToken(']');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this._putAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this._putAsComments(target);
					if (!this._putAsValue(target)) {
						target.setToken(openIndex, '!');
						return this._putAsError(target);
					}
				}
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Wertliste.<br>
	 * Hierbei werden die {@link #_putAsValue(FEMFormatter, FEMValue) formatierten} Werte mit {@code ";"} separiert sowie in {@code "["} und {@code "]"}
	 * eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die Wertliste mehr als
	 * ein Element enthält.
	 *
	 * @see #putAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #_putAsValue(FEMFormatter, FEMValue)
	 * @param target Formatierer.
	 * @param source Wertliste.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsArray(FEMFormatter target, Iterable<? extends FEMValue> source) throws NullPointerException, IllegalArgumentException {
		this.putAsItems(target, source, "[", ";", "]", new Setter<FEMFormatter, FEMValue>() {

			@Override
			public void set(final FEMFormatter input, final FEMValue value) {
				FEMDomain.this._putAsValue(input, value);
			}

		});
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Werts als Element einer {@link #_putAsArray(FEMParser) Wertliste} und gibt nur dann
	 * {@code true} zurück, wenn dieser erkannt wurde. Sie probiert hierfür {@link #_putAsArray(FEMParser) Wertlisten}, {@link #_putAsString(FEMParser)
	 * Zeichenketten}, {@link #_putAsHandler(FEMParser) Funktionszeiger} und {@link #_putAsConst(FEMParser) Konstanten} durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Wert erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsValue(final FEMParser target) throws NullPointerException {
		return this._putAsArray(target) || this._putAsString(target) || this._putAsHandler(target) || this._putAsConst(target);
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen Werts.<br>
	 * Für ein {@link FEMArray}, einen {@link FEMString} und eine {@link FEMFuture} wird die Textdarstellung über {@link #_putAsArray(FEMFormatter, Iterable)},
	 * {@link #_putAsString(FEMFormatter, FEMString)} bzw. {@link #putAsFuture(FEMFormatter, FEMFuture)} erfasst. Jeder andere {@link FEMValue} wird über
	 * {@link FEMValue#toString()} in eine Zeichenkette überführt, welche anschließend über {@link #_putAsConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @param target Formatierer.
	 * @param source Wert.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsValue(final FEMFormatter target, final FEMValue source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMArray) {
			final FEMArray array = (FEMArray)source;
			this._putAsArray(target, array);
		} else if (source instanceof FEMString) {
			final FEMString string = (FEMString)source;
			this._putAsString(target, string);
		} else if (source instanceof FEMFuture) {
			final FEMFuture future = (FEMFuture)source;
			this.putAsFuture(target, future);
		} else {
			this._putAsConst(target, source.toString());
		}
	}

	/** Diese Methode parst und erfasst den {@link Token Bereich} einer Zeichenkette und gibt nur dann {@code true} zurück, wenn diese erkannt wurde. Sie probiert
	 * hierfür mit einfachen und doppelten Anführungszeichen {@link #_putAsSequence(FEMParser, char) maskierte} Zeichenketten durch.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Zeichenkette erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsString(final FEMParser target) throws NullPointerException {
		return this._putAsSequence(target, '\'') || this._putAsSequence(target, '\"');
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Zeichenkette.<br>
	 * Die Formatierung erfolgt dazu über {@link Strings#formatSequence(CharSequence, char)} mit dem einfachen Anführungszeichen zur Maskierung.
	 *
	 * @param target Formatierer.
	 * @param source Zeichenkette.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist. */
	protected void _putAsString(final FEMFormatter target, final FEMString source) throws NullPointerException {
		target.putToken(Strings.formatSequence(source.toString(), '"'));
	}

	protected void putAsFuture(final FEMFormatter target, final FEMFuture value) throws IllegalArgumentException {
		synchronized (value) {
			if (value.result != null) {
				this._putAsValue(target, value.result);
			} else {
				this._putAsHandler(target, value.function);
				this.putAsFrame(target, value.frame);
			}
		}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} eines Funktionszeigers und gibt nur dann {@code true} zurück, wenn dieser an der öffnenden
	 * geschweiften Klammer erkannt wurde.<br>
	 * Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #_putAsName(FEMParser) Parameternamen}. Auf diese folgen dann ein Doppelpunkt,
	 * die {@link #_putAsFunction(FEMParser) Funktion} sowie die schließenden geschweifte Klammer. Die Symbole <code>'{'</code>, {@code ';'}, {@code ':'} und
	 * <code>'}'</code> werden direkt als Typ der erfassten Bereiche eingesetzt. Wenn de Funktionszeiger fehlerhaft ist, wird für die öffnende geschweifte Klammer
	 * sowie den Doppelpunkt der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen Komponenten können beliebig viele {@link #_putAsComments(FEMParser)
	 * Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn der Funktionszeiger erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsHandler(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '{') return false;
		final int openIndex = target.putToken('{'), closeIndex;
		target.skip();
		this._putAsComments(target);
		if (target.symbol() == ':') {
			closeIndex = target.putToken(':');
			target.skip();
		} else if (target.isParsed() || !this._putAsName(target)) {
			target.setToken(openIndex, '!');
			return this._putAsError(target);
		} else {
			while (true) {
				this._putAsComments(target);
				if (target.symbol() == ':') {
					closeIndex = target.putToken(':');
					target.skip();
					break;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this._putAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this._putAsComments(target);
					if (!this._putAsName(target)) {
						target.setToken(openIndex, '!');
						return this._putAsError(target);
					}
				}
			}
		}
		this._putAsComments(target);
		if (target.isParsed() || !this._putAsFunction(target)) {
			target.setToken(openIndex, '!');
			target.setToken(closeIndex, '!');
			return this._putAsError(target);
		}
		this._putAsComments(target);
		if (target.isParsed() || (target.symbol() != '}')) {
			target.setToken(openIndex, '!');
			target.setToken(closeIndex, '!');
			return this._putAsError(target);
		}
		target.putToken('}');
		target.skip();
		return true;
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung eines Funktionszeigers auf die gegebene Funktion.<br>
	 * Die {@link #_putAsFunction(FEMFormatter, FEMFunction) formatierte} Funktion wird dabei in <code>"{:"</code> und <code>"}"</code> eingeschlossen.
	 *
	 * @see #_putAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsHandler(final FEMFormatter target, final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		target.putToken("{:");
		this._putAsFunction(target, source);
		target.putToken("}");
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Funktion und gibt nur dann {@code true} zurück, wenn diese erkannt wurde.<br>
	 * Sie probiert hierfür {@link #_putAsArray(FEMParser) Wertlisten}, {@link #_putAsString(FEMParser) Zeichenketten}, {@link #_putAsLocale(FEMParser)
	 * Parameterverweise}, {@link #_putAsHandler(FEMParser) Funktionszeiger} und {@link #_putAsConst(FEMParser) Konstanten} durch, wobei die letzten drei noch von
	 * beliebig viel {@link #_putAsComments(FEMParser) Leerraum/Kommentaren} und {@link #_putAsParams(FEMParser) Parameterlisten} gefolgt werden können.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Funktion erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsFunction(final FEMParser target) throws NullPointerException {
		if (this._putAsArray(target) || this._putAsString(target)) return true;
		if (!this._putAsLocale(target) && !this._putAsHandler(target) && !this._putAsConst(target)) return false;
		while (true) {
			this._putAsComments(target);
			if (!this._putAsParams(target)) return true;
		}
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung der gegebene Funktion.<br>
	 * Für eine {@link TraceFunction}, eine {@link FrameFunction}, eine {@link FutureFunction} oder eine {@link ClosureFunction} wird die Textdarstellung ihrer
	 * referenzierten Funktion mit dieser Methode erfasst. Bei einer {@link ConcatFunction} oder einer {@link CompositeFunction} werden die Textdarstellungen der
	 * aufzurufenden Funktion mit dieser Methode sowie die der Parameterliste mit {@link #_putAsParams(FEMFormatter, Iterable)} erfasst. Bei einem
	 * {@link FEMProxy} wird dessen {@link FEMProxy#name() Name} über {@link #_putAsConst(FEMFormatter, String)} erfasst. Jeder andere {@link FEMValue} würd über
	 * {@link #_putAsValue(FEMFormatter, FEMValue)} erfasst. Jede andere {@link FEMFunction} wird über {@link FEMFunction#toString()} in eine Zeichenkette
	 * überführt, welche anschließend über {@link #_putAsConst(FEMFormatter, String)} erfasst wird.
	 *
	 * @param target Formatierer.
	 * @param source Funktion.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsFunction(final FEMFormatter target, final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) {
			final FEMValue value = (FEMValue)source;
			this._putAsValue(target, value);
		} else if (source instanceof FEMProxy) {
			final FEMProxy value = (FEMProxy)source;
			this._putAsConst(target, value.name);
		} else if (source instanceof CompositeFunction) {
			final CompositeFunction value = (CompositeFunction)source;
			this._putAsFunction(target, value.function);
			this._putAsParams(target, Arrays.asList(value.params));
		} else if (source instanceof ConcatFunction) {
			final ConcatFunction concatFunction = (ConcatFunction)source;
			this._putAsFunction(target, concatFunction.function);
			this._putAsParams(target, Arrays.asList(concatFunction.params));
		} else if (source instanceof ClosureFunction) {
			final ClosureFunction closureFunction = (ClosureFunction)source;
			this._putAsFunction(target, closureFunction.function);
		} else if (source instanceof FrameFunction) {
			final FrameFunction frameFunction = (FrameFunction)source;
			this._putAsFunction(target, frameFunction.function);
		} else if (source instanceof FutureFunction) {
			final FutureFunction futureFunction = (FutureFunction)source;
			this._putAsFunction(target, futureFunction.function);
		} else if (source instanceof TraceFunction) {
			final TraceFunction traceFunction = (TraceFunction)source;
			this._putAsFunction(target, traceFunction.function);
		} else {
			this._putAsConst(target, source.toString());
		}
	}

	/** Diese Methode fügt die Textdarstellung der Liste der gegebenen Parameterwerte eines Stapelrahmens an den gegebenen {@link FEMFormatter} an.<br>
	 * Wenn diese Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die nummerierten und {@link #_putAsValue(FEMFormatter, FEMValue) formatierten}
	 * Parameterwerte mit {@code ";"} separiert sowie in {@code "("} und {@code ")"} eingeschlossen angefügt. Vor jedem Parameterwert wird dessen logische
	 * Position {@code i} als {@code "$i: "} angefügt.<br>
	 * Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
	 *
	 * @see #putAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #_putAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param params Parameterwerte.
	 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
	protected void putAsFrame(FEMFormatter target, Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalArgumentException {
		this.putAsItems(target, params, "(", ";", ")", new Setter<FEMFormatter, FEMFunction>() {

			int index = 1;

			@Override
			public void set(final FEMFormatter input, final FEMFunction value) {
				input.putToken("$").putToken(new Integer(this.index)).putToken(": ");
				FEMDomain.this._putAsFunction(input, value);
				this.index++;
			}

		});
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} einer Parameterliste und gibt nur dann {@code true} zurück, wenn diese an der öffnenden runden
	 * Klammer erkannt wurde. Dieser Klammer folgen die untereineander mit Semikolon separierten {@link #_putAsFunction(FEMParser) Parameterfunktionen}. Die
	 * Parameterliste endet mit der schließenden runden Klammer. Die Symbole {@code '('}, {@code ';'} und {@code ')'} werden direkt als Bereichstyp der erfassten
	 * Bereiche eingesetzt. Wenn die Wertliste fehlerhaft ist, wird für die öffnende runde Klammer der Bereichstyp {@code '!'} eingesetzt. Zwischen all diesen
	 * Komponenten können beliebig viele {@link #_putAsComments(FEMParser) Kommentare/Leerraum} stehen.
	 *
	 * @param target Parser.
	 * @return {@code true}, wenn die Parameterliste erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsParams(final FEMParser target) throws NullPointerException {
		if (target.symbol() != '(') return false;
		final int openIndex = target.putToken('(');
		target.skip();
		this._putAsComments(target);
		if (target.symbol() == ')') {
			target.putToken(')');
			target.skip();
			return true;
		} else if (target.isParsed() || !this._putAsFunction(target)) {
			target.setToken(openIndex, '!');
			return this._putAsError(target);
		} else {
			while (true) {
				this._putAsComments(target);
				if (target.symbol() == ')') {
					target.putToken(')');
					target.skip();
					return true;
				} else if (target.isParsed() || (target.symbol() != ';')) {
					target.setToken(openIndex, '!');
					return this._putAsError(target);
				} else {
					target.putToken(';');
					target.skip();
					this._putAsComments(target);
					if (!this._putAsFunction(target)) {
						target.setToken(openIndex, '!');
						return this._putAsError(target);
					}
				}
			}
		}
	}

	/** Diese Methode formateirt und erfasst die Textdarstellung der gegebenen Parameterfunktionsliste.<br>
	 * Hierbei werden die {@link #_putAsFunction(FEMFormatter, FEMFunction) formatierten} Parameterfunktionen mit {@code ";"} separiert sowie in {@code "("} und
	 * {@code ")"} eingeschlossen erfasst. Die aktuelle Hierarchieebene wird als einzurücken {@link FEMFormatter#putIndent() markiert}, wenn mehrere die
	 * Funktionsliste mehr als ein Element enthält.
	 *
	 * @see #putAsItems(FEMFormatter, Iterable, Object, Object, Object, Setter)
	 * @see #_putAsFunction(FEMFormatter, FEMFunction)
	 * @param target Formatierer.
	 * @param source Parameterfunktionen.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsParams(FEMFormatter target, Iterable<? extends FEMFunction> source) throws NullPointerException, IllegalArgumentException {
		this.putAsItems(target, source, "(", ";", ")", new Setter<FEMFormatter, FEMFunction>() {

			@Override
			public void set(final FEMFormatter input, final FEMFunction value) {
				FEMDomain.this._putAsFunction(input, value);
			}

		});
	}

	/** Diese Methode erfassten die verbleibende Eingabe als {@link Token Fehlerbereich} mit dem Typ {@code '!'} und gibt {@code true} zurück.
	 *
	 * @param target Parser.
	 * @return {@code true}.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsError(final FEMParser target) throws NullPointerException {
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
	protected boolean _putAsIndex(final FEMParser target) throws NullPointerException {
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
	protected boolean _putAsName(final FEMParser target) throws NullPointerException {
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
	 * Diesem Zeichen kann ein {@link #_putAsIndex(FEMParser) Parameterindex} oder ein {@link #_putAsName(FEMParser) Parametername} folgen. Das Symbol {@code '$'}
	 * wird direkt als Typ des erfassten Bereichs eingesetzt.
	 *
	 * @param source Parser.
	 * @return {@code true}, wenn der Parameterverweis erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsLocale(final FEMParser source) throws NullPointerException {
		if (source.symbol() != '$') return false;
		source.putToken('$');
		source.skip();
		if (this._putAsIndex(source)) return true;
		this._putAsName(source);
		return true;
	}

	/** Diese Methode parst den {@link Token Bereich} von Leerraum, erfasst diese jedoch nicht.<br>
	 * Zum Leerraum zählen alle Symbole kleiner oder gleich {@code ' '}.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void _putAsSpace(final FEMParser source) throws NullPointerException {
		for (int symbol = source.symbol(); (symbol >= 0) && (symbol <= ' '); symbol = source.skip()) {}
	}

	/** Diese Methode parst und erfasst die {@link Token Bereiche} von Kommentaren.<br>
	 * Ein Kommentar wird als mit Schrägstrich {@link #_putAsSequence(FEMParser, char)} maskierte} Zeichenkette erkannt und erfasst. Vor und nach einem Kommentar
	 * kann beliebig viel {@link #_putAsSpace(FEMParser) Leerraum} stehen.
	 *
	 * @param source Parser.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected void _putAsComments(final FEMParser source) throws NullPointerException {
		do {
			this._putAsSpace(source);
		} while (this._putAsSequence(source, '/'));
	}

	/** Diese Methode ist eine Abkürzung für {@link #_putAsSequence(FEMParser, char, char, char) this.putAsSequence(source, maskSymbol, maskSymbol, maskSymbol)}.
	 *
	 * @param source Parser.
	 * @param maskSymbol Maskierungszeichen.
	 * @return {@code true}, wenn die Sequenz erkannt wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	protected boolean _putAsSequence(final FEMParser source, final char maskSymbol) throws NullPointerException {
		return this._putAsSequence(source, maskSymbol, maskSymbol, maskSymbol);
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
	protected boolean _putAsSequence(final FEMParser target, final char openSymbol, final char maskSymbol, final char closeSymbol) throws NullPointerException {
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
				if (symbol < 0) {
					if (maskSymbol == closeSymbol) {
						target.putToken(openSymbol, offset);
						return true;
					}
					target.putToken('!', offset);
					return true;
				}
			} else if (symbol == closeSymbol) return true;
		}
	}

	/** Diese Methode formatiert und erfasst die Textdarstellung des gegebenen aufbereiteten Quelltexts.
	 *
	 * @param target Formatierer.
	 * @param source Parser zum aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code target} bzw. {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code source} nicht formatiert werden kann. */
	protected void _putAsScript(final FEMFormatter target, final FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		while (true) {
			this._putAsScript(target, source, false);
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
	protected void _putAsScript(final FEMFormatter target, final FEMCompiler source, final boolean simpleSpace)
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
					this._putAsScript(target, source, false);
					if (source.symbol() == ')') {
						target.putBreakDec().putToken(")");
						source.skip();
					}
				break;
				case '[':
					target.putToken("[").putBreakInc();
					source.skip();
					this._putAsScript(target, source, false);
					if (source.symbol() == ']') {
						target.putBreakDec().putToken("]");
						source.skip();
					}
				break;
				case '{':
					target.putToken("{");
					source.skip();
					this._putAsScript(target, source, true);
					if (source.symbol() == ':') {
						target.putToken(": ");
						source.skip();
						this._putAsScript(target, source, false);
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

	/** Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den Typ des ersten bedeutsamen Bereichs oder {@code -1}
	 * zurück. Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
	 *
	 * @see #skip()
	 * @return aktueller Bereichstyp. */
	final void getAsComments(FEMCompiler source) {
		for (int symbol = source.symbol(); symbol == '/'; symbol = source.skip()) {}
	}

	/** Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
	 *
	 * @param string Zeichenkette.
	 * @return Zahl. */
	final int compileIndex(final String string) {
		if ((string == null) || string.isEmpty()) return -1;
		final char symbol = string.charAt(0);
		if ((symbol < '0') || (symbol > '9')) return -1;
		try {
			return Integer.parseInt(string);
		} catch (final NumberFormatException e) {
			return -1;
		}
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMFunction} und gibt diesen zurück.
	 *
	 * @see FEMFunction#concat(FEMFunction...)
	 * @see FEMFunction#compose(FEMFunction...)
	 * @see FEMParam#VIEW
	 * @return Wertliste als {@link FEMFunction}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMFunction compileArrayAsFunction() throws IllegalArgumentException {
		// if (!this.arrayEnabled) throw this.illegal(null, " Wertlisten sind nicht zulässig.");
		this.skip();
		if (this.getAsComments() == ']') {
			this.skip();
			return FEMArray.EMPTY;
		}
		final List<FEMFunction> list = new ArrayList<>();
		boolean value = true;
		while (true) {
			final FEMFunction item = this.getAsFunction();
			list.add(item);
			value = value && (this.functionToValue(item) != null);
			switch (this.getAsComments()) {
				case ';': {
					this.skip();
					this.getAsComments();
					break;
				}
				case ']': {
					this.skip();
					final int size = list.size();
					if (!value) {
						final FEMFunction result = FEMParam.VIEW.compose(list.toArray(new FEMFunction[size]));
						return result;
					}
					final FEMValue[] values = new FEMValue[size];
					for (int i = 0; i < size; i++) {
						values[i] = list.get(i).invoke(FEMFrame.EMPTY);
					}
					return FEMArray.from(values);
				}
				default: {
					throw this.illegal(null, " Zeichen «;» oder «]» erwartet.");
				}
			}
		}
	}

	/** Diese Methode kompiliert via {@code this.domain().compileParam(this, this.section())} die beim aktuellen Bereich beginnende Parameterfunktion und gibt
	 * diese zurück.
	 *
	 * @see FEMDomain#compileFunction(FEMCompiler)
	 * @return Parameterfunktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMFunction compileLocale(FEMCompiler source) throws IllegalArgumentException {
		try {
			final FEMFunction result = this.domain.compileFunction(this);
			if (result == null) throw this.illegal(null, " Parameter erwartet.");
			this.skip();
			return result;
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final RuntimeException cause) {
			throw this.illegal(cause, "");
		}
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
	 *
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	FEMFunction parseFunction(FEMCompiler source) throws IllegalArgumentException {
		FEMFunction result;
		switch (this.getAsComments()) {
			case -1:
			case ';':
			case ':':
			case '(':
			case ')':
			case ']':
			case '}': {
				throw this.illegal(null, " Wert oder Funktion erwartet.");
			}
			case '[': {
				return this.compileArrayAsFunction();
			}
			case '$': {
				return this.getAsLocale(source);
			}
			case '{': {
				result = this.getAsHandler(source);
				if (result == null) return null;

				if (this.getAsComments() != '(') return this.compileClosure(result);

				// if (!this.concatEnabled) throw this.illegal(null, " Funktionsverkettungen ist nicht zulässsig.");
				break;
			}
			default: {
				result = this.compileLocale();
				if (this.getAsComments() != '(') return result;
			}
		}
		if (this.getAsComments() != '(') return result;
		boolean concat = false;
		do {
			// if (concat && !this.concatEnabled) throw this.illegal(null, " Funktionsverkettungen ist nicht zulässsig.");
			if (this.getAsComments() != '(') return result;
			this.skip(); // '('
			final List<FEMFunction> list = new ArrayList<>();
			while (true) {
				if (this.getAsComments() == ')') {
					this.skip();
					final FEMFunction[] params = list.toArray(new FEMFunction[list.size()]);
					result = concat ? result.concat(params) : result.compose(params);
					break;
				}
				final FEMFunction item = this.getAsFunction();
				list.add(item);
				switch (this.getAsComments()) {
					default:
						throw this.illegal(null, " Zeichen «;» oder «)» erwartet.");
					case ';':
						this.skip();
					case ')':
				}
			}
			concat = true;
		} while (this.getAsComments() == '(');
		return result;
	}

	final List<FEMFunction> getAsParams(FEMCompiler source) throws IllegalArgumentException {
		if (source.symbol() != '(') return null;
		source.skip();
		this.getAsComments(source);
		if (source.symbol() == ')') {
			source.skip();
			return Collections.emptyList();
		}
		final FEMFunction item = this.getAsFunction(source);
		if (item == null) throw new IllegalArgumentException();
		this.getAsComments(source);

		result.add(item);

		final List<FEMFunction> result = new ArrayList<>();
		while (true) {
			this.getAsComments(source);
			if (source.symbol() == ')') {
				source.skip();
				return result;
			}
			final FEMFunction item = this.getAsFunction(source);
			if (item == null) return null;
			result.add(item);
			this.getAsComments(source);
			if (source.symbol() == ';') {
				this.skip();
			}
			switch (this.getAsComments()) {
				default:
					throw this.illegal(null, " Zeichen «;» oder «)» erwartet.");
				case ';':
				case ')':
			}
		}
	}

	protected FEMFunction getAsLocale(FEMCompiler source) {
		this.skip();
		final String name = this.compileName();
		if (name == null) return FEMParam.VIEW;
		int index = this.compileIndex(name);
		if (index < 0) {
			index = this.params.indexOf(name);
			if (index < 0) throw this.illegal(null, " Parametername «%s» ist unbekannt.", name);
		} else if (index > 0) {
			index--;
		} else throw this.illegal(null, " Parameterindex «%s» ist unzulässig.", index);
		return FEMParam.from(index);
	}

	/** Diese Methode gibt eine Funktion zurück, welche die gegebene Funktion als {@link FEMHandler} liefert.
	 *
	 * @see FEMFunction#toValue()
	 * @see FEMFunction#toClosure()
	 * @param function
	 * @return */
	FEMFunction compileClosure(FEMFunction function) {
		return function.toClosure();
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
	 *
	 * @return Parameterfunktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMProxy compileProxy(FEMCompiler source) throws IllegalArgumentException {
		final String name = this.getAsName(source);
		if ((name == null) || (this.compileIndex(name) >= 0)) throw this.illegal(null, " Funktionsname erwartet.");
		final FEMProxy result = this.proxy(name);
		if (this.getAsComments() != '{') throw this.illegal(null, " Parametrisierter Funktionsaufruf erwartet.");
		result.set(this.getAsHandler());
		return result;
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
	 *
	 * @return Wertliste als {@link FEMValue}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMValue getAsArray(FEMCompiler source) throws IllegalArgumentException {
		if (source.symbol() != '[') return null;
		source.skip();
		this.getAsComments(source);
		if (source.symbol() == ']') {
			source.skip();
			return FEMArray.EMPTY;
		}
		final List<FEMValue> result = new ArrayList<>();
		while (true) {
			final FEMValue value = this.getAsValue(source);
			if (value == null) throw new IllegalArgumentException();
			result.add(value);
			this.getAsComments(source);
			if (source.symbol() == ']') {
				source.skip();
				return FEMArray.EMPTY;
			} else if (source.symbol() == ';') {
				source.skip();
				this.getAsComments(source);
			} else throw new IllegalArgumentException();
		}
	}

	/** value, null, illegal */
	protected FEMValue getAsValue(FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		FEMValue result = this.getAsArray(source);
		if (result != null) return result;
		result = this.getAsString(source);
		if (result != null) return result;
		result = this.getAsHandler(source);
		if (result != null) return result;
		final String string = this.getAsConst(source);
		if (string == null) return null;
		result = this.getAsValue(source, string);
		if (result != null) return result;
		throw new IllegalArgumentException();
	}

	protected FEMValue getAsValue(FEMCompiler source, String string) {
		try {
			return FEMVoid.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMBoolean.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMInteger.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDecimal.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDatetime.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMDuration.from(string);
		} catch (final IllegalArgumentException cause) {}
		try {
			return FEMBinary.from(string);
		} catch (final IllegalArgumentException cause) {}
		return null;
	}

	/** value, null, illegal */
	protected FEMFunction getAsFunction(FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		FEMFunction result = this.getAsArray(source);
		if (result != null) return result;
		result = this.getAsString(source);
		if (result != null) return result;
		result = this.getAsHandler(source);
		if (result != null) return result;
		final String string = this.getAsConst(source);
		if (string == null) return null;
		result = this.getAsFunction(source, string);
		if (result != null) return result;
		throw new IllegalArgumentException();
	}

	protected FEMFunction getAsFunction(FEMCompiler source, String string) {
		final FEMValue result = this.getAsValue(source, string);
		return result != null ? result : source.proxy(string);
	}

	/** string, null, illegal */
	protected FEMString getAsString(FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		final int symbol = source.symbol();
		if ((symbol != '\"') && (symbol != '\'')) return null;
		final String result = Strings.parseSequence(source.section(), (char)symbol);
		if (result == null) throw new IllegalArgumentException();
		return FEMString.from(result);
	}

	/** string, null, illegal */
	protected String getAsConst(FEMCompiler source) throws NullPointerException, IllegalArgumentException {
		final int symbol = source.symbol();
		final String result;
		if (symbol == '<') {
			result = Strings.parseSequence(source.section(), '<', '/', '>');
		} else if (symbol == '?') {
			result = source.section();
		} else return null;
		if ((result == null) || result.isEmpty()) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt den im {@link FEMCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
	 *
	 * @see FEMCompiler#range()
	 * @see FEMCompiler#script()
	 * @param compiler Kompiler mit Bereich und Quelltext.
	 * @return Funktions- bzw. Parametername.
	 * @throws IllegalArgumentException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
	 *         <p>
	 *         Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
	 *         Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0}
	 *         ist.
	 * @return Funktions- oder Parametername oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected String getAsName(FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		if (source.symbol() != '~') return null;
		final String result = source.section();
		if (result.isEmpty()) throw new IllegalArgumentException();
		return result;
	}

	protected Integer getAsIndex(FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		if (source.symbol() != '#') return null;
		return Integer.valueOf(source.section());
	}

	/** Diese Methode parst einen {@link FEMHandler Funktionszeiger} und gibt diesen nur dann zurück, wenn er an der öffnenden geschweiften Klammer erkannt wurde.
	 * Andernfalls wird {@code null} geliefert.
	 *
	 * @return Funktionszeiger oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	protected FEMHandler getAsHandler(FEMCompiler source) throws NullPointerException, IllegalArgumentException { // OKAY
		if (source.symbol() != '{') return null;
		source.skip();
		int index = 0;
		while (true) {
			this.getAsComments(source);
			if (source.symbol() < 0) throw new IllegalArgumentException();
			final String name = this.getAsName(source);
			if (name != null) {
				source.params.add(index++, name);
				this.getAsComments(source);
			}
			if (source.symbol() == ';') {
				if (name == null) throw new IllegalArgumentException();
				source.skip();
			} else if (source.symbol() == ':') {
				source.skip();
				final FEMFunction result = this.getAsFunction(source);
				if (result == null) throw new IllegalArgumentException();
				this.getAsComments(source);
				if (source.symbol() != '}') throw new IllegalArgumentException();
				source.skip();
				source.params.subList(0, index).clear();
				return FEMHandler.from(result);
			} else throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt den Ergebniswert der gegebenen Funktion zurück, sofer diese ein {@link FEMValue} ist. Andernfalls wird {@code null} geliefert.
	 *
	 * @param function Funktion.
	 * @return Ergebniswert oder {@code null}. */
	final FEMValue functionToValue(final FEMFunction function) {
		if (function instanceof FEMValue) return (FEMValue)function;
		return null;
	}

}