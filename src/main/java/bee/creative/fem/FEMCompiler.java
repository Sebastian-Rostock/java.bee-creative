package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Range;
import bee.creative.util.Objects;
import bee.creative.util.Parser;

/** Diese Klasse implementiert einen Kompiler, der {@link FEMScript aufbereitete Quelltexte} in {@link FEMValue Werte} sowie {@link FEMFunction Funktionen}
 * überführen und diese im Rahmen eines {@link FEMFormatter} auch formatieren kann.
 * <p>
 * Die Bereichestypen der Quelltexte haben folgende Bedeutung:
 * <ul>
 * <li>Bereiche mit den Typen {@code '_'} (Leerraum) und {@code '/'} (Kommentar) sind bedeutungslos, dürfen an jeder Position vorkommen und werden
 * ignoriert.</li>
 * <li>Bereiche mit den Typen {@code '['} und {@code ']'} zeigen den Beginn bzw. das Ende eines {@link FEMArray}s an, dessen Elemente mit Bereichen vom Typ
 * {@code ';'} separiert werden müssen. Funktionsaufrufe sind als Elemente nur dann zulässig, wenn das {@link FEMArray} als Funktion bzw. Parameterwert
 * kompiliert wird.</li>
 * <li>Bereiche mit den Typen {@code '('} und {@code ')'} zeigen den Beginn bzw. das Ende der Parameterliste eines Funktionsaufrufs an, deren Parameter mit
 * Bereichen vom Typ {@code ';'} separiert werden müssen und als Funktionen kompiliert werden.</li>
 * <li>Bereiche mit den Typen <code>'{'</code> und <code>'}'</code> zeigen den Beginn bzw. das Ende einer parametrisierten Funktion an. Die Parameterliste
 * besteht aus beliebig vielen Parameternamen, die mit Bereichen vom Typ {@code ';'} separiert werden müssen und welche mit einem Bereich vom Typ {@code ':'}
 * abgeschlossen werden muss. Ein Parametername muss durch einen Bereich gegeben sein, der über {@link FEMDomain#compileName(FEMCompiler)} aufgelöst werden
 * kann. Für Parameternamen gilt die Überschreibung der Sichtbarkeit analog zu Java. Nach der Parameterliste folgen dann die Bereiche, die zu genau einer
 * Funktion kompiliert werden.</li>
 * <li>Der Bereich vom Typ {@code '$'} zeigt eine {@link FEMParam} an, wenn danach ein Bereich mit dem Namen bzw. der 1-basierenden Nummer eines Parameters
 * folgen ({@code $1} wird zu {@code FEMParam.from(0)}). Andernfalls steht der Bereich für {@link FEMParam#VIEW}.</li>
 * <li>Alle restlichen Bereiche werden über {@link FEMDomain#compileFunction(FEMCompiler)} in Parameterfunktionen überführt.</li>
 * </ul>
 * <p>
 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
 *
 * @see #formatScript(FEMFormatter)
 * @see #compileValue()
 * @see #compileFunction()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMCompiler extends Parser {

	@SuppressWarnings ("javadoc")
	boolean active;

	/** Dieses Feld speichert die Kompilationsmethoden. */
	FEMDomain domain = FEMDomain.NORMAL;

	/** Dieses Feld speichert den Quelltext. */
	FEMScript script = FEMScript.EMPTY;

	/** Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter. */
	final Map<String, FEMProxy> proxies = Collections.synchronizedMap(new LinkedHashMap<String, FEMProxy>());

	/** Dieses Feld speichert die Parameternamen. */
	final List<String> params = Collections.synchronizedList(new LinkedList<String>());

	/** Dieses Feld speichert die Zulässigkeit von Wertlisten. */
	boolean arrayEnabled = true;

	/** Dieses Feld speichert die Zulässigkeit der Verkettung von Funktionen. */
	boolean concatEnabled = true;

	/** Dieses Feld speichert die Zulässigkeit der Bindung des Stapelrahmens. */
	boolean closureEnabled = true;

	/** Dieses Feld speichert den Formatierer. */
	FEMFormatter formatter;

	/** Dieser Konstruktor initialisiert einen neuen Kompiler, welcher {@link FEMDomain#NORMAL} nutzt und {@link FEMCompiler#useArrayEnabled(boolean) Wertlisten},
	 * {@link FEMCompiler#useClosureEnabled(boolean) Stapelrahmenbindung} sowie {@link FEMCompiler#useChainingEnabled(boolean) Funktionsverkettung} zulässt. */
	public FEMCompiler() {
	}

	{}

	/** Diese Methode markiert den Beginn der Verarbeitung und muss in Verbindung mit {@link #stop()} verwendet werden.
	 *
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	final synchronized void start() throws IllegalStateException {
		this.check();
		this.active = true;
		this.proxies.clear();
		this.reset();
	}

	@SuppressWarnings ("javadoc")
	synchronized final void stop() {
		this.active = false;
	}

	@SuppressWarnings ("javadoc")
	final void check() throws IllegalStateException {
		if (this.active) throw new IllegalStateException();
	}

	@SuppressWarnings ("javadoc")
	final IllegalArgumentException illegal(final Throwable cause, final String format, final Object... args) {
		final String hint = String.format(format, args);
		if (this.isParsed()) return new IllegalArgumentException("Unerwartetes Ende der Zeichenkette." + hint, cause);
		final String source = this.script().source();
		final int offset = this.range().start();
		final int length = source.length();
		int pos = source.lastIndexOf('\n', offset);
		int row = 1;
		final int col = offset - pos;
		while (pos >= 0) {
			pos = source.lastIndexOf('\n', pos - 1);
			row++;
		}
		return new IllegalArgumentException(
			String.format("Unerwartete Zeichenkette «%s» an Position %s:%s (%s%%) bei Textstelle «%s».%s", //
				this.section(), row, col, (100 * offset) / Math.max(length, 0), source.substring(Math.max(offset - 10, 0), Math.min(offset + 10, length)), hint),
			cause);
	}

	/** Diese Methode formatiert den aktuellen Quelltext als Sequenz von Werten und Stoppzeichen. */
	final void format() {
		final FEMFormatter formatter = this.formatter;
		while (true) {
			this.formatSequence(false);
			if (this.symbol() < 0) return;
			formatter.put(this.section()).putBreakSpace();
			this.skip();
		}
	}

	/** Diese Methode formatiert die aktuelle Wertliste. */
	final void formatArray() {
		final FEMFormatter formatter = this.formatter;
		formatter.put("[").putBreakInc();
		this.skip();
		this.formatSequence(false);
		if (this.symbol() == ']') {
			formatter.putBreakDec().put("]");
			this.skip();
		}
	}

	/** Diese Methode formatiert die aktuelle Parameterliste. */
	final void formatParam() {
		final FEMFormatter formatter = this.formatter;
		formatter.put("(").putBreakInc();
		this.skip();
		this.formatSequence(false);
		if (this.symbol() == ')') {
			formatter.putBreakDec().put(")");
			this.skip();
		}
	}

	/** Diese Methode formatiert die aktuelle parametrisierte Funktion. */
	final void formatFrame() {
		final FEMFormatter formatter = this.formatter;
		formatter.put("{");
		this.skip();
		this.formatSequence(true);
		if (this.symbol() == ':') {
			formatter.put(": ");
			this.skip();
			this.formatSequence(false);
		}
		if (this.symbol() == '}') {
			formatter.put("}");
			this.skip();
		}
	}

	/** Diese Methode formatiert die aktuelle Wertsequenz, die bei einer schließenden Klammer oder Doppelpunkt endet.
	 *
	 * @param space {@code true}, wenn hinter Kommentaren und Semikola ein Leerzeichen statt eines bedingten Umbruchs eingefügt werden soll. */
	final void formatSequence(final boolean space) {
		final FEMFormatter formatter = this.formatter;
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
					this.formatParam();
					break;
				}
				case '[': {
					this.formatArray();
					break;
				}
				case '{': {
					this.formatFrame();
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
	final int compileType() {
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

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Wertliste in eine {@link FEMValue} und gibt diesen zurück.
	 *
	 * @return Wertliste als {@link FEMValue}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMValue compileArrayAsValue() throws IllegalArgumentException {
		if (!this.arrayEnabled) throw this.illegal(null, " Wertlisten sind nicht zulässig.");
		final List<FEMValue> result = new ArrayList<>();
		this.skip();
		if (this.compileType() == ']') {
			this.skip();
			return FEMArray.EMPTY;
		}
		while (true) {
			final FEMValue value = this.compileParamAsValue();
			result.add(value);
			switch (this.compileType()) {
				case ';': {
					this.skip();
					this.compileType();
					break;
				}
				case ']': {
					this.skip();
					return FEMArray.from(result);
				}
				default: {
					throw this.illegal(null, " Zeichen «;» oder «]» erwartet.");
				}
			}
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
		if (!this.arrayEnabled) throw this.illegal(null, " Wertlisten sind nicht zulässig.");
		this.skip();
		if (this.compileType() == ']') {
			this.skip();
			return FEMArray.EMPTY;
		}
		final List<FEMFunction> list = new ArrayList<>();
		boolean value = true;
		while (true) {
			final FEMFunction item = this.compileParamAsFunction();
			list.add(item);
			value = value && (this.functionToValue(item) != null);
			switch (this.compileType()) {
				case ';': {
					this.skip();
					this.compileType();
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
	final FEMFunction compileParam() throws IllegalArgumentException {
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

	/** Diese Methode kompiliert denF beim aktuellen Bereich beginnende Wert und gibt diese zurück.
	 *
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMValue compileParamAsValue() throws IllegalArgumentException {
		switch (this.compileType()) {
			case -1:
			case '$':
			case ';':
			case ':':
			case '(':
			case ')':
			case ']':
			case '}': {
				throw this.illegal(null, " Wert erwartet.");
			}
			case '[': {
				return this.compileArrayAsValue();
			}
			case '{': {
				if (this.closureEnabled) throw this.illegal(null, " Ungebundene Funktion unzulässig.");
				final FEMFunction retult = this.compileFrame();
				return FEMHandler.from(retult);
			}
			default: {
				final FEMFunction param = this.compileParam();
				final FEMValue result = this.functionToValue(param);
				if (result == null) throw this.illegal(null, " Wert erwartet.");
				return result;
			}
		}
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Funktion und gibt diese zurück.
	 *
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMFunction compileParamAsFunction() throws IllegalArgumentException {
		FEMFunction result;
		boolean concat = false;
		switch (this.compileType()) {
			case -1:
			case ';':
			case ':':
			case '(':
			case ')':
			case ']':
			case '}': {
				throw this.illegal(null, " Wert oder Funktion erwartet.");
			}
			case '$': {
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
			case '{': {
				result = this.compileFrame();
				if (this.compileType() != '(') {
					if (this.closureEnabled) return result.toClosure();
					return result.toValue();
				}
				if (!this.concatEnabled) throw this.illegal(null, " Funktionsverkettungen ist nicht zulässsig.");
				break;
			}
			case '[': {
				return this.compileArrayAsFunction();
			}
			default: {
				result = this.compileParam();
				if (this.compileType() != '(') return result;
			}
		}
		do {
			if (concat && !this.concatEnabled) throw this.illegal(null, " Funktionsverkettungen ist nicht zulässsig.");
			this.skip(); // '('
			final List<FEMFunction> list = new ArrayList<>();
			while (true) {
				if (this.compileType() == ')') {
					this.skip();
					final FEMFunction[] params = list.toArray(new FEMFunction[list.size()]);
					result = concat ? result.concat(params) : result.compose(params);
					break;
				}
				final FEMFunction item = this.compileParamAsFunction();
				list.add(item);
				switch (this.compileType()) {
					default:
						throw this.illegal(null, " Zeichen «;» oder «)» erwartet.");
					case ';':
						this.skip();
					case ')':
				}
			}
			concat = true;
		} while (this.compileType() == '(');
		return result;
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich beginnende Parameterfunktion und gibt diese zurück.
	 *
	 * @return Parameterfunktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMProxy compileProxy() throws IllegalArgumentException {
		final String name = this.compileName();
		if ((name == null) || (this.compileIndex(name) >= 0)) throw this.illegal(null, " Funktionsname erwartet.");
		final FEMProxy result = this.proxy(name);
		if (this.compileType() != '{') throw this.illegal(null, " Parametrisierter Funktionsaufruf erwartet.");
		result.set(this.compileFrame());
		return result;
	}

	/** Diese Methode kompiliert den aktuellen, bedeutsamen Bereich zu einen Funktionsnamen, Parameternamen oder Parameterindex und gibt diesen zurück.<br>
	 * Der Rückgabewert ist {@code null}, wenn der Bereich vom Typ {@code ':'}, {@code ';'}, {@code ')'}, <code>'}'</code>, {@code ']'} oder {@code 0} ist.
	 *
	 * @return Funktions- oder Parametername oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final String compileName() throws IllegalArgumentException {
		try {
			switch (this.compileType()) {
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
			final String result = this.domain.compileName(this);
			if (result.isEmpty()) throw new IllegalArgumentException();
			this.skip();
			return result;
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final RuntimeException cause) {
			throw this.illegal(cause, " Funktionsname, Parametername oder Parameterindex erwartet.");
		}
	}

	/** Diese Methode kompiliert die beim aktuellen Bereich (<code>'{'</code>) beginnende, parametrisierte Funktion und gibt diese zurück.
	 *
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist. */
	final FEMFunction compileFrame() throws IllegalArgumentException {
		this.skip();
		int count = 0;
		while (true) {
			if (this.compileType() < 0) throw this.illegal(null, "");
			final String name = this.compileName();
			if (name != null) {
				if (this.compileIndex(name) >= 0) throw this.illegal(null, " Parametername erwartet.");
				this.params.add(count++, name);
			}
			switch (this.compileType()) {
				case ';': {
					if (name == null) throw this.illegal(null, " Parametername oder Zeichen «:» erwartet.");
					this.skip();
					break;
				}
				case ':': {
					this.skip();
					final FEMFunction result = this.compileParamAsFunction();
					if (this.compileType() != '}') throw this.illegal(null, " Zeichen «}» erwartet.");
					this.skip();
					this.params.subList(0, count).clear();
					return result;
				}
				default: {
					throw this.illegal(null, "");
				}
			}
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

	/** Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
	 *
	 * @param name Name des Platzhalters.
	 * @return Platzhalterfunktion.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public final FEMProxy proxy(final String name) throws NullPointerException {
		synchronized (this.proxies) {
			FEMProxy result = this.proxies.get(name);
			if (result != null) return result;
			this.proxies.put(name, result = new FEMProxy(name));
			return result;
		}
	}

	/** Diese Methode gibt den aktuellen Bereich zurück.
	 *
	 * @return aktueller Bereich. */
	public final Range range() {
		return this.isParsed() ? Range.EMPTY : this.script.get(this.index());
	}

	/** Diese Methode gibt den zu kompilierenden Quelltext zurück.
	 *
	 * @return Quelltext. */
	public final FEMScript script() {
		return this.script;
	}

	/** Diese Methode gibt die genutzten Kompilationsmethoden zurück.
	 *
	 * @return Kompilationsmethoden. */
	public final FEMDomain domain() {
		return this.domain;
	}

	/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.<br>
	 * Die gelieferte Abbildung wird vor jeder Kompilation geleert.
	 *
	 * @return Abbildung von Namen auf Platzhalter. */
	public final Map<String, FEMProxy> proxies() {
		return this.proxies;
	}

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public final List<String> params() {
		return Collections.unmodifiableList(this.params);
	}

	/** Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
	 *
	 * @see Range#extract(String)
	 * @return Aktuelle Zeichenkette. */
	public final String section() {
		return this.range().extract(this.script.source());
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn Wertlisten zulässig sind (z.B. {@code [1;2]}).
	 *
	 * @see #compileFunction()
	 * @return Zulässigkeit von Wertlisten. */
	public final boolean isArrayEnabled() {
		return this.arrayEnabled;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Verkettung von Funktionen zulässig ist, d.h. ob die Funktion, die von einem Funktionsaufruf
	 * geliefert wird, direkt wieder aufgerufen werden darf (z.B. {@code FUN(1)(2)}).
	 *
	 * @see #compileFunction()
	 * @see FEMFunction#concat(FEMFunction...)
	 * @return Zulässigkeit der Verkettung von Funktionen. */
	public final boolean isConcatEnabled() {
		return this.concatEnabled;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn Funktionen als Parameter nicht über {@link FEMFunction#toValue()} sondern über
	 * {@link FEMFunction#toClosure()} kompiliert werden.
	 *
	 * @see #compileFunction()
	 * @return Zulässigkeit der Bindung des Stapelrahmens. */
	public final boolean isClosureEnabled() {
		return this.closureEnabled;
	}

	/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
	 *
	 * @param value Quelltext.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useScript(final FEMScript value) throws NullPointerException, IllegalStateException {
		this.check();
		this.source(value.types());
		this.script = value;
		return this;
	}

	/** Diese Methode setzt die zu nutzenden Kompilationsmethoden und gibt {@code this} zurück.
	 *
	 * @param value Kompilationsmethoden.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useDomain(final FEMDomain value) throws NullPointerException, IllegalStateException {
		this.check();
		this.domain = Objects.assertNotNull(value);
		return this;
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMCompiler useParams(final String... value) throws NullPointerException, IllegalStateException {
		return this.useParams(Arrays.asList(value));
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useParams(final List<String> value) throws NullPointerException, IllegalStateException {
		if (value.contains(null)) throw new NullPointerException("value.contains(null)");
		this.check();
		this.params.clear();
		this.params.addAll(value);
		return this;
	}

	/** Diese Methode setzt die Zulässigkeit von Wertlisten.
	 *
	 * @see #isArrayEnabled()
	 * @param value Zulässigkeit von Wertlisten.
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useArrayEnabled(final boolean value) throws IllegalStateException {
		this.check();
		this.arrayEnabled = value;
		return this;
	}

	/** Diese Methode setzt die Zulässigkeit der Verkettung von Funktionen.
	 *
	 * @see #isConcatEnabled()
	 * @param value Zulässigkeit der Verkettung von Funktionen.
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useChainingEnabled(final boolean value) throws IllegalStateException {
		this.check();
		this.concatEnabled = value;
		return this;
	}

	/** Diese Methode setzt die Zulässigkeit der Bindung des Stapelrahmens.
	 *
	 * @see #isClosureEnabled()
	 * @param value Zulässigkeit der Bindung des Stapelrahmens.
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public synchronized final FEMCompiler useClosureEnabled(final boolean value) throws IllegalStateException {
		this.check();
		this.closureEnabled = value;
		return this;
	}

	/** Diese Methode formatiert den Quelltext im Rahmen des gegebenen Formatierers.
	 *
	 * @param target Formatierer.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final void formatScript(final FEMFormatter target) throws NullPointerException, IllegalStateException {
		this.start();
		if (target == null) throw new NullPointerException("target = null");
		this.formatter = target;
		try {
			this.format();
		} finally {
			this.formatter = null;
			this.stop();
		}
	}

	/** Diese Methode kompiliert den Quelltext in einen Wert und gibt diesen zurück.<br>
	 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
	 *
	 * @return Wert oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMValue compileValue() throws IllegalArgumentException, IllegalStateException {
		this.start();
		try {
			if (this.compileType() < 0) return null;
			final FEMValue result = this.compileParamAsValue();
			if (this.compileType() < 0) return result;
			throw this.illegal(null, " Keine weiteren Definitionen erwartet.");
		} finally {
			this.stop();
		}
	}

	/** Diese Methode kompiliert den Quelltext in eine Liste von Werten und gibt diese zurück.<br>
	 * Die Werte müssen durch Bereiche vom Typ {@code ';'} separiert sein. Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Wertliste
	 * geliefert.
	 *
	 * @return Werte.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMValue[] compileValues() throws IllegalArgumentException, IllegalStateException {
		this.start();
		try {
			if (this.compileType() < 0) return new FEMValue[0];
			final List<FEMValue> result = new ArrayList<FEMValue>();
			while (true) {
				result.add(this.compileParamAsValue());
				switch (this.compileType()) {
					case -1: {
						return result.toArray(new FEMValue[result.size()]);
					}
					case ';': {
						this.skip();
					}
				}
			}
		} finally {
			this.stop();
		}
	}

	/** Diese Methode kompiliert den Quelltext in eine Liste von Parameterfunktion und gibt diese zurück.<br>
	 * Die Parameterfunktion müssen durch Bereiche vom Typ {@code ';'} separiert sein. Eine Parameterfunktion beginnt mit einem
	 * {@link FEMDomain#compileName(FEMCompiler) Namen} und endet dann mit einer in geschweifte Klammern eingeschlossenen parametrisierten Funktion. Wenn der
	 * Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert. Nach dem Aufruf dieser Methode ist Abbildung {@link #proxies()}
	 * entsprechend bestückt.
	 *
	 * @return Funktionen.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMProxy[] compileProxies() throws IllegalArgumentException, IllegalStateException {
		this.start();
		try {
			final List<FEMProxy> result = new ArrayList<FEMProxy>();
			if (this.compileType() < 0) return new FEMProxy[0];
			while (true) {
				result.add(this.compileProxy());
				switch (this.compileType()) {
					case -1: {
						return result.toArray(new FEMProxy[result.size()]);
					}
					case ';': {
						this.skip();
					}
				}
			}
		} finally {
			this.stop();
		}
	}

	/** Diese Methode kompiliert den Quelltext in eine Funktion und gibt diese zurück.<br>
	 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird {@code null} geliefert.
	 *
	 * @return Funktion oder {@code null}.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMFunction compileFunction() throws IllegalArgumentException, IllegalStateException {
		this.start();
		try {
			if (this.compileType() < 0) return null;
			final FEMFunction result = this.compileParamAsFunction();
			if (this.compileType() < 0) return result;
			throw this.illegal(null, " Keine weiteren Definitionen erwartet.");
		} finally {
			this.stop();
		}
	}

	/** Diese Methode kompiliert den Quelltext in eine Liste von Funktionen und gibt diese zurück. Die Funktionen müssen durch Bereiche vom Typ {@code ';'}
	 * separiert sein.<br>
	 * Wenn der Quelltext nur Bedeutungslose Bereiche enthält, wird eine leere Funktionsliste geliefert.
	 *
	 * @return Funktionen.
	 * @throws IllegalArgumentException Wenn der Quelltext ungültig ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMFunction[] compileFunctions() throws IllegalArgumentException, IllegalStateException {
		this.start();
		try {
			if (this.compileType() < 0) return new FEMFunction[0];
			final List<FEMFunction> result = new ArrayList<FEMFunction>();
			while (true) {
				result.add(this.compileParamAsFunction());
				switch (this.compileType()) {
					case -1: {
						return result.toArray(new FEMFunction[result.size()]);
					}
					case ';': {
						this.skip();
					}
				}
			}
		} finally {
			this.stop();
		}
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.domain, this.params, this.script, this.proxies);
	}

}