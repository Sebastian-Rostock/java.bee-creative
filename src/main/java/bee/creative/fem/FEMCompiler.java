package bee.creative.fem;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.fem.FEMScript.Token;
import bee.creative.lang.Objects;
import bee.creative.util.Parser;

/** Diese Klasse implementiert einen Kompiler, welcher als {@link Parser} auf den {@link FEMScript#types() Bereichstypen} eines {@link FEMScript aufbereiteten
 * Quelltexts} arbeitet.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMCompiler extends Parser {

	/** Dieses Feld speichert den Quelltext. */
	FEMScript script = FEMScript.EMPTY;

	/** Dieses Feld speichert die über {@link #proxy(String)} erzeugten Platzhalter. */
	final Map<String, FEMProxy> proxies = new LinkedHashMap<>();

	/** Dieses Feld speichert die Parameternamen. */
	final List<String> params = new LinkedList<>();

	/** Diese Methode gibt den zu kompilierenden Quelltext zurück.
	 *
	 * @return Quelltext. */
	public FEMScript script() {
		return this.script;
	}

	/** Diese Methode gibt die {@link #index() aktuelle Position} als {@link Token#start() Startposition} des {@link #token() aktuellen Bereichs} bezogen auf die
	 * {@link #scriptSource() Zeichenkette des aufbereiteten Quelltexts} zurück.
	 *
	 * @see #token()
	 * @see Token#start()
	 * @return Startposition des aktuellen Quelltextbereichs. */
	public int scriptOffset() {
		return this.token().start();
	}

	/** Diese Methode gibt die Zeichenkette des {@link #script() aufbereiteten Quelltexts} zurück.
	 *
	 * @see #script()
	 * @see FEMScript#source()
	 * @return Zeichenkette des Quelltexts. */
	public String scriptSource() {
		return this.script().source();
	}

	/** Diese Methode gibt die Länge der Zeichenkette des {@link #script() aufbereiteten Quelltexts} zurück.
	 *
	 * @see #script()
	 * @see FEMScript#source()
	 * @return Länge des Quelltexts. */
	public int scriptLength() {
		return this.script().source().length();
	}

	/** Diese Methode gibt die Spaltennummer zur {@link #scriptOffset() aktuellen Quelltextposition} zurück.
	 *
	 * @return aktuelle Spaltennummer. */
	public int scriptColIndex() {
		final String source = this.script().source();
		final int offset = this.scriptOffset(), index = source.lastIndexOf('\n', offset);
		return (offset + 1) - (index < 0 ? 0 : index);
	}

	/** Diese Methode gibt die Zeilennummer zur {@link #scriptOffset() aktuellen Quelltextposition} zurück.
	 *
	 * @return aktuelle Zeilennummer. */
	public int scriptRowIndex() {
		final String source = this.script().source();
		int result = 0;
		for (int index = this.scriptOffset() + 1; index >= 0; index = source.lastIndexOf('\n', index - 1)) {
			result++;
		}
		return result;
	}

	/** Diese Methode setzt den zu kompilierenden Quelltext und gibt {@code this} zurück.
	 *
	 * @param script Quelltext.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code script} {@code null} ist.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMCompiler useScript(final FEMScript script) throws NullPointerException {
		this.source(script.types());
		this.script = script;
		return this;
	}

	/** Diese Methode gibt den Platzhalter der Funktion mit dem gegebenen Namen zurück.
	 *
	 * @param name {@link FEMProxy#from(String) Name und Kennung} des Platzhalters.
	 * @return Platzhalterfunktion.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public FEMProxy proxy(final String name) throws NullPointerException {
		FEMProxy result = this.proxies.get(name);
		if (result != null) return result;
		this.proxies.put(name, result = FEMProxy.from(name));
		return result;
	}

	/** Diese Methode gibt die über {@link #proxy(String)} erzeugten Platzhalter zurück.
	 *
	 * @return Abbildung von Namen auf Platzhalter. */
	public Map<String, FEMProxy> proxies() {
		return this.proxies;
	}

	/** Diese Methode ist eine Abkürzung für {@code this.putProxies(Arrays.asList(proxies))}.
	 *
	 * @see #putProxies(Iterable)
	 * @param proxies Platzhalter.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist oder enthält. */
	public FEMCompiler putProxies(final FEMProxy... proxies) throws NullPointerException {
		return this.putProxies(Arrays.asList(proxies));
	}

	/** Diese Methode fügt die gegebenen Platzhalter in {@link #proxies()} ein und gibt {@code this} zurück.
	 *
	 * @param proxies Platzhalter.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist oder enthält. */
	public FEMCompiler putProxies(final Iterable<FEMProxy> proxies) throws NullPointerException {
		for (final FEMProxy proxy: proxies) {
			this.proxies.put(proxy.name().toString(), proxy);
		}
		return this;
	}

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public List<String> params() {
		return this.params;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Parameternames in {@link #params()} zurück.
	 *
	 * @see List#indexOf(Object)
	 * @param name Parametername.
	 * @return Position oder {@code -1}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public int getParam(final String name) throws NullPointerException {
		return this.params.indexOf(name.toString());
	}

	/** Diese Methode fügt den gegebenen Parameternamen an der gegebenen Position in {@link #params()} ein und gibt {@code this} zurück.
	 *
	 * @see List#add(int, Object)
	 * @param index Einfügelosition.
	 * @param name Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index} ungültig ist. */
	public FEMCompiler putParam(final int index, final String name) throws NullPointerException, IllegalArgumentException {
		this.params.add(index, name.toString());
		return this;
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMCompiler useParams(final String... value) throws NullPointerException {
		return this.useParams(Arrays.asList(value));
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMCompiler useParams(final List<String> value) throws NullPointerException {
		if (value.contains(null)) throw new NullPointerException();
		this.params.clear();
		this.params.addAll(value);
		return this;
	}

	/** Diese Methode entfernt die gegebene Anzahl der an Parameternamen ab dem BEginn der Liste aus {@link #params()} und gibt {@code this} zurück.
	 *
	 * @param count Anzahl.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl ungültig ist. */
	public FEMCompiler popParams(final int count) throws IllegalArgumentException {
		this.params.subList(0, count).clear();
		return this;
	}

	/** Diese Methode gibt den aktuellen Bereich zurück.
	 *
	 * @see #index()
	 * @see #script()
	 * @see FEMScript#get(int)
	 * @return aktueller Bereich. */
	public Token token() {
		return this.isParsed() ? Token.EMPTY : this.script.get(this.index());
	}

	/** Diese Methode gibt die Zeichenkette im {@link #token() aktuellen Abschnitt} des {@link #script() Quelltexts} zurück.
	 *
	 * @see #token()
	 * @see Token#extract(String)
	 * @return Aktuelle Zeichenkette. */
	public String section() {
		return this.token().extract(this.script.source());
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.params, this.script, this.proxies);
	}

}