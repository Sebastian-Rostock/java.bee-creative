package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import bee.creative.io.MappedBuffer;
import bee.creative.mmi.MMIArray;
import bee.creative.mmi.MMIArrayL;
import bee.creative.util.Getter;

/** Diese Klasse implementiert einen Puffer für {@link IAMArray Zahlenfolgen}, welcher diese mindestens {@link IAMArray#compact() kompaktiert}, und darüber
 * hinaus in einen {@link MappedBuffer Dateipuffer} auslagern kann.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMBuffer implements Getter<IAMArray, IAMArray> {

	/** Dieses Feld speichert den Zahlenfolgenpuffer ohne Dateianbindung, welcher die ihm {@link #get(IAMArray) übergebenen} Zahlenfolgen lediglich
	 * kompaktiert. */
	public static final IAMBuffer EMPTY = new IAMBuffer(null);

	/** Diese Methode ist eine Abkürzung für {@link #from(File, long) IAMBuffer.from(file, 1 << 20)}.
	 *
	 * @param file Dateipfad.
	 * @return Zahlenfolgenpuffer.
	 * @throws IOException Wenn die Datei nicht angebunden werden kann. */
	public static IAMBuffer from(final File file) throws IOException {
		return IAMBuffer.from(file, 1 << 20);
	}

	/** Diese Methode gibt das zurück. Wenn der Dateiname {@code null} ist, wird eine {@link File#createTempFile(String, String) temporäre} Datei angelegt.
	 *
	 * @param file Dateipfad.
	 * @param size initiale Dateigröße.
	 * @return Zahlenfolgenpuffer.
	 * @throws IOException Wenn die Datei nicht angebunden werden kann. */
	public static IAMBuffer from(final File file, final long size) throws IOException {
		return new IAMBuffer(new MappedBuffer(file, size));
	}

	/** Diese Methode ist eine Abkürzung für {@link #temp(long) IAMBuffer.temp(1 << 20)}.
	 *
	 * @return Zahlenfolgenpuffer.
	 * @throws IOException Wenn die Datei nicht angebunden werden kann. */
	public static IAMBuffer temp() throws IOException {
		return IAMBuffer.temp(1 << 20);
	}

	/** Diese Methode gibt einen neuen Zahlenfolgenpuffer zurück, der seine Zahlenfolgen in eine {@link File#createTempFile(String, String) temporäre} Datei
	 * auslagert. Sie ist eine Abkürzung ür {@link #from(File, long) IAMBuffer.from(File.createTempFile("temp", ".iambuffer"), size)}.
	 *
	 * @param size initiale Dateigröße.
	 * @return Zahlenfolgenpuffer.
	 * @throws IOException Wenn die Datei nicht angebunden werden kann. */
	public static IAMBuffer temp(final long size) throws IOException {
		return IAMBuffer.from(File.createTempFile("temp", ".iambuffer"), size);
	}

	/** Dieses Feld speichert den Dateipuffer, in den die Zahlenfolgen ausgelagert werden. Wenn dieser {@code null} ist, werden die Zahlenfolgen lediglich
	 * kompaktiert. */
	final MappedBuffer buffer;

	/** Dieses Feld speichert die Adresse innerhalb des Dateipuffers, an welche die nächste Zahlenfolge geschrieben wird. */
	long addr;

	IAMBuffer(final MappedBuffer buffer) {
		this.buffer = buffer;
	}

	/** Diese Methode überführt die gegebene Zahlenfolge in eine {@link IAMArray#compact() kompaktierte} und gibt diese zurück. Wenn dieser Zahlenfolgenpuffer an
	 * eine {@link #file() Datei} angebunden ist, wird die Zahlenfolge in diese {@link MappedBuffer#putArray(long, IAMArray) ausgelagert}, damit sie nur noch 24
	 * Byte Hauptspeicher belegt.
	 *
	 * @param item gegebene Zahlenfolge.
	 * @return kompaktierte und ggf. ausgelagerte Zahlenfolge. */
	@Override
	public IAMArray get(final IAMArray item) {
		if (this.buffer == null) return item.compact();
		if (item instanceof MMIArrayL) {
			final MMIArrayL array = (MMIArrayL)item;
			if (array.buffer == this.buffer) return array;
		}
		final int length = item.length();
		if (length == 0) return IAMArray.EMPTY;
		final int mode = item.mode();
		final long addr;
		synchronized (this) {
			addr = this.addr;
			final long size = (addr + (MMIArray.size(mode) * (long)length) + 3) & -4L;
			this.buffer.grow(this.addr = size);
		}
		this.buffer.putArray(addr, item);
		return this.buffer.getArray(addr, length, mode);
	}

	/** Diese Methode gibt die Datei des intern genutzten {@link MappedBuffer Puffers} oder {@code null} zurück.
	 *
	 * @return Datei oder {@code null}. */
	public File file() {
		return this.buffer != null ? this.buffer.file() : null;
	}

}