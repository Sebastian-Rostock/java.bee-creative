package bee.creative.fem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Converter;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

/** FEM - Function Evaluation Model
 * <p>
 * Diese Klasse implementiert grundlegende {@link FEMValue Werte} und {@link FEMFunction Funktionen} sowie {@link ScriptParser Parser}, {@link ScriptFormatter
 * Formatter} und {@link ScriptCompiler Compiler} für {@link FEMScript Queltexte}.
 * 
 * @see ScriptParser
 * @see ScriptFormatter
 * @see ScriptCompiler
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEM {

	/** Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
	 * {@link ScriptCompiler} in Werte und Funktionen überführt werden.
	 * <p>
	 * Die Erzeugung von {@link Range Bereichen} erfolgt gemäß dieser Regeln:
	 * <ul>
	 * <li>Die Zeichen {@code '/'}, {@code '\''} und {@code '\"'} erzeugen je einen Bereich, der das entsprechende Zeichen als Bereichstyp verwendet, mit dem
	 * Zeichen beginnt und endet sowie das Zeichen zwischen dem ersten und letzten nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der
	 * Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.</li>
	 * <li>Das Zeichen <code>'&lt;'</code> erzeugen einen Bereich, der mit dem Zeichen <code>'&gt;'</code> endet und beide Zeichen zwischen dem ersten und letzten
	 * jeweils nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der Bereich an der Stelle des Fehlers und hat den Bereichstyp
	 * {@code '?'}. Andernfalls hat er den Bereichstyp {@code '!'}.</li>
	 * <li>Jedes der Zeichen {@code '$'}, {@code ';'}, {@code ':'}, {@code '('}, {@code ')'}, <code>'{'</code> und <code>'}'</code> erzeugt eine eigene Bereich,
	 * der das entsprechende Zeichen als Bereichstyp verwendet.</li>
	 * <li>Sequenzen aus Zeichen kleiner gleich dem Leerzeichen werden zu Bereichen mit dem Bereichstyp {@code '_'}.</li>
	 * <li>Alle restlichen Zeichenfolgen werden zu Bereichen mit dem Bereichstyp {@code '.'}.</li>
	 * </ul>
	 * <p>
	 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
	 * 
	 * @see #parseRanges()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptParser extends Parser {

		@SuppressWarnings ("javadoc")
		boolean _active_;

		/** Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}. */
		int _value_;

		/** Dieses Feld speichert die bisher ermittelten Bereiche. */
		final List<Range> _ranges_ = new ArrayList<>();

		{}

		/** Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		synchronized final void _start_() throws IllegalStateException {
			this._check_();
			this._active_ = true;
			this._value_ = -1;
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._active_ = false;
		}

		@SuppressWarnings ("javadoc")
		final void _check_() throws IllegalStateException {
			if (this._active_) throw new IllegalStateException();
		}

		/** Diese Methode fügt eine neue Bereich mit den gegebenen Parametern hinzu, der bei {@link #index()} endet.
		 * 
		 * @param type Typ des Bereichs.
		 * @param start Start des Bereichs. */
		final void _put_(final int type, final int start) {
			this._ranges_.add(new Range((char)type, start, this.index() - start));
		}

		/** Diese Methode beginnt das parsen eines Wertbereichs mit dem Bereichstyp {@code '.'}, welches mit {@link #_closeValue_()} beendet werden muss. */
		final void _openValue_() {
			if (this._value_ >= 0) return;
			this._value_ = this.index();
		}

		/** Diese Methode beendet das einlesen des Wertbereichs mit dem Bereichstyp {@code '.'}. */
		final void _closeValue_() {
			final int start = this._value_;
			if (start < 0) return;
			this._value_ = -1;
			if (this.index() <= start) return;
			this._put_('.', start);
		}

		/** Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher
		 * das Zeichen als Typ verwendet.
		 * 
		 * @param type Zeichen als Bereichstyp. */
		final void _parseMask_(final int type) {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						this._put_(type, start);
						return;
					}
				}
			}
			this._put_('?', start);
		}

		/** Diese Methode parst einen Bereich, der mit dem Zeichen <code>'&lt;'</code> beginnt, mit dem Zeichen <code>'&gt;'</code> ende und in dem diese Zeichen nur
		 * paarweise vorkommen dürfen. ein solcher Bereich geparst werden konnte, ist dessen Bereichstyp {@code '!'}. Wenn eine dieser Regeln verletzt wird, ist der
		 * Bereichstyp {@code '?'}. */
		final void _parseName_() {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '>') {
					if (this.skip() != '>') {
						this._put_('!', start);
						return;
					}
				} else if (symbol == '<') {
					if (this.skip() != '<') {
						break;
					}
				}
			}
			this._put_('?', start);
		}

		/** Diese Methode überspringt alle Zeichen, die kleiner oder gleich dem eerzeichen sind. */
		final void _parseSpace_() {
			final int start = this.index();
			for (int symbol = this.skip(); (symbol >= 0) && (symbol <= ' '); symbol = this.skip()) {}
			this._put_('_', start);
		}

		/** Diese Methode erzeugt zum gegebenen Zeichen einen Bereich der Länge 1 und navigiert zum nächsten Zeichen.
		 * 
		 * @see #skip()
		 * @see #_put_(int, int)
		 * @param type Zeichen als Bereichstyp. */
		final void _parseSymbol_(final int type) {
			final int start = this.index();
			this.skip();
			this._put_(type, start);
		}

		/** Diese Methode parst die {@link #source() Eingabe}. */
		final void _parseSource_() {
			for (int symbol; true;) {
				switch (symbol = this.symbol()) {
					case -1: {
						this._closeValue_();
						return;
					}
					case '\'':
					case '\"':
					case '/': {
						this._closeValue_();
						this._parseMask_(symbol);
						break;
					}
					case '<': {
						this._closeValue_();
						this._parseName_();
						break;
					}
					case '$':
					case ':':
					case ';':
					case '(':
					case ')':
					case '[':
					case ']':
					case '{':
					case '}': {
						this._closeValue_();
						this._parseSymbol_(symbol);
						break;
					}
					default: {
						if (symbol <= ' ') {
							this._closeValue_();
							this._parseSpace_();
						} else {
							this._openValue_();
							this.skip();
						}
					}
				}
			}
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #source() Eingabe} keines der in {@link #_parseSource_()} erkannten Zeichen enthält,
		 * d.h. wenn das Parsen der Eingabe via {@link #parseRanges()} genau einen Bereich mit dem Typ {@code '.'} ergibt, welcher über {@link #_openValue_()} und
		 * {@link #_closeValue_()} entstand.
		 * 
		 * @return {@code true}, wenn die Eingabe nur einen Wert enthält. */
		final boolean _checkSource_() {
			if (this.isParsed()) return false;
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				switch (symbol) {
					case '\'':
					case '\"':
					case '/':
					case '<':
					case '>':
					case '$':
					case ':':
					case ';':
					case '(':
					case ')':
					case '[':
					case ']':
					case '{':
					case '}':
						return false;
					default: {
						if (symbol <= ' ') return false;
					}
				}
			}
			return true;
		}

		/** Diese Methode gibt die in Anführungszeichen eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #_parseMask_(int)
		 * @param type Anführungszeichen.
		 * @return Eingabe mit Maskierung. */
		final String _encodeMask_(final int type) {
			this.take(type);
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					this.take(symbol);
				}
				this.take(symbol);
			}
			this.take(type);
			return this.target();
		}

		/** Diese Methode gibt die in spitze Klammern eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #_parseName_()
		 * @return Eingabe mit Maskierung. */
		final String _encodeValue_() {
			this.take('<');
			for (int symbol = this.symbol(); symbol >= 0; symbol = this.skip()) {
				if ((symbol == '<') || (symbol == '>')) {
					this.take(symbol);
				}
				this.take(symbol);
			}
			this.take('>');
			return this.target();
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.
		 * 
		 * @param type Anführungszeichen.
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		final String _decodeMask_(final int type) throws IllegalArgumentException {
			if (this.symbol() != type) throw new IllegalArgumentException();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						if (this.isParsed()) return this.target();
						throw new IllegalArgumentException();
					}
				}
				this.take(symbol);
			}
			throw new IllegalArgumentException();
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.
		 * 
		 * @see #_parseName_()
		 * @return Eingabe ohne Maskierung.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		final String _decodeValue_() throws IllegalArgumentException {
			if (this.symbol() != '<') throw new IllegalArgumentException();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '<') {
					if (this.skip() != '<') throw new IllegalArgumentException();
				} else if (symbol == '>') {
					if (this.skip() != '>') {
						if (this.isParsed()) return this.target();
						throw new IllegalArgumentException();
					}
				}
				this.take(symbol);
			}
			throw new IllegalArgumentException();
		}

		/** Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
		 * 
		 * @param value Eingabe.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptParser useSource(final String value) throws NullPointerException, IllegalStateException {
			this._check_();
			super.source(value);
			return this;
		}

		/** Diese Methode parst die {@link #source() Eingabe} in einen aufbereiteten Quelltext und gibt diesen zurück.
		 * 
		 * @see FEMScript
		 * @see #parseRanges()
		 * @return aufbereiteter Quelltext.
		 * @throws IllegalStateException Wenn aktuell geparst wird. */
		public final FEMScript parseScript() throws IllegalStateException {
			return new FEMScript(this.source(), this.parseRanges());
		}

		/** Diese Methode parst die {@link #source() Eingabe} und gibt die Liste der ermittelten Bereiche zurück.
		 * 
		 * @see Range
		 * @return Bereiche.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final Range[] parseRanges() throws IllegalStateException {
			this._start_();
			try {
				this._ranges_.clear();
				this._parseSource_();
				final Range[] result = this._ranges_.toArray(new Range[this._ranges_.size()]);
				return result;
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Anführungszeichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit einem der Anführungszeichen {@code '\''} oder {@code '\"'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatString()
		 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseString() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '\'') return this._decodeMask_('\'');
				if (this.symbol() == '\"') return this._decodeMask_('\"');
				throw new IllegalArgumentException();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden Schrägstrichen und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit {@code '/'} und enthält dieses Zeichen nur gedoppelt.
		 * 
		 * @see #formatComment()
		 * @return Eingabe ohne Maskierung mit {@code '/'}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseComment() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '/') return this._decodeMask_('/');
				throw new IllegalArgumentException();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die {@link #source() Eingabe} ohne den einschließenden spitzen Klammern und deren Maskierungen zurück.<br>
		 * Die Eingabe beginnt und endet hierbei mit <code>'&lt;'</code> bzw. <code>'&gt;'</code> und enthält diese Zeichen nur gedoppelt. Wenn die Eingabe nicht
		 * derart beginnt, wird sie unverändert zurück gegeben.
		 * 
		 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft.
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
		public final String parseValue() throws IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				if (this.symbol() == '<') return this._decodeValue_();
				return this.source();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in spitze Klammern eingeschlossenen und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.<br>
		 * Wenn die Eingabe keine von diesem Parser besonders behandelten Zeichen enthält, wird sie unverändert zurück gegeben.
		 * 
		 * @see #parseValue()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatValue() throws IllegalStateException {
			this._start_();
			try {
				if (this._checkSource_()) return this.source();
				this.reset();
				return this._encodeValue_();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in {@code '\''} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseString()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatString() throws IllegalStateException {
			this._start_();
			try {
				return this._encodeMask_('\'');
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode gibt die in {@code '/'} eingeschlossene und mit entsprechenden Maskierungen versehene {@link #source() Eingabe} zurück.
		 * 
		 * @see #parseComment()
		 * @return Eingabe mit Maskierung.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final String formatComment() throws IllegalStateException {
			this._start_();
			try {
				return this._encodeMask_('/');
			} finally {
				this._stop_();
			}
		}

	}

	/** Diese Klasse implementiert einen Kompiler, der {@link FEMScript aufbereitete Quelltexte} in {@link FEMValue Werte} sowie {@link FEMFunction Funktionen}
	 * überführen und diese im Rahmen eines {@link ScriptFormatter} auch formatieren kann.
	 * <p>
	 * Die Bereichestypen der Quelltexte haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) sind bedeutungslos, dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link FEMArray}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link FEMArray} als Funktion bzw. Parameterwert
	 * kompiliert wird.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über {@link ScriptCompilerHelper#compileName(ScriptCompiler)}
	 * aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu
	 * genau einer Funktion kompiliert werden.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link FEMParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.from(0)}). Andernfalls steht der Bereich für {@link FEMParamFunction#VIEW}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileParam(ScriptCompiler)} in Parameterfunktionen überführt.</li>
	 * </ul>
	 * <p>
	 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
	 * 
	 * @see #formatScript(ScriptFormatter)
	 * @see #compileValue()
	 * @see #compileFunction()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptCompiler extends Parser {

		@SuppressWarnings ("javadoc")
		boolean _active_;

		/** Dieses Feld speichert die Kompilationsmethoden. */
		ScriptCompilerHelper _helper_ = ScriptCompilerHelper.DEFAULT;

		/** Dieses Feld speichert den Quelltext. */
		FEMScript _script_ = FEMScript.EMPTY;

		/** Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter. */
		final Map<String, FEMProxyFunction> _proxies_ = Collections.synchronizedMap(new LinkedHashMap<String, FEMProxyFunction>());

		/** Dieses Feld speichert die Parameternamen. */
		final List<String> _params_ = Collections.synchronizedList(new LinkedList<String>());

		/** Dieses Feld speichert die Zulässigkeit von Wertlisten. */
		boolean _arrayEnabled_ = true;

		/** Dieses Feld speichert die Zulässigkeit der Bindung des Stapelrahmens. */
		boolean _closureEnabled_ = true;

		/** Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen. */
		boolean _chainingEnabled_ = true;

		/** Dieses Feld speichert den Formatierer. */
		ScriptFormatter _formatter_;

		{}

		/** Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		final synchronized void _start_() throws IllegalStateException {
			this._check_();
			this._active_ = true;
			this._proxies_.clear();
			this.reset();
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._active_ = false;
		}

		@SuppressWarnings ("javadoc")
		final void _check_() throws IllegalStateException {
			if (this._active_) throw new IllegalStateException();
		}

		/** Diese Methode formatiert den aktuellen Quelltext als Sequenz von Werten und Stoppzeichen. */
		final void _format_() {
			final ScriptFormatter formatter = this._formatter_;
			while (true) {
				this._formatSequence_(false);
				if (this.symbol() < 0) return;
				formatter.put(this.section()).putBreakSpace();
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Wertliste. */
		final void _formatArray_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("[").putBreakInc();
			this.skip();
			this._formatSequence_(false);
			if (this.symbol() == ']') {
				formatter.putBreakDec().put("]");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Parameterliste. */
		final void _formatParam_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("(").putBreakInc();
			this.skip();
			this._formatSequence_(false);
			if (this.symbol() == ')') {
				formatter.putBreakDec().put(")");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle parametrisierte Funktion. */
		final void _formatFrame_() {
			final ScriptFormatter formatter = this._formatter_;
			formatter.put("{");
			this.skip();
			this._formatSequence_(true);
			if (this.symbol() == ':') {
				formatter.put(": ");
				this.skip();
				this._formatSequence_(false);
			}
			if (this.symbol() == '}') {
				formatter.put("}");
				this.skip();
			}
		}

		/** Diese Methode formatiert die aktuelle Wertsequenz, die bei einer schließenden Klammer oder Doppelpunkt endet.
		 * 
		 * @param space {@code true}, wenn hinter Kommentaren und Semikola ein Leerzeichen statt eines bedingten Umbruchs eingefügt werden soll. */
		final void _formatSequence_(final boolean space) {
			final ScriptFormatter formatter = this._formatter_;
			int count = 0;
			while (true) {
				switch (this.symbol()) {
					case '_': {
						this.skip();
						break;
					}
					case '/': {
						formatter.put(this.section());
						if (space) {
							formatter.put(" ");
						} else {
							formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case ';': {
						formatter.put(";");
						if (space) {
							formatter.put(" ");
						} else {
							formatter.putBreakSpace();
						}
						this.skip();
						count++;
						break;
					}
					case '(': {
						this._formatParam_();
						break;
					}
					case '[': {
						this._formatArray_();
						break;
					}
					case '{': {
						this._formatFrame_();
						break;
					}
					default: {
						formatter.put(this.section());
						this.skip();
						break;
					}
					case ':':
					case ']':
					case '}':
					case ')':
					case -1: {
						if (count < 2) return;
						formatter.putIndent();
						return;
					}
				}
			}
		}

		/** Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den Typ des ersten bedeutsamen Bereichs oder {@code -1}
		 * zurück. Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereichstyp. */
		final int _compileType_() {
			int symbol = this.symbol();
			while ((symbol == '_') || (symbol == '/')) {
				symbol = this.skip();
			}
			return symbol;
		}

		/** Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
		 * 
		 * @param string Zeichenkette.
		 * @return Zahl. */
		final int _compileIndex_(final String string) {
			if ((string == null) || string.isEmpty()) return -1;
			final char symbol = string.charAt(0);
			if ((symbol < '0') || (symbol > '9')) return -1;
			try {
				return Integer.parseInt(string);
			} catch (final NumberFormatException e) {
				return -1;
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link FEMValue}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMValue _compileArrayAsValue_() throws ScriptException {
			if (!this._arrayEnabled_) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			final List<FEMValue> result = new ArrayList<>();
			this.skip();
			if (this._compileType_() == ']') {
				this.skip();
				return FEMArray.EMPTY;
			}
			while (true) {
				final FEMValue value = this._compileParamAsValue_();
				result.add(value);
				switch (this._compileType_()) {
					case ';': {
						this.skip();
						this._compileType_();
						break;
					}
					case ']': {
						this.skip();
						return FEMArray.from(result);
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMFunction} und gibt diesen zurück.
		 * 
		 * @see FEMValueFunction
		 * @see FEMInvokeFunction
		 * @see FEMParamFunction#VIEW
		 * @return Wertliste als {@link FEMFunction}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileArrayAsFunction_() throws ScriptException {
			if (!this._arrayEnabled_) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			this.skip();
			if (this._compileType_() == ']') {
				this.skip();
				return FEMArray.EMPTY;
			}
			final List<FEMFunction> list = new ArrayList<>();
			boolean value = true;
			while (true) {
				final FEMFunction item = this._compileParamAsFunction_();
				list.add(item);
				value = value && (this._functionToValue(item) != null);
				switch (this._compileType_()) {
					case ';': {
						this.skip();
						this._compileType_();
						break;
					}
					case ']': {
						this.skip();
						final int size = list.size();
						if (!value) {
							final FEMFunction result = FEMParamFunction.VIEW.withParams(list.toArray(new FEMFunction[size]));
							return result;
						}
						final FEMValue[] values = new FEMValue[size];
						for (int i = 0; i < size; i++) {
							values[i] = list.get(i).invoke(FEMFrame.EMPTY);
						}
						return FEMArray.from(values);
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}

		/** Diese Methode kompiliert via {@code this.helper().compileParam(this, this.section())} die beim aktuellen Bereich beginnende Parameterfunktion und gibt
		 * diese zurück.
		 * 
		 * @see ScriptCompilerHelper#compileParam(ScriptCompiler)
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileParam_() throws ScriptException {
			try {
				final FEMFunction result = this._helper_.compileParam(this);
				if (result == null) throw new ScriptException().useSender(this).useHint(" Parameter erwartet.");
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this);
			}
		}

		/** Diese Methode kompiliert denF beim aktuellen Bereich beginnende Wert und gibt diese zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMValue _compileParamAsValue_() throws ScriptException {
			switch (this._compileType_()) {
				case -1:
				case '$':
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}': {
					throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
				}
				case '[': {
					return this._compileArrayAsValue_();
				}
				case '{': {
					if (this._closureEnabled_) throw new ScriptException().useSender(this).useHint(" Ungebundene Funktion unzulässig.");
					final FEMFunction retult = this._compileFrame_();
					return FEMHandler.from(retult);
				}
				default: {
					final FEMFunction param = this._compileParam_();
					final FEMValue result = this._functionToValue(param);
					if (result == null) throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
					return result;
				}
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileParamAsFunction_() throws ScriptException {
			FEMFunction result;
			boolean indirect = false;
			switch (this._compileType_()) {
				case -1:
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}': {
					throw new ScriptException().useSender(this).useHint(" Wert oder Funktion erwartet.");
				}
				case '$': {
					this.skip();
					final String name = this._compileName_();
					if (name == null) return FEMParamFunction.VIEW;
					int index = this._compileIndex_(name);
					if (index < 0) {
						index = this._params_.indexOf(name);
						if (index < 0) throw new ScriptException().useSender(this).useHint(" Parametername «%s» ist unbekannt.", name);
					} else if (index > 0) {
						index--;
					} else throw new ScriptException().useSender(this).useHint(" Parameterindex «%s» ist unzulässig.", index);
					return FEMParamFunction.from(index);
				}
				case '{': {
					result = this._compileFrame_();
					if (this._compileType_() != '(') {
						if (this._closureEnabled_) return FEMClosureFunction.from(result);
						return FEMHandler.from(result);
					}
					if (!this._chainingEnabled_) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
					break;
				}
				case '[': {
					return this._compileArrayAsFunction_();
				}
				default: {
					result = this._compileParam_();
					if (this._compileType_() != '(') return result;
				}
			}
			do {
				if (indirect && !this._chainingEnabled_) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
				this.skip(); // '('
				final List<FEMFunction> list = new ArrayList<>();
				while (true) {
					if (this._compileType_() == ')') {
						this.skip();
						result = FEMInvokeFunction.from(result, !indirect, list.toArray(new FEMFunction[list.size()]));
						break;
					}
					final FEMFunction item = this._compileParamAsFunction_();
					list.add(item);
					switch (this._compileType_()) {
						default:
							throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «)» erwartet.");
						case ';':
							this.skip();
						case ')':
					}
				}
				indirect = true;
			} while (this._compileType_() == '(');
			return result;
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
		 * 
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMProxyFunction _compileProxy_() throws ScriptException {
			final String name = this._compileName_();
			if ((name == null) || (this._compileIndex_(name) >= 0)) throw new ScriptException().useSender(this).useHint(" Funktionsname erwartet.");
			final FEMProxyFunction result = this.proxy(name);
			if (this._compileType_() != '{') throw new ScriptException().useSender(this).useHint(" Parametrisierter Funktionsaufruf erwartet.");
			result.set(this._compileFrame_());
			return result;
		}

		/** Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
		 * Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0} ist.
		 * 
		 * @return Funktions- oder Parametername oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final String _compileName_() throws ScriptException {
			try {
				switch (this._compileType_()) {
					case '$':
					case '(':
					case '[':
					case '{': {
						throw new IllegalStateException();
					}
					case -1:
					case ':':
					case ';':
					case ')':
					case '}':
					case ']': {
						return null;
					}
				}
				final String result = this._helper_.compileName(this);
				if (result.isEmpty()) throw new IllegalArgumentException();
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this).useHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
			}
		}

		/** Diese Methode kompiliert die beim aktuellen Bereich (<code>'{'</code>) beginnende, parametrisierte Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist. */
		final FEMFunction _compileFrame_() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this._compileType_() < 0) throw new ScriptException().useSender(this);
				final String name = this._compileName_();
				if (name != null) {
					if (this._compileIndex_(name) >= 0) throw new ScriptException().useSender(this).useHint(" Parametername erwartet.");
					this._params_.add(count++, name);
				}
				switch (this._compileType_()) {
					case ';': {
						if (name == null) throw new ScriptException().useSender(this).useHint(" Parametername oder Zeichen «:» erwartet.");
						this.skip();
						break;
					}
					case ':': {
						this.skip();
						final FEMFunction result = this._compileParamAsFunction_();
						if (this._compileType_() != '}') throw new ScriptException().useSender(this).useHint(" Zeichen «}» erwartet.");
						this.skip();
						this._params_.subList(0, count).clear();
						return result;
					}
					default: {
						throw new ScriptException().useSender(this);
					}
				}
			}
		}

		/** Diese Methode gibt den Ergebniswert der gegebenen Funktion zurück, sofer diese ein {@link FEMBaseValue} oder eine {@link FEMValueFunction} ist.<br>
		 * Andernfalls wird {@code null} geliefert.
		 * 
		 * @param function Funktion.
		 * @return Ergebniswert oder {@code null}. */
		final FEMValue _functionToValue(final FEMFunction function) {
			if ((function instanceof FEMBaseValue) || (function instanceof FEMValueFunction)) return function.invoke(FEMFrame.EMPTY);
			return null;
		}

		/** Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name des Platzhalters.
		 * @return Platzhalterfunktion.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
		public final FEMProxyFunction proxy(final String name) throws NullPointerException {
			synchronized (this._proxies_) {
				FEMProxyFunction result = this._proxies_.get(name);
				if (result != null) return result;
				this._proxies_.put(name, result = new FEMProxyFunction(name));
				return result;
			}
		}

		/** Diese Methode gibt den aktuellen Bereich zurück.
		 * 
		 * @return aktueller Bereich. */
		public final Range range() {
			return this.isParsed() ? Range.EMPTY : this._script_.get(this.index());
		}

		/** Diese Methode gibt den zu kompilierenden Quelltext zurück.
		 * 
		 * @return Quelltext. */
		public final FEMScript script() {
			return this._script_;
		}

		/** Diese Methode gibt die genutzten Kompilationsmethoden zurück.
		 * 
		 * @return Kompilationsmethoden. */
		public final ScriptCompilerHelper helper() {
			return this._helper_;
		}

		/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.<br>
		 * Die gelieferte Abbildung wird vor jeder Kompilation geleert.
		 * 
		 * @return Abbildung von Namen auf Platzhalter. */
		public final Map<String, FEMProxyFunction> proxies() {
			return this._proxies_;
		}

		/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
		 * 
		 * @return Parameternamen. */
		public final List<String> params() {
			return Collections.unmodifiableList(this._params_);
		}

		/** Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
		 * 
		 * @see Range#extract(String)
		 * @return Aktuelle Zeichenkette. */
		public final String section() {
			return this.range().extract(this._script_.source());
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Wertlisten. */
		public final boolean isArrayEnabled() {
			return this._arrayEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link FEMClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Stapelrahmens. */
		public final boolean isClosureEnabled() {
			return this._closureEnabled_;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
		 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
		 * 
		 * @see #compileFunction()
		 * @see FEMInvokeFunction#direct()
		 * @see FEMInvokeFunction#invoke(FEMFrame)
		 * @return Zulässigkeit der Verkettung von Funktionen. */
		public final boolean isChainingEnabled() {
			return this._chainingEnabled_;
		}

		/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
		 * 
		 * @param value Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useScript(final FEMScript value) throws NullPointerException, IllegalStateException {
			this._check_();
			this.source(value.types());
			this._script_ = value;
			return this;
		}

		/** Diese Methode setzt die zu nutzenden Kompilationsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Kompilationsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useHelper(final ScriptCompilerHelper value) throws NullPointerException, IllegalStateException {
			if (value == null) throw new NullPointerException("value = null");
			this._check_();
			this._helper_ = value;
			return this;
		}

		/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final ScriptCompiler useParams(final String... value) throws NullPointerException, IllegalStateException {
			return this.useParams(Arrays.asList(value));
		}

		/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useParams(final List<String> value) throws NullPointerException, IllegalStateException {
			if (value.contains(null)) throw new NullPointerException("value.contains(null)");
			this._check_();
			this._params_.clear();
			this._params_.addAll(value);
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit von Wertlisten.
		 * 
		 * @see #isArrayEnabled()
		 * @param value Zulässigkeit von Wertlisten.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useArrayEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._arrayEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit der Bindung des Stapelrahmens.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Stapelrahmens.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useClosureEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._closureEnabled_ = value;
			return this;
		}

		/** Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
		 * 
		 * @see #isChainingEnabled()
		 * @param value Zulässigkeit der Verkettung von Funktionen.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public synchronized final ScriptCompiler useChainingEnabled(final boolean value) throws IllegalStateException {
			this._check_();
			this._chainingEnabled_ = value;
			return this;
		}

		/** Diese Methode formatiert den Quelltext im Rahmen des gegebenen Formatierers.
		 * 
		 * @param target Formatierer.
		 * @throws NullPointerException Wenn {@code target} {@code null} ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final void formatScript(final ScriptFormatter target) throws NullPointerException, IllegalStateException {
			this._start_();
			if (target == null) throw new NullPointerException("target = null");
			this._formatter_ = target;
			try {
				this._format_();
			} finally {
				this._formatter_ = null;
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Wert oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMValue compileValue() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return null;
				final FEMValue result = this._compileParamAsValue_();
				if (this._compileType_() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück.<br>
		 * Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert sein. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Wertliste
		 * geliefert.
		 * 
		 * @return Werte.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMValue[] compileValues() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return new FEMValue[0];
				final List<FEMValue> result = new ArrayList<FEMValue>();
				while (true) {
					result.add(this._compileParamAsValue_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new FEMValue[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück.<br>
		 * Die Parameterfunktion müssen durch Bereiche vom Typ {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem
		 * {@link ScriptCompilerHelper#compileName(ScriptCompiler) Namen} und endet dann mit einer in geschweifte Klammern eingeschlossenen parametrisierten
		 * Funktion. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert. Nach dem Aufruf dieser Methode ist Abbildung
		 * {@link #proxies()} entsprechend bestückt.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMProxyFunction[] compileProxies() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				final List<FEMProxyFunction> result = new ArrayList<FEMProxyFunction>();
				if (this._compileType_() < 0) return new FEMProxyFunction[0];
				while (true) {
					result.add(this._compileProxy_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new FEMProxyFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Funktion oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMFunction compileFunction() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return null;
				final FEMFunction result = this._compileParamAsFunction_();
				if (this._compileType_() < 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
		 * separiert sein.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
		public final FEMFunction[] compileFunctions() throws ScriptException, IllegalStateException {
			this._start_();
			try {
				if (this._compileType_() < 0) return new FEMFunction[0];
				final List<FEMFunction> result = new ArrayList<FEMFunction>();
				while (true) {
					result.add(this._compileParamAsFunction_());
					switch (this._compileType_()) {
						case -1: {
							return result.toArray(new FEMFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this._stop_();
			}
		}

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this._helper_, this._params_, this._script_, this._proxies_);
		}

	}

	/** Diese Schnittstelle definiert Kompilationsmethoden, die von einem {@link ScriptCompiler Kompiler} zur Übersetzung von Quelltexten in Werte, Funktionen und
	 * Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptCompilerHelper {

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, der in {@link #compileParam(ScriptCompiler)} sofern möglich den Typ {@link FEMNative} mit
		 * Nutzdaten {@code null}, {@code true}, {@code false}, {@link String} und {@link Character} sowie {@link FEMNativeFunction} nutzt und andernfalls einen
		 * {@link ScriptCompiler#proxy(String)} liefert. */
		static ScriptCompilerHelper NATIVE = new ScriptCompilerHelper() {

			@Override
			public String compileName(final ScriptCompiler compiler) throws ScriptException {
				return compiler.section();
			}

			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler) throws ScriptException {
				String section = compiler.section();
				switch (compiler.symbol()) {
					case '"':
						return new FEMNative(FEM.parseString(section));
					case '\'':
						return new FEMNative(new Character(FEM.parseString(section).charAt(0)));
					case '!':
						section = FEM.parseValue(section);
					default: {
						if (section.equals("null")) return FEMNative.NULL;
						if (section.equals("true")) return FEMNative.TRUE;
						if (section.equals("false")) return FEMNative.FALSE;
						try {
							return new FEMNative(new BigDecimal(section));
						} catch (final NumberFormatException cause) {}
						try {
							return FEMNativeFunction.from(section);
						} catch (final Exception cause) {}
						return compiler.proxy(section);
					}
				}
			}

			@Override
			public String toString() {
				return "NATIVE";
			}

		};

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, der in {@link #compileParam(ScriptCompiler)} sofern möglich die Typen {@link FEMVoid},
		 * {@link FEMBoolean}, {@link FEMString}, {@link FEMBinary}, {@link FEMInteger}, {@link FEMDecimal}, {@link FEMDatetime} und {@link FEMDuration} nutzt und
		 * andernfalls einen {@link ScriptCompiler#proxy(String)} liefert. */
		static ScriptCompilerHelper DEFAULT = new ScriptCompilerHelper() {

			@Override
			public String compileName(final ScriptCompiler compiler) throws ScriptException {
				return compiler.section();
			}

			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler) throws ScriptException {
				String section = compiler.section();
				switch (compiler.symbol()) {
					case '"':
					case '\'': {
						return FEMString.from(FEM.parseString(section));
					}
					case '!': {
						section = FEM.parseValue(section);
					}
					default: {
						try {
							return FEMVoid.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMBoolean.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMInteger.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMDecimal.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMDatetime.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMDuration.from(section);
						} catch (final IllegalArgumentException cause) {}
						try {
							return FEMBinary.from(section);
						} catch (final IllegalArgumentException cause) {}
						return compiler.proxy(section);
					}
				}
			}

			@Override
			public String toString() {
				return "DEFAULT";
			}

		};

		/** Diese Methode gibt den im {@link ScriptCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
		 * 
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Funktions- bzw. Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen). */
		public String compileName(ScriptCompiler compiler) throws ScriptException;

		/** Diese Methode gibt den im {@link ScriptCompiler#section() aktuellen Bereich} des gegebenen Kompilers angegebene Parameter als Funktion zurück.<br>
		 * Der Wert des Parameters entspricht hierbei dem Ergebniswert der gelieferten Funktion.<br>
		 * Konstante Parameterwerte können als {@link FEMBaseValue}, {@link FEMValueFunction} oder {@link FEMProxyFunction} geliefert werden. Funktion als
		 * Parameterwert können als {@link FEMHandler} geliefert werden.
		 * 
		 * @see ScriptCompiler#proxy(String)
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält. */
		public FEMFunction compileParam(ScriptCompiler compiler) throws ScriptException;

	}

	/** Diese Klasse implementiert einen Formatierer, der Daten, Werten und Funktionen in eine Zeichenkette überführen kann.<br>
	 * Er realisiert damit die entgegengesetzte Operation zur Kombination von {@link ScriptParser} und {@link ScriptCompiler}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptFormatter {

		/** Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
		 * 
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
		static final class Mark {

			/** Dieses Feld speichert das Objekt, dass in {@link #_items_} vor jeder Markierung eingefügt wird. */
			static final Mark EMPTY = new ScriptFormatter.Mark(0, false, false, false);

			{}

			/** Dieses Feld speichert die Eigenschaften dieser Markierung. */
			int _data_;

			/** Dieser Konstruktor initialisiert die Markierung.
			 * 
			 * @param level Einrücktiefe ({@link #level()}).
			 * @param last Endmarkierung ({@link #isLast()}).
			 * @param space Leerzeichen ({@link #isSpace()}).
			 * @param enabled Aktivierung ({@link #isEnabled()}). */
			public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
				this._data_ = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
			}

			{}

			/** Diese Methode gibt die Tiefe der Einrückung zurück.
			 * 
			 * @return Tiefe der Einrückung. */
			public final int level() {
				return this._data_ >> 3;
			}

			/** Diese Methode aktiviert die Einrückung. */
			public final void enable() {
				this._data_ |= 2;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
			 * 
			 * @return {@code true} bei einer Endmarkierung. */
			public final boolean isLast() {
				return (this._data_ & 1) != 0;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
			 * 
			 * @return {@code true} bei einem bedingten Leerzeichen. */
			public final boolean isSpace() {
				return (this._data_ & 4) != 0;
			}

			/** Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
			 * 
			 * @return Aktivierung. */
			public final boolean isEnabled() {
				return (this._data_ & 2) != 0;
			}

			{}

			/** {@inheritDoc} */
			@Override
			public final String toString() {
				return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
			}

		}

		{}

		/** Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen. */
		final List<Object> _items_ = new ArrayList<Object>();

		/** Dieses Feld speichert den Puffer für {@link #_format_()}. */
		final StringBuilder _string_ = new StringBuilder();

		/** Dieses Feld speichert den Stack der Hierarchieebenen. */
		final LinkedList<Boolean> _indents_ = new LinkedList<Boolean>();

		/** Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "  "}. */
		String _indent_;

		/** Dieses Feld speichert die Formatierungsmethoden. */
		ScriptFormatterHelper _helper_ = ScriptFormatterHelper.EMPTY;

		{}

		/** Diese Methode beginnt das Parsen und sollte nur in Verbindung mit {@link #_stop_()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		synchronized final void _start_() throws IllegalStateException {
			this._check_(true);
			this._indents_.addLast(Boolean.FALSE);
		}

		@SuppressWarnings ("javadoc")
		synchronized final void _stop_() {
			this._items_.clear();
			this._string_.setLength(0);
			this._indents_.clear();
		}

		@SuppressWarnings ("javadoc")
		final void _check_(final boolean idling) throws IllegalStateException {
			if (this._indents_.isEmpty() != idling) throw new IllegalStateException();
		}

		/** Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
		 * 
		 * @param object Markierung.
		 * @return {@code this}. */
		final ScriptFormatter _putMark_(final Mark object) {
			this._items_.add(object);
			return this;
		}

		/** Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
		 * angefügt.
		 * 
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakInc() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			indents.addLast(Boolean.FALSE);
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, false, false));
		}

		/** Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
		 * passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde oder aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakDec() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(value, true, false, indents.removeLast().booleanValue()));
		}

		/** Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
		 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putBreakSpace() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			return this._putMark_(Mark.EMPTY)._putMark_(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
		}

		/** Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
		 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
		 * 
		 * @see #putBreakSpace()
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird. */
		public final ScriptFormatter putIndent() throws IllegalStateException {
			this._check_(false);
			final LinkedList<Boolean> indents = this._indents_;
			if (this._indents_.getLast().booleanValue()) return this;
			final int value = indents.size();
			for (int i = 0; i < value; i++) {
				indents.set(i, Boolean.TRUE);
			}
			final List<Object> items = this._items_;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == Mark.EMPTY) {
					final Mark token = (Mark)items.get(i + 1);
					if (token.level() <= value) { // ALTERNATIV: else if (token.level() < value) return this;
						if (token.isEnabled()) return this;
						token.enable();
					}
					i--;
				}
			}
			return this;
		}

		/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * 
		 * @see Object#toString()
		 * @param part Objekt.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann. */
		public final ScriptFormatter put(final Object part) throws IllegalStateException, IllegalArgumentException {
			if (part == null) throw new NullPointerException("part = null");
			this._check_(false);
			this._items_.add(part.toString());
			return this;
		}

		/** Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * 
		 * @see ScriptFormatterHelper#formatData(ScriptFormatter, Object)
		 * @param data Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
		public final ScriptFormatter putData(final Object data) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (data == null) throw new NullPointerException("data = null");
			this._check_(false);
			this._helper_.formatData(this, data);
			return this;
		}

		/** Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
		 * Wenn die Liste leer ist, wird {@code "[]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
		 * separiert über {@link #putValue(FEMValue)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor
		 * der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putValue(FEMValue)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param array Wertliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann. */
		public final ScriptFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (array == null) throw new NullPointerException("array = null");
			this._check_(false);
			final Iterator<? extends FEMValue> iter = array.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("[").putBreakInc().putValue(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace().putValue(item);
					} while (iter.hasNext());
					this.putBreakDec().put("]");
				} else {
					this.put("[").putBreakInc().putValue(item).putBreakDec().put("]");
				}
			} else {
				this.put("[]");
			}
			return this;
		}

		/** Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.<br>
		 * 
		 * @see ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
		public final ScriptFormatter putValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value == null) throw new NullPointerException("value = null");
			this._check_(false);
			this._helper_.formatValue(this, value);
			return this;
		}

		/** Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Stapelrahmens an und gibt {@code this} zurück.<br>
		 * Wenn diese Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die nummerierten Parameterwerte in {@code "("} und {@code ")"} eingeschlossen,
		 * sowie mit {@code ";"} separiert über {@link #putValue(FEMValue)} angefügt. Vor jedem Parameterwert wird dessen logische Position {@code i} als
		 * {@code "$i: "} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor der schließenden Klammer
		 * {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param params Stapelrahmen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
		public final ScriptFormatter putFrame(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this._check_(false);
			final Iterator<? extends FEMValue> iter = params.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().put("$1: ").putValue(item);
					int index = 2;
					do {
						item = iter.next();
						this.put(";").putBreakSpace().put("$").put(new Integer(index)).put(": ").putValue(item);
						index++;
					} while (iter.hasNext());
					this.putBreakDec().put(")");
				} else {
					this.put("(").putBreakInc().put("$1: ").putValue(item).putBreakDec().put(")");
				}
			} else {
				this.put("()");
			}
			return this;
		}

		/** Diese Methode fügt den Quelltext der Liste der gegebenen Parameterfunktionen an und gibt {@code this} zurück.<br>
		 * Wenn die Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die Parameterfunktionen in {@code "("} und {@code ")"} eingeschlossen sowie mit
		 * {@code ";"} separiert über {@link #putFunction(FEMFunction)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue
		 * Hierarchieebene, die vor der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes
		 * Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken {@link #putIndent() markiert}, wenn die Funktionsliste mehr als ein Element enthält.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @param params Funktionsliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann. */
		public final ScriptFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this._check_(false);
			final Iterator<? extends FEMFunction> iter = params.iterator();
			if (iter.hasNext()) {
				FEMFunction item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().putFunction(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace().putFunction(item);
					} while (iter.hasNext());
					this.putBreakDec().put(")");
				} else {
					this.put("(").putBreakInc().putFunction(item).putBreakDec().put(")");
				}
			} else {
				this.put("()");
			}
			return this;
		}

		/** Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.<br>
		 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(FEMFunction)} angefügt.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param function parametrisierte Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public final ScriptFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			return this.put("{: ").putFunction(function).put("}");
		}

		/** Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
		 * 
		 * @see ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)
		 * @param function Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public final ScriptFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			this._check_(false);
			this._helper_.formatFunction(this, function);
			return this;
		}

		/** Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
		 * 
		 * @return Zeichenkette zur Einrückung oder {@code null}. */
		public final String getIndent() {
			return this._indent_;
		}

		/** Diese Methode gibt die genutzten Formatierungsmethoden zurück.
		 * 
		 * @return Formatierungsmethoden. */
		public final ScriptFormatterHelper getHelper() {
			return this._helper_;
		}

		/** Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.<br>
		 * Wenn diese {@code null} ist, wird nicht eingerückt.
		 * 
		 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		public synchronized final ScriptFormatter useIndent(final String indent) throws IllegalStateException {
			this._check_(true);
			this._indent_ = indent;
			return this;
		}

		/** Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird. */
		public synchronized final ScriptFormatter useHelper(final ScriptFormatterHelper helper) throws NullPointerException, IllegalStateException {
			if (helper == null) throw new NullPointerException("helper = null");
			this._check_(true);
			this._helper_ = helper;
			return this;
		}

		/** Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
		 * 
		 * @see #put(Object)
		 * @return Quelltext. */
		final String _format_() {
			final String indent = this._indent_;
			final List<Object> items = this._items_;
			final StringBuilder string = this._string_;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == Mark.EMPTY) {
					final Mark token = (Mark)items.get(i++);
					if (token.isEnabled() && (indent != null)) {
						string.append('\n');
						for (int count = token.level() - (token.isLast() ? 2 : 1); count > 0; count--) {
							string.append(indent);
						}
					} else if (token.isSpace()) {
						string.append(' ');
					}
				} else {
					string.append(item);
				}
			}
			return string.toString();
		}

		/** Diese Methode formatiert die gegebenen Elemente in einen Quelltext und gibt diesen zurück.<br>
		 * Die Elemente werden über den gegebenen {@link Converter} angefügt und mit {@code ';'} separiert. In der Methode {@link Converter#convert(Object)} sollten
		 * hierfür {@link #putData(Object)}, {@link #putValue(FEMValue)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
		 * 
		 * @see #formatDatas(Iterable)
		 * @see #formatValues(Iterable)
		 * @see #formatFunctions(Iterable)
		 * @param <GItem> Typ der Elemente.
		 * @param items Elemente.
		 * @param formatter {@link Converter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann. */
		final <GItem> String _format_(final Iterable<? extends GItem> items, final Converter<GItem, ?> formatter) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			this._start_();
			try {
				final Iterator<? extends GItem> iter = items.iterator();
				if (!iter.hasNext()) return "";
				GItem item = iter.next();
				if (iter.hasNext()) {
					this.putIndent();
					formatter.convert(item);
					do {
						item = iter.next();
						this.put(";").putBreakSpace();
						formatter.convert(item);
					} while (iter.hasNext());
				} else {
					formatter.convert(item);
				}
				return this._format_();
			} finally {
				this._stop_();
			}
		}

		/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatDatas(Iterables.itemIterable(object))}.
		 * 
		 * @see #formatDatas(Iterable)
		 * @param object Objekt.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code object} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn das Object nicht formatiert werden kann. */
		public final String formatData(final Object object) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatDatas(Iterables.itemIterable(object));
		}

		/** Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putData(Object)
		 * @param objects Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code objects} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann. */
		public final String formatDatas(final Iterable<?> objects) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this._format_(objects, new Converter<Object, Object>() {

				@Override
				public Object convert(final Object input) {
					return ScriptFormatter.this.putData(input);
				}

			});
		}

		/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatValue(Iterables.itemIterable(value))}.
		 * 
		 * @see #formatValues(Iterable)
		 * @param value Wert.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann. */
		public final String formatValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatValues(Iterables.itemIterable(value));
		}

		/** Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Die Werte werden über {@link #putValue(FEMValue)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putValue(FEMValue)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann. */
		public final String formatValues(final Iterable<? extends FEMValue> values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this._format_(values, new Converter<FEMValue, Object>() {

				@Override
				public Object convert(final FEMValue input) {
					return ScriptFormatter.this.putValue(input);
				}

			});
		}

		/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatFunction(Iterables.itemIterable(function))}.
		 * 
		 * @see #formatFunctions(Iterable)
		 * @param function Funktion.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann. */
		public final String formatFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.formatFunctions(Iterables.itemIterable(function));
		}

		/** Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann. */
		public final String formatFunctions(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			return this._format_(functions, new Converter<FEMFunction, Object>() {

				@Override
				public Object convert(final FEMFunction input) {
					return ScriptFormatter.this.putFunction(input);
				}

			});
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toInvokeString(this, this._helper_, this._indent_, this._items_);
		}

	}

	/** Diese Schnittstelle definiert ein Objekt, welches sich selbst in seine Quelltextdarstellung überführen und diese an einen {@link ScriptFormatter} anfügen
	 * kann.
	 * 
	 * @see ScriptFormatterHelper#formatData(ScriptFormatter, Object)
	 * @see ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)
	 * @see ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptFormatterInput {

		/** Diese Methode formatiert dieses Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.<br>
		 * Sie kann von einem {@link ScriptFormatterHelper} im Rahmen folgender Methoden aufgerufen:
		 * <ul>
		 * <li>{@link ScriptFormatterHelper#formatData(ScriptFormatter, Object)}</li>
		 * <li>{@link ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)}</li>
		 * <li>{@link ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)}</li>
		 * </ul>
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann. */
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException;

	}

	/** Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link ScriptFormatter#putValue(FEMValue)} und
	 * {@link ScriptFormatter#putFunction(FEMFunction)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface ScriptFormatterHelper {

		/** Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden ihre Eingeben über {@link String#valueOf(Object)} formatieren.<br>
		 * {@link FEMScript Aufbereitete Quelltexte} werden in {@link #formatData(ScriptFormatter, Object)} analog zur Interpretation des {@link ScriptCompiler}
		 * formatiert. Daten, Werte und Funktionen mit der Schnittstelle {@link ScriptFormatterInput} werden über deren
		 * {@link ScriptFormatterInput#toScript(ScriptFormatter)}-Methode formatiert. */
		static ScriptFormatterHelper EMPTY = new ScriptFormatterHelper() {

			@Override
			public void formatData(final ScriptFormatter target, final Object data) throws IllegalArgumentException {
				if (data instanceof FEMScript) {
					FEM.scriptCompiler().useScript((FEMScript)data).formatScript(target);
				} else if (data instanceof ScriptFormatterInput) {
					((ScriptFormatterInput)data).toScript(target);
				} else {
					target.put(String.valueOf(data));
				}
			}

			@Override
			public void formatValue(final ScriptFormatter target, final FEMValue value) throws IllegalArgumentException {
				if (value instanceof ScriptFormatterInput) {
					((ScriptFormatterInput)value).toScript(target);
				} else {
					target.put(String.valueOf(value));
				}
			}

			@Override
			public void formatFunction(final ScriptFormatter target, final FEMFunction function) throws IllegalArgumentException {
				if (function instanceof ScriptFormatterInput) {
					((ScriptFormatterInput)function).toScript(target);
				} else {
					target.put(String.valueOf(function));
				}
			}

			@Override
			public String toString() {
				return "EMPTY";
			}

		};

		/** Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param data Objekt.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann. */
		public void formatData(ScriptFormatter target, Object data) throws IllegalArgumentException;

		/** Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann. */
		public void formatValue(ScriptFormatter target, FEMValue value) throws IllegalArgumentException;

		/** Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann. */
		public void formatFunction(ScriptFormatter target, FEMFunction function) throws IllegalArgumentException;

	}

	/** Diese Klasse implementiert die {@link IllegalArgumentException}, die bei Syntaxfehlern von einem {@link ScriptParser} oder {@link ScriptCompiler} ausgelöst
	 * wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class ScriptException extends IllegalArgumentException {

		/** Dieses Feld speichert die Serial-Version-UID. */
		private static final long serialVersionUID = -918623847189389909L;

		{}

		/** Dieses Feld speichert den Hinweis zum erwarteten Inhalt des Bereichs. */
		String _hint_ = "";

		/** Dieses Feld speichert den Quelltext. */
		FEMScript _script_ = FEMScript.EMPTY;

		/** Dieses Feld speichert den Bereich, in dem der Syntaxfehler entdeckt wurde. */
		Range _range_ = Range.EMPTY;

		/** Dieser Konstruktor initialisiert die {@link ScriptException} ohne Ursache. */
		public ScriptException() {
			super();
		}

		/** Dieser Konstruktor initialisiert die {@link ScriptException} mit Ursache.
		 * 
		 * @param cause Urssache. */
		public ScriptException(final Throwable cause) {
			super(cause);
		}

		{}

		/** Diese Methode gibt den Hinweis zum erwarteten Inhalt des Bereichs zurück.
		 * 
		 * @see #getRange()
		 * @return Hinweis oder {@code null}. */
		public final String getHint() {
			return this._hint_;
		}

		/** Diese Methode gibt den Bereich zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Bereich. */
		public final Range getRange() {
			return this._range_;
		}

		/** Diese Methode gibt Quelltext zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Quelltext. */
		public final FEMScript getScript() {
			return this._script_;
		}

		/** Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #getHint()
		 * @param hint Hinweis.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code hint} {@code null} ist. */
		public final ScriptException useHint(final String hint) throws NullPointerException {
			this._hint_ = hint.toString();
			return this;
		}

		/** Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #useHint(String)
		 * @see String#format(String, Object...)
		 * @param format Hinweis.
		 * @param args Formatargumente.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code format} bzw. {@code args} {@code null} ist. */
		public final ScriptException useHint(final String format, final Object... args) throws NullPointerException {
			return this.useHint(String.format(format, args));
		}

		/** Diese Methode setzt den Bereich und gibt {@code this} zurück.
		 * 
		 * @see #getRange()
		 * @param range Bereich.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code range} {@code null} ist. */
		public final ScriptException useRange(final Range range) throws NullPointerException {
			if (range == null) throw new NullPointerException("range = null");
			this._range_ = range;
			return this;
		}

		/** Diese Methode setzt den Quelltext und gibt {@code this} zurück.
		 * 
		 * @see #getScript()
		 * @param script Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code script} {@code null} ist. */
		public final ScriptException useScript(final FEMScript script) throws NullPointerException {
			if (script == null) throw new NullPointerException("script = null");
			this._script_ = script;
			return this;
		}

		/** Diese Methode setzt Quelltext sowie Bereich und gibt {@code this} zurück.
		 * 
		 * @see #useScript(FEMScript)
		 * @see #useRange(Range)
		 * @param sender {@link ScriptCompiler}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code sender} {@code null} ist. */
		public final ScriptException useSender(final ScriptCompiler sender) throws NullPointerException {
			return this.useRange(sender.range()).useScript(sender.script());
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final String getMessage() {
			return (this._range_ == Range.EMPTY //
				? "Unerwartetes Ende der Zeichenkette." //
				: "Unerwartete Zeichenkette «" + this._range_.extract(this._script_._source_) + "» an Position " + this._range_._offset_ + ".") //
				+ this._hint_;
		}

	}

	{}

	/** Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().arrayFrom(data)}.
	 * 
	 * @see FEMContext#arrayFrom(Object)
	 * @param data Wertliste, natives Array oder {@link Iterable}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn das gegebene Objekt bzw. eines der Elemente nicht umgewandelt werden kann. */
	public static FEMArray arrayFrom(final Object data) throws IllegalArgumentException {
		return FEMContext._default_.arrayFrom(data);
	}

	/** Diese Methode ist eine Abkürzung für {@code Context.DEFAULT().valueFrom(data)}.
	 * 
	 * @see FEMContext#valueFrom(Object)
	 * @param data Nutzdaten.
	 * @return Wert mit den gegebenen Nutzdaten.
	 * @throws IllegalArgumentException Wenn kein Wert mit den gegebenen Nutzdaten erzeugt werden kann. */
	public static FEMValue valueFrom(final Object data) throws IllegalArgumentException {
		return FEMContext._default_.valueFrom(data);
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptParser} und gibt diesen zurück.
	 * 
	 * @see ScriptParser
	 * @return {@link ScriptParser}. */
	public static ScriptParser scriptParser() {
		return new ScriptParser();
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptCompiler} und gibt diesen zurück.<br>
	 * Der gelieferte {@link ScriptCompiler} nutzt {@link ScriptCompilerHelper#DEFAULT} und lässt {@link ScriptCompiler#useArrayEnabled(boolean) Wertlisten},
	 * {@link ScriptCompiler#useClosureEnabled(boolean) Stapelrahmenbindung} sowie {@link ScriptCompiler#useChainingEnabled(boolean) Funktionsverkettung} zu.
	 * 
	 * @see ScriptCompiler
	 * @return {@link ScriptCompiler}. */
	public static ScriptCompiler scriptCompiler() {
		return new ScriptCompiler();
	}

	/** Diese Methode erzeugt einen neuen {@link ScriptFormatter} und gibt diesen zurück.<br>
	 * Der gelieferte {@link ScriptFormatter} nutzt {@link ScriptFormatterHelper#EMPTY} und keine {@link ScriptFormatter#useIndent(String) Einrückung}.
	 * 
	 * @see ScriptFormatter
	 * @return {@link ScriptFormatter}. */
	public static ScriptFormatter scriptFormatter() {
		return new ScriptFormatter();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseValue()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseValue()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseValue(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseString()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseString()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseString(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseString();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).parseComment()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#parseComment()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '/'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseComment(final String source) throws NullPointerException, IllegalArgumentException {
		return FEM.scriptParser().useSource(source).parseComment();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatValue()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatValue()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatValue(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatString()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatString()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatString(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatString();
	}

	/** Diese Methode ist eine Abkürzung für {@code scriptParser().useSource(source).formatComment()}.
	 * 
	 * @see #scriptParser()
	 * @see ScriptParser#formatComment()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatComment(final String source) throws NullPointerException {
		return FEM.scriptParser().useSource(source).formatComment();
	}

}
