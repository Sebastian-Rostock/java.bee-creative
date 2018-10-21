package bee.creative.data;

import bee.creative.array.ByteArraySection;

/** Diese Klasse implementiert ein {@link CodecSource} als {@link ArrayDataSource}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts. */
public class ArrayCodecSource<GContext> extends ArrayDataSource implements CodecSource<GContext> {

	/** Dieses Feld speichert das Kontextobjekt. */
	protected final GContext context;

	/** Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 *
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayCodecSource(final GContext context, final byte... data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/** Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 *
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayCodecSource(final GContext context, final ByteArraySection data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	public GContext context() {
		return this.context;
	}

}