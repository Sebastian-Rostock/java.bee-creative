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

	static int hashHash(int hash) {
		hash ^= (hash >>> 23) ^ (hash >>> 11);
		return hash ^ (hash >>> 7) ^ (hash >>> 3);
	}

	public static int hashValue(final String value) {
		int hash = 1;
		for(int i = 0, length = value.length(); i < length; i++){
			hash = (hash * 31) + value.charAt(i);
		}
		return Coder.hashHash(hash);
	}

	public static int hashLabel(final int uri, final int name) {
		return Coder.hashHash((uri * 31) + name);
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
