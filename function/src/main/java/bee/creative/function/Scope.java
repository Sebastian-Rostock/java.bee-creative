package bee.creative.function;

import bee.creative.util.Comparables.Get;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Schnittstelle definiert den Ausführungskontext einer {@link Function Funktion}. Ein solcher {@link Scope Ausführungskontext} stellt eine Liste von {@link Value Parameterwerten} sowie ein Kontextobjekt zur Verfügung.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Scope extends Get<Value>, Iterable<Value>, UseToString {

	/**
	 * Diese Methode gibt den {@code index}-ten {@link Value Parameterwert} zurück. Über die {@link #size() Anzahl der Parameterwerte} für eine aufgerufene {@link Function Funktion} hinaus können auch zusätzliche {@link Value Parameterwerte} bereitgestellt werden.
	 * 
	 * @see Value
	 * @param index Index.
	 * @return {@code index}-ter {@link Value Parameterwert}.
	 * @throws IndexOutOfBoundsException Wenn für den gegebenen Index kein {@link Value Parameterwert} existiert.
	 */
	@Override
	public Value get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der {@link Value Parameterwerte} zurück, die zur Verwendung durch eine aufgerufene {@link Function Funktion} bestimmt sind. Über die Methode {@link #get(int)} werden mindestens soviele {@link Value Parameterwerte} bereitgestellt.
	 * 
	 * @return Anzahl der {@link Value Parameterwert}.
	 */
	public int size();

	/**
	 * Diese Methode gibt das Kontextobjekt zurück. {@link Function Funktionen} können aus diesem Objekt Informationen für ihre Berechnungen extrahieren oder auch den Zustand dieses Objekts modifizieren. Das Kontextobjekt entspricht dem Kontext {@code this} in {@code Java}-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public Object context();

	/**
	 * Diese Methode ruft die gegebene {@link Function Funktion} mit einem neuen {@link Scope Ausführungskontext} auf und gibt deren {@link Value Ergebniswert} zurück. Der hierfür erzeugte {@link Scope Ausführungskontext} verwendet das Kontextobjekt dieses {@link Scope Ausführungskontexts} und besitzt eine Liste von {@link Value Parameterwerten}, die durch das Ersetzen der ersen {@code deleteCount} {@link Value Parameterwerte} dieses {@link Scope Ausführungskontexts} mit den in {@code insertValues} gegebenen {@link Value Parameterwerten} entsteht.
	 * 
	 * @see Value
	 * @see Function
	 * @see #execute(Object, Function, Value...)
	 * @param function {@link Function Funktion}.
	 * @param values Array der ersten {@link Value Parameterwerte} des neuen {@link Scope Ausführungskontexts}.
	 * @return {@link Value Ergebniswert}.
	 * @throws NullPointerException Wenn die gegebene {@link Function Funktion} bzw. einer der gegebenen {@link Value Parameterwerte} {@code null} ist.
	 */
	public Value execute(Function function, Value... values) throws NullPointerException;

	/**
	 * Diese Methode ruft die gegebene {@link Function Funktion} mit einem neuen {@link Scope Ausführungskontext} auf und gibt deren {@link Value Ergebniswert} zurück. Der hierfür erzeugte {@link Scope Ausführungskontext} verwendet das gegebene Kontextobjekt {@code context} und besitzt eine Liste von {@link Value Parameterwerten}, die durch das Ersetzen der {@link Value Parameterwerte} dieses {@link Scope Ausführungskontexts} mit den gegebenen {@link Value Parameterwerten} entsteht.
	 * 
	 * @see Value
	 * @see Function
	 * @param context Kontextobjekt.
	 * @param function {@link Function Funktion}.
	 * @param values Array der ersten {@link Value Parameterwerte} des neuen {@link Scope Ausführungskontexts}.
	 * @return {@link Value Ergebniswert}.
	 * @throws NullPointerException Wenn die gegebene {@link Function Funktion} bzw. einer der gegebenen {@link Value Parameterwerte} {@code null} ist.
	 */
	public Value execute(Object context, Function function, Value... values) throws NullPointerException;

}
