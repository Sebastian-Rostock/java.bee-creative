package bee.creative.io;

import java.io.DataInput;
import java.io.IOException;

/** Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einem {@link DataInput}.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class InputDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	protected final DataInput data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public InputDataSource(final DataInput data) throws NullPointerException {
		this.data = data;
	}

	@Override
	public Object data() {
		return this.data;
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		this.data.readFully(b, off, len);
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		return this.data.skipBytes(n);
	}

}