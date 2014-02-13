package bee.creative.data.codec;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert eine Methode, die einen Wert aus einer {@link CodecInput} ausliest und ihn zurück gibt.<br>
 * Die {@link CodecInput} kann dazu ein Kontextobjekt mit zusätzlichen Informationen bereitstellen.
 * 
 * @see CodecInput#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des ausgelesenen (dekodierten) Werts.
 */
public interface Decoder<GContext, GValue> {

	/**
	 * Diese Methode gibt liest einen Wert aus der gegebenen {@link CodecInput} aus und gibt ihn zurück.
	 * 
	 * @param source {@link CodecInput} mit den Eingabedaten und dem Kontextobjekt.
	 * @return Wert, der aus der {@link CodecInput} ausgelesen (dekodiert) wird.
	 * @throws IOException Wenn ein Fehler beim Einlesen oder Dekodieren auftritt.
	 */
	public GValue decode(CodecInput<? extends GContext> source) throws IOException;

}
