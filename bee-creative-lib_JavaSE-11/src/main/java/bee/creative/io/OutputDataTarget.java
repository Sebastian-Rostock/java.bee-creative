package bee.creative.io;

import java.io.DataOutput;
import java.io.IOException;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link DataOutput}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class OutputDataTarget extends BaseDataTarget {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final DataOutput data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public OutputDataTarget(final DataOutput data) throws NullPointerException {
		this.data = data;
	}

	@Override
	public Object data() {
		return this.data;
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		this.data.write(b, off, len);
	}

}