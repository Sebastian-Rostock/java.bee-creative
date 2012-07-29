package bee.creative.xml.coder;

import java.nio.charset.Charset;
import bee.creative.array.ArrayCopy;

public class Coder {

	static final int[] VOID_INTS = new int[0];

	static final byte[] VOID_BYTES = new byte[0];

	/**
	 * Dieses Feld speichert das verwendete {@link Charset}.
	 */
	static final Charset CHARSET = Charset.forName("UTF-8");

	public static int hashValue(final String value) {
		int hash = 0x811C9DC5;
		for(int i = 0, length = value.length(); i < length; i++){
			hash = (hash * 0x01000193) ^ value.charAt(i);
		}
		return hash;
	}

	public static int hashLabel(final int uri, final int name) {
		return (uri * 23) ^ (name * 97);
	}

	public static byte[] encodeChars(final String value) {
		return value.getBytes(Coder.CHARSET);
	}

	public static String decodeChars(final byte[] value) {
		return new String(value, Coder.CHARSET);
	}

	public static byte[] encodeIndices(final int... value) {
		final int length = value.length << 2;
		if(length == 0) return Coder.VOID_BYTES;
		final byte[] array = new byte[length];
		ArrayCopy.copy(value, 0, array, 0, length);
		return array;
	}

	public static int[] decodeIndices(final byte... value) {
		final int length = value.length >> 2;
		if(length == 0) return Coder.VOID_INTS;
		final int[] array = new int[length];
		ArrayCopy.copy(value, 0, array, 0, length);
		return array;
	}

}
