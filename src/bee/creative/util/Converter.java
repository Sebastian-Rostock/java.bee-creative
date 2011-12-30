package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Konvertierungsmethode, die gegebene Objekte vom Typ <code>GInput</code> in Objekte
 * vom Typ <code>GOutput</code> umwandelt. Bei der Konvertierung kann es sich um eine Navigation in einem Objektgraphen
 * oder auch das Parsen bzw. Formatieren eines Objektes handel.
 * 
 * @see Converter
 * @see Converters
 * @see Conversion
 * @see Conversions
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ des Eingabe.
 * @param <GOutput> Typ der Ausgabe.
 */
public interface Converter<GInput, GOutput> {

	/**
	 * Diese Methode konvertiert die Eingabe in den Ausgabewert und gibt diesen zurück. Bei der Konvertierung kann es sich
	 * um eine Navigation in einem Objektgraphen oder auch das Parsen bzw. Formatieren eines Objektes handel.
	 * 
	 * @param input Eingabe.
	 * @return Ausgabe.
	 */
	public GOutput convert(GInput input);

}
