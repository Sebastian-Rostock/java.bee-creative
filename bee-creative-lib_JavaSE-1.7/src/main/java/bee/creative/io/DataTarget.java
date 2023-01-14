package bee.creative.io;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import bee.creative.lang.Bytes;

/** Diese Schnittstelle definiert eine Erweiterung einer {@link Closeable} {@link DataOutput} um die in {@link DataBase} spezifizierte Navigationsposition.
 *
 * @see DataBase
 * @see DataOutput
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DataTarget extends DataBase, DataOutput, Closeable {

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte} des gegebenen Zahlenwerts.
	 *
	 * @see Bytes#setIntBE(byte[], int, int, int)
	 * @param v Zahlenwert.
	 * @param size Anzahl der {@code byte} (0..4).
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public void writeInt(int v, int size) throws IOException;

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte} des gegebenen Zahlenwerts.
	 *
	 * @see Bytes#setLongBE(byte[], int, int, long)
	 * @param v Zahlenwert.
	 * @param size Anzahl der {@code byte} (0..8).
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public void writeLong(long v, int size) throws IOException;

	/** Diese Methode setzt die Länge der Nutzdaten. Die Navigationsposition wird dabei falls nötig auf den gegebenen Wert verkleinert.
	 *
	 * @see #length()
	 * @param value Anzahl verfügbarer {@code byte}.
	 * @throws IOException Wenn ein I/O-Fehler auftritt oder die Lönge nicht gesetzt werden kann. */
	public void allocate(long value) throws IOException;

}