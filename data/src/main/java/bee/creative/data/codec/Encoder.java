package bee.creative.data.codec;

import java.io.IOException;

/**
 * Diese Schnittstelle definiert eine Methode, die einen Wert in ein {@link CodecOutput} schreibt.<br>
 * Das {@link CodecOutput} kann dazu ein Kontextobjekt mit zusätzlichen Informationen bereitstellen.
 * 
 * @see CodecOutput#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des Werts.
 */
public interface Encoder<GContext, GValue> {

	/**
	 * Diese Methode schreibt den gegebenen Wert in das gegebene {@link CodecOutput}.
	 * 
	 * @param target {@link CodecOutput} mit den Ausgabedaten und dem Kontextobjekt.
	 * @param value Wert, der in das {@link CodecOutput} geschrieben (kodiert) wird.
	 * @throws IOException Wenn ein Fehler beim Schreiben oder Kodieren auftritt.
	 */
	public void encode(CodecOutput<? extends GContext> target, GValue value) throws IOException;

}
