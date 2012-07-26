package bee.creative.xml.coder;

import java.nio.charset.Charset;

public class Coder {

	/**
	 * Dieses Feld speichert das verwendete {@link Charset}.
	 */
	static final Charset CHARSET = Charset.forName("UTF-8");

	public static int hashHash(int hash) {
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

	public static byte[] encode(final String value) {
		return value.getBytes(Coder.CHARSET);
	}

	public static String decode(final byte[] value) {
		return new String(value, Coder.CHARSET);
	}

}
