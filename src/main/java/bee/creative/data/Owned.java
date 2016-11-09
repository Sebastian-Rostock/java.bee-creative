package bee.creative.data;

/** Diese Schnittstelle definiert ein Objekt mit Besitzer, welcher das Objekt erzeugt und verwaltet hat.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Owned {

	/** Diese Methode gibt den Besitzer zurück, welcher das Objekt erzeugt und verwaltet hat.
	 *
	 * @return Besitzer. */
	public Object owner();

}
