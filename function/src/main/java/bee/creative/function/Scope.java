package bee.creative.function;

import bee.creative.util.Comparables.Get;

/**
 * Diese Schnittstelle definiert den Ausführungskontext einer {@link Function Funktion}. Ein solcher {@link Scope Ausführungskontext} stellt eine Liste von
 * {@link Value Parameterwerten} sowie ein Kontextobjekt zur Verfügung.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Scope extends Get<Value>, Iterable<Value> {

	/**
	 * Diese Methode gibt den {@code index}-ten {@link Value Parameterwert} zurück. Über die {@link #size() Anzahl der Parameterwerte} hinaus, können auch
	 * zusätzliche {@link Value Parameterwerte} bereitgestellt werden.
	 * 
	 * @see Value
	 * @param index Index.
	 * @return {@code index}-ter {@link Value Parameterwert}.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein {@link Value Parameterwert} existiert.
	 */
	@Override
	public Value get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der {@link Value Parameterwerte} zurück, die zur Verwendung durch eine aufgerufene {@link Function Funktion} bestimmt sind.
	 * Über die Methode {@link #get(int)} werden mindestens soviele {@link Value Parameterwerte} bereitgestellt.
	 * 
	 * @return Anzahl der {@link Value Parameterwert}.
	 */
	public int size();

	/**
	 * Diese Methode gibt das Kontextobjekt zurück. {@link Function Funktionen} können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch
	 * den Zustand dieses Objekts modifizieren. Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public Object context();

}
