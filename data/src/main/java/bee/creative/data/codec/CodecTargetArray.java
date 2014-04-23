package bee.creative.data.codec;

import bee.creative.array.CompactByteArray;
import bee.creative.data.Data.DataTargetArray;

/**
 * Diese Klasse implementiert ein {@link CodecTarget} als {@link DataTargetArray}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GContext> Typ des Kontextobjekts.
 */
public class CodecTargetArray<GContext> extends DataTargetArray implements CodecTarget<GContext> {

	/**
	 * Dieses Feld speichert das Kontextobjekt.
	 */
	private final GContext context;

	/**
	 * Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit 128 Byte Größe.
	 * 
	 * @param context Kontextobjekt.
	 */
	public CodecTargetArray(final GContext context) {
		this.context = context;
	}

	/**
	 * Dieser Konstruktor initialisiert Kontextobjekt und Nutzdaten.
	 * 
	 * @param context Kontextobjekt.
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
	 */
	public CodecTargetArray(final GContext context, final CompactByteArray data) throws NullPointerException {
		super(data);
		this.context = context;
	}

	/**
	 * Dieser Konstruktor initialisiert das Kontextobjekt sowie die Nutzdaten mit der gegebenen Größe.
	 * 
	 * @see CompactByteArray#CompactByteArray(int)
	 * @param context Kontextobjekt.
	 * @param size Größe.
	 */
	public CodecTargetArray(final GContext context, final int size) {
		super(size);
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