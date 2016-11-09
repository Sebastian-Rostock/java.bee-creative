package bee.creative.fem;

import java.util.ArrayList;
import java.util.List;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Parser;

/** Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
 * {@link FEMCompiler} in Werte und Funktionen überführt werden.
 * <p>
 * Die Erzeugung von {@link Range Bereichen} erfolgt gemäß dieser Regeln:
 * <ul>
 * <li>Die Zeichen {@code '/'}, {@code '\''} und {@code '\"'} erzeugen je einen Bereich, der das entsprechende Zeichen als Bereichstyp verwendet, mit dem
 * Zeichen beginnt und endet sowie das Zeichen zwischen dem ersten und letzten nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der
 * Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.</li>
 * <li>Das Zeichen <code>'&lt;'</code> erzeugen einen Bereich, der mit dem Zeichen <code>'&gt;'</code> endet und beide Zeichen zwischen dem ersten und letzten
 * jeweils nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.
 * Andernfalls hat er den Bereichstyp {@code '!'}.</li>
 * <li>Jedes der Zeichen {@code '$'}, {@code ';'}, {@code ':'}, {@code '('}, {@code ')'}, <code>'{'</code> und <code>'}'</code> erzeugt eine eigene Bereich, der
 * das entsprechende Zeichen als Bereichstyp verwendet.</li>
 * <li>Sequenzen aus Zeichen kleiner gleich dem Leerzeichen werden zu Bereichen mit dem Bereichstyp {@code '_'}.</li>
 * <li>Alle restlichen Zeichenfolgen werden zu Bereichen mit dem Bereichstyp {@code '.'}.</li>
 * </ul>
 * <p>
 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
 *
 * @see #parseRanges()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMParser extends Parser {

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).parseValue()}.
	 *
	 * @see #parseValue()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit <code>'&lt;'</code> und <code>'&gt;'</code>.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseValue(final String source) throws NullPointerException, IllegalArgumentException {
		return new FEMParser().useSource(source).parseValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).parseString()}.
	 *
	 * @see #parseString()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '\''} bzw. {@code '\"'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseString(final String source) throws NullPointerException, IllegalArgumentException {
		return new FEMParser().useSource(source).parseString();
	}

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).parseComment()}.
	 *
	 * @see #parseComment()
	 * @param source Eingabe.
	 * @return Eingabe ohne Maskierung mit {@code '/'}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist. */
	public static String parseComment(final String source) throws NullPointerException, IllegalArgumentException {
		return new FEMParser().useSource(source).parseComment();
	}

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).formatValue()}.
	 *
	 * @see #formatValue()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatValue(final String source) throws NullPointerException {
		return new FEMParser().useSource(source).formatValue();
	}

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).formatString()}.
	 *
	 * @see #formatString()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatString(final String source) throws NullPointerException {
		return new FEMParser().useSource(source).formatString();
	}

	/** Diese Methode ist eine Abkürzung für {@code new FEMParser().useSource(source).formatComment()}.
	 *
	 * @see #formatComment()
	 * @param source Eingabe.
	 * @return Eingabe mit Maskierung.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public static String formatComment(final String source) throws NullPointerException {
		return new FEMParser().useSource(source).formatComment();
	}

	{}

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

	/** Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher das
	 * Zeichen als Typ verwendet.
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

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #source() Eingabe} keines der in {@link #_parseSource_()} erkannten Zeichen enthält, d.h.
	 * wenn das Parsen der Eingabe via {@link #parseRanges()} genau einen Bereich mit dem Typ {@code '.'} ergibt, welcher über {@link #_openValue_()} und
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
	public synchronized final FEMParser useSource(final String value) throws NullPointerException, IllegalStateException {
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