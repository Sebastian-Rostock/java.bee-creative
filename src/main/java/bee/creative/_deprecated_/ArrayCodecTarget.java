package bee.creative._deprecated_;

import bee.creative.array.CompactByteArray;
import bee.creative.io.ArrayDataTarget;

/** Diese Klasse implementiert ein {@link CodecTarget} als {@link ArrayDataTarget}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts. */
public class ArrayCodecTarget<GContext> extends ArrayDataTarget implements CodecTarget<GContext> {

	/** Dieses Feld speichert das Kontextobjekt. */
	protected final GContext context;

	/** Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit 128 Byte Größe.
	 *
	 * @param context Kontextobjekt. */
	public ArrayCodecTarget(final GContext context) {
		this.context = context;
	}

	/** Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 *
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public ArrayCodecTarget(final GContext context, final CompactByteArray data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/** Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit der gegebenen Größe.
	 *
	 * @see CompactByteArray#CompactByteArray(int)
	 * @param context Kontextobjekt.
	 * @param size Größe. */
	public ArrayCodecTarget(final GContext context, final int size) {
		super(size);
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	public GContext context() {
		return this.context;
	}

}