package bee.creative.function;

import java.util.Iterator;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Schnittstelle definiert den Ausführungskontext einer {@link Function Funktion}. Ein Ausführungskontext
 * beinhalten die Liste der {@link Value Parameterwerte} für die aufgerufene {@link Function Funktion} und das
 * Kontextobjekt.
 * 
 * @see Value
 * @see Scopes
 * @see Function
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface Scope<GContext> extends Iterable<Value>, UseToString {

	/**
	 * Diese Methode gibt den {@link Value Parameterwert} mit dem gegebenen Index zurück.
	 * 
	 * @param index Index.
	 * @return {@link Value Parameterwerte}.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public Value get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der {@link Value Parameterwerte} zurück.
	 * 
	 * @return Anzahl der {@link Value Parameterwerte}.
	 */
	public int size();

	/**
	 * Diese Methode gibt das Kontextobjekt zurück. {@link Function Funktionen} können aus diesem Objekt Informationen für
	 * ihre Berechnungen extrahieren oder auch den Zustand dieses Objekts modifizieren. Das Kontextobjekt entspricht dem
	 * Kontext {@code this} in Java-Methoden.
	 * 
	 * @return Kontextobjekt.
	 */
	public GContext context();

	/**
	 * Diese Methode gibt den {@link Iterator} über die {@link Value Parameterwerte} zurück.
	 */
	@Override
	public Iterator<Value> iterator();

	/**
	 * {@inheritDoc} Der {@link Object#hashCode() Streuwert} wird aus den {@link Value Parameterwerten} berechnet.
	 */
	@Override
	public int hashCode();

	/**
	 * {@inheritDoc} Die {@link Object#equals(Object) Äquivalenz} basiert auf den {@link Value Parameterwerten}.
	 */
	@Override
	public boolean equals(Object obj);

}
