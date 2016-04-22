package bee.creative.bex;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert grundlegende Klassen und Methoden zur Umsetzung des {@code BEX – Binary Encoded XML}.
 * 
 * @see BEXBuilder
 * @see BEXLoader
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEX {

	/** Dieses Feld speichert das {@code UTF8}-{@link Charset} zur kodierung und dekodierung von Zeichenketten. */
	public static final Charset CHARSET = Charset.forName("UTF8");

	{}

	/** Diese Methode kodiert die gegebene Zeichenkette via {@link String#getBytes(Charset)} in eine Bytefolge, wandelt diese in eine nullterminierte Zahlenfolge
	 * um gibt diese zurück.
	 * 
	 * @param value Zeichenkette.
	 * @return Zahlenfolge. */
	public static final int[] toItem(final String value) {
		final byte[] bytes = value.getBytes(BEX.CHARSET);
		final int length = bytes.length;
		final int[] result = new int[length + 1];
		for (int i = 0; i < length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	/** Diese Methode kodiert die gegebene Zeichenkette via {@link String#getBytes(Charset)} in eine Bytefolge, wandelt diese in eine nullterminierte Zahlenfolge
	 * um gibt diese zurück.
	 * 
	 * @see IAMArray#from(byte[])
	 * @param value Zeichenkette.
	 * @return Zahlenfolge. */
	public static final IAMArray toArray(final String value) {
		final byte[] bytes = value.getBytes(BEX.CHARSET);
		return IAMArray.from(Arrays.copyOf(bytes, bytes.length + 1));
	}

	/** Diese Methode wandelt die gegebene nullterminierte Zahlenfolge mit dem {@link #CHARSET} in eine Zeichenkette um und gibt diese zurück.
	 * 
	 * @see String#String(byte[], Charset)
	 * @see MMFArray#toBytes()
	 * @param value Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static final String toString(final MMFArray value) {
		if (value.length() == 0) return "";
		return new String(value.section(0, value.length() - 1).toBytes(), BEX.CHARSET);
	}

	/** Diese Methode gibt einen neuen {@link BEXBuilder} zurück.
	 * 
	 * @return neuer {@link BEXBuilder}. */
	public static final BEXBuilder encoder() {
		return new BEXBuilder();
	}

	/** Diese Methode gibt einen neuen {@link BEXLoader} zurück.
	 * 
	 * @return neuer {@link BEXLoader}. */
	public static final BEXLoader decoder() {
		return new BEXLoader();
	}

}
