package bee.creative.fem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEM.ClosureFunction;
import bee.creative.fem.FEM.InvokeFunction;
import bee.creative.fem.FEM.ParamFunction;
import bee.creative.fem.FEM.ProxyFunction;
import bee.creative.fem.FEM.ValueFunction;
import bee.creative.fem.Scripts.ScriptCompiler;
import bee.creative.fem.Scripts.ScriptCompilerHelper;
import bee.creative.fem.Scripts.ScriptException;
import bee.creative.fem.Scripts.ScriptFormatter;
import bee.creative.fem.Scripts.ScriptFormatter.Mark;
import bee.creative.fem.Scripts.ScriptFormatterHelper;
import bee.creative.fem.Scripts.ScriptFormatterInput;
import bee.creative.fem.Scripts.ScriptTracer;
import bee.creative.fem.Scripts.ScriptTracerHelper;
import bee.creative.fem.Scripts.ScriptTracerInput;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Converter;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

/**
 * Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 * <p>
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Verarbeitung von aufbereiteten Quelltexten.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Script implements Items<Script.Range>, Iterable<Script.Range> {

	/**
	 * Diese Klasse implementiert ein Objekt, dass einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Range)}
	 * erfolgt gemäß ihrer Startposition.
	 * 
	 * @see Script
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class Range implements Comparable<Range> {

		/**
		 * Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind.
		 */
		public static final Range EMPTY = new Range((char)0, 0, 0);

		{}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Range#start() Startposition} ist, größer der {@link Range#end() Endposition} ist bzw. innerhalb der oder gleich den Grenzen des Bereichs liegt.
		 * 
		 * @see Range#end()
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen.
		 */
		public static final Comparable<Range> contains(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					final int start = value.start;
					return index < start ? -1 : index > (value.length + start) ? +1 : 0;
				}

			};
		}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#end() Endposition}en mit der gegebenen Position vergleicht. Der
		 * Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position kleiner,
		 * gleich bzw. größer der {@link Range#end() Endposition} eines gegebenen Bereichs ist.
		 * 
		 * @see Range#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für das Ende von {@link Range}s.
		 */
		public static final Comparable<Range> endingAt(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.start + value.length);
				}

			};
		}

		/**
		 * Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Range#start() Startposition}en mit der gegebenen Position vergleicht.
		 * Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position
		 * kleiner, gleich bzw. größer der {@link Range#start() Startposition} eines gegebenen Bereichs ist.
		 * 
		 * @see Range#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen.
		 */
		public static final Comparable<Range> startingAt(final int index) {
			return new Comparable<Script.Range>() {

				@Override
				public int compareTo(final Range value) {
					return Comparators.compare(index, value.start);
				}

			};
		}

		{}

		/**
		 * Dieses Feld speichert den Typ des Bereichs.
		 */
		final char type;

		/**
		 * Dieses Feld speichert die Startposition.
		 */
		final int start;

		/**
		 * Dieses Feld speichert die Länge.
		 */
		final int length;

		/**
		 * Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn die Startposition oder die Länge negativ sind.
		 */
		public Range(final char type, final int start, final int length) throws IllegalArgumentException {
			if (start < 0) throw new IllegalArgumentException("start < 0");
			if (length < 0) throw new IllegalArgumentException("length < 0");
			this.type = type;
			this.start = start;
			this.length = length;
		}

		{}

		/**
		 * Diese Methode gibt den Typ des Bereichs zurück.
		 * 
		 * @see Script
		 * @return Bereichstyp.
		 */
		public char type() {
			return this.type;
		}

		/**
		 * Diese Methode gibt die Position zurück, vord der die {@link Range} endet.
		 * 
		 * @return Endposition.
		 */
		public int end() {
			return this.start + this.length;
		}

		/**
		 * Diese Methode gibt die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public int start() {
			return this.start;
		}

		/**
		 * Diese Methode gibt die Länge des die Position zurück, an der die {@link Range} beginnt.
		 * 
		 * @return Startposition.
		 */
		public int length() {
			return this.length;
		}

		/**
		 * Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.
		 * 
		 * @param source Zeichenkette.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 */
		public String extract(final String source) throws NullPointerException {
			final int start = this.start;
			return source.substring(start, start + this.length);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final Range value) {
			return Comparators.compare(this.start, value.start);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.type ^ this.start ^ this.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Range)) return false;
			final Range data = (Range)object;
			return (this.start == data.start) && (this.length == data.length) && (this.type == data.type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "'" + this.type + "'@" + this.start + "/" + this.length;
		}

	}

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
		protected final void startParsing() throws IllegalStateException {
			this.checkIdling();
			this.value = -1;
			this.ranges.clear();
		}
	
		/**
		 * Diese Methode beendet das Parsen und sollte nur in Verbindung mit {@link #startParsing()} verwendet werden.
		 */
		protected final void stopParsing() {
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
	 * Diese Klasse implementiert einen Kompiler für {@link FEMValue Werte} und {@link FEMFunction Funktionen}.
	 * <p>
	 * Die Bereichestypen eines Quelltexts haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) sind bedeutungslos, dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link FEMArray}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link FEMArray} als Funktion bzw. Parameterwert
	 * kompiliert wird.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über
	 * {@link ScriptCompilerHelper#compileName(ScriptCompiler, String)} aufgelöst werden kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog
	 * zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu genau einer Funktion kompiliert werden.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link FEM.ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.valueOf(0)}). Andernfalls steht der Bereich für {@link FEM#PARAMS_VIEW_FUNCTION}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompilerHelper#compileParam(ScriptCompiler, String)} in Werte überführt. Funktionen werden hierbei
	 * als {@link FEM#functionValue(FEMFunction)}s angegeben.</li>
	 * </ul>
	 * 
	 * @see #compileValue()
	 * @see #compileFunction()
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptCompiler {
	
		/**
		 * Dieses Feld speichert den {@link FEMValue} zu {@link FEMArray#EMPTY}.
		 */
		static final FEMValue EMPTY_ARRAY_VALUE = FEM.arrayValue(FEMArray.EMPTY);
	
		/**
		 * Dieses Feld speichert die {@link FEMFunction} zu {@link #EMPTY_ARRAY_VALUE}.
		 */
		static final FEMFunction EMPTY_ARRAY_FUNCTION = new FEM.ValueFunction(ScriptCompiler.EMPTY_ARRAY_VALUE);
	
		{}
	
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
		private Script script = EMPTY;
	
		/**
		 * Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter.
		 */
		private final Map<String, FEM.ProxyFunction> proxies = Collections.synchronizedMap(new LinkedHashMap<String, FEM.ProxyFunction>());
	
		/**
		 * Dieses Feld speichert die Parameternamen.
		 */
		private final List<String> params = Collections.synchronizedList(new LinkedList<String>());
	
		/**
		 * Dieses Feld speichert die Zulässigkeit von Wertlisten.
		 */
		private boolean arrayEnabled = true;
	
		/**
		 * Dieses Feld speichert die Zulässigkeit von Funktionszeigern.
		 */
		private boolean handlerEnabled = true;
	
		/**
		 * Dieses Feld speichert die Zulässigkeit der Bindung des Rahmendatens.
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
		protected int parseIndex(final String string) {
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
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
		 * 
		 * @see FEM#arrayValue(FEMArray)
		 * @return Wertliste als {@link FEMValue}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEMValue compileNextArrayAsValue() throws ScriptException {
			if (!this.arrayEnabled) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			final List<FEMValue> result = new ArrayList<>();
			this.skip();
			if (this.skipSpace().type == ']') {
				this.skip();
				return ScriptCompiler.EMPTY_ARRAY_VALUE;
			}
			while (true) {
				final FEMValue value = this.compileNextParamAsValue();
				result.add(value);
				switch (this.skipSpace().type) {
					case ';': {
						this.skip();
						this.skipSpace();
						break;
					}
					case ']': {
						this.skip();
						return FEM.arrayValue(FEMArray.from(result));
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}
	
		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMFunction} und gibt diesen zurück.
		 * 
		 * @see FEM.ValueFunction
		 * @see FEM.InvokeFunction
		 * @see FEM#arrayValue(FEMArray)
		 * @see FEM#PARAMS_VIEW_FUNCTION
		 * @return Wertliste als {@link FEMFunction}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEMFunction compileNextArrayAsFunction() throws ScriptException {
			if (!this.arrayEnabled) throw new ScriptException().useSender(this).useHint(" Wertlisten sind nicht zulässig.");
			this.skip();
			if (this.skipSpace().type == ']') {
				this.skip();
				return ScriptCompiler.EMPTY_ARRAY_FUNCTION;
			}
			final List<FEMFunction> list = new ArrayList<>();
			boolean value = true;
			while (true) {
				final FEMFunction item = this.compileNextParamAsFunction();
				list.add(item);
				value = value && (item instanceof FEM.ValueFunction);
				switch (this.skipSpace().type) {
					case ';': {
						this.skip();
						this.skipSpace();
						break;
					}
					case ']': {
						this.skip();
						final int size = list.size();
						if (!value) {
							final FEMFunction result = new FEM.InvokeFunction(FEM.PARAMS_VIEW_FUNCTION, true, list.toArray(new FEMFunction[size]));
							return result;
						}
						final FEMValue[] values = new FEMValue[size];
						for (int i = 0; i < size; i++) {
							values[i] = list.get(i).invoke(FEMFrame.EMPTY);
						}
						return new FEM.ValueFunction(FEM.arrayValue(FEMArray.from(values)));
					}
					default: {
						throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «]» erwartet.");
					}
				}
			}
		}
	
		/**
		 * Diese Methode kompiliert via {@code this.helper().compileParam(this, this.section())} den beim aktuellen Bereich beginnende Parameter und gibt diesen
		 * zurück.
		 * 
		 * @see ScriptCompilerHelper#compileParam(ScriptCompiler, String)
		 * @return Parameter.
		 * @throws ScriptException Wenn {@link #section()} ungültig ist.
		 */
		protected FEMFunction compileNextParam() throws ScriptException {
			try {
				final FEMFunction result = this.helper.compileParam(this, this.section());
				if (result == null) throw new ScriptException().useSender(this).useHint(" Parameter erwartet.");
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this);
			}
		}
	
		/**
		 * Diese Methode kompiliert denF beim aktuellen Bereich beginnende Wert und gibt diese zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEMValue compileNextParamAsValue() throws ScriptException {
			switch (this.skipSpace().type) {
				case 0:
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
					return this.compileNextArrayAsValue();
				}
				case '{': {
					if (this.closureEnabled) throw new ScriptException().useSender(this).useHint(" Ungebundene Funktion unzulässig.");
					final FEMFunction retult = this.compileNextScope();
					return FEM.functionValue(retult);
				}
				default: {
					final FEMFunction param = this.compileNextParam();
					if (!(param instanceof FEM.ValueFunction)) throw new ScriptException().useSender(this).useHint(" Wert erwartet.");
					final FEMValue result = param.invoke(FEMFrame.EMPTY);
					return result;
				}
			}
		}
	
		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEMFunction compileNextParamAsFunction() throws ScriptException {
			FEMFunction result;
			boolean indirect = false;
			switch (this.skipSpace().type) {
				case 0:
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
					final String name = this.compileNextName();
					if (name == null) return FEM.PARAMS_VIEW_FUNCTION;
					int index = this.parseIndex(name);
					if (index < 0) {
						index = this.params.indexOf(name);
						if (index < 0) throw new ScriptException().useSender(this).useHint(" Parametername «%s» ist unbekannt.", name);
					} else if (index > 0) {
						index--;
					} else throw new ScriptException().useSender(this).useHint(" Parameterindex «%s» ist unzulässig.", index);
					return FEM.ParamFunction.from(index);
				}
				case '{': {
					result = this.compileNextScope();
					if (this.skipSpace().type != '(') {
						if (this.closureEnabled) return new FEM.ClosureFunction(result);
						return new FEM.ValueFunction(FEM.functionValue(result));
					}
					if (!this.chainingEnabled) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
					break;
				}
				case '[': {
					return this.compileNextArrayAsFunction();
				}
				default: {
					result = this.compileNextParam();
					if (this.skipSpace().type != '(') {
						if (this.handlerEnabled) return new FEM.ValueFunction(FEM.functionValue(result));
						throw new ScriptException().useSender(this).useHint(" Funktionsverweise sind nicht zulässig.");
					}
				}
			}
			do {
				if (indirect && !this.chainingEnabled) throw new ScriptException().useSender(this).useHint(" Funktionsverkettungen ist nicht zulässsig.");
				this.skip(); // '('
				this.skipSpace();
				final List<FEMFunction> list = new ArrayList<>();
				while (true) {
					if (this.range.type == ')') {
						this.skip();
						result = new FEM.InvokeFunction(result, !indirect, list.toArray(new FEMFunction[list.size()]));
						break;
					}
					final FEMFunction item = this.compileNextParamAsFunction();
					list.add(item);
					switch (this.skipSpace().type) {
						default:
							throw new ScriptException().useSender(this).useHint(" Zeichen «;» oder «)» erwartet.");
						case ';':
							this.skip();
						case ')':
					}
				}
				indirect = true;
			} while (this.skipSpace().type == '(');
			return result;
		}
	
		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
		 * 
		 * @return Parameterfunktion.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEM.ProxyFunction compileNextProxy() throws ScriptException {
			final String name = this.compileNextName();
			if ((name == null) || (this.parseIndex(name) >= 0)) throw new ScriptException().useSender(this).useHint(" Funktionsname erwartet.");
			final FEM.ProxyFunction result = this.proxy(name);
			if (this.skipSpace().type != '{') throw new ScriptException().useSender(this).useHint(" Parametrisierter Funktionsaufruf erwartet.");
			final FEMFunction target = this.compileNextScope();
			result.set(target);
			return result;
		}
	
		/**
		 * Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
		 * Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0} ist.
		 * 
		 * @return Funktions- oder Parametername oder {@code null}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected String compileNextName() throws ScriptException {
			try {
				switch (this.skipSpace().type) {
					case '$':
					case '(':
					case '[':
					case '{': {
						throw new IllegalStateException();
					}
					case 0:
					case ':':
					case ';':
					case ')':
					case '}':
					case ']': {
						return null;
					}
				}
				final String result = this.helper.compileName(this, this.section());
				if (result.isEmpty()) throw new IllegalArgumentException();
				this.skip();
				return result;
			} catch (final ScriptException cause) {
				throw cause;
			} catch (final RuntimeException cause) {
				throw new ScriptException(cause).useSender(this).useHint(" Funktionsname, Parametername oder Parameterindex erwartet.");
			}
		}
	
		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich (<code>'{'</code>) beginnende, parametrisierte Funktion in einen
		 * {@link FEM#functionValue(FEMFunction)} und gibt diesen zurück.
		 * 
		 * @return Funktion als {@link FEM#functionValue(FEMFunction)}.
		 * @throws ScriptException Wenn {@link #script()} ungültig ist.
		 */
		protected FEMFunction compileNextScope() throws ScriptException {
			this.skip();
			int count = 0;
			while (true) {
				if (this.skipSpace().type == 0) throw new ScriptException().useSender(this);
				final String name = this.compileNextName();
				if (name != null) {
					if (this.parseIndex(name) >= 0) throw new ScriptException().useSender(this).useHint(" Parametername erwartet.");
					this.params.add(count++, name);
				}
				switch (this.skipSpace().type) {
					case ';': {
						if (name == null) throw new ScriptException().useSender(this).useHint(" Parametername oder Zeichen «:» erwartet.");
						this.skip();
						break;
					}
					case ':': {
						this.skip();
						final FEMFunction result = this.compileNextParamAsFunction();
						if (this.skipSpace().type != '}') throw new ScriptException().useSender(this).useHint(" Zeichen «}» erwartet.");
						this.skip();
						this.params.subList(0, count).clear();
						return result;
					}
					default: {
						throw new ScriptException().useSender(this);
					}
				}
			}
		}
	
		/**
		 * Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
		 * 
		 * @param name Name des Platzhalters.
		 * @return Platzhalterfunktion.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 */
		public final FEM.ProxyFunction proxy(final String name) throws NullPointerException {
			if (name == null) throw new NullPointerException("name = null");
			synchronized (this.proxies) {
				FEM.ProxyFunction result = this.proxies.get(name);
				if (result != null) return result;
				this.proxies.put(name, result = new FEM.ProxyFunction(name));
				return result;
			}
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
		 * Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.
		 * 
		 * @return Abbildung von Namen auf Platzhalter.
		 */
		public final Map<String, FEM.ProxyFunction> proxies() {
			return this.proxies;
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die von {@link ScriptCompilerHelper#compileParam(ScriptCompiler, String)} als
		 * {@link FEM#functionValue(FEMFunction)} gelieferten Funktionen als Funktionszeiger zu {@link FEM.ValueFunction}s kompiliert werden dürfen (z.B
		 * {@code SORT(array; compFun)}).
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit von Funktionszeigern.
		 */
		public final boolean isHandlerEnabled() {
			return this.handlerEnabled;
		}
	
		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn parametrisierte Funktionen zu {@link FEM.ClosureFunction}s kompiliert werden.
		 * 
		 * @see #compileFunction()
		 * @return Zulässigkeit der Bindung des Rahmendatens.
		 */
		public final boolean isClosureEnabled() {
			return this.closureEnabled;
		}
	
		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
		 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
		 * 
		 * @see #compileFunction()
		 * @see InvokeFunction#direct()
		 * @see InvokeFunction#invoke(FEMFrame)
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
			if (value == null) throw new NullPointerException("value = null");
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
			if (value == null) throw new NullPointerException("value = null");
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
			if (value == null) throw new NullPointerException("value = null");
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
			if (value == null) throw new NullPointerException("value = null");
			if (value.contains(null)) throw new NullPointerException("value.contains(null)");
			this.checkIdling();
			this.params.clear();
			this.params.addAll(value);
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
		 * Diese Methode setzt die Zulässigkeit der Bindung des Rahmendatens.
		 * 
		 * @see #isClosureEnabled()
		 * @param value Zulässigkeit der Bindung des Rahmendatens.
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
		 * Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Wert oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMValue compileValue() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				if (this.skipSpace().type == 0) return null;
				final FEMValue result = this.compileNextParamAsValue();
				if (this.skipSpace().type == 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.stopCompiling();
			}
		}
	
		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück.<br>
		 * Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert sein. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Wertliste
		 * geliefert.
		 * 
		 * @return Werte.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMValue[] compileValues() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				if (this.skipSpace().type == 0) return new FEMValue[0];
				final List<FEMValue> result = new ArrayList<FEMValue>();
				while (true) {
					result.add(this.compileNextParamAsValue());
					switch (this.skipSpace().type) {
						case 0: {
							return result.toArray(new FEMValue[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.stopCompiling();
			}
		}
	
		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück.<br>
		 * Die Parameterfunktion müssen durch Bereiche vom Typ {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem
		 * {@link ScriptCompilerHelper#compileName(ScriptCompiler, String) Namen} und ist sonst durch eine parametrisierte Funktion gegeben. Wenn der Quelltext nur
		 * Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEM.ProxyFunction[] compileProxies() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				final List<FEM.ProxyFunction> result = new ArrayList<FEM.ProxyFunction>();
				if (this.skipSpace().type == 0) return new FEM.ProxyFunction[0];
				while (true) {
					result.add(this.compileNextProxy());
					switch (this.skipSpace().type) {
						case 0: {
							return result.toArray(new FEM.ProxyFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.stopCompiling();
			}
		}
	
		/**
		 * Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
		 * 
		 * @return Funktion oder {@code null}.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMFunction compileFunction() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				if (this.skipSpace().type == 0) return null;
				final FEMFunction result = this.compileNextParamAsFunction();
				if (this.skipSpace().type == 0) return result;
				throw new ScriptException().useSender(this).useHint(" Keine weiteren Definitionen erwartet.");
			} finally {
				this.stopCompiling();
			}
		}
	
		/**
		 * Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
		 * separiert sein.<br>
		 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
		 * 
		 * @return Funktionen.
		 * @throws ScriptException Wenn der Quelltext ungültig ist.
		 * @throws IllegalStateException Wenn aktuell kompiliert wird.
		 */
		public FEMFunction[] compileFunctions() throws ScriptException, IllegalStateException {
			this.startCompiling();
			try {
				if (this.skipSpace().type == 0) return new FEMFunction[0];
				final List<FEMFunction> result = new ArrayList<FEMFunction>();
				while (true) {
					result.add(this.compileNextParamAsFunction());
					switch (this.skipSpace().type) {
						case 0: {
							return result.toArray(new FEMFunction[result.size()]);
						}
						case ';': {
							this.skip();
						}
					}
				}
			} finally {
				this.stopCompiling();
			}
		}
	
		{}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.helper, this.params, this.script, this.proxies);
		}
	
	}

	/**
	 * Diese Schnittstelle definiert Kompilationsmethoden, die von einem {@link ScriptCompiler Kompiler} zur Übersetzung von Quelltexten in Werte, Funktionen und
	 * Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptCompilerHelper {
	
		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden immer {@code null} liefern.
		 */
		static ScriptCompilerHelper DEFAULT = new ScriptCompilerHelper() {
	
			String content(final ScriptCompiler compiler, final String section) {
				final int length = section.length() - 1;
				if (length <= 0) throw new ScriptException().useSender(compiler);
				return section.substring(1, length);
			}
	
			{}
	
			@Override
			public String compileName(final ScriptCompiler compiler, final String section) throws ScriptException {
				return section;
			}
	
			@Override
			public FEMFunction compileParam(final ScriptCompiler compiler, final String section) throws ScriptException {
				switch (compiler.range().type()) {
					case '\'': {
						return new FEM.ValueFunction(FEM.stringValue(this.content(compiler, section).replaceAll("''", "'")));
					}
					case '"': {
						return new FEM.ValueFunction(FEM.stringValue(this.content(compiler, section).replaceAll("\"\"", "\"")));
					}
					case '?': {
						final String name = this.content(compiler, section).replaceAll("<<", "<").replaceAll(">>", ">");
						return compiler.proxy(name);
					}
					default: {
						if (section.equalsIgnoreCase("NULL")) return new FEM.ValueFunction(FEM.NULL);
						if (section.equalsIgnoreCase("TRUE")) return new FEM.ValueFunction(FEM.__true);
						if (section.equalsIgnoreCase("FALSE")) return new FEM.ValueFunction(FEM.FALSE);
						try {
							return new FEM.ValueFunction(FEM.numberValue(new BigDecimal(section)));
						} catch (final NumberFormatException cause) {
							return compiler.proxy(section);
						}
					}
				}
			}
	
			@Override
			public String toString() {
				return "DEFAULT";
			}
	
		};
	
		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebenen Funktions- bzw. Parameternamen zurück.
		 * 
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @param section aktuellen Bereich des Quelltexts ({@link ScriptCompiler#section()}).
		 * @return Funktions- bzw. Parametername.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Namen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
		 */
		public String compileName(ScriptCompiler compiler, String section) throws ScriptException;
	
		/**
		 * Diese Methode gibt den im aktuellen Bereich des Quelltexts des gegebenen Kompilers angegebene Parameter als Funktion zurück. Ein Parameter kann hierbei
		 * für eine Funktion stehen. Konstante Parameterwerte sollten als {@link FEM.ValueFunction} oder {@link FEM.ProxyFunction} geliefert werden.
		 * 
		 * @see ScriptCompiler#proxy(String)
		 * @see ScriptCompiler#range()
		 * @see ScriptCompiler#script()
		 * @param compiler Kompiler mit Bereich und Quelltext.
		 * @param section aktuellen Bereich des Quelltexts ({@link ScriptCompiler#section()}).
		 * @return Parameter als {@link FEMFunction}, Parameterwert als {@link FEM.ValueFunction} oder Platzhalter als {@link FEM.ProxyFunction}.
		 * @throws ScriptException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält.
		 */
		public FEMFunction compileParam(ScriptCompiler compiler, String section) throws ScriptException;
	
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
	
			{}
	
			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return "M" + (this.level() == 0 ? "" : (this.isLast() ? "D" : this.isSpace() ? "S" : "I") + (this.isEnabled() ? "E" : ""));
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
		protected final void startFormatting() throws IllegalStateException {
			this.checkIdling();
			this.indents.addLast(Boolean.FALSE);
		}
	
		/**
		 * Diese Methode beendet das Parsen und sollte nur in Verbindung mit {@link #startFormatting()} verwendet werden.
		 */
		protected final void stopFormatting() {
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
		 * Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * seine {@link Object#toString() Textdarstellung} angefügt.
		 * 
		 * @see Object#toString()
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @param part Objekt.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code part} nicht formatiert werden kann.
		 */
		public ScriptFormatter put(final Object part) throws IllegalStateException, IllegalArgumentException {
			if (part == null) throw new NullPointerException("part = null");
			this.checkFormatting();
			if (part instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)part).toScript(this);
			} else {
				this.items.add(part.toString());
			}
			return this;
		}
	
		/**
		 * Diese Methode fügt die Zeichenkette des gegebenen Objekts an und gibt {@code this} zurück.<br>
		 * Wenn das Objekt ein {@link ScriptFormatterInput} ist, wird es über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird es
		 * über {@link ScriptFormatterHelper#formatData(ScriptFormatter, Object)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatData(ScriptFormatter, Object)
		 * @param data Objekt.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann.
		 */
		public ScriptFormatter putData(final Object data) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (data == null) throw new NullPointerException("function = null");
			this.checkFormatting();
			if (data instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)data).toScript(this);
			} else {
				this.helper.formatData(this, data);
			}
			return this;
		}
	
		/**
		 * Diese Methode fügt die gegebenen Wertliste an und gibt {@code this} zurück.<br>
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
		 * @throws IllegalArgumentException Wenn {@code array} nicht formatiert werden kann.
		 */
		public ScriptFormatter putArray(final Iterable<? extends FEMValue> array) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (array == null) throw new NullPointerException("array = null");
			this.checkFormatting();
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
	
		/**
		 * Diese Methode fügt den Quelltext des gegebenen Werts an und gibt {@code this} zurück.<br>
		 * Wenn der Wert ein {@link ScriptFormatterInput} ist, wird er über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird er
		 * über {@link ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatValue(ScriptFormatter, FEMValue)
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann.
		 */
		public ScriptFormatter putValue(final FEMValue value) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (value == null) throw new NullPointerException("value = null");
			this.checkFormatting();
			if (value instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)value).toScript(this);
			} else {
				this.helper.formatValue(this, value);
			}
			return this;
		}
	
		/**
		 * Diese Methode fügt den Quelltext der Liste der gegebenen zugesicherten Parameterwerte eines Rahmendatens an und gibt {@code this} zurück.<br>
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
		 * @param params Rahmendaten.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code params} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann.
		 */
		public ScriptFormatter putScope(final Iterable<? extends FEMValue> params) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this.checkFormatting();
			final Iterator<? extends FEMValue> iter = params.iterator();
			if (iter.hasNext()) {
				FEMValue item = iter.next();
				if (iter.hasNext()) {
					this.putIndent().put("(").putBreakInc().put("$1: ").putValue(item);
					int index = 2;
					do {
						item = iter.next();
						this.put(";").putBreakSpace().put("$").put(index).put(": ").putValue(item);
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
	
		/**
		 * Diese Methode fügt den Quelltext der Liste der gegebenen Parameterfunktionen an und gibt {@code this} zurück.<br>
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
		 * @throws IllegalArgumentException Wenn {@code params} nicht formatiert werden kann.
		 */
		public ScriptFormatter putParams(final Iterable<? extends FEMFunction> params) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (params == null) throw new NullPointerException("params = null");
			this.checkFormatting();
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
	
		/**
		 * Diese Methode fügt die gegebenen, parametrisierte Funktion an und gibt {@code this} zurück.<br>
		 * Die parametrisierte Funktion wird dabei in <code>"{: "</code> und <code>"}"</code> eingeschlossen und über {@link #putFunction(FEMFunction)} angefügt.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param function parametrisierte Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public ScriptFormatter putHandler(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			return this.put("{: ").putFunction(function).put("}");
		}
	
		/**
		 * Diese Methode fügt den Quelltext der gegebenen Funktion an und gibt {@code this} zurück.<br>
		 * Wenn die Funktion ein {@link ScriptFormatterInput} ist, wird sie über {@link ScriptFormatterInput#toScript(ScriptFormatter)} angefügt. Andernfalls wird
		 * sie über {@link ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)} angefügt.
		 * 
		 * @see ScriptFormatterInput#toScript(ScriptFormatter)
		 * @see ScriptFormatterHelper#formatFunction(ScriptFormatter, FEMFunction)
		 * @param function Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code function} {@code null} ist.
		 * @throws IllegalStateException Wenn aktuell nicht formatiert wird.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public ScriptFormatter putFunction(final FEMFunction function) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (function == null) throw new NullPointerException("function = null");
			this.checkFormatting();
			if (function instanceof ScriptFormatterInput) {
				((ScriptFormatterInput)function).toScript(this);
			} else {
				this.helper.formatFunction(this, function);
			}
			return this;
		}
	
		/**
		 * Diese Methode gibt die Zeichenkette zur Einrückung einer Hierarchieebene zurück. Diese ist {@code null}, wenn nicht eingerückt wird.
		 * 
		 * @return Zeichenkette zur Einrückung oder {@code null}.
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
		 * Diese Methode setzt die Zeichenkette zur Einrückung einer Hierarchieebene und gibt {@code this} zurück. Wenn diese {@code null} ist, wird nicht
		 * eingerückt.
		 * 
		 * @param indent Zeichenkette zur Einrückung (z.B. {@code null}, {@code "\t"} oder {@code "  "}).
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 */
		public ScriptFormatter useIndent(final String indent) throws IllegalStateException {
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
	
		/**
		 * Diese Methode formatiert die gegebenen Elemente in einen Quelltext und gibt diesen zurück.<br>
		 * Die Elemente werden über den gegebenen {@link Converter} angefügt und mit {@code ';'} separiert. In der Methode {@link Converter#convert(Object)} sollten
		 * hierfür {@link #putData(Object)}, {@link #putValue(FEMValue)} bzw. {@link #putFunction(FEMFunction)} aufgerufen werden.
		 * 
		 * @see #formatData(Iterable)
		 * @see #formatValue(Iterable)
		 * @see #formatFunction(Iterable)
		 * @param <GItem> Typ der Elemente.
		 * @param items Elemente.
		 * @param formatter {@link Converter} zur Aufruf der spetifischen Formatierungsmethoden je Element.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Element nicht formatiert werden kann.
		 */
		private <GItem> String format(final Iterable<? extends GItem> items, final Converter<GItem, ?> formatter) throws NullPointerException,
			IllegalStateException, IllegalArgumentException {
			this.startFormatting();
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
				return this.format();
			} finally {
				this.stopFormatting();
			}
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatData(Arrays.asList(datas))}.
		 * 
		 * @see #formatData(Iterable)
		 * @param datas Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code datas} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatData(final Object... datas) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (datas == null) throw new NullPointerException("datas = null");
			return this.formatData(Arrays.asList(datas));
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Objekt in einen Quelltext und gibt diesen zurück.<br>
		 * Die Objekt werden über {@link #putData(Object)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putData(Object)
		 * @param datas Objekte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code datas} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Objekt nicht formatiert werden kann.
		 */
		public String formatData(final Iterable<?> datas) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (datas == null) throw new NullPointerException("values = null");
			return this.format(datas, new Converter<Object, Object>() {
	
				@Override
				public Object convert(final Object input) {
					return ScriptFormatter.this.putData(input);
				}
	
			});
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatValue(Arrays.asList(values))}.
		 * 
		 * @see #formatValue(Iterable)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatValue(final FEMValue... values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (values == null) throw new NullPointerException("values = null");
			return this.formatValue(Arrays.asList(values));
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Wert in einen Quelltext und gibt diesen zurück.<br>
		 * Die Werte werden über {@link #putValue(FEMValue)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putValue(FEMValue)
		 * @param values Werte.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn ein Wert nicht formatiert werden kann.
		 */
		public String formatValue(final Iterable<? extends FEMValue> values) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (values == null) throw new NullPointerException("values = null");
			return this.format(values, new Converter<FEMValue, Object>() {
	
				@Override
				public Object convert(final FEMValue input) {
					return ScriptFormatter.this.putValue(input);
				}
	
			});
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Der Rückgabewert entspricht {@code this.formatFunction(Arrays.asList(functions))}.
		 * 
		 * @see #formatFunction(Iterable)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann.
		 */
		public String formatFunction(final FEMFunction... functions) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (functions == null) throw new NullPointerException("functions = null");
			return this.formatFunction(Arrays.asList(functions));
		}
	
		/**
		 * Diese Methode formatiert die gegebenen Funktionen in einen Quelltext und gibt diesen zurück.<br>
		 * Die Funktionen werden über {@link #putFunction(FEMFunction)} angefügt und mit {@code ';'} separiert.
		 * 
		 * @see #putFunction(FEMFunction)
		 * @param functions Funktionen.
		 * @return formatierter Quelltext.
		 * @throws NullPointerException Wenn {@code functions} {@code null} ist oder enthält.
		 * @throws IllegalStateException Wenn aktuell formatiert wird.
		 * @throws IllegalArgumentException Wenn eine Funktion nicht formatiert werden kann.
		 */
		public String formatFunction(final Iterable<? extends FEMFunction> functions) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (functions == null) throw new NullPointerException("functions = null");
			return this.format(functions, new Converter<FEMFunction, Object>() {
	
				@Override
				public Object convert(final FEMFunction input) {
					return ScriptFormatter.this.putFunction(input);
				}
	
			});
		}
	
		{}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.helper, this.indent, this.items);
		}
	
	}

	/**
	 * Diese Schnittstelle definiert ein Objekt, welches sich selbst in seine Quelltextdarstellung überführen und diese an einen {@link ScriptFormatter} anfügen
	 * kann.
	 * 
	 * @see ScriptFormatter#put(Object)
	 * @see ScriptFormatter#putValue(FEMValue)
	 * @see ScriptFormatter#putFunction(FEMFunction)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptFormatterInput {
	
		/**
		 * Diese Methode formatiert dieses Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.<br>
		 * Sie wird vom {@link ScriptFormatter} im Rahmen folgender Methoden aufgerufen:
		 * <ul>
		 * <li>{@link ScriptFormatter#put(Object)}</li>
		 * <li>{@link ScriptFormatter#putData(Object)}</li>
		 * <li>{@link ScriptFormatter#putValue(FEMValue)}</li>
		 * <li>{@link ScriptFormatter#putFunction(FEMFunction)}</li>
		 * </ul>
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @throws IllegalArgumentException Wenn das Objekt nicht formatiert werden kann.
		 */
		public void toScript(final ScriptFormatter target) throws IllegalArgumentException;
	
	}

	/**
	 * Diese Schnittstelle definiert Formatierungsmethoden, die in den Methoden {@link ScriptFormatter#putValue(FEMValue)} und
	 * {@link ScriptFormatter#putFunction(FEMFunction)} zur Übersetzung von Werten und Funktionen in Quelltexte genutzt werden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public interface ScriptFormatterHelper {
	
		/**
		 * Dieses Feld speichert den {@link ScriptFormatterHelper}, dessen Methoden ihre Eingeben über {@link String#valueOf(Object)} formatieren.
		 */
		static ScriptFormatterHelper DEFAULT = new ScriptFormatterHelper() {
	
			@Override
			public void formatData(final ScriptFormatter target, final Object data) throws IllegalArgumentException {
				target.put(String.valueOf(data));
			}
	
			@Override
			public void formatValue(final ScriptFormatter target, final FEMValue value) throws IllegalArgumentException {
				target.put(String.valueOf(value));
			}
	
			@Override
			public void formatFunction(final ScriptFormatter target, final FEMFunction function) throws IllegalArgumentException {
				target.put(String.valueOf(function));
			}
	
			@Override
			public String toString() {
				return "DEFAULT";
			}
	
		};
	
		/**
		 * Diese Methode formatiert das gegebene Objekt in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param data Objekt.
		 * @throws IllegalArgumentException Wenn {@code data} nicht formatiert werden kann.
		 */
		public void formatData(ScriptFormatter target, Object data) throws IllegalArgumentException;
	
		/**
		 * Diese Methode formatiert den gegebenen Wert in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param value Wert.
		 * @throws IllegalArgumentException Wenn {@code value} nicht formatiert werden kann.
		 */
		public void formatValue(ScriptFormatter target, FEMValue value) throws IllegalArgumentException;
	
		/**
		 * Diese Methode formatiert die gegebene Funktion in einen Quelltext und fügt diesen an den gegebenen {@link ScriptFormatter} an.
		 * 
		 * @param target {@link ScriptFormatter}.
		 * @param function Funktion.
		 * @throws IllegalArgumentException Wenn {@code function} nicht formatiert werden kann.
		 */
		public void formatFunction(ScriptFormatter target, FEMFunction function) throws IllegalArgumentException;
	
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
		Script script = EMPTY;
	
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
		 * @param hint Hinweis.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code hint} {@code null} ist.
		 */
		public ScriptException useHint(final String hint) throws NullPointerException {
			if (hint == null) throw new NullPointerException("hint = null");
			this.hint = hint;
			return this;
		}
	
		/**
		 * Diese Methode setzt den Hinweis und gibt {@code this} zurück.
		 * 
		 * @see #useHint(String)
		 * @see String#format(String, Object...)
		 * @param format Hinweis.
		 * @param args Formatargumente.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code format} bzw. {@code args} {@code null} ist.
		 */
		public ScriptException useHint(final String format, final Object... args) throws NullPointerException {
			return this.useHint(String.format(format, args));
		}
	
		/**
		 * Diese Methode setzt den Bereich und gibt {@code this} zurück.
		 * 
		 * @see #getRange()
		 * @param range Bereich.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code range} {@code null} ist.
		 */
		public ScriptException useRange(final Range range) throws NullPointerException {
			if (range == null) throw new NullPointerException("range = null");
			this.range = range;
			return this;
		}
	
		/**
		 * Diese Methode setzt den Quelltext und gibt {@code this} zurück.
		 * 
		 * @see #getScript()
		 * @param script Quelltext.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code script} {@code null} ist.
		 */
		public ScriptException useScript(final Script script) throws NullPointerException {
			if (script == null) throw new NullPointerException("script = null");
			this.script = script;
			return this;
		}
	
		/**
		 * Diese Methode setzt Quelltext sowie Bereich und gibt {@code this} zurück.
		 * 
		 * @see #useScript(Script)
		 * @see #useRange(Range)
		 * @param sender {@link ScriptCompiler}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code sender} {@code null} ist.
		 */
		public ScriptException useSender(final ScriptCompiler sender) throws NullPointerException {
			if (sender == null) throw new NullPointerException("sender = null");
			return this.useRange(sender.range()).useScript(sender.script());
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

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung der Zustandsdaten einer {@link FEM.TraceFunction} zur Verfolgung und Überwachung der Verarbeitung von
	 * Funktionen. Dieses Objekt wird dazu das Argument für die Methoden des {@link ScriptTracerHelper} genutzt, welcher auf die Ereignisse der Überwachung
	 * reagieren kann.
	 * 
	 * @see FEM.TraceFunction
	 * @see ScriptTracerHelper
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ScriptTracer {
	
		/**
		 * Dieses Feld speichert den {@link ScriptTracerHelper}.
		 */
		ScriptTracerHelper helper = ScriptTracerHelper.DEFAULT;
	
		/**
		 * Dieses Feld speichert den Rahmendaten der Funktion. Dieser kann in der Methode {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf
		 * angepasst werden.
		 */
		FEMFrame scope;
	
		/**
		 * Dieses Feld speichert die Function, die nach {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen wird bzw. vor
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} oder {@link ScriptTracerHelper#onReturn(ScriptTracer)} aufgerufen wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onExecute(ScriptTracer)} für den Aufruf angepasst werden.
		 */
		FEMFunction function;
	
		/**
		 * Dieses Feld speichert den Ergebniswert, der von der Funktion zurück gegeben wurde. Dieser kann in der Methode
		 * {@link ScriptTracerHelper#onReturn(ScriptTracer)} angepasst werden.
		 */
		FEMValue result;
	
		/**
		 * Dieses Feld speichert die {@link RuntimeException}, die von der Funktion ausgelöst wurde. Diese kann in der Methode
		 * {@link ScriptTracerHelper#onThrow(ScriptTracer)} angepasst werden.
		 */
		RuntimeException exception;
	
		{}
	
		/**
		 * Diese Methode gibt den {@link ScriptTracerHelper} zurück.
		 * 
		 * @return {@link ScriptTracerHelper}.
		 */
		public ScriptTracerHelper getHelper() {
			return this.helper;
		}
	
		/**
		 * Diese Methode gibt den aktuellen Rahmendaten zurück, der zur Auswertung der {@link #getFunction() aktuellen Funktion} verwendet wird.
		 * 
		 * @return Rahmendaten oder {@code null}.
		 */
		public FEMFrame getScope() {
			return this.scope;
		}
	
		/**
		 * Diese Methode gibt die aktuelle Funktion zurück, die mit dem {@link #getScope() aktuellen Rahmendaten} ausgewertet wird.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public FEMFunction getFunction() {
			return this.function;
		}
	
		/**
		 * Diese Methode gibt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ergebniswert oder {@code null}.
		 */
		public FEMValue getResult() {
			return this.result;
		}
	
		/**
		 * Diese Methode gibt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} zurück.
		 * 
		 * @return Ausnahme oder {@code null}.
		 */
		public RuntimeException getException() {
			return this.exception;
		}
	
		/**
		 * Diese Methode setzt die Überwachungsmethoden und gibt {@code this} zurück.
		 * 
		 * @param value Überwachungsmethoden.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useHelper(final ScriptTracerHelper value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.helper = value;
			return this;
		}
	
		/**
		 * Diese Methode setzt den aktuellen Rahmendaten und gibt {@code this} zurück. Dieser wird zur Auswertung der {@link #getFunction() aktuellen Funktion}
		 * verwendet.
		 * 
		 * @param value Rahmendaten.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useScope(final FEMFrame value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.scope = value;
			return this;
		}
	
		/**
		 * Diese Methode setzt die aktuelle Funktion und gibt {@code this} zurück. Diese wird mit dem {@link #getScope() aktuellen Rahmendaten} ausgewertet.
		 * 
		 * @param value Funktion.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useFunction(final FEMFunction value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.function = value;
			return this;
		}
	
		/**
		 * Diese Methode setzt den aktuellen Ergebniswert der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Die {@link #getException()
		 * aktuelle Ausnahme} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ergebniswert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useResult(final FEMValue value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.result = value;
			this.exception = null;
			return this;
		}
	
		/**
		 * Diese Methode setzt die aktuelle Ausnahme der {@link #getFunction() aktuellen Funktion} und gibt {@code this} zurück. Der {@link #getResult() aktuelle
		 * Ergebniswert} wird damit auf {@code null} gesetzt.
		 * 
		 * @param value Ausnahme.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 */
		public ScriptTracer useException(final RuntimeException value) throws NullPointerException {
			if (value == null) throw new NullPointerException("value = null");
			this.result = null;
			this.exception = value;
			return this;
		}
	
		/**
		 * Diese Methode setzt alle aktuellen Einstellungen auf {@code null} und gibt {@code this} zurück.
		 * 
		 * @see #getScope()
		 * @see #getFunction()
		 * @see #getResult()
		 * @see #getException()
		 * @return {@code this}.
		 */
		public ScriptTracer clear() {
			this.scope = null;
			this.function = null;
			this.result = null;
			this.exception = null;
			return this;
		}
	
		/**
		 * Diese Methode gibt die gegebenen Funktion als {@link FEM.TraceFunction} oder unverändert zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation von
		 * {@link ScriptTracer#function} verwendet werden.
		 * <p>
		 * Wenn die Funktion ein {@link ScriptTracerInput} ist, wird das Ergebnis von {@link ScriptTracerInput#toTrace(ScriptTracer)} zurück gegeben. Andernfalls
		 * wird die gegebene Funktion zurück gegeben.
		 * 
		 * @param function Funktion.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public FEMFunction trace(final FEMFunction function) throws NullPointerException {
			if (function == null) throw new NullPointerException("function = null");
			if (function instanceof ScriptTracerInput) return ((ScriptTracerInput)function).toTrace(this);
			return function;
		}
	
		{}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toFormatString(true, true, this, "helper", this.helper, "scope", this.scope, "function", this.function, "result", this.result,
				"exception", this.exception);
		}
	
	}

	/**
	 * Diese Schnittstelle definiert ein Objekt, welches sich selbst in eine {@link FEM.TraceFunction} überführen kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptTracerInput {
	
		/**
		 * Diese Methode gibt dieses Objekt als als {@link FEM.TraceFunction} mit dem gegebenen {@link ScriptTracer} zurück.<br>
		 * Sie sollte zur rekursiven Weiterverfolgung in {@link ScriptTracerHelper#onExecute(ScriptTracer)} aufgerufen und zur Modifikation der
		 * {@link ScriptTracer#getFunction() aktuellen Funktion} des {@link ScriptTracer} verwendet werden.<br>
		 * Wenn dieses Objekt ein Wert ist, muss er sich in einer {@link FEM.ValueFunction} liefern.
		 * 
		 * @param tracer {@link ScriptTracer}.
		 * @return Funktion.
		 * @throws NullPointerException Wenn {@code handler} bzw. {@code function} {@code null} ist.
		 */
		public FEMFunction toTrace(final ScriptTracer tracer) throws NullPointerException;
	
	}

	/**
	 * Diese Schnittstelle definiert die Überwachungsmethoden zur Verfolgung der Verarbeitung von Funktionen.
	 * 
	 * @see ScriptTracer
	 * @see FEM.TraceFunction
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptTracerHelper {
	
		/**
		 * Dieses Feld speichert den {@code default}-{@link ScriptTracerHelper}, dessen Methoden den {@link ScriptTracer} nicht modifizieren.
		 */
		public static final ScriptTracerHelper DEFAULT = new ScriptTracerHelper() {
	
			@Override
			public void onThrow(final ScriptTracer event) {
			}
	
			@Override
			public void onReturn(final ScriptTracer event) {
			}
	
			@Override
			public void onExecute(final ScriptTracer event) {
			}
	
			@Override
			public String toString() {
				return "DEFAULT";
			}
	
		};
	
		/**
		 * Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code throw} aufgerufen. Das Feld
		 * {@link ScriptTracer#exception} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#exception
		 * @param event {@link ScriptTracer}.
		 */
		public void onThrow(ScriptTracer event);
	
		/**
		 * Diese Methode wird nach dem Verlassen der {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} einer Funktion via {@code return} aufgerufen. Das Feld
		 * {@link ScriptTracer#result} kann hierbei angepasst werden.
		 * 
		 * @see ScriptTracer#result
		 * @param event {@link ScriptTracer}.
		 */
		public void onReturn(ScriptTracer event);
	
		/**
		 * Diese Methode wird vor dem Aufruf einer Funktion aufgerufen. Die Felder {@link ScriptTracer#scope} und {@link ScriptTracer#function} können hierbei
		 * angepasst werden, um den Aufruf auf eine andere Funktion umzulenken bzw. mit einem anderen Rahmendaten durchzuführen.
		 * 
		 * @see ScriptTracer#scope
		 * @see ScriptTracer#function
		 * @param event {@link ScriptTracer}.
		 */
		public void onExecute(ScriptTracer event);
	
	}

	{}

	/**
	 * Dieses Feld speichert den leeren Quelltext ohne Bereiche.
	 */
	public static final Script EMPTY = new Script("", new Range[0]);

	{}

	/**
	 * Dieses Feld speichert die Zeichenkette.
	 */
	final String source;

	/**
	 * Dieses Feld speichert die Bereiche.
	 */
	final Range[] ranges;

	/**
	 * Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen.
	 */
	public Script(final String source, final Range[] ranges) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		final int length = source.length();
		for (final Range range: ranges) {
			final int start = range.start;
			if (start < offset) throw new IllegalArgumentException("ranges overlapping");
			offset = start + range.length;
		}
		if (offset > length) throw new IllegalArgumentException("ranges exceeding");
		this.source = source;
		this.ranges = ranges.clone();
	}

	/**
	 * Dieser Konstruktor initialisiert die Zeichenkette sowie die Bereiche.
	 * 
	 * @see Range
	 * @param source Zeichenkette.
	 * @param ranges Bereiche.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code ranges} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus gehen.
	 */
	public Script(final String source, final Collection<? extends Range> ranges) throws NullPointerException, IllegalArgumentException {
		this(source, ranges.toArray(new Range[ranges.size()]));
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Bereich zurück.
	 */
	@Override
	public Range get(final int index) throws IndexOutOfBoundsException {
		return this.ranges[index];
	}

	/**
	 * Diese Methode gibt die Zeichenkette zurück.
	 * 
	 * @return Zeichenkette.
	 */
	public String source() {
		return this.source;
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Range#type() Typen} der {@link #ranges() Bereiche} das Zeichenkette zurück.
	 * 
	 * @see Range#type()
	 * @see #ranges()
	 * @return Bereichstypen als Zeichenkette.
	 */
	public char[] types() {
		final Range[] ranges = this.ranges;
		final int length = ranges.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = ranges[i].type;
		}
		return types;
	}

	/**
	 * Diese Methode gibt eine Koppie der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Bereiche.
	 */
	public Range[] ranges() {
		return this.ranges.clone();
	}

	/**
	 * Diese Methode gibt die Anzahl der Bereiche zurück.
	 * 
	 * @see #get(int)
	 * @see #ranges()
	 * @see #iterator()
	 * @return Anzahl der Bereiche.
	 */
	public int length() {
		return this.ranges.length;
	}

	/**
	 * Diese Methode gibt diesen Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette}, der nicht in
	 * einem der {@link #ranges() Bereiche} enthalten ist.
	 * 
	 * @return normalisierter Quelltext.
	 */
	public Script normalize() {
		final List<Range> normalRanges = new ArrayList<Range>(this.ranges.length);
		final StringBuilder normalSource = new StringBuilder();
		final String source = this.source;
		int start = 0;
		for (final Range range: this.ranges) {
			final int length = range.length;
			normalSource.append(range.extract(source));
			normalRanges.add(new Range(range.type, start, length));
			start += length;
		}
		return new Script(normalSource.toString(), normalRanges);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Range> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.source) ^ Objects.hash((Object[])this.ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Script)) return false;
		final Script data = (Script)object;
		return Objects.equals(this.source, data.source) && Objects.equals(this.ranges, data.ranges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.source();
	}

}
