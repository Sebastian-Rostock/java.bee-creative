package bee.creative.data.codec;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert ein Objekt, dass sich selbst in ein {@link CodecTarget} schreiben kann.
 * 
 * @see CodecTarget#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface Encodable<GContext> {

	/**
	 * Diese Methode schreibt dieses Objekt in das gegebene {@link CodecTarget}.
	 * 
	 * @param target {@link CodecTarget} mit den Ausgabedaten und dem Kontextobjekt.
	 * @throws IOException Wenn ein Fehler beim Schreiben oder Kodieren auftritt.
	 */
	public void encode(CodecTarget<? extends GContext> target) throws IOException;

}
