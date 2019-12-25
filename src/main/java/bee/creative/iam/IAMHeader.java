package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.lang.Bytes;
import bee.creative.mmi.MMIArray;

/** Diese Klasse implementiert ein Objekt zur Analyse und Prüfung der Kennung einer Datenstruktur in den Kopfdaten von {@code IAM_MAPPING}, {@code IAM_LISTING}
 * oder {@code IAM_INDEX}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMHeader {

	/** Dieses Feld speichert die Bitmaske. */
	final int mask;

	/** Dieses Feld speichert den Vergleichswert. */
	final int value;

	/** Dieser Konstruktor initialisiert Bitmaske und Vergleichswert.
	 *
	 * @param mask Bitmaske.
	 * @param value Vergleichswert. */
	public IAMHeader(final int mask, final int value) {
		this.mask = mask;
		this.value = value;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Kopfdaten eine gültige Datenstrukturkennung enthalten.
	 *
	 * @param header Kopfdaten.
	 * @return {@code true}, wenn die Kopfdaten eine gültige Kennunge enthalten. */
	public final boolean isValid(final int header) {
		return (header & this.mask) == this.value;
	}

	/** Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der Quelldaten
	 * die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null} geliefert.
	 *
	 * @param bytes Quelldaten.
	 * @return Bytereihenfolge oder {@code null}.
	 * @throws NullPointerException Wenn {@code bytes} {@code null} ist. */
	public final ByteOrder orderOf(final byte[] bytes) throws NullPointerException {
		if (bytes.length < 4) return null;
		if (this.isValid(Bytes.getInt4BE(bytes, 0))) return ByteOrder.BIG_ENDIAN;
		if (this.isValid(Bytes.getInt4LE(bytes, 0))) return ByteOrder.LITTLE_ENDIAN;
		return null;
	}

	/** Diese Methode gibt die Bytereihenfolge zur Interpretation der gegebenen Quelldaten zurück, für welche die Kopfdaten in den ersten vier Byte der Quelldaten
	 * die {@link #isValid(int) gültige} Datenstrukturkennung enthalten. Wenn die Kopfdaten {@link #isValid(int) ungültig} sind, wird {@code null} geliefert.
	 *
	 * @see #orderOf(byte[])
	 * @see MMIArray#from(Object)
	 * @param object Quelldaten.
	 * @return Bytereihenfolge oder {@code null}.
	 * @throws IOException Wenn {@link MMIArray#from(Object)} eine entsprechende Ausnahme auslöst. **/
	public final ByteOrder orderOf(final Object object) throws IOException {
		final byte[] bytes = MMIArray.from(object).section(0, 4).toBytes();
		return this.orderOf(bytes);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Integer.toHexString(this.value);
	}

}