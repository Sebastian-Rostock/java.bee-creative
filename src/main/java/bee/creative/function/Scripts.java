package bee.creative.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.function.Functions.ArrayFunction;
import bee.creative.function.Functions.ClosureFunction;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.function.Functions.LazyFunction;
import bee.creative.function.Functions.ParamFunction;
import bee.creative.function.Functions.ProxyFunction;
import bee.creative.function.Functions.TraceFunction;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Script.Range;
import bee.creative.function.Values.ArrayValue;
import bee.creative.function.Values.FunctionValue;
import bee.creative.util.Parser;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von aufbereiteten Quelltexten.
 * 
 * @see Script
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Scripts {

	/**
	 * Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
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
	 * 
	 * @see #parseRanges()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptParser extends Parser {

		/**
		 * Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}.
		 */
		private int value = Integer.MIN_VALUE;

		/**
		 * Dieses Feld speichert die bisher ermittelten Bereiche.
		 */
		private final List<Range> ranges = new ArrayList<Range>();

		{}

		/**
		 * Diese Methode beginnt das Parsen und sollte nur in Verbindung mit {@link #stopParsing()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		protected final synchronized void startParsing() throws IllegalStateException {
			this.checkIdling();
			this.value = -1;
			this.ranges.clear();
		}

		/**
		 * Diese Methode beendet das Parsen und sollte nur in Verbindung mit {@link #startParsing()} verwendet werden.
		 */
		protected final synchronized void stopParsing() {
			this.value = Integer.MIN_VALUE;
			this.reset();
		}

		/**
		 * Diese Methode prüft den Parsestatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		protected final void checkIdling() throws IllegalStateException {
			if (this.value != Integer.MIN_VALUE) throw new IllegalStateException();
		}

		/**
		 * Diese Methode fügt eine neue {@link Range} mit den gegebenen Parametern hinzu, die bei {@link #index()} endet.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 */
		protected final void range(final int type, final int start) {
			this.ranges.add(new Range((char)type, start, this.index() - start));
		}

		/**
		 * Diese Methode beendet das einlesen des Wertbereichs.
		 */
		protected void closeValue() {
			final int start = this.value;
			if (start < 0) return;
			this.value = -1;
			if (this.index() <= start) return;
			this.range('.', start);
		}

		/**
		 * Diese Methode beginnt das parsen eines Wertbereichs, welches mit {@link #closeValue()} beendet werden muss.
		 */
		protected void parseValue() {
			if (this.value >= 0) return;
			this.value = this.index();
		}

		/**
		 * Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher
		 * das Zeichen als Typ verwendet.
		 * 
		 * @param type Zeichen als Bereichstyp.
		 */
		protected void parseMask(final int type) {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == type) {
					if (this.skip() != type) {
						this.range(type, start);
						return;
					}
				}
			}
			this.range('?', start);
		}

		/**
		 * Diese Methode parst einen Bereich, der mit dem Zeichen <code>'&lt;'</code> beginnt, mit dem Zeichen <code>'&gt;'</code> ende und in dem diese Zeichen nur
		 * paarweise vorkommen dürfen. ein solcher Bereich geparst werden konnte, ist dessen Bereichstyp {@code '!'}. Wenn eine dieser Regeln verletzt wird, ist der
		 * Bereichstyp {@code '?'}.
		 */
		protected void parseName() {
			final int start = this.index();
			for (int symbol = this.skip(); symbol >= 0; symbol = this.skip()) {
				if (symbol == '>') {
					if (this.skip() != '>') {
						this.range('!', start);
						return;
					}
				} else if (symbol == '<') {
					if (this.skip() != '<') {
						break;
					}
				}
			}
			this.range('?', start);
		}

		/**
		 * Diese Methode überspringt alle Zeichen, die kleiner oder gleich dem eerzeichen sind.
		 */
		protected void parseSpace() {
			final int start = this.index();
			for (int symbol = this.skip(); (symbol >= 0) && (symbol <= ' '); symbol = this.skip()) {}
			this.range('_', start);
		}

		/**
		 * Diese Methode erzeugt zum gegebenen Zeichen einen Bereich der Länge 1 und navigiert zum nächsten Zeichen.
		 * 
		 * @param type Zeichen als Bereichstyp.
		 */
		protected void parseSymbol(final int type) {
			final int start = this.index();
			this.skip();
			this.range(type, start);
		}

		/**
		 * Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
		 * 
		 * @param value Eingabe.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		public ScriptParser useSource(final String value) throws NullPointerException, IllegalStateException {
			this.checkIdling();
			super.source(value);
			return this;
		}

		/**
		 * Diese Methode parst die {@link #source() Eingabe} in einen aufbereiteten Quelltext und gibt diesen zurück.
		 * 
		 * @see Script
		 * @see #parseRanges()
		 * @return aufbereiteter Quelltext.
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		public Script parseScript() throws IllegalStateException {
			return new Script(this.source(), this.parseRanges());
		}

		/**
		 * Diese Methode parst die {@link #source() Eingabe} und gibt die Liste der ermittelten Bereiche zurück.
		 * 
		 * @see Range
		 * @return Bereiche.
		 * @throws IllegalStateException Wenn aktuell geparst wird.
		 */
		public Range[] parseRanges() throws IllegalStateException {
			this.startParsing();
			try {
				for (int symbol; true;) {
					switch (symbol = this.symbol()) {
						case -1: {
							this.closeValue();
							return this.ranges.toArray(new Range[this.ranges.size()]);
						}
						case '\'':
						case '\"':
						case '/': {
							this.closeValue();
							this.parseMask(symbol);
							break;
						}
						case '<': {
							this.closeValue();
							this.parseName();
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
							this.closeValue();
							this.parseSymbol(symbol);
							break;
						}
						default: {
							if (symbol <= ' ') {
								this.closeValue();
								this.parseSpace();
							} else {
								this.parseValue();
								this.skip();
							}
						}
					}
				}
			} finally {
				this.stopParsing();
			}
		}

	}

	/**
	 * Diese Klasse implementiert einen Formatter, der Werten und Funktionen in eine Zeichenkette überführen kann. Dieser realisiert damit die entgegen gesetzte
	 * Operation zur Kombination von {@link ScriptParser} und {@link ScriptCompiler}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptFormatter {

		/**
		 * Diese Klasse implementiert eine Markierung, mit welcher die Tiefe und Aktivierung der Einrückung definiert werden kann.
		 * 
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		private static final class Mark {

			/**
			 * Dieses Feld speichert das Objekt, dass in {@link #items} vor jeder Markierung eingefügt wird.
			 */
			public static final Mark DEFAULT = new ScriptFormatter.Mark(0, false, false, false);

			{}

			/**
			 * Dieses Feld speichert die Eigenschaften dieser Markierung.
			 */
			protected int data;

			/**
			 * Dieser Konstruktor initialisiert die Markierung.
			 * 
			 * @param level Einrücktiefe ({@link #level()}).
			 * @param last Endmarkierung ({@link #isLast()}).
			 * @param space Leerzeichen ({@link #isSpace()}).
			 * @param enabled Aktivierung ({@link #isEnabled()}).
			 */
			public Mark(final int level, final boolean last, final boolean space, final boolean enabled) {
				this.data = (level << 3) | (last ? 1 : 0) | (enabled ? 2 : 0) | (space ? 4 : 0);
			}

			{}

			/**
			 * Diese Methode gibt die Tiefe der Einrückung zurück.
			 * 
			 * @return Tiefe der Einrückung.
			 */
			public int level() {
				return this.data >> 3;
			}

			/**
			 * Diese Methode aktiviert die Einrückung.
			 */
			public void enable() {
				this.data |= 2;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt das Ende einer Einrückungsebene markiert.
			 * 
			 * @return {@code true} bei einer Endmarkierung.
			 */
			public boolean isLast() {
				return (this.data & 1) != 0;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn dieses Objekt ein bedingtes Leerzeichen markiert.
			 * 
			 * @return {@code true} bei einem bedingten Leerzeichen.
			 */
			public boolean isSpace() {
				return (this.data & 4) != 0;
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn die Einrückung aktiviert ist.
			 * 
			 * @return Aktivierung.
			 */
			public boolean isEnabled() {
				return (this.data & 2) != 0;
			}

		}

		{}

		/**
		 * Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen.
		 */
		private final List<Object> items = new ArrayList<Object>();

		/**
		 * Dieses Feld speichert den Puffer für {@link #format()}.
		 */
		private final StringBuilder string = new StringBuilder();

		/**
		 * Dieses Feld speichert den Stack der Hierarchieebenen.
		 */
		private final LinkedList<Boolean> indents = new LinkedList<Boolean>();

		/**
		 * Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "    "}.
		 */
		private String indent = "\t";

		/**
		 * Dieses Feld speichert die Formatierungsmethoden.
		 */
		private ScriptFormatterHelper helper = ScriptFormatterHelper.DEFAULT;

		{}

		/**
		 * Diese Methode beginnt das Parsen und sollte nur in Verbindung mit {@link #stopFormatting()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		protected final synchronized void startFormatting() throws IllegalStateException {
			this.checkIdling();
			this.indents.addLast(Boolean.FALSE);
		}

		/**
		 * Diese Methode beendet das Parsen und sollte nur in Verbindung mit {@link #startFormatting()} verwendet werden.
		 */
		protected final synchronized void stopFormatting() {
			this.items.clear();
			this.string.setLength(0);
			this.indents.clear();
		}

		/**
		 * Diese Methode prüft den Parsestatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		protected final void checkIdling() throws IllegalStateException {
			if (this.indents.size() != 0) throw new IllegalStateException();
		}

		/**
		 * Diese Methode prüft den Parsestatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		protected final void checkFormatting() throws IllegalStateException {
			if (this.indents.size() == 0) throw new IllegalStateException();
		}

		/**
		 * Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
		 * 
		 * @param object Markierung.
		 * @return {@code this}.
		 */
		private ScriptFormatter putMark(final Mark object) {
			this.items.add(object);
			return this;
		}

		/**
		 * Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für diese Hierarchieebene aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung
		 * angefügt.
		 * 
		 * @see #putBreakDec()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakInc() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.indents;
			indents.addLast(Boolean.FALSE);
			return this.putMark(Mark.DEFAULT).putMark(new Mark(indents.size(), false, false, false));
		}

		/**
		 * Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Wenn über
		 * {@link #putIndent()} die Einrückung für eine der tieferen Hierarchieebenen aktiviert wurde, wird ein Zeilenumbruch gefolgt von der zur aktuellen Ebene
		 * passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakSpace()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde oder aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakDec() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.indents;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this.putMark(Mark.DEFAULT).putMark(new Mark(value, true, false, indents.removeLast().booleanValue()));
		}

		/**
		 * Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #putIndent()} die Einrückung für die aktuelle
		 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
		 * 
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @see #putIndent()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putBreakSpace() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.indents;
			return this.putMark(Mark.DEFAULT).putMark(new Mark(indents.size(), false, true, indents.getLast().booleanValue()));
		}

		/**
		 * Diese Methode markiert die aktuelle sowie alle übergeordneten Hierarchieebenen als einzurücken und gibt {@code this} zurück. Beginn und Ende einer
		 * Hierarchieebene werden über {@link #putBreakInc()} und {@link #putBreakDec()} markiert.
		 * 
		 * @see #putBreakSpace()
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter putIndent() throws IllegalStateException {
			this.checkFormatting();
			final LinkedList<Boolean> indents = this.indents;
			if (this.indents.getLast().booleanValue()) return this;
			final int value = indents.size();
			for (int i = 0; i < value; i++) {
				indents.set(i, Boolean.TRUE);
			}
			final List<Object> items = this.items;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == Mark.DEFAULT) {
					final Mark token = (Mark)items.get(i + 1);
					if (token.level() == value) {
						if (token.isEnabled()) return this;
						token.enable();
					} else if (token.level() < value) return this;
					i--;
				}
			}
			return this;
		}

		/**
		 * Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück. Zur Umwandlung des Objekts in eine Zeichenkette wird die
		 * Methode {@link Object#toString()} verwendet.
		 * 
		 * @param object Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code object} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 */
		public ScriptFormatter put(final Object object) throws NullPointerException, IllegalStateException {
			this.checkFormatting();
			this.items.add(object.toString());
			return this;
		}

		/**
		 * Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.
		 * <p>
		 * Wenn die Liste leer ist, wird {@code "[]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
		 * separiert über {@link #putValue(Value)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue Hierarchieebene, die vor der
		 * schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken markiert, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putValue(Value)
		 * @see #putBreakSpace()
		 * @see #putBreakInc()
		 * @see #putBreakDec()
		 * @param value Wertliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn einer der Werte nicht formatiert werden kann.
		 */
		public ScriptFormatter putArray(final Array value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			this.checkFormatting();
			final int length = value.length();
			if (length == 0) return this.put("[]");
			(length == 1 ? this : this.putIndent()).put("[").putBreakInc().putValue(value.get(0));
			for (int i = 1; i < length; i++) {
				this.put(";").putBreakSpace().putValue(value.get(i));
			}
			return this.putBreakDec().put("]");
		}

		/**
		 * Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.
		 * <p>
		 * Wenn der Wert ein {@link ArrayValue} oder ein {@link FunctionValue} ist, wird er über {@link #putArray(Array)} bzw. {@link #putScope(Function)} angefügt.
		 * Andernfalls wird er über {@link ScriptFormatterHelper#formatValue(ScriptFormatter, Value)} formatiert.
		 * 
		 * @see #putArray(Array)
		 * @see #putFunction(Function)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
		 */
		public ScriptFormatter putValue(final Value value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value instanceof ArrayValue) return this.putArray((Array)value.data());
			if (value instanceof FunctionValue) return this.putScope((Function)value.data());
			this.helper.formatValue(this, value);
			return this;
		}

		/**
		 * Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.
		 * <p>
		 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(Function)} angefügt.
		 * 
		 * @param value parametrisierte Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn einer der Werte nicht formatiert werden kann.
		 */
		public ScriptFormatter putScope(final Function value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			return this.put("{: ").putFunction(value).put("}");
		}

		/**
		 * Diese Methode fügt den Quelltext der gegebenen Parameterfunktionen an und gibt {@code this} zurück.
		 * <p>
		 * Wenn die Liste leer ist, wird {@code "()"} angefügt. Andernfalls werden die Parameterfunktionen in {@code "("} und {@code ")"} eingeschlossen sowie mit
		 * {@code ";"} separiert über {@link #putFunction(Function)} angefügt. Nach der öffnenden Klammer {@link #putBreakInc() beginnt} dabei eine neue
		 * Hierarchieebene, die vor der schließenden Klammer {@link #putBreakDec() endet}. Nach jedem Trennzeichen wird ein {@link #putBreakSpace() bedingtes
		 * Leerzeichen} eingefügt.<br>
		 * Die aktuelle Hierarchieebene wird als einzurücken markiert, wenn die Funktionsliste mehr als ein Element enthält.
		 * 
		 * @param value Funktionsliste.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann.
		 */
		public ScriptFormatter putParams(final Function... value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			final int length = value.length;
			if (length == 0) return this.put("()");
			(length > 1 ? this.putIndent() : this).put("(").putBreakInc().putFunction(value[0]);
			for (int i = 1; i < length; i++) {
				this.put(";").putBreakSpace().putFunction(value[i]);
			}
			this.putBreakDec().put(")");
			return this;
		}

		/**
		 * Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.
		 * <p>
		 * Wenn die Funktion eine {@link ArrayFunction} ist, wird {@code "$"} angefügt. Wenn die Funktion eine {@link ParamFunction} ist, wird {@code "$i"}
		 * angefügt, wobei {@code i} der um eins vergrößerte Parameterindex ist. Wenn die Funktion eine {@link ValueFunction} ist, wird ihr Wert via
		 * {@link #putValue(Value)} angefügt. Wenn die Funktion eine {@link ClosureFunction} ist, wird ihre Funktion über {@link #putScope(Function)} angefügt. Wenn
		 * die Funktion eine {@link LazyFunction} oder eine {@link TraceFunction} ist, wird ihre Funktion über {@link #putFunction(Function)} angefügt. Wenn die
		 * Funktion eine {@link ProxyFunction} ist, werden ihr Name über {@link #put(Object)} und ihre Funktion über {@link #putFunction(Function)} angefügt. Wenn
		 * die Funktion eine {@link CompositeFunction} ist, werden ihre aufzurufende Funktion über {@link #putFunction(Function)} und ihre Parameterfunktionen über
		 * {@link #putParams(Function...)} angefügt. Andernfalls wird die Funktion über {@link ScriptFormatterHelper#formatFunction(ScriptFormatter, Function)}
		 * formatiert.
		 * 
		 * @param value Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann.
		 */
		public ScriptFormatter putFunction(final Function value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value instanceof ArrayFunction) return this.put("$");
			if (value instanceof ParamFunction) return this.put("$").put(String.valueOf(((ParamFunction)value).index() + 1));
			if (value instanceof ValueFunction) return this.putValue(((ValueFunction)value).value());
			if (value instanceof LazyFunction) return this.putFunction(((LazyFunction)value).function());
			if (value instanceof TraceFunction) return this.putFunction(((TraceFunction)value).function());
			if (value instanceof ClosureFunction) return this.putScope(((ClosureFunction)value).function());
			if (value instanceof ProxyFunction) {
				final ProxyFunction function = (ProxyFunction)value;
				return this.put(function.name()).putScope(function.function());
			}
			if (value instanceof CompositeFunction) {
				final CompositeFunction function = (CompositeFunction)value;
				return this.putFunction(function.function()).putParams(function.params());
			}
			this.helper.formatFunction(this, value);
			return this;
		}

		/**
		 * Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück.
		 * 
		 * @return Zeichenkette zur Einrückung .
		 */
		public String getIndent() {
			return this.indent;
		}

		/**
		 * Diese Methode gibt die genutzten Formatierungsmethoden zurück.
		 * 
		 * @return Formatierungsmethoden.
		 */
		public ScriptFormatterHelper getHelper() {
			return this.helper;
		}

		/**
		 * Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück.
		 * 
		 * @param indent Zeichenkette zur Einrückung (z.B. {@code "\t"} oder {@code "  "}).
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		public ScriptFormatter useIndent(final String indent) throws NullPointerException, IllegalStateException {
			if (indent == null) throw new NullPointerException("indent = null");
			this.checkIdling();
			this.indent = indent;
			return this;
		}

		/**
		 * Diese Methode gibt setzt die zu nutzenden Formatierungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		public ScriptFormatter useHelper(final ScriptFormatterHelper helper) throws NullPointerException, IllegalStateException {
			if (helper == null) throw new NullPointerException("helper = null");
			this.checkIdling();
			this.helper = helper;
			return this;
		}

		/**
		 * Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
		 * 
		 * @see #put(Object)
		 * @return Quelltext.
		 */
		private String format() {
			final String indent = this.indent;
			final List<Object> items = this.items;
			final StringBuilder string = this.string;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == Mark.DEFAULT) {
					final Mark token = (Mark)items.get(i++);
					if (token.isEnabled()) {
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

		/**
		 * Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Die Werte werden mit {@code ';'} separiert.
		 * 
		 * @param value Wert.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatValue(final Value... value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			this.startFormatting();
			try {
				final int length = value.length;
				if (length == 0) return "";
				(length == 1 ? this : this.putIndent()).putValue(value[0]);
				for (int i = 1; i < length; i++) {
					this.put(";").putBreakSpace().putValue(value[i]);
				}
				return this.format();
			} finally {
				this.stopFormatting();
			}
		}

		/**
		 * Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Die Funktionen werden mit {@code ';'} separiert.
		 * 
		 * @param value Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann.
		 */
		public String formatFunction(final Function... value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			this.startFormatting();
			try {
				final int length = value.length;
				if (length == 0) return "";
				(length == 1 ? this : this.putIndent()).putFunction(value[0]);
				for (int i = 1; i < length; i++) {
					this.put(";").putBreakSpace().putFunction(value[i]);
				}
				return this.format();
			} finally {
				this.stopFormatting();
			}
		}

	}

	/**
	 * Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link ScriptFormatter#putValue(Value)} und
	 * {@link ScriptFormatter#putFunction(Function)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public interface ScriptFormatterHelper {

		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden den Wert bzw. die Funktion über {@link Object#toString()} formatieren.
		 */
		static ScriptFormatterHelper DEFAULT = new ScriptFormatterHelper() {

			@Override
			public void formatValue(final Scripts.ScriptFormatter target, final Value value) throws IllegalArgumentException {
				target.put(value.toString());
			}

			@Override
			public void formatFunction(final Scripts.ScriptFormatter target, final Function function) throws IllegalArgumentException {
				target.put(function.toString());
			}

		};

		/**
		 * Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
		 */
		public void formatValue(ScriptFormatter target, Value value) throws IllegalArgumentException;

		/**
		 * Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann.
		 */
		public void formatFunction(ScriptFormatter target, Function function) throws IllegalArgumentException;

	}

	/**
	 * Diese Klasse implementiert einen Kompiler für {@link Value Werte} und {@link Function Funktionen}.
	 * <p>
	 * Die Bereichestypen eines Quelltexts haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link Array}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link Array} als Funktion bzw. Parameterwert
	 * kompiliert wird.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über {@link ScriptCompilerHelper#compileName(ScriptCompiler)}
	 * aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu
	 * genau einer Funktion kompiliert werden.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.valueOf(0)}). Andernfalls steht der Bereich für {@link ArrayFunction#VIEW}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileValue(ScriptCompiler)} in Werte überführt. Funktionen werden hierbei als
	 * {@link FunctionValue}s angegeben.</li>
	 * </ul>
	 * 
	 * @see #compileValue()
	 * @see #compileFunction()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptCompiler {

		/**
		 * Dieses Feld speichert den aktuellen Bereich.
		 */
		private Range range = Range.EMPTY;

		/**
		 * Dieses Feld speichert die Kompilationsmethoden.
		 */
		private ScriptCompilerHelper helper = ScriptCompilerHelper.DEFAULT;

		/**
		 * Dieses Feld speichert den {@link Iterator} über die Bereiche von {@link #script()}.
		 */
		private Iterator<Range> iterator;

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		private Script script = Script.EMPTY;

		/**
		 * Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter.
		 */
		private final Map<String, ProxyFunction> proxies = new HashMap<>();

		/**
		 * Dieses Feld speichert die Parameternamen.
		 */
		private LinkedList<String> params = new LinkedList<String>();

		/**
		 * Dieses Feld speichert die Zulässigkeit von Wertlisten.
		 */
		private boolean arrayEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit von Funktionszeigern.
		 */
		private boolean handlerEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Bindung des Ausführungskontexts.
		 */
		private boolean closureEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen.
		 */
		private boolean chainingEnabled = true;

		{}

		/**
		 * Diese Methode beginnt die Kompilation und sollte nur in Verbindung mit {@link #stopCompiling()} verwendet werden.
		 * 
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		protected final synchronized void startCompiling() throws IllegalStateException {
			this.checkIdling();
			this.iterator = this.script.iterator();
			this.skip();
		}

		/**
		 * Diese Methode beendet die Kompilation und sollte nur in Verbindung mit {@link #startCompiling()} verwendet werden.
		 */
		protected final synchronized void stopCompiling() {
			this.proxies.clear();
			this.iterator = null;
		}

		/**
		 * Diese Methode prüft den Kompilationsstatus.
		 * 
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		protected final void checkIdling() throws IllegalStateException {
			if (this.iterator != null) throw new IllegalStateException();
		}

		/**
		 * Diese Methode überspringt den aktuellen Bereich und gibt den nächsten oder {@link Range#EMPTY} zurück.<br>
		 * Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @return aktueller Bereich oder {@link Range#EMPTY}.
		 */
		protected final Range skip() {
			if (!this.iterator.hasNext()) return this.range = Range.EMPTY;
			return this.range = this.iterator.next();
		}

		/**
		 * Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den ersten bedeutsamen Bereich oder {@link Range#EMPTY}
		 * zurück. Der {@link #range() aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereich oder {@link Range#EMPTY}.
		 */
		protected Range skipSpace() {
			for (int type = this.range.type; (type == '_') || (type == '/'); type = this.skip().type) {}
			return this.range;
		}

		/**
		 * Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
		 * 
		 * @param string Zeichenkette.
		 * @return Zahl.
		 */
		protected int doCompileIndex(final String string) {
			if ((string == null) || string.isEmpty()) return -1;
			final char symbol = string.charAt(0);
			if ((symbol < '0') || (symbol > '9')) return -1;
			try {
				return Integer.parseInt(string);
			} catch (final NumberFormatException e) {
				return -1;
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in einen {@link Value} oder eine {@link Function} und gibt diesen zurück.
		 * 
		 * @return Wertliste.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Object doCompileArray() throws ScriptException {
			if (!this.arrayEnabled) throw new ScriptException() //
				.setRange(this.range).setScript(this.script).setHint(" Wertlisten sind nicht zulässig.");
			this.skip();
			if (this.skipSpace().type == ']') {
				this.skip();
				return ArrayValue.valueOf(Array.EMPTY);
			}
			boolean value = true;
			for (final LinkedList<Function> array = new LinkedList<Function>(); true;) {
				final Function item = this.doCompileFunction();
				array.add(item);
				value = value && (item instanceof ValueFunction);
				switch (this.skipSpace().type) {
					case ';':
						this.skip();
						this.skipSpace();
						break;
					case ']': {
						this.skip();
						final int size = array.size();
						if (!value) return CompositeFunction.valueOf(ArrayFunction.COPY, array.toArray(new Function[size]));
						final Value[] values = new Value[size];
						for (int i = 0; i < size; i++) {
							values[i] = array.get(i).execute(null);
						}
						return ArrayValue.valueOf(Array.valueOf(values));
					}
					default:
						throw new ScriptException() //
							.setRange(this.range).setScript(this.script).setHint(" Zeichen «;» oder «]» erwartet.");
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link Value}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Value doCompileArrayAsValue() throws ScriptException {
			final Object array = this.doCompileArray();
			if (array instanceof Value) return (Value)array;
			throw new ScriptException() //
				.setRange(this.range).setScript(this.script).setHint(" Wertliste erwartet.");
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link Function} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link Function}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Function doCompileArrayAsFunction() throws ScriptException {
			final Object array = this.doCompileArray();
			if (array instanceof Value) return ValueFunction.valueOf(array);
			return (Function)array;
		}

		/**
		 * Diese Methode kompiliert den beim aktuellen Bereich beginnende Wert und gibt diesen zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Value doCompileValue() throws ScriptException {
			final Value value;
			switch (this.skipSpace().type) {
				case 0:
				case '$':
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}':
					throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Wert erwartet.");
				case '[':
					return this.doCompileArrayAsValue();
				case '{':
					if (this.closureEnabled) throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Ungebundene Funktion unzulässig.");
					return FunctionValue.valueOf(this.doCompileScope());
				default:
					try {
						value = this.helper.compileValue(this);
						if (value == null) throw new ScriptException() //
							.setRange(this.range).setScript(this.script);
					} catch (final ScriptException cause) {
						throw cause;
					} catch (final RuntimeException cause) {
						throw new ScriptException(cause) //
							.setRange(this.range).setScript(this.script);
					}
					this.skip();
					return value;
			}
		}

		/**
		 * Diese Methode kompiliert den aktuellen Bereich zu einen Funktions- oder Parameternamen und gibt diesen oder {@code null} zurück.
		 * 
		 * @return Funktions- oder Parametername oder {@code null}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected String doCompileName() throws ScriptException {
			switch (this.skipSpace().type) {
				case 0:
				case '$':
				case '(':
				case '[':
				case '{':
					throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
				case ':':
				case ';':
				case ')':
				case '}':
				case ']':
					return null;
			}
			final String name;
			try {
				name = this.helper.compileName(this);
				if (name.isEmpty()) throw new IllegalArgumentException();
			} catch (final ScriptException e) {
				throw e;
			} catch (final RuntimeException e) {
				throw new ScriptException(e) //
					.setRange(this.range).setScript(this.script).setHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
			}
			this.skip();
			return name;
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
		 * 
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected ProxyFunction doCompileProxy() throws ScriptException {
			final String name = this.doCompileName();
			if (name == null) throw new ScriptException() //
				.setRange(this.range).setScript(this.script).setHint(" Funktionsname erwartet.");
			final ProxyFunction result = this.proxy(name);
			if (this.skipSpace().type != '{') throw new ScriptException() //
				.setRange(this.range).setScript(this.script).setHint(" Parametrisierter Funktionsaufruf erwartet.");
			this.skip();
			final Function function = this.doCompileScope();
			result.set(function);
			return result;
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende, parametrisierte Funktion in einen {@link FunctionValue} und gibt diesen zurück.
		 * 
		 * @return Funktion als {@link FunctionValue}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Function doCompileScope() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this.skipSpace().type == 0) throw new ScriptException() //
					.setRange(this.range).setScript(this.script);
				final String name = this.doCompileName();
				if (name != null) {
					if (this.doCompileIndex(name) >= 0) throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Parametername erwartet.");
					this.params.add(count++, name);
				}
				switch (this.skipSpace().type) {
					case ';':
						if (name == null) throw new ScriptException() //
							.setRange(this.range).setScript(this.script).setHint(" Parametername oder Zeichen «:» erwartet.");
						this.skip();
						break;
					case ':': {
						this.skip();
						final Function function = this.doCompileFunction();
						if (this.skipSpace().type != '}') throw new ScriptException() //
							.setRange(this.range).setScript(this.script).setHint(" Zeichen «}» erwartet.");
						this.skip();
						this.params.subList(0, count).clear();
						return function;
					}
					default:
						throw new ScriptException() //
							.setRange(this.range).setScript(this.script);
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected Function doCompileFunction() throws ScriptException {
			Function function;
			boolean chained = false;
			switch (this.skipSpace().type) {
				case '$': {
					this.skip();
					final String name = this.doCompileName();
					if (name == null) return ArrayFunction.VIEW;
					int index = this.doCompileIndex(name);
					if (index < 0) {
						index = this.params.indexOf(name);
						if (index < 0) throw new ScriptException() //
							.setRange(this.range).setScript(this.script) //
							.setHint(String.format(" Parametername «%s» ist unbekannt.", name));
					} else if (index > 0) {
						index--;
					} else throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Parameterindex «0» ist unzulässig.");
					return ParamFunction.valueOf(index);
				}
				case '{': {
					function = this.doCompileScope();
					if (this.skipSpace().type != '(') return this.closureEnabled ? //
						ClosureFunction.valueOf(function) : ValueFunction.valueOf(FunctionValue.valueOf(function));
					if (!this.chainingEnabled) throw new ScriptException() //
						.setRange(this.range).setScript(this.script).setHint(" Funktionsverkettungen ist nicht zulässsig.");
					break;
				}
				case '[': {
					return this.doCompileArrayAsFunction();
				}
				default: {
					final Value value = this.doCompileValue();
					if (!(value instanceof FunctionValue)) return ValueFunction.valueOf(value);
					if (this.skipSpace().type != '(') {
						if (this.handlerEnabled) return ValueFunction.valueOf(value);
						throw new ScriptException() //
							.setRange(this.range).setScript(this.script).setHint(" Funktionsverweise sind nicht zulässig.");
					}
					function = ((FunctionValue)value).data();
				}
			}
			do {
				if (chained && !this.chainingEnabled) throw new ScriptException() //
					.setRange(this.range).setScript(this.script).setHint(" Funktionsverkettungen ist nicht zulässsig.");
				this.skip(); // '('
				this.skipSpace();
				for (final LinkedList<Function> functions = new LinkedList<Function>(); true;) {
					if (this.range.type == ')') {
						this.skip();
						function = CompositeFunction.valueOf(function, chained, functions.toArray(new Function[functions.size()]));
						break;
					}
					functions.add(this.doCompileFunction());
					switch (this.skipSpace().type) {
						default:
							throw new ScriptException() //
								.setRange(this.range).setScript(this.script).setHint(" Zeichen «;» oder «)» erwartet.");
						case ';':
							this.skip();
						case ')':
					}
				}
				chained = true;
			} while (this.skipSpace().type == '(');
			return function;
		}

		/**
		 * Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name des Platzhalters.
		 * @return Platzhalterfunktion.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 */
		public synchronized final ProxyFunction proxy(final String name) throws NullPointerException {
			ProxyFunction result = this.proxies.get(name);
			if (result != null) return result;
			this.proxies.put(name, result = new ProxyFunction(name));
			return result;
		}

		/**
		 * Diese Methode gibt den aktuellen Bereich zurück.
		 * 
		 * @return aktueller Bereich.
		 */
		public final Range range() {
			return this.range;
		}

		/**
		 * Diese Methode gibt den zu kompilierenden Quelltext zurück.
		 * 
		 * @return Quelltext.
		 */
		public final Script script() {
			return this.script;
		}

		/**
		 * Diese Methode gibt die genutzten Kompilationsmethoden zurück.
		 * 
		 * @return Kompilationsmethoden.
		 */
		public final ScriptCompilerHelper helper() {
			return this.helper;
		}

		/**
		 * Diese Methode gibt die Liste der aktellen Parameternamen zurück.
		 * 
		 * @return Parameternamen.
		 */
		public final List<String> params() {
			return Collections.unmodifiableList(this.params);
		}

		/**
		 * Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
		 * 
		 * @see Range#extract(String)
		 * @return Aktuelle Zeichenkette.
		 */
		public final String section() {
			return this.range.extract(this.script.source());
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Wertlisten.
		 */
		public final boolean isArrayEnabled() {
			return this.arrayEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link ScriptCompilerHelper#compileValue(ScriptCompiler)} als {@link FunctionValue}
		 * gelieferten Funktionen als Funktionszeiger zu {@link ValueFunction}s kompiliert werden dürfen (z.B {@code SORT(array; compFun)}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Funktionszeigern.
		 */
		public final boolean isHandlerEnabled() {
			return this.handlerEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link ClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Ausführungskontexts.
		 */
		public final boolean isClosureEnabled() {
			return this.closureEnabled;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
		 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
		 * 
		 * @see #compileFunction()
		 * @see CompositeFunction#chained()
		 * @see CompositeFunction#execute(Scope)
		 * @return Zulässigkeit der Verkettung von Funktionen.
		 */
		public final boolean isChainingEnabled() {
			return this.chainingEnabled;
		}

		/**
		 * Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
		 * 
		 * @param value Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useScript(final Script value) throws NullPointerException, IllegalStateException {
			value.length();
			this.checkIdling();
			this.script = value;
			return this;
		}

		/**
		 * Diese Methode setzt die zu nutzenden Kompilationsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Kompilationsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useHelper(final ScriptCompilerHelper value) throws NullPointerException, IllegalStateException {
			if (value == null) throw new NullPointerException();
			this.checkIdling();
			this.helper = value;
			return this;
		}

		/**
		 * Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useParams(final String... value) throws NullPointerException, IllegalStateException {
			return this.useParams(Arrays.asList(value.clone()));
		}

		/**
		 * Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
		 * 
		 * @param value Parameternamen.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useParams(final List<String> value) throws NullPointerException, IllegalStateException {
			if (value.contains(null)) throw new NullPointerException();
			this.checkIdling();
			this.params = new LinkedList<String>(value);
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Wertlisten.
		 * 
		 * @see #isArrayEnabled()
		 * @param value Zulässigkeit von Wertlisten.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useArrayEnabled(final boolean value) throws IllegalStateException {
			this.checkIdling();
			this.arrayEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Funktionszeigern.
		 * 
		 * @see #isHandlerEnabled()
		 * @param value Zulässigkeit von Funktionszeigern.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useHandlerEnabled(final boolean value) throws IllegalStateException {
			this.checkIdling();
			this.handlerEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Bindung des Ausführungskontexts.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Ausführungskontexts.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useClosureEnabled(final boolean value) throws IllegalStateException {
			this.checkIdling();
			this.closureEnabled = value;
			return this;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
		 * 
		 * @see #isChainingEnabled()
		 * @param value Zulässigkeit der Verkettung von Funktionen.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ScriptCompiler useChainingEnabled(final boolean value) throws IllegalStateException {
			this.checkIdling();
			this.chainingEnabled = value;
			return this;
		}

		/**
		 * Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public Value compileValue() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final Value result = this.doCompileValue();
				if (this.skipSpace().type == 0) return result;
				throw new ScriptException() //
					.setRange(this.range).setScript(this.script).setHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.stopCompiling();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück. Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert
		 * sein.
		 * 
		 * @return Werte.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public Value[] compileValues() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final List<Value> result = new ArrayList<Value>();
				while (true) {
					result.add(this.doCompileValue());
					switch (this.skipSpace().type) {
						case 0:
							return result.toArray(new Value[result.size()]);
						case ';':
							this.skip();
					}
				}
			} finally {
				this.stopCompiling();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück. Die Parameterfunktion müssen durch Bereiche vom Typ
		 * {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem {@link ScriptCompilerHelper#compileName(ScriptCompiler) Namen} und ist sonst durch
		 * eine parametrisierte Funktion gegeben.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public ProxyFunction[] compileProxies() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final List<ProxyFunction> result = new ArrayList<ProxyFunction>();
				while (true) {
					result.add(this.doCompileProxy());
					switch (this.skipSpace().type) {
						case 0:
							return result.toArray(new ProxyFunction[result.size()]);
						case ';':
							this.skip();
					}
				}
			} finally {
				this.stopCompiling();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public Function compileFunction() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final Function result = this.doCompileFunction();
				if (this.skipSpace().type == 0) return result;
				throw new ScriptException() //
					.setRange(this.range).setScript(this.script).setHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.stopCompiling();
			}
		}

		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
		 * separiert sein.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public Function[] compileFunctions() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final List<Function> result = new ArrayList<Function>();
				while (true) {
					result.add(this.doCompileFunction());
					switch (this.skipSpace().type) {
						case 0:
							return result.toArray(new Function[result.size()]);
						case ';':
							this.skip();
					}
				}
			} finally {
				this.stopCompiling();
			}
		}

	}

	/**
	 * Diese Schnittstelle definiert Kompilationsmethoden, die in den Methoden {@link ScriptCompiler#compileValue()} und {@link ScriptCompiler#compileFunction()}
	 * zur Übersetzung von Quelltexten in Werte, Funktionen bzw. Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptCompilerHelper {

		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden immer {@code null} liefern.
		 */
		static ScriptCompilerHelper DEFAULT = new ScriptCompilerHelper() {

			@Override
			public String compileName(final ScriptCompiler compiler) throws ScriptException {
				return null;
			}

			@Override
			public Value compileValue(final ScriptCompiler compiler) throws ScriptException {
				return null;
			}

		};

		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
		 * 
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Funktions- bzw. Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
		 */
		public String compileName(ScriptCompiler compiler) throws ScriptException;

		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebenen Wert zurück. Funktionen müssen als {@link FunctionValue}
		 * geliefert werden.
		 * 
		 * @see ScriptCompiler#proxy(String)
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @return Wert als {@link Value} oder Funktion als {@link FunctionValue}.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält.
		 */
		public Value compileValue(ScriptCompiler compiler) throws ScriptException;

	}

	/**
	 * Diese Klasse implementiert die {@link IllegalArgumentException}, die bei Syntaxfehlern von einem {@link ScriptParser} oder {@link ScriptCompiler} ausgelöst
	 * wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptException extends IllegalArgumentException {

		/**
		 * Dieses Feld speichert die Serial-Version-UID.
		 */
		private static final long serialVersionUID = -918623847189389909L;

		{}

		/**
		 * Dieses Feld speichert den Hinweis zum erwarteten Inhalt des Bereichs.
		 */
		String hint = "";

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		Script script = Script.EMPTY;

		/**
		 * Dieses Feld speichert den Bereich, in dem der Syntaxfehler entdeckt wurde.
		 */
		Range range = Range.EMPTY;

		/**
		 * Dieser Konstruktor initialisiert die {@link ScriptException} ohne Ursache.
		 */
		public ScriptException() {
			super();
		}

		/**
		 * Dieser Konstruktor initialisiert die {@link ScriptException} mit Ursache.
		 * 
		 * @param cause Urssache.
		 */
		public ScriptException(final Throwable cause) {
			super(cause);
		}

		{}

		/**
		 * Diese Methode gibt den Hinweis zum erwarteten Inhalt des Bereichs zurück.
		 * 
		 * @see #getRange()
		 * @return Hinweis oder {@code null}.
		 */
		public String getHint() {
			return this.hint;
		}

		/**
		 * Diese Methode gibt den Bereich zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Bereich.
		 */
		public Range getRange() {
			return this.range;
		}

		/**
		 * Diese Methode gibt Quelltext zurück, in dem der Syntaxfehler auftrat.
		 * 
		 * @return Quelltext.
		 */
		public Script getScript() {
			return this.script;
		}

		/**
		 * Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #getHint()
		 * @param value Hinweis.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptException setHint(final String value) throws NullPointerException {
			value.length();
			this.hint = value;
			return this;
		}

		/**
		 * Diese Methode setzt den Bereich und gibt {@code this} zurück.
		 * 
		 * @see #getRange()
		 * @param value Bereich.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptException setRange(final Range value) throws NullPointerException {
			value.type();
			this.range = value;
			return this;
		}

		/**
		 * Diese Methode setzt den Quelltext und gibt {@code this} zurück.
		 * 
		 * @see #getScript()
		 * @param value Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptException setScript(final Script value) throws NullPointerException {
			value.length();
			this.script = value;
			return this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMessage() {
			return (this.range == Range.EMPTY //
				? "Unerwartetes Ende der Zeichenkette." //
				: "Unerwartete Zeichenkette «" + this.range.extract(this.script.source) + "» an Position " + this.range.start + ".") //
				+ this.hint;
		}
	}

	{}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptParser} und gibt diesen zurück.
	 * 
	 * @see ScriptParser
	 * @return {@link ScriptParser}.
	 */
	public static ScriptParser scriptParser() {
		return new ScriptParser();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptCompiler} und gibt diesen zurück.
	 * 
	 * @see ScriptCompiler
	 * @return {@link ScriptCompiler}.
	 */
	public static ScriptCompiler scriptCompiler() {
		return new ScriptCompiler();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ScriptFormatter} und gibt diesen zurück.
	 * 
	 * @see ScriptFormatter
	 * @return {@link ScriptFormatter}.
	 */
	public static ScriptFormatter scriptFormatter() {
		return new ScriptFormatter();
	}

}
