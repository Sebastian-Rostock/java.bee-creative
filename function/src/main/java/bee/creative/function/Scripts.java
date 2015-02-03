package bee.creative.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.function.Array.ValueArray;
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

public class Scripts {

	/**
	 * Diese Klasse implementiert den Parser für {@link Values#parseScript(String)}.
	 * 
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

		/**
		 * Diese Methode parst die Eingabe und gibt die Liste der ermittelten Bereiche zurück. Die Erzeugung von {@link Range Bereichen} erfolgt gemäß dieser
		 * Regeln:
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

	}

	public static class ScriptFormatter {

		private static class Mark {

			private int data;

			public Mark(final int level, final boolean last, final boolean enabled) {
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

		private static final Object TAG = new Mark(0, false, false);

		private final String indent;

		private final List<Object> items;

		private final LinkedList<Boolean> indents;

		private final ScriptFormatterHelper helper;

		public ScriptFormatter(final ScriptFormatterHelper helper) {
			this(helper, "\t");
		}

		public ScriptFormatter(final ScriptFormatterHelper helper, final String indent) {
			if (helper == null) throw new NullPointerException();
			if (indent.isEmpty()) throw new IllegalArgumentException();
			this.helper = helper;
			this.indent = indent;
			this.items = new ArrayList<Object>();
			this.indents = new LinkedList<Boolean>();
			this.indents.addLast(Boolean.FALSE);
		}

		{}

		// markiert die aktuelle hierarchieebene als einzurücken, die nächst tiefere kann ggf bleiben
		/**
		 * Diese Methode markiert die aktuelle Hierarchieebene als einzurücken und gibt {@code this} zurück. Behinn und Ende einer Hierarchieebene werden über
		 * {@link #putSpaceInc()} und {@link #putSpaceDec()} markiert.
		 * 
		 * @return
		 */
		public ScriptFormatter setIndent() { // OKAY
			final LinkedList<Boolean> indents = this.indents;
			if (this.indents.getLast().booleanValue()) return this;
			final int value = indents.size();
			indents.set(value - 1, Boolean.TRUE);
			final List<Object> items = this.items;
			for (int i = items.size() - 2; i >= 0; i--) {
				final Object item = items.get(i);
				if (item == ScriptFormatter.TAG) {
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
		 * Diese Methode fügt das gegebene Objekt an und gibt {@code this} zurück. Zur Umwandlung des Objekts in eine Zeichenkette wird die Methode
		 * {@link Object#toString()} verwendet.
		 * 
		 * @param object Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ScriptFormatter put(final Object object) throws NullPointerException {
			if (object == null) throw new NullPointerException();
			this.items.add(object);
			return this;
		}

		public ScriptFormatter putSpace() { // FEIN
			final LinkedList<Boolean> indents = this.indents;
			return this.put(ScriptFormatter.TAG).put(new Mark(indents.size(), false, indents.getLast().booleanValue()));
		}

		/**
		 * Diese Methode markiert den Beginn einer neuen Hierarchieebene, erhöht die Tiefe der Einrückung um eins und gibt {@code this} zurück.<br>
		 * Hierbei fügt sie gleich einen {@link #putSpace() bedingten Umbruch} ein.
		 * 
		 * @return {@code this}.
		 */
		public ScriptFormatter putSpaceInc() { // FEIN
			final LinkedList<Boolean> indents = this.indents;
			indents.addLast(Boolean.FALSE);
			return this.put(ScriptFormatter.TAG).put(new Mark(indents.size(), false, false));
		}

		/**
		 * Diese Methode markiert das Ende der aktuellen Hierarchieebene, reduziert die Tiefe der Einrückung um eins und gibt {@code this} zurück. Hierbei fügt sie
		 * gleich einen {@link #putSpace() bedingten Umbruch} ein.
		 * 
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn zuvor keine Hierarchieebene begonnen wurde.
		 */
		public ScriptFormatter putSpaceDec() throws IllegalStateException { // FEIN
			final LinkedList<Boolean> indents = this.indents;
			final int value = indents.size();
			if (value <= 1) throw new IllegalStateException();
			return this.put(ScriptFormatter.TAG).put(new Mark(value, true, indents.removeLast().booleanValue()));
		}

		public ScriptFormatter putArray(final Array array) throws IllegalArgumentException { // OKAY
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
		 * angefügt. Andernfalls wird der Wert über {@link ScriptFormatterHelper#formatValue(ScriptFormatter, Value)} formatiert.
		 * 
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

		public ScriptFormatter putFunction(final Function function) throws IllegalArgumentException { // OKAY
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

		{}

		public String format() {
			final StringBuilder result = new StringBuilder();
			final String indent = this.indent;
			final List<Object> items = this.items;
			final int size = items.size();
			for (int i = 0; i < size;) {
				final Object item = items.get(i++);
				if (item == ScriptFormatter.TAG) {
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
	 * Diese Klasse implementiert den Kompiler für {@link Values#compileToValue(Script, ScriptCompilerHelper, String...)} bzw.
	 * {@link Values#compileToFunction(Script, ScriptCompilerHelper, String...)}.
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
		protected int compileIndex(final String string) {
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
		protected Object compileArray() throws ScriptException {
			if (!this.arrayEnabled) throw new ScriptException(this.script, this.range);
			this.skip();
			if (this.skipSpace().type == ']') {
				this.skip();
				return new ArrayValue(Array.EMPTY_ARRAY);
			}
			boolean value = true;
			for (final LinkedList<Function> array = new LinkedList<Function>(); true;) {
				final Function item = this.compileFunction();
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
						if (!value) return new CompositeFunction(ArrayFunction.COPY, array.toArray(new Function[size]));
						final Value[] values = new Value[size];
						for (int i = 0; i < size; i++) {
							values[i] = array.get(i).execute(null);
						}
						return new ArrayValue(new ValueArray(values));
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
		protected Value compileValue() throws ScriptException {
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
					return this.compileValueArray();
				case '{':
					if (this.closureEnabled) throw new ScriptException(this.script, this.range);
					return new FunctionValue(this.compileScope());
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
		protected Value compileValueArray() throws ScriptException {
			final Object array = this.compileArray();
			if (array instanceof Value) return (Value)array;
			throw new ScriptException(this.script, this.range);
		}

		/**
		 * Diese Methode kompiliert den aktuellen Bereich zu einen Parameternamen und gibt diesen oder {@code null} zurück.
		 * 
		 * @return Parametername oder {@code null}.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		protected String compileParam() throws ScriptException {
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
		protected Function compileScope() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this.skipSpace().type == 0) throw new ScriptException(this.script, this.range);
				final String name = this.compileParam();
				if (name != null) {
					if (this.compileIndex(name) >= 0) throw new ScriptException(this.script, this.range);
					this.params.add(count++, name);
				}
				switch (this.skipSpace().type) {
					case ';':
						if (name == null) throw new ScriptException(this.script, this.range);
						this.skip();
						break;
					case ':': {
						this.skip();
						final Function function = this.compileFunction();
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
		protected Function compileFunction() throws ScriptException {
			Function function;
			boolean chained = false;
			switch (this.skipSpace().type) {
				case '$': {
					this.skip();
					final String name = this.compileParam();
					if (name == null) return ArrayFunction.VIEW;
					final int index = this.compileIndex(name);
					final int index2 = (index < 0) ? this.params.indexOf(name) : (index - 1);
					if (index2 < 0) throw new ScriptException(this.script, this.range);
					return ParamFunction.valueOf(index2);
				}
				case '{': {
					function = this.compileScope();
					if (this.skipSpace().type != '(') return this.closureEnabled ? new ClosureFunction(function) : new ValueFunction(new FunctionValue(function));
					if (!this.chainingEnabled) throw new ScriptException(this.script, this.range);
					break;
				}
				case '[': {
					return this.compileFunctionArray();
				}
				default: {
					final Value value = this.compileValue();
					if (!(value instanceof FunctionValue)) return new ValueFunction(value);
					if (this.skipSpace().type != '(') {
						if (this.handlerEnabled) return new ValueFunction(value);
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
					functions.add(this.compileFunction());
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
		protected Function compileFunctionArray() throws ScriptException {
			final Object array = this.compileArray();
			if (array instanceof Value) return new ValueFunction((Value)array);
			return (Function)array;
		}

		/**
		 * Diese Methode kompiliert den Quelltext zu einem Wert und gibt diesen zurück.
		 * 
		 * @see #compileToFunction()
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script} ungültig ist.
		 */
		public Value compileToValue() throws ScriptException {
			final Value value = this.compileValue();
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
		public Function compileToFunction() throws ScriptException {
			final Function function = this.compileFunction();
			if (this.skipSpace().type == 0) return function;
			throw new ScriptException(this.script, this.range);
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
		 * 
		 * @see #compileToFunction()
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
		 * @see #compileToFunction()
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
		 * @see #compileToFunction()
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
		 * @see #compileToFunction()
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
	 * Diese Schnittstelle definiert Kompilationsmethoden, die in den Methoden {@link ScriptCompiler#compileToValue()} und
	 * {@link ScriptCompiler#compileToFunction()} zur Übersetzung von Quelltexten in Werte, Funktionen bzw. Parameternamen genutzt werden.
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

}
