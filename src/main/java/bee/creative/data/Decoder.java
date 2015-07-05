package bee.creative.data;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert eine Methode, die einen Wert aus einer {@link CodecSource} ausliest und ihn zurück gibt.<br>
 * Die {@link CodecSource} kann dazu ein Kontextobjekt mit zusätzlichen Informationen bereitstellen.
 * 
 * @see CodecSource#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des ausgelesenen (dekodierten) Werts.
 */
public interface Decoder<GContext, GValue> {

	/**
	 * Diese Methode gibt liest einen Wert aus der gegebenen {@link CodecSource} aus und gibt ihn zurück.
	 * 
	 * @param source {@link CodecSource} mit den Eingabedaten und dem Kontextobjekt.
	 * @return Wert, der aus der {@link CodecSource} ausgelesen (dekodiert) wird.
	 * @throws IOException Wenn ein Fehler beim Einlesen oder Dekodieren auftritt.
	 */
	public GValue decode(CodecSource<? extends GContext> source) throws IOException;

}
