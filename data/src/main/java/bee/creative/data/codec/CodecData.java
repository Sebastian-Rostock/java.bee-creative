package bee.creative.data.codec;

/**
 * Diese Schnittstelle definiert die Basis einer {@link DecoderSource} bzw. eines {@link EncoderTarget}s als Objekt, dass ein Kontextobjekt bereit stellt.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public interface CodecData<GContext> {

	/**
	 * Diese Methode gibt das Objekt zurück, in dessen Kontext ein Wert aus einer {@link DecoderSource} gelesen bzw. in ein {@link EncoderTarget} geschrieben
	 * wird.
	 * 
	 * @return Kontextobjekt.
	 */
	public GContext context();

}
