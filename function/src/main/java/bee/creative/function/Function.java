package bee.creative.function;

/**
 * Diese Schnittstelle definiert eine Funktion, deren {@link Function#execute(Scope) Berechnungsmethode} mit einem
 * {@link Scope Ausführungskontext} aufgerufen wird und einen {@link Value Ergebniswert} zurück gibt. Aus dem
 * Kontextobjekt des {@link Scope Ausführungskontexts} können hierbei Informationen für die Berechnungen extrahiert oder
 * auch der Zustand dieses Objekts modifiziert werden.
 * 
 * @see Value
 * @see Scope
 * @see Functions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface Function<GContext> {

	/**
	 * Diese Methode führt Berechnungen im gegebenen {@link Scope Ausführungskontext} durch und gibt deren {@link Value
	 * Ergebniswert} zurück.
	 * 
	 * @param scope {@link Scope Ausführungskontext}.
	 * @return {@link Value Ergebniswert}.
	 */
	public Value execute(Scope<? extends GContext> scope);

}
