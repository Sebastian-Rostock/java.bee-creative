package bee.creative.data;

import java.io.IOException;

/** Diese Schnittstelle definiert eine Methode, die einen Wert in ein {@link CodecTarget} schreibt.<br>
 * Das {@link CodecTarget} kann dazu ein Kontextobjekt mit zusätzlichen Informationen bereitstellen.
 * 
 * @see CodecTarget#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des Werts. */
public interface Encoder<GContext, GValue> {

	/** Diese Methode schreibt den gegebenen Wert in das gegebene {@link CodecTarget}.
	 * 
	 * @param target {@link CodecTarget} mit den Ausgabedaten und dem Kontextobjekt.
	 * @param value Wert, der in das {@link CodecTarget} geschrieben (kodiert) wird.
	 * @throws IOException Wenn ein Fehler beim Schreiben oder Kodieren auftritt. */
	public void encode(CodecTarget<? extends GContext> target, GValue value) throws IOException;

}
