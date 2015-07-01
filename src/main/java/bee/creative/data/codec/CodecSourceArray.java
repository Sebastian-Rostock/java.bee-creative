package bee.creative.data.codec;

import bee.creative.array.ByteArraySection;
import bee.creative.data.Data.DataSourceArray;

/**
 * Diese Klasse implementiert ein {@link CodecSource} als {@link DataSourceArray}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public class CodecSourceArray<GContext> extends DataSourceArray implements CodecSource<GContext> {

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	private final GContext context;

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 * 
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public CodecSourceArray(final GContext context, final byte... data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 * 
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public CodecSourceArray(final GContext context, final ByteArraySection data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GContext context() {
		return this.context;
	}

}