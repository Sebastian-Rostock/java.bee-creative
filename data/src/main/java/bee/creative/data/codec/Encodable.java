package bee.creative.data.codec;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert ein Objekt, dass sich selbst in ein {@link CodecOutput} schreiben kann.
 * 
 * @see CodecOutput#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface Encodable<GContext> {

	/**
	 * Diese Methode schreibt dieses Objekt in das gegebene {@link CodecOutput}.
	 * 
	 * @param target {@link CodecOutput} mit den Ausgabedaten und dem Kontextobjekt.
	 * @throws IOException Wenn ein Fehler beim Schreiben oder Kodieren auftritt.
	 */
	public void encode(CodecOutput<? extends GContext> target) throws IOException;

}
