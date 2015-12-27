package bee.creative.data;

import bee.creative.array.CompactByteArray;

/**
 * Diese Klasse implementiert ein {@link CodecTarget} als {@link ArrayDataTarget}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public class ArrayCodecTarget<GContext> extends ArrayDataTarget implements CodecTarget<GContext> {

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	final GContext __context;

	/**
	 * Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit 128 Byte Größe.
	 * 
	 * @param context Kontextobjekt.
	 */
	public ArrayCodecTarget(final GContext context) {
		this.__context = context;
	}

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 * 
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public ArrayCodecTarget(final GContext context, final CompactByteArray data) throws NullPointerException {
		super(data);
		this.__context = context;
	}

	/**
	 * Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit der gegebenen Größe.
	 * 
	 * @see CompactByteArray#CompactByteArray(int)
	 * @param context Kontextobjekt.
	 * @param size Größe.
	 */
	public ArrayCodecTarget(final GContext context, final int size) {
		super(size);
		this.__context = context;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final GContext context() {
		return this.__context;
	}

}