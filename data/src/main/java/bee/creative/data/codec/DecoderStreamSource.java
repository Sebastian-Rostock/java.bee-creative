package bee.creative.data.codec;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Diese Klasse implementiert eine {@link DecoderSource} als {@link DataInputStream}, auf desren internen {@link InputStream} via {@link #stream()} zugegriffen
 * werden kann.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GStream> Typ des {@link InputStream}s.
 */
public class DecoderStreamSource<GContext, GStream extends InputStream> extends DataInputStream implements DecoderSource<GContext> {

	/**
	 * Dieses Feld speichert den {@link InputStream}.
	 */
	protected final GStream stream;

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	protected final GContext context;

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und {@link InputStream}.
	 * 
	 * @param context Kontextobjekt.
	 * @param stream {@link InputStream}.
	 * @throws NullPointerException Wenn der gegebene {@link InputStream} {@code null} ist.
	 */
	public DecoderStreamSource(final GContext context, final GStream stream) {
		super(stream);
		if(stream == null) throw new NullPointerException();
		this.stream = stream;
		this.context = context;
	}

	/**
	 * Diese Methode gibt den {@link InputStream} zurück.
	 * 
	 * @return {@link InputStream}.
	 */
	public GStream stream() {
		return this.stream;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GContext context() {
		return this.context;
	}

}