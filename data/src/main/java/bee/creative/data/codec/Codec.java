package bee.creative.data.codec;

/**
 * Diese Schnittstelle definiert die Kombination eines {@link Encoder}s und eines {@link Decoder}s als ein Objekt, dass einen gegebenen Wert in ein
 * {@link EncoderTarget} schreiben sowie einen solchen Wert wieder aus einer {@link DecoderSource} einlesen kann.
 * 
 * @see CodecData#context()
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GValue> Typ des geschriebenen bzw. gelesenen Werts.
 */
public interface Codec<GContext, GValue> extends Encoder<GContext, GValue>, Decoder<GContext, GValue> {

}
