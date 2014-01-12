package bee.creative.data.codec;

import java.io.DataOutput;

/**
 * Diese Schnittstelle definiert die Ausgabedaten eines {@link Encoder}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface EncoderTarget<GContext> extends CodecData<GContext>, DataOutput {

}
