package bee.creative._deprecated_;

import bee.creative.io.DataSource;

/** Diese Schnittstelle definiert die Eingabedaten eines {@link Decoder}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts. */
public interface CodecSource<GContext> extends CodecData<GContext>, DataSource {

}
