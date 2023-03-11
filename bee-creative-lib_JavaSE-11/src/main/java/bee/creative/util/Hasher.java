package bee.creative.util;

/** Diese Schnittstelle definiert Methoden zur Berechnung von {@link Object#hashCode() Streuwert} und {@link Object#equals(Object) Äquivalenz} gegebener
 * Objekte.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Hasher {

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Objekts zurück.
	 *
	 * @param input Objekt.
	 * @return {@link Object#hashCode() Streuwert}. */
	public int hash(Object input);

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück.
	 *
	 * @param input1 Objekt 1.
	 * @param input2 Objekt 2.
	 * @return {@link Object#equals(Object) Äquivalenz}. */
	public boolean equals(Object input1, Object input2);

}
