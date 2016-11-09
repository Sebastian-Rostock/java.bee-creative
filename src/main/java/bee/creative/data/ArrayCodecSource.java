package bee.creative.data;

import bee.creative.array.ByteArraySection;

/** Diese Klasse implementiert ein {@link CodecSource} als {@link ArrayDataSource}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts. */
public class ArrayCodecSource<GContext> extends ArrayDataSource implements CodecSource<GContext> {

	/** Dieses Feld speichert das Kontextobjekt. */
	final GContext _context_;

	/** Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 *
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayCodecSource(final GContext context, final byte... data) throws NullPointerException {
		super(data);
		this._context_ = context;
	}

	/** Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 *
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayCodecSource(final GContext context, final ByteArraySection data) throws NullPointerException {
		super(data);
		this._context_ = context;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final GContext context() {
		return this._context_;
	}

}