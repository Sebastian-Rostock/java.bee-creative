package bee.creative.util;

/**
 * Diese Schnittstelle definiert das Paar aus Ein- und Ausgabe eines {@link Converter Converters}.
 * 
 * @see Converter
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des Eingabe.
 * @param <GOutput> Typ der Ausgabe.
 */
public interface Conversion<GInput, GOutput> {

	/**
	 * Diese Methode gibt die Eingabe eines {@link Converter Converters} zurück.
	 * 
	 * @return Eingabe.
	 */
	public GInput input();

	/**
	 * Diese Methode gibt die Ausgabe eines {@link Converter Converters} zurück.
	 * 
	 * @return Ausgabe.
	 */
	public GOutput output();

}
