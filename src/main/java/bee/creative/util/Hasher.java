package bee.creative.util;

/**
 * Diese Schnittstelle definiert Methoden zur Berechnung von {@link Object#hashCode() Streuwert} und {@link Object#equals(Object) Äquivalenz} gegebener
 * Eingaben.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingaben.
 */
public interface Hasher<GInput> {

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Eingabe zurück.
	 * 
	 * @param input Eingabe.
	 * @return {@link Object#hashCode() Streuwert}.
	 */
	public int hash(final GInput input);

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Eingaben zurück.
	 * 
	 * @param input1 Eingabe 1.
	 * @param input2 Eingabe 2.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Eingaben.
	 */
	public boolean equals(final GInput input1, final GInput input2);

}