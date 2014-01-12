package bee.creative.data.codec;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert eine Methode, die einen Wert aus einer {@link DecoderSource} ausliest und ihn zurück gibt.<br>
 * Die {@link DecoderSource} kann dazu ein Kontextobjekt mit zusätzlichen Informationen bereitstellen.
 * 
 * @see DecoderSource#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des ausgelesenen (dekodierten) Werts.
 */
public interface Decoder<GContext, GValue> {

	/**
	 * Diese Methode gibt liest einen Wert aus der gegebenen {@link DecoderSource} aus und gibt ihn zurück.
	 * 
	 * @param source {@link DecoderSource} mit den Eingabedaten und dem Kontextobjekt.
	 * @return Wert, der aus der {@link DecoderSource} ausgelesen (dekodiert) wird.
	 * @throws IOException Wenn ein Fehler beim Einlesen oder Dekodieren auftritt.
	 */
	public GValue decode(DecoderSource<? extends GContext> source) throws IOException;

}
