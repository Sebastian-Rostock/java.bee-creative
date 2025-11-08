package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Berechnung der {@link Object#equals(Object) Äquivalenz} gegebener Objekte.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface HasherEquals {

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück.
	 *
	 * @param input1 Objekt 1.
	 * @param input2 Objekt 2.
	 * @return {@link Object#equals(Object) Äquivalenz}. */
	boolean equals(Object input1, Object input2);
}