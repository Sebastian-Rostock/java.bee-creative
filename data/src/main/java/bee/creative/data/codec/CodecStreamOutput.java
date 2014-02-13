package bee.creative.data.codec;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * Diese Klasse implementiert ein {@link CodecOutput} als {@link DataOutputStream}, auf dessen internen {@link OutputStream} via {@link #stream()} zugegriffen
 * werden kann.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 * @param <GStream> Typ des {@link OutputStream}s.
 */
public class CodecStreamOutput<GContext, GStream extends OutputStream> extends DataOutputStream implements CodecOutput<GContext> {

	/**
	 * Dieses Feld speichert den {@link OutputStream}.
	 */
	protected final GStream stream;

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	protected final GContext context;

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und {@link OutputStream}.
	 * 
	 * @param context Kontextobjekt.
	 * @param stream {@link OutputStream}.
	 * @throws NullPointerException Wenn der gegebene {@link OutputStream} {@code null} ist.
	 */
	public CodecStreamOutput(final GContext context, final GStream stream) throws NullPointerException {
		super(stream);
		if(stream == null) throw new NullPointerException();
		this.stream = stream;
		this.context = context;
	}

	/**
	 * Diese Methode gibt den {@link OutputStream} zurück.
	 * 
	 * @return {@link OutputStream}.
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