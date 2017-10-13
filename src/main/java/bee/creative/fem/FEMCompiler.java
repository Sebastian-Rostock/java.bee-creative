package bee.creative.fem;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Token;
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

	/** Dieses Feld speichert den Quelltext. */
	FEMScript script = FEMScript.EMPTY;

	/** Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter. */
	final Map<String, FEMProxy> proxies = new LinkedHashMap<>();

	/** Dieses Feld speichert die Parameternamen. */
	final List<String> params = new LinkedList<>();

	@SuppressWarnings ("javadoc")
	// TODO die positionen/texte als getter am compiler
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

	/** Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
	 *
	 * @param name Name des Platzhalters.
	 * @return Platzhalterfunktion.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public final FEMProxy proxy(final String name) throws NullPointerException {
		FEMProxy result = this.proxies.get(name);
		if (result != null) return result;
		this.proxies.put(name, result = new FEMProxy(name));
		return result;
	}

	/** Diese Methode gibt den aktuellen Bereich zurück.
	 *
	 * @see #index()
	 * @see #script()
	 * @see FEMScript#get(int)
	 * @return aktueller Bereich. */
	public final Token range() {
		return this.isParsed() ? Token.EMPTY : this.script.get(this.index());
	}

	/** Diese Methode gibt den zu kompilierenden Quelltext zurück.
	 *
	 * @return Quelltext. */
	public final FEMScript script() {
		return this.script;
	}

	/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.
	 *
	 * @return Abbildung von Namen auf Platzhalter. */
	public final Map<String, FEMProxy> proxies() {
		return this.proxies;
	}

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public final List<String> params() {
		return this.params;
	}

	/** Diese Methode gibt die Zeichenkette im {@link #range() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
	 *
	 * @see #range()
	 * @see Token#extract(String)
	 * @return Aktuelle Zeichenkette. */
	public final String section() {
		return this.range().extract(this.script.source());
	}

 

	/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
	 *
	 * @param value Quelltext.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code vslue} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMCompiler useScript(final FEMScript value) throws NullPointerException {
		this.source(value.types());
		this.script = value;
		return this;
	}

	public final int getParams(final String name) throws NullPointerException {
		return this.params.indexOf(name);
	}

	public void addParam(final int index, final String param) {
		this.params.add(index, param);
	}

	public final void popParams(final int count) throws NullPointerException {
		this.params.subList(0, count).clear();
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMCompiler useParams(final String... value) throws NullPointerException {
		return this.useParams(Arrays.asList(value));
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public final FEMCompiler useParams(final List<String> value) throws NullPointerException {
		if (value.contains(null)) throw new NullPointerException();
		this.params.clear();
		this.params.addAll(value);
		return this;
	}
 

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.params, this.script, this.proxies);
	}

}