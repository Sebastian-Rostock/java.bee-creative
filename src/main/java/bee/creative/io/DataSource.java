package bee.creative.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import bee.creative.util.Bytes;

/** Diese Schnittstelle definiert eine Erweiterung eines {@link Closeable} {@link DataInput} um die in {@link DataBase} spezifizierte Navigationsposition.
 *
 * @see DataBase
 * @see DataInput
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DataSource extends DataBase, DataInput, Closeable {

	/** Diese Methode liest die gegebene Anzahl an {@code byte} und gibt diese als {@code int} interpretiert zurück.
	 *
	 * @see Bytes#getIntBE(byte[], int, int)
	 * @param size Anzahl der {@code byte} (0..4).
	 * @return Zahlenwert.
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public int readInt(int size) throws IOException;

	/** Diese Methode liest die gegebene Anzahl an {@code byte} und gibt diese als {@code long} interpretiert zurück.
	 *
	 * @see Bytes#getLongBE(byte[], int, int)
	 * @param size Anzahl der {@code byte} (0..8).
	 * @return Zahlenwert.
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public long readLong(int size) throws IOException;

}