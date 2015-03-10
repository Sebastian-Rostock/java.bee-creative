package bee.creative.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.function.Functions.ArrayFunction;
import bee.creative.function.Functions.ClosureFunction;
import bee.creative.function.Functions.CompositeFunction;
import bee.creative.function.Functions.LazyFunction;
import bee.creative.function.Functions.ParamFunction;
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
	 * 
	 * @see #parse()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ScriptParser extends Parser {

		/**
		 * Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}.
		 */
		protected int value;

		/**
		 * Dieses Feld speichert die bisher ermittelten Bereiche.
		 */
		protected final List<Range> ranges;

		/**
		 * Dieser Konstruktor initialisiert die Eingabe.
		 * 
		 * @param source Eingabe.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ScriptParser(final String source) throws NullPointerException {
			super(source);
			this.value = -1;
			this.ranges = new ArrayList<Range>((this.length() / 10) + 5);
		}

		{}

		/**
		 * Diese Methode parst die {@link #source() Eingabe} und gibt die Liste der ermittelten Bereiche zurück. Die Erzeugung von {@link Range Bereiche} erfolgt
		 * gemäß dieser Regeln:
		 * <ul>
		 * <li>Die Zeichen {@code '/'}, {@code '\''} und {@code '\"'} erzeugen je einen Bereich, der das entsprechende Zeichen als Bereichstyp verwendet, mit dem
		 * Zeichen beginnt und endet sowie das Zeichen zwischen dem ersten und letzten nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet
		 * der Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.</li>
		 * <li>Das Zeichen <code>'&lt;'</code> erzeugen einen Bereich, der mit dem Zeichen <code>'&gt;'</code> endet und beide Zeichen zwischen dem ersten und
		 * letzten jeweils nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der Bereich an der Stelle des Fehlers und hat den Bereichstyp
		 * {@code '?'}. Andernfalls hat er den Bereichstyp {@code '!'}.</li>
		 * <li>Jedes der Zeichen {@code '$'}, {@code ';'}, {@code ':'}, {@code '('}, {@code ')'}, <code>'{'</code> und <code>'}'</code> erzeugt eine eigene Bereich,
		 * der das entsprechende Zeichen als Bereichstyp verwendet.</li>
		 * <li>Sequenzen aus Zeichen kleiner gleich dem Leerzeichen werden zu Bereichen mit dem Bereichstyp {@code '_'}.</li>
		 * <li>Alle restlichen Zeichenfolgen werden zu Bereichen mit dem Bereichstyp {@code '.'}.</li>
		 * </ul>
		 * 
		 * @return Bereiche.
		 */
		public List<Range> parse() {
			for (int symbol; true;) {
				switch (symbol = this.symbol()) {
					case -1: {
						this.closeValue();
						return this.ranges;
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
		}

		{}

		/**
		 * Diese Methode fügt eine neue {@link Range} mit den gegebenen Parametern hinzu, die bei {@link #index()} endet.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 */
		protected void range(final int type, final int start) {
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
		protected static final class Mark {

			/**
			 * Dieses Feld speichert die Eigenschaften dieser Markierung.
			 */
			protected int data;

			/**
			 * Dieser Konstruktor initialisiert die Markierung.
			 * 
			 * @param level Einrücktiefe.
			 * @param last Endmarkierung.
			 * @param enabled Aktivierung.
			 */
			Mark(final int level, final boolean last, final boolean enabled) {
				this.data = (level << 2) | (last ? 1 : 0) | (enabled ? 2 : 0);
			}

			/**
			 * Diese Methode gibt die Tiefe der Einrückung zurück.
			 * 
			 * @return Tiefe der Einrückung.
			 */
			public int level() {
				return this.data >> 2;
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
		 * Dieses Feld speichert das Objekt, dass in {@link #items} vor jeder Markierung eingefügt wird.
		 */
		protected static final Mark MARK = new Mark(0, false, false);

		{}

		/**
		 * Dieses Feld speichert die Zeichenkette zur Einrückung, z.B. {@code "\t"} oder {@code "    "}.
		 */
		protected final String indent;

		/**
		 * Dieses Feld speichert die bisher gesammelten Zeichenketten und Markierungen.
		 */
		protected final List<Object> items;

		/**
		 * Dieses Feld speichert den Stack der Hierarchieebenen.
		 */
		protected final LinkedList<Boolean> indents;

		/**
		 * Dieses Feld speichert die Formatierungsmethoden.
		 */
		protected final ScriptFormatterHelper helper;

		/**
		 * Dieser Konstruktor initialisiert die Formatierungsmethoden.<br>
		 * Der Einzug erfolgt mit {@code "\t"}.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ScriptFormatter(final ScriptFormatterHelper helper) throws NullPointerException {
			this(helper, "\t");
		}

		/**
		 * Dieser Konstruktor initialisiert Formatierungsmethoden und Einzug.<br>
		 * Der Einzug ist eine Zeichenkette, die zum Einrücken der Hierarchieebene eingesetzt wird.
		 * 
		 * @param helper Formatierungsmethoden.
		 * @param indent Einzug.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn der Einzug leer ist.
		 */
		public ScriptFormatter(final ScriptFormatterHelper helper, final String indent) throws NullPointerException, IllegalArgumentException {
			if (helper == null) throw new NullPointerException();
			if (indent.isEmpty()) throw new IllegalArgumentException();
			this.helper = helper;
			this.indent = indent;
			this.items = new ArrayList<Object>();
			this.indents = new LinkedList<Boolean>();
			this.indents.addLast(Boolean.FALSE);
		}

		{}

		/**
		 * Diese Methode markiert die aktuelle Hierarchieebene als einzurücken und gibt {@code this} zurück. Beginn und Ende einer Hierarchieebene werden über
		 * {@link #putSpaceInc()} und {@link #putSpaceDec()} markiert.
		 * 
		 * @see #putSpace()
		 * @see #putSpaceInc()
		 * @see #putSpaceDec()
		 * @return {@code this}.
		 */
		public ScriptFormatter setIndent() {
			final LinkedList<Boolean> indents = this.indents;
			if (this.indents.getLast().booleanValue()) return this;
			final int value = indents.size();
			indents.set(value - 1, Boolean.TRUE);
			final List<Object> items = this.items;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == ScriptFormatter.MARK) {
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
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ScriptFormatter put(final Object object) throws NullPointerException {
			this.items.add(object.toString());
			return this;
		}

		/**
		 * Diese Methode fügt die gegebenen Markierung an und gibt {@code this} zurück.
		 * 
		 * @param object Markierung.
		 * @return {@code this}.
		 */
		protected ScriptFormatter putMark(final Mark object) {
			this.items.add(object);
			return this;
		}

		/**
		 * Diese Methode fügt ein bedingtes Leerzeichen an und gibt {@code this} zurück. Wenn über {@link #setIndent()} die Einrückung für die aktuelle
		 * Hierarchieebene aktiviert wurde, wird statt eines Leerzeichens ein Zeilenumbruch gefolgt von der zur Ebene passenden Einrückung angefügt.
		 * 
		 * @see #setIndent()
		 * @see #putSpaceInc()
		 * @see #putSpaceDec()
		 * @return {@code this}.
		 */
		public ScriptFormatter putSpace() {
			final LinkedList<Boolean> indents = this.indents;
			return this.putMark(ScriptFormatter.MARK).putMark(new Mark(indents.size(), false, indents.getLast().booleanValue()));
		}

		/**
		 * Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück. Hierbei fügt sie
		 * gleich einen {@link #putSpace() bedingtes Leerzeichen} ein.
		 * 
		 * @see #setIndent()
		 * @see #putSpace()
		 * @see #putSpaceDec()
		 * @return {@code this}.
		 */
		public ScriptFormatter putSpaceInc() {
			final LinkedList<Boolean> indents = this.indents;
			indents.addLast(Boolean.FALSE);
			return this.putMark(ScriptFormatter.MARK).putMark(new Mark(indents.size(), false, false));
		}

		/**
		 * Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Hierbei fügt sie
		 * gleich einen {@link #putSpace() bedingtes Leerzeichen} ein.
		 * 
		 * @see #setIndent()
		 * @see #putSpace()
		 * @see #putSpaceInc()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde.
		 */
		public ScriptFormatter putSpaceDec() throws IllegalStateException {
			final LinkedList<Boolean> indents = this.indents;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this.putMark(ScriptFormatter.MARK).putMark(new Mark(value, true, indents.removeLast().booleanValue()));
		}

		/**
		 * Diese Methode fügt die Werte der gegebenen Liste an und gibt {@code this} zurück.
		 * <p>
		 * Wenn die Liste leer ist, wird {@code "[ ]"} angefügt. Andernfalls werden die Werte in {@code "["} und {@code "]"} eingeschlossen sowie mit {@code ";"}
		 * separiert über {@link #putValue(Value)} angefügt. Nach der öffnenden Klammer {@link #putSpaceInc() beginnt} dabei eine neue Hierarchieebene, die vor der
		 * schließenden Klammer {@link #putSpaceDec() endet}. Nach jedem Trennzeichen wird ein {@link #putSpace() bedingtes Leerzeichen} eingefügt. <br>
		 * Die aktuelle Hierarchieebene wird als einzurücken markiert, wenn die Wertliste mehr als ein Element enthält.
		 * 
		 * @see #putValue(Value)
		 * @see #putSpace()
		 * @see #putSpaceInc()
		 * @see #putSpaceDec()
		 * @param array Liste von Werten.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn einer der Werte nicht formatiert werden kann.
		 */
		public ScriptFormatter putArray(final Array array) throws IllegalArgumentException {
			final int length = array.length();
			if (length == 0) return this.put("[ ]");
			(length == 1 ? this : this.setIndent()).put("[").putSpaceInc().putValue(array.get(0));
			for (int i = 1; i < length; i++) {
				this.put(";").putSpace().putValue(array.get(i));
			}
			return this.putSpaceDec().put("]");
		}

		/**
		 * Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.
		 * <p>
		 * Wenn der Wert ein {@link ArrayValue} oder ein {@link FunctionValue} ist, wird er über {@link #putArray(Array)} bzw. über {@link #putFunction(Function)}
		 * angefügt. Eine {@link FunctionValue} wird dabei in <code>"{ : "</code> und <code>" }"</code> eingeschlossen. Andernfalls wird der Wert über
		 * {@link ScriptFormatterHelper#formatValue(ScriptFormatter, Value)} formatiert.
		 * 
		 * @see #putArray(Array)
		 * @see #putFunction(Function)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
		 */
		public ScriptFormatter putValue(final Value value) throws IllegalArgumentException {
			if (value instanceof ArrayValue) return this.putArray((Array)value.data());
			if (value instanceof FunctionValue) return this.put("{ : ").putFunction((Function)value.data()).put(" }");
			this.helper.formatValue(this, value);
			return this;
		}

		/**
		 * Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.
		 * <p>
		 * Wenn die Funktion eine {@link ArrayFunction} ist, wird {@code "$"} angefügt. Wenn die Funktion eine {@link ParamFunction} ist, wird {@code "$i"}
		 * angefügt, wobei {@code i} der um eins vergrößerte Parameterindex ist. Wenn die Funktion eine {@link ValueFunction} ist, wird ihr Wert via
		 * {@link #putValue(Value)} angefügt. Wenn die Funktion eine {@link ClosureFunction} ist, wird ihre Funktion als {@link FunctionValue} über
		 * {@link #putValue(Value)} angefügt. Wenn die Funktion eine {@link LazyFunction} bzw. eine {@link TraceFunction} ist, wird ihre Funktion über
		 * {@link #putFunction(Function)} angefügt. Wenn die Funktion eine {@link CompositeFunction} ist, werden ihre aufzurufende Funktion gefolgt von den in
		 * {@code "("} und {@code ")"} eingeschlossenen und mit {@code ";"} separierten Parameterfunktionen angefügt, wobei jede diese Funktionen selbst über
		 * {@link #putFunction(Function)} angefügt wird. Andernfalls wird die Funktion über {@link ScriptFormatterHelper#formatFunction(ScriptFormatter, Function)}
		 * formatiert.
		 * 
		 * @param function Funktion.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann.
		 */
		public ScriptFormatter putFunction(final Function function) throws IllegalArgumentException {
			if (function instanceof ArrayFunction) return this.put("$");
			if (function instanceof ParamFunction) return this.put("$").put(Integer.valueOf(((ParamFunction)function).index() + 1));
			if (function instanceof ValueFunction) return this.putValue(((ValueFunction)function).value());
			if (function instanceof ClosureFunction) return this.putValue(FunctionValue.valueOf(((ClosureFunction)function).function()));
			if (function instanceof LazyFunction) return this.putFunction(((LazyFunction)function).function());
			if (function instanceof TraceFunction) return this.putFunction(((TraceFunction)function).function());
			if (function instanceof CompositeFunction) {
				final CompositeFunction compositeFunction = (CompositeFunction)function;
				this.putFunction(compositeFunction.function());
				final Function[] functions = compositeFunction.functions();
				final int length = functions.length;
				if (length == 0) return this.put("( )");
				(length > 1 ? this.setIndent() : this).put("(").putSpaceInc().putFunction(functions[0]);
				for (int i = 1; i < length; i++) {
					this.put(";").putSpace().putFunction(functions[i]);
				}
				this.putSpaceDec().put(")");
				return this;
			}
			this.helper.formatFunction(this, function);
			return this;
		}

		/**
		 * Diese Methode gibt die Verkettung der bisher gesammelten Zeichenketten als Quelltext zurück.
		 * 
		 * @see #put(Object)
		 * @return Quelltext.
		 */
		public String format() {
			final StringBuilder result = new StringBuilder();
			final String indent = this.indent;
			final List<Object> items = this.items;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == ScriptFormatter.MARK) {
					final Mark token = (Mark)items.get(i++);
					if (token.isEnabled()) {
						result.append('\n');
						for (int count = token.level() - (token.isLast() ? 2 : 1); count > 0; count--) {
							result.append(indent);
						}
					} else {
						result.append(' ');
					}
				} else {
					result.append(item.toString());
				}
			}
			return result.toString();
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
		 * Diese Methode formatiert den gegebenen Wert in einen Quelltext unf fügt diesen an den gegebenen {@link ScriptFormatter} anfügen.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
		 */
		public void formatValue(ScriptFormatter target, Value value) throws IllegalArgumentException;

		/**
		 * Diese Methode formatiert die gegebene Funktion in einen Quelltext unf fügt diesen an den gegebenen {@link ScriptFormatter} anfügen.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn die Funktion nicht formatiert werden kann.
		 */
		public void formatFunction(ScriptFormatter target, Function function) throws IllegalArgumentException;

	}

	/**
	 * Diese Klasse implementiert den Kompiler für {@link Scripts#compileValue(Script, ScriptCompilerHelper, String...)} bzw.
	 * {@link Scripts#compileFunction(Script, ScriptCompilerHelper, String...)}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptCompiler {

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		protected final Script script;

		/**
		 * Dieses Feld speichert die Parameternamen.
		 */
		protected final LinkedList<String> params;

		/**
		 * Dieses Feld speichert den {@link Iterator} über die Bereiche von {@link #script}.
		 */
		protected final Iterator<Range> iterator;

		/**
		 * Dieses Feld speichert den aktuellen Bereich.
		 */
		protected Range range;

		/**
		 * Dieses Feld speichert die Kompilationsmethoden.
		 */
		protected final ScriptCompilerHelper helper;

		/**
		 * Dieses Feld speichert die Zulässigkeit von Wertlisten.
		 */
		protected boolean arrayEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit von Funktionszeigern.
		 */
		protected boolean handlerEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Bindung des Ausführungskontexts.
		 */
		protected boolean closureEnabled = true;

		/**
		 * Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen.
		 */
		protected boolean chainingEnabled = true;

		/**
		 * Dieser Konstruktor initialisiert Quelltext, Kompilationsmethoden und Parameternamen.
		 * 
		 * @param script Quelltext.
		 * @param helper Kompilationsmethoden.
		 * @param params Parameternamen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist oder enthält.
		 */
		public ScriptCompiler(final Script script, final ScriptCompilerHelper helper, final String... params) throws NullPointerException {
			this.script = script;
			this.iterator = script.iterator();
			if (helper == null) throw new NullPointerException();
			this.helper = helper;
			this.params = new LinkedList<String>(Arrays.asList(params));
			if (this.params.contains(null)) throw new NullPointerException();
			this.skip();
		}

		{}

		/**
		 * Diese Methode überspringt den aktuellen Bereich und gibt den nächsten oder {@link Range#NULL} zurück. Der {@link #range aktuelle Bereich} wird durch
		 * diese Methode verändert.
		 * 
		 * @return aktueller Bereich oder {@link Range#NULL}.
		 */
		protected Range skip() {
			if (!this.iterator.hasNext()) return this.range = Range.NULL;
			return this.range = this.iterator.next();
		}

		/**
		 * Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den ersten bedeutsamen Bereich oder {@link Range#NULL}
		 * zurück. Der {@link #range aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereich oder {@link Range#NULL}.
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
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected Object doCompileArray() throws ScriptException {
			if (!this.arrayEnabled) throw new ScriptException(this.script, this.range);
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
						throw new ScriptException(this.script, this.range);
				}
			}
		}

		/**
		 * Diese Methode kompiliert den beim aktuellen Bereich beginnende Wert und gibt diesen zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
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
					throw new ScriptException(this.script, this.range);
				case '[':
					return this.doCompileValueArray();
				case '{':
					if (this.closureEnabled) throw new ScriptException(this.script, this.range);
					return FunctionValue.valueOf(this.doCompileScope());
				default:
					try {
						value = this.helper.compileValue(this.script, this.range);
					} catch (final ScriptException e) {
						throw e;
					} catch (final RuntimeException e) {
						throw new ScriptException(this.script, this.range, e);
					}
					if (value == null) throw new ScriptException(this.script, this.range);
					this.skip();
					return value;
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link Value}.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected Value doCompileValueArray() throws ScriptException {
			final Object array = this.doCompileArray();
			if (array instanceof Value) return (Value)array;
			throw new ScriptException(this.script, this.range);
		}

		/**
		 * Diese Methode kompiliert den aktuellen Bereich zu einen Parameternamen und gibt diesen oder {@code null} zurück.
		 * 
		 * @return Parametername oder {@code null}.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected String doCompileParam() throws ScriptException {
			switch (this.skipSpace().type) {
				case 0:
				case '$':
				case '(':
				case '[':
				case '{':
					throw new ScriptException(this.script, this.range);
				case ':':
				case ';':
				case ')':
				case '}':
				case ']':
					return null;
			}
			final String name;
			try {
				name = this.helper.compileParam(this.script, this.range);
				if (name.isEmpty()) throw new IllegalArgumentException();
			} catch (final ScriptException e) {
				throw e;
			} catch (final RuntimeException e) {
				throw new ScriptException(this.script, this.range, e);
			}
			this.skip();
			return name;
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende, parametrisierte Funktion in einen {@link FunctionValue} und gibt diesen zurück.
		 * 
		 * @return Funktion als {@link FunctionValue}.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected Function doCompileScope() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this.skipSpace().type == 0) throw new ScriptException(this.script, this.range);
				final String name = this.doCompileParam();
				if (name != null) {
					if (this.doCompileIndex(name) >= 0) throw new ScriptException(this.script, this.range);
					this.params.add(count++, name);
				}
				switch (this.skipSpace().type) {
					case ';':
						if (name == null) throw new ScriptException(this.script, this.range);
						this.skip();
						break;
					case ':': {
						this.skip();
						final Function function = this.doCompileFunction();
						if (this.skipSpace().type != '}') throw new ScriptException(this.script, this.range);
						this.skip();
						this.params.subList(0, count).clear();
						return function;
					}
					default:
						throw new ScriptException(this.script, this.range);
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diesen zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected Function doCompileFunction() throws ScriptException {
			Function function;
			boolean chained = false;
			switch (this.skipSpace().type) {
				case '$': {
					this.skip();
					final String name = this.doCompileParam();
					if (name == null) return ArrayFunction.VIEW;
					final int index = this.doCompileIndex(name);
					final int index2 = (index < 0) ? this.params.indexOf(name) : (index - 1);
					if (index2 < 0) throw new ScriptException(this.script, this.range);
					return ParamFunction.valueOf(index2);
				}
				case '{': {
					function = this.doCompileScope();
					if (this.skipSpace().type != '(') return this.closureEnabled ? //
						ClosureFunction.valueOf(function) : ValueFunction.valueOf(FunctionValue.valueOf(function));
					if (!this.chainingEnabled) throw new ScriptException(this.script, this.range);
					break;
				}
				case '[': {
					return this.doCompileFunctionArray();
				}
				default: {
					final Value value = this.doCompileValue();
					if (!(value instanceof FunctionValue)) return ValueFunction.valueOf(value);
					if (this.skipSpace().type != '(') {
						if (this.handlerEnabled) return ValueFunction.valueOf(value);
						throw new ScriptException(this.script, this.range);
					}
					function = ((FunctionValue)value).data();
				}
			}
			do {
				if (chained && !this.chainingEnabled) throw new ScriptException(this.script, this.range);
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
							throw new ScriptException(this.script, this.range);
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
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link Function} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link Function}.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected Function doCompileFunctionArray() throws ScriptException {
			final Object array = this.doCompileArray();
			if (array instanceof Value) return ValueFunction.valueOf((Value)array);
			return (Function)array;
		}

		{}

		/**
		 * Diese Methode kompiliert den Quelltext zu einem Wert und gibt diesen zurück.
		 * 
		 * @see #compileFunction()
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		public Value compileValue() throws ScriptException {
			final Value value = this.doCompileValue();
			if (this.skipSpace().type == 0) return value;
			throw new ScriptException(this.script, this.range);
		}

		/**
		 * Diese Methode kompiliert den Quelltext im Kontext der Kompilationsmethoden sowie der Funktionsparameter in eine Funktion und gibt diese Funktion zurück.
		 * Die Bereichestypen des Quelltexts haben folgende Bedeutung:
		 * <ul>
		 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) dürfen an jeder Position vorkommen und werden ignoriert.</li>
		 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link Array}s an, dessen Elemente mit Bereichen vom Typ
		 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente unzulässig .</li>
		 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
		 * Bereichen vom Typ {@code ';'} separiert werden müssen.</li>
		 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
		 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ
		 * {@code ':'} abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über
		 * {@link ScriptCompilerHelper#compileParam(Script, Range)} aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu
		 * Java. Nach der Parameterliste folgen dann die Bereiche, die zu genau einem {@link FunctionValue} kompilieren müssen.</li>
		 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
		 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.valueOf(0)}). Andernfalls steht der Bereich für {@link ArrayFunction#VIEW}.</li>
		 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileValue(Script, Range)} in Werte überführt. Funktionen werden hierbei zu
		 * {@link FunctionValue}s.</li>
		 * </ul>
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		public Function compileFunction() throws ScriptException {
			final Function function = this.doCompileFunction();
			if (this.skipSpace().type == 0) return function;
			throw new ScriptException(this.script, this.range);
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Wertlisten.
		 */
		public boolean isArrayEnabled() {
			return this.arrayEnabled;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Wertlisten.
		 * 
		 * @see #isArrayEnabled()
		 * @param value Zulässigkeit von Wertlisten.
		 * @return {@code this}.
		 */
		public ScriptCompiler setArrayEnabled(final boolean value) {
			this.arrayEnabled = value;
			return this;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link ScriptCompilerHelper#compileValue(Script, Range)} als {@link FunctionValue}
		 * gelieferten Funktionen als Funktionszeiger zu {@link ValueFunction}s kompiliert werden dürfen (z.B {@code SORT(array; compFun)}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Funktionszeigern.
		 */
		public boolean isHandlerEnabled() {
			return this.handlerEnabled;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit von Funktionszeigern.
		 * 
		 * @see #isHandlerEnabled()
		 * @param value Zulässigkeit von Funktionszeigern.
		 * @return {@code this}.
		 */
		public ScriptCompiler setHandlerEnabled(final boolean value) {
			this.handlerEnabled = value;
			return this;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link ClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Ausführungskontexts.
		 */
		public boolean isClosureEnabled() {
			return this.closureEnabled;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Bindung des Ausführungskontexts.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Ausführungskontexts.
		 * @return {@code this}.
		 */
		public ScriptCompiler setClosureEnabled(final boolean value) {
			this.closureEnabled = value;
			return this;
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
		public boolean isChainingEnabled() {
			return this.chainingEnabled;
		}

		/**
		 * Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
		 * 
		 * @see #isChainingEnabled()
		 * @param value Zulässigkeit der Verkettung von Funktionen.
		 * @return {@code this}.
		 */
		public ScriptCompiler setChainingEnabled(final boolean value) {
			this.chainingEnabled = value;
			return this;
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
		 * Diese Methode gibt den im gegebenen Bereich des gegebenen Quelltexts angegebenen Wert zurück. Funktionen müssen als {@link FunctionValue} geliefert
		 * werden.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich.
		 * @return Wert als {@link Value} oder Funktionen als {@link FunctionValue}.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält.
		 */
		public Value compileValue(Script script, final Range range) throws ScriptException;

		/**
		 * Diese Methode gibt den im gegebenen Bereich des gegebenen Quelltexts angegebenen Parameternamen zurück.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich.
		 * @return Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Parameternamen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
		 */
		public String compileParam(Script script, final Range range) throws ScriptException;

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
		private static final long serialVersionUID = -2729122475649970656L;

		{}

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		public final Script script;

		/**
		 * Dieses Feld speichert den Bereich, in dem der Syntaxfehler entdeckt wurde.
		 */
		public final Range range;

		/**
		 * Dieser Konstruktor initialisiert den Quelltext und den Bereich mit dem Syntaxfehler.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich mit dem Syntaxfehler.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ScriptException(final Script script, final Range range) throws NullPointerException {
			this(script, range, null);
		}

		/**
		 * Dieser Konstruktor initialisiert den Quelltext und den Bereich mit dem Syntaxfehler.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich mit dem Syntaxfehler.
		 * @param cause Verursachende Ausnahme.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ScriptException(final Script script, final Range range, final Throwable cause) throws NullPointerException {
			super(range == Range.NULL ? //
				"Unerwartetes Ende der Zeichenkette." : //
				"Unerwartete Zeichenkette '" + range.extract(script.source) + "' an Position " + range.start + ".", cause);
			this.script = script;
			this.range = range;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link ScriptFormatterHelper}.
	 */
	static ScriptFormatterHelper defaultFormatterHelper = new ScriptFormatterHelper() {

		@Override
		public void formatValue(final ScriptFormatter target, final Value value) throws IllegalArgumentException {
			target.put(value.toString());
		}

		@Override
		public void formatFunction(final ScriptFormatter target, final Function function) throws IllegalArgumentException {
			target.put(function.toString());
		}

	};

	/**
	 * Diese Methode gibt den {@code default}-{@link ScriptFormatterHelper} zurück, der in den Methoden {@link #formatValue(Value)} und
	 * {@link #formatFunction(Function)} verwendet wird.
	 * 
	 * @return {@code default}-{@link ScriptFormatterHelper}.
	 */
	public static ScriptFormatterHelper getDefaultFormatterHelper() {
		return Scripts.defaultFormatterHelper;
	}

	/**
	 * Diese Methode setzt den {@code default}-{@link ScriptFormatterHelper}.
	 * 
	 * @see #getDefaultFormatterHelper()
	 * @param value neuer {@code default}-{@link ScriptFormatterHelper}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static void setDefaultFormatterHelper(final ScriptFormatterHelper value) {
		if (value == null) throw new NullPointerException();
		Scripts.defaultFormatterHelper = value;
	}

	{}

	/**
	 * Dieses Feld speichert den {@code default}-{@link ScriptFormatterHelper}.
	 */
	static ScriptCompilerHelper defaultCompilerHelper = new ScriptCompilerHelper() {

		@Override
		public Value compileValue(final Script script, final Range range) throws ScriptException {
			return null;
		}

		@Override
		public String compileParam(final Script script, final Range range) throws ScriptException {
			return null;
		}

	};

	/**
	 * Diese Methode gibt den {@code default}-{@link ScriptCompilerHelper} zurück, der in den Methoden {@link #compileValue(Script, String...)} und
	 * {@link #compileFunction(Script, String...)} verwendet wird.
	 * 
	 * @return {@code default}-{@link ScriptCompilerHelper}.
	 */
	public static ScriptCompilerHelper getDefaultCompilerHelper() {
		return Scripts.defaultCompilerHelper;
	}

	/**
	 * Diese Methode setzt den {@code default}-{@link ScriptCompilerHelper}.
	 * 
	 * @see #getDefaultCompilerHelper()
	 * @param value neuer {@code default}-{@link ScriptCompilerHelper}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static void setDefaultCompilerHelper(final ScriptCompilerHelper value) {
		if (value == null) throw new NullPointerException();
		Scripts.defaultCompilerHelper = value;
	}

	{}

	/**
	 * Diese Methode parst die gegebene Zeichenkette in einen aufbereiteten Quelltext und gibt diesen zurück.
	 * 
	 * @see Script
	 * @see ScriptParser#parse()
	 * @see #compileValue(Script, ScriptCompilerHelper, String...)
	 * @see #compileFunction(Script, ScriptCompilerHelper, String...)
	 * @param source Zeichenkette.
	 * @return aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Script parse(final String source) throws NullPointerException {
		return new Script(source, new ScriptParser(source).parse());
	}

	{}

	/**
	 * Diese Methode formatiert den gegebenen Wert in eine Zeichenkette und gibt diese zurück.
	 * 
	 * @see #getDefaultFormatterHelper()
	 * @see #formatValue(Value, ScriptFormatterHelper)
	 * @param value Wert.
	 * @return Zeichenkette.
	 */
	public static String formatValue(final Value value) {
		return Scripts.formatValue(value, Scripts.getDefaultFormatterHelper());
	}

	/**
	 * Diese Methode formatiert den gegebenen Wert im Kontext der gegebenen Formatierungsmethoden in eine Zeichenkette und gibt diese zurück.
	 * 
	 * @see ScriptFormatter#putValue(Value)
	 * @param value Wert.
	 * @param helper Formatierungsmethoden.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn die Formatierungsmethoden {@code null} sind.
	 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
	 */
	public static String formatValue(final Value value, final ScriptFormatterHelper helper) throws NullPointerException, IllegalArgumentException {
		return new ScriptFormatter(helper).putValue(value).format();
	}

	/**
	 * Diese Methode formatiert die gegebene Funktion in eine Zeichenkette und gibt diese zurück.
	 * 
	 * @see #getDefaultFormatterHelper()
	 * @see #formatFunction(Function, ScriptFormatterHelper)
	 * @param function Funktion.
	 * @return Zeichenkette.
	 */
	public static String formatFunction(final Function function) {
		return Scripts.formatFunction(function, Scripts.getDefaultFormatterHelper());
	}

	/**
	 * Diese Methode formatiert die gegebene Funktion im Kontext der gegebenen Formatierungsmethoden in eine Zeichenkette und gibt diese zurück.
	 * 
	 * @see ScriptFormatter#putFunction(Function)
	 * @param function Funktion.
	 * @param helper Formatierungsmethoden.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn die Formatierungsmethoden {@code null} sind.
	 * @throws IllegalArgumentException Wenn der Wert nicht formatiert werden kann.
	 */
	public static String formatFunction(final Function function, final ScriptFormatterHelper helper) {
		return new ScriptFormatter(helper).putFunction(function).format();
	}

	{}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Funktionsparameter in einen Wert und gibt diesen zurück.
	 * 
	 * @see #getDefaultCompilerHelper()
	 * @see #compileValue(Script, ScriptCompilerHelper, String...)
	 * @param script Quelltext.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierter Wert.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptException Wenn eine der Eingaben ungültig ist.
	 */
	public static Value compileValue(final Script script, final String... params) throws NullPointerException, ScriptException {
		return Scripts.compileValue(script, Scripts.getDefaultCompilerHelper(), params);
	}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Kompilationsmethoden und Funktionsparameter in einen Wert und gibt diesen zurück.
	 * 
	 * @see #parse(String)
	 * @see #compileFunction(Script, ScriptCompilerHelper, String...)
	 * @see ScriptCompiler#compileValue()
	 * @param script Quelltext.
	 * @param helper Kompilationsmethoden.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierter Wert.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptException Wenn eine der Eingaben ungültig ist.
	 */
	public static Value compileValue(final Script script, final ScriptCompilerHelper helper, final String... params) throws NullPointerException, ScriptException {
		return new ScriptCompiler(script, helper, params).compileValue();
	}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Funktionsparameter in eine Funktion und gibt diese zurück.
	 * 
	 * @see #getDefaultCompilerHelper()
	 * @see #compileFunction(Script, ScriptCompilerHelper, String...)
	 * @param script Quelltext.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierte Funktion.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptException Wenn eine der Eingaben ungültig ist.
	 */
	public static Function compileFunction(final Script script, final String... params) throws NullPointerException, ScriptException {
		return Scripts.compileFunction(script, Scripts.getDefaultCompilerHelper(), params);
	}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Kompilationsmethoden und Funktionsparameter in eine Funktion und gibt diese
	 * zurück.
	 * 
	 * @see #parse(String)
	 * @see #compileValue(Script, ScriptCompilerHelper, String...)
	 * @see ScriptCompiler#compileFunction()
	 * @param script Quelltext.
	 * @param helper Kompilationsmethoden.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierte Funktion.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptException Wenn eine der Eingaben ungültig ist.
	 */
	public static Function compileFunction(final Script script, final ScriptCompilerHelper helper, final String... params) throws NullPointerException,
		ScriptException {
		return new ScriptCompiler(script, helper, params).compileFunction();
	}

}
