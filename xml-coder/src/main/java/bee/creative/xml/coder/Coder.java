package bee.creative.xml.coder;

import java.nio.charset.Charset;
import bee.creative.array.ArrayCopy;
import bee.creative.xml.coder.Decoder.DecodeDocument;
import bee.creative.xml.coder.Decoder.DecodeLabel;
import bee.creative.xml.coder.Decoder.DecodeValue;
import bee.creative.xml.coder.Encoder.EncodeLabel;
import bee.creative.xml.coder.Encoder.EncodeValue;

public class Coder {

	static final int[] VOID_INTS = new int[0];

	static final byte[] VOID_BYTES = new byte[0];

	/**
	 * Dieses Feld speichert das in {@link #encodeChars(String)} und {@link #decodeChars(byte[])} verwendete
	 * {@link Charset}.
	 */
	static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen {@link String}s zurück, der in den
	 * {@code Hash}es des {@link DecodeDocument}s verwendet werden.
	 * 
	 * @see EncodeValue#value()
	 * @see DecodeValue#value()
	 * @see DecodeDocument#uriHash()
	 * @see DecodeDocument#valueHash()
	 * @see DecodeDocument#getXmlnsNameHash()
	 * @see DecodeDocument#getElementNameHash()
	 * @see DecodeDocument#getAttributeNameHash()
	 * @param value {@link String}.
	 * @return {@link Object#hashCode() Streuwert} des {@link String}s.
	 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
	 */
	public static int hashValue(final String value) throws NullPointerException {
		if(value == null) throw new NullPointerException("value is null");
		int hash = 0x811C9DC5;
		for(int i = 0, length = value.length(); i < length; i++){
			hash = (hash * 0x01000193) ^ value.charAt(i);
		}
		return hash;
	}

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} der gegebenen Indices zurück, der in den {@code Hash}es
	 * des {@link DecodeDocument}s verwendet werden.
	 * 
	 * @see EncodeLabel#uri()
	 * @see EncodeLabel#name()
	 * @see DecodeLabel#uri()
	 * @see DecodeLabel#name()
	 * @see DecodeDocument#getXmlnsLabelHash()
	 * @see DecodeDocument#getElementLabelHash()
	 * @see DecodeDocument#getAttributeLabelHash()
	 * @param uri {@code URI}-Index.
	 * @param name {@code Name}-Index.
	 * @return {@link Object#hashCode() Streuwert} der Indices.
	 */
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
