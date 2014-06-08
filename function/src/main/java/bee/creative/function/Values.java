package bee.creative.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.function.Functions.ArrayFunction;
import bee.creative.function.Functions.ParamFunction;
import bee.creative.function.Functions.ValueFunction;
import bee.creative.function.Scopes.CompositeScope;
import bee.creative.function.Script.Range;
import bee.creative.function.Types.ArrayType;
import bee.creative.function.Types.BooleanType;
import bee.creative.function.Types.FunctionType;
import bee.creative.function.Types.NullType;
import bee.creative.function.Types.NumberType;
import bee.creative.function.Types.ObjectType;
import bee.creative.function.Types.StringType;
import bee.creative.util.Converter;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

/**
 * Diese Klasse implementiert {@link Value}s für {@code null}-, {@link Value}{@code []}-, {@link Object}-, {@link Function}-, {@link String}-, {@link Number}-
 * und {@link Boolean}-Nutzdaten.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Values {

	/**
	 * Diese Klasse implementiert den Parser für {@link Values#parseScript(String)}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	private static final class ValueParser extends Parser {

		/**
		 * Dieses Feld speichert die Startposition des aktuell geparsten Wertbereichs oder {@code -1}.
		 */
		private int value;

		/**
		 * Dieses Feld speichert die bisher ermittelten Bereiche.
		 */
		private final List<Range> ranges;

		/**
		 * Dieser Konstruktor initialisiert die Eingabe.
		 * 
		 * @param source Eingabe.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ValueParser(final String source) throws NullPointerException {
			super(source);
			this.value = -1;
			this.ranges = new ArrayList<Range>((length() / 10) + 5);
		}

		/**
		 * Diese Methode fügt eine neue {@link Range} mit den gegebenen Parametern hinzu, die bei {@link #index()} endet.
		 * 
		 * @param type Typ.
		 * @param start Startposition.
		 */
		private void range(final int type, final int start) {
			this.ranges.add(new Range((char)type, start, this.index() - start));
		}

		/**
		 * Diese Methode beendet das einlesen des Wertbereichs.
		 */
		private void closeValue() {
			final int start = this.value;
			if(start < 0) return;
			this.value = -1;
			if(this.index() <= start) return;
			this.range('.', start);
		}

		/**
		 * Diese Methode beginnt das parsen eines Wertbereichs, welches mit {@link #closeValue()} beendet werden muss.
		 */
		private void parseValue() {
			if(this.value >= 0) return;
			this.value = this.index();
		}

		/**
		 * Diese Methode parst einen Bereich, der mit dem gegebenen Zeichen beginnt, endet, in dem das Zeichen durch Verdopplung maskiert werden kann und welcher
		 * das Zeichen als Typ verwendet.
		 * 
		 * @param type Zeichen als Bereichstyp.
		 */
		private void parseMask(final int type) {
			final int start = this.index();
			for(int symbol = this.skip(); symbol >= 0; symbol = this.skip()){
				if(symbol == type){
					if(this.skip() != type){
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
		private void parseName() {
			final int start = this.index();
			for(int symbol = this.skip(); symbol >= 0; symbol = this.skip()){
				if(symbol == '>'){
					if(this.skip() != '>'){
						this.range('!', start);
						return;
					}
				}else if(symbol == '<'){
					if(this.skip() != '<'){
						break;
					}
				}
			}
			this.range('?', start);
		}

		/**
		 * Diese Methode überspringt alle Zeichen, die kleiner oder gleich dem eerzeichen sind.
		 */
		private void parseSpace() {
			final int start = this.index();
			for(int symbol = this.skip(); (symbol >= 0) && (symbol <= ' '); symbol = this.skip()){}
			this.range('_', start);
		}

		/**
		 * Diese Methode erzeugt zum gegebenen Zeichen einen Bereich der Länge 1 und navigiert zum nächsten Zeichen.
		 * 
		 * @param type Zeichen als Bereichstyp.
		 */
		private void parseSymbol(final int type) {
			final int start = this.index();
			this.skip();
			this.range(type, start);
		}

		/**
		 * Diese Methode parst die Eingabe und gibt die Liste der ermittelten Bereiche zurück.
		 * 
		 * @return Bereiche.
		 */
		public List<Range> parse() {
			for(int symbol; true;){
				switch(symbol = this.symbol()){
					case -1:{
						this.closeValue();
						return this.ranges;
					}
					case '\'':
					case '\"':
					case '/':{
						this.closeValue();
						this.parseMask(symbol);
						break;
					}
					case '<':{
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
					case '}':{
						this.closeValue();
						this.parseSymbol(symbol);
						break;
					}
					default:{
						if(symbol <= ' '){
							this.closeValue();
							this.parseSpace();
						}else{
							this.parseValue();
							this.skip();
						}
					}
				}
			}
		}

	}

	/**
	 * Diese Klasse implementiert den Kompiler für {@link Values#compileValue(Script, ScriptCompiler, String...)} bzw.
	 * {@link Values#compileFunction(Script, ScriptCompiler, String...)}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	private static final class ValueCompiler {

		/**
		 * Dieses Feld speichert den aktuellen Bereich.
		 */
		private Range range;

		/**
		 * Dieses Feld speichert den Quelltext.
		 */
		private final Script script;

		/**
		 * Dieses Feld speichert die Parameternamen.
		 */
		private final LinkedList<String> params;

		/**
		 * Dieses Feld speichert den {@link Iterator} über die Bereiche von {@link #script}.
		 */
		private final Iterator<Range> iterator;

		/**
		 * Dieses Feld speichert die Kompilationsmethoden.
		 */
		private final ScriptCompiler compiler;

		/**
		 * Dieser Konstruktor initialisiert Quelltext, Kompilationsmethoden und Parameternamen.
		 * 
		 * @param script Quelltext.
		 * @param compiler Kompilationsmethoden.
		 * @param params Parameternamen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist oder enthält.
		 */
		public ValueCompiler(final Script script, final ScriptCompiler compiler, final String... params) throws NullPointerException {
			this.script = script;
			this.iterator = script.iterator();
			if(compiler == null) throw new NullPointerException();
			this.compiler = compiler;
			this.params = new LinkedList<String>(Arrays.asList(params));
			if(this.params.contains(null)) throw new NullPointerException();
			this.skip();
		}

		/**
		 * Diese Methode überspringt den aktuellen Bereich und gibt den nächsten oder {@link Range#NULL} zurück. Der {@link #range aktuelle Bereich} wird durch
		 * diese Methode verändert. Am Ende der
		 * 
		 * @return aktueller Bereich oder {@link Range#NULL}.
		 */
		private Range skip() {
			if(!this.iterator.hasNext()) return this.range = Range.NULL;
			return this.range = this.iterator.next();
		}

		/**
		 * Diese Methode überspringt bedeutungslose Bereiche (Typen {@code '_'} und {@code '/'}) und gibt den ersten bedeutsamen Bereich oder {@link Range#NULL}
		 * zurück. Der {@link #range aktuelle Bereich} wird durch diese Methode verändert.
		 * 
		 * @see #skip()
		 * @return aktueller Bereich oder {@link Range#NULL}.
		 */
		private Range skipSpace() {
			for(int type = this.range.type; (type == '_') || (type == '/'); type = this.skip().type){}
			return this.range;
		}

		/**
		 * Diese Methode interpretiert die gegebene Zeichenkette als positive Zahl und gibt diese oder {@code -1} zurück.
		 * 
		 * @param string Zeichenkette.
		 * @return Zahl.
		 */
		private int compileIndex(final String string) {
			if(string.isEmpty()) return -1;
			final char symbol = string.charAt(0);
			if((symbol < '0') || (symbol > '9')) return -1;
			try{
				return Integer.parseInt(string);
			}catch(final NumberFormatException e){
				return -1;
			}
		}

		/**
		 * Diese Methode kompiliert den beim aktuellen Bereich beginnende Wert und gibt diesen zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		private Value compileValue() throws ScriptCompilerException {
			switch(this.skipSpace().type){
				case 0:
				case '$':
				case ';':
				case ':':
				case '(':
				case ')':
				case ']':
				case '}':
					throw new ScriptCompilerException(this.script, this.range);
				case '[':
					return this.compileArray();
				case '{':
					return FunctionValue.valueOf(this.compileScope());
				default:{
					final Value value = this.compiler.valueOf(this.script, this.range);
					if(value == null) throw new ScriptCompilerException(this.script, this.range);
					this.skip();
					return value;
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in einen {@link ArrayValue} und gibt diesen zurück.
		 * 
		 * @return Wertliste als {@link ArrayValue}.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		private ArrayValue compileArray() throws ScriptCompilerException {
			this.skip();
			if(this.skipSpace().type == ']'){
				this.skip();
				return ArrayType.NULL_VALUE;
			}
			for(final LinkedList<Value> array = new LinkedList<Value>(); true;){
				final Value value;
				switch(this.range.type){
					case 0:
					case ';':
					case '$':
					case '(':
					case ')':
					case '}':
						throw new ScriptCompilerException(this.script, this.range);
					case '[':
						value = this.compileArray();
						break;
					case '{':
						value = FunctionValue.valueOf(this.compileScope());
						break;
					default:
						value = NullValue.valueOf(this.compiler.valueOf(this.script, this.skipSpace()));
						break;
				}
				array.add(value);
				switch(this.skipSpace().type){
					case ';':
						this.skip();
						break;
					case ']':
						this.skip();
						return ArrayValue.valueOf(Array.valueOf(array));
					default:
						throw new ScriptCompilerException(this.script, this.range);
				}
			}
		}

		/**
		 * Diese Methode kompiliert den aktuellen Bereich zu einen Parameternamen und gibt diesen oder {@code null} zurück.
		 * 
		 * @return Parametername oder {@code null}.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		private String compileParam() throws ScriptCompilerException {
			switch(this.skipSpace().type){
				case 0:
				case '\'':
				case '\"':
				case '$':
				case '(':
				case '[':
				case ']':
				case '{':
					throw new ScriptCompilerException(this.script, this.range);
				case ':':
				case ';':
				case ')':
				case '}':
					return null;
			}
			final String name = this.compiler.paramOf(this.script, this.range);
			if(name.isEmpty()) throw new IllegalArgumentException();
			this.skip();
			return name;
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende, parametrisierte Funktion in einen {@link FunctionValue} und gibt diesen zurück.
		 * 
		 * @return Funktion als {@link FunctionValue}.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		private Function compileScope() throws ScriptCompilerException {
			this.skip();
			int count = 0;
			while(true){
				if(this.skipSpace().type == 0) throw new ScriptCompilerException(this.script, this.range);
				final String name = this.compileParam();
				if(name != null){
					if(this.compileIndex(name) >= 0) throw new IllegalArgumentException("Illegal param name " + Objects.toString(name) + ".");
					this.params.add(count++, name);
				}
				switch(this.skipSpace().type){
					case ';':
						if(name == null) throw new ScriptCompilerException(this.script, this.range);
						this.skip();
						break;
					case ':':{
						this.skip();
						final Function function = this.compileFunction();
						if(this.skipSpace().type != '}') throw new ScriptCompilerException(this.script, this.range);
						this.skip();
						this.params.subList(0, count).clear();
						return function;
					}
					default:
						throw new ScriptCompilerException(this.script, this.range);
				}
			}
		}

		/**
		 * Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diesen zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		private Function compileFunction() throws ScriptCompilerException {
			Function function;
			boolean chained = false;
			switch(this.skipSpace().type){
				case '$':{
					this.skip();
					final String name = this.compileParam();
					if(name == null) return Functions.ArrayFunction.INSTANCE;
					final int index = this.compileIndex(name);
					final int index2 = (index < 0) ? this.params.indexOf(name) : (index - 1);
					if(index2 < 0) throw new IllegalArgumentException("Unknown parameter name " + Objects.toString(name) + ".");
					return Functions.ParamFunction.valueOf(index2);
				}
				case '{':{
					function = this.compileScope();
					if(this.skipSpace().type != '(') return ValueFunction.valueOf(FunctionValue.valueOf(function));
					break;
				}
				default:{
					final Value value = this.compileValue();
					if(!value.type().is(FunctionValue.TYPE)) return ValueFunction.valueOf(value);
					function = FunctionValue.TYPE.valueOf(value).data();
					if(this.skipSpace().type != '(') return function;
				}
			}
			do{
				this.skip(); // '('
				this.skipSpace();
				final LinkedList<Function> functions = new LinkedList<Function>();
				while(true){
					if(this.range.type == ')'){
						this.skip();
						function = Functions.CompositeFunction.valueOf(function, chained, functions.toArray(new Function[functions.size()]));
						break;
					}
					functions.add(this.compileFunction());
					switch(this.skipSpace().type){
						default:
							throw new ScriptCompilerException(this.script, this.range);
						case ';':
							this.skip();
						case ')':
					}
				}
				chained = true;
			}while(this.skipSpace().type == '(');
			return function;
		}

		/**
		 * Diese Methode kompiliert den Quelltext zu einem Wert und gibt diesen zurück.
		 * 
		 * @return Wert.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		public Value compileToValue() throws ScriptCompilerException {
			final Value value = this.compileValue();
			if(this.skipSpace().type == 0) return value;
			throw new ScriptCompilerException(this.script, this.range);
		}

		/**
		 * Diese Methode kompiliert den Quelltext zu einer Funktion und gibt diese zurück.
		 * 
		 * @return Funktion.
		 * @throws ScriptCompilerException Wenn {@link #script} ungültig ist.
		 */
		public Function compileToFunction() throws ScriptCompilerException {
			final Function function = this.compileFunction();
			if(this.skipSpace().type == 0) return function;
			throw new ScriptCompilerException(this.script, this.range);
		}

	}

	/**
	 * Diese Schnittstelle definiert Kompilationsmethoden, die in den Methoden {@link Values#compileValue(Script, ScriptCompiler, String...)} und
	 * {@link Values#compileFunction(Script, ScriptCompiler, String...)} zur Übersetzung von Quelltexten in Werte, Funktionen bzw. Parameternamen genutzt werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ScriptCompiler {

		/**
		 * Diese Methode gibt den im gegebenen Bereich des gegebenen Quelltexts angegebenen Wert zurück. Funktionen müssen als {@link FunctionValue} geliefert
		 * werden.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich.
		 * @return Wert oder Funktionen als {@link FunctionValue}.
		 * @throws ScriptCompilerException Wenn der Bereich keinen gültigen Funktionsnamen oder Wert enthält.
		 */
		public Value valueOf(Script script, final Range range) throws ScriptCompilerException;

		/**
		 * Diese Methode gibt den im gegebenen Bereich des gegebenen Quelltexts angegebenen Parameternamen zurück.
		 * 
		 * @param script Quelltext.
		 * @param range Bereich.
		 * @return Parametername.
		 * @throws ScriptCompilerException Wenn der Bereich keinen gültigen Parameternamen enthält (z.B. bei Verwechslungsgefahr mit anderen Datentypen).
		 */
		public String paramOf(Script script, final Range range) throws ScriptCompilerException;

	}

	/**
	 * Diese Klasse implementiert die {@link IllegalArgumentException}, die bei Syntaxfehlern von einem {@link ScriptCompiler} oder den Methoden
	 * {@link Values#compileValue(Script, ScriptCompiler, String...)} und {@link Values#compileValue(Script, ScriptCompiler, String...)} ausgelöst wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class ScriptCompilerException extends IllegalArgumentException {

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
		public ScriptCompilerException(final Script script, final Range range) throws NullPointerException {
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
		public ScriptCompilerException(final Script script, final Range range, Throwable cause) throws NullPointerException {
			super("Unerwartete Zeichenkette '" + range.extract(script.source) + "' an Position " + range.start + ".", cause);
			this.script = script;
			this.range = range;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Wert, dem zur Vollständigkeit nur noch die {@link #data() Nutzdaten} und der {@link #type() Datentyp} fehlen.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractValue implements Value {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> GValue valueTo(final Type<GValue> type) throws NullPointerException, IllegalArgumentException {
			return type.valueOf(this);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Objects#hash(Object)
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.data());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see Objects#equals(Object, Object)
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			final Value data = (Value)object;
			return Objects.equals(this.type(), data.type()) && Objects.equals(this.data(), data.data());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.data());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Wert mit {@link #data() Nutzdaten}, dem zur Vollständigkeit nur noch der {@link #type() Datentyp} fehlt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten.
	 */
	public static abstract class AbstractValue2<GData> extends AbstractValue {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		final GData data;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public AbstractValue2(final GData data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData data() {
			return this.data;
		}

	}

	/**
	 * Diese Klasse implementiert den Ergebniswert einer Funktion mit {@code call-by-reference}-Semantik, welcher eine gegebene Funktion erst dann mit einem
	 * gegebenen Ausführungskontext einmalig auswertet, wenn {@link #type() Datentyp} oder {@link #data() Nutzdaten} gelesen werden. Der von der Funktion
	 * berechnete Ergebniswert wird zur Wiederverwendung zwischengespeichert. Nach der einmaligen Auswertung der Funktion werden die Verweise auf
	 * Ausführungskontext und Funktion aufgelöst.
	 * 
	 * @see CompositeScope
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class LazyValue implements Value {

		/**
		 * Diese Methode konvertiert den gegebenen Funktionsaufruf in einen {@link LazyValue} und gibt diesen zurück.
		 * 
		 * @param scope Ausführungskontext.
		 * @param function auszuwertende Funktion.
		 * @return {@link LazyValue}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public static LazyValue valueOf(final Scope scope, final Function function) throws NullPointerException {
			return new LazyValue(scope, function);
		}

		/**
		 * Dieses Feld speichert das von der Funktion berechnete Ergebnis oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Value value;

		/**
		 * Dieses Feld speichert den Ausführungskontext zum Aufruf der Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Scope scope;

		/**
		 * Dieses Feld speichert die Funktion oder {@code null}.
		 * 
		 * @see Function#execute(Scope)
		 */
		Function function;

		/**
		 * Dieser Konstruktor initialisiert Ausführungskontext und Funktion.
		 * 
		 * @param scope Ausführungskontext.
		 * @param function Funktion.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public LazyValue(final Scope scope, final Function function) throws NullPointerException {
			if((scope == null) || (function == null)) throw new NullPointerException();
			this.scope = scope;
			this.function = function;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Type<?> type() {
			return this.value().type();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return this.value().data();
		}

		/**
		 * Diese Methode gibt den Ergebniswert der Ausführung der Funktion mit dem Ausführungskontext zurück.
		 * 
		 * @see Function#execute(Scope)
		 * @return Ergebniswert.
		 * @throws NullPointerException Wenn der berechnete Ergebniswert {@code null} ist.
		 */
		public Value value() throws NullPointerException {
			Value value = this.value;
			if(value != null) return value;
			value = this.function.execute(this.scope);
			if(value == null) throw new NullPointerException();
			this.value = value;
			this.scope = null;
			this.function = null;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GValue> GValue valueTo(final Type<GValue> type) throws NullPointerException, IllegalArgumentException {
			return this.value().valueTo(type);
		}

		/**
		 * Diese Methode gibt den Ausführungskontext oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt den Ausführungskontext auf {@code null}.
		 * 
		 * @return Ausführungskontext oder {@code null}.
		 */
		public Scope scope() {
			return this.scope;
		}

		/**
		 * Diese Methode gibt die Funktion oder {@code null} zurück. Der erste Aufruf von {@link #value()} setzt die Funktion auf {@code null}.
		 * 
		 * @return Funktion oder {@code null}.
		 */
		public Function function() {
			return this.function;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.value().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Value)) return false;
			return this.value().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.value != null) return Objects.toStringCallFormat(true, true, this, new Object[]{"value", this.value});
			return Objects.toStringCallFormat(true, true, this, new Object[]{"scope", this.scope, "function", this.function});
		}

	}

	/**
	 * Diese Klasse implementiert den Wert zu {@code null}.
	 * 
	 * @see NullType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullValue extends AbstractValue {

		/**
		 * Dieses Feld speichert den {@link NullType}.
		 */
		public static final NullType TYPE = new NullType();

		/**
		 * Dieses Feld speichert den {@link NullValue}.
		 */
		public static final NullValue INSTANCE = new NullValue();

		/**
		 * Diese Methode gibt den gegebenen Wert oder {@link NullValue#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullValue#INSTANCE} zurück
		 * gegeben.
		 * 
		 * @param value Wert oder {@code null}.
		 * @return Wert oder {@link NullValue#INSTANCE}.
		 */
		public static Value valueOf(final Value value) {
			if(value == null) return NullValue.INSTANCE;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object data() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NullType type() {
			return NullValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit {@link Value}{@code []} als Nutzdaten.
	 * 
	 * @see ArrayType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ArrayValue extends AbstractValue2<Array> {

		/**
		 * Dieses Feld speichert den {@link ArrayType}.
		 */
		public static final ArrayType TYPE = new ArrayType();

		/**
		 * Diese Methode konvertiert die gegebene Wertliste in einen {@link Value} und gibt diesen zurück.
		 * 
		 * @param data Wertliste.
		 * @return {@link ArrayValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ArrayValue valueOf(final Array data) throws NullPointerException {
			return new ArrayValue(data);
		}

		/**
		 * Dieser Konstruktor initialisiert den Datensatz.
		 * 
		 * @param data Datensatz.
		 * @throws NullPointerException Wenn der Datensatz {@code null} ist oder enthält.
		 */
		public ArrayValue(final Array data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayType type() {
			return ArrayValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert mit beliebigen Objekten als Nutzdaten.
	 * 
	 * @see ObjectType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ObjectValue extends AbstractValue2<Object> {

		/**
		 * Dieses Feld speichert den {@link ObjectType}.
		 */
		public static final ObjectType TYPE = new ObjectType();

		/**
		 * Diese Methode konvertiert das gegebene Object in einen {@link ObjectValue} und gibt diesen zurück.
		 * 
		 * @param data Object.
		 * @return {@link ObjectValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static ObjectValue valueOf(final Object data) throws NullPointerException {
			return new ObjectValue(data);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ObjectValue(final Object data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectType type() {
			return ObjectValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für Funktionen als Nutzdaten.
	 * 
	 * @see FunctionType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FunctionValue extends AbstractValue2<Function> {

		/**
		 * Dieses Feld speichert den {@link FunctionType}.
		 */
		public static final FunctionType TYPE = new FunctionType();

		/**
		 * Diese Methode konvertiert die gegebene Funktion in einen {@link FunctionValue} und gibt diesen zurück.
		 * 
		 * @param data Funktion.
		 * @return {@link FunctionValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static FunctionValue valueOf(final Function data) throws NullPointerException {
			return new FunctionValue(data);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public FunctionValue(final Function data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FunctionType type() {
			return FunctionValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link String} als Nutzdaten.
	 * 
	 * @see StringType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class StringValue extends AbstractValue2<String> {

		/**
		 * Dieses Feld speichert den {@link StringType}.
		 */
		public static final StringType TYPE = new StringType();

		/**
		 * Diese Methode konvertiert den gegebenen Test in einen {@link StringValue} und gibt diesen zurück.
		 * 
		 * @param data Text.
		 * @return {@link StringValue}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public static StringValue valueOf(final String data) throws NullPointerException {
			return new StringValue(data);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public StringValue(final String data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public StringType type() {
			return StringValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Number} als Nutzdaten.
	 * 
	 * @see NumberType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NumberValue extends AbstractValue2<Number> {

		/**
		 * Dieses Feld speichert den {@link NumberType}.
		 */
		public static final NumberType TYPE = new NumberType();

		/**
		 * Diese Methode konvertiert den gegebenen Zahlenwert in einen {@link NumberValue} und gibt diesen zurück.
		 * 
		 * @param data Zahlenwert.
		 * @return {@link NumberValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static NumberValue valueOf(final Number data) throws NullPointerException {
			return new NumberValue(data);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public NumberValue(final Number data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NumberType type() {
			return NumberValue.TYPE;
		}

	}

	/**
	 * Diese Klasse implementiert den Wert für {@link Boolean} als Nutzdaten.
	 * 
	 * @see BooleanType
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BooleanValue extends AbstractValue2<Boolean> {

		/**
		 * Dieses Feld speichert den {@link BooleanType}.
		 */
		public static final BooleanType TYPE = new BooleanType();

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#TRUE}.
		 */
		public static final BooleanValue TRUE = new BooleanValue(Boolean.TRUE);

		/**
		 * Dieses Feld speichert den {@link BooleanValue} für {@link Boolean#FALSE}.
		 */
		public static final BooleanValue FALSE = new BooleanValue(Boolean.FALSE);

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 */
		public static BooleanValue valueOf(final boolean data) {
			return (data ? BooleanValue.TRUE : BooleanValue.FALSE);
		}

		/**
		 * Diese Methode konvertiert den gegebenen Wahrheitswert in einen {@link BooleanValue} und gibt diesen zurück.
		 * 
		 * @param data Wahrheitswert.
		 * @return {@link BooleanValue}.
		 * @throws NullPointerException wenn die Eingabe {@code null} ist.
		 */
		public static BooleanValue valueOf(final Boolean data) throws NullPointerException {
			return BooleanValue.valueOf(data.booleanValue());
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public BooleanValue(final Boolean data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BooleanType type() {
			return BooleanValue.TYPE;
		}

	}

	/**
	 * Dieses Feld speichert den {@link Converter} zur Anpassung von {@link Values#valueOf(Object)}.
	 */
	static Converter<? super Object, ? extends Value> converter;

	/**
	 * Diese Methode konvertiert das gegebene Objekt in einen {@link Value} und gibt diesen zurück. Abhängig vom Datentyp des gegebenen Objekts kann hierfür
	 * automatisch ein {@link ArrayValue}, {@link ObjectValue}, {@link FunctionValue}, {@link StringValue}, {@link NumberValue} oder {@link BooleanValue}
	 * verwendet.
	 * <ul>
	 * <li>Wenn das Objekt {@code null} ist, wird {@link NullValue#INSTANCE} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link Value} ist, wird dieser unverändert zurück gegeben.</li>
	 * <li>Wenn der via {@link #setConverter(Converter)} registrierte {@link Converter} sowie das Ergebnis seiner {@link Converter#convert(Object)
	 * Konvertierungsmethode} nicht {@code null} sind, wird dieses Ergebnis zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link String} ist, wird dieser als {@link StringValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt eine {@link Number} ist, wird dieser als {@link NumberValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein {@link Boolean} ist, wird dieser als {@link BooleanValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt eine {@link Function} ist, wird dieser als {@link FunctionValue} zurück gegeben.</li>
	 * <li>Wenn das Objekt ein Array ist, wird dieser als {@link ArrayValue} zurück gegeben.</li>
	 * <li>In allen anderen Fällen wird der Datensatz als {@link ObjectValue} zurück gegeben.</li>
	 * </ul>
	 * 
	 * @see Converter#convert(Object)
	 * @see StringValue#valueOf(String)
	 * @see NumberValue#valueOf(Number)
	 * @see BooleanValue#valueOf(Boolean)
	 * @see FunctionValue#valueOf(Function)
	 * @see ArrayValue#valueOf(Object)
	 * @see ObjectValue#valueOf(Object)
	 * @param data Datensatz oder {@code null}.
	 * @return {@link Value}.
	 */
	public static Value valueOf(final Object data) {
		if(data == null) return NullValue.INSTANCE;
		if(data instanceof Value) return (Value)data;
		final Converter<? super Object, ? extends Value> converter = Values.converter;
		if(converter != null){
			final Value value = converter.convert(data);
			if(value != null) return value;
		}
		if(data instanceof String) return StringValue.valueOf((String)data);
		if(data instanceof Number) return NumberValue.valueOf((Number)data);
		if(data instanceof Boolean) return BooleanValue.valueOf((Boolean)data);
		if(data instanceof Function) return FunctionValue.valueOf((Function)data);
		final Array array = Array.from(data);
		if(array != null) return ArrayValue.valueOf(array);
		return ObjectValue.valueOf(data);
	}

	/**
	 * Diese Methode parst die gegebene Zeichenkette in einen aufbereiteten Quelltext und gibt diesen zurück. Die Erzeugung von {@link Range Bereichen} erfolgt
	 * gemäß dieser Regeln:
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
	 * @see Script
	 * @see #compileValue(Script, ScriptCompiler, String...)
	 * @see #compileFunction(Script, ScriptCompiler, String...)
	 * @param source Zeichenkette.
	 * @return aufbereiteter Quelltext.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static Script parseScript(final String source) throws NullPointerException {
		return new Script(source, new ValueParser(source).parse());
	}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Kompilationsmethoden und Funktionsparameter in einen Wert und gibt diesen zurück.
	 * 
	 * @see #parseScript(String)
	 * @see #compileFunction(Script, ScriptCompiler, String...)
	 * @param script Quelltext.
	 * @param compiler Kompilationsmethoden.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierter Wert.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptCompilerException Wenn eine der Eingaben ungültig ist.
	 */
	public static Value compileValue(final Script script, final ScriptCompiler compiler, final String... params) throws NullPointerException,
		ScriptCompilerException {
		return new ValueCompiler(script, compiler, params).compileToValue();
	}

	/**
	 * Diese Methode kompiliert den gegebenen Quelltext im Kontext der gegebenen Kompilationsmethoden und Funktionsparameter in eine Funktion und gibt diese
	 * zurück. Die Bereichestypen des Quelltexts haben folgende Bedeutung:
	 * <ul>
	 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) dürfen an jeder Position vorkommen und werden ignoriert.</li>
	 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link Array}s an, dessen Elemente mit Bereichen vom Typ
	 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente unzulässig.</li>
	 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
	 * Bereichen vom Typ {@code ';'} separiert werden müssen.</li>
	 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
	 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
	 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über {@link ScriptCompiler#paramOf(Script, Range)} aufgelöst werden
	 * kann. Fpr Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu genau einem
	 * {@link FunctionValue} kompilieren müssen.</li>
	 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link ParamFunction} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines
	 * Parameters folgen ({@code $1} wird zu {@code ParamFunction.valueOf(0)}). Andernfalls steht der Bereich für die {@link ArrayFunction}.</li>
	 * <li>Alle restlichen Bereiche werden über {@link ScriptCompiler#valueOf(Script, Range)} in Werte überführt. Funktionen werden hierbei zu
	 * {@link FunctionValue}s.</li>
	 * </ul>
	 * 
	 * @see #parseScript(String)
	 * @see #compileValue(Script, ScriptCompiler, String...)
	 * @param script Quelltext.
	 * @param compiler Kompilationsmethoden.
	 * @param params Namen der Parameter, in deren Kontext eine Funktion kompiliert werden soll.
	 * @return kompilierte Funktion.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist, enthält oder liefert.
	 * @throws ScriptCompilerException Wenn eine der Eingaben ungültig ist.
	 */
	public static Function compileFunction(final Script script, final ScriptCompiler compiler, final String... params) throws NullPointerException,
		ScriptCompilerException {
		return new ValueCompiler(script, compiler, params).compileToFunction();
	}

	/**
	 * Diese Methode gibt den {@link Converter} zur Anpassung von {@link Values#valueOf(Object)} zurück.
	 * 
	 * @return {@link Converter} oder {@code null}.
	 */
	public static Converter<? super Object, ? extends Value> getConverter() {
		return Values.converter;
	}

	/**
	 * Diese Methode setzt den {@link Converter} zur Anpassung von {@link Values#valueOf(Object)}.
	 * 
	 * @param handler {@link Converter} oder {@code null}.
	 */
	public static void setConverter(final Converter<? super Object, ? extends Value> handler) {
		Values.converter = handler;
	}

}
