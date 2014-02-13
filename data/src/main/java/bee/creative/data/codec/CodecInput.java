package bee.creative.data.codec;

import java.io.DataInput;

/**
 * Diese Schnittstelle definiert die Eingabedaten eines {@link Decoder}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface CodecInput<GContext> extends CodecData<GContext>, DataInput {

}
