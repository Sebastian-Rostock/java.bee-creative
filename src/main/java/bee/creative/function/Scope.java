package bee.creative.function;

import bee.creative.util.Comparables.Items;

/**
 * Diese Schnittstelle definiert den Ausführungskontext einer Funktion. Ein solcher Ausführungskontext stellt eine unveränderliche Liste von Parameterwerten
 * sowie ein konstantes Kontextobjekt zur Verfügung. Über die {@link #size() Anzahl der Parameterwerte} hinaus, können von der Methode {@link #get(int)} auch
 * zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt werden. Die Methode {@link #get(int)} muss für einen gegebenen Index immer
 * den gleichen Wert liefern bzw. immer eine Ausnahme auslösen. Analoges gilt für die Methoden {@link #size()} und {@link #context()}.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Scope extends Items<Value>, Iterable<Value> {

	/**
	 * Diese Methode gibt den {@code index}-ten Parameterwert zurück. Über die {@link #size() Anzahl der Parameterwerte} hinaus, können auch zusätzliche
	 * Parameterwerte bereitgestellt werden.
	 * 
	 * @see Value
	 * @param index Index.
	 * @return {@code index}-ter Parameterwert.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein Parameterwert existiert.
	 */
	@Override
	public Value get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Parameterwerte zurück, die zur Verwendung durch eine aufgerufene Funktion bestimmt sind. Über die Methode
	 * {@link #get(int)} werden mindestens soviele Parameterwerte bereitgestellt.
	 * 
	 * @return Anzahl der Parameterwert.
	 */
	public int size();

	/**
	 * Diese Methode gibt das Kontextobjekt zurück. Funktionen können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch den Zustand
	 * dieses Objekts modifizieren. Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public Context context();

}
